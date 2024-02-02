/*    */ package org.apache.http.auth.params;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.params.HttpParams;
/*    */ import org.apache.http.protocol.HTTP;
/*    */ import org.apache.http.util.Args;
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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public final class AuthParams
/*    */ {
/*    */   public static String getCredentialCharset(HttpParams params) {
/* 62 */     Args.notNull(params, "HTTP parameters");
/* 63 */     String charset = (String)params.getParameter("http.auth.credential-charset");
/*    */     
/* 65 */     if (charset == null) {
/* 66 */       charset = HTTP.DEF_PROTOCOL_CHARSET.name();
/*    */     }
/* 68 */     return charset;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void setCredentialCharset(HttpParams params, String charset) {
/* 79 */     Args.notNull(params, "HTTP parameters");
/* 80 */     params.setParameter("http.auth.credential-charset", charset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\params\AuthParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */