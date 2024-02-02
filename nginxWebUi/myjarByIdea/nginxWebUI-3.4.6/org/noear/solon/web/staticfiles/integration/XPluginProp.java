package org.noear.solon.web.staticfiles.integration;

import org.noear.solon.Solon;

public class XPluginProp {
   static final String PROP_ENABLE = "solon.staticfiles.enable";
   static final String PROP_MAX_AGE = "solon.staticfiles.maxAge";
   static final String RES_STATIC_LOCATION = "static/";
   static int maxAge = -1;

   public static boolean enable() {
      return Solon.cfg().getBool("solon.staticfiles.enable", true);
   }

   public static int maxAge() {
      if (maxAge < 0) {
         if (Solon.cfg().isDebugMode()) {
            maxAge = 0;
         } else {
            maxAge = Solon.cfg().getInt("solon.staticfiles.maxAge", 600);
         }
      }

      return maxAge;
   }
}
