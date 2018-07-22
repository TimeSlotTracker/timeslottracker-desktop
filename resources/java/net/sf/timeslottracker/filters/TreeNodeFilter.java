package net.sf.timeslottracker.filters;

import javax.swing.tree.TreeNode;

/**
 * Filter to find node in tree
 * 
 * @version File version: $Revision: 998 $, $Date: 2009-08-06 09:55:17 +0700
 *          (Thu, 06 Aug 2009) $
 * @author Last change: $Author: cnitsa $
 */
public interface TreeNodeFilter extends Filter<TreeNode> {

  @Override
  public boolean accept(TreeNode treeNode);

}