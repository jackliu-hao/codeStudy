/*    */ package org.h2.engine;
/*    */ 
/*    */ import java.lang.ref.WeakReference;
/*    */ import org.h2.message.Trace;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class DelayedDatabaseCloser
/*    */   extends Thread
/*    */ {
/*    */   private final Trace trace;
/*    */   private volatile WeakReference<Database> databaseRef;
/*    */   private int delayInMillis;
/*    */   
/*    */   DelayedDatabaseCloser(Database paramDatabase, int paramInt) {
/* 24 */     this.databaseRef = new WeakReference<>(paramDatabase);
/* 25 */     this.delayInMillis = paramInt;
/* 26 */     this.trace = paramDatabase.getTrace(2);
/* 27 */     setName("H2 Close Delay " + paramDatabase.getShortName());
/* 28 */     setDaemon(true);
/* 29 */     start();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void reset() {
/* 37 */     this.databaseRef = null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/* 42 */     while (this.delayInMillis > 0) {
/*    */       try {
/* 44 */         byte b = 100;
/* 45 */         Thread.sleep(b);
/* 46 */         this.delayInMillis -= b;
/* 47 */       } catch (Exception exception) {}
/*    */ 
/*    */       
/* 50 */       WeakReference<Database> weakReference1 = this.databaseRef;
/* 51 */       if (weakReference1 == null || weakReference1.get() == null) {
/*    */         return;
/*    */       }
/*    */     } 
/*    */     
/* 56 */     WeakReference<Database> weakReference = this.databaseRef; Database database;
/* 57 */     if (weakReference != null && (database = weakReference.get()) != null)
/*    */       try {
/* 59 */         database.close(false);
/* 60 */       } catch (RuntimeException runtimeException) {
/*    */ 
/*    */         
/*    */         try {
/*    */           
/* 65 */           this.trace.error(runtimeException, "could not close the database");
/*    */         
/*    */         }
/* 68 */         catch (Throwable throwable) {
/* 69 */           runtimeException.addSuppressed(throwable);
/* 70 */           throw runtimeException;
/*    */         } 
/*    */       }  
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\DelayedDatabaseCloser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */