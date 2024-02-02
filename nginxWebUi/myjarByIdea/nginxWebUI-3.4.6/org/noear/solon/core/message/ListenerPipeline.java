package org.noear.solon.core.message;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ListenerPipeline implements Listener {
   private List<Listener> chain = new LinkedList();

   public ListenerPipeline next(Listener listener) {
      this.chain.add(listener);
      return this;
   }

   public ListenerPipeline prev(Listener listener) {
      this.chain.add(0, listener);
      return this;
   }

   public void onOpen(Session session) {
      Iterator var2 = this.chain.iterator();

      while(var2.hasNext()) {
         Listener l = (Listener)var2.next();
         l.onOpen(session);
      }

   }

   public void onMessage(Session session, Message message) throws IOException {
      Iterator var3 = this.chain.iterator();

      while(var3.hasNext()) {
         Listener l = (Listener)var3.next();
         l.onMessage(session, message);
      }

   }

   public void onClose(Session session) {
      Iterator var2 = this.chain.iterator();

      while(var2.hasNext()) {
         Listener l = (Listener)var2.next();
         l.onClose(session);
      }

   }

   public void onError(Session session, Throwable error) {
      Iterator var3 = this.chain.iterator();

      while(var3.hasNext()) {
         Listener l = (Listener)var3.next();
         l.onError(session, error);
      }

   }
}
