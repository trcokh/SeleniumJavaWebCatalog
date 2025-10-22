package testcases;

import listeners.TestListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.*;
import utils.*;
import utils.annotations.DriverPerTest;
import utils.annotations.TestDataFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DriverPerTest
public class E2ETest extends BaseTest {
    private HomePage homePage;
    private SignUpPage signUpPage;
    private SubmitNewPage submitNewPage;
    private HistoryPage historyPage;
    private AccountSettingsPage accountSettingsPage;

    @BeforeMethod
    public void setUpPages() {
        homePage = new HomePage();
        signUpPage = new SignUpPage();
        submitNewPage = new SubmitNewPage();
        historyPage = new HistoryPage();
        accountSettingsPage = new AccountSettingsPage();
    }

    @Test(dataProvider = "testData", dataProviderClass = TestDataProvider.class)
    @TestDataFile(file = "${excelDataFile}", sheet = "SubmitNewApp")
    public void E2E_Test(String appName, String appUrl) {
        LogUtils.infoWithScreenshot("Phase 1 - 🔐 User Registration Flow: Start sign-up and verify OTP");
        homePage.clickBtnLSignIn();
        signUpPage.clickBtnContinueWithEmail();
        MailTmPage.MailAccount acc = MailTmPage.generateRandomAccount();
        MailTmPage mail = new MailTmPage(acc.address(), acc.password());
        signUpPage.enterEmail(acc.address());
        signUpPage.clickBtnContinue();
        signUpPage.enterOTP(mail.extractOtpFromMail());
        AssertUtils.assertTrue(homePage.isAvatarMenuDisplayed(), "User should land on dashboard");

        LogUtils.infoWithScreenshot("Phase 2 - 🚀 App Submission Workflow: Submit new app and validate history");
        homePage.clickBtnSubmitNewApp();
        submitNewPage.enterAppName(appName);
        submitNewPage.enterAppUrl(appUrl);
        submitNewPage.clickBtnSubmit();
        submitNewPage.clickLinkHistory();
        AssertUtils.softAssertEquals(historyPage.getAppLinkText(), StringUtils.normalizeUrl(appUrl), "App url should be the same as sheet");
        AssertUtils.softAssertEquals(historyPage.getAppNameText(), appName, "App name should be the same as sheet");
        AssertUtils.softAssertEquals(historyPage.getAppStatusText(), "Pending", "App status should display the text \"Pending\"");
        AssertUtils.softAssertEquals(historyPage.getAppSubmitDate(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")), "App submit date should be the current date");

        LogUtils.infoWithScreenshot("Phase 3 - 🗑️ Account Termination Flow: Delete account and verify redirection");
        homePage.clickBtnAvatarMenu();
        homePage.clickBtnAccountSettings();
        accountSettingsPage.clickTabAccount();
        accountSettingsPage.clickBtnDeleteAccount();
        accountSettingsPage.clickBtnDeleteAccountConfirm();
        AssertUtils.assertTrue(signUpPage.isImgLogoDisplayed(), "Verify Signup page should be displayed");
    }
}