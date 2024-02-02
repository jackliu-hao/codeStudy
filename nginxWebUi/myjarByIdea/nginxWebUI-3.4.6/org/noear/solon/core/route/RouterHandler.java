package org.noear.solon.core.route;

import java.util.Iterator;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Endpoint;
import org.noear.solon.core.handle.Handler;

public class RouterHandler implements Handler {
   private Router router;

   public RouterHandler(Router router) {
      this.bind(router);
   }

   public void bind(Router router) {
      this.router = router;
   }

   public void handle(Context ctx) throws Throwable {
      if (!ctx.getHandled()) {
         boolean _handled = false;
         boolean _throwabled = false;

         try {
            this.handleMultiple(ctx, Endpoint.before);
            if (!ctx.getHandled()) {
               _handled = this.handleOne(ctx, Endpoint.main);
               ctx.setHandled(_handled);
            }
         } catch (Throwable var8) {
            _throwabled = true;
            if (ctx.errors == null) {
               ctx.errors = var8;
               EventBus.push(var8);
            }

            throw var8;
         } finally {
            this.handleMultiple(ctx, Endpoint.after);
            if (!_throwabled && ctx.status() < 1) {
               if (_handled) {
                  ctx.status(200);
               } else {
                  ctx.status(404);
               }
            }

         }

      }
   }

   protected boolean handleOne(Context ctx, Endpoint endpoint) throws Throwable {
      Handler h = this.router.matchOne(ctx, endpoint);
      if (h != null) {
         h.handle(ctx);
         return ctx.status() != 404;
      } else {
         return false;
      }
   }

   protected void handleMultiple(Context ctx, Endpoint endpoint) throws Throwable {
      Iterator var3 = this.router.matchAll(ctx, endpoint).iterator();

      while(var3.hasNext()) {
         Handler h = (Handler)var3.next();
         h.handle(ctx);
      }

   }
}
