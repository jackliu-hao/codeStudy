package org.h2.util.json;

public class JSONBoolean extends JSONValue {
   public static final JSONBoolean FALSE = new JSONBoolean(false);
   public static final JSONBoolean TRUE = new JSONBoolean(true);
   private final boolean value;

   private JSONBoolean(boolean var1) {
      this.value = var1;
   }

   public void addTo(JSONTarget<?> var1) {
      if (this.value) {
         var1.valueTrue();
      } else {
         var1.valueFalse();
      }

   }

   public boolean getBoolean() {
      return this.value;
   }
}
