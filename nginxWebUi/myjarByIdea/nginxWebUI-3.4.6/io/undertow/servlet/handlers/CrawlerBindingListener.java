package io.undertow.servlet.handlers;

import java.io.Serializable;
import java.util.Map;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

class CrawlerBindingListener implements HttpSessionBindingListener, Serializable {
   private static final long serialVersionUID = -8841692120840734349L;
   private transient Map<String, String> clientIpSessionId;
   private transient Map<String, String> sessionIdClientIp;

   CrawlerBindingListener(Map<String, String> clientIpSessionId, Map<String, String> sessionIdClientIp) {
      this.clientIpSessionId = clientIpSessionId;
      this.sessionIdClientIp = sessionIdClientIp;
   }

   public void valueBound(HttpSessionBindingEvent event) {
   }

   public void valueUnbound(HttpSessionBindingEvent event) {
      if (this.sessionIdClientIp != null) {
         String clientIp = (String)this.sessionIdClientIp.remove(event.getSession().getId());
         if (clientIp != null) {
            this.clientIpSessionId.remove(clientIp);
         }
      }

   }
}
