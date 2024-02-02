/*     */ package ch.qos.logback.classic.pattern;
/*     */ 
/*     */ import ch.qos.logback.classic.spi.CallerData;
/*     */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.boolex.EvaluationException;
/*     */ import ch.qos.logback.core.boolex.EventEvaluator;
/*     */ import ch.qos.logback.core.status.ErrorStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class CallerDataConverter
/*     */   extends ClassicConverter
/*     */ {
/*     */   public static final String DEFAULT_CALLER_LINE_PREFIX = "Caller+";
/*     */   public static final String DEFAULT_RANGE_DELIMITER = "..";
/*  40 */   private int depthStart = 0;
/*  41 */   private int depthEnd = 5;
/*  42 */   List<EventEvaluator<ILoggingEvent>> evaluatorList = null;
/*     */   
/*  44 */   final int MAX_ERROR_COUNT = 4;
/*  45 */   int errorCount = 0;
/*     */ 
/*     */   
/*     */   public void start() {
/*  49 */     String depthStr = getFirstOption();
/*  50 */     if (depthStr == null) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/*  55 */       if (isRange(depthStr)) {
/*  56 */         String[] numbers = splitRange(depthStr);
/*  57 */         if (numbers.length == 2) {
/*  58 */           this.depthStart = Integer.parseInt(numbers[0]);
/*  59 */           this.depthEnd = Integer.parseInt(numbers[1]);
/*  60 */           checkRange();
/*     */         } else {
/*  62 */           addError("Failed to parse depth option as range [" + depthStr + "]");
/*     */         } 
/*     */       } else {
/*  65 */         this.depthEnd = Integer.parseInt(depthStr);
/*     */       } 
/*  67 */     } catch (NumberFormatException nfe) {
/*  68 */       addError("Failed to parse depth option [" + depthStr + "]", nfe);
/*     */     } 
/*     */     
/*  71 */     List<String> optionList = getOptionList();
/*     */     
/*  73 */     if (optionList != null && optionList.size() > 1) {
/*  74 */       int optionListSize = optionList.size();
/*  75 */       for (int i = 1; i < optionListSize; i++) {
/*  76 */         String evaluatorStr = optionList.get(i);
/*  77 */         Context context = getContext();
/*  78 */         if (context != null) {
/*  79 */           Map<String, EventEvaluator<?>> evaluatorMap = (Map<String, EventEvaluator<?>>)context.getObject("EVALUATOR_MAP");
/*  80 */           EventEvaluator<ILoggingEvent> ee = (EventEvaluator<ILoggingEvent>)evaluatorMap.get(evaluatorStr);
/*  81 */           if (ee != null) {
/*  82 */             addEvaluator(ee);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isRange(String depthStr) {
/*  90 */     return depthStr.contains(getDefaultRangeDelimiter());
/*     */   }
/*     */   
/*     */   private String[] splitRange(String depthStr) {
/*  94 */     return depthStr.split(Pattern.quote(getDefaultRangeDelimiter()), 2);
/*     */   }
/*     */   
/*     */   private void checkRange() {
/*  98 */     if (this.depthStart < 0 || this.depthEnd < 0) {
/*  99 */       addError("Invalid depthStart/depthEnd range [" + this.depthStart + ", " + this.depthEnd + "] (negative values are not allowed)");
/* 100 */     } else if (this.depthStart >= this.depthEnd) {
/* 101 */       addError("Invalid depthEnd range [" + this.depthStart + ", " + this.depthEnd + "] (start greater or equal to end)");
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addEvaluator(EventEvaluator<ILoggingEvent> ee) {
/* 106 */     if (this.evaluatorList == null) {
/* 107 */       this.evaluatorList = new ArrayList<EventEvaluator<ILoggingEvent>>();
/*     */     }
/* 109 */     this.evaluatorList.add(ee);
/*     */   }
/*     */   
/*     */   public String convert(ILoggingEvent le) {
/* 113 */     StringBuilder buf = new StringBuilder();
/*     */     
/* 115 */     if (this.evaluatorList != null) {
/* 116 */       boolean printCallerData = false;
/* 117 */       for (int i = 0; i < this.evaluatorList.size(); i++) {
/* 118 */         EventEvaluator<ILoggingEvent> ee = this.evaluatorList.get(i);
/*     */         try {
/* 120 */           if (ee.evaluate(le)) {
/* 121 */             printCallerData = true;
/*     */             break;
/*     */           } 
/* 124 */         } catch (EvaluationException eex) {
/* 125 */           this.errorCount++;
/* 126 */           if (this.errorCount < 4) {
/* 127 */             addError("Exception thrown for evaluator named [" + ee.getName() + "]", (Throwable)eex);
/* 128 */           } else if (this.errorCount == 4) {
/* 129 */             ErrorStatus errorStatus = new ErrorStatus("Exception thrown for evaluator named [" + ee.getName() + "].", this, (Throwable)eex);
/* 130 */             errorStatus.add((Status)new ErrorStatus("This was the last warning about this evaluator's errors.We don't want the StatusManager to get flooded.", this));
/*     */             
/* 132 */             addStatus((Status)errorStatus);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 138 */       if (!printCallerData) {
/* 139 */         return "";
/*     */       }
/*     */     } 
/*     */     
/* 143 */     StackTraceElement[] cda = le.getCallerData();
/* 144 */     if (cda != null && cda.length > this.depthStart) {
/* 145 */       int limit = (this.depthEnd < cda.length) ? this.depthEnd : cda.length;
/*     */       
/* 147 */       for (int i = this.depthStart; i < limit; i++) {
/* 148 */         buf.append(getCallerLinePrefix());
/* 149 */         buf.append(i);
/* 150 */         buf.append("\t at ");
/* 151 */         buf.append(cda[i]);
/* 152 */         buf.append(CoreConstants.LINE_SEPARATOR);
/*     */       } 
/* 154 */       return buf.toString();
/*     */     } 
/* 156 */     return CallerData.CALLER_DATA_NA;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getCallerLinePrefix() {
/* 161 */     return "Caller+";
/*     */   }
/*     */   
/*     */   protected String getDefaultRangeDelimiter() {
/* 165 */     return "..";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\CallerDataConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */