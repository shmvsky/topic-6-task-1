package ru.shmvsky.urlcut.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shmvsky.urlcut.core.entity.Url;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Url findByShortUrl(String url);

    Url findByDestination(String destination);

    boolean existsByShortUrl(String link);

    boolean existsByDestination(String destination);

    List<Url> findAllByTtlBefore(LocalDateTime time);
}
