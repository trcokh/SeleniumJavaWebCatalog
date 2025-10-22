package listeners;

import base.DriverFactory;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.AllureManager;
import utils.CaptureHelpers;
import utils.ExtentManager;
import utils.LogUtils;

public class TestListener implements ITestListener {

    private String suiteName;

    private String getTestName(ITestResult result) {
        return result.getTestName() != null
                ? result.getTestName()
                : result.getMethod().getConstructorOrMethod().getName();
    }

    private String getTestDetail(ITestResult result) {
        return result.getMethod().getDescription() != null
                ? result.getMethod().getDescription()
                : result.getMethod().getMethodName();
    }

    @Override
    public void onStart(ITestContext context) {
        suiteName = context.getName();
        LogUtils.info("🚀 Starting test suite: " + suiteName);
        ExtentManager.getTestReport(suiteName);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testMethod = getTestName(result);
        LogUtils.info("▶️ Starting test: " + testMethod);

        ExtentTest test = ExtentManager.getTestReport(suiteName)
                .createTest(testMethod, getTestDetail(result));
        ExtentManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        handleResult(result, Status.PASS, "✅ Test Passed: " + getTestDetail(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String message = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Unknown failure";
        handleResult(result, Status.FAIL, "❌ Test Failed: " + message);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        handleResult(result, Status.SKIP, "⚠️ Test Skipped: " + getTestDetail(result));
    }

    private void handleResult(ITestResult result, Status status, String message) {
        String testName = getTestName(result);
        WebDriver driver = DriverFactory.getDriver();
        ExtentTest test = ExtentManager.getTest();

        LogUtils.info(status == Status.PASS ? "✅ " + testName + " passed"
                : status == Status.FAIL ? "❌ " + testName + " failed"
                : "⚠️ " + testName + " skipped");

        if (test != null) {
            test.log(status, message);

            if (driver != null) {
                CaptureHelpers.logWithScreenShot(driver, status, message);
            }
        }

        try {
            AllureManager.attachText(message);
            if (driver != null) {
                AllureManager.attachScreenshot(driver);
            }
        } catch (Exception e) {
            LogUtils.warn("⚠️ Could not attach Allure result for " + testName + ": " + e.getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        LogUtils.info("🏁 Finished test suite: " + context.getName());
        ExtentManager.getTestReport(suiteName).flush();
    }
}