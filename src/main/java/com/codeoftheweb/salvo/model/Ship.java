package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String type;
    private boolean sunk = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "location")
    private List<String> shipLocations = new ArrayList<>();

    public Ship(){
    }

    public Ship(String _shipType, GamePlayer _gamePlayer, List<String> _locations){
        this.type = _shipType;
        _gamePlayer.addShip(this);
        this.shipLocations = _locations;
    }

    public long getId() {
        return this.id;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String _shipType){
        this.type = _shipType;
    }

    public boolean isSunk() {
        return this.sunk;
    }

    public void setSunk(boolean _sunk) {
        this.sunk = _sunk;
    }

    public GamePlayer getGamePlayer(){
        return this.gamePlayer;
    }

    public void setGamePlayer(GamePlayer _gamePlayer){
        this.gamePlayer = _gamePlayer;
    }

    public List<String> getShipLocations(){
        return this.shipLocations;
    }

    public void addLocation(String _location){
        this.shipLocations.add(_location);
    }

    public Map<String, Object> getInfo(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", getType());
        dto.put("locations", getShipLocations());
        dto.put("sunk", isSunk());
        return dto;
    }
}
