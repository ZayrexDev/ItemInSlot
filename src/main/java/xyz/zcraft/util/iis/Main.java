package xyz.zcraft.idk;

import com.formdev.flatlaf.FlatLightLaf;
import xyz.zcraft.idk.ui.ActivateUI;
import xyz.zcraft.idk.ui.MainUI;

import java.io.IOException;
import java.util.Properties;

public class Main {
    private static Properties properties;

    public static void main(String[] args) throws IOException {
        properties = new Properties();
        properties.load(Main.class.getResourceAsStream("prop.properties"));

        FlatLightLaf.setup();

        final ActivateUI activateUI = new ActivateUI();
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public static void openMain() {
        new MainUI().create();
    }
}
