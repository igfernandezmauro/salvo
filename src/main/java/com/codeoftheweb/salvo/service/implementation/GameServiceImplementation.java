package com.codeoftheweb.salvo.service.implementation;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GameServiceImplementation implements GameService {

    @Autowired
    GameRepository gameRepository;

    @Override
    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public List<Game> getGame() {
        return gameRepository.findAll();
    }

    @Override
    public Game updateGame(Game game) {
        return null;
    }

    @Override
    public boolean deleteGame(Long id) {
        return false;
    }

    @Override
    public Game findGameById(Long id) {
        return gameRepository.findById(id).get();
    }
}
