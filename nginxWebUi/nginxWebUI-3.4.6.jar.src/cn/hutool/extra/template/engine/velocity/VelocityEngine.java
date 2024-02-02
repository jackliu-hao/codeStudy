/*     */ package cn.hutool.extra.template.engine.velocity;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.extra.template.Template;
/*     */ import cn.hutool.extra.template.TemplateConfig;
/*     */ import cn.hutool.extra.template.TemplateEngine;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VelocityEngine
/*     */   implements TemplateEngine
/*     */ {
/*     */   private org.apache.velocity.app.VelocityEngine engine;
/*     */   private TemplateConfig config;
/*     */   
/*     */   public VelocityEngine() {}
/*     */   
/*     */   public VelocityEngine(TemplateConfig config) {
/*  34 */     init(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public VelocityEngine(org.apache.velocity.app.VelocityEngine engine) {
/*  43 */     init(engine);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateEngine init(TemplateConfig config) {
/*  49 */     if (null == config) {
/*  50 */       config = TemplateConfig.DEFAULT;
/*     */     }
/*  52 */     this.config = config;
/*  53 */     init(createEngine(config));
/*  54 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(org.apache.velocity.app.VelocityEngine engine) {
/*  63 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public org.apache.velocity.app.VelocityEngine getRawEngine() {
/*  73 */     return this.engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public Template getTemplate(String resource) {
/*  78 */     if (null == this.engine) {
/*  79 */       init(TemplateConfig.DEFAULT);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  85 */     String charsetStr = null;
/*  86 */     if (null != this.config) {
/*  87 */       String root = this.config.getPath();
/*  88 */       charsetStr = this.config.getCharsetStr();
/*     */ 
/*     */ 
/*     */       
/*  92 */       TemplateConfig.ResourceMode resourceMode = this.config.getResourceMode();
/*  93 */       if (TemplateConfig.ResourceMode.CLASSPATH == resourceMode || TemplateConfig.ResourceMode.WEB_ROOT == resourceMode)
/*     */       {
/*  95 */         resource = StrUtil.addPrefixIfNot(resource, StrUtil.addSuffixIfNot(root, "/"));
/*     */       }
/*     */     } 
/*     */     
/*  99 */     return (Template)VelocityTemplate.wrap(this.engine.getTemplate(resource, charsetStr));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static org.apache.velocity.app.VelocityEngine createEngine(TemplateConfig config) {
/*     */     String path;
/* 109 */     if (null == config) {
/* 110 */       config = new TemplateConfig();
/*     */     }
/*     */     
/* 113 */     org.apache.velocity.app.VelocityEngine ve = new org.apache.velocity.app.VelocityEngine();
/*     */     
/* 115 */     String charsetStr = config.getCharset().toString();
/* 116 */     ve.setProperty("resource.default_encoding", charsetStr);
/*     */     
/* 118 */     ve.setProperty("resource.loader.file.cache", Boolean.valueOf(true));
/*     */ 
/*     */     
/* 121 */     switch (config.getResourceMode()) {
/*     */ 
/*     */       
/*     */       case CLASSPATH:
/* 125 */         ve.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
/*     */         break;
/*     */       
/*     */       case FILE:
/* 129 */         path = config.getPath();
/* 130 */         if (null != path) {
/* 131 */           ve.setProperty("resource.loader.file.path", path);
/*     */         }
/*     */         break;
/*     */       case WEB_ROOT:
/* 135 */         ve.setProperty("resource.loader", "webapp");
/* 136 */         ve.setProperty("webapp.resource.loader.class", "org.apache.velocity.tools.view.servlet.WebappLoader");
/* 137 */         ve.setProperty("webapp.resource.loader.path", StrUtil.nullToDefault(config.getPath(), "/"));
/*     */         break;
/*     */       case STRING:
/* 140 */         ve.setProperty("resource.loader", "str");
/* 141 */         ve.setProperty("str.resource.loader.class", SimpleStringResourceLoader.class.getName());
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 147 */     ve.init();
/* 148 */     return ve;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\velocity\VelocityEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */