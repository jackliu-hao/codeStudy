package cn.hutool.core.thread;

import java.util.concurrent.ExecutorService;

public class FinalizableDelegatedExecutorService extends DelegatedExecutorService {
   FinalizableDelegatedExecutorService(ExecutorService executor) {
      super(executor);
   }

   protected void finalize() {
      super.shutdown();
   }
}
