package org.noear.solon.web.servlet;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.noear.solon.core.handle.SessionState;

public class SolonServletSessionState implements SessionState {
   private HttpServletRequest _request;

   public SolonServletSessionState(HttpServletRequest request) {
      this._request = request;
   }

   public String sessionId() {
      return this._request.getRequestedSessionId();
   }

   public String sessionChangeId() {
      return this._request.changeSessionId();
   }

   public Collection<String> sessionKeys() {
      return Collections.list(this._request.getSession().getAttributeNames());
   }

   public Object sessionGet(String key) {
      return this._request.getSession().getAttribute(key);
   }

   public void sessionSet(String key, Object val) {
      if (val == null) {
         this.sessionRemove(key);
      } else {
         this._request.getSession().setAttribute(key, val);
      }

   }

   public void sessionRemove(String key) {
      this._request.getSession().removeAttribute(key);
   }

   public void sessionClear() {
      Enumeration<String> names = this._request.getSession().getAttributeNames();

      while(names.hasMoreElements()) {
         this._request.getSession().removeAttribute((String)names.nextElement());
      }

   }

   public void sessionReset() {
      this._request.getSession().invalidate();
   }
}
