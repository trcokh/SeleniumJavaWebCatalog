package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.LogUtils;
import utils.readers.ConfigReader;

import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private static final int WINDOW_WIDTH = 450;
    private static final int WINDOW_HEIGHT = 800;
    private static final int GAP = 0;
    private static final int MAX_COLUMNS = 3;

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void initDriver(String browser) {
        if (getDriver() != null) {
            LogUtils.warn("Driver already initialized for this thread.");
            return;
        }

        WebDriver webDriver = createWebDriver(browser);
        arrangeWindowPosition(webDriver);

        applyZoom(webDriver);

        DRIVER.set(webDriver);
        openUrl(ConfigReader.getProperty("url"));
    }

    public static void quitDriver() {
        WebDriver driver = getDriver();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                LogUtils.error("Error while quitting WebDriver: " + e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }

    public static void openUrl(String url) {
        try {
            getDriver().get(url);
            applyZoom(getDriver());
        } catch (Exception e) {
            LogUtils.error("Failed to open URL [" + url + "]: " + e.getMessage());
        }
    }

    public static void reloadPage() {
        try {
            getDriver().navigate().refresh();
        } catch (Exception e) {
            LogUtils.error("Failed to reload page: " + e.getMessage());
        }
    }

    public static void openNewTabWithUrl(String url) {
        try {
            WebDriver newTab = getDriver().switchTo().newWindow(WindowType.TAB);
            newTab.get(url);
        } catch (Exception e) {
            LogUtils.error("Failed to open new tab with URL [" + url + "]: " + e.getMessage());
        }
    }

    public static void openNewBrowser(String browser) {
        quitDriver();
        initDriver(browser);
    }

    private static WebDriver createWebDriver(String browser) {
        return switch (browser == null ? "" : browser.toLowerCase()) {
            case "firefox" -> createFirefoxDriver();
            case "edge" -> createEdgeDriver();
            default -> createChromeDriver();
        };
    }

    private static ChromeDriver createChromeDriver() {
        ChromeOptions options = applyCommonArgs(new ChromeOptions());
        setupDriverBinary("chrome");
        return new ChromeDriver(options);
    }

    private static FirefoxDriver createFirefoxDriver() {
        FirefoxOptions options = applyCommonArgs(new FirefoxOptions());
        setupDriverBinary("firefox");
        return new FirefoxDriver(options);
    }

    private static EdgeDriver createEdgeDriver() {
        EdgeOptions options = applyCommonArgs(new EdgeOptions());
        setupDriverBinary("edge");
        return new EdgeDriver(options);
    }

    private static void setupDriverBinary(String browser) {
        boolean useLocal = Boolean.parseBoolean(ConfigReader.getProperty("useLocalDriver", "false"));
        String driverPath = ConfigReader.getProperty("driverPath", "src/test/resources/drivers/");

        if (useLocal) {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            LogUtils.info("Running on OS: " + System.getProperty("os.name"));

            String driverFileName = switch (browser) {
                case "chrome" -> "chromedriver" + (isWindows ? ".exe" : "");
                case "firefox" -> "geckodriver" + (isWindows ? ".exe" : "");
                case "edge" -> "msedgedriver" + (isWindows ? ".exe" : "");
                default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
            };

            File driverFile = new File(driverPath, driverFileName);

            if (!driverFile.exists()) {
                throw new RuntimeException("Local driver not found at: " + driverFile.getAbsolutePath());
            }

            System.setProperty("webdriver." + browser + ".driver", driverFile.getAbsolutePath());
            LogUtils.info("Using local driver: " + driverFile.getAbsolutePath());
        } else {
            switch (browser) {
                case "chrome" -> WebDriverManager.chromedriver().setup();
                case "firefox" -> WebDriverManager.firefoxdriver().setup();
                case "edge" -> WebDriverManager.edgedriver().setup();
            }
        }
    }

    private static <T extends MutableCapabilities> T applyCommonArgs(T options) {
        boolean isCi = System.getenv("CI") != null || System.getenv("JENKINS_HOME") != null;
        boolean configHeadless = Boolean.parseBoolean(ConfigReader.getProperty("headless", "false"));
        boolean isHeadless = isCi || configHeadless;

        String[] baseArgs = {"--disable-infobars", "--disable-extensions", "--disable-popup-blocking"};

        if (options instanceof ChromeOptions chrome) {
            chrome.addArguments(baseArgs);
            if (isHeadless) {
                chrome.addArguments("--headless=new", "--no-sandbox", "--disable-gpu", "--disable-dev-shm-usage", "--window-size=1920,1080");
            }

            chrome.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            chrome.setExperimentalOption("useAutomationExtension", false);

        } else if (options instanceof FirefoxOptions firefox) {
            if (isHeadless) {
                firefox.addArguments("--headless");
            }

            firefox.addPreference("dom.webdriver.enabled", false);
            firefox.addPreference("useAutomationExtension", false);
            firefox.addPreference("dom.webnotifications.enabled", false);
            firefox.addPreference("media.volume_scale", "0.0");
            firefox.addPreference("extensions.enabledScopes", 0);
            firefox.addPreference("extensions.autoDisableScopes", 0);
            firefox.addPreference("dom.disable_open_during_load", false);

        } else if (options instanceof EdgeOptions edge) {
            edge.addArguments(baseArgs);
            if (isHeadless) {
                edge.addArguments("--headless=new", "--disable-gpu", "--disable-dev-shm-usage", "--window-size=1920,1080");
            }

            edge.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            edge.setExperimentalOption("useAutomationExtension", false);
        }

        return options;
    }

    private static void arrangeWindowPosition(WebDriver driver) {
        boolean isAutoArrangeWindows = Boolean.parseBoolean(ConfigReader.getProperty("autoArrangeWindows", "false"));
        if (isAutoArrangeWindows) {
            try {
                int index = COUNTER.getAndIncrement();
                int col = index % MAX_COLUMNS;
                int row = index / MAX_COLUMNS;

                int x = col * (WINDOW_WIDTH + GAP);
                int y = row * (WINDOW_HEIGHT + GAP);

                driver.manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
                driver.manage().window().setPosition(new Point(x, y));
            } catch (Exception e) {
                LogUtils.warn("Unable to arrange browser window: " + e.getMessage());
            }
        }
    }

    public static String openNewTab(String url, String currentWindowHandle) {
        ((JavascriptExecutor) getDriver()).executeScript("window.open('" + url + "', '_blank');");
        for (String handle : getDriver().getWindowHandles()) {
            if (!handle.equals(currentWindowHandle)) {
                getDriver().switchTo().window(handle);
                return handle;
            }
        }
        return null;
    }

    public static void closeTabAndSwitch(String tabToClose, String tabToReturn) {
        if (tabToClose != null) {
            getDriver().switchTo().window(tabToClose).close();
        }
        getDriver().switchTo().window(tabToReturn);
    }

    public static void openVerifyLinkAndReturnToCurrentTab(String verifyLink) throws InterruptedException {
        WebDriver driver = getDriver();

        String mainWindow = driver.getWindowHandle();

        driver.switchTo().newWindow(WindowType.TAB);
        driver.get(verifyLink);

        LogUtils.infoWithScreenshot("Opened verification link: " + verifyLink);

        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        driver.close();

        driver.switchTo().window(mainWindow);
        LogUtils.infoWithScreenshot("Returned to main application tab");
    }

    private static void applyZoom(WebDriver driver) {
        try {
            ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='100%'");
            LogUtils.info("Applied zoom=100% for consistent rendering.");
        } catch (Exception e) {
            LogUtils.warn("Could not apply zoom setting: " + e.getMessage());
        }
    }
}
