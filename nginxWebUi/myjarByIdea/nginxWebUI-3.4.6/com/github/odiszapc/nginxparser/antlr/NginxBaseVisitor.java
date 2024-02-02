package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

public class NginxBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements NginxVisitor<T> {
   public T visitConfig(NginxParser.ConfigContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitStatement(NginxParser.StatementContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitGenericStatement(NginxParser.GenericStatementContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitRegexHeaderStatement(NginxParser.RegexHeaderStatementContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitBlock(NginxParser.BlockContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitGenericBlockHeader(NginxParser.GenericBlockHeaderContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitIf_statement(NginxParser.If_statementContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitIf_body(NginxParser.If_bodyContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitRegexp(NginxParser.RegexpContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitLocationBlockHeader(NginxParser.LocationBlockHeaderContext ctx) {
      return this.visitChildren(ctx);
   }

   public T visitRewriteStatement(NginxParser.RewriteStatementContext ctx) {
      return this.visitChildren(ctx);
   }
}
