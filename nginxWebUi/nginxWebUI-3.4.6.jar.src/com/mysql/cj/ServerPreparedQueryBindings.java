/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.mysql.cj.util.TimeUtil;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.NClob;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.time.Duration;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.util.Calendar;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerPreparedQueryBindings
/*     */   extends AbstractQueryBindings<ServerPreparedQueryBindValue>
/*     */ {
/*  60 */   private AtomicBoolean sendTypesToServer = new AtomicBoolean(false);
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean longParameterSwitchDetected = false;
/*     */ 
/*     */ 
/*     */   
/*     */   public ServerPreparedQueryBindings(int parameterCount, Session sess) {
/*  69 */     super(parameterCount, sess);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initBindValues(int parameterCount) {
/*  74 */     this.bindValues = new ServerPreparedQueryBindValue[parameterCount];
/*  75 */     for (int i = 0; i < parameterCount; i++) {
/*  76 */       this.bindValues[i] = new ServerPreparedQueryBindValue(this.session.getServerSession().getDefaultTimeZone(), this.session
/*  77 */           .getServerSession().getSessionTimeZone(), this.session.getPropertySet());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ServerPreparedQueryBindings clone() {
/*  83 */     ServerPreparedQueryBindings newBindings = new ServerPreparedQueryBindings(this.bindValues.length, this.session);
/*  84 */     ServerPreparedQueryBindValue[] bvs = new ServerPreparedQueryBindValue[this.bindValues.length];
/*  85 */     for (int i = 0; i < this.bindValues.length; i++) {
/*  86 */       bvs[i] = this.bindValues[i].clone();
/*     */     }
/*  88 */     newBindings.bindValues = bvs;
/*  89 */     newBindings.sendTypesToServer = this.sendTypesToServer;
/*  90 */     newBindings.longParameterSwitchDetected = this.longParameterSwitchDetected;
/*  91 */     newBindings.isLoadDataQuery = this.isLoadDataQuery;
/*  92 */     return newBindings;
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
/*     */   public ServerPreparedQueryBindValue getBinding(int parameterIndex, boolean forLongData) {
/* 107 */     if (this.bindValues[parameterIndex] != null)
/*     */     {
/*     */       
/* 110 */       if ((this.bindValues[parameterIndex]).isStream && !forLongData) {
/* 111 */         this.longParameterSwitchDetected = true;
/*     */       }
/*     */     }
/*     */     
/* 115 */     return this.bindValues[parameterIndex];
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkParameterSet(int columnIndex) {
/* 120 */     if (!this.bindValues[columnIndex].isSet()) {
/* 121 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 122 */           Messages.getString("ServerPreparedStatement.13") + (columnIndex + 1) + Messages.getString("ServerPreparedStatement.14"));
/*     */     }
/*     */   }
/*     */   
/*     */   public AtomicBoolean getSendTypesToServer() {
/* 127 */     return this.sendTypesToServer;
/*     */   }
/*     */   
/*     */   public boolean isLongParameterSwitchDetected() {
/* 131 */     return this.longParameterSwitchDetected;
/*     */   }
/*     */   
/*     */   public void setLongParameterSwitchDetected(boolean longParameterSwitchDetected) {
/* 135 */     this.longParameterSwitchDetected = longParameterSwitchDetected;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x) {
/* 140 */     setAsciiStream(parameterIndex, x, -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, int length) {
/* 145 */     if (x == null) {
/* 146 */       setNull(parameterIndex);
/*     */     } else {
/* 148 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, true);
/* 149 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, this.numberOfExecutions));
/* 150 */       binding.value = x;
/* 151 */       binding.isStream = true;
/* 152 */       binding.streamLength = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue() ? length : -1L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length) {
/* 158 */     setAsciiStream(parameterIndex, x, (int)length);
/* 159 */     this.bindValues[parameterIndex].setMysqlType(MysqlType.TEXT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBigDecimal(int parameterIndex, BigDecimal x) {
/* 164 */     if (x == null) {
/* 165 */       setNull(parameterIndex);
/*     */     } else {
/* 167 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 168 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(246, this.numberOfExecutions));
/* 169 */       binding.value = StringUtils.fixDecimalExponent(x.toPlainString());
/* 170 */       binding.parameterType = MysqlType.DECIMAL;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBigInteger(int parameterIndex, BigInteger x) {
/* 176 */     setValue(parameterIndex, x.toString(), MysqlType.BIGINT_UNSIGNED);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x) {
/* 181 */     setBinaryStream(parameterIndex, x, -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, int length) {
/* 186 */     if (x == null) {
/* 187 */       setNull(parameterIndex);
/*     */     } else {
/* 189 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, true);
/* 190 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, this.numberOfExecutions));
/* 191 */       binding.value = x;
/* 192 */       binding.isStream = true;
/* 193 */       binding.streamLength = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue() ? length : -1L;
/* 194 */       binding.parameterType = MysqlType.BLOB;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length) {
/* 200 */     setBinaryStream(parameterIndex, x, (int)length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream) {
/* 205 */     setBinaryStream(parameterIndex, inputStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length) {
/* 210 */     setBinaryStream(parameterIndex, inputStream, (int)length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, Blob x) {
/* 216 */     if (x == null) {
/* 217 */       setNull(parameterIndex);
/*     */     } else {
/*     */       try {
/* 220 */         ServerPreparedQueryBindValue binding = getBinding(parameterIndex, true);
/* 221 */         this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, this.numberOfExecutions));
/* 222 */         binding.value = x;
/* 223 */         binding.isStream = true;
/* 224 */         binding.streamLength = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue() ? x.length() : -1L;
/* 225 */         binding.parameterType = MysqlType.BLOB;
/* 226 */       } catch (Throwable t) {
/* 227 */         throw ExceptionFactory.createException(t.getMessage(), t);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBoolean(int parameterIndex, boolean x) {
/* 234 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 235 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(1, this.numberOfExecutions));
/* 236 */     binding.value = Long.valueOf(x ? 1L : 0L);
/* 237 */     binding.parameterType = MysqlType.BOOLEAN;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setByte(int parameterIndex, byte x) {
/* 242 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 243 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(1, this.numberOfExecutions));
/* 244 */     binding.value = Long.valueOf(x);
/* 245 */     binding.parameterType = MysqlType.TINYINT;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBytes(int parameterIndex, byte[] x) {
/* 250 */     if (x == null) {
/* 251 */       setNull(parameterIndex);
/*     */     } else {
/* 253 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 254 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(253, this.numberOfExecutions));
/* 255 */       binding.value = x;
/* 256 */       binding.parameterType = MysqlType.BINARY;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars) {
/* 262 */     setBytes(parameterIndex, x);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes) {
/* 267 */     setBytes(parameterIndex, parameterAsBytes);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) {
/* 272 */     setBytes(parameterIndex, parameterAsBytes);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader) {
/* 277 */     setCharacterStream(parameterIndex, reader, -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, int length) {
/* 282 */     if (reader == null) {
/* 283 */       setNull(parameterIndex);
/*     */     } else {
/* 285 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, true);
/* 286 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, this.numberOfExecutions));
/* 287 */       binding.value = reader;
/* 288 */       binding.isStream = true;
/* 289 */       binding.streamLength = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue() ? length : -1L;
/* 290 */       binding.parameterType = MysqlType.TEXT;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length) {
/* 296 */     setCharacterStream(parameterIndex, reader, (int)length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader) {
/* 301 */     setCharacterStream(parameterIndex, reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader, long length) {
/* 306 */     setCharacterStream(parameterIndex, reader, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Clob x) {
/* 311 */     if (x == null) {
/* 312 */       setNull(parameterIndex);
/*     */     } else {
/*     */       try {
/* 315 */         ServerPreparedQueryBindValue binding = getBinding(parameterIndex, true);
/* 316 */         this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, this.numberOfExecutions));
/* 317 */         binding.value = x.getCharacterStream();
/* 318 */         binding.isStream = true;
/* 319 */         binding.streamLength = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue() ? x.length() : -1L;
/* 320 */         binding.parameterType = MysqlType.TEXT;
/* 321 */       } catch (Throwable t) {
/* 322 */         throw ExceptionFactory.createException(t.getMessage(), t);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x) {
/* 329 */     setDate(parameterIndex, x, (Calendar)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x, Calendar cal) {
/* 334 */     if (x == null) {
/* 335 */       setNull(parameterIndex);
/*     */     } else {
/* 337 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 338 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(10, this.numberOfExecutions));
/* 339 */       binding.value = x;
/* 340 */       binding.calendar = (cal == null) ? null : (Calendar)cal.clone();
/* 341 */       binding.parameterType = MysqlType.DATE;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDouble(int parameterIndex, double x) {
/* 347 */     if (!((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.allowNanAndInf).getValue()).booleanValue() && (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY || 
/* 348 */       Double.isNaN(x))) {
/* 349 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.64", new Object[] { Double.valueOf(x) }), this.session
/* 350 */           .getExceptionInterceptor());
/*     */     }
/* 352 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 353 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(5, this.numberOfExecutions));
/* 354 */     binding.value = Double.valueOf(x);
/* 355 */     binding.parameterType = MysqlType.DOUBLE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFloat(int parameterIndex, float x) {
/* 360 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 361 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(4, this.numberOfExecutions));
/* 362 */     binding.value = Float.valueOf(x);
/* 363 */     binding.parameterType = MysqlType.FLOAT;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInt(int parameterIndex, int x) {
/* 368 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 369 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(3, this.numberOfExecutions));
/* 370 */     binding.value = Long.valueOf(x);
/* 371 */     binding.parameterType = MysqlType.INT;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLocalDate(int parameterIndex, LocalDate x, MysqlType targetMysqlType) {
/* 376 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 377 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(10, this.numberOfExecutions));
/* 378 */     binding.parameterType = targetMysqlType;
/* 379 */     binding.value = x;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLocalTime(int parameterIndex, LocalTime x, MysqlType targetMysqlType) {
/* 384 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 385 */     if (targetMysqlType == MysqlType.DATE) {
/* 386 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(10, this.numberOfExecutions));
/* 387 */       binding.parameterType = targetMysqlType;
/* 388 */       binding.value = DEFAULT_DATE;
/*     */       
/*     */       return;
/*     */     } 
/* 392 */     if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/* 393 */       if (x.getNano() > 0) {
/* 394 */         x = x.withNano(0);
/*     */       }
/*     */     } else {
/* 397 */       int fractLen = 6;
/* 398 */       if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0)
/*     */       {
/* 400 */         fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*     */       }
/* 402 */       x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
/*     */     } 
/*     */     
/* 405 */     if (targetMysqlType == MysqlType.TIME) {
/* 406 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(11, this.numberOfExecutions));
/*     */     } else {
/*     */       
/* 409 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(12, this.numberOfExecutions));
/*     */     } 
/* 411 */     binding.parameterType = targetMysqlType;
/* 412 */     binding.value = x;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLocalDateTime(int parameterIndex, LocalDateTime x, MysqlType targetMysqlType) {
/* 417 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 418 */     if (targetMysqlType == MysqlType.DATE) {
/* 419 */       x = LocalDateTime.of(x.toLocalDate(), DEFAULT_TIME);
/* 420 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(10, this.numberOfExecutions));
/*     */     } else {
/* 422 */       int fractLen = 6;
/* 423 */       if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0)
/*     */       {
/* 425 */         fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*     */       }
/*     */       
/* 428 */       if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/* 429 */         if (x.getNano() > 0) {
/* 430 */           x = x.withNano(0);
/*     */         }
/*     */       } else {
/* 433 */         x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
/*     */       } 
/*     */       
/* 436 */       if (targetMysqlType == MysqlType.TIME) {
/* 437 */         this.sendTypesToServer.compareAndSet(false, binding.resetToType(11, this.numberOfExecutions));
/*     */       } else {
/*     */         
/* 440 */         this.sendTypesToServer.compareAndSet(false, binding.resetToType(12, this.numberOfExecutions));
/*     */       } 
/*     */     } 
/* 443 */     binding.parameterType = targetMysqlType;
/* 444 */     binding.value = x;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLong(int parameterIndex, long x) {
/* 449 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 450 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(8, this.numberOfExecutions));
/* 451 */     binding.value = Long.valueOf(x);
/* 452 */     binding.parameterType = MysqlType.BIGINT;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value) {
/* 457 */     setNCharacterStream(parameterIndex, value, -1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader reader, long length) {
/* 462 */     if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8")) {
/* 463 */       throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.28"), this.session.getExceptionInterceptor());
/*     */     }
/*     */     
/* 466 */     if (reader == null) {
/* 467 */       setNull(parameterIndex);
/*     */     } else {
/* 469 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, true);
/* 470 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, this.numberOfExecutions));
/* 471 */       binding.value = reader;
/* 472 */       binding.isStream = true;
/* 473 */       binding.streamLength = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue() ? length : -1L;
/* 474 */       binding.parameterType = MysqlType.TEXT;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader) {
/* 480 */     setNCharacterStream(parameterIndex, reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader, long length) {
/* 485 */     if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8")) {
/* 486 */       throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.29"), this.session.getExceptionInterceptor());
/*     */     }
/* 488 */     setNCharacterStream(parameterIndex, reader, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, NClob value) {
/*     */     try {
/* 494 */       setNClob(parameterIndex, value.getCharacterStream(), ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue() ? value.length() : -1L);
/* 495 */     } catch (Throwable t) {
/* 496 */       throw ExceptionFactory.createException(t.getMessage(), t, this.session.getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNString(int parameterIndex, String x) {
/* 502 */     if (this.charEncoding.equalsIgnoreCase("UTF-8") || this.charEncoding.equalsIgnoreCase("utf8")) {
/* 503 */       setString(parameterIndex, x);
/*     */     } else {
/* 505 */       throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.30"), this.session.getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNull(int parameterIndex) {
/* 511 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 512 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(6, this.numberOfExecutions));
/* 513 */     binding.setNull(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setShort(int parameterIndex, short x) {
/* 518 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 519 */     this.sendTypesToServer.compareAndSet(false, binding.resetToType(2, this.numberOfExecutions));
/* 520 */     binding.value = Long.valueOf(x);
/* 521 */     binding.parameterType = MysqlType.SMALLINT;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setString(int parameterIndex, String x) {
/* 526 */     if (x == null) {
/* 527 */       setNull(parameterIndex);
/*     */     } else {
/* 529 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 530 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(253, this.numberOfExecutions));
/* 531 */       binding.value = x;
/* 532 */       binding.charEncoding = this.charEncoding;
/* 533 */       binding.parameterType = MysqlType.VARCHAR;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setTime(int parameterIndex, Time x, Calendar cal) {
/* 538 */     if (x == null) {
/* 539 */       setNull(parameterIndex);
/*     */     } else {
/* 541 */       if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() || 
/* 542 */         !((Boolean)this.sendFractionalSecondsForTime.getValue()).booleanValue()) {
/* 543 */         x = TimeUtil.truncateFractionalSeconds(x);
/*     */       }
/*     */       
/* 546 */       ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 547 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(11, this.numberOfExecutions));
/* 548 */       binding.value = x;
/* 549 */       binding.calendar = (cal == null) ? null : (Calendar)cal.clone();
/* 550 */       binding.parameterType = MysqlType.TIME;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setTime(int parameterIndex, Time x) {
/* 555 */     setTime(parameterIndex, x, (Calendar)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void bindTimestamp(int parameterIndex, Timestamp x, Calendar targetCalendar, int fractionalLength, MysqlType targetMysqlType) {
/* 560 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 561 */     this.sendTypesToServer.compareAndSet(false, binding
/* 562 */         .resetToType((targetMysqlType == MysqlType.TIMESTAMP) ? 7 : 12, this.numberOfExecutions));
/*     */     
/* 564 */     if (fractionalLength < 0)
/*     */     {
/* 566 */       fractionalLength = 6;
/*     */     }
/*     */     
/* 569 */     x = TimeUtil.adjustNanosPrecision(x, fractionalLength, !this.session.getServerSession().isServerTruncatesFracSecs());
/*     */     
/* 571 */     binding.value = x;
/* 572 */     binding.calendar = (targetCalendar == null) ? null : (Calendar)targetCalendar.clone();
/* 573 */     binding.parameterType = targetMysqlType;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDuration(int parameterIndex, Duration x, MysqlType targetMysqlType) {
/* 579 */     ServerPreparedQueryBindValue binding = getBinding(parameterIndex, false);
/* 580 */     if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/* 581 */       if (x.getNano() > 0) {
/* 582 */         x = x.isNegative() ? x.plusSeconds(1L).withNanos(0) : x.withNanos(0);
/*     */       }
/*     */     } else {
/* 585 */       int fractLen = 6;
/* 586 */       if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0)
/*     */       {
/* 588 */         fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*     */       }
/* 590 */       x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
/*     */     } 
/*     */     
/* 593 */     if (targetMysqlType == MysqlType.TIME) {
/* 594 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(11, this.numberOfExecutions));
/*     */     } else {
/*     */       
/* 597 */       this.sendTypesToServer.compareAndSet(false, binding.resetToType(12, this.numberOfExecutions));
/*     */     } 
/* 599 */     binding.parameterType = targetMysqlType;
/* 600 */     binding.value = x;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ServerPreparedQueryBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */