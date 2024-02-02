/*    */ package org.apache.http.impl;
/*    */ 
/*    */ import org.apache.http.ConnectionReuseStrategy;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.protocol.HttpContext;
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
/*    */ public class NoConnectionReuseStrategy
/*    */   implements ConnectionReuseStrategy
/*    */ {
/* 44 */   public static final NoConnectionReuseStrategy INSTANCE = new NoConnectionReuseStrategy();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean keepAlive(HttpResponse response, HttpContext context) {
/* 52 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\NoConnectionReuseStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */