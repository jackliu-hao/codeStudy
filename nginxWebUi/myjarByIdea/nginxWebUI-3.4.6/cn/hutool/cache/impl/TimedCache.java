package cn.hutool.cache.impl;

import cn.hutool.cache.GlobalPruneTimer;
import cn.hutool.core.lang.mutable.Mutable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class TimedCache<K, V> extends StampedCache<K, V> {
   private static final long serialVersionUID = 1L;
   private ScheduledFuture<?> pruneJobFuture;

   public TimedCache(long timeout) {
      this(timeout, new HashMap());
   }

   public TimedCache(long timeout, Map<Mutable<K>, CacheObj<K, V>> map) {
      this.capacity = 0;
      this.timeout = timeout;
      this.cacheMap = map;
   }

   protected int pruneCache() {
      int count = 0;
      Iterator<CacheObj<K, V>> values = this.cacheObjIter();

      while(values.hasNext()) {
         CacheObj<K, V> co = (CacheObj)values.next();
         if (co.isExpired()) {
            values.remove();
            this.onRemove(co.key, co.obj);
            ++count;
         }
      }

      return count;
   }

   public void schedulePrune(long delay) {
      this.pruneJobFuture = GlobalPruneTimer.INSTANCE.schedule(this::prune, delay);
   }

   public void cancelPruneSchedule() {
      if (null != this.pruneJobFuture) {
         this.pruneJobFuture.cancel(true);
      }

   }
}
