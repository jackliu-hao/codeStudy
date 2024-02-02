/*     */ package org.xnio;
/*     */ 
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class AutomaticReference<T>
/*     */   extends PhantomReference<T>
/*     */ {
/*  43 */   static final Object PERMIT = new Object();
/*     */ 
/*     */   
/*  46 */   private static final Set<AutomaticReference<?>> LIVE_SET = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object getPermit() {
/*  55 */     SecurityManager sm = System.getSecurityManager();
/*  56 */     if (sm != null) {
/*  57 */       sm.checkPermission(new RuntimePermission("createAutomaticReference"));
/*     */     }
/*  59 */     return PERMIT;
/*     */   }
/*     */   
/*     */   private static ReferenceQueue<Object> checkPermit(Object permit) {
/*  63 */     if (permit == PERMIT)
/*     */     {
/*  65 */       return Cleaner.QUEUE;
/*     */     }
/*  67 */     throw new SecurityException("Unauthorized subclass of " + AutomaticReference.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final T get() {
/*  77 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void clear() {
/*  86 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEnqueued() {
/*  95 */     return super.isEnqueued();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean enqueue() {
/* 105 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AutomaticReference(T referent, Object permit) {
/* 116 */     super(referent, (ReferenceQueue)checkPermit(permit));
/* 117 */     LIVE_SET.add(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void free();
/*     */ 
/*     */ 
/*     */   
/*     */   static class Cleaner
/*     */     implements Runnable
/*     */   {
/* 130 */     private static final ReferenceQueue<Object> QUEUE = new ReferenceQueue(); static {
/* 131 */       AccessController.doPrivileged(new PrivilegedAction<Void>() {
/*     */             public Void run() {
/* 133 */               Thread thr = new Thread(new AutomaticReference.Cleaner(), "XNIO cleaner thread");
/* 134 */               thr.setDaemon(true);
/* 135 */               thr.setContextClassLoader(null);
/* 136 */               thr.start();
/* 137 */               return null;
/*     */             }
/*     */           });
/*     */     }
/*     */     
/*     */     public void run() {
/*     */       while (true) {
/*     */         try {
/* 145 */           AutomaticReference<?> ref = (AutomaticReference)QUEUE.remove();
/*     */           try {
/* 147 */             ref.free();
/*     */           } finally {
/* 149 */             AutomaticReference.LIVE_SET.remove(ref);
/*     */           } 
/* 151 */         } catch (Throwable throwable) {}
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\AutomaticReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */