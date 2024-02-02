package org.noear.solon.core.handle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SessionStateEmpty implements SessionState {
   private Map<String, Object> sessionMap = null;

   public Map<String, Object> sessionMap() {
      if (this.sessionMap == null) {
         this.sessionMap = new HashMap();
      }

      return this.sessionMap;
   }

   public String sessionId() {
      return null;
   }

   public String sessionChangeId() {
      return null;
   }

   public Collection<String> sessionKeys() {
      return this.sessionMap().keySet();
   }

   public Object sessionGet(String key) {
      return this.sessionMap().get(key);
   }

   public void sessionSet(String key, Object val) {
      if (val == null) {
         this.sessionRemove(key);
      } else {
         this.sessionMap().put(key, val);
      }

   }

   public void sessionRemove(String key) {
      this.sessionMap().remove(key);
   }

   public void sessionClear() {
      this.sessionMap().clear();
   }

   public void sessionReset() {
      this.sessionMap().clear();
   }
}
