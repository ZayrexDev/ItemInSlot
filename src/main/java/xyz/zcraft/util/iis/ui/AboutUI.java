package xyz.zcraft.idk.ui;

import xyz.zcraft.idk.Main;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutUI {
    private JButton closeBtn;
    private JLabel verLbl;
    private JLabel userLbl;
    private JLabel expLbl;
    private JLabel runtimeLbl;
    private JLabel vmLbl;
    private JLabel titleLbl;
    private JPanel rootPane;
    private JLabel licenseTypeLbl;

    public AboutUI(MainUI mainUI) {
        JDialog dialog = new JDialog(mainUI.jFrame, "关于 Item In Slot", true);
        dialog.setContentPane(rootPane);
        dialog.setResizable(false);
        closeBtn.addActionListener(e -> dialog.dispose());

        titleLbl.setText(String.format(titleLbl.getText(), Main.getProperty("date"), Main.getProperty("edition")));
        verLbl.setText(String.format(verLbl.getText(), Main.getProperty("ver"), Main.getProperty("build-date")));
        userLbl.setText(String.format(userLbl.getText(), Main.getProperty("user")));
        expLbl.setText(String.format(expLbl.getText(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(Main.getProperty("expire"))))));
        licenseTypeLbl.setText(String.format(licenseTypeLbl.getText(), Main.getProperty("license-type")));

        runtimeLbl.setText(String.format(runtimeLbl.getText(), System.getProperty("java.version")));
        vmLbl.setText(String.format(vmLbl.getText(), System.getProperty("java.vm.info")));

        dialog.pack();
        dialog.setLocationRelativeTo(mainUI.jFrame);

        dialog.setVisible(true);
    }
}
