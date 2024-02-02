package org.noear.solon.boot;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

public class ServerProps {
   public static final boolean output_meta;
   public static final String session_cookieName;
   /** @deprecated */
   @Deprecated
   public static final String session_cookieName2;
   public static final String request_encoding;
   public static final int request_maxHeaderSize;
   public static final int request_maxBodySize;
   public static final int request_maxFileSize;
   public static final int session_timeout;
   public static final String session_state_domain;
   public static final String response_encoding;

   public static void init() {
   }

   static void synProps(String appProp, String sysProp) {
      String tmp = Solon.cfg().get(appProp);
      if (tmp != null) {
         System.setProperty(sysProp, tmp);
      }

   }

   static int getSize(String tmp) {
      if (tmp == null) {
         return 0;
      } else {
         int val;
         if (tmp.endsWith("mb")) {
            val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            return val * 1204 * 1204;
         } else if (tmp.endsWith("kb")) {
            val = Integer.parseInt(tmp.substring(0, tmp.length() - 2));
            return val * 1204;
         } else {
            return tmp.length() > 0 ? Integer.parseInt(tmp) : 0;
         }
      }
   }

   static {
      String tmp = null;
      output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
      session_cookieName = System.getProperty("server.session.cookieName", "SOLONID");
      session_cookieName2 = session_cookieName + "2";
      synProps("server.ssl.keyStore", "javax.net.ssl.keyStore");
      synProps("server.ssl.keyType", "javax.net.ssl.keyStoreType");
      synProps("server.ssl.keyPassword", "javax.net.ssl.keyStorePassword");
      tmp = Solon.cfg().get("server.request.maxHeaderSize", "").trim().toLowerCase();
      request_maxHeaderSize = getSize(tmp);
      tmp = Solon.cfg().get("server.request.maxBodySize", "").trim().toLowerCase();
      if (Utils.isEmpty(tmp)) {
         tmp = Solon.cfg().get("server.request.maxRequestSize", "").trim().toLowerCase();
      }

      request_maxBodySize = getSize(tmp);
      tmp = Solon.cfg().get("server.request.maxFileSize", "").trim().toLowerCase();
      if (Utils.isEmpty(tmp)) {
         request_maxFileSize = request_maxBodySize;
      } else {
         request_maxFileSize = getSize(tmp);
      }

      tmp = Solon.cfg().get("server.request.encoding", "").trim();
      if (Utils.isEmpty(tmp)) {
         tmp = Solon.cfg().get("solon.encoding.request", "").trim();
      }

      if (Utils.isEmpty(tmp)) {
         request_encoding = Solon.encoding();
      } else {
         request_encoding = tmp;
      }

      session_timeout = Solon.cfg().getInt("server.session.timeout", 0);
      session_state_domain = Solon.cfg().get("server.session.state.domain");
      tmp = Solon.cfg().get("server.response.encoding", "").trim();
      if (Utils.isEmpty(tmp)) {
         tmp = Solon.cfg().get("solon.encoding.response", "").trim();
      }

      if (Utils.isEmpty(tmp)) {
         response_encoding = Solon.encoding();
      } else {
         response_encoding = tmp;
      }

   }
}
