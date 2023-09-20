package xyz.zcraft.util.iis.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.zcraft.util.iis.util.Node;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class StatUI {
    private static final String[] columnNames = {"标题", "路径", "次数"};
    private JTable rankTable;
    private JPanel rootPane;

    public StatUI(Frame owner, Node rootNode) {
        initTable(rootNode);
        initDialog(owner);
    }

    private void initDialog(Frame owner) {
        JDialog dialog = new JDialog(owner, "统计信息", true);
        dialog.setContentPane(rootPane);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
    private static final Logger LOGGER = LogManager.getLogger(StatUI.class);

    private void initTable(Node rootNode) {
        final DefaultTableModel dataModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rankTable.setModel(dataModel);

        LOGGER.info("Collecting statistics");
        final List<Node> collect = rootNode.getAllChild();
        collect.sort(Comparator.comparingInt(o -> o.getChild().size()));
        collect.forEach(entry -> dataModel.addRow(new Object[]{entry, entry.getPathString(), entry.getMarks().size()}));
        LOGGER.info("Statistics collected. " + collect.size() + " nodes collected.");

        rankTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
