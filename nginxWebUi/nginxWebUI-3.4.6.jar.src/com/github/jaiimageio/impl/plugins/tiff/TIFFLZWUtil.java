/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.imageio.IIOException;
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
/*     */ public class TIFFLZWUtil
/*     */ {
/*     */   private static final boolean debug = false;
/*     */   byte[] srcData;
/*     */   int srcIndex;
/*     */   byte[] dstData;
/*  62 */   int dstIndex = 0;
/*     */   byte[][] stringTable;
/*     */   int tableIndex;
/*  65 */   int bitsToGet = 9;
/*     */   int predictor;
/*     */   int samplesPerPixel;
/*  68 */   int nextData = 0;
/*  69 */   int nextBits = 0;
/*     */   
/*  71 */   private static final int[] andTable = new int[] { 511, 1023, 2047, 4095 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] decode(byte[] data, int predictor, int samplesPerPixel, int width, int height) throws IOException {
/*  80 */     if (data[0] == 0 && data[1] == 1) {
/*  81 */       throw new IIOException("TIFF 5.0-style LZW compression is not supported!");
/*     */     }
/*     */     
/*  84 */     this.srcData = data;
/*  85 */     this.srcIndex = 0;
/*  86 */     this.nextData = 0;
/*  87 */     this.nextBits = 0;
/*     */     
/*  89 */     this.dstData = new byte[8192];
/*  90 */     this.dstIndex = 0;
/*     */     
/*  92 */     initializeStringTable();
/*     */     
/*  94 */     int oldCode = 0;
/*     */     
/*     */     int code;
/*  97 */     while ((code = getNextCode()) != 257) {
/*  98 */       if (code == 256) {
/*  99 */         initializeStringTable();
/* 100 */         code = getNextCode();
/* 101 */         if (code == 257) {
/*     */           break;
/*     */         }
/*     */         
/* 105 */         writeString(this.stringTable[code]);
/* 106 */         oldCode = code; continue;
/*     */       } 
/* 108 */       if (code < this.tableIndex) {
/* 109 */         byte[] arrayOfByte = this.stringTable[code];
/*     */         
/* 111 */         writeString(arrayOfByte);
/* 112 */         addStringToTable(this.stringTable[oldCode], arrayOfByte[0]);
/* 113 */         oldCode = code; continue;
/*     */       } 
/* 115 */       byte[] string = this.stringTable[oldCode];
/* 116 */       string = composeString(string, string[0]);
/* 117 */       writeString(string);
/* 118 */       addStringToTable(string);
/* 119 */       oldCode = code;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 124 */     if (predictor == 2)
/*     */     {
/*     */       
/* 127 */       for (int j = 0; j < height; j++) {
/*     */         
/* 129 */         int count = samplesPerPixel * (j * width + 1);
/*     */         
/* 131 */         for (int i = samplesPerPixel; i < width * samplesPerPixel; i++) {
/*     */           
/* 133 */           this.dstData[count] = (byte)(this.dstData[count] + this.dstData[count - samplesPerPixel]);
/* 134 */           count++;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 139 */     byte[] newDstData = new byte[this.dstIndex];
/* 140 */     System.arraycopy(this.dstData, 0, newDstData, 0, this.dstIndex);
/* 141 */     return newDstData;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initializeStringTable() {
/* 148 */     this.stringTable = new byte[4096][];
/*     */     
/* 150 */     for (int i = 0; i < 256; i++) {
/* 151 */       this.stringTable[i] = new byte[1];
/* 152 */       this.stringTable[i][0] = (byte)i;
/*     */     } 
/*     */     
/* 155 */     this.tableIndex = 258;
/* 156 */     this.bitsToGet = 9;
/*     */   }
/*     */   
/*     */   private void ensureCapacity(int bytesToAdd) {
/* 160 */     if (this.dstIndex + bytesToAdd > this.dstData.length) {
/* 161 */       byte[] newDstData = new byte[Math.max((int)(this.dstData.length * 1.2F), this.dstIndex + bytesToAdd)];
/*     */       
/* 163 */       System.arraycopy(this.dstData, 0, newDstData, 0, this.dstData.length);
/* 164 */       this.dstData = newDstData;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeString(byte[] string) {
/* 172 */     ensureCapacity(string.length);
/* 173 */     for (int i = 0; i < string.length; i++) {
/* 174 */       this.dstData[this.dstIndex++] = string[i];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addStringToTable(byte[] oldString, byte newString) {
/* 182 */     int length = oldString.length;
/* 183 */     byte[] string = new byte[length + 1];
/* 184 */     System.arraycopy(oldString, 0, string, 0, length);
/* 185 */     string[length] = newString;
/*     */ 
/*     */     
/* 188 */     this.stringTable[this.tableIndex++] = string;
/*     */     
/* 190 */     if (this.tableIndex == 511) {
/* 191 */       this.bitsToGet = 10;
/* 192 */     } else if (this.tableIndex == 1023) {
/* 193 */       this.bitsToGet = 11;
/* 194 */     } else if (this.tableIndex == 2047) {
/* 195 */       this.bitsToGet = 12;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addStringToTable(byte[] string) {
/* 204 */     this.stringTable[this.tableIndex++] = string;
/*     */     
/* 206 */     if (this.tableIndex == 511) {
/* 207 */       this.bitsToGet = 10;
/* 208 */     } else if (this.tableIndex == 1023) {
/* 209 */       this.bitsToGet = 11;
/* 210 */     } else if (this.tableIndex == 2047) {
/* 211 */       this.bitsToGet = 12;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] composeString(byte[] oldString, byte newString) {
/* 219 */     int length = oldString.length;
/* 220 */     byte[] string = new byte[length + 1];
/* 221 */     System.arraycopy(oldString, 0, string, 0, length);
/* 222 */     string[length] = newString;
/*     */     
/* 224 */     return string;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNextCode() {
/*     */     try {
/* 235 */       this.nextData = this.nextData << 8 | this.srcData[this.srcIndex++] & 0xFF;
/* 236 */       this.nextBits += 8;
/*     */       
/* 238 */       if (this.nextBits < this.bitsToGet) {
/* 239 */         this.nextData = this.nextData << 8 | this.srcData[this.srcIndex++] & 0xFF;
/* 240 */         this.nextBits += 8;
/*     */       } 
/*     */       
/* 243 */       int code = this.nextData >> this.nextBits - this.bitsToGet & andTable[this.bitsToGet - 9];
/*     */       
/* 245 */       this.nextBits -= this.bitsToGet;
/*     */       
/* 247 */       return code;
/* 248 */     } catch (ArrayIndexOutOfBoundsException e) {
/*     */       
/* 250 */       return 257;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFLZWUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */