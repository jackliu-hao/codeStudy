package com.github.odiszapc.nginxparser;

import java.util.HashMap;
import java.util.Map;

public enum NgxEntryType {
   PARAM(NgxParam.class),
   COMMENT(NgxComment.class),
   IF(NgxIfBlock.class),
   BLOCK(NgxBlock.class);

   private final Class<? extends NgxEntry> clazz;
   private static Map<Class<? extends NgxEntry>, NgxEntryType> types = new HashMap();

   Class<? extends NgxEntry> getType() {
      return this.clazz;
   }

   private NgxEntryType(Class clazz) {
      this.clazz = clazz;
   }

   public static NgxEntryType fromClass(Class<? extends NgxEntry> clazz) {
      return (NgxEntryType)types.get(clazz);
   }

   static {
      NgxEntryType[] arr$ = values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         NgxEntryType type = arr$[i$];
         types.put(type.clazz, type);
      }

   }
}
