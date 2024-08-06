package ru.shmvsky.urlcut.core.exception;

public class AliasAlreadyTakenException extends RuntimeException {

    public AliasAlreadyTakenException(String alias) {
        super("Alias '" + alias + "' already taken");
    }

}
