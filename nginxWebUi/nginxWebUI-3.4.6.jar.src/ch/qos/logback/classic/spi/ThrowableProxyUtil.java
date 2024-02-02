/*     */ package ch.qos.logback.classic.spi;
/*     */ 
/*     */ import ch.qos.logback.core.CoreConstants;
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
/*     */ public class ThrowableProxyUtil
/*     */ {
/*     */   public static final int REGULAR_EXCEPTION_INDENT = 1;
/*     */   public static final int SUPPRESSED_EXCEPTION_INDENT = 1;
/*     */   private static final int BUILDER_CAPACITY = 2048;
/*     */   
/*     */   public static void build(ThrowableProxy nestedTP, Throwable nestedThrowable, ThrowableProxy parentTP) {
/*  32 */     StackTraceElement[] nestedSTE = nestedThrowable.getStackTrace();
/*     */     
/*  34 */     int commonFramesCount = -1;
/*  35 */     if (parentTP != null) {
/*  36 */       commonFramesCount = findNumberOfCommonFrames(nestedSTE, parentTP.getStackTraceElementProxyArray());
/*     */     }
/*     */     
/*  39 */     nestedTP.commonFrames = commonFramesCount;
/*  40 */     nestedTP.stackTraceElementProxyArray = steArrayToStepArray(nestedSTE);
/*     */   }
/*     */   
/*     */   static StackTraceElementProxy[] steArrayToStepArray(StackTraceElement[] stea) {
/*  44 */     if (stea == null) {
/*  45 */       return new StackTraceElementProxy[0];
/*     */     }
/*  47 */     StackTraceElementProxy[] stepa = new StackTraceElementProxy[stea.length];
/*  48 */     for (int i = 0; i < stepa.length; i++) {
/*  49 */       stepa[i] = new StackTraceElementProxy(stea[i]);
/*     */     }
/*  51 */     return stepa;
/*     */   }
/*     */   
/*     */   static int findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElementProxy[] parentSTEPArray) {
/*  55 */     if (parentSTEPArray == null || steArray == null) {
/*  56 */       return 0;
/*     */     }
/*     */     
/*  59 */     int steIndex = steArray.length - 1;
/*  60 */     int parentIndex = parentSTEPArray.length - 1;
/*  61 */     int count = 0;
/*  62 */     while (steIndex >= 0 && parentIndex >= 0) {
/*  63 */       StackTraceElement ste = steArray[steIndex];
/*  64 */       StackTraceElement otherSte = (parentSTEPArray[parentIndex]).ste;
/*  65 */       if (ste.equals(otherSte)) {
/*  66 */         count++;
/*     */ 
/*     */ 
/*     */         
/*  70 */         steIndex--;
/*  71 */         parentIndex--;
/*     */       } 
/*  73 */     }  return count;
/*     */   }
/*     */   
/*     */   public static String asString(IThrowableProxy tp) {
/*  77 */     StringBuilder sb = new StringBuilder(2048);
/*     */     
/*  79 */     recursiveAppend(sb, null, 1, tp);
/*     */     
/*  81 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private static void recursiveAppend(StringBuilder sb, String prefix, int indent, IThrowableProxy tp) {
/*  85 */     if (tp == null)
/*     */       return; 
/*  87 */     subjoinFirstLine(sb, prefix, indent, tp);
/*  88 */     sb.append(CoreConstants.LINE_SEPARATOR);
/*  89 */     subjoinSTEPArray(sb, indent, tp);
/*  90 */     IThrowableProxy[] suppressed = tp.getSuppressed();
/*  91 */     if (suppressed != null) {
/*  92 */       for (IThrowableProxy current : suppressed) {
/*  93 */         recursiveAppend(sb, "Suppressed: ", indent + 1, current);
/*     */       }
/*     */     }
/*  96 */     recursiveAppend(sb, "Caused by: ", indent, tp.getCause());
/*     */   }
/*     */   
/*     */   public static void indent(StringBuilder buf, int indent) {
/* 100 */     for (int j = 0; j < indent; j++) {
/* 101 */       buf.append('\t');
/*     */     }
/*     */   }
/*     */   
/*     */   private static void subjoinFirstLine(StringBuilder buf, String prefix, int indent, IThrowableProxy tp) {
/* 106 */     indent(buf, indent - 1);
/* 107 */     if (prefix != null) {
/* 108 */       buf.append(prefix);
/*     */     }
/* 110 */     subjoinExceptionMessage(buf, tp);
/*     */   }
/*     */   
/*     */   public static void subjoinPackagingData(StringBuilder builder, StackTraceElementProxy step) {
/* 114 */     if (step != null) {
/* 115 */       ClassPackagingData cpd = step.getClassPackagingData();
/* 116 */       if (cpd != null) {
/* 117 */         if (!cpd.isExact()) {
/* 118 */           builder.append(" ~[");
/*     */         } else {
/* 120 */           builder.append(" [");
/*     */         } 
/*     */         
/* 123 */         builder.append(cpd.getCodeLocation()).append(':').append(cpd.getVersion()).append(']');
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void subjoinSTEP(StringBuilder sb, StackTraceElementProxy step) {
/* 129 */     sb.append(step.toString());
/* 130 */     subjoinPackagingData(sb, step);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void subjoinSTEPArray(StringBuilder sb, IThrowableProxy tp) {
/* 140 */     subjoinSTEPArray(sb, 1, tp);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void subjoinSTEPArray(StringBuilder sb, int indentLevel, IThrowableProxy tp) {
/* 149 */     StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
/* 150 */     int commonFrames = tp.getCommonFrames();
/*     */     
/* 152 */     for (int i = 0; i < stepArray.length - commonFrames; i++) {
/* 153 */       StackTraceElementProxy step = stepArray[i];
/* 154 */       indent(sb, indentLevel);
/* 155 */       subjoinSTEP(sb, step);
/* 156 */       sb.append(CoreConstants.LINE_SEPARATOR);
/*     */     } 
/*     */     
/* 159 */     if (commonFrames > 0) {
/* 160 */       indent(sb, indentLevel);
/* 161 */       sb.append("... ").append(commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void subjoinFirstLine(StringBuilder buf, IThrowableProxy tp) {
/* 167 */     int commonFrames = tp.getCommonFrames();
/* 168 */     if (commonFrames > 0) {
/* 169 */       buf.append("Caused by: ");
/*     */     }
/* 171 */     subjoinExceptionMessage(buf, tp);
/*     */   }
/*     */   
/*     */   public static void subjoinFirstLineRootCauseFirst(StringBuilder buf, IThrowableProxy tp) {
/* 175 */     if (tp.getCause() != null) {
/* 176 */       buf.append("Wrapped by: ");
/*     */     }
/* 178 */     subjoinExceptionMessage(buf, tp);
/*     */   }
/*     */   
/*     */   private static void subjoinExceptionMessage(StringBuilder buf, IThrowableProxy tp) {
/* 182 */     buf.append(tp.getClassName()).append(": ").append(tp.getMessage());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\ThrowableProxyUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */