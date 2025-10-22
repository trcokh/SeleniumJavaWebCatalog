package utils;

import base.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class LogUtils {
    private static final Logger logger = LogManager.getLogger(LogUtils.class);

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String PURPLE = "\u001B[35m";

    public static void info(String message) {
        logger.info(CYAN + "ℹ️  " + message + RESET);
        AllureManager.attachText("ℹ️ " + message);
    }

    public static void success(String message) {
        logger.info(GREEN + "✅  " + message + RESET);
        AllureManager.attachText("✅ " + message);
    }

    public static void warn(String message) {
        logger.warn(YELLOW + "⚠️  " + message + RESET);
        AllureManager.attachText("⚠️ " + message);
    }

    public static void error(String message) {
        logger.error(RED + "❌  " + message + RESET);
        AllureManager.attachText("❌ " + message);

        attachScreenshotIfAvailable();
    }

    public static void error(String message, Object var2, Object var3) {
        logger.error(RED + "❌  " + message + RESET, var2, var3);
        AllureManager.attachText("❌ " + message);
        attachScreenshotIfAvailable();
    }

    public static void error(String message, Object... vars) {
        logger.error(RED + "❌  " + message + RESET, vars);
        AllureManager.attachText("❌ " + message);
        attachScreenshotIfAvailable();
    }

    public static void infoWithScreenshot(String message) {
        logger.info(PURPLE + "📸  " + message + RESET);
        AllureManager.attachText("📸 " + message);
        attachScreenshotIfAvailable();
    }

    private static void attachScreenshotIfAvailable() {
        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            AllureManager.attachScreenshot(driver);
        }
    }

    public static void step(String message) {
        logger.info(PURPLE + "🪜 STEP → " + message + RESET);
        AllureManager.attachText("🪜 " + message);
    }
}