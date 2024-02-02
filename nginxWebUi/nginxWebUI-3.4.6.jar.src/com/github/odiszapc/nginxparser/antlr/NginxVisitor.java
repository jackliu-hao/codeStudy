package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public interface NginxVisitor<T> extends ParseTreeVisitor<T> {
  T visitConfig(NginxParser.ConfigContext paramConfigContext);
  
  T visitStatement(NginxParser.StatementContext paramStatementContext);
  
  T visitGenericStatement(NginxParser.GenericStatementContext paramGenericStatementContext);
  
  T visitRegexHeaderStatement(NginxParser.RegexHeaderStatementContext paramRegexHeaderStatementContext);
  
  T visitBlock(NginxParser.BlockContext paramBlockContext);
  
  T visitGenericBlockHeader(NginxParser.GenericBlockHeaderContext paramGenericBlockHeaderContext);
  
  T visitIf_statement(NginxParser.If_statementContext paramIf_statementContext);
  
  T visitIf_body(NginxParser.If_bodyContext paramIf_bodyContext);
  
  T visitRegexp(NginxParser.RegexpContext paramRegexpContext);
  
  T visitLocationBlockHeader(NginxParser.LocationBlockHeaderContext paramLocationBlockHeaderContext);
  
  T visitRewriteStatement(NginxParser.RewriteStatementContext paramRewriteStatementContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\antlr\NginxVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */