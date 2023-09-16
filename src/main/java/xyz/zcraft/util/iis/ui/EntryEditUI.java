package xyz.zcraft.util.iis.ui;

import xyz.zcraft.util.iis.util.Mark;
import xyz.zcraft.util.iis.util.Node;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EntryEditUI {
    private final JDialog dialog;
    private final DefaultListModel<Mark> model;
    private boolean markChanged = false;
    private JTextField nameField;
    private JButton confirmBtn;
    private JButton delBtn;
    private JList<Mark> markJList;
    private JTextArea markText;
    private JButton addMarkBtn;
    private JButton delMarkBtn;
    private JPanel rootPane;
    private JLabel editTimeLbl;
    private JButton saveMarkBtn;
    private JPanel markEditPane;
    private JTextField markTitleField;
    private JPanel pathPane;
    private JPanel markPane;
    private JTextField pathField;
    private JButton browseBtn;
    private Node selectedParent = null;

    public EntryEditUI(Frame owner, Node selected, Node root, Runnable onEdit) {
        dialog = new JDialog(owner, "编辑项目", true);
        dialog.setContentPane(rootPane);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        nameField.setText(selected.getTitle());

        model = new DefaultListModel<>();
        model.addAll(selected.getMarks());
        markJList.setModel(model);
        markJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (selected.getParent() == null) {
            pathField.setText("(根节点)");
            browseBtn.setEnabled(false);
        } else {
            pathField.setText(selected.getParent().getPathString());
        }

        delBtn.addActionListener(e -> {
            selected.getParent().getChild().remove(selected);
            onEdit.run();
            dialog.dispose();
        });

        confirmBtn.addActionListener(e -> {
            if (selected.getTitle().equals(nameField.getText())
                    && selectedParent == null
                    && !markChanged) {
                dialog.dispose();
            } else {
                System.out.println("ee");
                selected.setTitle(nameField.getText());
                selected.setParent(selectedParent);
                selected.getMarks().clear();
                for (int i = 0; i < model.getSize(); i++) {
                    selected.getMarks().add(model.get(i));
                }
                onEdit.run();
                dialog.dispose();
            }
        });

        delMarkBtn.addActionListener(e -> {
            if (markJList.getSelectedValue() != null) {
                markChanged = true;
                model.removeElement(markJList.getSelectedValue());
                markEditPane.setVisible(false);
            }
        });

        saveMarkBtn.addActionListener(e -> {
            final Mark selectedValue = markJList.getSelectedValue();
            selectedValue.setContent(markText.getText());
            selectedValue.setTime(Calendar.getInstance());
            selectedValue.setTitle(markTitleField.getText());
            editTimeLbl.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedValue.getTime().getTime()));
            markChanged = true;
            markJList.updateUI();
        });

        markJList.addListSelectionListener(e -> {
            final Mark selectedValue = markJList.getSelectedValue();
            if (selectedValue == null) return;
            markText.setText(selectedValue.getContent());
            editTimeLbl.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedValue.getTime().getTime()));
            markTitleField.setText(selectedValue.getTitle());
            markEditPane.setVisible(true);
        });

        addMarkBtn.addActionListener(e -> {
            markChanged = true;
            model.addElement(new Mark("新标记", ""));
        });

        browseBtn.addActionListener(e -> {
            selectedParent = new PathBrowser(owner, root, selected).show();
            if (selectedParent == null) return;
            pathField.setText(selectedParent.getPathString());
        });

        dialog.setVisible(true);
    }
}
