package org.h2.value;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import org.h2.api.Interval;
import org.h2.command.CommandInterface;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Session;
import org.h2.jdbc.JdbcArray;
import org.h2.jdbc.JdbcBlob;
import org.h2.jdbc.JdbcClob;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcLob;
import org.h2.jdbc.JdbcResultSet;
import org.h2.jdbc.JdbcSQLXML;
import org.h2.jdbc.JdbcStatement;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.util.JSR310Utils;
import org.h2.util.JdbcUtils;
import org.h2.util.LegacyDateTimeUtils;

public final class ValueToObjectConverter extends TraceObject {
   public static final Class<?> GEOMETRY_CLASS;
   private static final String GEOMETRY_CLASS_NAME = "org.locationtech.jts.geom.Geometry";

   public static Value objectToValue(Session var0, Object var1, int var2) {
      if (var1 == null) {
         return ValueNull.INSTANCE;
      } else if (var2 == 35) {
         return ValueJavaObject.getNoCopy(JdbcUtils.serialize(var1, var0.getJavaObjectSerializer()));
      } else if (var1 instanceof Value) {
         Value var5 = (Value)var1;
         if (var5 instanceof ValueLob) {
            var0.addTemporaryLob((ValueLob)var5);
         }

         return var5;
      } else {
         Class var3 = var1.getClass();
         if (var3 == String.class) {
            return ValueVarchar.get((String)var1, var0);
         } else if (var3 == Long.class) {
            return ValueBigint.get((Long)var1);
         } else if (var3 == Integer.class) {
            return ValueInteger.get((Integer)var1);
         } else if (var3 == Boolean.class) {
            return ValueBoolean.get((Boolean)var1);
         } else if (var3 == Byte.class) {
            return ValueTinyint.get((Byte)var1);
         } else if (var3 == Short.class) {
            return ValueSmallint.get((Short)var1);
         } else if (var3 == Float.class) {
            return ValueReal.get((Float)var1);
         } else if (var3 == Double.class) {
            return ValueDouble.get((Double)var1);
         } else if (var3 == byte[].class) {
            return ValueVarbinary.get((byte[])((byte[])var1));
         } else if (var3 == UUID.class) {
            return ValueUuid.get((UUID)var1);
         } else if (var3 == Character.class) {
            return ValueChar.get(((Character)var1).toString());
         } else if (var3 == LocalDate.class) {
            return JSR310Utils.localDateToValue((LocalDate)var1);
         } else if (var3 == LocalTime.class) {
            return JSR310Utils.localTimeToValue((LocalTime)var1);
         } else if (var3 == LocalDateTime.class) {
            return JSR310Utils.localDateTimeToValue((LocalDateTime)var1);
         } else if (var3 == Instant.class) {
            return JSR310Utils.instantToValue((Instant)var1);
         } else if (var3 == OffsetTime.class) {
            return JSR310Utils.offsetTimeToValue((OffsetTime)var1);
         } else if (var3 == OffsetDateTime.class) {
            return JSR310Utils.offsetDateTimeToValue((OffsetDateTime)var1);
         } else if (var3 == ZonedDateTime.class) {
            return JSR310Utils.zonedDateTimeToValue((ZonedDateTime)var1);
         } else if (var3 == Interval.class) {
            Interval var4 = (Interval)var1;
            return ValueInterval.from(var4.getQualifier(), var4.isNegative(), var4.getLeading(), var4.getRemaining());
         } else if (var3 == Period.class) {
            return JSR310Utils.periodToValue((Period)var1);
         } else if (var3 == Duration.class) {
            return JSR310Utils.durationToValue((Duration)var1);
         } else if (var1 instanceof Object[]) {
            return arrayToValue(var0, var1);
         } else if (GEOMETRY_CLASS != null && GEOMETRY_CLASS.isAssignableFrom(var3)) {
            return ValueGeometry.getFromGeometry(var1);
         } else if (var1 instanceof BigInteger) {
            return ValueNumeric.get((BigInteger)var1);
         } else {
            return (Value)(var1 instanceof BigDecimal ? ValueNumeric.getAnyScale((BigDecimal)var1) : otherToValue(var0, var1));
         }
      }
   }

   private static Value otherToValue(Session var0, Object var1) {
      if (var1 instanceof Array) {
         Array var9 = (Array)var1;

         try {
            return arrayToValue(var0, var9.getArray());
         } catch (SQLException var5) {
            throw DbException.convert(var5);
         }
      } else if (var1 instanceof ResultSet) {
         return resultSetToValue(var0, (ResultSet)var1);
      } else {
         Object var2;
         if (var1 instanceof Reader) {
            Object var3 = (Reader)var1;
            if (!(var3 instanceof BufferedReader)) {
               var3 = new BufferedReader((Reader)var3);
            }

            var2 = var0.getDataHandler().getLobStorage().createClob((Reader)var3, -1L);
         } else if (var1 instanceof Clob) {
            try {
               Clob var10 = (Clob)var1;
               BufferedReader var4 = new BufferedReader(var10.getCharacterStream());
               var2 = var0.getDataHandler().getLobStorage().createClob(var4, var10.length());
            } catch (SQLException var8) {
               throw DbException.convert(var8);
            }
         } else if (var1 instanceof InputStream) {
            var2 = var0.getDataHandler().getLobStorage().createBlob((InputStream)var1, -1L);
         } else if (var1 instanceof Blob) {
            try {
               Blob var11 = (Blob)var1;
               var2 = var0.getDataHandler().getLobStorage().createBlob(var11.getBinaryStream(), var11.length());
            } catch (SQLException var7) {
               throw DbException.convert(var7);
            }
         } else {
            if (!(var1 instanceof SQLXML)) {
               Value var12 = LegacyDateTimeUtils.legacyObjectToValue(var0, var1);
               if (var12 != null) {
                  return var12;
               }

               return ValueJavaObject.getNoCopy(JdbcUtils.serialize(var1, var0.getJavaObjectSerializer()));
            }

            try {
               var2 = var0.getDataHandler().getLobStorage().createClob(new BufferedReader(((SQLXML)var1).getCharacterStream()), -1L);
            } catch (SQLException var6) {
               throw DbException.convert(var6);
            }
         }

         return var0.addTemporaryLob((ValueLob)var2);
      }
   }

   private static Value arrayToValue(Session var0, Object var1) {
      Object[] var2 = (Object[])((Object[])var1);
      int var3 = var2.length;
      Value[] var4 = new Value[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         var4[var5] = objectToValue(var0, var2[var5], -1);
      }

      return ValueArray.get(var4, var0);
   }

   static Value resultSetToValue(Session var0, ResultSet var1) {
      try {
         ResultSetMetaData var2 = var1.getMetaData();
         int var3 = var2.getColumnCount();
         LinkedHashMap var4 = readResultSetMeta(var0, var2, var3);
         if (!var1.next()) {
            throw DbException.get(22018, (String)"Empty ResultSet to ROW value");
         } else {
            Value[] var5 = new Value[var3];
            Iterator var6 = var4.entrySet().iterator();

            for(int var7 = 0; var7 < var3; ++var7) {
               var5[var7] = objectToValue(var0, var1.getObject(var7 + 1), ((TypeInfo)((Map.Entry)var6.next()).getValue()).getValueType());
            }

            if (var1.next()) {
               throw DbException.get(22018, (String)"Multi-row ResultSet to ROW value");
            } else {
               return ValueRow.get(new ExtTypeInfoRow(var4), var5);
            }
         }
      } catch (SQLException var8) {
         throw DbException.convert(var8);
      }
   }

   private static LinkedHashMap<String, TypeInfo> readResultSetMeta(Session var0, ResultSetMetaData var1, int var2) throws SQLException {
      LinkedHashMap var3 = new LinkedHashMap();

      for(int var4 = 0; var4 < var2; ++var4) {
         String var5 = var1.getColumnLabel(var4 + 1);
         String var6 = var1.getColumnTypeName(var4 + 1);
         int var7 = DataType.convertSQLTypeToValueType(var1.getColumnType(var4 + 1), var6);
         int var8 = var1.getPrecision(var4 + 1);
         int var9 = var1.getScale(var4 + 1);
         TypeInfo var10;
         if (var7 == 40 && var6.endsWith(" ARRAY")) {
            var10 = TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.getTypeInfo(DataType.getTypeByName(var6.substring(0, var6.length() - 6), var0.getMode()).type));
         } else {
            var10 = TypeInfo.getTypeInfo(var7, (long)var8, var9, (ExtTypeInfo)null);
         }

         var3.put(var5, var10);
      }

      return var3;
   }

   public static <T> T valueToObject(Class<T> var0, Value var1, JdbcConnection var2) {
      if (var1 == ValueNull.INSTANCE) {
         return null;
      } else if (var0 == BigDecimal.class) {
         return var1.getBigDecimal();
      } else if (var0 == BigInteger.class) {
         return var1.getBigDecimal().toBigInteger();
      } else if (var0 == String.class) {
         return var1.getString();
      } else if (var0 == Boolean.class) {
         return var1.getBoolean();
      } else if (var0 == Byte.class) {
         return var1.getByte();
      } else if (var0 == Short.class) {
         return var1.getShort();
      } else if (var0 == Integer.class) {
         return var1.getInt();
      } else if (var0 == Long.class) {
         return var1.getLong();
      } else if (var0 == Float.class) {
         return var1.getFloat();
      } else if (var0 == Double.class) {
         return var1.getDouble();
      } else if (var0 == UUID.class) {
         return var1.convertToUuid().getUuid();
      } else if (var0 == byte[].class) {
         return var1.getBytes();
      } else if (var0 == Character.class) {
         String var4 = var1.getString();
         return var4.isEmpty() ? ' ' : var4.charAt(0);
      } else if (var0 == Interval.class) {
         if (!(var1 instanceof ValueInterval)) {
            var1 = var1.convertTo(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND);
         }

         ValueInterval var3 = (ValueInterval)var1;
         return new Interval(var3.getQualifier(), false, var3.getLeading(), var3.getRemaining());
      } else if (var0 == LocalDate.class) {
         return JSR310Utils.valueToLocalDate(var1, var2);
      } else if (var0 == LocalTime.class) {
         return JSR310Utils.valueToLocalTime(var1, var2);
      } else if (var0 == LocalDateTime.class) {
         return JSR310Utils.valueToLocalDateTime(var1, var2);
      } else if (var0 == OffsetTime.class) {
         return JSR310Utils.valueToOffsetTime(var1, var2);
      } else if (var0 == OffsetDateTime.class) {
         return JSR310Utils.valueToOffsetDateTime(var1, var2);
      } else if (var0 == ZonedDateTime.class) {
         return JSR310Utils.valueToZonedDateTime(var1, var2);
      } else if (var0 == Instant.class) {
         return JSR310Utils.valueToInstant(var1, var2);
      } else if (var0 == Period.class) {
         return JSR310Utils.valueToPeriod(var1);
      } else if (var0 == Duration.class) {
         return JSR310Utils.valueToDuration(var1);
      } else if (var0.isArray()) {
         return valueToArray(var0, var1, var2);
      } else {
         return GEOMETRY_CLASS != null && GEOMETRY_CLASS.isAssignableFrom(var0) ? var1.convertToGeometry((ExtTypeInfoGeometry)null).getGeometry() : valueToOther(var0, var1, var2);
      }
   }

   private static Object valueToArray(Class<?> var0, Value var1, JdbcConnection var2) {
      Value[] var3 = ((ValueArray)var1).getList();
      Class var4 = var0.getComponentType();
      int var5 = var3.length;
      Object[] var6 = (Object[])((Object[])java.lang.reflect.Array.newInstance(var4, var5));

      for(int var7 = 0; var7 < var5; ++var7) {
         var6[var7] = valueToObject(var4, var3[var7], var2);
      }

      return var6;
   }

   private static Object valueToOther(Class<?> var0, Value var1, JdbcConnection var2) {
      if (var0 == Object.class) {
         return JdbcUtils.deserialize(var1.convertToJavaObject(TypeInfo.TYPE_JAVA_OBJECT, 0, (Object)null).getBytesNoCopy(), var2.getJavaObjectSerializer());
      } else if (var0 == InputStream.class) {
         return var1.getInputStream();
      } else if (var0 == Reader.class) {
         return var1.getReader();
      } else if (var0 == Array.class) {
         return new JdbcArray(var2, var1, getNextId(16));
      } else if (var0 == Blob.class) {
         return new JdbcBlob(var2, var1, JdbcLob.State.WITH_VALUE, getNextId(9));
      } else if (var0 == Clob.class) {
         return new JdbcClob(var2, var1, JdbcLob.State.WITH_VALUE, getNextId(10));
      } else if (var0 == SQLXML.class) {
         return new JdbcSQLXML(var2, var1, JdbcLob.State.WITH_VALUE, getNextId(17));
      } else if (var0 == ResultSet.class) {
         return new JdbcResultSet(var2, (JdbcStatement)null, (CommandInterface)null, var1.convertToAnyRow().getResult(), getNextId(4), true, false, false);
      } else {
         Object var3 = LegacyDateTimeUtils.valueToLegacyType(var0, var1, var2);
         if (var3 != null) {
            return var3;
         } else {
            if (var1.getValueType() == 35) {
               var3 = JdbcUtils.deserialize(var1.getBytesNoCopy(), var2.getJavaObjectSerializer());
               if (var0.isAssignableFrom(var3.getClass())) {
                  return var3;
               }
            }

            throw DbException.getUnsupportedException("converting to class " + var0.getName());
         }
      }
   }

   public static Class<?> getDefaultClass(int var0, boolean var1) {
      switch (var0) {
         case 0:
            return Void.class;
         case 1:
         case 2:
         case 4:
         case 36:
            return String.class;
         case 3:
            return Clob.class;
         case 5:
         case 6:
         case 38:
            return byte[].class;
         case 7:
            return Blob.class;
         case 8:
            return Boolean.class;
         case 9:
            if (var1) {
               return Integer.class;
            }

            return Byte.class;
         case 10:
            if (var1) {
               return Integer.class;
            }

            return Short.class;
         case 11:
            return Integer.class;
         case 12:
            return Long.class;
         case 13:
         case 16:
            return BigDecimal.class;
         case 14:
            return Float.class;
         case 15:
            return Double.class;
         case 17:
            return var1 ? Date.class : LocalDate.class;
         case 18:
            return var1 ? Time.class : LocalTime.class;
         case 19:
            return OffsetTime.class;
         case 20:
            return var1 ? Timestamp.class : LocalDateTime.class;
         case 21:
            return OffsetDateTime.class;
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
            return Interval.class;
         case 35:
            return var1 ? Object.class : byte[].class;
         case 37:
            Class var2 = GEOMETRY_CLASS;
            return var2 != null ? var2 : String.class;
         case 39:
            return UUID.class;
         case 40:
            if (var1) {
               return Array.class;
            }

            return Object[].class;
         case 41:
            if (var1) {
               return ResultSet.class;
            }

            return Object[].class;
         default:
            throw DbException.getUnsupportedException("data type " + var0);
      }
   }

   public static Object valueToDefaultObject(Value var0, JdbcConnection var1, boolean var2) {
      switch (var0.getValueType()) {
         case 0:
            return null;
         case 1:
         case 2:
         case 4:
         case 36:
            return var0.getString();
         case 3:
            return new JdbcClob(var1, var0, JdbcLob.State.WITH_VALUE, getNextId(10));
         case 5:
         case 6:
         case 38:
            return var0.getBytes();
         case 7:
            return new JdbcBlob(var1, var0, JdbcLob.State.WITH_VALUE, getNextId(9));
         case 8:
            return var0.getBoolean();
         case 9:
            if (var2) {
               return var0.getInt();
            }

            return var0.getByte();
         case 10:
            if (var2) {
               return var0.getInt();
            }

            return var0.getShort();
         case 11:
            return var0.getInt();
         case 12:
            return var0.getLong();
         case 13:
         case 16:
            return var0.getBigDecimal();
         case 14:
            return var0.getFloat();
         case 15:
            return var0.getDouble();
         case 17:
            return var2 ? LegacyDateTimeUtils.toDate(var1, (TimeZone)null, var0) : JSR310Utils.valueToLocalDate(var0, (CastDataProvider)null);
         case 18:
            return var2 ? LegacyDateTimeUtils.toTime(var1, (TimeZone)null, var0) : JSR310Utils.valueToLocalTime(var0, (CastDataProvider)null);
         case 19:
            return JSR310Utils.valueToOffsetTime(var0, (CastDataProvider)null);
         case 20:
            return var2 ? LegacyDateTimeUtils.toTimestamp(var1, (TimeZone)null, var0) : JSR310Utils.valueToLocalDateTime(var0, (CastDataProvider)null);
         case 21:
            return JSR310Utils.valueToOffsetDateTime(var0, (CastDataProvider)null);
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
            return ((ValueInterval)var0).getInterval();
         case 35:
            return var2 ? JdbcUtils.deserialize(var0.getBytesNoCopy(), var1.getJavaObjectSerializer()) : var0.getBytes();
         case 37:
            return GEOMETRY_CLASS != null ? ((ValueGeometry)var0).getGeometry() : var0.getString();
         case 39:
            return ((ValueUuid)var0).getUuid();
         case 40:
            if (var2) {
               return new JdbcArray(var1, var0, getNextId(16));
            }

            return valueToDefaultArray(var0, var1, var2);
         case 41:
            if (var2) {
               return new JdbcResultSet(var1, (JdbcStatement)null, (CommandInterface)null, ((ValueRow)var0).getResult(), getNextId(4), true, false, false);
            }

            return valueToDefaultArray(var0, var1, var2);
         default:
            throw DbException.getUnsupportedException("data type " + var0.getValueType());
      }
   }

   public static Object valueToDefaultArray(Value var0, JdbcConnection var1, boolean var2) {
      Value[] var3 = ((ValueCollectionBase)var0).getList();
      int var4 = var3.length;
      Object[] var5 = new Object[var4];

      for(int var6 = 0; var6 < var4; ++var6) {
         var5[var6] = valueToDefaultObject(var3[var6], var1, var2);
      }

      return var5;
   }

   public static Value readValue(Session var0, JdbcResultSet var1, int var2) {
      Object var3 = var1.getInternal(var2);
      switch (((Value)var3).getValueType()) {
         case 3:
            var3 = var0.addTemporaryLob(var0.getDataHandler().getLobStorage().createClob(new BufferedReader(((Value)var3).getReader()), -1L));
            break;
         case 7:
            var3 = var0.addTemporaryLob(var0.getDataHandler().getLobStorage().createBlob(((Value)var3).getInputStream(), -1L));
      }

      return (Value)var3;
   }

   private ValueToObjectConverter() {
   }

   static {
      Class var0;
      try {
         var0 = JdbcUtils.loadUserClass("org.locationtech.jts.geom.Geometry");
      } catch (Exception var2) {
         var0 = null;
      }

      GEOMETRY_CLASS = var0;
   }
}
