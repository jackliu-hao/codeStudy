/*    */ package cn.hutool.extra.template.engine;
/*    */ 
/*    */ import cn.hutool.core.lang.Singleton;
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import cn.hutool.core.util.ServiceLoaderUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.template.TemplateConfig;
/*    */ import cn.hutool.extra.template.TemplateEngine;
/*    */ import cn.hutool.extra.template.TemplateException;
/*    */ import cn.hutool.log.StaticLog;
/*    */ import java.lang.invoke.SerializedLambda;
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
/*    */ public class TemplateFactory
/*    */ {
/*    */   public static TemplateEngine get() {
/* 26 */     return (TemplateEngine)Singleton.get(TemplateEngine.class.getName(), TemplateFactory::create);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static TemplateEngine create() {
/* 37 */     return create(new TemplateConfig());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static TemplateEngine create(TemplateConfig config) {
/* 48 */     TemplateEngine engine = doCreate(config);
/* 49 */     StaticLog.debug("Use [{}] Engine As Default.", new Object[] { StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine") });
/* 50 */     return engine;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static TemplateEngine doCreate(TemplateConfig config) {
/*    */     TemplateEngine engine;
/* 61 */     Class<? extends TemplateEngine> customEngineClass = config.getCustomEngine();
/*    */     
/* 63 */     if (null != customEngineClass) {
/* 64 */       engine = (TemplateEngine)ReflectUtil.newInstance(customEngineClass, new Object[0]);
/*    */     } else {
/* 66 */       engine = (TemplateEngine)ServiceLoaderUtil.loadFirstAvailable(TemplateEngine.class);
/*    */     } 
/* 68 */     if (null != engine) {
/* 69 */       return engine.init(config);
/*    */     }
/*    */     
/* 72 */     throw new TemplateException("No template found ! Please add one of template jar to your project !");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\template\engine\TemplateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */