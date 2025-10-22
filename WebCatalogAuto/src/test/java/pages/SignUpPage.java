package pages;

import org.openqa.selenium.By;

public class SignUpPage extends BasePage {
    private final By btnContinueWithEmail = By.xpath("//a[contains(@href, 'email')]");
    private final By inputEmail = By.id("email");
    private final By btnContinue = By.xpath("//button[@data-slot=\"button\"]");
    private final By inputOTP = By.id("code");
    private final By btnSignIn = By.xpath("//button[@data-slot=\"button\"]");
    private final By lnkSignIn = By.xpath("//a[contains(@href, \"login\") and not(contains(@href, \"login-with\"))]");
    private final By imgLogo = By.xpath("//a[@title=\"WebCatalog\"]");

    public boolean isImgLogoDisplayed() {
        return isDisplayed(imgLogo, defaultTimeout);
    }
    public void clickImgLogo() {
        click(imgLogo, defaultTimeout);
    }

    public void clickBtnContinueWithEmail() {
        click(btnContinueWithEmail, defaultTimeout);
    }

    public void enterEmail(String email) {
        sendKeys(inputEmail, email, defaultTimeout);
    }

    public void clickBtnContinue() {
        click(btnContinue, defaultTimeout);
    }

    public void enterOTP(String otp) {
        sendKeys(inputOTP, otp, defaultTimeout);
    }

    public void clickSignInLink() {
        click(lnkSignIn, defaultTimeout);
    }
}