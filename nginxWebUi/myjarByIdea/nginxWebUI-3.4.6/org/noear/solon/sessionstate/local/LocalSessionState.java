package org.noear.solon.sessionstate.local;

import java.util.Collection;
import org.noear.solon.Utils;
import org.noear.solon.boot.web.SessionStateBase;
import org.noear.solon.core.handle.Context;

public class LocalSessionState extends SessionStateBase {
   private static int _expiry = 7200;
   private static String _domain = null;
   private static ScheduledStore _store;
   private Context ctx;

   protected LocalSessionState(Context ctx) {
      this.ctx = ctx;
   }

   protected String cookieGet(String key) {
      return this.ctx.cookie(key);
   }

   protected void cookieSet(String key, String val) {
      if (SessionProp.session_state_domain_auto && _domain != null && this.ctx.uri().getHost().indexOf(_domain) < 0) {
         this.ctx.cookieSet(key, val, (String)null, _expiry);
      } else {
         this.ctx.cookieSet(key, val, _domain, _expiry);
      }
   }

   public String sessionId() {
      String _sessionId = (String)this.ctx.attr("sessionId", (Object)null);
      if (_sessionId == null) {
         _sessionId = this.sessionIdGet(false);
         this.ctx.attrSet("sessionId", _sessionId);
      }

      return _sessionId;
   }

   public String sessionChangeId() {
      this.sessionIdGet(true);
      this.ctx.attrSet("sessionId", (Object)null);
      return this.sessionId();
   }

   public Collection<String> sessionKeys() {
      return _store.keys();
   }

   public Object sessionGet(String key) {
      return _store.get(this.sessionId(), key);
   }

   public void sessionSet(String key, Object val) {
      if (val == null) {
         this.sessionRemove(key);
      } else {
         _store.put(this.sessionId(), key, val);
      }

   }

   public void sessionRemove(String key) {
      _store.remove(this.sessionId(), key);
   }

   public void sessionClear() {
      _store.clear(this.sessionId());
   }

   public void sessionReset() {
      this.sessionClear();
      this.sessionChangeId();
   }

   public void sessionRefresh() {
      String sid = this.sessionIdPush();
      if (!Utils.isEmpty(sid)) {
         _store.delay(this.sessionId());
      }

   }

   public boolean replaceable() {
      return false;
   }

   static {
      if (SessionProp.session_timeout > 0) {
         _expiry = SessionProp.session_timeout;
      }

      if (SessionProp.session_state_domain != null) {
         _domain = SessionProp.session_state_domain;
      }

      _store = new ScheduledStore(_expiry);
   }
}
