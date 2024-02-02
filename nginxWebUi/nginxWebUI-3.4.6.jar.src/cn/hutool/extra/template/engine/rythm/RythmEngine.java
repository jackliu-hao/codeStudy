/*    */ package cn.hutool.extra.template.engine.rythm;
/*    */ 
/*    */ import cn.hutool.extra.template.Template;
/*    */ import cn.hutool.extra.template.TemplateConfig;
/*    */ import cn.hutool.extra.template.TemplateEngine;
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RythmEngine
/*    */   implements TemplateEngine
/*    */ {
/*    */   org.rythmengine.RythmEngine engine;
/*    */   
/*    */   public RythmEngine() {}
/*    */   
/*    */   public RythmEngine(TemplateConfig config) {
/* 32 */     init(config);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RythmEngine(org.rythmengine.RythmEngine engine) {
/* 41 */     init(engine);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public TemplateEngine init(TemplateConfig config) {
/* 47 */     if (null == config) {
/* 48 */       config = TemplateConfig.DEFAULT;
/*    */     }
/* 50 */     init(createEngine(config));
/* 51 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void init(org.rythmengine.RythmEngine engine) {
/* 59 */     this.engine = engine;
/*    */   }
/*    */ 
/*    */   
/*    */   public Template getTemplate(String resource) {
/* 64 */     if (null == this.engine) {
/* 65 */       init(TemplateConfig.DEFAULT);
/*    */     }
/* 67 */     return (Template)RythmTemplate.wrap(this.engine.getTemplate(resource, new Object[0]));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static org.rythmengine.RythmEngine createEngine(TemplateConfig config) {
/* 77 */     if (null == config) {
/* 78 */       config = new TemplateConfig();
/*    */     }
/*    */     
/* 81 */     Properties props = new Properties();
/* 82 */     String path = config.getPath();
/* 83 */     if (null != path) {
/* 84 */       props.put("home.template", path);
/*    */     }
/*    */     
/* 87 */     return new org.rythmengine.RythmEngine(props);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\rythm\RythmEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */