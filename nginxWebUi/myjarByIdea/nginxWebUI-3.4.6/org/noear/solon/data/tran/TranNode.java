package org.noear.solon.data.tran;

import org.noear.solon.ext.RunnableEx;

public interface TranNode {
   default void add(TranNode slave) {
   }

   void apply(RunnableEx runnable) throws Throwable;
}
