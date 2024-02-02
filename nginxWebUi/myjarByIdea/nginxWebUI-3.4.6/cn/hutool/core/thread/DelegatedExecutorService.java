package cn.hutool.core.thread;

import cn.hutool.core.lang.Assert;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DelegatedExecutorService extends AbstractExecutorService {
   private final ExecutorService e;

   DelegatedExecutorService(ExecutorService executor) {
      Assert.notNull(executor, "executor must be not null !");
      this.e = executor;
   }

   public void execute(Runnable command) {
      this.e.execute(command);
   }

   public void shutdown() {
      this.e.shutdown();
   }

   public List<Runnable> shutdownNow() {
      return this.e.shutdownNow();
   }

   public boolean isShutdown() {
      return this.e.isShutdown();
   }

   public boolean isTerminated() {
      return this.e.isTerminated();
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return this.e.awaitTermination(timeout, unit);
   }

   public Future<?> submit(Runnable task) {
      return this.e.submit(task);
   }

   public <T> Future<T> submit(Callable<T> task) {
      return this.e.submit(task);
   }

   public <T> Future<T> submit(Runnable task, T result) {
      return this.e.submit(task, result);
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      return this.e.invokeAll(tasks);
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
      return this.e.invokeAll(tasks, timeout, unit);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      return this.e.invokeAny(tasks);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return this.e.invokeAny(tasks, timeout, unit);
   }
}
