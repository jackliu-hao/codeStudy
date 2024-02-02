package io.undertow.util;

import io.undertow.UndertowLogger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/** @deprecated */
@Deprecated
public class PipeliningExecutor implements Executor {
   private final Executor executor;
   private static final ThreadLocal<LinkedList<Runnable>> THREAD_QUEUE = new ThreadLocal();

   public PipeliningExecutor(Executor executor) {
      this.executor = executor;
   }

   public void execute(final Runnable command) {
      List<Runnable> queue = (List)THREAD_QUEUE.get();
      if (queue != null) {
         queue.add(command);
      } else {
         this.executor.execute(new Runnable() {
            public void run() {
               LinkedList<Runnable> queue = (LinkedList)PipeliningExecutor.THREAD_QUEUE.get();
               if (queue == null) {
                  PipeliningExecutor.THREAD_QUEUE.set(queue = new LinkedList());
               }

               try {
                  command.run();
               } catch (Throwable var5) {
                  UndertowLogger.REQUEST_LOGGER.debugf(var5, "Task %s failed", command);
               }

               for(Runnable runnable = (Runnable)queue.poll(); runnable != null; runnable = (Runnable)queue.poll()) {
                  try {
                     runnable.run();
                  } catch (Throwable var4) {
                     UndertowLogger.REQUEST_LOGGER.debugf(var4, "Task %s failed", command);
                  }
               }

            }
         });
      }

   }
}
