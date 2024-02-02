package org.noear.solon.web.cors;

import java.util.HashMap;
import java.util.Map;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.web.cors.annotation.CrossOrigin;

public class CrossOriginInterceptor implements Handler {
   Map<CrossOrigin, CrossHandler> handlerMap = new HashMap();

   public void handle(Context ctx) throws Throwable {
      if (!ctx.getHandled()) {
         Action action = ctx.action();
         if (action != null) {
            CrossOrigin anno = (CrossOrigin)action.method().getAnnotation(CrossOrigin.class);
            if (anno == null) {
               anno = (CrossOrigin)action.controller().annotationGet(CrossOrigin.class);
            }

            if (anno == null) {
               return;
            }

            this.handleDo(ctx, anno);
         }

      }
   }

   protected void handleDo(Context ctx, CrossOrigin anno) throws Throwable {
      CrossHandler handler = (CrossHandler)this.handlerMap.get(anno);
      if (handler == null) {
         synchronized(anno) {
            handler = (CrossHandler)this.handlerMap.get(anno);
            if (handler == null) {
               handler = new CrossHandler(anno);
               this.handlerMap.put(anno, handler);
            }
         }
      }

      handler.handle(ctx);
   }
}
