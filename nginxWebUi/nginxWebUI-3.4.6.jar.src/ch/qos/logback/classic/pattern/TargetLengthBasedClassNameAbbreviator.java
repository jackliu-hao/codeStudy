/*     */ package ch.qos.logback.classic.pattern;
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
/*     */ public class TargetLengthBasedClassNameAbbreviator
/*     */   implements Abbreviator
/*     */ {
/*     */   final int targetLength;
/*     */   
/*     */   public TargetLengthBasedClassNameAbbreviator(int targetLength) {
/*  24 */     this.targetLength = targetLength;
/*     */   }
/*     */   
/*     */   public String abbreviate(String fqClassName) {
/*  28 */     StringBuilder buf = new StringBuilder(this.targetLength);
/*  29 */     if (fqClassName == null) {
/*  30 */       throw new IllegalArgumentException("Class name may not be null");
/*     */     }
/*     */     
/*  33 */     int inLen = fqClassName.length();
/*  34 */     if (inLen < this.targetLength) {
/*  35 */       return fqClassName;
/*     */     }
/*     */     
/*  38 */     int[] dotIndexesArray = new int[16];
/*     */ 
/*     */     
/*  41 */     int[] lengthArray = new int[17];
/*     */     
/*  43 */     int dotCount = computeDotIndexes(fqClassName, dotIndexesArray);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  48 */     if (dotCount == 0) {
/*  49 */       return fqClassName;
/*     */     }
/*     */     
/*  52 */     computeLengthArray(fqClassName, dotIndexesArray, lengthArray, dotCount);
/*     */     
/*  54 */     for (int i = 0; i <= dotCount; i++) {
/*  55 */       if (i == 0) {
/*  56 */         buf.append(fqClassName.substring(0, lengthArray[i] - 1));
/*     */       } else {
/*  58 */         buf.append(fqClassName.substring(dotIndexesArray[i - 1], dotIndexesArray[i - 1] + lengthArray[i]));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  63 */     return buf.toString();
/*     */   }
/*     */   
/*     */   static int computeDotIndexes(String className, int[] dotArray) {
/*  67 */     int dotCount = 0;
/*  68 */     int k = 0;
/*     */ 
/*     */     
/*     */     while (true) {
/*  72 */       k = className.indexOf('.', k);
/*  73 */       if (k != -1 && dotCount < 16) {
/*  74 */         dotArray[dotCount] = k;
/*  75 */         dotCount++;
/*  76 */         k++;
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/*  81 */     return dotCount;
/*     */   }
/*     */   
/*     */   void computeLengthArray(String className, int[] dotArray, int[] lengthArray, int dotCount) {
/*  85 */     int toTrim = className.length() - this.targetLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  91 */     for (int i = 0; i < dotCount; i++) {
/*  92 */       int previousDotPosition = -1;
/*  93 */       if (i > 0) {
/*  94 */         previousDotPosition = dotArray[i - 1];
/*     */       }
/*  96 */       int available = dotArray[i] - previousDotPosition - 1;
/*     */ 
/*     */       
/*  99 */       int len = (available < 1) ? available : 1;
/*     */ 
/*     */       
/* 102 */       if (toTrim > 0) {
/* 103 */         len = (available < 1) ? available : 1;
/*     */       } else {
/* 105 */         len = available;
/*     */       } 
/* 107 */       toTrim -= available - len;
/* 108 */       lengthArray[i] = len + 1;
/*     */     } 
/*     */     
/* 111 */     int lastDotIndex = dotCount - 1;
/* 112 */     lengthArray[dotCount] = className.length() - dotArray[lastDotIndex];
/*     */   }
/*     */   
/*     */   static void printArray(String msg, int[] ia) {
/* 116 */     System.out.print(msg);
/* 117 */     for (int i = 0; i < ia.length; i++) {
/* 118 */       if (i == 0) {
/* 119 */         System.out.print(ia[i]);
/*     */       } else {
/* 121 */         System.out.print(", " + ia[i]);
/*     */       } 
/*     */     } 
/* 124 */     System.out.println();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\pattern\TargetLengthBasedClassNameAbbreviator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */