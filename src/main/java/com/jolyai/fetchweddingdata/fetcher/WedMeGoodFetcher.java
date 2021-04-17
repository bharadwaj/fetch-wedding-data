package com.jolyai.fetchweddingdata.fetcher;
import com.jolyai.fetchweddingdata.model.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WedMeGoodFetcher {

    @Autowired
    private PhotographerRepository photographerRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    public int fetchTotalPagesCount(WebDriver driver, String baseUrl, String paginationClass){

        // launch Fire fox and direct it to the Base URL
        driver.get(baseUrl);

        WebElement totalPages = driver.findElement(By.cssSelector(paginationClass));
        List<WebElement> liItems = totalPages.findElements(By.tagName("li"));
        WebElement lastPageItem = liItems.get(liItems.size() - 2);
        WebElement anchorTag = lastPageItem.findElement(By.tagName("a"));
        System.out.println("Total Number of Pages: " + anchorTag.getText());

        return Integer.parseInt(anchorTag.getText());
    }

    public void fetchPhotographerCardsOfAPage(WebDriver driver, int pageNumber, String city) {


        String baseUrl = "https://www.wedmegood.com/vendors/"+city+"/wedding-photographers/?page=" + pageNumber;


        // launch Chrome and direct it to the Base URL
        driver.get(baseUrl);

        List<WebElement> vendorCards = driver.findElements(By.cssSelector(".vendor-card"));

        int count = (pageNumber * vendorCards.size()) - vendorCards.size() + 1;
        for (WebElement vendorCard : vendorCards) {
            Photographer toSave = new Photographer();
            toSave.setSource("wedmegood");
            toSave.setCity(city);

            System.out.println("Getting content from Card: " + count++);

            // This is for Photographer Name and Link
            WebElement photographerName = vendorCard.findElement(By.cssSelector(".vendor-detail.text-bold.h6"));
            System.out.println("Photographer Name: " + photographerName.getText());
            System.out.println("url: " + photographerName.getAttribute("href"));
            toSave.setName(photographerName.getText());
            // Wedmegood's pagination algorithm is dynamic and keeps shuffling the order of photographers per page.
            // Save only new non saved photographers into the database.
            // Check if the photographer is already existing in database.
            if(photographerRepository.getPhotographerByName(photographerName.getText()) != null)
                continue;
            // Sometimes real_weddings links show up to avoid those save only profile urls.
            // ex: https://www.wedmegood.com/real_wedding/detail/soumya-gagan-bangalore
            if(!photographerName.getAttribute("href").contains("profile"))
                continue;
            toSave.setUrl(photographerName.getAttribute("href"));

            // Is the photographer Verified.
            WebElement vendorInfo = vendorCard.findElement(By.cssSelector(".vendor-info"));
            if(!vendorInfo.findElements(By.tagName("img")).isEmpty()){
                toSave.setVerified(true);
                System.out.println("Verified Photographer");
            }

            // Rating
            WebElement starRating = vendorInfo.findElement(By.cssSelector(".StarRating"));
            System.out.println("Star Rating: " + starRating.getText());
            toSave.setRating(starRating.getText());

            // Reviews
            WebElement reviews = vendorInfo.findElement(By.cssSelector(".review-cnt"));
            System.out.println("Reviews: " + reviews.getText().substring(0, reviews.getText().length() - " reviews".length()));
            toSave.setReviews(reviews.getText().substring(0, reviews.getText().length() - " reviews".length()));

            // Get Photographer Location
            WebElement location = vendorCard.findElement(By.tagName("p"));
            WebElement locationText = location.findElement(By.tagName("span"));
            System.out.println("Location: " + locationText.getText());
            toSave.setLocation(locationText.getText());

            // Get Photographer Price
            WebElement vendorPrice = vendorCard.findElement(By.cssSelector(".vendor-price"));
            List<WebElement> vendorPriceText = vendorPrice.findElements(By.tagName("p"));
            if(vendorPriceText.size()>=2) {
                String rate = vendorPriceText.get(1).findElement(By.tagName("span")).getText().replaceAll(",", "");
                Long rateLong = Long.valueOf(rate.length() > 2 ? rate : "0");
                System.out.println("Rate: " + rateLong);
                toSave.setRate(rateLong);
            }

            photographerRepository.save(toSave);
        }

    }

    public void fetchAlbumsOfPhotographer(WebDriver driver, Photographer photographer){

        String baseUrl = photographer.getUrl() + "/albums";

        // launch Chrome and direct it to the Base URL
        driver.get(baseUrl);

        WebElement photoContent = driver.findElement(By.cssSelector(".AlbumGrid"));

        List<WebElement> allLinks = photoContent.findElements(By.tagName("a"));
        List<WebElement> allAlbumItems = photoContent.findElements(By.cssSelector(".AlbumItem"));

        int count = 0;
        for(WebElement eachLink: allLinks){
            Album toSave = new Album();
            toSave.setPhotographer(photographer);
            // Link of the Album
            System.out.println(eachLink.getAttribute("href"));
            toSave.setUrl(eachLink.getAttribute("href"));

            // For Album Name, Location if present and Description if present.
            WebElement eachAlbumDetail = allAlbumItems.get(count).findElement(By.cssSelector(".album-detail"));
            String albumName = eachAlbumDetail.findElement(By.tagName("h6")).getText();
            System.out.println(albumName);
            toSave.setAlbumName(albumName);

            // Optional Location of Album and Description
            List<WebElement> locationAndDesc = eachAlbumDetail.findElements(By.tagName("div"));

            if(locationAndDesc.size() >= 1){
                // Only Location is Added
                String locationText = locationAndDesc.get(0).getText();
                System.out.println(locationText);
                if(locationText.length()<30){
                    toSave.setLocation(locationText);
                } else {
                    toSave.setDescription(locationText);
                }
                if(locationAndDesc.size() >= 2){
                    // Description.
                    System.out.println(locationAndDesc.get(1).getText());
                    toSave.setDescription(locationAndDesc.get(1).getText());
                }
            }
            count++;
            albumRepository.save(toSave);
        }
    }

    public void fetchPhotosOfAlbum(WebDriver driver, Album undoneAlbum, int pageNumber){
        String baseUrl = undoneAlbum.getUrl() + "?page=" + pageNumber;

        // launch Chrome and direct it to the Base URL
        driver.get(baseUrl);

        WebElement photoContent = driver.findElement(By.cssSelector(".Masonry"));

        List<WebElement> allImgs = photoContent.findElements(By.tagName("img"));

        int count = 1;
        for(WebElement eachImg: allImgs){
            Photo toSave = new Photo();
            System.out.println("Image " + count++ + ": ");
            System.out.println(eachImg.getAttribute("src"));
            toSave.setUrl(eachImg.getAttribute("src"));
            toSave.setAlbum(undoneAlbum);
            photoRepository.save(toSave);
        }

    }

}
