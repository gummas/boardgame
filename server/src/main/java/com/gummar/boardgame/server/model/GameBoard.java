/*
 * Copyright (C) 2020 Raj Gumma.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gummar.boardgame.server.model;


import java.util.Arrays;
import java.util.UUID;

/**
 * The GameBoard model.
 *
 * @author Raj Gumma
 */
public class GameBoard {

    /**
     * The Board.
     */
    Board board;
    /**
     * The Player 1.
     */
    Player player1;
    /**
     * The Player 2.
     */
    Player player2;
    /**
     * The State.
     */
    BOARD_STATE state;
    /**
     * The Winner.
     */
    String winner;

    private GameBoard(final String[][] board, final String boardId) {
        this.board = new Board(board, boardId);
        this.state = BOARD_STATE.INITIALIZED;
    }

    /**
     * Create new game board.
     *
     * @param rowSize the row size
     * @param colSize the col size
     * @return the board
     */
    public static GameBoard createNewBoard(int rowSize, int colSize) {
        String[][] board = new String[rowSize][colSize];
        Arrays.stream(board).forEach(row -> Arrays.fill(row, "_"));//Initialise board with Zeroes
        return new GameBoard(board, UUID.randomUUID().toString());
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

    /**
     * Gets board.
     *
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets player 1.
     *
     * @return the player 1
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * Sets player 1.
     *
     * @param player1 the player 1
     */
    public void setPlayer1(final Player player1) {
        this.player1 = player1;
    }

    /**
     * Gets player 2.
     *
     * @return the player 2
     */
    public Player getPlayer2() {
        return player2;
    }

    /**
     * Sets player 2.
     *
     * @param player2 the player 2
     */
    public void setPlayer2(final Player player2) {
        this.player2 = player2;
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

    @Override
    public String toString() {
        return "GameBoard{" +
                "board=" + Arrays.toString(board.getBoard()) +
                ", boardId='" + board.getBoardId() + '\'' +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", state=" + state +
                ", steps=" + board.getSteps() +
                '}';
    }
}
