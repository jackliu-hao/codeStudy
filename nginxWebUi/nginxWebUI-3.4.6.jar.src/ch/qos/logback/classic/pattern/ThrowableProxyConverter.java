/*     */ package ch.qos.logback.classic.pattern;
/*     */ 
/*     */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*     */ import ch.qos.logback.classic.spi.IThrowableProxy;
/*     */ import ch.qos.logback.classic.spi.StackTraceElementProxy;
/*     */ import ch.qos.logback.classic.spi.ThrowableProxyUtil;
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.boolex.EvaluationException;
/*     */ import ch.qos.logback.core.boolex.EventEvaluator;
/*     */ import ch.qos.logback.core.status.ErrorStatus;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class ThrowableProxyConverter
/*     */   extends ThrowableHandlingConverter
/*     */ {
/*     */   protected static final int BUILDER_CAPACITY = 2048;
/*     */   int lengthOption;
/*  40 */   List<EventEvaluator<ILoggingEvent>> evaluatorList = null;
/*  41 */   List<String> ignoredStackTraceLines = null;
/*     */   
/*  43 */   int errorCount = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*  48 */     String lengthStr = getFirstOption();
/*     */     
/*  50 */     if (lengthStr == null) {
/*  51 */       this.lengthOption = Integer.MAX_VALUE;
/*     */     } else {
/*  53 */       lengthStr = lengthStr.toLowerCase();
/*  54 */       if ("full".equals(lengthStr)) {
/*  55 */         this.lengthOption = Integer.MAX_VALUE;
/*  56 */       } else if ("short".equals(lengthStr)) {
/*  57 */         this.lengthOption = 1;
/*     */       } else {
/*     */         try {
/*  60 */           this.lengthOption = Integer.parseInt(lengthStr);
/*  61 */         } catch (NumberFormatException nfe) {
/*  62 */           addError("Could not parse [" + lengthStr + "] as an integer");
/*  63 */           this.lengthOption = Integer.MAX_VALUE;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  68 */     List<String> optionList = getOptionList();
/*     */     
/*  70 */     if (optionList != null && optionList.size() > 1) {
/*  71 */       int optionListSize = optionList.size();
/*  72 */       for (int i = 1; i < optionListSize; i++) {
/*  73 */         String evaluatorOrIgnoredStackTraceLine = optionList.get(i);
/*  74 */         Context context = getContext();
/*  75 */         Map<String, EventEvaluator<?>> evaluatorMap = (Map<String, EventEvaluator<?>>)context.getObject("EVALUATOR_MAP");
/*  76 */         EventEvaluator<ILoggingEvent> ee = (EventEvaluator<ILoggingEvent>)evaluatorMap.get(evaluatorOrIgnoredStackTraceLine);
/*  77 */         if (ee != null) {
/*  78 */           addEvaluator(ee);
/*     */         } else {
/*  80 */           addIgnoreStackTraceLine(evaluatorOrIgnoredStackTraceLine);
/*     */         } 
/*     */       } 
/*     */     } 
/*  84 */     super.start();
/*     */   }
/*     */   
/*     */   private void addEvaluator(EventEvaluator<ILoggingEvent> ee) {
/*  88 */     if (this.evaluatorList == null) {
/*  89 */       this.evaluatorList = new ArrayList<EventEvaluator<ILoggingEvent>>();
/*     */     }
/*  91 */     this.evaluatorList.add(ee);
/*     */   }
/*     */   
/*     */   private void addIgnoreStackTraceLine(String ignoredStackTraceLine) {
/*  95 */     if (this.ignoredStackTraceLines == null) {
/*  96 */       this.ignoredStackTraceLines = new ArrayList<String>();
/*     */     }
/*  98 */     this.ignoredStackTraceLines.add(ignoredStackTraceLine);
/*     */   }
/*     */   
/*     */   public void stop() {
/* 102 */     this.evaluatorList = null;
/* 103 */     super.stop();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void extraData(StringBuilder builder, StackTraceElementProxy step) {}
/*     */ 
/*     */   
/*     */   public String convert(ILoggingEvent event) {
/* 112 */     IThrowableProxy tp = event.getThrowableProxy();
/* 113 */     if (tp == null) {
/* 114 */       return "";
/*     */     }
/*     */ 
/*     */     
/* 118 */     if (this.evaluatorList != null) {
/* 119 */       boolean printStack = true;
/* 120 */       for (int i = 0; i < this.evaluatorList.size(); i++) {
/* 121 */         EventEvaluator<ILoggingEvent> ee = this.evaluatorList.get(i);
/*     */         try {
/* 123 */           if (ee.evaluate(event)) {
/* 124 */             printStack = false;
/*     */             break;
/*     */           } 
/* 127 */         } catch (EvaluationException eex) {
/* 128 */           this.errorCount++;
/* 129 */           if (this.errorCount < 4) {
/* 130 */             addError("Exception thrown for evaluator named [" + ee.getName() + "]", (Throwable)eex);
/* 131 */           } else if (this.errorCount == 4) {
/* 132 */             ErrorStatus errorStatus = new ErrorStatus("Exception thrown for evaluator named [" + ee.getName() + "].", this, (Throwable)eex);
/* 133 */             errorStatus.add((Status)new ErrorStatus("This was the last warning about this evaluator's errors.We don't want the StatusManager to get flooded.", this));
/*     */             
/* 135 */             addStatus((Status)errorStatus);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 140 */       if (!printStack) {
/* 141 */         return "";
/*     */       }
/*     */     } 
/*     */     
/* 145 */     return throwableProxyToString(tp);
/*     */   }
/*     */   
/*     */   protected String throwableProxyToString(IThrowableProxy tp) {
/* 149 */     StringBuilder sb = new StringBuilder(2048);
/*     */     
/* 151 */     recursiveAppend(sb, (String)null, 1, tp);
/*     */     
/* 153 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private void recursiveAppend(StringBuilder sb, String prefix, int indent, IThrowableProxy tp) {
/* 157 */     if (tp == null)
/*     */       return; 
/* 159 */     subjoinFirstLine(sb, prefix, indent, tp);
/* 160 */     sb.append(CoreConstants.LINE_SEPARATOR);
/* 161 */     subjoinSTEPArray(sb, indent, tp);
/* 162 */     IThrowableProxy[] suppressed = tp.getSuppressed();
/* 163 */     if (suppressed != null) {
/* 164 */       for (IThrowableProxy current : suppressed) {
/* 165 */         recursiveAppend(sb, "Suppressed: ", indent + 1, current);
/*     */       }
/*     */     }
/* 168 */     recursiveAppend(sb, "Caused by: ", indent, tp.getCause());
/*     */   }
/*     */   
/*     */   private void subjoinFirstLine(StringBuilder buf, String prefix, int indent, IThrowableProxy tp) {
/* 172 */     ThrowableProxyUtil.indent(buf, indent - 1);
/* 173 */     if (prefix != null) {
/* 174 */       buf.append(prefix);
/*     */     }
/* 176 */     subjoinExceptionMessage(buf, tp);
/*     */   }
/*     */   
/*     */   private void subjoinExceptionMessage(StringBuilder buf, IThrowableProxy tp) {
/* 180 */     buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
/*     */   }
/*     */   
/*     */   protected void subjoinSTEPArray(StringBuilder buf, int indent, IThrowableProxy tp) {
/* 184 */     StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
/* 185 */     int commonFrames = tp.getCommonFrames();
/*     */     
/* 187 */     boolean unrestrictedPrinting = (this.lengthOption > stepArray.length);
/*     */     
/* 189 */     int maxIndex = unrestrictedPrinting ? stepArray.length : this.lengthOption;
/* 190 */     if (commonFrames > 0 && unrestrictedPrinting) {
/* 191 */       maxIndex -= commonFrames;
/*     */     }
/*     */     
/* 194 */     int ignoredCount = 0;
/* 195 */     for (int i = 0; i < maxIndex; i++) {
/* 196 */       StackTraceElementProxy element = stepArray[i];
/* 197 */       if (!isIgnoredStackTraceLine(element.toString())) {
/* 198 */         ThrowableProxyUtil.indent(buf, indent);
/* 199 */         printStackLine(buf, ignoredCount, element);
/* 200 */         ignoredCount = 0;
/* 201 */         buf.append(CoreConstants.LINE_SEPARATOR);
/*     */       } else {
/* 203 */         ignoredCount++;
/* 204 */         if (maxIndex < stepArray.length) {
/* 205 */           maxIndex++;
/*     */         }
/*     */       } 
/*     */     } 
/* 209 */     if (ignoredCount > 0) {
/* 210 */       printIgnoredCount(buf, ignoredCount);
/* 211 */       buf.append(CoreConstants.LINE_SEPARATOR);
/*     */     } 
/*     */     
/* 214 */     if (commonFrames > 0 && unrestrictedPrinting) {
/* 215 */       ThrowableProxyUtil.indent(buf, indent);
/* 216 */       buf.append("... ").append(tp.getCommonFrames()).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void printStackLine(StringBuilder buf, int ignoredCount, StackTraceElementProxy element) {
/* 221 */     buf.append(element);
/* 222 */     extraData(buf, element);
/* 223 */     if (ignoredCount > 0) {
/* 224 */       printIgnoredCount(buf, ignoredCount);
/*     */     }
/*     */   }
/*     */   
/*     */   private void printIgnoredCount(StringBuilder buf, int ignoredCount) {
/* 229 */     buf.append(" [").append(ignoredCount).append(" skipped]");
/*     */   }
/*     */   
/*     */   private boolean isIgnoredStackTraceLine(String line) {
/* 233 */     if (this.ignoredStackTraceLines != null) {
/* 234 */       for (String ignoredStackTraceLine : this.ignoredStackTraceLines) {
/* 235 */         if (line.contains(ignoredStackTraceLine)) {
/* 236 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 240 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\ThrowableProxyConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */