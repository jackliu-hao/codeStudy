/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.TypedValueExpression;
/*     */ import org.h2.expression.aggregate.Aggregate;
/*     */ import org.h2.expression.aggregate.AggregateType;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueBinary;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueSmallint;
/*     */ import org.h2.value.ValueTinyint;
/*     */ import org.h2.value.ValueVarbinary;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BitFunction
/*     */   extends Function1_2
/*     */ {
/*     */   public static final int BITAND = 0;
/*     */   public static final int BITOR = 1;
/*     */   public static final int BITXOR = 2;
/*     */   public static final int BITNOT = 3;
/*     */   public static final int BITNAND = 4;
/*     */   public static final int BITNOR = 5;
/*     */   public static final int BITXNOR = 6;
/*     */   public static final int BITGET = 7;
/*     */   public static final int BITCOUNT = 8;
/*     */   public static final int LSHIFT = 9;
/*     */   public static final int RSHIFT = 10;
/*     */   public static final int ULSHIFT = 11;
/*     */   public static final int URSHIFT = 12;
/*     */   public static final int ROTATELEFT = 13;
/*     */   public static final int ROTATERIGHT = 14;
/* 108 */   private static final String[] NAMES = new String[] { "BITAND", "BITOR", "BITXOR", "BITNOT", "BITNAND", "BITNOR", "BITXNOR", "BITGET", "BITCOUNT", "LSHIFT", "RSHIFT", "ULSHIFT", "URSHIFT", "ROTATELEFT", "ROTATERIGHT" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */ 
/*     */   
/*     */   public BitFunction(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/* 116 */     super(paramExpression1, paramExpression2);
/* 117 */     this.function = paramInt;
/*     */   }
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2) {
/*     */     long l;
/* 122 */     switch (this.function) {
/*     */       case 7:
/* 124 */         return (Value)bitGet(paramValue1, paramValue2);
/*     */       case 8:
/* 126 */         return (Value)bitCount(paramValue1);
/*     */       case 9:
/* 128 */         return shift(paramValue1, paramValue2.getLong(), false);
/*     */       case 10:
/* 130 */         l = paramValue2.getLong();
/* 131 */         return shift(paramValue1, (l != Long.MIN_VALUE) ? -l : Long.MAX_VALUE, false);
/*     */       
/*     */       case 11:
/* 134 */         return shift(paramValue1, paramValue2.getLong(), true);
/*     */       case 12:
/* 136 */         return shift(paramValue1, -paramValue2.getLong(), true);
/*     */       case 13:
/* 138 */         return rotate(paramValue1, paramValue2.getLong(), false);
/*     */       case 14:
/* 140 */         return rotate(paramValue1, paramValue2.getLong(), true);
/*     */     } 
/* 142 */     return getBitwise(this.function, this.type, paramValue1, paramValue2);
/*     */   }
/*     */   
/*     */   private static ValueBoolean bitGet(Value paramValue1, Value paramValue2) {
/* 146 */     long l = paramValue2.getLong();
/*     */     
/* 148 */     if (l >= 0L)
/* 149 */     { boolean bool1; byte[] arrayOfByte; int i; switch (paramValue1.getValueType())
/*     */       { case 5:
/*     */         case 6:
/* 152 */           arrayOfByte = paramValue1.getBytesNoCopy();
/* 153 */           i = (int)(l & 0x7L);
/* 154 */           l >>>= 3L;
/* 155 */           bool1 = (l < arrayOfByte.length && (arrayOfByte[(int)l] & 1 << i) != 0) ? true : false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 176 */           return ValueBoolean.get(bool1);case 9: bool1 = (l < 8L && (paramValue1.getByte() & 1 << (int)l) != 0) ? true : false; return ValueBoolean.get(bool1);case 10: bool1 = (l < 16L && (paramValue1.getShort() & 1 << (int)l) != 0) ? true : false; return ValueBoolean.get(bool1);case 11: bool1 = (l < 32L && (paramValue1.getInt() & 1 << (int)l) != 0) ? true : false; return ValueBoolean.get(bool1);case 12: bool1 = ((paramValue1.getLong() & 1L << (int)l) != 0L) ? true : false; return ValueBoolean.get(bool1); }  throw DbException.getInvalidValueException("bit function parameter", paramValue1.getTraceSQL()); }  boolean bool = false; return ValueBoolean.get(bool); } private static ValueBigint bitCount(Value paramValue) { long l;
/*     */     byte[] arrayOfByte;
/*     */     int i;
/*     */     int j;
/*     */     int k;
/* 181 */     switch (paramValue.getValueType()) {
/*     */       case 5:
/*     */       case 6:
/* 184 */         arrayOfByte = paramValue.getBytesNoCopy();
/* 185 */         i = arrayOfByte.length;
/* 186 */         l = 0L;
/* 187 */         j = i >>> 3;
/* 188 */         for (k = 0; k < j; k++) {
/* 189 */           l += Long.bitCount(Bits.readLong(arrayOfByte, k));
/*     */         }
/* 191 */         for (k = j << 3; k < i; k++) {
/* 192 */           l += Integer.bitCount(arrayOfByte[k] & 0xFF);
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 211 */         return ValueBigint.get(l);case 9: l = Integer.bitCount(paramValue.getByte() & 0xFF); return ValueBigint.get(l);case 10: l = Integer.bitCount(paramValue.getShort() & 0xFFFF); return ValueBigint.get(l);case 11: l = Integer.bitCount(paramValue.getInt()); return ValueBigint.get(l);case 12: l = Long.bitCount(paramValue.getLong()); return ValueBigint.get(l);
/*     */     }  throw DbException.getInvalidValueException("bit function parameter", paramValue.getTraceSQL()); } private static Value shift(Value paramValue, long paramLong, boolean paramBoolean) { byte[] arrayOfByte1; int j; long l;
/*     */     int k;
/*     */     byte[] arrayOfByte2;
/* 215 */     if (paramLong == 0L) {
/* 216 */       return paramValue;
/*     */     }
/* 218 */     int i = paramValue.getValueType();
/* 219 */     switch (i) {
/*     */       case 5:
/*     */       case 6:
/* 222 */         arrayOfByte1 = paramValue.getBytesNoCopy();
/* 223 */         k = arrayOfByte1.length;
/* 224 */         if (k == 0) {
/* 225 */           return paramValue;
/*     */         }
/* 227 */         arrayOfByte2 = new byte[k];
/* 228 */         if (paramLong > -8L * k && paramLong < 8L * k) {
/* 229 */           if (paramLong > 0L) {
/* 230 */             int m = (int)(paramLong >> 3L);
/* 231 */             int n = (int)paramLong & 0x7;
/* 232 */             if (n == 0) {
/* 233 */               System.arraycopy(arrayOfByte1, m, arrayOfByte2, 0, k - m);
/*     */             } else {
/* 235 */               int i1 = 8 - n;
/* 236 */               byte b = 0; int i2 = m;
/* 237 */               k--;
/* 238 */               while (i2 < k) {
/* 239 */                 arrayOfByte2[b++] = (byte)(arrayOfByte1[i2++] << n | (arrayOfByte1[i2] & 0xFF) >>> i1);
/*     */               }
/*     */               
/* 242 */               arrayOfByte2[b] = (byte)(arrayOfByte1[i2] << n);
/*     */             } 
/*     */           } else {
/* 245 */             paramLong = -paramLong;
/* 246 */             int m = (int)(paramLong >> 3L);
/* 247 */             int n = (int)paramLong & 0x7;
/* 248 */             if (n == 0) {
/* 249 */               System.arraycopy(arrayOfByte1, 0, arrayOfByte2, m, k - m);
/*     */             } else {
/* 251 */               int i1 = 8 - n;
/* 252 */               int i2 = m; byte b = 0;
/* 253 */               arrayOfByte2[i2++] = (byte)((arrayOfByte1[b] & 0xFF) >>> n);
/* 254 */               while (i2 < k) {
/* 255 */                 arrayOfByte2[i2++] = (byte)(arrayOfByte1[b++] << i1 | (arrayOfByte1[b] & 0xFF) >>> n);
/*     */               }
/*     */             } 
/*     */           } 
/*     */         }
/*     */         
/* 261 */         return (i == 5) ? (Value)ValueBinary.getNoCopy(arrayOfByte2) : (Value)ValueVarbinary.getNoCopy(arrayOfByte2);
/*     */ 
/*     */       
/*     */       case 9:
/* 265 */         if (paramLong < 8L) {
/* 266 */           j = paramValue.getByte();
/* 267 */           if (paramLong > -8L) {
/* 268 */             if (paramLong > 0L) {
/* 269 */               j = (byte)(j << (int)paramLong);
/* 270 */             } else if (paramBoolean) {
/* 271 */               j = (byte)((j & 0xFF) >>> (int)-paramLong);
/*     */             } else {
/* 273 */               j = (byte)(j >> (int)-paramLong);
/*     */             } 
/* 275 */           } else if (paramBoolean) {
/* 276 */             j = 0;
/*     */           } else {
/* 278 */             j = (byte)(j >> 7);
/*     */           } 
/*     */         } else {
/* 281 */           j = 0;
/*     */         } 
/* 283 */         return (Value)ValueTinyint.get(j);
/*     */ 
/*     */       
/*     */       case 10:
/* 287 */         if (paramLong < 16L) {
/* 288 */           j = paramValue.getShort();
/* 289 */           if (paramLong > -16L) {
/* 290 */             if (paramLong > 0L) {
/* 291 */               j = (short)(j << (int)paramLong);
/* 292 */             } else if (paramBoolean) {
/* 293 */               j = (short)((j & 0xFFFF) >>> (int)-paramLong);
/*     */             } else {
/* 295 */               j = (short)(j >> (int)-paramLong);
/*     */             } 
/* 297 */           } else if (paramBoolean) {
/* 298 */             j = 0;
/*     */           } else {
/* 300 */             j = (short)(j >> 15);
/*     */           } 
/*     */         } else {
/* 303 */           j = 0;
/*     */         } 
/* 305 */         return (Value)ValueSmallint.get(j);
/*     */ 
/*     */       
/*     */       case 11:
/* 309 */         if (paramLong < 32L) {
/* 310 */           j = paramValue.getInt();
/* 311 */           if (paramLong > -32L) {
/* 312 */             if (paramLong > 0L) {
/* 313 */               j <<= (int)paramLong;
/* 314 */             } else if (paramBoolean) {
/* 315 */               j >>>= (int)-paramLong;
/*     */             } else {
/* 317 */               j >>= (int)-paramLong;
/*     */             } 
/* 319 */           } else if (paramBoolean) {
/* 320 */             j = 0;
/*     */           } else {
/* 322 */             j >>= 31;
/*     */           } 
/*     */         } else {
/* 325 */           j = 0;
/*     */         } 
/* 327 */         return (Value)ValueInteger.get(j);
/*     */ 
/*     */       
/*     */       case 12:
/* 331 */         if (paramLong < 64L) {
/* 332 */           l = paramValue.getLong();
/* 333 */           if (paramLong > -64L) {
/* 334 */             if (paramLong > 0L) {
/* 335 */               l <<= (int)paramLong;
/* 336 */             } else if (paramBoolean) {
/* 337 */               l >>>= (int)-paramLong;
/*     */             } else {
/* 339 */               l >>= (int)-paramLong;
/*     */             } 
/* 341 */           } else if (paramBoolean) {
/* 342 */             l = 0L;
/*     */           } else {
/* 344 */             l >>= 63L;
/*     */           } 
/*     */         } else {
/* 347 */           l = 0L;
/*     */         } 
/* 349 */         return (Value)ValueBigint.get(l);
/*     */     } 
/*     */     
/* 352 */     throw DbException.getInvalidValueException("bit function parameter", paramValue.getTraceSQL()); } private static Value rotate(Value paramValue, long paramLong, boolean paramBoolean) {
/*     */     byte[] arrayOfByte1;
/*     */     int j, k;
/*     */     long l;
/*     */     byte[] arrayOfByte2;
/* 357 */     int m, n, i = paramValue.getValueType();
/* 358 */     switch (i) {
/*     */       case 5:
/*     */       case 6:
/* 361 */         arrayOfByte1 = paramValue.getBytesNoCopy();
/* 362 */         k = arrayOfByte1.length;
/* 363 */         if (k == 0) {
/* 364 */           return paramValue;
/*     */         }
/* 366 */         l = (k << 3);
/* 367 */         paramLong %= l;
/* 368 */         if (paramBoolean) {
/* 369 */           paramLong = -paramLong;
/*     */         }
/* 371 */         if (paramLong == 0L)
/* 372 */           return paramValue; 
/* 373 */         if (paramLong < 0L) {
/* 374 */           paramLong += l;
/*     */         }
/* 376 */         arrayOfByte2 = new byte[k];
/* 377 */         m = (int)(paramLong >> 3L);
/* 378 */         n = (int)paramLong & 0x7;
/* 379 */         if (n == 0) {
/* 380 */           System.arraycopy(arrayOfByte1, m, arrayOfByte2, 0, k - m);
/* 381 */           System.arraycopy(arrayOfByte1, 0, arrayOfByte2, k - m, m);
/*     */         } else {
/* 383 */           int i1 = 8 - n; byte b; int i2;
/* 384 */           for (b = 0, i2 = m; b < k;) {
/* 385 */             arrayOfByte2[b++] = (byte)(arrayOfByte1[i2] << n | (arrayOfByte1[i2 = (i2 + 1) % k] & 0xFF) >>> i1);
/*     */           }
/*     */         } 
/*     */         
/* 389 */         return (i == 5) ? (Value)ValueBinary.getNoCopy(arrayOfByte2) : (Value)ValueVarbinary.getNoCopy(arrayOfByte2);
/*     */       
/*     */       case 9:
/* 392 */         j = (int)paramLong;
/* 393 */         if (paramBoolean) {
/* 394 */           j = -j;
/*     */         }
/* 396 */         if ((j &= 0x7) == 0) {
/* 397 */           return paramValue;
/*     */         }
/* 399 */         k = paramValue.getByte() & 0xFF;
/* 400 */         return (Value)ValueTinyint.get((byte)(k << j | k >>> 8 - j));
/*     */       
/*     */       case 10:
/* 403 */         j = (int)paramLong;
/* 404 */         if (paramBoolean) {
/* 405 */           j = -j;
/*     */         }
/* 407 */         if ((j &= 0xF) == 0) {
/* 408 */           return paramValue;
/*     */         }
/* 410 */         k = paramValue.getShort() & 0xFFFF;
/* 411 */         return (Value)ValueSmallint.get((short)(k << j | k >>> 16 - j));
/*     */       
/*     */       case 11:
/* 414 */         j = (int)paramLong;
/* 415 */         if (paramBoolean) {
/* 416 */           j = -j;
/*     */         }
/* 418 */         if ((j &= 0x1F) == 0) {
/* 419 */           return paramValue;
/*     */         }
/* 421 */         return (Value)ValueInteger.get(Integer.rotateLeft(paramValue.getInt(), j));
/*     */       
/*     */       case 12:
/* 424 */         j = (int)paramLong;
/* 425 */         if (paramBoolean) {
/* 426 */           j = -j;
/*     */         }
/* 428 */         if ((j &= 0x3F) == 0) {
/* 429 */           return paramValue;
/*     */         }
/* 431 */         return (Value)ValueBigint.get(Long.rotateLeft(paramValue.getLong(), j));
/*     */     } 
/*     */     
/* 434 */     throw DbException.getInvalidValueException("bit function parameter", paramValue.getTraceSQL());
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
/*     */   public static Value getBitwise(int paramInt, TypeInfo paramTypeInfo, Value paramValue1, Value paramValue2) {
/* 454 */     return (paramTypeInfo.getValueType() < 9) ? getBinaryString(paramInt, paramTypeInfo, paramValue1, paramValue2) : 
/* 455 */       getNumeric(paramInt, paramTypeInfo, paramValue1, paramValue2);
/*     */   }
/*     */   
/*     */   private static Value getBinaryString(int paramInt, TypeInfo paramTypeInfo, Value paramValue1, Value paramValue2) {
/*     */     byte[] arrayOfByte;
/* 460 */     if (paramInt == 3) {
/* 461 */       arrayOfByte = paramValue1.getBytes(); byte b; int i;
/* 462 */       for (b = 0, i = arrayOfByte.length; b < i; b++)
/* 463 */         arrayOfByte[b] = (byte)(arrayOfByte[b] ^ 0xFFFFFFFF); 
/*     */     } else {
/*     */       int k, m;
/* 466 */       byte[] arrayOfByte1 = paramValue1.getBytesNoCopy(), arrayOfByte2 = paramValue2.getBytesNoCopy();
/* 467 */       int i = arrayOfByte1.length, j = arrayOfByte2.length;
/*     */       
/* 469 */       if (i <= j) {
/* 470 */         k = i;
/* 471 */         m = j;
/*     */       } else {
/* 473 */         k = j;
/* 474 */         m = i;
/* 475 */         byte[] arrayOfByte3 = arrayOfByte1;
/* 476 */         arrayOfByte1 = arrayOfByte2;
/* 477 */         arrayOfByte2 = arrayOfByte3;
/*     */       } 
/* 479 */       int n = (int)paramTypeInfo.getPrecision();
/* 480 */       if (k > n) {
/* 481 */         m = k = n;
/* 482 */       } else if (m > n) {
/* 483 */         m = n;
/*     */       } 
/* 485 */       arrayOfByte = new byte[m];
/* 486 */       byte b = 0;
/* 487 */       switch (paramInt) {
/*     */         case 0:
/* 489 */           for (; b < k; b++) {
/* 490 */             arrayOfByte[b] = (byte)(arrayOfByte1[b] & arrayOfByte2[b]);
/*     */           }
/*     */           break;
/*     */         case 1:
/* 494 */           for (; b < k; b++) {
/* 495 */             arrayOfByte[b] = (byte)(arrayOfByte1[b] | arrayOfByte2[b]);
/*     */           }
/* 497 */           System.arraycopy(arrayOfByte2, b, arrayOfByte, b, m - b);
/*     */           break;
/*     */         case 2:
/* 500 */           for (; b < k; b++) {
/* 501 */             arrayOfByte[b] = (byte)(arrayOfByte1[b] ^ arrayOfByte2[b]);
/*     */           }
/* 503 */           System.arraycopy(arrayOfByte2, b, arrayOfByte, b, m - b);
/*     */           break;
/*     */         case 4:
/* 506 */           for (; b < k; b++) {
/* 507 */             arrayOfByte[b] = (byte)(arrayOfByte1[b] & arrayOfByte2[b] ^ 0xFFFFFFFF);
/*     */           }
/* 509 */           Arrays.fill(arrayOfByte, b, m, (byte)-1);
/*     */           break;
/*     */         case 5:
/* 512 */           for (; b < k; b++) {
/* 513 */             arrayOfByte[b] = (byte)((arrayOfByte1[b] | arrayOfByte2[b]) ^ 0xFFFFFFFF);
/*     */           }
/* 515 */           for (; b < m; b++) {
/* 516 */             arrayOfByte[b] = (byte)(arrayOfByte2[b] ^ 0xFFFFFFFF);
/*     */           }
/*     */           break;
/*     */         case 6:
/* 520 */           for (; b < k; b++) {
/* 521 */             arrayOfByte[b] = (byte)(arrayOfByte1[b] ^ arrayOfByte2[b] ^ 0xFFFFFFFF);
/*     */           }
/* 523 */           for (; b < m; b++) {
/* 524 */             arrayOfByte[b] = (byte)(arrayOfByte2[b] ^ 0xFFFFFFFF);
/*     */           }
/*     */           break;
/*     */         default:
/* 528 */           throw DbException.getInternalError("function=" + paramInt);
/*     */       } 
/*     */     } 
/* 531 */     return (paramTypeInfo.getValueType() == 5) ? (Value)ValueBinary.getNoCopy(arrayOfByte) : (Value)ValueVarbinary.getNoCopy(arrayOfByte);
/*     */   }
/*     */   
/*     */   private static Value getNumeric(int paramInt, TypeInfo paramTypeInfo, Value paramValue1, Value paramValue2) {
/* 535 */     long l = paramValue1.getLong();
/* 536 */     switch (paramInt) {
/*     */       case 0:
/* 538 */         l &= paramValue2.getLong();
/*     */         break;
/*     */       case 1:
/* 541 */         l |= paramValue2.getLong();
/*     */         break;
/*     */       case 2:
/* 544 */         l ^= paramValue2.getLong();
/*     */         break;
/*     */       case 3:
/* 547 */         l ^= 0xFFFFFFFFFFFFFFFFL;
/*     */         break;
/*     */       case 4:
/* 550 */         l = l & paramValue2.getLong() ^ 0xFFFFFFFFFFFFFFFFL;
/*     */         break;
/*     */       case 5:
/* 553 */         l = (l | paramValue2.getLong()) ^ 0xFFFFFFFFFFFFFFFFL;
/*     */         break;
/*     */       case 6:
/* 556 */         l = l ^ paramValue2.getLong() ^ 0xFFFFFFFFFFFFFFFFL;
/*     */         break;
/*     */       default:
/* 559 */         throw DbException.getInternalError("function=" + paramInt);
/*     */     } 
/* 561 */     switch (paramTypeInfo.getValueType()) {
/*     */       case 9:
/* 563 */         return (Value)ValueTinyint.get((byte)(int)l);
/*     */       case 10:
/* 565 */         return (Value)ValueSmallint.get((short)(int)l);
/*     */       case 11:
/* 567 */         return (Value)ValueInteger.get((int)l);
/*     */       case 12:
/* 569 */         return (Value)ValueBigint.get(l);
/*     */     } 
/* 571 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 577 */     this.left = this.left.optimize(paramSessionLocal);
/* 578 */     if (this.right != null) {
/* 579 */       this.right = this.right.optimize(paramSessionLocal);
/*     */     }
/* 581 */     switch (this.function) {
/*     */       case 3:
/* 583 */         return optimizeNot(paramSessionLocal);
/*     */       case 7:
/* 585 */         this.type = TypeInfo.TYPE_BOOLEAN;
/*     */         break;
/*     */       case 8:
/* 588 */         this.type = TypeInfo.TYPE_BIGINT;
/*     */         break;
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/*     */       case 13:
/*     */       case 14:
/* 596 */         this.type = checkArgType(this.left);
/*     */         break;
/*     */       default:
/* 599 */         this.type = getCommonType(this.left, this.right);
/*     */         break;
/*     */     } 
/* 602 */     if (this.left.isConstant() && (this.right == null || this.right.isConstant())) {
/* 603 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type);
/*     */     }
/* 605 */     return (Expression)this;
/*     */   }
/*     */   
/*     */   private Expression optimizeNot(SessionLocal paramSessionLocal) {
/* 609 */     this.type = checkArgType(this.left);
/* 610 */     if (this.left.isConstant())
/* 611 */       return (Expression)TypedValueExpression.getTypedIfNull(getValue(paramSessionLocal), this.type); 
/* 612 */     if (this.left instanceof BitFunction) {
/* 613 */       BitFunction bitFunction = (BitFunction)this.left;
/* 614 */       int i = bitFunction.function;
/* 615 */       switch (i)
/*     */       { case 0:
/*     */         case 1:
/*     */         case 2:
/* 619 */           i += 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 631 */           return (new BitFunction(bitFunction.left, bitFunction.right, i)).optimize(paramSessionLocal);case 3: return bitFunction.left;case 4: case 5: case 6: i -= 4; return (new BitFunction(bitFunction.left, bitFunction.right, i)).optimize(paramSessionLocal); }  return (Expression)this;
/* 632 */     }  if (this.left instanceof Aggregate) {
/* 633 */       AggregateType aggregateType; Aggregate aggregate = (Aggregate)this.left;
/*     */       
/* 635 */       switch (aggregate.getAggregateType()) {
/*     */         case BIT_AND_AGG:
/* 637 */           aggregateType = AggregateType.BIT_NAND_AGG;
/*     */           break;
/*     */         case BIT_OR_AGG:
/* 640 */           aggregateType = AggregateType.BIT_NOR_AGG;
/*     */           break;
/*     */         case BIT_XOR_AGG:
/* 643 */           aggregateType = AggregateType.BIT_XNOR_AGG;
/*     */           break;
/*     */         case BIT_NAND_AGG:
/* 646 */           aggregateType = AggregateType.BIT_AND_AGG;
/*     */           break;
/*     */         case BIT_NOR_AGG:
/* 649 */           aggregateType = AggregateType.BIT_OR_AGG;
/*     */           break;
/*     */         case BIT_XNOR_AGG:
/* 652 */           aggregateType = AggregateType.BIT_XOR_AGG;
/*     */           break;
/*     */         default:
/* 655 */           return (Expression)this;
/*     */       } 
/* 657 */       return (new Aggregate(aggregateType, new Expression[] { aggregate.getSubexpression(0) }, aggregate.getSelect(), aggregate.isDistinct()))
/* 658 */         .optimize(paramSessionLocal);
/*     */     } 
/* 660 */     return (Expression)this;
/*     */   }
/*     */   
/*     */   private static TypeInfo getCommonType(Expression paramExpression1, Expression paramExpression2) {
/* 664 */     TypeInfo typeInfo1 = checkArgType(paramExpression1), typeInfo2 = checkArgType(paramExpression2);
/* 665 */     int i = typeInfo1.getValueType(), j = typeInfo2.getValueType();
/* 666 */     boolean bool = DataType.isBinaryStringType(i);
/* 667 */     if (bool != DataType.isBinaryStringType(j)) {
/* 668 */       throw DbException.getInvalidValueException("bit function parameters", typeInfo2
/* 669 */           .getSQL(typeInfo1.getSQL(new StringBuilder(), 3).append(" vs "), 3)
/* 670 */           .toString());
/*     */     }
/* 672 */     if (bool) {
/*     */       long l;
/* 674 */       if (i == 5) {
/* 675 */         l = typeInfo1.getDeclaredPrecision();
/* 676 */         if (j == 5) {
/* 677 */           l = Math.max(l, typeInfo2.getDeclaredPrecision());
/*     */         }
/*     */       }
/* 680 */       else if (j == 5) {
/* 681 */         i = 5;
/* 682 */         l = typeInfo2.getDeclaredPrecision();
/*     */       } else {
/* 684 */         long l1 = typeInfo1.getDeclaredPrecision(), l2 = typeInfo2.getDeclaredPrecision();
/* 685 */         l = (l1 <= 0L || l2 <= 0L) ? -1L : Math.max(l1, l2);
/*     */       } 
/*     */       
/* 688 */       return TypeInfo.getTypeInfo(i, l, 0, null);
/*     */     } 
/* 690 */     return TypeInfo.getTypeInfo(Math.max(i, j));
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
/*     */   public static TypeInfo checkArgType(Expression paramExpression) {
/* 705 */     TypeInfo typeInfo = paramExpression.getType();
/* 706 */     switch (typeInfo.getValueType()) {
/*     */       case 0:
/*     */       case 5:
/*     */       case 6:
/*     */       case 9:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/* 714 */         return typeInfo;
/*     */     } 
/* 716 */     throw DbException.getInvalidExpressionTypeException("bit function argument", paramExpression);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 721 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\BitFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */