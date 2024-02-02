/*     */ package io.undertow.server.handlers.proxy;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface ConnectionPoolErrorHandler
/*     */ {
/*     */   boolean isAvailable();
/*     */   
/*     */   boolean handleError();
/*     */   
/*     */   boolean clearError();
/*     */   
/*     */   public static class SimpleConnectionPoolErrorHandler
/*     */     implements ConnectionPoolErrorHandler
/*     */   {
/*     */     private volatile boolean problem;
/*     */     
/*     */     public boolean isAvailable() {
/*  55 */       return !this.problem;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean handleError() {
/*  60 */       this.problem = true;
/*  61 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean clearError() {
/*  66 */       this.problem = false;
/*  67 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class CountingErrorHandler
/*     */     implements ConnectionPoolErrorHandler
/*     */   {
/*     */     private int count;
/*     */     
/*     */     private long timeout;
/*     */     
/*     */     private final long interval;
/*     */     private final int errorCount;
/*     */     private final int successCount;
/*     */     private final ConnectionPoolErrorHandler delegate;
/*     */     
/*     */     public CountingErrorHandler(int errorCount, int successCount, long interval) {
/*  85 */       this(errorCount, successCount, interval, new ConnectionPoolErrorHandler.SimpleConnectionPoolErrorHandler());
/*     */     }
/*     */     
/*     */     public CountingErrorHandler(int errorCount, int successCount, long interval, ConnectionPoolErrorHandler delegate) {
/*  89 */       this.errorCount = Math.max(errorCount, 1);
/*  90 */       this.successCount = Math.max(successCount, 1);
/*  91 */       this.interval = Math.max(interval, 0L);
/*  92 */       this.delegate = delegate;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isAvailable() {
/*  97 */       return this.delegate.isAvailable();
/*     */     }
/*     */ 
/*     */     
/*     */     public synchronized boolean handleError() {
/* 102 */       if (this.delegate.isAvailable()) {
/* 103 */         long time = System.currentTimeMillis();
/*     */         
/* 105 */         if (time >= this.timeout) {
/* 106 */           this.count = 1;
/* 107 */           this.timeout = time + this.interval;
/*     */         }
/* 109 */         else if (this.count++ == 1) {
/* 110 */           this.timeout = time + this.interval;
/*     */         } 
/*     */         
/* 113 */         if (this.count >= this.errorCount) {
/* 114 */           return this.delegate.handleError();
/*     */         }
/* 116 */         return true;
/*     */       } 
/* 118 */       this.count = 0;
/* 119 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public synchronized boolean clearError() {
/* 125 */       if (this.delegate.isAvailable()) {
/* 126 */         this.count = 0;
/* 127 */         return true;
/*     */       } 
/*     */       
/* 130 */       if (this.count++ == this.successCount) {
/* 131 */         return this.delegate.clearError();
/*     */       }
/* 133 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ConnectionPoolErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */