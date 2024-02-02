/*     */ package org.h2.value;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Array;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
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
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import org.h2.api.Interval;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.jdbc.JdbcArray;
/*     */ import org.h2.jdbc.JdbcBlob;
/*     */ import org.h2.jdbc.JdbcClob;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.jdbc.JdbcLob;
/*     */ import org.h2.jdbc.JdbcResultSet;
/*     */ import org.h2.jdbc.JdbcSQLXML;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.TraceObject;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.util.JSR310Utils;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.LegacyDateTimeUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValueToObjectConverter
/*     */   extends TraceObject
/*     */ {
/*     */   public static final Class<?> GEOMETRY_CLASS;
/*     */   private static final String GEOMETRY_CLASS_NAME = "org.locationtech.jts.geom.Geometry";
/*     */   
/*     */   static {
/*     */     Class clazz;
/*     */     try {
/*  66 */       clazz = JdbcUtils.loadUserClass("org.locationtech.jts.geom.Geometry");
/*  67 */     } catch (Exception exception) {
/*  68 */       clazz = null;
/*     */     } 
/*  70 */     GEOMETRY_CLASS = clazz;
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
/*     */   public static Value objectToValue(Session paramSession, Object paramObject, int paramInt) {
/*  85 */     if (paramObject == null)
/*  86 */       return ValueNull.INSTANCE; 
/*  87 */     if (paramInt == 35)
/*  88 */       return ValueJavaObject.getNoCopy(JdbcUtils.serialize(paramObject, paramSession.getJavaObjectSerializer())); 
/*  89 */     if (paramObject instanceof Value) {
/*  90 */       Value value = (Value)paramObject;
/*  91 */       if (value instanceof ValueLob) {
/*  92 */         paramSession.addTemporaryLob((ValueLob)value);
/*     */       }
/*  94 */       return value;
/*     */     } 
/*  96 */     Class<?> clazz = paramObject.getClass();
/*  97 */     if (clazz == String.class)
/*  98 */       return ValueVarchar.get((String)paramObject, (CastDataProvider)paramSession); 
/*  99 */     if (clazz == Long.class)
/* 100 */       return ValueBigint.get(((Long)paramObject).longValue()); 
/* 101 */     if (clazz == Integer.class)
/* 102 */       return ValueInteger.get(((Integer)paramObject).intValue()); 
/* 103 */     if (clazz == Boolean.class)
/* 104 */       return ValueBoolean.get(((Boolean)paramObject).booleanValue()); 
/* 105 */     if (clazz == Byte.class)
/* 106 */       return ValueTinyint.get(((Byte)paramObject).byteValue()); 
/* 107 */     if (clazz == Short.class)
/* 108 */       return ValueSmallint.get(((Short)paramObject).shortValue()); 
/* 109 */     if (clazz == Float.class)
/* 110 */       return ValueReal.get(((Float)paramObject).floatValue()); 
/* 111 */     if (clazz == Double.class)
/* 112 */       return ValueDouble.get(((Double)paramObject).doubleValue()); 
/* 113 */     if (clazz == byte[].class)
/* 114 */       return ValueVarbinary.get((byte[])paramObject); 
/* 115 */     if (clazz == UUID.class)
/* 116 */       return ValueUuid.get((UUID)paramObject); 
/* 117 */     if (clazz == Character.class)
/* 118 */       return ValueChar.get(((Character)paramObject).toString()); 
/* 119 */     if (clazz == LocalDate.class)
/* 120 */       return JSR310Utils.localDateToValue((LocalDate)paramObject); 
/* 121 */     if (clazz == LocalTime.class)
/* 122 */       return JSR310Utils.localTimeToValue((LocalTime)paramObject); 
/* 123 */     if (clazz == LocalDateTime.class)
/* 124 */       return JSR310Utils.localDateTimeToValue((LocalDateTime)paramObject); 
/* 125 */     if (clazz == Instant.class)
/* 126 */       return JSR310Utils.instantToValue((Instant)paramObject); 
/* 127 */     if (clazz == OffsetTime.class)
/* 128 */       return JSR310Utils.offsetTimeToValue((OffsetTime)paramObject); 
/* 129 */     if (clazz == OffsetDateTime.class)
/* 130 */       return JSR310Utils.offsetDateTimeToValue((OffsetDateTime)paramObject); 
/* 131 */     if (clazz == ZonedDateTime.class)
/* 132 */       return JSR310Utils.zonedDateTimeToValue((ZonedDateTime)paramObject); 
/* 133 */     if (clazz == Interval.class) {
/* 134 */       Interval interval = (Interval)paramObject;
/* 135 */       return ValueInterval.from(interval.getQualifier(), interval.isNegative(), interval.getLeading(), interval.getRemaining());
/* 136 */     }  if (clazz == Period.class)
/* 137 */       return JSR310Utils.periodToValue((Period)paramObject); 
/* 138 */     if (clazz == Duration.class) {
/* 139 */       return JSR310Utils.durationToValue((Duration)paramObject);
/*     */     }
/* 141 */     if (paramObject instanceof Object[])
/* 142 */       return arrayToValue(paramSession, paramObject); 
/* 143 */     if (GEOMETRY_CLASS != null && GEOMETRY_CLASS.isAssignableFrom(clazz))
/* 144 */       return ValueGeometry.getFromGeometry(paramObject); 
/* 145 */     if (paramObject instanceof BigInteger)
/* 146 */       return ValueNumeric.get((BigInteger)paramObject); 
/* 147 */     if (paramObject instanceof BigDecimal) {
/* 148 */       return ValueNumeric.getAnyScale((BigDecimal)paramObject);
/*     */     }
/* 150 */     return otherToValue(paramSession, paramObject);
/*     */   }
/*     */   
/*     */   private static Value otherToValue(Session paramSession, Object paramObject) {
/*     */     ValueClob valueClob;
/* 155 */     if (paramObject instanceof Array) {
/* 156 */       Array array = (Array)paramObject;
/*     */       try {
/* 158 */         return arrayToValue(paramSession, array.getArray());
/* 159 */       } catch (SQLException sQLException) {
/* 160 */         throw DbException.convert(sQLException);
/*     */       } 
/* 162 */     }  if (paramObject instanceof ResultSet) {
/* 163 */       return resultSetToValue(paramSession, (ResultSet)paramObject);
/*     */     }
/*     */     
/* 166 */     if (paramObject instanceof Reader) {
/* 167 */       Reader reader = (Reader)paramObject;
/* 168 */       if (!(reader instanceof BufferedReader)) {
/* 169 */         reader = new BufferedReader(reader);
/*     */       }
/* 171 */       valueClob = paramSession.getDataHandler().getLobStorage().createClob(reader, -1L);
/* 172 */     } else if (paramObject instanceof Clob) {
/*     */       try {
/* 174 */         Clob clob = (Clob)paramObject;
/* 175 */         BufferedReader bufferedReader = new BufferedReader(clob.getCharacterStream());
/* 176 */         valueClob = paramSession.getDataHandler().getLobStorage().createClob(bufferedReader, clob.length());
/* 177 */       } catch (SQLException sQLException) {
/* 178 */         throw DbException.convert(sQLException);
/*     */       } 
/* 180 */     } else if (paramObject instanceof InputStream) {
/* 181 */       ValueBlob valueBlob = paramSession.getDataHandler().getLobStorage().createBlob((InputStream)paramObject, -1L);
/* 182 */     } else if (paramObject instanceof Blob) {
/*     */       try {
/* 184 */         Blob blob = (Blob)paramObject;
/* 185 */         ValueBlob valueBlob = paramSession.getDataHandler().getLobStorage().createBlob(blob.getBinaryStream(), blob.length());
/* 186 */       } catch (SQLException sQLException) {
/* 187 */         throw DbException.convert(sQLException);
/*     */       } 
/* 189 */     } else if (paramObject instanceof SQLXML) {
/*     */       
/*     */       try {
/* 192 */         valueClob = paramSession.getDataHandler().getLobStorage().createClob(new BufferedReader(((SQLXML)paramObject).getCharacterStream()), -1L);
/* 193 */       } catch (SQLException sQLException) {
/* 194 */         throw DbException.convert(sQLException);
/*     */       } 
/*     */     } else {
/* 197 */       Value value = LegacyDateTimeUtils.legacyObjectToValue((CastDataProvider)paramSession, paramObject);
/* 198 */       if (value != null) {
/* 199 */         return value;
/*     */       }
/* 201 */       return ValueJavaObject.getNoCopy(JdbcUtils.serialize(paramObject, paramSession.getJavaObjectSerializer()));
/*     */     } 
/* 203 */     return paramSession.addTemporaryLob(valueClob);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Value arrayToValue(Session paramSession, Object paramObject) {
/* 209 */     Object[] arrayOfObject = (Object[])paramObject;
/* 210 */     int i = arrayOfObject.length;
/* 211 */     Value[] arrayOfValue = new Value[i];
/* 212 */     for (byte b = 0; b < i; b++) {
/* 213 */       arrayOfValue[b] = objectToValue(paramSession, arrayOfObject[b], -1);
/*     */     }
/* 215 */     return ValueArray.get(arrayOfValue, (CastDataProvider)paramSession);
/*     */   }
/*     */   
/*     */   static Value resultSetToValue(Session paramSession, ResultSet paramResultSet) {
/*     */     try {
/* 220 */       ResultSetMetaData resultSetMetaData = paramResultSet.getMetaData();
/* 221 */       int i = resultSetMetaData.getColumnCount();
/* 222 */       LinkedHashMap<String, TypeInfo> linkedHashMap = readResultSetMeta(paramSession, resultSetMetaData, i);
/* 223 */       if (!paramResultSet.next()) {
/* 224 */         throw DbException.get(22018, "Empty ResultSet to ROW value");
/*     */       }
/* 226 */       Value[] arrayOfValue = new Value[i];
/* 227 */       Iterator<Map.Entry> iterator = linkedHashMap.entrySet().iterator();
/* 228 */       for (byte b = 0; b < i; b++) {
/* 229 */         arrayOfValue[b] = objectToValue(paramSession, paramResultSet.getObject(b + 1), ((TypeInfo)((Map.Entry)iterator
/* 230 */             .next()).getValue()).getValueType());
/*     */       }
/* 232 */       if (paramResultSet.next()) {
/* 233 */         throw DbException.get(22018, "Multi-row ResultSet to ROW value");
/*     */       }
/* 235 */       return ValueRow.get(new ExtTypeInfoRow(linkedHashMap), arrayOfValue);
/* 236 */     } catch (SQLException sQLException) {
/* 237 */       throw DbException.convert(sQLException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static LinkedHashMap<String, TypeInfo> readResultSetMeta(Session paramSession, ResultSetMetaData paramResultSetMetaData, int paramInt) throws SQLException {
/* 243 */     LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
/* 244 */     for (byte b = 0; b < paramInt; b++) {
/* 245 */       TypeInfo typeInfo; String str1 = paramResultSetMetaData.getColumnLabel(b + 1);
/* 246 */       String str2 = paramResultSetMetaData.getColumnTypeName(b + 1);
/* 247 */       int i = DataType.convertSQLTypeToValueType(paramResultSetMetaData.getColumnType(b + 1), str2);
/* 248 */       int j = paramResultSetMetaData.getPrecision(b + 1);
/* 249 */       int k = paramResultSetMetaData.getScale(b + 1);
/*     */       
/* 251 */       if (i == 40 && str2.endsWith(" ARRAY")) {
/*     */         
/* 253 */         typeInfo = TypeInfo.getTypeInfo(40, -1L, 0, 
/* 254 */             TypeInfo.getTypeInfo((DataType.getTypeByName(str2
/* 255 */                 .substring(0, str2.length() - 6), paramSession
/* 256 */                 .getMode())).type));
/*     */       } else {
/* 258 */         typeInfo = TypeInfo.getTypeInfo(i, j, k, null);
/*     */       } 
/* 260 */       linkedHashMap.put(str1, typeInfo);
/*     */     } 
/* 262 */     return (LinkedHashMap)linkedHashMap;
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
/*     */   public static <T> T valueToObject(Class<T> paramClass, Value paramValue, JdbcConnection paramJdbcConnection) {
/* 281 */     if (paramValue == ValueNull.INSTANCE)
/* 282 */       return null; 
/* 283 */     if (paramClass == BigDecimal.class)
/* 284 */       return (T)paramValue.getBigDecimal(); 
/* 285 */     if (paramClass == BigInteger.class)
/* 286 */       return (T)paramValue.getBigDecimal().toBigInteger(); 
/* 287 */     if (paramClass == String.class)
/* 288 */       return (T)paramValue.getString(); 
/* 289 */     if (paramClass == Boolean.class)
/* 290 */       return (T)Boolean.valueOf(paramValue.getBoolean()); 
/* 291 */     if (paramClass == Byte.class)
/* 292 */       return (T)Byte.valueOf(paramValue.getByte()); 
/* 293 */     if (paramClass == Short.class)
/* 294 */       return (T)Short.valueOf(paramValue.getShort()); 
/* 295 */     if (paramClass == Integer.class)
/* 296 */       return (T)Integer.valueOf(paramValue.getInt()); 
/* 297 */     if (paramClass == Long.class)
/* 298 */       return (T)Long.valueOf(paramValue.getLong()); 
/* 299 */     if (paramClass == Float.class)
/* 300 */       return (T)Float.valueOf(paramValue.getFloat()); 
/* 301 */     if (paramClass == Double.class)
/* 302 */       return (T)Double.valueOf(paramValue.getDouble()); 
/* 303 */     if (paramClass == UUID.class)
/* 304 */       return (T)paramValue.convertToUuid().getUuid(); 
/* 305 */     if (paramClass == byte[].class)
/* 306 */       return (T)paramValue.getBytes(); 
/* 307 */     if (paramClass == Character.class) {
/* 308 */       String str = paramValue.getString();
/* 309 */       return (T)Character.valueOf(str.isEmpty() ? 32 : str.charAt(0));
/* 310 */     }  if (paramClass == Interval.class) {
/* 311 */       if (!(paramValue instanceof ValueInterval)) {
/* 312 */         paramValue = paramValue.convertTo(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND);
/*     */       }
/* 314 */       ValueInterval valueInterval = (ValueInterval)paramValue;
/* 315 */       return (T)new Interval(valueInterval.getQualifier(), false, valueInterval.getLeading(), valueInterval.getRemaining());
/* 316 */     }  if (paramClass == LocalDate.class)
/* 317 */       return (T)JSR310Utils.valueToLocalDate(paramValue, (CastDataProvider)paramJdbcConnection); 
/* 318 */     if (paramClass == LocalTime.class)
/* 319 */       return (T)JSR310Utils.valueToLocalTime(paramValue, (CastDataProvider)paramJdbcConnection); 
/* 320 */     if (paramClass == LocalDateTime.class)
/* 321 */       return (T)JSR310Utils.valueToLocalDateTime(paramValue, (CastDataProvider)paramJdbcConnection); 
/* 322 */     if (paramClass == OffsetTime.class)
/* 323 */       return (T)JSR310Utils.valueToOffsetTime(paramValue, (CastDataProvider)paramJdbcConnection); 
/* 324 */     if (paramClass == OffsetDateTime.class)
/* 325 */       return (T)JSR310Utils.valueToOffsetDateTime(paramValue, (CastDataProvider)paramJdbcConnection); 
/* 326 */     if (paramClass == ZonedDateTime.class)
/* 327 */       return (T)JSR310Utils.valueToZonedDateTime(paramValue, (CastDataProvider)paramJdbcConnection); 
/* 328 */     if (paramClass == Instant.class)
/* 329 */       return (T)JSR310Utils.valueToInstant(paramValue, (CastDataProvider)paramJdbcConnection); 
/* 330 */     if (paramClass == Period.class)
/* 331 */       return (T)JSR310Utils.valueToPeriod(paramValue); 
/* 332 */     if (paramClass == Duration.class)
/* 333 */       return (T)JSR310Utils.valueToDuration(paramValue); 
/* 334 */     if (paramClass.isArray())
/* 335 */       return (T)valueToArray(paramClass, paramValue, paramJdbcConnection); 
/* 336 */     if (GEOMETRY_CLASS != null && GEOMETRY_CLASS.isAssignableFrom(paramClass)) {
/* 337 */       return (T)paramValue.convertToGeometry(null).getGeometry();
/*     */     }
/* 339 */     return (T)valueToOther(paramClass, paramValue, paramJdbcConnection);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Object valueToArray(Class<?> paramClass, Value paramValue, JdbcConnection paramJdbcConnection) {
/* 344 */     Value[] arrayOfValue = ((ValueArray)paramValue).getList();
/* 345 */     Class<?> clazz = paramClass.getComponentType();
/* 346 */     int i = arrayOfValue.length;
/* 347 */     Object[] arrayOfObject = (Object[])Array.newInstance(clazz, i);
/* 348 */     for (byte b = 0; b < i; b++) {
/* 349 */       arrayOfObject[b] = valueToObject(clazz, arrayOfValue[b], paramJdbcConnection);
/*     */     }
/* 351 */     return arrayOfObject;
/*     */   }
/*     */   
/*     */   private static Object valueToOther(Class<?> paramClass, Value paramValue, JdbcConnection paramJdbcConnection) {
/* 355 */     if (paramClass == Object.class)
/* 356 */       return JdbcUtils.deserialize(paramValue
/* 357 */           .convertToJavaObject(TypeInfo.TYPE_JAVA_OBJECT, 0, null).getBytesNoCopy(), paramJdbcConnection
/* 358 */           .getJavaObjectSerializer()); 
/* 359 */     if (paramClass == InputStream.class)
/* 360 */       return paramValue.getInputStream(); 
/* 361 */     if (paramClass == Reader.class)
/* 362 */       return paramValue.getReader(); 
/* 363 */     if (paramClass == Array.class)
/* 364 */       return new JdbcArray(paramJdbcConnection, paramValue, getNextId(16)); 
/* 365 */     if (paramClass == Blob.class)
/* 366 */       return new JdbcBlob(paramJdbcConnection, paramValue, JdbcLob.State.WITH_VALUE, getNextId(9)); 
/* 367 */     if (paramClass == Clob.class)
/* 368 */       return new JdbcClob(paramJdbcConnection, paramValue, JdbcLob.State.WITH_VALUE, getNextId(10)); 
/* 369 */     if (paramClass == SQLXML.class)
/* 370 */       return new JdbcSQLXML(paramJdbcConnection, paramValue, JdbcLob.State.WITH_VALUE, getNextId(17)); 
/* 371 */     if (paramClass == ResultSet.class) {
/* 372 */       return new JdbcResultSet(paramJdbcConnection, null, null, (ResultInterface)paramValue.convertToAnyRow().getResult(), 
/* 373 */           getNextId(4), true, false, false);
/*     */     }
/* 375 */     Object object = LegacyDateTimeUtils.valueToLegacyType(paramClass, paramValue, (CastDataProvider)paramJdbcConnection);
/* 376 */     if (object != null) {
/* 377 */       return object;
/*     */     }
/* 379 */     if (paramValue.getValueType() == 35) {
/* 380 */       object = JdbcUtils.deserialize(paramValue.getBytesNoCopy(), paramJdbcConnection.getJavaObjectSerializer());
/* 381 */       if (paramClass.isAssignableFrom(object.getClass())) {
/* 382 */         return object;
/*     */       }
/*     */     } 
/* 385 */     throw DbException.getUnsupportedException("converting to class " + paramClass.getName());
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
/*     */   public static Class<?> getDefaultClass(int paramInt, boolean paramBoolean) {
/*     */     Class<?> clazz;
/* 400 */     switch (paramInt) {
/*     */       case 0:
/* 402 */         return Void.class;
/*     */       case 1:
/*     */       case 2:
/*     */       case 4:
/*     */       case 36:
/* 407 */         return String.class;
/*     */       case 3:
/* 409 */         return Clob.class;
/*     */       case 5:
/*     */       case 6:
/*     */       case 38:
/* 413 */         return byte[].class;
/*     */       case 7:
/* 415 */         return Blob.class;
/*     */       case 8:
/* 417 */         return Boolean.class;
/*     */       case 9:
/* 419 */         if (paramBoolean) {
/* 420 */           return Integer.class;
/*     */         }
/* 422 */         return Byte.class;
/*     */       case 10:
/* 424 */         if (paramBoolean) {
/* 425 */           return Integer.class;
/*     */         }
/* 427 */         return Short.class;
/*     */       case 11:
/* 429 */         return Integer.class;
/*     */       case 12:
/* 431 */         return Long.class;
/*     */       case 13:
/*     */       case 16:
/* 434 */         return BigDecimal.class;
/*     */       case 14:
/* 436 */         return Float.class;
/*     */       case 15:
/* 438 */         return Double.class;
/*     */       case 17:
/* 440 */         return paramBoolean ? Date.class : LocalDate.class;
/*     */       case 18:
/* 442 */         return paramBoolean ? Time.class : LocalTime.class;
/*     */       case 19:
/* 444 */         return OffsetTime.class;
/*     */       case 20:
/* 446 */         return paramBoolean ? Timestamp.class : LocalDateTime.class;
/*     */       case 21:
/* 448 */         return OffsetDateTime.class;
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 25:
/*     */       case 26:
/*     */       case 27:
/*     */       case 28:
/*     */       case 29:
/*     */       case 30:
/*     */       case 31:
/*     */       case 32:
/*     */       case 33:
/*     */       case 34:
/* 462 */         return Interval.class;
/*     */       case 35:
/* 464 */         return paramBoolean ? Object.class : byte[].class;
/*     */       case 37:
/* 466 */         clazz = GEOMETRY_CLASS;
/* 467 */         return (clazz != null) ? clazz : String.class;
/*     */       
/*     */       case 39:
/* 470 */         return UUID.class;
/*     */       case 40:
/* 472 */         if (paramBoolean) {
/* 473 */           return Array.class;
/*     */         }
/* 475 */         return Object[].class;
/*     */       case 41:
/* 477 */         if (paramBoolean) {
/* 478 */           return ResultSet.class;
/*     */         }
/* 480 */         return Object[].class;
/*     */     } 
/* 482 */     throw DbException.getUnsupportedException("data type " + paramInt);
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
/*     */   public static Object valueToDefaultObject(Value paramValue, JdbcConnection paramJdbcConnection, boolean paramBoolean) {
/* 499 */     switch (paramValue.getValueType()) {
/*     */       case 0:
/* 501 */         return null;
/*     */       case 1:
/*     */       case 2:
/*     */       case 4:
/*     */       case 36:
/* 506 */         return paramValue.getString();
/*     */       case 3:
/* 508 */         return new JdbcClob(paramJdbcConnection, paramValue, JdbcLob.State.WITH_VALUE, getNextId(10));
/*     */       case 5:
/*     */       case 6:
/*     */       case 38:
/* 512 */         return paramValue.getBytes();
/*     */       case 7:
/* 514 */         return new JdbcBlob(paramJdbcConnection, paramValue, JdbcLob.State.WITH_VALUE, getNextId(9));
/*     */       case 8:
/* 516 */         return Boolean.valueOf(paramValue.getBoolean());
/*     */       case 9:
/* 518 */         if (paramBoolean) {
/* 519 */           return Integer.valueOf(paramValue.getInt());
/*     */         }
/* 521 */         return Byte.valueOf(paramValue.getByte());
/*     */       case 10:
/* 523 */         if (paramBoolean) {
/* 524 */           return Integer.valueOf(paramValue.getInt());
/*     */         }
/* 526 */         return Short.valueOf(paramValue.getShort());
/*     */       case 11:
/* 528 */         return Integer.valueOf(paramValue.getInt());
/*     */       case 12:
/* 530 */         return Long.valueOf(paramValue.getLong());
/*     */       case 13:
/*     */       case 16:
/* 533 */         return paramValue.getBigDecimal();
/*     */       case 14:
/* 535 */         return Float.valueOf(paramValue.getFloat());
/*     */       case 15:
/* 537 */         return Double.valueOf(paramValue.getDouble());
/*     */       case 17:
/* 539 */         return paramBoolean ? LegacyDateTimeUtils.toDate((CastDataProvider)paramJdbcConnection, null, paramValue) : JSR310Utils.valueToLocalDate(paramValue, null);
/*     */       case 18:
/* 541 */         return paramBoolean ? LegacyDateTimeUtils.toTime((CastDataProvider)paramJdbcConnection, null, paramValue) : JSR310Utils.valueToLocalTime(paramValue, null);
/*     */       case 19:
/* 543 */         return JSR310Utils.valueToOffsetTime(paramValue, null);
/*     */       case 20:
/* 545 */         return paramBoolean ? LegacyDateTimeUtils.toTimestamp((CastDataProvider)paramJdbcConnection, null, paramValue) : 
/* 546 */           JSR310Utils.valueToLocalDateTime(paramValue, null);
/*     */       case 21:
/* 548 */         return JSR310Utils.valueToOffsetDateTime(paramValue, null);
/*     */       case 22:
/*     */       case 23:
/*     */       case 24:
/*     */       case 25:
/*     */       case 26:
/*     */       case 27:
/*     */       case 28:
/*     */       case 29:
/*     */       case 30:
/*     */       case 31:
/*     */       case 32:
/*     */       case 33:
/*     */       case 34:
/* 562 */         return ((ValueInterval)paramValue).getInterval();
/*     */       case 35:
/* 564 */         return paramBoolean ? JdbcUtils.deserialize(paramValue.getBytesNoCopy(), paramJdbcConnection.getJavaObjectSerializer()) : paramValue
/* 565 */           .getBytes();
/*     */       case 37:
/* 567 */         return (GEOMETRY_CLASS != null) ? ((ValueGeometry)paramValue).getGeometry() : paramValue.getString();
/*     */       case 39:
/* 569 */         return ((ValueUuid)paramValue).getUuid();
/*     */       case 40:
/* 571 */         if (paramBoolean) {
/* 572 */           return new JdbcArray(paramJdbcConnection, paramValue, getNextId(16));
/*     */         }
/* 574 */         return valueToDefaultArray(paramValue, paramJdbcConnection, paramBoolean);
/*     */       case 41:
/* 576 */         if (paramBoolean) {
/* 577 */           return new JdbcResultSet(paramJdbcConnection, null, null, (ResultInterface)((ValueRow)paramValue).getResult(), 
/* 578 */               getNextId(4), true, false, false);
/*     */         }
/* 580 */         return valueToDefaultArray(paramValue, paramJdbcConnection, paramBoolean);
/*     */     } 
/* 582 */     throw DbException.getUnsupportedException("data type " + paramValue.getValueType());
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
/*     */   public static Object valueToDefaultArray(Value paramValue, JdbcConnection paramJdbcConnection, boolean paramBoolean) {
/* 600 */     Value[] arrayOfValue = ((ValueCollectionBase)paramValue).getList();
/* 601 */     int i = arrayOfValue.length;
/* 602 */     Object[] arrayOfObject = new Object[i];
/* 603 */     for (byte b = 0; b < i; b++) {
/* 604 */       arrayOfObject[b] = valueToDefaultObject(arrayOfValue[b], paramJdbcConnection, paramBoolean);
/*     */     }
/* 606 */     return arrayOfObject;
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
/*     */   public static Value readValue(Session paramSession, JdbcResultSet paramJdbcResultSet, int paramInt) {
/* 621 */     Value value = paramJdbcResultSet.getInternal(paramInt);
/* 622 */     switch (value.getValueType()) {
/*     */       case 3:
/* 624 */         value = paramSession.addTemporaryLob(paramSession
/* 625 */             .getDataHandler().getLobStorage().createClob(new BufferedReader(value.getReader()), -1L));
/*     */         break;
/*     */       
/*     */       case 7:
/* 629 */         value = paramSession.addTemporaryLob(paramSession.getDataHandler().getLobStorage().createBlob(value.getInputStream(), -1L)); break;
/*     */     } 
/* 631 */     return value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueToObjectConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */