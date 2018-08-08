package net.sf.timeslottracker.integrations.issuetracker.jira;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.DataLoadedListener;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.TimeSlotChangedListener;
import net.sf.timeslottracker.integrations.issuetracker.Issue;
import net.sf.timeslottracker.integrations.issuetracker.IssueHandler;
import net.sf.timeslottracker.integrations.issuetracker.IssueKeyAttributeType;
import net.sf.timeslottracker.integrations.issuetracker.IssueTracker;
import net.sf.timeslottracker.integrations.issuetracker.IssueTrackerException;
import net.sf.timeslottracker.integrations.issuetracker.IssueWorklogStatusType;
import net.sf.timeslottracker.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;

/**
 * Implementation of Issue Tracker for Jira
 *
 * <p>
 * JIRA (R) Issue tracking project management software
 * (http://www.atlassian.com/software/jira)
 */
public class JiraTracker implements IssueTracker {

  public static final String JIRA_VERSION_6 = "6";
  public static final String JIRA_VERSION_310 = "3.10";
  public static final String JIRA_VERSION_3 = "3";
  private static final String JIRA_DEFAULT_VERSION = JIRA_VERSION_6;

  private static final Logger LOG = Logger
      .getLogger(JiraTracker.class.getName());

  private static String decodeString(String s) {
    Pattern p = Pattern.compile("&#([\\d]+);");
    Matcher m = p.matcher(s);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb,
          new String(Character.toChars(Integer.parseInt(m.group(1)))));
    }
    m.appendTail(sb);
    return sb.toString();
  }

  private static String prepareKey(String key) {
    if (key == null) {
      return null;
    }

    return key.trim().toUpperCase();
  }

  private final ExecutorService executorService;

  private final IssueKeyAttributeType issueKeyAttributeType;

  private final IssueWorklogStatusType issueWorklogStatusType;

  private final Pattern patternIssueId = Pattern
      .compile("<key id=\"([0-9]+)\">([\\d,\\s\u0021-\u0451]+)<");

  private final Pattern patternSummary = Pattern
      .compile("<summary>([\\d,\\s\u0021-\u0451]+)<");

  /**
   * JIRA password per application runtime session
   */
  private String sessionPassword = StringUtils.EMPTY;

  private final TimeSlotTracker timeSlotTracker;

  private final String issueUrlTemplate;
  private final String filterUrlTemplate;

  private final String version;

  public JiraTracker(final TimeSlotTracker timeSlotTracker) {
    this.timeSlotTracker = timeSlotTracker;
    this.executorService = Executors.newSingleThreadExecutor();

    this.issueKeyAttributeType = IssueKeyAttributeType.getInstance();
    this.issueWorklogStatusType = IssueWorklogStatusType.getInstance();

    this.issueUrlTemplate = timeSlotTracker.getConfiguration().get(
        Configuration.JIRA_ISSUE_URL_TEMPLATE,
        "{0}/si/jira.issueviews:issue-xml/{1}/?{2}");

    this.version = timeSlotTracker.getConfiguration()
        .get(Configuration.JIRA_VERSION, JIRA_DEFAULT_VERSION);

    this.filterUrlTemplate = timeSlotTracker.getConfiguration().get(
        Configuration.JIRA_FILTER_URL_TEMPLATE,
        "{0}/sr/jira.issueviews:searchrequest-xml/{1}/SearchRequest-{1}.xml?tempMax=1000&{2}");

    this.timeSlotTracker.addActionListener((DataLoadedListener) action -> init());
  }

  public void add(final TimeSlot timeSlot) throws IssueTrackerException {
    // getting issue key
    final String key = getIssueKey(timeSlot.getTask());
    if (key == null) {
      return;
    }

    LOG.info("Updating jira worklog for issue with key " + key + " ...");

    // analyze the existing worklog status and duration
    final long duration;
    final Attribute statusAttribute = getIssueWorkLogDuration(timeSlot);
    if (statusAttribute != null) {
      int lastDuration = Integer
          .parseInt(String.valueOf(statusAttribute.get()));
      if (timeSlot.getTime() <= lastDuration) {
        LOG.info("Skipped updating jira worklog for issue with key " + key
            + ". Reason: current timeslot duration <= already saved in worklog");
        return;
      }

      duration = timeSlot.getTime() - lastDuration;
    } else {
      duration = timeSlot.getTime();
    }

    Runnable searchIssueTask = () -> {
      Issue issue = null;
      try {
        issue = getIssue(key);
      } catch (IssueTrackerException e2) {
        LOG.info(e2.getMessage());
      }
      if (issue == null) {
        LOG.info("Nothing updated. Not found issue with key " + key);
        return;
      }

      final String issueId = issue.getId();
      Runnable updateWorklogTask = () -> {
        try {
          addWorklog(timeSlot, key, issueId, statusAttribute,
              duration);
        } catch (IOException e) {
          LOG.warning("Error occured while updating jira worklog:"
              + e.getMessage());
        }
      };
      executorService.execute(updateWorklogTask);
    };
    executorService.execute(searchIssueTask);
  }

  private Attribute getIssueWorkLogDuration(final TimeSlot timeSlot) {
    for (Attribute attribute : timeSlot.getAttributes()) {
      if (attribute.getAttributeType().equals(issueWorklogStatusType)) {
        return attribute;
      }
    }

    return null;
  }

  public Issue getIssue(String key) throws IssueTrackerException {
    try {
      key = prepareKey(key);
      if (key == null) {
        return null;
      }

      String urlString = MessageFormat.format(issueUrlTemplate,
          getBaseJiraUrl(), key, getAuthorizedParams());
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection();
      try {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        String line = br.readLine();
        String id = null;
        String summary = null;
        while (line != null) {
          line = decodeString(line);
          Matcher matcherId = patternIssueId.matcher(line);
          if (id == null && matcherId.find()) {
            id = matcherId.group(1);
            continue;
          }

          Matcher matcherSummary = patternSummary.matcher(line);
          if (summary == null && matcherSummary.find()) {
            summary = matcherSummary.group(1);
            continue;
          }

          if (id != null && summary != null) {
            return new JiraIssue(key, id, summary);
          }

          line = br.readLine();
        }
      } finally {
        connection.getInputStream().close();
      }
      return null;
    } catch (FileNotFoundException e) {
      return null;
    } catch (IOException e) {
      throw new IssueTrackerException(e);
    }
  }

  @Override
  public URI getIssueUrl(Task task) throws IssueTrackerException {
    String issueKey = getIssueKey(task);

    if (issueKey == null) {
      throw new IssueTrackerException("Given task \"" + task.getName()
          + "\" is not issue task (i.e. does not has issue key attribute)");
    }

    String uriStr = getBaseJiraUrl() + "/browse/" + issueKey;
    try {
      return new URI(uriStr);
    } catch (URISyntaxException e) {
      throw new IssueTrackerException(
          "Error occured while creating uri: " + uriStr);
    }
  }

  @Override
  public void getFilterIssues(final String filterId, final IssueHandler handler)
      throws IssueTrackerException {
      executorService.execute(() -> {
        try {
          // if not
          String urlString = MessageFormat.format(filterUrlTemplate,
              getBaseJiraUrl(), filterId, getAuthorizedParams());
          URL url = new URL(urlString);
          URLConnection connection = url.openConnection();
          try {

            BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

            String line = br.readLine();
            String id = null;
            String key = null;
            String summary = null;
            while (line != null && !handler.stopProcess()) {
              line = decodeString(line);
              Matcher matcherId = patternIssueId.matcher(line);
              if (id == null && matcherId.find()) {
                id = matcherId.group(1);
                key = matcherId.group(2);
                continue;
              }

              Matcher matcherSummary = patternSummary.matcher(line);
              if (summary == null && matcherSummary.find()) {
                summary = matcherSummary.group(1);
                continue;
              }

              if (id != null && summary != null) {
                JiraIssue jiraIssue = new JiraIssue(key, id, summary);
                handler.handle(jiraIssue);
                id = key = summary = null;
              }

              line = br.readLine();
            }
          } finally {
            connection.getInputStream().close();
          }
        } catch (FileNotFoundException e) {
          LOG.throwing("", "", e);
        } catch (IssueTrackerException e) {
          LOG.throwing("", "", e);
        } catch (IOException e) {
          LOG.throwing("", "", e);
        }
      });
  }

  public boolean isIssueTask(Task task) {
    return task != null && getIssueKey(task) != null;
  }

  public boolean isValidKey(String key) {
    String preparedKey = prepareKey(key);
    return preparedKey != null && preparedKey.matches("[a-z,A-Z0-9]+-[0-9]+");
  }

  private void addWorklog(final TimeSlot timeSlot, final String key,
                          final String issueId, Attribute statusAttribute,
                          long duration)
      throws IOException {
    URL url = new URL(getBaseJiraUrl() + getAddWorklogPath(issueId));
    URLConnection connection = url.openConnection();
    if (connection instanceof HttpURLConnection) {
      HttpURLConnection httpConnection = (HttpURLConnection) connection;

      // preparing connection
      if (version.equals(JIRA_VERSION_6)) {
        String basicAuth = "Basic " + new String(new Base64()
            .encode((getLogin() + ":" + getPassword()).getBytes()));
        httpConnection.setRequestProperty("Authorization", basicAuth);
      }
      httpConnection.setRequestMethod("POST");
      httpConnection.setDoInput(true);
      httpConnection.setDoOutput(true);
      httpConnection.setUseCaches(false);
      httpConnection.setRequestProperty("Content-Type",
          getContentType());

      // sending data
      OutputStreamWriter writer = new OutputStreamWriter(
          httpConnection.getOutputStream());
      try {

        String jiraDuration = (duration / 1000 / 60) + "m";
        if (version.equals(JIRA_VERSION_6)) {
          writer.append("{").append(getPairSC("timeSpent", jiraDuration)).append(",")
              .append(getPairSC("started", new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:SS.sZ")
                  .format(timeSlot.getStartDate()))).append(",")
              .append(getPairSC("comment", timeSlot.getDescription()))
              .append("}");
        } else {
          writer.append(getAuthorizedParams()).append(getPair("id", issueId))
              .append(getPair("comment", URLEncoder.encode(timeSlot.getDescription(), "UTF-8")))
              .append(getPair("worklogId", ""))
              .append(getPair("timeLogged", jiraDuration))
              .append(getPair("startDate", URLEncoder.encode(new SimpleDateFormat("dd/MMM/yy KK:mm a")
                      .format(timeSlot.getStartDate()), "UTF-8")))
              .append(getPair("adjustEstimate", "auto"))
              .append(getPair("newEstimate", ""))
              .append(getPair("commentLevel", ""));
        }
      } finally {
        writer.flush();
        writer.close();
      }
      BufferedReader br = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
      String line = br.readLine();
      br.close();
      LOG.finest("jira result: " + line);

      if (statusAttribute == null) {
        statusAttribute = new Attribute(issueWorklogStatusType);
        List<Attribute> list = new ArrayList<Attribute>(
            timeSlot.getAttributes());
        list.add(statusAttribute);
        timeSlot.setAttributes(list);
      }

      statusAttribute.set(timeSlot.getTime());

      LOG.info("Updated jira worklog with key: " + key);
    }
  }

  private String getAddWorklogPath(String issueId) {
    String path;
    if (version.equals(JIRA_VERSION_3)) {
      path = "/secure/LogWork.jspa";
    }
    else if (version.equals(JIRA_VERSION_310)) {
      path = "/secure/CreateWorklog.jspa";
    }
    else {
      path = "/rest/api/2/issue/" + issueId + "/worklog";
    }
    return path;
  }

  private String getContentType() {
    return version.equals(JIRA_VERSION_6) ? "application/json" : "application/x-www-form-urlencoded";
  }

  private String getAuthorizedParams() {
    return "os_username=" + getLogin() + getPair("os_password", getPassword());
  }

  private String getBaseJiraUrl() {
    String url = this.timeSlotTracker.getConfiguration()
        .getString(Configuration.JIRA_URL, "");

    // truncate symbol / if present
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    return url;
  }

  private String getIssueKey(Task task) {
    for (Attribute attribute : task.getAttributes()) {
      if (attribute.getAttributeType().equals(issueKeyAttributeType)) {
        return String.valueOf(attribute.get());
      }
    }
    return null;
  }

  private String getLogin() {
    return this.timeSlotTracker.getConfiguration()
        .getString(Configuration.JIRA_LOGIN, "");
  }

  private String getPairSC(String name, String value) {
    return "\"" + name + "\"" + ":" + "\"" +value + "\"";
  }

  private String getPair(String name, String value) {
    return "&" + name + "=" + value;
  }

  private String getPassword() {
    String password = this.timeSlotTracker.getConfiguration()
        .getString(Configuration.JIRA_PASSWORD, null);
    if (!StringUtils.isBlank(password)) {
      return password;
    }

    if (StringUtils.isBlank(sessionPassword)) {
      sessionPassword = JOptionPane
          .showInputDialog(timeSlotTracker.getRootFrame(), timeSlotTracker
              .getString("issueTracker.credentialsInputDialog.password"));
    }

    return sessionPassword;
  }

  private void init() {
    // updates when timeslot changed
    this.timeSlotTracker.getLayoutManager()
        .addActionListener(new TimeSlotChangedListener() {
          public void actionPerformed(Action action) {
            Boolean enabled = timeSlotTracker.getConfiguration()
                .getBoolean(Configuration.JIRA_ENABLED, false);

            if (!enabled) {
              return;
            }

            if (!action.getName().equalsIgnoreCase("TimeSlotChanged")) {
              return;
            }

            // no active timeSlot
            TimeSlot timeSlot = (TimeSlot) action.getParam();
            if (timeSlot == null) {
              return;
            }

            boolean isNullStart = timeSlot.getStartDate() == null;
            boolean isNullStop = timeSlot.getStopDate() == null;

            // paused timeSlot
            if (isNullStart && isNullStop) {
              return;
            }

            // started timeSlot
            if (isNullStop) {
              return;
            }

            // removed timeSlot
            if (timeSlot.getTask() == null) {
              return;
            }

            // stopped or edited task
            try {
              add(timeSlot);
            } catch (IssueTrackerException e) {
              LOG.warning(e.getMessage());
            }
          }
        });
  }

}
