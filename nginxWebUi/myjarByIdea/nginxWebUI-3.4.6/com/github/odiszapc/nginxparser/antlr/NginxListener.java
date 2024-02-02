package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

public interface NginxListener extends ParseTreeListener {
   void enterConfig(NginxParser.ConfigContext var1);

   void exitConfig(NginxParser.ConfigContext var1);

   void enterStatement(NginxParser.StatementContext var1);

   void exitStatement(NginxParser.StatementContext var1);

   void enterGenericStatement(NginxParser.GenericStatementContext var1);

   void exitGenericStatement(NginxParser.GenericStatementContext var1);

   void enterRegexHeaderStatement(NginxParser.RegexHeaderStatementContext var1);

   void exitRegexHeaderStatement(NginxParser.RegexHeaderStatementContext var1);

   void enterBlock(NginxParser.BlockContext var1);

   void exitBlock(NginxParser.BlockContext var1);

   void enterGenericBlockHeader(NginxParser.GenericBlockHeaderContext var1);

   void exitGenericBlockHeader(NginxParser.GenericBlockHeaderContext var1);

   void enterIf_statement(NginxParser.If_statementContext var1);

   void exitIf_statement(NginxParser.If_statementContext var1);

   void enterIf_body(NginxParser.If_bodyContext var1);

   void exitIf_body(NginxParser.If_bodyContext var1);

   void enterRegexp(NginxParser.RegexpContext var1);

   void exitRegexp(NginxParser.RegexpContext var1);

   void enterLocationBlockHeader(NginxParser.LocationBlockHeaderContext var1);

   void exitLocationBlockHeader(NginxParser.LocationBlockHeaderContext var1);

   void enterRewriteStatement(NginxParser.RewriteStatementContext var1);

   void exitRewriteStatement(NginxParser.RewriteStatementContext var1);
}
