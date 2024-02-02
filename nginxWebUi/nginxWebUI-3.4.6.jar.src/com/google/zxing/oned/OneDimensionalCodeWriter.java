/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.Writer;
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.BitMatrix;
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
/*     */ public abstract class OneDimensionalCodeWriter
/*     */   implements Writer
/*     */ {
/*     */   public final BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
/*  37 */     return encode(contents, format, width, height, null);
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
/*     */ 
/*     */   
/*     */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/*  53 */     if (contents.isEmpty()) {
/*  54 */       throw new IllegalArgumentException("Found empty contents");
/*     */     }
/*     */     
/*  57 */     if (width < 0 || height < 0) {
/*  58 */       throw new IllegalArgumentException("Negative size is not allowed. Input: " + width + 'x' + height);
/*     */     }
/*     */ 
/*     */     
/*  62 */     int sidesMargin = getDefaultMargin();
/*  63 */     if (hints != null && hints.containsKey(EncodeHintType.MARGIN)) {
/*  64 */       sidesMargin = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
/*     */     }
/*     */ 
/*     */     
/*  68 */     return renderResult(encode(contents), width, height, sidesMargin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static BitMatrix renderResult(boolean[] code, int width, int height, int sidesMargin) {
/*  77 */     int inputWidth, fullWidth = (inputWidth = code.length) + sidesMargin;
/*  78 */     int outputWidth = Math.max(width, fullWidth);
/*  79 */     int outputHeight = Math.max(1, height);
/*     */     
/*  81 */     int multiple = outputWidth / fullWidth;
/*  82 */     int leftPadding = (outputWidth - inputWidth * multiple) / 2;
/*     */     
/*  84 */     BitMatrix output = new BitMatrix(outputWidth, outputHeight); int outputX;
/*  85 */     for (int inputX = 0; inputX < inputWidth; inputX++, outputX += multiple) {
/*  86 */       if (code[inputX]) {
/*  87 */         output.setRegion(outputX, 0, multiple, outputHeight);
/*     */       }
/*     */     } 
/*  90 */     return output;
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
/*     */   protected static int appendPattern(boolean[] target, int pos, int[] pattern, boolean startColor) {
/* 102 */     boolean color = startColor;
/* 103 */     int numAdded = 0; int arrayOfInt[], i; byte b;
/* 104 */     for (i = (arrayOfInt = pattern).length, b = 0; b < i; ) { int len = arrayOfInt[b];
/* 105 */       for (int j = 0; j < len; j++) {
/* 106 */         target[pos++] = color;
/*     */       }
/* 108 */       numAdded += len;
/* 109 */       color = !color; b++; }
/*     */     
/* 111 */     return numAdded;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDefaultMargin() {
/* 117 */     return 10;
/*     */   }
/*     */   
/*     */   public abstract boolean[] encode(String paramString);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\OneDimensionalCodeWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */