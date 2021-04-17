package com.jolyai.fetchweddingdata.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    @Query(value = "select a.* from Album a where " +
            "a.fetched_photos <> True OR a.fetched_photos is null order by a.id asc limit 1",
            nativeQuery = true)
    Album getAlbumWithUndonePhotos();

    @Query(value = "select a.* from Album a where " +
            "a.fetched_photos <> True OR a.fetched_photos is null order by a.id desc limit 1",
            nativeQuery = true)
    Album getAlbumWithUndonePhotos2();

}
