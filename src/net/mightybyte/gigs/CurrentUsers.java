package net.mightybyte.gigs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CurrentUsers implements Iterable<ConnectedUser> {
  private List<ConnectedUser> userList;

  private Map<String, ConnectedUser> userMap;

  public CurrentUsers() {
    userList = new ArrayList<ConnectedUser>();
    userMap = new HashMap<String, ConnectedUser>();
  }

  public Iterator<ConnectedUser> iterator() {
    return userList.iterator();
  }

  public ConnectedUser getUser(String username) {
    return userMap.get(username);
  }

  public void addUser(ConnectedUser user) {
    userList.add(user);
    userMap.put(user.getName(), user);
  }

  public void removeUser(String username) {
    ConnectedUser user = userMap.remove(username);
    userList.remove(user);
  }
  
  public boolean isConnected(String username) {
    return userMap.containsKey(username);
  }
}
