package cn.hutool.cache;

public interface CacheListener<K, V> {
   void onRemove(K var1, V var2);
}
