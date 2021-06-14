package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.GameServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.ShipServiceImplementation;
import com.codeoftheweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class GamesController {

    @Autowired
    GameServiceImplementation gameServiceImplementation;

    @Autowired
    PlayerServiceImplementation playerServiceImplementation;

    @Autowired
    GamePlayerServiceImplementation gamePlayerServiceImplementation;

    @Autowired
    ShipServiceImplementation shipServiceImplementation;

    public GamesController(){}

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getGames(Authentication auth){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if(!Util.isGuest(auth)){
            dto.put("player", getAuthenticatedPlayer(auth).getInfo());
        }
        else {
            dto.put("player", "Guest");
        }
        dto.put("games", gameServiceImplementation.getGame().stream().map(Game::getInfo).collect(toList()));
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication auth){
        if(!Util.isGuest(auth)){
            Player player = getAuthenticatedPlayer(auth);
            Game newGame = gameServiceImplementation.saveGame(new Game(new Date()));
            GamePlayer newGamePlayer = gamePlayerServiceImplementation.saveGamePlayer(new GamePlayer(newGame, player));
            return new ResponseEntity<>(Util.makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.GET)
    public Map<String, Object> getPlayersInGame(@PathVariable long gameId){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Game game = gameServiceImplementation.findGameById(gameId);
        dto.put("players", game.getPlayers().stream().map(Player::getInfo).collect(toList()));
        return dto;
    }

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameId, Authentication auth){
        if(!Util.isGuest(auth)){
            Game game = gameServiceImplementation.findGameById(gameId);
            if(game != null){
                if(!Util.isGameFull(game)){
                    Player player = getAuthenticatedPlayer(auth);
                    if(!game.getPlayers().contains(player)){
                        GamePlayer newGamePlayer = gamePlayerServiceImplementation.saveGamePlayer(new GamePlayer(game, player));
                        return new ResponseEntity<>(Util.makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
                    }
                    return new ResponseEntity<>(Util.makeMap("error", "Player already in game"), HttpStatus.FORBIDDEN);
                }
                return new ResponseEntity<>(Util.makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(Util.makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getShipsGameplayer(@PathVariable long gamePlayerId, Authentication auth){
        if(!Util.isGuest(auth)){
            GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
            if(gp != null){
                if(gp.getPlayer().equals(getAuthenticatedPlayer(auth))){
                    Map<String, Object> dto = new LinkedHashMap<String, Object>();
                    dto.put("ships", gp.getShips().stream().map(Ship::getInfo).collect(toList()));
                    return new ResponseEntity<>(dto, HttpStatus.OK);
                }
                return new ResponseEntity<>(Util.makeMap("error", "Can't see other player's information"), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(Util.makeMap("error", "Gameplayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships,
                                                           Authentication auth){
        if(!Util.isGuest(auth)){
            GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
            if(gp != null){
                if(gp.getPlayer().equals(getAuthenticatedPlayer(auth))){
                    if(gp.getShips().size() == 0){
                        for(int i = 0; i < ships.size(); i++){
                            Ship newShip = ships.get(i);
                            gp.addShip(newShip);
                            shipServiceImplementation.saveShip(newShip);
                        }
                        return new ResponseEntity<>(Util.makeMap("OK", "Ships placed"), HttpStatus.CREATED);
                    }
                    return new ResponseEntity<>(Util.makeMap("error", "Ships already placed"), HttpStatus.FORBIDDEN);
                }
                return new ResponseEntity<>(Util.makeMap("error", "Can't create other player's ships"), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(Util.makeMap("error", "Gameplayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    /*@RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSalvoesGameplayer(@PathVariable long gamePlayerId, Authentication auth){

    }*/

    private Player getAuthenticatedPlayer(Authentication auth){
        return playerServiceImplementation.findPlayerByUsername(auth.getName());
    }
}
