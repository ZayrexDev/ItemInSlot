package xyz.zcraft.util.iis.util;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class CustomTreeModel implements TreeModel {
    private final Node root;
    private final EventListenerList eventListenerList = new EventListenerList();

    public CustomTreeModel(Node root) {
        this.root = root;
    }

    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    public Node getChild(Object parent, int index) {
        return ((Node) parent).getChild().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((Node) parent).getChild().size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((Node) node).getChild().size() == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((Node) parent).getChild().indexOf(((Node) child));
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        eventListenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        eventListenerList.remove(TreeModelListener.class, l);
    }
}
