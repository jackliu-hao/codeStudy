package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class NginxBaseListener implements NginxListener {
   public void enterConfig(NginxParser.ConfigContext ctx) {
   }

   public void exitConfig(NginxParser.ConfigContext ctx) {
   }

   public void enterStatement(NginxParser.StatementContext ctx) {
   }

   public void exitStatement(NginxParser.StatementContext ctx) {
   }

   public void enterGenericStatement(NginxParser.GenericStatementContext ctx) {
   }

   public void exitGenericStatement(NginxParser.GenericStatementContext ctx) {
   }

   public void enterRegexHeaderStatement(NginxParser.RegexHeaderStatementContext ctx) {
   }

   public void exitRegexHeaderStatement(NginxParser.RegexHeaderStatementContext ctx) {
   }

   public void enterBlock(NginxParser.BlockContext ctx) {
   }

   public void exitBlock(NginxParser.BlockContext ctx) {
   }

   public void enterGenericBlockHeader(NginxParser.GenericBlockHeaderContext ctx) {
   }

   public void exitGenericBlockHeader(NginxParser.GenericBlockHeaderContext ctx) {
   }

   public void enterIf_statement(NginxParser.If_statementContext ctx) {
   }

   public void exitIf_statement(NginxParser.If_statementContext ctx) {
   }

   public void enterIf_body(NginxParser.If_bodyContext ctx) {
   }

   public void exitIf_body(NginxParser.If_bodyContext ctx) {
   }

   public void enterRegexp(NginxParser.RegexpContext ctx) {
   }

   public void exitRegexp(NginxParser.RegexpContext ctx) {
   }

   public void enterLocationBlockHeader(NginxParser.LocationBlockHeaderContext ctx) {
   }

   public void exitLocationBlockHeader(NginxParser.LocationBlockHeaderContext ctx) {
   }

   public void enterRewriteStatement(NginxParser.RewriteStatementContext ctx) {
   }

   public void exitRewriteStatement(NginxParser.RewriteStatementContext ctx) {
   }

   public void enterEveryRule(ParserRuleContext ctx) {
   }

   public void exitEveryRule(ParserRuleContext ctx) {
   }

   public void visitTerminal(TerminalNode node) {
   }

   public void visitErrorNode(ErrorNode node) {
   }
}
