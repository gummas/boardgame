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

package com.gummar.boardgame.server.cache.providers;


import com.gummar.boardgame.server.model.GameBoard;

import java.util.Optional;

/**
 * The interface Board cache provider.
 *
 * @author Raj Gumma
 */
public interface BoardCacheProvider {
    /**
     * The constant ROW_SIZE.
     */
    int ROW_SIZE = 6;
    /**
     * The constant COL_SIZE.
     */
    int COL_SIZE = 9;

    /**
     * Gets board by id.
     *
     * @param boardId the board id
     * @return the board by id
     */
    Optional<GameBoard> getBoardById(final String boardId);


    /**
     * Put board.
     *
     * @param boardId   the board id
     * @param gameBoard the game board
     */
    void putBoard(String boardId, final GameBoard gameBoard);

    /**
     * Delete board.
     *
     * @param boardId the board id
     */
    void deleteBoard(String boardId);

    /**
     * Gets board for new player.
     *
     * @return the board for new player
     */
    GameBoard getBoardForNewPlayer();
}
