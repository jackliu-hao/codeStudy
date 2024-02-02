/*     */ package cn.hutool.extra.template.engine.beetl;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.extra.template.Template;
/*     */ import cn.hutool.extra.template.TemplateConfig;
/*     */ import cn.hutool.extra.template.TemplateEngine;
/*     */ import java.io.IOException;
/*     */ import org.beetl.core.Configuration;
/*     */ import org.beetl.core.GroupTemplate;
/*     */ import org.beetl.core.ResourceLoader;
/*     */ import org.beetl.core.resource.ClasspathResourceLoader;
/*     */ import org.beetl.core.resource.CompositeResourceLoader;
/*     */ import org.beetl.core.resource.FileResourceLoader;
/*     */ import org.beetl.core.resource.StringTemplateResourceLoader;
/*     */ import org.beetl.core.resource.WebAppResourceLoader;
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
/*     */ public class BeetlEngine
/*     */   implements TemplateEngine
/*     */ {
/*     */   private GroupTemplate engine;
/*     */   
/*     */   public BeetlEngine() {}
/*     */   
/*     */   public BeetlEngine(TemplateConfig config) {
/*  39 */     init(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BeetlEngine(GroupTemplate engine) {
/*  48 */     init(engine);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateEngine init(TemplateConfig config) {
/*  55 */     init(createEngine(config));
/*  56 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(GroupTemplate engine) {
/*  64 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public Template getTemplate(String resource) {
/*  69 */     if (null == this.engine) {
/*  70 */       init(TemplateConfig.DEFAULT);
/*     */     }
/*  72 */     return (Template)BeetlTemplate.wrap(this.engine.getTemplate(resource));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static GroupTemplate createEngine(TemplateConfig config) {
/*  82 */     if (null == config) {
/*  83 */       config = TemplateConfig.DEFAULT;
/*     */     }
/*     */     
/*  86 */     switch (config.getResourceMode()) {
/*     */       case CLASSPATH:
/*  88 */         return createGroupTemplate((ResourceLoader<?>)new ClasspathResourceLoader(config.getPath(), config.getCharsetStr()));
/*     */       case FILE:
/*  90 */         return createGroupTemplate((ResourceLoader<?>)new FileResourceLoader(config.getPath(), config.getCharsetStr()));
/*     */       case WEB_ROOT:
/*  92 */         return createGroupTemplate((ResourceLoader<?>)new WebAppResourceLoader(config.getPath(), config.getCharsetStr()));
/*     */       case STRING:
/*  94 */         return createGroupTemplate((ResourceLoader<?>)new StringTemplateResourceLoader());
/*     */       
/*     */       case COMPOSITE:
/*  97 */         return createGroupTemplate((ResourceLoader<?>)new CompositeResourceLoader());
/*     */     } 
/*  99 */     return new GroupTemplate();
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
/*     */   private static GroupTemplate createGroupTemplate(ResourceLoader<?> loader) {
/*     */     try {
/* 113 */       return createGroupTemplate(loader, Configuration.defaultConfiguration());
/* 114 */     } catch (IOException e) {
/* 115 */       throw new IORuntimeException(e);
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
/*     */   private static GroupTemplate createGroupTemplate(ResourceLoader<?> loader, Configuration conf) {
/* 127 */     return new GroupTemplate(loader, conf);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\beetl\BeetlEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */