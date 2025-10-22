package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import utils.readers.ConfigReader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {
    private static ExtentReports extent;
    private static ExtentTest test;

    public static ExtentTest getTest() {
        return test;
    }

    public static void setTest(ExtentTest testInstance) {
        test = testInstance;
    }

    public static ExtentReports getTestReport(String testName) {
        if (extent == null) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String baseReportPath = ConfigReader.getProperty("reportPath"); // e.g., "./reports/"
            String reportDir = baseReportPath + File.separator + currentDate;

            new File(reportDir).mkdirs();

            String timestamp = new SimpleDateFormat("HHmmss").format(new Date());
            String reportFileName = reportDir + File.separator + testName + "_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFileName);
            sparkReporter.config().setDocumentTitle("Test Report");
            sparkReporter.config().setReportName("Test Execution Report");
            sparkReporter.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
        }

        return extent;
    }
}