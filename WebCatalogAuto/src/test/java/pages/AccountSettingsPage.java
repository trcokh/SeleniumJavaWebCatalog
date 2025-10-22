package pages;

import org.openqa.selenium.By;
import utils.readers.ImageReader;

public class AccountSettingsPage extends BasePage {
    private By lnkAccountTab(String tabName) {
        return By.xpath(String.format("//a[contains(@href,'account/%s')]", tabName));
    }

    private final By inputName = By.xpath("//input[@name=\"name\"]");
    private final By btnSelectCountry = By.xpath("//select/preceding-sibling::button[not(@title=\"Language\")]");
    private final By optionNotSelected = By.xpath("//div[@role=\"option\"and@data-state=\"unchecked\"]");
    private final By txtSelectedCountry = By.xpath("//select/preceding-sibling::button[not(@title=\"Language\")]/span[@data-slot=\"select-value\"]");
    private final By inputEmail = By.xpath("//input[@name=\"email\"]");
    private final By btnUpdate = By.xpath("//button[@type=\"submit\"]");
    private final By textSuccessMessage = By.xpath("//li[@tabindex=\"0\"]//div[@data-title]");
    private final By btnDeleteAccount = By.xpath("//div[@data-slot=\"card\"]//button");
    private final By btnDeleteAccountConfirm = By.xpath("//div[@role=\"alertdialog\"]//button[2]");
    private final By btnChoosePicture = By.id("picture");
    private final By imgProfileAvatar = By.xpath("//img[@data-slot=\"avatar-image\" and not(@alt)]");

    public void clickTabProfile() {
        click(lnkAccountTab("profile"), defaultTimeout);
    }

    public void clickTabEmail() {
        click(lnkAccountTab("email"), defaultTimeout);
    }

    public void clickTabTeam() {
        click(lnkAccountTab("team"), defaultTimeout);
    }

    public void clickTabBilling() {
        click(lnkAccountTab("billing"), defaultTimeout);
    }

    public void clickTabAccount() {
        click(lnkAccountTab("account"), defaultTimeout);
    }

    public void enterName(String name) {
        sendKeysAfterAttributeLoaded(inputName, "value", name, defaultTimeout);
    }

    public String getTextName() {
        return getAttribute(inputName, "value", defaultTimeout);
    }

    public void clickSelectCountry() {
        click(btnSelectCountry, defaultTimeout);
    }

    public void clickOptionNotSelected() {
        clickRandomElement(optionNotSelected, defaultTimeout);
    }

    public String getTextSelectedCountry() {
        return waitForNonEmptyText(txtSelectedCountry, defaultTimeout);
    }


    public void enterEmail(String email) {
        sendKeysAfterAttributeLoaded(inputEmail, "value", email, defaultTimeout);
    }

    public void clickEmailField() {
        click(inputEmail, defaultTimeout);
    }

    public String getTextEmail() {
        return getAttribute(inputEmail, "value", defaultTimeout);
    }

    public void clickBtnUpdate() {
        click(btnUpdate, defaultTimeout);
    }

    public boolean isBtnUpdateClickable() {
        return isClickable(btnUpdate, defaultTimeout);
    }

    public String getTextSuccessMessage() {
        return waitForNonEmptyText(textSuccessMessage, defaultTimeout);
    }

    public void clickBtnDeleteAccount() {
        click(btnDeleteAccount, defaultTimeout);
    }

    public void clickBtnDeleteAccountConfirm() {
        click(btnDeleteAccountConfirm, defaultTimeout);
    }

    public void uploadPicture(String imagePath) {
        sendKeys(btnChoosePicture, imagePath, defaultTimeout);
    }

    public String getProfileAvatarPath() {
        return getAttribute(imgProfileAvatar, "src", defaultTimeout);
    }

    public boolean isUploadedProfileAvatarCorrect(String profileAvatarPath, String uploadedImagePath) {
        return ImageReader.areImagesVisuallySimilar(profileAvatarPath, uploadedImagePath, 500);
    }
}