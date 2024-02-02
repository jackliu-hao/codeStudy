/*     */ package cn.hutool.extra.template.engine.freemarker;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.extra.template.Template;
/*     */ import cn.hutool.extra.template.TemplateConfig;
/*     */ import cn.hutool.extra.template.TemplateEngine;
/*     */ import cn.hutool.extra.template.TemplateException;
/*     */ import freemarker.cache.ClassTemplateLoader;
/*     */ import freemarker.cache.FileTemplateLoader;
/*     */ import freemarker.cache.TemplateLoader;
/*     */ import freemarker.template.Configuration;
/*     */ import java.io.IOException;
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
/*     */ public class FreemarkerEngine
/*     */   implements TemplateEngine
/*     */ {
/*     */   private Configuration cfg;
/*     */   
/*     */   public FreemarkerEngine() {}
/*     */   
/*     */   public FreemarkerEngine(TemplateConfig config) {
/*  40 */     init(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FreemarkerEngine(Configuration freemarkerCfg) {
/*  49 */     init(freemarkerCfg);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateEngine init(TemplateConfig config) {
/*  55 */     if (null == config) {
/*  56 */       config = TemplateConfig.DEFAULT;
/*     */     }
/*  58 */     init(createCfg(config));
/*  59 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(Configuration freemarkerCfg) {
/*  68 */     this.cfg = freemarkerCfg;
/*     */   }
/*     */ 
/*     */   
/*     */   public Template getTemplate(String resource) {
/*  73 */     if (null == this.cfg) {
/*  74 */       init(TemplateConfig.DEFAULT);
/*     */     }
/*     */     try {
/*  77 */       return (Template)FreemarkerTemplate.wrap(this.cfg.getTemplate(resource));
/*  78 */     } catch (IOException e) {
/*  79 */       throw new IORuntimeException(e);
/*  80 */     } catch (Exception e) {
/*  81 */       throw new TemplateException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Configuration createCfg(TemplateConfig config) {
/*  92 */     if (null == config) {
/*  93 */       config = new TemplateConfig();
/*     */     }
/*     */     
/*  96 */     Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
/*  97 */     cfg.setLocalizedLookup(false);
/*  98 */     cfg.setDefaultEncoding(config.getCharset().toString());
/*     */     
/* 100 */     switch (config.getResourceMode()) {
/*     */       case CLASSPATH:
/* 102 */         cfg.setTemplateLoader((TemplateLoader)new ClassTemplateLoader(ClassUtil.getClassLoader(), config.getPath()));
/*     */         break;
/*     */       case FILE:
/*     */         try {
/* 106 */           cfg.setTemplateLoader((TemplateLoader)new FileTemplateLoader(FileUtil.file(config.getPath())));
/* 107 */         } catch (IOException e) {
/* 108 */           throw new IORuntimeException(e);
/*     */         } 
/*     */         break;
/*     */       case WEB_ROOT:
/*     */         try {
/* 113 */           cfg.setTemplateLoader((TemplateLoader)new FileTemplateLoader(FileUtil.file(FileUtil.getWebRoot(), config.getPath())));
/* 114 */         } catch (IOException e) {
/* 115 */           throw new IORuntimeException(e);
/*     */         } 
/*     */         break;
/*     */       case STRING:
/* 119 */         cfg.setTemplateLoader(new SimpleStringTemplateLoader());
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 125 */     return cfg;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\freemarker\FreemarkerEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */