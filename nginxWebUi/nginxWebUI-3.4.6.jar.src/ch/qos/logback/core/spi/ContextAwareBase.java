/*     */ package ch.qos.logback.core.spi;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.status.ErrorStatus;
/*     */ import ch.qos.logback.core.status.InfoStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.StatusManager;
/*     */ import ch.qos.logback.core.status.WarnStatus;
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
/*     */ public class ContextAwareBase
/*     */   implements ContextAware
/*     */ {
/*  30 */   private int noContextWarning = 0;
/*     */   protected Context context;
/*     */   final Object declaredOrigin;
/*     */   
/*     */   public ContextAwareBase() {
/*  35 */     this.declaredOrigin = this;
/*     */   }
/*     */   
/*     */   public ContextAwareBase(ContextAware declaredOrigin) {
/*  39 */     this.declaredOrigin = declaredOrigin;
/*     */   }
/*     */   
/*     */   public void setContext(Context context) {
/*  43 */     if (this.context == null) {
/*  44 */       this.context = context;
/*  45 */     } else if (this.context != context) {
/*  46 */       throw new IllegalStateException("Context has been already set");
/*     */     } 
/*     */   }
/*     */   
/*     */   public Context getContext() {
/*  51 */     return this.context;
/*     */   }
/*     */   
/*     */   public StatusManager getStatusManager() {
/*  55 */     if (this.context == null) {
/*  56 */       return null;
/*     */     }
/*  58 */     return this.context.getStatusManager();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object getDeclaredOrigin() {
/*  68 */     return this.declaredOrigin;
/*     */   }
/*     */   
/*     */   public void addStatus(Status status) {
/*  72 */     if (this.context == null) {
/*  73 */       if (this.noContextWarning++ == 0) {
/*  74 */         System.out.println("LOGBACK: No context given for " + this);
/*     */       }
/*     */       return;
/*     */     } 
/*  78 */     StatusManager sm = this.context.getStatusManager();
/*  79 */     if (sm != null) {
/*  80 */       sm.add(status);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addInfo(String msg) {
/*  85 */     addStatus((Status)new InfoStatus(msg, getDeclaredOrigin()));
/*     */   }
/*     */   
/*     */   public void addInfo(String msg, Throwable ex) {
/*  89 */     addStatus((Status)new InfoStatus(msg, getDeclaredOrigin(), ex));
/*     */   }
/*     */   
/*     */   public void addWarn(String msg) {
/*  93 */     addStatus((Status)new WarnStatus(msg, getDeclaredOrigin()));
/*     */   }
/*     */   
/*     */   public void addWarn(String msg, Throwable ex) {
/*  97 */     addStatus((Status)new WarnStatus(msg, getDeclaredOrigin(), ex));
/*     */   }
/*     */   
/*     */   public void addError(String msg) {
/* 101 */     addStatus((Status)new ErrorStatus(msg, getDeclaredOrigin()));
/*     */   }
/*     */   
/*     */   public void addError(String msg, Throwable ex) {
/* 105 */     addStatus((Status)new ErrorStatus(msg, getDeclaredOrigin(), ex));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\ContextAwareBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */