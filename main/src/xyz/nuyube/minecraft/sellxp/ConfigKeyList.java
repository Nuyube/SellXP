package xyz.nuyube.minecraft.sellxp;

import java.util.ArrayList;
import java.util.Iterator;

final class ConfigKeyList implements Iterable<ConfigKey> {
  private ArrayList<ConfigKey> Keys;
  public void addKey(ConfigKey key) {
    Keys.add(key);
  }
  public void addKeys(ConfigKey[] keys) {
    for(ConfigKey key : keys) {
      Keys.add(key);
    }
  }
  public Iterator<ConfigKey> iterator() {
    return Keys.iterator();
  }
  public void removeKeys(ConfigKey[] keys) {
    for(ConfigKey key : keys) {
      Keys.remove(key);
    }
  }
  public void removeKey(ConfigKey key) {
    Keys.remove(key);
  }
  public ConfigKey getKeyByName(String name) {
    for(ConfigKey key : Keys) {
      if(key.name == name) return key;
    }
    return null;
  }
  public ConfigKeyList() {
    Keys = new ArrayList<ConfigKey>();
  }
}