package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utils.annotations.RetryCountIfFailed;
import utils.readers.ConfigReader;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private int maxRetryCount = ConfigReader.getProperty("defaultRetryCount") != null
            ? Integer.parseInt(ConfigReader.getProperty("defaultRetryCount"))
            : 3;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount == 0) {
            RetryCountIfFailed annotation = result.getMethod()
                    .getConstructorOrMethod()
                    .getMethod()
                    .getAnnotation(RetryCountIfFailed.class);
            if (annotation != null) {
                maxRetryCount = annotation.value();
            }
        }

        if (retryCount < maxRetryCount) {
            retryCount++;
            LogUtils.warn("🔁 Retrying test (" + retryCount + "/" + maxRetryCount + "): " + result.getName());
            return true;
        }
        return false;
    }
}