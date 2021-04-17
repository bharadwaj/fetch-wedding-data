package com.jolyai.fetchweddingdata.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    @Query(value = "select p.* from PHoto p where " +
            "p.DOWNLOADED <> True OR p.DOWNLOADED is null order by p.id asc limit 1",
            nativeQuery = true)
    Photo getPhotoNotDownloaded();
}
