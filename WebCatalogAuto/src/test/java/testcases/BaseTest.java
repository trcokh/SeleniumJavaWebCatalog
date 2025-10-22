package testcases;

import base.DriverFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.AssertUtils;
import utils.annotations.DriverPerTest;
import utils.annotations.DriverPerMethod;

public class BaseTest {

    private boolean isPerTest = false;
    private boolean isPerMethod = false;

    @Parameters("browser")
    @BeforeTest(alwaysRun = true)
    public void beforeTest(@Optional("chrome") String browser) {
        System.out.println("[BeforeTest] Preparing test group setup...");
    }

    @Parameters("browser")
    @BeforeClass(alwaysRun = true)
    public void beforeClass(@Optional("chrome") String browser) {
        Class<?> clazz = this.getClass();
        isPerMethod = clazz.isAnnotationPresent(DriverPerMethod.class);
        isPerTest = clazz.isAnnotationPresent(DriverPerTest.class);

        if (isPerTest) {
            System.out.println("[Setup] Initializing driver once per CLASS for: " + clazz.getSimpleName());
            DriverFactory.initDriver(browser);
        } else if (!isPerMethod) {
            System.out.println("[Setup] Initializing default driver once per CLASS for: " + clazz.getSimpleName());
            DriverFactory.initDriver(browser);
        }
    }

    @Parameters("browser")
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(@Optional("chrome") String browser) {
        if (isPerMethod) {
            System.out.println("[Setup] Initializing driver per METHOD for: " + this.getClass().getSimpleName());
            DriverFactory.initDriver(browser);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        AssertUtils.assertAll();
        if (isPerMethod) {
            System.out.println("[Teardown] Quitting driver per METHOD for: " + this.getClass().getSimpleName());
            DriverFactory.quitDriver();
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        Class<?> clazz = this.getClass();
        if (isPerTest || !isPerMethod) {
            System.out.println("[Teardown] Quitting driver once per CLASS for: " + clazz.getSimpleName());
            DriverFactory.quitDriver();
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        System.out.println("[AfterTest] Completed test group teardown.");
    }
}