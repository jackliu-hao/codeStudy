package org.noear.solon.data.tranImp;

import org.noear.solon.data.tran.TranManager;
import org.noear.solon.data.tran.TranNode;
import org.noear.solon.ext.RunnableEx;

public class TranNeverImp implements TranNode {
   public void apply(RunnableEx runnable) throws Throwable {
      if (TranManager.current() != null) {
         throw new RuntimeException("Never support transactions");
      } else {
         runnable.run();
      }
   }
}
