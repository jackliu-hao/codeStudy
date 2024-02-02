/*     */ package org.noear.solon.web.staticfiles;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.Date;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.Handler;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.web.staticfiles.integration.XPluginProp;
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
/*     */ public class StaticResourceHandler
/*     */   implements Handler
/*     */ {
/*     */   private static final String CACHE_CONTROL = "Cache-Control";
/*     */   private static final String LAST_MODIFIED = "Last-Modified";
/*     */   
/*     */   public void handle(Context ctx) throws Exception {
/*  29 */     if (ctx.getHandled()) {
/*     */       return;
/*     */     }
/*     */     
/*  33 */     if (!MethodType.GET.name.equals(ctx.method())) {
/*     */       return;
/*     */     }
/*     */     
/*  37 */     String path = ctx.pathNew();
/*     */ 
/*     */     
/*  40 */     String suffix = findByExtName(path);
/*  41 */     if (Utils.isEmpty(suffix)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  46 */     String conentType = StaticMimes.findByExt(suffix);
/*     */     
/*  48 */     if (Utils.isEmpty(conentType)) {
/*  49 */       conentType = Utils.mime(suffix);
/*     */     }
/*     */     
/*  52 */     if (Utils.isEmpty(conentType)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  57 */     URL uri = StaticMappings.find(path);
/*     */     
/*  59 */     if (uri == null) {
/*     */       return;
/*     */     }
/*  62 */     ctx.setHandled(true);
/*     */     
/*  64 */     String modified_since = ctx.header("If-Modified-Since");
/*  65 */     String modified_now = modified_time.toString();
/*     */     
/*  67 */     if (modified_since != null && XPluginProp.maxAge() > 0 && 
/*  68 */       modified_since.equals(modified_now)) {
/*  69 */       ctx.headerSet("Cache-Control", "max-age=" + XPluginProp.maxAge());
/*  70 */       ctx.headerSet("Last-Modified", modified_now);
/*  71 */       ctx.status(304);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  76 */     if (XPluginProp.maxAge() > 0) {
/*  77 */       ctx.headerSet("Cache-Control", "max-age=" + XPluginProp.maxAge());
/*  78 */       ctx.headerSet("Last-Modified", modified_time.toString());
/*     */     } 
/*     */     
/*  81 */     try (InputStream stream = uri.openStream()) {
/*  82 */       ctx.contentType(conentType);
/*  83 */       ctx.status(200);
/*  84 */       ctx.output(stream);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*  89 */   private static final Date modified_time = new Date();
/*     */ 
/*     */   
/*     */   private String findByExtName(String path) {
/*  93 */     String ext = "";
/*  94 */     int pos = path.lastIndexOf('#');
/*  95 */     if (pos > 0) {
/*  96 */       path = path.substring(0, pos - 1);
/*     */     }
/*     */     
/*  99 */     pos = path.lastIndexOf('.');
/* 100 */     pos = Math.max(pos, path.lastIndexOf('/'));
/* 101 */     pos = Math.max(pos, path.lastIndexOf('?'));
/* 102 */     if (pos != -1 && path.charAt(pos) == '.') {
/* 103 */       ext = path.substring(pos).toLowerCase();
/*     */     }
/*     */     
/* 106 */     return ext;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\StaticResourceHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */