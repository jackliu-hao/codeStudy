/*     */ package ch.qos.logback.core.status;
/*     */ 
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.spi.LifeCycle;
/*     */ import ch.qos.logback.core.util.StatusPrinter;
/*     */ import java.io.PrintStream;
/*     */ import java.util.List;
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
/*     */ public abstract class OnPrintStreamStatusListenerBase
/*     */   extends ContextAwareBase
/*     */   implements StatusListener, LifeCycle
/*     */ {
/*     */   boolean isStarted = false;
/*     */   static final long DEFAULT_RETROSPECTIVE = 300L;
/*  32 */   long retrospectiveThresold = 300L;
/*     */ 
/*     */ 
/*     */   
/*     */   String prefix;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract PrintStream getPrintStream();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void print(Status status) {
/*  47 */     StringBuilder sb = new StringBuilder();
/*  48 */     if (this.prefix != null) {
/*  49 */       sb.append(this.prefix);
/*     */     }
/*  51 */     StatusPrinter.buildStr(sb, "", status);
/*  52 */     getPrintStream().print(sb);
/*     */   }
/*     */   
/*     */   public void addStatusEvent(Status status) {
/*  56 */     if (!this.isStarted)
/*     */       return; 
/*  58 */     print(status);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void retrospectivePrint() {
/*  65 */     if (this.context == null)
/*     */       return; 
/*  67 */     long now = System.currentTimeMillis();
/*  68 */     StatusManager sm = this.context.getStatusManager();
/*  69 */     List<Status> statusList = sm.getCopyOfStatusList();
/*  70 */     for (Status status : statusList) {
/*  71 */       long timestampOfStatusMesage = status.getDate().longValue();
/*  72 */       if (isElapsedTimeLongerThanThreshold(now, timestampOfStatusMesage)) {
/*  73 */         print(status);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isElapsedTimeLongerThanThreshold(long now, long timestamp) {
/*  79 */     long elapsedTime = now - timestamp;
/*  80 */     return (elapsedTime < this.retrospectiveThresold);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  88 */     this.isStarted = true;
/*  89 */     if (this.retrospectiveThresold > 0L) {
/*  90 */       retrospectivePrint();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getPrefix() {
/*  95 */     return this.prefix;
/*     */   }
/*     */   
/*     */   public void setPrefix(String prefix) {
/*  99 */     this.prefix = prefix;
/*     */   }
/*     */   
/*     */   public void setRetrospective(long retrospective) {
/* 103 */     this.retrospectiveThresold = retrospective;
/*     */   }
/*     */   
/*     */   public long getRetrospective() {
/* 107 */     return this.retrospectiveThresold;
/*     */   }
/*     */   
/*     */   public void stop() {
/* 111 */     this.isStarted = false;
/*     */   }
/*     */   
/*     */   public boolean isStarted() {
/* 115 */     return this.isStarted;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\status\OnPrintStreamStatusListenerBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */