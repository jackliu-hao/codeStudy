package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Parser;

public interface ParseTree extends SyntaxTree {
   ParseTree getParent();

   ParseTree getChild(int var1);

   <T> T accept(ParseTreeVisitor<? extends T> var1);

   String getText();

   String toStringTree(Parser var1);
}
