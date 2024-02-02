package org.antlr.v4.runtime.tree;

public interface ParseTreeVisitor<T> {
   T visit(ParseTree var1);

   T visitChildren(RuleNode var1);

   T visitTerminal(TerminalNode var1);

   T visitErrorNode(ErrorNode var1);
}
