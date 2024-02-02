package org.noear.solon.web.staticfiles.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.NvMap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.HandlerPipeline;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.StaticMimes;
import org.noear.solon.web.staticfiles.StaticResourceHandler;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      if (Solon.app().enableStaticfiles()) {
         if (XPluginProp.enable()) {
            XPluginProp.maxAge();
            if (Utils.getResource("static/") != null) {
               StaticMappings.add("/", new ClassPathStaticRepository("static/"));
            }

            NvMap mimeTypes = Solon.cfg().getXmap("solon.mime");
            mimeTypes.forEach((k, v) -> {
               StaticMimes.add("." + k, v);
            });
            HandlerPipeline pipeline = new HandlerPipeline();
            pipeline.next(new StaticResourceHandler()).next(Solon.app().handlerGet());
            Solon.app().handlerSet(pipeline);
         }
      }
   }
}
