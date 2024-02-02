package io.undertow.util;

import java.util.concurrent.Executor;

public class SameThreadExecutor implements Executor {
   public static final Executor INSTANCE = new SameThreadExecutor();

   private SameThreadExecutor() {
   }

   public void execute(Runnable command) {
      command.run();
   }
}
