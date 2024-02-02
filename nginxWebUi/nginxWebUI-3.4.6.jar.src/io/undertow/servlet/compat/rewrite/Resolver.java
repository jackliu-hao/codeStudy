/*    */ package io.undertow.servlet.compat.rewrite;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Resolver
/*    */ {
/*    */   public abstract String resolve(String paramString);
/*    */   
/*    */   public String resolveEnv(String key) {
/* 31 */     return System.getProperty(key);
/*    */   }
/*    */   
/*    */   public abstract String resolveSsl(String paramString);
/*    */   
/*    */   public abstract String resolveHttp(String paramString);
/*    */   
/*    */   public abstract boolean resolveResource(int paramInt, String paramString);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */