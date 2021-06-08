package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.service.PlayerService;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.GameServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    private PlayerService playerService;

    public SalvoController() {
    }

    @RequestMapping("/games")
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

    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGameViews(@PathVariable long nn){
        GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(nn);
        if(gp != null){
            return gp.getGame().getInfo(gp);
        }
        return null;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String username, @RequestParam String password){
        if(username.isEmpty() || password.isEmpty()){
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        Player player = playerService.findPlayerByUsername(username);
        if(player != null){
            return new ResponseEntity<>(makeMap("error", "Username already in use"), HttpStatus.FORBIDDEN);
        }
        Player newPlayer = playerService.savePlayer(new Player(username, password));
        return new ResponseEntity<>(makeMap("name", newPlayer.getUsername()), HttpStatus.CREATED);
    }

    private Player getAuthenticatedPlayer(Authentication auth){
        return playerService.findPlayerByUsername(auth.getName());
    }

    private boolean isGuest(Authentication auth){
        return auth == null || auth instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
