package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Token;

public interface TerminalNode extends ParseTree {
   Token getSymbol();
}
