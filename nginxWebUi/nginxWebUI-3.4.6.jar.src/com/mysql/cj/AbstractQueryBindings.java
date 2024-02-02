/*      */ package com.mysql.cj;
/*      */ 
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.conf.RuntimeProperty;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.WrongArgumentException;
/*      */ import com.mysql.cj.protocol.ColumnDefinition;
/*      */ import com.mysql.cj.protocol.a.NativeConstants;
/*      */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import com.mysql.cj.util.TimeUtil;
/*      */ import com.mysql.cj.util.Util;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.time.Duration;
/*      */ import java.time.LocalDate;
/*      */ import java.time.LocalDateTime;
/*      */ import java.time.LocalTime;
/*      */ import java.time.OffsetDateTime;
/*      */ import java.time.OffsetTime;
/*      */ import java.time.ZoneOffset;
/*      */ import java.time.ZonedDateTime;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class AbstractQueryBindings<T extends BindValue>
/*      */   implements QueryBindings<T>
/*      */ {
/*   67 */   protected static final byte[] HEX_DIGITS = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */ 
/*      */   
/*   70 */   protected static final LocalDate DEFAULT_DATE = LocalDate.of(1970, 1, 1);
/*   71 */   protected static final LocalTime DEFAULT_TIME = LocalTime.of(0, 0);
/*      */ 
/*      */   
/*      */   protected Session session;
/*      */   
/*      */   protected T[] bindValues;
/*      */   
/*      */   protected String charEncoding;
/*      */   
/*   80 */   protected int numberOfExecutions = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   protected RuntimeProperty<Boolean> useStreamLengthsInPrepStmts;
/*      */ 
/*      */ 
/*      */   
/*      */   protected RuntimeProperty<Boolean> preserveInstants;
/*      */ 
/*      */ 
/*      */   
/*      */   protected RuntimeProperty<Boolean> sendFractionalSeconds;
/*      */ 
/*      */ 
/*      */   
/*      */   protected RuntimeProperty<Boolean> sendFractionalSecondsForTime;
/*      */ 
/*      */ 
/*      */   
/*      */   private RuntimeProperty<Boolean> treatUtilDateAsTimestamp;
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean isLoadDataQuery = false;
/*      */ 
/*      */   
/*      */   protected ColumnDefinition columnDefinition;
/*      */ 
/*      */ 
/*      */   
/*      */   public void setColumnDefinition(ColumnDefinition colDef) {
/*  112 */     this.columnDefinition = colDef;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isLoadDataQuery() {
/*  117 */     return this.isLoadDataQuery;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setLoadDataQuery(boolean isLoadDataQuery) {
/*  122 */     this.isLoadDataQuery = isLoadDataQuery;
/*      */   }
/*      */ 
/*      */   
/*      */   public T[] getBindValues() {
/*  127 */     return this.bindValues;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setBindValues(T[] bindValues) {
/*  132 */     this.bindValues = bindValues;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean clearBindValues() {
/*  137 */     boolean hadLongData = false;
/*      */     
/*  139 */     if (this.bindValues != null) {
/*  140 */       for (int i = 0; i < this.bindValues.length; i++) {
/*  141 */         if (this.bindValues[i] != null && this.bindValues[i].isStream()) {
/*  142 */           hadLongData = true;
/*      */         }
/*  144 */         this.bindValues[i].reset();
/*      */       } 
/*      */     }
/*      */     
/*  148 */     return hadLongData;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void checkAllParametersSet() {
/*  154 */     for (int i = 0; i < this.bindValues.length; i++) {
/*  155 */       checkParameterSet(i);
/*      */     }
/*      */   }
/*      */   
/*      */   public int getNumberOfExecutions() {
/*  160 */     return this.numberOfExecutions;
/*      */   }
/*      */   
/*      */   public void setNumberOfExecutions(int numberOfExecutions) {
/*  164 */     this.numberOfExecutions = numberOfExecutions;
/*      */   }
/*      */   
/*      */   public final synchronized void setValue(int paramIndex, byte[] val, MysqlType type) {
/*  168 */     this.bindValues[paramIndex].setByteValue(val);
/*  169 */     this.bindValues[paramIndex].setMysqlType(type);
/*      */   }
/*      */   
/*      */   public final synchronized void setOrigValue(int paramIndex, byte[] val) {
/*  173 */     this.bindValues[paramIndex].setOrigByteValue(val);
/*      */   }
/*      */ 
/*      */   
/*      */   public synchronized byte[] getOrigBytes(int parameterIndex) {
/*  178 */     return this.bindValues[parameterIndex].getOrigByteValue();
/*      */   }
/*      */   
/*      */   public final synchronized void setValue(int paramIndex, String val, MysqlType type) {
/*  182 */     byte[] parameterAsBytes = StringUtils.getBytes(val, this.charEncoding);
/*  183 */     setValue(paramIndex, parameterAsBytes, type);
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
/*      */   public final void hexEscapeBlock(byte[] buf, NativePacketPayload packet, int size) {
/*  197 */     for (int i = 0; i < size; i++) {
/*  198 */       byte b = buf[i];
/*  199 */       int lowBits = (b & 0xFF) / 16;
/*  200 */       int highBits = (b & 0xFF) % 16;
/*      */       
/*  202 */       packet.writeInteger(NativeConstants.IntegerDataType.INT1, HEX_DIGITS[lowBits]);
/*  203 */       packet.writeInteger(NativeConstants.IntegerDataType.INT1, HEX_DIGITS[highBits]);
/*      */     } 
/*      */   }
/*      */   
/*  207 */   static Map<Class<?>, MysqlType> DEFAULT_MYSQL_TYPES = new HashMap<>(); private byte[] streamConvertBuf;
/*      */   static {
/*  209 */     DEFAULT_MYSQL_TYPES.put(String.class, MysqlType.VARCHAR);
/*  210 */     DEFAULT_MYSQL_TYPES.put(Date.class, MysqlType.DATE);
/*  211 */     DEFAULT_MYSQL_TYPES.put(Time.class, MysqlType.TIME);
/*  212 */     DEFAULT_MYSQL_TYPES.put(Timestamp.class, MysqlType.TIMESTAMP);
/*  213 */     DEFAULT_MYSQL_TYPES.put(Byte.class, MysqlType.INT);
/*  214 */     DEFAULT_MYSQL_TYPES.put(BigDecimal.class, MysqlType.DECIMAL);
/*  215 */     DEFAULT_MYSQL_TYPES.put(Short.class, MysqlType.SMALLINT);
/*  216 */     DEFAULT_MYSQL_TYPES.put(Integer.class, MysqlType.INT);
/*  217 */     DEFAULT_MYSQL_TYPES.put(Long.class, MysqlType.BIGINT);
/*  218 */     DEFAULT_MYSQL_TYPES.put(Float.class, MysqlType.FLOAT);
/*  219 */     DEFAULT_MYSQL_TYPES.put(Double.class, MysqlType.DOUBLE);
/*  220 */     DEFAULT_MYSQL_TYPES.put(byte[].class, MysqlType.BINARY);
/*  221 */     DEFAULT_MYSQL_TYPES.put(Boolean.class, MysqlType.BOOLEAN);
/*  222 */     DEFAULT_MYSQL_TYPES.put(LocalDate.class, MysqlType.DATE);
/*  223 */     DEFAULT_MYSQL_TYPES.put(LocalTime.class, MysqlType.TIME);
/*  224 */     DEFAULT_MYSQL_TYPES.put(LocalDateTime.class, MysqlType.DATETIME);
/*  225 */     DEFAULT_MYSQL_TYPES.put(OffsetTime.class, MysqlType.TIME);
/*  226 */     DEFAULT_MYSQL_TYPES.put(OffsetDateTime.class, MysqlType.TIMESTAMP);
/*  227 */     DEFAULT_MYSQL_TYPES.put(ZonedDateTime.class, MysqlType.TIMESTAMP);
/*  228 */     DEFAULT_MYSQL_TYPES.put(Duration.class, MysqlType.TIME);
/*  229 */     DEFAULT_MYSQL_TYPES.put(Blob.class, MysqlType.BLOB);
/*  230 */     DEFAULT_MYSQL_TYPES.put(Clob.class, MysqlType.TEXT);
/*  231 */     DEFAULT_MYSQL_TYPES.put(BigInteger.class, MysqlType.BIGINT);
/*  232 */     DEFAULT_MYSQL_TYPES.put(Date.class, MysqlType.TIMESTAMP);
/*  233 */     DEFAULT_MYSQL_TYPES.put(Calendar.class, MysqlType.TIMESTAMP);
/*  234 */     DEFAULT_MYSQL_TYPES.put(InputStream.class, MysqlType.BLOB);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, MysqlType targetMysqlType) {
/*  239 */     int fractLen = -1;
/*  240 */     if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/*  241 */       fractLen = 0;
/*  242 */     } else if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0) {
/*  243 */       fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*      */     } 
/*      */     
/*  246 */     setTimestamp(parameterIndex, x, (Calendar)null, fractLen, targetMysqlType);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal, MysqlType targetMysqlType) {
/*  251 */     int fractLen = -1;
/*  252 */     if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/*  253 */       fractLen = 0;
/*  254 */     } else if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0 && this.columnDefinition
/*  255 */       .getFields()[parameterIndex].getDecimals() > 0) {
/*  256 */       fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*      */     } 
/*      */     
/*  259 */     setTimestamp(parameterIndex, x, cal, fractLen, targetMysqlType);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar targetCalendar, int fractionalLength, MysqlType targetMysqlType) {
/*  264 */     if (x == null) {
/*  265 */       setNull(parameterIndex);
/*      */       return;
/*      */     } 
/*  268 */     if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/*  269 */       x = TimeUtil.truncateFractionalSeconds(x);
/*      */     }
/*      */     
/*  272 */     bindTimestamp(parameterIndex, x, targetCalendar, fractionalLength, targetMysqlType);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(int parameterIndex, Object parameterObj) {
/*  277 */     if (parameterObj == null) {
/*  278 */       setNull(parameterIndex);
/*      */       
/*      */       return;
/*      */     } 
/*  282 */     MysqlType defaultMysqlType = DEFAULT_MYSQL_TYPES.get(parameterObj.getClass());
/*      */     
/*  284 */     if (defaultMysqlType == null) {
/*      */       
/*  286 */       Optional<MysqlType> mysqlType = DEFAULT_MYSQL_TYPES.entrySet().stream().filter(m -> ((Class)m.getKey()).isAssignableFrom(parameterObj.getClass())).map(m -> (MysqlType)m.getValue()).findFirst();
/*  287 */       if (mysqlType.isPresent()) {
/*  288 */         defaultMysqlType = mysqlType.get();
/*      */       }
/*      */     } 
/*      */     
/*  292 */     if (defaultMysqlType != null) {
/*  293 */       setObject(parameterIndex, parameterObj, defaultMysqlType);
/*      */     } else {
/*      */       
/*  296 */       setSerializableObject(parameterIndex, parameterObj);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void setObject(int parameterIndex, Object parameterObj, MysqlType targetMysqlType) {
/*  302 */     setObject(parameterIndex, parameterObj, targetMysqlType, (parameterObj instanceof BigDecimal) ? ((BigDecimal)parameterObj).scale() : 0);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setObject(int parameterIndex, Object parameterObj, MysqlType targetMysqlType, int scaleOrLength) {
/*  323 */     if (parameterObj == null) {
/*  324 */       setNull(parameterIndex);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*      */     try {
/*  331 */       if (parameterObj instanceof LocalDate) {
/*  332 */         switch (targetMysqlType) {
/*      */           case DATE:
/*  334 */             setLocalDate(parameterIndex, (LocalDate)parameterObj, targetMysqlType);
/*      */             return;
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*  338 */             setLocalDateTime(parameterIndex, LocalDateTime.of((LocalDate)parameterObj, DEFAULT_TIME), targetMysqlType);
/*      */             return;
/*      */           case YEAR:
/*  341 */             setInt(parameterIndex, ((LocalDate)parameterObj).getYear());
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  349 */             setString(parameterIndex, parameterObj.toString());
/*      */             return;
/*      */         } 
/*  352 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  353 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  354 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  357 */       if (parameterObj instanceof LocalTime) {
/*  358 */         switch (targetMysqlType) {
/*      */           case TIME:
/*  360 */             setLocalTime(parameterIndex, (LocalTime)parameterObj, targetMysqlType);
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  368 */             setString(parameterIndex, ((LocalTime)parameterObj)
/*  369 */                 .format((((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((LocalTime)parameterObj).getNano() > 0) ? TimeUtil.TIME_FORMATTER_WITH_NANOS_NO_OFFSET : TimeUtil.TIME_FORMATTER_NO_FRACT_NO_OFFSET));
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  374 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  375 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  376 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  379 */       if (parameterObj instanceof LocalDateTime) {
/*  380 */         switch (targetMysqlType) {
/*      */           case DATE:
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*      */           case TIME:
/*  385 */             setLocalDateTime(parameterIndex, (LocalDateTime)parameterObj, targetMysqlType);
/*      */             return;
/*      */           case YEAR:
/*  388 */             setInt(parameterIndex, ((LocalDateTime)parameterObj).getYear());
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  396 */             setString(parameterIndex, ((LocalDateTime)parameterObj)
/*  397 */                 .format((((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((LocalDateTime)parameterObj).getNano() > 0) ? TimeUtil.DATETIME_FORMATTER_WITH_NANOS_NO_OFFSET : TimeUtil.DATETIME_FORMATTER_NO_FRACT_NO_OFFSET));
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  402 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  403 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  404 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  407 */       if (parameterObj instanceof OffsetTime) {
/*  408 */         switch (targetMysqlType) {
/*      */           case TIME:
/*  410 */             setLocalTime(parameterIndex, ((OffsetTime)parameterObj)
/*      */                 
/*  412 */                 .withOffsetSameInstant(
/*  413 */                   ZoneOffset.ofTotalSeconds(this.session.getServerSession().getDefaultTimeZone().getRawOffset() / 1000))
/*  414 */                 .toLocalTime(), targetMysqlType);
/*      */             return;
/*      */           
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  423 */             setString(parameterIndex, ((OffsetTime)parameterObj)
/*  424 */                 .format((((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((OffsetTime)parameterObj).getNano() > 0) ? TimeUtil.TIME_FORMATTER_WITH_NANOS_WITH_OFFSET : TimeUtil.TIME_FORMATTER_NO_FRACT_WITH_OFFSET));
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  429 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  430 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  431 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  434 */       if (parameterObj instanceof OffsetDateTime) {
/*  435 */         Timestamp ts; int fractLen; switch (targetMysqlType) {
/*      */           case DATE:
/*  437 */             setLocalDate(parameterIndex, ((OffsetDateTime)parameterObj)
/*  438 */                 .atZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId()).toLocalDate(), targetMysqlType);
/*      */             return;
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*  442 */             ts = Timestamp.valueOf(((OffsetDateTime)parameterObj)
/*  443 */                 .atZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId()).toLocalDateTime());
/*      */             
/*  445 */             fractLen = -1;
/*  446 */             if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/*  447 */               fractLen = 0;
/*  448 */             } else if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0 && this.columnDefinition
/*  449 */               .getFields()[parameterIndex].getDecimals() > 0) {
/*  450 */               fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*      */             } 
/*      */             
/*  453 */             if (fractLen == 0) {
/*  454 */               ts = TimeUtil.truncateFractionalSeconds(ts);
/*      */             }
/*      */             
/*  457 */             bindTimestamp(parameterIndex, ts, null, fractLen, targetMysqlType);
/*      */             return;
/*      */           case TIME:
/*  460 */             setLocalTime(parameterIndex, ((OffsetDateTime)parameterObj)
/*  461 */                 .atZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId()).toLocalTime(), targetMysqlType);
/*      */             return;
/*      */           case YEAR:
/*  464 */             setInt(parameterIndex, ((OffsetDateTime)parameterObj)
/*  465 */                 .atZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId()).getYear());
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  473 */             setString(parameterIndex, ((OffsetDateTime)parameterObj)
/*  474 */                 .format((((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((OffsetDateTime)parameterObj).getNano() > 0) ? TimeUtil.DATETIME_FORMATTER_WITH_NANOS_WITH_OFFSET : TimeUtil.DATETIME_FORMATTER_NO_FRACT_WITH_OFFSET));
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  479 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  480 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  481 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  484 */       if (parameterObj instanceof ZonedDateTime) {
/*  485 */         Timestamp ts; int fractLen; switch (targetMysqlType) {
/*      */           case DATE:
/*  487 */             setLocalDate(parameterIndex, ((ZonedDateTime)parameterObj)
/*  488 */                 .withZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId()).toLocalDate(), targetMysqlType);
/*      */             return;
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*  492 */             ts = Timestamp.valueOf(((ZonedDateTime)parameterObj)
/*  493 */                 .withZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId()).toLocalDateTime());
/*      */             
/*  495 */             fractLen = -1;
/*  496 */             if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/*  497 */               fractLen = 0;
/*  498 */             } else if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0 && this.columnDefinition
/*  499 */               .getFields()[parameterIndex].getDecimals() > 0) {
/*  500 */               fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*      */             } 
/*      */             
/*  503 */             if (fractLen == 0) {
/*  504 */               ts = TimeUtil.truncateFractionalSeconds(ts);
/*      */             }
/*      */             
/*  507 */             bindTimestamp(parameterIndex, ts, null, fractLen, targetMysqlType);
/*      */             return;
/*      */           case TIME:
/*  510 */             setLocalTime(parameterIndex, ((ZonedDateTime)parameterObj)
/*  511 */                 .withZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId()).toLocalTime(), targetMysqlType);
/*      */             return;
/*      */           case YEAR:
/*  514 */             setInt(parameterIndex, ((ZonedDateTime)parameterObj)
/*  515 */                 .withZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId()).getYear());
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  523 */             setString(parameterIndex, ((ZonedDateTime)parameterObj)
/*  524 */                 .format((((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((ZonedDateTime)parameterObj).getNano() > 0) ? TimeUtil.DATETIME_FORMATTER_WITH_NANOS_WITH_OFFSET : TimeUtil.DATETIME_FORMATTER_NO_FRACT_WITH_OFFSET));
/*      */             return;
/*      */         } 
/*      */ 
/*      */         
/*  529 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  530 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  531 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  534 */       if (parameterObj instanceof Duration) {
/*  535 */         switch (targetMysqlType) {
/*      */           case TIME:
/*  537 */             setDuration(parameterIndex, (Duration)parameterObj, targetMysqlType);
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  545 */             setString(parameterIndex, TimeUtil.getDurationString((Duration)parameterObj));
/*      */             return;
/*      */         } 
/*  548 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  549 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  550 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  553 */       if (parameterObj instanceof Date) {
/*  554 */         Calendar cal; switch (targetMysqlType) {
/*      */           case DATE:
/*  556 */             setDate(parameterIndex, (Date)parameterObj);
/*      */             return;
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*  560 */             setTimestamp(parameterIndex, new Timestamp(((Date)parameterObj).getTime()), targetMysqlType);
/*      */             return;
/*      */           case YEAR:
/*  563 */             cal = Calendar.getInstance();
/*  564 */             cal.setTime((Date)parameterObj);
/*  565 */             setInt(parameterIndex, cal.get(1));
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  573 */             setString(parameterIndex, parameterObj.toString());
/*      */             return;
/*      */         } 
/*  576 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  577 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  578 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  581 */       if (parameterObj instanceof Timestamp) {
/*  582 */         Calendar cal; String val; int dotPos; switch (targetMysqlType) {
/*      */           case DATE:
/*  584 */             setDate(parameterIndex, new Date(((Date)parameterObj).getTime()));
/*      */             return;
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*  588 */             setTimestamp(parameterIndex, (Timestamp)parameterObj, targetMysqlType);
/*      */             return;
/*      */           case YEAR:
/*  591 */             cal = Calendar.getInstance();
/*  592 */             cal.setTime((Date)parameterObj);
/*  593 */             setInt(parameterIndex, cal.get(1));
/*      */             return;
/*      */           case TIME:
/*  596 */             setLocalTime(parameterIndex, ((Timestamp)parameterObj).toLocalDateTime().toLocalTime(), targetMysqlType);
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  604 */             val = parameterObj.toString();
/*      */             
/*  606 */             if ((((Timestamp)parameterObj).getNanos() == 0 || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) && (
/*  607 */               dotPos = val.indexOf(".")) > 0) {
/*  608 */               val = val.substring(0, dotPos);
/*      */             }
/*  610 */             setString(parameterIndex, val);
/*      */             return;
/*      */         } 
/*  613 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  614 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  615 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  618 */       if (parameterObj instanceof Time) {
/*  619 */         Timestamp ts; int fractLen; Calendar cal; switch (targetMysqlType) {
/*      */           case DATE:
/*  621 */             setDate(parameterIndex, new Date(((Date)parameterObj).getTime()));
/*      */             return;
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*  625 */             ts = new Timestamp(((Time)parameterObj).getTime());
/*      */             
/*  627 */             fractLen = -1;
/*  628 */             if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSecondsForTime.getValue()).booleanValue() || 
/*  629 */               !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/*  630 */               fractLen = 0;
/*  631 */             } else if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0 && this.columnDefinition
/*  632 */               .getFields()[parameterIndex].getDecimals() > 0) {
/*  633 */               fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*      */             } 
/*      */             
/*  636 */             if (fractLen == 0) {
/*  637 */               ts = TimeUtil.truncateFractionalSeconds(ts);
/*      */             }
/*      */             
/*  640 */             bindTimestamp(parameterIndex, ts, null, fractLen, MysqlType.DATETIME);
/*      */             return;
/*      */           
/*      */           case YEAR:
/*  644 */             cal = Calendar.getInstance();
/*  645 */             cal.setTime((Date)parameterObj);
/*  646 */             setInt(parameterIndex, cal.get(1));
/*      */             return;
/*      */           case TIME:
/*  649 */             setTime(parameterIndex, (Time)parameterObj);
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  657 */             setString(parameterIndex, 
/*      */                 
/*  659 */                 TimeUtil.getSimpleDateFormat((this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && ((Boolean)this.sendFractionalSeconds
/*  660 */                   .getValue()).booleanValue() && ((Boolean)this.sendFractionalSecondsForTime.getValue()).booleanValue() && 
/*  661 */                   TimeUtil.hasFractionalSeconds((Time)parameterObj).booleanValue()) ? "HH:mm:ss.SSS" : "HH:mm:ss", null)
/*  662 */                 .format(parameterObj));
/*      */             return;
/*      */         } 
/*  665 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  666 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  667 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  670 */       if (parameterObj instanceof Date) {
/*  671 */         Calendar cal; LocalTime lt; if (!((Boolean)this.treatUtilDateAsTimestamp.getValue()).booleanValue()) {
/*  672 */           setSerializableObject(parameterIndex, parameterObj);
/*      */           return;
/*      */         } 
/*  675 */         switch (targetMysqlType) {
/*      */           case DATE:
/*  677 */             setDate(parameterIndex, new Date(((Date)parameterObj).getTime()));
/*      */             return;
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*  681 */             setTimestamp(parameterIndex, new Timestamp(((Date)parameterObj).getTime()), targetMysqlType);
/*      */             return;
/*      */           case YEAR:
/*  684 */             cal = Calendar.getInstance();
/*  685 */             cal.setTime((Date)parameterObj);
/*  686 */             setInt(parameterIndex, cal.get(1));
/*      */             return;
/*      */           
/*      */           case TIME:
/*  690 */             lt = ((Date)parameterObj).toInstant().atZone(this.session.getServerSession().getDefaultTimeZone().toZoneId()).toLocalTime();
/*  691 */             setLocalTime(parameterIndex, lt, targetMysqlType);
/*      */             return;
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  699 */             setString(parameterIndex, TimeUtil.getSimpleDateFormat((this.session
/*  700 */                   .getServerSession().getCapabilities().serverSupportsFracSecs() && ((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((Date)parameterObj)
/*  701 */                   .toInstant().getNano() > 0) ? "yyyy-MM-dd HH:mm:ss.SSS" : "yyyy-MM-dd HH:mm:ss", null)
/*  702 */                 .format(parameterObj));
/*      */             return;
/*      */         } 
/*  705 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  706 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  707 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  710 */       if (parameterObj instanceof Calendar) {
/*  711 */         LocalTime lt; ZonedDateTime zdt; switch (targetMysqlType) {
/*      */           case DATE:
/*  713 */             setDate(parameterIndex, new Date(((Calendar)parameterObj).getTimeInMillis()));
/*      */             return;
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*  717 */             setTimestamp(parameterIndex, new Timestamp(((Calendar)parameterObj).getTimeInMillis()), targetMysqlType);
/*      */             return;
/*      */           case YEAR:
/*  720 */             setInt(parameterIndex, ((Calendar)parameterObj).get(1));
/*      */             return;
/*      */           
/*      */           case TIME:
/*  724 */             lt = ((Calendar)parameterObj).toInstant().atZone(this.session.getServerSession().getDefaultTimeZone().toZoneId()).toLocalTime();
/*  725 */             setLocalTime(parameterIndex, lt, targetMysqlType);
/*      */             return;
/*      */ 
/*      */           
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*  735 */             zdt = ZonedDateTime.ofInstant(((Calendar)parameterObj).toInstant(), ((Calendar)parameterObj).getTimeZone().toZoneId()).withZoneSameInstant(this.session.getServerSession().getDefaultTimeZone().toZoneId());
/*  736 */             setString(parameterIndex, zdt
/*  737 */                 .format((zdt.getNano() > 0 && this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && ((Boolean)this.sendFractionalSeconds
/*  738 */                   .getValue()).booleanValue()) ? TimeUtil.DATETIME_FORMATTER_WITH_MILLIS_NO_OFFSET : TimeUtil.DATETIME_FORMATTER_NO_FRACT_NO_OFFSET));
/*      */             return;
/*      */         } 
/*      */         
/*  742 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  743 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  744 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  747 */       if (parameterObj instanceof String) {
/*  748 */         BigDecimal parameterAsNum; BigDecimal scaledBigDecimal; switch (targetMysqlType) {
/*      */           case BOOLEAN:
/*  750 */             if ("true".equalsIgnoreCase((String)parameterObj) || "Y".equalsIgnoreCase((String)parameterObj)) {
/*  751 */               setBoolean(parameterIndex, true);
/*  752 */             } else if ("false".equalsIgnoreCase((String)parameterObj) || "N".equalsIgnoreCase((String)parameterObj)) {
/*  753 */               setBoolean(parameterIndex, false);
/*  754 */             } else if (((String)parameterObj).matches("-?\\d+\\.?\\d*")) {
/*  755 */               setBoolean(parameterIndex, !((String)parameterObj).matches("-?[0]+[.]*[0]*"));
/*      */             } else {
/*  757 */               throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  758 */                   Messages.getString("PreparedStatement.66", new Object[] { parameterObj }), this.session.getExceptionInterceptor());
/*      */             } 
/*      */             return;
/*      */           case BIT:
/*  762 */             if ("1".equals(parameterObj) || "0".equals(parameterObj)) {
/*  763 */               setInt(parameterIndex, Integer.valueOf((String)parameterObj).intValue());
/*      */             } else {
/*  765 */               boolean parameterAsBoolean = "true".equalsIgnoreCase((String)parameterObj);
/*  766 */               setInt(parameterIndex, parameterAsBoolean ? 1 : 0);
/*      */             } 
/*      */             return;
/*      */ 
/*      */           
/*      */           case TINYINT:
/*      */           case TINYINT_UNSIGNED:
/*      */           case SMALLINT:
/*      */           case SMALLINT_UNSIGNED:
/*      */           case MEDIUMINT:
/*      */           case MEDIUMINT_UNSIGNED:
/*      */           case INT:
/*      */           case INT_UNSIGNED:
/*  779 */             setInt(parameterIndex, Integer.valueOf((String)parameterObj).intValue());
/*      */             return;
/*      */           case BIGINT:
/*  782 */             setLong(parameterIndex, Long.valueOf((String)parameterObj).longValue());
/*      */             return;
/*      */           case BIGINT_UNSIGNED:
/*  785 */             setLong(parameterIndex, (new BigInteger((String)parameterObj)).longValue());
/*      */             return;
/*      */           case FLOAT:
/*      */           case FLOAT_UNSIGNED:
/*  789 */             setFloat(parameterIndex, Float.valueOf((String)parameterObj).floatValue());
/*      */             return;
/*      */           case DOUBLE:
/*      */           case DOUBLE_UNSIGNED:
/*  793 */             setDouble(parameterIndex, Double.valueOf((String)parameterObj).doubleValue());
/*      */             return;
/*      */           case DECIMAL:
/*      */           case DECIMAL_UNSIGNED:
/*  797 */             parameterAsNum = new BigDecimal((String)parameterObj);
/*  798 */             scaledBigDecimal = null;
/*      */             
/*      */             try {
/*  801 */               scaledBigDecimal = parameterAsNum.setScale(scaleOrLength);
/*  802 */             } catch (ArithmeticException ex) {
/*      */               try {
/*  804 */                 scaledBigDecimal = parameterAsNum.setScale(scaleOrLength, 4);
/*  805 */               } catch (ArithmeticException arEx) {
/*  806 */                 throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  807 */                     Messages.getString("PreparedStatement.65", new Object[] { Integer.valueOf(scaleOrLength), parameterAsNum }), this.session
/*  808 */                     .getExceptionInterceptor());
/*      */               } 
/*      */             } 
/*  811 */             setBigDecimal(parameterIndex, scaledBigDecimal);
/*      */             return;
/*      */           
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*      */           case ENUM:
/*      */           case SET:
/*      */           case JSON:
/*  823 */             setString(parameterIndex, parameterObj.toString());
/*      */             return;
/*      */           case BINARY:
/*      */           case GEOMETRY:
/*      */           case VARBINARY:
/*      */           case TINYBLOB:
/*      */           case BLOB:
/*      */           case MEDIUMBLOB:
/*      */           case LONGBLOB:
/*  832 */             setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charEncoding));
/*      */             return;
/*      */           case DATE:
/*      */           case DATETIME:
/*      */           case TIMESTAMP:
/*      */           case YEAR:
/*      */           case TIME:
/*  839 */             setObject(parameterIndex, TimeUtil.parseToDateTimeObject((String)parameterObj, targetMysqlType), targetMysqlType);
/*      */             return;
/*      */           case UNKNOWN:
/*  842 */             setSerializableObject(parameterIndex, parameterObj);
/*      */             return;
/*      */         } 
/*  845 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  846 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  847 */             .getExceptionInterceptor());
/*      */       } 
/*      */       
/*  850 */       if (parameterObj instanceof InputStream) {
/*  851 */         setBinaryStream(parameterIndex, (InputStream)parameterObj, -1);
/*      */       } else {
/*  853 */         if (parameterObj instanceof Boolean) {
/*  854 */           switch (targetMysqlType) {
/*      */             case BOOLEAN:
/*  856 */               setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */               return;
/*      */             case YEAR:
/*      */             case BIT:
/*      */             case TINYINT:
/*      */             case TINYINT_UNSIGNED:
/*      */             case SMALLINT:
/*      */             case SMALLINT_UNSIGNED:
/*      */             case MEDIUMINT:
/*      */             case MEDIUMINT_UNSIGNED:
/*      */             case INT:
/*      */             case INT_UNSIGNED:
/*  868 */               setInt(parameterIndex, ((Boolean)parameterObj).booleanValue() ? 1 : 0);
/*      */               return;
/*      */             case BIGINT:
/*      */             case BIGINT_UNSIGNED:
/*  872 */               setLong(parameterIndex, ((Boolean)parameterObj).booleanValue() ? 1L : 0L);
/*      */               return;
/*      */             case FLOAT:
/*      */             case FLOAT_UNSIGNED:
/*  876 */               setFloat(parameterIndex, ((Boolean)parameterObj).booleanValue() ? 1.0F : 0.0F);
/*      */               return;
/*      */             case DOUBLE:
/*      */             case DOUBLE_UNSIGNED:
/*  880 */               setDouble(parameterIndex, ((Boolean)parameterObj).booleanValue() ? 1.0D : 0.0D);
/*      */               return;
/*      */             case DECIMAL:
/*      */             case DECIMAL_UNSIGNED:
/*  884 */               setBigDecimal(parameterIndex, new BigDecimal(((Boolean)parameterObj).booleanValue() ? 1.0D : 0.0D));
/*      */               return;
/*      */             case CHAR:
/*      */             case VARCHAR:
/*      */             case TINYTEXT:
/*      */             case TEXT:
/*      */             case MEDIUMTEXT:
/*      */             case LONGTEXT:
/*  892 */               setString(parameterIndex, parameterObj.toString());
/*      */               return;
/*      */           } 
/*  895 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  896 */               Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  897 */               .getExceptionInterceptor());
/*      */         } 
/*      */         
/*  900 */         if (parameterObj instanceof Number) {
/*  901 */           Number parameterAsNum = (Number)parameterObj;
/*  902 */           switch (targetMysqlType) {
/*      */             case BOOLEAN:
/*  904 */               setBoolean(parameterIndex, (parameterAsNum.intValue() != 0));
/*      */               return;
/*      */             case YEAR:
/*      */             case BIT:
/*      */             case TINYINT:
/*      */             case TINYINT_UNSIGNED:
/*      */             case SMALLINT:
/*      */             case SMALLINT_UNSIGNED:
/*      */             case MEDIUMINT:
/*      */             case MEDIUMINT_UNSIGNED:
/*      */             case INT:
/*      */             case INT_UNSIGNED:
/*  916 */               setInt(parameterIndex, parameterAsNum.intValue());
/*      */               return;
/*      */             case BIGINT:
/*      */             case BIGINT_UNSIGNED:
/*  920 */               setLong(parameterIndex, parameterAsNum.longValue());
/*      */               return;
/*      */             case FLOAT:
/*      */             case FLOAT_UNSIGNED:
/*  924 */               setFloat(parameterIndex, parameterAsNum.floatValue());
/*      */               return;
/*      */             case DOUBLE:
/*      */             case DOUBLE_UNSIGNED:
/*  928 */               setDouble(parameterIndex, parameterAsNum.doubleValue());
/*      */               return;
/*      */             case DECIMAL:
/*      */             case DECIMAL_UNSIGNED:
/*  932 */               if (parameterAsNum instanceof BigDecimal) {
/*  933 */                 BigDecimal scaledBigDecimal = null;
/*      */                 
/*      */                 try {
/*  936 */                   scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scaleOrLength);
/*  937 */                 } catch (ArithmeticException ex) {
/*      */                   try {
/*  939 */                     scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scaleOrLength, 4);
/*  940 */                   } catch (ArithmeticException arEx) {
/*  941 */                     throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  942 */                         Messages.getString("PreparedStatement.65", new Object[] { Integer.valueOf(scaleOrLength), parameterAsNum }), this.session
/*  943 */                         .getExceptionInterceptor());
/*      */                   } 
/*      */                 } 
/*      */                 
/*  947 */                 setBigDecimal(parameterIndex, scaledBigDecimal);
/*  948 */               } else if (parameterAsNum instanceof BigInteger) {
/*  949 */                 setBigDecimal(parameterIndex, new BigDecimal((BigInteger)parameterAsNum, scaleOrLength));
/*      */               } else {
/*  951 */                 setBigDecimal(parameterIndex, new BigDecimal(parameterAsNum.doubleValue()));
/*      */               } 
/*      */               return;
/*      */             
/*      */             case CHAR:
/*      */             case VARCHAR:
/*      */             case TINYTEXT:
/*      */             case TEXT:
/*      */             case MEDIUMTEXT:
/*      */             case LONGTEXT:
/*      */             case ENUM:
/*      */             case SET:
/*      */             case JSON:
/*  964 */               if (parameterObj instanceof BigDecimal) {
/*  965 */                 setString(parameterIndex, StringUtils.fixDecimalExponent(((BigDecimal)parameterObj).toPlainString()));
/*      */               } else {
/*  967 */                 setString(parameterIndex, parameterObj.toString());
/*      */               } 
/*      */               return;
/*      */             
/*      */             case BINARY:
/*      */             case GEOMETRY:
/*      */             case VARBINARY:
/*      */             case TINYBLOB:
/*      */             case BLOB:
/*      */             case MEDIUMBLOB:
/*      */             case LONGBLOB:
/*  978 */               setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charEncoding));
/*      */               return;
/*      */           } 
/*  981 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  982 */               Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/*  983 */               .getExceptionInterceptor());
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  988 */         switch (targetMysqlType) {
/*      */           case BOOLEAN:
/*  990 */             throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/*  991 */                 Messages.getString("PreparedStatement.66", new Object[] { parameterObj.getClass().getName() }), this.session
/*  992 */                 .getExceptionInterceptor());
/*      */           case CHAR:
/*      */           case VARCHAR:
/*      */           case TINYTEXT:
/*      */           case TEXT:
/*      */           case MEDIUMTEXT:
/*      */           case LONGTEXT:
/*      */           case ENUM:
/*      */           case SET:
/*      */           case JSON:
/* 1002 */             if (parameterObj instanceof BigDecimal) {
/* 1003 */               setString(parameterIndex, StringUtils.fixDecimalExponent(((BigDecimal)parameterObj).toPlainString()));
/* 1004 */             } else if (parameterObj instanceof Clob) {
/* 1005 */               setClob(parameterIndex, (Clob)parameterObj);
/*      */             } else {
/* 1007 */               setString(parameterIndex, parameterObj.toString());
/*      */             } 
/*      */             return;
/*      */           
/*      */           case BINARY:
/*      */           case GEOMETRY:
/*      */           case VARBINARY:
/*      */           case TINYBLOB:
/*      */           case BLOB:
/*      */           case MEDIUMBLOB:
/*      */           case LONGBLOB:
/* 1018 */             if (parameterObj instanceof byte[]) {
/* 1019 */               setBytes(parameterIndex, (byte[])parameterObj);
/* 1020 */             } else if (parameterObj instanceof Blob) {
/* 1021 */               setBlob(parameterIndex, (Blob)parameterObj);
/*      */             } else {
/* 1023 */               setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charEncoding));
/*      */             } 
/*      */             return;
/*      */           
/*      */           case UNKNOWN:
/* 1028 */             setSerializableObject(parameterIndex, parameterObj);
/*      */             return;
/*      */         } 
/*      */         
/* 1032 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 1033 */             Messages.getString("PreparedStatement.67", new Object[] { parameterObj.getClass().getName(), targetMysqlType.toString() }), this.session
/* 1034 */             .getExceptionInterceptor());
/*      */       }
/*      */     
/* 1037 */     } catch (Exception ex) {
/* 1038 */       throw ExceptionFactory.createException(
/* 1039 */           Messages.getString("PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString("PreparedStatement.18") + ex
/* 1040 */           .getClass().getName() + Messages.getString("PreparedStatement.19") + ex.getMessage(), ex, this.session
/* 1041 */           .getExceptionInterceptor());
/*      */     } 
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
/*      */   protected final void setSerializableObject(int parameterIndex, Object parameterObj) {
/*      */     try {
/* 1055 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/* 1056 */       ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
/* 1057 */       objectOut.writeObject(parameterObj);
/* 1058 */       objectOut.flush();
/* 1059 */       objectOut.close();
/* 1060 */       bytesOut.flush();
/* 1061 */       bytesOut.close();
/*      */       
/* 1063 */       byte[] buf = bytesOut.toByteArray();
/* 1064 */       ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
/* 1065 */       setBinaryStream(parameterIndex, bytesIn, buf.length);
/* 1066 */       this.bindValues[parameterIndex].setMysqlType(MysqlType.BINARY);
/* 1067 */     } catch (Exception ex) {
/* 1068 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.54") + ex.getClass().getName(), ex, this.session
/* 1069 */           .getExceptionInterceptor());
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isNull(int parameterIndex) {
/* 1075 */     return this.bindValues[parameterIndex].isNull();
/*      */   }
/*      */   
/*      */   public byte[] getBytesRepresentation(int parameterIndex) {
/* 1079 */     if (this.bindValues[parameterIndex].isStream()) {
/* 1080 */       return streamToBytes(parameterIndex, ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.useStreamLengthsInPrepStmts).getValue()).booleanValue());
/*      */     }
/*      */     
/* 1083 */     byte[] parameterVal = this.bindValues[parameterIndex].getByteValue();
/*      */     
/* 1085 */     if (parameterVal == null) {
/* 1086 */       return null;
/*      */     }
/*      */     
/* 1089 */     return StringUtils.unquoteBytes(parameterVal);
/*      */   }
/*      */   
/* 1092 */   public AbstractQueryBindings(int parameterCount, Session sess) { this.streamConvertBuf = null; this.session = sess; this.charEncoding = (String)this.session.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue(); this.preserveInstants = this.session.getPropertySet().getBooleanProperty(PropertyKey.preserveInstants); this.sendFractionalSeconds = this.session.getPropertySet().getBooleanProperty(PropertyKey.sendFractionalSeconds); this.sendFractionalSecondsForTime = this.session.getPropertySet().getBooleanProperty(PropertyKey.sendFractionalSecondsForTime);
/*      */     this.treatUtilDateAsTimestamp = this.session.getPropertySet().getBooleanProperty(PropertyKey.treatUtilDateAsTimestamp);
/*      */     this.useStreamLengthsInPrepStmts = this.session.getPropertySet().getBooleanProperty(PropertyKey.useStreamLengthsInPrepStmts);
/* 1095 */     initBindValues(parameterCount); } private final byte[] streamToBytes(int parameterIndex, boolean useLength) { InputStream in = this.bindValues[parameterIndex].getStreamValue();
/* 1096 */     in.mark(2147483647);
/*      */     try {
/* 1098 */       if (this.streamConvertBuf == null) {
/* 1099 */         this.streamConvertBuf = new byte[4096];
/*      */       }
/* 1101 */       if (this.bindValues[parameterIndex].getStreamLength() == -1L) {
/* 1102 */         useLength = false;
/*      */       }
/*      */       
/* 1105 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */ 
/*      */       
/* 1108 */       int bc = useLength ? Util.readBlock(in, this.streamConvertBuf, (int)this.bindValues[parameterIndex].getStreamLength(), null) : Util.readBlock(in, this.streamConvertBuf, null);
/*      */       
/* 1110 */       int lengthLeftToRead = (int)this.bindValues[parameterIndex].getStreamLength() - bc;
/*      */       
/* 1112 */       while (bc > 0) {
/* 1113 */         bytesOut.write(this.streamConvertBuf, 0, bc);
/*      */         
/* 1115 */         if (useLength) {
/* 1116 */           bc = Util.readBlock(in, this.streamConvertBuf, lengthLeftToRead, null);
/*      */           
/* 1118 */           if (bc > 0)
/* 1119 */             lengthLeftToRead -= bc; 
/*      */           continue;
/*      */         } 
/* 1122 */         bc = Util.readBlock(in, this.streamConvertBuf, null);
/*      */       } 
/*      */ 
/*      */       
/* 1126 */       return bytesOut.toByteArray();
/*      */     } finally {
/*      */       try {
/* 1129 */         in.reset();
/* 1130 */       } catch (IOException iOException) {}
/*      */       
/* 1132 */       if (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.autoClosePStmtStreams).getValue()).booleanValue()) {
/*      */         try {
/* 1134 */           in.close();
/* 1135 */         } catch (IOException iOException) {}
/*      */ 
/*      */         
/* 1138 */         in = null;
/*      */       } 
/*      */     }  }
/*      */ 
/*      */   
/*      */   protected abstract void initBindValues(int paramInt);
/*      */   
/*      */   public abstract AbstractQueryBindings<T> clone();
/*      */   
/*      */   public abstract void checkParameterSet(int paramInt);
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\AbstractQueryBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */