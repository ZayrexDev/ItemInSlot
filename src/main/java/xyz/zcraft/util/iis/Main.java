package xyz.zcraft.util.iis;

import com.formdev.flatlaf.FlatLightLaf;
import xyz.zcraft.util.iis.ui.MenuUI;

import java.io.IOException;
import java.util.Properties;

public class Main {
    private static Properties properties;

    public static void main(String[] args) throws IOException {
        properties = new Properties();
        properties.load(Main.class.getResourceAsStream("prop.properties"));

        FlatLightLaf.setup();

        launch();
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static void launch() {
        new MenuUI();
    }
}
