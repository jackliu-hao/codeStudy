package org.noear.solon.boot.jlhttp;

import java.io.IOException;
import org.noear.solon.Solon;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.event.EventBus;

public class JlHttpContextHandler implements HTTPServer.ContextHandler {
   public int serve(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
      try {
         return this.handleDo(request, response);
      } catch (Throwable var4) {
         EventBus.push(var4);
         response.sendHeaders(500);
         return 0;
      }
   }

   private int handleDo(HTTPServer.Request request, HTTPServer.Response response) throws IOException {
      JlHttpContext ctx = new JlHttpContext(request, response);
      ctx.contentType("text/plain;charset=UTF-8");
      if (ServerProps.output_meta) {
         ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
      }

      Solon.app().tryHandle(ctx);
      if (ctx.getHandled() && ctx.status() >= 200) {
         ctx.commit();
         return 0;
      } else {
         return 404;
      }
   }
}
