package pages;

import base.DriverFactory;
import org.openqa.selenium.*;
import utils.readers.ConfigReader;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YopmailPage extends BasePage {
    private final By iframeInbox = By.id("ifinbox");
    private final By iframeMail = By.id("ifmail");
    private final By latestMail = By.cssSelector("div.m");
    private final By mailBody = By.id("mail");
    private final By refreshButton = By.id("refresh");
    private final By btnVerifyEmail = By.xpath("//a[contains(@href,'auth-api.webcatalog')]");

    public String openYopmailTab(String appWindow) {
        return DriverFactory.openNewTab(ConfigReader.getProperty("yopMailUrl"), appWindow);
    }

    public void openInbox(String email) {
        DriverFactory.getDriver().get(ConfigReader.getProperty("yopMailUrl") + "?login=" + email);
    }

    public void openLatestMail(String email) {
        openInbox(email);
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            click(refreshButton, 60);
        }
        switchToFrame(iframeInbox, defaultTimeout);
        fluentWait(defaultTimeout).until(driver -> !driver.findElements(latestMail).isEmpty());

        List<WebElement> mails = DriverFactory.getDriver().findElements(latestMail);
        if (!mails.isEmpty()) {
            scrollToElement(mails.get(0));
            mails.get(0).click();
        } else {
            throw new RuntimeException("No mail found after 3 refreshes for: " + email);
        }
        DriverFactory.getDriver().switchTo().defaultContent();
    }


    public String getLatestOtp() {
        switchToFrame(iframeMail, defaultTimeout);
        String body = waitForNonEmptyText(mailBody, defaultTimeout);
        DriverFactory.getDriver().switchTo().defaultContent();

        Matcher matcher = Pattern.compile("(?i)(?:code|otp)[^\\d]*(\\d{6})").matcher(body);
        if (matcher.find()) return matcher.group(1);

        matcher = Pattern.compile("\\b\\d{6}\\b").matcher(body);
        if (matcher.find()) return matcher.group(0);

        throw new RuntimeException("No 6-digit OTP found in email body: " + body);
    }


    public void closeYopmailTab(String yopmailHandle, String appWindow) {
        DriverFactory.closeTabAndSwitch(yopmailHandle, appWindow);
    }

    public String generateRandomYopMail() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10) + "@yopmail.com";
    }

    public void clickVerifyEmailLink() {
        switchToFrame(iframeMail, defaultTimeout);
        click(btnVerifyEmail, defaultTimeout);
        DriverFactory.getDriver().switchTo().defaultContent();
    }
}