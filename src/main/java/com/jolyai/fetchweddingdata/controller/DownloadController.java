package com.jolyai.fetchweddingdata.controller;

import com.jolyai.fetchweddingdata.model.Photo;
import com.jolyai.fetchweddingdata.model.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("download")
public class DownloadController {

    @Autowired
    private PhotoRepository photoRepository;

    @GetMapping("photos")
    public ResponseEntity<?> downloadPhotos(){

        String baseDir = "/home/jaya/Pictures"; // /wedmegood/hyderabad/pixexl8/anusha+sahas/

        Photo photo = photoRepository.getPhotoNotDownloaded();


        while (photo != null ){
            String source = photo.getAlbum().getPhotographer().getSource();
            String location = photo.getAlbum().getPhotographer().getLocation();

            String photographerName = photo.getAlbum().getPhotographer().getName();
            String albumName = photo.getAlbum().getAlbumName();

            String fullDirPath = baseDir + "/" + source + "/" + location + "/" + photographerName + "/" + albumName + "/";
            String imageName = photo.getUrl().substring(photo.getUrl().lastIndexOf("/") + 1);
            String fullImagePath = fullDirPath + imageName;

            if(createPhotographerAlbumDir(fullDirPath)){
                try(InputStream in = new URL(photo.getUrl().replace("/350X", "/1000X")).openStream()){
                    Files.copy(in, Paths.get(fullImagePath), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }

            photo.setDownloaded(true);
            photoRepository.save(photo);

            photo = photoRepository.getPhotoNotDownloaded();
        }


        return new ResponseEntity<>("Finished", HttpStatus.OK);
    }

    @GetMapping("ip")
    public ResponseEntity<?> whatIsMyIP() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("System IP Address : " +
                (localhost.getHostAddress()).trim());

        // Find public IP address
        String systemipaddress = "";
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc =
                    new BufferedReader(new InputStreamReader(url_name.openStream()));

            // reads system IPAddress
            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e)
        {
            System.err.println(e);
            systemipaddress = "Cannot Execute Properly";
        }
        System.out.println("Public IP Address: " + systemipaddress +"\n");

        return new ResponseEntity<>("Ok", HttpStatus.OK);
    }

    public Boolean createPhotographerAlbumDir(String dirPath){
        try {

            Path path = Paths.get(dirPath);

            //java.nio.file.Files;
            Files.createDirectories(path);

            System.out.println("Directory is created: " + path);

            return true;

        } catch (IOException e) {
            System.err.println("Failed to create directory." + e.getMessage());
            return false;
        }
    }
}
