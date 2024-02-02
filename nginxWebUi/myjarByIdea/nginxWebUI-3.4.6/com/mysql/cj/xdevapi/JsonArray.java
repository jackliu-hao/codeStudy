package com.mysql.cj.xdevapi;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonArray extends ArrayList<JsonValue> implements JsonValue {
   private static final long serialVersionUID = 6557406141541247905L;

   public String toString() {
      StringBuilder sb = new StringBuilder("[");

      JsonValue val;
      for(Iterator var2 = this.iterator(); var2.hasNext(); sb.append(val.toString())) {
         val = (JsonValue)var2.next();
         if (sb.length() > 1) {
            sb.append(",");
         }
      }

      sb.append("]");
      return sb.toString();
   }

   public String toFormattedString() {
      StringBuilder sb = new StringBuilder("[");

      JsonValue val;
      for(Iterator var2 = this.iterator(); var2.hasNext(); sb.append(val.toFormattedString())) {
         val = (JsonValue)var2.next();
         if (sb.length() > 1) {
            sb.append(", ");
         }
      }

      sb.append("]");
      return sb.toString();
   }

   public JsonArray addValue(JsonValue val) {
      this.add(val);
      return this;
   }
}
