package com.gummar.boardgame.server.model;

/**
 * The Move request.
 *
 * @author Raj Gumma
 */
public class MoveRequest {
    /**
     * The Board id.
     */
    String boardId;
    /**
     * The Player id.
     */
    String playerId;
    /**
     * The Move.
     */
    int move;

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

    /**
     * Gets move.
     *
     * @return the move
     */
    public int getMove() {
        return move;
    }

    /**
     * Sets move.
     *
     * @param move the move
     */
    public void setMove(final int move) {
        this.move = move;
    }
}
