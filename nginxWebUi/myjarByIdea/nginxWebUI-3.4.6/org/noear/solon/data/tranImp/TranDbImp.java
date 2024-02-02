package org.noear.solon.data.tranImp;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tran.TranNode;
import org.noear.solon.ext.RunnableEx;

public class TranDbImp extends DbTran implements TranNode {
   public TranDbImp(Tran meta) {
      super(meta);
   }

   public void apply(RunnableEx runnable) throws Throwable {
      super.execute(() -> {
         runnable.run();
      });
   }
}
