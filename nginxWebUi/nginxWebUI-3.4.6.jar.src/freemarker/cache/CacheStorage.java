package freemarker.cache;

public interface CacheStorage {
  Object get(Object paramObject);
  
  void put(Object paramObject1, Object paramObject2);
  
  void remove(Object paramObject);
  
  void clear();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\CacheStorage.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */