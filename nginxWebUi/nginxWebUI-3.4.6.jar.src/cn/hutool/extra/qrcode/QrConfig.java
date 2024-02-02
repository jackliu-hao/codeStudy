/*     */ package cn.hutool.extra.qrcode;
/*     */ 
/*     */ import cn.hutool.core.img.ImgUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/*     */ import java.awt.Color;
/*     */ import java.awt.Image;
/*     */ import java.io.File;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.HashMap;
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
/*     */ public class QrConfig
/*     */ {
/*     */   private static final int BLACK = -16777216;
/*     */   private static final int WHITE = -1;
/*     */   protected int width;
/*     */   protected int height;
/*  32 */   protected int foreColor = -16777216;
/*     */   
/*  34 */   protected Integer backColor = Integer.valueOf(-1);
/*     */   
/*  36 */   protected Integer margin = Integer.valueOf(2);
/*     */   
/*     */   protected Integer qrVersion;
/*     */   
/*  40 */   protected ErrorCorrectionLevel errorCorrection = ErrorCorrectionLevel.M;
/*     */   
/*  42 */   protected Charset charset = CharsetUtil.CHARSET_UTF_8;
/*     */   
/*     */   protected Image img;
/*     */   
/*  46 */   protected int ratio = 6;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static QrConfig create() {
/*  54 */     return new QrConfig();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig() {
/*  61 */     this(300, 300);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig(int width, int height) {
/*  71 */     this.width = width;
/*  72 */     this.height = height;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWidth() {
/*  81 */     return this.width;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setWidth(int width) {
/*  91 */     this.width = width;
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getHeight() {
/* 101 */     return this.height;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setHeight(int height) {
/* 111 */     this.height = height;
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getForeColor() {
/* 121 */     return this.foreColor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public QrConfig setForeColor(int foreColor) {
/* 133 */     this.foreColor = foreColor;
/* 134 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setForeColor(Color foreColor) {
/* 145 */     if (null != foreColor) {
/* 146 */       this.foreColor = foreColor.getRGB();
/*     */     }
/* 148 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBackColor() {
/* 157 */     return this.backColor.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public QrConfig setBackColor(int backColor) {
/* 169 */     this.backColor = Integer.valueOf(backColor);
/* 170 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setBackColor(Color backColor) {
/* 181 */     if (null == backColor) {
/* 182 */       this.backColor = null;
/*     */     } else {
/* 184 */       this.backColor = Integer.valueOf(backColor.getRGB());
/*     */     } 
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getMargin() {
/* 195 */     return this.margin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setMargin(Integer margin) {
/* 205 */     this.margin = margin;
/* 206 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getQrVersion() {
/* 215 */     return this.qrVersion;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setQrVersion(Integer qrVersion) {
/* 225 */     this.qrVersion = qrVersion;
/* 226 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ErrorCorrectionLevel getErrorCorrection() {
/* 235 */     return this.errorCorrection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setErrorCorrection(ErrorCorrectionLevel errorCorrection) {
/* 245 */     this.errorCorrection = errorCorrection;
/* 246 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 255 */     return this.charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setCharset(Charset charset) {
/* 265 */     this.charset = charset;
/* 266 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Image getImg() {
/* 275 */     return this.img;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setImg(String imgPath) {
/* 285 */     return setImg(FileUtil.file(imgPath));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setImg(File imgFile) {
/* 295 */     return setImg(ImgUtil.read(imgFile));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setImg(Image img) {
/* 305 */     this.img = img;
/* 306 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRatio() {
/* 315 */     return this.ratio;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QrConfig setRatio(int ratio) {
/* 325 */     this.ratio = ratio;
/* 326 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<EncodeHintType, Object> toHints() {
/* 335 */     return toHints(BarcodeFormat.QR_CODE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<EncodeHintType, Object> toHints(BarcodeFormat format) {
/* 346 */     HashMap<EncodeHintType, Object> hints = new HashMap<>();
/* 347 */     if (null != this.charset) {
/* 348 */       hints.put(EncodeHintType.CHARACTER_SET, this.charset.toString().toLowerCase());
/*     */     }
/* 350 */     if (null != this.errorCorrection) {
/*     */       Object value;
/* 352 */       if (BarcodeFormat.AZTEC == format || BarcodeFormat.PDF_417 == format) {
/*     */         
/* 354 */         value = Integer.valueOf(this.errorCorrection.getBits());
/*     */       } else {
/* 356 */         value = this.errorCorrection;
/*     */       } 
/*     */       
/* 359 */       hints.put(EncodeHintType.ERROR_CORRECTION, value);
/*     */     } 
/* 361 */     if (null != this.margin) {
/* 362 */       hints.put(EncodeHintType.MARGIN, this.margin);
/*     */     }
/* 364 */     if (null != this.qrVersion) {
/* 365 */       hints.put(EncodeHintType.QR_VERSION, this.qrVersion);
/*     */     }
/* 367 */     return hints;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\qrcode\QrConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */