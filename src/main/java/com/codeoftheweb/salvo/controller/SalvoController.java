package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.GameServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.ShipServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameServiceImplementation gameServiceImplementation;

    @Autowired
    private GamePlayerServiceImplementation gamePlayerServiceImplementation;

    @Autowired
    private PlayerServiceImplementation playerServiceImplementation;

    @Autowired
    private ShipServiceImplementation shipServiceImplementation;

    public SalvoController() {
    }

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getGames(Authentication auth){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if(!isGuest(auth)){
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
        if(!isGuest(auth)){
            Player player = getAuthenticatedPlayer(auth);
            Game newGame = gameServiceImplementation.saveGame(new Game(new Date()));
            GamePlayer newGamePlayer = gamePlayerServiceImplementation.saveGamePlayer(new GamePlayer(newGame, player));
            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
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
        if(!isGuest(auth)){
            Game game = gameServiceImplementation.findGameById(gameId);
            if(game != null){
                if(!isGameFull(game)){
                    Player player = getAuthenticatedPlayer(auth);
                    if(!game.getPlayers().contains(player)){
                        GamePlayer newGamePlayer = gamePlayerServiceImplementation.saveGamePlayer(new GamePlayer(game, player));
                        return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);
                    }
                    return new ResponseEntity<>(makeMap("error", "Player already in game"), HttpStatus.FORBIDDEN);
                }
                return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getShipsGameplayer(@PathVariable long gamePlayerId, Authentication auth){
        if(!isGuest(auth)){
            GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
            if(gp != null){
                if(gp.getPlayer().equals(getAuthenticatedPlayer(auth))){
                    Map<String, Object> dto = new LinkedHashMap<String, Object>();
                    dto.put("ships", gp.getShips().stream().map(Ship::getInfo).collect(toList()));
                    return new ResponseEntity<>(dto, HttpStatus.OK);
                }
                return new ResponseEntity<>(makeMap("error", "Can't see other player's information"), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(makeMap("error", "Gameplayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships,
                                                           Authentication auth){
        if(!isGuest(auth)){
            GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
            if(gp != null){
                if(gp.getPlayer().equals(getAuthenticatedPlayer(auth))){
                    if(gp.getShips().size() == 0){
                        for(int i = 0; i < ships.size(); i++){
                            Ship newShip = ships.get(i);
                            gp.addShip(newShip);
                            shipServiceImplementation.saveShip(newShip);
                        }
                        return new ResponseEntity<>(makeMap("OK", "Ships placed"), HttpStatus.CREATED);
                    }
                    return new ResponseEntity<>(makeMap("error", "Ships already placed"), HttpStatus.FORBIDDEN);
                }
                return new ResponseEntity<>(makeMap("error", "Can't create other player's ships"), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(makeMap("error", "Gameplayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameViews(@PathVariable long gamePlayerId, Authentication auth){
        GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
        Player player = getAuthenticatedPlayer(auth);
        if(gp != null){
            if(gp.getPlayer().equals(player)) {
                return new ResponseEntity<>(gp.getGame().getInfo(gp), HttpStatus.OK);
            }
            return new ResponseEntity<>(makeMap("error", "Can't see other player's information"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(makeMap("error", "GamePlayer doesn't exist"), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String email, @RequestParam String password){
        if(email.isEmpty() || password.isEmpty()){
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        Player player = playerServiceImplementation.findPlayerByUsername(email);
        if(player != null){
            return new ResponseEntity<>(makeMap("error", "Username already in use"), HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerServiceImplementation.savePlayer(new Player(email, password));
        return new ResponseEntity<>(makeMap("name", newPlayer.getUsername()), HttpStatus.CREATED);
    }

    private Player getAuthenticatedPlayer(Authentication auth){
        return playerServiceImplementation.findPlayerByUsername(auth.getName());
    }

    private boolean isGuest(Authentication auth){
        return auth == null || auth instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGameFull(Game game){
        return !(game.getGamePlayers().size() < 2);
    }
}
