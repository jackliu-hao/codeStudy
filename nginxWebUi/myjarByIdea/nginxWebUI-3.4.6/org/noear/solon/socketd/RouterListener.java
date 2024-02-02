package org.noear.solon.socketd;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterListener implements Listener {
   static final Logger log = LoggerFactory.getLogger(RouterListener.class);
   static final ExecutorService executors = Executors.newCachedThreadPool();

   public void onOpen(Session session) {
      executors.submit(() -> {
         this.onOpen0(session);
      });
   }

   private void onOpen0(Session session) {
      try {
         Listener sl = this.get(session);
         if (sl != null) {
            sl.onOpen(session);
         }

         if (session.listener() != null) {
            session.listener().onOpen(session);
         }
      } catch (Throwable var3) {
         EventBus.push(var3);
      }

   }

   public void onMessage(Session session, Message message) throws IOException {
      if (message != null) {
         executors.submit(() -> {
            this.onMessage0(session, message);
         });
      }
   }

   private void onMessage0(Session session, Message message) {
      try {
         log.trace((String)"Listener proxy receive: {}", (Object)message);
         Listener sl = this.get(session);
         if (sl != null) {
            sl.onMessage(session, message);
         }

         if (session.listener() != null) {
            session.listener().onMessage(session, message);
         }

         if (message.flag() == 11) {
            return;
         }

         if (message.flag() == 13) {
            CompletableFuture<Message> request = RequestManager.get(message.key());
            if (request != null) {
               RequestManager.remove(message.key());
               request.complete(message);
               return;
            }
         }

         if (!message.getHandled()) {
            SocketContextHandler.instance.handle(session, message);
         }
      } catch (Throwable var5) {
         this.onError0(session, var5);
         EventBus.push(var5);
      }

   }

   public void onClose(Session session) {
      executors.submit(() -> {
         this.onClose0(session);
      });
   }

   private void onClose0(Session session) {
      try {
         Listener sl = this.get(session);
         if (sl != null) {
            sl.onClose(session);
         }

         if (session.listener() != null) {
            session.listener().onClose(session);
         }
      } catch (Throwable var3) {
         EventBus.push(var3);
      }

   }

   public void onError(Session session, Throwable error) {
      executors.submit(() -> {
         this.onError0(session, error);
      });
   }

   private void onError0(Session session, Throwable error) {
      try {
         Listener sl = this.get(session);
         if (sl != null) {
            sl.onError(session, error);
         }

         if (session.listener() != null) {
            session.listener().onError(session, error);
         }
      } catch (Throwable var4) {
         EventBus.push(var4);
      }

   }

   private Listener get(Session s) {
      return Solon.app().router().matchOne(s);
   }
}
