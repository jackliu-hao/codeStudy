package org.noear.solon.boot.web;

import org.noear.solon.Utils;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.handle.SessionState;

public abstract class SessionStateBase implements SessionState {
   protected abstract String cookieGet(String key);

   protected abstract void cookieSet(String key, String val);

   protected String sessionIdGet(boolean reset) {
      String sid = this.cookieGet(ServerProps.session_cookieName);
      if (!reset && !Utils.isEmpty(sid) && sid.length() > 30) {
         return sid;
      } else {
         sid = Utils.guid();
         this.cookieSet(ServerProps.session_cookieName, sid);
         this.cookieSet(ServerProps.session_cookieName2, Utils.md5(sid + "&L8e!@T0"));
         return sid;
      }
   }

   protected String sessionIdPush() {
      String skey = this.cookieGet(ServerProps.session_cookieName);
      if (Utils.isNotEmpty(skey)) {
         this.cookieSet(ServerProps.session_cookieName, skey);
      }

      return skey;
   }
}
