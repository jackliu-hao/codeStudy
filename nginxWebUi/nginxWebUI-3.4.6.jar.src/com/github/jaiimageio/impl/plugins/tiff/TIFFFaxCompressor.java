/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFField;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class TIFFFaxCompressor
/*     */   extends TIFFCompressor
/*     */ {
/*     */   public static final int WHITE = 0;
/*     */   public static final int BLACK = 1;
/*  70 */   public static byte[] byteTable = new byte[] { 8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   public static int[] termCodesBlack = new int[] { 230686730, 1073741827, -1073741822, -2147483646, 1610612739, 805306372, 536870916, 402653189, 335544326, 268435462, 134217735, 167772167, 234881031, 67108872, 117440520, 201326601, 96469002, 100663306, 33554442, 216006667, 218103819, 226492427, 115343371, 83886091, 48234507, 50331659, 211812364, 212860940, 213909516, 214958092, 109051916, 110100492, 111149068, 112197644, 220200972, 221249548, 222298124, 223346700, 224395276, 225443852, 113246220, 114294796, 228589580, 229638156, 88080396, 89128972, 90177548, 91226124, 104857612, 105906188, 85983244, 87031820, 37748748, 57671692, 58720268, 40894476, 41943052, 92274700, 93323276, 45088780, 46137356, 94371852, 106954764, 108003340 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 114 */   public static int[] termCodesWhite = new int[] { 889192456, 469762054, 1879048196, -2147483644, -1342177276, -1073741820, -536870908, -268435452, -1744830459, -1610612731, 939524101, 1073741829, 536870918, 201326598, -805306362, -738197498, -1476395002, -1409286138, 1308622855, 402653191, 268435463, 771751943, 100663303, 134217735, 1342177287, 1442840583, 637534215, 1207959559, 805306375, 33554440, 50331656, 436207624, 452984840, 301989896, 318767112, 335544328, 352321544, 369098760, 385875976, 671088648, 687865864, 704643080, 721420296, 738197512, 754974728, 67108872, 83886088, 167772168, 184549384, 1375731720, 1392508936, 1409286152, 1426063368, 603979784, 620757000, 1476395016, 1493172232, 1509949448, 1526726664, 1241513992, 1258291208, 838860808, 855638024, 872415240 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 136 */   public static int[] makeupCodesBlack = new int[] { 0, 62914570, 209715212, 210763788, 95420428, 53477388, 54525964, 55574540, 56623117, 57147405, 38797325, 39321613, 39845901, 40370189, 59768845, 60293133, 60817421, 61341709, 61865997, 62390285, 42991629, 43515917, 44040205, 44564493, 47185933, 47710221, 52428813, 52953101, 16777227, 25165835, 27262987, 18874380, 19922956, 20971532, 22020108, 23068684, 24117260, 29360140, 30408716, 31457292, 32505868, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 157 */   public static int[] makeupCodesWhite = new int[] { 0, -671088635, -1879048187, 1543503878, 1845493767, 905969672, 922746888, 1677721608, 1694498824, 1744830472, 1728053256, 1711276041, 1719664649, 1761607689, 1769996297, 1778384905, 1786773513, 1795162121, 1803550729, 1811939337, 1820327945, 1828716553, 1837105161, 1275068425, 1283457033, 1291845641, 1610612742, 1300234249, 16777227, 25165835, 27262987, 18874380, 19922956, 20971532, 22020108, 23068684, 24117260, 29360140, 30408716, 31457292, 32505868, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 178 */   public static int[] passMode = new int[] { 268435460 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 185 */   public static int[] vertMode = new int[] { 100663303, 201326598, 1610612739, -2147483647, 1073741827, 134217734, 67108871 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 198 */   public static int[] horzMode = new int[] { 536870915 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 205 */   public static int[][] termCodes = new int[][] { termCodesWhite, termCodesBlack };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 211 */   public static int[][] makeupCodes = new int[][] { makeupCodesWhite, makeupCodesBlack };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 217 */   public static int[][] pass = new int[][] { passMode, passMode };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 222 */   public static int[][] vert = new int[][] { vertMode, vertMode };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 227 */   public static int[][] horz = new int[][] { horzMode, horzMode };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean inverseFill = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int bits;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int ndex;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TIFFFaxCompressor(String compressionType, int compressionTagValue, boolean isCompressionLossless) {
/* 253 */     super(compressionType, compressionTagValue, isCompressionLossless);
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
/*     */   public void setMetadata(IIOMetadata metadata) {
/* 268 */     super.setMetadata(metadata);
/*     */     
/* 270 */     if (metadata instanceof TIFFImageMetadata) {
/* 271 */       TIFFImageMetadata tim = (TIFFImageMetadata)metadata;
/* 272 */       TIFFField f = tim.getTIFFField(266);
/* 273 */       this.inverseFill = (f != null && f.getAsInt(0) == 2);
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
/*     */   public int nextState(byte[] data, int base, int bitOffset, int maxOffset) {
/*     */     int testbyte;
/* 286 */     if (data == null) {
/* 287 */       return maxOffset;
/*     */     }
/*     */     
/* 290 */     int next = base + (bitOffset >>> 3);
/*     */ 
/*     */     
/* 293 */     if (next >= data.length) {
/* 294 */       return maxOffset;
/*     */     }
/* 296 */     int end = base + (maxOffset >>> 3);
/* 297 */     if (end == data.length) {
/* 298 */       end--;
/*     */     }
/* 300 */     int extra = bitOffset & 0x7;
/*     */ 
/*     */ 
/*     */     
/* 304 */     if ((data[next] & 128 >>> extra) != 0) {
/* 305 */       testbyte = (data[next] ^ 0xFFFFFFFF) & 255 >>> extra;
/* 306 */       while (next < end && 
/* 307 */         testbyte == 0)
/*     */       {
/*     */         
/* 310 */         testbyte = (data[++next] ^ 0xFFFFFFFF) & 0xFF;
/*     */       }
/*     */     } else {
/* 313 */       if ((testbyte = data[next] & 255 >>> extra) != 0) {
/* 314 */         bitOffset = (next - base) * 8 + byteTable[testbyte];
/* 315 */         return (bitOffset < maxOffset) ? bitOffset : maxOffset;
/*     */       } 
/* 317 */       while (next < end) {
/* 318 */         if ((testbyte = data[++next] & 0xFF) != 0) {
/*     */           
/* 320 */           bitOffset = (next - base) * 8 + byteTable[testbyte];
/* 321 */           return (bitOffset < maxOffset) ? bitOffset : maxOffset;
/*     */         } 
/*     */       } 
/*     */     } 
/* 325 */     bitOffset = (next - base) * 8 + byteTable[testbyte];
/* 326 */     return (bitOffset < maxOffset) ? bitOffset : maxOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void initBitBuf() {
/* 334 */     this.ndex = 0;
/* 335 */     this.bits = 0;
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
/*     */   public int add1DBits(byte[] buf, int where, int count, int color) {
/* 348 */     int len = where;
/*     */     
/* 350 */     int sixtyfours = count >>> 6;
/* 351 */     count &= 0x3F;
/* 352 */     if (sixtyfours != 0) {
/* 353 */       for (; sixtyfours > 40; sixtyfours -= 40) {
/* 354 */         int j = makeupCodes[color][40];
/* 355 */         this.bits |= (j & 0xFFF80000) >>> this.ndex;
/* 356 */         this.ndex += j & 0xFFFF;
/* 357 */         while (this.ndex > 7) {
/* 358 */           buf[len++] = (byte)(this.bits >>> 24);
/* 359 */           this.bits <<= 8;
/* 360 */           this.ndex -= 8;
/*     */         } 
/*     */       } 
/*     */       
/* 364 */       int i = makeupCodes[color][sixtyfours];
/* 365 */       this.bits |= (i & 0xFFF80000) >>> this.ndex;
/* 366 */       this.ndex += i & 0xFFFF;
/* 367 */       while (this.ndex > 7) {
/* 368 */         buf[len++] = (byte)(this.bits >>> 24);
/* 369 */         this.bits <<= 8;
/* 370 */         this.ndex -= 8;
/*     */       } 
/*     */     } 
/*     */     
/* 374 */     int mask = termCodes[color][count];
/* 375 */     this.bits |= (mask & 0xFFF80000) >>> this.ndex;
/* 376 */     this.ndex += mask & 0xFFFF;
/* 377 */     while (this.ndex > 7) {
/* 378 */       buf[len++] = (byte)(this.bits >>> 24);
/* 379 */       this.bits <<= 8;
/* 380 */       this.ndex -= 8;
/*     */     } 
/*     */     
/* 383 */     return len - where;
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
/*     */   public int add2DBits(byte[] buf, int where, int[][] mode, int entry) {
/* 395 */     int len = where;
/* 396 */     int color = 0;
/*     */     
/* 398 */     int mask = mode[color][entry];
/* 399 */     this.bits |= (mask & 0xFFF80000) >>> this.ndex;
/* 400 */     this.ndex += mask & 0xFFFF;
/* 401 */     while (this.ndex > 7) {
/* 402 */       buf[len++] = (byte)(this.bits >>> 24);
/* 403 */       this.bits <<= 8;
/* 404 */       this.ndex -= 8;
/*     */     } 
/*     */     
/* 407 */     return len - where;
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
/*     */   public int addEOL(boolean is1DMode, boolean addFill, boolean add1, byte[] buf, int where) {
/* 420 */     int len = where;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 427 */     if (addFill)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 435 */       this.ndex += (this.ndex <= 4) ? (4 - this.ndex) : (12 - this.ndex);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 441 */     if (is1DMode) {
/* 442 */       this.bits |= 1048576 >>> this.ndex;
/* 443 */       this.ndex += 12;
/*     */     } else {
/* 445 */       this.bits |= (add1 ? 1572864 : 1048576) >>> this.ndex;
/* 446 */       this.ndex += 13;
/*     */     } 
/*     */     
/* 449 */     while (this.ndex > 7) {
/* 450 */       buf[len++] = (byte)(this.bits >>> 24);
/* 451 */       this.bits <<= 8;
/* 452 */       this.ndex -= 8;
/*     */     } 
/*     */     
/* 455 */     return len - where;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int addEOFB(byte[] buf, int where) {
/* 465 */     int len = where;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 470 */     this.bits |= 1048832 >>> this.ndex;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 475 */     this.ndex += 24;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 480 */     while (this.ndex > 0) {
/* 481 */       buf[len++] = (byte)(this.bits >>> 24);
/* 482 */       this.bits <<= 8;
/* 483 */       this.ndex -= 8;
/*     */     } 
/*     */     
/* 486 */     return len - where;
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
/*     */   public int encode1D(byte[] data, int rowOffset, int colOffset, int rowLength, byte[] compData, int compOffset) {
/* 502 */     int lineAddr = rowOffset;
/* 503 */     int bitIndex = colOffset;
/*     */     
/* 505 */     int last = bitIndex + rowLength;
/* 506 */     int outIndex = compOffset;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 511 */     int testbit = (data[lineAddr + (bitIndex >>> 3)] & 0xFF) >>> 7 - (bitIndex & 0x7) & 0x1;
/*     */ 
/*     */     
/* 514 */     int currentColor = 1;
/* 515 */     if (testbit != 0) {
/* 516 */       outIndex += add1DBits(compData, outIndex, 0, 0);
/*     */     } else {
/* 518 */       currentColor = 0;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 524 */     while (bitIndex < last) {
/*     */       
/* 526 */       int bitCount = nextState(data, lineAddr, bitIndex, last) - bitIndex;
/* 527 */       outIndex += 
/* 528 */         add1DBits(compData, outIndex, bitCount, currentColor);
/* 529 */       bitIndex += bitCount;
/* 530 */       currentColor ^= 0x1;
/*     */     } 
/*     */     
/* 533 */     return outIndex - compOffset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFFaxCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */