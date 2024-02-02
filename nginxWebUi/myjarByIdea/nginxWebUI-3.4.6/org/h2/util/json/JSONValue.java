package org.h2.util.json;

public abstract class JSONValue {
   JSONValue() {
   }

   public abstract void addTo(JSONTarget<?> var1);

   public final String toString() {
      JSONStringTarget var1 = new JSONStringTarget();
      this.addTo(var1);
      return var1.getResult();
   }
}
