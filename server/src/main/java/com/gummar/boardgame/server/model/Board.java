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

/**
 * The Board model.
 *
 * @author Raj Gumma
 */
public class Board {
    /**
     * The GameBoard.
     */
    String[][] board;
    /**
     * The GameBoard id.
     */
    String boardId;

    /**
     * The Steps.
     */
    int steps = 0;

    /**
     * Instantiates a new Board.
     *
     * @param board   the board
     * @param boardId the board id
     */
    public Board(final String[][] board, final String boardId) {
        this.board = board;
        this.boardId = boardId;
    }

    /**
     * Get board int
     *
     * @return the int [ ] [ ]
     */
    public String[][] getBoard() {
        return board;
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
     * Gets steps.
     *
     * @return the steps
     */
    public int getSteps() {
        return steps;
    }


    /**
     * Increment steps.
     */
    public void incrementSteps() {
        this.steps += 1;
    }
}
