/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
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
/*     */ public enum ZipMethod
/*     */ {
/*  39 */   STORED(0),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   UNSHRINKING(1),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   EXPANDING_LEVEL_1(2),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   EXPANDING_LEVEL_2(3),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   EXPANDING_LEVEL_3(4),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   EXPANDING_LEVEL_4(5),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   IMPLODING(6),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   TOKENIZATION(7),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   DEFLATED(8),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 110 */   ENHANCED_DEFLATED(9),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 117 */   PKWARE_IMPLODING(10),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 124 */   BZIP2(12),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 132 */   LZMA(14),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 140 */   XZ(95),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 147 */   JPEG(96),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 154 */   WAVPACK(97),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 161 */   PPMD(98),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 169 */   AES_ENCRYPTED(99),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 174 */   UNKNOWN;
/*     */   
/*     */   static final int UNKNOWN_CODE = -1;
/*     */   
/*     */   private final int code;
/*     */   
/*     */   private static final Map<Integer, ZipMethod> codeToEnum;
/*     */   
/*     */   static {
/* 183 */     Map<Integer, ZipMethod> cte = new HashMap<>();
/* 184 */     for (ZipMethod method : values()) {
/* 185 */       cte.put(Integer.valueOf(method.getCode()), method);
/*     */     }
/* 187 */     codeToEnum = Collections.unmodifiableMap(cte);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ZipMethod(int code) {
/* 198 */     this.code = code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCode() {
/* 209 */     return this.code;
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
/*     */   public static ZipMethod getMethodByCode(int code) {
/* 221 */     return codeToEnum.get(Integer.valueOf(code));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */