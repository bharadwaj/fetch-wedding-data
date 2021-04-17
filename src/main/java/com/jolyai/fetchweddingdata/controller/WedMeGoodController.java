package com.jolyai.fetchweddingdata.controller;

import com.jolyai.fetchweddingdata.fetcher.WedMeGoodFetcher;
import com.jolyai.fetchweddingdata.model.Album;
import com.jolyai.fetchweddingdata.model.AlbumRepository;
import com.jolyai.fetchweddingdata.model.Photographer;
import com.jolyai.fetchweddingdata.model.PhotographerRepository;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("wedmegood")
public class WedMeGoodController {

    @Autowired
    private WedMeGoodFetcher wedMeGoodFetcher;

    @Autowired
    private PhotographerRepository photographerRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @GetMapping("photographers/{city}")
    public ResponseEntity<?> getWebMeGoodPhotographers(@PathVariable String city) {
        WebDriver driver = torWebDriver();
        String baseUrl = "https://www.wedmegood.com/vendors/" + city + "/wedding-photographers/";
        int totalPages = wedMeGoodFetcher.fetchTotalPagesCount(driver, baseUrl, ".vendor-pagination");
        int page = 1;
        while (page <= totalPages) {
            System.out.println("Starting page: " + page);
            wedMeGoodFetcher.fetchPhotographerCardsOfAPage(driver, page, city);
            page++;
        }
        driver.close();
        return new ResponseEntity<>(totalPages, HttpStatus.OK);
    }

    @GetMapping("albums")
    public ResponseEntity<?> getWebMeGoodAlbumsOfPhotographers() {
        long startTime = System.nanoTime();

        Photographer photographer = photographerRepository.getPhotographerUndoneAlbum();
        WebDriver driver = torWebDriver();

        while(photographer != null){
            wedMeGoodFetcher.fetchAlbumsOfPhotographer(driver, photographer);
            photographer.setFetchedAlbums(true);
            photographerRepository.save(photographer);
            photographer = photographerRepository.getPhotographerUndoneAlbum();
        }

        driver.close();

        long endTime   = System.nanoTime();
        long seconds = TimeUnit.NANOSECONDS.toSeconds(startTime - endTime);
        return new ResponseEntity<>("Ran for seconds: " + seconds, HttpStatus.OK);
    }

    @GetMapping("albums/2")
    public ResponseEntity<?> getWebMeGoodAlbumsOfPhotographers2() {
        long startTime = System.nanoTime();

        Photographer photographer = photographerRepository.getPhotographerUndoneAlbum2();
        WebDriver driver = torWebDriver();

        while(photographer != null){
            wedMeGoodFetcher.fetchAlbumsOfPhotographer(driver, photographer);
            photographer.setFetchedAlbums(true);
            photographerRepository.save(photographer);
            photographer = photographerRepository.getPhotographerUndoneAlbum2();
        }

        driver.close();

        long endTime   = System.nanoTime();
        long seconds = TimeUnit.NANOSECONDS.toSeconds(startTime - endTime);
        return new ResponseEntity<>("Ran for seconds: " + seconds, HttpStatus.OK);
    }

    @GetMapping("photos")
    public ResponseEntity<?> getWedMeGoodPhotosOfAlbums() {

        WebDriver driver = torWebDriver();
        Album undonePhotosAlbum = albumRepository.getAlbumWithUndonePhotos();

        while(undonePhotosAlbum != null) {
            int totalPages = wedMeGoodFetcher.fetchTotalPagesCount(driver, undonePhotosAlbum.getUrl(), ".pagination");
            int page = 1;
            while (page <= totalPages) {
                System.out.println("Starting fetching photos from page: " + page);
                wedMeGoodFetcher.fetchPhotosOfAlbum(driver, undonePhotosAlbum, page);
                page++;
            }
            undonePhotosAlbum.setFetchedPhotos(true);
            albumRepository.save(undonePhotosAlbum);
            undonePhotosAlbum = albumRepository.getAlbumWithUndonePhotos();
        }

        driver.close();

        return new ResponseEntity<>("Finished", HttpStatus.OK);
    }

    @GetMapping("photos/2")
    public ResponseEntity<?> getWedMeGoodPhotosOfAlbums2() {

        WebDriver driver = torWebDriver();
        Album undonePhotosAlbum = albumRepository.getAlbumWithUndonePhotos2();

        while(undonePhotosAlbum != null) {
            int totalPages = wedMeGoodFetcher.fetchTotalPagesCount(driver, undonePhotosAlbum.getUrl(), ".pagination");
            int page = 1;
            while (page <= totalPages) {
                System.out.println("Starting fetching photos from page: " + page);
                wedMeGoodFetcher.fetchPhotosOfAlbum(driver, undonePhotosAlbum, page);
                page++;
            }
            undonePhotosAlbum.setFetchedPhotos(true);
            albumRepository.save(undonePhotosAlbum);
            undonePhotosAlbum = albumRepository.getAlbumWithUndonePhotos2();
        }

        driver.close();

        return new ResponseEntity<>("Finished", HttpStatus.OK);
    }

    @GetMapping("testing/{name}")
    public ResponseEntity<?> testing(@PathVariable String name) {
        Photographer found = photographerRepository.getPhotographerByName(name);
        if(found == null ){
            return new ResponseEntity<>("Not Found", HttpStatus.OK);
        }
        return new ResponseEntity<>("Found", HttpStatus.OK);
    }

    public static WebDriver torWebDriver()  {
        WebDriver driver;
        Process torProcess;
        String torBinaryPath = "/home/jaya/Downloads/tor-browser_en-US/Browser/firefox";
        Runtime runTime = Runtime.getRuntime();
        try {
            torProcess = runTime.exec(torBinaryPath + " -n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.socks", "127.0.0.1");
        profile.setPreference("network.proxy.socks_port", 9150);
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setProfile(profile);
        driver = new FirefoxDriver(firefoxOptions);

        return driver;
    }

}
