package org.noear.solon.core.handle;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.noear.solon.Utils;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.DataThrowable;

public class RenderManager implements Render {
   private static final Map<String, Render> _mapping = new HashMap();
   private static final Map<String, Render> _lib = new HashMap();
   private static Render _def = (d, c) -> {
      if (d != null) {
         c.output(d.toString());
      }

   };
   public static Render global = new RenderManager();

   private RenderManager() {
   }

   public static Render get(String name) {
      Render tmp = (Render)_lib.get(name);
      if (tmp == null) {
         tmp = (Render)_mapping.get(name);
      }

      return tmp;
   }

   public static void register(Render render) {
      if (render != null) {
         _def = render;
         _lib.put(render.getClass().getSimpleName(), render);
         _lib.put(render.getClass().getName(), render);
         PrintUtil.info("View: load: " + render.getClass().getSimpleName());
         PrintUtil.info("View: load: " + render.getClass().getName());
      }
   }

   public static void mapping(String suffix, Render render) {
      if (suffix != null && render != null) {
         _mapping.put(suffix, render);
         PrintUtil.info("View: mapping: " + suffix + "=" + render.getName());
      }
   }

   public static void mapping(String suffix, String clzName) {
      if (suffix != null && clzName != null) {
         Render render = (Render)_lib.get(clzName);
         if (render == null) {
            PrintUtil.warn("solon: " + clzName + " not exists!");
         } else {
            _mapping.put(suffix, render);
            PrintUtil.info("View: mapping: " + suffix + "=" + clzName);
         }
      }
   }

   public static String renderAndReturn(ModelAndView modelAndView) {
      try {
         return global.renderAndReturn(modelAndView, Context.current());
      } catch (Throwable var2) {
         Throwable ex = Utils.throwableUnwrap(var2);
         if (ex instanceof RuntimeException) {
            throw (RuntimeException)ex;
         } else {
            throw new RuntimeException(ex);
         }
      }
   }

   public String renderAndReturn(Object data, Context ctx) throws Throwable {
      if (data instanceof ModelAndView) {
         ModelAndView mv = (ModelAndView)data;
         if (Utils.isNotEmpty(mv.view())) {
            int suffix_idx = mv.view().lastIndexOf(".");
            if (suffix_idx > 0) {
               String suffix = mv.view().substring(suffix_idx);
               Render render = (Render)_mapping.get(suffix);
               if (render != null) {
                  ctx.attrMap().forEach((k, v) -> {
                     mv.putIfAbsent(k, v);
                  });
                  return render.renderAndReturn(mv, ctx);
               }
            }

            return _def.renderAndReturn(mv, ctx);
         }

         data = mv.model();
      }

      Render render = this.resolveRander(ctx);
      return render != null ? render.renderAndReturn(data, ctx) : _def.renderAndReturn(data, ctx);
   }

   public void render(Object data, Context ctx) throws Throwable {
      if (!(data instanceof DataThrowable)) {
         if (data instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView)data;
            if (!Utils.isEmpty(mv.view())) {
               int suffix_idx = mv.view().lastIndexOf(".");
               if (suffix_idx > 0) {
                  String suffix = mv.view().substring(suffix_idx);
                  Render render = (Render)_mapping.get(suffix);
                  if (render != null) {
                     ctx.attrMap().forEach((k, v) -> {
                        mv.putIfAbsent(k, v);
                     });
                     render.render(mv, ctx);
                     return;
                  }
               }

               _def.render(mv, ctx);
               return;
            }

            data = mv.model();
         }

         if (data instanceof File) {
            ctx.outputAsFile((File)data);
         } else if (data instanceof DownloadedFile) {
            ctx.outputAsFile((DownloadedFile)data);
         } else if (data instanceof InputStream) {
            ctx.output((InputStream)data);
         } else {
            Render render = this.resolveRander(ctx);
            if (render != null) {
               render.render(data, ctx);
            } else {
               _def.render(data, ctx);
            }

         }
      }
   }

   private Render resolveRander(Context ctx) {
      Render render = null;
      String mode = ctx.header("X-Serialization");
      if (Utils.isEmpty(mode)) {
         mode = (String)ctx.attr("@render");
      }

      if (!Utils.isEmpty(mode)) {
         render = (Render)_mapping.get(mode);
         if (render == null) {
            ctx.headerSet("Solon.serialization.mode", "Not supported " + mode);
         }
      }

      if (render == null && ctx.remoting()) {
         render = (Render)_mapping.get("@type_json");
      }

      if (render == null) {
         render = (Render)_mapping.get("@json");
      }

      return render;
   }
}
