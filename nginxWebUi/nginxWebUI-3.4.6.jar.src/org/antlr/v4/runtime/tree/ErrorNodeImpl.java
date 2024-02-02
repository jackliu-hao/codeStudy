/*    */ package org.antlr.v4.runtime.tree;
/*    */ 
/*    */ import org.antlr.v4.runtime.Token;
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
/*    */ public class ErrorNodeImpl
/*    */   extends TerminalNodeImpl
/*    */   implements ErrorNode
/*    */ {
/*    */   public ErrorNodeImpl(Token token) {
/* 43 */     super(token);
/*    */   }
/*    */ 
/*    */   
/*    */   public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/* 48 */     return visitor.visitErrorNode(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\ErrorNodeImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */