package org.noear.solon.boot.undertow.http;

import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.undertow.XPluginImp;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.servlet.SolonServletHandler;

public class UtHandlerJspHandler extends SolonServletHandler {
   protected void preHandle(Context ctx) {
      if (ServerProps.output_meta) {
         ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
      }

   }
}
