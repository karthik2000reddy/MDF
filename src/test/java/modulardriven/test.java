package modulardriven;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import io.qameta.allure.Step;

@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class test  {

    WebDriver driver;

   @BeforeClass
    public void instantiateDriver() {
        // Initialize WebDriver once
        driver = new ChromeDriver();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    @Step("testLogin {1}")
    @Test(enabled = false, priority = 1, description = "scenario1: verify the service type available in HTML table")
    public void validateServiceType() throws InterruptedException {
        invokeBrowser();
        login();
        serviceTableNavigate();
        boolean result = verifyServiceType("General Medicine");
        Assert.assertTrue(result);
    }

    @Step("testLogin {2}")
    @Test(priority = 2, description = "scenario2: Delete the service type and verify it is not available in the HTML table")
    public void deleteServiceType() throws InterruptedException {
        invokeBrowser();
        login();
        serviceTableNavigate();
        SoftAssert sa = new SoftAssert();
        boolean result = deleteServiceType("Gynecology");
        navigateToHomePage();
        serviceTableNavigate();
        result = verifyServiceType("Gynecology");
        sa.assertFalse(result);
        sa.assertAll();
    }

    public void serviceTableNavigate() throws InterruptedException {
        // Navigate to Appointment
        driver.findElement(By.id("appointmentschedulingui-homeAppLink-appointmentschedulingui-homeAppLink-extension")).click();
        // Navigate to Manage Service
        driver.findElement(By.id("appointmentschedulingui-manageAppointmentTypes-app")).click();
        Thread.sleep(2000);
    }

    public void navigateToHomePage() {
        driver.findElement(By.xpath("//i[@class='icon-home small']")).click();
    }

    public void invokeBrowser() {
        driver.get("https://demo.openmrs.org/openmrs/login.htm");
    }

    public void login() throws InterruptedException {
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("admin");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("Admin123");
        driver.findElement(By.xpath("//ul//li[@id='Inpatient Ward']")).click();
        driver.findElement(By.xpath("//input[@id='loginButton']")).click();
        Thread.sleep(2000);
    }

    public boolean verifyServiceType(String serviceType) {
        boolean result = false;
        List<WebElement> pageList = driver.findElements(By.xpath("//div[@id='appointmentTypesTable_paginate']/span"));
        for (int i = 0; i < pageList.size(); i++) {
            pageList.get(i).click();
            List<WebElement> serviceTypeList = driver.findElements(By.xpath("//table[@id='appointmentTypesTable']/tbody/tr/td[1]"));
            for (WebElement service : serviceTypeList) {
                if (service.getText().equals(serviceType)) {
                    System.out.println("Service type found: " + service.getText());
                    result = true;
                    break;
                }
            }
            if (result) break;  // Stop searching if the service type is found
        }
        return result;
    }

    public boolean deleteServiceType(String serviceType) throws InterruptedException {
        boolean result = false;
        List<WebElement> pageList = driver.findElements(By.xpath("//div[@id='appointmentTypesTable_paginate']/span"));
        for (int i = 0; i < pageList.size(); i++) {
            pageList.get(i).click();
            List<WebElement> serviceTypeList = driver.findElements(By.xpath("//table[@id='appointmentTypesTable']/tbody/tr/td[1]"));
            for (int j = 0; j < serviceTypeList.size(); j++) {
                if (serviceTypeList.get(j).getText().equals(serviceType)) {
                    System.out.println("Service type found for deletion: " + serviceTypeList.get(j).getText());
                    driver.findElement(By.xpath("//table[@id='appointmentTypesTable']/tbody/tr/td[text()='" + serviceType + "']/following-sibling::td/span/i[@title='Delete']")).click();
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//button[@class='confirm right'])[22]"))).click();
                    result = true;
                    System.out.println("Element deleted");
                    Thread.sleep(3000);
                    break;
                }
            }
            if (result) break;  // Stop searching if the service type is deleted
        }
        return result;
    }

    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
