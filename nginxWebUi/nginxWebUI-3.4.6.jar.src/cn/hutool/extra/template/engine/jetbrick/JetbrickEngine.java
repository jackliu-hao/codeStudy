/*     */ package cn.hutool.extra.template.engine.jetbrick;
/*     */ 
/*     */ import cn.hutool.extra.template.Template;
/*     */ import cn.hutool.extra.template.TemplateConfig;
/*     */ import cn.hutool.extra.template.TemplateEngine;
/*     */ import java.util.Properties;
/*     */ import jetbrick.template.JetEngine;
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
/*     */ public class JetbrickEngine
/*     */   implements TemplateEngine
/*     */ {
/*     */   private JetEngine engine;
/*     */   
/*     */   public JetbrickEngine() {}
/*     */   
/*     */   public JetbrickEngine(TemplateConfig config) {
/*  33 */     init(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JetbrickEngine(JetEngine engine) {
/*  42 */     init(engine);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateEngine init(TemplateConfig config) {
/*  49 */     init(createEngine(config));
/*  50 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init(JetEngine engine) {
/*  58 */     this.engine = engine;
/*     */   }
/*     */ 
/*     */   
/*     */   public Template getTemplate(String resource) {
/*  63 */     if (null == this.engine) {
/*  64 */       init(TemplateConfig.DEFAULT);
/*     */     }
/*  66 */     return (Template)JetbrickTemplate.wrap(this.engine.getTemplate(resource));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static JetEngine createEngine(TemplateConfig config) {
/*  76 */     if (null == config) {
/*  77 */       config = TemplateConfig.DEFAULT;
/*     */     }
/*     */     
/*  80 */     Properties props = new Properties();
/*  81 */     props.setProperty("jetx.input.encoding", config.getCharsetStr());
/*  82 */     props.setProperty("jetx.output.encoding", config.getCharsetStr());
/*  83 */     props.setProperty("jetx.template.loaders", "$loader");
/*     */     
/*  85 */     switch (config.getResourceMode()) {
/*     */       case CLASSPATH:
/*  87 */         props.setProperty("$loader", "jetbrick.template.loader.ClasspathResourceLoader");
/*  88 */         props.setProperty("$loader.root", config.getPath());
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
/* 107 */         return JetEngine.create(props);case FILE: props.setProperty("$loader", "jetbrick.template.loader.FileSystemResourceLoader"); props.setProperty("$loader.root", config.getPath()); return JetEngine.create(props);case WEB_ROOT: props.setProperty("$loader", "jetbrick.template.loader.ServletResourceLoader"); props.setProperty("$loader.root", config.getPath()); return JetEngine.create(props);case STRING: props.setProperty("$loader", "cn.hutool.extra.template.engine.jetbrick.loader.StringResourceLoader"); props.setProperty("$loader.charset", config.getCharsetStr()); return JetEngine.create(props);
/*     */     } 
/*     */     return JetEngine.create();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\jetbrick\JetbrickEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */