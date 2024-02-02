/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*     */ import java.io.IOException;
/*     */ import javax.imageio.IIOException;
/*     */ import javax.imageio.metadata.IIOMetadata;
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
/*     */ public class TIFFT4Compressor
/*     */   extends TIFFFaxCompressor
/*     */ {
/*     */   private boolean is1DMode = false;
/*     */   private boolean isEOLAligned = false;
/*     */   
/*     */   public TIFFT4Compressor() {
/*  65 */     super("CCITT T.4", 3, true);
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
/*     */   public void setMetadata(IIOMetadata metadata) {
/*  81 */     super.setMetadata(metadata);
/*     */     
/*  83 */     if (metadata instanceof TIFFImageMetadata) {
/*  84 */       TIFFImageMetadata tim = (TIFFImageMetadata)metadata;
/*  85 */       TIFFField f = tim.getTIFFField(292);
/*  86 */       if (f != null) {
/*  87 */         int options = f.getAsInt(0);
/*  88 */         this.is1DMode = ((options & 0x1) == 0);
/*  89 */         this.isEOLAligned = ((options & 0x4) == 4);
/*     */       } else {
/*  91 */         long[] oarray = new long[1];
/*  92 */         oarray[0] = ((this.isEOLAligned ? true : false) | (this.is1DMode ? false : true));
/*     */ 
/*     */         
/*  95 */         BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
/*     */         
/*  97 */         TIFFField T4Options = new TIFFField(base.getTag(292), 4, 1, oarray);
/*     */ 
/*     */ 
/*     */         
/* 101 */         tim.rootIFD.addTIFFField(T4Options);
/*     */       } 
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encodeT4(boolean is1DMode, boolean isEOLAligned, byte[] data, int lineStride, int colOffset, int width, int height, byte[] compData) {
/* 135 */     byte[] refData = data;
/* 136 */     int lineAddr = 0;
/* 137 */     int outIndex = 0;
/*     */     
/* 139 */     initBitBuf();
/*     */     
/* 141 */     int KParameter = 2;
/* 142 */     for (int numRows = 0; numRows < height; numRows++) {
/* 143 */       if (is1DMode || numRows % KParameter == 0) {
/*     */         
/* 145 */         outIndex += addEOL(is1DMode, isEOLAligned, true, compData, outIndex);
/*     */ 
/*     */ 
/*     */         
/* 149 */         outIndex += encode1D(data, lineAddr, colOffset, width, compData, outIndex);
/*     */       }
/*     */       else {
/*     */         
/* 153 */         outIndex += addEOL(is1DMode, isEOLAligned, false, compData, outIndex);
/*     */ 
/*     */ 
/*     */         
/* 157 */         int refAddr = lineAddr - lineStride;
/*     */ 
/*     */         
/* 160 */         int a0 = colOffset;
/* 161 */         int last = a0 + width;
/*     */         
/* 163 */         int testbit = (data[lineAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
/*     */ 
/*     */ 
/*     */         
/* 167 */         int a1 = (testbit != 0) ? a0 : nextState(data, lineAddr, a0, last);
/*     */         
/* 169 */         testbit = (refData[refAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
/*     */ 
/*     */         
/* 172 */         int b1 = (testbit != 0) ? a0 : nextState(refData, refAddr, a0, last);
/*     */ 
/*     */         
/* 175 */         int color = 0;
/*     */         
/*     */         while (true) {
/* 178 */           int b2 = nextState(refData, refAddr, b1, last);
/* 179 */           if (b2 < a1) {
/* 180 */             outIndex += add2DBits(compData, outIndex, pass, 0);
/* 181 */             a0 = b2;
/*     */           } else {
/* 183 */             int tmp = b1 - a1 + 3;
/* 184 */             if (tmp <= 6 && tmp >= 0) {
/* 185 */               outIndex += 
/* 186 */                 add2DBits(compData, outIndex, vert, tmp);
/* 187 */               a0 = a1;
/*     */             } else {
/* 189 */               int a2 = nextState(data, lineAddr, a1, last);
/* 190 */               outIndex += 
/* 191 */                 add2DBits(compData, outIndex, horz, 0);
/* 192 */               outIndex += 
/* 193 */                 add1DBits(compData, outIndex, a1 - a0, color);
/* 194 */               outIndex += 
/* 195 */                 add1DBits(compData, outIndex, a2 - a1, color ^ 0x1);
/* 196 */               a0 = a2;
/*     */             } 
/*     */           } 
/* 199 */           if (a0 >= last) {
/*     */             break;
/*     */           }
/* 202 */           color = (data[lineAddr + (a0 >>> 3)] & 0xFF) >>> 7 - (a0 & 0x7) & 0x1;
/*     */           
/* 204 */           a1 = nextState(data, lineAddr, a0, last);
/* 205 */           b1 = nextState(refData, refAddr, a0, last);
/* 206 */           testbit = (refData[refAddr + (b1 >>> 3)] & 0xFF) >>> 7 - (b1 & 0x7) & 0x1;
/*     */           
/* 208 */           if (testbit == color) {
/* 209 */             b1 = nextState(refData, refAddr, b1, last);
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 215 */       lineAddr += lineStride;
/*     */     } 
/*     */     int i;
/* 218 */     for (i = 0; i < 6; i++) {
/* 219 */       outIndex += addEOL(is1DMode, isEOLAligned, true, compData, outIndex);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 226 */     while (this.ndex > 0) {
/* 227 */       compData[outIndex++] = (byte)(this.bits >>> 24);
/* 228 */       this.bits <<= 8;
/* 229 */       this.ndex -= 8;
/*     */     } 
/*     */ 
/*     */     
/* 233 */     if (this.inverseFill) {
/* 234 */       for (i = 0; i < outIndex; i++) {
/* 235 */         compData[i] = TIFFFaxDecompressor.flipTable[compData[i] & 0xFF];
/*     */       }
/*     */     }
/*     */     
/* 239 */     return outIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
/* 246 */     if (bitsPerSample.length != 1 || bitsPerSample[0] != 1) {
/* 247 */       throw new IIOException("Bits per sample must be 1 for T4 compression!");
/*     */     }
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
/* 260 */     int maxBits = 9 * (width + 1) / 2 + 2;
/* 261 */     int bufSize = (maxBits + 7) / 8;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 266 */     bufSize = height * (bufSize + 2) + 12;
/*     */     
/* 268 */     byte[] compData = new byte[bufSize];
/*     */     
/* 270 */     int bytes = encodeT4(this.is1DMode, this.isEOLAligned, b, scanlineStride, 8 * off, width, height, compData);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 276 */     this.stream.write(compData, 0, bytes);
/* 277 */     return bytes;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFT4Compressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */