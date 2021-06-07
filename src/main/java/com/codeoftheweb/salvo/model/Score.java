package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private double score;
    private Date finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    public Score(){
    }

    public Score(double _score, Date _finishDate, Game _game, Player _player){
        this.score = _score;
        this.finishDate = _finishDate;
        this.game = _game;
        this.player = _player;
    }

    public long getId(){
        return this.id;
    }

    public double getScore(){
        return this.score;
    }

    public void setScore(double _score){
        this.score = _score;
    }

    public Date getFinishDate(){
        return this.finishDate;
    }

    public void setFinishDate(Date _finishDate){
        this.finishDate = _finishDate;
    }

    public Game getGame(){
        return this.game;
    }

    public void setGame(Game _game){
        this.game = _game;
    }

    public Player getPlayer(){
        return this.player;
    }

    public void setPlayer(Player _player){
        this.player = _player;
    }

    public Map<String, Object> getInfo(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("score", getScore());
        dto.put("player", getPlayer().getId());
        return dto;
    }

}
