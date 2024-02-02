package org.xnio;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/** @deprecated */
@Deprecated
public abstract class AutomaticReference<T> extends PhantomReference<T> {
   static final Object PERMIT = new Object();
   private static final Set<AutomaticReference<?>> LIVE_SET = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap()));

   public static Object getPermit() {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(new RuntimePermission("createAutomaticReference"));
      }

      return PERMIT;
   }

   private static ReferenceQueue<Object> checkPermit(Object permit) {
      if (permit == PERMIT) {
         return AutomaticReference.Cleaner.QUEUE;
      } else {
         throw new SecurityException("Unauthorized subclass of " + AutomaticReference.class);
      }
   }

   public final T get() {
      return null;
   }

   public final void clear() {
      throw new UnsupportedOperationException();
   }

   public final boolean isEnqueued() {
      return super.isEnqueued();
   }

   public final boolean enqueue() {
      throw new UnsupportedOperationException();
   }

   protected AutomaticReference(T referent, Object permit) {
      super(referent, checkPermit(permit));
      LIVE_SET.add(this);
   }

   protected abstract void free();

   static class Cleaner implements Runnable {
      private static final ReferenceQueue<Object> QUEUE = new ReferenceQueue();

      public void run() {
         while(true) {
            try {
               AutomaticReference<?> ref = (AutomaticReference)QUEUE.remove();

               try {
                  ref.free();
               } finally {
                  AutomaticReference.LIVE_SET.remove(ref);
               }
            } catch (Throwable var6) {
            }
         }
      }

      static {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               Thread thr = new Thread(new Cleaner(), "XNIO cleaner thread");
               thr.setDaemon(true);
               thr.setContextClassLoader((ClassLoader)null);
               thr.start();
               return null;
            }
         });
      }
   }
}
