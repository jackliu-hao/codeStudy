package org.noear.solon.web.staticfiles;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.web.staticfiles.integration.XPluginProp;

public class StaticResourceHandler implements Handler {
   private static final String CACHE_CONTROL = "Cache-Control";
   private static final String LAST_MODIFIED = "Last-Modified";
   private static final Date modified_time = new Date();

   public void handle(Context ctx) throws Exception {
      if (!ctx.getHandled()) {
         if (MethodType.GET.name.equals(ctx.method())) {
            String path = ctx.pathNew();
            String suffix = this.findByExtName(path);
            if (!Utils.isEmpty(suffix)) {
               String conentType = StaticMimes.findByExt(suffix);
               if (Utils.isEmpty(conentType)) {
                  conentType = Utils.mime(suffix);
               }

               if (!Utils.isEmpty(conentType)) {
                  URL uri = StaticMappings.find(path);
                  if (uri != null) {
                     ctx.setHandled(true);
                     String modified_since = ctx.header("If-Modified-Since");
                     String modified_now = modified_time.toString();
                     if (modified_since != null && XPluginProp.maxAge() > 0 && modified_since.equals(modified_now)) {
                        ctx.headerSet("Cache-Control", "max-age=" + XPluginProp.maxAge());
                        ctx.headerSet("Last-Modified", modified_now);
                        ctx.status(304);
                     } else {
                        if (XPluginProp.maxAge() > 0) {
                           ctx.headerSet("Cache-Control", "max-age=" + XPluginProp.maxAge());
                           ctx.headerSet("Last-Modified", modified_time.toString());
                        }

                        InputStream stream = uri.openStream();
                        Throwable var9 = null;

                        try {
                           ctx.contentType(conentType);
                           ctx.status(200);
                           ctx.output(stream);
                        } catch (Throwable var18) {
                           var9 = var18;
                           throw var18;
                        } finally {
                           if (stream != null) {
                              if (var9 != null) {
                                 try {
                                    stream.close();
                                 } catch (Throwable var17) {
                                    var9.addSuppressed(var17);
                                 }
                              } else {
                                 stream.close();
                              }
                           }

                        }

                     }
                  }
               }
            }
         }
      }
   }

   private String findByExtName(String path) {
      String ext = "";
      int pos = path.lastIndexOf(35);
      if (pos > 0) {
         path = path.substring(0, pos - 1);
      }

      pos = path.lastIndexOf(46);
      pos = Math.max(pos, path.lastIndexOf(47));
      pos = Math.max(pos, path.lastIndexOf(63));
      if (pos != -1 && path.charAt(pos) == '.') {
         ext = path.substring(pos).toLowerCase();
      }

      return ext;
   }
}
