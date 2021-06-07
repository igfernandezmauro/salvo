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
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", getId());
        dto.put("created", getCreationDate());
        dto.put("gamePlayers", getGamePlayers().stream().map(GamePlayer::getPlayerInfo).collect(toList()));
        dto.put("ships", gp.getShips().stream().map(Ship::getInfo).collect(toList()));
        dto.put("salvoes", getGamePlayers().stream().map(GamePlayer::getSalvoesInfo).flatMap(Collection::stream).collect(toList()));
        return dto;
    }
}
