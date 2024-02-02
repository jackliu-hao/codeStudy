/*     */ package org.apache.http.util;
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
/*     */ public class Args
/*     */ {
/*     */   public static void check(boolean expression, String message) {
/*  35 */     if (!expression) {
/*  36 */       throw new IllegalArgumentException(message);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void check(boolean expression, String message, Object... args) {
/*  41 */     if (!expression) {
/*  42 */       throw new IllegalArgumentException(String.format(message, args));
/*     */     }
/*     */   }
/*     */   
/*     */   public static void check(boolean expression, String message, Object arg) {
/*  47 */     if (!expression) {
/*  48 */       throw new IllegalArgumentException(String.format(message, new Object[] { arg }));
/*     */     }
/*     */   }
/*     */   
/*     */   public static <T> T notNull(T argument, String name) {
/*  53 */     if (argument == null) {
/*  54 */       throw new IllegalArgumentException(name + " may not be null");
/*     */     }
/*  56 */     return argument;
/*     */   }
/*     */   
/*     */   public static <T extends CharSequence> T notEmpty(T argument, String name) {
/*  60 */     if (argument == null) {
/*  61 */       throw new IllegalArgumentException(name + " may not be null");
/*     */     }
/*  63 */     if (TextUtils.isEmpty((CharSequence)argument)) {
/*  64 */       throw new IllegalArgumentException(name + " may not be empty");
/*     */     }
/*  66 */     return argument;
/*     */   }
/*     */   
/*     */   public static <T extends CharSequence> T notBlank(T argument, String name) {
/*  70 */     if (argument == null) {
/*  71 */       throw new IllegalArgumentException(name + " may not be null");
/*     */     }
/*  73 */     if (TextUtils.isBlank((CharSequence)argument)) {
/*  74 */       throw new IllegalArgumentException(name + " may not be blank");
/*     */     }
/*  76 */     return argument;
/*     */   }
/*     */   
/*     */   public static <T extends CharSequence> T containsNoBlanks(T argument, String name) {
/*  80 */     if (argument == null) {
/*  81 */       throw new IllegalArgumentException(name + " may not be null");
/*     */     }
/*  83 */     if (argument.length() == 0) {
/*  84 */       throw new IllegalArgumentException(name + " may not be empty");
/*     */     }
/*  86 */     if (TextUtils.containsBlanks((CharSequence)argument)) {
/*  87 */       throw new IllegalArgumentException(name + " may not contain blanks");
/*     */     }
/*  89 */     return argument;
/*     */   }
/*     */   
/*     */   public static <E, T extends java.util.Collection<E>> T notEmpty(T argument, String name) {
/*  93 */     if (argument == null) {
/*  94 */       throw new IllegalArgumentException(name + " may not be null");
/*     */     }
/*  96 */     if (argument.isEmpty()) {
/*  97 */       throw new IllegalArgumentException(name + " may not be empty");
/*     */     }
/*  99 */     return argument;
/*     */   }
/*     */   
/*     */   public static int positive(int n, String name) {
/* 103 */     if (n <= 0) {
/* 104 */       throw new IllegalArgumentException(name + " may not be negative or zero");
/*     */     }
/* 106 */     return n;
/*     */   }
/*     */   
/*     */   public static long positive(long n, String name) {
/* 110 */     if (n <= 0L) {
/* 111 */       throw new IllegalArgumentException(name + " may not be negative or zero");
/*     */     }
/* 113 */     return n;
/*     */   }
/*     */   
/*     */   public static int notNegative(int n, String name) {
/* 117 */     if (n < 0) {
/* 118 */       throw new IllegalArgumentException(name + " may not be negative");
/*     */     }
/* 120 */     return n;
/*     */   }
/*     */   
/*     */   public static long notNegative(long n, String name) {
/* 124 */     if (n < 0L) {
/* 125 */       throw new IllegalArgumentException(name + " may not be negative");
/*     */     }
/* 127 */     return n;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\Args.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */