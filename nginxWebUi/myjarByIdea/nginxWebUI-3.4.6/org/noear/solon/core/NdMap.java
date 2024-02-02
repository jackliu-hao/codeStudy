package org.noear.solon.core;

import java.util.Map;
import org.noear.solon.ext.LinkedCaseInsensitiveMap;

public class NdMap extends LinkedCaseInsensitiveMap<Object> {
   public NdMap() {
   }

   public NdMap(Map map) {
      map.forEach((k, v) -> {
         this.put(k.toString(), v);
      });
   }
}
