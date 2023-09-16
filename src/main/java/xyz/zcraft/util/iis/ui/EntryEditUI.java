package xyz.zcraft.idk.ui;

import xyz.zcraft.idk.util.Entry;
import xyz.zcraft.idk.util.Mark;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class EntryEditUI {
    private final JDialog dialog;
    private final DefaultListModel<Mark> model;
    private final Entry oldEntry;
    private JTextField nameField;
    private JButton confirmBtn;
    private JButton delBtn;
    private JList<Mark> markJList;
    private JTextArea markText;
    private JButton addMarkBtn;
    private JButton delMarkBtn;
    private JPanel rootPane;
    private JComboBox<String> catCombo;
    private JLabel editTimeLbl;
    private JButton saveMarkBtn;
    private JPanel markEditPane;
    private JTextField markTitleField;

    public EntryEditUI(MainUI mainUI, Entry selectedEntry) {
        oldEntry = selectedEntry;

        dialog = new JDialog(mainUI.jFrame, "编辑项目", true);
        dialog.setContentPane(rootPane);
        dialog.pack();
        dialog.setLocationRelativeTo(mainUI.jFrame);

        nameField.setText(selectedEntry.getName());
        catCombo.setModel(new DefaultComboBoxModel<>(mainUI.getCats().toArray(new String[0])));
        catCombo.setSelectedItem(selectedEntry.getCat());

        model = new DefaultListModel<>();
        model.addAll(oldEntry.getMarks());
        markJList.setModel(model);
        markJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        delBtn.addActionListener(e -> {
            mainUI.removeEntry(oldEntry);
            dialog.dispose();
        });

        confirmBtn.addActionListener(e -> {
            if ((!Objects.equals(oldEntry.getCat(), Objects.requireNonNull(catCombo.getSelectedItem()).toString()) || !Objects.equals(oldEntry.getName(), Objects.requireNonNull(nameField.getText())) && mainUI.isDuplicated(nameField.getText(), Objects.requireNonNull(catCombo.getSelectedItem()).toString()))) {
                JOptionPane.showMessageDialog(dialog, "项目重复");
                return;
            }
            oldEntry.setName(nameField.getText());
            oldEntry.setCat(Objects.requireNonNull(catCombo.getSelectedItem()).toString());
            oldEntry.getMarks().clear();
            for (int i = 0; i < model.getSize(); i++) {
                oldEntry.getMarks().add(model.get(i));
            }
            mainUI.recreateTree();
            dialog.dispose();
        });

        delMarkBtn.addActionListener(e -> {
            if (markJList.getSelectedValue() != null) {
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

        addMarkBtn.addActionListener(e -> model.addElement(new Mark("新标记", "")));
    }

    public boolean show() {
        dialog.setVisible(true);

        return oldEntry.getName().equals(nameField.getText()) && oldEntry.getCat().equals(catCombo.getSelectedItem());
    }
}
