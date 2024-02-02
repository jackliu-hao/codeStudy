package org.antlr.v4.runtime.tree;

public interface ParseTreeVisitor<T> {
  T visit(ParseTree paramParseTree);
  
  T visitChildren(RuleNode paramRuleNode);
  
  T visitTerminal(TerminalNode paramTerminalNode);
  
  T visitErrorNode(ErrorNode paramErrorNode);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\ParseTreeVisitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */