package net.sf.timeslottracker.gui.layouts.classic.tasks;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.Configuration;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TaskChangedListener;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.filters.HiddenTaskFilter;
import net.sf.timeslottracker.gui.DialogPanel;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.gui.NewIssueDialog;
import net.sf.timeslottracker.gui.TaskEditDialog;
import net.sf.timeslottracker.gui.TasksInterface;
import net.sf.timeslottracker.gui.actions.FilterTimeSlotsAction;
import net.sf.timeslottracker.gui.layouts.classic.SwitchViewCombobox;
import net.sf.timeslottracker.gui.reports.ReportContext;
import net.sf.timeslottracker.integrations.issuetracker.Issue;
import net.sf.timeslottracker.integrations.issuetracker.IssueHandler;
import net.sf.timeslottracker.integrations.issuetracker.IssueKeyAttributeType;
import net.sf.timeslottracker.integrations.issuetracker.IssueTracker;
import net.sf.timeslottracker.integrations.issuetracker.IssueTrackerException;
import net.sf.timeslottracker.utils.StringUtils;
import net.sf.timeslottracker.utils.SwingUtils;

/**
 * Module with JTree to present tasks in a tree.
 *
 * File version: $Revision: 1139 $, $Date: 2009-05-16 09:00:38 +0700 (Sat, 16
 * May 2009) $ Last change: $Author: cnitsa $
 */
@SuppressWarnings("serial")
public class TasksTree extends JPanel implements TasksInterface,
    TreeSelectionListener, ReportContext {

  private static final HiddenTaskFilter HIDDEN_TASK_FILTER = new HiddenTaskFilter();

  private final LayoutManager layoutManager;

  /** dialog panel where we will keep all our info * */
  private final DialogPanel dialogPanel;

  /** DataSource which holds and manages our data * */
  private DataSource dataSource;

  /** tree to present tasks * */
  JTree tree;

  TaskTreeNode root;

  // flag to show hidden tasks
  private boolean showHiddenTasks;

  private final SwitchViewCombobox combobox;

  public TasksTree(final LayoutManager layoutManager,
      AbstractAction switchViewAction) {
    super(new BorderLayout());
    this.layoutManager = layoutManager;
    createTree();
    tree.addTreeSelectionListener(this);
    tree.setCellRenderer(new TasksTreeCellRenderer(layoutManager));

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

    new TreePopupMenu(layoutManager, this, tree);

    layoutManager.getTimeSlotTracker().addActionListener(
        new TaskChangedAction());

    showHiddenTasks = layoutManager.getTimeSlotTracker().getConfiguration()
        .getBoolean(Configuration.TASK_TREE_SHOW_HIDDEN_TASKS, false);

    layoutManager.getTimeSlotTracker().addActionListener(
        new ShowHiddenTaskListener(layoutManager),
        Action.ACTION_SET_CONFIGURATION);

    tree.setDragEnabled(true);
    tree.setTransferHandler(new TaskTreeTransferHandler(this, layoutManager));

    // listen if user double click on some task to edit it
    tree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
          editSelected();
        }
      }
    });

    // prevents from collapse root node
    tree.addTreeWillExpandListener(new TreeWillExpandListener() {
      public void treeWillExpand(TreeExpansionEvent e)
          throws ExpandVetoException {
      }

      public void treeWillCollapse(TreeExpansionEvent e)
          throws ExpandVetoException {
        if (null == e.getPath().getParentPath()) {
          throw new ExpandVetoException(e);
        }
      }
    });

    // adds keyboard shortcuts
    tree.addKeyListener(new TaskTreeKeyAdapter(layoutManager));
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sf.timeslottracker.gui.TasksInterface#activate()
   */
  @Override
  public void activate() {
    valueChanged(null);
    combobox.setSelectedIndex(0);
  }

  void setCutOff(TaskTreeNode sourceTaskNode, LayoutManager layoutManager) {
    if (sourceTaskNode.isCut()) {
      sourceTaskNode.setCut(false);
      layoutManager.getTimeSlotTracker().fireTaskChanged(
          sourceTaskNode.getTask());
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

  public void reloadTree() {
    // saving last selected node
    final Task lastSelectedTask;
    if (root != null && getSelectedTask() != null) {
      lastSelectedTask = getSelectedTask().getTask();
    } else {
      lastSelectedTask = null;
    }

    // construct tree from data source
    dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    if (dataSource == null) {
      String alertTitle = layoutManager.getCoreString("alert.warning.title");
      String alertMsg = layoutManager
          .getString("taskstree.alert.no-data-source");
      JOptionPane.showMessageDialog(this, alertMsg, alertTitle,
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    root = new TaskTreeNode(dataSource.getRoot());
    addChildrenNodes(root);

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        tree.setModel(new DefaultTreeModel(root));

        Task selectedTask = null;
        boolean expand = false;
        if (lastSelectedTask != null) {
          // set on last selected node
          if (lastSelectedTask.isHidden() && !showHiddenTasks) {
            selectedTask = lastSelectedTask.getParentTask();
            expand = true;
          } else {
            selectedTask = lastSelectedTask;
          }
        } else {
          // set on active node
          TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
              .getActiveTimeSlot();
          if (activeTimeSlot != null) {
            selectedTask = activeTimeSlot.getTask();
          }
        }

        if (selectedTask != null) {
          selectTask(selectedTask, expand);
        }
      }
    });
  }

  /**
   * Adds children to given TaskTreeNode node.
   *
   * @param node
   *          node to which we have to add all children from node's task.
   */
  private void addChildrenNodes(TaskTreeNode node) {
    Task parent = node.getTask();
    // layoutManager.getTimeSlotTracker().debugLog("adding children for "+node
    // +": "+parent);

    Collection<Task> childrenCollection = dataSource.getChildren(parent);
    if (childrenCollection == null) {
      return; // well, it shouldn't be null, but an empty collection, but...
    }

    Iterator<Task> children = childrenCollection.iterator();
    while (children.hasNext()) {
      Task child = children.next();

      // filter hidden tasks
      if (!showHiddenTasks && !HIDDEN_TASK_FILTER.accept(child)) {
        continue;
      }

      TaskTreeNode childNode = new TaskTreeNode(child);
      node.add(childNode);
      addChildrenNodes(childNode);
    }
  }

  /**
   * Its role is to response to user selection action
   */
  public void valueChanged(TreeSelectionEvent e) {
    TaskTreeNode selected = (TaskTreeNode) tree.getLastSelectedPathComponent();
    layoutManager.fireTaskSelectionChanged(selected == null ? null : selected
        .getTask());
  }

  /**
   * Called when a new task was added. Tree must be informed about it.
   *
   * @param parentNode
   *          parent node where to add
   * @param taskToAdd
   *          a task we want to add
   * @param index
   *          new task index
   * @param selectNewNode
   *          should select new node in tree
   */
  void addTask(final TaskTreeNode parentNode, final Task taskToAdd, int index,
      boolean selectNewNode) {
    addTask(parentNode, taskToAdd, index, false, selectNewNode);
  }

  /**
   * Called when a new task was added. Tree must be informed about it.
   *
   * @param withChildren
   *          add task with it's children
   * @param selectNewNode
   *          should select new node in tree
   *
   * @see #addTask(TaskTreeNode, Task, int, boolean)
   */
  void addTask(final TaskTreeNode parentNode, final Task taskToAdd, int index,
      boolean withChildren, boolean selectNewNode) {
    TaskTreeNode child = new TaskTreeNode(taskToAdd);
    if (index < 0) {
      parentNode.add(child);
    } else {
      parentNode.insert(child, index);
    }

    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    model.nodeStructureChanged(parentNode);
    model.nodeChanged(child);

    if (withChildren) {
      if (taskToAdd.getChildren() != null) {
        for (Task task : taskToAdd.getChildren()) {
          addTask(child, task, -1, withChildren, selectNewNode);
        }
      }
      return; // do not select path for children
    }

    if (selectNewNode) {
      TreePath path = new TreePath(parentNode.getPath());
      path = path.pathByAddingChild(child);
      select(path);
    }
  }

  public void addTaskFromIssueTracker() {
    Issue issue;
    try {

      final IssueTracker issueTracker = layoutManager.getTimeSlotTracker()
          .getIssueTracker();
      final String filterId = layoutManager.getTimeSlotTracker()
          .getConfiguration().getString(Configuration.JIRA_FILTER, "-1");

      final NewIssueDialog newIssueDialog = new NewIssueDialog(layoutManager);

      new SwingWorker<List<Issue>, Issue>() {
        @Override
        protected List<Issue> doInBackground() throws Exception {

          issueTracker.getFilterIssues(filterId, new IssueHandler() {
            boolean stop = false;

            @Override
            public void handle(Issue issue) throws IssueTrackerException {
              if (isCancelled()) {
                stop = true;
                return;
              }

              publish(issue);
            }

            @Override
            public boolean stopProcess() {
              return stop;
            }
          });

          return Collections.emptyList();
        }

        @Override
        protected void process(List<Issue> issues) {
          for (Issue issue : issues) {
            if (isCancelled()) {
              break;
            }

            newIssueDialog.add(issue);
          }
        }

        @Override
        protected void done() {
          if (isCancelled()) {
            return;
          }
        }

      }.execute();

      newIssueDialog.activate();

      String key = newIssueDialog.getKey();
      if (StringUtils.isBlank(key)) {
        return;
      }
      if (!issueTracker.isValidKey(key)) {
        JOptionPane.showMessageDialog(layoutManager.getTimeSlotTracker()
            .getRootFrame(), layoutManager
            .getCoreString("issueTracker.issue.notValidNumber"), layoutManager
            .getCoreString("alert.warning.title"), JOptionPane.WARNING_MESSAGE);
        return;
      }

      issue = issueTracker.getIssue(key);

      if (issue == null) {
        JOptionPane.showMessageDialog(layoutManager.getTimeSlotTracker()
            .getRootFrame(), layoutManager
            .getCoreString("issueTracker.issue.notFound"), layoutManager
            .getCoreString("alert.warning.title"), JOptionPane.WARNING_MESSAGE);
        return;
      }
    } catch (IssueTrackerException e1) {
      layoutManager.getTimeSlotTracker().errorLog(e1);
      JOptionPane.showMessageDialog(layoutManager.getTimeSlotTracker()
          .getRootFrame(), MessageFormat.format(
          layoutManager.getCoreString("issueTracker.issue.foundError"),
          e1.getMessage()), layoutManager.getCoreString("alert.error.title"),
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    add(issue.getSummary(), Arrays.asList(new Attribute(IssueKeyAttributeType
        .getInstance(), issue.getKey())));
  }

  public void selectTask(Task task, boolean expand) {
    /*
     * The first implementation will just look into every tree node to find it.
     * In the future it should be changed to (for example) keeping a map of
     * treenodes where the key is a task or task's id.
     */
    TaskTreeNode found = findTask(root, task);
    if (found != null) {
      TreePath treePath = new TreePath(found.getPath());
      select(treePath);
      if (expand) {
        tree.expandPath(treePath);
      }
    }
  }

  public void selectTask(Task task) {
    selectTask(task, false);
  }

  private void select(TreePath path) {
    tree.scrollPathToVisible(path);
    tree.setSelectionPath(path);
  }

  public void add(String taskName, Collection<Attribute> attributes) {
    TaskTreeNode selectedTaskNode = getSelectedTask();

    Task parentTask = null;
    if (selectedTaskNode != null) {
      parentTask = selectedTaskNode.getTask();
    }
    if (parentTask == null) {
      return;
    }
    TaskEditDialog dialog = new TaskEditDialog(layoutManager, taskName,
        attributes, false);
    Task added = dialog.getTask();
    if (added != null) {
      DataSource dataSource = layoutManager.getTimeSlotTracker()
          .getDataSource();
      dataSource.moveTask(added, parentTask);
      addTask(selectedTaskNode, added, -1, true);
    }
  }

  @Override
  public void addWoDialog(String name, Collection<Attribute> attributes) {
    TaskTreeNode selectedTaskNode = getSelectedTask();

    Task parentTask = null;
    if (selectedTaskNode != null) {
      parentTask = selectedTaskNode.getTask();
    }
    if (parentTask == null) {
      return;
    }

    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    Task task = dataSource.createTask(parentTask, null, name, null, false);
    task.setAttributes(attributes);

    addTask(selectedTaskNode, task, -1, false);

    layoutManager.getTimeSlotTracker().fireTaskChanged(task);
  }

  @Override
  public Task getSelected() {
    return getSelectedTask() == null ? null : getSelectedTask().getTask();
  }

  private TaskTreeNode getSelectedTask() {
    TreePath selectionPath = tree.getSelectionPath();
    if (selectionPath == null) {
      return null;
    }
    return (TaskTreeNode) selectionPath.getLastPathComponent();
  }

  public void editSelected() {
    TaskTreeNode selectedTaskNode = getSelectedTask();
    if (selectedTaskNode == null) {
      return;
    }

    Task selectedTask = selectedTaskNode.getTask();
    TaskEditDialog dialog = new TaskEditDialog(layoutManager, selectedTask,
        false);
    if (dialog.getTask() != null) {
      DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
      model.nodeChanged(selectedTaskNode);
    }
  }

  /**
   * Creates a sibling task ("clone").
   */
  public void cloneSelected() {
    TaskTreeNode selectedTaskNode = getSelectedTask();
    if (selectedTaskNode == null) {
      return;
    }

    Task selectedTask = selectedTaskNode.getTask();
    Task parentTask = selectedTask.getParentTask();
    Task newTask = null;

    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    if (dataSource != null) {
      newTask = dataSource.copyTask(selectedTask, parentTask, -1, true);
      addTask(findTask(parentTask), newTask, -1, true, true);
    }

    TaskEditDialog dialog = new TaskEditDialog(layoutManager, newTask, false);
    if (dialog.getTask() != null) {
      layoutManager.getTimeSlotTracker().fireTaskChanged(dialog.getTask());
    }
  }

  @Override
  public void setShowHiddenTasks(boolean showHiddenTasks) {
    this.showHiddenTasks = showHiddenTasks;
  }

  @Override
  public boolean showHiddenTasks() {
    return this.showHiddenTasks;
  }

  /**
   * Looks for a specified task, starting at given node.
   *
   * @param node
   *          a parent node to look into. Then (if not found) check children.
   * @param task
   *          a task to be found
   */
  TaskTreeNode findTask(TaskTreeNode node, Task task) {
    if (node.getTask().equals(task)) {
      return node;
    }
    Enumeration children = node.children();
    if (children == null) {
      return null;
    }
    while (children.hasMoreElements()) {
      TaskTreeNode child = (TaskTreeNode) children.nextElement();
      TaskTreeNode found = findTask(child, task);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  public TaskTreeNode findTask(Task task) {
    return findTask(root, task);
  }

  // implementation of ReportContext interface
  public Object getReportContext(Context parameter) {
    if (parameter == ReportContext.Context.STARTING_TASK) {
      TaskTreeNode node = getSelectedTask();
      if (node != null) {
        return node.getTask();
      }
    }
    return null;
  }

  public Point getMenuPoint(boolean forceUseSelectedNode) {
    Point point = new Point();

    Point mousePosition = tree.getMousePosition();
    TreePath pathForLocation;
    if (forceUseSelectedNode || mousePosition == null
        || !tree.contains(mousePosition)) {
      pathForLocation = tree.getSelectionPath();
    } else {
      pathForLocation = tree.getClosestPathForLocation(
          (int) mousePosition.getX(), (int) mousePosition.getY());
    }

    Rectangle pathBounds = tree.getPathBounds(pathForLocation);
    point.setLocation(pathBounds.getMaxX(), pathBounds.getCenterY());

    return point;
  }

  @Override
  public Collection<javax.swing.JMenuItem> getMenuItems() {
    javax.swing.JMenuItem showHideNodes = new JCheckBoxMenuItem(
        new javax.swing.AbstractAction() {
          {
            putValue(javax.swing.Action.NAME,
                layoutManager
                    .getString("menuBar.item.View.TaskTree.ShowHiddenTask"));
            update();
          }

          private void update() {
            putValue(javax.swing.Action.SELECTED_KEY, showHiddenTasks);
          }

          public void actionPerformed(ActionEvent ae) {
            showHiddenTasks = !showHiddenTasks;

            update();

            reloadTree();
          }
        });
    javax.swing.JMenuItem filterTimeSlots = new JCheckBoxMenuItem(
        new FilterTimeSlotsAction(layoutManager));

    return Arrays.asList(showHideNodes, filterTimeSlots);
  }

  /**
   * Hide/unhide selected task in tree
   */
  public void hideSelectedTask() {
    TaskTreeNode selectedTaskNode = getSelectedTask();
    if (selectedTaskNode == null) {
      return;
    }
    Task selectedTask = selectedTaskNode.getTask();

    // check if root
    if (selectedTask.isRoot()) {
      return;
    }

    // changing the state
    selectedTask.setHidden(!selectedTask.isHidden());
    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    if (dataSource != null) {
      dataSource.save(selectedTask);
    }

    // changing the tree
    updateNode(selectedTaskNode);
  }

  private void updateNode(TaskTreeNode selectedTaskNode) {
    if (selectedTaskNode == null) {
      return;
    }

    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    if (!selectedTaskNode.getTask().isHidden() || showHiddenTasks) {
      model.nodeChanged(selectedTaskNode);
    } else {
      // finding next selection node
      DefaultMutableTreeNode nodeForSelection = selectedTaskNode
          .getPreviousSibling();
      if (nodeForSelection == null) {
        nodeForSelection = (DefaultMutableTreeNode) selectedTaskNode
            .getParent();
      }

      // removing node
      model.removeNodeFromParent(selectedTaskNode);

      // selecting previous
      tree.getSelectionModel().setSelectionPath(
          new TreePath(nodeForSelection.getPath()));
    }
  }

  /**
   * Listener to action fired when a task was changed
   * <p>
   * It should repaint that node
   */
  private class TaskChangedAction implements TaskChangedListener {
    public void actionPerformed(Action action) {
      Task task = (Task) action.getParam();
      if (root == null || task == null) {
        return;
      }

      updateNode(findTask(root, task));
    }
  }

  private class ShowHiddenTaskListener implements ActionListener {
    private final LayoutManager layoutManager;

    public ShowHiddenTaskListener(LayoutManager layoutManager) {
      this.layoutManager = layoutManager;
    }

    @Override
    public void actionPerformed(Action action) {
      layoutManager
          .getTimeSlotTracker()
          .getConfiguration()
          .set(Configuration.TASK_TREE_SHOW_HIDDEN_TASKS,
              Boolean.valueOf(showHiddenTasks));
    }
  }

  private class TaskTreeKeyAdapter extends KeyAdapter {
    private final LayoutManager layoutManager;

    private TaskTreeNode selectedTaskNode = null;

    public TaskTreeKeyAdapter(LayoutManager layoutManager) {
      this.layoutManager = layoutManager;
    }

    @Override
    public void keyReleased(KeyEvent e) {
      TimeSlot activeTimeSlot = layoutManager.getTimeSlotTracker()
          .getActiveTimeSlot();
      boolean isActive = activeTimeSlot != null;

      int hideKeyCode = KeyStroke.getKeyStroke(
          layoutManager.getString("taskstree.popupmenu.hideTask.mnemonic"))
          .getKeyCode();

      switch (e.getKeyCode()) {
      case KeyEvent.VK_F2:
        editSelected();
        break;

      case KeyEvent.VK_INSERT:
        if (e.getModifiers() == ActionEvent.ALT_MASK) {
          addTaskFromIssueTracker();
        } else {
          add((String) null, (Collection<Attribute>) null);
        }
        break;

      case KeyEvent.VK_SPACE:
        if (isActive
            && activeTimeSlot.getTask().equals(getSelectedTask().getTask())
            && e.getModifiers() != InputEvent.SHIFT_MASK) {
          layoutManager.getTimeSlotTracker().stopTiming();
        } else {
          layoutManager.getTimeSlotTracker().startTiming();
        }
        break;

      case KeyEvent.VK_DELETE:
        new DeleteTaskAction(layoutManager, tree)
            .actionPerformed(new ActionEvent(this, 0, null));
        break;

      case KeyEvent.VK_UP:
        if (e.getModifiers() == InputEvent.SHIFT_MASK) {
          moveSelectedTaskUp(selectedTaskNode);
        }
        break;

      case KeyEvent.VK_DOWN:
        if (e.getModifiers() == InputEvent.SHIFT_MASK) {
          moveSelectedTaskDown(selectedTaskNode);
        }
        break;

      case KeyEvent.VK_J:
        if (e.getModifiers() == InputEvent.CTRL_MASK) {
          gotoIssueUrl(selectedTaskNode);
        }
        break;

      default:
        if (e.getKeyCode() == hideKeyCode
            && e.getModifiers() == InputEvent.CTRL_MASK) {
          hideSelectedTask();
        }
      }
    }

    @Override
    public void keyPressed(KeyEvent e) {
      selectedTaskNode = getSelectedTask();
    }

  }

  protected void moveSelectedTaskUp(TaskTreeNode selectedTaskNode) {
    Task selectedTask = selectedTaskNode.getTask();
    TaskTreeNode parentNode = (TaskTreeNode) selectedTaskNode.getParent();
    if (parentNode == null) {
      return;
    }
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    int indexOfChild = model.getIndexOfChild(parentNode, selectedTaskNode);
    if (indexOfChild == 0) {
      return;
    }
    model.removeNodeFromParent(selectedTaskNode);
    model.insertNodeInto(selectedTaskNode, parentNode, indexOfChild - 1);
    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    if (dataSource != null) {
      dataSource.moveTask(selectedTask, indexOfChild - 1);
    }
    selectTask(selectedTask);
  }

  protected void moveSelectedTaskDown(TaskTreeNode selectedTaskNode) {
    Task selectedTask = selectedTaskNode.getTask();
    TaskTreeNode parentNode = (TaskTreeNode) selectedTaskNode.getParent();
    if (parentNode == null) {
      return;
    }
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    int indexOfChild = model.getIndexOfChild(parentNode, selectedTaskNode);
    if (indexOfChild >= parentNode.getChildCount() - 1) {
      return;
    }
    model.removeNodeFromParent(selectedTaskNode);
    model.insertNodeInto(selectedTaskNode, parentNode, indexOfChild + 1);
    DataSource dataSource = layoutManager.getTimeSlotTracker().getDataSource();
    if (dataSource != null) {
      dataSource.moveTask(selectedTask, indexOfChild + 1);
    }
    selectTask(selectedTask);
  }

  void gotoIssueUrl(TaskTreeNode selectedTaskNode) {
    Task task = selectedTaskNode.getTask();
    try {
      URI issueUrl = layoutManager.getTimeSlotTracker().getIssueTracker()
          .getIssueUrl(task);

      SwingUtils.browse(issueUrl);
    } catch (IssueTrackerException e1) {
      layoutManager.getTimeSlotTracker().errorLog(e1);
    }
  }

}
