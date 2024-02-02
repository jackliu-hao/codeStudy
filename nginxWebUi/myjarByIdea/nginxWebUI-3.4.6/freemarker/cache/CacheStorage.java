package freemarker.cache;

public interface CacheStorage {
   Object get(Object var1);

   void put(Object var1, Object var2);

   void remove(Object var1);

   void clear();
}
