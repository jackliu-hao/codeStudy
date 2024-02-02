package org.xnio;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public interface XnioExecutor extends Executor {
   void execute(Runnable var1);

   Key executeAfter(Runnable var1, long var2, TimeUnit var4);

   Key executeAtInterval(Runnable var1, long var2, TimeUnit var4);

   public interface Key {
      Key IMMEDIATE = new Key() {
         public boolean remove() {
            return false;
         }

         public String toString() {
            return "Immediate key";
         }
      };

      boolean remove();
   }
}
