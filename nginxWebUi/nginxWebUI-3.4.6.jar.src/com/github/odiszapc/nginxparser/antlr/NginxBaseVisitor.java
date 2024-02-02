/*    */ package com.github.odiszapc.nginxparser.antlr;
/*    */ 
/*    */ import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
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
/*    */ public class NginxBaseVisitor<T>
/*    */   extends AbstractParseTreeVisitor<T>
/*    */   implements NginxVisitor<T>
/*    */ {
/*    */   public T visitConfig(NginxParser.ConfigContext ctx) {
/* 23 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitStatement(NginxParser.StatementContext ctx) {
/* 30 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitGenericStatement(NginxParser.GenericStatementContext ctx) {
/* 37 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitRegexHeaderStatement(NginxParser.RegexHeaderStatementContext ctx) {
/* 44 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitBlock(NginxParser.BlockContext ctx) {
/* 51 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitGenericBlockHeader(NginxParser.GenericBlockHeaderContext ctx) {
/* 58 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitIf_statement(NginxParser.If_statementContext ctx) {
/* 65 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitIf_body(NginxParser.If_bodyContext ctx) {
/* 72 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitRegexp(NginxParser.RegexpContext ctx) {
/* 79 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitLocationBlockHeader(NginxParser.LocationBlockHeaderContext ctx) {
/* 86 */     return visitChildren(ctx);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T visitRewriteStatement(NginxParser.RewriteStatementContext ctx) {
/* 93 */     return visitChildren(ctx);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\antlr\NginxBaseVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */