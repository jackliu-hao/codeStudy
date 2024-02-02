package org.noear.solon.socketd;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

public class SocketContextHandler {
   public static final SocketContextHandler instance = new SocketContextHandler();

   public void handle(Session session, Message message) {
      if (message != null) {
         if (!Utils.isEmpty(message.resourceDescriptor())) {
            try {
               SocketContext ctx = new SocketContext(session, message);
               Solon.app().tryHandle(ctx);
               if (ctx.getHandled() || ctx.status() != 404) {
                  ctx.commit();
               }
            } catch (Throwable var4) {
               EventBus.push(var4);
            }

         }
      }
   }
}
