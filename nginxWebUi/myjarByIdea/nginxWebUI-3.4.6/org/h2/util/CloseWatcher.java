package org.h2.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CloseWatcher extends PhantomReference<Object> {
   private static final ReferenceQueue<Object> queue = new ReferenceQueue();
   private static final Set<CloseWatcher> refs = Collections.synchronizedSet(new HashSet());
   private String openStackTrace;
   private AutoCloseable closeable;

   public CloseWatcher(Object var1, ReferenceQueue<Object> var2, AutoCloseable var3) {
      super(var1, var2);
      this.closeable = var3;
   }

   public static CloseWatcher pollUnclosed() {
      CloseWatcher var0;
      do {
         var0 = (CloseWatcher)queue.poll();
         if (var0 == null) {
            return null;
         }

         if (refs != null) {
            refs.remove(var0);
         }
      } while(var0.closeable == null);

      return var0;
   }

   public static CloseWatcher register(Object var0, AutoCloseable var1, boolean var2) {
      CloseWatcher var3 = new CloseWatcher(var0, queue, var1);
      if (var2) {
         Exception var4 = new Exception("Open Stack Trace");
         StringWriter var5 = new StringWriter();
         var4.printStackTrace(new PrintWriter(var5));
         var3.openStackTrace = var5.toString();
      }

      refs.add(var3);
      return var3;
   }

   public static void unregister(CloseWatcher var0) {
      var0.closeable = null;
      refs.remove(var0);
   }

   public String getOpenStackTrace() {
      return this.openStackTrace;
   }

   public AutoCloseable getCloseable() {
      return this.closeable;
   }
}
