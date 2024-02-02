package org.h2.util.json;

public enum JSONItemType {
   VALUE,
   ARRAY,
   OBJECT,
   SCALAR;

   public boolean includes(JSONItemType var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this == VALUE || this == var1;
      }
   }
}
