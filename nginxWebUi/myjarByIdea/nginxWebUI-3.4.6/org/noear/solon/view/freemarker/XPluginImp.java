package org.noear.solon.view.freemarker;

import freemarker.template.TemplateDirectiveModel;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.view.freemarker.tags.AuthPermissionsTag;
import org.noear.solon.view.freemarker.tags.AuthRolesTag;

public class XPluginImp implements Plugin {
   public static boolean output_meta = false;

   public void start(AopContext context) {
      output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
      FreemarkerRender render = FreemarkerRender.global();
      context.beanOnloaded((ctx) -> {
         ctx.beanForeach((k, v) -> {
            if ((k.startsWith("view:") || k.startsWith("ftl:")) && TemplateDirectiveModel.class.isAssignableFrom(v.clz())) {
               render.putDirective(k.split(":")[1], (TemplateDirectiveModel)v.raw());
            }

            if (k.startsWith("share:")) {
               render.putVariable(k.split(":")[1], v.raw());
            }
         });
      });
      RenderManager.register(render);
      RenderManager.mapping(".ftl", (Render)render);
      if (Utils.loadClass("org.noear.solon.auth.AuthUtil") != null) {
         render.putDirective("authPermissions", new AuthPermissionsTag());
         render.putDirective("authRoles", new AuthRolesTag());
      }

   }
}
