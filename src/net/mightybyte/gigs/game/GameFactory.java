package net.mightybyte.gigs.game;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GameFactory {
  Map<String, Class> gameDef;

  /**
   * Builds a GameFactory from a game definition file
   * 
   * @param filename
   */
  public GameFactory(String filename) {
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(filename));
    } catch (IOException e) {
      throw new RuntimeException("Couldn't load " + filename);
    }

    try {
      gameDef = new HashMap<String, Class>();
      for (Object key : props.keySet()) {
        String str = (String) key;
        String value = props.getProperty(str);
        Class clazz = Class.forName(value);
        gameDef.put(str, clazz);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error in game definition file " + filename);
    }
  }

  /**
   * Creates a game described by a PendingGame
   * 
   * @param pGame
   * @return An instance of Game
   */
  public Game createGame(PendingGame pGame) {
    Class clazz = gameDef.get(pGame.getGameType());

    Game game;
    try {
      game = (Game) clazz.newInstance();
      game.init(pGame.getGameParams());
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("Invalid game type");
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Invalid game type");
    }
    return game;
  }
}
