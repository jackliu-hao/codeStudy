/*    */ package ch.qos.logback.core.rolling.helper;
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
/*    */ public class TokenConverter
/*    */ {
/*    */   static final int IDENTITY = 0;
/*    */   static final int INTEGER = 1;
/*    */   static final int DATE = 1;
/*    */   int type;
/*    */   TokenConverter next;
/*    */   
/*    */   protected TokenConverter(int t) {
/* 35 */     this.type = t;
/*    */   }
/*    */   
/*    */   public TokenConverter getNext() {
/* 39 */     return this.next;
/*    */   }
/*    */   
/*    */   public void setNext(TokenConverter next) {
/* 43 */     this.next = next;
/*    */   }
/*    */   
/*    */   public int getType() {
/* 47 */     return this.type;
/*    */   }
/*    */   
/*    */   public void setType(int i) {
/* 51 */     this.type = i;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\helper\TokenConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */