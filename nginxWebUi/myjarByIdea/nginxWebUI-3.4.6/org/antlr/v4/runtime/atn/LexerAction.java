package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;

public interface LexerAction {
   LexerActionType getActionType();

   boolean isPositionDependent();

   void execute(Lexer var1);
}
