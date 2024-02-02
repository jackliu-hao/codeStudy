/*     */ package ch.qos.logback.core.util;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import ch.qos.logback.core.CoreConstants;
/*     */ import ch.qos.logback.core.helpers.ThrowableToStringArray;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.StatusManager;
/*     */ import ch.qos.logback.core.status.StatusUtil;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
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
/*     */ public class StatusPrinter
/*     */ {
/*  29 */   private static PrintStream ps = System.out;
/*     */   
/*  31 */   static CachingDateFormatter cachingDateFormat = new CachingDateFormatter("HH:mm:ss,SSS");
/*     */   
/*     */   public static void setPrintStream(PrintStream printStream) {
/*  34 */     ps = printStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void printInCaseOfErrorsOrWarnings(Context context) {
/*  44 */     printInCaseOfErrorsOrWarnings(context, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void printInCaseOfErrorsOrWarnings(Context context, long threshold) {
/*  54 */     if (context == null) {
/*  55 */       throw new IllegalArgumentException("Context argument cannot be null");
/*     */     }
/*     */     
/*  58 */     StatusManager sm = context.getStatusManager();
/*  59 */     if (sm == null) {
/*  60 */       ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
/*     */     } else {
/*  62 */       StatusUtil statusUtil = new StatusUtil(context);
/*  63 */       if (statusUtil.getHighestLevel(threshold) >= 1) {
/*  64 */         print(sm, threshold);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void printIfErrorsOccured(Context context) {
/*  76 */     if (context == null) {
/*  77 */       throw new IllegalArgumentException("Context argument cannot be null");
/*     */     }
/*     */     
/*  80 */     StatusManager sm = context.getStatusManager();
/*  81 */     if (sm == null) {
/*  82 */       ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
/*     */     } else {
/*  84 */       StatusUtil statusUtil = new StatusUtil(context);
/*  85 */       if (statusUtil.getHighestLevel(0L) == 2) {
/*  86 */         print(sm);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void print(Context context) {
/*  97 */     print(context, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void print(Context context, long threshold) {
/* 105 */     if (context == null) {
/* 106 */       throw new IllegalArgumentException("Context argument cannot be null");
/*     */     }
/*     */     
/* 109 */     StatusManager sm = context.getStatusManager();
/* 110 */     if (sm == null) {
/* 111 */       ps.println("WARN: Context named \"" + context.getName() + "\" has no status manager");
/*     */     } else {
/* 113 */       print(sm, threshold);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void print(StatusManager sm) {
/* 118 */     print(sm, 0L);
/*     */   }
/*     */   
/*     */   public static void print(StatusManager sm, long threshold) {
/* 122 */     StringBuilder sb = new StringBuilder();
/* 123 */     List<Status> filteredList = StatusUtil.filterStatusListByTimeThreshold(sm.getCopyOfStatusList(), threshold);
/* 124 */     buildStrFromStatusList(sb, filteredList);
/* 125 */     ps.println(sb.toString());
/*     */   }
/*     */   
/*     */   public static void print(List<Status> statusList) {
/* 129 */     StringBuilder sb = new StringBuilder();
/* 130 */     buildStrFromStatusList(sb, statusList);
/* 131 */     ps.println(sb.toString());
/*     */   }
/*     */   
/*     */   private static void buildStrFromStatusList(StringBuilder sb, List<Status> statusList) {
/* 135 */     if (statusList == null)
/*     */       return; 
/* 137 */     for (Status s : statusList) {
/* 138 */       buildStr(sb, "", s);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void appendThrowable(StringBuilder sb, Throwable t) {
/* 146 */     String[] stringRep = ThrowableToStringArray.convert(t);
/*     */     
/* 148 */     for (String s : stringRep) {
/* 149 */       if (!s.startsWith("Caused by: "))
/*     */       {
/* 151 */         if (Character.isDigit(s.charAt(0))) {
/*     */           
/* 153 */           sb.append("\t... ");
/*     */         } else {
/*     */           
/* 156 */           sb.append("\tat ");
/*     */         }  } 
/* 158 */       sb.append(s).append(CoreConstants.LINE_SEPARATOR);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void buildStr(StringBuilder sb, String indentation, Status s) {
/*     */     String prefix;
/* 164 */     if (s.hasChildren()) {
/* 165 */       prefix = indentation + "+ ";
/*     */     } else {
/* 167 */       prefix = indentation + "|-";
/*     */     } 
/*     */     
/* 170 */     if (cachingDateFormat != null) {
/* 171 */       String dateStr = cachingDateFormat.format(s.getDate().longValue());
/* 172 */       sb.append(dateStr).append(" ");
/*     */     } 
/* 174 */     sb.append(prefix).append(s).append(CoreConstants.LINE_SEPARATOR);
/*     */     
/* 176 */     if (s.getThrowable() != null) {
/* 177 */       appendThrowable(sb, s.getThrowable());
/*     */     }
/* 179 */     if (s.hasChildren()) {
/* 180 */       Iterator<Status> ite = s.iterator();
/* 181 */       while (ite.hasNext()) {
/* 182 */         Status child = ite.next();
/* 183 */         buildStr(sb, indentation + "  ", child);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\StatusPrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */