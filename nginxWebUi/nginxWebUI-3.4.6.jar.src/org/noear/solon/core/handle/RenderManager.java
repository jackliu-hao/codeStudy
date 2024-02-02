/*     */ package org.noear.solon.core.handle;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.util.PrintUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RenderManager
/*     */   implements Render
/*     */ {
/*  20 */   private static final Map<String, Render> _mapping = new HashMap<>();
/*  21 */   private static final Map<String, Render> _lib = new HashMap<>();
/*     */   private static Render _def;
/*     */   
/*     */   static {
/*  25 */     _def = ((d, c) -> {
/*     */         if (d != null) {
/*     */           c.output(d.toString());
/*     */         }
/*     */       });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  35 */   public static Render global = new RenderManager();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Render get(String name) {
/*  41 */     Render tmp = _lib.get(name);
/*  42 */     if (tmp == null) {
/*  43 */       tmp = _mapping.get(name);
/*     */     }
/*     */     
/*  46 */     return tmp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void register(Render render) {
/*  55 */     if (render == null) {
/*     */       return;
/*     */     }
/*     */     
/*  59 */     _def = render;
/*  60 */     _lib.put(render.getClass().getSimpleName(), render);
/*  61 */     _lib.put(render.getClass().getName(), render);
/*     */     
/*  63 */     PrintUtil.info("View: load: " + render.getClass().getSimpleName());
/*  64 */     PrintUtil.info("View: load: " + render.getClass().getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void mapping(String suffix, Render render) {
/*  74 */     if (suffix == null || render == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  79 */     _mapping.put(suffix, render);
/*     */     
/*  81 */     PrintUtil.info("View: mapping: " + suffix + "=" + render.getName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void mapping(String suffix, String clzName) {
/*  91 */     if (suffix == null || clzName == null) {
/*     */       return;
/*     */     }
/*     */     
/*  95 */     Render render = _lib.get(clzName);
/*  96 */     if (render == null) {
/*  97 */       PrintUtil.warn("solon: " + clzName + " not exists!");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 102 */     _mapping.put(suffix, render);
/*     */     
/* 104 */     PrintUtil.info("View: mapping: " + suffix + "=" + clzName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String renderAndReturn(ModelAndView modelAndView) {
/*     */     try {
/* 112 */       return global.renderAndReturn(modelAndView, Context.current());
/* 113 */     } catch (Throwable ex) {
/* 114 */       ex = Utils.throwableUnwrap(ex);
/* 115 */       if (ex instanceof RuntimeException) {
/* 116 */         throw (RuntimeException)ex;
/*     */       }
/* 118 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String renderAndReturn(Object<String, Object> data, Context ctx) throws Throwable {
/* 128 */     if (data instanceof ModelAndView) {
/* 129 */       ModelAndView mv = (ModelAndView)data;
/*     */       
/* 131 */       if (Utils.isNotEmpty(mv.view())) {
/*     */ 
/*     */ 
/*     */         
/* 135 */         int suffix_idx = mv.view().lastIndexOf(".");
/* 136 */         if (suffix_idx > 0) {
/* 137 */           String suffix = mv.view().substring(suffix_idx);
/* 138 */           Render render1 = _mapping.get(suffix);
/*     */           
/* 140 */           if (render1 != null) {
/*     */ 
/*     */ 
/*     */             
/* 144 */             ctx.attrMap().forEach((k, v) -> mv.putIfAbsent(k, v));
/*     */ 
/*     */ 
/*     */             
/* 148 */             return render1.renderAndReturn(mv, ctx);
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 154 */         return _def.renderAndReturn(mv, ctx);
/*     */       } 
/* 156 */       data = (Object<String, Object>)mv.model();
/*     */     } 
/*     */ 
/*     */     
/* 160 */     Render render = resolveRander(ctx);
/*     */     
/* 162 */     if (render != null) {
/* 163 */       return render.renderAndReturn(data, ctx);
/*     */     }
/*     */ 
/*     */     
/* 167 */     return _def.renderAndReturn(data, ctx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void render(Object<String, Object> data, Context ctx) throws Throwable {
/* 180 */     if (data instanceof org.noear.solon.ext.DataThrowable) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 185 */     if (data instanceof ModelAndView) {
/* 186 */       ModelAndView mv = (ModelAndView)data;
/*     */       
/* 188 */       if (!Utils.isEmpty(mv.view())) {
/*     */ 
/*     */ 
/*     */         
/* 192 */         int suffix_idx = mv.view().lastIndexOf(".");
/* 193 */         if (suffix_idx > 0) {
/* 194 */           String suffix = mv.view().substring(suffix_idx);
/* 195 */           Render render1 = _mapping.get(suffix);
/*     */           
/* 197 */           if (render1 != null) {
/*     */ 
/*     */ 
/*     */             
/* 201 */             ctx.attrMap().forEach((k, v) -> mv.putIfAbsent(k, v));
/*     */ 
/*     */ 
/*     */             
/* 205 */             render1.render(mv, ctx);
/*     */ 
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/*     */         
/* 212 */         _def.render(mv, ctx);
/*     */         return;
/*     */       } 
/* 215 */       data = (Object<String, Object>)mv.model();
/*     */     } 
/*     */ 
/*     */     
/* 219 */     if (data instanceof File) {
/* 220 */       ctx.outputAsFile((File)data);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 225 */     if (data instanceof DownloadedFile) {
/* 226 */       ctx.outputAsFile((DownloadedFile)data);
/*     */       
/*     */       return;
/*     */     } 
/* 230 */     if (data instanceof InputStream) {
/* 231 */       ctx.output((InputStream)data);
/*     */       
/*     */       return;
/*     */     } 
/* 235 */     Render render = resolveRander(ctx);
/*     */     
/* 237 */     if (render != null) {
/* 238 */       render.render(data, ctx);
/*     */     }
/*     */     else {
/*     */       
/* 242 */       _def.render(data, ctx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Render resolveRander(Context ctx) {
/* 258 */     Render render = null;
/* 259 */     String mode = ctx.header("X-Serialization");
/*     */     
/* 261 */     if (Utils.isEmpty(mode)) {
/* 262 */       mode = ctx.<String>attr("@render");
/*     */     }
/*     */     
/* 265 */     if (!Utils.isEmpty(mode)) {
/* 266 */       render = _mapping.get(mode);
/*     */       
/* 268 */       if (render == null) {
/* 269 */         ctx.headerSet("Solon.serialization.mode", "Not supported " + mode);
/*     */       }
/*     */     } 
/*     */     
/* 273 */     if (render == null && 
/* 274 */       ctx.remoting()) {
/* 275 */       render = _mapping.get("@type_json");
/*     */     }
/*     */ 
/*     */     
/* 279 */     if (render == null) {
/* 280 */       render = _mapping.get("@json");
/*     */     }
/*     */     
/* 283 */     return render;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\RenderManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */