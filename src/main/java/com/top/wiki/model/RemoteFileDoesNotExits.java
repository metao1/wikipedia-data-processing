package com.top.wiki.model;

/**
 * Uses when there is not file presented in the remote host
 */
public class RemoteFileDoesNotExits extends RuntimeException {
    public RemoteFileDoesNotExits(String message) {
        super(message);
    }
}
