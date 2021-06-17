package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import static java.util.stream.Collectors.toList;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> players;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores;

    public Game(){
    }

    public Game(Date _creationDate){
        this.creationDate = _creationDate;
    }

    public long getId() {
        return this.id;
    }

    public Date getCreationDate(){
        return this.creationDate;
    }

    public void setCreationDate(Date _creationDate){
        this.creationDate = _creationDate;
    }

    public void addPlayer(GamePlayer _gamePlayer){
        players.add(_gamePlayer);
    }

    public List<Player> getPlayers(){
        return players.stream().map(GamePlayer::getPlayer).collect(toList());
    }

    public List<GamePlayer> getGamePlayers(){
        return new ArrayList<>(this.players);
    }

    public void addScore(Score _score){
        scores.add(_score);
    }

    public List<Score> getScores(){
        return new ArrayList<>(this.scores);
    }

    public Map<String, Object> getInfo(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", getId());
        dto.put("created", getCreationDate());
        dto.put("gamePlayers", getGamePlayers().stream().map(GamePlayer::getPlayerInfoGames).collect(toList()));
        dto.put("scores", getScores().stream().map(Score::getInfo).collect(toList()));
        return dto;
    }

    public Map<String, Object> getInfo(GamePlayer gp){
        GamePlayer opponent = players.stream().filter(gamePlayer -> gamePlayer.getPlayer() != gp.getPlayer()).
                findFirst().get();
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Map<String, Object> hits = new LinkedHashMap<String, Object>();
        hits.put("self", createHits(gp)); //Self: cuando me pegan a mi
        hits.put("opponent", createHits(opponent)); //Opponent; Cuando yo pego
        dto.put("id", getId());
        dto.put("created", getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", getGamePlayers().stream().map(GamePlayer::getPlayerInfo).collect(toList()));
        dto.put("ships", gp.getShips().stream().map(Ship::getInfo).collect(toList()));
        dto.put("salvoes", getGamePlayers().stream().map(GamePlayer::getSalvoesInfo).flatMap(Collection::stream).collect(toList()));
        dto.put("hits", hits);
        return dto;
    }

    private List<Object> createHits(GamePlayer gamePlayer){
        Set<Ship> selfShips = gamePlayer.getShips();
        List<Salvo> opponentSalvoes = getGamePlayers().stream().filter(gp -> gp.getPlayer() != gamePlayer.getPlayer()).
                findFirst().get().getSalvoes().stream().sorted(Comparator.comparingInt(Salvo::getTurn)).collect(toList());
        List<Object> hits = new LinkedList<>();
        int carrier = 0, battleship = 0, submarine = 0, destroyer = 0, patrolboat = 0;
        for(Salvo salvo : opponentSalvoes){
            int carrierHits = 0, battleshipHits = 0, submarineHits = 0, destroyerHits = 0, patrolboatHits = 0;
            Map<String, Object> salvoDTO = new LinkedHashMap<>();
            List<String> hitLocations = calculateHits(salvo, selfShips);
            salvoDTO.put("turn", salvo.getTurn());
            salvoDTO.put("hitLocations", hitLocations);
            for(String hit : hitLocations){
                for(Ship ship : selfShips){
                    for(String shipLocation : ship.getShipLocations()){
                        if(hit.equals(shipLocation)){
                            switch (ship.getType()){
                                case "carrier":
                                    carrier++;
                                    carrierHits++;
                                    break;
                                case "battleship":
                                    battleship++;
                                    battleshipHits++;
                                    break;
                                case "submarine":
                                    submarine++;
                                    submarineHits++;
                                    break;
                                case "destroyer":
                                    destroyer++;
                                    destroyerHits++;
                                    break;
                                case "patrolboat":
                                    patrolboat++;
                                    patrolboatHits++;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
            Map<String, Object> damagesDTO = new LinkedHashMap<>();
            damagesDTO.put("carrierHits", carrierHits);
            damagesDTO.put("battleshipHits", battleshipHits);
            damagesDTO.put("submarineHits", submarineHits);
            damagesDTO.put("destroyerHits", destroyerHits);
            damagesDTO.put("patrolboatHits", patrolboatHits);
            damagesDTO.put("carrier", carrier);
            damagesDTO.put("battleship", battleship);
            damagesDTO.put("submarine", submarine);
            damagesDTO.put("destroyer", destroyer);
            damagesDTO.put("patrolboat", patrolboat);
            salvoDTO.put("damages", damagesDTO);
            salvoDTO.put("missed", (salvo.getSalvoLocations().size() - hitLocations.size()));
            hits.add(salvoDTO);
        }
        return hits;
    }

    private List<String> calculateHits(Salvo salvo, Set<Ship> ships){
        List<String> hitLocations = new LinkedList<>();
        List<String> salvoLocations = salvo.getSalvoLocations();
        for(String salvoLocation : salvoLocations){
            for(Ship ship : ships){
                for(String shipLocation : ship.getShipLocations()){
                    if(salvoLocation.equals(shipLocation)) hitLocations.add(salvoLocation);
                }
            }
        }
        return hitLocations;
    }
}
