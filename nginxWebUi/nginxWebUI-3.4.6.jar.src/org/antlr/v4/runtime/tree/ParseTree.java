package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Parser;

public interface ParseTree extends SyntaxTree {
  ParseTree getParent();
  
  ParseTree getChild(int paramInt);
  
  <T> T accept(ParseTreeVisitor<? extends T> paramParseTreeVisitor);
  
  String getText();
  
  String toStringTree(Parser paramParser);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\ParseTree.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */