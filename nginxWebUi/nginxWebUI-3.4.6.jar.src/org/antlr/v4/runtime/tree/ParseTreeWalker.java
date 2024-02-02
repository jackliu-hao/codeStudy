/*    */ package org.antlr.v4.runtime.tree;
/*    */ 
/*    */ import org.antlr.v4.runtime.ParserRuleContext;
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
/*    */ public class ParseTreeWalker
/*    */ {
/* 37 */   public static final ParseTreeWalker DEFAULT = new ParseTreeWalker();
/*    */   
/*    */   public void walk(ParseTreeListener listener, ParseTree t) {
/* 40 */     if (t instanceof ErrorNode) {
/* 41 */       listener.visitErrorNode((ErrorNode)t);
/*    */       return;
/*    */     } 
/* 44 */     if (t instanceof TerminalNode) {
/* 45 */       listener.visitTerminal((TerminalNode)t);
/*    */       return;
/*    */     } 
/* 48 */     RuleNode r = (RuleNode)t;
/* 49 */     enterRule(listener, r);
/* 50 */     int n = r.getChildCount();
/* 51 */     for (int i = 0; i < n; i++) {
/* 52 */       walk(listener, r.getChild(i));
/*    */     }
/* 54 */     exitRule(listener, r);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void enterRule(ParseTreeListener listener, RuleNode r) {
/* 64 */     ParserRuleContext ctx = (ParserRuleContext)r.getRuleContext();
/* 65 */     listener.enterEveryRule(ctx);
/* 66 */     ctx.enterRule(listener);
/*    */   }
/*    */   
/*    */   protected void exitRule(ParseTreeListener listener, RuleNode r) {
/* 70 */     ParserRuleContext ctx = (ParserRuleContext)r.getRuleContext();
/* 71 */     ctx.exitRule(listener);
/* 72 */     listener.exitEveryRule(ctx);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\ParseTreeWalker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */