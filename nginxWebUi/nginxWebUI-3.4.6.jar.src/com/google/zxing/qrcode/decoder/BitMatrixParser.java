/*     */ package com.google.zxing.qrcode.decoder;
/*     */ 
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.common.BitMatrix;
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
/*     */ final class BitMatrixParser
/*     */ {
/*     */   private final BitMatrix bitMatrix;
/*     */   private Version parsedVersion;
/*     */   private FormatInformation parsedFormatInfo;
/*     */   private boolean mirror;
/*     */   
/*     */   BitMatrixParser(BitMatrix bitMatrix) throws FormatException {
/*     */     int dimension;
/*  38 */     if ((dimension = bitMatrix.getHeight()) < 21 || (dimension & 0x3) != 1) {
/*  39 */       throw FormatException.getFormatInstance();
/*     */     }
/*  41 */     this.bitMatrix = bitMatrix;
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
/*     */   FormatInformation readFormatInformation() throws FormatException {
/*  53 */     if (this.parsedFormatInfo != null) {
/*  54 */       return this.parsedFormatInfo;
/*     */     }
/*     */ 
/*     */     
/*  58 */     int formatInfoBits1 = 0;
/*  59 */     for (int i = 0; i < 6; i++) {
/*  60 */       formatInfoBits1 = copyBit(i, 8, formatInfoBits1);
/*     */     }
/*     */     
/*  63 */     formatInfoBits1 = copyBit(7, 8, formatInfoBits1);
/*  64 */     formatInfoBits1 = copyBit(8, 8, formatInfoBits1);
/*  65 */     formatInfoBits1 = copyBit(8, 7, formatInfoBits1);
/*     */     
/*  67 */     for (int j = 5; j >= 0; j--) {
/*  68 */       formatInfoBits1 = copyBit(8, j, formatInfoBits1);
/*     */     }
/*     */ 
/*     */     
/*  72 */     int dimension = this.bitMatrix.getHeight();
/*  73 */     int formatInfoBits2 = 0;
/*  74 */     int jMin = dimension - 7;
/*  75 */     for (int m = dimension - 1; m >= jMin; m--) {
/*  76 */       formatInfoBits2 = copyBit(8, m, formatInfoBits2);
/*     */     }
/*  78 */     for (int k = dimension - 8; k < dimension; k++) {
/*  79 */       formatInfoBits2 = copyBit(k, 8, formatInfoBits2);
/*     */     }
/*     */     
/*  82 */     this.parsedFormatInfo = FormatInformation.decodeFormatInformation(formatInfoBits1, formatInfoBits2);
/*  83 */     if (this.parsedFormatInfo != null) {
/*  84 */       return this.parsedFormatInfo;
/*     */     }
/*  86 */     throw FormatException.getFormatInstance();
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
/*     */   Version readVersion() throws FormatException {
/*  98 */     if (this.parsedVersion != null) {
/*  99 */       return this.parsedVersion;
/*     */     }
/*     */ 
/*     */     
/*     */     int dimension, provisionalVersion;
/*     */     
/* 105 */     if ((provisionalVersion = ((dimension = this.bitMatrix.getHeight()) - 17) / 4) <= 6) {
/* 106 */       return Version.getVersionForNumber(provisionalVersion);
/*     */     }
/*     */ 
/*     */     
/* 110 */     int versionBits = 0;
/* 111 */     int ijMin = dimension - 11;
/* 112 */     for (int j = 5; j >= 0; j--) {
/* 113 */       for (int k = dimension - 9; k >= ijMin; k--) {
/* 114 */         versionBits = copyBit(k, j, versionBits);
/*     */       }
/*     */     } 
/*     */     
/*     */     Version theParsedVersion;
/* 119 */     if ((theParsedVersion = Version.decodeVersionInformation(versionBits)) != null && theParsedVersion.getDimensionForVersion() == dimension) {
/* 120 */       this.parsedVersion = theParsedVersion;
/* 121 */       return theParsedVersion;
/*     */     } 
/*     */ 
/*     */     
/* 125 */     versionBits = 0;
/* 126 */     for (int i = 5; i >= 0; i--) {
/* 127 */       for (int k = dimension - 9; k >= ijMin; k--) {
/* 128 */         versionBits = copyBit(i, k, versionBits);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 133 */     if ((theParsedVersion = Version.decodeVersionInformation(versionBits)) != null && theParsedVersion.getDimensionForVersion() == dimension) {
/* 134 */       this.parsedVersion = theParsedVersion;
/* 135 */       return theParsedVersion;
/*     */     } 
/* 137 */     throw FormatException.getFormatInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   private int copyBit(int i, int j, int versionBits) {
/* 142 */     return (this.mirror ? this.bitMatrix.get(j, i) : this.bitMatrix.get(i, j)) ? (versionBits << 1 | 0x1) : (versionBits << 1);
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
/*     */   byte[] readCodewords() throws FormatException {
/* 155 */     FormatInformation formatInfo = readFormatInformation();
/* 156 */     Version version = readVersion();
/*     */ 
/*     */ 
/*     */     
/* 160 */     DataMask dataMask = DataMask.values()[formatInfo.getDataMask()];
/* 161 */     int dimension = this.bitMatrix.getHeight();
/* 162 */     dataMask.unmaskBitMatrix(this.bitMatrix, dimension);
/*     */     
/* 164 */     BitMatrix functionPattern = version.buildFunctionPattern();
/*     */     
/* 166 */     boolean readingUp = true;
/* 167 */     byte[] result = new byte[version.getTotalCodewords()];
/* 168 */     int resultOffset = 0;
/* 169 */     int currentByte = 0;
/* 170 */     int bitsRead = 0;
/*     */     
/* 172 */     for (int j = dimension - 1; j > 0; j -= 2) {
/* 173 */       if (j == 6)
/*     */       {
/*     */         
/* 176 */         j--;
/*     */       }
/*     */       
/* 179 */       for (int count = 0; count < dimension; count++) {
/* 180 */         int k = readingUp ? (dimension - 1 - count) : count;
/* 181 */         for (int col = 0; col < 2; col++) {
/*     */           
/* 183 */           if (!functionPattern.get(j - col, k)) {
/*     */             
/* 185 */             bitsRead++;
/* 186 */             currentByte <<= 1;
/* 187 */             if (this.bitMatrix.get(j - col, k)) {
/* 188 */               currentByte |= 0x1;
/*     */             }
/*     */             
/* 191 */             if (bitsRead == 8) {
/* 192 */               result[resultOffset++] = (byte)currentByte;
/* 193 */               bitsRead = 0;
/* 194 */               currentByte = 0;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 199 */       int i = readingUp ^ true;
/*     */     } 
/* 201 */     if (resultOffset != version.getTotalCodewords()) {
/* 202 */       throw FormatException.getFormatInstance();
/*     */     }
/* 204 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void remask() {
/* 211 */     if (this.parsedFormatInfo == null) {
/*     */       return;
/*     */     }
/* 214 */     DataMask dataMask = DataMask.values()[this.parsedFormatInfo.getDataMask()];
/* 215 */     int dimension = this.bitMatrix.getHeight();
/* 216 */     dataMask.unmaskBitMatrix(this.bitMatrix, dimension);
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
/*     */   void setMirror(boolean mirror) {
/* 228 */     this.parsedVersion = null;
/* 229 */     this.parsedFormatInfo = null;
/* 230 */     this.mirror = mirror;
/*     */   }
/*     */ 
/*     */   
/*     */   void mirror() {
/* 235 */     for (int x = 0; x < this.bitMatrix.getWidth(); x++) {
/* 236 */       for (int y = x + 1; y < this.bitMatrix.getHeight(); y++) {
/* 237 */         if (this.bitMatrix.get(x, y) != this.bitMatrix.get(y, x)) {
/* 238 */           this.bitMatrix.flip(y, x);
/* 239 */           this.bitMatrix.flip(x, y);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\BitMatrixParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */