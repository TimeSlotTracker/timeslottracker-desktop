package net.sf.timeslottracker.integrations.issuetracker.jira;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.data.*;
import net.sf.timeslottracker.gui.AbstractSimplePanelDialog;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.integrations.issuetracker.*;
import net.sf.timeslottracker.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  private final SAXParserFactory saxFactory;

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

    this.saxFactory = SAXParserFactory.newInstance();
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
      URLConnection connection = getUrlConnection(url);
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
          String urlString = MessageFormat.format(filterUrlTemplate,
              getBaseJiraUrl(), filterId, getAuthorizedParams());
          URL url = new URL(urlString);
          URLConnection connection = getUrlConnection(url);
          SAXParser saxParser = saxFactory.newSAXParser();

          try (InputStream inputStream = connection.getInputStream()) {
            saxParser.parse(inputStream, new DefaultHandler() {
              StringBuilder stringBuilder = null;
              JiraIssue jiraIssue;

              @Override
              public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (handler.stopProcess()) {
                  throw new SAXException("Cancel xml processing");
                }

                switch (qName) {
                  case "summary":
                    stringBuilder = new StringBuilder();
                    break;
                  case "item":
                    jiraIssue = new JiraIssue();
                    break;
                  case "key":
                    jiraIssue.setId(attributes.getValue("id"));
                    stringBuilder = new StringBuilder();
                    break;
                  case "assignee":
                    stringBuilder = new StringBuilder();
                    break;
                  case "parent":
                    jiraIssue.setSubTask(true);
                    break;
                }
              }

              @Override
              public void characters(char[] ch, int start, int length) throws SAXException {
                if (stringBuilder != null) {
                  stringBuilder.append(new String(ch, start, length));
                }
              }

              @Override
              public void endElement(String uri, String localName, String qName) throws SAXException {
                switch (qName) {
                  case "item":
                    try {
                      handler.handle(jiraIssue);
                    } catch (IssueTrackerException e) {
                      LOG.throwing("", "", e);
                    }
                    jiraIssue = null;
                    break;
                  case "summary":
                    jiraIssue.setSummary(stringBuilder.toString());
                    break;
                  case "assignee":
                    jiraIssue.setAssignee(stringBuilder.toString());
                    break;
                  case "key":
                    jiraIssue.setKey(stringBuilder.toString());
                    break;
                }
                stringBuilder = null;
              }
            });
          }
        } catch (Exception e) {
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
    URLConnection connection = getUrlConnection(url);
    if (connection instanceof HttpURLConnection) {
      HttpURLConnection httpConnection = (HttpURLConnection) connection;
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

  private URLConnection getUrlConnection(URL url) throws IOException {
    URLConnection connection = url.openConnection();
    // preparing connection
    if (version.equals(JIRA_VERSION_6)) {
      String basicAuth = "Basic " + new String(new Base64()
          .encode((getLogin() + ":" + getPassword()).getBytes()));
      connection.setRequestProperty("Authorization", basicAuth);
    }
    return connection;
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
    String password = this.timeSlotTracker.getConfiguration().getString(Configuration.JIRA_PASSWORD, null);
    if (!StringUtils.isBlank(password)) {
      return password;
    }

    if (StringUtils.isBlank(sessionPassword)) {
      new EnterPasswordDialog(timeSlotTracker.getLayoutManager()).activate();
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

  private class EnterPasswordDialog extends AbstractSimplePanelDialog {
    private final JPasswordField passwordField = new JPasswordField();

    public EnterPasswordDialog(LayoutManager layoutManager) {
      super(layoutManager, layoutManager.getTimeSlotTracker().getString("issueTracker.credentialsInputDialog.password"));
    }

    @Override
    protected void fillDialogPanel(DialogPanel panel) {
      panel.addRow(passwordField);
      this.setModal(true);
    }

    @Override
    protected Collection<JButton> getButtons() {
      JButton processButton = new JButton("OK");
      processButton.addActionListener(e -> {
        sessionPassword = new String(passwordField.getPassword());
        EnterPasswordDialog.this.dispose();
      });
      processButton.setIcon(icon("save"));

      return Collections.singletonList(processButton);
    }

    @Override
    protected int getDefaultHeight() {
      return 110;
    }

    @Override
    protected int getDefaultWidth() {
      return 300;
    }
  }
}
