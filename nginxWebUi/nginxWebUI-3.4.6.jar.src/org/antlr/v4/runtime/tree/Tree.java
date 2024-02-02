package org.antlr.v4.runtime.tree;

public interface Tree {
  Tree getParent();
  
  Object getPayload();
  
  Tree getChild(int paramInt);
  
  int getChildCount();
  
  String toStringTree();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\Tree.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */