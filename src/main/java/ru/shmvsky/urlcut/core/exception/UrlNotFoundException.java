package ru.shmvsky.urlcut.core.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String url) {
        super("Short url '" + url + "' not found");
    }

}
