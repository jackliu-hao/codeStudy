package cn.hutool.core.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncUtil {
   public static void waitAll(CompletableFuture<?>... tasks) {
      try {
         CompletableFuture.allOf(tasks).get();
      } catch (ExecutionException | InterruptedException var2) {
         throw new ThreadException(var2);
      }
   }

   public static <T> T waitAny(CompletableFuture<?>... tasks) {
      try {
         return CompletableFuture.anyOf(tasks).get();
      } catch (ExecutionException | InterruptedException var2) {
         throw new ThreadException(var2);
      }
   }

   public static <T> T get(CompletableFuture<T> task) {
      try {
         return task.get();
      } catch (ExecutionException | InterruptedException var2) {
         throw new ThreadException(var2);
      }
   }
}
