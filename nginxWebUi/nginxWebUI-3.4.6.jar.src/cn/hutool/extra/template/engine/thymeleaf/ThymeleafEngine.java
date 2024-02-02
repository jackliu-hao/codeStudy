/*     */ package cn.hutool.extra.template.engine.thymeleaf;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.extra.template.Template;
/*     */ import cn.hutool.extra.template.TemplateConfig;
/*     */ import cn.hutool.extra.template.TemplateEngine;
/*     */ import org.thymeleaf.TemplateEngine;
/*     */ import org.thymeleaf.templatemode.TemplateMode;
/*     */ import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
/*     */ import org.thymeleaf.templateresolver.DefaultTemplateResolver;
/*     */ import org.thymeleaf.templateresolver.FileTemplateResolver;
/*     */ import org.thymeleaf.templateresolver.ITemplateResolver;
/*     */ import org.thymeleaf.templateresolver.StringTemplateResolver;
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
/*     */ public class ThymeleafEngine
/*     */   implements TemplateEngine
/*     */ {
/*     */   TemplateEngine engine;
/*     */   TemplateConfig config;
/*     */   
/*     */   public ThymeleafEngine() {}
/*     */   
/*     */   public ThymeleafEngine(TemplateConfig config) {
/*  39 */     init(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThymeleafEngine(TemplateEngine engine) {
/*  48 */     init(engine);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateEngine init(TemplateConfig config) {
/*  54 */     if (null == config) {
/*  55 */       config = TemplateConfig.DEFAULT;
/*     */     }
/*  57 */     this.config = config;
/*  58 */     init(createEngine(config));
/*  59 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(TemplateEngine engine) {
/*  67 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public Template getTemplate(String resource) {
/*  72 */     if (null == this.engine) {
/*  73 */       init(TemplateConfig.DEFAULT);
/*     */     }
/*  75 */     return (Template)ThymeleafTemplate.wrap(this.engine, resource, (null == this.config) ? null : this.config.getCharset());
/*     */   }
/*     */ 
/*     */   
/*     */   private static TemplateEngine createEngine(TemplateConfig config) {
/*     */     ClassLoaderTemplateResolver classLoaderTemplateResolver1;
/*     */     FileTemplateResolver fileTemplateResolver2, fileTemplateResolver1;
/*     */     StringTemplateResolver stringTemplateResolver;
/*     */     ClassLoaderTemplateResolver classLoaderResolver;
/*     */     FileTemplateResolver fileResolver, webRootResolver;
/*  85 */     if (null == config) {
/*  86 */       config = new TemplateConfig();
/*     */     }
/*     */ 
/*     */     
/*  90 */     switch (config.getResourceMode())
/*     */     { case CLASSPATH:
/*  92 */         classLoaderResolver = new ClassLoaderTemplateResolver();
/*  93 */         classLoaderResolver.setCharacterEncoding(config.getCharsetStr());
/*  94 */         classLoaderResolver.setTemplateMode(TemplateMode.HTML);
/*  95 */         classLoaderResolver.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), "/"));
/*  96 */         classLoaderTemplateResolver1 = classLoaderResolver;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 120 */         engine = new TemplateEngine();
/* 121 */         engine.setTemplateResolver((ITemplateResolver)classLoaderTemplateResolver1);
/* 122 */         return engine;case FILE: fileResolver = new FileTemplateResolver(); fileResolver.setCharacterEncoding(config.getCharsetStr()); fileResolver.setTemplateMode(TemplateMode.HTML); fileResolver.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), "/")); fileTemplateResolver2 = fileResolver; engine = new TemplateEngine(); engine.setTemplateResolver((ITemplateResolver)fileTemplateResolver2); return engine;case WEB_ROOT: webRootResolver = new FileTemplateResolver(); webRootResolver.setCharacterEncoding(config.getCharsetStr()); webRootResolver.setTemplateMode(TemplateMode.HTML); webRootResolver.setPrefix(StrUtil.addSuffixIfNot(FileUtil.getAbsolutePath(FileUtil.file(FileUtil.getWebRoot(), config.getPath())), "/")); fileTemplateResolver1 = webRootResolver; engine = new TemplateEngine(); engine.setTemplateResolver((ITemplateResolver)fileTemplateResolver1); return engine;case STRING: stringTemplateResolver = new StringTemplateResolver(); engine = new TemplateEngine(); engine.setTemplateResolver((ITemplateResolver)stringTemplateResolver); return engine; }  DefaultTemplateResolver defaultTemplateResolver = new DefaultTemplateResolver(); TemplateEngine engine = new TemplateEngine(); engine.setTemplateResolver((ITemplateResolver)defaultTemplateResolver); return engine;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\thymeleaf\ThymeleafEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */