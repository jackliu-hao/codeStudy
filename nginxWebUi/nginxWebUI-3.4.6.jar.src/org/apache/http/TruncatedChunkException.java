/*    */ package org.apache.http;
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
/*    */ public class TruncatedChunkException
/*    */   extends MalformedChunkCodingException
/*    */ {
/*    */   private static final long serialVersionUID = -23506263930279460L;
/*    */   
/*    */   public TruncatedChunkException(String message) {
/* 45 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TruncatedChunkException(String format, Object... args) {
/* 57 */     super(HttpException.clean(String.format(format, args)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\TruncatedChunkException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */