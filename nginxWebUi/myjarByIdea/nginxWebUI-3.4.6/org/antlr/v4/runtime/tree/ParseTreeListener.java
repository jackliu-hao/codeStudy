package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;

public interface ParseTreeListener {
   void visitTerminal(TerminalNode var1);

   void visitErrorNode(ErrorNode var1);

   void enterEveryRule(ParserRuleContext var1);

   void exitEveryRule(ParserRuleContext var1);
}
