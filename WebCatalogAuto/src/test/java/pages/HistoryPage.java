package pages;

import base.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import utils.AssertUtils;
import utils.LogUtils;
import utils.MultipleSubmitUtils;
import utils.readers.ConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryPage extends pages.BasePage {
    private final By firstCardGrid = By.xpath("//div[@data-slot='card'][1]");
    private final By allCardGrid = By.xpath("//div[@data-slot='card']");
    private final By appLink = By.xpath("//div[@data-slot='card'][1]//div[@data-slot='card-title']//a");
    private final By appName = By.xpath("//div[@data-slot='card'][1]//div[@data-slot='card-description'][1]");
    private final By submitDate = By.xpath("//div[@data-slot='card'][1]//div[@data-slot='card-description'][2]");
    private final By appStatus = By.xpath("//div[@data-slot='card'][1]//span[@data-slot='badge']");
    private final By blankIcon = By.xpath("//h1//following-sibling::div//*[name()='svg']");

    public boolean isBlankIconDisplayed(){
        return isDisplayed(blankIcon, defaultTimeout);
    }

    public By getCardGridByIndex(int index){
        return By.xpath(String.format("//div[@data-slot='card'][%d]", index + 1));
    }

    public By getAppLinkByIndex(int index){
        return By.xpath(String.format("//div[@data-slot='card'][%d]//div[@data-slot='card-title']//a", index + 1));
    }

    public By getAppNameByIndex(int index){
        return By.xpath(String.format("//div[@data-slot='card'][%d]//div[@data-slot='card-description'][1]", index + 1));
    }

    public By getSubmitDateByIndex(int index){
        return By.xpath(String.format("//div[@data-slot='card'][%d]//div[@data-slot='card-description'][2]", index + 1));
    }

    public By getAppStatusByIndex(int index){
        return By.xpath(String.format("//div[@data-slot='card'][%d]//span[@data-slot='badge']", index + 1));
    }

    public List<WebElement> getAllGrids(){
        return DriverFactory.getDriver().findElements(allCardGrid);
    }

    public WebElement getGridByIndex(int index){
        return getAllGrids().get(index);
    }

    //public String getText(By locator, int timeoutSeconds){ return super.getText(locator, timeoutSeconds); }

    public void clickAppLink() {
        click(appLink, defaultTimeout);
    }

    public boolean isAppLinkDisplayed() {
        return isDisplayed(appLink, defaultTimeout);
    }

    public boolean isAppNameDisplayed() {
        return isDisplayed(appName, defaultTimeout);
    }

    public boolean isSubmitDateDisplayed() {
        return isDisplayed(submitDate, defaultTimeout);
    }

    public boolean isAppStatusDisplayed() {
        return isDisplayed(appStatus, defaultTimeout);
    }

    public boolean isAllCardGridDisplayed(){
        return isDisplayed(allCardGrid, defaultTimeout);
    }

    public String getAppNameText() {
        return getText(appName, defaultTimeout);
    }

    public String getAppLinkText() {
        return getText(appLink, defaultTimeout);
    }

    public String getAppStatusText() {
        return getText(appStatus, defaultTimeout);
    }

    public String getAppSubmitDate() {
        return getText(submitDate, defaultTimeout);
    }

    //Verify each app in grid (newest first)
    public void verifySubmittedApps(MultipleSubmitUtils result){
        for (int i = 0; i < result.getNames().size(); i++){
            AssertUtils.softAssertEquals(getText(getAppNameByIndex(i), defaultTimeout), result.getNames().get(i), "Verify app name"); //"App name value matched at position" + (i + 1) + "Expected: " + result.getNames().get(i)
            AssertUtils.softAssertEquals(getText(getAppLinkByIndex(i), defaultTimeout), result.getUrls().get(i), "Verify app url");
            AssertUtils.softAssertEquals(getText(getSubmitDateByIndex(i), defaultTimeout), LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")), "Verify submit date");
            AssertUtils.softAssertEquals(getText(getAppStatusByIndex(i), defaultTimeout), "Pending", "Verify app status");
        }
    }

    public int countSubmittedAppGridCards(){
        return countElements(allCardGrid, defaultTimeout);
    }
}