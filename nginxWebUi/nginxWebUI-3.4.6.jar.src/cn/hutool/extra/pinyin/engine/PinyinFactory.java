/*    */ package cn.hutool.extra.pinyin.engine;
/*    */ 
/*    */ import cn.hutool.core.lang.Singleton;
/*    */ import cn.hutool.core.util.ServiceLoaderUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.pinyin.PinyinEngine;
/*    */ import cn.hutool.extra.pinyin.PinyinException;
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
/*    */ public class PinyinFactory
/*    */ {
/*    */   public static PinyinEngine get() {
/* 24 */     return (PinyinEngine)Singleton.get(PinyinEngine.class.getName(), PinyinFactory::create);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static PinyinEngine create() {
/* 34 */     PinyinEngine engine = doCreate();
/* 35 */     StaticLog.debug("Use [{}] Engine As Default.", new Object[] { StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine") });
/* 36 */     return engine;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static PinyinEngine doCreate() {
/* 46 */     PinyinEngine engine = (PinyinEngine)ServiceLoaderUtil.loadFirstAvailable(PinyinEngine.class);
/* 47 */     if (null != engine) {
/* 48 */       return engine;
/*    */     }
/*    */     
/* 51 */     throw new PinyinException("No pinyin jar found ! Please add one of it to your project !");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\pinyin\engine\PinyinFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */