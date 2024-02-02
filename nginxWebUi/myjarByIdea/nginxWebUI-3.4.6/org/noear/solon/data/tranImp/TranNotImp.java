package org.noear.solon.data.tranImp;

import org.noear.solon.data.tran.TranManager;
import org.noear.solon.data.tran.TranNode;
import org.noear.solon.ext.RunnableEx;

public class TranNotImp implements TranNode {
   public void apply(RunnableEx runnable) throws Throwable {
      DbTran tran = TranManager.trySuspend();

      try {
         runnable.run();
      } finally {
         TranManager.tryResume(tran);
      }

   }
}
