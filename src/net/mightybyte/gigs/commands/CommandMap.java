package net.mightybyte.gigs.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CommandMap {
  private static CommandMap instance = new CommandMap("commands.props");

  private Map<String, Command> commands;

  private String propsFile;

  public static CommandMap getInstance() {
    return instance;
  }

  private CommandMap(String filename) {
    propsFile = filename;
    loadCommands();
  }

  public void loadCommands() {
    System.out.println("Loading CommandMap");

    commands = new HashMap<String, Command>();
    Properties props = new Properties();

    try {
      ClassLoader loader = new URLClassLoader(new URL[] {
          new File(".").toURL(), new File("classes").toURL() });

      props.load(new FileInputStream(propsFile));
      for (Object o : props.keySet()) {
        String s = (String) o;
        String className = props.getProperty(s);
        // Class c = Class.forName(className);
        Class c = loader.loadClass(className);
        Command cmd = (Command) c.newInstance();
        commands.put(s, cmd);
      }
    } catch (IOException e) {
      System.err.println("Could not open " + propsFile);
    } catch (Exception e) {
      System.err.println("Error in " + propsFile);
      e.printStackTrace();
    }

  }

  public Command getCommand(String commandName) {
    return commands.get(commandName);
  }
}
