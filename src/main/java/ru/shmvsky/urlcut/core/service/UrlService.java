package ru.shmvsky.urlcut.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shmvsky.urlcut.core.entity.Url;
import ru.shmvsky.urlcut.core.exception.AliasAlreadyTakenException;
import ru.shmvsky.urlcut.core.exception.UrlNotFoundException;
import ru.shmvsky.urlcut.core.repository.UrlRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final Random random = new Random();

    private final UrlRepository urlRepository;

    @Transactional
    public String shortenUrl(String destination, String alias, LocalDateTime ttl) {

        if (urlRepository.existsByDestination(destination)) {
            return urlRepository.findByDestination(destination).getShortUrl();
        }

        String shortUrl;

        if (alias != null) {
            shortUrl = alias;
            if (urlRepository.existsByShortUrl(shortUrl)) {
                throw new AliasAlreadyTakenException(alias);
            }
        } else {
            do {
                shortUrl = Integer.toHexString(random.nextInt());
            } while (urlRepository.existsByShortUrl(shortUrl));
        }

        Url url = new Url();
        url.setDestination(destination);
        url.setShortUrl(shortUrl);
        url.setTtl(ttl);
        urlRepository.save(url);

        return shortUrl;
    }

    public String getDestination(String shortUrl) {

        Url url;

        if (urlRepository.existsByShortUrl(shortUrl)) {
            url = urlRepository.findByShortUrl(shortUrl);
        } else {
            throw new UrlNotFoundException(shortUrl);
        }

        return url.getDestination();
    }

    @Transactional
    @Scheduled(fixedDelay = 60000)
    public void deleteExpired() {
        urlRepository.deleteAll(urlRepository.findAllByTtlBefore(LocalDateTime.now()));
    }

}
