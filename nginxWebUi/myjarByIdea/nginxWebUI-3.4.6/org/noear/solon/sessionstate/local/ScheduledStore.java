package org.noear.solon.sessionstate.local;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class ScheduledStore {
   private int _defaultSeconds;
   private Map<String, Entity> _data = new ConcurrentHashMap();
   private static ScheduledExecutorService _exec = Executors.newSingleThreadScheduledExecutor();

   public ScheduledStore(int seconds) {
      this._defaultSeconds = seconds;
   }

   public Collection<String> keys() {
      return this._data.keySet();
   }

   public void put(String block, String key, Object obj) {
      synchronized(block.intern()) {
         Entity ent = (Entity)this._data.get(block);
         if (ent == null) {
            ent = new Entity();
            this._data.put(block, ent);
         } else {
            ent.futureDel();
         }

         ent.map.put(key, obj);
         ent.future = _exec.schedule(() -> {
            this._data.remove(block);
         }, (long)this._defaultSeconds, TimeUnit.SECONDS);
      }
   }

   public void delay(String block) {
      Entity ent = (Entity)this._data.get(block);
      if (ent != null) {
         ent.futureDel();
         ent.future = _exec.schedule(() -> {
            this._data.remove(block);
         }, (long)this._defaultSeconds, TimeUnit.SECONDS);
      }

   }

   public Object get(String block, String key) {
      Entity ent = (Entity)this._data.get(block);
      return ent != null ? ent.map.get(key) : null;
   }

   public void remove(String block, String key) {
      synchronized(block.intern()) {
         Entity ent = (Entity)this._data.get(block);
         if (ent != null) {
            ent.map.remove(key);
         }

      }
   }

   public void clear(String block) {
      synchronized(block.intern()) {
         Entity ent = (Entity)this._data.get(block);
         if (ent != null) {
            ent.futureDel();
            this._data.remove(block);
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

   private static class Entity {
      public Map<String, Object> map;
      public Future future;

      private Entity() {
         this.map = new ConcurrentHashMap();
      }

      protected void futureDel() {
         if (this.future != null) {
            this.future.cancel(true);
            this.future = null;
         }

      }

      // $FF: synthetic method
      Entity(Object x0) {
         this();
      }
   }
}
