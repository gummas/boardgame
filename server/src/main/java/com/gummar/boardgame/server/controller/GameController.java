package com.gummar.boardgame.server.controller;

import com.gummar.boardgame.server.model.Board;
import com.gummar.boardgame.server.model.GamePlayRequest;
import com.gummar.boardgame.server.model.GamePlayResponse;
import com.gummar.boardgame.server.model.MoveRequest;
import com.gummar.boardgame.server.model.Player;
import com.gummar.boardgame.server.service.GamePlayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * The Game controller.
 *
 * @author Raj Gumma
 */
@RestController
@RequestMapping(path = "/connectfive")
class GameController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final GamePlayService service;

    /**
     * Instantiates a new Game controller.
     *
     * @param service the service
     */
    @Autowired
    public GameController(final GamePlayService service) {
        this.service = service;
    }

    /**
     * Register player response entity.
     *
     * @param playerId           the player id
     * @param player             the player
     * @param httpServletRequest the http servlet request
     * @return the response entity
     */
    @PostMapping(value = "/register/{playerId}", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity<?> registerPlayer(@PathVariable(value = "playerId") final String playerId,
                                     @RequestBody final Player player,
                                     final HttpServletRequest httpServletRequest) {
        LOGGER.info("In register User to register {}", player);
        Board board = service.assignPlayerToBoard(player);

        return ResponseEntity.ok(board);
    }

    /**
     * Check turn response entity.
     *
     * @param request            the request
     * @param httpServletRequest the http servlet request
     * @return the response entity
     */
    @GetMapping(value = "/checkturn", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity<?> checkTurn(@RequestBody final GamePlayRequest request,
                                final HttpServletRequest httpServletRequest) {
        LOGGER.info("Check if it's this player's turn {}", request.getPlayerId());
        GamePlayResponse response = service.checkPlayerTurn(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Move response entity.
     *
     * @param request            the request
     * @param httpServletRequest the http servlet request
     * @return the response entity
     */
    @PostMapping(value = "/move", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity<?> move(@RequestBody final MoveRequest request,
                           final HttpServletRequest httpServletRequest) {
        LOGGER.info("ProcessMove {}", request);
        GamePlayResponse response = service.processMove(request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "disconnect", consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    ResponseEntity<?> disconnect(@RequestBody final GamePlayRequest request, final HttpServletRequest httpServletRequest) {
        service.disconnectedPlayer(request);
        return ResponseEntity.ok(null);
    }
}
