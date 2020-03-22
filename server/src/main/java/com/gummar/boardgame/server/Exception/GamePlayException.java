package com.gummar.boardgame.server.Exception;

import org.springframework.web.client.RestClientException;

/**
 * The Game play exception.
 *
 * @author Raj Gumma
 */
public class GamePlayException extends RestClientException {
    /**
     * Instantiates a new Game play exception.
     *
     * @param msg the msg
     */
    public GamePlayException(final String msg) {
        super(msg);
    }

    /**
     * Instantiates a new Game play exception.
     *
     * @param msg the msg
     * @param ex  the ex
     */
    public GamePlayException(final String msg, final Throwable ex) {
        super(msg, ex);
    }
}
