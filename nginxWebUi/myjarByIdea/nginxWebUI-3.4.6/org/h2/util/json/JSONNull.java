package org.h2.util.json;

public class JSONNull extends JSONValue {
   public static final JSONNull NULL = new JSONNull();

   private JSONNull() {
   }

   public void addTo(JSONTarget<?> var1) {
      var1.valueNull();
   }
}
