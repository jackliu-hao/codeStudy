/*     */ package com.google.zxing;
/*     */ 
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
/*     */ public enum DecodeHintType
/*     */ {
/*  35 */   OTHER(Object.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   PURE_BARCODE(Void.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   POSSIBLE_FORMATS(List.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   TRY_HARDER(Void.class),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   CHARACTER_SET(String.class),
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   ALLOWED_LENGTHS(int[].class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   ASSUME_CODE_39_CHECK_DIGIT(Void.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   ASSUME_GS1(Void.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   RETURN_CODABAR_START_END(Void.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   NEED_RESULT_POINT_CALLBACK(ResultPointCallback.class),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  99 */   ALLOWED_EAN_EXTENSIONS(int[].class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Class<?> valueType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   DecodeHintType(Class<?> valueType) {
/* 115 */     this.valueType = valueType;
/*     */   }
/*     */   
/*     */   public Class<?> getValueType() {
/* 119 */     return this.valueType;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\DecodeHintType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */