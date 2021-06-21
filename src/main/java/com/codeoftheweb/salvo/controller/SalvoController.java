package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Score;
import com.codeoftheweb.salvo.model.Ship;
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
                if(Util.getRemainingShips(gp) == 0 || Util.getRemainingShips(opponent) == 0){
                    if(opponent.getId() != 0){
                        if(!checkScoreExists(gp) && !checkScoreExists(opponent)){
                            Score playerScore = new Score();
                            playerScore.setGame(gp.getGame());
                            playerScore.setPlayer(gp.getPlayer());
                            playerScore.setFinishDate(new Date());
                            if(Util.getRemainingShips(gp) == 0){
                                if(Util.getRemainingShips(opponent) == 0){
                                    playerScore.setScore(0.5);
                                }
                                else{
                                    playerScore.setScore(0);
                                }
                            }
                            else{
                                playerScore.setScore(1);
                            }
                            scoreServiceImplementation.saveScore(playerScore);
                        }
                    }
                }
                return new ResponseEntity<>(gp.getGame().getInfo(gp), HttpStatus.OK);
            }
            return new ResponseEntity<>(Util.makeMap("error", "Can't see other player's information"), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(Util.makeMap("error", "GamePlayer doesn't exist"), HttpStatus.BAD_REQUEST);
    }

    private Player getAuthenticatedPlayer(Authentication auth){
        return playerServiceImplementation.findPlayerByUsername(auth.getName());
    }

    private boolean checkScoreExists(GamePlayer gp){
        List<Score> gameScores = scoreServiceImplementation.findScoresByGameId(gp.getGame().getId());
        for(Score score : gameScores){
            if(score.getPlayer().equals(gp.getPlayer())) return true;
        }
        return false;
    }
}
