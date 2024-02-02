/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.StrUtil;
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
/*     */ public class TextSimilarity
/*     */ {
/*     */   public static double similar(String strA, String strB) {
/*     */     String newStrA, newStrB;
/*  29 */     if (strA.length() < strB.length()) {
/*  30 */       newStrA = removeSign(strB);
/*  31 */       newStrB = removeSign(strA);
/*     */     } else {
/*  33 */       newStrA = removeSign(strA);
/*  34 */       newStrB = removeSign(strB);
/*     */     } 
/*     */ 
/*     */     
/*  38 */     int temp = Math.max(newStrA.length(), newStrB.length());
/*  39 */     if (0 == temp)
/*     */     {
/*  41 */       return 1.0D;
/*     */     }
/*     */     
/*  44 */     int commonLength = longestCommonSubstringLength(newStrA, newStrB);
/*  45 */     return NumberUtil.div(commonLength, temp);
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
/*     */   public static String similar(String strA, String strB, int scale) {
/*  57 */     return NumberUtil.formatPercent(similar(strA, strB), scale);
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
/*     */   
/*     */   public static String longestCommonSubstring(String strA, String strB) {
/*  71 */     int[][] matrix = generateMatrix(strA, strB);
/*     */     
/*  73 */     int m = strA.length();
/*  74 */     int n = strB.length();
/*     */ 
/*     */     
/*  77 */     char[] result = new char[matrix[m][n]];
/*  78 */     int currentIndex = result.length - 1;
/*  79 */     while (matrix[m][n] != 0) {
/*  80 */       if (matrix[m][n] == matrix[m][n - 1]) {
/*  81 */         n--; continue;
/*  82 */       }  if (matrix[m][n] == matrix[m - 1][n]) {
/*  83 */         m--; continue;
/*     */       } 
/*  85 */       result[currentIndex] = strA.charAt(m - 1);
/*  86 */       currentIndex--;
/*  87 */       n--;
/*  88 */       m--;
/*     */     } 
/*     */     
/*  91 */     return new String(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String removeSign(String str) {
/* 102 */     int length = str.length();
/* 103 */     StringBuilder sb = StrUtil.builder(length);
/*     */ 
/*     */     
/* 106 */     for (int i = 0; i < length; i++) {
/* 107 */       char c = str.charAt(i);
/* 108 */       if (isValidChar(c)) {
/* 109 */         sb.append(c);
/*     */       }
/*     */     } 
/*     */     
/* 113 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isValidChar(char charValue) {
/* 123 */     return ((charValue >= '一' && charValue <= '鿿') || (charValue >= 'a' && charValue <= 'z') || (charValue >= 'A' && charValue <= 'Z') || (charValue >= '0' && charValue <= '9'));
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
/*     */   
/*     */   private static int longestCommonSubstringLength(String strA, String strB) {
/* 137 */     int m = strA.length();
/* 138 */     int n = strB.length();
/* 139 */     return generateMatrix(strA, strB)[m][n];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int[][] generateMatrix(String strA, String strB) {
/* 150 */     int m = strA.length();
/* 151 */     int n = strB.length();
/*     */ 
/*     */ 
/*     */     
/* 155 */     int[][] matrix = new int[m + 1][n + 1];
/* 156 */     for (int i = 1; i <= m; i++) {
/* 157 */       for (int j = 1; j <= n; j++) {
/* 158 */         if (strA.charAt(i - 1) == strB.charAt(j - 1)) {
/* 159 */           matrix[i][j] = matrix[i - 1][j - 1] + 1;
/*     */         } else {
/* 161 */           matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 166 */     return matrix;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\TextSimilarity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */