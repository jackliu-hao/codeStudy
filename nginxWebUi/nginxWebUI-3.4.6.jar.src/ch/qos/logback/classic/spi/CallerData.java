/*     */ package ch.qos.logback.classic.spi;
/*     */ 
/*     */ import ch.qos.logback.core.CoreConstants;
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
/*     */ public class CallerData
/*     */ {
/*     */   public static final String NA = "?";
/*     */   private static final String LOG4J_CATEGORY = "org.apache.log4j.Category";
/*     */   private static final String SLF4J_BOUNDARY = "org.slf4j.Logger";
/*     */   public static final int LINE_NA = -1;
/*  44 */   public static final String CALLER_DATA_NA = "?#?:?" + CoreConstants.LINE_SEPARATOR;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   public static final StackTraceElement[] EMPTY_CALLER_DATA_ARRAY = new StackTraceElement[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StackTraceElement[] extract(Throwable t, String fqnOfInvokingClass, int maxDepth, List<String> frameworkPackageList) {
/*  56 */     if (t == null) {
/*  57 */       return null;
/*     */     }
/*     */     
/*  60 */     StackTraceElement[] steArray = t.getStackTrace();
/*     */ 
/*     */     
/*  63 */     int found = -1;
/*  64 */     for (int i = 0; i < steArray.length; i++) {
/*  65 */       if (isInFrameworkSpace(steArray[i].getClassName(), fqnOfInvokingClass, frameworkPackageList)) {
/*     */         
/*  67 */         found = i + 1;
/*     */       }
/*  69 */       else if (found != -1) {
/*     */         break;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  76 */     if (found == -1) {
/*  77 */       return EMPTY_CALLER_DATA_ARRAY;
/*     */     }
/*     */     
/*  80 */     int availableDepth = steArray.length - found;
/*  81 */     int desiredDepth = (maxDepth < availableDepth) ? maxDepth : availableDepth;
/*     */     
/*  83 */     StackTraceElement[] callerDataArray = new StackTraceElement[desiredDepth];
/*  84 */     for (int j = 0; j < desiredDepth; j++) {
/*  85 */       callerDataArray[j] = steArray[found + j];
/*     */     }
/*  87 */     return callerDataArray;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isInFrameworkSpace(String currentClass, String fqnOfInvokingClass, List<String> frameworkPackageList) {
/*  93 */     if (currentClass.equals(fqnOfInvokingClass) || currentClass.equals("org.apache.log4j.Category") || currentClass.startsWith("org.slf4j.Logger") || 
/*  94 */       isInFrameworkSpaceList(currentClass, frameworkPackageList)) {
/*  95 */       return true;
/*     */     }
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isInFrameworkSpaceList(String currentClass, List<String> frameworkPackageList) {
/* 105 */     if (frameworkPackageList == null) {
/* 106 */       return false;
/*     */     }
/* 108 */     for (String s : frameworkPackageList) {
/* 109 */       if (currentClass.startsWith(s))
/* 110 */         return true; 
/*     */     } 
/* 112 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StackTraceElement naInstance() {
/* 122 */     return new StackTraceElement("?", "?", "?", -1);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\CallerData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */