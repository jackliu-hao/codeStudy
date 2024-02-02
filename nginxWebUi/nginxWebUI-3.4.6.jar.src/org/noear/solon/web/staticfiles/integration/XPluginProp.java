/*    */ package org.noear.solon.web.staticfiles.integration;
/*    */ 
/*    */ import org.noear.solon.Solon;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XPluginProp
/*    */ {
/*    */   static final String PROP_ENABLE = "solon.staticfiles.enable";
/*    */   static final String PROP_MAX_AGE = "solon.staticfiles.maxAge";
/*    */   static final String RES_STATIC_LOCATION = "static/";
/*    */   
/*    */   public static boolean enable() {
/* 19 */     return Solon.cfg().getBool("solon.staticfiles.enable", true);
/*    */   }
/*    */   
/* 22 */   static int maxAge = -1;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int maxAge() {
/* 28 */     if (maxAge < 0) {
/* 29 */       if (Solon.cfg().isDebugMode()) {
/* 30 */         maxAge = 0;
/*    */       } else {
/* 32 */         maxAge = Solon.cfg().getInt("solon.staticfiles.maxAge", 600);
/*    */       } 
/*    */     }
/*    */     
/* 36 */     return maxAge;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\integration\XPluginProp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */