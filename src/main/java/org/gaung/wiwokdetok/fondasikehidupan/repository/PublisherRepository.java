package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PublisherRepository extends JpaRepository<Publisher, UUID> {
    Optional<Publisher> findByNameIgnoreCase(String name);
}