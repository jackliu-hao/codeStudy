package org.noear.solon.socketd;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.annotation.ClientEndpoint;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      Solon.app().listenAfter(new RouterListener());
      context.beanBuilderAdd(ClientEndpoint.class, (clz, wrap, anno) -> {
         if (Listener.class.isAssignableFrom(clz)) {
            Listener l = (Listener)wrap.raw();
            Session s = SocketD.createSession(anno.uri(), anno.autoReconnect());
            s.listener(l);
            if (Utils.isNotEmpty(anno.handshakeHeader())) {
               try {
                  s.sendHandshake(Message.wrapHandshake(anno.handshakeHeader()));
               } catch (Throwable var6) {
                  EventBus.push(var6);
               }
            }

            if (anno.heartbeatRate() > 0) {
               s.sendHeartbeatAuto(anno.heartbeatRate());
            }
         }

      });
   }
}
