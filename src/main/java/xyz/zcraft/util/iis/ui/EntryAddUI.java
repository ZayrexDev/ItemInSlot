package xyz.zcraft.util.iis.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.zcraft.util.iis.util.Node;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class EntryAddUI {
    private final JDialog dialog;
    private JTextField titleField;
    private JTextField pathField;
    private JButton browseBtn;
    private JButton confirmBtn;
    private JButton cancelBtn;
    private JPanel rootPanel;
    private JButton importBtn;
    private Node selectedParent = null;
    private static final Logger LOGGER = LogManager.getLogger(EntryEditUI.class);
    public EntryAddUI(Frame owner, Node root, Runnable onAdd) {
        dialog = new JDialog(owner, "添加项目", true);
        dialog.setContentPane(rootPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        browseBtn.addActionListener(e -> {
            selectedParent = new PathBrowser(owner, root, null).show();

            if (selectedParent == null) return;

            pathField.setText(selectedParent.getPathString());
        });

        confirmBtn.addActionListener(e -> {
            if (!titleField.getText().trim().equals("")) {
                if (selectedParent != null) {
                    final Node e1 = new Node(titleField.getText(), selectedParent);
                    selectedParent.getChild().add(e1);
                    onAdd.run();
                    dialog.dispose();

                    LOGGER.info("Added new node: " + e1);
                } else {
                    JOptionPane.showMessageDialog(dialog, "未指定路径", "警告", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "未指定标题", "警告", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        importBtn.addActionListener(e -> {
            if (selectedParent != null) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("ItemInSlot 文件 (*.iis)", "iis"));
                jFileChooser.showOpenDialog(null);
                final File selectedFile = jFileChooser.getSelectedFile();
                if (selectedFile == null) return;
                final Path selected = selectedFile.toPath();
                Node rootNode;

                try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(selected))) {
                    rootNode = (Node) ois.readObject();
                    rootNode.setParent(selectedParent);
                } catch (IOException | ClassNotFoundException ex) {
                    LOGGER.error("Error in importing from " + selected.toAbsolutePath(), ex);
                    JOptionPane.showMessageDialog(dialog, "打开 " + selected.toAbsolutePath() + " 失败:" + ex, "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                selectedParent.getChild().add(rootNode);
                onAdd.run();
                dialog.dispose();

                LOGGER.info("Imported node from" + selected.toAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(dialog, "未指定路径", "警告", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
