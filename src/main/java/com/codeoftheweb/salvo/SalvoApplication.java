package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) { SpringApplication.run(SalvoApplication.class, args); }

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
									  SalvoRepository salvoRepository, ScoreRepository scoreRepository){
		return(args) -> {
			//Create Players
			Player player1 = new Player("j.bauer@ctu.gov","24");
			Player player2 = new Player("c.obrian@ctu.gov", "42");
			Player player3 = new Player("kim_bauer@gmail.com", "kb");
			Player player4 = new Player("t.almeida@ctu.gov", "mole");

			//Create Games
			Date actualDate = new Date();
			Game game1 = new Game(actualDate);

			actualDate = Date.from(actualDate.toInstant().plusSeconds(3600));
			Game game2 = new Game(actualDate);

			actualDate = Date.from(actualDate.toInstant().plusSeconds(3600));
			Game game3 = new Game(actualDate);

			actualDate = Date.from(actualDate.toInstant().plusSeconds(3600));
			Game game4 = new Game(actualDate);

			actualDate = Date.from(actualDate.toInstant().plusSeconds(3600));
			Game game5 = new Game(actualDate);

			actualDate = Date.from(actualDate.toInstant().plusSeconds(3600));
			Game game6 = new Game(actualDate);

			actualDate = Date.from(actualDate.toInstant().plusSeconds(3600));
			Game game7 = new Game(actualDate);

			actualDate = Date.from(actualDate.toInstant().plusSeconds(3600));
			Game game8 = new Game(actualDate);

			//Join Players and Games
			GamePlayer gp1 = new GamePlayer(game1, player1);
			GamePlayer gp2 = new GamePlayer(game1, player2);
			GamePlayer gp3 = new GamePlayer(game2, player1);
			GamePlayer gp4 = new GamePlayer(game2, player2);
			GamePlayer gp5 = new GamePlayer(game3, player2);
			GamePlayer gp6 = new GamePlayer(game3, player4);
			GamePlayer gp7 = new GamePlayer(game4, player2);
			GamePlayer gp8 = new GamePlayer(game4, player1);
			GamePlayer gp9 = new GamePlayer(game5, player4);
			GamePlayer gp10 = new GamePlayer(game5, player1);
			GamePlayer gp11 = new GamePlayer(game6, player3);
			GamePlayer gp12 = new GamePlayer(game7, player4);
			GamePlayer gp13 = new GamePlayer(game8, player3);
			GamePlayer gp14 = new GamePlayer(game8, player4);

			//Create Ships
			Ship s1 = new Ship("destroyer", gp1, Arrays.asList("H2", "H3", "H4"));
			Ship s2 = new Ship("submarine", gp1, Arrays.asList("E1", "F1", "G1"));
			Ship s3 = new Ship("patrolboat", gp1, Arrays.asList("B4", "B5"));
			Ship s4 = new Ship("destroyer", gp2, Arrays.asList("B5", "C5", "D5"));
			Ship s5 = new Ship("patrolboat", gp2, Arrays.asList("F1", "F2"));
			Ship s6 = new Ship("destroyer", gp3, Arrays.asList("B5", "C5", "D5"));
			Ship s7 = new Ship("patrolboat", gp3, Arrays.asList("C6", "C7"));
			Ship s8 = new Ship("submarine", gp4, Arrays.asList("A2", "A3", "A4"));
			Ship s9 = new Ship("patrolboat", gp4, Arrays.asList("G6", "H6"));
			Ship s10 = new Ship("destroyer", gp5, Arrays.asList("B5", "C5", "D5"));
			Ship s11 = new Ship("patrolboat", gp5, Arrays.asList("C6", "C7"));
			Ship s12 = new Ship("submarine", gp6, Arrays.asList("A2", "A3", "A4"));
			Ship s13 = new Ship("patrolboat", gp6, Arrays.asList("G6", "H6"));
			Ship s14 = new Ship("destroyer", gp7, Arrays.asList("B5", "C5", "D5"));
			Ship s15 = new Ship("patrolboat", gp7, Arrays.asList("C6", "C7"));
			Ship s16 = new Ship("submarine", gp8, Arrays.asList("A2", "A3", "A4"));
			Ship s17 = new Ship("patrolboat", gp8, Arrays.asList("G6", "H6"));
			Ship s18 = new Ship("destroyer", gp9, Arrays.asList("B5", "C5", "D5"));
			Ship s19 = new Ship("patrolboat", gp9, Arrays.asList("C6", "C7"));
			Ship s20 = new Ship("submarine", gp10, Arrays.asList("A2", "A3", "A4"));
			Ship s21 = new Ship("patrolboat", gp10, Arrays.asList("G6", "H6"));
			Ship s22 = new Ship("destroyer", gp11, Arrays.asList("B5", "C5", "D5"));
			Ship s23 = new Ship("patrolboat", gp11, Arrays.asList("C6", "C7"));
			Ship s24 = new Ship("destroyer", gp13, Arrays.asList("B5", "C5", "D5"));
			Ship s25 = new Ship("patrolboat", gp13, Arrays.asList("C6", "C7"));
			Ship s26 = new Ship("submarine", gp14, Arrays.asList("A2", "A3", "A4"));
			Ship s27 = new Ship("patrolboat", gp14, Arrays.asList("G6", "H6"));

			//Create Salvoes
			Salvo sal1 = new Salvo(1, gp1, Arrays.asList("B5","C5", "F1"));
			Salvo sal2 = new Salvo(1, gp2, Arrays.asList("B4","B5", "B6"));
			Salvo sal3 = new Salvo(2, gp1, Arrays.asList("F2","D5"));
			Salvo sal4 = new Salvo(2, gp2, Arrays.asList("E1","H3", "A2"));
			Salvo sal5 = new Salvo(1, gp3, Arrays.asList("A2","A4", "G6"));
			Salvo sal6 = new Salvo(1, gp4, Arrays.asList("B5","D5", "C7"));
			Salvo sal7 = new Salvo(2, gp3, Arrays.asList("A3","H6"));
			Salvo sal8 = new Salvo(2, gp4, Arrays.asList("C5","C6"));
			Salvo sal9 = new Salvo(1, gp5, Arrays.asList("G6","H6", "A4"));
			Salvo sal10 = new Salvo(1, gp6, Arrays.asList("H1","H2", "H3"));
			Salvo sal11 = new Salvo(2, gp5, Arrays.asList("A2","A3", "D8"));
			Salvo sal12 = new Salvo(2, gp6, Arrays.asList("E1","F2", "G3"));
			Salvo sal13 = new Salvo(1, gp7, Arrays.asList("A3","A4", "F7"));
			Salvo sal14 = new Salvo(1, gp8, Arrays.asList("B5","C6", "H1"));
			Salvo sal15 = new Salvo(2, gp7, Arrays.asList("A2","G6", "H6"));
			Salvo sal16 = new Salvo(2, gp8, Arrays.asList("C5","C7", "D5"));
			Salvo sal17 = new Salvo(1, gp9, Arrays.asList("A1","A2", "A3"));
			Salvo sal18 = new Salvo(1, gp10, Arrays.asList("B5","B6", "C7"));
			Salvo sal19 = new Salvo(2, gp9, Arrays.asList("G6","G7", "G8"));
			Salvo sal20 = new Salvo(2, gp10, Arrays.asList("C6","D6", "E6"));
			Salvo sal21 = new Salvo(3, gp10, Arrays.asList("H1","H8"));

			//Create Scores
			Date finishDate = Date.from(new Date().toInstant().plusSeconds(3600));
			Score sc1 = new Score(1.0, finishDate, game1, player1);
			Score sc2 = new Score(0.0, finishDate, game1, player2);

			finishDate = Date.from(finishDate.toInstant().plusSeconds(3600));
			Score sc3 = new Score(0.5, finishDate, game2, player1);
			Score sc4 = new Score(0.5, finishDate, game2, player2);

			finishDate = Date.from(finishDate.toInstant().plusSeconds(3600));
			Score sc5 = new Score(1.0, finishDate, game3, player2);
			Score sc6 = new Score(0.0, finishDate, game3, player3);

			finishDate = Date.from(finishDate.toInstant().plusSeconds(3600));
			Score sc7 = new Score(0.5, finishDate, game4, player2);
			Score sc8 = new Score(0.5, finishDate, game4, player1);

			//Save Players
			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);

			//Save Games
			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);
			gameRepository.save(game4);
			gameRepository.save(game5);
			gameRepository.save(game6);
			gameRepository.save(game7);
			gameRepository.save(game8);

			//Save GamePlayers
			gamePlayerRepository.save(gp1);
			gamePlayerRepository.save(gp2);
			gamePlayerRepository.save(gp3);
			gamePlayerRepository.save(gp4);
			gamePlayerRepository.save(gp5);
			gamePlayerRepository.save(gp6);
			gamePlayerRepository.save(gp7);
			gamePlayerRepository.save(gp8);
			gamePlayerRepository.save(gp9);
			gamePlayerRepository.save(gp10);
			gamePlayerRepository.save(gp11);
			gamePlayerRepository.save(gp12);
			gamePlayerRepository.save(gp13);
			gamePlayerRepository.save(gp14);

			//Save Ships
			shipRepository.save(s1);
			shipRepository.save(s2);
			shipRepository.save(s3);
			shipRepository.save(s4);
			shipRepository.save(s5);
			shipRepository.save(s6);
			shipRepository.save(s7);
			shipRepository.save(s8);
			shipRepository.save(s9);
			shipRepository.save(s10);
			shipRepository.save(s11);
			shipRepository.save(s12);
			shipRepository.save(s13);
			shipRepository.save(s14);
			shipRepository.save(s15);
			shipRepository.save(s16);
			shipRepository.save(s17);
			shipRepository.save(s18);
			shipRepository.save(s19);
			shipRepository.save(s20);
			shipRepository.save(s21);
			shipRepository.save(s22);
			shipRepository.save(s23);
			shipRepository.save(s24);
			shipRepository.save(s25);
			shipRepository.save(s26);
			shipRepository.save(s27);

			//Save Salvoes
			salvoRepository.save(sal1);
			salvoRepository.save(sal2);
			salvoRepository.save(sal3);
			salvoRepository.save(sal4);
			salvoRepository.save(sal5);
			salvoRepository.save(sal6);
			salvoRepository.save(sal7);
			salvoRepository.save(sal8);
			salvoRepository.save(sal9);
			salvoRepository.save(sal10);
			salvoRepository.save(sal11);
			salvoRepository.save(sal12);
			salvoRepository.save(sal13);
			salvoRepository.save(sal14);
			salvoRepository.save(sal15);
			salvoRepository.save(sal16);
			salvoRepository.save(sal17);
			salvoRepository.save(sal18);
			salvoRepository.save(sal19);
			salvoRepository.save(sal20);
			salvoRepository.save(sal21);

			//Save Scores
			scoreRepository.save(sc1);
			scoreRepository.save(sc2);
			scoreRepository.save(sc3);
			scoreRepository.save(sc4);
			scoreRepository.save(sc5);
			scoreRepository.save(sc6);
			scoreRepository.save(sc7);
			scoreRepository.save(sc8);
		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(inputName->{
			Player player = playerRepository.findByUsername(inputName);
			if(player != null){
				return new User(player.getUsername(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			}
			else{
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http
				.authorizeRequests()
					.antMatchers("/api/games", "/api/players", "/api/login").permitAll()
					.antMatchers("/web/game.html", "/api/game_view/**").hasAuthority("USER")
					.antMatchers("/web/**").permitAll()
					.antMatchers("/h2-console/**").permitAll().anyRequest().authenticated()
					.and().csrf().ignoringAntMatchers("/h2-console/**")
					.and().headers().frameOptions().sameOrigin()
					.and()
				.formLogin()
					.usernameParameter("name")
					.passwordParameter("pwd")
					.loginPage("/api/login")
					.and()
				.logout()
					.logoutUrl("/api/logout");

		http.csrf().disable();
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session != null){
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}
