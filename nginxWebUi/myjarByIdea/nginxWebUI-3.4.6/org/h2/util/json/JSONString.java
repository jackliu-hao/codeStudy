package org.h2.util.json;

public class JSONString extends JSONValue {
   private final String value;

   JSONString(String var1) {
      this.value = var1;
   }

   public void addTo(JSONTarget<?> var1) {
      var1.valueString(this.value);
   }

   public String getString() {
      return this.value;
   }
}
