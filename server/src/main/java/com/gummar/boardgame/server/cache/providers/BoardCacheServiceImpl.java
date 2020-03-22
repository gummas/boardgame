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

import com.gummar.boardgame.server.cache.CacheConfiguration;
import com.gummar.boardgame.server.model.BOARD_STATE;
import com.gummar.boardgame.server.model.GameBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The GameBoard cache service.
 *
 * @author Raj Gumma
 */
@Service
public class BoardCacheServiceImpl implements BoardCacheProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(BoardCacheProvider.class);
    private final CacheManager cacheManager;
    private final Cache cache;

    /**
     * Instantiates a new GameBoard cache service.
     *
     * @param cacheManager the cache manager
     */
    @Autowired
    public BoardCacheServiceImpl(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        cache = getCache(CacheConfiguration.CACHE_NAME);
    }


    @Override
    public Optional<GameBoard> getBoardById(final String boardId) {
        LOGGER.info("Getting board from cache using key: {}", boardId);
        final GameBoard value = cache.get(boardId, GameBoard.class);
        return Optional.ofNullable(value);
    }


    @Override
    public GameBoard getBoardForNewPlayer() {
        GameBoard result;
        Optional<GameBoard> board = findFreeBoardInCache();
        if (board.isPresent()) {
            result = board.get();
        } else {
            result = GameBoard.createNewBoard(ROW_SIZE, COL_SIZE);
            result.setState(BOARD_STATE.INITIALIZED);
            putBoard(result.getBoard().getBoardId(), result);
        }
        return result;
    }

    private Optional<GameBoard> findFreeBoardInCache() {
        if (cache != null) {
            final Object nativeCache = cache.getNativeCache();
            if (nativeCache instanceof ConcurrentHashMap) {
                @SuppressWarnings("unchecked") final ConcurrentHashMap<Object, Object> concurrentHashMap =
                        (ConcurrentHashMap<Object, Object>) nativeCache;
                final List<GameBoard> gameBoards = new ArrayList<>();

                concurrentHashMap.keySet().stream().forEach(key -> {
                    final Optional<GameBoard> optional = getBoardById(key.toString());
                    if (optional.isPresent()) {
                        final GameBoard gameBoard = optional.get();
                        if (gameBoard.getState() == BOARD_STATE.PLAYER_1_IN || gameBoard.getState() == BOARD_STATE.INITIALIZED) {
                            LOGGER.info("GameBoard found waiting for a player {}", gameBoard.getBoard().getBoardId());
                            gameBoards.add(gameBoard);
                        }
                    }
                });
                if (!gameBoards.isEmpty()) {
                    return Optional.ofNullable(gameBoards.get(0));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void putBoard(final String boardId, final GameBoard gameBoard) {
        LOGGER.info("Add/Overwrite GameBoard: {} with key: {} into cache ...", gameBoard, boardId);
        cache.put(boardId, gameBoard);
    }

    @Override
    public void deleteBoard(final String boardId) {
        final Optional<GameBoard> optional = getBoardById(boardId);
        if (optional.isPresent()) {
            final GameBoard gameBoard = optional.get();

            LOGGER.info("Will evict gameBoard from cache with boardId: {}", boardId);
            cache.evict(boardId);
        } else {
            LOGGER.error("Unable to find board for boardId: {} ", boardId);
        }
    }

    /**
     * Clear cache.
     *
     * @param name the name
     */
    protected void clearCache(final String name) {
        final Cache cache = cacheManager.getCache(name);
        if (cache != null) {
            final ConcurrentHashMap<?, ?> nativeCache = (ConcurrentHashMap<?, ?>) cache.getNativeCache();
            LOGGER.info("Clear all entries from cahce: {}", cache.getName());
            nativeCache.clear();
        }
    }

    /**
     * Gets cache.
     *
     * @param name the name
     * @return the cache
     */
    protected Cache getCache(final String name) {
        return cacheManager.getCache(name);
    }

}
