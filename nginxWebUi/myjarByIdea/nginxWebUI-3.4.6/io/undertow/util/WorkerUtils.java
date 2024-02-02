package io.undertow.util;

import io.undertow.UndertowLogger;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;

public class WorkerUtils {
   private WorkerUtils() {
   }

   public static XnioExecutor.Key executeAfter(XnioIoThread thread, Runnable task, long timeout, TimeUnit timeUnit) {
      try {
         return thread.executeAfter(task, timeout, timeUnit);
      } catch (RejectedExecutionException var6) {
         if (thread.getWorker().isShutdown()) {
            UndertowLogger.ROOT_LOGGER.debugf(var6, "Failed to schedule task %s as worker is shutting down", task);
            return new XnioExecutor.Key() {
               public boolean remove() {
                  return false;
               }
            };
         } else {
            throw var6;
         }
      }
   }
}
