package xyz.zcraft.util.iis.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.zcraft.util.iis.util.CustomTreeModel;
import xyz.zcraft.util.iis.util.Node;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainUI {
    private final Node rootNode;
    public JFrame jFrame;
    private JTree entryTree;
    private JPanel root;
    private boolean changed;
    private boolean autoRefresh = true;
    private Path origPath;
    private static final Logger LOGGER = LogManager.getLogger(MainUI.class);


    public MainUI(Node rootNode, Path origPath) {
        this.rootNode = rootNode;
        this.origPath = origPath;
    }

    private void openAbout() {
        new AboutUI(jFrame);
    }

    private void openStat() {
        new StatUI(jFrame, rootNode);
    }

    private void addObj() {
        new EntryAddUI(jFrame, rootNode, () -> {
            changed = true;
            if (autoRefresh) recreateTree();
        });
    }

    private void initMenuBar() {
        LOGGER.info("Initializing menu bar");
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("文件");

        JMenuItem saveItem = new JMenuItem("保存");
        saveItem.addActionListener(e -> save());
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("另存为");
        saveAsItem.addActionListener(e -> saveAs());
        fileMenu.add(saveAsItem);

        JMenuItem closeItem = new JMenuItem("关闭");
        closeItem.addActionListener(e -> close());
        fileMenu.add(closeItem);

        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("编辑");

        JMenuItem addItem = new JMenuItem("添加项目");
        addItem.addActionListener(e -> addObj());
        editMenu.add(addItem);

        JMenuItem editItem = new JMenuItem("编辑项目");
        editItem.addActionListener(e -> openEditWindow());
        editMenu.add(editItem);

        menuBar.add(editMenu);

        JMenu viewMenu = new JMenu("查看");

        JMenuItem statItem = new JMenuItem("统计信息");
        statItem.addActionListener(e -> openStat());
        viewMenu.add(statItem);

        JMenuItem refreshItem = new JMenuItem("刷新");
        refreshItem.addActionListener(e -> recreateTree());
        viewMenu.add(refreshItem);

        JCheckBoxMenuItem autoRefreshItem = new JCheckBoxMenuItem("自动刷新");
        autoRefreshItem.setState(true);
        autoRefreshItem.addActionListener(e -> autoRefresh = autoRefreshItem.getState());
        viewMenu.add(autoRefreshItem);

        menuBar.add(viewMenu);

        JMenu helpMenu = new JMenu("帮助");

        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> openAbout());
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        jFrame.setJMenuBar(menuBar);
        LOGGER.info("Menu bar initialized");
    }

    private void openEditWindow() {
        if (entryTree.getLastSelectedPathComponent() == null) return;
        new EntryEditUI(jFrame, ((Node) entryTree.getLastSelectedPathComponent()), rootNode, () -> {
            changed = true;
            if (autoRefresh) recreateTree();
        });
    }

    private void close() {
        if (changed) {
            final int i = JOptionPane.showConfirmDialog(jFrame, "数据未保存，确定要退出吗？", "提示", JOptionPane.OK_CANCEL_OPTION);
            if (i == 0) {
                jFrame.dispose();
            }
        } else {
            jFrame.dispose();
        }
    }

    private Path askPath() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new FileNameExtensionFilter("ItemInSlot 文件 (*.iis)", "iis"));
        jFileChooser.showSaveDialog(jFrame);
        File selectedFile = jFileChooser.getSelectedFile();
        if (selectedFile == null) return null;
        if (!selectedFile.getName().endsWith(".iis")) {
            selectedFile = new File(jFileChooser.getCurrentDirectory(), selectedFile.getName() + ".iis");
        }
        LOGGER.info("Path selected:" + selectedFile.toPath());
        return selectedFile.toPath();
    }

    private void saveAs() {
        var selected = askPath();
        if (selected == null) return;
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(selected))) {
            if (!Files.exists(origPath)) Files.delete(origPath);
            objectOutputStream.writeObject(rootNode);
            objectOutputStream.flush();
            changed = false;
            LOGGER.info("Successfully saved data to " + origPath);
        } catch (IOException ex) {
            LOGGER.error("Error in saving data to" + origPath, ex);
            JOptionPane.showMessageDialog(jFrame, "保存至 " + selected.toAbsolutePath() + " 失败:" + ex, "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save() {
        if (origPath == null) {
            final Path path = askPath();
            if (path == null) return;
            origPath = path;
        }

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(origPath))) {
            if (Files.exists(origPath.getParent())) Files.createDirectories(origPath.getParent());
            if (!Files.exists(origPath)) Files.delete(origPath);
            objectOutputStream.writeObject(rootNode);
            objectOutputStream.flush();
            changed = false;
            LOGGER.info("Successfully saved data to " + origPath);
        } catch (IOException ex) {
            LOGGER.error("Error in saving data to" + origPath, ex);
            JOptionPane.showMessageDialog(jFrame, "保存至 " + origPath.toAbsolutePath() + " 失败:" + ex, "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void create() {
        LOGGER.info("Creating main UI");
        jFrame = new JFrame();
        jFrame.setContentPane(root);
        jFrame.pack();
        jFrame.setSize(400, 200);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame.setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!changed) System.exit(0);
                final int i = JOptionPane.showConfirmDialog(jFrame, "数据未保存，确定要退出吗？", "提示", JOptionPane.OK_CANCEL_OPTION);
                if (i == 0) System.exit(0);
            }
        });

        jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        recreateTree();
        initMenuBar();

        jFrame.setVisible(true);
    }

    void recreateTree() {
        LOGGER.info("Updating tree");
        entryTree.setModel(new CustomTreeModel(rootNode));
        entryTree.updateUI();
    }
}
