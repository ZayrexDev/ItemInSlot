package xyz.zcraft.util.iis.util;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node implements Serializable {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private List<Node> child;
    @Getter
    @Setter
    private List<Mark> marks;
    @Getter
    @Setter
    private Node parent;

    public Node(String title, Node parent) {
        this.title = title;
        this.parent = parent;
        this.child = new LinkedList<>();
        this.marks = new LinkedList<>();
    }

    public Node(String title) {
        this.title = title;
        this.parent = null;
        this.child = new LinkedList<>();
        this.marks = new LinkedList<>();
    }

    public String getPathString() {
        LinkedList<Node> path = new LinkedList<>();
        path.add(this);
        Node parent = this.getParent();
        StringBuilder sb = new StringBuilder();
        while (parent != null) {
            path.add(parent);
            parent = parent.getParent();
        }
        sb.append(path.get(path.size() - 1).getTitle());
        for (int i = path.size() - 2; i >= 0; i--) {
            sb.append(" -> ").append(path.get(i).getTitle());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return title;
    }

    public boolean deepContains(Node other) {
        if (other == this) return true;
        Node parent = other.getParent();
        while (parent != null) {
            if (parent == this) return true;
            parent = parent.getParent();
        }
        return false;
    }

    public List<Node> getAllChild() {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(this);

        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(this);

        while (queue.size() > 0) {
            final Node cur = queue.poll();
            cur.getChild().forEach(queue::offer);
            nodes.addAll(cur.getChild());
        }

        return nodes;
    }
}
