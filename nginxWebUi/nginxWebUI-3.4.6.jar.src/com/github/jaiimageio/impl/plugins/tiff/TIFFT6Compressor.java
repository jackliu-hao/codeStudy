/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
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
/*     */ public class TIFFT6Compressor
/*     */   extends TIFFFaxCompressor
/*     */ {
/*     */   public TIFFT6Compressor() {
/*  61 */     super("CCITT T.6", 4, true);
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
/*     */   public synchronized int encodeT6(byte[] data, int lineStride, int colOffset, int width, int height, byte[] compData) {
/*  89 */     byte[] refData = null;
/*  90 */     int refAddr = 0;
/*  91 */     int lineAddr = 0;
/*  92 */     int outIndex = 0;
/*     */     
/*  94 */     initBitBuf();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     while (height-- != 0) {
/* 100 */       int a0 = colOffset;
/* 101 */       int last = a0 + width;
/*     */       
/* 103 */       int testbit = (data[lineAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
/*     */ 
/*     */ 
/*     */       
/* 107 */       int a1 = (testbit != 0) ? a0 : nextState(data, lineAddr, a0, last);
/*     */       
/* 109 */       testbit = (refData == null) ? 0 : ((refData[refAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1);
/*     */ 
/*     */ 
/*     */       
/* 113 */       int b1 = (testbit != 0) ? a0 : nextState(refData, refAddr, a0, last);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 118 */       int color = 0;
/*     */       
/*     */       while (true) {
/* 121 */         int b2 = nextState(refData, refAddr, b1, last);
/* 122 */         if (b2 < a1) {
/* 123 */           outIndex += add2DBits(compData, outIndex, pass, 0);
/* 124 */           a0 = b2;
/*     */         } else {
/* 126 */           int tmp = b1 - a1 + 3;
/* 127 */           if (tmp <= 6 && tmp >= 0) {
/* 128 */             outIndex += add2DBits(compData, outIndex, vert, tmp);
/* 129 */             a0 = a1;
/*     */           } else {
/* 131 */             int a2 = nextState(data, lineAddr, a1, last);
/* 132 */             outIndex += add2DBits(compData, outIndex, horz, 0);
/* 133 */             outIndex += add1DBits(compData, outIndex, a1 - a0, color);
/* 134 */             outIndex += add1DBits(compData, outIndex, a2 - a1, color ^ 0x1);
/* 135 */             a0 = a2;
/*     */           } 
/*     */         } 
/* 138 */         if (a0 >= last) {
/*     */           break;
/*     */         }
/* 141 */         color = (data[lineAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
/*     */         
/* 143 */         a1 = nextState(data, lineAddr, a0, last);
/* 144 */         b1 = nextState(refData, refAddr, a0, last);
/* 145 */         testbit = (refData == null) ? 0 : ((refData[refAddr + (b1 >>> 3)] & 0xFF) >>> 7 - (b1 & 0x7) & 0x1);
/*     */ 
/*     */         
/* 148 */         if (testbit == color) {
/* 149 */           b1 = nextState(refData, refAddr, b1, last);
/*     */         }
/*     */       } 
/*     */       
/* 153 */       refData = data;
/* 154 */       refAddr = lineAddr;
/* 155 */       lineAddr += lineStride;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     outIndex += addEOFB(compData, outIndex);
/*     */ 
/*     */     
/* 165 */     if (this.inverseFill) {
/* 166 */       for (int i = 0; i < outIndex; i++) {
/* 167 */         compData[i] = TIFFFaxDecompressor.flipTable[compData[i] & 0xFF];
/*     */       }
/*     */     }
/*     */     
/* 171 */     return outIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
/* 178 */     if (bitsPerSample.length != 1 || bitsPerSample[0] != 1) {
/* 179 */       throw new IIOException("Bits per sample must be 1 for T6 compression!");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 184 */     if (this.metadata instanceof TIFFImageMetadata) {
/* 185 */       TIFFImageMetadata tim = (TIFFImageMetadata)this.metadata;
/*     */       
/* 187 */       long[] options = new long[1];
/* 188 */       options[0] = 0L;
/*     */       
/* 190 */       BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
/*     */       
/* 192 */       TIFFField T6Options = new TIFFField(base.getTag(293), 4, 1, options);
/*     */ 
/*     */ 
/*     */       
/* 196 */       tim.rootIFD.addTIFFField(T6Options);
/*     */     } 
/*     */ 
/*     */     
/* 200 */     int maxBits = 9 * (width + 1) / 2 + 2;
/* 201 */     int bufSize = (maxBits + 7) / 8;
/* 202 */     bufSize = height * (bufSize + 2) + 12;
/*     */     
/* 204 */     byte[] compData = new byte[bufSize];
/* 205 */     int bytes = encodeT6(b, scanlineStride, 8 * off, width, height, compData);
/*     */     
/* 207 */     this.stream.write(compData, 0, bytes);
/* 208 */     return bytes;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFT6Compressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */