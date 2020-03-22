package com.gummar.boardgame.server.service;

import com.gummar.boardgame.server.model.Board;
import com.gummar.boardgame.server.model.GamePlayRequest;
import com.gummar.boardgame.server.model.GamePlayResponse;
import com.gummar.boardgame.server.model.MoveRequest;
import com.gummar.boardgame.server.model.Player;

/**
 * The interface Game play service.
 *
 * @author Raj Gumma
 */
public interface GamePlayService {
    /**
     * Process move game play response.
     *
     * @param request the request
     * @return the game play response
     */
    GamePlayResponse processMove(MoveRequest request);

    /**
     * Assign player to board board.
     *
     * @param player the player
     * @return the board
     */
    Board assignPlayerToBoard(Player player);

    /**
     * Check player turn game play response.
     *
     * @param request the request
     * @return the game play response
     */
    GamePlayResponse checkPlayerTurn(GamePlayRequest request);

    void disconnectedPlayer(GamePlayRequest request);
}
