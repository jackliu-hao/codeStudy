/*    */ package org.apache.http.cookie;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.util.Args;
/*    */ import org.apache.http.util.TextUtils;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public final class CookieOrigin
/*    */ {
/*    */   private final String host;
/*    */   private final int port;
/*    */   private final String path;
/*    */   private final boolean secure;
/*    */   
/*    */   public CookieOrigin(String host, int port, String path, boolean secure) {
/* 52 */     Args.notBlank(host, "Host");
/* 53 */     Args.notNegative(port, "Port");
/* 54 */     Args.notNull(path, "Path");
/* 55 */     this.host = host.toLowerCase(Locale.ROOT);
/* 56 */     this.port = port;
/* 57 */     if (!TextUtils.isBlank(path)) {
/* 58 */       this.path = path;
/*    */     } else {
/* 60 */       this.path = "/";
/*    */     } 
/* 62 */     this.secure = secure;
/*    */   }
/*    */   
/*    */   public String getHost() {
/* 66 */     return this.host;
/*    */   }
/*    */   
/*    */   public String getPath() {
/* 70 */     return this.path;
/*    */   }
/*    */   
/*    */   public int getPort() {
/* 74 */     return this.port;
/*    */   }
/*    */   
/*    */   public boolean isSecure() {
/* 78 */     return this.secure;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 83 */     StringBuilder buffer = new StringBuilder();
/* 84 */     buffer.append('[');
/* 85 */     if (this.secure) {
/* 86 */       buffer.append("(secure)");
/*    */     }
/* 88 */     buffer.append(this.host);
/* 89 */     buffer.append(':');
/* 90 */     buffer.append(Integer.toString(this.port));
/* 91 */     buffer.append(this.path);
/* 92 */     buffer.append(']');
/* 93 */     return buffer.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\cookie\CookieOrigin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */