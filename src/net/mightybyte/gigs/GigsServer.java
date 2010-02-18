package net.mightybyte.gigs;

import java.net.*;
import java.io.*;

import net.mightybyte.gigs.commands.CommandMap;

public class GigsServer {

  public final int PORT = 8000;

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    new GigsServer();

  }

  public GigsServer() {
    ServerSocket sksListener;
    Socket skcConnection;
    ClientConnection cc;
    
    CommandMap.getInstance();
    try {
      System.out.println("Server started");
      sksListener = new ServerSocket(PORT);
      while (true) {
        skcConnection = sksListener.accept();
        cc = new ClientConnection(skcConnection);
        cc.start();
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

}
