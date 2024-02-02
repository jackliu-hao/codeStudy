package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.misc.Interval;

public interface SyntaxTree extends Tree {
   Interval getSourceInterval();
}
