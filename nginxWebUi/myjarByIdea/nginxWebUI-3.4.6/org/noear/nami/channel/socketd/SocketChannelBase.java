package org.noear.nami.channel.socketd;

import org.noear.nami.Context;
import org.noear.nami.NamiManager;

public class SocketChannelBase {
   protected void pretreatment(Context ctx) {
      String ct;
      if (ctx.config.getDecoder() == null) {
         ct = ctx.config.getHeader("Accept");
         if (ct == null) {
            ct = "application/json";
         }

         ctx.config.setDecoder(NamiManager.getDecoder(ct));
      }

      if (ctx.config.getEncoder() == null) {
         ct = ctx.config.getHeader("Content-Type");
         if (ct == null) {
            ct = "application/json";
         }

         ctx.config.setEncoder(NamiManager.getEncoder(ct));
      }

   }
}
