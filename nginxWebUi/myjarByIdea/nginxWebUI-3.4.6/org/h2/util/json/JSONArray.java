package org.h2.util.json;

import java.util.ArrayList;
import java.util.Iterator;

public class JSONArray extends JSONValue {
   private final ArrayList<JSONValue> elements = new ArrayList();

   JSONArray() {
   }

   void addElement(JSONValue var1) {
      this.elements.add(var1);
   }

   public void addTo(JSONTarget<?> var1) {
      var1.startArray();
      Iterator var2 = this.elements.iterator();

      while(var2.hasNext()) {
         JSONValue var3 = (JSONValue)var2.next();
         var3.addTo(var1);
      }

      var1.endArray();
   }

   public JSONValue[] getArray() {
      return (JSONValue[])this.elements.toArray(new JSONValue[0]);
   }
}
