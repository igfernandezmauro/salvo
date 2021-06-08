package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String username;
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> games;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores;

    public Player(){
    }

    public Player(String _userName, String _password){
        this.username = _userName;
        this.password = passwordEncoder().encode(_password);
    }

    public long getId() {
        return this.id;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String _userName){
        this.username = _userName;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String _password){
        this.password = _password;
    }

    public void addGame(GamePlayer _gamePlayer){
        games.add(_gamePlayer);
    }

    public List<Game> getGames(){
        return games.stream().map(GamePlayer::getGame).collect(toList());
    }

    public void addScore(Score _score){
        scores.add(_score);
    }

    public List<Score> getScores(){
        return new ArrayList<>(this.scores);
    }

    public Score getScore(Game game){
        Optional<Score> gameScore = getScores().stream().filter(s -> s.getGame().equals(game)).findFirst();
        if(gameScore.isPresent()){
            return gameScore.get();
        }
        else{
            return null;
        }
    }

    public Map<String, Object> getInfo(){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", getId());
        dto.put("email", getUsername());
        return dto;
    }
}
