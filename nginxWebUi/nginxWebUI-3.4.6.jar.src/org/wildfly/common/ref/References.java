/*     */ package org.wildfly.common.ref;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.wildfly.common.Assert;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class References
/*     */ {
/*  36 */   private static final Reference<?, ?> NULL = new Reference<Object, Object>() {
/*     */       public Object get() {
/*  38 */         return null;
/*     */       }
/*     */       
/*     */       public Object getAttachment() {
/*  42 */         return null;
/*     */       }
/*     */ 
/*     */       
/*     */       public void clear() {}
/*     */       
/*     */       public Reference.Type getType() {
/*  49 */         return Reference.Type.NULL;
/*     */       }
/*     */       
/*     */       public String toString() {
/*  53 */         return "NULL reference";
/*     */       }
/*     */     };
/*     */   
/*     */   static final class ReaperThread extends Thread {
/*  58 */     static final ReferenceQueue<Object> REAPER_QUEUE = new ReferenceQueue();
/*     */     
/*     */     static {
/*  61 */       AtomicInteger cnt = new AtomicInteger(1);
/*  62 */       PrivilegedAction<Void> action = () -> {
/*     */           ReaperThread thr = new ReaperThread();
/*     */           thr.setName("Reference Reaper #" + cnt.getAndIncrement());
/*     */           thr.setDaemon(true);
/*     */           thr.start();
/*     */           return null;
/*     */         };
/*  69 */       for (int i = 0; i < 3; i++)
/*  70 */         AccessController.doPrivileged(action); 
/*     */     }
/*     */     
/*     */     public void run() {
/*     */       while (true) {
/*     */         try {
/*  76 */           Reference<?> ref = REAPER_QUEUE.remove();
/*  77 */           if (ref instanceof CleanerReference) {
/*  78 */             ((CleanerReference)ref).clean();
/*     */           }
/*  80 */           if (ref instanceof Reapable) {
/*  81 */             reap((Reapable)ref);
/*     */           }
/*  83 */         } catch (InterruptedException interruptedException) {
/*     */         
/*  85 */         } catch (Throwable cause) {
/*  86 */           Log.log.reapFailed(cause);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     private static <T, A> void reap(Reapable<T, A> reapable) {
/*  92 */       reapable.getReaper().reap((Reference)reapable);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, A> Reference<T, A> create(Reference.Type type, T value, A attachment, Reaper<T, A> reaper) {
/* 110 */     Assert.checkNotNullParam("type", type);
/* 111 */     if (value == null) return getNullReference(); 
/* 112 */     switch (type) {
/*     */       case STRONG:
/* 114 */         return new StrongReference<>(value, attachment);
/*     */       case WEAK:
/* 116 */         return new WeakReference<>(value, attachment, reaper);
/*     */       case PHANTOM:
/* 118 */         return new PhantomReference<>(value, attachment, reaper);
/*     */       case SOFT:
/* 120 */         return new SoftReference<>(value, attachment, reaper);
/*     */       case NULL:
/* 122 */         return getNullReference();
/*     */     } 
/* 124 */     throw Assert.impossibleSwitchCase(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, A> Reference<T, A> create(Reference.Type type, T value, A attachment, ReferenceQueue<? super T> referenceQueue) {
/* 142 */     Assert.checkNotNullParam("type", type);
/* 143 */     if (referenceQueue == null) return create(type, value, attachment); 
/* 144 */     if (value == null) return getNullReference(); 
/* 145 */     switch (type) {
/*     */       case STRONG:
/* 147 */         return new StrongReference<>(value, attachment);
/*     */       case WEAK:
/* 149 */         return new WeakReference<>(value, attachment, referenceQueue);
/*     */       case PHANTOM:
/* 151 */         return new PhantomReference<>(value, attachment, referenceQueue);
/*     */       case SOFT:
/* 153 */         return new SoftReference<>(value, attachment, referenceQueue);
/*     */       case NULL:
/* 155 */         return getNullReference();
/*     */     } 
/* 157 */     throw Assert.impossibleSwitchCase(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, A> Reference<T, A> create(Reference.Type type, T value, A attachment) {
/* 175 */     Assert.checkNotNullParam("type", type);
/* 176 */     if (value == null) return getNullReference(); 
/* 177 */     switch (type) {
/*     */       case STRONG:
/* 179 */         return new StrongReference<>(value, attachment);
/*     */       case WEAK:
/* 181 */         return new WeakReference<>(value, attachment);
/*     */       case PHANTOM:
/* 183 */         return getNullReference();
/*     */       case SOFT:
/* 185 */         return new SoftReference<>(value, attachment);
/*     */       case NULL:
/* 187 */         return getNullReference();
/*     */     } 
/* 189 */     throw Assert.impossibleSwitchCase(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T, A> Reference<T, A> getNullReference() {
/* 203 */     return (Reference)NULL;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\References.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */