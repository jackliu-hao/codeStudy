/*    */ package org.antlr.v4.runtime.tree;
/*    */ 
/*    */ import org.antlr.v4.runtime.Parser;
/*    */ import org.antlr.v4.runtime.Token;
/*    */ import org.antlr.v4.runtime.misc.Interval;
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
/*    */ public class TerminalNodeImpl
/*    */   implements TerminalNode
/*    */ {
/*    */   public Token symbol;
/*    */   public ParseTree parent;
/*    */   
/*    */   public TerminalNodeImpl(Token symbol) {
/* 41 */     this.symbol = symbol;
/*    */   }
/*    */   public ParseTree getChild(int i) {
/* 44 */     return null;
/*    */   }
/*    */   public Token getSymbol() {
/* 47 */     return this.symbol;
/*    */   }
/*    */   public ParseTree getParent() {
/* 50 */     return this.parent;
/*    */   }
/*    */   public Token getPayload() {
/* 53 */     return this.symbol;
/*    */   }
/*    */   
/*    */   public Interval getSourceInterval() {
/* 57 */     if (this.symbol == null) return Interval.INVALID;
/*    */     
/* 59 */     int tokenIndex = this.symbol.getTokenIndex();
/* 60 */     return new Interval(tokenIndex, tokenIndex);
/*    */   }
/*    */   
/*    */   public int getChildCount() {
/* 64 */     return 0;
/*    */   }
/*    */   
/*    */   public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/* 68 */     return visitor.visitTerminal(this);
/*    */   }
/*    */   
/*    */   public String getText() {
/* 72 */     return this.symbol.getText();
/*    */   }
/*    */   
/*    */   public String toStringTree(Parser parser) {
/* 76 */     return toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     if (this.symbol.getType() == -1) return "<EOF>"; 
/* 82 */     return this.symbol.getText();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toStringTree() {
/* 87 */     return toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\TerminalNodeImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */