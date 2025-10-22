package testcases;

import base.DriverFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.*;
import utils.AssertUtils;
import utils.LogUtils;
import utils.annotations.DriverPerTest;

@DriverPerTest
public class AccountSettingsTest extends BaseTest {
    HomePage homePage;
    AccountSettingsPage accountSettingsPage;
    SignUpPage signupPage;

    @BeforeClass
    public void preCondition() {
        homePage = new HomePage();
        accountSettingsPage = new AccountSettingsPage();
        signupPage = new SignUpPage();

        LogUtils.infoWithScreenshot("Pre-condition: Sign Up");
        homePage.clickBtnLSignIn();
        signupPage.clickBtnContinueWithEmail();
        MailTmPage.MailAccount acc = MailTmPage.generateRandomAccount();
        MailTmPage mail = new MailTmPage(acc.address(), acc.password());
        signupPage.enterEmail(acc.address());
        signupPage.clickBtnContinue();
        signupPage.enterOTP(mail.extractOtpFromMail());

        LogUtils.infoWithScreenshot("Pre-condition: Go to Account Settings page");
        homePage.clickBtnAvatarMenu();
        homePage.clickBtnAccountSettings();
    }

    @Test
    public void AS01_testUpdateAccountName() {
        accountSettingsPage.clickTabProfile();
        LogUtils.infoWithScreenshot("Step 1: Click \"Profile\" tab");
        accountSettingsPage.enterName("UpdateAccountName");
        LogUtils.infoWithScreenshot("Step 2: Input data to \"Name\" field");
        accountSettingsPage.clickBtnUpdate();
        LogUtils.infoWithScreenshot("Step 3: Click \"Update\" button");
        AssertUtils.softAssertEquals(accountSettingsPage.getTextSuccessMessage(), "Updated successfully!", "Verify success message is displayed");
        DriverFactory.reloadPage();
        LogUtils.infoWithScreenshot("Step 4: Reload page");
        AssertUtils.assertEquals(accountSettingsPage.getTextName(), "UpdateAccountName", "Verify \"Name\" field still displays the updated value");
    }

    @Test
    public void AS02_testUpdateAccountCountry() {
        accountSettingsPage.clickTabProfile();
        LogUtils.infoWithScreenshot("Step 1: Click \"Profile\" tab");
        accountSettingsPage.enterName("UpdateAccountName");
        accountSettingsPage.clickSelectCountry();
        LogUtils.infoWithScreenshot("Step 2: Open \"Country\" dropdown");
        accountSettingsPage.clickOptionNotSelected();
        String selectedOption = accountSettingsPage.getTextSelectedCountry();
        LogUtils.infoWithScreenshot("Step 3: Select the country " + selectedOption);
        accountSettingsPage.clickBtnUpdate();
        LogUtils.infoWithScreenshot("Step 4: Click \"Update\" button");
        AssertUtils.softAssertEquals(accountSettingsPage.getTextSuccessMessage(), "Updated successfully!", "Verify success message is displayed");
        DriverFactory.reloadPage();
        LogUtils.infoWithScreenshot("Step 5: Reload page");
        AssertUtils.assertEquals(accountSettingsPage.getTextSelectedCountry(), selectedOption, "Verify \"Country\" dropdown still displays " + selectedOption);
    }

    @Test
    public void AS03_testUpdateAccountEmail() {
        accountSettingsPage.clickTabEmail();
        LogUtils.infoWithScreenshot("Step 1: Click \"Email address\" tab");
        MailTmPage.MailAccount acc = MailTmPage.generateRandomAccount();
        MailTmPage mail = new MailTmPage(acc.address(), acc.password());
        accountSettingsPage.enterEmail(acc.address());
        LogUtils.infoWithScreenshot("Step 2: Input " + acc.address() + " to \"Email address\" field");
        accountSettingsPage.clickBtnUpdate();
        LogUtils.infoWithScreenshot("Step 3: Click \"Update\" button");
        AssertUtils.softAssertEquals(accountSettingsPage.getTextSuccessMessage(), "A confirmation message has been sent to the new email address. Please follow the instructions in the message to continue.", "Verify Message");
        mail.clickOnVerifyLink(mail.extractVerificationLink());
        LogUtils.infoWithScreenshot("Step 4: Open inbox of mail '" + acc.address() + "' and click verify button");
        DriverFactory.reloadPage();
        LogUtils.infoWithScreenshot("Step 5: Back to WebCatalog browser tab and Reload page");
        AssertUtils.assertEquals(accountSettingsPage.getTextEmail(), acc.address(), "Verify email address is updated");
    }

    @Test
    public void AS04_testUpdateAccountEmailWithoutChangingEmail() {
        accountSettingsPage.clickTabEmail();
        LogUtils.infoWithScreenshot("Step 1: Click \"Email address\" tab");
        accountSettingsPage.clickEmailField();
        LogUtils.infoWithScreenshot("Step 2: Do not modify the existing value in the \"Email address\" field");
        AssertUtils.assertFalse(accountSettingsPage.isBtnUpdateClickable(), "Verify Update Button should not be enabled");
    }

    @Test
    public void AS05_testDeleteAccount() {
        accountSettingsPage.clickTabAccount();
        LogUtils.infoWithScreenshot("Step 1: Click \"Account\" tab");
        accountSettingsPage.clickBtnDeleteAccount();
        LogUtils.infoWithScreenshot("Step 2: Click on \"Delete account\" button");
        accountSettingsPage.clickBtnDeleteAccountConfirm();
        LogUtils.infoWithScreenshot("Step 3: Select \"Delete account\"");
        AssertUtils.assertTrue(signupPage.isImgLogoDisplayed(), "Verify Signup page should be displayed");
        signupPage.clickImgLogo();
    }
}