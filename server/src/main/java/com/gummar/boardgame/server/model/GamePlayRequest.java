package com.gummar.boardgame.server.model;

/**
 * The Game play request.
 *
 * @author Raj Gumma
 */
public class GamePlayRequest {

    /**
     * The Board id.
     */
    String boardId;
    /**
     * The Player id.
     */
    String playerId;

    /**
     * Gets board id.
     *
     * @return the board id
     */
    public String getBoardId() {
        return boardId;
    }

    /**
     * Sets board id.
     *
     * @param boardId the board id
     */
    public void setBoardId(final String boardId) {
        this.boardId = boardId;
    }

    /**
     * Gets player id.
     *
     * @return the player id
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Sets player id.
     *
     * @param playerId the player id
     */
    public void setPlayerId(final String playerId) {
        this.playerId = playerId;
    }
}
