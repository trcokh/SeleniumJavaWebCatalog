package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import utils.LogUtils;

public class HomePage extends BasePage {
    private final By imgLogo = By.xpath("//a[@aria-label=\"Home\"]");
    private final By btnLSignIn = By.xpath("//header//button[@data-slot='button'][1]");
    private final By btnSubmitNewApp = By.xpath("//a[contains(@href,\"submit\")]");
    private final By btnAvatarMenu = By.xpath("//span[@data-slot=\"avatar\"]//ancestor::button");
    private final By txtAccountName = By.xpath("//div[@data-slot=\"dropdown-menu-content\"]//p[2]");
    private final By btnAccountSettings = By.xpath("//a[@data-slot=\"dropdown-menu-item\"]");
    private final By btnLogout = By.xpath("//button[@data-slot=\"dropdown-menu-item\"]");

    public void clickImgLogo() {
        click(imgLogo, defaultTimeout);
    }

    public void clickBtnLSignIn() {
        click(btnLSignIn, 20);
    }

    public void clickBtnSubmitNewApp() {
        click(btnSubmitNewApp, defaultTimeout);
    }

    public void clickBtnAvatarMenu() {
        click(btnAvatarMenu, defaultTimeout);
    }

    public String getTextAccountName() {
        return getText(txtAccountName, defaultTimeout);
    }

    public void clickBtnAccountSettings() {
        click(btnAccountSettings, defaultTimeout);
    }

    public void clickBtnLogout() {
        click(btnLogout, defaultTimeout);
    }

    public boolean isAvatarMenuDisplayed () {
            return isDisplayed(btnAvatarMenu, defaultTimeout);
    }
}