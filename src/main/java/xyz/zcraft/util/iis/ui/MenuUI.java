package xyz.zcraft.util.iis.ui;

import xyz.zcraft.util.iis.util.Node;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MenuUI {
    private final JFrame jFrame;
    private JButton openBtn;
    private JButton newBtn;
    private JPanel rootPane;
    private JButton aboutBtn;

    public MenuUI() {
        jFrame = new JFrame("欢迎访问 Item In Slot");
        jFrame.setContentPane(rootPane);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);

        newBtn.addActionListener(e -> {
            final String s = JOptionPane.showInputDialog(jFrame, "根节点名:");
            if (s.trim().equals("")) {
                JOptionPane.showMessageDialog(jFrame, "未命名根节点", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Node n = new Node(s);
            new MainUI(n, null).create();
            jFrame.dispose();
        });

        openBtn.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.setMultiSelectionEnabled(false);
            jFileChooser.setFileFilter(new FileNameExtensionFilter("ItemInSlot 文件 (*.iis)", "iis"));
            jFileChooser.showOpenDialog(null);
            final File selectedFile = jFileChooser.getSelectedFile();
            if (selectedFile == null) return;
            final Path selected = selectedFile.toPath();
            Node rootNode = null;
            Path path = null;

            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(selected))) {
                rootNode = (Node) ois.readObject();
                path = selected;
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(jFrame, "打开 " + selected.toAbsolutePath() + " 失败:" + ex, "错误", JOptionPane.ERROR_MESSAGE);
                jFrame.dispose();
                System.exit(0);
            }

            new MainUI(rootNode, path).create();
            jFrame.dispose();
        });
        aboutBtn.addActionListener(e -> new AboutUI(jFrame));

        jFrame.setVisible(true);
    }
}
