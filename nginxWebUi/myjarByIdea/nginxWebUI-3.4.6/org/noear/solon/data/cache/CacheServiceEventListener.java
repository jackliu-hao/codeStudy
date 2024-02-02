package org.noear.solon.data.cache;

import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventListener;

public class CacheServiceEventListener implements EventListener<BeanWrap> {
   public void onEvent(BeanWrap bw) {
      if (bw.raw() instanceof CacheService) {
         if (Utils.isEmpty(bw.name())) {
            CacheLib.cacheServiceAdd("", (CacheService)bw.raw());
         } else {
            CacheLib.cacheServiceAddIfAbsent(bw.name(), (CacheService)bw.raw());
            if (bw.typed()) {
               CacheLib.cacheServiceAdd("", (CacheService)bw.raw());
            }
         }
      }

   }
}
