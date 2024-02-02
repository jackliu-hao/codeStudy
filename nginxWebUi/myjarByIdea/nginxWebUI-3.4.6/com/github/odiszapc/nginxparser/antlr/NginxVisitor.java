package com.github.odiszapc.nginxparser.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

public interface NginxVisitor<T> extends ParseTreeVisitor<T> {
   T visitConfig(NginxParser.ConfigContext var1);

   T visitStatement(NginxParser.StatementContext var1);

   T visitGenericStatement(NginxParser.GenericStatementContext var1);

   T visitRegexHeaderStatement(NginxParser.RegexHeaderStatementContext var1);

   T visitBlock(NginxParser.BlockContext var1);

   T visitGenericBlockHeader(NginxParser.GenericBlockHeaderContext var1);

   T visitIf_statement(NginxParser.If_statementContext var1);

   T visitIf_body(NginxParser.If_bodyContext var1);

   T visitRegexp(NginxParser.RegexpContext var1);

   T visitLocationBlockHeader(NginxParser.LocationBlockHeaderContext var1);

   T visitRewriteStatement(NginxParser.RewriteStatementContext var1);
}
