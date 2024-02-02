package org.wildfly.common.ref;

import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import org.wildfly.common.Assert;

public final class References {
   private static final Reference<?, ?> NULL = new Reference<Object, Object>() {
      public Object get() {
         return null;
      }

      public Object getAttachment() {
         return null;
      }

      public void clear() {
      }

      public Reference.Type getType() {
         return Reference.Type.NULL;
      }

      public String toString() {
         return "NULL reference";
      }
   };

   private References() {
   }

   public static <T, A> Reference<T, A> create(Reference.Type type, T value, A attachment, Reaper<T, A> reaper) {
      Assert.checkNotNullParam("type", type);
      if (value == null) {
         return getNullReference();
      } else {
         switch (type) {
            case STRONG:
               return new StrongReference(value, attachment);
            case WEAK:
               return new WeakReference(value, attachment, reaper);
            case PHANTOM:
               return new PhantomReference(value, attachment, reaper);
            case SOFT:
               return new SoftReference(value, attachment, reaper);
            case NULL:
               return getNullReference();
            default:
               throw Assert.impossibleSwitchCase(type);
         }
      }
   }

   public static <T, A> Reference<T, A> create(Reference.Type type, T value, A attachment, ReferenceQueue<? super T> referenceQueue) {
      Assert.checkNotNullParam("type", type);
      if (referenceQueue == null) {
         return create(type, value, attachment);
      } else if (value == null) {
         return getNullReference();
      } else {
         switch (type) {
            case STRONG:
               return new StrongReference(value, attachment);
            case WEAK:
               return new WeakReference(value, attachment, referenceQueue);
            case PHANTOM:
               return new PhantomReference(value, attachment, referenceQueue);
            case SOFT:
               return new SoftReference(value, attachment, referenceQueue);
            case NULL:
               return getNullReference();
            default:
               throw Assert.impossibleSwitchCase(type);
         }
      }
   }

   public static <T, A> Reference<T, A> create(Reference.Type type, T value, A attachment) {
      Assert.checkNotNullParam("type", type);
      if (value == null) {
         return getNullReference();
      } else {
         switch (type) {
            case STRONG:
               return new StrongReference(value, attachment);
            case WEAK:
               return new WeakReference(value, attachment);
            case PHANTOM:
               return getNullReference();
            case SOFT:
               return new SoftReference(value, attachment);
            case NULL:
               return getNullReference();
            default:
               throw Assert.impossibleSwitchCase(type);
         }
      }
   }

   public static <T, A> Reference<T, A> getNullReference() {
      return NULL;
   }

   static final class ReaperThread extends Thread {
      static final ReferenceQueue<Object> REAPER_QUEUE = new ReferenceQueue();

      public void run() {
         while(true) {
            try {
               java.lang.ref.Reference<?> ref = REAPER_QUEUE.remove();
               if (ref instanceof CleanerReference) {
                  ((CleanerReference)ref).clean();
               }

               if (ref instanceof Reapable) {
                  reap((Reapable)ref);
               }
            } catch (InterruptedException var2) {
            } catch (Throwable var3) {
               Log.log.reapFailed(var3);
            }
         }
      }

      private static <T, A> void reap(Reapable<T, A> reapable) {
         reapable.getReaper().reap((Reference)reapable);
      }

      static {
         AtomicInteger cnt = new AtomicInteger(1);
         PrivilegedAction<Void> action = () -> {
            ReaperThread thr = new ReaperThread();
            thr.setName("Reference Reaper #" + cnt.getAndIncrement());
            thr.setDaemon(true);
            thr.start();
            return null;
         };

         for(int i = 0; i < 3; ++i) {
            AccessController.doPrivileged(action);
         }

      }
   }
}
