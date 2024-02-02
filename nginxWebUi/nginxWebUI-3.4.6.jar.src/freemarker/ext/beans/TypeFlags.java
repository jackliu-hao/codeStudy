/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class TypeFlags
/*     */ {
/*     */   static final int WIDENED_NUMERICAL_UNWRAPPING_HINT = 1;
/*     */   static final int BYTE = 4;
/*     */   static final int SHORT = 8;
/*     */   static final int INTEGER = 16;
/*     */   static final int LONG = 32;
/*     */   static final int FLOAT = 64;
/*     */   static final int DOUBLE = 128;
/*     */   static final int BIG_INTEGER = 256;
/*     */   static final int BIG_DECIMAL = 512;
/*     */   static final int UNKNOWN_NUMERICAL_TYPE = 1024;
/*     */   static final int ACCEPTS_NUMBER = 2048;
/*     */   static final int ACCEPTS_DATE = 4096;
/*     */   static final int ACCEPTS_STRING = 8192;
/*     */   static final int ACCEPTS_BOOLEAN = 16384;
/*     */   static final int ACCEPTS_MAP = 32768;
/*     */   static final int ACCEPTS_LIST = 65536;
/*     */   static final int ACCEPTS_SET = 131072;
/*     */   static final int ACCEPTS_ARRAY = 262144;
/*     */   static final int CHARACTER = 524288;
/*     */   static final int ACCEPTS_ANY_OBJECT = 522240;
/*     */   static final int MASK_KNOWN_INTEGERS = 316;
/*     */   static final int MASK_KNOWN_NONINTEGERS = 704;
/*     */   static final int MASK_ALL_KNOWN_NUMERICALS = 1020;
/*     */   static final int MASK_ALL_NUMERICALS = 2044;
/*     */   
/*     */   static int classToTypeFlags(Class<Object> pClass) {
/*  75 */     if (pClass == Object.class)
/*  76 */       return 522240; 
/*  77 */     if (pClass == String.class)
/*  78 */       return 8192; 
/*  79 */     if (pClass.isPrimitive()) {
/*  80 */       if (pClass == int.class) return 2064; 
/*  81 */       if (pClass == long.class) return 2080; 
/*  82 */       if (pClass == double.class) return 2176; 
/*  83 */       if (pClass == float.class) return 2112; 
/*  84 */       if (pClass == byte.class) return 2052; 
/*  85 */       if (pClass == short.class) return 2056; 
/*  86 */       if (pClass == char.class) return 524288; 
/*  87 */       if (pClass == boolean.class) return 16384; 
/*  88 */       return 0;
/*  89 */     }  if (Number.class.isAssignableFrom(pClass)) {
/*  90 */       if (pClass == Integer.class) return 2064; 
/*  91 */       if (pClass == Long.class) return 2080; 
/*  92 */       if (pClass == Double.class) return 2176; 
/*  93 */       if (pClass == Float.class) return 2112; 
/*  94 */       if (pClass == Byte.class) return 2052; 
/*  95 */       if (pClass == Short.class) return 2056; 
/*  96 */       if (BigDecimal.class.isAssignableFrom(pClass)) return 2560; 
/*  97 */       if (BigInteger.class.isAssignableFrom(pClass)) return 2304; 
/*  98 */       return 3072;
/*  99 */     }  if (pClass.isArray()) {
/* 100 */       return 262144;
/*     */     }
/* 102 */     int flags = 0;
/* 103 */     if (pClass.isAssignableFrom(String.class)) {
/* 104 */       flags |= 0x2000;
/*     */     }
/* 106 */     if (pClass.isAssignableFrom(Date.class)) {
/* 107 */       flags |= 0x1000;
/*     */     }
/* 109 */     if (pClass.isAssignableFrom(Boolean.class)) {
/* 110 */       flags |= 0x4000;
/*     */     }
/* 112 */     if (pClass.isAssignableFrom(Map.class)) {
/* 113 */       flags |= 0x8000;
/*     */     }
/* 115 */     if (pClass.isAssignableFrom(List.class)) {
/* 116 */       flags |= 0x10000;
/*     */     }
/* 118 */     if (pClass.isAssignableFrom(Set.class)) {
/* 119 */       flags |= 0x20000;
/*     */     }
/*     */     
/* 122 */     if (pClass == Character.class) {
/* 123 */       flags |= 0x80000;
/*     */     }
/*     */     
/* 126 */     return flags;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\TypeFlags.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */