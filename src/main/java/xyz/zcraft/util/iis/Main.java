package xyz.zcraft.util.iis;

import com.formdev.flatlaf.FlatLightLaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.zcraft.util.iis.ui.MenuUI;

import java.io.IOException;
import java.util.Properties;

public class Main {
    private static Properties properties;

    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        LOGGER.info("Launching program");
        properties = new Properties();
        properties.load(Main.class.getResourceAsStream("prop.properties"));
        LOGGER.info("Properties loaded successfully." + properties);

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
