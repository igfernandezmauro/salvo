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
    private String shipType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "location")
    private List<String> locations = new ArrayList<>();

    public Ship(){
    }

    public Ship(String _shipType, GamePlayer _gamePlayer, List<String> _locations){
        this.shipType = _shipType;
        _gamePlayer.addShip(this);
        this.locations = _locations;
    }

    public long getId() {
        return this.id;
    }

    public String getShipType(){
        return this.shipType;
    }

    public void setShipType(String _shipType){
        this.shipType = _shipType;
    }

    public GamePlayer getGamePlayer(){
        return this.gamePlayer;
    }

    public void setGamePlayer(GamePlayer _gamePlayer){
        this.gamePlayer = _gamePlayer;
    }

    public List<String> getLocations(){
        return this.locations;
    }

    public void addLocation(String _location){
        this.locations.add(_location);
    }

    public Map<String, Object> getInfo(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", getShipType());
        dto.put("locations", getLocations());
        return dto;
    }
}
