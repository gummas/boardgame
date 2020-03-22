package com.gummar.boardgame.server.service;

import com.gummar.boardgame.server.Exception.GamePlayException;
import com.gummar.boardgame.server.cache.CacheConfiguration;
import com.gummar.boardgame.server.cache.providers.BoardCacheProvider;
import com.gummar.boardgame.server.cache.providers.BoardCacheServiceImpl;
import com.gummar.boardgame.server.model.BOARD_STATE;
import com.gummar.boardgame.server.model.Board;
import com.gummar.boardgame.server.model.GameBoard;
import com.gummar.boardgame.server.model.GamePlayRequest;
import com.gummar.boardgame.server.model.GamePlayResponse;
import com.gummar.boardgame.server.model.MoveRequest;
import com.gummar.boardgame.server.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Raj Gumma
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
class GamePlayServiceImplTest {
    BoardCacheServiceImpl cacheService;
    CacheManager manager;
    Cache cache;
    GameBoard gameBoard;
    GamePlayService service;

    @BeforeEach
    void setUp() {
        manager = new ConcurrentMapCacheManager(CacheConfiguration.CACHE_NAME);
        cacheService = new BoardCacheServiceImpl(manager);
        cache = manager.getCache(CacheConfiguration.CACHE_NAME);
        gameBoard = GameBoard.createNewBoard(BoardCacheProvider.ROW_SIZE, BoardCacheProvider.COL_SIZE);
        service = new GamePlayServiceImpl(cacheService);
    }

    private MoveRequest moveSetup(int player) {
        Player player1 = new Player("test", "Test_Id");
        player1.setPiece("X");
        Player player2 = new Player("player2", "Test_ID_2");
        player2.setPiece("O");
        gameBoard.setPlayer1(player1);
        gameBoard.setPlayer2(player2);
        gameBoard.setState(BOARD_STATE.PLAYER_2_IN);
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        MoveRequest request = new MoveRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId(player == 1 ? player1.getPlayerId() : player2.getPlayerId());
        if (player % 2 == 0) {
            gameBoard.getBoard().incrementSteps();
        }
        return request;
    }

    @Test
    void processMove() {
        MoveRequest request = moveSetup(1);
        request.setMove(0);
        GamePlayResponse response = service.processMove(request);
        assertEquals(BOARD_STATE.GAME_IN_PROGRESS, response.getState());
        assertFalse(response.isPlayerTurn());
        assertEquals("X", response.getBoard()[0][0]);
    }

    @Test
    void processVerticalWinningMove() {
        MoveRequest request = moveSetup(2);
        // Testing Vertical Line Win
        String[][] playBoard = gameBoard.getBoard().getBoard();
        playBoard[0][0] = "O";
        playBoard[1][0] = "O";
        playBoard[2][0] = "O";
        playBoard[3][0] = "O";
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        request.setMove(0);

        GamePlayResponse response = service.processMove(request);
        assertEquals(BOARD_STATE.GAME_WON, response.getState());
        assertEquals(response.getWinner(), request.getPlayerId());
    }

    @Test
    void processHorizontalWinningMove() {
        MoveRequest request = moveSetup(1);
        // Testing Vertical Line Win
        String[][] playBoard = gameBoard.getBoard().getBoard();
        playBoard[0][0] = "X";
        playBoard[0][1] = "X";
        playBoard[0][2] = "X";
        playBoard[0][3] = "X";
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        request.setMove(4);

        GamePlayResponse response = service.processMove(request);
        assertEquals(BOARD_STATE.GAME_WON, response.getState());
        assertEquals(response.getWinner(), request.getPlayerId());
    }

    @Test
    void processDiagonalWinningMove1() {
        MoveRequest request = moveSetup(1);
        // Testing Diagonal Line Win
        String[][] playBoard = gameBoard.getBoard().getBoard();
        playBoard[0][0] = "X";
        playBoard[1][1] = "X";
        playBoard[2][2] = "X";
        playBoard[3][3] = "X";
        playBoard[0][4] = "O";
        playBoard[1][4] = "O";
        playBoard[2][4] = "O";
        playBoard[3][4] = "O";
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        request.setMove(4);

        GamePlayResponse response = service.processMove(request);
        assertEquals(BOARD_STATE.GAME_WON, response.getState());
        assertEquals(response.getWinner(), request.getPlayerId());
    }

    @Test
    void processDiagonalWinningMove2() {
        MoveRequest request = moveSetup(2);
        // Testing inverse Diagonal Line Win
        String[][] playBoard = gameBoard.getBoard().getBoard();
        playBoard[0][8] = "O";
        playBoard[1][7] = "O";
        playBoard[2][6] = "O";
        playBoard[3][5] = "O";
        playBoard[0][4] = "X";
        playBoard[1][4] = "X";
        playBoard[2][4] = "X";
        playBoard[3][4] = "X";
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        request.setMove(4);

        GamePlayResponse response = service.processMove(request);
        assertEquals(BOARD_STATE.GAME_WON, response.getState());
        assertEquals(response.getWinner(), request.getPlayerId());
    }

    @Test
    void processTie() {
        MoveRequest request = moveSetup(2);
        // Testing inverse Diagonal Line Win
        createTieBoard();
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        request.setMove(0);

        GamePlayResponse response = service.processMove(request);
        assertEquals(BOARD_STATE.GAME_TIED, response.getState());
        assertEquals(response.getWinner(), "");
    }

    private void createTieBoard() {
        gameBoard.getBoard().getBoard()[0]= new String[] {"X", "X", "X", "O", "O", "O","X", "X", "X"};
        gameBoard.getBoard().getBoard()[1]= new String[] { "O", "O", "O","X", "X", "X", "O", "O", "O"};
        gameBoard.getBoard().getBoard()[2]= new String[] {"X", "X", "X", "X", "O", "X","O", "O", "O"};
        gameBoard.getBoard().getBoard()[3]= new String[] {"O", "O", "O", "X", "O", "O","X", "X", "X"};
        gameBoard.getBoard().getBoard()[4]= new String[] {"O", "O", "X", "O", "X", "X","O", "X", "X"};
        gameBoard.getBoard().getBoard()[5]= new String[] {"_", "O", "X", "X", "O", "X","O", "X", "O"};
        for (int count =0; count<BoardCacheProvider.ROW_SIZE*BoardCacheProvider.COL_SIZE; count++){
            gameBoard.getBoard().incrementSteps();
        }
    }

    @Test
    void testProcessMoveBoardNotInPlay() {
        Player player1 = new Player("test", "Test_Id");
        player1.setPiece("X");
        Player player2 = new Player("player2", "Test_ID_2");
        player2.setPiece("O");
        gameBoard.setPlayer1(player1);
        gameBoard.setPlayer2(player2);
        gameBoard.setState(BOARD_STATE.GAME_WON);
        gameBoard.setWinner("Winner");
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        MoveRequest request = new MoveRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId(player1.getPlayerId());
        request.setMove(0);

        GamePlayResponse response = service.processMove(request);
        assertEquals(BOARD_STATE.GAME_WON, response.getState());
        assertFalse(response.isPlayerTurn());
        assertEquals("Winner", response.getWinner());
        assertEquals("Board No longer in play", response.getErrorDetail().getErrorMessage());
    }

    @Test
    void testAssignPlayerToBoardInCache() {
        Player player1 = new Player("test", "Test_Id");
        gameBoard.setPlayer1(player1);
        gameBoard.setState(BOARD_STATE.PLAYER_1_IN);
        cacheService.putBoard(gameBoard.getBoard().getBoardId(), gameBoard);

        Board assignedBoard = service.assignPlayerToBoard(new Player("player2", "Test_ID_2"));
        assertEquals(assignedBoard, gameBoard.getBoard());
    }

    @Test
    void testAssignPlayerToNewBoard() {
        Player player1 = new Player("test", "Test_Id");
        Board assignedBoard = service.assignPlayerToBoard(player1);
        assertNotNull(assignedBoard);
        String boardId = assignedBoard.getBoardId();
        assertNotNull(boardId);
        GameBoard cachedGameBoard = cache.get(boardId, GameBoard.class);
        assertEquals(cachedGameBoard.getPlayer1(), player1);
        assertEquals(BOARD_STATE.PLAYER_1_IN, cachedGameBoard.getState());
    }


    @Test
    void checkPlayerTurn() {
        Player player1 = new Player("test", "Test_Id");
        Player player2 = new Player("player2", "Test_ID_2");
        gameBoard.setPlayer1(player1);
        gameBoard.setPlayer2(player2);
        gameBoard.setState(BOARD_STATE.PLAYER_2_IN);
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        // Check player 1s turn
        GamePlayRequest request = new GamePlayRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId(player1.getPlayerId());
        GamePlayResponse response = service.checkPlayerTurn(request);
        assertTrue(response.isPlayerTurn());

        //Check Player 2s turn
        request.setPlayerId(player2.getPlayerId());
        response = service.checkPlayerTurn(request);
        assertFalse(response.isPlayerTurn());
    }

    @Test
    void checkPlayerTurnWhileWaitingForPlayer() {
        Player player1 = new Player("test", "Test_Id");
        gameBoard.setPlayer1(player1);
        gameBoard.setState(BOARD_STATE.PLAYER_1_IN);
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        // Check player 1s turn
        GamePlayRequest request = new GamePlayRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId(player1.getPlayerId());
        GamePlayResponse response = service.checkPlayerTurn(request);
        assertFalse(response.isPlayerTurn());
    }

    @Test
    void checkPlayerTurnOnWin() {
        Player player1 = new Player("test", "Test_Id");
        Player player2 = new Player("player2", "Test_ID_2");
        gameBoard.setPlayer1(player1);
        gameBoard.setPlayer2(player2);
        gameBoard.setState(BOARD_STATE.GAME_WON);
        gameBoard.setWinner(player1.getPlayerId());
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        // Check player 1s turn
        GamePlayRequest request = new GamePlayRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId(player1.getPlayerId());
        GamePlayResponse response = service.checkPlayerTurn(request);
        assertFalse(response.isPlayerTurn());
        assertEquals(response.getWinner(), player1.getPlayerId());
    }

    @Test
    void checkPlayerTurnOnInvalidUser() {
        Player player1 = new Player("test", "Test_Id");
        Player player2 = new Player("player2", "Test_ID_2");
        gameBoard.setPlayer1(player1);
        gameBoard.setPlayer2(player2);
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        // Invalid User
        GamePlayRequest request = new GamePlayRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId("Invalid USER ID");
        GamePlayResponse response = service.checkPlayerTurn(request);
        assertFalse(response.isPlayerTurn());
        assertNotNull(response.getErrorDetail());
    }

    @Test
    void checkPlayerTurnOnInvalidBoard() {
        Player player1 = new Player("test", "Test_Id");
        Player player2 = new Player("player2", "Test_ID_2");
        gameBoard.setPlayer1(player1);
        gameBoard.setPlayer2(player2);
//        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);
        try {
            // Invalid User
            GamePlayRequest request = new GamePlayRequest();
            request.setBoardId(gameBoard.getBoard().getBoardId());
            request.setPlayerId("Invalid USER ID");
            GamePlayResponse response = service.checkPlayerTurn(request);
            assertFalse(response.isPlayerTurn());
            assertNotNull(response.getErrorDetail());
            fail("This should have raised an exception");
        } catch (GamePlayException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void disconnectedPlayerForOnlyPlayer() {
        Player player1 = new Player("test", "Test_Id");
        gameBoard.setPlayer1(player1);
        gameBoard.setState(BOARD_STATE.PLAYER_1_IN);
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        GamePlayRequest request = new GamePlayRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId(player1.getPlayerId());
        service.disconnectedPlayer(request);
        assertNull(cache.get(gameBoard.getBoard().getBoardId(), GameBoard.class));

    }

    @Test
    public void disconnectedPlayer1() {
        Player player1 = new Player("test", "Test_Id");
        Player player2 = new Player("test2", "Test_Id2");
        gameBoard.setPlayer1(player1);
        gameBoard.setPlayer2(player2);
        gameBoard.setState(BOARD_STATE.PLAYER_2_IN);
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        GamePlayRequest request = new GamePlayRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId(player1.getPlayerId());
        service.disconnectedPlayer(request);
        assertEquals(BOARD_STATE.GAME_WON, gameBoard.getState());
        assertEquals(player2.getPlayerId(),gameBoard.getWinner());
    }

    @Test
    public void disconnectedPlayer2() {
        Player player1 = new Player("test", "Test_Id");
        Player player2 = new Player("test2", "Test_Id2");
        gameBoard.setPlayer1(player1);
        gameBoard.setPlayer2(player2);
        gameBoard.setState(BOARD_STATE.GAME_IN_PROGRESS);
        cache.put(gameBoard.getBoard().getBoardId(), gameBoard);

        GamePlayRequest request = new GamePlayRequest();
        request.setBoardId(gameBoard.getBoard().getBoardId());
        request.setPlayerId(player2.getPlayerId());
        service.disconnectedPlayer(request);
        assertEquals(BOARD_STATE.GAME_WON, gameBoard.getState());
        assertEquals(player1.getPlayerId(),gameBoard.getWinner());
    }
}