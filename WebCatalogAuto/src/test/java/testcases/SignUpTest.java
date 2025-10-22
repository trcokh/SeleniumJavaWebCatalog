package testcases;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.MailTmPage;
import pages.SignUpPage;
import utils.AssertUtils;
import utils.LogUtils;
import utils.annotations.DriverPerTest;

@DriverPerTest
public class SignUpTest extends BaseTest {
    private HomePage home;
    private SignUpPage signUp;

    private MailTmPage.MailAccount emailAccount;

    @BeforeMethod
    public void setUpPages() {
        home = new HomePage();
        signUp = new SignUpPage();

        navigateToEmailEntry();
    }

    private void navigateToEmailEntry() {
        home.clickBtnLSignIn();
        LogUtils.infoWithScreenshot("Pre-condition: Click 'Sign In' button on the home page");

        signUp.clickBtnContinueWithEmail();
        LogUtils.infoWithScreenshot("Pre-condition: Click 'Continue with Email' button on the sign up page");
    }

    @Test
    public void SU01_testSignUpWithValidEmail() {
        emailAccount = MailTmPage.generateRandomAccount();
        MailTmPage mail = new MailTmPage(emailAccount.address(), emailAccount.password());
        LogUtils.infoWithScreenshot("Step 1: Generate random YopMail email: " + emailAccount.address());

        signUp.enterEmail(emailAccount.address());
        LogUtils.infoWithScreenshot("Step 2: Input data to 'Email' field: " + emailAccount.address());

        signUp.clickBtnContinue();
        LogUtils.infoWithScreenshot("Step 3: Click 'Continue' button");

        String otp = mail.extractOtpFromMail();
        LogUtils.info("Step 4: Get OTP code from Mail TM: " + otp);

        signUp.enterOTP(otp);
        LogUtils.infoWithScreenshot("Step 5: Input OTP code to 'OTP' field: " + otp);

        AssertUtils.assertTrue(home.isAvatarMenuDisplayed(),
                "User should land on dashboard after login");
    }

    @Test
    public void SU02_testSignUpWithRegisteredEmail() {
        MailTmPage mail = new MailTmPage(emailAccount.address(), emailAccount.password());
        mail.clearInbox();

        signUp.enterEmail(emailAccount.address());
        LogUtils.infoWithScreenshot("Step 1: Input data to 'Email' field: " + emailAccount.address());

        signUp.clickBtnContinue();
        LogUtils.infoWithScreenshot("Step 2: Click 'Continue' button");

        String otp = mail.extractOtpFromMail();
        LogUtils.info("Step 3: Get OTP code from Mail TM: " + otp);

        signUp.enterOTP(otp);
        LogUtils.infoWithScreenshot("Step 4: Input OTP code to 'OTP' field: " + otp);

        AssertUtils.assertTrue(home.isAvatarMenuDisplayed(),
                "User should land on dashboard after login");
    }

    @AfterMethod
    public void signOut() {
        if (home.isAvatarMenuDisplayed()) {
            home.clickBtnAvatarMenu();
            home.clickBtnLogout();
            LogUtils.infoWithScreenshot("Post-condition: Log out if logged in");
        }
    }
}
