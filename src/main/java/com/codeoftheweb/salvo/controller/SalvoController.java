package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.GameServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @RequestMapping(path = "/games/{nn}/players", method = RequestMethod.GET)
    public Map<String, Object> getPlayersInGame(@PathVariable long nn){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Game game = gameServiceImplementation.findGameById(nn);
        dto.put("players", game.getPlayers().stream().map(Player::getInfo).collect(toList()));
        return dto;
    }

    @RequestMapping(path = "/game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long nn, Authentication auth){
        if(!isGuest(auth)){
            Game game = gameServiceImplementation.findGameById(nn);
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

    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGameViews(@PathVariable long nn, Authentication auth){
        GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(nn);
        Player player = getAuthenticatedPlayer(auth);
        if(gp != null){
            if(gp.getPlayer().equals(player)) {
                return gp.getGame().getInfo(gp);
            }
        }
        return null;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String username, @RequestParam String password){
        if(username.isEmpty() || password.isEmpty()){
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        Player player = playerServiceImplementation.findPlayerByUsername(username);
        if(player != null){
            return new ResponseEntity<>(makeMap("error", "Username already in use"), HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerServiceImplementation.savePlayer(new Player(username, password));
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
