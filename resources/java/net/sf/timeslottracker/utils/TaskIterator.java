package net.sf.timeslottracker.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import net.sf.timeslottracker.data.Task;

/**
 * Iterator for tasks
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-05-16 08:53:21 +0700
 *          (Sat, 16 May 2009) $
 * @author Last change: $Author: cnitsa $
 */
public class TaskIterator implements Iterator<Task> {

  private Stack<Task> stack = new Stack<Task>();

  public TaskIterator(Task task) {
    this.stack.push(task);
  }

  @Override
  public boolean hasNext() {
    return !stack.isEmpty();
  }

  @Override
  public Task next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    Task task = stack.pop();
    if (task.getChildren() != null) {
      stack.addAll(task.getChildren());
    }

    return task;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
