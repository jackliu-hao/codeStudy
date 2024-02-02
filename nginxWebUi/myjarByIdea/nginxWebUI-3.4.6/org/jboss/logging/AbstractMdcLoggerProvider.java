package org.jboss.logging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractMdcLoggerProvider extends AbstractLoggerProvider {
   private final ThreadLocal<Map<String, Object>> mdcMap = new ThreadLocal();

   public void clearMdc() {
      Map<String, Object> map = (Map)this.mdcMap.get();
      if (map != null) {
         map.clear();
      }

   }

   public Object getMdc(String key) {
      return this.mdcMap.get() == null ? null : ((Map)this.mdcMap.get()).get(key);
   }

   public Map<String, Object> getMdcMap() {
      Map<String, Object> map = (Map)this.mdcMap.get();
      return map == null ? Collections.emptyMap() : map;
   }

   public Object putMdc(String key, Object value) {
      Map<String, Object> map = (Map)this.mdcMap.get();
      if (map == null) {
         map = new HashMap();
         this.mdcMap.set(map);
      }

      return ((Map)map).put(key, value);
   }

   public void removeMdc(String key) {
      Map<String, Object> map = (Map)this.mdcMap.get();
      if (map != null) {
         map.remove(key);
      }
   }
}
