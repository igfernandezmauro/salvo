package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.service.GameService;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GamePlayerServiceImplementation gamePlayerServiceImplementation;

    @RequestMapping("/games")
    public Map<String, Object> getGames(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("games", gameService.getGame().stream().map(Game::getInfo).collect(toList()));
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
}
