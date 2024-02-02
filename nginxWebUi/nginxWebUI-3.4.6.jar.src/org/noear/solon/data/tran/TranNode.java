package org.noear.solon.data.tran;

import org.noear.solon.ext.RunnableEx;

public interface TranNode {
  default void add(TranNode slave) {}
  
  void apply(RunnableEx paramRunnableEx) throws Throwable;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\tran\TranNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */