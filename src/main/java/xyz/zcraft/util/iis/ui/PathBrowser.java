package xyz.zcraft.util.iis.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.zcraft.util.iis.util.CustomTreeModel;
import xyz.zcraft.util.iis.util.Node;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class PathBrowser {
    private static final Logger LOGGER = LogManager.getLogger(PathBrowser.class);
    private final JDialog dialog;
    private JTree browseTree;
    private JPanel rootPanel;
    private JTextField pathChosenField;
    private JButton confirmBtn;
    private JButton cancelBtn;

    public PathBrowser(Frame owner, Node rootNode, Node currentNode) {
        dialog = new JDialog(owner, "选择路径", true);
        dialog.setContentPane(rootPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        cancelBtn.addActionListener(e -> {
            browseTree.clearSelection();
            dialog.dispose();
        });

        confirmBtn.addActionListener(e -> {
            if (browseTree.getSelectionModel().getSelectionPath() != null) {
                if (currentNode != null && currentNode.deepContains((Node) browseTree.getLastSelectedPathComponent())) {
                    JOptionPane.showMessageDialog(dialog, "不能包含自己", "警告", JOptionPane.WARNING_MESSAGE);
                } else {
                    dialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "未选择路径", "警告", JOptionPane.WARNING_MESSAGE);
            }
        });

        browseTree.setModel(new CustomTreeModel(rootNode));
        browseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        browseTree.addTreeSelectionListener(e -> {
            if (browseTree.getSelectionModel().getSelectionPath() != null)
                pathChosenField.setText(((Node) browseTree.getLastSelectedPathComponent()).getPathString());
            else {
                pathChosenField.setText("");
            }
        });
    }

    public Node show() {
        dialog.setVisible(true);
        if (browseTree.getSelectionModel().getSelectionPath() != null) {
            final Node lastSelectedPathComponent = (Node) browseTree.getLastSelectedPathComponent();
            LOGGER.info("Selected parent: " + lastSelectedPathComponent);
            return lastSelectedPathComponent;
        } else return null;
    }
}
