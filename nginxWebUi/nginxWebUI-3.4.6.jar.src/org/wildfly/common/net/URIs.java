/*    */ package org.wildfly.common.net;
/*    */ 
/*    */ import java.net.URI;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class URIs
/*    */ {
/*    */   public static String getUserFromURI(URI uri) {
/* 36 */     String userInfo = uri.getUserInfo();
/* 37 */     if (userInfo == null && "domain".equals(uri.getScheme())) {
/* 38 */       String ssp = uri.getSchemeSpecificPart();
/* 39 */       int at = ssp.lastIndexOf('@');
/* 40 */       if (at == -1) {
/* 41 */         return null;
/*    */       }
/* 43 */       userInfo = ssp.substring(0, at);
/*    */     } 
/* 45 */     if (userInfo != null) {
/* 46 */       int colon = userInfo.indexOf(':');
/* 47 */       if (colon != -1) {
/* 48 */         userInfo = userInfo.substring(0, colon);
/*    */       }
/*    */     } 
/* 51 */     return userInfo;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\net\URIs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */