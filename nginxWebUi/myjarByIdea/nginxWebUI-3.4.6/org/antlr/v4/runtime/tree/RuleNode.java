package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.RuleContext;

public interface RuleNode extends ParseTree {
   RuleContext getRuleContext();
}
