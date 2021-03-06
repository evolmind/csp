package com.bowman.cardserv;

import com.bowman.cardserv.interfaces.StaleEntryListener;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: bowman
 * Date: Sep 9, 2010
 * Time: 3:15:35 PM
 */
public class MessageCacheMap extends LinkedHashMap {

  private long maxAge;
  private StaleEntryListener listener;

  public MessageCacheMap(long maxAge) {
    this.maxAge = maxAge;
  }

  protected boolean removeEldestEntry(Map.Entry eldest) {
    CamdNetMessage msg;
    if(eldest.getKey() instanceof CamdNetMessage) msg = (CamdNetMessage)eldest.getKey();
    else if(eldest.getValue() instanceof CamdNetMessage) msg = (CamdNetMessage)eldest.getValue();
    else msg = (CamdNetMessage)((Set)eldest.getValue()).iterator().next();
    if(System.currentTimeMillis() - msg.getTimeStamp() > maxAge) {
      if(listener != null) listener.onRemoveStale(msg);
      return true;
    } else return false;
  }

  public void setStaleEntryListener(StaleEntryListener listener) {
    this.listener = listener;
  }

  public void setMaxAge(long maxAge) {
    if(maxAge < this.maxAge) {
      long now = System.currentTimeMillis();
      Object key; CamdNetMessage msg;
      for(Iterator iter = keySet().iterator(); iter.hasNext(); ) {
        key = iter.next();
        if(key instanceof CamdNetMessage) msg = (CamdNetMessage)key;
        else msg = (CamdNetMessage)get(key);
        if(now - msg.getTimeStamp() > maxAge) iter.remove();
      }
    }
    this.maxAge = maxAge;
  }
}
