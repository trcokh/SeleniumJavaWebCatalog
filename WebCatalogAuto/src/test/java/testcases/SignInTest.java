package testcases;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.*;
import utils.AssertUtils;
import utils.LogUtils;
import utils.annotations.DriverPerTest;
import utils.annotations.TestDataFile;
import utils.TestDataProvider;

@DriverPerTest
public class SignInTest extends BaseTest {
    private HomePage home;
    private SignUpPage signUp;
    private SignInPage signIn;

    @BeforeClass
    public void setUpPages() {
        home = new HomePage();
        signUp = new SignUpPage();
        signIn = new SignInPage();

    }

    @BeforeMethod
    private void navigateToEmailEntry() {
        home.clickBtnLSignIn();
        LogUtils.infoWithScreenshot("Pre-condition: Click 'Sign In' button on the home page");

        signUp.clickBtnContinueWithEmail();
        LogUtils.infoWithScreenshot("Pre-condition: Click 'Continue with Email' button on the sign up page");
    }

    @Test
    public void SI01_testSignInWithValidEmail() {
        MailTmPage.MailAccount acc = MailTmPage.generateRandomAccount();
        MailTmPage mail = new MailTmPage(acc.address(), acc.password());

        signIn.enterEmail(acc.address());
        LogUtils.infoWithScreenshot("Step 1: Input data to 'Email' field: " + acc.address());

        signIn.clickContinueButton();
        LogUtils.infoWithScreenshot("Step 2: Click 'Continue' button");

        String otp = mail.extractOtpFromMail();
        LogUtils.info("Step 3: Get OTP code from Mail TM: " + otp);

        signIn.enterOtp(otp);
        LogUtils.infoWithScreenshot("Step 4: Input OTP code to 'OTP' field: " + otp);

        AssertUtils.assertTrue(home.isAvatarMenuDisplayed(),
                "User should land on dashboard after login");

        home.clickBtnAvatarMenu();
        home.clickBtnLogout();
    }

    @Test
    public void SI02_submitEmptyEmail() {
        signIn.enterEmail("");
        LogUtils.infoWithScreenshot("Step 1: Input data to 'Email' field: empty");

        signIn.clickContinueButton();
        LogUtils.infoWithScreenshot("Step 2: Click 'Continue' button");

        boolean isOTPInputDisplayed = signIn.isOTPInputDisplayed();
        LogUtils.infoWithScreenshot("Step 3: Check if OTP input is displayed: " + isOTPInputDisplayed);

        AssertUtils.assertFalse(isOTPInputDisplayed,
                "Email cannot empty");

        signIn.clickImgLogo();
    }

    @Test(dataProvider = "testData", dataProviderClass = TestDataProvider.class)
    @TestDataFile(file = "${excelDataFile}", sheet = "InvalidEmail")
    public void SI03_testSignInWithInvalidFormatEmail(String email) {
        signIn.enterEmail(email);
        LogUtils.infoWithScreenshot("Step 1: Input data to 'Email' field: " + email);

        signIn.clickContinueButton();
        LogUtils.infoWithScreenshot("Step 2: Click 'Continue' button");

        boolean isOTPInputDisplayed = signIn.isOTPInputDisplayed();
        LogUtils.infoWithScreenshot("Step 3: Check if OTP input is displayed: " + isOTPInputDisplayed);

        AssertUtils.assertFalse(isOTPInputDisplayed,
                "Incorrect email format");

        signIn.clickImgLogo();
    }
}