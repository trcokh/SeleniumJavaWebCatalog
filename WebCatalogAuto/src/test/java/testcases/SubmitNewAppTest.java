package testcases;

import org.testng.annotations.*;
import pages.*;
import utils.*;
import utils.annotations.DriverPerTest;
import utils.annotations.TestDataFile;
import utils.readers.ConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DriverPerTest
public class SubmitNewAppTest extends BaseTest {
    private HomePage home;
    private SignUpPage signUp;
    private SubmitNewPage submitNewAppPage;
    private HistoryPage historyPage;

    @BeforeClass
    public void setUpPages() {
        home = new HomePage();
        signUp = new SignUpPage();
        submitNewAppPage = new SubmitNewPage();
        historyPage = new HistoryPage();

        home.clickBtnLSignIn();
        LogUtils.infoWithScreenshot("Pre-condition: Go to sign up flow");
    }


    @BeforeMethod
    private void setUpPrecondition() {
        signUp.clickBtnContinueWithEmail();
        MailTmPage.MailAccount acc = MailTmPage.generateRandomAccount();
        MailTmPage mail = new MailTmPage(acc.address(), acc.password());
        signUp.enterEmail(acc.address());
        signUp.clickBtnContinue();
        signUp.enterOTP(mail.extractOtpFromMail());

    }

    //Test case 1: Submit 1 app with valid data
    @Test(dataProvider = "testData", dataProviderClass = TestDataProvider.class)
    @TestDataFile(file = "${excelDataFile}", sheet = "SubmitNewApp")
    public void SA01_testSubmitNewApp(String appName, String appUrl) {
        //Submit new app flow
        home.clickBtnSubmitNewApp();
        LogUtils.infoWithScreenshot("Step 1: Go to submit new app page");
        submitNewAppPage.enterAppName(appName);
        LogUtils.infoWithScreenshot("Step 2: submitting name: " + appName);
        submitNewAppPage.enterAppUrl(appUrl);
        LogUtils.infoWithScreenshot("Step 3: submitting url: " + appUrl);

        //Write to excel for other purpose not now
        ExcelUtils.writeToCell(ConfigReader.getProperty("excelDataFile"), "SubmitNewApp", 1, 2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));

        submitNewAppPage.clickBtnSubmit();
        LogUtils.infoWithScreenshot("Step 4: Click Submit button");
        submitNewAppPage.clickLinkHistory();
        LogUtils.infoWithScreenshot("Step 5: Go to History page");

        AssertUtils.assertEquals(historyPage.countSubmittedAppGridCards(), 1, "Display apps must equal amount of apps in Excel");

        //String expectAppUrl = StringUtils.normalizeUrl(appUrl);
        AssertUtils.softAssertEquals(historyPage.getAppLinkText(), StringUtils.normalizeUrl(appUrl), "App url must be the same as sheet");
        AssertUtils.softAssertEquals(historyPage.getAppNameText(), appName, "App name must be the same as sheet");
        AssertUtils.softAssertEquals(historyPage.getAppStatusText(), "Pending", "App status must be the same as sheet");
        AssertUtils.softAssertEquals(historyPage.getAppSubmitDate(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")), "App date should be the same as sheet");
    }

    //Test case 2: Submit app with invalid url
    @Test(enabled = true)
    public void SA04_testSubmitAppWithInValidUrl() {
        home.clickBtnSubmitNewApp();
        LogUtils.infoWithScreenshot("Step 1: Go to submit new app page");
        submitNewAppPage.enterAppName("Zalo");
        submitNewAppPage.enterAppUrl("https://zalo@#");
        LogUtils.infoWithScreenshot("Step 2: Submitting invalid url");
        submitNewAppPage.clickBtnSubmit();
        LogUtils.infoWithScreenshot("Step 3: Click Submit button");

        AssertUtils.assertFalse(submitNewAppPage.isInformSuccessfullyTitleMessageDisplay(), "Form can't submit when fields are empty or invalid.");
    }

    //Test case 3: Submit app without data
    @Test
    public void SA05_testSubmitAppWithoutData() {
        home.clickBtnSubmitNewApp();
        LogUtils.infoWithScreenshot("Step 1: Go to Submit new app page");
        submitNewAppPage.enterAppName("");
        LogUtils.infoWithScreenshot("Step 2: Submitting new app without data");
        submitNewAppPage.clickBtnSubmit();
        LogUtils.infoWithScreenshot("Step 3: Click button Submit");
        submitNewAppPage.enterAppName("Test");
        LogUtils.infoWithScreenshot("Step 4: Enter text into app name");
        submitNewAppPage.enterAppUrl("");
        LogUtils.infoWithScreenshot("Step 5: Leave app url empty");
        submitNewAppPage.clickBtnSubmit();
        LogUtils.infoWithScreenshot("Step 6: Click button Submit");

        AssertUtils.assertFalse(submitNewAppPage.isInformSuccessfullyTitleMessageDisplay(), "Form should not submit successfully when fields are empty or invalid.");
    }

    //Test case 4:Submit multiple app
    @Test
    public void SA02_testSubmitMultipleApp() {
        //Read app data from Excel then send key to field for submit
        //Return final result to here
        home.clickBtnSubmitNewApp();
        LogUtils.infoWithScreenshot("Step 1: Go to submit new app page");
        MultipleSubmitUtils result = submitNewAppPage.submitMultipleAppsFromExcel(ConfigReader.getProperty("excelDataFile"), "SubmitMultipleApp");
        LogUtils.infoWithScreenshot("Step 2: Submit then return result");

        submitNewAppPage.clickLinkHistory();
        LogUtils.infoWithScreenshot("Step 3: Go to History page");
        AssertUtils.assertEquals(historyPage.countSubmittedAppGridCards(), result.getNames().size(), "Display apps must equal amount of apps in Excel");
        LogUtils.infoWithScreenshot("Verifying submitted apps in History page");
        historyPage.verifySubmittedApps(result);
    }

    //Test case 5:Check empty history
    @Test
    public void SA03_testCheckEmptyHistory() {
        home.clickBtnSubmitNewApp();
        LogUtils.infoWithScreenshot("Step 1: Go to Submit new app page");
        submitNewAppPage.clickLinkHistory();
        LogUtils.infoWithScreenshot("Step 2: Go to History page");

        AssertUtils.assertFalse(historyPage.isAllCardGridDisplayed(), "There is no app submitted");

        AssertUtils.softAssertTrue(historyPage.isBlankIconDisplayed(), "Items can't be displayed when no app submitted");
    }

    @AfterMethod
    public void signOut() {
        if (home.isAvatarMenuDisplayed()) {
            home.clickBtnAvatarMenu();
            home.clickBtnLogout();
            LogUtils.infoWithScreenshot("Post-condition: Log out if logged in");
        }
    }

    @AfterClass
    public void goToHomePage() {
        if (signUp.isImgLogoDisplayed()) {
            signUp.clickImgLogo();
            LogUtils.infoWithScreenshot("Post-condition: Go to home if Signup page is displayed");
        }
    }
}