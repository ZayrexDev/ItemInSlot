package xyz.zcraft.idk.ui;

import xyz.zcraft.idk.util.Entry;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainUI {
    private JTree entryTree;
    private JPanel root;
    private JButton newCatBtn;
    private JButton newObjBtn;
    private JButton rankBtn;
    private JButton aboutBtn;
    public JFrame jFrame;
    private HashMap<String, DefaultMutableTreeNode> catMap;

    public MainUI() {
        newObjBtn.addActionListener(e -> new AddEntryUI(this));
        newCatBtn.addActionListener(e -> {
            String s = JOptionPane.showInputDialog("分类名称:");
            if(s == null || s.trim().equals("")) return;
            if(catList.contains(s)) {
                JOptionPane.showMessageDialog(jFrame, "分类已存在");
            } else {
                catList.add(s);

                recreateTree();
            }
        });
        rankBtn.addActionListener(e -> new RankUI(this));
        aboutBtn.addActionListener(e -> new AboutUI(this));
    }

    public void create() {
        jFrame = new JFrame();
        jFrame.setContentPane(root);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame.setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);

        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Path dataPath = Path.of("data", "entries.dat");
                try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(dataPath))) {
                    oos.writeObject(entryList);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "程序遇到了严重错误，即将退出。错误信息如下:" + ex, "错误", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });

        initTree();

        jFrame.setVisible(true);
    }

    private List<Entry> entryList = new LinkedList<>();
    private final List<String> catList = new LinkedList<>();

    public void addEntry(Entry entry) {
        if(isDuplicated(entry)) {
            JOptionPane.showMessageDialog(jFrame, "项目已存在");
        } else {
            entryList.add(entry);
            recreateTree();
        }
    }

    public boolean isDuplicated(Entry entry) {
        return entryList.contains(entry);
    }

    public boolean isDuplicated(String name, String cat) {
        return entryList.stream().anyMatch(e -> Objects.equals(e.getName(), name) && Objects.equals(e.getCat(), cat));
    }

    public List<String> getCats() {
        return catList;
    }

    private void initTree() {
        Path dataPath = Path.of("data","entries.dat");
        if (!Files.exists(dataPath)) {
            entryList = new LinkedList<>();
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(dataPath))) {
                //noinspection unchecked
                entryList = (List<Entry>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "程序遇到了严重错误，即将退出。错误信息如下:" + e, "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }

        recreateTree();
    }

    void recreateTree() {
        System.out.println("Recreating tree");
        catMap = new HashMap<>();

        DefaultMutableTreeNode topNode = new DefaultMutableTreeNode("项目");

        catList.forEach(e -> catMap.putIfAbsent(e, new DefaultMutableTreeNode(e){
            @Override
            public boolean isLeaf() {
                return false;
            }
        }));

        for (Entry entry : entryList) {
            if (!catList.contains(entry.getCat())) {
                catList.add(entry.getCat());
                catMap.putIfAbsent(entry.getCat(), new DefaultMutableTreeNode(entry.getCat()){
                    @Override
                    public boolean isLeaf() {
                        return false;
                    }
                });
            }
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(entry);
            newChild.setAllowsChildren(false);
            catMap.get(entry.getCat()).add(newChild);
        }

        for (DefaultMutableTreeNode defaultMutableTreeNode : catMap.values()) {
            topNode.add(defaultMutableTreeNode);
        }

        entryTree.setModel(new DefaultTreeModel(topNode));
        entryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        entryTree.addTreeSelectionListener(e -> {
            if (entryTree.getSelectionModel().getSelectionPath() != null && ((DefaultMutableTreeNode) entryTree.getLastSelectedPathComponent()).getUserObject() instanceof Entry entry) {
                final EntryEditUI entryEditUI = new EntryEditUI(this, entry);
                System.out.println(entry);
                if(!entryEditUI.show()) recreateTree();
                entryTree.clearSelection();
            }
        });

        entryTree.setShowsRootHandles(false);
    }

    public void removeEntry(Entry oldEntry) {
        entryList.remove(oldEntry);

        recreateTree();
    }

    public List<Entry> getEntries() {
        return entryList;
    }
}
