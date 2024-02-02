package org.noear.snack.core.exts;

import java.util.HashMap;
import java.util.Map;

public class EnumWrap {
   protected final Map<String, Enum> enumMap = new HashMap();
   protected final Enum[] enumOrdinal;

   public EnumWrap(Class<?> enumClass) {
      this.enumOrdinal = (Enum[])((Enum[])enumClass.getEnumConstants());

      for(int i = 0; i < this.enumOrdinal.length; ++i) {
         Enum e = this.enumOrdinal[i];
         this.enumMap.put(e.name().toLowerCase(), e);
      }

   }

   public Enum get(int ordinal) {
      return this.enumOrdinal[ordinal];
   }

   public Enum get(String name) {
      return (Enum)this.enumMap.get(name.toLowerCase());
   }
}
