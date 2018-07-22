package net.sf.timeslottracker.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.sf.timeslottracker.Starter;
import net.sf.timeslottracker.core.Action;
import net.sf.timeslottracker.core.SearchEngine;
import net.sf.timeslottracker.core.TimeSlotTracker;
import net.sf.timeslottracker.core.TimeSlotTrackerException;
import net.sf.timeslottracker.data.Attribute;
import net.sf.timeslottracker.data.Task;
import net.sf.timeslottracker.data.TimeSlot;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * Implements the search engine using the Lucine.
 * <p/>
 * <code>http://lucene.apache.org/java/docs/index.html</code>
 * <p/>
 * Index is created in memory on every app starting (but in background)
 * 
 * @author User: zgibek Date: 2008-08-30 Time: 17:27:07 $Id: LuceneSearch.java
 *         800 2009-05-16 01:53:21Z cnitsa $
 */
public class LuceneSearch implements SearchEngine {
  /**
   * stores the singleton instance of class
   */
  private static LuceneSearch instance = null;

  private TimeSlotTracker tst = null;

  /**
   * stores the index directory *
   */
  private Directory indexDir;

  private IndexWriter indexWriter;

  private IndexSearcher indexSearcher;

  private LuceneSearch() {
    tst = Starter.getTimeSlotTracker();
    try {
      indexDir = new RAMDirectory();
      indexWriter = new IndexWriter(indexDir, new StandardAnalyzer(), true);
    } catch (IOException e) {
      e.printStackTrace();
      tst.errorLog(e);
    }
  }

  public static synchronized LuceneSearch getInstance() {
    if (instance == null) {
      instance = new LuceneSearch();
    }
    return instance;
  }

  /**
   * remembers is the index is already created or not *
   */
  private boolean indexCreated = false;

  public synchronized void createIndex(final Task root) {
    if (indexDir == null || indexWriter == null) {
      tst.errorLog("indexDir=" + indexDir + " ; indexWriter=" + indexWriter);
    }
    if (indexCreated) {
      return;
    }
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          indexTask(root, true);
          indexCreated = true;
          tst.fireAction(new Action(SearchEngine.INDEX_CREATED, this,
              Boolean.TRUE));
          indexWriter.flush();
          indexSearcher = new IndexSearcher(indexDir);
        } catch (Exception e) {
          indexCreated = false;
          tst.fireAction(new Action(SearchEngine.INDEX_CREATED, this,
              Boolean.FALSE));
          tst.errorLog(e);
        }
      }
    };
    Thread searchThread = new Thread(runnable);
    searchThread.setPriority(Thread.MIN_PRIORITY);
    searchThread.start();
  }

  /**
   * Indexes the given task (and timeslots) and calls itself for subtasks.
   * 
   * @param task
   *          task to index.
   */
  private void indexTask(Task task, boolean includeChildren)
      throws TimeSlotTrackerException {
    if (task == null) {
      throw new TimeSlotTrackerException(tst, "searchEngine.error.taskIsNull");
    }
    Action indexingAction = new Action(SearchEngine.INDEXING, this,
        task.getName());
    tst.fireAction(indexingAction);
    Thread.yield();
    tst.debugLog("Indexing task: " + task.getName());

    try {
      // first add task and it's attributes
      Document doc = new Document();
      doc.add(new Field("type", "task", Field.Store.YES, Field.Index.NO));
      doc.add(new Field("task_id", task.getId().toString(), Field.Store.YES,
          Field.Index.UN_TOKENIZED));
      doc.add(new Field("task_name", task.getName(), Field.Store.YES,
          Field.Index.NO));
      addFieldText(doc, "contents", task.getName());
      addFieldText(doc, "contents", task.getDescription());
      doc.add(new Field("task.hidden", task.isHidden() ? "yes" : "no",
          Field.Store.YES, Field.Index.UN_TOKENIZED));
      indexWriter.addDocument(doc);
      if (task.getAttributes() != null) {
        for (Attribute attr : task.getAttributes()) {
          if (!attr.getAttributeType().getCategory().includeInIndex()) {
            continue;
          }
          Document aDoc = new Document();
          aDoc.add(new Field("type", "attr", Field.Store.YES, Field.Index.NO));
          aDoc.add(new Field("task_id", task.getId().toString(),
              Field.Store.YES, Field.Index.UN_TOKENIZED));
          aDoc.add(new Field("task_name", task.getName(), Field.Store.YES,
              Field.Index.NO));
          aDoc.add(new Field("contents", attr.get() == null ? StringUtils.EMPTY
              : attr.get().toString(), Field.Store.YES, Field.Index.TOKENIZED));
          indexWriter.addDocument(aDoc);
        }
      }

      // then add timeslots as new documents
      for (TimeSlot slot : task.getTimeslots()) {
        Document slotDoc = new Document();
        slotDoc.add(new Field("task_id", task.getId().toString(),
            Field.Store.YES, Field.Index.UN_TOKENIZED));
        slotDoc.add(new Field("task_name", task.getName(), Field.Store.YES,
            Field.Index.NO));
        slotDoc.add(new Field("type", "timeslot", Field.Store.YES,
            Field.Index.NO));
        slotDoc.add(new Field("timeslot_id", String.valueOf(slot.getId()),
            Field.Store.YES, Field.Index.NO));
        addFieldText(slotDoc, "contents", slot.getDescription());
        if (slot.getAttributes() != null) {
          for (Attribute attr : slot.getAttributes()) {
            if (attr.get() != null) {
              addFieldText(slotDoc, "contents", attr.get().toString());
            }
          }
        }
        indexWriter.addDocument(slotDoc);
      }

      // and add sub tasks
      if (includeChildren && task.getChildren() != null) {
        for (Task child : task.getChildren()) {
          indexTask(child, includeChildren);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      throw new TimeSlotTrackerException(tst, "searchEngine.indeException");
    }
  }

  private void addFieldText(Document doc, String name, String value) {
    if (value == null) {
      return;
    }
    doc.add(new Field(name, value, Field.Store.YES, Field.Index.TOKENIZED));
  }

  public synchronized Collection<Document> doSearch(String query)
      throws TimeSlotTrackerException {
    if (!indexCreated) {
      throw new TimeSlotTrackerException(tst,
          "searchEngine.error.indexNotCreated");
    }
    if (indexSearcher == null) {
      throw new TimeSlotTrackerException(tst,
          "searchEngine.error.indexSearcher.searcherNotCreated");
    }
    ArrayList<Document> found = new ArrayList<Document>();
    try {
      QueryParser queryParser = new QueryParser("contents",
          new StandardAnalyzer());
      if (!query.contains(" ")) {
        query += "*";
      }
      Query q = queryParser.parse(query);
      Hits hits = indexSearcher.search(q);
      for (int i = 0; i < hits.length(); i++) {
        Document doc = hits.doc(i);
        tst.debugLog("Doc[" + i + "].type=[" + doc.get("type") + "], "
            + "task_id=" + doc.get("task_id") + ", contents=["
            + doc.get("contents") + "]");
        found.add(doc);
      }
    } catch (Exception e) {
      e.printStackTrace();
      tst.errorLog(e);
    } finally {
      tst.fireAction(new Action(SearchEngine.SEARCHING_FINISHED, this, found));
    }
    return found;
  }

  public synchronized void update(Task task) {
    if (task == null) {
      return;
    }
    try {
      indexSearcher.close();

      Term termToDelete = new Term("task_id", task.getId().toString());
      indexWriter.deleteDocuments(termToDelete);

      indexTask(task, false);
      indexWriter.optimize();
      indexWriter.flush();

      indexSearcher = new IndexSearcher(indexDir);
    } catch (IOException e) {
      e.printStackTrace();
      tst.errorLog(e);
    } catch (TimeSlotTrackerException e) {
      e.printStackTrace();
      tst.errorLog(e);
    }
  }

  public synchronized void update(TimeSlot timeSlot) {
    update(timeSlot.getTask());
  }
}
