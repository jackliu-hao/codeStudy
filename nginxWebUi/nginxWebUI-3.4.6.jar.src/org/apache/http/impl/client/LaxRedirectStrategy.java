/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class LaxRedirectStrategy
/*    */   extends DefaultRedirectStrategy
/*    */ {
/* 48 */   public static final LaxRedirectStrategy INSTANCE = new LaxRedirectStrategy();
/*    */   
/*    */   public LaxRedirectStrategy() {
/* 51 */     super(new String[] { "GET", "POST", "HEAD", "DELETE" });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\LaxRedirectStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */