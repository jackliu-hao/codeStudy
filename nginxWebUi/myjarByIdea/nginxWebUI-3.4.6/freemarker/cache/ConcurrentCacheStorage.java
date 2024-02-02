package freemarker.cache;

public interface ConcurrentCacheStorage extends CacheStorage {
   boolean isConcurrent();
}
