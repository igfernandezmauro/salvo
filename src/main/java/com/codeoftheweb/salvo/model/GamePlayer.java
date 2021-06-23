package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.*;
import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvoes = new HashSet<>();

    public GamePlayer(){
    }

    public GamePlayer(Game _game, Player _player){
        this.game = _game;
        this.player = _player;
        this.joinDate = new Date();
    }

    public long getId() {
        return this.id;
    }

    public Date getJoinDate() {
        return this.joinDate;
    }

    public void setJoinDate(Date _joinDate) {
        this.joinDate = _joinDate;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game _game) {
        this.game = _game;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player _player) {
        this.player = _player;
    }

    public Set<Ship> getShips(){
        return this.ships;
    }

    public void addShip(Ship _ship){
        _ship.setGamePlayer(this);
        this.ships.add(_ship);
    }

    public Set<Salvo> getSalvoes(){
        return this.salvoes;
    }

    public void addSalvo(Salvo _salvo){
        _salvo.setGamePlayer(this);
        this.salvoes.add(_salvo);
    }

    public Map<String, Object> getPlayerInfo(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", getId());
        dto.put("player", getPlayer().getInfo());
        dto.put("joinDate", getJoinDate());
        return dto;
    }

    public Map<String, Object> getPlayerInfoGames(){
        Score score = getPlayer().getScore(getGame());
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", getId());
        dto.put("player", getPlayer().getInfo());
        if(score != null){
            dto.put("score", score.getScore());
        }
        else{
            dto.put("score", 0);
        }
        return dto;
    }

    public List<Object> getSalvoesInfo(){
        return this.getSalvoes().stream().map(Salvo::getInfo).collect(toList());
    }

    public int getRemainingShips(){
        int sunkShips = 0;
        for(Ship ship : getShips()){
            if(ship.isSunk()) sunkShips++;
        }
        return (getShips().size() - sunkShips);
    }

}
