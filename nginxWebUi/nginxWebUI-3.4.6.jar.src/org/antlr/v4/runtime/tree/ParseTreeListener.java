package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;

public interface ParseTreeListener {
  void visitTerminal(TerminalNode paramTerminalNode);
  
  void visitErrorNode(ErrorNode paramErrorNode);
  
  void enterEveryRule(ParserRuleContext paramParserRuleContext);
  
  void exitEveryRule(ParserRuleContext paramParserRuleContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\ParseTreeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */