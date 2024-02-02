package com.mysql.cj;

public interface CacheAdapterFactory<K, V> {
  CacheAdapter<K, V> getInstance(Object paramObject, String paramString, int paramInt1, int paramInt2);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\CacheAdapterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */