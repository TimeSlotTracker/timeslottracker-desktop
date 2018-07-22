package net.sf.timeslottracker.gui.layouts.classic.search;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

import net.sf.timeslottracker.gui.Column;
import net.sf.timeslottracker.gui.LayoutManager;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Table model for search result list.
 * 
 * @author User: zgibek Date: 2008-09-03 Time: 00:00:34 $Id:
 *         ResultsTableModel.java 800 2009-05-16 01:53:21Z cnitsa $
 */
public class ResultsTableModel extends AbstractTableModel {

  private Column[] columns;
  private final ResultWindow resultWindow;
  private final LayoutManager layoutManager;
  private ArrayList rows;

  public ResultsTableModel(ResultWindow resultWindow,
      LayoutManager layoutManager, ArrayList rows) {
    this.resultWindow = resultWindow;
    this.layoutManager = layoutManager;
    this.rows = rows;
    localizeRows();
    setColumns();
  }

  /**
   * Translate strings in results to localized versions.
   */
  private void localizeRows() {
    if (rows != null) {
      for (Object o : rows) {
        Document d = (Document) o;
        String localizedString = layoutManager
            .getString("search.resultWindow.type." + d.get("type"));
        Field type = d.getField("type");
        type.setValue(localizedString);
      }
    }
  }

  private void setColumns() {
    columns = new Column[3];
    columns[0] = new Column(
        layoutManager.getString("search.resultWindow.table.column.type"), 120,
        JLabel.LEFT);
    columns[1] = new Column(
        layoutManager.getString("search.resultWindow.table.column.task"), 160,
        JLabel.LEFT);
    columns[2] = new Column(
        layoutManager.getString("search.resultWindow.table.column.content"),
        450, JLabel.LEFT);

  }

  public int getRowCount() {
    return rows.size();
  }

  public int getColumnCount() {
    return 3;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    if (rowIndex < 0 || rowIndex >= getRowCount()) {
      return null;
    }
    if (columnIndex < 0 || columnIndex >= getColumnCount()) {
      return null;
    }

    Document doc = getDocument(rowIndex);
    switch (columnIndex) {
    case 0:
      return doc.get("type");
    case 1:
      return doc.get("task_name");
    case 2:
      StringBuffer buf = new StringBuffer();
      String[] v = doc.getValues("contents");
      if (v == null) {
        return "";
      }
      for (String s : v) {
        if (buf.length() > 0) {
          buf.append('\n');
        }
        buf.append(s);
      }
      return buf;
      // return doc.get("contents");
    }

    return null;
  }

  public Document getDocument(int rowIndex) {
    if (rowIndex < 0 || rowIndex >= getRowCount()) {
      return null;
    }
    return (Document) rows.get(rowIndex);
  }

  public String getColumnName(int columnNo) {
    return columns[columnNo].getName();
  }

  public int getColumnWidth(int columnNo) {
    return columns[columnNo].getWidth();
  }

  public int getColumnAlignment(int columnNo) {
    return columns[columnNo].getAlignment();
  }

}
