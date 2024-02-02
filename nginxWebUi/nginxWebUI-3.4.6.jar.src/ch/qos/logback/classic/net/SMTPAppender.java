/*     */ package ch.qos.logback.classic.net;
/*     */ 
/*     */ import ch.qos.logback.classic.ClassicConstants;
/*     */ import ch.qos.logback.classic.PatternLayout;
/*     */ import ch.qos.logback.classic.boolex.OnErrorEvaluator;
/*     */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*     */ import ch.qos.logback.core.Layout;
/*     */ import ch.qos.logback.core.boolex.EventEvaluator;
/*     */ import ch.qos.logback.core.helpers.CyclicBuffer;
/*     */ import ch.qos.logback.core.net.SMTPAppenderBase;
/*     */ import ch.qos.logback.core.pattern.PatternLayoutBase;
/*     */ import org.slf4j.Marker;
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
/*     */ public class SMTPAppender
/*     */   extends SMTPAppenderBase<ILoggingEvent>
/*     */ {
/*     */   static final String DEFAULT_SUBJECT_PATTERN = "%logger{20} - %m";
/*  42 */   private int bufferSize = 512;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean includeCallerData = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  55 */     if (this.eventEvaluator == null) {
/*  56 */       OnErrorEvaluator onError = new OnErrorEvaluator();
/*  57 */       onError.setContext(getContext());
/*  58 */       onError.setName("onError");
/*  59 */       onError.start();
/*  60 */       this.eventEvaluator = (EventEvaluator)onError;
/*     */     } 
/*  62 */     super.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SMTPAppender(EventEvaluator<ILoggingEvent> eventEvaluator) {
/*  70 */     this.eventEvaluator = eventEvaluator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void subAppend(CyclicBuffer<ILoggingEvent> cb, ILoggingEvent event) {
/*  78 */     if (this.includeCallerData) {
/*  79 */       event.getCallerData();
/*     */     }
/*  81 */     event.prepareForDeferredProcessing();
/*  82 */     cb.add(event);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void fillBuffer(CyclicBuffer<ILoggingEvent> cb, StringBuffer sbuf) {
/*  87 */     int len = cb.length();
/*  88 */     for (int i = 0; i < len; i++) {
/*  89 */       ILoggingEvent event = (ILoggingEvent)cb.get();
/*  90 */       sbuf.append(this.layout.doLayout(event));
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean eventMarksEndOfLife(ILoggingEvent eventObject) {
/*  95 */     Marker marker = eventObject.getMarker();
/*  96 */     if (marker == null) {
/*  97 */       return false;
/*     */     }
/*  99 */     return marker.contains(ClassicConstants.FINALIZE_SESSION_MARKER);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Layout<ILoggingEvent> makeSubjectLayout(String subjectStr) {
/* 104 */     if (subjectStr == null) {
/* 105 */       subjectStr = "%logger{20} - %m";
/*     */     }
/* 107 */     PatternLayout pl = new PatternLayout();
/* 108 */     pl.setContext(getContext());
/* 109 */     pl.setPattern(subjectStr);
/*     */ 
/*     */ 
/*     */     
/* 113 */     pl.setPostCompileProcessor(null);
/* 114 */     pl.start();
/* 115 */     return (Layout<ILoggingEvent>)pl;
/*     */   }
/*     */   
/*     */   protected PatternLayout makeNewToPatternLayout(String toPattern) {
/* 119 */     PatternLayout pl = new PatternLayout();
/* 120 */     pl.setPattern(toPattern + "%nopex");
/* 121 */     return pl;
/*     */   }
/*     */   
/*     */   public boolean isIncludeCallerData() {
/* 125 */     return this.includeCallerData;
/*     */   }
/*     */   
/*     */   public void setIncludeCallerData(boolean includeCallerData) {
/* 129 */     this.includeCallerData = includeCallerData;
/*     */   }
/*     */   
/*     */   public SMTPAppender() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\SMTPAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */