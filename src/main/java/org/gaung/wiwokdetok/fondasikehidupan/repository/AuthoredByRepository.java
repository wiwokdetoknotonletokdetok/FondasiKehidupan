package org.gaung.wiwokdetok.fondasikehidupan.repository;

import org.gaung.wiwokdetok.fondasikehidupan.model.AuthoredBy;
import org.gaung.wiwokdetok.fondasikehidupan.model.AuthoredById;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthoredByRepository extends JpaRepository<AuthoredBy, AuthoredById> {
}
