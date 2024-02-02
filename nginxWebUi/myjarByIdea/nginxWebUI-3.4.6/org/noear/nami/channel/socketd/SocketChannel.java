package org.noear.nami.channel.socketd;

import java.util.Map;
import java.util.function.Supplier;
import org.noear.nami.Channel;
import org.noear.nami.Context;
import org.noear.nami.Encoder;
import org.noear.nami.NamiManager;
import org.noear.nami.Result;
import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.annotation.Handshake;
import org.noear.solon.socketd.util.HeaderUtil;

public class SocketChannel extends SocketChannelBase implements Channel {
   public Supplier<Session> sessions;

   public SocketChannel(Supplier<Session> sessions) {
      this.sessions = sessions;
   }

   public Result call(Context ctx) throws Throwable {
      this.pretreatment(ctx);
      if (ctx.config.getDecoder() == null) {
         throw new IllegalArgumentException("There is no suitable decoder");
      } else {
         ctx.config.getDecoder().pretreatment(ctx);
         Message message = null;
         String message_key = Message.guid();
         int flag = 10;
         if (ctx.method != null) {
            Handshake h = (Handshake)ctx.method.getAnnotation(Handshake.class);
            if (h != null) {
               flag = 12;
               if (Utils.isNotEmpty(h.handshakeHeader())) {
                  Map<String, String> headerMap = HeaderUtil.decodeHeaderMap(h.handshakeHeader());
                  ctx.headers.putAll(headerMap);
               }
            }
         }

         Encoder encoder = ctx.config.getEncoder();
         if (encoder == null) {
            encoder = NamiManager.getEncoder("application/json");
         }

         if (encoder == null) {
            throw new IllegalArgumentException("There is no suitable encoder");
         } else {
            ctx.headers.put("Content-Type", encoder.enctype());
            byte[] bytes = encoder.encode(ctx.body);
            message = new Message(flag, message_key, ctx.url, HeaderUtil.encodeHeaderMap(ctx.headers), bytes);
            Session session = (Session)this.sessions.get();
            if (ctx.config.getHeartbeat() > 0) {
               session.sendHeartbeatAuto(ctx.config.getHeartbeat());
            }

            Message res = session.sendAndResponse(message, ctx.config.getTimeout());
            if (res == null) {
               return null;
            } else {
               Result result = new Result(200, res.body());
               if (Utils.isNotEmpty(res.header())) {
                  Map<String, String> map = HeaderUtil.decodeHeaderMap(res.header());
                  map.forEach((k, v) -> {
                     result.headerAdd(k, v);
                  });
               }

               return result;
            }
         }
      }
   }
}
