package ru.shmvsky.urlcut;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import ru.shmvsky.urlcut.core.entity.Url;
import ru.shmvsky.urlcut.core.exception.AliasAlreadyTakenException;
import ru.shmvsky.urlcut.core.exception.UrlNotFoundException;
import ru.shmvsky.urlcut.core.repository.UrlRepository;
import ru.shmvsky.urlcut.core.service.UrlService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UrlServiceTest {


    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(urlService, "random", new Random(0));
    }

    @Test
    public void testShortenUrl_withAlias_success() {
        String destination = "http://example.com";
        String alias = "customAlias";
        LocalDateTime ttl = LocalDateTime.now().plusDays(1);

        when(urlRepository.existsByDestination(destination)).thenReturn(false);
        when(urlRepository.existsByShortUrl(alias)).thenReturn(false);

        String result = urlService.shortenUrl(destination, alias, ttl);

        assertEquals(alias, result);
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    public void testShortenUrl_withAlias_alreadyTaken() {
        String destination = "http://example.com";
        String alias = "customAlias";
        LocalDateTime ttl = LocalDateTime.now().plusDays(1);

        when(urlRepository.existsByDestination(destination)).thenReturn(false);
        when(urlRepository.existsByShortUrl(alias)).thenReturn(true);

        assertThrows(AliasAlreadyTakenException.class, () -> {
            urlService.shortenUrl(destination, alias, ttl);
        });
    }

    @Test
    public void testShortenUrl_withoutAlias_success() {
        String destination = "http://example.com";
        LocalDateTime ttl = LocalDateTime.now().plusDays(1);

        when(urlRepository.existsByDestination(destination)).thenReturn(false);
        when(urlRepository.existsByShortUrl(anyString())).thenReturn(false);

        String result = urlService.shortenUrl(destination, null, ttl);

        assertNotNull(result);
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    public void testGetDestination_found() {
        String shortUrl = "customAlias";
        String destination = "http://example.com";

        Url url = new Url();
        url.setShortUrl(shortUrl);
        url.setDestination(destination);

        when(urlRepository.existsByShortUrl(shortUrl)).thenReturn(true);
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(url);

        String result = urlService.getDestination(shortUrl);

        assertEquals(destination, result);
    }

    @Test
    public void testGetDestination_notFound() {
        String shortUrl = "nonexistentAlias";

        when(urlRepository.existsByShortUrl(shortUrl)).thenReturn(false);

        assertThrows(UrlNotFoundException.class, () -> {
            urlService.getDestination(shortUrl);
        });
    }

    @Test
    public void testDeleteExpired() {
        LocalDateTime now = LocalDateTime.now();
        Url expiredUrl = new Url();
        expiredUrl.setTtl(now.minusDays(1));

        when(urlRepository.findAllByTtlBefore(now)).thenReturn(Collections.singletonList(expiredUrl));

        urlService.deleteExpired();

        verify(urlRepository, times(1)).deleteAll(anyList());
    }

}
