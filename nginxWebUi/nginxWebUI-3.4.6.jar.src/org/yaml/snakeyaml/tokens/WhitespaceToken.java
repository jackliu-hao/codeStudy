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
/*    */ 
/*    */ 
/*    */ public class WhitespaceToken
/*    */   extends Token
/*    */ {
/*    */   public WhitespaceToken(Mark startMark, Mark endMark) {
/* 25 */     super(startMark, endMark);
/*    */   }
/*    */ 
/*    */   
/*    */   public Token.ID getTokenId() {
/* 30 */     return Token.ID.Whitespace;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\tokens\WhitespaceToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */