/*     */ package org.h2.util.geometry;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ public final class EWKTUtils
/*     */ {
/*  47 */   static final String[] TYPES = new String[] { "POINT", "LINESTRING", "POLYGON", "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON", "GEOMETRYCOLLECTION" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   private static final String[] DIMENSION_SYSTEMS = new String[] { "XY", "Z", "M", "ZM" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class EWKTTarget
/*     */     extends GeometryUtils.Target
/*     */   {
/*     */     private final StringBuilder output;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final int dimensionSystem;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int type;
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean inMulti;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public EWKTTarget(StringBuilder param1StringBuilder, int param1Int) {
/*  89 */       this.output = param1StringBuilder;
/*  90 */       this.dimensionSystem = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void init(int param1Int) {
/*  95 */       if (param1Int != 0) {
/*  96 */         this.output.append("SRID=").append(param1Int).append(';');
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPoint() {
/* 102 */       writeHeader(1);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startLineString(int param1Int) {
/* 107 */       writeHeader(2);
/* 108 */       if (param1Int == 0) {
/* 109 */         this.output.append("EMPTY");
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygon(int param1Int1, int param1Int2) {
/* 115 */       writeHeader(3);
/* 116 */       if (param1Int2 == 0) {
/* 117 */         this.output.append("EMPTY");
/*     */       } else {
/* 119 */         this.output.append('(');
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygonInner(int param1Int) {
/* 125 */       this.output.append((param1Int > 0) ? ", " : ", EMPTY");
/*     */     }
/*     */ 
/*     */     
/*     */     protected void endNonEmptyPolygon() {
/* 130 */       this.output.append(')');
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startCollection(int param1Int1, int param1Int2) {
/* 135 */       writeHeader(param1Int1);
/* 136 */       if (param1Int2 == 0) {
/* 137 */         this.output.append("EMPTY");
/*     */       }
/* 139 */       if (param1Int1 != 7) {
/* 140 */         this.inMulti = true;
/*     */       }
/*     */     }
/*     */     
/*     */     private void writeHeader(int param1Int) {
/* 145 */       this.type = param1Int;
/* 146 */       if (this.inMulti) {
/*     */         return;
/*     */       }
/* 149 */       this.output.append(EWKTUtils.TYPES[param1Int - 1]);
/* 150 */       switch (this.dimensionSystem) {
/*     */         case 1:
/* 152 */           this.output.append(" Z");
/*     */           break;
/*     */         case 2:
/* 155 */           this.output.append(" M");
/*     */           break;
/*     */         case 3:
/* 158 */           this.output.append(" ZM"); break;
/*     */       } 
/* 160 */       this.output.append(' ');
/*     */     }
/*     */ 
/*     */     
/*     */     protected GeometryUtils.Target startCollectionItem(int param1Int1, int param1Int2) {
/* 165 */       if (param1Int1 == 0) {
/* 166 */         this.output.append('(');
/*     */       } else {
/* 168 */         this.output.append(", ");
/*     */       } 
/* 170 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void endCollectionItem(GeometryUtils.Target param1Target, int param1Int1, int param1Int2, int param1Int3) {
/* 175 */       if (param1Int2 + 1 == param1Int3) {
/* 176 */         this.output.append(')');
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected void endObject(int param1Int) {
/* 182 */       switch (param1Int) {
/*     */         case 4:
/*     */         case 5:
/*     */         case 6:
/* 186 */           this.inMulti = false;
/*     */           break;
/*     */       } 
/*     */     }
/*     */     
/*     */     protected void addCoordinate(double param1Double1, double param1Double2, double param1Double3, double param1Double4, int param1Int1, int param1Int2) {
/* 192 */       if (this.type == 1 && Double.isNaN(param1Double1) && Double.isNaN(param1Double2) && Double.isNaN(param1Double3) && Double.isNaN(param1Double4)) {
/* 193 */         this.output.append("EMPTY");
/*     */         return;
/*     */       } 
/* 196 */       if (param1Int1 == 0) {
/* 197 */         this.output.append('(');
/*     */       } else {
/* 199 */         this.output.append(", ");
/*     */       } 
/* 201 */       writeDouble(param1Double1);
/* 202 */       this.output.append(' ');
/* 203 */       writeDouble(param1Double2);
/* 204 */       if ((this.dimensionSystem & 0x1) != 0) {
/* 205 */         this.output.append(' ');
/* 206 */         writeDouble(param1Double3);
/*     */       } 
/* 208 */       if ((this.dimensionSystem & 0x2) != 0) {
/* 209 */         this.output.append(' ');
/* 210 */         writeDouble(param1Double4);
/*     */       } 
/* 212 */       if (param1Int1 + 1 == param1Int2) {
/* 213 */         this.output.append(')');
/*     */       }
/*     */     }
/*     */     
/*     */     private void writeDouble(double param1Double) {
/* 218 */       String str = Double.toString(GeometryUtils.checkFinite(param1Double));
/* 219 */       if (str.endsWith(".0")) {
/* 220 */         this.output.append(str, 0, str.length() - 2);
/*     */       } else {
/* 222 */         int i = str.indexOf(".0E");
/* 223 */         if (i < 0) {
/* 224 */           this.output.append(str);
/*     */         } else {
/* 226 */           this.output.append(str, 0, i).append(str, i + 2, str.length());
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class EWKTSource
/*     */   {
/*     */     private final String ewkt;
/*     */     
/*     */     private int offset;
/*     */ 
/*     */     
/*     */     EWKTSource(String param1String) {
/* 242 */       this.ewkt = param1String;
/*     */     }
/*     */     int readSRID() {
/*     */       boolean bool;
/* 246 */       skipWS();
/*     */       
/* 248 */       if (this.ewkt.regionMatches(true, this.offset, "SRID=", 0, 5)) {
/* 249 */         this.offset += 5;
/* 250 */         int i = this.ewkt.indexOf(';', 5);
/* 251 */         if (i < 0) {
/* 252 */           throw new IllegalArgumentException();
/*     */         }
/* 254 */         int j = i;
/* 255 */         while (this.ewkt.charAt(j - 1) <= ' ') {
/* 256 */           j--;
/*     */         }
/* 258 */         bool = Integer.parseInt(StringUtils.trimSubstring(this.ewkt, this.offset, j));
/* 259 */         this.offset = i + 1;
/*     */       } else {
/* 261 */         bool = false;
/*     */       } 
/* 263 */       return bool;
/*     */     }
/*     */     
/*     */     void read(char param1Char) {
/* 267 */       skipWS();
/* 268 */       int i = this.ewkt.length();
/* 269 */       if (this.offset >= i) {
/* 270 */         throw new IllegalArgumentException();
/*     */       }
/* 272 */       if (this.ewkt.charAt(this.offset) != param1Char) {
/* 273 */         throw new IllegalArgumentException();
/*     */       }
/* 275 */       this.offset++;
/*     */     }
/*     */     
/*     */     int readType() {
/* 279 */       skipWS();
/* 280 */       int i = this.ewkt.length();
/* 281 */       if (this.offset >= i) {
/* 282 */         throw new IllegalArgumentException();
/*     */       }
/* 284 */       int j = 0;
/* 285 */       char c = this.ewkt.charAt(this.offset);
/* 286 */       switch (c) {
/*     */         case 'P':
/*     */         case 'p':
/* 289 */           j = match("POINT", 1);
/* 290 */           if (j == 0) {
/* 291 */             j = match("POLYGON", 3);
/*     */           }
/*     */           break;
/*     */         case 'L':
/*     */         case 'l':
/* 296 */           j = match("LINESTRING", 2);
/*     */           break;
/*     */         case 'M':
/*     */         case 'm':
/* 300 */           if (match("MULTI", 1) != 0) {
/* 301 */             j = match("POINT", 4);
/* 302 */             if (j == 0) {
/* 303 */               j = match("POLYGON", 6);
/* 304 */               if (j == 0) {
/* 305 */                 j = match("LINESTRING", 5);
/*     */               }
/*     */             } 
/*     */           } 
/*     */           break;
/*     */         case 'G':
/*     */         case 'g':
/* 312 */           j = match("GEOMETRYCOLLECTION", 7);
/*     */           break;
/*     */       } 
/* 315 */       if (j == 0) {
/* 316 */         throw new IllegalArgumentException();
/*     */       }
/* 318 */       return j;
/*     */     }
/*     */     
/*     */     int readDimensionSystem() {
/* 322 */       int i = this.offset;
/* 323 */       skipWS();
/* 324 */       int j = this.ewkt.length();
/* 325 */       if (this.offset >= j) {
/* 326 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 329 */       char c = this.ewkt.charAt(this.offset);
/* 330 */       switch (c)
/*     */       { case 'M':
/*     */         case 'm':
/* 333 */           b = 2;
/* 334 */           this.offset++;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 358 */           checkStringEnd(j);
/* 359 */           return b;case 'Z': case 'z': this.offset++; if (this.offset >= j) { b = 1; } else { c = this.ewkt.charAt(this.offset); if (c == 'M' || c == 'm') { this.offset++; b = 3; } else { b = 1; }  }  checkStringEnd(j); return b; }  byte b = 0; if (i != this.offset) return b;  checkStringEnd(j); return b;
/*     */     }
/*     */     
/*     */     boolean readEmpty() {
/* 363 */       skipWS();
/* 364 */       int i = this.ewkt.length();
/* 365 */       if (this.offset >= i) {
/* 366 */         throw new IllegalArgumentException();
/*     */       }
/* 368 */       if (this.ewkt.charAt(this.offset) == '(') {
/* 369 */         this.offset++;
/* 370 */         return false;
/*     */       } 
/* 372 */       if (match("EMPTY", 1) != 0) {
/* 373 */         checkStringEnd(i);
/* 374 */         return true;
/*     */       } 
/* 376 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*     */     private int match(String param1String, int param1Int) {
/* 380 */       int i = param1String.length();
/* 381 */       if (this.offset <= this.ewkt.length() - i && this.ewkt.regionMatches(true, this.offset, param1String, 0, i)) {
/* 382 */         this.offset += i;
/*     */       } else {
/* 384 */         param1Int = 0;
/*     */       } 
/* 386 */       return param1Int;
/*     */     }
/*     */     
/*     */     private void checkStringEnd(int param1Int) {
/* 390 */       if (this.offset < param1Int) {
/* 391 */         char c = this.ewkt.charAt(this.offset);
/* 392 */         if (c > ' ' && c != '(' && c != ')' && c != ',') {
/* 393 */           throw new IllegalArgumentException();
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean hasCoordinate() {
/* 399 */       skipWS();
/* 400 */       if (this.offset >= this.ewkt.length()) {
/* 401 */         return false;
/*     */       }
/* 403 */       return isNumberStart(this.ewkt.charAt(this.offset));
/*     */     }
/*     */     
/*     */     public double readCoordinate() {
/* 407 */       skipWS();
/* 408 */       int i = this.ewkt.length();
/* 409 */       if (this.offset >= i) {
/* 410 */         throw new IllegalArgumentException();
/*     */       }
/* 412 */       char c = this.ewkt.charAt(this.offset);
/* 413 */       if (!isNumberStart(c)) {
/* 414 */         throw new IllegalArgumentException();
/*     */       }
/* 416 */       int j = this.offset++;
/* 417 */       while (this.offset < i && isNumberPart(c = this.ewkt.charAt(this.offset))) {
/* 418 */         this.offset++;
/*     */       }
/* 420 */       if (this.offset < i && 
/* 421 */         c > ' ' && c != ')' && c != ',') {
/* 422 */         throw new IllegalArgumentException();
/*     */       }
/*     */       
/* 425 */       Double double_ = Double.valueOf(Double.parseDouble(this.ewkt.substring(j, this.offset)));
/* 426 */       return (double_.doubleValue() == 0.0D) ? 0.0D : double_.doubleValue();
/*     */     }
/*     */     
/*     */     private static boolean isNumberStart(char param1Char) {
/* 430 */       if (param1Char >= '0' && param1Char <= '9') {
/* 431 */         return true;
/*     */       }
/* 433 */       switch (param1Char) {
/*     */         case '+':
/*     */         case '-':
/*     */         case '.':
/* 437 */           return true;
/*     */       } 
/* 439 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     private static boolean isNumberPart(char param1Char) {
/* 444 */       if (param1Char >= '0' && param1Char <= '9') {
/* 445 */         return true;
/*     */       }
/* 447 */       switch (param1Char) {
/*     */         case '+':
/*     */         case '-':
/*     */         case '.':
/*     */         case 'E':
/*     */         case 'e':
/* 453 */           return true;
/*     */       } 
/* 455 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasMoreCoordinates() {
/* 460 */       skipWS();
/* 461 */       if (this.offset >= this.ewkt.length()) {
/* 462 */         throw new IllegalArgumentException();
/*     */       }
/* 464 */       switch (this.ewkt.charAt(this.offset)) {
/*     */         case ',':
/* 466 */           this.offset++;
/* 467 */           return true;
/*     */         case ')':
/* 469 */           this.offset++;
/* 470 */           return false;
/*     */       } 
/* 472 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean hasData() {
/* 477 */       skipWS();
/* 478 */       return (this.offset < this.ewkt.length());
/*     */     }
/*     */     
/*     */     int getItemCount() {
/* 482 */       byte b1 = 1;
/* 483 */       int i = this.offset; byte b2 = 0; int j = this.ewkt.length();
/* 484 */       while (i < j) {
/* 485 */         switch (this.ewkt.charAt(i++)) {
/*     */           case ',':
/* 487 */             if (!b2) {
/* 488 */               b1++;
/*     */             }
/*     */           
/*     */           case '(':
/* 492 */             b2++;
/*     */           
/*     */           case ')':
/* 495 */             if (--b2 < 0) {
/* 496 */               return b1;
/*     */             }
/*     */         } 
/*     */       } 
/* 500 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*     */     private void skipWS() {
/* 504 */       for (int i = this.ewkt.length(); this.offset < i && this.ewkt.charAt(this.offset) <= ' '; this.offset++);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 510 */       return (new StringBuilder(this.ewkt.length() + 3)).append(this.ewkt, 0, this.offset).append("<*>")
/* 511 */         .append(this.ewkt, this.offset, this.ewkt.length()).toString();
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
/*     */   public static String ewkb2ewkt(byte[] paramArrayOfbyte) {
/* 524 */     return ewkb2ewkt(paramArrayOfbyte, EWKBUtils.getDimensionSystem(paramArrayOfbyte));
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
/*     */   public static String ewkb2ewkt(byte[] paramArrayOfbyte, int paramInt) {
/* 537 */     StringBuilder stringBuilder = new StringBuilder();
/* 538 */     EWKBUtils.parseEWKB(paramArrayOfbyte, new EWKTTarget(stringBuilder, paramInt));
/* 539 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] ewkt2ewkb(String paramString) {
/* 550 */     return ewkt2ewkb(paramString, getDimensionSystem(paramString));
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
/*     */   public static byte[] ewkt2ewkb(String paramString, int paramInt) {
/* 563 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 564 */     EWKBUtils.EWKBTarget eWKBTarget = new EWKBUtils.EWKBTarget(byteArrayOutputStream, paramInt);
/* 565 */     parseEWKT(paramString, eWKBTarget);
/* 566 */     return byteArrayOutputStream.toByteArray();
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
/*     */   public static void parseEWKT(String paramString, GeometryUtils.Target paramTarget) {
/* 578 */     parseEWKT(new EWKTSource(paramString), paramTarget, 0, 0);
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
/*     */   public static int parseGeometryType(String paramString) {
/* 592 */     EWKTSource eWKTSource = new EWKTSource(paramString);
/* 593 */     int i = eWKTSource.readType();
/* 594 */     int j = 0;
/* 595 */     if (eWKTSource.hasData()) {
/* 596 */       j = eWKTSource.readDimensionSystem();
/* 597 */       if (eWKTSource.hasData()) {
/* 598 */         throw new IllegalArgumentException();
/*     */       }
/*     */     } 
/* 601 */     return j * 1000 + i;
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
/*     */   public static int parseDimensionSystem(String paramString) {
/* 617 */     EWKTSource eWKTSource = new EWKTSource(paramString);
/* 618 */     int i = eWKTSource.readDimensionSystem();
/* 619 */     if (eWKTSource.hasData() || i == 0) {
/* 620 */       throw new IllegalArgumentException();
/*     */     }
/* 622 */     return i;
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
/*     */   public static StringBuilder formatGeometryTypeAndDimensionSystem(StringBuilder paramStringBuilder, int paramInt) {
/* 637 */     int i = paramInt % 1000, j = paramInt / 1000;
/* 638 */     if (i < 1 || i > 7 || j < 0 || j > 3) {
/* 639 */       throw new IllegalArgumentException();
/*     */     }
/* 641 */     paramStringBuilder.append(TYPES[i - 1]);
/* 642 */     if (j != 0) {
/* 643 */       paramStringBuilder.append(' ').append(DIMENSION_SYSTEMS[j]);
/*     */     }
/* 645 */     return paramStringBuilder;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void parseEWKT(EWKTSource paramEWKTSource, GeometryUtils.Target paramTarget, int paramInt1, int paramInt2) {
/*     */     // Byte code:
/*     */     //   0: iload_2
/*     */     //   1: ifne -> 12
/*     */     //   4: aload_1
/*     */     //   5: aload_0
/*     */     //   6: invokevirtual readSRID : ()I
/*     */     //   9: invokevirtual init : (I)V
/*     */     //   12: iload_2
/*     */     //   13: tableswitch default -> 40, 4 -> 54, 5 -> 60, 6 -> 66
/*     */     //   40: aload_0
/*     */     //   41: invokevirtual readType : ()I
/*     */     //   44: istore #4
/*     */     //   46: aload_0
/*     */     //   47: invokevirtual readDimensionSystem : ()I
/*     */     //   50: istore_3
/*     */     //   51: goto -> 69
/*     */     //   54: iconst_1
/*     */     //   55: istore #4
/*     */     //   57: goto -> 69
/*     */     //   60: iconst_2
/*     */     //   61: istore #4
/*     */     //   63: goto -> 69
/*     */     //   66: iconst_3
/*     */     //   67: istore #4
/*     */     //   69: aload_1
/*     */     //   70: iload_3
/*     */     //   71: invokevirtual dimensionSystem : (I)V
/*     */     //   74: iload #4
/*     */     //   76: tableswitch default -> 602, 1 -> 120, 2 -> 196, 3 -> 352, 4 -> 578, 5 -> 578, 6 -> 578, 7 -> 590
/*     */     //   120: iload_2
/*     */     //   121: ifeq -> 143
/*     */     //   124: iload_2
/*     */     //   125: iconst_4
/*     */     //   126: if_icmpeq -> 143
/*     */     //   129: iload_2
/*     */     //   130: bipush #7
/*     */     //   132: if_icmpeq -> 143
/*     */     //   135: new java/lang/IllegalArgumentException
/*     */     //   138: dup
/*     */     //   139: invokespecial <init> : ()V
/*     */     //   142: athrow
/*     */     //   143: aload_0
/*     */     //   144: invokevirtual readEmpty : ()Z
/*     */     //   147: istore #5
/*     */     //   149: aload_1
/*     */     //   150: invokevirtual startPoint : ()V
/*     */     //   153: iload #5
/*     */     //   155: ifeq -> 179
/*     */     //   158: aload_1
/*     */     //   159: ldc2_w NaN
/*     */     //   162: ldc2_w NaN
/*     */     //   165: ldc2_w NaN
/*     */     //   168: ldc2_w NaN
/*     */     //   171: iconst_0
/*     */     //   172: iconst_1
/*     */     //   173: invokevirtual addCoordinate : (DDDDII)V
/*     */     //   176: goto -> 610
/*     */     //   179: aload_0
/*     */     //   180: aload_1
/*     */     //   181: iload_3
/*     */     //   182: iconst_0
/*     */     //   183: iconst_1
/*     */     //   184: invokestatic addCoordinate : (Lorg/h2/util/geometry/EWKTUtils$EWKTSource;Lorg/h2/util/geometry/GeometryUtils$Target;III)V
/*     */     //   187: aload_0
/*     */     //   188: bipush #41
/*     */     //   190: invokevirtual read : (C)V
/*     */     //   193: goto -> 610
/*     */     //   196: iload_2
/*     */     //   197: ifeq -> 219
/*     */     //   200: iload_2
/*     */     //   201: iconst_5
/*     */     //   202: if_icmpeq -> 219
/*     */     //   205: iload_2
/*     */     //   206: bipush #7
/*     */     //   208: if_icmpeq -> 219
/*     */     //   211: new java/lang/IllegalArgumentException
/*     */     //   214: dup
/*     */     //   215: invokespecial <init> : ()V
/*     */     //   218: athrow
/*     */     //   219: aload_0
/*     */     //   220: invokevirtual readEmpty : ()Z
/*     */     //   223: istore #5
/*     */     //   225: iload #5
/*     */     //   227: ifeq -> 238
/*     */     //   230: aload_1
/*     */     //   231: iconst_0
/*     */     //   232: invokevirtual startLineString : (I)V
/*     */     //   235: goto -> 610
/*     */     //   238: new java/util/ArrayList
/*     */     //   241: dup
/*     */     //   242: invokespecial <init> : ()V
/*     */     //   245: astore #6
/*     */     //   247: aload #6
/*     */     //   249: aload_0
/*     */     //   250: iload_3
/*     */     //   251: invokestatic readCoordinate : (Lorg/h2/util/geometry/EWKTUtils$EWKTSource;I)[D
/*     */     //   254: invokevirtual add : (Ljava/lang/Object;)Z
/*     */     //   257: pop
/*     */     //   258: aload_0
/*     */     //   259: invokevirtual hasMoreCoordinates : ()Z
/*     */     //   262: ifne -> 247
/*     */     //   265: aload #6
/*     */     //   267: invokevirtual size : ()I
/*     */     //   270: istore #7
/*     */     //   272: iload #7
/*     */     //   274: iflt -> 283
/*     */     //   277: iload #7
/*     */     //   279: iconst_1
/*     */     //   280: if_icmpne -> 291
/*     */     //   283: new java/lang/IllegalArgumentException
/*     */     //   286: dup
/*     */     //   287: invokespecial <init> : ()V
/*     */     //   290: athrow
/*     */     //   291: aload_1
/*     */     //   292: iload #7
/*     */     //   294: invokevirtual startLineString : (I)V
/*     */     //   297: iconst_0
/*     */     //   298: istore #8
/*     */     //   300: iload #8
/*     */     //   302: iload #7
/*     */     //   304: if_icmpge -> 349
/*     */     //   307: aload #6
/*     */     //   309: iload #8
/*     */     //   311: invokevirtual get : (I)Ljava/lang/Object;
/*     */     //   314: checkcast [D
/*     */     //   317: astore #9
/*     */     //   319: aload_1
/*     */     //   320: aload #9
/*     */     //   322: iconst_0
/*     */     //   323: daload
/*     */     //   324: aload #9
/*     */     //   326: iconst_1
/*     */     //   327: daload
/*     */     //   328: aload #9
/*     */     //   330: iconst_2
/*     */     //   331: daload
/*     */     //   332: aload #9
/*     */     //   334: iconst_3
/*     */     //   335: daload
/*     */     //   336: iload #8
/*     */     //   338: iload #7
/*     */     //   340: invokevirtual addCoordinate : (DDDDII)V
/*     */     //   343: iinc #8, 1
/*     */     //   346: goto -> 300
/*     */     //   349: goto -> 610
/*     */     //   352: iload_2
/*     */     //   353: ifeq -> 376
/*     */     //   356: iload_2
/*     */     //   357: bipush #6
/*     */     //   359: if_icmpeq -> 376
/*     */     //   362: iload_2
/*     */     //   363: bipush #7
/*     */     //   365: if_icmpeq -> 376
/*     */     //   368: new java/lang/IllegalArgumentException
/*     */     //   371: dup
/*     */     //   372: invokespecial <init> : ()V
/*     */     //   375: athrow
/*     */     //   376: aload_0
/*     */     //   377: invokevirtual readEmpty : ()Z
/*     */     //   380: istore #5
/*     */     //   382: iload #5
/*     */     //   384: ifeq -> 396
/*     */     //   387: aload_1
/*     */     //   388: iconst_0
/*     */     //   389: iconst_0
/*     */     //   390: invokevirtual startPolygon : (II)V
/*     */     //   393: goto -> 610
/*     */     //   396: aload_0
/*     */     //   397: iload_3
/*     */     //   398: invokestatic readRing : (Lorg/h2/util/geometry/EWKTUtils$EWKTSource;I)Ljava/util/ArrayList;
/*     */     //   401: astore #6
/*     */     //   403: new java/util/ArrayList
/*     */     //   406: dup
/*     */     //   407: invokespecial <init> : ()V
/*     */     //   410: astore #7
/*     */     //   412: aload_0
/*     */     //   413: invokevirtual hasMoreCoordinates : ()Z
/*     */     //   416: ifeq -> 433
/*     */     //   419: aload #7
/*     */     //   421: aload_0
/*     */     //   422: iload_3
/*     */     //   423: invokestatic readRing : (Lorg/h2/util/geometry/EWKTUtils$EWKTSource;I)Ljava/util/ArrayList;
/*     */     //   426: invokevirtual add : (Ljava/lang/Object;)Z
/*     */     //   429: pop
/*     */     //   430: goto -> 412
/*     */     //   433: aload #7
/*     */     //   435: invokevirtual size : ()I
/*     */     //   438: istore #8
/*     */     //   440: aload #6
/*     */     //   442: invokevirtual size : ()I
/*     */     //   445: istore #9
/*     */     //   447: iload #9
/*     */     //   449: iconst_1
/*     */     //   450: if_icmplt -> 467
/*     */     //   453: iload #9
/*     */     //   455: iconst_3
/*     */     //   456: if_icmpgt -> 467
/*     */     //   459: new java/lang/IllegalArgumentException
/*     */     //   462: dup
/*     */     //   463: invokespecial <init> : ()V
/*     */     //   466: athrow
/*     */     //   467: iload #9
/*     */     //   469: ifne -> 485
/*     */     //   472: iload #8
/*     */     //   474: ifle -> 485
/*     */     //   477: new java/lang/IllegalArgumentException
/*     */     //   480: dup
/*     */     //   481: invokespecial <init> : ()V
/*     */     //   484: athrow
/*     */     //   485: aload_1
/*     */     //   486: iload #8
/*     */     //   488: iload #9
/*     */     //   490: invokevirtual startPolygon : (II)V
/*     */     //   493: iload #9
/*     */     //   495: ifle -> 575
/*     */     //   498: aload #6
/*     */     //   500: aload_1
/*     */     //   501: invokestatic addRing : (Ljava/util/ArrayList;Lorg/h2/util/geometry/GeometryUtils$Target;)V
/*     */     //   504: iconst_0
/*     */     //   505: istore #10
/*     */     //   507: iload #10
/*     */     //   509: iload #8
/*     */     //   511: if_icmpge -> 571
/*     */     //   514: aload #7
/*     */     //   516: iload #10
/*     */     //   518: invokevirtual get : (I)Ljava/lang/Object;
/*     */     //   521: checkcast java/util/ArrayList
/*     */     //   524: astore #11
/*     */     //   526: aload #11
/*     */     //   528: invokevirtual size : ()I
/*     */     //   531: istore #9
/*     */     //   533: iload #9
/*     */     //   535: iconst_1
/*     */     //   536: if_icmplt -> 553
/*     */     //   539: iload #9
/*     */     //   541: iconst_3
/*     */     //   542: if_icmpgt -> 553
/*     */     //   545: new java/lang/IllegalArgumentException
/*     */     //   548: dup
/*     */     //   549: invokespecial <init> : ()V
/*     */     //   552: athrow
/*     */     //   553: aload_1
/*     */     //   554: iload #9
/*     */     //   556: invokevirtual startPolygonInner : (I)V
/*     */     //   559: aload #11
/*     */     //   561: aload_1
/*     */     //   562: invokestatic addRing : (Ljava/util/ArrayList;Lorg/h2/util/geometry/GeometryUtils$Target;)V
/*     */     //   565: iinc #10, 1
/*     */     //   568: goto -> 507
/*     */     //   571: aload_1
/*     */     //   572: invokevirtual endNonEmptyPolygon : ()V
/*     */     //   575: goto -> 610
/*     */     //   578: aload_0
/*     */     //   579: aload_1
/*     */     //   580: iload #4
/*     */     //   582: iload_2
/*     */     //   583: iload_3
/*     */     //   584: invokestatic parseCollection : (Lorg/h2/util/geometry/EWKTUtils$EWKTSource;Lorg/h2/util/geometry/GeometryUtils$Target;III)V
/*     */     //   587: goto -> 610
/*     */     //   590: aload_0
/*     */     //   591: aload_1
/*     */     //   592: bipush #7
/*     */     //   594: iload_2
/*     */     //   595: iconst_0
/*     */     //   596: invokestatic parseCollection : (Lorg/h2/util/geometry/EWKTUtils$EWKTSource;Lorg/h2/util/geometry/GeometryUtils$Target;III)V
/*     */     //   599: goto -> 610
/*     */     //   602: new java/lang/IllegalArgumentException
/*     */     //   605: dup
/*     */     //   606: invokespecial <init> : ()V
/*     */     //   609: athrow
/*     */     //   610: aload_1
/*     */     //   611: iload #4
/*     */     //   613: invokevirtual endObject : (I)V
/*     */     //   616: iload_2
/*     */     //   617: ifne -> 635
/*     */     //   620: aload_0
/*     */     //   621: invokevirtual hasData : ()Z
/*     */     //   624: ifeq -> 635
/*     */     //   627: new java/lang/IllegalArgumentException
/*     */     //   630: dup
/*     */     //   631: invokespecial <init> : ()V
/*     */     //   634: athrow
/*     */     //   635: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #661	-> 0
/*     */     //   #662	-> 4
/*     */     //   #665	-> 12
/*     */     //   #667	-> 40
/*     */     //   #668	-> 46
/*     */     //   #669	-> 51
/*     */     //   #672	-> 54
/*     */     //   #673	-> 57
/*     */     //   #675	-> 60
/*     */     //   #676	-> 63
/*     */     //   #678	-> 66
/*     */     //   #681	-> 69
/*     */     //   #682	-> 74
/*     */     //   #684	-> 120
/*     */     //   #685	-> 135
/*     */     //   #687	-> 143
/*     */     //   #688	-> 149
/*     */     //   #689	-> 153
/*     */     //   #690	-> 158
/*     */     //   #692	-> 179
/*     */     //   #693	-> 187
/*     */     //   #695	-> 193
/*     */     //   #698	-> 196
/*     */     //   #699	-> 211
/*     */     //   #701	-> 219
/*     */     //   #702	-> 225
/*     */     //   #703	-> 230
/*     */     //   #705	-> 238
/*     */     //   #707	-> 247
/*     */     //   #708	-> 258
/*     */     //   #709	-> 265
/*     */     //   #710	-> 272
/*     */     //   #711	-> 283
/*     */     //   #713	-> 291
/*     */     //   #714	-> 297
/*     */     //   #715	-> 307
/*     */     //   #716	-> 319
/*     */     //   #714	-> 343
/*     */     //   #719	-> 349
/*     */     //   #722	-> 352
/*     */     //   #723	-> 368
/*     */     //   #725	-> 376
/*     */     //   #726	-> 382
/*     */     //   #727	-> 387
/*     */     //   #729	-> 396
/*     */     //   #730	-> 403
/*     */     //   #731	-> 412
/*     */     //   #732	-> 419
/*     */     //   #734	-> 433
/*     */     //   #735	-> 440
/*     */     //   #737	-> 447
/*     */     //   #738	-> 459
/*     */     //   #740	-> 467
/*     */     //   #741	-> 477
/*     */     //   #743	-> 485
/*     */     //   #744	-> 493
/*     */     //   #745	-> 498
/*     */     //   #746	-> 504
/*     */     //   #747	-> 514
/*     */     //   #748	-> 526
/*     */     //   #750	-> 533
/*     */     //   #751	-> 545
/*     */     //   #753	-> 553
/*     */     //   #754	-> 559
/*     */     //   #746	-> 565
/*     */     //   #756	-> 571
/*     */     //   #759	-> 575
/*     */     //   #764	-> 578
/*     */     //   #765	-> 587
/*     */     //   #767	-> 590
/*     */     //   #768	-> 599
/*     */     //   #770	-> 602
/*     */     //   #772	-> 610
/*     */     //   #773	-> 616
/*     */     //   #774	-> 627
/*     */     //   #776	-> 635
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void parseCollection(EWKTSource paramEWKTSource, GeometryUtils.Target paramTarget, int paramInt1, int paramInt2, int paramInt3) {
/* 780 */     if (paramInt2 != 0 && paramInt2 != 7) {
/* 781 */       throw new IllegalArgumentException();
/*     */     }
/* 783 */     if (paramEWKTSource.readEmpty()) {
/* 784 */       paramTarget.startCollection(paramInt1, 0);
/*     */     }
/* 786 */     else if (paramInt1 == 4 && paramEWKTSource.hasCoordinate()) {
/* 787 */       parseMultiPointAlternative(paramEWKTSource, paramTarget, paramInt3);
/*     */     } else {
/* 789 */       int i = paramEWKTSource.getItemCount();
/* 790 */       paramTarget.startCollection(paramInt1, i);
/* 791 */       for (byte b = 0; b < i; b++) {
/* 792 */         if (b > 0) {
/* 793 */           paramEWKTSource.read(',');
/*     */         }
/* 795 */         GeometryUtils.Target target = paramTarget.startCollectionItem(b, i);
/* 796 */         parseEWKT(paramEWKTSource, target, paramInt1, paramInt3);
/* 797 */         paramTarget.endCollectionItem(target, paramInt1, b, i);
/*     */       } 
/* 799 */       paramEWKTSource.read(')');
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void parseMultiPointAlternative(EWKTSource paramEWKTSource, GeometryUtils.Target paramTarget, int paramInt) {
/* 806 */     ArrayList<double[]> arrayList = new ArrayList();
/*     */     while (true) {
/* 808 */       arrayList.add(readCoordinate(paramEWKTSource, paramInt));
/* 809 */       if (!paramEWKTSource.hasMoreCoordinates()) {
/* 810 */         int i = arrayList.size();
/* 811 */         paramTarget.startCollection(4, i);
/* 812 */         for (byte b = 0; b < arrayList.size(); b++) {
/* 813 */           GeometryUtils.Target target = paramTarget.startCollectionItem(b, i);
/* 814 */           paramTarget.startPoint();
/* 815 */           double[] arrayOfDouble = arrayList.get(b);
/* 816 */           paramTarget.addCoordinate(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3], 0, 1);
/* 817 */           paramTarget.endCollectionItem(target, 4, b, i);
/*     */         } 
/*     */         return;
/*     */       } 
/*     */     }  } private static ArrayList<double[]> readRing(EWKTSource paramEWKTSource, int paramInt) {
/* 822 */     if (paramEWKTSource.readEmpty()) {
/* 823 */       return (ArrayList)new ArrayList<>(0);
/*     */     }
/* 825 */     ArrayList<double[]> arrayList = new ArrayList();
/* 826 */     double[] arrayOfDouble = readCoordinate(paramEWKTSource, paramInt);
/* 827 */     double d1 = arrayOfDouble[0], d2 = arrayOfDouble[1];
/* 828 */     arrayList.add(arrayOfDouble);
/* 829 */     while (paramEWKTSource.hasMoreCoordinates()) {
/* 830 */       arrayList.add(readCoordinate(paramEWKTSource, paramInt));
/*     */     }
/* 832 */     int i = arrayList.size();
/* 833 */     if (i < 4) {
/* 834 */       throw new IllegalArgumentException();
/*     */     }
/* 836 */     arrayOfDouble = arrayList.get(i - 1);
/* 837 */     double d3 = arrayOfDouble[0], d4 = arrayOfDouble[1];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 842 */     if (d1 != d3 || d2 != d4) {
/* 843 */       throw new IllegalArgumentException();
/*     */     }
/* 845 */     return arrayList;
/*     */   } private static void addRing(ArrayList<double[]> paramArrayList, GeometryUtils.Target paramTarget) {
/*     */     byte b;
/*     */     int i;
/* 849 */     for (b = 0, i = paramArrayList.size(); b < i; b++) {
/* 850 */       double[] arrayOfDouble = paramArrayList.get(b);
/* 851 */       paramTarget.addCoordinate(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3], b, i);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void addCoordinate(EWKTSource paramEWKTSource, GeometryUtils.Target paramTarget, int paramInt1, int paramInt2, int paramInt3) {
/* 856 */     double d1 = paramEWKTSource.readCoordinate();
/* 857 */     double d2 = paramEWKTSource.readCoordinate();
/* 858 */     double d3 = ((paramInt1 & 0x1) != 0) ? paramEWKTSource.readCoordinate() : Double.NaN;
/* 859 */     double d4 = ((paramInt1 & 0x2) != 0) ? paramEWKTSource.readCoordinate() : Double.NaN;
/* 860 */     paramTarget.addCoordinate(d1, d2, d3, d4, paramInt2, paramInt3);
/*     */   }
/*     */   
/*     */   private static double[] readCoordinate(EWKTSource paramEWKTSource, int paramInt) {
/* 864 */     double d1 = paramEWKTSource.readCoordinate();
/* 865 */     double d2 = paramEWKTSource.readCoordinate();
/* 866 */     double d3 = ((paramInt & 0x1) != 0) ? paramEWKTSource.readCoordinate() : Double.NaN;
/* 867 */     double d4 = ((paramInt & 0x2) != 0) ? paramEWKTSource.readCoordinate() : Double.NaN;
/* 868 */     return new double[] { d1, d2, d3, d4 };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getDimensionSystem(String paramString) {
/* 879 */     EWKTSource eWKTSource = new EWKTSource(paramString);
/* 880 */     eWKTSource.readSRID();
/* 881 */     eWKTSource.readType();
/* 882 */     return eWKTSource.readDimensionSystem();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\geometry\EWKTUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */