package org.wildfly.common.net;

import java.net.URI;

public final class URIs {
   private URIs() {
   }

   public static String getUserFromURI(URI uri) {
      String userInfo = uri.getUserInfo();
      if (userInfo == null && "domain".equals(uri.getScheme())) {
         String ssp = uri.getSchemeSpecificPart();
         int at = ssp.lastIndexOf(64);
         if (at == -1) {
            return null;
         }

         userInfo = ssp.substring(0, at);
      }

      if (userInfo != null) {
         int colon = userInfo.indexOf(58);
         if (colon != -1) {
            userInfo = userInfo.substring(0, colon);
         }
      }

      return userInfo;
   }
}
