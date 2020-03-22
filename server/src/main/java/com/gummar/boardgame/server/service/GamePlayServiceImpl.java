package com.gummar.boardgame.server.service;

import com.gummar.boardgame.server.Exception.GamePlayException;
import com.gummar.boardgame.server.cache.providers.BoardCacheProvider;
import com.gummar.boardgame.server.model.BOARD_STATE;
import com.gummar.boardgame.server.model.Board;
import com.gummar.boardgame.server.model.ErrorDetail;
import com.gummar.boardgame.server.model.GameBoard;
import com.gummar.boardgame.server.model.GamePlayRequest;
import com.gummar.boardgame.server.model.GamePlayResponse;
import com.gummar.boardgame.server.model.MoveRequest;
import com.gummar.boardgame.server.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.gummar.boardgame.server.cache.providers.BoardCacheProvider.COL_SIZE;
import static com.gummar.boardgame.server.cache.providers.BoardCacheProvider.ROW_SIZE;

/**
 * The Game play service implementation.
 *
 * @author Raj Gumma
 */
@Service
public class GamePlayServiceImpl implements GamePlayService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final BoardCacheProvider cacheServiceProvider;

    /**
     * Instantiates a new Game play service.
     *
     * @param cacheServiceProvider the cache service provider
     */
    @Autowired
    public GamePlayServiceImpl(final BoardCacheProvider cacheServiceProvider) {
        this.cacheServiceProvider = cacheServiceProvider;
    }

    @Override
    public GamePlayResponse processMove(final MoveRequest request) {
        GamePlayResponse response = new GamePlayResponse();
        response.setPlayerId(request.getPlayerId());
        GameBoard gameBoard = getGameBoard(request.getBoardId());
        if (gameBoard.getState() == BOARD_STATE.PLAYER_2_IN || gameBoard.getState() == BOARD_STATE.GAME_IN_PROGRESS) {
            if (validatePlayerOnBoard(gameBoard, request.getPlayerId())) {
                updateBoardWithMove(gameBoard, request);
            }
        } else if (gameBoard.getState() == BOARD_STATE.INITIALIZED || gameBoard.getState() == BOARD_STATE.PLAYER_1_IN) {
            LOGGER.error("Player Not allocated yet");
            response.setErrorDetail(new ErrorDetail("Player Not allocated yet", "Waiting for opponent"));
        } else if (gameBoard.getState() == BOARD_STATE.GAME_TIED || gameBoard.getState() == BOARD_STATE.GAME_WON) {
            LOGGER.error("Board No longer in play");
            response.setErrorDetail(new ErrorDetail("Board No longer in play", null));
            if (!gameBoard.getWinner().isEmpty()) {
                response.setWinner(gameBoard.getWinner());
            }
        }

        //map Response

        Optional<GameBoard> optional = cacheServiceProvider.getBoardById(request.getBoardId());
        if (optional.isPresent()) {
            gameBoard = optional.get();
            response.setBoard(gameBoard.getBoard().getBoard());
            LOGGER.info("Board after move {}", gameBoard.getBoard().getBoard());
            response.setBoardId(gameBoard.getBoard().getBoardId());
            response.setState(gameBoard.getState());
            response.setWinner(gameBoard.getState() == BOARD_STATE.GAME_WON ? gameBoard.getWinner() : "");
        }
        return response;
    }

    private boolean isTied(final GameBoard gameBoard) {
        return gameBoard.getBoard().getSteps() >= ROW_SIZE * COL_SIZE;
    }

    private boolean isWinningMove(final GameBoard gameBoard, String piece) {
        // Check horizontal line wins
        String[][] board = gameBoard.getBoard().getBoard();
        for (int col = 0; col < COL_SIZE - 4; col++) {
            for (int row = 0; row < ROW_SIZE; row++) {
                if (board[row][col] == piece && board[row][col + 1] == piece && board[row][col + 2] == piece &&
                        board[row][col + 3] == piece && board[row][col + 4] == piece) {
                    return true;
                }
            }
        }
        // Check for vertical line wins
        for (int col = 0; col < COL_SIZE; col++) {
            for (int row = 0; row < ROW_SIZE - 4; row++) {
                if (board[row][col] == piece && board[row + 1][col] == piece && board[row + 2][col] == piece &&
                        board[row + 3][col] == piece && board[row + 4][col] == piece) {
                    return true;
                }
            }
        }

        // Check for positive slopes /
        for (int col = 0; col < COL_SIZE - 4; col++) {
            for (int row = 0; row < ROW_SIZE - 4; row++) {
                if (board[row][col] == piece && board[row + 1][col + 1] == piece && board[row + 2][col + 2] == piece
                        && board[row + 3][col + 3] == piece && board[row + 4][col + 4] == piece) {
                    return true;
                }
            }
        }

        //Check for negative slopes \
        for (int col = 0; col < COL_SIZE - 4; col++) {
            for (int row = 4; row < ROW_SIZE; row++) {
                if (board[row][col] == piece && board[row - 1][col + 1] == piece && board[row - 2][col + 2] == piece
                        && board[row - 3][col + 3] == piece && board[row - 4][col + 4] == piece) {
                    return true;
                }
            }
        }

        return false;
    }

    private void updateBoardWithMove(final GameBoard gameBoard, final MoveRequest request) {
        String piece = getPlayerPiece(gameBoard, request.getPlayerId());
        int row = getFirstOpenRow(gameBoard, request.getMove());
        gameBoard.getBoard().getBoard()[row][request.getMove()] = piece;
        gameBoard.getBoard().incrementSteps();
        if (gameBoard.getState() != BOARD_STATE.GAME_IN_PROGRESS) {
            gameBoard.setState(BOARD_STATE.GAME_IN_PROGRESS);
        }

        if (isWinningMove(gameBoard, piece)) {
            gameBoard.setState(BOARD_STATE.GAME_WON);
            gameBoard.setWinner(request.getPlayerId());
        } else if (isTied(gameBoard)) {
            gameBoard.setState(BOARD_STATE.GAME_TIED);
        } else {
            gameBoard.setState(BOARD_STATE.GAME_IN_PROGRESS);
        }

        cacheServiceProvider.putBoard(gameBoard.getBoard().getBoardId(), gameBoard);
    }

    private int getFirstOpenRow(final GameBoard gameBoard, final int move) {
        for (int row = 0; row < ROW_SIZE; row++) {
            if (gameBoard.getBoard().getBoard()[row][move] == "_") {
                return row;
            }
        }
        throw new GamePlayException("Board is full on this column");
    }

    private String getPlayerPiece(final GameBoard gameBoard, final String playerId) {
        //Validate if this is user's turn
        if (gameBoard.getBoard().getSteps() % 2 == 0 && gameBoard.getPlayer1().getPlayerId().equals(playerId)) {
            return gameBoard.getPlayer1().getPiece();
        } else if (gameBoard.getBoard().getSteps() % 2 == 1 && gameBoard.getPlayer2().getPlayerId().equals(playerId)) {
            return gameBoard.getPlayer2().getPiece();
        } else {
            throw new GamePlayException("Not this player's turn");
        }
    }


    @Override
    public Board assignPlayerToBoard(final Player player) {
        GameBoard gameBoard = cacheServiceProvider.getBoardForNewPlayer();
        switch (gameBoard.getState()) {
            case INITIALIZED:
                gameBoard.setPlayer1(player);
                gameBoard.getPlayer1().setPiece("X");
                gameBoard.setState(BOARD_STATE.PLAYER_1_IN);
                break;
            case PLAYER_1_IN:
                gameBoard.setPlayer2(player);
                gameBoard.getPlayer2().setPiece("O");
                gameBoard.setState(BOARD_STATE.PLAYER_2_IN);
                break;
        }
        cacheServiceProvider.putBoard(gameBoard.getBoard().getBoardId(), gameBoard);
        return gameBoard.getBoard();

    }

    @Override
    public GamePlayResponse checkPlayerTurn(final GamePlayRequest request) {
        GamePlayResponse response = new GamePlayResponse();
        GameBoard gameBoard = getGameBoard(request.getBoardId());

        boolean isValidRequest = false;
        if (isBoardInPlay(gameBoard) && validatePlayerOnBoard(gameBoard, request.getPlayerId())) {
            isValidRequest = true;
        } else if (gameBoard.getWinner() != null) {
            response.setWinner(gameBoard.getWinner());
        } else {
            LOGGER.warn("Invalid User accessing the board");
            response.setErrorDetail(new ErrorDetail("User Not authorised to access board", null));
        }

        if (isValidRequest && bothPlayersAllocated(gameBoard.getPlayer1(), gameBoard.getPlayer2())) {
            Player player;
            switch (gameBoard.getBoard().getSteps() % 2) {
                case 0:
                    LOGGER.info("It's player1s turn");
                    player = gameBoard.getPlayer1();
                    break;
                case 1:
                    LOGGER.info("It's player2s turn");
                    player = gameBoard.getPlayer2();
                    break;
                default:
                    throw new RuntimeException("This will never happen");
            }
            if (player.getPlayerId().equals(request.getPlayerId())) {
                response.setPlayerTurn(true);
            }
        }
        // Map response
        response.setBoard(gameBoard.getBoard().getBoard());
        response.setBoardId(gameBoard.getBoard().getBoardId());
        response.setPlayerId(request.getPlayerId());
        response.setState(gameBoard.getState());
        return response;
    }

    @Override
    public void disconnectedPlayer(final GamePlayRequest request) {
        GameBoard gameBoard = getGameBoard(request.getBoardId());
        if (validatePlayerOnBoard(gameBoard, request.getPlayerId())) {
            switch (gameBoard.getState()) {

                case PLAYER_1_IN:
                    if (gameBoard.getPlayer1().getPlayerId().equals(request.getPlayerId())) {
                        LOGGER.info("The only player on the board has abandoned. Removing the board");
                        cacheServiceProvider.deleteBoard(gameBoard.getBoard().getBoardId());
                        break;
                    }
                case PLAYER_2_IN:
                case GAME_IN_PROGRESS:
                    if (gameBoard.getPlayer1().getPlayerId().equals(request.getPlayerId())) {
                        LOGGER.info("Player 1 has abandoned, declaring Player2 as winner");
                        gameBoard.setWinner(gameBoard.getPlayer2().getPlayerId());
                    } else if (gameBoard.getPlayer2().getPlayerId().equals(request.getPlayerId())) {
                        LOGGER.info("Player 2 has abandoned, declaring Player1 as winner");
                        gameBoard.setWinner(gameBoard.getPlayer1().getPlayerId());
                    }
                    gameBoard.setState(BOARD_STATE.GAME_WON);
                    cacheServiceProvider.putBoard(gameBoard.getBoard().getBoardId(), gameBoard);
                    break;
            }
        }

    }

    private GameBoard getGameBoard(String boardId) {
        Optional<GameBoard> optional = cacheServiceProvider.getBoardById(boardId);
        if (optional.isPresent()) {
            final GameBoard gameBoard = optional.get();
            return gameBoard;
        } else {
            LOGGER.error("Board not found");
            throw new GamePlayException("Board Not Found. Re-register player");
        }
    }

    private boolean bothPlayersAllocated(final Player player1, final Player player2) {
        return player1 != null &&
                player1.getPlayerId() != null &&
                !player1.getPlayerId().isEmpty() &&
                player2 != null &&
                player2.getPlayerId() != null &&
                !player2.getPlayerId().isEmpty();
    }

    private boolean isBoardInPlay(final GameBoard gameBoard) {
        if (gameBoard.getState() == BOARD_STATE.PLAYER_1_IN ||
                gameBoard.getState() == BOARD_STATE.PLAYER_2_IN ||
                gameBoard.getState() == BOARD_STATE.GAME_IN_PROGRESS ||
                gameBoard.getState() == BOARD_STATE.INITIALIZED) {
            return true;
        } else {
            LOGGER.info("Game Over");
            return false;
        }
    }

    private boolean validatePlayerOnBoard(final GameBoard gameBoard, final String playerId) {
        return gameBoard.getPlayer1().getPlayerId().equals(playerId) ||
                gameBoard.getPlayer2().getPlayerId().equals(playerId);
    }
}
