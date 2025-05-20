package com.example.messageSender.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    public  void sendMessage() throws InterruptedException, IOException{
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver"); // Update if needed

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        driver.get("https://web.whatsapp.com");

        System.out.println("📷 Please scan the QR code.");
        Thread.sleep(30000); // Time for user to scan QR code

        for (int i = 0; i < 15; i++) {
            List<String> numbers = Files.lines(Paths.get("src/main/resources/numbers.txt"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());

            String message = "Hello,this message will be send in the numbers file";

            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

            for (String phone : numbers) {
                try {
                    String url = "https://web.whatsapp.com/send?phone=" + phone + "&text=" + encodedMessage;
                    driver.get(url);

                    // Wait until the new send icon is clickable
                    WebElement sendButton = new WebDriverWait(driver, Duration.ofSeconds(20))
                            .until(ExpectedConditions.elementToBeClickable(
                                    By.xpath("//span[@data-icon='wds-ic-send-filled']")));

                    sendButton.click();
                    System.out.println("✅ Message sent to: " + phone);
                    Thread.sleep(5000); // Wait before sending next
                } catch (Exception e) {
                    System.out.println("❌ Problem sending message to: " + phone);
                    e.printStackTrace();
                }
            }

            System.out.println("✅ Round " + (i + 1) + " of messages sent.");
            Thread.sleep(600000); // Wait 10 minutes before next round
        }
        System.out.println("✅✅ All rounds of messages sent.");
        driver.quit();
    }
}
