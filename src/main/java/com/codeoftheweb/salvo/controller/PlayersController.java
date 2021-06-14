package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlayersController {

    @Autowired
    PlayerServiceImplementation playerServiceImplementation;

    public PlayersController(){}

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

    private Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
