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
 * The enum Board state.
 *
 * @author Raj Gumma
 */
public enum BOARD_STATE {
    /**
     * Initialized board state.
     */
    INITIALIZED(0),
    /**
     * Player 1 in board state.
     */
    PLAYER_1_IN(1),
    /**
     * Player 2 in board state.
     */
    PLAYER_2_IN(2),
    /**
     * Game in progress board state.
     */
    GAME_IN_PROGRESS(3),
    /**
     * Game won board state.
     */
    GAME_WON(4),
    /**
     * Game tied board state.
     */
    GAME_TIED(5),
    /**
     * Inactive board state.
     */
    INACTIVE(6);

    private int state;

    BOARD_STATE(final int state) {
        this.state = state;
    }

}
