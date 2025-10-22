package pages;

import org.openqa.selenium.By;
import utils.*;
import utils.readers.ConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SubmitNewPage extends BasePage {
    private final By HistoryNavigateLink = By.xpath("//a[contains(@href , 'history')]");
    private final By InputAppName = By.id("appName");
    private final By InputAppUrl = By.id("url");
    private final By SubmitBtn = By.xpath("//button[@type ='submit']");
    private final By InformSuccessfullyTitleMessage = By.xpath("//h3");
    private final By InformSuccessfullySubMessage = By.xpath("//h3/following-sibling::p");
    private final By SubmitAnotherAppBtn =  By.xpath("//button[@data-slot='button']");

    public boolean isInformSuccessfullyTitleMessageDisplay(){
        return isDisplayed(InformSuccessfullyTitleMessage, defaultTimeout);
    }

    public String getAppNameText(){
        return getText(InputAppName, defaultTimeout);
    }

    public String getAppUrlText(){
        return getText(InputAppUrl, defaultTimeout);
    }

    public void enterAppName(String appName){
        sendKeys(InputAppName, appName, defaultTimeout);
    }

    public void enterAppUrl(String appUrl){
        sendKeys(InputAppUrl, appUrl, defaultTimeout);
    }

    public void clickBtnSubmit(){
        click(SubmitBtn, defaultTimeout);
    }

    public boolean isSubmitBtnDisplayed(){
        return isDisplayed(SubmitBtn, defaultTimeout);
    }

    public void clickLinkHistory(){
        click(HistoryNavigateLink, defaultTimeout);
    }

    public boolean isHistoryLinkClickable(){ return isClickable(HistoryNavigateLink, defaultTimeout);}

    public boolean isHistoryLinkDisplayed(){
        return isDisplayed(HistoryNavigateLink, defaultTimeout);
    }

    public void clickSubmitAnotherApp(){
        click(SubmitAnotherAppBtn, defaultTimeout);
    }

    public boolean isSubmitAnotherAppBtnDisplayed(){
        return isDisplayed(SubmitAnotherAppBtn, defaultTimeout);
    }

    public MultipleSubmitUtils submitMultipleAppsFromExcel(String filePath, String sheetName) {
        LogUtils.info("Reading excel file: " + filePath + " | sheet: " + sheetName);
        Object[][] data = ExcelUtils.readExcel(filePath, sheetName);

        List<String> names = new ArrayList<>();
        List<String> urls = new ArrayList<>();

        for (Object[] row : data) {
            String name = (String) row[0];
            String url = (String) row[1];

            enterAppName(name);
            enterAppUrl(url);
            LogUtils.infoWithScreenshot("Submitting app: " + name + " | " + url);
            clickBtnSubmit();
            clickSubmitAnotherApp();

            names.add(name);
            urls.add(StringUtils.normalizeUrl(url));
        }
        // Reverse lists to have the most recent submissions first
        names = ExcelUtils.reverse(names);
        urls = ExcelUtils.reverse(urls);

        return new MultipleSubmitUtils(names, urls);
    }
}