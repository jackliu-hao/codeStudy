package io.undertow.server.session;

import io.undertow.server.HttpServerExchange;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class SessionListeners {
   private final List<SessionListener> sessionListeners = new CopyOnWriteArrayList();

   public void addSessionListener(SessionListener listener) {
      this.sessionListeners.add(listener);
   }

   public boolean removeSessionListener(SessionListener listener) {
      return this.sessionListeners.remove(listener);
   }

   public void clear() {
      this.sessionListeners.clear();
   }

   public void sessionCreated(Session session, HttpServerExchange exchange) {
      Iterator var3 = this.sessionListeners.iterator();

      while(var3.hasNext()) {
         SessionListener listener = (SessionListener)var3.next();
         listener.sessionCreated(session, exchange);
      }

   }

   public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
      List<SessionListener> listeners = new ArrayList(this.sessionListeners);
      ListIterator<SessionListener> iterator = listeners.listIterator(listeners.size());

      while(iterator.hasPrevious()) {
         ((SessionListener)iterator.previous()).sessionDestroyed(session, exchange, reason);
      }

   }

   public void attributeAdded(Session session, String name, Object value) {
      Iterator var4 = this.sessionListeners.iterator();

      while(var4.hasNext()) {
         SessionListener listener = (SessionListener)var4.next();
         listener.attributeAdded(session, name, value);
      }

   }

   public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {
      Iterator var5 = this.sessionListeners.iterator();

      while(var5.hasNext()) {
         SessionListener listener = (SessionListener)var5.next();
         listener.attributeUpdated(session, name, newValue, oldValue);
      }

   }

   public void attributeRemoved(Session session, String name, Object oldValue) {
      Iterator var4 = this.sessionListeners.iterator();

      while(var4.hasNext()) {
         SessionListener listener = (SessionListener)var4.next();
         listener.attributeRemoved(session, name, oldValue);
      }

   }

   public void sessionIdChanged(Session session, String oldSessionId) {
      Iterator var3 = this.sessionListeners.iterator();

      while(var3.hasNext()) {
         SessionListener listener = (SessionListener)var3.next();
         listener.sessionIdChanged(session, oldSessionId);
      }

   }
}
