/*     */ package com.google.zxing.qrcode.encoder;
/*     */ 
/*     */ import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/*     */ import com.google.zxing.qrcode.decoder.Mode;
/*     */ import com.google.zxing.qrcode.decoder.Version;
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
/*     */ public final class QRCode
/*     */ {
/*     */   public static final int NUM_MASK_PATTERNS = 8;
/*     */   private Mode mode;
/*     */   private ErrorCorrectionLevel ecLevel;
/*     */   private Version version;
/*  38 */   private int maskPattern = -1;
/*     */   private ByteMatrix matrix;
/*     */   
/*     */   public Mode getMode() {
/*  42 */     return this.mode;
/*     */   }
/*     */   
/*     */   public ErrorCorrectionLevel getECLevel() {
/*  46 */     return this.ecLevel;
/*     */   }
/*     */   
/*     */   public Version getVersion() {
/*  50 */     return this.version;
/*     */   }
/*     */   
/*     */   public int getMaskPattern() {
/*  54 */     return this.maskPattern;
/*     */   }
/*     */   
/*     */   public ByteMatrix getMatrix() {
/*  58 */     return this.matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     StringBuilder result;
/*  64 */     (result = new StringBuilder(200)).append("<<\n");
/*  65 */     result.append(" mode: ");
/*  66 */     result.append(this.mode);
/*  67 */     result.append("\n ecLevel: ");
/*  68 */     result.append(this.ecLevel);
/*  69 */     result.append("\n version: ");
/*  70 */     result.append(this.version);
/*  71 */     result.append("\n maskPattern: ");
/*  72 */     result.append(this.maskPattern);
/*  73 */     if (this.matrix == null) {
/*  74 */       result.append("\n matrix: null\n");
/*     */     } else {
/*  76 */       result.append("\n matrix:\n");
/*  77 */       result.append(this.matrix);
/*     */     } 
/*  79 */     result.append(">>\n");
/*  80 */     return result.toString();
/*     */   }
/*     */   
/*     */   public void setMode(Mode value) {
/*  84 */     this.mode = value;
/*     */   }
/*     */   
/*     */   public void setECLevel(ErrorCorrectionLevel value) {
/*  88 */     this.ecLevel = value;
/*     */   }
/*     */   
/*     */   public void setVersion(Version version) {
/*  92 */     this.version = version;
/*     */   }
/*     */   
/*     */   public void setMaskPattern(int value) {
/*  96 */     this.maskPattern = value;
/*     */   }
/*     */   
/*     */   public void setMatrix(ByteMatrix value) {
/* 100 */     this.matrix = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isValidMaskPattern(int maskPattern) {
/* 105 */     return (maskPattern >= 0 && maskPattern < 8);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\encoder\QRCode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */