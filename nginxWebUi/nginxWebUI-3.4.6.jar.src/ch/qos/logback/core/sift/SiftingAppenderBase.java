/*     */ package ch.qos.logback.core.sift;
/*     */ 
/*     */ import ch.qos.logback.core.Appender;
/*     */ import ch.qos.logback.core.AppenderBase;
/*     */ import ch.qos.logback.core.util.Duration;
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
/*     */ public abstract class SiftingAppenderBase<E>
/*     */   extends AppenderBase<E>
/*     */ {
/*     */   protected AppenderTracker<E> appenderTracker;
/*     */   AppenderFactory<E> appenderFactory;
/*  34 */   Duration timeout = new Duration(1800000L);
/*  35 */   int maxAppenderCount = Integer.MAX_VALUE;
/*     */   
/*     */   Discriminator<E> discriminator;
/*     */   
/*     */   public Duration getTimeout() {
/*  40 */     return this.timeout;
/*     */   }
/*     */   
/*     */   public void setTimeout(Duration timeout) {
/*  44 */     this.timeout = timeout;
/*     */   }
/*     */   
/*     */   public int getMaxAppenderCount() {
/*  48 */     return this.maxAppenderCount;
/*     */   }
/*     */   
/*     */   public void setMaxAppenderCount(int maxAppenderCount) {
/*  52 */     this.maxAppenderCount = maxAppenderCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAppenderFactory(AppenderFactory<E> appenderFactory) {
/*  60 */     this.appenderFactory = appenderFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public void start() {
/*  65 */     int errors = 0;
/*  66 */     if (this.discriminator == null) {
/*  67 */       addError("Missing discriminator. Aborting");
/*  68 */       errors++;
/*     */     } 
/*  70 */     if (!this.discriminator.isStarted()) {
/*  71 */       addError("Discriminator has not started successfully. Aborting");
/*  72 */       errors++;
/*     */     } 
/*  74 */     if (this.appenderFactory == null) {
/*  75 */       addError("AppenderFactory has not been set. Aborting");
/*  76 */       errors++;
/*     */     } else {
/*  78 */       this.appenderTracker = new AppenderTracker<E>(this.context, this.appenderFactory);
/*  79 */       this.appenderTracker.setMaxComponents(this.maxAppenderCount);
/*  80 */       this.appenderTracker.setTimeout(this.timeout.getMilliseconds());
/*     */     } 
/*  82 */     if (errors == 0) {
/*  83 */       super.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/*  89 */     for (Appender<E> appender : (Iterable<Appender<E>>)this.appenderTracker.allComponents()) {
/*  90 */       appender.stop();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract long getTimestamp(E paramE);
/*     */   
/*     */   protected void append(E event) {
/*  98 */     if (!isStarted()) {
/*     */       return;
/*     */     }
/* 101 */     String discriminatingValue = this.discriminator.getDiscriminatingValue(event);
/* 102 */     long timestamp = getTimestamp(event);
/*     */     
/* 104 */     Appender<E> appender = (Appender<E>)this.appenderTracker.getOrCreate(discriminatingValue, timestamp);
/*     */     
/* 106 */     if (eventMarksEndOfLife(event)) {
/* 107 */       this.appenderTracker.endOfLife(discriminatingValue);
/*     */     }
/* 109 */     this.appenderTracker.removeStaleComponents(timestamp);
/* 110 */     appender.doAppend(event);
/*     */   }
/*     */   
/*     */   protected abstract boolean eventMarksEndOfLife(E paramE);
/*     */   
/*     */   public Discriminator<E> getDiscriminator() {
/* 116 */     return this.discriminator;
/*     */   }
/*     */   
/*     */   public void setDiscriminator(Discriminator<E> discriminator) {
/* 120 */     this.discriminator = discriminator;
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
/*     */   public AppenderTracker<E> getAppenderTracker() {
/* 133 */     return this.appenderTracker;
/*     */   }
/*     */   
/*     */   public String getDiscriminatorKey() {
/* 137 */     if (this.discriminator != null) {
/* 138 */       return this.discriminator.getKey();
/*     */     }
/* 140 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\sift\SiftingAppenderBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */