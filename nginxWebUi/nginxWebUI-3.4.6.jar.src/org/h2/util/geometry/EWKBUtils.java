/*     */ package org.h2.util.geometry;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class EWKBUtils
/*     */ {
/*     */   public static final int EWKB_Z = -2147483648;
/*     */   public static final int EWKB_M = 1073741824;
/*     */   public static final int EWKB_SRID = 536870912;
/*     */   
/*     */   public static final class EWKBTarget
/*     */     extends GeometryUtils.Target
/*     */   {
/*     */     private final ByteArrayOutputStream output;
/*     */     private final int dimensionSystem;
/*  54 */     private final byte[] buf = new byte[8];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int type;
/*     */ 
/*     */ 
/*     */     
/*     */     private int srid;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public EWKBTarget(ByteArrayOutputStream param1ByteArrayOutputStream, int param1Int) {
/*  69 */       this.output = param1ByteArrayOutputStream;
/*  70 */       this.dimensionSystem = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void init(int param1Int) {
/*  75 */       this.srid = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPoint() {
/*  80 */       writeHeader(1);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startLineString(int param1Int) {
/*  85 */       writeHeader(2);
/*  86 */       writeInt(param1Int);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygon(int param1Int1, int param1Int2) {
/*  91 */       writeHeader(3);
/*  92 */       if (param1Int1 == 0 && param1Int2 == 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  98 */         writeInt(0);
/*     */       } else {
/* 100 */         writeInt(param1Int1 + 1);
/* 101 */         writeInt(param1Int2);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygonInner(int param1Int) {
/* 107 */       writeInt(param1Int);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startCollection(int param1Int1, int param1Int2) {
/* 112 */       writeHeader(param1Int1);
/* 113 */       writeInt(param1Int2);
/*     */     }
/*     */     
/*     */     private void writeHeader(int param1Int) {
/* 117 */       this.type = param1Int;
/* 118 */       switch (this.dimensionSystem) {
/*     */         case 1:
/* 120 */           param1Int |= Integer.MIN_VALUE;
/*     */           break;
/*     */         case 3:
/* 123 */           param1Int |= Integer.MIN_VALUE;
/*     */         
/*     */         case 2:
/* 126 */           param1Int |= 0x40000000; break;
/*     */       } 
/* 128 */       if (this.srid != 0) {
/* 129 */         param1Int |= 0x20000000;
/*     */       }
/* 131 */       this.output.write(0);
/* 132 */       writeInt(param1Int);
/* 133 */       if (this.srid != 0) {
/* 134 */         writeInt(this.srid);
/*     */         
/* 136 */         this.srid = 0;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected GeometryUtils.Target startCollectionItem(int param1Int1, int param1Int2) {
/* 142 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void addCoordinate(double param1Double1, double param1Double2, double param1Double3, double param1Double4, int param1Int1, int param1Int2) {
/* 148 */       boolean bool = (this.type != 1 || !Double.isNaN(param1Double1) || !Double.isNaN(param1Double2) || !Double.isNaN(param1Double3) || !Double.isNaN(param1Double4)) ? true : false;
/* 149 */       if (bool) {
/* 150 */         GeometryUtils.checkFinite(param1Double1);
/* 151 */         GeometryUtils.checkFinite(param1Double2);
/*     */       } 
/* 153 */       writeDouble(param1Double1);
/* 154 */       writeDouble(param1Double2);
/* 155 */       if ((this.dimensionSystem & 0x1) != 0) {
/* 156 */         writeDouble(bool ? GeometryUtils.checkFinite(param1Double3) : param1Double3);
/* 157 */       } else if (bool && !Double.isNaN(param1Double3)) {
/* 158 */         throw new IllegalArgumentException();
/*     */       } 
/* 160 */       if ((this.dimensionSystem & 0x2) != 0) {
/* 161 */         writeDouble(bool ? GeometryUtils.checkFinite(param1Double4) : param1Double4);
/* 162 */       } else if (bool && !Double.isNaN(param1Double4)) {
/* 163 */         throw new IllegalArgumentException();
/*     */       } 
/*     */     }
/*     */     
/*     */     private void writeInt(int param1Int) {
/* 168 */       Bits.writeInt(this.buf, 0, param1Int);
/* 169 */       this.output.write(this.buf, 0, 4);
/*     */     }
/*     */     
/*     */     private void writeDouble(double param1Double) {
/* 173 */       param1Double = GeometryUtils.toCanonicalDouble(param1Double);
/* 174 */       Bits.writeDouble(this.buf, 0, param1Double);
/* 175 */       this.output.write(this.buf, 0, 8);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class EWKBSource
/*     */   {
/*     */     private final byte[] ewkb;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int offset;
/*     */ 
/*     */ 
/*     */     
/*     */     boolean bigEndian;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     EWKBSource(byte[] param1ArrayOfbyte) {
/* 200 */       this.ewkb = param1ArrayOfbyte;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     byte readByte() {
/* 209 */       return this.ewkb[this.offset++];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int readInt() {
/* 218 */       int i = this.bigEndian ? Bits.readInt(this.ewkb, this.offset) : Bits.readIntLE(this.ewkb, this.offset);
/* 219 */       this.offset += 4;
/* 220 */       return i;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     double readCoordinate() {
/* 229 */       double d = this.bigEndian ? Bits.readDouble(this.ewkb, this.offset) : Bits.readDoubleLE(this.ewkb, this.offset);
/* 230 */       this.offset += 8;
/* 231 */       return GeometryUtils.toCanonicalDouble(d);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 236 */       String str = StringUtils.convertBytesToHex(this.ewkb);
/* 237 */       int i = this.offset * 2;
/* 238 */       return (new StringBuilder(str.length() + 3)).append(str, 0, i).append("<*>").append(str, i, str.length())
/* 239 */         .toString();
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
/*     */   public static byte[] ewkb2ewkb(byte[] paramArrayOfbyte) {
/* 269 */     return ewkb2ewkb(paramArrayOfbyte, getDimensionSystem(paramArrayOfbyte));
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
/*     */   public static byte[] ewkb2ewkb(byte[] paramArrayOfbyte, int paramInt) {
/* 284 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 285 */     EWKBTarget eWKBTarget = new EWKBTarget(byteArrayOutputStream, paramInt);
/* 286 */     parseEWKB(paramArrayOfbyte, eWKBTarget);
/* 287 */     return byteArrayOutputStream.toByteArray();
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
/*     */   public static void parseEWKB(byte[] paramArrayOfbyte, GeometryUtils.Target paramTarget) {
/*     */     try {
/* 300 */       parseEWKB(new EWKBSource(paramArrayOfbyte), paramTarget, 0);
/* 301 */     } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
/* 302 */       throw new IllegalArgumentException();
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
/*     */   public static int type2dimensionSystem(int paramInt) {
/* 315 */     boolean bool1 = ((paramInt & Integer.MIN_VALUE) != 0) ? true : false;
/* 316 */     boolean bool2 = ((paramInt & 0x40000000) != 0) ? true : false;
/*     */     
/* 318 */     paramInt &= 0xFFFF;
/* 319 */     switch (paramInt / 1000) {
/*     */       case 1:
/* 321 */         bool1 = true;
/*     */         break;
/*     */       case 3:
/* 324 */         bool1 = true;
/*     */       
/*     */       case 2:
/* 327 */         bool2 = true; break;
/*     */     } 
/* 329 */     return (bool1 ? 1 : 0) | (bool2 ? 2 : 0);
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
/*     */   private static void parseEWKB(EWKBSource paramEWKBSource, GeometryUtils.Target paramTarget, int paramInt) {
/*     */     int j, k;
/* 344 */     switch (paramEWKBSource.readByte()) {
/*     */       case 0:
/* 346 */         paramEWKBSource.bigEndian = true;
/*     */         break;
/*     */       case 1:
/* 349 */         paramEWKBSource.bigEndian = false;
/*     */         break;
/*     */       default:
/* 352 */         throw new IllegalArgumentException();
/*     */     } 
/*     */     
/* 355 */     int i = paramEWKBSource.readInt();
/*     */     
/* 357 */     boolean bool1 = ((i & Integer.MIN_VALUE) != 0) ? true : false;
/* 358 */     boolean bool2 = ((i & 0x40000000) != 0) ? true : false;
/* 359 */     boolean bool3 = ((i & 0x20000000) != 0) ? paramEWKBSource.readInt() : false;
/*     */     
/* 361 */     if (paramInt == 0) {
/* 362 */       paramTarget.init(bool3);
/*     */     }
/*     */     
/* 365 */     i &= 0xFFFF;
/* 366 */     switch (i / 1000) {
/*     */       case 1:
/* 368 */         bool1 = true;
/*     */         break;
/*     */       case 3:
/* 371 */         bool1 = true;
/*     */       
/*     */       case 2:
/* 374 */         bool2 = true; break;
/*     */     } 
/* 376 */     paramTarget.dimensionSystem((bool1 ? 1 : 0) | (bool2 ? 2 : 0));
/* 377 */     i %= 1000;
/* 378 */     switch (i) {
/*     */       case 1:
/* 380 */         if (paramInt != 0 && paramInt != 4 && paramInt != 7) {
/* 381 */           throw new IllegalArgumentException();
/*     */         }
/* 383 */         paramTarget.startPoint();
/* 384 */         addCoordinate(paramEWKBSource, paramTarget, bool1, bool2, 0, 1);
/*     */         break;
/*     */       case 2:
/* 387 */         if (paramInt != 0 && paramInt != 5 && paramInt != 7) {
/* 388 */           throw new IllegalArgumentException();
/*     */         }
/* 390 */         j = paramEWKBSource.readInt();
/* 391 */         if (j < 0 || j == 1) {
/* 392 */           throw new IllegalArgumentException();
/*     */         }
/* 394 */         paramTarget.startLineString(j);
/* 395 */         for (k = 0; k < j; k++) {
/* 396 */           addCoordinate(paramEWKBSource, paramTarget, bool1, bool2, k, j);
/*     */         }
/*     */         break;
/*     */       
/*     */       case 3:
/* 401 */         if (paramInt != 0 && paramInt != 6 && paramInt != 7) {
/* 402 */           throw new IllegalArgumentException();
/*     */         }
/* 404 */         j = paramEWKBSource.readInt();
/* 405 */         if (j == 0) {
/* 406 */           paramTarget.startPolygon(0, 0); break;
/*     */         } 
/* 408 */         if (j < 0) {
/* 409 */           throw new IllegalArgumentException();
/*     */         }
/* 411 */         j--;
/* 412 */         k = paramEWKBSource.readInt();
/*     */         
/* 414 */         if (k < 0 || (k >= 1 && k <= 3)) {
/* 415 */           throw new IllegalArgumentException();
/*     */         }
/* 417 */         if (k == 0 && j > 0) {
/* 418 */           throw new IllegalArgumentException();
/*     */         }
/* 420 */         paramTarget.startPolygon(j, k);
/* 421 */         if (k > 0) {
/* 422 */           addRing(paramEWKBSource, paramTarget, bool1, bool2, k);
/* 423 */           for (byte b = 0; b < j; b++) {
/* 424 */             k = paramEWKBSource.readInt();
/*     */             
/* 426 */             if (k < 0 || (k >= 1 && k <= 3)) {
/* 427 */               throw new IllegalArgumentException();
/*     */             }
/* 429 */             paramTarget.startPolygonInner(k);
/* 430 */             addRing(paramEWKBSource, paramTarget, bool1, bool2, k);
/*     */           } 
/* 432 */           paramTarget.endNonEmptyPolygon();
/*     */         } 
/*     */         break;
/*     */       
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/* 440 */         if (paramInt != 0 && paramInt != 7) {
/* 441 */           throw new IllegalArgumentException();
/*     */         }
/* 443 */         j = paramEWKBSource.readInt();
/* 444 */         if (j < 0) {
/* 445 */           throw new IllegalArgumentException();
/*     */         }
/* 447 */         paramTarget.startCollection(i, j);
/* 448 */         for (k = 0; k < j; k++) {
/* 449 */           GeometryUtils.Target target = paramTarget.startCollectionItem(k, j);
/* 450 */           parseEWKB(paramEWKBSource, target, i);
/* 451 */           paramTarget.endCollectionItem(target, i, k, j);
/*     */         } 
/*     */         break;
/*     */       
/*     */       default:
/* 456 */         throw new IllegalArgumentException();
/*     */     } 
/* 458 */     paramTarget.endObject(i);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void addRing(EWKBSource paramEWKBSource, GeometryUtils.Target paramTarget, boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
/* 463 */     if (paramInt >= 4) {
/* 464 */       double d1 = paramEWKBSource.readCoordinate(), d2 = paramEWKBSource.readCoordinate();
/* 465 */       paramTarget.addCoordinate(d1, d2, paramBoolean1 ? paramEWKBSource
/* 466 */           .readCoordinate() : Double.NaN, paramBoolean2 ? paramEWKBSource.readCoordinate() : Double.NaN, 0, paramInt);
/*     */       
/* 468 */       for (byte b = 1; b < paramInt - 1; b++) {
/* 469 */         addCoordinate(paramEWKBSource, paramTarget, paramBoolean1, paramBoolean2, b, paramInt);
/*     */       }
/* 471 */       double d3 = paramEWKBSource.readCoordinate(), d4 = paramEWKBSource.readCoordinate();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 476 */       if (d1 != d3 || d2 != d4) {
/* 477 */         throw new IllegalArgumentException();
/*     */       }
/* 479 */       paramTarget.addCoordinate(d3, d4, paramBoolean1 ? paramEWKBSource
/* 480 */           .readCoordinate() : Double.NaN, paramBoolean2 ? paramEWKBSource.readCoordinate() : Double.NaN, paramInt - 1, paramInt);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addCoordinate(EWKBSource paramEWKBSource, GeometryUtils.Target paramTarget, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) {
/* 487 */     paramTarget.addCoordinate(paramEWKBSource.readCoordinate(), paramEWKBSource.readCoordinate(), paramBoolean1 ? paramEWKBSource
/* 488 */         .readCoordinate() : Double.NaN, paramBoolean2 ? paramEWKBSource.readCoordinate() : Double.NaN, paramInt1, paramInt2);
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
/*     */   public static int getDimensionSystem(byte[] paramArrayOfbyte) {
/* 500 */     EWKBSource eWKBSource = new EWKBSource(paramArrayOfbyte);
/*     */     
/* 502 */     switch (eWKBSource.readByte()) {
/*     */       case 0:
/* 504 */         eWKBSource.bigEndian = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 512 */         return type2dimensionSystem(eWKBSource.readInt());case 1: eWKBSource.bigEndian = false; return type2dimensionSystem(eWKBSource.readInt());
/*     */     } 
/*     */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] envelope2wkb(double[] paramArrayOfdouble) {
/*     */     byte[] arrayOfByte;
/* 523 */     if (paramArrayOfdouble == null) {
/* 524 */       return null;
/*     */     }
/*     */     
/* 527 */     double d1 = paramArrayOfdouble[0], d2 = paramArrayOfdouble[1], d3 = paramArrayOfdouble[2], d4 = paramArrayOfdouble[3];
/* 528 */     if (d1 == d2 && d3 == d4) {
/* 529 */       arrayOfByte = new byte[21];
/* 530 */       arrayOfByte[4] = 1;
/* 531 */       Bits.writeDouble(arrayOfByte, 5, d1);
/* 532 */       Bits.writeDouble(arrayOfByte, 13, d3);
/* 533 */     } else if (d1 == d2 || d3 == d4) {
/* 534 */       arrayOfByte = new byte[41];
/* 535 */       arrayOfByte[4] = 2;
/* 536 */       arrayOfByte[8] = 2;
/* 537 */       Bits.writeDouble(arrayOfByte, 9, d1);
/* 538 */       Bits.writeDouble(arrayOfByte, 17, d3);
/* 539 */       Bits.writeDouble(arrayOfByte, 25, d2);
/* 540 */       Bits.writeDouble(arrayOfByte, 33, d4);
/*     */     } else {
/* 542 */       arrayOfByte = new byte[93];
/* 543 */       arrayOfByte[4] = 3;
/* 544 */       arrayOfByte[8] = 1;
/* 545 */       arrayOfByte[12] = 5;
/* 546 */       Bits.writeDouble(arrayOfByte, 13, d1);
/* 547 */       Bits.writeDouble(arrayOfByte, 21, d3);
/* 548 */       Bits.writeDouble(arrayOfByte, 29, d1);
/* 549 */       Bits.writeDouble(arrayOfByte, 37, d4);
/* 550 */       Bits.writeDouble(arrayOfByte, 45, d2);
/* 551 */       Bits.writeDouble(arrayOfByte, 53, d4);
/* 552 */       Bits.writeDouble(arrayOfByte, 61, d2);
/* 553 */       Bits.writeDouble(arrayOfByte, 69, d3);
/* 554 */       Bits.writeDouble(arrayOfByte, 77, d1);
/* 555 */       Bits.writeDouble(arrayOfByte, 85, d3);
/*     */     } 
/* 557 */     return arrayOfByte;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\geometry\EWKBUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */