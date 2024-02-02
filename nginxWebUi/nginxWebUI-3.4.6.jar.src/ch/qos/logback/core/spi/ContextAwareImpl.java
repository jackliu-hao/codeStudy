/*    */ package ch.qos.logback.core.spi;
/*    */ 
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.status.ErrorStatus;
/*    */ import ch.qos.logback.core.status.InfoStatus;
/*    */ import ch.qos.logback.core.status.Status;
/*    */ import ch.qos.logback.core.status.StatusManager;
/*    */ import ch.qos.logback.core.status.WarnStatus;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContextAwareImpl
/*    */   implements ContextAware
/*    */ {
/* 31 */   private int noContextWarning = 0;
/*    */   protected Context context;
/*    */   final Object origin;
/*    */   
/*    */   public ContextAwareImpl(Context context, Object origin) {
/* 36 */     this.context = context;
/* 37 */     this.origin = origin;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Object getOrigin() {
/* 42 */     return this.origin;
/*    */   }
/*    */   
/*    */   public void setContext(Context context) {
/* 46 */     if (this.context == null) {
/* 47 */       this.context = context;
/* 48 */     } else if (this.context != context) {
/* 49 */       throw new IllegalStateException("Context has been already set");
/*    */     } 
/*    */   }
/*    */   
/*    */   public Context getContext() {
/* 54 */     return this.context;
/*    */   }
/*    */   
/*    */   public StatusManager getStatusManager() {
/* 58 */     if (this.context == null) {
/* 59 */       return null;
/*    */     }
/* 61 */     return this.context.getStatusManager();
/*    */   }
/*    */   
/*    */   public void addStatus(Status status) {
/* 65 */     if (this.context == null) {
/* 66 */       if (this.noContextWarning++ == 0) {
/* 67 */         System.out.println("LOGBACK: No context given for " + this);
/*    */       }
/*    */       return;
/*    */     } 
/* 71 */     StatusManager sm = this.context.getStatusManager();
/* 72 */     if (sm != null) {
/* 73 */       sm.add(status);
/*    */     }
/*    */   }
/*    */   
/*    */   public void addInfo(String msg) {
/* 78 */     addStatus((Status)new InfoStatus(msg, getOrigin()));
/*    */   }
/*    */   
/*    */   public void addInfo(String msg, Throwable ex) {
/* 82 */     addStatus((Status)new InfoStatus(msg, getOrigin(), ex));
/*    */   }
/*    */   
/*    */   public void addWarn(String msg) {
/* 86 */     addStatus((Status)new WarnStatus(msg, getOrigin()));
/*    */   }
/*    */   
/*    */   public void addWarn(String msg, Throwable ex) {
/* 90 */     addStatus((Status)new WarnStatus(msg, getOrigin(), ex));
/*    */   }
/*    */   
/*    */   public void addError(String msg) {
/* 94 */     addStatus((Status)new ErrorStatus(msg, getOrigin()));
/*    */   }
/*    */   
/*    */   public void addError(String msg, Throwable ex) {
/* 98 */     addStatus((Status)new ErrorStatus(msg, getOrigin(), ex));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\ContextAwareImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */