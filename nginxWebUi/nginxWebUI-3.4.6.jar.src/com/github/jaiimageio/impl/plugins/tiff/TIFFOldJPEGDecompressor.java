/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import javax.imageio.IIOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFOldJPEGDecompressor
/*     */   extends TIFFJPEGDecompressor
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private static final int DHT = 196;
/*     */   private static final int DQT = 219;
/*     */   private static final int DRI = 221;
/*     */   private static final int SOF0 = 192;
/*     */   private static final int SOS = 218;
/*     */   private boolean isInitialized = false;
/*  93 */   private Long JPEGStreamOffset = null;
/*     */   
/*  95 */   private int SOFPosition = -1;
/*     */   
/*  97 */   private byte[] SOSMarker = null;
/*     */ 
/*     */   
/* 100 */   private int subsamplingX = 2;
/*     */ 
/*     */   
/* 103 */   private int subsamplingY = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void initialize() throws IOException {
/* 180 */     if (this.isInitialized) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 185 */     TIFFImageMetadata tim = (TIFFImageMetadata)this.metadata;
/*     */ 
/*     */ 
/*     */     
/* 189 */     TIFFField JPEGInterchangeFormatField = tim.getTIFFField(513);
/*     */ 
/*     */ 
/*     */     
/* 193 */     TIFFField segmentOffsetField = tim.getTIFFField(324);
/* 194 */     if (segmentOffsetField == null) {
/*     */       
/* 196 */       segmentOffsetField = tim.getTIFFField(273);
/* 197 */       if (segmentOffsetField == null) {
/* 198 */         segmentOffsetField = JPEGInterchangeFormatField;
/*     */       }
/*     */     } 
/* 201 */     long[] segmentOffsets = segmentOffsetField.getAsLongs();
/*     */ 
/*     */     
/* 204 */     boolean isTiled = (segmentOffsets.length > 1);
/*     */     
/* 206 */     if (!isTiled) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 214 */       this.stream.seek(this.offset);
/* 215 */       this.stream.mark();
/* 216 */       if (this.stream.read() == 255 && this.stream.read() == 216) {
/*     */         
/* 218 */         this.JPEGStreamOffset = new Long(this.offset);
/*     */ 
/*     */ 
/*     */         
/* 222 */         ((TIFFImageReader)this.reader).forwardWarningMessage("SOI marker detected at start of strip or tile.");
/* 223 */         this.isInitialized = true;
/* 224 */         this.stream.reset();
/*     */         return;
/*     */       } 
/* 227 */       this.stream.reset();
/*     */       
/* 229 */       if (JPEGInterchangeFormatField != null) {
/*     */ 
/*     */         
/* 232 */         long jpegInterchangeOffset = JPEGInterchangeFormatField.getAsLong(0);
/*     */ 
/*     */         
/* 235 */         this.stream.mark();
/* 236 */         this.stream.seek(jpegInterchangeOffset);
/* 237 */         if (this.stream.read() == 255 && this.stream.read() == 216) {
/*     */           
/* 239 */           this.JPEGStreamOffset = new Long(jpegInterchangeOffset);
/*     */         } else {
/* 241 */           ((TIFFImageReader)this.reader).forwardWarningMessage("JPEGInterchangeFormat does not point to SOI");
/* 242 */         }  this.stream.reset();
/*     */ 
/*     */ 
/*     */         
/* 246 */         TIFFField JPEGInterchangeFormatLengthField = tim.getTIFFField(514);
/*     */         
/* 248 */         if (JPEGInterchangeFormatLengthField == null) {
/*     */           
/* 250 */           ((TIFFImageReader)this.reader).forwardWarningMessage("JPEGInterchangeFormatLength field is missing");
/*     */         }
/*     */         else {
/*     */           
/* 254 */           long jpegInterchangeLength = JPEGInterchangeFormatLengthField.getAsLong(0);
/*     */           
/* 256 */           if (jpegInterchangeOffset >= segmentOffsets[0] || jpegInterchangeOffset + jpegInterchangeLength <= segmentOffsets[0])
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 262 */             ((TIFFImageReader)this.reader).forwardWarningMessage("JPEGInterchangeFormatLength field value is invalid");
/*     */           }
/*     */         } 
/*     */ 
/*     */         
/* 267 */         if (this.JPEGStreamOffset != null) {
/* 268 */           this.isInitialized = true;
/*     */ 
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 276 */     TIFFField YCbCrSubsamplingField = tim.getTIFFField(530);
/* 277 */     if (YCbCrSubsamplingField != null) {
/* 278 */       this.subsamplingX = YCbCrSubsamplingField.getAsChars()[0];
/* 279 */       this.subsamplingY = YCbCrSubsamplingField.getAsChars()[1];
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 286 */     if (JPEGInterchangeFormatField != null) {
/*     */ 
/*     */       
/* 289 */       long jpegInterchangeOffset = JPEGInterchangeFormatField.getAsLong(0);
/*     */ 
/*     */ 
/*     */       
/* 293 */       TIFFField JPEGInterchangeFormatLengthField = tim.getTIFFField(514);
/*     */       
/* 295 */       if (JPEGInterchangeFormatLengthField != null) {
/*     */ 
/*     */         
/* 298 */         long jpegInterchangeLength = JPEGInterchangeFormatLengthField.getAsLong(0);
/*     */         
/* 300 */         if (jpegInterchangeLength >= 2L && jpegInterchangeOffset + jpegInterchangeLength <= segmentOffsets[0]) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 305 */           this.stream.mark();
/* 306 */           this.stream.seek(jpegInterchangeOffset + jpegInterchangeLength - 2L);
/* 307 */           if (this.stream.read() == 255 && this.stream.read() == 217) {
/* 308 */             this.tables = new byte[(int)(jpegInterchangeLength - 2L)];
/*     */           } else {
/* 310 */             this.tables = new byte[(int)jpegInterchangeLength];
/*     */           } 
/* 312 */           this.stream.reset();
/*     */ 
/*     */           
/* 315 */           this.stream.mark();
/* 316 */           this.stream.seek(jpegInterchangeOffset);
/* 317 */           this.stream.readFully(this.tables);
/* 318 */           this.stream.reset();
/*     */ 
/*     */           
/* 321 */           ((TIFFImageReader)this.reader).forwardWarningMessage("Incorrect JPEG interchange format: using JPEGInterchangeFormat offset to derive tables.");
/*     */         } else {
/* 323 */           ((TIFFImageReader)this.reader).forwardWarningMessage("JPEGInterchangeFormat+JPEGInterchangeFormatLength > offset to first strip or tile.");
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 328 */     if (this.tables == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 334 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */ 
/*     */ 
/*     */       
/* 338 */       long streamLength = this.stream.length();
/*     */ 
/*     */       
/* 341 */       baos.write(255);
/* 342 */       baos.write(216);
/*     */ 
/*     */ 
/*     */       
/* 346 */       TIFFField f = tim.getTIFFField(519);
/* 347 */       if (f == null) {
/* 348 */         throw new IIOException("JPEGQTables field missing!");
/*     */       }
/* 350 */       long[] off = f.getAsLongs();
/*     */       
/* 352 */       for (int i = 0; i < off.length; i++) {
/* 353 */         baos.write(255);
/* 354 */         baos.write(219);
/*     */         
/* 356 */         char markerLength = 'C';
/* 357 */         baos.write(markerLength >>> 8 & 0xFF);
/* 358 */         baos.write(markerLength & 0xFF);
/*     */         
/* 360 */         baos.write(i);
/*     */         
/* 362 */         byte[] qtable = new byte[64];
/* 363 */         if (streamLength != -1L && off[i] > streamLength) {
/* 364 */           throw new IIOException("JPEGQTables offset for index " + i + " is not in the stream!");
/*     */         }
/*     */         
/* 367 */         this.stream.seek(off[i]);
/* 368 */         this.stream.readFully(qtable);
/*     */         
/* 370 */         baos.write(qtable);
/*     */       } 
/*     */ 
/*     */       
/* 374 */       for (int k = 0; k < 2; k++) {
/* 375 */         int tableTagNumber = (k == 0) ? 520 : 521;
/*     */ 
/*     */         
/* 378 */         f = tim.getTIFFField(tableTagNumber);
/* 379 */         String fieldName = (tableTagNumber == 520) ? "JPEGDCTables" : "JPEGACTables";
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 384 */         if (f == null) {
/* 385 */           throw new IIOException(fieldName + " field missing!");
/*     */         }
/* 387 */         off = f.getAsLongs();
/*     */         
/* 389 */         for (int j = 0; j < off.length; j++) {
/* 390 */           baos.write(255);
/* 391 */           baos.write(196);
/*     */           
/* 393 */           byte[] blengths = new byte[16];
/* 394 */           if (streamLength != -1L && off[j] > streamLength) {
/* 395 */             throw new IIOException(fieldName + " offset for index " + j + " is not in the stream!");
/*     */           }
/*     */           
/* 398 */           this.stream.seek(off[j]);
/* 399 */           this.stream.readFully(blengths);
/* 400 */           int numCodes = 0;
/* 401 */           for (int m = 0; m < 16; m++) {
/* 402 */             numCodes += blengths[m] & 0xFF;
/*     */           }
/*     */           
/* 405 */           char markerLength = (char)(19 + numCodes);
/*     */           
/* 407 */           baos.write(markerLength >>> 8 & 0xFF);
/* 408 */           baos.write(markerLength & 0xFF);
/*     */           
/* 410 */           baos.write(j | k << 4);
/*     */           
/* 412 */           baos.write(blengths);
/*     */           
/* 414 */           byte[] bcodes = new byte[numCodes];
/* 415 */           this.stream.readFully(bcodes);
/* 416 */           baos.write(bcodes);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 421 */       baos.write(-1);
/* 422 */       baos.write(-64);
/* 423 */       short sval = (short)(8 + 3 * this.samplesPerPixel);
/* 424 */       baos.write((byte)(sval >>> 8 & 0xFF));
/* 425 */       baos.write((byte)(sval & 0xFF));
/* 426 */       baos.write(8);
/* 427 */       sval = (short)this.srcHeight;
/* 428 */       baos.write((byte)(sval >>> 8 & 0xFF));
/* 429 */       baos.write((byte)(sval & 0xFF));
/* 430 */       sval = (short)this.srcWidth;
/* 431 */       baos.write((byte)(sval >>> 8 & 0xFF));
/* 432 */       baos.write((byte)(sval & 0xFF));
/* 433 */       baos.write((byte)this.samplesPerPixel);
/* 434 */       if (this.samplesPerPixel == 1) {
/* 435 */         baos.write(1);
/* 436 */         baos.write(17);
/* 437 */         baos.write(0);
/*     */       } else {
/* 439 */         for (int j = 0; j < 3; j++) {
/* 440 */           baos.write((byte)(j + 1));
/* 441 */           baos.write((j != 0) ? 17 : (byte)((this.subsamplingX & 0xF) << 4 | this.subsamplingY & 0xF));
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 446 */           baos.write((byte)j);
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 452 */       f = tim.getTIFFField(515);
/* 453 */       if (f != null) {
/* 454 */         char restartInterval = f.getAsChars()[0];
/*     */         
/* 456 */         if (restartInterval != '\000') {
/* 457 */           baos.write(-1);
/* 458 */           baos.write(-35);
/*     */           
/* 460 */           sval = 4;
/* 461 */           baos.write((byte)(sval >>> 8 & 0xFF));
/* 462 */           baos.write((byte)(sval & 0xFF));
/*     */ 
/*     */           
/* 465 */           baos.write((byte)(restartInterval >>> 8 & 0xFF));
/* 466 */           baos.write((byte)(restartInterval & 0xFF));
/*     */         } 
/*     */       } 
/*     */       
/* 470 */       this.tables = baos.toByteArray();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 478 */     int idx = 0;
/* 479 */     int idxMax = this.tables.length - 1;
/* 480 */     while (idx < idxMax) {
/* 481 */       if ((this.tables[idx] & 0xFF) == 255 && (this.tables[idx + 1] & 0xFF) == 192) {
/*     */         
/* 483 */         this.SOFPosition = idx;
/*     */         break;
/*     */       } 
/* 486 */       idx++;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 492 */     if (this.SOFPosition == -1) {
/* 493 */       byte[] tmpTables = new byte[this.tables.length + 10 + 3 * this.samplesPerPixel];
/*     */       
/* 495 */       System.arraycopy(this.tables, 0, tmpTables, 0, this.tables.length);
/* 496 */       int tmpOffset = this.tables.length;
/* 497 */       this.SOFPosition = this.tables.length;
/* 498 */       this.tables = tmpTables;
/*     */       
/* 500 */       this.tables[tmpOffset++] = -1;
/* 501 */       this.tables[tmpOffset++] = -64;
/* 502 */       short sval = (short)(8 + 3 * this.samplesPerPixel);
/* 503 */       this.tables[tmpOffset++] = (byte)(sval >>> 8 & 0xFF);
/* 504 */       this.tables[tmpOffset++] = (byte)(sval & 0xFF);
/* 505 */       this.tables[tmpOffset++] = 8;
/* 506 */       sval = (short)this.srcHeight;
/* 507 */       this.tables[tmpOffset++] = (byte)(sval >>> 8 & 0xFF);
/* 508 */       this.tables[tmpOffset++] = (byte)(sval & 0xFF);
/* 509 */       sval = (short)this.srcWidth;
/* 510 */       this.tables[tmpOffset++] = (byte)(sval >>> 8 & 0xFF);
/* 511 */       this.tables[tmpOffset++] = (byte)(sval & 0xFF);
/* 512 */       this.tables[tmpOffset++] = (byte)this.samplesPerPixel;
/* 513 */       if (this.samplesPerPixel == 1) {
/* 514 */         this.tables[tmpOffset++] = 1;
/* 515 */         this.tables[tmpOffset++] = 17;
/* 516 */         this.tables[tmpOffset++] = 0;
/*     */       } else {
/* 518 */         for (int i = 0; i < 3; i++) {
/* 519 */           this.tables[tmpOffset++] = (byte)(i + 1);
/* 520 */           this.tables[tmpOffset++] = (i != 0) ? 17 : (byte)((this.subsamplingX & 0xF) << 4 | this.subsamplingY & 0xF);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 525 */           this.tables[tmpOffset++] = (byte)i;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 533 */     this.stream.mark();
/* 534 */     this.stream.seek(segmentOffsets[0]);
/* 535 */     if (this.stream.read() == 255 && this.stream.read() == 218) {
/*     */ 
/*     */ 
/*     */       
/* 539 */       int SOSLength = this.stream.read() << 8 | this.stream.read();
/* 540 */       this.SOSMarker = new byte[SOSLength + 2];
/* 541 */       this.SOSMarker[0] = -1;
/* 542 */       this.SOSMarker[1] = -38;
/* 543 */       this.SOSMarker[2] = (byte)((SOSLength & 0xFF00) >> 8);
/* 544 */       this.SOSMarker[3] = (byte)(SOSLength & 0xFF);
/* 545 */       this.stream.readFully(this.SOSMarker, 4, SOSLength - 2);
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 550 */       this.SOSMarker = new byte[8 + 2 * this.samplesPerPixel];
/* 551 */       int SOSMarkerIndex = 0;
/* 552 */       this.SOSMarker[SOSMarkerIndex++] = -1;
/* 553 */       this.SOSMarker[SOSMarkerIndex++] = -38;
/* 554 */       short sval = (short)(6 + 2 * this.samplesPerPixel);
/* 555 */       this.SOSMarker[SOSMarkerIndex++] = (byte)(sval >>> 8 & 0xFF);
/* 556 */       this.SOSMarker[SOSMarkerIndex++] = (byte)(sval & 0xFF);
/*     */       
/* 558 */       this.SOSMarker[SOSMarkerIndex++] = (byte)this.samplesPerPixel;
/* 559 */       if (this.samplesPerPixel == 1) {
/* 560 */         this.SOSMarker[SOSMarkerIndex++] = 1;
/* 561 */         this.SOSMarker[SOSMarkerIndex++] = 0;
/*     */       } else {
/* 563 */         for (int i = 0; i < 3; i++) {
/* 564 */           this.SOSMarker[SOSMarkerIndex++] = (byte)(i + 1);
/*     */           
/* 566 */           this.SOSMarker[SOSMarkerIndex++] = (byte)(i << 4 | i);
/*     */         } 
/*     */       } 
/*     */       
/* 570 */       this.SOSMarker[SOSMarkerIndex++] = 0;
/* 571 */       this.SOSMarker[SOSMarkerIndex++] = 63;
/* 572 */       this.SOSMarker[SOSMarkerIndex++] = 0;
/*     */     } 
/* 574 */     this.stream.reset();
/*     */ 
/*     */     
/* 577 */     this.isInitialized = true;
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
/*     */   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/* 592 */     initialize();
/*     */     
/* 594 */     TIFFImageMetadata tim = (TIFFImageMetadata)this.metadata;
/*     */     
/* 596 */     if (this.JPEGStreamOffset != null) {
/* 597 */       this.stream.seek(this.JPEGStreamOffset.longValue());
/* 598 */       this.JPEGReader.setInput(this.stream, false, true);
/*     */     } else {
/*     */       
/* 601 */       int tableLength = this.tables.length;
/* 602 */       int bufLength = tableLength + this.SOSMarker.length + this.byteCount + 2;
/*     */       
/* 604 */       byte[] buf = new byte[bufLength];
/* 605 */       if (this.tables != null) {
/* 606 */         System.arraycopy(this.tables, 0, buf, 0, tableLength);
/*     */       }
/* 608 */       int bufOffset = tableLength;
/*     */ 
/*     */       
/* 611 */       short sval = (short)this.srcHeight;
/* 612 */       buf[this.SOFPosition + 5] = (byte)(sval >>> 8 & 0xFF);
/* 613 */       buf[this.SOFPosition + 6] = (byte)(sval & 0xFF);
/* 614 */       sval = (short)this.srcWidth;
/* 615 */       buf[this.SOFPosition + 7] = (byte)(sval >>> 8 & 0xFF);
/* 616 */       buf[this.SOFPosition + 8] = (byte)(sval & 0xFF);
/*     */ 
/*     */       
/* 619 */       this.stream.seek(this.offset);
/*     */ 
/*     */       
/* 622 */       byte[] twoBytes = new byte[2];
/* 623 */       this.stream.readFully(twoBytes);
/* 624 */       if ((twoBytes[0] & 0xFF) != 255 || (twoBytes[1] & 0xFF) != 218) {
/*     */ 
/*     */         
/* 627 */         System.arraycopy(this.SOSMarker, 0, buf, bufOffset, this.SOSMarker.length);
/*     */         
/* 629 */         bufOffset += this.SOSMarker.length;
/*     */       } 
/*     */ 
/*     */       
/* 633 */       buf[bufOffset++] = twoBytes[0];
/* 634 */       buf[bufOffset++] = twoBytes[1];
/* 635 */       this.stream.readFully(buf, bufOffset, this.byteCount - 2);
/* 636 */       bufOffset += this.byteCount - 2;
/*     */ 
/*     */       
/* 639 */       buf[bufOffset++] = -1;
/* 640 */       buf[bufOffset++] = -39;
/*     */       
/* 642 */       ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, bufOffset);
/*     */       
/* 644 */       ImageInputStream is = new MemoryCacheImageInputStream(bais);
/*     */       
/* 646 */       this.JPEGReader.setInput(is, true, true);
/*     */     } 
/*     */ 
/*     */     
/* 650 */     this.JPEGParam.setDestination(this.rawImage);
/* 651 */     this.JPEGReader.read(0, this.JPEGParam);
/*     */   }
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 655 */     super.finalize();
/* 656 */     this.JPEGReader.dispose();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFOldJPEGDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */