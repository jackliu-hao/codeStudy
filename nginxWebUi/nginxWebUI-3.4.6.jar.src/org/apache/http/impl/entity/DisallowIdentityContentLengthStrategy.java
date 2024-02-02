/*    */ package org.apache.http.impl.entity;
/*    */ 
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpMessage;
/*    */ import org.apache.http.ProtocolException;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.entity.ContentLengthStrategy;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*    */ public class DisallowIdentityContentLengthStrategy
/*    */   implements ContentLengthStrategy
/*    */ {
/* 46 */   public static final DisallowIdentityContentLengthStrategy INSTANCE = new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0));
/*    */ 
/*    */   
/*    */   private final ContentLengthStrategy contentLengthStrategy;
/*    */ 
/*    */   
/*    */   public DisallowIdentityContentLengthStrategy(ContentLengthStrategy contentLengthStrategy) {
/* 53 */     this.contentLengthStrategy = contentLengthStrategy;
/*    */   }
/*    */ 
/*    */   
/*    */   public long determineLength(HttpMessage message) throws HttpException {
/* 58 */     long result = this.contentLengthStrategy.determineLength(message);
/* 59 */     if (result == -1L) {
/* 60 */       throw new ProtocolException("Identity transfer encoding cannot be used");
/*    */     }
/* 62 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\entity\DisallowIdentityContentLengthStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */