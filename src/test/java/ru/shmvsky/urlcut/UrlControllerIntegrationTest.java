package ru.shmvsky.urlcut;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.shmvsky.urlcut.core.controller.UrlController;
import ru.shmvsky.urlcut.core.exception.UrlNotFoundException;
import ru.shmvsky.urlcut.core.service.UrlService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
public class UrlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @Test
    public void testShortenUrl_withAlias() throws Exception {
        String destination = "http://example.com";
        String alias = "customAlias";
        LocalDateTime ttl = LocalDateTime.now().plusDays(1);
        String formattedTtl = ttl.format(DateTimeFormatter.ISO_DATE_TIME);

        when(urlService.shortenUrl(destination, alias, ttl)).thenReturn(alias);

        mockMvc.perform(get("/api/cut")
                        .param("destination", destination)
                        .param("alias", alias)
                        .param("ttl", formattedTtl))
                .andExpect(status().isOk())
                .andExpect(content().string(alias));
    }

    @Test
    public void testShortenUrl_withoutAlias() throws Exception {
        String destination = "http://example.com";
        String shortUrl = "shortUrl";
        LocalDateTime ttl = LocalDateTime.now().plusDays(1);
        String formattedTtl = ttl.format(DateTimeFormatter.ISO_DATE_TIME);

        when(urlService.shortenUrl(destination, null, ttl)).thenReturn(shortUrl);

        mockMvc.perform(get("/api/cut")
                        .param("destination", destination)
                        .param("ttl", formattedTtl))
                .andExpect(status().isOk())
                .andExpect(content().string(shortUrl));
    }

    @Test
    public void testRedirect() throws Exception {
        String shortUrl = "shortUrl";
        String destination = "http://example.com";

        when(urlService.getDestination(shortUrl)).thenReturn(destination);

        mockMvc.perform(get("/api/{shortUrl}", shortUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(destination));
    }

    @Test
    public void testRedirect_notFound() throws Exception {
        String shortUrl = "nonexistentAlias";

        when(urlService.getDestination(shortUrl)).thenThrow(new UrlNotFoundException(shortUrl));

        mockMvc.perform(get("/api/{shortUrl}", shortUrl))
                .andExpect(status().isNotFound());
    }
}
