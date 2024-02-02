package org.h2.value;

import java.util.HashMap;
import org.h2.util.StringUtils;

public class CaseInsensitiveMap<V> extends HashMap<String, V> {
   private static final long serialVersionUID = 1L;

   public CaseInsensitiveMap() {
   }

   public CaseInsensitiveMap(int var1) {
      super(var1);
   }

   public V get(Object var1) {
      return super.get(StringUtils.toUpperEnglish((String)var1));
   }

   public V put(String var1, V var2) {
      return super.put(StringUtils.toUpperEnglish(var1), var2);
   }

   public V putIfAbsent(String var1, V var2) {
      return super.putIfAbsent(StringUtils.toUpperEnglish(var1), var2);
   }

   public boolean containsKey(Object var1) {
      return super.containsKey(StringUtils.toUpperEnglish((String)var1));
   }

   public V remove(Object var1) {
      return super.remove(StringUtils.toUpperEnglish((String)var1));
   }
}
