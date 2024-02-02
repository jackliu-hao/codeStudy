package org.noear.solon.data.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.noear.solon.Utils;

public class LocalCacheService implements CacheService {
   public static final CacheService instance = new LocalCacheService();
   private int _defaultSeconds;
   private Map<String, Entity> _data;
   private static ScheduledExecutorService _exec = Executors.newSingleThreadScheduledExecutor();

   public LocalCacheService() {
      this(30);
   }

   public LocalCacheService(int defSeconds) {
      this._data = new ConcurrentHashMap();
      this._defaultSeconds = defSeconds;
   }

   public LocalCacheService(Properties prop) {
      this._data = new ConcurrentHashMap();
      String defSeconds_str = prop.getProperty("defSeconds");
      if (Utils.isNotEmpty(defSeconds_str)) {
         this._defaultSeconds = Integer.parseInt(defSeconds_str);
      }

      if (this._defaultSeconds < 1) {
         this._defaultSeconds = 30;
      }

   }

   public void store(String key, Object obj, int seconds) {
      if (seconds <= 0) {
         seconds = this.getDefalutSeconds();
      }

      synchronized(key.intern()) {
         Entity ent = (Entity)this._data.get(key);
         if (ent == null) {
            ent = new Entity(obj);
            this._data.put(key, ent);
         } else {
            ent.value = obj;
            ent.futureDel();
         }

         if (seconds > 0) {
            ent.future = _exec.schedule(() -> {
               this._data.remove(key);
            }, (long)seconds, TimeUnit.SECONDS);
         }

      }
   }

   public Object get(String key) {
      Entity ent = (Entity)this._data.get(key);
      return ent == null ? null : ent.value;
   }

   public void remove(String key) {
      synchronized(key.intern()) {
         Entity ent = (Entity)this._data.remove(key);
         if (ent != null) {
            ent.futureDel();
         }

      }
   }

   public void clear() {
      Iterator var1 = this._data.values().iterator();

      while(var1.hasNext()) {
         Entity ent = (Entity)var1.next();
         ent.futureDel();
      }

      this._data.clear();
   }

   public int getDefalutSeconds() {
      return this._defaultSeconds;
   }

   private static class Entity {
      public Object value;
      public Future future;

      public Entity(Object val) {
         this.value = val;
      }

      protected void futureDel() {
         if (this.future != null) {
            this.future.cancel(true);
            this.future = null;
         }

      }
   }
}
