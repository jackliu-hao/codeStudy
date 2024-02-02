package io.undertow.server.handlers;

public enum CookieSameSiteMode {
   STRICT("Strict"),
   LAX("Lax"),
   NONE("None");

   private static final CookieSameSiteMode[] SAMESITE_MODES = values();
   private final String label;

   private CookieSameSiteMode(String label) {
      this.label = label;
   }

   public static String lookupModeString(String mode) {
      CookieSameSiteMode[] var1 = SAMESITE_MODES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         CookieSameSiteMode m = var1[var3];
         if (m.name().equalsIgnoreCase(mode)) {
            return m.toString();
         }
      }

      return null;
   }

   public String toString() {
      return this.label;
   }
}
