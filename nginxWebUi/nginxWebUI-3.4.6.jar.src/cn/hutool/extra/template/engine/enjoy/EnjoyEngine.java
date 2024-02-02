/*     */ package cn.hutool.extra.template.engine.enjoy;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.IdUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.extra.template.Template;
/*     */ import cn.hutool.extra.template.TemplateConfig;
/*     */ import cn.hutool.extra.template.TemplateEngine;
/*     */ import com.jfinal.template.Engine;
/*     */ import com.jfinal.template.source.FileSourceFactory;
/*     */ import com.jfinal.template.source.ISourceFactory;
/*     */ import java.io.File;
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
/*     */ public class EnjoyEngine
/*     */   implements TemplateEngine
/*     */ {
/*     */   private Engine engine;
/*     */   private TemplateConfig.ResourceMode resourceMode;
/*     */   
/*     */   public EnjoyEngine() {}
/*     */   
/*     */   public EnjoyEngine(TemplateConfig config) {
/*  38 */     init(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnjoyEngine(Engine engine) {
/*  47 */     init(engine);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateEngine init(TemplateConfig config) {
/*  53 */     if (null == config) {
/*  54 */       config = TemplateConfig.DEFAULT;
/*     */     }
/*  56 */     this.resourceMode = config.getResourceMode();
/*  57 */     init(createEngine(config));
/*  58 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(Engine engine) {
/*  66 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public Template getTemplate(String resource) {
/*  71 */     if (null == this.engine) {
/*  72 */       init(TemplateConfig.DEFAULT);
/*     */     }
/*  74 */     if (ObjectUtil.equal(TemplateConfig.ResourceMode.STRING, this.resourceMode)) {
/*  75 */       return (Template)EnjoyTemplate.wrap(this.engine.getTemplateByString(resource));
/*     */     }
/*  77 */     return (Template)EnjoyTemplate.wrap(this.engine.getTemplate(resource));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Engine createEngine(TemplateConfig config) {
/*     */     File root;
/*  87 */     Engine engine = Engine.create("Hutool-Enjoy-Engine-" + IdUtil.fastSimpleUUID());
/*  88 */     engine.setEncoding(config.getCharsetStr());
/*     */     
/*  90 */     switch (config.getResourceMode()) {
/*     */ 
/*     */ 
/*     */       
/*     */       case CLASSPATH:
/*  95 */         engine.setToClassPathSourceFactory();
/*  96 */         engine.setBaseTemplatePath(config.getPath());
/*     */         break;
/*     */       case FILE:
/*  99 */         engine.setSourceFactory((ISourceFactory)new FileSourceFactory());
/* 100 */         engine.setBaseTemplatePath(config.getPath());
/*     */         break;
/*     */       case WEB_ROOT:
/* 103 */         engine.setSourceFactory((ISourceFactory)new FileSourceFactory());
/* 104 */         root = FileUtil.file(FileUtil.getWebRoot(), config.getPath());
/* 105 */         engine.setBaseTemplatePath(FileUtil.getAbsolutePath(root));
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 111 */     return engine;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\enjoy\EnjoyEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */