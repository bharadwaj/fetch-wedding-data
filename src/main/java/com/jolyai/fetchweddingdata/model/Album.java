package com.jolyai.fetchweddingdata.model;

import javax.persistence.*;

@Entity
public class Album {
    @Id
    @GeneratedValue
    private Long id;

    private String albumName;
    private String url;
    private String location;
    @Column(name = "description", columnDefinition="VARCHAR(500)")
    private String description;

    private Boolean fetchedPhotos;

    @ManyToOne
    private Photographer photographer;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getFetchedPhotos() {
        return fetchedPhotos;
    }

    public void setFetchedPhotos(Boolean fetchedPhotos) {
        this.fetchedPhotos = fetchedPhotos;
    }
}
