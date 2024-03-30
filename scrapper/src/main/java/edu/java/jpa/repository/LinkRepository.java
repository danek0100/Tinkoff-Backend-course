package edu.java.jpa.repository;

import edu.java.domain.Link;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByUrl(String url);

    List<Link> findByLastCheckTimeBeforeOrLastCheckTimeIsNull(LocalDateTime dateTime);

    boolean existsByUrl(String url);
}
