package org.noear.solon.view.freemarker;

import freemarker.cache.MruCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.ext.SupplierEx;

public class FreemarkerRender implements Render {
   private static FreemarkerRender _global;
   Configuration provider;
   Configuration provider_debug;
   private String _baseUri = "/WEB-INF/view/";

   public static FreemarkerRender global() {
      if (_global == null) {
         _global = new FreemarkerRender();
      }

      return _global;
   }

   public FreemarkerRender() {
      String baseUri = Solon.cfg().get("slon.mvc.view.prefix");
      if (!Utils.isEmpty(baseUri)) {
         this._baseUri = baseUri;
      }

      this.forDebug();
      this.forRelease();
      Solon.app().onSharedAdd((k, v) -> {
         this.putVariable(k, v);
      });
   }

   private void forDebug() {
      if (Solon.cfg().isDebugMode()) {
         if (this.provider_debug == null) {
            URL rooturi = Utils.getResource("/");
            if (rooturi != null) {
               this.provider_debug = new Configuration(Configuration.VERSION_2_3_28);
               this.provider_debug.setNumberFormat("#");
               this.provider_debug.setDefaultEncoding("utf-8");
               String rootdir = rooturi.toString().replace("target/classes/", "");
               File dir = null;
               if (rootdir.startsWith("file:")) {
                  String dir_str = rootdir + "src/main/resources" + this._baseUri;
                  dir = new File(URI.create(dir_str));
                  if (!dir.exists()) {
                     dir_str = rootdir + "src/main/webapp" + this._baseUri;
                     dir = new File(URI.create(dir_str));
                  }
               }

               try {
                  if (dir != null && dir.exists()) {
                     this.provider_debug.setDirectoryForTemplateLoading(dir);
                  }

                  EventBus.push(this.provider_debug);
               } catch (Exception var5) {
                  EventBus.push(var5);
               }

            }
         }
      }
   }

   private void forRelease() {
      if (this.provider == null) {
         this.provider = new Configuration(Configuration.VERSION_2_3_28);
         this.provider.setNumberFormat("#");
         this.provider.setDefaultEncoding("utf-8");

         try {
            this.provider.setClassLoaderForTemplateLoading(JarClassLoader.global(), this._baseUri);
         } catch (Exception var2) {
            EventBus.push(var2);
         }

         this.provider.setCacheStorage(new MruCacheStorage(0, Integer.MAX_VALUE));
         EventBus.push(this.provider);
      }
   }

   public <T extends TemplateDirectiveModel> void putDirective(String name, T obj) {
      this.putVariable(name, obj);
   }

   public void putVariable(String name, Object value) {
      try {
         this.provider.setSharedVariable(name, value);
         if (this.provider_debug != null) {
            this.provider_debug.setSharedVariable(name, value);
         }
      } catch (Exception var4) {
         EventBus.push(var4);
      }

   }

   public void render(Object obj, Context ctx) throws Throwable {
      if (obj != null) {
         if (obj instanceof ModelAndView) {
            this.render_mav((ModelAndView)obj, ctx, () -> {
               return ctx.outputStream();
            });
         } else {
            ctx.output(obj.toString());
         }

      }
   }

   public String renderAndReturn(Object obj, Context ctx) throws Throwable {
      if (obj == null) {
         return null;
      } else if (obj instanceof ModelAndView) {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         this.render_mav((ModelAndView)obj, ctx, () -> {
            return outputStream;
         });
         return outputStream.toString();
      } else {
         return obj.toString();
      }
   }

   public void render_mav(ModelAndView mv, Context ctx, SupplierEx<OutputStream> outputStream) throws Throwable {
      if (ctx.contentTypeNew() == null) {
         ctx.contentType("text/html;charset=utf-8");
      }

      if (XPluginImp.output_meta) {
         ctx.headerSet("Solon-View", "FreemarkerRender");
      }

      PrintWriter writer = new PrintWriter((OutputStream)outputStream.get());
      Template template = null;
      if (this.provider_debug != null) {
         try {
            template = this.provider_debug.getTemplate(mv.view(), Solon.encoding());
         } catch (TemplateNotFoundException var7) {
         }
      }

      if (template == null) {
         template = this.provider.getTemplate(mv.view(), Solon.encoding());
      }

      template.process(mv.model(), writer);
   }
}
