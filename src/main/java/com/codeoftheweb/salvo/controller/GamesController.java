package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.service.implementation.*;
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

    @Autowired
    SalvoServiceImplementation salvoServiceImplementation;

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
                        for(Ship ship : ships){
                            if(validateShipLength(ship)){
                                gp.addShip(ship);
                                shipServiceImplementation.saveShip(ship);
                            }
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

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getSalvoesGameplayer(@PathVariable long gamePlayerId, Authentication auth){
        if(!Util.isGuest(auth)){
            GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
            if(gp != null){
                if(gp.getPlayer().equals(getAuthenticatedPlayer(auth))){
                    Map<String, Object> dto = new LinkedHashMap<String, Object>();
                    dto.put("salvoes", gp.getSalvoes().stream().map(Salvo::getInfo).collect(toList()));
                    return new ResponseEntity<>(dto, HttpStatus.OK);
                }
                return new ResponseEntity<>(Util.makeMap("error", "Can't see other player's information"), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(Util.makeMap("error", "Gameplayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createSalvo(@PathVariable long gamePlayerId, @RequestBody Salvo salvo,
                                                           Authentication auth){
        if(!Util.isGuest(auth)){
            GamePlayer gp = gamePlayerServiceImplementation.findGamePlayerById(gamePlayerId);
            if(gp != null){
                if(gp.getPlayer().equals(getAuthenticatedPlayer(auth))){
                    int selfSalvoes = gp.getSalvoes().size();
                    int opponentSalvoes = gp.getGame().getGamePlayers().stream().
                            map(gamePlayer -> gamePlayer.getPlayer() != gp.getPlayer()).collect(toList()).size();
                    if(selfSalvoes <= opponentSalvoes){
                        int shotsAmmount = salvo.getSalvoLocations().size();
                        if(shotsAmmount >= 1 && shotsAmmount <= 5){
                            int salvoesFired = gp.getSalvoes().size();
                            salvo.setTurn(salvoesFired+1);
                            gp.addSalvo(salvo);
                            salvoServiceImplementation.saveSalvo(salvo);
                            return new ResponseEntity<>(Util.makeMap("OK", "Salvo fired"), HttpStatus.CREATED);
                        }
                        return new ResponseEntity<>(Util.makeMap("error", "Wrong shots ammount for Salvo"), HttpStatus.UNAUTHORIZED);
                    }
                    return new ResponseEntity<>(Util.makeMap("error", "Can't fire salvo yet"), HttpStatus.UNAUTHORIZED);
                }
                return new ResponseEntity<>(Util.makeMap("error", "Can't fire other player's salvoes"), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(Util.makeMap("error", "Gameplayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(Util.makeMap("error", "User not logged in"), HttpStatus.UNAUTHORIZED);
    }

    private Player getAuthenticatedPlayer(Authentication auth){
        return playerServiceImplementation.findPlayerByUsername(auth.getName());
    }

    private boolean validateShipLength(Ship ship){
        String shipType = ship.getType();
        int length = ship.getShipLocations().size();
        switch (shipType){
            case "carrier":
                return length == 5;
            case "battleship":
                return length == 4;
            case "submarine":
            case "destroyer":
                return length == 3;
            case "patrolboat":
                return length == 2;
            default:
                return false;
        }
    }
}
