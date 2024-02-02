/*    */ package org.apache.http.impl.cookie;
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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.SAFE)
/*    */ public class BestMatchSpec
/*    */   extends DefaultCookieSpec
/*    */ {
/*    */   public BestMatchSpec(String[] datepatterns, boolean oneHeader) {
/* 46 */     super(datepatterns, oneHeader);
/*    */   }
/*    */   
/*    */   public BestMatchSpec() {
/* 50 */     this(null, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 55 */     return "best-match";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BestMatchSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */