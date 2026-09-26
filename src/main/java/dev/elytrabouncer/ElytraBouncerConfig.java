package dev.elytrabouncer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ElytraBouncerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElytraBouncerConfig.class);
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("elytra-bouncer.properties");
    private static boolean isEnabled = true;

    private ElytraBouncerConfig() {
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            return;
        }

        Properties savedConfig = new Properties();

        try (InputStream configInput = Files.newInputStream(CONFIG_PATH)) {
            savedConfig.load(configInput);
            String enabledSetting = savedConfig.getProperty("enabled");

            if ("true".equalsIgnoreCase(enabledSetting)) {
                isEnabled = true;
                return;
            }

            if ("false".equalsIgnoreCase(enabledSetting)) {
                isEnabled = false;
                return;
            }

            LOGGER.warn("Ignoring invalid enabled setting in {}", CONFIG_PATH);
        } catch (IOException exception) {
            LOGGER.warn("Could not load Elytra Bouncer settings from {}", CONFIG_PATH, exception);
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setEnabled(boolean shouldEnable) {
        isEnabled = shouldEnable;
    }

    public static void saveConfig() {
        Properties savedConfig = new Properties();
        String enabledSetting = Boolean.toString(isEnabled);
        savedConfig.setProperty("enabled", enabledSetting);

        try {
            Path configurationDirectory = CONFIG_PATH.getParent();
            Files.createDirectories(configurationDirectory);

            try (OutputStream configOutput = Files.newOutputStream(CONFIG_PATH)) {
                savedConfig.store(configOutput, "Elytra Bouncer settings");
            }
        } catch (IOException exception) {
            LOGGER.warn("Could not save Elytra Bouncer settings to {}", CONFIG_PATH, exception);
        }
    }
}
