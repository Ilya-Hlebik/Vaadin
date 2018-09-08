package com.gp.vaadin.demo;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.concurrent.TimeUnit;

public class AbstractUITest {
    protected static final String BASE_URL = "http://localhost:8080";
    protected WebDriver webDriver;
    public AbstractUITest() {
        System.setProperty("webdriver.chrome.driver", "/opt/soft/study/chromedriver");
    }
    @Before
    public void initDriver() {
        webDriver = new ChromeDriver();
    }

    @After
    public void tearDown() throws InterruptedException {
        sleap();
        webDriver.quit();
    }
    public void sleap() throws InterruptedException {
        Thread.sleep(1000);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
}