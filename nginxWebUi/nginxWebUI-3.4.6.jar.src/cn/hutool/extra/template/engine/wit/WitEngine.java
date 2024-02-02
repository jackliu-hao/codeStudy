/*     */ package cn.hutool.extra.template.engine.wit;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.lang.Dict;
/*     */ import cn.hutool.extra.template.Template;
/*     */ import cn.hutool.extra.template.TemplateConfig;
/*     */ import cn.hutool.extra.template.TemplateEngine;
/*     */ import cn.hutool.extra.template.TemplateException;
/*     */ import java.io.File;
/*     */ import java.util.Map;
/*     */ import org.febit.wit.Engine;
/*     */ import org.febit.wit.exceptions.ResourceNotFoundException;
/*     */ import org.febit.wit.util.Props;
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
/*     */ public class WitEngine
/*     */   implements TemplateEngine
/*     */ {
/*     */   private Engine engine;
/*     */   
/*     */   public WitEngine() {}
/*     */   
/*     */   public WitEngine(TemplateConfig config) {
/*  36 */     init(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WitEngine(Engine engine) {
/*  45 */     init(engine);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateEngine init(TemplateConfig config) {
/*  52 */     init(createEngine(config));
/*  53 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(Engine engine) {
/*  61 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public Template getTemplate(String resource) {
/*  66 */     if (null == this.engine) {
/*  67 */       init(TemplateConfig.DEFAULT);
/*     */     }
/*     */     try {
/*  70 */       return (Template)WitTemplate.wrap(this.engine.getTemplate(resource));
/*  71 */     } catch (ResourceNotFoundException e) {
/*  72 */       throw new TemplateException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Engine createEngine(TemplateConfig config) {
/*  83 */     Props configProps = Engine.createConfigProps("");
/*  84 */     Dict dict = null;
/*     */     
/*  86 */     if (null != config) {
/*  87 */       File root; dict = Dict.create();
/*     */       
/*  89 */       dict.set("DEFAULT_ENCODING", config.getCharset());
/*     */       
/*  91 */       switch (config.getResourceMode()) {
/*     */         case CLASSPATH:
/*  93 */           configProps.set("pathLoader.root", config.getPath());
/*  94 */           configProps.set("routeLoader.defaultLoader", "classpathLoader");
/*     */           break;
/*     */         case STRING:
/*  97 */           configProps.set("routeLoader.defaultLoader", "stringLoader");
/*     */           break;
/*     */         case FILE:
/* 100 */           configProps.set("pathLoader.root", config.getPath());
/* 101 */           configProps.set("routeLoader.defaultLoader", "fileLoader");
/*     */           break;
/*     */         case WEB_ROOT:
/* 104 */           root = FileUtil.file(FileUtil.getWebRoot(), config.getPath());
/* 105 */           configProps.set("pathLoader.root", FileUtil.getAbsolutePath(root));
/* 106 */           configProps.set("routeLoader.defaultLoader", "fileLoader");
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 111 */     return Engine.create(configProps, (Map)dict);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\wit\WitEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */