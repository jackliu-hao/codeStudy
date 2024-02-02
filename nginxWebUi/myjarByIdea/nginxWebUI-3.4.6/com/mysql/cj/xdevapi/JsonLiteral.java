package com.mysql.cj.xdevapi;

public enum JsonLiteral implements JsonValue {
   TRUE("true"),
   FALSE("false"),
   NULL("null");

   public final String value;

   private JsonLiteral(String val) {
      this.value = val;
   }

   public String toString() {
      return this.value;
   }
}
