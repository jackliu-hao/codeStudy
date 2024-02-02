package cn.hutool.cache;

public interface CacheListener<K, V> {
  void onRemove(K paramK, V paramV);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cache\CacheListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */