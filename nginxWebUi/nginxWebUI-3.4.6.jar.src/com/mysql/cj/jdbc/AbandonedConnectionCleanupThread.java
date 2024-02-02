/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.MysqlConnection;
/*     */ import com.mysql.cj.protocol.NetworkResources;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AbandonedConnectionCleanupThread
/*     */   implements Runnable
/*     */ {
/*  51 */   private static final Set<ConnectionFinalizerPhantomReference> connectionFinalizerPhantomRefs = ConcurrentHashMap.newKeySet();
/*  52 */   private static final ReferenceQueue<MysqlConnection> referenceQueue = new ReferenceQueue<>();
/*     */   
/*     */   private static final ExecutorService cleanupThreadExecutorService;
/*  55 */   private static Thread threadRef = null;
/*  56 */   private static Lock threadRefLock = new ReentrantLock();
/*  57 */   private static boolean abandonedConnectionCleanupDisabled = Boolean.getBoolean("com.mysql.cj.disableAbandonedConnectionCleanup");
/*     */   
/*     */   static {
/*  60 */     if (abandonedConnectionCleanupDisabled) {
/*  61 */       cleanupThreadExecutorService = null;
/*     */     } else {
/*  63 */       cleanupThreadExecutorService = Executors.newSingleThreadExecutor(r -> {
/*     */             Thread t = new Thread(r, "mysql-cj-abandoned-connection-cleanup");
/*     */ 
/*     */             
/*     */             t.setDaemon(true);
/*     */             
/*     */             ClassLoader classLoader = AbandonedConnectionCleanupThread.class.getClassLoader();
/*     */             
/*     */             if (classLoader == null) {
/*     */               classLoader = ClassLoader.getSystemClassLoader();
/*     */             }
/*     */             
/*     */             t.setContextClassLoader(classLoader);
/*     */             
/*     */             return threadRef = t;
/*     */           });
/*     */       
/*  80 */       cleanupThreadExecutorService.execute(new AbandonedConnectionCleanupThread());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     while (true) {
/*     */       try {
/*  90 */         checkThreadContextClassLoader();
/*  91 */         Reference<? extends MysqlConnection> reference = referenceQueue.remove(5000L);
/*  92 */         if (reference != null) {
/*  93 */           finalizeResource((ConnectionFinalizerPhantomReference)reference);
/*     */         }
/*  95 */       } catch (InterruptedException e) {
/*  96 */         threadRefLock.lock();
/*     */         try {
/*  98 */           threadRef = null;
/*     */           
/*     */           Reference<? extends MysqlConnection> reference;
/*     */           
/* 102 */           while ((reference = referenceQueue.poll()) != null) {
/* 103 */             finalizeResource((ConnectionFinalizerPhantomReference)reference);
/*     */           }
/* 105 */           connectionFinalizerPhantomRefs.clear();
/*     */         } finally {
/* 107 */           threadRefLock.unlock();
/*     */         } 
/*     */         return;
/* 110 */       } catch (Exception exception) {}
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
/*     */   private void checkThreadContextClassLoader() {
/*     */     try {
/* 123 */       threadRef.getContextClassLoader().getResource("");
/* 124 */     } catch (Throwable e) {
/*     */       
/* 126 */       uncheckedShutdown();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean consistentClassLoaders() {
/* 136 */     threadRefLock.lock();
/*     */     try {
/* 138 */       if (threadRef == null) {
/* 139 */         return false;
/*     */       }
/* 141 */       ClassLoader callerCtxClassLoader = Thread.currentThread().getContextClassLoader();
/* 142 */       ClassLoader threadCtxClassLoader = threadRef.getContextClassLoader();
/* 143 */       return (callerCtxClassLoader != null && threadCtxClassLoader != null && callerCtxClassLoader == threadCtxClassLoader);
/*     */     } finally {
/* 145 */       threadRefLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void shutdown(boolean checked) {
/* 156 */     if (checked && !consistentClassLoaders()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 161 */     if (cleanupThreadExecutorService != null) {
/* 162 */       cleanupThreadExecutorService.shutdownNow();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkedShutdown() {
/* 171 */     shutdown(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void uncheckedShutdown() {
/* 178 */     shutdown(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAlive() {
/* 187 */     threadRefLock.lock();
/*     */     try {
/* 189 */       return (threadRef != null && threadRef.isAlive());
/*     */     } finally {
/* 191 */       threadRefLock.unlock();
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
/*     */   protected static void trackConnection(MysqlConnection conn, NetworkResources io) {
/* 204 */     if (abandonedConnectionCleanupDisabled) {
/*     */       return;
/*     */     }
/* 207 */     threadRefLock.lock();
/*     */     try {
/* 209 */       if (isAlive()) {
/* 210 */         ConnectionFinalizerPhantomReference reference = new ConnectionFinalizerPhantomReference(conn, io, referenceQueue);
/* 211 */         connectionFinalizerPhantomRefs.add(reference);
/*     */       } 
/*     */     } finally {
/* 214 */       threadRefLock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void finalizeResource(ConnectionFinalizerPhantomReference reference) {
/*     */     try {
/* 226 */       reference.finalizeResources();
/* 227 */       reference.clear();
/*     */     } finally {
/* 229 */       connectionFinalizerPhantomRefs.remove(reference);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ConnectionFinalizerPhantomReference
/*     */     extends PhantomReference<MysqlConnection>
/*     */   {
/*     */     private NetworkResources networkResources;
/*     */ 
/*     */     
/*     */     ConnectionFinalizerPhantomReference(MysqlConnection conn, NetworkResources networkResources, ReferenceQueue<? super MysqlConnection> refQueue) {
/* 241 */       super(conn, refQueue);
/* 242 */       this.networkResources = networkResources;
/*     */     }
/*     */     
/*     */     void finalizeResources() {
/* 246 */       if (this.networkResources != null)
/*     */         try {
/* 248 */           this.networkResources.forceClose();
/*     */         } finally {
/* 250 */           this.networkResources = null;
/*     */         }  
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\AbandonedConnectionCleanupThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */