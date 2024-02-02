/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.util.WeakHashMap;
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
/*     */ class OnExitDatabaseCloser
/*     */   extends Thread
/*     */ {
/*  17 */   private static final WeakHashMap<Database, Void> DATABASES = new WeakHashMap<>();
/*     */   
/*  19 */   private static final Thread INSTANCE = new OnExitDatabaseCloser();
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean registered;
/*     */ 
/*     */   
/*     */   private static boolean terminated;
/*     */ 
/*     */ 
/*     */   
/*     */   static synchronized void register(Database paramDatabase) {
/*  31 */     if (terminated) {
/*     */       return;
/*     */     }
/*     */     
/*  35 */     DATABASES.put(paramDatabase, null);
/*  36 */     if (!registered) {
/*     */ 
/*     */       
/*  39 */       registered = true;
/*     */       try {
/*  41 */         Runtime.getRuntime().addShutdownHook(INSTANCE);
/*  42 */       } catch (IllegalStateException illegalStateException) {
/*     */ 
/*     */       
/*     */       }
/*  46 */       catch (SecurityException securityException) {}
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
/*     */   static synchronized void unregister(Database paramDatabase) {
/*  60 */     if (terminated) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  65 */     DATABASES.remove(paramDatabase);
/*  66 */     if (DATABASES.isEmpty() && registered) {
/*     */       try {
/*  68 */         Runtime.getRuntime().removeShutdownHook(INSTANCE);
/*  69 */       } catch (IllegalStateException illegalStateException) {
/*     */       
/*  71 */       } catch (SecurityException securityException) {}
/*     */ 
/*     */       
/*  74 */       registered = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void onShutdown() {
/*  79 */     synchronized (OnExitDatabaseCloser.class) {
/*  80 */       terminated = true;
/*     */     } 
/*  82 */     RuntimeException runtimeException = null;
/*  83 */     for (Database database : DATABASES.keySet()) {
/*     */       try {
/*  85 */         database.close(true);
/*  86 */       } catch (RuntimeException runtimeException1) {
/*     */ 
/*     */         
/*     */         try {
/*     */           
/*  91 */           database.getTrace(2).error(runtimeException1, "could not close the database");
/*     */         
/*     */         }
/*  94 */         catch (Throwable throwable) {
/*  95 */           runtimeException1.addSuppressed(throwable);
/*  96 */           if (runtimeException == null) {
/*  97 */             runtimeException = runtimeException1; continue;
/*     */           } 
/*  99 */           runtimeException.addSuppressed(runtimeException1);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 104 */     if (runtimeException != null) {
/* 105 */       throw runtimeException;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 114 */     onShutdown();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\OnExitDatabaseCloser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */