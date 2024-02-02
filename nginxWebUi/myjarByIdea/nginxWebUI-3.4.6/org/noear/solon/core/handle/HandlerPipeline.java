package org.noear.solon.core.handle;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HandlerPipeline implements Handler {
   private List<Handler> chain = new LinkedList();

   public HandlerPipeline next(Handler handler) {
      this.chain.add(handler);
      return this;
   }

   public HandlerPipeline prev(Handler handler) {
      this.chain.add(0, handler);
      return this;
   }

   public void handle(Context ctx) throws Throwable {
      Iterator var2 = this.chain.iterator();

      while(var2.hasNext()) {
         Handler h = (Handler)var2.next();
         if (ctx.getHandled()) {
            break;
         }

         h.handle(ctx);
      }

   }
}
