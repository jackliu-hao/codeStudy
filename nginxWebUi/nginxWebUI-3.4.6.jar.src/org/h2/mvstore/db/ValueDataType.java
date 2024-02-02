/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.h2.api.IntervalQualifier;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mode.DefaultNullOrdering;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.WriteBuffer;
/*     */ import org.h2.mvstore.type.BasicDataType;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.MetaType;
/*     */ import org.h2.mvstore.type.StatefulDataType;
/*     */ import org.h2.result.RowFactory;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.ExtTypeInfoEnum;
/*     */ import org.h2.value.ExtTypeInfoRow;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueBinary;
/*     */ import org.h2.value.ValueBlob;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueChar;
/*     */ import org.h2.value.ValueClob;
/*     */ import org.h2.value.ValueCollectionBase;
/*     */ import org.h2.value.ValueDate;
/*     */ import org.h2.value.ValueDecfloat;
/*     */ import org.h2.value.ValueDouble;
/*     */ import org.h2.value.ValueGeometry;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueInterval;
/*     */ import org.h2.value.ValueJavaObject;
/*     */ import org.h2.value.ValueJson;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueNumeric;
/*     */ import org.h2.value.ValueReal;
/*     */ import org.h2.value.ValueRow;
/*     */ import org.h2.value.ValueSmallint;
/*     */ import org.h2.value.ValueTime;
/*     */ import org.h2.value.ValueTimeTimeZone;
/*     */ import org.h2.value.ValueTimestamp;
/*     */ import org.h2.value.ValueTimestampTimeZone;
/*     */ import org.h2.value.ValueTinyint;
/*     */ import org.h2.value.ValueUuid;
/*     */ import org.h2.value.ValueVarbinary;
/*     */ import org.h2.value.ValueVarchar;
/*     */ import org.h2.value.ValueVarcharIgnoreCase;
/*     */ import org.h2.value.lob.LobData;
/*     */ import org.h2.value.lob.LobDataDatabase;
/*     */ import org.h2.value.lob.LobDataInMemory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValueDataType
/*     */   extends BasicDataType<Value>
/*     */   implements StatefulDataType<Database>
/*     */ {
/*     */   private static final byte NULL = 0;
/*     */   private static final byte TINYINT = 2;
/*     */   private static final byte SMALLINT = 3;
/*     */   private static final byte INTEGER = 4;
/*     */   private static final byte BIGINT = 5;
/*     */   private static final byte NUMERIC = 6;
/*     */   private static final byte DOUBLE = 7;
/*     */   private static final byte REAL = 8;
/*     */   private static final byte TIME = 9;
/*     */   private static final byte DATE = 10;
/*     */   private static final byte TIMESTAMP = 11;
/*     */   private static final byte VARBINARY = 12;
/*     */   private static final byte VARCHAR = 13;
/*     */   private static final byte VARCHAR_IGNORECASE = 14;
/*     */   private static final byte BLOB = 15;
/*     */   private static final byte CLOB = 16;
/*     */   private static final byte ARRAY = 17;
/*     */   private static final byte JAVA_OBJECT = 19;
/*     */   private static final byte UUID = 20;
/*     */   private static final byte CHAR = 21;
/*     */   private static final byte GEOMETRY = 22;
/*     */   private static final byte TIMESTAMP_TZ_OLD = 24;
/*     */   private static final byte ENUM = 25;
/*     */   private static final byte INTERVAL = 26;
/*     */   private static final byte ROW = 27;
/*     */   private static final byte INT_0_15 = 32;
/*     */   private static final byte BIGINT_0_7 = 48;
/*     */   private static final byte NUMERIC_0_1 = 56;
/*     */   private static final byte NUMERIC_SMALL_0 = 58;
/*     */   private static final byte NUMERIC_SMALL = 59;
/*     */   private static final byte DOUBLE_0_1 = 60;
/*     */   private static final byte REAL_0_1 = 62;
/*     */   private static final byte BOOLEAN_FALSE = 64;
/*     */   private static final byte BOOLEAN_TRUE = 65;
/*     */   private static final byte INT_NEG = 66;
/*     */   private static final byte BIGINT_NEG = 67;
/*     */   private static final byte VARCHAR_0_31 = 68;
/*     */   private static final int VARBINARY_0_31 = 100;
/*     */   private static final int JSON = 134;
/*     */   private static final int TIMESTAMP_TZ = 135;
/*     */   private static final int TIME_TZ = 136;
/*     */   private static final int BINARY = 137;
/*     */   private static final int DECFLOAT = 138;
/*     */   final DataHandler handler;
/*     */   final CastDataProvider provider;
/*     */   final CompareMode compareMode;
/*     */   final int[] sortTypes;
/*     */   private RowFactory rowFactory;
/*     */   
/*     */   public ValueDataType() {
/* 133 */     this(null, CompareMode.getInstance(null, 0), null, null);
/*     */   }
/*     */   
/*     */   public ValueDataType(Database paramDatabase, int[] paramArrayOfint) {
/* 137 */     this((CastDataProvider)paramDatabase, paramDatabase.getCompareMode(), (DataHandler)paramDatabase, paramArrayOfint);
/*     */   }
/*     */   
/*     */   public ValueDataType(CastDataProvider paramCastDataProvider, CompareMode paramCompareMode, DataHandler paramDataHandler, int[] paramArrayOfint) {
/* 141 */     this.provider = paramCastDataProvider;
/* 142 */     this.compareMode = paramCompareMode;
/* 143 */     this.handler = paramDataHandler;
/* 144 */     this.sortTypes = paramArrayOfint;
/*     */   }
/*     */   
/*     */   public RowFactory getRowFactory() {
/* 148 */     return this.rowFactory;
/*     */   }
/*     */   
/*     */   public void setRowFactory(RowFactory paramRowFactory) {
/* 152 */     this.rowFactory = paramRowFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value[] createStorage(int paramInt) {
/* 157 */     return new Value[paramInt];
/*     */   }
/*     */ 
/*     */   
/*     */   public int compare(Value paramValue1, Value paramValue2) {
/* 162 */     if (paramValue1 == paramValue2) {
/* 163 */       return 0;
/*     */     }
/* 165 */     if (paramValue1 instanceof SearchRow && paramValue2 instanceof SearchRow)
/* 166 */       return compare((SearchRow)paramValue1, (SearchRow)paramValue2); 
/* 167 */     if (paramValue1 instanceof ValueCollectionBase && paramValue2 instanceof ValueCollectionBase) {
/* 168 */       Value[] arrayOfValue1 = ((ValueCollectionBase)paramValue1).getList();
/* 169 */       Value[] arrayOfValue2 = ((ValueCollectionBase)paramValue2).getList();
/* 170 */       int i = arrayOfValue1.length;
/* 171 */       int j = arrayOfValue2.length;
/* 172 */       int k = Math.min(i, j);
/* 173 */       for (byte b = 0; b < k; b++) {
/* 174 */         boolean bool = (this.sortTypes == null) ? false : this.sortTypes[b];
/* 175 */         Value value1 = arrayOfValue1[b];
/* 176 */         Value value2 = arrayOfValue2[b];
/* 177 */         if (value1 == null || value2 == null) {
/* 178 */           return compareValues(arrayOfValue1[k - 1], arrayOfValue2[k - 1], 0);
/*     */         }
/*     */         
/* 181 */         int m = compareValues(value1, value2, bool);
/* 182 */         if (m != 0) {
/* 183 */           return m;
/*     */         }
/*     */       } 
/* 186 */       if (k < i)
/* 187 */         return -1; 
/* 188 */       if (k < j) {
/* 189 */         return 1;
/*     */       }
/* 191 */       return 0;
/*     */     } 
/* 193 */     return compareValues(paramValue1, paramValue2, 0);
/*     */   }
/*     */   
/*     */   private int compare(SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 197 */     if (paramSearchRow1 == paramSearchRow2) {
/* 198 */       return 0;
/*     */     }
/* 200 */     int[] arrayOfInt = this.rowFactory.getIndexes();
/* 201 */     if (arrayOfInt == null) {
/* 202 */       int i = paramSearchRow1.getColumnCount();
/* 203 */       assert i == paramSearchRow2.getColumnCount() : i + " != " + paramSearchRow2.getColumnCount();
/* 204 */       for (byte b1 = 0; b1 < i; b1++) {
/* 205 */         int j = compareValues(paramSearchRow1.getValue(b1), paramSearchRow2.getValue(b1), this.sortTypes[b1]);
/* 206 */         if (j != 0) {
/* 207 */           return j;
/*     */         }
/*     */       } 
/* 210 */       return 0;
/*     */     } 
/* 212 */     assert this.sortTypes.length == arrayOfInt.length;
/* 213 */     for (byte b = 0; b < arrayOfInt.length; b++) {
/* 214 */       int i = arrayOfInt[b];
/* 215 */       Value value1 = paramSearchRow1.getValue(i);
/* 216 */       Value value2 = paramSearchRow2.getValue(i);
/* 217 */       if (value1 == null || value2 == null) {
/*     */         break;
/*     */       }
/*     */       
/* 221 */       int j = compareValues(paramSearchRow1.getValue(i), paramSearchRow2.getValue(i), this.sortTypes[b]);
/* 222 */       if (j != 0) {
/* 223 */         return j;
/*     */       }
/*     */     } 
/* 226 */     long l1 = paramSearchRow1.getKey();
/* 227 */     long l2 = paramSearchRow2.getKey();
/* 228 */     return (l1 == SearchRow.MATCH_ALL_ROW_KEY || l2 == SearchRow.MATCH_ALL_ROW_KEY) ? 0 : 
/* 229 */       Long.compare(l1, l2);
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
/*     */   public int compareValues(Value paramValue1, Value paramValue2, int paramInt) {
/* 243 */     if (paramValue1 == paramValue2) {
/* 244 */       return 0;
/*     */     }
/* 246 */     boolean bool = (paramValue1 == ValueNull.INSTANCE) ? true : false;
/* 247 */     if (bool || paramValue2 == ValueNull.INSTANCE)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 252 */       return DefaultNullOrdering.LOW.compareNull(bool, paramInt);
/*     */     }
/*     */     
/* 255 */     int i = paramValue1.compareTo(paramValue2, this.provider, this.compareMode);
/*     */     
/* 257 */     if ((paramInt & 0x1) != 0) {
/* 258 */       i = -i;
/*     */     }
/* 260 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory(Value paramValue) {
/* 265 */     return (paramValue == null) ? 0 : paramValue.getMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public Value read(ByteBuffer paramByteBuffer) {
/* 270 */     return readValue(paramByteBuffer, null); } public void write(WriteBuffer paramWriteBuffer, Value paramValue) { int j; BigDecimal bigDecimal; ValueDecfloat valueDecfloat; ValueTimeTimeZone valueTimeTimeZone; ValueTimestamp valueTimestamp; ValueTimestampTimeZone valueTimestampTimeZone; byte[] arrayOfByte; ValueUuid valueUuid; String str; double d; float f; ValueBlob valueBlob; ValueClob valueClob; Value[] arrayOfValue; ValueInterval valueInterval;
/*     */     long l;
/*     */     int m;
/*     */     LobData lobData;
/*     */     int k;
/* 275 */     if (paramValue == ValueNull.INSTANCE) {
/* 276 */       paramWriteBuffer.put((byte)0);
/*     */       return;
/*     */     } 
/* 279 */     int i = paramValue.getValueType();
/* 280 */     switch (i) {
/*     */       case 8:
/* 282 */         paramWriteBuffer.put(paramValue.getBoolean() ? 65 : 64);
/*     */         return;
/*     */       case 9:
/* 285 */         paramWriteBuffer.put((byte)2).put(paramValue.getByte());
/*     */         return;
/*     */       case 10:
/* 288 */         paramWriteBuffer.put((byte)3).putShort(paramValue.getShort());
/*     */         return;
/*     */       case 11:
/*     */       case 36:
/* 292 */         j = paramValue.getInt();
/* 293 */         if (j < 0) {
/* 294 */           paramWriteBuffer.put((byte)66).putVarInt(-j);
/* 295 */         } else if (j < 16) {
/* 296 */           paramWriteBuffer.put((byte)(32 + j));
/*     */         } else {
/* 298 */           paramWriteBuffer.put((i == 11) ? 4 : 25).putVarInt(j);
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 12:
/* 303 */         writeLong(paramWriteBuffer, paramValue.getLong());
/*     */         return;
/*     */       case 13:
/* 306 */         bigDecimal = paramValue.getBigDecimal();
/* 307 */         if (BigDecimal.ZERO.equals(bigDecimal)) {
/* 308 */           paramWriteBuffer.put((byte)56);
/* 309 */         } else if (BigDecimal.ONE.equals(bigDecimal)) {
/* 310 */           paramWriteBuffer.put((byte)57);
/*     */         } else {
/* 312 */           int n = bigDecimal.scale();
/* 313 */           BigInteger bigInteger = bigDecimal.unscaledValue();
/* 314 */           int i1 = bigInteger.bitLength();
/* 315 */           if (i1 <= 63) {
/* 316 */             if (n == 0) {
/* 317 */               paramWriteBuffer.put((byte)58)
/* 318 */                 .putVarLong(bigInteger.longValue());
/*     */             } else {
/* 320 */               paramWriteBuffer.put((byte)59)
/* 321 */                 .putVarInt(n)
/* 322 */                 .putVarLong(bigInteger.longValue());
/*     */             } 
/*     */           } else {
/* 325 */             byte[] arrayOfByte1 = bigInteger.toByteArray();
/* 326 */             paramWriteBuffer.put((byte)6)
/* 327 */               .putVarInt(n)
/* 328 */               .putVarInt(arrayOfByte1.length)
/* 329 */               .put(arrayOfByte1);
/*     */           } 
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 16:
/* 335 */         valueDecfloat = (ValueDecfloat)paramValue;
/* 336 */         paramWriteBuffer.put((byte)-118);
/* 337 */         if (valueDecfloat.isFinite()) {
/* 338 */           BigDecimal bigDecimal1 = valueDecfloat.getBigDecimal();
/* 339 */           byte[] arrayOfByte1 = bigDecimal1.unscaledValue().toByteArray();
/* 340 */           paramWriteBuffer.putVarInt(bigDecimal1.scale())
/* 341 */             .putVarInt(arrayOfByte1.length)
/* 342 */             .put(arrayOfByte1);
/*     */         } else {
/*     */           byte b;
/* 345 */           if (valueDecfloat == ValueDecfloat.NEGATIVE_INFINITY) {
/* 346 */             b = -3;
/* 347 */           } else if (valueDecfloat == ValueDecfloat.POSITIVE_INFINITY) {
/* 348 */             b = -2;
/*     */           } else {
/* 350 */             b = -1;
/*     */           } 
/* 352 */           paramWriteBuffer.putVarInt(0).putVarInt(b);
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 18:
/* 357 */         writeTimestampTime(paramWriteBuffer.put((byte)9), ((ValueTime)paramValue).getNanos());
/*     */         return;
/*     */       case 19:
/* 360 */         valueTimeTimeZone = (ValueTimeTimeZone)paramValue;
/* 361 */         l = valueTimeTimeZone.getNanos();
/* 362 */         paramWriteBuffer.put((byte)-120)
/* 363 */           .putVarInt((int)(l / 1000000000L))
/* 364 */           .putVarInt((int)(l % 1000000000L));
/* 365 */         writeTimeZone(paramWriteBuffer, valueTimeTimeZone.getTimeZoneOffsetSeconds());
/*     */         return;
/*     */       
/*     */       case 17:
/* 369 */         paramWriteBuffer.put((byte)10).putVarLong(((ValueDate)paramValue).getDateValue());
/*     */         return;
/*     */       case 20:
/* 372 */         valueTimestamp = (ValueTimestamp)paramValue;
/* 373 */         paramWriteBuffer.put((byte)11).putVarLong(valueTimestamp.getDateValue());
/* 374 */         writeTimestampTime(paramWriteBuffer, valueTimestamp.getTimeNanos());
/*     */         return;
/*     */       
/*     */       case 21:
/* 378 */         valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue;
/* 379 */         paramWriteBuffer.put((byte)-121).putVarLong(valueTimestampTimeZone.getDateValue());
/* 380 */         writeTimestampTime(paramWriteBuffer, valueTimestampTimeZone.getTimeNanos());
/* 381 */         writeTimeZone(paramWriteBuffer, valueTimestampTimeZone.getTimeZoneOffsetSeconds());
/*     */         return;
/*     */       
/*     */       case 35:
/* 385 */         writeBinary((byte)19, paramWriteBuffer, paramValue);
/*     */         return;
/*     */       case 6:
/* 388 */         arrayOfByte = paramValue.getBytesNoCopy();
/* 389 */         m = arrayOfByte.length;
/* 390 */         if (m < 32) {
/* 391 */           paramWriteBuffer.put((byte)(100 + m)).put(arrayOfByte);
/*     */         } else {
/* 393 */           paramWriteBuffer.put((byte)12).putVarInt(m).put(arrayOfByte);
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 5:
/* 398 */         writeBinary((byte)-119, paramWriteBuffer, paramValue);
/*     */         return;
/*     */       case 39:
/* 401 */         valueUuid = (ValueUuid)paramValue;
/* 402 */         paramWriteBuffer.put((byte)20)
/* 403 */           .putLong(valueUuid.getHigh())
/* 404 */           .putLong(valueUuid.getLow());
/*     */         return;
/*     */       
/*     */       case 2:
/* 408 */         str = paramValue.getString();
/* 409 */         m = str.length();
/* 410 */         if (m < 32) {
/* 411 */           paramWriteBuffer.put((byte)(68 + m)).putStringData(str, m);
/*     */         } else {
/* 413 */           writeString(paramWriteBuffer.put((byte)13), str);
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 4:
/* 418 */         writeString(paramWriteBuffer.put((byte)14), paramValue.getString());
/*     */         return;
/*     */       case 1:
/* 421 */         writeString(paramWriteBuffer.put((byte)21), paramValue.getString());
/*     */         return;
/*     */       case 15:
/* 424 */         d = paramValue.getDouble();
/* 425 */         if (d == 1.0D) {
/* 426 */           paramWriteBuffer.put((byte)61);
/*     */         } else {
/* 428 */           long l1 = Double.doubleToLongBits(d);
/* 429 */           if (l1 == 0L) {
/* 430 */             paramWriteBuffer.put((byte)60);
/*     */           } else {
/* 432 */             paramWriteBuffer.put((byte)7)
/* 433 */               .putVarLong(Long.reverse(l1));
/*     */           } 
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 14:
/* 439 */         f = paramValue.getFloat();
/* 440 */         if (f == 1.0F) {
/* 441 */           paramWriteBuffer.put((byte)63);
/*     */         } else {
/* 443 */           m = Float.floatToIntBits(f);
/* 444 */           if (m == 0) {
/* 445 */             paramWriteBuffer.put((byte)62);
/*     */           } else {
/* 447 */             paramWriteBuffer.put((byte)8)
/* 448 */               .putVarInt(Integer.reverse(m));
/*     */           } 
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 7:
/* 454 */         paramWriteBuffer.put((byte)15);
/* 455 */         valueBlob = (ValueBlob)paramValue;
/* 456 */         lobData = valueBlob.getLobData();
/* 457 */         if (lobData instanceof LobDataDatabase) {
/* 458 */           LobDataDatabase lobDataDatabase = (LobDataDatabase)lobData;
/* 459 */           paramWriteBuffer.putVarInt(-3)
/* 460 */             .putVarInt(lobDataDatabase.getTableId())
/* 461 */             .putVarLong(lobDataDatabase.getLobId())
/* 462 */             .putVarLong(valueBlob.octetLength());
/*     */         } else {
/* 464 */           byte[] arrayOfByte1 = ((LobDataInMemory)lobData).getSmall();
/* 465 */           paramWriteBuffer.putVarInt(arrayOfByte1.length)
/* 466 */             .put(arrayOfByte1);
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 3:
/* 471 */         paramWriteBuffer.put((byte)16);
/* 472 */         valueClob = (ValueClob)paramValue;
/* 473 */         lobData = valueClob.getLobData();
/* 474 */         if (lobData instanceof LobDataDatabase) {
/* 475 */           LobDataDatabase lobDataDatabase = (LobDataDatabase)lobData;
/* 476 */           paramWriteBuffer.putVarInt(-3)
/* 477 */             .putVarInt(lobDataDatabase.getTableId())
/* 478 */             .putVarLong(lobDataDatabase.getLobId())
/* 479 */             .putVarLong(valueClob.octetLength())
/* 480 */             .putVarLong(valueClob.charLength());
/*     */         } else {
/* 482 */           byte[] arrayOfByte1 = ((LobDataInMemory)lobData).getSmall();
/* 483 */           paramWriteBuffer.putVarInt(arrayOfByte1.length)
/* 484 */             .put(arrayOfByte1)
/* 485 */             .putVarLong(valueClob.charLength());
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 40:
/*     */       case 41:
/* 491 */         arrayOfValue = ((ValueCollectionBase)paramValue).getList();
/* 492 */         paramWriteBuffer.put((i == 40) ? 17 : 27)
/* 493 */           .putVarInt(arrayOfValue.length);
/* 494 */         for (Value value : arrayOfValue) {
/* 495 */           write(paramWriteBuffer, value);
/*     */         }
/*     */         return;
/*     */       
/*     */       case 37:
/* 500 */         writeBinary((byte)22, paramWriteBuffer, paramValue);
/*     */         return;
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 25:
/*     */       case 26:
/* 507 */         valueInterval = (ValueInterval)paramValue;
/* 508 */         k = i - 22;
/* 509 */         if (valueInterval.isNegative()) {
/* 510 */           k ^= 0xFFFFFFFF;
/*     */         }
/* 512 */         paramWriteBuffer.put((byte)26)
/* 513 */           .put((byte)k)
/* 514 */           .putVarLong(valueInterval.getLeading());
/*     */         return;
/*     */       
/*     */       case 27:
/*     */       case 28:
/*     */       case 29:
/*     */       case 30:
/*     */       case 31:
/*     */       case 32:
/*     */       case 33:
/*     */       case 34:
/* 525 */         valueInterval = (ValueInterval)paramValue;
/* 526 */         k = i - 22;
/* 527 */         if (valueInterval.isNegative()) {
/* 528 */           k ^= 0xFFFFFFFF;
/*     */         }
/* 530 */         paramWriteBuffer.put((byte)26)
/* 531 */           .put((byte)k)
/* 532 */           .putVarLong(valueInterval.getLeading())
/* 533 */           .putVarLong(valueInterval.getRemaining());
/*     */         return;
/*     */       
/*     */       case 38:
/* 537 */         writeBinary((byte)-122, paramWriteBuffer, paramValue);
/*     */         return;
/*     */     } 
/* 540 */     throw DbException.getInternalError("type=" + paramValue.getValueType()); }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void writeBinary(byte paramByte, WriteBuffer paramWriteBuffer, Value paramValue) {
/* 545 */     byte[] arrayOfByte = paramValue.getBytesNoCopy();
/* 546 */     paramWriteBuffer.put(paramByte).putVarInt(arrayOfByte.length).put(arrayOfByte);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeLong(WriteBuffer paramWriteBuffer, long paramLong) {
/* 556 */     if (paramLong < 0L) {
/* 557 */       paramWriteBuffer.put((byte)67).putVarLong(-paramLong);
/* 558 */     } else if (paramLong < 8L) {
/* 559 */       paramWriteBuffer.put((byte)(int)(48L + paramLong));
/*     */     } else {
/* 561 */       paramWriteBuffer.put((byte)5).putVarLong(paramLong);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeString(WriteBuffer paramWriteBuffer, String paramString) {
/* 566 */     int i = paramString.length();
/* 567 */     paramWriteBuffer.putVarInt(i).putStringData(paramString, i);
/*     */   }
/*     */   
/*     */   private static void writeTimestampTime(WriteBuffer paramWriteBuffer, long paramLong) {
/* 571 */     long l = paramLong / 1000000L;
/* 572 */     paramWriteBuffer.putVarLong(l).putVarInt((int)(paramLong - l * 1000000L));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void writeTimeZone(WriteBuffer paramWriteBuffer, int paramInt) {
/* 578 */     if (paramInt % 900 == 0) {
/*     */       
/* 580 */       paramWriteBuffer.put((byte)(paramInt / 900));
/* 581 */     } else if (paramInt > 0) {
/* 582 */       paramWriteBuffer.put(127).putVarInt(paramInt);
/*     */     } else {
/* 584 */       paramWriteBuffer.put(-128).putVarInt(-paramInt);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   Value readValue(ByteBuffer paramByteBuffer, TypeInfo paramTypeInfo) {
/*     */     int j, k;
/*     */     Value[] arrayOfValue;
/*     */     byte[] arrayOfByte;
/*     */     TypeInfo[] arrayOfTypeInfo;
/*     */     byte b;
/* 596 */     int i = paramByteBuffer.get() & 0xFF;
/* 597 */     switch (i) {
/*     */       case 0:
/* 599 */         return (Value)ValueNull.INSTANCE;
/*     */       case 65:
/* 601 */         return (Value)ValueBoolean.TRUE;
/*     */       case 64:
/* 603 */         return (Value)ValueBoolean.FALSE;
/*     */       case 66:
/* 605 */         return (Value)ValueInteger.get(-DataUtils.readVarInt(paramByteBuffer));
/*     */       case 4:
/* 607 */         return (Value)ValueInteger.get(DataUtils.readVarInt(paramByteBuffer));
/*     */       case 67:
/* 609 */         return (Value)ValueBigint.get(-DataUtils.readVarLong(paramByteBuffer));
/*     */       case 5:
/* 611 */         return (Value)ValueBigint.get(DataUtils.readVarLong(paramByteBuffer));
/*     */       case 2:
/* 613 */         return (Value)ValueTinyint.get(paramByteBuffer.get());
/*     */       case 3:
/* 615 */         return (Value)ValueSmallint.get(paramByteBuffer.getShort());
/*     */       case 56:
/* 617 */         return (Value)ValueNumeric.ZERO;
/*     */       case 57:
/* 619 */         return (Value)ValueNumeric.ONE;
/*     */       case 58:
/* 621 */         return (Value)ValueNumeric.get(BigDecimal.valueOf(DataUtils.readVarLong(paramByteBuffer)));
/*     */       case 59:
/* 623 */         j = DataUtils.readVarInt(paramByteBuffer);
/* 624 */         return (Value)ValueNumeric.get(BigDecimal.valueOf(DataUtils.readVarLong(paramByteBuffer), j));
/*     */       
/*     */       case 6:
/* 627 */         j = DataUtils.readVarInt(paramByteBuffer);
/* 628 */         return (Value)ValueNumeric.get(new BigDecimal(new BigInteger(readVarBytes(paramByteBuffer)), j));
/*     */       
/*     */       case 138:
/* 631 */         j = DataUtils.readVarInt(paramByteBuffer); k = DataUtils.readVarInt(paramByteBuffer);
/* 632 */         switch (k) {
/*     */           case -3:
/* 634 */             return (Value)ValueDecfloat.NEGATIVE_INFINITY;
/*     */           case -2:
/* 636 */             return (Value)ValueDecfloat.POSITIVE_INFINITY;
/*     */           case -1:
/* 638 */             return (Value)ValueDecfloat.NAN;
/*     */         } 
/* 640 */         arrayOfByte = Utils.newBytes(k);
/* 641 */         paramByteBuffer.get(arrayOfByte, 0, k);
/* 642 */         return (Value)ValueDecfloat.get(new BigDecimal(new BigInteger(arrayOfByte), j));
/*     */ 
/*     */       
/*     */       case 10:
/* 646 */         return (Value)ValueDate.fromDateValue(DataUtils.readVarLong(paramByteBuffer));
/*     */       case 9:
/* 648 */         return (Value)ValueTime.fromNanos(readTimestampTime(paramByteBuffer));
/*     */       case 136:
/* 650 */         return (Value)ValueTimeTimeZone.fromNanos(DataUtils.readVarInt(paramByteBuffer) * 1000000000L + DataUtils.readVarInt(paramByteBuffer), 
/* 651 */             readTimeZone(paramByteBuffer));
/*     */       case 11:
/* 653 */         return (Value)ValueTimestamp.fromDateValueAndNanos(DataUtils.readVarLong(paramByteBuffer), readTimestampTime(paramByteBuffer));
/*     */       case 24:
/* 655 */         return (Value)ValueTimestampTimeZone.fromDateValueAndNanos(DataUtils.readVarLong(paramByteBuffer), readTimestampTime(paramByteBuffer), 
/* 656 */             DataUtils.readVarInt(paramByteBuffer) * 60);
/*     */       case 135:
/* 658 */         return (Value)ValueTimestampTimeZone.fromDateValueAndNanos(DataUtils.readVarLong(paramByteBuffer), readTimestampTime(paramByteBuffer), 
/* 659 */             readTimeZone(paramByteBuffer));
/*     */       case 12:
/* 661 */         return (Value)ValueVarbinary.getNoCopy(readVarBytes(paramByteBuffer));
/*     */       case 137:
/* 663 */         return (Value)ValueBinary.getNoCopy(readVarBytes(paramByteBuffer));
/*     */       case 19:
/* 665 */         return (Value)ValueJavaObject.getNoCopy(readVarBytes(paramByteBuffer));
/*     */       case 20:
/* 667 */         return (Value)ValueUuid.get(paramByteBuffer.getLong(), paramByteBuffer.getLong());
/*     */       case 13:
/* 669 */         return ValueVarchar.get(DataUtils.readString(paramByteBuffer));
/*     */       case 14:
/* 671 */         return (Value)ValueVarcharIgnoreCase.get(DataUtils.readString(paramByteBuffer));
/*     */       case 21:
/* 673 */         return (Value)ValueChar.get(DataUtils.readString(paramByteBuffer));
/*     */       case 25:
/* 675 */         j = DataUtils.readVarInt(paramByteBuffer);
/* 676 */         if (paramTypeInfo != null) {
/* 677 */           return (Value)((ExtTypeInfoEnum)paramTypeInfo.getExtTypeInfo()).getValue(j, this.provider);
/*     */         }
/* 679 */         return (Value)ValueInteger.get(j);
/*     */       
/*     */       case 26:
/* 682 */         j = paramByteBuffer.get();
/* 683 */         k = (j < 0) ? 1 : 0;
/* 684 */         if (k != 0) {
/* 685 */           j ^= 0xFFFFFFFF;
/*     */         }
/* 687 */         return (Value)ValueInterval.from(IntervalQualifier.valueOf(j), k, DataUtils.readVarLong(paramByteBuffer), (j < 5) ? 0L : 
/* 688 */             DataUtils.readVarLong(paramByteBuffer));
/*     */       
/*     */       case 62:
/* 691 */         return (Value)ValueReal.ZERO;
/*     */       case 63:
/* 693 */         return (Value)ValueReal.ONE;
/*     */       case 60:
/* 695 */         return (Value)ValueDouble.ZERO;
/*     */       case 61:
/* 697 */         return (Value)ValueDouble.ONE;
/*     */       case 7:
/* 699 */         return (Value)ValueDouble.get(Double.longBitsToDouble(Long.reverse(DataUtils.readVarLong(paramByteBuffer))));
/*     */       case 8:
/* 701 */         return (Value)ValueReal.get(Float.intBitsToFloat(Integer.reverse(DataUtils.readVarInt(paramByteBuffer))));
/*     */       case 15:
/* 703 */         j = DataUtils.readVarInt(paramByteBuffer);
/* 704 */         if (j >= 0) {
/* 705 */           byte[] arrayOfByte1 = Utils.newBytes(j);
/* 706 */           paramByteBuffer.get(arrayOfByte1, 0, j);
/* 707 */           return (Value)ValueBlob.createSmall(arrayOfByte1);
/* 708 */         }  if (j == -3) {
/* 709 */           return (Value)new ValueBlob((LobData)readLobDataDatabase(paramByteBuffer), DataUtils.readVarLong(paramByteBuffer));
/*     */         }
/* 711 */         throw DbException.get(90030, "lob type: " + j);
/*     */ 
/*     */       
/*     */       case 16:
/* 715 */         j = DataUtils.readVarInt(paramByteBuffer);
/* 716 */         if (j >= 0) {
/* 717 */           byte[] arrayOfByte1 = Utils.newBytes(j);
/* 718 */           paramByteBuffer.get(arrayOfByte1, 0, j);
/* 719 */           return (Value)ValueClob.createSmall(arrayOfByte1, DataUtils.readVarLong(paramByteBuffer));
/* 720 */         }  if (j == -3) {
/* 721 */           return (Value)new ValueClob((LobData)readLobDataDatabase(paramByteBuffer), DataUtils.readVarLong(paramByteBuffer), DataUtils.readVarLong(paramByteBuffer));
/*     */         }
/* 723 */         throw DbException.get(90030, "lob type: " + j);
/*     */ 
/*     */       
/*     */       case 17:
/* 727 */         if (paramTypeInfo != null) {
/* 728 */           TypeInfo typeInfo = (TypeInfo)paramTypeInfo.getExtTypeInfo();
/* 729 */           return (Value)ValueArray.get(typeInfo, readArrayElements(paramByteBuffer, typeInfo), this.provider);
/*     */         } 
/* 731 */         return (Value)ValueArray.get(readArrayElements(paramByteBuffer, null), this.provider);
/*     */       
/*     */       case 27:
/* 734 */         j = DataUtils.readVarInt(paramByteBuffer);
/* 735 */         arrayOfValue = new Value[j];
/* 736 */         if (paramTypeInfo != null) {
/* 737 */           ExtTypeInfoRow extTypeInfoRow = (ExtTypeInfoRow)paramTypeInfo.getExtTypeInfo();
/* 738 */           Iterator<Map.Entry> iterator = extTypeInfoRow.getFields().iterator();
/* 739 */           for (byte b1 = 0; b1 < j; b1++) {
/* 740 */             arrayOfValue[b1] = readValue(paramByteBuffer, (TypeInfo)((Map.Entry)iterator.next()).getValue());
/*     */           }
/* 742 */           return (Value)ValueRow.get(paramTypeInfo, arrayOfValue);
/*     */         } 
/* 744 */         arrayOfTypeInfo = this.rowFactory.getColumnTypes();
/* 745 */         for (b = 0; b < j; b++) {
/* 746 */           arrayOfValue[b] = readValue(paramByteBuffer, arrayOfTypeInfo[b]);
/*     */         }
/* 748 */         return (Value)ValueRow.get(arrayOfValue);
/*     */       
/*     */       case 22:
/* 751 */         return (Value)ValueGeometry.get(readVarBytes(paramByteBuffer));
/*     */       case 134:
/* 753 */         return (Value)ValueJson.getInternal(readVarBytes(paramByteBuffer));
/*     */     } 
/* 755 */     if (i >= 32 && i < 48) {
/* 756 */       j = i - 32;
/* 757 */       if (paramTypeInfo != null && paramTypeInfo.getValueType() == 36) {
/* 758 */         return (Value)((ExtTypeInfoEnum)paramTypeInfo.getExtTypeInfo()).getValue(j, this.provider);
/*     */       }
/* 760 */       return (Value)ValueInteger.get(j);
/* 761 */     }  if (i >= 48 && i < 56)
/* 762 */       return (Value)ValueBigint.get((i - 48)); 
/* 763 */     if (i >= 100 && i < 132) {
/* 764 */       j = i - 100;
/* 765 */       byte[] arrayOfByte1 = Utils.newBytes(j);
/* 766 */       paramByteBuffer.get(arrayOfByte1, 0, j);
/* 767 */       return (Value)ValueVarbinary.getNoCopy(arrayOfByte1);
/* 768 */     }  if (i >= 68 && i < 100) {
/* 769 */       return ValueVarchar.get(DataUtils.readString(paramByteBuffer, i - 68));
/*     */     }
/* 771 */     throw DbException.get(90030, "type: " + i);
/*     */   }
/*     */ 
/*     */   
/*     */   private LobDataDatabase readLobDataDatabase(ByteBuffer paramByteBuffer) {
/* 776 */     int i = DataUtils.readVarInt(paramByteBuffer);
/* 777 */     long l = DataUtils.readVarLong(paramByteBuffer);
/* 778 */     return new LobDataDatabase(this.handler, i, l);
/*     */   }
/*     */ 
/*     */   
/*     */   private Value[] readArrayElements(ByteBuffer paramByteBuffer, TypeInfo paramTypeInfo) {
/* 783 */     int i = DataUtils.readVarInt(paramByteBuffer);
/* 784 */     Value[] arrayOfValue = new Value[i];
/* 785 */     for (byte b = 0; b < i; b++) {
/* 786 */       arrayOfValue[b] = readValue(paramByteBuffer, paramTypeInfo);
/*     */     }
/* 788 */     return arrayOfValue;
/*     */   }
/*     */   
/*     */   private static byte[] readVarBytes(ByteBuffer paramByteBuffer) {
/* 792 */     int i = DataUtils.readVarInt(paramByteBuffer);
/* 793 */     byte[] arrayOfByte = Utils.newBytes(i);
/* 794 */     paramByteBuffer.get(arrayOfByte, 0, i);
/* 795 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   private static long readTimestampTime(ByteBuffer paramByteBuffer) {
/* 799 */     return DataUtils.readVarLong(paramByteBuffer) * 1000000L + DataUtils.readVarInt(paramByteBuffer);
/*     */   }
/*     */   
/*     */   private static int readTimeZone(ByteBuffer paramByteBuffer) {
/* 803 */     byte b = paramByteBuffer.get();
/* 804 */     if (b == Byte.MAX_VALUE)
/* 805 */       return DataUtils.readVarInt(paramByteBuffer); 
/* 806 */     if (b == Byte.MIN_VALUE) {
/* 807 */       return -DataUtils.readVarInt(paramByteBuffer);
/*     */     }
/* 809 */     return b * 900;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 815 */     if (paramObject == this)
/* 816 */       return true; 
/* 817 */     if (!(paramObject instanceof ValueDataType)) {
/* 818 */       return false;
/*     */     }
/* 820 */     ValueDataType valueDataType = (ValueDataType)paramObject;
/* 821 */     if (!this.compareMode.equals(valueDataType.compareMode)) {
/* 822 */       return false;
/*     */     }
/* 824 */     int[] arrayOfInt1 = (this.rowFactory == null) ? null : this.rowFactory.getIndexes();
/* 825 */     int[] arrayOfInt2 = (valueDataType.rowFactory == null) ? null : valueDataType.rowFactory.getIndexes();
/* 826 */     return (Arrays.equals(this.sortTypes, valueDataType.sortTypes) && 
/* 827 */       Arrays.equals(arrayOfInt1, arrayOfInt2));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 832 */     int[] arrayOfInt = (this.rowFactory == null) ? null : this.rowFactory.getIndexes();
/* 833 */     return super.hashCode() ^ Arrays.hashCode(arrayOfInt) ^ this.compareMode
/* 834 */       .hashCode() ^ Arrays.hashCode(this.sortTypes);
/*     */   }
/*     */ 
/*     */   
/*     */   public void save(WriteBuffer paramWriteBuffer, MetaType<Database> paramMetaType) {
/* 839 */     writeIntArray(paramWriteBuffer, this.sortTypes);
/* 840 */     boolean bool = (this.rowFactory == null) ? false : this.rowFactory.getColumnCount();
/* 841 */     paramWriteBuffer.putVarInt(bool);
/* 842 */     int[] arrayOfInt = (this.rowFactory == null) ? null : this.rowFactory.getIndexes();
/* 843 */     writeIntArray(paramWriteBuffer, arrayOfInt);
/* 844 */     paramWriteBuffer.put((this.rowFactory == null || this.rowFactory.getRowDataType().isStoreKeys()) ? 1 : 0);
/*     */   }
/*     */   
/*     */   private static void writeIntArray(WriteBuffer paramWriteBuffer, int[] paramArrayOfint) {
/* 848 */     if (paramArrayOfint == null) {
/* 849 */       paramWriteBuffer.putVarInt(0);
/*     */     } else {
/* 851 */       paramWriteBuffer.putVarInt(paramArrayOfint.length + 1);
/* 852 */       for (int i : paramArrayOfint) {
/* 853 */         paramWriteBuffer.putVarInt(i);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Factory getFactory() {
/* 860 */     return FACTORY;
/*     */   }
/*     */   
/* 863 */   private static final Factory FACTORY = new Factory();
/*     */   
/*     */   public static final class Factory
/*     */     implements StatefulDataType.Factory<Database>
/*     */   {
/*     */     public DataType<?> create(ByteBuffer param1ByteBuffer, MetaType<Database> param1MetaType, Database param1Database) {
/* 869 */       int[] arrayOfInt1 = readIntArray(param1ByteBuffer);
/* 870 */       int i = DataUtils.readVarInt(param1ByteBuffer);
/* 871 */       int[] arrayOfInt2 = readIntArray(param1ByteBuffer);
/* 872 */       boolean bool = (param1ByteBuffer.get() != 0) ? true : false;
/* 873 */       CompareMode compareMode = (param1Database == null) ? CompareMode.getInstance(null, 0) : param1Database.getCompareMode();
/* 874 */       if (param1Database == null)
/* 875 */         return (DataType<?>)new ValueDataType(); 
/* 876 */       if (arrayOfInt1 == null) {
/* 877 */         return (DataType<?>)new ValueDataType(param1Database, null);
/*     */       }
/* 879 */       RowFactory rowFactory = RowFactory.getDefaultRowFactory().createRowFactory((CastDataProvider)param1Database, compareMode, (DataHandler)param1Database, arrayOfInt1, arrayOfInt2, null, i, bool);
/*     */       
/* 881 */       return (DataType<?>)rowFactory.getRowDataType();
/*     */     }
/*     */     
/*     */     private static int[] readIntArray(ByteBuffer param1ByteBuffer) {
/* 885 */       int i = DataUtils.readVarInt(param1ByteBuffer) - 1;
/* 886 */       if (i < 0) {
/* 887 */         return null;
/*     */       }
/* 889 */       int[] arrayOfInt = new int[i];
/* 890 */       for (byte b = 0; b < arrayOfInt.length; b++) {
/* 891 */         arrayOfInt[b] = DataUtils.readVarInt(param1ByteBuffer);
/*     */       }
/* 893 */       return arrayOfInt;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\ValueDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */