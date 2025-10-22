package utils.readers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties PROPS = loadProps();

    private ConfigReader() {}

    private static Properties loadProps() {
        Properties p = new Properties();
        try (InputStream in = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("configs.properties")) {

            if (in == null) {
                throw new IllegalStateException("configs.properties not found on the classpath");
            }
            p.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load configs.properties", e);
        }
        return p;
    }

    public static String getProperty(String key) {
        return PROPS.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue);
    }
}
