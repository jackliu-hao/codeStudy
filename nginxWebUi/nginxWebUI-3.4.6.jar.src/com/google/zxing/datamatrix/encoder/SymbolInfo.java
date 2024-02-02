/*     */ package com.google.zxing.datamatrix.encoder;
/*     */ 
/*     */ import com.google.zxing.Dimension;
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
/*     */ public class SymbolInfo
/*     */ {
/*  28 */   static final SymbolInfo[] PROD_SYMBOLS = new SymbolInfo[] { new SymbolInfo(false, 3, 5, 8, 8, 1), new SymbolInfo(false, 5, 7, 10, 10, 1), new SymbolInfo(true, 5, 7, 16, 6, 1), new SymbolInfo(false, 8, 10, 12, 12, 1), new SymbolInfo(true, 10, 11, 14, 6, 2), new SymbolInfo(false, 12, 12, 14, 14, 1), new SymbolInfo(true, 16, 14, 24, 10, 1), new SymbolInfo(false, 18, 14, 16, 16, 1), new SymbolInfo(false, 22, 18, 18, 18, 1), new SymbolInfo(true, 22, 18, 16, 10, 2), new SymbolInfo(false, 30, 20, 20, 20, 1), new SymbolInfo(true, 32, 24, 16, 14, 2), new SymbolInfo(false, 36, 24, 22, 22, 1), new SymbolInfo(false, 44, 28, 24, 24, 1), new SymbolInfo(true, 49, 28, 22, 14, 2), new SymbolInfo(false, 62, 36, 14, 14, 4), new SymbolInfo(false, 86, 42, 16, 16, 4), new SymbolInfo(false, 114, 48, 18, 18, 4), new SymbolInfo(false, 144, 56, 20, 20, 4), new SymbolInfo(false, 174, 68, 22, 22, 4), new SymbolInfo(false, 204, 84, 24, 24, 4, 102, 42), new SymbolInfo(false, 280, 112, 14, 14, 16, 140, 56), new SymbolInfo(false, 368, 144, 16, 16, 16, 92, 36), new SymbolInfo(false, 456, 192, 18, 18, 16, 114, 48), new SymbolInfo(false, 576, 224, 20, 20, 16, 144, 56), new SymbolInfo(false, 696, 272, 22, 22, 16, 174, 68), new SymbolInfo(false, 816, 336, 24, 24, 16, 136, 56), new SymbolInfo(false, 1050, 408, 18, 18, 36, 175, 68), new SymbolInfo(false, 1304, 496, 20, 20, 36, 163, 62), new DataMatrixSymbolInfo144() }; private static SymbolInfo[] symbols = new SymbolInfo[30];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean rectangular;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int dataCapacity;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int errorCodewords;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int matrixWidth;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int matrixHeight;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int dataRegions;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int rsBlockData;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int rsBlockError;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void overrideSymbolSet(SymbolInfo[] override) {
/*  72 */     symbols = override;
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
/*     */   public SymbolInfo(boolean rectangular, int dataCapacity, int errorCodewords, int matrixWidth, int matrixHeight, int dataRegions) {
/*  86 */     this(rectangular, dataCapacity, errorCodewords, matrixWidth, matrixHeight, dataRegions, dataCapacity, errorCodewords);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   SymbolInfo(boolean rectangular, int dataCapacity, int errorCodewords, int matrixWidth, int matrixHeight, int dataRegions, int rsBlockData, int rsBlockError) {
/*  93 */     this.rectangular = rectangular;
/*  94 */     this.dataCapacity = dataCapacity;
/*  95 */     this.errorCodewords = errorCodewords;
/*  96 */     this.matrixWidth = matrixWidth;
/*  97 */     this.matrixHeight = matrixHeight;
/*  98 */     this.dataRegions = dataRegions;
/*  99 */     this.rsBlockData = rsBlockData;
/* 100 */     this.rsBlockError = rsBlockError;
/*     */   }
/*     */   
/*     */   public static SymbolInfo lookup(int dataCodewords) {
/* 104 */     return lookup(dataCodewords, SymbolShapeHint.FORCE_NONE, true);
/*     */   }
/*     */   
/*     */   public static SymbolInfo lookup(int dataCodewords, SymbolShapeHint shape) {
/* 108 */     return lookup(dataCodewords, shape, true);
/*     */   }
/*     */   
/*     */   public static SymbolInfo lookup(int dataCodewords, boolean allowRectangular, boolean fail) {
/* 112 */     SymbolShapeHint shape = allowRectangular ? SymbolShapeHint.FORCE_NONE : SymbolShapeHint.FORCE_SQUARE;
/*     */     
/* 114 */     return lookup(dataCodewords, shape, fail);
/*     */   }
/*     */   
/*     */   private static SymbolInfo lookup(int dataCodewords, SymbolShapeHint shape, boolean fail) {
/* 118 */     return lookup(dataCodewords, shape, null, null, fail);
/*     */   }
/*     */ 
/*     */   
/*     */   public static SymbolInfo lookup(int dataCodewords, SymbolShapeHint shape, Dimension minSize, Dimension maxSize, boolean fail) {
/*     */     SymbolInfo[] arrayOfSymbolInfo;
/*     */     int i;
/*     */     byte b;
/* 126 */     for (i = (arrayOfSymbolInfo = symbols).length, b = 0; b < i; ) { SymbolInfo symbol = arrayOfSymbolInfo[b];
/* 127 */       if (shape != SymbolShapeHint.FORCE_SQUARE || !symbol.rectangular)
/*     */       {
/*     */         
/* 130 */         if (shape != SymbolShapeHint.FORCE_RECTANGLE || symbol.rectangular)
/*     */         {
/*     */           
/* 133 */           if (minSize == null || (symbol
/* 134 */             .getSymbolWidth() >= minSize.getWidth() && symbol
/* 135 */             .getSymbolHeight() >= minSize.getHeight()))
/*     */           {
/*     */             
/* 138 */             if (maxSize == null || (symbol
/* 139 */               .getSymbolWidth() <= maxSize.getWidth() && symbol
/* 140 */               .getSymbolHeight() <= maxSize.getHeight()))
/*     */             {
/*     */               
/* 143 */               if (dataCodewords <= symbol.dataCapacity)
/* 144 */                 return symbol;  }  }  }  } 
/*     */       b++; }
/*     */     
/* 147 */     if (fail) {
/* 148 */       throw new IllegalArgumentException("Can't find a symbol arrangement that matches the message. Data codewords: " + dataCodewords);
/*     */     }
/*     */ 
/*     */     
/* 152 */     return null;
/*     */   }
/*     */   
/*     */   private int getHorizontalDataRegions() {
/* 156 */     switch (this.dataRegions) {
/*     */       case 1:
/* 158 */         return 1;
/*     */       case 2:
/*     */       case 4:
/* 161 */         return 2;
/*     */       case 16:
/* 163 */         return 4;
/*     */       case 36:
/* 165 */         return 6;
/*     */     } 
/* 167 */     throw new IllegalStateException("Cannot handle this number of data regions");
/*     */   }
/*     */ 
/*     */   
/*     */   private int getVerticalDataRegions() {
/* 172 */     switch (this.dataRegions) {
/*     */       case 1:
/*     */       case 2:
/* 175 */         return 1;
/*     */       case 4:
/* 177 */         return 2;
/*     */       case 16:
/* 179 */         return 4;
/*     */       case 36:
/* 181 */         return 6;
/*     */     } 
/* 183 */     throw new IllegalStateException("Cannot handle this number of data regions");
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getSymbolDataWidth() {
/* 188 */     return getHorizontalDataRegions() * this.matrixWidth;
/*     */   }
/*     */   
/*     */   public final int getSymbolDataHeight() {
/* 192 */     return getVerticalDataRegions() * this.matrixHeight;
/*     */   }
/*     */   
/*     */   public final int getSymbolWidth() {
/* 196 */     return getSymbolDataWidth() + (getHorizontalDataRegions() << 1);
/*     */   }
/*     */   
/*     */   public final int getSymbolHeight() {
/* 200 */     return getSymbolDataHeight() + (getVerticalDataRegions() << 1);
/*     */   }
/*     */   
/*     */   public int getCodewordCount() {
/* 204 */     return this.dataCapacity + this.errorCodewords;
/*     */   }
/*     */   
/*     */   public int getInterleavedBlockCount() {
/* 208 */     return this.dataCapacity / this.rsBlockData;
/*     */   }
/*     */   
/*     */   public final int getDataCapacity() {
/* 212 */     return this.dataCapacity;
/*     */   }
/*     */   
/*     */   public final int getErrorCodewords() {
/* 216 */     return this.errorCodewords;
/*     */   }
/*     */   
/*     */   public int getDataLengthForInterleavedBlock(int index) {
/* 220 */     return this.rsBlockData;
/*     */   }
/*     */   
/*     */   public final int getErrorLengthForInterleavedBlock(int index) {
/* 224 */     return this.rsBlockError;
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 229 */     return (this.rectangular ? "Rectangular Symbol:" : "Square Symbol:") + " data region " + this.matrixWidth + 'x' + this.matrixHeight + ", symbol size " + 
/*     */       
/* 231 */       getSymbolWidth() + 'x' + getSymbolHeight() + ", symbol data size " + 
/* 232 */       getSymbolDataWidth() + 'x' + getSymbolDataHeight() + ", codewords " + this.dataCapacity + '+' + this.errorCodewords;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\SymbolInfo.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */