package com.blueDragon.Convenience.Service;

import com.blueDragon.Convenience.Model.ConvenienceType;
import com.blueDragon.Convenience.Model.Product;
import com.blueDragon.Convenience.Repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SevenProductService extends ProductServiceBase {

    private static final String SEVEN_URL = "https://www.7-eleven.co.kr/product/presentList.asp";

    public SevenProductService(ProductRepository productRepository) {
        super(productRepository);
    }

    @Override
    public List<Product> crawlAndSaveProducts() {
        List<Product> sevenProducts = new ArrayList<>();
        try {
            driver.get(SEVEN_URL);

            // Keep clicking "More" button until all products are loaded
            loadAllProducts();

            // Collect all product elements
            List<WebElement> productElements = driver.findElements(By.cssSelector(".pic_product"));
            saveUniqueProductsWithImages(productElements, sevenProducts, ConvenienceType.Seven);

        } catch (Exception e) {
            System.err.println("An error occurred while processing products from 7-Eleven.");
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return sevenProducts;
    }

    private void loadAllProducts() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        while (true) {
            try {
                WebElement moreButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn_more > a")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", moreButton);
                Thread.sleep(3000); // Wait for products to load
            } catch (NoSuchElementException | TimeoutException e) {
                System.out.println("No more 'More' button found. All products loaded.");
                break;
            }
        }
    }

    @Override
    protected String extractName(WebElement productElement) {
        String fullName = productElement.findElement(By.cssSelector(".name")).getText().trim();
        int index = fullName.indexOf(")");
        return (index != -1) ? fullName.substring(index + 1).trim() : fullName;
    }

    @Override
    protected String extractPrice(WebElement productElement) {
        String priceText = productElement.findElement(By.cssSelector(".price")).getText();
        return priceText.isEmpty() ? "0" : priceText;
    }

    @Override
    protected String extractImageUrl(WebElement productElement) {
        try {
            String imgUrl = productElement.findElement(By.cssSelector("img")).getAttribute("src").trim();
            // Only return valid image URLs
            return imgUrl.isEmpty() ? null : imgUrl.startsWith("https:") ? imgUrl : "https://www.7-eleven.co.kr" + imgUrl;
        } catch (NoSuchElementException e) {
            return null; // No image found for this product
        }
    }

    private void saveUniqueProductsWithImages(List<WebElement> productElements, List<Product> productsList, ConvenienceType storeType) {
        List<Product> newProducts = new ArrayList<>();
        for (WebElement productElement : productElements) {
            try {
                String name = extractName(productElement);
                String price = extractPrice(productElement);
                String imageUrl = extractImageUrl(productElement);

                // Skip products without images
                if (imageUrl == null) {
                    continue;
                }

                // Check for duplicates
                if (productRepository.existsByNameAndPrice(name, price)) {
                    System.out.println("Duplicate product found: " + name + ", Price: " + price);
                    continue;
                }

                List<ConvenienceType> availableAt = List.of(storeType);
                Product product = Product.customBuilder(name, price, availableAt, List.of(classifyFoodTypeByKeywords(name)), imageUrl);
                newProducts.add(product);

            } catch (StaleElementReferenceException e) {
                System.err.println("Stale element encountered. Skipping this element.");
            }
        }
        productRepository.saveAll(newProducts);
        productsList.addAll(newProducts);
    }
}
