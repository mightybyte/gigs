package net.mightybyte.gigs.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class GigsClient {

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
      if ( args.length < 3 ) {
        System.err.println("Usage: java -jar <jarfile> <host> <port> <executable>");
        return;
      }
      
      PrintStream log = new PrintStream(new File("tobot.log"));
      Socket s = new Socket(args[0], Integer.parseInt(args[1]));
      String command = args[2];
      BufferedReader in = new BufferedReader(new InputStreamReader(s
          .getInputStream()));
      Process p = Runtime.getRuntime().exec(command);
      PrintStream botOut = new PrintStream(p.getOutputStream());

      OneWayPipe userPipe = new OneWayPipe(System.in, s.getOutputStream());
      OneWayPipe botPipe = new OneWayPipe(p.getInputStream(), s.getOutputStream());

      userPipe.start();
      botPipe.start();
      int state = 0;
      while (true) {
//        String input = in.readLine();
//        System.out.print(input);
//        if (input.matches("^~~~")) {
//          botOut.print(input);
//        }
        int c = s.getInputStream().read();
        if ( c == -1 ) {
          System.out.println("Disconnected");
          System.exit(0);
        } else {
          if ( state == 0 ) {
            if ( c == 10 ) state++;
          } else if ( state >= 1 && state <= 3 ) {
            if ( c == 126 ) state++;
            else if ( c == 10 ) state = 1;
            else state = 0;
          } else if ( state == 4 ) {
            botOut.write(c);
            botOut.flush();
//            log.write(c);
//            log.flush();
            if ( c == 10 ) {
              state = 0;
            }
          }
          log.println("Character "+c+", state "+state);
          System.out.write(c);
          System.out.flush();
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
