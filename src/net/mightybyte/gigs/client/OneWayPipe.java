package net.mightybyte.gigs.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

public class OneWayPipe extends Thread {
  private PrintStream log;
  private BufferedReader in;
//  private PrintStream out;
  private OutputStream out;
  
  public OneWayPipe(InputStream i, OutputStream o) {
    try {
      log = new PrintStream(new File("pipe.log"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    in = new BufferedReader(new InputStreamReader(System.in));
    out = o;
//    out = new PrintStream(o);
  }

  public void run() {
    try {
      while (true) {
        String line = in.readLine();
        log.println("Read: "+line);
        out.write(line.getBytes());
        out.write(10);
        out.flush();
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
