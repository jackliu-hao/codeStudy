package org.noear.solon.socketd;

import java.util.Collection;
import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.message.Session;

public class SocketSessionState implements SessionState {
   Session session;

   public SocketSessionState(Session session) {
      this.session = session;
   }

   public boolean replaceable() {
      return false;
   }

   public String sessionId() {
      return this.session.sessionId();
   }

   public String sessionChangeId() {
      return this.session.sessionId();
   }

   public Collection<String> sessionKeys() {
      return this.session.attrMap().keySet();
   }

   public Object sessionGet(String key) {
      return this.session.attr(key);
   }

   public void sessionSet(String key, Object val) {
      if (val == null) {
         this.sessionRemove(key);
      } else {
         this.session.attrSet(key, val);
      }

   }

   public void sessionRemove(String key) {
      this.session.attrMap().remove(key);
   }

   public void sessionClear() {
      this.session.attrMap().clear();
   }

   public void sessionReset() {
   }
}
