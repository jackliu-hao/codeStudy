/*    */ package cn.hutool.extra.expression.engine;
/*    */ 
/*    */ import cn.hutool.core.lang.Singleton;
/*    */ import cn.hutool.core.util.ServiceLoaderUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.expression.ExpressionEngine;
/*    */ import cn.hutool.extra.expression.ExpressionException;
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
/*    */ 
/*    */ public class ExpressionFactory
/*    */ {
/*    */   public static ExpressionEngine get() {
/* 25 */     return (ExpressionEngine)Singleton.get(ExpressionEngine.class.getName(), ExpressionFactory::create);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ExpressionEngine create() {
/* 35 */     ExpressionEngine engine = doCreate();
/* 36 */     StaticLog.debug("Use [{}] Engine As Default.", new Object[] { StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine") });
/* 37 */     return engine;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static ExpressionEngine doCreate() {
/* 47 */     ExpressionEngine engine = (ExpressionEngine)ServiceLoaderUtil.loadFirstAvailable(ExpressionEngine.class);
/* 48 */     if (null != engine) {
/* 49 */       return engine;
/*    */     }
/*    */     
/* 52 */     throw new ExpressionException("No expression jar found ! Please add one of it to your project !");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\expression\engine\ExpressionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */