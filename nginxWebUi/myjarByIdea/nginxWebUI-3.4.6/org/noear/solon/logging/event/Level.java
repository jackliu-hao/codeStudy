package org.noear.solon.logging.event;

public enum Level {
   TRACE(10),
   DEBUG(20),
   INFO(30),
   WARN(40),
   ERROR(50);

   public final int code;

   public static Level of(int code, Level def) {
      Level[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Level v = var2[var4];
         if (v.code == code) {
            return v;
         }
      }

      return def;
   }

   public static Level of(String name, Level def) {
      if (name != null && name.length() != 0) {
         switch (name.toUpperCase()) {
            case "TRACE":
               return TRACE;
            case "DEBUG":
               return DEBUG;
            case "INFO":
               return INFO;
            case "WARN":
               return WARN;
            case "ERROR":
               return ERROR;
            default:
               return def;
         }
      } else {
         return def;
      }
   }

   private Level(int code) {
      this.code = code;
   }
}
