package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import utils.readers.ConfigReader;

public class SignInPage extends BasePage {

    int defaultTimeout = Integer.parseInt(ConfigReader.getProperty("defaultTimeout"));

    private final By btnContinueWithEmail = By.xpath("//a[contains(@href, 'email')]");
    private final By emailInput = By.id("email");
    private final By continueButton = By.xpath("//button[@type='submit']");
    private final By otpInput = By.id("code");
    private final By imgLogo = By.xpath("//a[@title=\"WebCatalog\"]");

    public void clickImgLogo() {
        click(imgLogo, defaultTimeout);
    }

    public boolean isbtnContinueWithEmailDisplayed() {
        return isDisplayed(btnContinueWithEmail, defaultTimeout);
    }

    public void clickBtnContinueWithEmail() {
        click(btnContinueWithEmail, defaultTimeout);
    }

    public void enterEmail(String email) {
        sendKeys(emailInput, email, defaultTimeout);
    }

    public void clickContinueButton() {
        click(continueButton, defaultTimeout);
    }

    public void enterOtp(String otp) {
        sendKeys(otpInput, otp, defaultTimeout);
    }

    public boolean isOTPInputDisplayed() {
        return isDisplayed(otpInput, defaultTimeout);
    }
}