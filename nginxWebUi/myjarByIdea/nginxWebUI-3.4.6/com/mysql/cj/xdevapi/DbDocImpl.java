package com.mysql.cj.xdevapi;

import java.util.Iterator;
import java.util.TreeMap;

public class DbDocImpl extends TreeMap<String, JsonValue> implements DbDoc {
   private static final long serialVersionUID = 6557406141541247905L;

   public String toString() {
      StringBuilder sb = new StringBuilder("{");

      String key;
      for(Iterator var2 = this.keySet().iterator(); var2.hasNext(); sb.append("\"").append(key).append("\":").append(((JsonValue)this.get(key)).toString())) {
         key = (String)var2.next();
         if (sb.length() > 1) {
            sb.append(",");
         }
      }

      sb.append("}");
      return sb.toString();
   }

   public String toFormattedString() {
      StringBuilder sb = new StringBuilder("{");

      String key;
      for(Iterator var2 = this.keySet().iterator(); var2.hasNext(); sb.append("\n\"").append(key).append("\" : ").append(((JsonValue)this.get(key)).toFormattedString())) {
         key = (String)var2.next();
         if (sb.length() > 1) {
            sb.append(",");
         }
      }

      if (this.size() > 0) {
         sb.append("\n");
      }

      sb.append("}");
      return sb.toString();
   }

   public DbDoc add(String key, JsonValue val) {
      this.put(key, val);
      return this;
   }
}
