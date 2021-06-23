package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplementation;
import com.codeoftheweb.salvo.service.implementation.ScoreServiceImplementation;
import com.codeoftheweb.salvo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GamePlayerServiceImplementation gamePlayerServiceImplementation;

    @Autowired
    private PlayerServiceImplementation playerServiceImplementation;

    @Autowired
    private ScoreServiceImplementation scoreServiceImplementation;

    public SalvoController() {
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameViews(@PathVariable long gamePlayerId, Authentication auth){
        GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
        GamePlayer opponent = Util.getOpponent(gp);
        Player player = getAuthenticatedPlayer(auth);
        if(gp != null){
            if(gp.getPlayer().equals(player)) {
                Map<String, Object> finalDTO = gp.getGame().getInfo(gp);
                if(gp.getShips().size() != 0){
                   if(gp.getGame().getGamePlayers().size() != 1){
                       if(opponent.getShips().size() != 0){
                           if(gp.getRemainingShips() == 0 || opponent.getRemainingShips() == 0){
                               if(gp.getSalvoes().size() == opponent.getSalvoes().size()){
                                   if(!checkScoreExists(gp)){
                                       Score playerScore = new Score();
                                       playerScore.setGame(gp.getGame());
                                       playerScore.setPlayer(gp.getPlayer());
                                       playerScore.setFinishDate(new Date());
                                       if(gp.getRemainingShips() == 0){
                                           if(opponent.getRemainingShips() == 0){
                                               playerScore.setScore(0.5);
                                           }
                                           else{
                                               playerScore.setScore(0);
                                           }
                                       }
                                       else{
                                           playerScore.setScore(1);
                                       }
                                       System.out.println(playerScore.getInfo());
                                       scoreServiceImplementation.saveScore(playerScore);
                                   }
                               }
                           }
                       }
                   }
                }
                return new ResponseEntity<>(finalDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>(Util.makeMap("error", "Can't see other player's information"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(Util.makeMap("error", "GamePlayer doesn't exist"), HttpStatus.BAD_REQUEST);
    }

    private Player getAuthenticatedPlayer(Authentication auth){
        return playerServiceImplementation.findPlayerByUsername(auth.getName());
    }

    private boolean checkScoreExists(GamePlayer gamePlayer){
        List<Score> gameScores = scoreServiceImplementation.findScoresByGameId(gamePlayer.getGame().getId());
        List<Score> gamePlayerScores = gameScores.stream().filter(s -> s.getPlayer() == gamePlayer.getPlayer()).collect(Collectors.toList());
        return (gamePlayerScores.size() == 1);
    }
}
