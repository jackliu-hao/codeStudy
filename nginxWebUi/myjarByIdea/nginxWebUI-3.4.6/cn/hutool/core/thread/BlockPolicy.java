package cn.hutool.core.thread;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class BlockPolicy implements RejectedExecutionHandler {
   public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
      try {
         e.getQueue().put(r);
      } catch (InterruptedException var4) {
         throw new RejectedExecutionException("Task " + r + " rejected from " + e);
      }
   }
}
