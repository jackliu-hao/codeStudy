/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
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
/*     */ public class TIFFLZWDecompressor
/*     */   extends TIFFDecompressor
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*  58 */   private static final int[] andTable = new int[] { 511, 1023, 2047, 4095 };
/*     */   
/*     */   int predictor;
/*     */   
/*     */   byte[] srcData;
/*     */   
/*     */   byte[] dstData;
/*     */   
/*     */   int srcIndex;
/*     */   
/*     */   int dstIndex;
/*     */   
/*     */   byte[][] stringTable;
/*     */   
/*     */   int tableIndex;
/*     */   
/*  74 */   int bitsToGet = 9;
/*     */   
/*  76 */   int nextData = 0;
/*  77 */   int nextBits = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFLZWDecompressor(int predictor) throws IIOException {
/*  82 */     if (predictor != 1 && predictor != 2)
/*     */     {
/*     */       
/*  85 */       throw new IIOException("Illegal value for Predictor in TIFF file");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     this.predictor = predictor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/*     */     byte[] buf;
/*     */     int bufOffset;
/* 102 */     if (this.predictor == 2) {
/*     */       
/* 104 */       int len = this.bitsPerSample.length;
/* 105 */       for (int i = 0; i < len; i++) {
/* 106 */         if (this.bitsPerSample[i] != 8) {
/* 107 */           throw new IIOException(this.bitsPerSample[i] + "-bit samples " + "are not supported for Horizontal " + "differencing Predictor");
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     this.stream.seek(this.offset);
/*     */     
/* 117 */     byte[] sdata = new byte[this.byteCount];
/* 118 */     this.stream.readFully(sdata);
/*     */     
/* 120 */     int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
/*     */ 
/*     */     
/* 123 */     if (bytesPerRow == scanlineStride) {
/* 124 */       buf = b;
/* 125 */       bufOffset = dstOffset;
/*     */     } else {
/* 127 */       buf = new byte[bytesPerRow * this.srcHeight];
/* 128 */       bufOffset = 0;
/*     */     } 
/*     */     
/* 131 */     int numBytesDecoded = decode(sdata, 0, buf, bufOffset);
/*     */     
/* 133 */     if (bytesPerRow != scanlineStride) {
/*     */ 
/*     */ 
/*     */       
/* 137 */       int off = 0;
/* 138 */       for (int y = 0; y < this.srcHeight; y++) {
/* 139 */         System.arraycopy(buf, off, b, dstOffset, bytesPerRow);
/* 140 */         off += bytesPerRow;
/* 141 */         dstOffset += scanlineStride;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int decode(byte[] sdata, int srcOffset, byte[] ddata, int dstOffset) throws IOException {
/* 149 */     if (sdata[0] == 0 && sdata[1] == 1) {
/* 150 */       throw new IIOException("TIFF 5.0-style LZW compression is not supported!");
/*     */     }
/*     */ 
/*     */     
/* 154 */     this.srcData = sdata;
/* 155 */     this.dstData = ddata;
/*     */     
/* 157 */     this.srcIndex = srcOffset;
/* 158 */     this.dstIndex = dstOffset;
/*     */     
/* 160 */     this.nextData = 0;
/* 161 */     this.nextBits = 0;
/*     */     
/* 163 */     initializeStringTable();
/*     */     
/* 165 */     int oldCode = 0;
/*     */     
/*     */     int code;
/* 168 */     while ((code = getNextCode()) != 257) {
/* 169 */       if (code == 256) {
/* 170 */         initializeStringTable();
/* 171 */         code = getNextCode();
/* 172 */         if (code == 257) {
/*     */           break;
/*     */         }
/*     */         
/* 176 */         writeString(this.stringTable[code]);
/* 177 */         oldCode = code; continue;
/*     */       } 
/* 179 */       if (code < this.tableIndex) {
/* 180 */         byte[] arrayOfByte = this.stringTable[code];
/*     */         
/* 182 */         writeString(arrayOfByte);
/* 183 */         addStringToTable(this.stringTable[oldCode], arrayOfByte[0]);
/* 184 */         oldCode = code; continue;
/*     */       } 
/* 186 */       byte[] string = this.stringTable[oldCode];
/* 187 */       string = composeString(string, string[0]);
/* 188 */       writeString(string);
/* 189 */       addStringToTable(string);
/* 190 */       oldCode = code;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 195 */     if (this.predictor == 2)
/*     */     {
/*     */       
/* 198 */       for (int j = 0; j < this.srcHeight; j++) {
/*     */         
/* 200 */         int count = dstOffset + this.samplesPerPixel * (j * this.srcWidth + 1);
/*     */         
/* 202 */         for (int i = this.samplesPerPixel; i < this.srcWidth * this.samplesPerPixel; i++) {
/*     */           
/* 204 */           this.dstData[count] = (byte)(this.dstData[count] + this.dstData[count - this.samplesPerPixel]);
/* 205 */           count++;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 210 */     return this.dstIndex - dstOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initializeStringTable() {
/* 217 */     this.stringTable = new byte[4096][];
/*     */     
/* 219 */     for (int i = 0; i < 256; i++) {
/* 220 */       this.stringTable[i] = new byte[1];
/* 221 */       this.stringTable[i][0] = (byte)i;
/*     */     } 
/*     */     
/* 224 */     this.tableIndex = 258;
/* 225 */     this.bitsToGet = 9;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeString(byte[] string) {
/* 232 */     if (this.dstIndex < this.dstData.length) {
/* 233 */       int maxIndex = Math.min(string.length, this.dstData.length - this.dstIndex);
/*     */ 
/*     */       
/* 236 */       for (int i = 0; i < maxIndex; i++) {
/* 237 */         this.dstData[this.dstIndex++] = string[i];
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addStringToTable(byte[] oldString, byte newString) {
/* 246 */     int length = oldString.length;
/* 247 */     byte[] string = new byte[length + 1];
/* 248 */     System.arraycopy(oldString, 0, string, 0, length);
/* 249 */     string[length] = newString;
/*     */ 
/*     */     
/* 252 */     this.stringTable[this.tableIndex++] = string;
/*     */     
/* 254 */     if (this.tableIndex == 511) {
/* 255 */       this.bitsToGet = 10;
/* 256 */     } else if (this.tableIndex == 1023) {
/* 257 */       this.bitsToGet = 11;
/* 258 */     } else if (this.tableIndex == 2047) {
/* 259 */       this.bitsToGet = 12;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addStringToTable(byte[] string) {
/* 268 */     this.stringTable[this.tableIndex++] = string;
/*     */     
/* 270 */     if (this.tableIndex == 511) {
/* 271 */       this.bitsToGet = 10;
/* 272 */     } else if (this.tableIndex == 1023) {
/* 273 */       this.bitsToGet = 11;
/* 274 */     } else if (this.tableIndex == 2047) {
/* 275 */       this.bitsToGet = 12;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] composeString(byte[] oldString, byte newString) {
/* 283 */     int length = oldString.length;
/* 284 */     byte[] string = new byte[length + 1];
/* 285 */     System.arraycopy(oldString, 0, string, 0, length);
/* 286 */     string[length] = newString;
/*     */     
/* 288 */     return string;
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
/* 299 */       this.nextData = this.nextData << 8 | this.srcData[this.srcIndex++] & 0xFF;
/* 300 */       this.nextBits += 8;
/*     */       
/* 302 */       if (this.nextBits < this.bitsToGet) {
/* 303 */         this.nextData = this.nextData << 8 | this.srcData[this.srcIndex++] & 0xFF;
/* 304 */         this.nextBits += 8;
/*     */       } 
/*     */       
/* 307 */       int code = this.nextData >> this.nextBits - this.bitsToGet & andTable[this.bitsToGet - 9];
/*     */       
/* 309 */       this.nextBits -= this.bitsToGet;
/*     */       
/* 311 */       return code;
/* 312 */     } catch (ArrayIndexOutOfBoundsException e) {
/*     */       
/* 314 */       return 257;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFLZWDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */