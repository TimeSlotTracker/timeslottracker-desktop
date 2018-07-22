package net.sf.timeslottracker.gui.layouts.classic.tasksbydays;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.filters.FilterUtils;
import net.sf.timeslottracker.filters.TimeSlotStartedInPeriod;
import net.sf.timeslottracker.filters.TreeNodeFilter;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.TasksByDaysInterface;
import net.sf.timeslottracker.gui.layouts.classic.SwitchViewCombobox;
import net.sf.timeslottracker.gui.listeners.TasksByDaysSelectionAction;
import net.sf.timeslottracker.utils.SwingUtils;
import net.sf.timeslottracker.utils.TimeUtils;
import net.sf.timeslottracker.utils.WeekComparator;
import net.sf.timeslottracker.worktime.WorkTimeService;

/**
 * Module with JTree to present tasks in a tree by month and date.
 * <p/>
 * File version: $Revision: 1150 $, $Date: 2009-05-16 09:00:38 +0700 (Sat, 16
 * May 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class DaysTree extends JPanel implements TasksByDaysInterface,
    TreeSelectionListener {

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
      "yyyy-MM-dd");

  /**
   * DataSource which holds and manages our data *
   */
  private DataSource dataSource;

  /**
   * dialog panel where we will keep all our info *
   */
  private final DialogPanel dialogPanel;

  private final LayoutManager layoutManager;

  // year, month, week, day
  private final Map<Date, Map<Date, Map<String, Map<Date, List<Task>>>>> records = new HashMap<Date, Map<Date, Map<String, Map<Date, List<Task>>>>>();

  private DaysTreeNode root;

  /**
   * tree to present tasks *
   */
  private JTree tree;

  private final int firstDayOfWeek;

  private final SwitchViewCombobox combobox;

  private final WorkTimeService workTimeService;

  public DaysTree(final LayoutManager layoutManager,
                  AbstractAction switchViewAction) {
    super(new BorderLayout());
    this.layoutManager = layoutManager;
    this.workTimeService = layoutManager.getTimeSlotTracker()
        .getWorkTimeService();

    firstDayOfWeek = layoutManager.getTimeSlotTracker().getConfiguration()
        .getInteger(Configuration.WEEK_FIRST_DAY, Calendar.MONDAY);

    createTree();
    tree.addTreeSelectionListener(this);
    tree.setCellRenderer(new TreeCellRenderer(layoutManager));

    // constructs dialog panel with our task's info variables
    dialogPanel = new DialogPanel(GridBagConstraints.BOTH, 0.0);
    combobox = new SwitchViewCombobox(layoutManager);
    JPanel taskViewSwitcher = new JPanel(
        new FlowLayout(FlowLayout.CENTER, 0, 0));
    combobox.addActionListener(switchViewAction);
    taskViewSwitcher.add(combobox);

    dialogPanel.addRow(taskViewSwitcher);
    JScrollPane scrollTable = new JScrollPane(tree);
    scrollTable.getViewport().setBackground(tree.getBackground());
    dialogPanel.fillToEnd(scrollTable);
    add(dialogPanel, BorderLayout.CENTER);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.gui.TasksByDaysInterface#activate()
   */
  @Override
  public void activate() {
    reloadTree();
    selectCurrent(Period.DAY);
    combobox.setSelectedIndex(1);
  }

  @Override
  public Collection<JMenuItem> getMenuItems() {
    final JMenuItem show = new JMenuItem(new javax.swing.AbstractAction(
        layoutManager.getString("menuBar.item.View.TasksByDays.Update"),
        layoutManager.getIcon("refresh")) {

      public void actionPerformed(ActionEvent ae) {
        reloadTree();
        selectCurrent(Period.DAY);
      }
    });

    return Collections.singletonList(show);
  }

  /**
   * Its role is to response to user selection action
   */
  public void valueChanged(TreeSelectionEvent e) {
    refresh();
  }

  private void addChildrenNodes(DaysTreeNode root) {
    Date now = new Date();

    for (Date year : new TreeSet<Date>(records.keySet())) {

      DaysTreeNode yearNode = new DateNode(year, Calendar.YEAR, null);
      root.add(yearNode);

      long yearTime = 0;

      Map<Date, Map<String, Map<Date, List<Task>>>> recordsForYear = records
          .get(year);
      for (Date month : new TreeSet<Date>(recordsForYear.keySet())) {

        DaysTreeNode monthNode = new DateNode(month, Calendar.MONTH,
            new SimpleDateFormat("MMMM").format(month) + " ("
                + recordsForYear.get(month).size() + " "
                + layoutManager.getString("daystree.days") + ")");
        yearNode.add(monthNode);

        long monthTime = 0;

        Map<String, Map<Date, List<Task>>> recordForMonth = recordsForYear
            .get(month);

        TreeSet<String> treeSet = new TreeSet<String>(new WeekComparator());
        treeSet.addAll(recordForMonth.keySet());

        for (String weekKey : treeSet) {

          String week = weekKey.substring(weekKey.indexOf("_") + 1);

          DaysTreeNode weekNode = new DateNode(month, Calendar.WEEK_OF_YEAR,
              null);
          monthNode.add(weekNode);

          long weekTime = 0;

          Map<Date, List<Task>> recordForWeek = recordForMonth.get(weekKey);
          for (Date day : new TreeSet<Date>(recordForWeek.keySet())) {

            DaysTreeNode dayNode = new DateNode(day, Calendar.DATE, null);
            weekNode.add(dayNode);

            Date endDate = TimeUtils.getDayEnd(day).getTime();

            long dayTime = 0;
            for (Task task : recordForWeek.get(day)) {
              dayTime += task.getTime(false, day, endDate);
              dayNode.add(new TaskNode(task));
            }

            dayNode.setUserObject(new SimpleDateFormat("dd, EEEE").format(day)
                + " (" + layoutManager.formatDuration(dayTime) + ")");

            weekTime += dayTime;
          }

          weekNode.setUserObject(layoutManager.getString("daystree.week") + " "
              + week + " (" + layoutManager.formatDuration(weekTime) + ")");

          monthTime += weekTime;
        }

        Date start = TimeUtils.getMonthBegin(month).getTime();
        Date monthEnd = TimeUtils.getMonthEnd(month).getTime();
        Date finish = monthEnd.before(now) ? monthEnd : now;

        long plannedMonthTime = workTimeService.getWorkTime(start, finish);
        long delta = monthTime - plannedMonthTime;
        monthNode.setUserObject(new SimpleDateFormat("MMMM").format(month)
            + " (" + layoutManager.formatDuration(monthTime) + "/"
            + layoutManager.formatDuration(plannedMonthTime) + "/"
            + layoutManager.formatDuration(delta) + ")");

        yearTime += monthTime;
      }

      yearNode.setUserObject(new SimpleDateFormat("yyyy").format(year) + " ("
          + layoutManager.formatDuration(yearTime) + ")");
    }
  }

  private void addTimeSlot(TimeSlot timeSlot) {
    Date date = timeSlot.getStartDate();
    if (date == null) {
      return; // skipping timeSlots with null start time
    }

    Date year = TimeUtils.getYearBegin(date).getTime();

    Date month = TimeUtils.getMonthBegin(date).getTime();

    Calendar dayCalendar = TimeUtils.getDayBegin(date);
    Date day = dayCalendar.getTime();

    Calendar weekCal = TimeUtils.getWeekBegin(date, firstDayOfWeek);
    weekCal.setFirstDayOfWeek(firstDayOfWeek);
    String week = weekCal.get(Calendar.YEAR) + "_"
        + weekCal.get(Calendar.WEEK_OF_YEAR);

    final Map recordsForYear;
    if (records.containsKey(year)) {
      recordsForYear = records.get(year);
    } else {
      recordsForYear = new HashMap();
      records.put(year, recordsForYear);
    }

    final Map recordsForMonth;
    if (recordsForYear.containsKey(month)) {
      recordsForMonth = (Map) recordsForYear.get(month);
    } else {
      recordsForMonth = new HashMap();
      recordsForYear.put(month, recordsForMonth);
    }

    final Map recordsForWeek;
    if (recordsForMonth.containsKey(week)) {
      recordsForWeek = (Map) recordsForMonth.get(week);
    } else {
      recordsForWeek = new HashMap();
      recordsForMonth.put(week, recordsForWeek);
    }

    final List recordsForDay;
    if (recordsForWeek.containsKey(day)) {
      recordsForDay = (List) recordsForWeek.get(day);
    } else {
      recordsForDay = new ArrayList();
      recordsForWeek.put(day, recordsForDay);
    }

    if (!recordsForDay.contains(timeSlot.getTask())) {
      recordsForDay.add(timeSlot.getTask());
    }
  }

  /**
   * Creates tree to present tasks in tree
   */
  private void createTree() {
    // construct visual JTree to show them
    tree = new JTree(root);
    tree.setEditable(false);
    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);
  }

  private void fillRecords(Task parent) {
    Collection<Task> childrenCollection = dataSource.getChildren(parent);
    if (childrenCollection == null) {
      return;
    }

    Iterator<Task> children = childrenCollection.iterator();
    while (children.hasNext()) {
      Task child = children.next();
      for (TimeSlot timeslot : child.getTimeslots()) {
        addTimeSlot(timeslot);
      }

      fillRecords(child);
    }
  }

  /**
   * Select node
   */
  private void refresh() {
    TreeNode selected = (TreeNode) tree.getLastSelectedPathComponent();
    if (selected == null) {
      return;
    }

    String description = null;
    Collection<TimeSlot> collection = null;

    if (selected instanceof TaskNode) {
      TaskNode taskNode = (TaskNode) selected;

      // getting date for filtering timeslots panel
      Date date = null;
      TreeNode parent = selected.getParent();
      if (parent instanceof DateNode) {
        DateNode dateNode = (DateNode) parent;
        if (dateNode.getCalendarDateType() == Calendar.DATE) {
          date = dateNode.getDate();
        }
      }

      description = DATE_FORMAT.format(date) + " - "
          + taskNode.getTask().getName();
      collection = FilterUtils.filter(taskNode.getTask().getTimeslots(),
          new TimeSlotStartedInPeriod(date));

    } else if (selected instanceof DateNode
        && ((DateNode) selected).getCalendarDateType() == Calendar.DATE) {
      DateNode dateNode = (DateNode) selected;

      ArrayList<TimeSlot> timeslots = new ArrayList<TimeSlot>();

      int childCount = dateNode.getChildCount();
      for (int i = 0; i < childCount; i++) {
        TaskNode childTaskNode = (TaskNode) dateNode.getChildAt(i);
        timeslots.addAll(childTaskNode.getTask().getTimeslots());
      }

      description = DATE_FORMAT.format(dateNode.getDate());
      collection = FilterUtils.filter(timeslots, new TimeSlotStartedInPeriod(
          dateNode.getDate()));
    }

    layoutManager
        .fireTasksByDaysSelectionChanged(new TasksByDaysSelectionAction(this,
            description, collection));
  }

  private void reloadTree() {
    // construct tree from data source
    dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    if (dataSource == null) {
      String alertTitle = layoutManager.getCoreString("alert.warning.title");
      String alertMsg = layoutManager
          .getString("daystree.alert.no-data-source");
      JOptionPane.showMessageDialog(this, alertMsg, alertTitle,
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    root = new StringNode(layoutManager.getString("daystree.rootnode.name"));
    fillRecords(dataSource.getRoot());
    addChildrenNodes(root);

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        tree.setModel(new DefaultTreeModel(root));
      }
    });
  }

  private void selectCurrent(Period period) {
    if (Period.DAY == period) {

      TreeNode searchNode = SwingUtils.searchNode(root, new TreeNodeFilter() {
        long dayStart = TimeUtils.getDayBegin(new Date()).getTimeInMillis();

        long dayEnd = TimeUtils.getDayEnd(new Date()).getTimeInMillis();

        @Override
        public boolean accept(TreeNode treeNode) {

          if (treeNode instanceof DateNode) {
            DateNode dateNode = (DateNode) treeNode;
            long time = dateNode.getDate().getTime();
            if (dayStart <= time && time <= dayEnd) {
              return true;
            }
          }

          return false;
        }
      });

      if (searchNode == null) {
        layoutManager
            .fireTasksByDaysSelectionChanged(new TasksByDaysSelectionAction(
                this, null, Collections.EMPTY_LIST));
        return;
      }

      TreeNode[] nodes = ((DefaultTreeModel) tree.getModel())
          .getPathToRoot(searchNode);
      final TreePath path = new TreePath(nodes);

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          tree.scrollPathToVisible(path);
          tree.setSelectionPath(path);
        }
      });
    }
  }

}
