package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplementation;
import com.codeoftheweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GamePlayerServiceImplementation gamePlayerServiceImplementation;

    @Autowired
    private PlayerServiceImplementation playerServiceImplementation;

    public SalvoController() {
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameViews(@PathVariable long gamePlayerId, Authentication auth){
        GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
        Player player = getAuthenticatedPlayer(auth);
        if(gp != null){
            if(gp.getPlayer().equals(player)) {
                return new ResponseEntity<>(gp.getGame().getInfo(gp), HttpStatus.OK);
            }
            return new ResponseEntity<>(Util.makeMap("error", "Can't see other player's information"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(Util.makeMap("error", "GamePlayer doesn't exist"), HttpStatus.BAD_REQUEST);
    }

    private Player getAuthenticatedPlayer(Authentication auth){
        return playerServiceImplementation.findPlayerByUsername(auth.getName());
    }
}
