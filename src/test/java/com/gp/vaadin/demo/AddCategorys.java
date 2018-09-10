package com.gp.vaadin.demo;

import org.junit.Test;
import org.openqa.selenium.By;

public class AddCategorys extends AbstractUITest {

    @Test
    public void testCategoryAdd() throws InterruptedException {
        webDriver.get(BASE_URL);
        sleap();
        webDriver.findElement(By.xpath("//SPAN[contains(text(),'Category')]")).click();
        sleap();

        for (int i = 1; i < 4; i++) {
            addCategory("New category " + i);
        }

    }

    private void addCategory(String name) throws InterruptedException {
        webDriver.findElement(By.xpath("//DIV[@class='v-button v-widget']")).click();
        sleap();
        webDriver.findElement(By.xpath("//INPUT[@class='v-textfield v-widget v-required v-textfield-required']")).sendKeys(name);
        Thread.sleep(500);
        webDriver.findElement(By.xpath("//DIV[@class='v-button v-widget'][.//SPAN[contains(text(),'Save')]]")).click();
        sleap();
    }
}
