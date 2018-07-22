package net.sf.timeslottracker.gui.layouts.classic.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.ActionListener;
import net.sf.timeslottracker.core.SearchEngine;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.core.TimeSlotTrackerException;
import net.sf.timeslottracker.data.DataLoadedListener;
import net.sf.timeslottracker.data.DataSource;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TaskChangedListener;
import net.sf.timeslottracker.data.TimeSlot;
import net.sf.timeslottracker.data.TimeSlotChangedListener;
import net.sf.timeslottracker.gui.LayoutManager;
import net.sf.timeslottracker.search.LuceneSearch;
import net.sf.timeslottracker.utils.StringUtils;

/**
 * Used to query the database and to shows the results.
 * 
 * @author User: zgibek Date: 2008-08-30 Time: 18:40:01 $Id: SearchPanel.java
 *         800 2009-05-16 01:53:21Z cnitsa $
 */
public class SearchPanel extends JPanel {
  private JTextField searchField = new JTextField();

  private JProgressBar progressBar = new JProgressBar();

  private SearchEngine engine;

  private TimeSlotTracker tst;

  private final LayoutManager layoutManager;

  public SearchPanel(LayoutManager layoutManager) {
    this.layoutManager = layoutManager;
    this.tst = layoutManager.getTimeSlotTracker();
    engine = LuceneSearch.getInstance();
    tst.addActionListener(new IndexCreatedAction(), SearchEngine.INDEX_CREATED);
    tst.addActionListener(new IndexingAction(), SearchEngine.INDEXING);
    tst.addActionListener(new FoundAction(), SearchEngine.SEARCHING_FINISHED);
    tst.addActionListener(new DataLoadedAction());
    tst.addActionListener(new TaskChangedAction());
    layoutManager.addActionListener(new TimeSlotChangedAction());
    createGui();
  }

  private void createGui() {
    setLayout(new BorderLayout());
    String labelText = layoutManager.getString("taskstree.search.title") + " ";
    String labelMnenonic = layoutManager
        .getString("taskstree.search.title.mnemonic");

    final JLabel searchLabel = new JLabel(labelText, JLabel.LEFT);
    searchLabel.setLabelFor(searchField);
    if (labelMnenonic != null && labelMnenonic.length() == 1) {
      searchLabel.setDisplayedMnemonic(labelMnenonic.charAt(0));
    }
    add(searchLabel, BorderLayout.WEST);

    progressBar.setIndeterminate(true);
    progressBar.setStringPainted(true);
    progressBar.setString(layoutManager
        .getString("taskstree.search.initializing"));
    add(progressBar, BorderLayout.CENTER);

    searchField.setEnabled(false);
    searchField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        String text = searchField.getText();
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !StringUtils.isBlank(text)) {
          try {
            e.consume();
            searchField.setEnabled(false);
            // because we accept the use of ":" not as a keyword description we
            // have to escape it
            StringBuffer textBuffer = new StringBuffer(text);
            int start = 0;
            while (textBuffer.indexOf(":", start) >= 0) {
              int i = textBuffer.indexOf(":", start);
              textBuffer.insert(i, '\\');
              start = i + 2;
            }
            text = textBuffer.toString();
            //System.out.println("Searching for [" + text + "]");
            engine.doSearch(text);
          } catch (TimeSlotTrackerException e1) {
            e1.printStackTrace();
            searchField.setEnabled(true);
          }
        }
      }
    });

    Dimension preferredSize = new Dimension(220, 22);
    setPreferredSize(preferredSize);
    setMaximumSize(preferredSize);
  }

  private class IndexCreatedAction implements ActionListener {
    public void actionPerformed(Action action) {
      //System.out.println(action.toString());
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          remove(progressBar);
          add(searchField, BorderLayout.CENTER);
          searchField.setEnabled(true);
          revalidate();
        }
      });
    }
  }

  private class IndexingAction implements ActionListener {
    public void actionPerformed(Action action) {
      final String taskName = action.getParam().toString();
      //System.out.println(taskName);
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          progressBar.setString(taskName);
        }
      });
    }
  }

  private class DataLoadedAction implements DataLoadedListener {
    public void actionPerformed(Action action) {
      DataSource dataSource = (DataSource) action.getSource();
      engine.createIndex(dataSource.getRoot());
    }
  }

  private class FoundAction implements DataLoadedListener {
    public void actionPerformed(Action action) {
      if (action == null || action.getParam() == null
          || !(action.getParam() instanceof Collection)) {
        tst.errorLog("FoundAction triggered, but there is no collection in parm.");
        return;
      }
      ArrayList found = (ArrayList) action.getParam();
      if (found.isEmpty()) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            String title = layoutManager.getString("search.nothingFound.title");
            String msg = layoutManager.getString("search.nothingFound.msg");
            JOptionPane.showMessageDialog(SearchPanel.this, msg, title,
                JOptionPane.WARNING_MESSAGE);
          }
        });
      } else {
        // show window with results
        ResultWindow resultWindow = new ResultWindow(layoutManager, found);
      }
      searchField.setEnabled(true);
      searchField.transferFocus();
    }
  }

  /**
   * Listener to action fired when a task was changed
   */
  private class TaskChangedAction implements TaskChangedListener {
    public void actionPerformed(Action action) {
      Task task = (Task) action.getParam();
      engine.update(task);
    }
  }

  /**
   * Listener to action fired when a timeslot was changed
   */
  private class TimeSlotChangedAction implements TimeSlotChangedListener {
    public void actionPerformed(Action action) {
      TimeSlot ts = (TimeSlot) action.getParam();
      engine.update(ts);
    }
  }

}
