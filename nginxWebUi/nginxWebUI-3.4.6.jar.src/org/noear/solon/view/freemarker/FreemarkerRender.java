/*     */ package org.noear.solon.view.freemarker;
/*     */ 
/*     */ import freemarker.cache.CacheStorage;
/*     */ import freemarker.cache.MruCacheStorage;
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateNotFoundException;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.JarClassLoader;
/*     */ import org.noear.solon.core.event.EventBus;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.ModelAndView;
/*     */ import org.noear.solon.core.handle.Render;
/*     */ import org.noear.solon.ext.SupplierEx;
/*     */ 
/*     */ public class FreemarkerRender implements Render {
/*     */   private static FreemarkerRender _global;
/*     */   
/*     */   public static FreemarkerRender global() {
/*  27 */     if (_global == null) {
/*  28 */       _global = new FreemarkerRender();
/*     */     }
/*     */     
/*  31 */     return _global;
/*     */   }
/*     */ 
/*     */   
/*     */   Configuration provider;
/*     */   
/*     */   Configuration provider_debug;
/*  38 */   private String _baseUri = "/WEB-INF/view/";
/*     */ 
/*     */ 
/*     */   
/*     */   public FreemarkerRender() {
/*  43 */     String baseUri = Solon.cfg().get("slon.mvc.view.prefix");
/*     */     
/*  45 */     if (!Utils.isEmpty(baseUri)) {
/*  46 */       this._baseUri = baseUri;
/*     */     }
/*     */     
/*  49 */     forDebug();
/*  50 */     forRelease();
/*     */     
/*  52 */     Solon.app().onSharedAdd((k, v) -> putVariable(k, v));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void forDebug() {
/*  59 */     if (!Solon.cfg().isDebugMode()) {
/*     */       return;
/*     */     }
/*     */     
/*  63 */     if (this.provider_debug != null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  68 */     URL rooturi = Utils.getResource("/");
/*  69 */     if (rooturi == null) {
/*     */       return;
/*     */     }
/*     */     
/*  73 */     this.provider_debug = new Configuration(Configuration.VERSION_2_3_28);
/*  74 */     this.provider_debug.setNumberFormat("#");
/*  75 */     this.provider_debug.setDefaultEncoding("utf-8");
/*     */     
/*  77 */     String rootdir = rooturi.toString().replace("target/classes/", "");
/*  78 */     File dir = null;
/*     */     
/*  80 */     if (rootdir.startsWith("file:")) {
/*  81 */       String dir_str = rootdir + "src/main/resources" + this._baseUri;
/*  82 */       dir = new File(URI.create(dir_str));
/*  83 */       if (!dir.exists()) {
/*  84 */         dir_str = rootdir + "src/main/webapp" + this._baseUri;
/*  85 */         dir = new File(URI.create(dir_str));
/*     */       } 
/*     */     } 
/*     */     
/*     */     try {
/*  90 */       if (dir != null && dir.exists()) {
/*  91 */         this.provider_debug.setDirectoryForTemplateLoading(dir);
/*     */       }
/*     */ 
/*     */       
/*  95 */       EventBus.push(this.provider_debug);
/*  96 */     } catch (Exception ex) {
/*  97 */       EventBus.push(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void forRelease() {
/* 103 */     if (this.provider != null) {
/*     */       return;
/*     */     }
/*     */     
/* 107 */     this.provider = new Configuration(Configuration.VERSION_2_3_28);
/* 108 */     this.provider.setNumberFormat("#");
/* 109 */     this.provider.setDefaultEncoding("utf-8");
/*     */     
/*     */     try {
/* 112 */       this.provider.setClassLoaderForTemplateLoading((ClassLoader)JarClassLoader.global(), this._baseUri);
/* 113 */     } catch (Exception ex) {
/* 114 */       EventBus.push(ex);
/*     */     } 
/*     */     
/* 117 */     this.provider.setCacheStorage((CacheStorage)new MruCacheStorage(0, 2147483647));
/*     */ 
/*     */     
/* 120 */     EventBus.push(this.provider);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends freemarker.template.TemplateDirectiveModel> void putDirective(String name, T obj) {
/* 127 */     putVariable(name, obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putVariable(String name, Object value) {
/*     */     try {
/* 135 */       this.provider.setSharedVariable(name, value);
/*     */       
/* 137 */       if (this.provider_debug != null) {
/* 138 */         this.provider_debug.setSharedVariable(name, value);
/*     */       }
/* 140 */     } catch (Exception ex) {
/* 141 */       EventBus.push(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void render(Object obj, Context ctx) throws Throwable {
/* 147 */     if (obj == null) {
/*     */       return;
/*     */     }
/*     */     
/* 151 */     if (obj instanceof ModelAndView) {
/* 152 */       render_mav((ModelAndView)obj, ctx, () -> ctx.outputStream());
/*     */     } else {
/* 154 */       ctx.output(obj.toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String renderAndReturn(Object obj, Context ctx) throws Throwable {
/* 160 */     if (obj == null) {
/* 161 */       return null;
/*     */     }
/*     */     
/* 164 */     if (obj instanceof ModelAndView) {
/* 165 */       ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 166 */       render_mav((ModelAndView)obj, ctx, () -> outputStream);
/*     */       
/* 168 */       return outputStream.toString();
/*     */     } 
/* 170 */     return obj.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void render_mav(ModelAndView mv, Context ctx, SupplierEx<OutputStream> outputStream) throws Throwable {
/* 175 */     if (ctx.contentTypeNew() == null) {
/* 176 */       ctx.contentType("text/html;charset=utf-8");
/*     */     }
/*     */     
/* 179 */     if (XPluginImp.output_meta) {
/* 180 */       ctx.headerSet("Solon-View", "FreemarkerRender");
/*     */     }
/*     */     
/* 183 */     PrintWriter writer = new PrintWriter((OutputStream)outputStream.get());
/*     */     
/* 185 */     Template template = null;
/*     */     
/* 187 */     if (this.provider_debug != null) {
/*     */       try {
/* 189 */         template = this.provider_debug.getTemplate(mv.view(), Solon.encoding());
/* 190 */       } catch (TemplateNotFoundException templateNotFoundException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 195 */     if (template == null) {
/* 196 */       template = this.provider.getTemplate(mv.view(), Solon.encoding());
/*     */     }
/*     */     
/* 199 */     template.process(mv.model(), writer);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\view\freemarker\FreemarkerRender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */