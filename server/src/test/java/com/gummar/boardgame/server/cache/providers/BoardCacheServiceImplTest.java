package com.gummar.boardgame.server.cache.providers;

import com.gummar.boardgame.server.cache.CacheConfiguration;
import com.gummar.boardgame.server.model.BOARD_STATE;
import com.gummar.boardgame.server.model.GameBoard;
import com.gummar.boardgame.server.model.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BoardCacheServiceImplTest {

    BoardCacheServiceImpl cacheService;
    CacheManager manager;
    Cache cache;
    GameBoard gameBoard;

    @Before
    public void setUp() throws Exception {
        manager = new ConcurrentMapCacheManager(CacheConfiguration.CACHE_NAME);
        cacheService = new BoardCacheServiceImpl(manager);
        cache = manager.getCache(CacheConfiguration.CACHE_NAME);
        gameBoard = GameBoard.createNewBoard(BoardCacheProvider.ROW_SIZE, BoardCacheProvider.COL_SIZE);
    }

    @Test
    public void testGetBoardById() {
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        Optional<GameBoard> result = cacheService.getBoardById(gameBoard.getBoard().getBoardId());
        assertTrue(result.isPresent());
        assertEquals(gameBoard, result.get());
    }

    @Test
    public void testGetBoardForNewPlayer() {
        // Board Exists
        Player player1 = new Player("User1",UUID.randomUUID().toString());
        gameBoard.setPlayer1(player1);
        gameBoard.setState(BOARD_STATE.PLAYER_1_IN);
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        final GameBoard boardFound = cacheService.getBoardForNewPlayer();
        assertEquals(boardFound, gameBoard);

        //Create a new board
        cache.clear();
        final GameBoard newBoard = cacheService.getBoardForNewPlayer();
        assertNotNull(newBoard);
        assertNotEquals(newBoard.getBoard().getBoardId(), gameBoard.getBoard().getBoardId());
    }

    @Test
    public void testPutBoard() {
        cacheService.putBoard(gameBoard.getBoard().getBoardId(), gameBoard);
        assertEquals(cache.get(gameBoard.getBoard().getBoardId(), GameBoard.class), gameBoard);
    }

    @Test
    public void testDeleteBoard() {
        cache.put("TestKey", gameBoard);
        assertNotNull(cache.get("TestKey", GameBoard.class));
        cacheService.deleteBoard("TestKey");
        assertNull(cache.get("TestKey", GameBoard.class));
    }

    @Test
    public void clearCache() {
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        Optional<GameBoard> result = cacheService.getBoardById(gameBoard.getBoard().getBoardId());
        assertTrue(result.isPresent());
        assertEquals(gameBoard, result.get());
        cacheService.clearCache(CacheConfiguration.CACHE_NAME);
        assertNull(cache.get(gameBoard.getBoard().getBoardId(), GameBoard.class));
    }
}