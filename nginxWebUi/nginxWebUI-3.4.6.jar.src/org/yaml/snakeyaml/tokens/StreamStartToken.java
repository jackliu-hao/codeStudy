/*    */ package org.yaml.snakeyaml.tokens;
/*    */ 
/*    */ import org.yaml.snakeyaml.error.Mark;
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
/*    */ public final class StreamStartToken
/*    */   extends Token
/*    */ {
/*    */   public StreamStartToken(Mark startMark, Mark endMark) {
/* 23 */     super(startMark, endMark);
/*    */   }
/*    */ 
/*    */   
/*    */   public Token.ID getTokenId() {
/* 28 */     return Token.ID.StreamStart;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\tokens\StreamStartToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */