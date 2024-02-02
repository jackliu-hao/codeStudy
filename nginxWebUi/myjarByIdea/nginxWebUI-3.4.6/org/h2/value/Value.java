package org.h2.value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.h2.api.IntervalQualifier;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Mode;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.DataHandler;
import org.h2.util.Bits;
import org.h2.util.DateTimeUtils;
import org.h2.util.HasSQL;
import org.h2.util.IntervalUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.geometry.GeoJsonUtils;
import org.h2.util.json.JsonConstructorUtils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataInMemory;

public abstract class Value extends VersionedValue<Value> implements HasSQL, Typed {
   public static final int UNKNOWN = -1;
   public static final int NULL = 0;
   public static final int CHAR = 1;
   public static final int VARCHAR = 2;
   public static final int CLOB = 3;
   public static final int VARCHAR_IGNORECASE = 4;
   public static final int BINARY = 5;
   public static final int VARBINARY = 6;
   public static final int BLOB = 7;
   public static final int BOOLEAN = 8;
   public static final int TINYINT = 9;
   public static final int SMALLINT = 10;
   public static final int INTEGER = 11;
   public static final int BIGINT = 12;
   public static final int NUMERIC = 13;
   public static final int REAL = 14;
   public static final int DOUBLE = 15;
   public static final int DECFLOAT = 16;
   public static final int DATE = 17;
   public static final int TIME = 18;
   public static final int TIME_TZ = 19;
   public static final int TIMESTAMP = 20;
   public static final int TIMESTAMP_TZ = 21;
   public static final int INTERVAL_YEAR = 22;
   public static final int INTERVAL_MONTH = 23;
   public static final int INTERVAL_DAY = 24;
   public static final int INTERVAL_HOUR = 25;
   public static final int INTERVAL_MINUTE = 26;
   public static final int INTERVAL_SECOND = 27;
   public static final int INTERVAL_YEAR_TO_MONTH = 28;
   public static final int INTERVAL_DAY_TO_HOUR = 29;
   public static final int INTERVAL_DAY_TO_MINUTE = 30;
   public static final int INTERVAL_DAY_TO_SECOND = 31;
   public static final int INTERVAL_HOUR_TO_MINUTE = 32;
   public static final int INTERVAL_HOUR_TO_SECOND = 33;
   public static final int INTERVAL_MINUTE_TO_SECOND = 34;
   public static final int JAVA_OBJECT = 35;
   public static final int ENUM = 36;
   public static final int GEOMETRY = 37;
   public static final int JSON = 38;
   public static final int UUID = 39;
   public static final int ARRAY = 40;
   public static final int ROW = 41;
   public static final int TYPE_COUNT = 42;
   static final int GROUP_NULL = 0;
   static final int GROUP_CHARACTER_STRING = 1;
   static final int GROUP_BINARY_STRING = 2;
   static final int GROUP_BOOLEAN = 3;
   static final int GROUP_NUMERIC = 4;
   static final int GROUP_DATETIME = 5;
   static final int GROUP_INTERVAL_YM = 6;
   static final int GROUP_INTERVAL_DT = 7;
   static final int GROUP_OTHER = 8;
   static final int GROUP_COLLECTION = 9;
   static final byte[] GROUPS = new byte[]{0, 1, 1, 1, 1, 2, 2, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 7, 7, 7, 7, 6, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 9, 9};
   private static final String[] NAMES = new String[]{"UNKNOWN", "NULL", "CHARACTER", "CHARACTER VARYING", "CHARACTER LARGE OBJECT", "VARCHAR_IGNORECASE", "BINARY", "BINARY VARYING", "BINARY LARGE OBJECT", "BOOLEAN", "TINYINT", "SMALLINT", "INTEGER", "BIGINT", "NUMERIC", "REAL", "DOUBLE PRECISION", "DECFLOAT", "DATE", "TIME", "TIME WITH TIME ZONE", "TIMESTAMP", "TIMESTAMP WITH TIME ZONE", "INTERVAL YEAR", "INTERVAL MONTH", "INTERVAL DAY", "INTERVAL HOUR", "INTERVAL MINUTE", "INTERVAL SECOND", "INTERVAL YEAR TO MONTH", "INTERVAL DAY TO HOUR", "INTERVAL DAY TO MINUTE", "INTERVAL DAY TO SECOND", "INTERVAL HOUR TO MINUTE", "INTERVAL HOUR TO SECOND", "INTERVAL MINUTE TO SECOND", "JAVA_OBJECT", "ENUM", "GEOMETRY", "JSON", "UUID", "ARRAY", "ROW"};
   public static final Value[] EMPTY_VALUES = new Value[0];
   private static SoftReference<Value[]> softCache;
   static final BigDecimal MAX_LONG_DECIMAL = BigDecimal.valueOf(Long.MAX_VALUE);
   public static final BigDecimal MIN_LONG_DECIMAL = BigDecimal.valueOf(Long.MIN_VALUE);
   static final int CONVERT_TO = 0;
   static final int CAST_TO = 1;
   static final int ASSIGN_TO = 2;

   public static String getTypeName(int var0) {
      return NAMES[var0 + 1];
   }

   static void rangeCheck(long var0, long var2, long var4) {
      if ((var0 | var2) < 0L || var2 > var4 - var0) {
         if (var0 >= 0L && var0 <= var4) {
            throw DbException.getInvalidValueException("length", var2);
         } else {
            throw DbException.getInvalidValueException("offset", var0 + 1L);
         }
      }
   }

   public abstract TypeInfo getType();

   public abstract int getValueType();

   public int getMemory() {
      return 24;
   }

   public abstract int hashCode();

   public abstract boolean equals(Object var1);

   public static int getHigherOrder(int var0, int var1) {
      if (var0 == var1) {
         if (var0 == -1) {
            throw DbException.get(50004, (String)"?, ?");
         } else {
            return var0;
         }
      } else {
         if (var0 < var1) {
            int var2 = var0;
            var0 = var1;
            var1 = var2;
         }

         if (var0 == -1) {
            if (var1 == 0) {
               throw DbException.get(50004, (String)"?, NULL");
            } else {
               return var1;
            }
         } else if (var1 == -1) {
            if (var0 == 0) {
               throw DbException.get(50004, (String)"NULL, ?");
            } else {
               return var0;
            }
         } else {
            return var1 == 0 ? var0 : getHigherOrderKnown(var0, var1);
         }
      }
   }

   static int getHigherOrderKnown(int var0, int var1) {
      byte var2 = GROUPS[var0];
      byte var3 = GROUPS[var1];
      switch (var2) {
         case 3:
            if (var3 == 2) {
               throw getDataTypeCombinationException(8, var1);
            }
         default:
            return var0;
         case 4:
            return getHigherNumeric(var0, var1, var3);
         case 5:
            return getHigherDateTime(var0, var1, var3);
         case 6:
            return getHigherIntervalYearMonth(var0, var1, var3);
         case 7:
            return getHigherIntervalDayTime(var0, var1, var3);
         case 8:
            return getHigherOther(var0, var1, var3);
      }
   }

   private static int getHigherNumeric(int var0, int var1, int var2) {
      if (var2 == 4) {
         switch (var0) {
            case 14:
               switch (var1) {
                  case 11:
                     return 15;
                  case 12:
                  case 13:
                     return 16;
                  default:
                     return var0;
               }
            case 15:
               switch (var1) {
                  case 12:
                  case 13:
                     return 16;
               }
         }
      } else if (var2 == 2) {
         throw getDataTypeCombinationException(var0, var1);
      }

      return var0;
   }

   private static int getHigherDateTime(int var0, int var1, int var2) {
      if (var2 == 1) {
         return var0;
      } else if (var2 != 5) {
         throw getDataTypeCombinationException(var0, var1);
      } else {
         switch (var0) {
            case 18:
               if (var1 == 17) {
                  return 20;
               }
               break;
            case 19:
               if (var1 == 17) {
                  return 21;
               }
               break;
            case 20:
               if (var1 == 19) {
                  return 21;
               }
         }

         return var0;
      }
   }

   private static int getHigherIntervalYearMonth(int var0, int var1, int var2) {
      switch (var2) {
         case 6:
            if (var0 == 23 && var1 == 22) {
               return 28;
            }
         case 1:
         case 4:
            return var0;
         default:
            throw getDataTypeCombinationException(var0, var1);
      }
   }

   private static int getHigherIntervalDayTime(int var0, int var1, int var2) {
      switch (var2) {
         case 1:
         case 4:
            return var0;
         case 7:
            switch (var0) {
               case 25:
                  return 29;
               case 26:
                  if (var1 == 24) {
                     return 30;
                  }

                  return 32;
               case 27:
                  if (var1 == 24) {
                     return 31;
                  }

                  if (var1 == 25) {
                     return 33;
                  }

                  return 34;
               case 28:
               case 31:
               default:
                  break;
               case 29:
                  if (var1 == 26) {
                     return 30;
                  }

                  if (var1 == 27) {
                     return 31;
                  }
                  break;
               case 30:
                  if (var1 == 27) {
                     return 31;
                  }
                  break;
               case 32:
                  switch (var1) {
                     case 24:
                     case 29:
                     case 30:
                        return 30;
                     case 25:
                     case 26:
                     case 28:
                     default:
                        return var0;
                     case 27:
                        return 33;
                     case 31:
                        return 31;
                  }
               case 33:
                  switch (var1) {
                     case 24:
                     case 29:
                     case 30:
                     case 31:
                        return 31;
                     case 25:
                     case 26:
                     case 27:
                     case 28:
                     default:
                        return var0;
                  }
               case 34:
                  switch (var1) {
                     case 24:
                     case 29:
                     case 30:
                     case 31:
                        return 31;
                     case 25:
                     case 32:
                     case 33:
                        return 33;
                     case 26:
                     case 27:
                     case 28:
                  }
            }

            return var0;
         default:
            throw getDataTypeCombinationException(var0, var1);
      }
   }

   private static int getHigherOther(int var0, int var1, int var2) {
      switch (var0) {
         case 35:
            if (var2 != 2) {
               throw getDataTypeCombinationException(var0, var1);
            }
            break;
         case 36:
            if (var2 == 1 || var2 == 4 && var1 <= 11) {
               break;
            }

            throw getDataTypeCombinationException(var0, var1);
         case 37:
            if (var2 != 1 && var2 != 2) {
               throw getDataTypeCombinationException(var0, var1);
            }
            break;
         case 38:
            switch (var2) {
               case 5:
               case 6:
               case 7:
               case 8:
                  throw getDataTypeCombinationException(var0, var1);
               default:
                  return var0;
            }
         case 39:
            switch (var2) {
               case 1:
               case 2:
                  break;
               case 8:
                  if (var1 != 35) {
                     throw getDataTypeCombinationException(var0, var1);
                  }
                  break;
               default:
                  throw getDataTypeCombinationException(var0, var1);
            }
      }

      return var0;
   }

   private static DbException getDataTypeCombinationException(int var0, int var1) {
      return DbException.get(22018, (String)(getTypeName(var0) + ", " + getTypeName(var1)));
   }

   static Value cache(Value var0) {
      if (SysProperties.OBJECT_CACHE) {
         int var1 = var0.hashCode();
         Value[] var2;
         if (softCache == null || (var2 = (Value[])softCache.get()) == null) {
            var2 = new Value[SysProperties.OBJECT_CACHE_SIZE];
            softCache = new SoftReference(var2);
         }

         int var3 = var1 & SysProperties.OBJECT_CACHE_SIZE - 1;
         Value var4 = var2[var3];
         if (var4 != null && var4.getValueType() == var0.getValueType() && var0.equals(var4)) {
            return var4;
         }

         var2[var3] = var0;
      }

      return var0;
   }

   public static void clearCache() {
      softCache = null;
   }

   public abstract String getString();

   public Reader getReader() {
      return new StringReader(this.getString());
   }

   public Reader getReader(long var1, long var3) {
      String var5 = this.getString();
      long var6 = var1 - 1L;
      rangeCheck(var6, var3, (long)var5.length());
      int var8 = (int)var6;
      return new StringReader(var5.substring(var8, var8 + (int)var3));
   }

   public byte[] getBytes() {
      throw this.getDataConversionError(6);
   }

   public byte[] getBytesNoCopy() {
      return this.getBytes();
   }

   public InputStream getInputStream() {
      return new ByteArrayInputStream(this.getBytesNoCopy());
   }

   public InputStream getInputStream(long var1, long var3) {
      byte[] var5 = this.getBytesNoCopy();
      long var6 = var1 - 1L;
      rangeCheck(var6, var3, (long)var5.length);
      return new ByteArrayInputStream(var5, (int)var6, (int)var3);
   }

   public boolean getBoolean() {
      return this.convertToBoolean().getBoolean();
   }

   public byte getByte() {
      return this.convertToTinyint((Object)null).getByte();
   }

   public short getShort() {
      return this.convertToSmallint((Object)null).getShort();
   }

   public int getInt() {
      return this.convertToInt((Object)null).getInt();
   }

   public long getLong() {
      return this.convertToBigint((Object)null).getLong();
   }

   public BigDecimal getBigDecimal() {
      throw this.getDataConversionError(13);
   }

   public float getFloat() {
      throw this.getDataConversionError(14);
   }

   public double getDouble() {
      throw this.getDataConversionError(15);
   }

   public Value add(Value var1) {
      throw this.getUnsupportedExceptionForOperation("+");
   }

   public int getSignum() {
      throw this.getUnsupportedExceptionForOperation("SIGNUM");
   }

   public Value negate() {
      throw this.getUnsupportedExceptionForOperation("NEG");
   }

   public Value subtract(Value var1) {
      throw this.getUnsupportedExceptionForOperation("-");
   }

   public Value divide(Value var1, TypeInfo var2) {
      throw this.getUnsupportedExceptionForOperation("/");
   }

   public Value multiply(Value var1) {
      throw this.getUnsupportedExceptionForOperation("*");
   }

   public Value modulus(Value var1) {
      throw this.getUnsupportedExceptionForOperation("%");
   }

   public final Value convertTo(int var1) {
      return this.convertTo(var1, (CastDataProvider)null);
   }

   public final Value convertTo(TypeInfo var1) {
      return this.convertTo(var1, (CastDataProvider)null, 0, (Object)null);
   }

   public final Value convertTo(int var1, CastDataProvider var2) {
      switch (var1) {
         case 40:
            return this.convertToAnyArray(var2);
         case 41:
            return this.convertToAnyRow();
         default:
            return this.convertTo(TypeInfo.getTypeInfo(var1), var2, 0, (Object)null);
      }
   }

   public final Value convertTo(TypeInfo var1, CastDataProvider var2) {
      return this.convertTo(var1, var2, 0, (Object)null);
   }

   public final Value convertTo(TypeInfo var1, CastDataProvider var2, Object var3) {
      return this.convertTo(var1, var2, 0, var3);
   }

   public final ValueArray convertToAnyArray(CastDataProvider var1) {
      return this.getValueType() == 40 ? (ValueArray)this : ValueArray.get(this.getType(), new Value[]{this}, var1);
   }

   public final ValueRow convertToAnyRow() {
      return this.getValueType() == 41 ? (ValueRow)this : ValueRow.get(new Value[]{this});
   }

   public final Value castTo(TypeInfo var1, CastDataProvider var2) {
      return this.convertTo(var1, var2, 1, (Object)null);
   }

   public final Value convertForAssignTo(TypeInfo var1, CastDataProvider var2, Object var3) {
      return this.convertTo(var1, var2, 2, var3);
   }

   private Value convertTo(TypeInfo var1, CastDataProvider var2, int var3, Object var4) {
      int var5 = this.getValueType();
      int var6;
      if (var5 != 0 && (var5 != (var6 = var1.getValueType()) || var3 != 0 || var1.getExtTypeInfo() != null || var5 == 1)) {
         switch (var6) {
            case 0:
               return ValueNull.INSTANCE;
            case 1:
               return this.convertToChar(var1, var2, var3, var4);
            case 2:
               return this.convertToVarchar(var1, var2, var3, var4);
            case 3:
               return this.convertToClob(var1, var3, var4);
            case 4:
               return this.convertToVarcharIgnoreCase(var1, var3, var4);
            case 5:
               return this.convertToBinary(var1, var3, var4);
            case 6:
               return this.convertToVarbinary(var1, var3, var4);
            case 7:
               return this.convertToBlob(var1, var3, var4);
            case 8:
               return this.convertToBoolean();
            case 9:
               return this.convertToTinyint(var4);
            case 10:
               return this.convertToSmallint(var4);
            case 11:
               return this.convertToInt(var4);
            case 12:
               return this.convertToBigint(var4);
            case 13:
               return this.convertToNumeric(var1, var2, var3, var4);
            case 14:
               return this.convertToReal();
            case 15:
               return this.convertToDouble();
            case 16:
               return this.convertToDecfloat(var1, var3);
            case 17:
               return this.convertToDate(var2);
            case 18:
               return this.convertToTime(var1, var2, var3);
            case 19:
               return this.convertToTimeTimeZone(var1, var2, var3);
            case 20:
               return this.convertToTimestamp(var1, var2, var3);
            case 21:
               return this.convertToTimestampTimeZone(var1, var2, var3);
            case 22:
            case 23:
            case 28:
               return this.convertToIntervalYearMonth(var1, var3, var4);
            case 24:
            case 25:
            case 26:
            case 27:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
               return this.convertToIntervalDayTime(var1, var3, var4);
            case 35:
               return this.convertToJavaObject(var1, var3, var4);
            case 36:
               return this.convertToEnum((ExtTypeInfoEnum)var1.getExtTypeInfo(), var2);
            case 37:
               return this.convertToGeometry((ExtTypeInfoGeometry)var1.getExtTypeInfo());
            case 38:
               return this.convertToJson(var1, var3, var4);
            case 39:
               return this.convertToUuid();
            case 40:
               return this.convertToArray(var1, var2, var3, var4);
            case 41:
               return this.convertToRow(var1, var2, var3, var4);
            default:
               throw this.getDataConversionError(var6);
         }
      } else {
         return this;
      }
   }

   public ValueChar convertToChar() {
      return this.convertToChar(TypeInfo.getTypeInfo(1), (CastDataProvider)null, 0, (Object)null);
   }

   private ValueChar convertToChar(TypeInfo var1, CastDataProvider var2, int var3, Object var4) {
      int var5 = this.getValueType();
      switch (var5) {
         case 7:
         case 35:
            throw this.getDataConversionError(var1.getValueType());
         default:
            String var6 = this.getString();
            int var7 = var6.length();
            int var8 = var7;
            if (var3 == 0) {
               while(var8 > 0 && var6.charAt(var8 - 1) == ' ') {
                  --var8;
               }
            } else {
               int var9 = MathUtils.convertLongToInt(var1.getPrecision());
               if (var2 != null && var2.getMode().charPadding != Mode.CharPadding.ALWAYS) {
                  if (var3 == 1 && var7 > var9) {
                     var8 = var9;
                  }

                  while(var8 > 0 && var6.charAt(var8 - 1) == ' ') {
                     --var8;
                  }

                  if (var3 == 2 && var8 > var9) {
                     throw this.getValueTooLongException(var1, var4);
                  }
               } else if (var7 != var9) {
                  if (var7 < var9) {
                     return ValueChar.get(StringUtils.pad(var6, var9, (String)null, true));
                  }

                  if (var3 == 1) {
                     var8 = var9;
                  } else {
                     do {
                        --var8;
                        if (var6.charAt(var8) != ' ') {
                           throw this.getValueTooLongException(var1, var4);
                        }
                     } while(var8 > var9);
                  }
               }
            }

            if (var7 != var8) {
               var6 = var6.substring(0, var8);
            } else if (var5 == 1) {
               return (ValueChar)this;
            }

            return ValueChar.get(var6);
      }
   }

   private Value convertToVarchar(TypeInfo var1, CastDataProvider var2, int var3, Object var4) {
      int var5 = this.getValueType();
      switch (var5) {
         case 7:
         case 35:
            throw this.getDataConversionError(var1.getValueType());
         default:
            if (var3 != 0) {
               String var6 = this.getString();
               int var7 = MathUtils.convertLongToInt(var1.getPrecision());
               if (var6.length() > var7) {
                  if (var3 != 1) {
                     throw this.getValueTooLongException(var1, var4);
                  }

                  return ValueVarchar.get(var6.substring(0, var7), var2);
               }
            }

            return var5 == 2 ? this : ValueVarchar.get(this.getString(), var2);
      }
   }

   private ValueClob convertToClob(TypeInfo var1, int var2, Object var3) {
      ValueClob var4;
      switch (this.getValueType()) {
         case 3:
            var4 = (ValueClob)this;
            break;
         case 7:
            LobData var5 = ((ValueBlob)this).lobData;
            if (var5 instanceof LobDataInMemory) {
               byte[] var6 = ((LobDataInMemory)var5).getSmall();
               byte[] var7 = (new String(var6, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
               if (Arrays.equals(var7, var6)) {
                  var7 = var6;
               }

               var4 = ValueClob.createSmall(var7);
               break;
            } else if (var5 instanceof LobDataDatabase) {
               var4 = var5.getDataHandler().getLobStorage().createClob(this.getReader(), -1L);
               break;
            }
         default:
            var4 = ValueClob.createSmall(this.getString());
            break;
         case 35:
            throw this.getDataConversionError(var1.getValueType());
      }

      if (var2 != 0) {
         if (var2 == 1) {
            var4 = var4.convertPrecision(var1.getPrecision());
         } else if (var4.charLength() > var1.getPrecision()) {
            throw var4.getValueTooLongException(var1, var3);
         }
      }

      return var4;
   }

   private Value convertToVarcharIgnoreCase(TypeInfo var1, int var2, Object var3) {
      int var4 = this.getValueType();
      switch (var4) {
         case 7:
         case 35:
            throw this.getDataConversionError(var1.getValueType());
         default:
            if (var2 != 0) {
               String var5 = this.getString();
               int var6 = MathUtils.convertLongToInt(var1.getPrecision());
               if (var5.length() > var6) {
                  if (var2 != 1) {
                     throw this.getValueTooLongException(var1, var3);
                  }

                  return ValueVarcharIgnoreCase.get(var5.substring(0, var6));
               }
            }

            return (Value)(var4 == 4 ? this : ValueVarcharIgnoreCase.get(this.getString()));
      }
   }

   private ValueBinary convertToBinary(TypeInfo var1, int var2, Object var3) {
      ValueBinary var4;
      if (this.getValueType() == 5) {
         var4 = (ValueBinary)this;
      } else {
         try {
            var4 = ValueBinary.getNoCopy(this.getBytesNoCopy());
         } catch (DbException var8) {
            if (var8.getErrorCode() == 22018) {
               throw this.getDataConversionError(5);
            }

            throw var8;
         }
      }

      if (var2 != 0) {
         byte[] var5 = var4.getBytesNoCopy();
         int var6 = var5.length;
         int var7 = MathUtils.convertLongToInt(var1.getPrecision());
         if (var6 != var7) {
            if (var2 == 2 && var6 > var7) {
               throw var4.getValueTooLongException(var1, var3);
            }

            var4 = ValueBinary.getNoCopy(Arrays.copyOf(var5, var7));
         }
      }

      return var4;
   }

   private ValueVarbinary convertToVarbinary(TypeInfo var1, int var2, Object var3) {
      ValueVarbinary var4;
      if (this.getValueType() == 6) {
         var4 = (ValueVarbinary)this;
      } else {
         var4 = ValueVarbinary.getNoCopy(this.getBytesNoCopy());
      }

      if (var2 != 0) {
         byte[] var5 = var4.getBytesNoCopy();
         int var6 = var5.length;
         int var7 = MathUtils.convertLongToInt(var1.getPrecision());
         if (var2 == 1) {
            if (var6 > var7) {
               var4 = ValueVarbinary.getNoCopy(Arrays.copyOf(var5, var7));
            }
         } else if (var6 > var7) {
            throw var4.getValueTooLongException(var1, var3);
         }
      }

      return var4;
   }

   private ValueBlob convertToBlob(TypeInfo var1, int var2, Object var3) {
      ValueBlob var4;
      switch (this.getValueType()) {
         case 3:
            DataHandler var5 = ((ValueLob)this).lobData.getDataHandler();
            if (var5 != null) {
               var4 = var5.getLobStorage().createBlob(this.getInputStream(), -1L);
               break;
            }
         default:
            try {
               var4 = ValueBlob.createSmall(this.getBytesNoCopy());
               break;
            } catch (DbException var7) {
               if (var7.getErrorCode() == 22018) {
                  throw this.getDataConversionError(7);
               }

               throw var7;
            }
         case 7:
            var4 = (ValueBlob)this;
      }

      if (var2 != 0) {
         if (var2 == 1) {
            var4 = var4.convertPrecision(var1.getPrecision());
         } else if (var4.octetLength() > var1.getPrecision()) {
            throw var4.getValueTooLongException(var1, var3);
         }
      }

      return var4;
   }

   public final ValueBoolean convertToBoolean() {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
            return ValueBoolean.get(this.getBoolean());
         case 3:
         case 5:
         case 6:
         case 7:
         default:
            throw this.getDataConversionError(8);
         case 8:
            return (ValueBoolean)this;
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
            return ValueBoolean.get(this.getSignum() != 0);
      }
   }

   public final ValueTinyint convertToTinyint(Object var1) {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
         case 8:
            return ValueTinyint.get(this.getByte());
         case 5:
         case 6:
            byte[] var2 = this.getBytesNoCopy();
            if (var2.length == 1) {
               return ValueTinyint.get(var2[0]);
            }
         case 3:
         case 7:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 35:
         default:
            throw this.getDataConversionError(9);
         case 9:
            return (ValueTinyint)this;
         case 10:
         case 11:
         case 36:
            return ValueTinyint.get(convertToByte((long)this.getInt(), var1));
         case 12:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
            return ValueTinyint.get(convertToByte(this.getLong(), var1));
         case 13:
         case 16:
            return ValueTinyint.get(convertToByte(convertToLong(this.getBigDecimal(), var1), var1));
         case 14:
         case 15:
            return ValueTinyint.get(convertToByte(convertToLong(this.getDouble(), var1), var1));
      }
   }

   public final ValueSmallint convertToSmallint(Object var1) {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
         case 8:
         case 9:
            return ValueSmallint.get(this.getShort());
         case 5:
         case 6:
            byte[] var2 = this.getBytesNoCopy();
            if (var2.length == 2) {
               return ValueSmallint.get((short)((var2[0] << 8) + (var2[1] & 255)));
            }
         case 3:
         case 7:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 35:
         default:
            throw this.getDataConversionError(10);
         case 10:
            return (ValueSmallint)this;
         case 11:
         case 36:
            return ValueSmallint.get(convertToShort((long)this.getInt(), var1));
         case 12:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
            return ValueSmallint.get(convertToShort(this.getLong(), var1));
         case 13:
         case 16:
            return ValueSmallint.get(convertToShort(convertToLong(this.getBigDecimal(), var1), var1));
         case 14:
         case 15:
            return ValueSmallint.get(convertToShort(convertToLong(this.getDouble(), var1), var1));
      }
   }

   public final ValueInteger convertToInt(Object var1) {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
         case 8:
         case 9:
         case 10:
         case 36:
            return ValueInteger.get(this.getInt());
         case 5:
         case 6:
            byte[] var2 = this.getBytesNoCopy();
            if (var2.length == 4) {
               return ValueInteger.get(Bits.readInt(var2, 0));
            }
         case 3:
         case 7:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 35:
         default:
            throw this.getDataConversionError(11);
         case 11:
            return (ValueInteger)this;
         case 12:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
            return ValueInteger.get(convertToInt(this.getLong(), var1));
         case 13:
         case 16:
            return ValueInteger.get(convertToInt(convertToLong(this.getBigDecimal(), var1), var1));
         case 14:
         case 15:
            return ValueInteger.get(convertToInt(convertToLong(this.getDouble(), var1), var1));
      }
   }

   public final ValueBigint convertToBigint(Object var1) {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
         case 8:
         case 9:
         case 10:
         case 11:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 36:
            return ValueBigint.get(this.getLong());
         case 5:
         case 6:
            byte[] var2 = this.getBytesNoCopy();
            if (var2.length == 8) {
               return ValueBigint.get(Bits.readLong(var2, 0));
            }
         case 3:
         case 7:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 35:
         default:
            throw this.getDataConversionError(12);
         case 12:
            return (ValueBigint)this;
         case 13:
         case 16:
            return ValueBigint.get(convertToLong(this.getBigDecimal(), var1));
         case 14:
         case 15:
            return ValueBigint.get(convertToLong(this.getDouble(), var1));
      }
   }

   private ValueNumeric convertToNumeric(TypeInfo var1, CastDataProvider var2, int var3, Object var4) {
      ValueNumeric var5;
      int var8;
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 8:
            var5 = this.getBoolean() ? ValueNumeric.ONE : ValueNumeric.ZERO;
            break;
         case 13:
            var5 = (ValueNumeric)this;
            break;
         default:
            BigDecimal var10 = this.getBigDecimal();
            int var11 = var1.getScale();
            var8 = var10.scale();
            if (var8 < 0 || var8 > 100000 || var3 != 0 && var8 != var11 && (var8 >= var11 || !var2.getMode().convertOnlyToSmallerScale)) {
               var10 = ValueNumeric.setScale(var10, var11);
            }

            if (var3 != 0 && (long)var10.precision() > var1.getPrecision() - (long)var11 + (long)var10.scale()) {
               throw this.getValueTooLongException(var1, var4);
            }

            return ValueNumeric.get(var10);
      }

      if (var3 != 0) {
         int var6 = var1.getScale();
         BigDecimal var7 = var5.getBigDecimal();
         var8 = var7.scale();
         if (var8 != var6 && (var8 >= var6 || !var2.getMode().convertOnlyToSmallerScale)) {
            var5 = ValueNumeric.get(ValueNumeric.setScale(var7, var6));
         }

         BigDecimal var9 = var5.getBigDecimal();
         if ((long)var9.precision() > var1.getPrecision() - (long)var6 + (long)var9.scale()) {
            throw var5.getValueTooLongException(var1, var4);
         }
      }

      return var5;
   }

   public final ValueReal convertToReal() {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 8:
            return this.getBoolean() ? ValueReal.ONE : ValueReal.ZERO;
         case 14:
            return (ValueReal)this;
         default:
            return ValueReal.get(this.getFloat());
      }
   }

   public final ValueDouble convertToDouble() {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 8:
            return this.getBoolean() ? ValueDouble.ONE : ValueDouble.ZERO;
         case 15:
            return (ValueDouble)this;
         default:
            return ValueDouble.get(this.getDouble());
      }
   }

   private ValueDecfloat convertToDecfloat(TypeInfo var1, int var2) {
      ValueDecfloat var3;
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
            String var11 = this.getString().trim();

            try {
               var3 = ValueDecfloat.get(new BigDecimal(var11));
               break;
            } catch (NumberFormatException var9) {
               switch (var11) {
                  case "-Infinity":
                     return ValueDecfloat.NEGATIVE_INFINITY;
                  case "Infinity":
                  case "+Infinity":
                     return ValueDecfloat.POSITIVE_INFINITY;
                  case "NaN":
                  case "-NaN":
                  case "+NaN":
                     return ValueDecfloat.NAN;
                  default:
                     throw this.getDataConversionError(16);
               }
            }
         case 3:
         case 5:
         case 6:
         case 7:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         default:
            try {
               var3 = ValueDecfloat.get(this.getBigDecimal());
               break;
            } catch (DbException var8) {
               if (var8.getErrorCode() == 22018) {
                  throw this.getDataConversionError(16);
               }

               throw var8;
            }
         case 8:
            var3 = this.getBoolean() ? ValueDecfloat.ONE : ValueDecfloat.ZERO;
            break;
         case 14:
            float var10 = this.getFloat();
            if (!Float.isFinite(var10)) {
               if (var10 == Float.POSITIVE_INFINITY) {
                  return ValueDecfloat.POSITIVE_INFINITY;
               }

               if (var10 == Float.NEGATIVE_INFINITY) {
                  return ValueDecfloat.NEGATIVE_INFINITY;
               }

               return ValueDecfloat.NAN;
            }

            var3 = ValueDecfloat.get(new BigDecimal(Float.toString(var10)));
            break;
         case 15:
            double var4 = this.getDouble();
            if (!Double.isFinite(var4)) {
               if (var4 == Double.POSITIVE_INFINITY) {
                  return ValueDecfloat.POSITIVE_INFINITY;
               }

               if (var4 == Double.NEGATIVE_INFINITY) {
                  return ValueDecfloat.NEGATIVE_INFINITY;
               }

               return ValueDecfloat.NAN;
            }

            var3 = ValueDecfloat.get(new BigDecimal(Double.toString(var4)));
            break;
         case 16:
            var3 = (ValueDecfloat)this;
            if (var3.value == null) {
               return var3;
            }
      }

      if (var2 != 0) {
         BigDecimal var12 = var3.value;
         int var5 = var12.precision();
         int var6 = (int)var1.getPrecision();
         if (var5 > var6) {
            var3 = ValueDecfloat.get(var12.setScale(var12.scale() - var5 + var6, RoundingMode.HALF_UP));
         }
      }

      return var3;
   }

   public final ValueDate convertToDate(CastDataProvider var1) {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
            return ValueDate.parse(this.getString().trim());
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 18:
         case 19:
         default:
            throw this.getDataConversionError(17);
         case 17:
            return (ValueDate)this;
         case 20:
            return ValueDate.fromDateValue(((ValueTimestamp)this).getDateValue());
         case 21:
            ValueTimestampTimeZone var2 = (ValueTimestampTimeZone)this;
            long var3 = var2.getTimeNanos();
            long var5 = DateTimeUtils.getEpochSeconds(var2.getDateValue(), var3, var2.getTimeZoneOffsetSeconds());
            return ValueDate.fromDateValue(DateTimeUtils.dateValueFromLocalSeconds(var5 + (long)var1.currentTimeZone().getTimeZoneOffsetUTC(var5)));
      }
   }

   private ValueTime convertToTime(TypeInfo var1, CastDataProvider var2, int var3) {
      ValueTime var4;
      long var6;
      long var8;
      switch (this.getValueType()) {
         case 1:
         case 2:
         case 4:
            var4 = ValueTime.parse(this.getString().trim());
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         default:
            throw this.getDataConversionError(18);
         case 18:
            var4 = (ValueTime)this;
            break;
         case 19:
            var4 = ValueTime.fromNanos(this.getLocalTimeNanos(var2));
            break;
         case 20:
            var4 = ValueTime.fromNanos(((ValueTimestamp)this).getTimeNanos());
            break;
         case 21:
            ValueTimestampTimeZone var5 = (ValueTimestampTimeZone)this;
            var6 = var5.getTimeNanos();
            var8 = DateTimeUtils.getEpochSeconds(var5.getDateValue(), var6, var5.getTimeZoneOffsetSeconds());
            var4 = ValueTime.fromNanos(DateTimeUtils.nanosFromLocalSeconds(var8 + (long)var2.currentTimeZone().getTimeZoneOffsetUTC(var8)) + var6 % 1000000000L);
      }

      if (var3 != 0) {
         int var10 = var1.getScale();
         if (var10 < 9) {
            var6 = var4.getNanos();
            var8 = DateTimeUtils.convertScale(var6, var10, 86400000000000L);
            if (var8 != var6) {
               var4 = ValueTime.fromNanos(var8);
            }
         }
      }

      return var4;
   }

   private ValueTimeTimeZone convertToTimeTimeZone(TypeInfo var1, CastDataProvider var2, int var3) {
      ValueTimeTimeZone var4;
      long var6;
      switch (this.getValueType()) {
         case 1:
         case 2:
         case 4:
            var4 = ValueTimeTimeZone.parse(this.getString().trim());
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         default:
            throw this.getDataConversionError(19);
         case 18:
            var4 = ValueTimeTimeZone.fromNanos(((ValueTime)this).getNanos(), var2.currentTimestamp().getTimeZoneOffsetSeconds());
            break;
         case 19:
            var4 = (ValueTimeTimeZone)this;
            break;
         case 20:
            ValueTimestamp var10 = (ValueTimestamp)this;
            var6 = var10.getTimeNanos();
            var4 = ValueTimeTimeZone.fromNanos(var6, var2.currentTimeZone().getTimeZoneOffsetLocal(var10.getDateValue(), var6));
            break;
         case 21:
            ValueTimestampTimeZone var5 = (ValueTimestampTimeZone)this;
            var4 = ValueTimeTimeZone.fromNanos(var5.getTimeNanos(), var5.getTimeZoneOffsetSeconds());
      }

      if (var3 != 0) {
         int var11 = var1.getScale();
         if (var11 < 9) {
            var6 = var4.getNanos();
            long var8 = DateTimeUtils.convertScale(var6, var11, 86400000000000L);
            if (var8 != var6) {
               var4 = ValueTimeTimeZone.fromNanos(var8, var4.getTimeZoneOffsetSeconds());
            }
         }
      }

      return var4;
   }

   private ValueTimestamp convertToTimestamp(TypeInfo var1, CastDataProvider var2, int var3) {
      ValueTimestamp var4;
      long var6;
      long var8;
      switch (this.getValueType()) {
         case 1:
         case 2:
         case 4:
            var4 = ValueTimestamp.parse(this.getString().trim(), var2);
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         default:
            throw this.getDataConversionError(20);
         case 17:
            return ValueTimestamp.fromDateValueAndNanos(((ValueDate)this).getDateValue(), 0L);
         case 18:
            var4 = ValueTimestamp.fromDateValueAndNanos(var2.currentTimestamp().getDateValue(), ((ValueTime)this).getNanos());
            break;
         case 19:
            var4 = ValueTimestamp.fromDateValueAndNanos(var2.currentTimestamp().getDateValue(), this.getLocalTimeNanos(var2));
            break;
         case 20:
            var4 = (ValueTimestamp)this;
            break;
         case 21:
            ValueTimestampTimeZone var5 = (ValueTimestampTimeZone)this;
            var6 = var5.getTimeNanos();
            var8 = DateTimeUtils.getEpochSeconds(var5.getDateValue(), var6, var5.getTimeZoneOffsetSeconds());
            var8 += (long)var2.currentTimeZone().getTimeZoneOffsetUTC(var8);
            var4 = ValueTimestamp.fromDateValueAndNanos(DateTimeUtils.dateValueFromLocalSeconds(var8), DateTimeUtils.nanosFromLocalSeconds(var8) + var6 % 1000000000L);
      }

      if (var3 != 0) {
         int var12 = var1.getScale();
         if (var12 < 9) {
            var6 = var4.getDateValue();
            var8 = var4.getTimeNanos();
            long var10 = DateTimeUtils.convertScale(var8, var12, var6 == 512000000415L ? 86400000000000L : Long.MAX_VALUE);
            if (var10 != var8) {
               if (var10 >= 86400000000000L) {
                  var10 -= 86400000000000L;
                  var6 = DateTimeUtils.incrementDateValue(var6);
               }

               var4 = ValueTimestamp.fromDateValueAndNanos(var6, var10);
            }
         }
      }

      return var4;
   }

   private long getLocalTimeNanos(CastDataProvider var1) {
      ValueTimeTimeZone var2 = (ValueTimeTimeZone)this;
      int var3 = var1.currentTimestamp().getTimeZoneOffsetSeconds();
      return DateTimeUtils.normalizeNanosOfDay(var2.getNanos() + (long)(var2.getTimeZoneOffsetSeconds() - var3) * 86400000000000L);
   }

   private ValueTimestampTimeZone convertToTimestampTimeZone(TypeInfo var1, CastDataProvider var2, int var3) {
      ValueTimestampTimeZone var4;
      long var6;
      long var8;
      long var13;
      switch (this.getValueType()) {
         case 1:
         case 2:
         case 4:
            var4 = ValueTimestampTimeZone.parse(this.getString().trim(), var2);
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         default:
            throw this.getDataConversionError(21);
         case 17:
            var13 = ((ValueDate)this).getDateValue();
            return ValueTimestampTimeZone.fromDateValueAndNanos(var13, 0L, var2.currentTimeZone().getTimeZoneOffsetLocal(var13, 0L));
         case 18:
            var13 = var2.currentTimestamp().getDateValue();
            long var7 = ((ValueTime)this).getNanos();
            var4 = ValueTimestampTimeZone.fromDateValueAndNanos(var13, var7, var2.currentTimeZone().getTimeZoneOffsetLocal(var13, var7));
            break;
         case 19:
            ValueTimeTimeZone var12 = (ValueTimeTimeZone)this;
            var4 = ValueTimestampTimeZone.fromDateValueAndNanos(var2.currentTimestamp().getDateValue(), var12.getNanos(), var12.getTimeZoneOffsetSeconds());
            break;
         case 20:
            ValueTimestamp var5 = (ValueTimestamp)this;
            var6 = var5.getDateValue();
            var8 = var5.getTimeNanos();
            var4 = ValueTimestampTimeZone.fromDateValueAndNanos(var6, var8, var2.currentTimeZone().getTimeZoneOffsetLocal(var6, var8));
            break;
         case 21:
            var4 = (ValueTimestampTimeZone)this;
      }

      if (var3 != 0) {
         int var14 = var1.getScale();
         if (var14 < 9) {
            var6 = var4.getDateValue();
            var8 = var4.getTimeNanos();
            long var10 = DateTimeUtils.convertScale(var8, var14, var6 == 512000000415L ? 86400000000000L : Long.MAX_VALUE);
            if (var10 != var8) {
               if (var10 >= 86400000000000L) {
                  var10 -= 86400000000000L;
                  var6 = DateTimeUtils.incrementDateValue(var6);
               }

               var4 = ValueTimestampTimeZone.fromDateValueAndNanos(var6, var10, var4.getTimeZoneOffsetSeconds());
            }
         }
      }

      return var4;
   }

   private ValueInterval convertToIntervalYearMonth(TypeInfo var1, int var2, Object var3) {
      ValueInterval var4 = this.convertToIntervalYearMonth(var1.getValueType(), var3);
      if (var2 != 0 && !var4.checkPrecision(var1.getPrecision())) {
         throw var4.getValueTooLongException(var1, var3);
      } else {
         return var4;
      }
   }

   private ValueInterval convertToIntervalYearMonth(int var1, Object var2) {
      long var3;
      switch (this.getValueType()) {
         case 1:
         case 2:
         case 4:
            String var8 = this.getString();

            try {
               return (ValueInterval)IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(var1 - 22), var8).convertTo(var1);
            } catch (Exception var7) {
               throw DbException.get(22007, var7, "INTERVAL", var8);
            }
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 24:
         case 25:
         case 26:
         case 27:
         default:
            throw this.getDataConversionError(var1);
         case 9:
         case 10:
         case 11:
            var3 = (long)this.getInt();
            break;
         case 12:
            var3 = this.getLong();
            break;
         case 13:
         case 16:
            if (var1 == 28) {
               return IntervalUtils.intervalFromAbsolute(IntervalQualifier.YEAR_TO_MONTH, this.getBigDecimal().multiply(BigDecimal.valueOf(12L)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
            }

            var3 = convertToLong(this.getBigDecimal(), var2);
            break;
         case 14:
         case 15:
            if (var1 == 28) {
               return IntervalUtils.intervalFromAbsolute(IntervalQualifier.YEAR_TO_MONTH, this.getBigDecimal().multiply(BigDecimal.valueOf(12L)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
            }

            var3 = convertToLong(this.getDouble(), var2);
            break;
         case 22:
         case 23:
         case 28:
            return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(var1 - 22), IntervalUtils.intervalToAbsolute((ValueInterval)this));
      }

      boolean var5 = false;
      if (var3 < 0L) {
         var5 = true;
         var3 = -var3;
      }

      return ValueInterval.from(IntervalQualifier.valueOf(var1 - 22), var5, var3, 0L);
   }

   private ValueInterval convertToIntervalDayTime(TypeInfo var1, int var2, Object var3) {
      ValueInterval var4 = this.convertToIntervalDayTime(var1.getValueType(), var3);
      if (var2 != 0) {
         var4 = var4.setPrecisionAndScale(var1, var3);
      }

      return var4;
   }

   private ValueInterval convertToIntervalDayTime(int var1, Object var2) {
      long var3;
      switch (this.getValueType()) {
         case 1:
         case 2:
         case 4:
            String var8 = this.getString();

            try {
               return (ValueInterval)IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(var1 - 22), var8).convertTo(var1);
            } catch (Exception var7) {
               throw DbException.get(22007, var7, "INTERVAL", var8);
            }
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 28:
         default:
            throw this.getDataConversionError(var1);
         case 9:
         case 10:
         case 11:
            var3 = (long)this.getInt();
            break;
         case 12:
            var3 = this.getLong();
            break;
         case 13:
         case 16:
            if (var1 > 26) {
               return this.convertToIntervalDayTime(this.getBigDecimal(), var1);
            }

            var3 = convertToLong(this.getBigDecimal(), var2);
            break;
         case 14:
         case 15:
            if (var1 > 26) {
               return this.convertToIntervalDayTime(this.getBigDecimal(), var1);
            }

            var3 = convertToLong(this.getDouble(), var2);
            break;
         case 24:
         case 25:
         case 26:
         case 27:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
            return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(var1 - 22), IntervalUtils.intervalToAbsolute((ValueInterval)this));
      }

      boolean var5 = false;
      if (var3 < 0L) {
         var5 = true;
         var3 = -var3;
      }

      return ValueInterval.from(IntervalQualifier.valueOf(var1 - 22), var5, var3, 0L);
   }

   private ValueInterval convertToIntervalDayTime(BigDecimal var1, int var2) {
      long var3;
      switch (var2) {
         case 27:
            var3 = 1000000000L;
            break;
         case 28:
         default:
            throw this.getDataConversionError(var2);
         case 29:
         case 30:
         case 31:
            var3 = 86400000000000L;
            break;
         case 32:
         case 33:
            var3 = 3600000000000L;
            break;
         case 34:
            var3 = 60000000000L;
      }

      return IntervalUtils.intervalFromAbsolute(IntervalQualifier.valueOf(var2 - 22), var1.multiply(BigDecimal.valueOf(var3)).setScale(0, RoundingMode.HALF_UP).toBigInteger());
   }

   public final ValueJavaObject convertToJavaObject(TypeInfo var1, int var2, Object var3) {
      ValueJavaObject var4;
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 5:
         case 6:
         case 7:
            var4 = ValueJavaObject.getNoCopy(this.getBytesNoCopy());
            break;
         case 35:
            var4 = (ValueJavaObject)this;
            break;
         default:
            throw this.getDataConversionError(35);
      }

      if (var2 != 0 && (long)var4.getBytesNoCopy().length > var1.getPrecision()) {
         throw var4.getValueTooLongException(var1, var3);
      } else {
         return var4;
      }
   }

   public final ValueEnum convertToEnum(ExtTypeInfoEnum var1, CastDataProvider var2) {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
            return var1.getValue(this.getString(), var2);
         case 3:
         case 5:
         case 6:
         case 7:
         case 8:
         case 14:
         case 15:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         default:
            throw this.getDataConversionError(36);
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 16:
            return var1.getValue(this.getInt(), var2);
         case 36:
            ValueEnum var3 = (ValueEnum)this;
            return var1.equals(var3.getEnumerators()) ? var3 : var1.getValue(var3.getString(), var2);
      }
   }

   public final ValueGeometry convertToGeometry(ExtTypeInfoGeometry var1) {
      ValueGeometry var2;
      int var3;
      Integer var4;
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 3:
         case 4:
            var2 = ValueGeometry.get(this.getString());
            break;
         case 5:
         case 6:
         case 7:
            var2 = ValueGeometry.getFromEWKB(this.getBytesNoCopy());
            break;
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         default:
            throw this.getDataConversionError(37);
         case 37:
            var2 = (ValueGeometry)this;
            break;
         case 38:
            var3 = 0;
            if (var1 != null) {
               var4 = var1.getSrid();
               if (var4 != null) {
                  var3 = var4;
               }
            }

            try {
               var2 = ValueGeometry.get(GeoJsonUtils.geoJsonToEwkb(this.getBytesNoCopy(), var3));
            } catch (RuntimeException var6) {
               throw DbException.get(22018, (String)this.getTraceSQL());
            }
      }

      if (var1 != null) {
         var3 = var1.getType();
         var4 = var1.getSrid();
         if (var3 != 0 && var2.getTypeAndDimensionSystem() != var3 || var4 != null && var2.getSRID() != var4) {
            StringBuilder var5 = ExtTypeInfoGeometry.toSQL(new StringBuilder(), var2.getTypeAndDimensionSystem(), var2.getSRID()).append(" -> ");
            var1.getSQL(var5, 3);
            throw DbException.get(22018, (String)var5.toString());
         }
      }

      return var2;
   }

   private ValueJson convertToJson(TypeInfo var1, int var2, Object var3) {
      ValueJson var4;
      switch (this.getValueType()) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 17:
         case 18:
         case 19:
         case 39:
            var4 = ValueJson.get(this.getString());
            break;
         case 5:
         case 6:
         case 7:
            var4 = ValueJson.fromJson(this.getBytesNoCopy());
            break;
         case 8:
            var4 = ValueJson.get(this.getBoolean());
            break;
         case 9:
         case 10:
         case 11:
            var4 = ValueJson.get(this.getInt());
            break;
         case 12:
            var4 = ValueJson.get(this.getLong());
            break;
         case 13:
         case 14:
         case 15:
         case 16:
            var4 = ValueJson.get(this.getBigDecimal());
            break;
         case 20:
            var4 = ValueJson.get(((ValueTimestamp)this).getISOString());
            break;
         case 21:
            var4 = ValueJson.get(((ValueTimestampTimeZone)this).getISOString());
            break;
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         default:
            throw this.getDataConversionError(38);
         case 37:
            ValueGeometry var10 = (ValueGeometry)this;
            var4 = ValueJson.getInternal(GeoJsonUtils.ewkbToGeoJson(var10.getBytesNoCopy(), var10.getDimensionSystem()));
            break;
         case 38:
            var4 = (ValueJson)this;
            break;
         case 40:
            ByteArrayOutputStream var5 = new ByteArrayOutputStream();
            var5.write(91);
            Value[] var6 = ((ValueArray)this).getList();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Value var9 = var6[var8];
               JsonConstructorUtils.jsonArrayAppend(var5, var9, 0);
            }

            var5.write(93);
            var4 = ValueJson.getInternal(var5.toByteArray());
      }

      if (var2 != 0 && (long)var4.getBytesNoCopy().length > var1.getPrecision()) {
         throw var4.getValueTooLongException(var1, var3);
      } else {
         return var4;
      }
   }

   public final ValueUuid convertToUuid() {
      switch (this.getValueType()) {
         case 0:
            throw DbException.getInternalError();
         case 1:
         case 2:
         case 4:
            return ValueUuid.get(this.getString());
         case 5:
         case 6:
            return ValueUuid.get(this.getBytesNoCopy());
         case 35:
            return JdbcUtils.deserializeUuid(this.getBytesNoCopy());
         case 39:
            return (ValueUuid)this;
         default:
            throw this.getDataConversionError(39);
      }
   }

   private ValueArray convertToArray(TypeInfo var1, CastDataProvider var2, int var3, Object var4) {
      TypeInfo var5 = (TypeInfo)var1.getExtTypeInfo();
      int var6 = this.getValueType();
      ValueArray var7;
      Value[] var8;
      if (var6 == 40) {
         var7 = (ValueArray)this;
      } else {
         switch (var6) {
            case 3:
               var8 = new Value[]{ValueVarchar.get(this.getString())};
               break;
            case 7:
               var8 = new Value[]{ValueVarbinary.get(this.getBytesNoCopy())};
               break;
            default:
               var8 = new Value[]{this};
         }

         var7 = ValueArray.get(var8, var2);
      }

      int var9;
      int var10;
      if (var5 != null) {
         var8 = var7.getList();
         var9 = var8.length;

         label47:
         for(var10 = 0; var10 < var9; ++var10) {
            Value var11 = var8[var10];
            Value var12 = var11.convertTo(var5, var2, var3, var4);
            if (var11 != var12) {
               Value[] var13 = new Value[var9];
               System.arraycopy(var8, 0, var13, 0, var10);
               var13[var10] = var12;

               while(true) {
                  ++var10;
                  if (var10 >= var9) {
                     var7 = ValueArray.get(var5, var13, var2);
                     break label47;
                  }

                  var13[var10] = var8[var10].convertTo(var5, var2, var3, var4);
               }
            }
         }
      }

      if (var3 != 0) {
         var8 = var7.getList();
         var9 = var8.length;
         if (var3 == 1) {
            var10 = MathUtils.convertLongToInt(var1.getPrecision());
            if (var9 > var10) {
               var7 = ValueArray.get(var7.getComponentType(), (Value[])Arrays.copyOf(var8, var10), var2);
            }
         } else if ((long)var9 > var1.getPrecision()) {
            throw var7.getValueTooLongException(var1, var4);
         }
      }

      return var7;
   }

   private Value convertToRow(TypeInfo var1, CastDataProvider var2, int var3, Object var4) {
      ValueRow var5;
      if (this.getValueType() == 41) {
         var5 = (ValueRow)this;
      } else {
         var5 = ValueRow.get(new Value[]{this});
      }

      ExtTypeInfoRow var6 = (ExtTypeInfoRow)var1.getExtTypeInfo();
      if (var6 != null) {
         Value[] var7 = var5.getList();
         int var8 = var7.length;
         Set var9 = var6.getFields();
         if (var8 != var9.size()) {
            throw this.getDataConversionError(var1);
         }

         Iterator var10 = var9.iterator();

         for(int var11 = 0; var11 < var8; ++var11) {
            Value var12 = var7[var11];
            TypeInfo var13 = (TypeInfo)((Map.Entry)var10.next()).getValue();
            Value var14 = var12.convertTo(var13, var2, var3, var4);
            if (var12 != var14) {
               Value[] var15 = new Value[var8];
               System.arraycopy(var7, 0, var15, 0, var11);
               var15[var11] = var14;

               while(true) {
                  ++var11;
                  if (var11 >= var8) {
                     var5 = ValueRow.get(var1, var15);
                     return var5;
                  }

                  var15[var11] = var7[var11].convertTo(var13, var2, var3, var4);
               }
            }
         }
      }

      return var5;
   }

   final DbException getDataConversionError(int var1) {
      throw DbException.get(22018, (String)(getTypeName(this.getValueType()) + " to " + getTypeName(var1)));
   }

   final DbException getDataConversionError(TypeInfo var1) {
      throw DbException.get(22018, (String)(getTypeName(this.getValueType()) + " to " + var1.getTraceSQL()));
   }

   final DbException getValueTooLongException(TypeInfo var1, Object var2) {
      StringBuilder var3 = new StringBuilder();
      if (var2 != null) {
         var3.append(var2).append(' ');
      }

      var1.getSQL(var3, 3);
      return DbException.getValueTooLongException(var3.toString(), this.getTraceSQL(), this.getType().getPrecision());
   }

   public abstract int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3);

   public final int compareTo(Value var1, CastDataProvider var2, CompareMode var3) {
      if (this == var1) {
         return 0;
      } else if (this == ValueNull.INSTANCE) {
         return -1;
      } else {
         return var1 == ValueNull.INSTANCE ? 1 : this.compareToNotNullable(var1, var2, var3);
      }
   }

   private int compareToNotNullable(Value var1, CastDataProvider var2, CompareMode var3) {
      Object var4 = this;
      int var5 = this.getValueType();
      int var6 = ((Value)var1).getValueType();
      if (var5 != var6 || var5 == 36) {
         int var7 = getHigherOrder(var5, var6);
         if (var7 == 36) {
            ExtTypeInfoEnum var8 = ExtTypeInfoEnum.getEnumeratorsForBinaryOperation(this, (Value)var1);
            var4 = this.convertToEnum(var8, var2);
            var1 = ((Value)var1).convertToEnum(var8, var2);
         } else {
            if (var7 <= 7) {
               if (var7 <= 3) {
                  if (var5 == 1 || var6 == 1) {
                     var7 = 1;
                  }
               } else if (var7 >= 5 && (var5 == 5 || var6 == 5)) {
                  var7 = 5;
               }
            }

            var4 = this.convertTo(var7, var2);
            var1 = ((Value)var1).convertTo(var7, var2);
         }
      }

      return ((Value)var4).compareTypeSafe((Value)var1, var3, var2);
   }

   public int compareWithNull(Value var1, boolean var2, CastDataProvider var3, CompareMode var4) {
      return this != ValueNull.INSTANCE && var1 != ValueNull.INSTANCE ? this.compareToNotNullable(var1, var3, var4) : Integer.MIN_VALUE;
   }

   public boolean containsNull() {
      return false;
   }

   private static byte convertToByte(long var0, Object var2) {
      if (var0 <= 127L && var0 >= -128L) {
         return (byte)((int)var0);
      } else {
         throw DbException.get(22004, (String[])(Long.toString(var0), getColumnName(var2)));
      }
   }

   private static short convertToShort(long var0, Object var2) {
      if (var0 <= 32767L && var0 >= -32768L) {
         return (short)((int)var0);
      } else {
         throw DbException.get(22004, (String[])(Long.toString(var0), getColumnName(var2)));
      }
   }

   public static int convertToInt(long var0, Object var2) {
      if (var0 <= 2147483647L && var0 >= -2147483648L) {
         return (int)var0;
      } else {
         throw DbException.get(22004, (String[])(Long.toString(var0), getColumnName(var2)));
      }
   }

   private static long convertToLong(double var0, Object var2) {
      if (!(var0 > 9.223372036854776E18) && !(var0 < -9.223372036854776E18)) {
         return Math.round(var0);
      } else {
         throw DbException.get(22004, (String[])(Double.toString(var0), getColumnName(var2)));
      }
   }

   private static long convertToLong(BigDecimal var0, Object var1) {
      if (var0.compareTo(MAX_LONG_DECIMAL) <= 0 && var0.compareTo(MIN_LONG_DECIMAL) >= 0) {
         return var0.setScale(0, RoundingMode.HALF_UP).longValue();
      } else {
         throw DbException.get(22004, (String[])(var0.toString(), getColumnName(var1)));
      }
   }

   private static String getColumnName(Object var0) {
      return var0 == null ? "" : var0.toString();
   }

   public String toString() {
      return this.getTraceSQL();
   }

   protected final DbException getUnsupportedExceptionForOperation(String var1) {
      return DbException.getUnsupportedException(getTypeName(this.getValueType()) + ' ' + var1);
   }

   public long charLength() {
      return (long)this.getString().length();
   }

   public long octetLength() {
      return (long)this.getBytesNoCopy().length;
   }

   public final boolean isTrue() {
      return this != ValueNull.INSTANCE ? this.getBoolean() : false;
   }

   public final boolean isFalse() {
      return this != ValueNull.INSTANCE && !this.getBoolean();
   }
}
