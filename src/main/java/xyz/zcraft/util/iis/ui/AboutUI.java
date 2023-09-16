package xyz.zcraft.util.iis.ui;

import xyz.zcraft.util.iis.Main;

import javax.swing.*;
import java.awt.*;

public class AboutUI {
    private JButton closeBtn;
    private JLabel verLbl;
    private JLabel runtimeLbl;
    private JLabel vmLbl;
    private JLabel titleLbl;
    private JPanel rootPane;

    public AboutUI(Frame owner) {
        JDialog dialog = new JDialog(owner, "关于 Item In Slot", true);
        dialog.setContentPane(rootPane);
        dialog.setResizable(false);
        closeBtn.addActionListener(e -> dialog.dispose());

        titleLbl.setText(String.format(titleLbl.getText(), Main.getProperty("date")));
        verLbl.setText(String.format(verLbl.getText(), Main.getProperty("ver"), Main.getProperty("build-date")));

        runtimeLbl.setText(String.format(runtimeLbl.getText(), System.getProperty("java.version")));
        vmLbl.setText(String.format(vmLbl.getText(), System.getProperty("java.vm.info")));

        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        dialog.setVisible(true);
    }
}
