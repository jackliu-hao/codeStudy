/*      */ package org.h2.value;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.h2.api.IntervalQualifier;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.Mode;
/*      */ import org.h2.engine.SysProperties;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.store.DataHandler;
/*      */ import org.h2.util.Bits;
/*      */ import org.h2.util.DateTimeUtils;
/*      */ import org.h2.util.HasSQL;
/*      */ import org.h2.util.IntervalUtils;
/*      */ import org.h2.util.JdbcUtils;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.geometry.GeoJsonUtils;
/*      */ import org.h2.util.json.JsonConstructorUtils;
/*      */ import org.h2.value.lob.LobData;
/*      */ import org.h2.value.lob.LobDataInMemory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class Value
/*      */   extends VersionedValue<Value>
/*      */   implements HasSQL, Typed
/*      */ {
/*      */   public static final int UNKNOWN = -1;
/*      */   public static final int NULL = 0;
/*      */   public static final int CHAR = 1;
/*      */   public static final int VARCHAR = 2;
/*      */   public static final int CLOB = 3;
/*      */   public static final int VARCHAR_IGNORECASE = 4;
/*      */   public static final int BINARY = 5;
/*      */   public static final int VARBINARY = 6;
/*      */   public static final int BLOB = 7;
/*      */   public static final int BOOLEAN = 8;
/*      */   public static final int TINYINT = 9;
/*      */   public static final int SMALLINT = 10;
/*      */   public static final int INTEGER = 11;
/*      */   public static final int BIGINT = 12;
/*      */   public static final int NUMERIC = 13;
/*      */   public static final int REAL = 14;
/*      */   public static final int DOUBLE = 15;
/*      */   public static final int DECFLOAT = 16;
/*      */   public static final int DATE = 17;
/*      */   public static final int TIME = 18;
/*      */   public static final int TIME_TZ = 19;
/*      */   public static final int TIMESTAMP = 20;
/*      */   public static final int TIMESTAMP_TZ = 21;
/*      */   public static final int INTERVAL_YEAR = 22;
/*      */   public static final int INTERVAL_MONTH = 23;
/*      */   public static final int INTERVAL_DAY = 24;
/*      */   public static final int INTERVAL_HOUR = 25;
/*      */   public static final int INTERVAL_MINUTE = 26;
/*      */   public static final int INTERVAL_SECOND = 27;
/*      */   public static final int INTERVAL_YEAR_TO_MONTH = 28;
/*      */   public static final int INTERVAL_DAY_TO_HOUR = 29;
/*      */   public static final int INTERVAL_DAY_TO_MINUTE = 30;
/*      */   public static final int INTERVAL_DAY_TO_SECOND = 31;
/*      */   public static final int INTERVAL_HOUR_TO_MINUTE = 32;
/*      */   public static final int INTERVAL_HOUR_TO_SECOND = 33;
/*      */   public static final int INTERVAL_MINUTE_TO_SECOND = 34;
/*      */   public static final int JAVA_OBJECT = 35;
/*      */   public static final int ENUM = 36;
/*      */   public static final int GEOMETRY = 37;
/*      */   public static final int JSON = 38;
/*      */   public static final int UUID = 39;
/*      */   public static final int ARRAY = 40;
/*      */   public static final int ROW = 41;
/*      */   public static final int TYPE_COUNT = 42;
/*      */   static final int GROUP_NULL = 0;
/*      */   static final int GROUP_CHARACTER_STRING = 1;
/*      */   static final int GROUP_BINARY_STRING = 2;
/*      */   static final int GROUP_BOOLEAN = 3;
/*      */   static final int GROUP_NUMERIC = 4;
/*      */   static final int GROUP_DATETIME = 5;
/*      */   static final int GROUP_INTERVAL_YM = 6;
/*      */   static final int GROUP_INTERVAL_DT = 7;
/*      */   static final int GROUP_OTHER = 8;
/*      */   static final int GROUP_COLLECTION = 9;
/*  322 */   static final byte[] GROUPS = new byte[] { 0, 1, 1, 1, 1, 2, 2, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 7, 7, 7, 7, 6, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 9, 9 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  354 */   private static final String[] NAMES = new String[] { "UNKNOWN", "NULL", "CHARACTER", "CHARACTER VARYING", "CHARACTER LARGE OBJECT", "VARCHAR_IGNORECASE", "BINARY", "BINARY VARYING", "BINARY LARGE OBJECT", "BOOLEAN", "TINYINT", "SMALLINT", "INTEGER", "BIGINT", "NUMERIC", "REAL", "DOUBLE PRECISION", "DECFLOAT", "DATE", "TIME", "TIME WITH TIME ZONE", "TIMESTAMP", "TIMESTAMP WITH TIME ZONE", "INTERVAL YEAR", "INTERVAL MONTH", "INTERVAL DAY", "INTERVAL HOUR", "INTERVAL MINUTE", "INTERVAL SECOND", "INTERVAL YEAR TO MONTH", "INTERVAL DAY TO HOUR", "INTERVAL DAY TO MINUTE", "INTERVAL DAY TO SECOND", "INTERVAL HOUR TO MINUTE", "INTERVAL HOUR TO SECOND", "INTERVAL MINUTE TO SECOND", "JAVA_OBJECT", "ENUM", "GEOMETRY", "JSON", "UUID", "ARRAY", "ROW" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  375 */   public static final Value[] EMPTY_VALUES = new Value[0];
/*      */   
/*      */   private static SoftReference<Value[]> softCache;
/*      */   
/*  379 */   static final BigDecimal MAX_LONG_DECIMAL = BigDecimal.valueOf(Long.MAX_VALUE);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  384 */   public static final BigDecimal MIN_LONG_DECIMAL = BigDecimal.valueOf(Long.MIN_VALUE);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int CONVERT_TO = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int CAST_TO = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final int ASSIGN_TO = 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getTypeName(int paramInt) {
/*  412 */     return NAMES[paramInt + 1];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static void rangeCheck(long paramLong1, long paramLong2, long paramLong3) {
/*  423 */     if ((paramLong1 | paramLong2) < 0L || paramLong2 > paramLong3 - paramLong1) {
/*  424 */       if (paramLong1 < 0L || paramLong1 > paramLong3) {
/*  425 */         throw DbException.getInvalidValueException("offset", Long.valueOf(paramLong1 + 1L));
/*      */       }
/*  427 */       throw DbException.getInvalidValueException("length", Long.valueOf(paramLong2));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract TypeInfo getType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract int getValueType();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMemory() {
/*  451 */     return 24;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract int hashCode();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract boolean equals(Object paramObject);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getHigherOrder(int paramInt1, int paramInt2) {
/*  479 */     if (paramInt1 == paramInt2) {
/*  480 */       if (paramInt1 == -1) {
/*  481 */         throw DbException.get(50004, "?, ?");
/*      */       }
/*  483 */       return paramInt1;
/*      */     } 
/*  485 */     if (paramInt1 < paramInt2) {
/*  486 */       int i = paramInt1;
/*  487 */       paramInt1 = paramInt2;
/*  488 */       paramInt2 = i;
/*      */     } 
/*  490 */     if (paramInt1 == -1) {
/*  491 */       if (paramInt2 == 0) {
/*  492 */         throw DbException.get(50004, "?, NULL");
/*      */       }
/*  494 */       return paramInt2;
/*  495 */     }  if (paramInt2 == -1) {
/*  496 */       if (paramInt1 == 0) {
/*  497 */         throw DbException.get(50004, "NULL, ?");
/*      */       }
/*  499 */       return paramInt1;
/*      */     } 
/*  501 */     if (paramInt2 == 0) {
/*  502 */       return paramInt1;
/*      */     }
/*  504 */     return getHigherOrderKnown(paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */   static int getHigherOrderKnown(int paramInt1, int paramInt2) {
/*  508 */     byte b1 = GROUPS[paramInt1], b2 = GROUPS[paramInt2];
/*  509 */     switch (b1) {
/*      */       case 3:
/*  511 */         if (b2 == 2) {
/*  512 */           throw getDataTypeCombinationException(8, paramInt2);
/*      */         }
/*      */         break;
/*      */       case 4:
/*  516 */         return getHigherNumeric(paramInt1, paramInt2, b2);
/*      */       case 5:
/*  518 */         return getHigherDateTime(paramInt1, paramInt2, b2);
/*      */       case 6:
/*  520 */         return getHigherIntervalYearMonth(paramInt1, paramInt2, b2);
/*      */       case 7:
/*  522 */         return getHigherIntervalDayTime(paramInt1, paramInt2, b2);
/*      */       case 8:
/*  524 */         return getHigherOther(paramInt1, paramInt2, b2);
/*      */     } 
/*  526 */     return paramInt1;
/*      */   }
/*      */   
/*      */   private static int getHigherNumeric(int paramInt1, int paramInt2, int paramInt3) {
/*  530 */     if (paramInt3 == 4) {
/*  531 */       switch (paramInt1) {
/*      */         case 14:
/*  533 */           switch (paramInt2) {
/*      */             case 11:
/*  535 */               return 15;
/*      */             case 12:
/*      */             case 13:
/*  538 */               return 16;
/*      */           } 
/*      */           break;
/*      */         case 15:
/*  542 */           switch (paramInt2) {
/*      */             case 12:
/*      */             case 13:
/*  545 */               return 16;
/*      */           } 
/*      */           break;
/*      */       } 
/*  549 */     } else if (paramInt3 == 2) {
/*  550 */       throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */     } 
/*  552 */     return paramInt1;
/*      */   }
/*      */   
/*      */   private static int getHigherDateTime(int paramInt1, int paramInt2, int paramInt3) {
/*  556 */     if (paramInt3 == 1) {
/*  557 */       return paramInt1;
/*      */     }
/*  559 */     if (paramInt3 != 5) {
/*  560 */       throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */     }
/*  562 */     switch (paramInt1) {
/*      */       case 18:
/*  564 */         if (paramInt2 == 17) {
/*  565 */           return 20;
/*      */         }
/*      */         break;
/*      */       case 19:
/*  569 */         if (paramInt2 == 17) {
/*  570 */           return 21;
/*      */         }
/*      */         break;
/*      */       case 20:
/*  574 */         if (paramInt2 == 19)
/*  575 */           return 21; 
/*      */         break;
/*      */     } 
/*  578 */     return paramInt1;
/*      */   }
/*      */   
/*      */   private static int getHigherIntervalYearMonth(int paramInt1, int paramInt2, int paramInt3) {
/*  582 */     switch (paramInt3) {
/*      */       case 6:
/*  584 */         if (paramInt1 == 23 && paramInt2 == 22) {
/*  585 */           return 28;
/*      */         }
/*      */       
/*      */       case 1:
/*      */       case 4:
/*  590 */         return paramInt1;
/*      */     } 
/*  592 */     throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */   
/*      */   private static int getHigherIntervalDayTime(int paramInt1, int paramInt2, int paramInt3) {
/*  597 */     switch (paramInt3) {
/*      */       case 7:
/*      */         break;
/*      */       case 1:
/*      */       case 4:
/*  602 */         return paramInt1;
/*      */       default:
/*  604 */         throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */     } 
/*  606 */     switch (paramInt1) {
/*      */       case 25:
/*  608 */         return 29;
/*      */       case 26:
/*  610 */         if (paramInt2 == 24) {
/*  611 */           return 30;
/*      */         }
/*  613 */         return 32;
/*      */       case 27:
/*  615 */         if (paramInt2 == 24) {
/*  616 */           return 31;
/*      */         }
/*  618 */         if (paramInt2 == 25) {
/*  619 */           return 33;
/*      */         }
/*  621 */         return 34;
/*      */       case 29:
/*  623 */         if (paramInt2 == 26) {
/*  624 */           return 30;
/*      */         }
/*  626 */         if (paramInt2 == 27) {
/*  627 */           return 31;
/*      */         }
/*      */         break;
/*      */       case 30:
/*  631 */         if (paramInt2 == 27) {
/*  632 */           return 31;
/*      */         }
/*      */         break;
/*      */       case 32:
/*  636 */         switch (paramInt2) {
/*      */           case 24:
/*      */           case 29:
/*      */           case 30:
/*  640 */             return 30;
/*      */           case 27:
/*  642 */             return 33;
/*      */           case 31:
/*  644 */             return 31;
/*      */         } 
/*      */         break;
/*      */       case 33:
/*  648 */         switch (paramInt2) {
/*      */           case 24:
/*      */           case 29:
/*      */           case 30:
/*      */           case 31:
/*  653 */             return 31;
/*      */         } 
/*      */         break;
/*      */       case 34:
/*  657 */         switch (paramInt2) {
/*      */           case 24:
/*      */           case 29:
/*      */           case 30:
/*      */           case 31:
/*  662 */             return 31;
/*      */           case 25:
/*      */           case 32:
/*      */           case 33:
/*  666 */             return 33;
/*      */         }  break;
/*      */     } 
/*  669 */     return paramInt1;
/*      */   }
/*      */   
/*      */   private static int getHigherOther(int paramInt1, int paramInt2, int paramInt3) {
/*  673 */     switch (paramInt1) {
/*      */       case 35:
/*  675 */         if (paramInt3 != 2) {
/*  676 */           throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */         }
/*      */         break;
/*      */       case 36:
/*  680 */         if (paramInt3 != 1 && (paramInt3 != 4 || paramInt2 > 11)) {
/*  681 */           throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */         }
/*      */         break;
/*      */       case 37:
/*  685 */         if (paramInt3 != 1 && paramInt3 != 2) {
/*  686 */           throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */         }
/*      */         break;
/*      */       case 38:
/*  690 */         switch (paramInt3) {
/*      */           case 5:
/*      */           case 6:
/*      */           case 7:
/*      */           case 8:
/*  695 */             throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */         } 
/*      */         break;
/*      */       case 39:
/*  699 */         switch (paramInt3) {
/*      */           case 1:
/*      */           case 2:
/*      */             break;
/*      */           case 8:
/*  704 */             if (paramInt2 == 35);
/*      */             break;
/*      */         } 
/*      */ 
/*      */         
/*  709 */         throw getDataTypeCombinationException(paramInt1, paramInt2);
/*      */     } 
/*      */     
/*  712 */     return paramInt1;
/*      */   }
/*      */   
/*      */   private static DbException getDataTypeCombinationException(int paramInt1, int paramInt2) {
/*  716 */     return DbException.get(22018, getTypeName(paramInt1) + ", " + getTypeName(paramInt2));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static Value cache(Value paramValue) {
/*  728 */     if (SysProperties.OBJECT_CACHE) {
/*  729 */       int i = paramValue.hashCode();
/*      */       Value[] arrayOfValue;
/*  731 */       if (softCache == null || (arrayOfValue = softCache.get()) == null) {
/*  732 */         arrayOfValue = new Value[SysProperties.OBJECT_CACHE_SIZE];
/*  733 */         softCache = (SoftReference)new SoftReference<>(arrayOfValue);
/*      */       } 
/*  735 */       int j = i & SysProperties.OBJECT_CACHE_SIZE - 1;
/*  736 */       Value value = arrayOfValue[j];
/*  737 */       if (value != null && 
/*  738 */         value.getValueType() == paramValue.getValueType() && paramValue.equals(value))
/*      */       {
/*  740 */         return value;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  747 */       arrayOfValue[j] = paramValue;
/*      */     } 
/*  749 */     return paramValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void clearCache() {
/*  756 */     softCache = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract String getString();
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getReader() {
/*  767 */     return new StringReader(getString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Reader getReader(long paramLong1, long paramLong2) {
/*  778 */     String str = getString();
/*  779 */     long l = paramLong1 - 1L;
/*  780 */     rangeCheck(l, paramLong2, str.length());
/*  781 */     int i = (int)l;
/*  782 */     return new StringReader(str.substring(i, i + (int)paramLong2));
/*      */   }
/*      */   
/*      */   public byte[] getBytes() {
/*  786 */     throw getDataConversionError(6);
/*      */   }
/*      */   
/*      */   public byte[] getBytesNoCopy() {
/*  790 */     return getBytes();
/*      */   }
/*      */   
/*      */   public InputStream getInputStream() {
/*  794 */     return new ByteArrayInputStream(getBytesNoCopy());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getInputStream(long paramLong1, long paramLong2) {
/*  805 */     byte[] arrayOfByte = getBytesNoCopy();
/*  806 */     long l = paramLong1 - 1L;
/*  807 */     rangeCheck(l, paramLong2, arrayOfByte.length);
/*  808 */     return new ByteArrayInputStream(arrayOfByte, (int)l, (int)paramLong2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getBoolean() {
/*  822 */     return convertToBoolean().getBoolean();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte getByte() {
/*  834 */     return convertToTinyint(null).getByte();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short getShort() {
/*  846 */     return convertToSmallint(null).getShort();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInt() {
/*  858 */     return convertToInt(null).getInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLong() {
/*  870 */     return convertToBigint(null).getLong();
/*      */   }
/*      */   
/*      */   public BigDecimal getBigDecimal() {
/*  874 */     throw getDataConversionError(13);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float getFloat() {
/*  886 */     throw getDataConversionError(14);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double getDouble() {
/*  898 */     throw getDataConversionError(15);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value add(Value paramValue) {
/*  908 */     throw getUnsupportedExceptionForOperation("+");
/*      */   }
/*      */   
/*      */   public int getSignum() {
/*  912 */     throw getUnsupportedExceptionForOperation("SIGNUM");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value negate() {
/*  921 */     throw getUnsupportedExceptionForOperation("NEG");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value subtract(Value paramValue) {
/*  931 */     throw getUnsupportedExceptionForOperation("-");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value divide(Value paramValue, TypeInfo paramTypeInfo) {
/*  943 */     throw getUnsupportedExceptionForOperation("/");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value multiply(Value paramValue) {
/*  953 */     throw getUnsupportedExceptionForOperation("*");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Value modulus(Value paramValue) {
/*  963 */     throw getUnsupportedExceptionForOperation("%");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Value convertTo(int paramInt) {
/*  974 */     return convertTo(paramInt, (CastDataProvider)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Value convertTo(TypeInfo paramTypeInfo) {
/*  985 */     return convertTo(paramTypeInfo, null, 0, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Value convertTo(int paramInt, CastDataProvider paramCastDataProvider) {
/*  997 */     switch (paramInt) {
/*      */       case 40:
/*  999 */         return convertToAnyArray(paramCastDataProvider);
/*      */       case 41:
/* 1001 */         return convertToAnyRow();
/*      */     } 
/* 1003 */     return convertTo(TypeInfo.getTypeInfo(paramInt), paramCastDataProvider, 0, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Value convertTo(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider) {
/* 1018 */     return convertTo(paramTypeInfo, paramCastDataProvider, 0, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Value convertTo(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, Object paramObject) {
/* 1035 */     return convertTo(paramTypeInfo, paramCastDataProvider, 0, paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueArray convertToAnyArray(CastDataProvider paramCastDataProvider) {
/* 1046 */     if (getValueType() == 40) {
/* 1047 */       return (ValueArray)this;
/*      */     }
/* 1049 */     return ValueArray.get(getType(), new Value[] { this }, paramCastDataProvider);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueRow convertToAnyRow() {
/* 1058 */     if (getValueType() == 41) {
/* 1059 */       return (ValueRow)this;
/*      */     }
/* 1061 */     return ValueRow.get(new Value[] { this });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Value castTo(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider) {
/* 1075 */     return convertTo(paramTypeInfo, paramCastDataProvider, 1, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Value convertForAssignTo(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, Object paramObject) {
/* 1092 */     return convertTo(paramTypeInfo, paramCastDataProvider, 2, paramObject);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Value convertTo(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt, Object paramObject) {
/* 1105 */     int i = getValueType(); int j;
/* 1106 */     if (i == 0 || (i == (
/* 1107 */       j = paramTypeInfo.getValueType()) && paramInt == 0 && paramTypeInfo
/* 1108 */       .getExtTypeInfo() == null && i != 1)) {
/* 1109 */       return this;
/*      */     }
/* 1111 */     switch (j) {
/*      */       case 0:
/* 1113 */         return ValueNull.INSTANCE;
/*      */       case 1:
/* 1115 */         return convertToChar(paramTypeInfo, paramCastDataProvider, paramInt, paramObject);
/*      */       case 2:
/* 1117 */         return convertToVarchar(paramTypeInfo, paramCastDataProvider, paramInt, paramObject);
/*      */       case 3:
/* 1119 */         return convertToClob(paramTypeInfo, paramInt, paramObject);
/*      */       case 4:
/* 1121 */         return convertToVarcharIgnoreCase(paramTypeInfo, paramInt, paramObject);
/*      */       case 5:
/* 1123 */         return convertToBinary(paramTypeInfo, paramInt, paramObject);
/*      */       case 6:
/* 1125 */         return convertToVarbinary(paramTypeInfo, paramInt, paramObject);
/*      */       case 7:
/* 1127 */         return convertToBlob(paramTypeInfo, paramInt, paramObject);
/*      */       case 8:
/* 1129 */         return convertToBoolean();
/*      */       case 9:
/* 1131 */         return convertToTinyint(paramObject);
/*      */       case 10:
/* 1133 */         return convertToSmallint(paramObject);
/*      */       case 11:
/* 1135 */         return convertToInt(paramObject);
/*      */       case 12:
/* 1137 */         return convertToBigint(paramObject);
/*      */       case 13:
/* 1139 */         return convertToNumeric(paramTypeInfo, paramCastDataProvider, paramInt, paramObject);
/*      */       case 14:
/* 1141 */         return convertToReal();
/*      */       case 15:
/* 1143 */         return convertToDouble();
/*      */       case 16:
/* 1145 */         return convertToDecfloat(paramTypeInfo, paramInt);
/*      */       case 17:
/* 1147 */         return convertToDate(paramCastDataProvider);
/*      */       case 18:
/* 1149 */         return convertToTime(paramTypeInfo, paramCastDataProvider, paramInt);
/*      */       case 19:
/* 1151 */         return convertToTimeTimeZone(paramTypeInfo, paramCastDataProvider, paramInt);
/*      */       case 20:
/* 1153 */         return convertToTimestamp(paramTypeInfo, paramCastDataProvider, paramInt);
/*      */       case 21:
/* 1155 */         return convertToTimestampTimeZone(paramTypeInfo, paramCastDataProvider, paramInt);
/*      */       case 22:
/*      */       case 23:
/*      */       case 28:
/* 1159 */         return convertToIntervalYearMonth(paramTypeInfo, paramInt, paramObject);
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/* 1170 */         return convertToIntervalDayTime(paramTypeInfo, paramInt, paramObject);
/*      */       case 35:
/* 1172 */         return convertToJavaObject(paramTypeInfo, paramInt, paramObject);
/*      */       case 36:
/* 1174 */         return convertToEnum((ExtTypeInfoEnum)paramTypeInfo.getExtTypeInfo(), paramCastDataProvider);
/*      */       case 37:
/* 1176 */         return convertToGeometry((ExtTypeInfoGeometry)paramTypeInfo.getExtTypeInfo());
/*      */       case 38:
/* 1178 */         return convertToJson(paramTypeInfo, paramInt, paramObject);
/*      */       case 39:
/* 1180 */         return convertToUuid();
/*      */       case 40:
/* 1182 */         return convertToArray(paramTypeInfo, paramCastDataProvider, paramInt, paramObject);
/*      */       case 41:
/* 1184 */         return convertToRow(paramTypeInfo, paramCastDataProvider, paramInt, paramObject);
/*      */     } 
/* 1186 */     throw getDataConversionError(j);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ValueChar convertToChar() {
/* 1196 */     return convertToChar(TypeInfo.getTypeInfo(1), null, 0, null);
/*      */   }
/*      */ 
/*      */   
/*      */   private ValueChar convertToChar(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt, Object paramObject) {
/* 1201 */     int i = getValueType();
/* 1202 */     switch (i) {
/*      */       case 7:
/*      */       case 35:
/* 1205 */         throw getDataConversionError(paramTypeInfo.getValueType());
/*      */     } 
/* 1207 */     String str = getString();
/* 1208 */     int j = str.length(), k = j;
/* 1209 */     if (paramInt == 0) {
/* 1210 */       while (k > 0 && str.charAt(k - 1) == ' ') {
/* 1211 */         k--;
/*      */       }
/*      */     } else {
/* 1214 */       int m = MathUtils.convertLongToInt(paramTypeInfo.getPrecision());
/* 1215 */       if (paramCastDataProvider == null || (paramCastDataProvider.getMode()).charPadding == Mode.CharPadding.ALWAYS) {
/* 1216 */         if (k != m) {
/* 1217 */           if (k < m)
/* 1218 */             return ValueChar.get(StringUtils.pad(str, m, null, true)); 
/* 1219 */           if (paramInt == 1) {
/* 1220 */             k = m;
/*      */           } else {
/*      */             do {
/* 1223 */               if (str.charAt(--k) != ' ') {
/* 1224 */                 throw getValueTooLongException(paramTypeInfo, paramObject);
/*      */               }
/* 1226 */             } while (k > m);
/*      */           } 
/*      */         } 
/*      */       } else {
/* 1230 */         if (paramInt == 1 && k > m) {
/* 1231 */           k = m;
/*      */         }
/* 1233 */         while (k > 0 && str.charAt(k - 1) == ' ') {
/* 1234 */           k--;
/*      */         }
/* 1236 */         if (paramInt == 2 && k > m) {
/* 1237 */           throw getValueTooLongException(paramTypeInfo, paramObject);
/*      */         }
/*      */       } 
/*      */     } 
/* 1241 */     if (j != k) {
/* 1242 */       str = str.substring(0, k);
/* 1243 */     } else if (i == 1) {
/* 1244 */       return (ValueChar)this;
/*      */     } 
/* 1246 */     return ValueChar.get(str);
/*      */   }
/*      */   
/*      */   private Value convertToVarchar(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt, Object paramObject) {
/* 1250 */     int i = getValueType();
/* 1251 */     switch (i) {
/*      */       case 7:
/*      */       case 35:
/* 1254 */         throw getDataConversionError(paramTypeInfo.getValueType());
/*      */     } 
/* 1256 */     if (paramInt != 0) {
/* 1257 */       String str = getString();
/* 1258 */       int j = MathUtils.convertLongToInt(paramTypeInfo.getPrecision());
/* 1259 */       if (str.length() > j) {
/* 1260 */         if (paramInt != 1) {
/* 1261 */           throw getValueTooLongException(paramTypeInfo, paramObject);
/*      */         }
/* 1263 */         return ValueVarchar.get(str.substring(0, j), paramCastDataProvider);
/*      */       } 
/*      */     } 
/* 1266 */     return (i == 2) ? this : ValueVarchar.get(getString(), paramCastDataProvider);
/*      */   }
/*      */   private ValueClob convertToClob(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/*      */     ValueClob valueClob;
/*      */     LobData lobData;
/* 1271 */     switch (getValueType()) {
/*      */       case 3:
/* 1273 */         valueClob = (ValueClob)this;
/*      */         break;
/*      */       case 35:
/* 1276 */         throw getDataConversionError(paramTypeInfo.getValueType());
/*      */       case 7:
/* 1278 */         lobData = ((ValueBlob)this).lobData;
/*      */         
/* 1280 */         if (lobData instanceof LobDataInMemory) {
/* 1281 */           byte[] arrayOfByte1 = ((LobDataInMemory)lobData).getSmall();
/* 1282 */           byte[] arrayOfByte2 = (new String(arrayOfByte1, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
/* 1283 */           if (Arrays.equals(arrayOfByte2, arrayOfByte1)) {
/* 1284 */             arrayOfByte2 = arrayOfByte1;
/*      */           }
/* 1286 */           valueClob = ValueClob.createSmall(arrayOfByte2); break;
/*      */         } 
/* 1288 */         if (lobData instanceof org.h2.value.lob.LobDataDatabase) {
/* 1289 */           valueClob = lobData.getDataHandler().getLobStorage().createClob(getReader(), -1L);
/*      */           break;
/*      */         } 
/*      */ 
/*      */       
/*      */       default:
/* 1295 */         valueClob = ValueClob.createSmall(getString()); break;
/*      */     } 
/* 1297 */     if (paramInt != 0) {
/* 1298 */       if (paramInt == 1) {
/* 1299 */         valueClob = valueClob.convertPrecision(paramTypeInfo.getPrecision());
/* 1300 */       } else if (valueClob.charLength() > paramTypeInfo.getPrecision()) {
/* 1301 */         throw valueClob.getValueTooLongException(paramTypeInfo, paramObject);
/*      */       } 
/*      */     }
/* 1304 */     return valueClob;
/*      */   }
/*      */   
/*      */   private Value convertToVarcharIgnoreCase(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/* 1308 */     int i = getValueType();
/* 1309 */     switch (i) {
/*      */       case 7:
/*      */       case 35:
/* 1312 */         throw getDataConversionError(paramTypeInfo.getValueType());
/*      */     } 
/* 1314 */     if (paramInt != 0) {
/* 1315 */       String str = getString();
/* 1316 */       int j = MathUtils.convertLongToInt(paramTypeInfo.getPrecision());
/* 1317 */       if (str.length() > j) {
/* 1318 */         if (paramInt != 1) {
/* 1319 */           throw getValueTooLongException(paramTypeInfo, paramObject);
/*      */         }
/* 1321 */         return ValueVarcharIgnoreCase.get(str.substring(0, j));
/*      */       } 
/*      */     } 
/* 1324 */     return (i == 4) ? this : ValueVarcharIgnoreCase.get(getString());
/*      */   }
/*      */   
/*      */   private ValueBinary convertToBinary(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/*      */     ValueBinary valueBinary;
/* 1329 */     if (getValueType() == 5) {
/* 1330 */       valueBinary = (ValueBinary)this;
/*      */     } else {
/*      */       try {
/* 1333 */         valueBinary = ValueBinary.getNoCopy(getBytesNoCopy());
/* 1334 */       } catch (DbException dbException) {
/* 1335 */         if (dbException.getErrorCode() == 22018) {
/* 1336 */           throw getDataConversionError(5);
/*      */         }
/* 1338 */         throw dbException;
/*      */       } 
/*      */     } 
/* 1341 */     if (paramInt != 0) {
/* 1342 */       byte[] arrayOfByte = valueBinary.getBytesNoCopy();
/* 1343 */       int i = arrayOfByte.length;
/* 1344 */       int j = MathUtils.convertLongToInt(paramTypeInfo.getPrecision());
/* 1345 */       if (i != j) {
/* 1346 */         if (paramInt == 2 && i > j) {
/* 1347 */           throw valueBinary.getValueTooLongException(paramTypeInfo, paramObject);
/*      */         }
/* 1349 */         valueBinary = ValueBinary.getNoCopy(Arrays.copyOf(arrayOfByte, j));
/*      */       } 
/*      */     } 
/* 1352 */     return valueBinary;
/*      */   }
/*      */   
/*      */   private ValueVarbinary convertToVarbinary(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/*      */     ValueVarbinary valueVarbinary;
/* 1357 */     if (getValueType() == 6) {
/* 1358 */       valueVarbinary = (ValueVarbinary)this;
/*      */     } else {
/* 1360 */       valueVarbinary = ValueVarbinary.getNoCopy(getBytesNoCopy());
/*      */     } 
/* 1362 */     if (paramInt != 0) {
/* 1363 */       byte[] arrayOfByte = valueVarbinary.getBytesNoCopy();
/* 1364 */       int i = arrayOfByte.length;
/* 1365 */       int j = MathUtils.convertLongToInt(paramTypeInfo.getPrecision());
/* 1366 */       if (paramInt == 1) {
/* 1367 */         if (i > j) {
/* 1368 */           valueVarbinary = ValueVarbinary.getNoCopy(Arrays.copyOf(arrayOfByte, j));
/*      */         }
/* 1370 */       } else if (i > j) {
/* 1371 */         throw valueVarbinary.getValueTooLongException(paramTypeInfo, paramObject);
/*      */       } 
/*      */     } 
/* 1374 */     return valueVarbinary;
/*      */   }
/*      */   private ValueBlob convertToBlob(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/*      */     ValueBlob valueBlob;
/*      */     DataHandler dataHandler;
/* 1379 */     switch (getValueType()) {
/*      */       case 7:
/* 1381 */         valueBlob = (ValueBlob)this;
/*      */         break;
/*      */       case 3:
/* 1384 */         dataHandler = ((ValueLob)this).lobData.getDataHandler();
/* 1385 */         if (dataHandler != null) {
/* 1386 */           valueBlob = dataHandler.getLobStorage().createBlob(getInputStream(), -1L);
/*      */           break;
/*      */         } 
/*      */       
/*      */       default:
/*      */         try {
/* 1392 */           valueBlob = ValueBlob.createSmall(getBytesNoCopy());
/* 1393 */         } catch (DbException dbException) {
/* 1394 */           if (dbException.getErrorCode() == 22018) {
/* 1395 */             throw getDataConversionError(7);
/*      */           }
/* 1397 */           throw dbException;
/*      */         } 
/*      */         break;
/*      */     } 
/* 1401 */     if (paramInt != 0) {
/* 1402 */       if (paramInt == 1) {
/* 1403 */         valueBlob = valueBlob.convertPrecision(paramTypeInfo.getPrecision());
/* 1404 */       } else if (valueBlob.octetLength() > paramTypeInfo.getPrecision()) {
/* 1405 */         throw valueBlob.getValueTooLongException(paramTypeInfo, paramObject);
/*      */       } 
/*      */     }
/* 1408 */     return valueBlob;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueBoolean convertToBoolean() {
/* 1418 */     switch (getValueType())
/*      */     { case 8:
/* 1420 */         return (ValueBoolean)this;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 1424 */         return ValueBoolean.get(getBoolean());
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 13:
/*      */       case 14:
/*      */       case 15:
/*      */       case 16:
/* 1433 */         return ValueBoolean.get((getSignum() != 0));
/*      */       default:
/* 1435 */         throw getDataConversionError(8);
/*      */       case 0:
/* 1437 */         break; }  throw DbException.getInternalError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueTinyint convertToTinyint(Object paramObject) {
/*      */     byte[] arrayOfByte;
/* 1451 */     switch (getValueType())
/*      */     { case 9:
/* 1453 */         return (ValueTinyint)this;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 8:
/* 1458 */         return ValueTinyint.get(getByte());
/*      */       case 10:
/*      */       case 11:
/*      */       case 36:
/* 1462 */         return ValueTinyint.get(convertToByte(getInt(), paramObject));
/*      */       case 12:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/* 1477 */         return ValueTinyint.get(convertToByte(getLong(), paramObject));
/*      */       case 13:
/*      */       case 16:
/* 1480 */         return ValueTinyint.get(convertToByte(convertToLong(getBigDecimal(), paramObject), paramObject));
/*      */       case 14:
/*      */       case 15:
/* 1483 */         return ValueTinyint.get(convertToByte(convertToLong(getDouble(), paramObject), paramObject));
/*      */       case 5:
/*      */       case 6:
/* 1486 */         arrayOfByte = getBytesNoCopy();
/* 1487 */         if (arrayOfByte.length == 1) {
/* 1488 */           return ValueTinyint.get(arrayOfByte[0]);
/*      */         }
/*      */ 
/*      */       
/*      */       default:
/* 1493 */         throw getDataConversionError(9);
/*      */       case 0:
/* 1495 */         break; }  throw DbException.getInternalError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueSmallint convertToSmallint(Object paramObject) {
/*      */     byte[] arrayOfByte;
/* 1508 */     switch (getValueType())
/*      */     { case 10:
/* 1510 */         return (ValueSmallint)this;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 8:
/*      */       case 9:
/* 1516 */         return ValueSmallint.get(getShort());
/*      */       case 11:
/*      */       case 36:
/* 1519 */         return ValueSmallint.get(convertToShort(getInt(), paramObject));
/*      */       case 12:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/* 1534 */         return ValueSmallint.get(convertToShort(getLong(), paramObject));
/*      */       case 13:
/*      */       case 16:
/* 1537 */         return ValueSmallint.get(convertToShort(convertToLong(getBigDecimal(), paramObject), paramObject));
/*      */       case 14:
/*      */       case 15:
/* 1540 */         return ValueSmallint.get(convertToShort(convertToLong(getDouble(), paramObject), paramObject));
/*      */       case 5:
/*      */       case 6:
/* 1543 */         arrayOfByte = getBytesNoCopy();
/* 1544 */         if (arrayOfByte.length == 2) {
/* 1545 */           return ValueSmallint.get((short)((arrayOfByte[0] << 8) + (arrayOfByte[1] & 0xFF)));
/*      */         }
/*      */ 
/*      */       
/*      */       default:
/* 1550 */         throw getDataConversionError(10);
/*      */       case 0:
/* 1552 */         break; }  throw DbException.getInternalError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueInteger convertToInt(Object paramObject) {
/*      */     byte[] arrayOfByte;
/* 1565 */     switch (getValueType())
/*      */     { case 11:
/* 1567 */         return (ValueInteger)this;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 36:
/* 1575 */         return ValueInteger.get(getInt());
/*      */       case 12:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/* 1590 */         return ValueInteger.get(convertToInt(getLong(), paramObject));
/*      */       case 13:
/*      */       case 16:
/* 1593 */         return ValueInteger.get(convertToInt(convertToLong(getBigDecimal(), paramObject), paramObject));
/*      */       case 14:
/*      */       case 15:
/* 1596 */         return ValueInteger.get(convertToInt(convertToLong(getDouble(), paramObject), paramObject));
/*      */       case 5:
/*      */       case 6:
/* 1599 */         arrayOfByte = getBytesNoCopy();
/* 1600 */         if (arrayOfByte.length == 4) {
/* 1601 */           return ValueInteger.get(Bits.readInt(arrayOfByte, 0));
/*      */         }
/*      */ 
/*      */       
/*      */       default:
/* 1606 */         throw getDataConversionError(11);
/*      */       case 0:
/* 1608 */         break; }  throw DbException.getInternalError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueBigint convertToBigint(Object paramObject) {
/*      */     byte[] arrayOfByte;
/* 1621 */     switch (getValueType())
/*      */     { case 12:
/* 1623 */         return (ValueBigint)this;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/*      */       case 36:
/* 1645 */         return ValueBigint.get(getLong());
/*      */       case 13:
/*      */       case 16:
/* 1648 */         return ValueBigint.get(convertToLong(getBigDecimal(), paramObject));
/*      */       case 14:
/*      */       case 15:
/* 1651 */         return ValueBigint.get(convertToLong(getDouble(), paramObject));
/*      */       case 5:
/*      */       case 6:
/* 1654 */         arrayOfByte = getBytesNoCopy();
/* 1655 */         if (arrayOfByte.length == 8) {
/* 1656 */           return ValueBigint.get(Bits.readLong(arrayOfByte, 0));
/*      */         }
/*      */ 
/*      */       
/*      */       default:
/* 1661 */         throw getDataConversionError(12);
/*      */       case 0:
/* 1663 */         break; }  throw DbException.getInternalError();
/*      */   }
/*      */   private ValueNumeric convertToNumeric(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt, Object paramObject) {
/*      */     ValueNumeric valueNumeric;
/*      */     BigDecimal bigDecimal;
/*      */     int i;
/*      */     int j;
/* 1670 */     switch (getValueType()) {
/*      */       case 13:
/* 1672 */         valueNumeric = (ValueNumeric)this;
/*      */         break;
/*      */       case 8:
/* 1675 */         valueNumeric = getBoolean() ? ValueNumeric.ONE : ValueNumeric.ZERO;
/*      */         break;
/*      */       default:
/* 1678 */         bigDecimal = getBigDecimal();
/* 1679 */         i = paramTypeInfo.getScale();
/* 1680 */         j = bigDecimal.scale();
/* 1681 */         if (j < 0 || j > 100000 || (paramInt != 0 && j != i && (j >= i || 
/* 1682 */           !(paramCastDataProvider.getMode()).convertOnlyToSmallerScale))) {
/* 1683 */           bigDecimal = ValueNumeric.setScale(bigDecimal, i);
/*      */         }
/* 1685 */         if (paramInt != 0 && bigDecimal
/* 1686 */           .precision() > paramTypeInfo.getPrecision() - i + bigDecimal.scale()) {
/* 1687 */           throw getValueTooLongException(paramTypeInfo, paramObject);
/*      */         }
/* 1689 */         return ValueNumeric.get(bigDecimal);
/*      */       
/*      */       case 0:
/* 1692 */         throw DbException.getInternalError();
/*      */     } 
/* 1694 */     if (paramInt != 0) {
/* 1695 */       int k = paramTypeInfo.getScale();
/* 1696 */       BigDecimal bigDecimal1 = valueNumeric.getBigDecimal();
/* 1697 */       j = bigDecimal1.scale();
/* 1698 */       if (j != k && (j >= k || !(paramCastDataProvider.getMode()).convertOnlyToSmallerScale)) {
/* 1699 */         valueNumeric = ValueNumeric.get(ValueNumeric.setScale(bigDecimal1, k));
/*      */       }
/* 1701 */       BigDecimal bigDecimal2 = valueNumeric.getBigDecimal();
/* 1702 */       if (bigDecimal2.precision() > paramTypeInfo.getPrecision() - k + bigDecimal2.scale()) {
/* 1703 */         throw valueNumeric.getValueTooLongException(paramTypeInfo, paramObject);
/*      */       }
/*      */     } 
/* 1706 */     return valueNumeric;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueReal convertToReal() {
/* 1715 */     switch (getValueType())
/*      */     { case 14:
/* 1717 */         return (ValueReal)this;
/*      */       case 8:
/* 1719 */         return getBoolean() ? ValueReal.ONE : ValueReal.ZERO;
/*      */       default:
/* 1721 */         return ValueReal.get(getFloat());
/*      */       case 0:
/* 1723 */         break; }  throw DbException.getInternalError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueDouble convertToDouble() {
/* 1733 */     switch (getValueType())
/*      */     { case 15:
/* 1735 */         return (ValueDouble)this;
/*      */       case 8:
/* 1737 */         return getBoolean() ? ValueDouble.ONE : ValueDouble.ZERO;
/*      */       default:
/* 1739 */         return ValueDouble.get(getDouble());
/*      */       case 0:
/* 1741 */         break; }  throw DbException.getInternalError();
/*      */   } private ValueDecfloat convertToDecfloat(TypeInfo paramTypeInfo, int paramInt) {
/*      */     ValueDecfloat valueDecfloat;
/*      */     String str;
/*      */     float f;
/*      */     double d;
/* 1747 */     switch (getValueType()) {
/*      */       case 16:
/* 1749 */         valueDecfloat = (ValueDecfloat)this;
/* 1750 */         if (valueDecfloat.value == null) {
/* 1751 */           return valueDecfloat;
/*      */         }
/*      */         break;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 1757 */         str = getString().trim();
/*      */         try {
/* 1759 */           valueDecfloat = ValueDecfloat.get(new BigDecimal(str));
/* 1760 */         } catch (NumberFormatException numberFormatException) {
/* 1761 */           switch (str) {
/*      */             case "-Infinity":
/* 1763 */               return ValueDecfloat.NEGATIVE_INFINITY;
/*      */             case "Infinity":
/*      */             case "+Infinity":
/* 1766 */               return ValueDecfloat.POSITIVE_INFINITY;
/*      */             case "NaN":
/*      */             case "-NaN":
/*      */             case "+NaN":
/* 1770 */               return ValueDecfloat.NAN;
/*      */           } 
/* 1772 */           throw getDataConversionError(16);
/*      */         } 
/*      */         break;
/*      */ 
/*      */       
/*      */       case 8:
/* 1778 */         valueDecfloat = getBoolean() ? ValueDecfloat.ONE : ValueDecfloat.ZERO;
/*      */         break;
/*      */       case 14:
/* 1781 */         f = getFloat();
/* 1782 */         if (Float.isFinite(f)) {
/* 1783 */           valueDecfloat = ValueDecfloat.get(new BigDecimal(Float.toString(f))); break;
/* 1784 */         }  if (f == Float.POSITIVE_INFINITY)
/* 1785 */           return ValueDecfloat.POSITIVE_INFINITY; 
/* 1786 */         if (f == Float.NEGATIVE_INFINITY) {
/* 1787 */           return ValueDecfloat.NEGATIVE_INFINITY;
/*      */         }
/* 1789 */         return ValueDecfloat.NAN;
/*      */ 
/*      */ 
/*      */       
/*      */       case 15:
/* 1794 */         d = getDouble();
/* 1795 */         if (Double.isFinite(d)) {
/* 1796 */           valueDecfloat = ValueDecfloat.get(new BigDecimal(Double.toString(d))); break;
/* 1797 */         }  if (d == Double.POSITIVE_INFINITY)
/* 1798 */           return ValueDecfloat.POSITIVE_INFINITY; 
/* 1799 */         if (d == Double.NEGATIVE_INFINITY) {
/* 1800 */           return ValueDecfloat.NEGATIVE_INFINITY;
/*      */         }
/* 1802 */         return ValueDecfloat.NAN;
/*      */ 
/*      */ 
/*      */       
/*      */       default:
/*      */         try {
/* 1808 */           valueDecfloat = ValueDecfloat.get(getBigDecimal());
/* 1809 */         } catch (DbException dbException) {
/* 1810 */           if (dbException.getErrorCode() == 22018) {
/* 1811 */             throw getDataConversionError(16);
/*      */           }
/* 1813 */           throw dbException;
/*      */         } 
/*      */         break;
/*      */       case 0:
/* 1817 */         throw DbException.getInternalError();
/*      */     } 
/* 1819 */     if (paramInt != 0) {
/* 1820 */       BigDecimal bigDecimal = valueDecfloat.value;
/* 1821 */       int i = bigDecimal.precision(), j = (int)paramTypeInfo.getPrecision();
/* 1822 */       if (i > j) {
/* 1823 */         valueDecfloat = ValueDecfloat.get(bigDecimal.setScale(bigDecimal.scale() - i + j, RoundingMode.HALF_UP));
/*      */       }
/*      */     } 
/* 1826 */     return valueDecfloat;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueDate convertToDate(CastDataProvider paramCastDataProvider) {
/*      */     ValueTimestampTimeZone valueTimestampTimeZone;
/*      */     long l1;
/*      */     long l2;
/* 1837 */     switch (getValueType())
/*      */     { case 17:
/* 1839 */         return (ValueDate)this;
/*      */       case 20:
/* 1841 */         return ValueDate.fromDateValue(((ValueTimestamp)this).getDateValue());
/*      */       case 21:
/* 1843 */         valueTimestampTimeZone = (ValueTimestampTimeZone)this;
/* 1844 */         l1 = valueTimestampTimeZone.getTimeNanos();
/* 1845 */         l2 = DateTimeUtils.getEpochSeconds(valueTimestampTimeZone.getDateValue(), l1, valueTimestampTimeZone
/* 1846 */             .getTimeZoneOffsetSeconds());
/* 1847 */         return ValueDate.fromDateValue(
/* 1848 */             DateTimeUtils.dateValueFromLocalSeconds(l2 + paramCastDataProvider
/* 1849 */               .currentTimeZone().getTimeZoneOffsetUTC(l2)));
/*      */       
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 1854 */         return ValueDate.parse(getString().trim());
/*      */       default:
/* 1856 */         throw getDataConversionError(17);
/*      */       case 0:
/* 1858 */         break; }  throw DbException.getInternalError();
/*      */   } private ValueTime convertToTime(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt) {
/*      */     ValueTime valueTime;
/*      */     ValueTimestampTimeZone valueTimestampTimeZone;
/*      */     long l1;
/*      */     long l2;
/* 1864 */     switch (getValueType()) {
/*      */       case 18:
/* 1866 */         valueTime = (ValueTime)this;
/*      */         break;
/*      */       case 19:
/* 1869 */         valueTime = ValueTime.fromNanos(getLocalTimeNanos(paramCastDataProvider));
/*      */         break;
/*      */       case 20:
/* 1872 */         valueTime = ValueTime.fromNanos(((ValueTimestamp)this).getTimeNanos());
/*      */         break;
/*      */       case 21:
/* 1875 */         valueTimestampTimeZone = (ValueTimestampTimeZone)this;
/* 1876 */         l1 = valueTimestampTimeZone.getTimeNanos();
/* 1877 */         l2 = DateTimeUtils.getEpochSeconds(valueTimestampTimeZone.getDateValue(), l1, valueTimestampTimeZone
/* 1878 */             .getTimeZoneOffsetSeconds());
/* 1879 */         valueTime = ValueTime.fromNanos(
/* 1880 */             DateTimeUtils.nanosFromLocalSeconds(l2 + paramCastDataProvider
/* 1881 */               .currentTimeZone().getTimeZoneOffsetUTC(l2)) + l1 % 1000000000L);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 1888 */         valueTime = ValueTime.parse(getString().trim());
/*      */         break;
/*      */       default:
/* 1891 */         throw getDataConversionError(18);
/*      */     } 
/* 1893 */     if (paramInt != 0) {
/* 1894 */       int i = paramTypeInfo.getScale();
/* 1895 */       if (i < 9) {
/* 1896 */         l1 = valueTime.getNanos();
/* 1897 */         l2 = DateTimeUtils.convertScale(l1, i, 86400000000000L);
/* 1898 */         if (l2 != l1) {
/* 1899 */           valueTime = ValueTime.fromNanos(l2);
/*      */         }
/*      */       } 
/*      */     } 
/* 1903 */     return valueTime;
/*      */   }
/*      */   private ValueTimeTimeZone convertToTimeTimeZone(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt) { ValueTimeTimeZone valueTimeTimeZone;
/*      */     ValueTimestamp valueTimestamp;
/*      */     ValueTimestampTimeZone valueTimestampTimeZone;
/*      */     long l;
/* 1909 */     switch (getValueType()) {
/*      */       case 19:
/* 1911 */         valueTimeTimeZone = (ValueTimeTimeZone)this;
/*      */         break;
/*      */       case 18:
/* 1914 */         valueTimeTimeZone = ValueTimeTimeZone.fromNanos(((ValueTime)this).getNanos(), paramCastDataProvider
/* 1915 */             .currentTimestamp().getTimeZoneOffsetSeconds());
/*      */         break;
/*      */       case 20:
/* 1918 */         valueTimestamp = (ValueTimestamp)this;
/* 1919 */         l = valueTimestamp.getTimeNanos();
/* 1920 */         valueTimeTimeZone = ValueTimeTimeZone.fromNanos(l, paramCastDataProvider
/* 1921 */             .currentTimeZone().getTimeZoneOffsetLocal(valueTimestamp.getDateValue(), l));
/*      */         break;
/*      */       
/*      */       case 21:
/* 1925 */         valueTimestampTimeZone = (ValueTimestampTimeZone)this;
/* 1926 */         valueTimeTimeZone = ValueTimeTimeZone.fromNanos(valueTimestampTimeZone.getTimeNanos(), valueTimestampTimeZone.getTimeZoneOffsetSeconds());
/*      */         break;
/*      */       
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 1932 */         valueTimeTimeZone = ValueTimeTimeZone.parse(getString().trim());
/*      */         break;
/*      */       default:
/* 1935 */         throw getDataConversionError(19);
/*      */     } 
/* 1937 */     if (paramInt != 0) {
/* 1938 */       int i = paramTypeInfo.getScale();
/* 1939 */       if (i < 9) {
/* 1940 */         l = valueTimeTimeZone.getNanos();
/* 1941 */         long l1 = DateTimeUtils.convertScale(l, i, 86400000000000L);
/* 1942 */         if (l1 != l) {
/* 1943 */           valueTimeTimeZone = ValueTimeTimeZone.fromNanos(l1, valueTimeTimeZone.getTimeZoneOffsetSeconds());
/*      */         }
/*      */       } 
/*      */     } 
/* 1947 */     return valueTimeTimeZone; } private ValueTimestamp convertToTimestamp(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt) {
/*      */     ValueTimestamp valueTimestamp;
/*      */     ValueTimestampTimeZone valueTimestampTimeZone;
/*      */     long l1;
/*      */     long l2;
/* 1952 */     switch (getValueType()) {
/*      */       case 20:
/* 1954 */         valueTimestamp = (ValueTimestamp)this;
/*      */         break;
/*      */       case 18:
/* 1957 */         valueTimestamp = ValueTimestamp.fromDateValueAndNanos(paramCastDataProvider.currentTimestamp().getDateValue(), ((ValueTime)this)
/* 1958 */             .getNanos());
/*      */         break;
/*      */       case 19:
/* 1961 */         valueTimestamp = ValueTimestamp.fromDateValueAndNanos(paramCastDataProvider.currentTimestamp().getDateValue(), 
/* 1962 */             getLocalTimeNanos(paramCastDataProvider));
/*      */         break;
/*      */       
/*      */       case 17:
/* 1966 */         return ValueTimestamp.fromDateValueAndNanos(((ValueDate)this).getDateValue(), 0L);
/*      */       case 21:
/* 1968 */         valueTimestampTimeZone = (ValueTimestampTimeZone)this;
/* 1969 */         l1 = valueTimestampTimeZone.getTimeNanos();
/* 1970 */         l2 = DateTimeUtils.getEpochSeconds(valueTimestampTimeZone.getDateValue(), l1, valueTimestampTimeZone
/* 1971 */             .getTimeZoneOffsetSeconds());
/* 1972 */         l2 += paramCastDataProvider.currentTimeZone().getTimeZoneOffsetUTC(l2);
/* 1973 */         valueTimestamp = ValueTimestamp.fromDateValueAndNanos(DateTimeUtils.dateValueFromLocalSeconds(l2), 
/* 1974 */             DateTimeUtils.nanosFromLocalSeconds(l2) + l1 % 1000000000L);
/*      */         break;
/*      */       
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 1980 */         valueTimestamp = ValueTimestamp.parse(getString().trim(), paramCastDataProvider);
/*      */         break;
/*      */       default:
/* 1983 */         throw getDataConversionError(20);
/*      */     } 
/* 1985 */     if (paramInt != 0) {
/* 1986 */       int i = paramTypeInfo.getScale();
/* 1987 */       if (i < 9) {
/* 1988 */         l1 = valueTimestamp.getDateValue(); l2 = valueTimestamp.getTimeNanos();
/* 1989 */         long l = DateTimeUtils.convertScale(l2, i, (l1 == 512000000415L) ? 86400000000000L : Long.MAX_VALUE);
/*      */         
/* 1991 */         if (l != l2) {
/* 1992 */           if (l >= 86400000000000L) {
/* 1993 */             l -= 86400000000000L;
/* 1994 */             l1 = DateTimeUtils.incrementDateValue(l1);
/*      */           } 
/* 1996 */           valueTimestamp = ValueTimestamp.fromDateValueAndNanos(l1, l);
/*      */         } 
/*      */       } 
/*      */     } 
/* 2000 */     return valueTimestamp;
/*      */   }
/*      */   
/*      */   private long getLocalTimeNanos(CastDataProvider paramCastDataProvider) {
/* 2004 */     ValueTimeTimeZone valueTimeTimeZone = (ValueTimeTimeZone)this;
/* 2005 */     int i = paramCastDataProvider.currentTimestamp().getTimeZoneOffsetSeconds();
/* 2006 */     return DateTimeUtils.normalizeNanosOfDay(valueTimeTimeZone.getNanos() + (valueTimeTimeZone
/* 2007 */         .getTimeZoneOffsetSeconds() - i) * 86400000000000L); } private ValueTimestampTimeZone convertToTimestampTimeZone(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt) { ValueTimestampTimeZone valueTimestampTimeZone; long l2; ValueTimeTimeZone valueTimeTimeZone;
/*      */     long l1;
/*      */     ValueTimestamp valueTimestamp;
/*      */     long l3;
/*      */     long l4;
/*      */     long l5;
/* 2013 */     switch (getValueType()) {
/*      */       case 21:
/* 2015 */         valueTimestampTimeZone = (ValueTimestampTimeZone)this;
/*      */         break;
/*      */       case 18:
/* 2018 */         l2 = paramCastDataProvider.currentTimestamp().getDateValue();
/* 2019 */         l4 = ((ValueTime)this).getNanos();
/* 2020 */         valueTimestampTimeZone = ValueTimestampTimeZone.fromDateValueAndNanos(l2, l4, paramCastDataProvider
/* 2021 */             .currentTimeZone().getTimeZoneOffsetLocal(l2, l4));
/*      */         break;
/*      */       
/*      */       case 19:
/* 2025 */         valueTimeTimeZone = (ValueTimeTimeZone)this;
/* 2026 */         valueTimestampTimeZone = ValueTimestampTimeZone.fromDateValueAndNanos(paramCastDataProvider.currentTimestamp().getDateValue(), valueTimeTimeZone
/* 2027 */             .getNanos(), valueTimeTimeZone.getTimeZoneOffsetSeconds());
/*      */         break;
/*      */       
/*      */       case 17:
/* 2031 */         l1 = ((ValueDate)this).getDateValue();
/*      */         
/* 2033 */         return ValueTimestampTimeZone.fromDateValueAndNanos(l1, 0L, paramCastDataProvider
/* 2034 */             .currentTimeZone().getTimeZoneOffsetLocal(l1, 0L));
/*      */       
/*      */       case 20:
/* 2037 */         valueTimestamp = (ValueTimestamp)this;
/* 2038 */         l3 = valueTimestamp.getDateValue();
/* 2039 */         l5 = valueTimestamp.getTimeNanos();
/* 2040 */         valueTimestampTimeZone = ValueTimestampTimeZone.fromDateValueAndNanos(l3, l5, paramCastDataProvider
/* 2041 */             .currentTimeZone().getTimeZoneOffsetLocal(l3, l5));
/*      */         break;
/*      */       
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 2047 */         valueTimestampTimeZone = ValueTimestampTimeZone.parse(getString().trim(), paramCastDataProvider);
/*      */         break;
/*      */       default:
/* 2050 */         throw getDataConversionError(21);
/*      */     } 
/* 2052 */     if (paramInt != 0) {
/* 2053 */       int i = paramTypeInfo.getScale();
/* 2054 */       if (i < 9) {
/* 2055 */         l3 = valueTimestampTimeZone.getDateValue();
/* 2056 */         l5 = valueTimestampTimeZone.getTimeNanos();
/* 2057 */         long l = DateTimeUtils.convertScale(l5, i, (l3 == 512000000415L) ? 86400000000000L : Long.MAX_VALUE);
/*      */         
/* 2059 */         if (l != l5) {
/* 2060 */           if (l >= 86400000000000L) {
/* 2061 */             l -= 86400000000000L;
/* 2062 */             l3 = DateTimeUtils.incrementDateValue(l3);
/*      */           } 
/* 2064 */           valueTimestampTimeZone = ValueTimestampTimeZone.fromDateValueAndNanos(l3, l, valueTimestampTimeZone.getTimeZoneOffsetSeconds());
/*      */         } 
/*      */       } 
/*      */     } 
/* 2068 */     return valueTimestampTimeZone; }
/*      */ 
/*      */   
/*      */   private ValueInterval convertToIntervalYearMonth(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/* 2072 */     ValueInterval valueInterval = convertToIntervalYearMonth(paramTypeInfo.getValueType(), paramObject);
/* 2073 */     if (paramInt != 0 && 
/* 2074 */       !valueInterval.checkPrecision(paramTypeInfo.getPrecision())) {
/* 2075 */       throw valueInterval.getValueTooLongException(paramTypeInfo, paramObject);
/*      */     }
/*      */     
/* 2078 */     return valueInterval;
/*      */   }
/*      */   private ValueInterval convertToIntervalYearMonth(int paramInt, Object paramObject) {
/*      */     long l;
/*      */     String str;
/* 2083 */     switch (getValueType()) {
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/* 2087 */         l = getInt();
/*      */         break;
/*      */       case 12:
/* 2090 */         l = getLong();
/*      */         break;
/*      */       case 14:
/*      */       case 15:
/* 2094 */         if (paramInt == 28) {
/* 2095 */           return IntervalUtils.intervalFromAbsolute(IntervalQualifier.YEAR_TO_MONTH, getBigDecimal()
/* 2096 */               .multiply(BigDecimal.valueOf(12L)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
/*      */         }
/* 2098 */         l = convertToLong(getDouble(), paramObject);
/*      */         break;
/*      */       case 13:
/*      */       case 16:
/* 2102 */         if (paramInt == 28) {
/* 2103 */           return IntervalUtils.intervalFromAbsolute(IntervalQualifier.YEAR_TO_MONTH, getBigDecimal()
/* 2104 */               .multiply(BigDecimal.valueOf(12L)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
/*      */         }
/* 2106 */         l = convertToLong(getBigDecimal(), paramObject);
/*      */         break;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 2111 */         str = getString();
/*      */         try {
/* 2113 */           return 
/* 2114 */             (ValueInterval)IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(paramInt - 22), str)
/* 2115 */             .convertTo(paramInt);
/* 2116 */         } catch (Exception exception) {
/* 2117 */           throw DbException.get(22007, exception, new String[] { "INTERVAL", str });
/*      */         } 
/*      */       
/*      */       case 22:
/*      */       case 23:
/*      */       case 28:
/* 2123 */         return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(paramInt - 22), 
/* 2124 */             IntervalUtils.intervalToAbsolute((ValueInterval)this));
/*      */       default:
/* 2126 */         throw getDataConversionError(paramInt);
/*      */     } 
/* 2128 */     boolean bool = false;
/* 2129 */     if (l < 0L) {
/* 2130 */       bool = true;
/* 2131 */       l = -l;
/*      */     } 
/* 2133 */     return ValueInterval.from(IntervalQualifier.valueOf(paramInt - 22), bool, l, 0L);
/*      */   }
/*      */ 
/*      */   
/*      */   private ValueInterval convertToIntervalDayTime(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/* 2138 */     ValueInterval valueInterval = convertToIntervalDayTime(paramTypeInfo.getValueType(), paramObject);
/* 2139 */     if (paramInt != 0) {
/* 2140 */       valueInterval = valueInterval.setPrecisionAndScale(paramTypeInfo, paramObject);
/*      */     }
/* 2142 */     return valueInterval;
/*      */   }
/*      */   private ValueInterval convertToIntervalDayTime(int paramInt, Object paramObject) {
/*      */     long l;
/*      */     String str;
/* 2147 */     switch (getValueType()) {
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/* 2151 */         l = getInt();
/*      */         break;
/*      */       case 12:
/* 2154 */         l = getLong();
/*      */         break;
/*      */       case 14:
/*      */       case 15:
/* 2158 */         if (paramInt > 26) {
/* 2159 */           return convertToIntervalDayTime(getBigDecimal(), paramInt);
/*      */         }
/* 2161 */         l = convertToLong(getDouble(), paramObject);
/*      */         break;
/*      */       case 13:
/*      */       case 16:
/* 2165 */         if (paramInt > 26) {
/* 2166 */           return convertToIntervalDayTime(getBigDecimal(), paramInt);
/*      */         }
/* 2168 */         l = convertToLong(getBigDecimal(), paramObject);
/*      */         break;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 2173 */         str = getString();
/*      */         try {
/* 2175 */           return 
/* 2176 */             (ValueInterval)IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(paramInt - 22), str)
/* 2177 */             .convertTo(paramInt);
/* 2178 */         } catch (Exception exception) {
/* 2179 */           throw DbException.get(22007, exception, new String[] { "INTERVAL", str });
/*      */         } 
/*      */       
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/* 2192 */         return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(paramInt - 22), 
/* 2193 */             IntervalUtils.intervalToAbsolute((ValueInterval)this));
/*      */       default:
/* 2195 */         throw getDataConversionError(paramInt);
/*      */     } 
/* 2197 */     boolean bool = false;
/* 2198 */     if (l < 0L) {
/* 2199 */       bool = true;
/* 2200 */       l = -l;
/*      */     } 
/* 2202 */     return ValueInterval.from(IntervalQualifier.valueOf(paramInt - 22), bool, l, 0L);
/*      */   }
/*      */ 
/*      */   
/*      */   private ValueInterval convertToIntervalDayTime(BigDecimal paramBigDecimal, int paramInt) {
/*      */     long l;
/* 2208 */     switch (paramInt) {
/*      */       case 27:
/* 2210 */         l = 1000000000L;
/*      */         break;
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/* 2215 */         l = 86400000000000L;
/*      */         break;
/*      */       case 32:
/*      */       case 33:
/* 2219 */         l = 3600000000000L;
/*      */         break;
/*      */       case 34:
/* 2222 */         l = 60000000000L;
/*      */         break;
/*      */       default:
/* 2225 */         throw getDataConversionError(paramInt);
/*      */     } 
/* 2227 */     return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(paramInt - 22), paramBigDecimal
/* 2228 */         .multiply(BigDecimal.valueOf(l)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueJavaObject convertToJavaObject(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/*      */     ValueJavaObject valueJavaObject;
/* 2246 */     switch (getValueType()) {
/*      */       case 35:
/* 2248 */         valueJavaObject = (ValueJavaObject)this;
/*      */         break;
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/* 2253 */         valueJavaObject = ValueJavaObject.getNoCopy(getBytesNoCopy());
/*      */         break;
/*      */       default:
/* 2256 */         throw getDataConversionError(35);
/*      */       case 0:
/* 2258 */         throw DbException.getInternalError();
/*      */     } 
/* 2260 */     if (paramInt != 0 && (valueJavaObject.getBytesNoCopy()).length > paramTypeInfo.getPrecision()) {
/* 2261 */       throw valueJavaObject.getValueTooLongException(paramTypeInfo, paramObject);
/*      */     }
/* 2263 */     return valueJavaObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueEnum convertToEnum(ExtTypeInfoEnum paramExtTypeInfoEnum, CastDataProvider paramCastDataProvider) {
/*      */     ValueEnum valueEnum;
/* 2276 */     switch (getValueType())
/*      */     { case 36:
/* 2278 */         valueEnum = (ValueEnum)this;
/* 2279 */         if (paramExtTypeInfoEnum.equals(valueEnum.getEnumerators())) {
/* 2280 */           return valueEnum;
/*      */         }
/* 2282 */         return paramExtTypeInfoEnum.getValue(valueEnum.getString(), paramCastDataProvider);
/*      */       
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 13:
/*      */       case 16:
/* 2290 */         return paramExtTypeInfoEnum.getValue(getInt(), paramCastDataProvider);
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 2294 */         return paramExtTypeInfoEnum.getValue(getString(), paramCastDataProvider);
/*      */       default:
/* 2296 */         throw getDataConversionError(36);
/*      */       case 0:
/* 2298 */         break; }  throw DbException.getInternalError();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueGeometry convertToGeometry(ExtTypeInfoGeometry paramExtTypeInfoGeometry) {
/*      */     ValueGeometry valueGeometry;
/*      */     int i;
/* 2312 */     switch (getValueType()) {
/*      */       case 37:
/* 2314 */         valueGeometry = (ValueGeometry)this;
/*      */         break;
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/* 2319 */         valueGeometry = ValueGeometry.getFromEWKB(getBytesNoCopy());
/*      */         break;
/*      */       case 38:
/* 2322 */         i = 0;
/* 2323 */         if (paramExtTypeInfoGeometry != null) {
/* 2324 */           Integer integer = paramExtTypeInfoGeometry.getSrid();
/* 2325 */           if (integer != null) {
/* 2326 */             i = integer.intValue();
/*      */           }
/*      */         } 
/*      */         try {
/* 2330 */           valueGeometry = ValueGeometry.get(GeoJsonUtils.geoJsonToEwkb(getBytesNoCopy(), i));
/* 2331 */         } catch (RuntimeException runtimeException) {
/* 2332 */           throw DbException.get(22018, getTraceSQL());
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/* 2340 */         valueGeometry = ValueGeometry.get(getString());
/*      */         break;
/*      */       default:
/* 2343 */         throw getDataConversionError(37);
/*      */       case 0:
/* 2345 */         throw DbException.getInternalError();
/*      */     } 
/* 2347 */     if (paramExtTypeInfoGeometry != null) {
/* 2348 */       i = paramExtTypeInfoGeometry.getType();
/* 2349 */       Integer integer = paramExtTypeInfoGeometry.getSrid();
/* 2350 */       if ((i != 0 && valueGeometry.getTypeAndDimensionSystem() != i) || (integer != null && valueGeometry.getSRID() != integer.intValue())) {
/*      */ 
/*      */         
/* 2353 */         StringBuilder stringBuilder = ExtTypeInfoGeometry.toSQL(new StringBuilder(), valueGeometry.getTypeAndDimensionSystem(), Integer.valueOf(valueGeometry.getSRID())).append(" -> ");
/* 2354 */         paramExtTypeInfoGeometry.getSQL(stringBuilder, 3);
/* 2355 */         throw DbException.get(22018, stringBuilder.toString());
/*      */       } 
/*      */     } 
/* 2358 */     return valueGeometry;
/*      */   } private ValueJson convertToJson(TypeInfo paramTypeInfo, int paramInt, Object paramObject) {
/*      */     ValueJson valueJson;
/*      */     ValueGeometry valueGeometry;
/*      */     ByteArrayOutputStream byteArrayOutputStream;
/* 2363 */     switch (getValueType()) {
/*      */       case 38:
/* 2365 */         valueJson = (ValueJson)this;
/*      */         break;
/*      */       case 8:
/* 2368 */         valueJson = ValueJson.get(getBoolean());
/*      */         break;
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/* 2373 */         valueJson = ValueJson.get(getInt());
/*      */         break;
/*      */       case 12:
/* 2376 */         valueJson = ValueJson.get(getLong());
/*      */         break;
/*      */       case 13:
/*      */       case 14:
/*      */       case 15:
/*      */       case 16:
/* 2382 */         valueJson = ValueJson.get(getBigDecimal());
/*      */         break;
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/* 2387 */         valueJson = ValueJson.fromJson(getBytesNoCopy());
/*      */         break;
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 17:
/*      */       case 18:
/*      */       case 19:
/*      */       case 39:
/* 2397 */         valueJson = ValueJson.get(getString());
/*      */         break;
/*      */       case 20:
/* 2400 */         valueJson = ValueJson.get(((ValueTimestamp)this).getISOString());
/*      */         break;
/*      */       case 21:
/* 2403 */         valueJson = ValueJson.get(((ValueTimestampTimeZone)this).getISOString());
/*      */         break;
/*      */       case 37:
/* 2406 */         valueGeometry = (ValueGeometry)this;
/* 2407 */         valueJson = ValueJson.getInternal(GeoJsonUtils.ewkbToGeoJson(valueGeometry.getBytesNoCopy(), valueGeometry.getDimensionSystem()));
/*      */         break;
/*      */       
/*      */       case 40:
/* 2411 */         byteArrayOutputStream = new ByteArrayOutputStream();
/* 2412 */         byteArrayOutputStream.write(91);
/* 2413 */         for (Value value : ((ValueArray)this).getList()) {
/* 2414 */           JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, value, 0);
/*      */         }
/* 2416 */         byteArrayOutputStream.write(93);
/* 2417 */         valueJson = ValueJson.getInternal(byteArrayOutputStream.toByteArray());
/*      */         break;
/*      */       
/*      */       default:
/* 2421 */         throw getDataConversionError(38);
/*      */     } 
/* 2423 */     if (paramInt != 0 && (valueJson.getBytesNoCopy()).length > paramTypeInfo.getPrecision()) {
/* 2424 */       throw valueJson.getValueTooLongException(paramTypeInfo, paramObject);
/*      */     }
/* 2426 */     return valueJson;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ValueUuid convertToUuid() {
/* 2435 */     switch (getValueType())
/*      */     { case 39:
/* 2437 */         return (ValueUuid)this;
/*      */       case 5:
/*      */       case 6:
/* 2440 */         return ValueUuid.get(getBytesNoCopy());
/*      */       case 35:
/* 2442 */         return JdbcUtils.deserializeUuid(getBytesNoCopy());
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/* 2446 */         return ValueUuid.get(getString());
/*      */       default:
/* 2448 */         throw getDataConversionError(39);
/*      */       case 0:
/* 2450 */         break; }  throw DbException.getInternalError();
/*      */   }
/*      */ 
/*      */   
/*      */   private ValueArray convertToArray(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt, Object paramObject) {
/*      */     ValueArray valueArray;
/* 2456 */     TypeInfo typeInfo = (TypeInfo)paramTypeInfo.getExtTypeInfo();
/* 2457 */     int i = getValueType();
/*      */     
/* 2459 */     if (i == 40) {
/* 2460 */       valueArray = (ValueArray)this;
/*      */     } else {
/*      */       Value[] arrayOfValue;
/* 2463 */       switch (i) {
/*      */         case 7:
/* 2465 */           arrayOfValue = new Value[] { ValueVarbinary.get(getBytesNoCopy()) };
/*      */           break;
/*      */         case 3:
/* 2468 */           arrayOfValue = new Value[] { ValueVarchar.get(getString()) };
/*      */           break;
/*      */         default:
/* 2471 */           arrayOfValue = new Value[] { this }; break;
/*      */       } 
/* 2473 */       valueArray = ValueArray.get(arrayOfValue, paramCastDataProvider);
/*      */     } 
/* 2475 */     if (typeInfo != null) {
/* 2476 */       Value[] arrayOfValue = valueArray.getList();
/* 2477 */       int j = arrayOfValue.length;
/* 2478 */       for (byte b = 0; b < j; b++) {
/* 2479 */         Value value1 = arrayOfValue[b];
/* 2480 */         Value value2 = value1.convertTo(typeInfo, paramCastDataProvider, paramInt, paramObject);
/* 2481 */         if (value1 != value2) {
/* 2482 */           Value[] arrayOfValue1 = new Value[j];
/* 2483 */           System.arraycopy(arrayOfValue, 0, arrayOfValue1, 0, b);
/* 2484 */           arrayOfValue1[b] = value2;
/* 2485 */           while (++b < j) {
/* 2486 */             arrayOfValue1[b] = arrayOfValue[b].convertTo(typeInfo, paramCastDataProvider, paramInt, paramObject);
/*      */           }
/* 2488 */           valueArray = ValueArray.get(typeInfo, arrayOfValue1, paramCastDataProvider);
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2493 */     if (paramInt != 0) {
/* 2494 */       Value[] arrayOfValue = valueArray.getList();
/* 2495 */       int j = arrayOfValue.length;
/* 2496 */       if (paramInt == 1) {
/* 2497 */         int k = MathUtils.convertLongToInt(paramTypeInfo.getPrecision());
/* 2498 */         if (j > k) {
/* 2499 */           valueArray = ValueArray.get(valueArray.getComponentType(), Arrays.<Value>copyOf(arrayOfValue, k), paramCastDataProvider);
/*      */         }
/* 2501 */       } else if (j > paramTypeInfo.getPrecision()) {
/* 2502 */         throw valueArray.getValueTooLongException(paramTypeInfo, paramObject);
/*      */       } 
/*      */     } 
/* 2505 */     return valueArray;
/*      */   }
/*      */ 
/*      */   
/*      */   private Value convertToRow(TypeInfo paramTypeInfo, CastDataProvider paramCastDataProvider, int paramInt, Object paramObject) {
/*      */     ValueRow valueRow;
/* 2511 */     if (getValueType() == 41) {
/* 2512 */       valueRow = (ValueRow)this;
/*      */     } else {
/* 2514 */       valueRow = ValueRow.get(new Value[] { this });
/*      */     } 
/* 2516 */     ExtTypeInfoRow extTypeInfoRow = (ExtTypeInfoRow)paramTypeInfo.getExtTypeInfo();
/* 2517 */     if (extTypeInfoRow != null) {
/* 2518 */       Value[] arrayOfValue = valueRow.getList();
/* 2519 */       int i = arrayOfValue.length;
/* 2520 */       Set<Map.Entry<String, TypeInfo>> set = extTypeInfoRow.getFields();
/* 2521 */       if (i != set.size()) {
/* 2522 */         throw getDataConversionError(paramTypeInfo);
/*      */       }
/* 2524 */       Iterator<Map.Entry<String, TypeInfo>> iterator = set.iterator();
/* 2525 */       for (byte b = 0; b < i; b++) {
/* 2526 */         Value value1 = arrayOfValue[b];
/* 2527 */         TypeInfo typeInfo = (TypeInfo)((Map.Entry)iterator.next()).getValue();
/* 2528 */         Value value2 = value1.convertTo(typeInfo, paramCastDataProvider, paramInt, paramObject);
/* 2529 */         if (value1 != value2) {
/* 2530 */           Value[] arrayOfValue1 = new Value[i];
/* 2531 */           System.arraycopy(arrayOfValue, 0, arrayOfValue1, 0, b);
/* 2532 */           arrayOfValue1[b] = value2;
/* 2533 */           while (++b < i) {
/* 2534 */             arrayOfValue1[b] = arrayOfValue[b].convertTo(typeInfo, paramCastDataProvider, paramInt, paramObject);
/*      */           }
/* 2536 */           valueRow = ValueRow.get(paramTypeInfo, arrayOfValue1);
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/* 2541 */     return valueRow;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final DbException getDataConversionError(int paramInt) {
/* 2551 */     throw DbException.get(22018, getTypeName(getValueType()) + " to " + 
/* 2552 */         getTypeName(paramInt));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final DbException getDataConversionError(TypeInfo paramTypeInfo) {
/* 2562 */     throw DbException.get(22018, getTypeName(getValueType()) + " to " + paramTypeInfo
/* 2563 */         .getTraceSQL());
/*      */   }
/*      */   
/*      */   final DbException getValueTooLongException(TypeInfo paramTypeInfo, Object paramObject) {
/* 2567 */     StringBuilder stringBuilder = new StringBuilder();
/* 2568 */     if (paramObject != null) {
/* 2569 */       stringBuilder.append(paramObject).append(' ');
/*      */     }
/* 2571 */     paramTypeInfo.getSQL(stringBuilder, 3);
/* 2572 */     return DbException.getValueTooLongException(stringBuilder.toString(), getTraceSQL(), getType().getPrecision());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final int compareTo(Value paramValue, CastDataProvider paramCastDataProvider, CompareMode paramCompareMode) {
/* 2598 */     if (this == paramValue) {
/* 2599 */       return 0;
/*      */     }
/* 2601 */     if (this == ValueNull.INSTANCE)
/* 2602 */       return -1; 
/* 2603 */     if (paramValue == ValueNull.INSTANCE) {
/* 2604 */       return 1;
/*      */     }
/* 2606 */     return compareToNotNullable(paramValue, paramCastDataProvider, paramCompareMode);
/*      */   }
/*      */   
/*      */   private int compareToNotNullable(Value paramValue, CastDataProvider paramCastDataProvider, CompareMode paramCompareMode) {
/* 2610 */     Value value = this;
/* 2611 */     int i = value.getValueType();
/* 2612 */     int j = paramValue.getValueType();
/* 2613 */     if (i != j || i == 36) {
/* 2614 */       int k = getHigherOrder(i, j);
/* 2615 */       if (k == 36) {
/* 2616 */         ExtTypeInfoEnum extTypeInfoEnum = ExtTypeInfoEnum.getEnumeratorsForBinaryOperation(value, paramValue);
/* 2617 */         value = value.convertToEnum(extTypeInfoEnum, paramCastDataProvider);
/* 2618 */         paramValue = paramValue.convertToEnum(extTypeInfoEnum, paramCastDataProvider);
/*      */       } else {
/* 2620 */         if (k <= 7) {
/* 2621 */           if (k <= 3) {
/* 2622 */             if (i == 1 || j == 1) {
/* 2623 */               k = 1;
/*      */             }
/* 2625 */           } else if (k >= 5 && (i == 5 || j == 5)) {
/* 2626 */             k = 5;
/*      */           } 
/*      */         }
/* 2629 */         value = value.convertTo(k, paramCastDataProvider);
/* 2630 */         paramValue = paramValue.convertTo(k, paramCastDataProvider);
/*      */       } 
/*      */     } 
/* 2633 */     return value.compareTypeSafe(paramValue, paramCompareMode, paramCastDataProvider);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int compareWithNull(Value paramValue, boolean paramBoolean, CastDataProvider paramCastDataProvider, CompareMode paramCompareMode) {
/* 2650 */     if (this == ValueNull.INSTANCE || paramValue == ValueNull.INSTANCE) {
/* 2651 */       return Integer.MIN_VALUE;
/*      */     }
/* 2653 */     return compareToNotNullable(paramValue, paramCastDataProvider, paramCompareMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean containsNull() {
/* 2662 */     return false;
/*      */   }
/*      */   
/*      */   private static byte convertToByte(long paramLong, Object paramObject) {
/* 2666 */     if (paramLong > 127L || paramLong < -128L)
/* 2667 */       throw DbException.get(22004, new String[] {
/* 2668 */             Long.toString(paramLong), getColumnName(paramObject)
/*      */           }); 
/* 2670 */     return (byte)(int)paramLong;
/*      */   }
/*      */   
/*      */   private static short convertToShort(long paramLong, Object paramObject) {
/* 2674 */     if (paramLong > 32767L || paramLong < -32768L)
/* 2675 */       throw DbException.get(22004, new String[] {
/* 2676 */             Long.toString(paramLong), getColumnName(paramObject)
/*      */           }); 
/* 2678 */     return (short)(int)paramLong;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int convertToInt(long paramLong, Object paramObject) {
/* 2689 */     if (paramLong > 2147483647L || paramLong < -2147483648L)
/* 2690 */       throw DbException.get(22004, new String[] {
/* 2691 */             Long.toString(paramLong), getColumnName(paramObject)
/*      */           }); 
/* 2693 */     return (int)paramLong;
/*      */   }
/*      */   
/*      */   private static long convertToLong(double paramDouble, Object paramObject) {
/* 2697 */     if (paramDouble > 9.223372036854776E18D || paramDouble < -9.223372036854776E18D)
/*      */     {
/*      */       
/* 2700 */       throw DbException.get(22004, new String[] {
/* 2701 */             Double.toString(paramDouble), getColumnName(paramObject)
/*      */           }); } 
/* 2703 */     return Math.round(paramDouble);
/*      */   }
/*      */   
/*      */   private static long convertToLong(BigDecimal paramBigDecimal, Object paramObject) {
/* 2707 */     if (paramBigDecimal.compareTo(MAX_LONG_DECIMAL) > 0 || paramBigDecimal
/* 2708 */       .compareTo(MIN_LONG_DECIMAL) < 0) {
/* 2709 */       throw DbException.get(22004, new String[] { paramBigDecimal
/* 2710 */             .toString(), getColumnName(paramObject) });
/*      */     }
/* 2712 */     return paramBigDecimal.setScale(0, RoundingMode.HALF_UP).longValue();
/*      */   }
/*      */   
/*      */   private static String getColumnName(Object paramObject) {
/* 2716 */     return (paramObject == null) ? "" : paramObject.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 2721 */     return getTraceSQL();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final DbException getUnsupportedExceptionForOperation(String paramString) {
/* 2732 */     return DbException.getUnsupportedException(getTypeName(getValueType()) + ' ' + paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long charLength() {
/* 2742 */     return getString().length();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long octetLength() {
/* 2752 */     return (getBytesNoCopy()).length;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isTrue() {
/* 2765 */     return (this != ValueNull.INSTANCE) ? getBoolean() : false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isFalse() {
/* 2778 */     return (this != ValueNull.INSTANCE && !getBoolean());
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\Value.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */