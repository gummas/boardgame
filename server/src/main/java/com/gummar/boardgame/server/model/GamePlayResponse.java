package com.gummar.boardgame.server.model;

/**
 * The Game play response.
 *
 * @author Raj Gumma
 */
public class GamePlayResponse {

    /**
     * The Board id.
     */
    String boardId;
    /**
     * The Player id.
     */
    String playerId;
    /**
     * The Player turn.
     */
    boolean playerTurn = false;
    /**
     * The State.
     */
    BOARD_STATE state;
    /**
     * The Board.
     */
    String[][] board;
    /**
     * The Error detail.
     */
    ErrorDetail errorDetail;
    private String winner;

    /**
     * Gets error detail.
     *
     * @return the error detail
     */
    public ErrorDetail getErrorDetail() {
        return errorDetail;
    }

    /**
     * Sets error detail.
     *
     * @param errorDetail the error detail
     */
    public void setErrorDetail(final ErrorDetail errorDetail) {
        this.errorDetail = errorDetail;
    }

    /**
     * Get board.
     *
     * @return the string array
     */
    public String[][] getBoard() {
        return board;
    }

    /**
     * Sets board.
     *
     * @param board the board
     */
    public void setBoard(final String[][] board) {
        this.board = board;
    }

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
     * Is player turn boolean.
     *
     * @return the boolean
     */
    public boolean isPlayerTurn() {
        return playerTurn;
    }

    /**
     * Sets player turn.
     *
     * @param playerTurn the player turn
     */
    public void setPlayerTurn(final boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public BOARD_STATE getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(final BOARD_STATE state) {
        this.state = state;
    }

    /**
     * Gets winner.
     *
     * @return the winner
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Sets winner.
     *
     * @param winner the winner
     */
    public void setWinner(final String winner) {
        this.winner = winner;
    }
}
