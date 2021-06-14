package com.codeoftheweb.salvo.util;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.service.PlayerService;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

public class Util {

    public static boolean isGuest(Authentication auth){
        return auth == null || auth instanceof AnonymousAuthenticationToken;
    }

    public static Map<String, Object> makeMap(String key, Object value){
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static boolean isGameFull(Game game){
        return !(game.getGamePlayers().size() < 2);
    }
}
