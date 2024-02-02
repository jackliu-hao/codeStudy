package com.mysql.cj;

import java.util.Set;

public interface CacheAdapter<K, V> {
  V get(K paramK);
  
  void put(K paramK, V paramV);
  
  void invalidate(K paramK);
  
  void invalidateAll(Set<K> paramSet);
  
  void invalidateAll();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\CacheAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */