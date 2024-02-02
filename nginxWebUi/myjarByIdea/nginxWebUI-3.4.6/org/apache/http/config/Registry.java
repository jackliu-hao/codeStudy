package org.apache.http.config;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public final class Registry<I> implements Lookup<I> {
   private final Map<String, I> map;

   Registry(Map<String, I> map) {
      this.map = new ConcurrentHashMap(map);
   }

   public I lookup(String key) {
      return key == null ? null : this.map.get(key.toLowerCase(Locale.ROOT));
   }

   public String toString() {
      return this.map.toString();
   }
}
