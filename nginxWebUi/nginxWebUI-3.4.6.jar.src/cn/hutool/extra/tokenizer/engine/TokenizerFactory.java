/*    */ package cn.hutool.extra.tokenizer.engine;
/*    */ 
/*    */ import cn.hutool.core.lang.Singleton;
/*    */ import cn.hutool.core.util.ServiceLoaderUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import cn.hutool.extra.tokenizer.TokenizerException;
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
/*    */ 
/*    */ public class TokenizerFactory
/*    */ {
/*    */   public static TokenizerEngine get() {
/* 26 */     return (TokenizerEngine)Singleton.get(TokenizerEngine.class.getName(), TokenizerFactory::create);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static TokenizerEngine create() {
/* 35 */     TokenizerEngine engine = doCreate();
/* 36 */     StaticLog.debug("Use [{}] Tokenizer Engine As Default.", new Object[] { StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine") });
/* 37 */     return engine;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static TokenizerEngine doCreate() {
/* 46 */     TokenizerEngine engine = (TokenizerEngine)ServiceLoaderUtil.loadFirstAvailable(TokenizerEngine.class);
/* 47 */     if (null != engine) {
/* 48 */       return engine;
/*    */     }
/*    */     
/* 51 */     throw new TokenizerException("No tokenizer found ! Please add some tokenizer jar to your project !");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\TokenizerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */