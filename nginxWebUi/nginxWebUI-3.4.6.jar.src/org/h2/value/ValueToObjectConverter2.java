/*     */ package org.h2.value;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.Period;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.util.UUID;
/*     */ import org.h2.api.IntervalQualifier;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.jdbc.JdbcResultSet;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.TraceObject;
/*     */ import org.h2.util.IntervalUtils;
/*     */ import org.h2.util.JSR310Utils;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.LegacyDateTimeUtils;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValueToObjectConverter2
/*     */   extends TraceObject
/*     */ {
/*     */   public static TypeInfo classToType(Class<?> paramClass) {
/*  58 */     if (paramClass == null) {
/*  59 */       return TypeInfo.TYPE_NULL;
/*     */     }
/*  61 */     if (paramClass.isPrimitive()) {
/*  62 */       paramClass = Utils.getNonPrimitiveClass(paramClass);
/*     */     }
/*  64 */     if (paramClass == Void.class)
/*  65 */       return TypeInfo.TYPE_NULL; 
/*  66 */     if (paramClass == String.class || paramClass == Character.class)
/*  67 */       return TypeInfo.TYPE_VARCHAR; 
/*  68 */     if (paramClass == byte[].class)
/*  69 */       return TypeInfo.TYPE_VARBINARY; 
/*  70 */     if (paramClass == Boolean.class)
/*  71 */       return TypeInfo.TYPE_BOOLEAN; 
/*  72 */     if (paramClass == Byte.class)
/*  73 */       return TypeInfo.TYPE_TINYINT; 
/*  74 */     if (paramClass == Short.class)
/*  75 */       return TypeInfo.TYPE_SMALLINT; 
/*  76 */     if (paramClass == Integer.class)
/*  77 */       return TypeInfo.TYPE_INTEGER; 
/*  78 */     if (paramClass == Long.class)
/*  79 */       return TypeInfo.TYPE_BIGINT; 
/*  80 */     if (paramClass == Float.class)
/*  81 */       return TypeInfo.TYPE_REAL; 
/*  82 */     if (paramClass == Double.class)
/*  83 */       return TypeInfo.TYPE_DOUBLE; 
/*  84 */     if (paramClass == LocalDate.class)
/*  85 */       return TypeInfo.TYPE_DATE; 
/*  86 */     if (paramClass == LocalTime.class)
/*  87 */       return TypeInfo.TYPE_TIME; 
/*  88 */     if (paramClass == OffsetTime.class)
/*  89 */       return TypeInfo.TYPE_TIME_TZ; 
/*  90 */     if (paramClass == LocalDateTime.class)
/*  91 */       return TypeInfo.TYPE_TIMESTAMP; 
/*  92 */     if (paramClass == OffsetDateTime.class || paramClass == ZonedDateTime.class || paramClass == Instant.class)
/*  93 */       return TypeInfo.TYPE_TIMESTAMP_TZ; 
/*  94 */     if (paramClass == Period.class)
/*  95 */       return TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH; 
/*  96 */     if (paramClass == Duration.class)
/*  97 */       return TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND; 
/*  98 */     if (UUID.class == paramClass)
/*  99 */       return TypeInfo.TYPE_UUID; 
/* 100 */     if (paramClass.isArray())
/* 101 */       return TypeInfo.getTypeInfo(40, 2147483647L, 0, classToType(paramClass.getComponentType())); 
/* 102 */     if (Clob.class.isAssignableFrom(paramClass) || Reader.class.isAssignableFrom(paramClass))
/* 103 */       return TypeInfo.TYPE_CLOB; 
/* 104 */     if (Blob.class.isAssignableFrom(paramClass) || InputStream.class.isAssignableFrom(paramClass))
/* 105 */       return TypeInfo.TYPE_BLOB; 
/* 106 */     if (BigDecimal.class.isAssignableFrom(paramClass))
/* 107 */       return TypeInfo.TYPE_NUMERIC_FLOATING_POINT; 
/* 108 */     if (ValueToObjectConverter.GEOMETRY_CLASS != null && ValueToObjectConverter.GEOMETRY_CLASS.isAssignableFrom(paramClass))
/* 109 */       return TypeInfo.TYPE_GEOMETRY; 
/* 110 */     if (Array.class.isAssignableFrom(paramClass))
/* 111 */       return TypeInfo.TYPE_ARRAY_UNKNOWN; 
/* 112 */     if (ResultSet.class.isAssignableFrom(paramClass)) {
/* 113 */       return TypeInfo.TYPE_ROW_EMPTY;
/*     */     }
/* 115 */     TypeInfo typeInfo = LegacyDateTimeUtils.legacyClassToType(paramClass);
/* 116 */     if (typeInfo != null) {
/* 117 */       return typeInfo;
/*     */     }
/* 119 */     return TypeInfo.TYPE_JAVA_OBJECT;
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
/*     */   public static Value readValue(Session paramSession, ResultSet paramResultSet, int paramInt1, int paramInt2) {
/*     */     Value value;
/* 138 */     if (paramResultSet instanceof JdbcResultSet) {
/* 139 */       value = ValueToObjectConverter.readValue(paramSession, (JdbcResultSet)paramResultSet, paramInt1);
/*     */     } else {
/*     */       try {
/* 142 */         value = readValueOther(paramSession, paramResultSet, paramInt1, paramInt2);
/* 143 */       } catch (SQLException sQLException) {
/* 144 */         throw DbException.convert(sQLException);
/*     */       } 
/*     */     } 
/* 147 */     return value; } private static Value readValueOther(Session paramSession, ResultSet paramResultSet, int paramInt1, int paramInt2) throws SQLException { ValueNull valueNull; Value value; String str2; byte[] arrayOfByte2; boolean bool; byte b; short s; int j; long l; BigDecimal bigDecimal2; float f; double d;
/*     */     BigDecimal bigDecimal1;
/*     */     String str1;
/*     */     byte[] arrayOfByte1;
/*     */     int i;
/*     */     Object object;
/* 153 */     switch (paramInt2) {
/*     */       case 0:
/* 155 */         valueNull = ValueNull.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 426 */         return valueNull;case 1: str2 = paramResultSet.getString(paramInt1); valueNull = (str2 == null) ? ValueNull.INSTANCE : (ValueNull)ValueChar.get(str2); return valueNull;case 2: str2 = paramResultSet.getString(paramInt1); value = (str2 == null) ? ValueNull.INSTANCE : ValueVarchar.get(str2, (CastDataProvider)paramSession); return value;case 3: if (paramSession == null) { str2 = paramResultSet.getString(paramInt1); value = (str2 == null) ? ValueNull.INSTANCE : ValueClob.createSmall(str2); } else { Reader reader = paramResultSet.getCharacterStream(paramInt1); value = (reader == null) ? ValueNull.INSTANCE : paramSession.addTemporaryLob(paramSession.getDataHandler().getLobStorage().createClob(new BufferedReader(reader), -1L)); }  return value;case 4: str2 = paramResultSet.getString(paramInt1); value = (str2 == null) ? ValueNull.INSTANCE : ValueVarcharIgnoreCase.get(str2); return value;case 5: arrayOfByte2 = paramResultSet.getBytes(paramInt1); value = (arrayOfByte2 == null) ? ValueNull.INSTANCE : ValueBinary.getNoCopy(arrayOfByte2); return value;case 6: arrayOfByte2 = paramResultSet.getBytes(paramInt1); value = (arrayOfByte2 == null) ? ValueNull.INSTANCE : ValueVarbinary.getNoCopy(arrayOfByte2); return value;case 7: if (paramSession == null) { arrayOfByte2 = paramResultSet.getBytes(paramInt1); value = (arrayOfByte2 == null) ? ValueNull.INSTANCE : ValueBlob.createSmall(arrayOfByte2); } else { InputStream inputStream = paramResultSet.getBinaryStream(paramInt1); value = (inputStream == null) ? ValueNull.INSTANCE : paramSession.addTemporaryLob(paramSession.getDataHandler().getLobStorage().createBlob(inputStream, -1L)); }  return value;case 8: bool = paramResultSet.getBoolean(paramInt1); value = paramResultSet.wasNull() ? ValueNull.INSTANCE : ValueBoolean.get(bool); return value;case 9: b = paramResultSet.getByte(paramInt1); value = paramResultSet.wasNull() ? ValueNull.INSTANCE : ValueTinyint.get(b); return value;case 10: s = paramResultSet.getShort(paramInt1); value = paramResultSet.wasNull() ? ValueNull.INSTANCE : ValueSmallint.get(s); return value;case 11: j = paramResultSet.getInt(paramInt1); value = paramResultSet.wasNull() ? ValueNull.INSTANCE : ValueInteger.get(j); return value;case 12: l = paramResultSet.getLong(paramInt1); value = paramResultSet.wasNull() ? ValueNull.INSTANCE : ValueBigint.get(l); return value;case 13: bigDecimal2 = paramResultSet.getBigDecimal(paramInt1); value = (bigDecimal2 == null) ? ValueNull.INSTANCE : ValueNumeric.getAnyScale(bigDecimal2); return value;case 14: f = paramResultSet.getFloat(paramInt1); value = paramResultSet.wasNull() ? ValueNull.INSTANCE : ValueReal.get(f); return value;case 15: d = paramResultSet.getDouble(paramInt1); value = paramResultSet.wasNull() ? ValueNull.INSTANCE : ValueDouble.get(d); return value;case 16: bigDecimal1 = paramResultSet.getBigDecimal(paramInt1); value = (bigDecimal1 == null) ? ValueNull.INSTANCE : ValueDecfloat.get(bigDecimal1); return value;case 17: try { LocalDate localDate = paramResultSet.<LocalDate>getObject(paramInt1, LocalDate.class); value = (localDate == null) ? ValueNull.INSTANCE : JSR310Utils.localDateToValue(localDate); } catch (SQLException sQLException) { Date date = paramResultSet.getDate(paramInt1); value = (date == null) ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromDate((CastDataProvider)paramSession, null, date); }  return value;case 18: try { LocalTime localTime = paramResultSet.<LocalTime>getObject(paramInt1, LocalTime.class); value = (localTime == null) ? ValueNull.INSTANCE : JSR310Utils.localTimeToValue(localTime); } catch (SQLException sQLException) { Time time = paramResultSet.getTime(paramInt1); value = (time == null) ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTime((CastDataProvider)paramSession, null, time); }  return value;case 19: try { OffsetTime offsetTime = paramResultSet.<OffsetTime>getObject(paramInt1, OffsetTime.class); value = (offsetTime == null) ? ValueNull.INSTANCE : JSR310Utils.offsetTimeToValue(offsetTime); } catch (SQLException sQLException) { Object object1 = paramResultSet.getObject(paramInt1); if (object1 == null) { value = ValueNull.INSTANCE; } else { value = ValueTimeTimeZone.parse(object1.toString()); }  }  return value;case 20: try { LocalDateTime localDateTime = paramResultSet.<LocalDateTime>getObject(paramInt1, LocalDateTime.class); value = (localDateTime == null) ? ValueNull.INSTANCE : JSR310Utils.localDateTimeToValue(localDateTime); } catch (SQLException sQLException) { Timestamp timestamp = paramResultSet.getTimestamp(paramInt1); value = (timestamp == null) ? ValueNull.INSTANCE : LegacyDateTimeUtils.fromTimestamp((CastDataProvider)paramSession, null, timestamp); }  return value;case 21: try { OffsetDateTime offsetDateTime = paramResultSet.<OffsetDateTime>getObject(paramInt1, OffsetDateTime.class); value = (offsetDateTime == null) ? ValueNull.INSTANCE : JSR310Utils.offsetDateTimeToValue(offsetDateTime); } catch (SQLException sQLException) { Object object1 = paramResultSet.getObject(paramInt1); if (object1 == null) { value = ValueNull.INSTANCE; } else if (object1 instanceof ZonedDateTime) { value = JSR310Utils.zonedDateTimeToValue((ZonedDateTime)object1); } else { value = ValueTimestampTimeZone.parse(object1.toString(), (CastDataProvider)paramSession); }  }  return value;case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29: case 30: case 31: case 32: case 33: case 34: str1 = paramResultSet.getString(paramInt1); value = (str1 == null) ? ValueNull.INSTANCE : IntervalUtils.parseFormattedInterval(IntervalQualifier.valueOf(paramInt2 - 22), str1); return value;case 35: try { arrayOfByte1 = paramResultSet.getBytes(paramInt1); } catch (SQLException sQLException) { try { Object object1 = paramResultSet.getObject(paramInt1); arrayOfByte1 = (object1 != null) ? JdbcUtils.serialize(object1, paramSession.getJavaObjectSerializer()) : null; } catch (Exception exception) { throw DbException.convert(exception); }  }  value = (arrayOfByte1 == null) ? ValueNull.INSTANCE : ValueJavaObject.getNoCopy(arrayOfByte1); return value;case 36: i = paramResultSet.getInt(paramInt1); value = paramResultSet.wasNull() ? ValueNull.INSTANCE : ValueInteger.get(i); return value;case 37: object = paramResultSet.getObject(paramInt1); value = (object == null) ? ValueNull.INSTANCE : ValueGeometry.getFromGeometry(object); return value;case 38: object = paramResultSet.getObject(paramInt1); if (object == null) { value = ValueNull.INSTANCE; } else { Class<?> clazz = object.getClass(); if (clazz == byte[].class) { value = ValueJson.fromJson((byte[])object); } else if (clazz == String.class) { value = ValueJson.fromJson((String)object); } else { value = ValueJson.fromJson(object.toString()); }  }  return value;case 39: object = paramResultSet.getObject(paramInt1); if (object == null) { value = ValueNull.INSTANCE; } else if (object instanceof UUID) { value = ValueUuid.get((UUID)object); } else if (object instanceof byte[]) { value = ValueUuid.get((byte[])object); } else { value = ValueUuid.get((String)object); }  return value;case 40: object = paramResultSet.getArray(paramInt1); if (object == null) { value = ValueNull.INSTANCE; } else { Object[] arrayOfObject = (Object[])object.getArray(); if (arrayOfObject == null) { value = ValueNull.INSTANCE; } else { int k = arrayOfObject.length; Value[] arrayOfValue = new Value[k]; for (byte b1 = 0; b1 < k; b1++) arrayOfValue[b1] = ValueToObjectConverter.objectToValue(paramSession, arrayOfObject[b1], 0);  value = ValueArray.get(arrayOfValue, (CastDataProvider)paramSession); }  }  return value;case 41: object = paramResultSet.getObject(paramInt1); if (object == null) { value = ValueNull.INSTANCE; } else if (object instanceof ResultSet) { value = ValueToObjectConverter.resultSetToValue(paramSession, (ResultSet)object); } else { Object[] arrayOfObject = (Object[])object; int k = arrayOfObject.length; Value[] arrayOfValue = new Value[k]; for (byte b1 = 0; b1 < k; b1++) arrayOfValue[b1] = ValueToObjectConverter.objectToValue(paramSession, arrayOfObject[b1], 0);  value = ValueRow.get(arrayOfValue); }  return value;
/*     */     } 
/*     */     throw DbException.getInternalError("data type " + paramInt2); }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueToObjectConverter2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */