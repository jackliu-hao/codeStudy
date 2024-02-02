package org.noear.solon.data.tranImp;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tran.TranManager;
import org.noear.solon.data.tran.TranNode;
import org.noear.solon.ext.RunnableEx;

public class TranDbNewImp extends DbTran implements TranNode {
   public TranDbNewImp(Tran meta) {
      super(meta);
   }

   public void apply(RunnableEx runnable) throws Throwable {
      DbTran tran = TranManager.trySuspend();

      try {
         super.execute(() -> {
            runnable.run();
         });
      } finally {
         TranManager.tryResume(tran);
      }

   }
}
