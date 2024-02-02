package io.undertow.servlet.api;

public enum ServletStackTraces {
   NONE("none"),
   LOCAL_ONLY("local-only"),
   ALL("all");

   private final String value;

   private ServletStackTraces(String value) {
      this.value = value;
   }

   public String toString() {
      return this.value;
   }
}
