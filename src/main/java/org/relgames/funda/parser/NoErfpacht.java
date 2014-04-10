package org.relgames.funda.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NoErfpacht {
    private static final Logger log = LoggerFactory.getLogger(NoErfpacht.class);

    private static final String FUNDA_URL = "http://m.funda.nl/koop/amsterdam/200000-375000/85+woonopp/";

    public static void main(String[] args) {
        WebDriver driver = new HtmlUnitDriver();
        driver.get(FUNDA_URL);

        List<WebElement> elements = driver.findElements(By.cssSelector("a.prop-item"));
        log.info("Loaded {} elements", elements.size());

        for (WebElement element : elements) {
            System.out.println(element);
        }
    }
}
