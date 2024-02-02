package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;

public interface LexerAction {
  LexerActionType getActionType();
  
  boolean isPositionDependent();
  
  void execute(Lexer paramLexer);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */