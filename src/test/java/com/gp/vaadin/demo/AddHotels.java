package com.gp.vaadin.demo;

import com.gp.vaadin.demo.Entity.Hotel;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import java.util.Random;

public class AddHotels extends AbstractUITest {
    @Test
    public void testHotelsAdd() throws InterruptedException {
        webDriver.get(BASE_URL);
        sleap();
        for (int i = 1; i < 4; i++) {
            addHotel((new Hotel("new amazing Hotel " + i, "cool address " + i, 5,
                    "awesome description " + i, "www." + i + ".com")));
        }
    }

    private void addHotel(Hotel hotel) throws InterruptedException {
        webDriver.findElement(By.xpath("//DIV[@class='v-button v-widget'][.//SPAN[contains(text(),'Add')]]")).click();
        sleap();
        webDriver.findElement(By.xpath("//INPUT[../preceding-sibling::td/DIV/SPAN[contains(text(),'Name')]]")).sendKeys(hotel.getName());
        Thread.sleep(400);
        webDriver.findElement(By.xpath("//INPUT[../preceding-sibling::td/DIV/SPAN[contains(text(),'Address')]]")).sendKeys(hotel.getAddress());
        Thread.sleep(400);
        webDriver.findElement(By.xpath("//INPUT[../preceding-sibling::td/DIV/SPAN[contains(text(),'Rating')]]")).sendKeys(hotel.getRating().toString());
        Thread.sleep(400);
        webDriver.findElement(By.className("v-datefield-button")).click();
        Thread.sleep(400);
        webDriver.findElement(By.className("v-button-prevmonth")).click();
        Thread.sleep(400);
        webDriver.findElement(By.xpath("//SPAN[contains(text(), '15')]")).click();
        Thread.sleep(400);
        new Select(webDriver.findElement(By.className("v-select-select"))).selectByIndex(new Random().nextInt(3) + 1);
        Thread.sleep(400);
        webDriver.findElement(By.xpath("//SPAN[./*[contains(text(),'Credit')]]")).click();
        Thread.sleep(400);
        webDriver.findElement(By.xpath("//INPUT[@class='v-textfield v-widget v-has-width']")).sendKeys(String.valueOf(new Random().nextInt(100)));
        Thread.sleep(400);
        webDriver.findElement(By.xpath("//TEXTAREA")).sendKeys(hotel.getDescription());
        Thread.sleep(400);
        webDriver.findElement(By.xpath("//INPUT[../preceding-sibling::td/DIV/SPAN[contains(text(),'URL')]]")).sendKeys(hotel.getUrl());
        Thread.sleep(400);
        webDriver.findElement(By.xpath("//DIV[./SPAN//SPAN[contains(text(),'Save')]]")).click();
        sleap();
    }
}
