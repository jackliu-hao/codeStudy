/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import javax.imageio.ImageReader;
/*     */ import javax.imageio.metadata.IIOMetadata;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import javax.imageio.stream.MemoryCacheImageInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFYCbCrDecompressor
/*     */   extends TIFFDecompressor
/*     */ {
/*     */   private static final boolean debug = false;
/*     */   private static final int FRAC_BITS = 16;
/*     */   private static final float FRAC_SCALE = 65536.0F;
/*  70 */   private float LumaRed = 0.299F;
/*  71 */   private float LumaGreen = 0.587F;
/*  72 */   private float LumaBlue = 0.114F;
/*     */   
/*  74 */   private float referenceBlackY = 0.0F;
/*  75 */   private float referenceWhiteY = 255.0F;
/*     */   
/*  77 */   private float referenceBlackCb = 128.0F;
/*  78 */   private float referenceWhiteCb = 255.0F;
/*     */   
/*  80 */   private float referenceBlackCr = 128.0F;
/*  81 */   private float referenceWhiteCr = 255.0F;
/*     */   
/*  83 */   private float codingRangeY = 255.0F;
/*     */   
/*  85 */   private int[] iYTab = new int[256];
/*  86 */   private int[] iCbTab = new int[256];
/*  87 */   private int[] iCrTab = new int[256];
/*     */   
/*  89 */   private int[] iGYTab = new int[256];
/*  90 */   private int[] iGCbTab = new int[256];
/*  91 */   private int[] iGCrTab = new int[256];
/*     */   
/*  93 */   private int chromaSubsampleH = 2;
/*  94 */   private int chromaSubsampleV = 2;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean colorConvert;
/*     */ 
/*     */ 
/*     */   
/*     */   private TIFFDecompressor decompressor;
/*     */ 
/*     */   
/*     */   private BufferedImage tmpImage;
/*     */ 
/*     */ 
/*     */   
/*     */   public TIFFYCbCrDecompressor(TIFFDecompressor decompressor, boolean colorConvert) {
/* 110 */     this.decompressor = decompressor;
/* 111 */     this.colorConvert = colorConvert;
/*     */   }
/*     */   
/*     */   private void warning(String message) {
/* 115 */     if (this.reader instanceof TIFFImageReader) {
/* 116 */       ((TIFFImageReader)this.reader).forwardWarningMessage(message);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReader(ImageReader reader) {
/* 125 */     if (this.decompressor != null) {
/* 126 */       this.decompressor.setReader(reader);
/*     */     }
/* 128 */     super.setReader(reader);
/*     */   }
/*     */   
/*     */   public void setMetadata(IIOMetadata metadata) {
/* 132 */     if (this.decompressor != null) {
/* 133 */       this.decompressor.setMetadata(metadata);
/*     */     }
/* 135 */     super.setMetadata(metadata);
/*     */   }
/*     */   
/*     */   public void setPhotometricInterpretation(int photometricInterpretation) {
/* 139 */     if (this.decompressor != null) {
/* 140 */       this.decompressor.setPhotometricInterpretation(photometricInterpretation);
/*     */     }
/* 142 */     super.setPhotometricInterpretation(photometricInterpretation);
/*     */   }
/*     */   
/*     */   public void setCompression(int compression) {
/* 146 */     if (this.decompressor != null) {
/* 147 */       this.decompressor.setCompression(compression);
/*     */     }
/* 149 */     super.setCompression(compression);
/*     */   }
/*     */   
/*     */   public void setPlanar(boolean planar) {
/* 153 */     if (this.decompressor != null) {
/* 154 */       this.decompressor.setPlanar(planar);
/*     */     }
/* 156 */     super.setPlanar(planar);
/*     */   }
/*     */   
/*     */   public void setSamplesPerPixel(int samplesPerPixel) {
/* 160 */     if (this.decompressor != null) {
/* 161 */       this.decompressor.setSamplesPerPixel(samplesPerPixel);
/*     */     }
/* 163 */     super.setSamplesPerPixel(samplesPerPixel);
/*     */   }
/*     */   
/*     */   public void setBitsPerSample(int[] bitsPerSample) {
/* 167 */     if (this.decompressor != null) {
/* 168 */       this.decompressor.setBitsPerSample(bitsPerSample);
/*     */     }
/* 170 */     super.setBitsPerSample(bitsPerSample);
/*     */   }
/*     */   
/*     */   public void setSampleFormat(int[] sampleFormat) {
/* 174 */     if (this.decompressor != null) {
/* 175 */       this.decompressor.setSampleFormat(sampleFormat);
/*     */     }
/* 177 */     super.setSampleFormat(sampleFormat);
/*     */   }
/*     */   
/*     */   public void setExtraSamples(int[] extraSamples) {
/* 181 */     if (this.decompressor != null) {
/* 182 */       this.decompressor.setExtraSamples(extraSamples);
/*     */     }
/* 184 */     super.setExtraSamples(extraSamples);
/*     */   }
/*     */   
/*     */   public void setColorMap(char[] colorMap) {
/* 188 */     if (this.decompressor != null) {
/* 189 */       this.decompressor.setColorMap(colorMap);
/*     */     }
/* 191 */     super.setColorMap(colorMap);
/*     */   }
/*     */   
/*     */   public void setStream(ImageInputStream stream) {
/* 195 */     if (this.decompressor != null) {
/* 196 */       this.decompressor.setStream(stream);
/*     */     } else {
/* 198 */       super.setStream(stream);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setOffset(long offset) {
/* 203 */     if (this.decompressor != null) {
/* 204 */       this.decompressor.setOffset(offset);
/*     */     }
/* 206 */     super.setOffset(offset);
/*     */   }
/*     */   
/*     */   public void setByteCount(int byteCount) {
/* 210 */     if (this.decompressor != null) {
/* 211 */       this.decompressor.setByteCount(byteCount);
/*     */     }
/* 213 */     super.setByteCount(byteCount);
/*     */   }
/*     */   
/*     */   public void setSrcMinX(int srcMinX) {
/* 217 */     if (this.decompressor != null) {
/* 218 */       this.decompressor.setSrcMinX(srcMinX);
/*     */     }
/* 220 */     super.setSrcMinX(srcMinX);
/*     */   }
/*     */   
/*     */   public void setSrcMinY(int srcMinY) {
/* 224 */     if (this.decompressor != null) {
/* 225 */       this.decompressor.setSrcMinY(srcMinY);
/*     */     }
/* 227 */     super.setSrcMinY(srcMinY);
/*     */   }
/*     */   
/*     */   public void setSrcWidth(int srcWidth) {
/* 231 */     if (this.decompressor != null) {
/* 232 */       this.decompressor.setSrcWidth(srcWidth);
/*     */     }
/* 234 */     super.setSrcWidth(srcWidth);
/*     */   }
/*     */   
/*     */   public void setSrcHeight(int srcHeight) {
/* 238 */     if (this.decompressor != null) {
/* 239 */       this.decompressor.setSrcHeight(srcHeight);
/*     */     }
/* 241 */     super.setSrcHeight(srcHeight);
/*     */   }
/*     */   
/*     */   public void setSourceXOffset(int sourceXOffset) {
/* 245 */     if (this.decompressor != null) {
/* 246 */       this.decompressor.setSourceXOffset(sourceXOffset);
/*     */     }
/* 248 */     super.setSourceXOffset(sourceXOffset);
/*     */   }
/*     */   
/*     */   public void setDstXOffset(int dstXOffset) {
/* 252 */     if (this.decompressor != null) {
/* 253 */       this.decompressor.setDstXOffset(dstXOffset);
/*     */     }
/* 255 */     super.setDstXOffset(dstXOffset);
/*     */   }
/*     */   
/*     */   public void setSourceYOffset(int sourceYOffset) {
/* 259 */     if (this.decompressor != null) {
/* 260 */       this.decompressor.setSourceYOffset(sourceYOffset);
/*     */     }
/* 262 */     super.setSourceYOffset(sourceYOffset);
/*     */   }
/*     */   
/*     */   public void setDstYOffset(int dstYOffset) {
/* 266 */     if (this.decompressor != null) {
/* 267 */       this.decompressor.setDstYOffset(dstYOffset);
/*     */     }
/* 269 */     super.setDstYOffset(dstYOffset);
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
/*     */   public void setSourceBands(int[] sourceBands) {
/* 290 */     if (this.decompressor != null) {
/* 291 */       this.decompressor.setSourceBands(sourceBands);
/*     */     }
/* 293 */     super.setSourceBands(sourceBands);
/*     */   }
/*     */   
/*     */   public void setDestinationBands(int[] destinationBands) {
/* 297 */     if (this.decompressor != null) {
/* 298 */       this.decompressor.setDestinationBands(destinationBands);
/*     */     }
/* 300 */     super.setDestinationBands(destinationBands);
/*     */   }
/*     */   
/*     */   public void setImage(BufferedImage image) {
/* 304 */     if (this.decompressor != null) {
/* 305 */       ColorModel cm = image.getColorModel();
/* 306 */       this
/*     */ 
/*     */         
/* 309 */         .tmpImage = new BufferedImage(cm, image.getRaster().createCompatibleWritableRaster(1, 1), cm.isAlphaPremultiplied(), null);
/*     */       
/* 311 */       this.decompressor.setImage(this.tmpImage);
/*     */     } 
/* 313 */     super.setImage(image);
/*     */   }
/*     */   
/*     */   public void setDstMinX(int dstMinX) {
/* 317 */     if (this.decompressor != null) {
/* 318 */       this.decompressor.setDstMinX(dstMinX);
/*     */     }
/* 320 */     super.setDstMinX(dstMinX);
/*     */   }
/*     */   
/*     */   public void setDstMinY(int dstMinY) {
/* 324 */     if (this.decompressor != null) {
/* 325 */       this.decompressor.setDstMinY(dstMinY);
/*     */     }
/* 327 */     super.setDstMinY(dstMinY);
/*     */   }
/*     */   
/*     */   public void setDstWidth(int dstWidth) {
/* 331 */     if (this.decompressor != null) {
/* 332 */       this.decompressor.setDstWidth(dstWidth);
/*     */     }
/* 334 */     super.setDstWidth(dstWidth);
/*     */   }
/*     */   
/*     */   public void setDstHeight(int dstHeight) {
/* 338 */     if (this.decompressor != null) {
/* 339 */       this.decompressor.setDstHeight(dstHeight);
/*     */     }
/* 341 */     super.setDstHeight(dstHeight);
/*     */   }
/*     */   
/*     */   public void setActiveSrcMinX(int activeSrcMinX) {
/* 345 */     if (this.decompressor != null) {
/* 346 */       this.decompressor.setActiveSrcMinX(activeSrcMinX);
/*     */     }
/* 348 */     super.setActiveSrcMinX(activeSrcMinX);
/*     */   }
/*     */   
/*     */   public void setActiveSrcMinY(int activeSrcMinY) {
/* 352 */     if (this.decompressor != null) {
/* 353 */       this.decompressor.setActiveSrcMinY(activeSrcMinY);
/*     */     }
/* 355 */     super.setActiveSrcMinY(activeSrcMinY);
/*     */   }
/*     */   
/*     */   public void setActiveSrcWidth(int activeSrcWidth) {
/* 359 */     if (this.decompressor != null) {
/* 360 */       this.decompressor.setActiveSrcWidth(activeSrcWidth);
/*     */     }
/* 362 */     super.setActiveSrcWidth(activeSrcWidth);
/*     */   }
/*     */   
/*     */   public void setActiveSrcHeight(int activeSrcHeight) {
/* 366 */     if (this.decompressor != null) {
/* 367 */       this.decompressor.setActiveSrcHeight(activeSrcHeight);
/*     */     }
/* 369 */     super.setActiveSrcHeight(activeSrcHeight);
/*     */   }
/*     */   
/*     */   private byte clamp(int f) {
/* 373 */     if (f < 0)
/* 374 */       return 0; 
/* 375 */     if (f > 16711680) {
/* 376 */       return -1;
/*     */     }
/* 378 */     return (byte)(f >> 16);
/*     */   }
/*     */ 
/*     */   
/*     */   public void beginDecoding() {
/* 383 */     if (this.decompressor != null) {
/* 384 */       this.decompressor.beginDecoding();
/*     */     }
/*     */     
/* 387 */     TIFFImageMetadata tmetadata = (TIFFImageMetadata)this.metadata;
/*     */ 
/*     */     
/* 390 */     TIFFField f = tmetadata.getTIFFField(530);
/* 391 */     if (f != null) {
/* 392 */       if (f.getCount() == 2) {
/* 393 */         this.chromaSubsampleH = f.getAsInt(0);
/* 394 */         this.chromaSubsampleV = f.getAsInt(1);
/*     */         
/* 396 */         if (this.chromaSubsampleH != 1 && this.chromaSubsampleH != 2 && this.chromaSubsampleH != 4) {
/*     */           
/* 398 */           warning("Y_CB_CR_SUBSAMPLING[0] has illegal value " + this.chromaSubsampleH + " (should be 1, 2, or 4), setting to 1");
/*     */ 
/*     */           
/* 401 */           this.chromaSubsampleH = 1;
/*     */         } 
/*     */         
/* 404 */         if (this.chromaSubsampleV != 1 && this.chromaSubsampleV != 2 && this.chromaSubsampleV != 4) {
/*     */           
/* 406 */           warning("Y_CB_CR_SUBSAMPLING[1] has illegal value " + this.chromaSubsampleV + " (should be 1, 2, or 4), setting to 1");
/*     */ 
/*     */           
/* 409 */           this.chromaSubsampleV = 1;
/*     */         } 
/*     */       } else {
/* 412 */         warning("Y_CB_CR_SUBSAMPLING count != 2, assuming no subsampling");
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 418 */     f = tmetadata.getTIFFField(529);
/* 419 */     if (f != null) {
/* 420 */       if (f.getCount() == 3) {
/* 421 */         this.LumaRed = f.getAsFloat(0);
/* 422 */         this.LumaGreen = f.getAsFloat(1);
/* 423 */         this.LumaBlue = f.getAsFloat(2);
/*     */       } else {
/* 425 */         warning("Y_CB_CR_COEFFICIENTS count != 3, assuming default values for CCIR 601-1");
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 431 */     f = tmetadata.getTIFFField(532);
/* 432 */     if (f != null) {
/* 433 */       if (f.getCount() == 6) {
/* 434 */         this.referenceBlackY = f.getAsFloat(0);
/* 435 */         this.referenceWhiteY = f.getAsFloat(1);
/* 436 */         this.referenceBlackCb = f.getAsFloat(2);
/* 437 */         this.referenceWhiteCb = f.getAsFloat(3);
/* 438 */         this.referenceBlackCr = f.getAsFloat(4);
/* 439 */         this.referenceWhiteCr = f.getAsFloat(5);
/*     */       } else {
/* 441 */         warning("REFERENCE_BLACK_WHITE count != 6, ignoring it");
/*     */       } 
/*     */     } else {
/* 444 */       warning("REFERENCE_BLACK_WHITE not found, assuming 0-255/128-255/128-255");
/*     */     } 
/*     */     
/* 447 */     this.colorConvert = true;
/*     */     
/* 449 */     float BCb = 2.0F - 2.0F * this.LumaBlue;
/* 450 */     float RCr = 2.0F - 2.0F * this.LumaRed;
/*     */     
/* 452 */     float GY = (1.0F - this.LumaBlue - this.LumaRed) / this.LumaGreen;
/* 453 */     float GCb = 2.0F * this.LumaBlue * (this.LumaBlue - 1.0F) / this.LumaGreen;
/* 454 */     float GCr = 2.0F * this.LumaRed * (this.LumaRed - 1.0F) / this.LumaGreen;
/*     */     
/* 456 */     for (int i = 0; i < 256; i++) {
/* 457 */       float fY = (i - this.referenceBlackY) * this.codingRangeY / (this.referenceWhiteY - this.referenceBlackY);
/*     */       
/* 459 */       float fCb = (i - this.referenceBlackCb) * 127.0F / (this.referenceWhiteCb - this.referenceBlackCb);
/*     */       
/* 461 */       float fCr = (i - this.referenceBlackCr) * 127.0F / (this.referenceWhiteCr - this.referenceBlackCr);
/*     */ 
/*     */       
/* 464 */       this.iYTab[i] = (int)(fY * 65536.0F);
/* 465 */       this.iCbTab[i] = (int)(fCb * BCb * 65536.0F);
/* 466 */       this.iCrTab[i] = (int)(fCr * RCr * 65536.0F);
/*     */       
/* 468 */       this.iGYTab[i] = (int)(fY * GY * 65536.0F);
/* 469 */       this.iGCbTab[i] = (int)(fCb * GCb * 65536.0F);
/* 470 */       this.iGCrTab[i] = (int)(fCr * GCr * 65536.0F);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void decodeRaw(byte[] buf, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/* 478 */     byte[] rows = new byte[3 * this.srcWidth * this.chromaSubsampleV];
/*     */     
/* 480 */     int elementsPerPacket = this.chromaSubsampleH * this.chromaSubsampleV + 2;
/* 481 */     byte[] packet = new byte[elementsPerPacket];
/*     */     
/* 483 */     if (this.decompressor != null) {
/* 484 */       int bytesPerRow = 3 * this.srcWidth;
/* 485 */       byte[] tmpBuf = new byte[bytesPerRow * this.srcHeight];
/* 486 */       this.decompressor.decodeRaw(tmpBuf, dstOffset, bitsPerPixel, bytesPerRow);
/*     */       
/* 488 */       ByteArrayInputStream byteStream = new ByteArrayInputStream(tmpBuf);
/*     */       
/* 490 */       this.stream = new MemoryCacheImageInputStream(byteStream);
/*     */     } else {
/* 492 */       this.stream.seek(this.offset);
/*     */     } 
/*     */     int y;
/* 495 */     for (y = this.srcMinY; y < this.srcMinY + this.srcHeight; y += this.chromaSubsampleV) {
/*     */       int x;
/* 497 */       for (x = this.srcMinX; x < this.srcMinX + this.srcWidth; 
/* 498 */         x += this.chromaSubsampleH) {
/*     */         try {
/* 500 */           this.stream.readFully(packet);
/* 501 */         } catch (EOFException e) {
/* 502 */           System.out.println("e = " + e);
/*     */           
/*     */           return;
/*     */         } 
/* 506 */         byte Cb = packet[elementsPerPacket - 2];
/* 507 */         byte Cr = packet[elementsPerPacket - 1];
/*     */         
/* 509 */         int iCb = 0, iCr = 0, iGCb = 0, iGCr = 0;
/*     */         
/* 511 */         if (this.colorConvert) {
/* 512 */           int Cbp = Cb & 0xFF;
/* 513 */           int Crp = Cr & 0xFF;
/*     */           
/* 515 */           iCb = this.iCbTab[Cbp];
/* 516 */           iCr = this.iCrTab[Crp];
/*     */           
/* 518 */           iGCb = this.iGCbTab[Cbp];
/* 519 */           iGCr = this.iGCrTab[Crp];
/*     */         } 
/*     */         
/* 522 */         int yIndex = 0;
/* 523 */         for (int v = 0; v < this.chromaSubsampleV; v++) {
/* 524 */           int idx = dstOffset + 3 * (x - this.srcMinX) + scanlineStride * (y - this.srcMinY + v);
/*     */ 
/*     */ 
/*     */           
/* 528 */           if (y + v >= this.srcMinY + this.srcHeight) {
/*     */             break;
/*     */           }
/*     */           
/* 532 */           for (int h = 0; h < this.chromaSubsampleH && 
/* 533 */             x + h < this.srcMinX + this.srcWidth; h++) {
/*     */ 
/*     */ 
/*     */             
/* 537 */             byte Y = packet[yIndex++];
/*     */             
/* 539 */             if (this.colorConvert) {
/* 540 */               int Yp = Y & 0xFF;
/* 541 */               int iY = this.iYTab[Yp];
/* 542 */               int iGY = this.iGYTab[Yp];
/*     */               
/* 544 */               int iR = iY + iCr;
/* 545 */               int iG = iGY + iGCb + iGCr;
/* 546 */               int iB = iY + iCb;
/*     */               
/* 548 */               byte r = clamp(iR);
/* 549 */               byte g = clamp(iG);
/* 550 */               byte b = clamp(iB);
/*     */               
/* 552 */               buf[idx] = r;
/* 553 */               buf[idx + 1] = g;
/* 554 */               buf[idx + 2] = b;
/*     */             } else {
/* 556 */               buf[idx] = Y;
/* 557 */               buf[idx + 1] = Cb;
/* 558 */               buf[idx + 2] = Cr;
/*     */             } 
/*     */             
/* 561 */             idx += 3;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFYCbCrDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */