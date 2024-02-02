package com.mysql.cj.xdevapi;

import java.util.HashMap;

public class JsonString implements JsonValue {
   static HashMap<Character, String> escapeChars = new HashMap();
   private String val = "";

   public String getString() {
      return this.val;
   }

   public JsonString setValue(String value) {
      this.val = value;
      return this;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder("\"");

      for(int i = 0; i < this.val.length(); ++i) {
         if (escapeChars.containsKey(this.val.charAt(i))) {
            sb.append((String)escapeChars.get(this.val.charAt(i)));
         } else {
            sb.append(this.val.charAt(i));
         }
      }

      sb.append("\"");
      return sb.toString();
   }

   static {
      JsonParser.EscapeChar[] var0 = JsonParser.EscapeChar.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         JsonParser.EscapeChar ec = var0[var2];
         escapeChars.put(ec.CHAR, ec.ESCAPED);
      }

   }
}
