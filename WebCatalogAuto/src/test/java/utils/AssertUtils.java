package utils;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class AssertUtils {
    private static final SoftAssert softAssert = new SoftAssert();

    // ---------- HARD ASSERTIONS ----------
    public static void assertEquals(String actual, String expected, String message) {
        logAssertion("Hard", "Equals", actual, expected, message);
        try {
            Assert.assertEquals(actual, expected, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.error("❌ Failed: " + e.getMessage());
            throw e;
        }
    }

    public static void assertEquals(int actual, int expected, String message) {
        logAssertion("Hard", "Equals", String.valueOf(actual), String.valueOf(expected), message);
        try {
            Assert.assertEquals(actual, expected, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.error("❌ Failed: " + e.getMessage());
            throw e;
        }
    }

    public static void assertNotEquals(String actual, String expected, String message) {
        logAssertion("Hard", "NotEquals", actual, expected, message);
        try {
            Assert.assertNotEquals(actual, expected, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.error("❌ Failed: " + e.getMessage());
            throw e;
        }
    }

    public static void assertNotEquals(int actual, int expected, String message) {
        logAssertion("Hard", "NotEquals", String.valueOf(actual), String.valueOf(expected), message);
        try {
            Assert.assertNotEquals(actual, expected, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.error("❌ Failed: " + e.getMessage());
            throw e;
        }
    }

    public static void assertTrue(boolean condition, String message) {
        logAssertion("Hard", "True", String.valueOf(condition), "true", message);
        try {
            Assert.assertTrue(condition, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.error("❌ Failed: " + e.getMessage());
            throw e;
        }
    }

    public static void assertFalse(boolean condition, String message) {
        logAssertion("Hard", "False", String.valueOf(condition), "false", message);
        try {
            Assert.assertFalse(condition, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.error("❌ Failed: " + e.getMessage());
            throw e;
        }
    }

    public static void assertNull(String actual, String message) {
        logAssertion("Hard", "Null", actual, "null", message);
        try {
            Assert.assertNull(actual, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.error("❌ Failed: " + e.getMessage());
            throw e;
        }
    }

    public static void assertNotNull(String actual, String message) {
        logAssertion("Hard", "NotNull", actual, "not null", message);
        try {
            Assert.assertNotNull(actual, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.error("❌ Failed: " + e.getMessage());
            throw e;
        }
    }

    public static void fail(String message) {
        LogUtils.error("❌ Forced Failure: " + message);
        Assert.fail(message);
    }

    // ---------- SOFT ASSERTIONS ----------

    public static void softAssertEquals(String actual, String expected, String message) {
        logAssertion("Soft", "Equals", actual, expected, message);
        try {
            softAssert.assertEquals(actual, expected, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.infoWithScreenshot("⚠\uFE0F Failed: " + e.getMessage());
        }
    }

    public static void softAssertEquals(int actual, int expected, String message) {
        logAssertion("Soft", "Equals", String.valueOf(actual), String.valueOf(expected), message);
        try {
            softAssert.assertEquals(actual, expected, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.infoWithScreenshot("⚠\uFE0F Failed: " + e.getMessage());
        }
    }

    public static void softAssertNotEquals(String actual, String expected, String message) {
        logAssertion("Soft", "NotEquals", actual, expected, message);
        try {
            softAssert.assertNotEquals(actual, expected, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.infoWithScreenshot("⚠\uFE0F Failed: " + e.getMessage());
        }
    }

    public static void softAssertNotEquals(int actual, int expected, String message) {
        logAssertion("Soft", "NotEquals", String.valueOf(actual), String.valueOf(expected), message);
        try {
            softAssert.assertNotEquals(actual, expected, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.infoWithScreenshot("⚠\uFE0F Failed: " + e.getMessage());
        }
    }

    public static void softAssertTrue(boolean condition, String message) {
        logAssertion("Soft", "True", String.valueOf(condition), "true", message);
        try {
            softAssert.assertTrue(condition, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.infoWithScreenshot("⚠\uFE0F Failed: " + e.getMessage());
        }
    }

    public static void softAssertFalse(boolean condition, String message) {
        logAssertion("Soft", "False", String.valueOf(condition), "false", message);
        try {
            softAssert.assertFalse(condition, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.infoWithScreenshot("⚠\uFE0F Failed: " + e.getMessage());
        }
    }

    public static void softAssertNull(String actual, String message) {
        logAssertion("Soft", "Null", actual, "null", message);
        try {
            softAssert.assertNull(actual, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.infoWithScreenshot("⚠\uFE0F Failed: " + e.getMessage());
        }
    }

    public static void softAssertNotNull(String actual, String message) {
        logAssertion("Soft", "NotNull", actual, "not null", message);
        try {
            softAssert.assertNotNull(actual, message);
            LogUtils.infoWithScreenshot("✅ Passed");
        } catch (AssertionError e) {
            LogUtils.infoWithScreenshot("⚠\uFE0F Failed: " + e.getMessage());
        }
    }

    public static void softFail(String message) {
        LogUtils.error("⚠\uFE0F Soft Forced Failure: " + message);
        softAssert.fail(message);
    }

    public static void assertAll() {
        try {
            softAssert.assertAll();
            LogUtils.info("✅ All soft assertions passed");
        } catch (AssertionError e) {
            LogUtils.error("⚠\uFE0F Soft assertions failed: " + e.getMessage());
            throw e;
        }
    }

    // ---------- INTERNAL LOGGING ----------
    private static void logAssertion(String type, String method, String actual, String expected, String message) {
        LogUtils.info(type + " Assert " + method + ": " + message);
        LogUtils.info("Expected: " + expected);
        LogUtils.info("Actual: " + actual);
    }
}