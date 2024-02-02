package org.h2.value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.UUID;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Session;
import org.h2.jdbc.JdbcResultSet;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.util.IntervalUtils;
import org.h2.util.JSR310Utils;
import org.h2.util.JdbcUtils;
import org.h2.util.LegacyDateTimeUtils;
import org.h2.util.Utils;

public final class ValueToObjectConverter2 extends TraceObject {
   public static TypeInfo classToType(Class<?> var0) {
      if (var0 == null) {
         return TypeInfo.TYPE_NULL;
      } else {
         if (var0.isPrimitive()) {
            var0 = Utils.getNonPrimitiveClass(var0);
         }

         if (var0 == Void.class) {
            return TypeInfo.TYPE_NULL;
         } else if (var0 != String.class && var0 != Character.class) {
            if (var0 == byte[].class) {
               return TypeInfo.TYPE_VARBINARY;
            } else if (var0 == Boolean.class) {
               return TypeInfo.TYPE_BOOLEAN;
            } else if (var0 == Byte.class) {
               return TypeInfo.TYPE_TINYINT;
            } else if (var0 == Short.class) {
               return TypeInfo.TYPE_SMALLINT;
            } else if (var0 == Integer.class) {
               return TypeInfo.TYPE_INTEGER;
            } else if (var0 == Long.class) {
               return TypeInfo.TYPE_BIGINT;
            } else if (var0 == Float.class) {
               return TypeInfo.TYPE_REAL;
            } else if (var0 == Double.class) {
               return TypeInfo.TYPE_DOUBLE;
            } else if (var0 == LocalDate.class) {
               return TypeInfo.TYPE_DATE;
            } else if (var0 == LocalTime.class) {
               return TypeInfo.TYPE_TIME;
            } else if (var0 == OffsetTime.class) {
               return TypeInfo.TYPE_TIME_TZ;
            } else if (var0 == LocalDateTime.class) {
               return TypeInfo.TYPE_TIMESTAMP;
            } else if (var0 != OffsetDateTime.class && var0 != ZonedDateTime.class && var0 != Instant.class) {
               if (var0 == Period.class) {
                  return TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH;
               } else if (var0 == Duration.class) {
                  return TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND;
               } else if (UUID.class == var0) {
                  return TypeInfo.TYPE_UUID;
               } else if (var0.isArray()) {
                  return TypeInfo.getTypeInfo(40, 2147483647L, 0, classToType(var0.getComponentType()));
               } else if (!Clob.class.isAssignableFrom(var0) && !Reader.class.isAssignableFrom(var0)) {
                  if (!Blob.class.isAssignableFrom(var0) && !InputStream.class.isAssignableFrom(var0)) {
                     if (BigDecimal.class.isAssignableFrom(var0)) {
                        return TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
                     } else if (ValueToObjectConverter.GEOMETRY_CLASS != null && ValueToObjectConverter.GEOMETRY_CLASS.isAssignableFrom(var0)) {
                        return TypeInfo.TYPE_GEOMETRY;
                     } else if (Array.class.isAssignableFrom(var0)) {
                        return TypeInfo.TYPE_ARRAY_UNKNOWN;
                     } else if (ResultSet.class.isAssignableFrom(var0)) {
                        return TypeInfo.TYPE_ROW_EMPTY;
                     } else {
                        TypeInfo var1 = LegacyDateTimeUtils.legacyClassToType(var0);
                        return var1 != null ? var1 : TypeInfo.TYPE_JAVA_OBJECT;
                     }
                  } else {
                     return TypeInfo.TYPE_BLOB;
                  }
               } else {
                  return TypeInfo.TYPE_CLOB;
               }
            } else {
               return TypeInfo.TYPE_TIMESTAMP_TZ;
            }
         } else {
            return TypeInfo.TYPE_VARCHAR;
         }
      }
   }

   public static Value readValue(Session var0, ResultSet var1, int var2, int var3) {
      Value var4;
      if (var1 instanceof JdbcResultSet) {
         var4 = ValueToObjectConverter.readValue(var0, (JdbcResultSet)var1, var2);
      } else {
         try {
            var4 = readValueOther(var0, var1, var2, var3);
         } catch (SQLException var6) {
            throw DbException.convert(var6);
         }
      }

      return var4;
   }

   private static Value readValueOther(Session var0, ResultSet var1, int var2, int var3) throws SQLException {
      Object var4;
      Object var5;
      Object[] var6;
      int var7;
      Value[] var8;
      int var9;
      int var18;
      Object var20;
      byte[] var21;
      String var24;
      BigDecimal var32;
      switch (var3) {
         case 0:
            var4 = ValueNull.INSTANCE;
            break;
         case 1:
            var24 = var1.getString(var2);
            var4 = var24 == null ? ValueNull.INSTANCE : ValueChar.get(var24);
            break;
         case 2:
            var24 = var1.getString(var2);
            var4 = var24 == null ? ValueNull.INSTANCE : ValueVarchar.get(var24, var0);
            break;
         case 3:
            if (var0 == null) {
               var24 = var1.getString(var2);
               var4 = var24 == null ? ValueNull.INSTANCE : ValueClob.createSmall(var24);
            } else {
               Reader var40 = var1.getCharacterStream(var2);
               var4 = var40 == null ? ValueNull.INSTANCE : var0.addTemporaryLob(var0.getDataHandler().getLobStorage().createClob(new BufferedReader(var40), -1L));
            }
            break;
         case 4:
            var24 = var1.getString(var2);
            var4 = var24 == null ? ValueNull.INSTANCE : ValueVarcharIgnoreCase.get(var24);
            break;
         case 5:
            var21 = var1.getBytes(var2);
            var4 = var21 == null ? ValueNull.INSTANCE : ValueBinary.getNoCopy(var21);
            break;
         case 6:
            var21 = var1.getBytes(var2);
            var4 = var21 == null ? ValueNull.INSTANCE : ValueVarbinary.getNoCopy(var21);
            break;
         case 7:
            if (var0 == null) {
               var21 = var1.getBytes(var2);
               var4 = var21 == null ? ValueNull.INSTANCE : ValueBlob.createSmall(var21);
            } else {
               InputStream var39 = var1.getBinaryStream(var2);
               var4 = var39 == null ? ValueNull.INSTANCE : var0.addTemporaryLob(var0.getDataHandler().getLobStorage().createBlob(var39, -1L));
            }
            break;
         case 8:
            boolean var38 = var1.getBoolean(var2);
            var4 = var1.wasNull() ? ValueNull.INSTANCE : ValueBoolean.get(var38);
            break;
         case 9:
            byte var37 = var1.getByte(var2);
            var4 = var1.wasNull() ? ValueNull.INSTANCE : ValueTinyint.get(var37);
            break;
         case 10:
            short var36 = var1.getShort(var2);
            var4 = var1.wasNull() ? ValueNull.INSTANCE : ValueSmallint.get(var36);
            break;
         case 11:
            var18 = var1.getInt(var2);
            var4 = var1.wasNull() ? ValueNull.INSTANCE : ValueInteger.get(var18);
            break;
         case 12:
            long var35 = var1.getLong(var2);
            var4 = var1.wasNull() ? ValueNull.INSTANCE : ValueBigint.get(var35);
            break;
         case 13:
            var32 = var1.getBigDecimal(var2);
            var4 = var32 == null ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(var32);
            break;
         case 14:
            float var34 = var1.getFloat(var2);
            var4 = var1.wasNull() ? ValueNull.INSTANCE : ValueReal.get(var34);
            break;
         case 15:
            double var33 = var1.getDouble(var2);
            var4 = var1.wasNull() ? ValueNull.INSTANCE : ValueDouble.get(var33);
            break;
         case 16:
            var32 = var1.getBigDecimal(var2);
            var4 = var32 == null ? ValueNull.INSTANCE : ValueDecfloat.get(var32);
            break;
         case 17:
            try {
               LocalDate var31 = (LocalDate)var1.getObject(var2, LocalDate.class);
               var4 = var31 == null ? ValueNull.INSTANCE : JSR310Utils.localDateToValue(var31);
            } catch (SQLException var16) {
               Date var26 = var1.getDate(var2);
               var4 = var26 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate(var0, (TimeZone)null, var26);
            }
            break;
         case 18:
            try {
               LocalTime var30 = (LocalTime)var1.getObject(var2, LocalTime.class);
               var4 = var30 == null ? ValueNull.INSTANCE : JSR310Utils.localTimeToValue(var30);
            } catch (SQLException var15) {
               Time var25 = var1.getTime(var2);
               var4 = var25 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime(var0, (TimeZone)null, var25);
            }
            break;
         case 19:
            try {
               OffsetTime var29 = (OffsetTime)var1.getObject(var2, OffsetTime.class);
               var4 = var29 == null ? ValueNull.INSTANCE : JSR310Utils.offsetTimeToValue(var29);
            } catch (SQLException var14) {
               var20 = var1.getObject(var2);
               if (var20 == null) {
                  var4 = ValueNull.INSTANCE;
               } else {
                  var4 = ValueTimeTimeZone.parse(var20.toString());
               }
            }
            break;
         case 20:
            try {
               LocalDateTime var28 = (LocalDateTime)var1.getObject(var2, LocalDateTime.class);
               var4 = var28 == null ? ValueNull.INSTANCE : JSR310Utils.localDateTimeToValue(var28);
            } catch (SQLException var13) {
               Timestamp var22 = var1.getTimestamp(var2);
               var4 = var22 == null ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp(var0, (TimeZone)null, var22);
            }
            break;
         case 21:
            try {
               OffsetDateTime var27 = (OffsetDateTime)var1.getObject(var2, OffsetDateTime.class);
               var4 = var27 == null ? ValueNull.INSTANCE : JSR310Utils.offsetDateTimeToValue(var27);
            } catch (SQLException var12) {
               var20 = var1.getObject(var2);
               if (var20 == null) {
                  var4 = ValueNull.INSTANCE;
               } else if (var20 instanceof ZonedDateTime) {
                  var4 = JSR310Utils.zonedDateTimeToValue((ZonedDateTime)var20);
               } else {
                  var4 = ValueTimestampTimeZone.parse(var20.toString(), var0);
               }
            }
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
            var24 = var1.getString(var2);
            var4 = var24 == null ? ValueNull.INSTANCE : IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(var3 - 22), var24);
            break;
         case 35:
            try {
               var21 = var1.getBytes(var2);
            } catch (SQLException var11) {
               try {
                  Object var23 = var1.getObject(var2);
                  var21 = var23 != null ? JdbcUtils.serialize(var23, var0.getJavaObjectSerializer()) : null;
               } catch (Exception var10) {
                  throw DbException.convert(var10);
               }
            }

            var4 = var21 == null ? ValueNull.INSTANCE : ValueJavaObject.getNoCopy(var21);
            break;
         case 36:
            var18 = var1.getInt(var2);
            var4 = var1.wasNull() ? ValueNull.INSTANCE : ValueInteger.get(var18);
            break;
         case 37:
            var5 = var1.getObject(var2);
            var4 = var5 == null ? ValueNull.INSTANCE : ValueGeometry.getFromGeometry(var5);
            break;
         case 38:
            var5 = var1.getObject(var2);
            if (var5 == null) {
               var4 = ValueNull.INSTANCE;
            } else {
               Class var19 = var5.getClass();
               if (var19 == byte[].class) {
                  var4 = ValueJson.fromJson((byte[])((byte[])var5));
               } else if (var19 == String.class) {
                  var4 = ValueJson.fromJson((String)var5);
               } else {
                  var4 = ValueJson.fromJson(var5.toString());
               }
            }
            break;
         case 39:
            var5 = var1.getObject(var2);
            if (var5 == null) {
               var4 = ValueNull.INSTANCE;
            } else if (var5 instanceof UUID) {
               var4 = ValueUuid.get((UUID)var5);
            } else if (var5 instanceof byte[]) {
               var4 = ValueUuid.get((byte[])((byte[])var5));
            } else {
               var4 = ValueUuid.get((String)var5);
            }
            break;
         case 40:
            Array var17 = var1.getArray(var2);
            if (var17 == null) {
               var4 = ValueNull.INSTANCE;
            } else {
               var6 = (Object[])((Object[])var17.getArray());
               if (var6 == null) {
                  var4 = ValueNull.INSTANCE;
               } else {
                  var7 = var6.length;
                  var8 = new Value[var7];

                  for(var9 = 0; var9 < var7; ++var9) {
                     var8[var9] = ValueToObjectConverter.objectToValue(var0, var6[var9], 0);
                  }

                  var4 = ValueArray.get(var8, var0);
               }
            }
            break;
         case 41:
            var5 = var1.getObject(var2);
            if (var5 == null) {
               var4 = ValueNull.INSTANCE;
            } else if (var5 instanceof ResultSet) {
               var4 = ValueToObjectConverter.resultSetToValue(var0, (ResultSet)var5);
            } else {
               var6 = (Object[])((Object[])var5);
               var7 = var6.length;
               var8 = new Value[var7];

               for(var9 = 0; var9 < var7; ++var9) {
                  var8[var9] = ValueToObjectConverter.objectToValue(var0, var6[var9], 0);
               }

               var4 = ValueRow.get(var8);
            }
            break;
         default:
            throw DbException.getInternalError("data type " + var3);
      }

      return (Value)var4;
   }

   private ValueToObjectConverter2() {
   }
}
