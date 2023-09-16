package xyz.zcraft.util.iis.ui;

import xyz.zcraft.util.iis.util.Node;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class RankUI {
    private JTable rankTable;
    private JPanel rootPane;
    private final JDialog dialog;

    public RankUI(Frame owner, Node rootNode) {
        dialog = new JDialog(owner, "标记排行", true);
        dialog.setContentPane(rootPane);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        initTable(rootNode);

        dialog.setVisible(true);
    }

    private static final String[] columnNames = {"标题", "路径", "次数"};
    private void initTable(Node rootNode) {
        final DefaultTableModel dataModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankTable.setModel(dataModel);
        final List<Node> collect = rootNode.getAllChild();
        collect.sort(Comparator.comparingInt(o -> o.getChild().size()));
        collect.forEach(entry -> dataModel.addRow(new Object[]{entry, entry.getPathString(), entry.getMarks().size()}));

        rankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
