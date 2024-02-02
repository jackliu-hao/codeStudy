package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

public interface NginxListener extends ParseTreeListener {
  void enterConfig(NginxParser.ConfigContext paramConfigContext);
  
  void exitConfig(NginxParser.ConfigContext paramConfigContext);
  
  void enterStatement(NginxParser.StatementContext paramStatementContext);
  
  void exitStatement(NginxParser.StatementContext paramStatementContext);
  
  void enterGenericStatement(NginxParser.GenericStatementContext paramGenericStatementContext);
  
  void exitGenericStatement(NginxParser.GenericStatementContext paramGenericStatementContext);
  
  void enterRegexHeaderStatement(NginxParser.RegexHeaderStatementContext paramRegexHeaderStatementContext);
  
  void exitRegexHeaderStatement(NginxParser.RegexHeaderStatementContext paramRegexHeaderStatementContext);
  
  void enterBlock(NginxParser.BlockContext paramBlockContext);
  
  void exitBlock(NginxParser.BlockContext paramBlockContext);
  
  void enterGenericBlockHeader(NginxParser.GenericBlockHeaderContext paramGenericBlockHeaderContext);
  
  void exitGenericBlockHeader(NginxParser.GenericBlockHeaderContext paramGenericBlockHeaderContext);
  
  void enterIf_statement(NginxParser.If_statementContext paramIf_statementContext);
  
  void exitIf_statement(NginxParser.If_statementContext paramIf_statementContext);
  
  void enterIf_body(NginxParser.If_bodyContext paramIf_bodyContext);
  
  void exitIf_body(NginxParser.If_bodyContext paramIf_bodyContext);
  
  void enterRegexp(NginxParser.RegexpContext paramRegexpContext);
  
  void exitRegexp(NginxParser.RegexpContext paramRegexpContext);
  
  void enterLocationBlockHeader(NginxParser.LocationBlockHeaderContext paramLocationBlockHeaderContext);
  
  void exitLocationBlockHeader(NginxParser.LocationBlockHeaderContext paramLocationBlockHeaderContext);
  
  void enterRewriteStatement(NginxParser.RewriteStatementContext paramRewriteStatementContext);
  
  void exitRewriteStatement(NginxParser.RewriteStatementContext paramRewriteStatementContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\antlr\NginxListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */