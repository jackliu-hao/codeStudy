/*     */ package com.google.zxing.oned.rss.expanded.decoders;
/*     */ 
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.common.BitArray;
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
/*     */ final class AI013x0x1xDecoder
/*     */   extends AI01weightDecoder
/*     */ {
/*     */   private static final int HEADER_SIZE = 8;
/*     */   private static final int WEIGHT_SIZE = 20;
/*     */   private static final int DATE_SIZE = 16;
/*     */   private final String dateCode;
/*     */   private final String firstAIdigits;
/*     */   
/*     */   AI013x0x1xDecoder(BitArray information, String firstAIdigits, String dateCode) {
/*  46 */     super(information);
/*  47 */     this.dateCode = dateCode;
/*  48 */     this.firstAIdigits = firstAIdigits;
/*     */   }
/*     */ 
/*     */   
/*     */   public String parseInformation() throws NotFoundException {
/*  53 */     if (getInformation().getSize() != 84) {
/*  54 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/*  57 */     StringBuilder buf = new StringBuilder();
/*     */     
/*  59 */     encodeCompressedGtin(buf, 8);
/*  60 */     encodeCompressedWeight(buf, 48, 20);
/*  61 */     encodeCompressedDate(buf, 68);
/*     */     
/*  63 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private void encodeCompressedDate(StringBuilder buf, int currentPos) {
/*     */     int numericDate;
/*  68 */     if ((numericDate = getGeneralDecoder().extractNumericValueFromBitArray(currentPos, 16)) == 38400) {
/*     */       return;
/*     */     }
/*     */     
/*  72 */     buf.append('(');
/*  73 */     buf.append(this.dateCode);
/*  74 */     buf.append(')');
/*     */     
/*  76 */     int day = numericDate % 32;
/*     */     
/*  78 */     int month = (numericDate = numericDate / 32) % 12 + 1;
/*     */ 
/*     */ 
/*     */     
/*  82 */     if ((numericDate = numericDate / 12) / 10 == 0) {
/*  83 */       buf.append('0');
/*     */     }
/*  85 */     buf.append(numericDate);
/*  86 */     if (month / 10 == 0) {
/*  87 */       buf.append('0');
/*     */     }
/*  89 */     buf.append(month);
/*  90 */     if (day / 10 == 0) {
/*  91 */       buf.append('0');
/*     */     }
/*  93 */     buf.append(day);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void addWeightCode(StringBuilder buf, int weight) {
/*  98 */     buf.append('(');
/*  99 */     buf.append(this.firstAIdigits);
/* 100 */     buf.append(weight / 100000);
/* 101 */     buf.append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   protected int checkWeight(int weight) {
/* 106 */     return weight % 100000;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\AI013x0x1xDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */