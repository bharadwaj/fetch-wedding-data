package com.jolyai.fetchweddingdata.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Photo {
    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition="VARCHAR(500)")
    private String url;
    private LocalDateTime created_timestamp = LocalDateTime.now();

    @ManyToOne
    private Album album;

    private Boolean downloaded;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(LocalDateTime created_timestamp) {
        this.created_timestamp = created_timestamp;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Boolean getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(Boolean downloaded) {
        this.downloaded = downloaded;
    }
}
