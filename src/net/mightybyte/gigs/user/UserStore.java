package net.mightybyte.gigs.user;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;

public class UserStore {
  private final String USER_DIR = "users/";
  private final String PASSWORD_FILE = "passwd";
  
  private static final UserStore _instance = new UserStore();
  
  private UserStore() {
    
  }
  
  public static UserStore getInstance() {
    return _instance;
  }
  
  public void addUser(String name, String passwd) throws Exception {
      File udir = new File(USER_DIR + name);
      MessageDigest sha = MessageDigest.getInstance("SHA");
      if (udir.exists())
        throw new Exception("User already exists");

      udir.mkdirs();
      
      FileOutputStream fos = new FileOutputStream(USER_DIR+PASSWORD_FILE);
      sha.update(passwd.getBytes());
      fos.write(sha.digest());
      fos.close();
  }

  public boolean userExists(String name) {
    File udir = new File(USER_DIR + name);
    return udir.exists();
  }
  
  public boolean authUser(String name, String passwd) throws Exception {
    return true;
  }
}
