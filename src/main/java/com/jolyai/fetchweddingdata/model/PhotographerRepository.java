package com.jolyai.fetchweddingdata.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhotographerRepository extends JpaRepository<Photographer, Long> {

    @Query(value = "select p.* from Photographer p where " +
            "p.FETCHED_ALBUMS <> True OR p.FETCHED_ALBUMS is null order by p.id asc limit 1",
            nativeQuery = true)
    Photographer getPhotographerUndoneAlbum();

    @Query(value = "select p.* from Photographer p where " +
            "p.FETCHED_ALBUMS <> True OR p.FETCHED_ALBUMS is null order by p.id desc limit 1",
            nativeQuery = true)
    Photographer getPhotographerUndoneAlbum2();

    @Query(value = "select p from Photographer p where p.name = :name")
    Photographer getPhotographerByName(@Param("name") String name);

}
