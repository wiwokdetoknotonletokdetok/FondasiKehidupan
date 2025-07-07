package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.BookLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookLocationRepository extends JpaRepository<BookLocation, Integer> {

    @Query(value = """
    SELECT 
        id AS id,
        location_name AS locationName,
        ST_Y(location::geometry) AS latitude,
        ST_X(location::geometry) AS longitude,
        ST_Distance(location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) AS distance
    FROM book_location
    WHERE id_book = :bookId
    ORDER BY location <-> ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
    """, nativeQuery = true)
    List<Object[]> findAllNearestBookLocations(@Param("bookId") UUID bookId,
                                               @Param("latitude") double latitude,
                                               @Param("longitude") double longitude);

    Optional<BookLocation> findByIdAndBookId(int locationId, UUID bookId);
}
