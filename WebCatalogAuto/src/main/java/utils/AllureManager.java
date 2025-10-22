package utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public class AllureManager {

    private static boolean isAllureTestActive() {
        try {
            Optional<String> current = Allure.getLifecycle().getCurrentTestCaseOrStep();
            return current.isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    @Attachment(value = "{0}", type = "text/plain")
    public static String attachText(String message) {
        if (!isAllureTestActive()) {
            System.out.println("ℹ️ [AllureManager] No active test case. Skipping text attachment.");
            return message;
        }

        try {
            return message;
        } catch (Exception e) {
            System.err.println("⚠️ [AllureManager] Failed to attach text: " + e.getMessage());
            return message;
        }
    }

    public static void attachScreenshot(WebDriver driver) {
        if (driver == null) {
            System.out.println("ℹ️ [AllureManager] WebDriver is null, skipping screenshot.");
            return;
        }

        if (!isAllureTestActive()) {
            System.out.println("ℹ️ [AllureManager] No active test case. Skipping screenshot attachment.");
            return;
        }

        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("📸 Screenshot", new ByteArrayInputStream(screenshot));
        } catch (Exception e) {
            System.err.println("⚠️ [AllureManager] Failed to attach screenshot: " + e.getMessage());
        }
    }
}