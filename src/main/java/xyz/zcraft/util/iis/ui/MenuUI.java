package xyz.zcraft.util.iis.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.zcraft.util.iis.Main;
import xyz.zcraft.util.iis.util.Node;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class MenuUI {
    private final JFrame jFrame;
    private JButton openBtn;
    private JButton newBtn;
    private JPanel rootPane;
    private JButton aboutBtn;
    private JList<Path> recentFileJList;
    private DefaultListModel<Path> recentFileModel;
    private JLabel dateLabel;
    private JLabel versionLabel;
    private static final Logger LOGGER = LogManager.getLogger(MenuUI.class);

    public MenuUI() {
        jFrame = new JFrame("欢迎访问 Item In Slot");
        jFrame.setContentPane(rootPane);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);

        newBtn.addActionListener(e -> {
            final String s = JOptionPane.showInputDialog(jFrame, "根节点名:");
            if(s == null) return;
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
            Node rootNode;
            Path path;

            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(selected))) {
                rootNode = (Node) ois.readObject();
                path = selected;
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(jFrame, "打开 " + selected.toAbsolutePath() + " 失败:" + ex, "错误", JOptionPane.ERROR_MESSAGE);
                LOGGER.error("Failed to open " + selected.toAbsolutePath(), ex);
                return;
            }

            new MainUI(rootNode, path).create();
            recentFileModel.addElement(path);
            saveRecent();
            jFrame.dispose();
        });
        aboutBtn.addActionListener(e -> new AboutUI(jFrame));

        recentFileJList.addListSelectionListener(e -> {
            final Path selectedValue = recentFileJList.getSelectedValue();
            Node rootNode;
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(selectedValue))) {
                rootNode = (Node) ois.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(jFrame, "打开 " + selectedValue.toAbsolutePath() + " 失败:" + ex, "错误", JOptionPane.ERROR_MESSAGE);
                LOGGER.error("Failed to open " + selectedValue.toAbsolutePath(), ex);
                recentFileModel.removeElement(selectedValue);
                return;
            }

            new MainUI(rootNode, selectedValue).create();
            jFrame.dispose();
        });

        dateLabel.setText(Main.getProperty("date"));
        versionLabel.setText(Main.getProperty("ver"));

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveRecent();
            }
        });

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadRecentFiles();

        jFrame.setVisible(true);
    }

    private void saveRecent() {
        LOGGER.info("Trying to save recent files...");
        final Path of = Path.of("data", "recent.bin");
        ObjectOutputStream oos = null;
        try {
            Files.createDirectories(of.getParent());
            if(Files.exists(of)) Files.delete(of);
            Files.createFile(of);
            oos = new ObjectOutputStream(Files.newOutputStream(of));
            List<String> l = new LinkedList<>();
            for(int i = 0; i < recentFileModel.size(); i++) {
                l.add(recentFileModel.get(i).toAbsolutePath().toString());
            }
            oos.writeObject(l);
            oos.flush();
            LOGGER.info("Recent files successfully saved.");
        } catch (IOException ex) {
            LOGGER.error("Failed to save recent files", ex);
        } finally {
            if(oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    LOGGER.error("Failed to close stream", ex);
                }
            }
        }
    }

    private void loadRecentFiles() {
        final Path of = Path.of("data", "recent.bin");
        recentFileModel = new DefaultListModel<>();
        if (Files.exists(of)) {
            try {
                ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(of));
                //noinspection unchecked
                var e = ((List<String>) ois.readObject());
                if(e!= null) e.forEach(s -> recentFileModel.addElement(Path.of(s)));
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.error("Failed to load recent files", e);
            }
        }

        recentFileJList.setModel(recentFileModel);
    }
}
