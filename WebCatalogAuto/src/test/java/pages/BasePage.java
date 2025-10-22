package pages;

import base.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.readers.ConfigReader;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class BasePage {
    private static final Logger logger = LoggerFactory.getLogger(BasePage.class);

    protected int defaultTimeout = Integer.parseInt(ConfigReader.getProperty("defaultTimeout"));

    protected Wait<WebDriver> fluentWait(int timeoutSeconds) {
        return new FluentWait<>(DriverFactory.getDriver()).withTimeout(Duration.ofSeconds(timeoutSeconds)).pollingEvery(Duration.ofMillis(200)).ignoring(NoSuchElementException.class).ignoring(StaleElementReferenceException.class);
    }

    protected WebElement findFreshElement(By locator, int timeoutSeconds) {
        return fluentWait(timeoutSeconds).until(driver -> {
            try {
                WebElement el = driver.findElement(locator);
                return (el.isDisplayed() || el.isEnabled()) ? el : null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected void scrollToElement(WebElement element) {
        try {
            if (element != null) {
                JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
                js.executeScript(
                        "arguments[0].scrollIntoView({behavior:'auto', block:'nearest', inline:'nearest'});",
                        element
                );
            }
        } catch (Exception e) {
            logger.error("Failed to scroll to element: {}", element, e);
            throw e;
        }
    }

    protected void click(By locator, int timeoutSeconds) {
        try {
            WebElement element = waitForClickable(locator, timeoutSeconds);
            scrollToElement(element);
            element.click();
        } catch (Exception e) {
            logger.error("Failed to click element at locator: {}", locator, e);
            throw e;
        }
    }

    protected void sendKeys(By locator, String text, int timeoutSeconds) {
        try {
            WebElement element = findFreshElement(locator, timeoutSeconds);
            scrollToElement(element);
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            logger.error("Failed to type '{}' into element at locator: {}", text, locator, e);
            throw e;
        }
    }

    protected String getText(By locator, int timeoutSeconds) {
        try {
            WebElement element = findFreshElement(locator, timeoutSeconds);
            scrollToElement(element);
            return element.getText();
        } catch (Exception e) {
            logger.error("Failed to get text from element at locator: {}", locator, e);
            throw e;
        }
    }

    protected String getAttribute(By locator, String attributeName, int timeoutSeconds) {
        try {
            return fluentWait(timeoutSeconds).until(driver -> {
                try {
                    WebElement element = driver.findElement(locator);
                    scrollToElement(element);
                    String value = element.getAttribute(attributeName);
                    return (value != null && !value.trim().isEmpty()) ? value : null;
                } catch (StaleElementReferenceException stale) {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Failed to get attribute '{}' from element at locator: {} within {} seconds",
                    attributeName, locator, timeoutSeconds, e);
            throw e;
        }
    }

    protected void selectByText(By locator, String text, int timeoutSeconds) {
        try {
            WebElement dropdown = findFreshElement(locator, timeoutSeconds);
            scrollToElement(dropdown);
            new Select(dropdown).selectByVisibleText(text);
        } catch (Exception e) {
            logger.error("Failed to select '{}' from dropdown at locator: {}", text, locator, e);
            throw e;
        }
    }

    protected void selectByIndex(By locator, int index, int timeoutSeconds) {
        try {
            WebElement dropdown = findFreshElement(locator, timeoutSeconds);
            scrollToElement(dropdown);
            new Select(dropdown).selectByIndex(index);
        } catch (Exception e) {
            logger.error("Failed to select index '{}' from dropdown at locator: {}", index, locator, e);
            throw e;
        }
    }

    protected boolean isDisplayed(By locator, int timeoutSeconds) {
        try {
            WebElement element = findFreshElement(locator, timeoutSeconds);
            if (element != null) {
                scrollToElement(element);
                return true;
            }
        } catch (Exception e) {
            logger.warn("Element not displayed at locator: {} within {} seconds", locator, timeoutSeconds, e);
        }
        return false;
    }

    protected boolean isEnabled(By locator, int timeoutSeconds) {
        try {
            WebElement element = findFreshElement(locator, timeoutSeconds);
            if (element != null) {
                scrollToElement(element);
                return element.isEnabled();
            }
        } catch (Exception e) {
            logger.warn("Element not enabled at locator: {} within {} seconds", locator, timeoutSeconds, e);
        }
        return false;
    }

    protected boolean isSelected(By locator, int timeoutSeconds) {
        try {
            WebElement element = findFreshElement(locator, timeoutSeconds);
            if (element != null) {
                scrollToElement(element);
                return element.isSelected();
            }
        } catch (Exception e) {
            logger.warn("Element not selected at locator: {} within {} seconds", locator, timeoutSeconds, e);
        }
        return false;
    }

    protected boolean isClickable(By locator, int timeoutSeconds) {
        try {
            WebElement element = fluentWait(timeoutSeconds).until(driver -> {
                try {
                    WebElement el = driver.findElement(locator);
                    if (el.isDisplayed() && el.isEnabled()
                            && ExpectedConditions.elementToBeClickable(locator).apply(driver) != null) {
                        scrollToElement(el);
                        return el;
                    }
                    return null;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });
            return element != null;
        } catch (Exception e) {
            logger.warn("Element not clickable at locator: {} within {} seconds", locator, timeoutSeconds, e);
            return false;
        }
    }

    protected WebElement waitForVisibility(By locator, int timeoutSeconds) {
        try {
            return fluentWait(timeoutSeconds).until(driver -> {
                try {
                    WebElement element = driver.findElement(locator);
                    return element.isDisplayed() ? element : null;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Element not visible at locator: {} within {} seconds", locator, timeoutSeconds, e);
            throw e;
        }
    }

    protected WebElement waitForClickable(By locator, int timeoutSeconds) {
        WebDriver driver = DriverFactory.getDriver();
        Wait<WebDriver> wait = fluentWait(timeoutSeconds);

        try {
            return wait.until(d -> {
                WebElement element = ExpectedConditions.elementToBeClickable(locator).apply(d);
                if (element != null && element.isDisplayed() && element.isEnabled()) {
                    return element;
                }
                return null;
            });
        } catch (Exception e) {
            logger.error("Element not clickable at locator: {} within {} seconds", locator, timeoutSeconds, e);
            throw e;
        }
    }

    protected String waitForNonEmptyText(By locator, int timeoutSeconds) {
        try {
            return fluentWait(timeoutSeconds).until(driver -> {
                try {
                    WebElement element = driver.findElement(locator);
                    scrollToElement(element);
                    String text = element.getText();
                    return !text.trim().isEmpty() ? text : null;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Text not loaded for element at locator: {} within {} seconds", locator, timeoutSeconds, e);
            throw e;
        }
    }

    protected int countElements(By locator, int timeoutSeconds) {
        try {
            List<WebElement> elements = fluentWait(timeoutSeconds).until(driver -> {
                try {
                    List<WebElement> found = driver.findElements(locator);
                    return found.isEmpty() ? null : found;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });
            return elements.size();
        } catch (Exception e) {
            logger.error("Failed to count elements at locator: {} within {} seconds", locator, timeoutSeconds, e);
            throw e;
        }
    }

    protected void clickRandomElement(By locator, int timeoutSeconds) {
        try {
            fluentWait(timeoutSeconds).until(driver -> !driver.findElements(locator).isEmpty());
            List<WebElement> elements = DriverFactory.getDriver().findElements(locator);
            if (elements.isEmpty()) {
                throw new NoSuchElementException("No elements found for locator: " + locator);
            }
            WebElement randomElement = elements.get(new Random().nextInt(elements.size()));
            WebElement clickable = fluentWait(timeoutSeconds).until(driver -> {
                try {
                    return (randomElement.isDisplayed() && randomElement.isEnabled()) ? randomElement : null;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });
            scrollToElement(clickable);
            clickable.click();
        } catch (Exception e) {
            logger.error("Failed to click random element for locator: {} within {} seconds", locator, timeoutSeconds, e);
            throw e;
        }
    }



    protected void sendKeysAfterAttributeLoaded(By locator, String attributeName, String textToType, int timeoutSeconds) {
        try {
            WebElement element = findFreshElement(locator, timeoutSeconds);
            scrollToElement(element);
            fluentWait(timeoutSeconds).until(driver -> {
                try {
                    element.clear();
                    element.sendKeys(textToType);
                    return textToType.equals(element.getAttribute(attributeName));
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Failed to type into element at locator: {} after clearing attribute '{}'", locator, attributeName, e);
            throw e;
        }
    }

    public void switchToFrame(By frameLocator, int timeoutSeconds) {
        try {
            DriverFactory.getDriver().switchTo().defaultContent();
            fluentWait(timeoutSeconds).until(driver -> {
                try {
                    driver.switchTo().frame(driver.findElement(frameLocator));
                    return true;
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("Failed to switch to frame at locator: {} within {} seconds", frameLocator, timeoutSeconds, e);
            throw e;
        }
    }
}