/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.mysql.cj.util.TimeUtil;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.NClob;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.time.Duration;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.DateTimeFormatterBuilder;
/*     */ import java.time.temporal.ChronoField;
/*     */ import java.util.Calendar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClientPreparedQueryBindings
/*     */   extends AbstractQueryBindings<ClientPreparedQueryBindValue>
/*     */ {
/*     */   private CharsetEncoder charsetEncoder;
/*     */   private SimpleDateFormat ddf;
/*     */   private SimpleDateFormat tdf;
/*  76 */   private SimpleDateFormat tsdf = null;
/*     */   
/*     */   public ClientPreparedQueryBindings(int parameterCount, Session sess) {
/*  79 */     super(parameterCount, sess);
/*  80 */     if (((NativeSession)this.session).getServerSession().getCharsetSettings().getRequiresEscapingEncoder()) {
/*  81 */       this.charsetEncoder = Charset.forName(this.charEncoding).newEncoder();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void initBindValues(int parameterCount) {
/*  87 */     this.bindValues = new ClientPreparedQueryBindValue[parameterCount];
/*  88 */     for (int i = 0; i < parameterCount; i++) {
/*  89 */       this.bindValues[i] = new ClientPreparedQueryBindValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientPreparedQueryBindings clone() {
/*  95 */     ClientPreparedQueryBindings newBindings = new ClientPreparedQueryBindings(this.bindValues.length, this.session);
/*  96 */     ClientPreparedQueryBindValue[] bvs = new ClientPreparedQueryBindValue[this.bindValues.length];
/*  97 */     for (int i = 0; i < this.bindValues.length; i++) {
/*  98 */       bvs[i] = this.bindValues[i].clone();
/*     */     }
/* 100 */     newBindings.setBindValues(bvs);
/* 101 */     newBindings.isLoadDataQuery = this.isLoadDataQuery;
/* 102 */     return newBindings;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkParameterSet(int columnIndex) {
/* 107 */     if (!this.bindValues[columnIndex].isSet()) {
/* 108 */       throw ExceptionFactory.createException(Messages.getString("PreparedStatement.40") + (columnIndex + 1), "07001", 0, true, null, this.session
/* 109 */           .getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x) {
/* 115 */     setAsciiStream(parameterIndex, x, -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, int length) {
/* 120 */     if (x == null) {
/* 121 */       setNull(parameterIndex);
/*     */     } else {
/* 123 */       setBinaryStream(parameterIndex, x, length);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length) {
/* 129 */     setAsciiStream(parameterIndex, x, (int)length);
/* 130 */     this.bindValues[parameterIndex].setMysqlType(MysqlType.TEXT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBigDecimal(int parameterIndex, BigDecimal x) {
/* 135 */     if (x == null) {
/* 136 */       setNull(parameterIndex);
/*     */     } else {
/* 138 */       setValue(parameterIndex, StringUtils.fixDecimalExponent(x.toPlainString()), MysqlType.DECIMAL);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBigInteger(int parameterIndex, BigInteger x) {
/* 144 */     setValue(parameterIndex, x.toString(), MysqlType.BIGINT_UNSIGNED);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x) {
/* 149 */     setBinaryStream(parameterIndex, x, -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, int length) {
/* 154 */     if (x == null) {
/* 155 */       setNull(parameterIndex);
/*     */     } else {
/* 157 */       this.bindValues[parameterIndex].setNull(false);
/* 158 */       this.bindValues[parameterIndex].setIsStream(true);
/* 159 */       this.bindValues[parameterIndex].setMysqlType(MysqlType.BLOB);
/* 160 */       this.bindValues[parameterIndex].setStreamValue(x, length);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length) {
/* 166 */     setBinaryStream(parameterIndex, x, (int)length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream) {
/* 171 */     setBinaryStream(parameterIndex, inputStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length) {
/* 176 */     setBinaryStream(parameterIndex, inputStream, (int)length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBlob(int parameterIndex, Blob x) {
/* 181 */     if (x == null) {
/* 182 */       setNull(parameterIndex);
/*     */     } else {
/*     */       try {
/* 185 */         setBinaryStream(parameterIndex, x.getBinaryStream(), x.length());
/* 186 */       } catch (Throwable t) {
/* 187 */         throw ExceptionFactory.createException(t.getMessage(), t, this.session.getExceptionInterceptor());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBoolean(int parameterIndex, boolean x) {
/* 194 */     setValue(parameterIndex, x ? "1" : "0", MysqlType.BOOLEAN);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setByte(int parameterIndex, byte x) {
/* 199 */     setValue(parameterIndex, String.valueOf(x), MysqlType.TINYINT);
/*     */   }
/*     */   
/*     */   public void setBytes(int parameterIndex, byte[] x) {
/* 203 */     setBytes(parameterIndex, x, true, true);
/*     */     
/* 205 */     if (x != null) {
/* 206 */       this.bindValues[parameterIndex].setMysqlType(MysqlType.BINARY);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars) {
/* 211 */     if (x == null) {
/* 212 */       setNull(parameterIndex);
/*     */     } else {
/* 214 */       if (this.session.getServerSession().isNoBackslashEscapesSet() || (escapeForMBChars && this.session
/* 215 */         .getServerSession().getCharsetSettings().isMultibyteCharset(this.charEncoding))) {
/*     */ 
/*     */ 
/*     */         
/* 219 */         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(x.length * 2 + 3);
/* 220 */         byteArrayOutputStream.write(120);
/* 221 */         byteArrayOutputStream.write(39);
/*     */ 
/*     */         
/* 224 */         for (int j = 0; j < x.length; j++) {
/* 225 */           int lowBits = (x[j] & 0xFF) / 16;
/* 226 */           int highBits = (x[j] & 0xFF) % 16;
/*     */           
/* 228 */           byteArrayOutputStream.write(HEX_DIGITS[lowBits]);
/* 229 */           byteArrayOutputStream.write(HEX_DIGITS[highBits]);
/*     */         } 
/*     */         
/* 232 */         byteArrayOutputStream.write(39);
/*     */         
/* 234 */         setValue(parameterIndex, byteArrayOutputStream.toByteArray(), MysqlType.BINARY);
/*     */ 
/*     */         
/* 237 */         setOrigValue(parameterIndex, x);
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 243 */       int numBytes = x.length;
/*     */       
/* 245 */       int pad = 2;
/*     */       
/* 247 */       if (checkForIntroducer) {
/* 248 */         pad += 7;
/*     */       }
/*     */       
/* 251 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream(numBytes + pad);
/*     */       
/* 253 */       if (checkForIntroducer) {
/* 254 */         bOut.write(95);
/* 255 */         bOut.write(98);
/* 256 */         bOut.write(105);
/* 257 */         bOut.write(110);
/* 258 */         bOut.write(97);
/* 259 */         bOut.write(114);
/* 260 */         bOut.write(121);
/*     */       } 
/* 262 */       bOut.write(39);
/*     */       
/* 264 */       for (int i = 0; i < numBytes; i++) {
/* 265 */         byte b = x[i];
/*     */         
/* 267 */         switch (b) {
/*     */           case 0:
/* 269 */             bOut.write(92);
/* 270 */             bOut.write(48);
/*     */             break;
/*     */           case 10:
/* 273 */             bOut.write(92);
/* 274 */             bOut.write(110);
/*     */             break;
/*     */           case 13:
/* 277 */             bOut.write(92);
/* 278 */             bOut.write(114);
/*     */             break;
/*     */           case 92:
/* 281 */             bOut.write(92);
/* 282 */             bOut.write(92);
/*     */             break;
/*     */           case 39:
/* 285 */             bOut.write(92);
/* 286 */             bOut.write(39);
/*     */             break;
/*     */           case 34:
/* 289 */             bOut.write(92);
/* 290 */             bOut.write(34);
/*     */             break;
/*     */           case 26:
/* 293 */             bOut.write(92);
/* 294 */             bOut.write(90);
/*     */             break;
/*     */           default:
/* 297 */             bOut.write(b);
/*     */             break;
/*     */         } 
/*     */       } 
/* 301 */       bOut.write(39);
/*     */       
/* 303 */       setValue(parameterIndex, bOut.toByteArray(), MysqlType.BINARY);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes) {
/* 309 */     byte[] parameterWithQuotes = StringUtils.quoteBytes(parameterAsBytes);
/*     */     
/* 311 */     setValue(parameterIndex, parameterWithQuotes, MysqlType.BINARY);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) {
/* 316 */     setValue(parameterIndex, parameterAsBytes, MysqlType.BINARY);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader) {
/* 321 */     setCharacterStream(parameterIndex, reader, -1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, int length) {
/*     */     try {
/* 327 */       if (reader == null) {
/* 328 */         setNull(parameterIndex);
/*     */       } else {
/* 330 */         char[] c = null;
/* 331 */         int len = 0;
/*     */         
/* 333 */         boolean useLength = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue();
/*     */         
/* 335 */         String forcedEncoding = this.session.getPropertySet().getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
/*     */         
/* 337 */         if (useLength && length != -1) {
/* 338 */           c = new char[length];
/*     */           
/* 340 */           int numCharsRead = Util.readFully(reader, c, length);
/*     */           
/* 342 */           if (forcedEncoding == null) {
/* 343 */             setString(parameterIndex, new String(c, 0, numCharsRead));
/*     */           } else {
/* 345 */             setBytes(parameterIndex, StringUtils.getBytes(new String(c, 0, numCharsRead), forcedEncoding));
/*     */           } 
/*     */         } else {
/* 348 */           c = new char[4096];
/*     */           
/* 350 */           StringBuilder buf = new StringBuilder();
/*     */           
/* 352 */           while ((len = reader.read(c)) != -1) {
/* 353 */             buf.append(c, 0, len);
/*     */           }
/*     */           
/* 356 */           if (forcedEncoding == null) {
/* 357 */             setString(parameterIndex, buf.toString());
/*     */           } else {
/* 359 */             setBytes(parameterIndex, StringUtils.getBytes(buf.toString(), forcedEncoding));
/*     */           } 
/*     */         } 
/*     */         
/* 363 */         this.bindValues[parameterIndex].setMysqlType(MysqlType.TEXT);
/*     */       } 
/* 365 */     } catch (UnsupportedEncodingException uec) {
/* 366 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, uec.toString(), uec, this.session.getExceptionInterceptor());
/* 367 */     } catch (IOException ioEx) {
/* 368 */       throw ExceptionFactory.createException(ioEx.toString(), ioEx, this.session.getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length) {
/* 374 */     setCharacterStream(parameterIndex, reader, (int)length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader) {
/* 379 */     setCharacterStream(parameterIndex, reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader, long length) {
/* 384 */     setCharacterStream(parameterIndex, reader, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setClob(int i, Clob x) {
/* 389 */     if (x == null) {
/* 390 */       setNull(i);
/*     */     } else {
/*     */       try {
/* 393 */         String forcedEncoding = this.session.getPropertySet().getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
/*     */         
/* 395 */         if (forcedEncoding == null) {
/* 396 */           setString(i, x.getSubString(1L, (int)x.length()));
/*     */         } else {
/* 398 */           setBytes(i, StringUtils.getBytes(x.getSubString(1L, (int)x.length()), forcedEncoding));
/*     */         } 
/*     */         
/* 401 */         this.bindValues[i].setMysqlType(MysqlType.TEXT);
/* 402 */       } catch (Throwable t) {
/* 403 */         throw ExceptionFactory.createException(t.getMessage(), t);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x) {
/* 410 */     setDate(parameterIndex, x, (Calendar)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDate(int parameterIndex, Date x, Calendar cal) {
/* 415 */     if (x == null) {
/* 416 */       setNull(parameterIndex);
/* 417 */     } else if (cal != null) {
/* 418 */       setValue(parameterIndex, TimeUtil.getSimpleDateFormat("''yyyy-MM-dd''", cal).format(x), MysqlType.DATE);
/*     */     } else {
/* 420 */       this.ddf = TimeUtil.getSimpleDateFormat(this.ddf, "''yyyy-MM-dd''", this.session.getServerSession().getDefaultTimeZone());
/* 421 */       setValue(parameterIndex, this.ddf.format(x), MysqlType.DATE);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDouble(int parameterIndex, double x) {
/* 427 */     if (!((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.allowNanAndInf).getValue()).booleanValue() && (x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY || 
/* 428 */       Double.isNaN(x))) {
/* 429 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.64", new Object[] { Double.valueOf(x) }), this.session
/* 430 */           .getExceptionInterceptor());
/*     */     }
/* 432 */     setValue(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)), MysqlType.DOUBLE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFloat(int parameterIndex, float x) {
/* 437 */     setValue(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)), MysqlType.FLOAT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInt(int parameterIndex, int x) {
/* 442 */     setValue(parameterIndex, String.valueOf(x), MysqlType.INT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLocalDate(int parameterIndex, LocalDate x, MysqlType targetMysqlType) {
/* 447 */     setValue(parameterIndex, "'" + x + "'", targetMysqlType);
/*     */   }
/*     */   
/*     */   public void setLocalTime(int parameterIndex, LocalTime x, MysqlType targetMysqlType) {
/*     */     StringBuilder sb;
/* 452 */     if (targetMysqlType == MysqlType.DATE) {
/* 453 */       setValue(parameterIndex, "'" + DEFAULT_DATE + "'", MysqlType.DATE);
/*     */       
/*     */       return;
/*     */     } 
/* 457 */     if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/* 458 */       if (x.getNano() > 0) {
/* 459 */         x = x.withNano(0);
/*     */       }
/*     */     } else {
/* 462 */       int fractLen = 6;
/* 463 */       if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0)
/*     */       {
/* 465 */         fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*     */       }
/*     */       
/* 468 */       x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
/*     */     } 
/*     */ 
/*     */     
/* 472 */     DateTimeFormatter formatter = (new DateTimeFormatterBuilder()).appendPattern("HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true).toFormatter();
/*     */     
/* 474 */     switch (targetMysqlType) {
/*     */       case TIME:
/* 476 */         sb = new StringBuilder("'");
/* 477 */         sb.append(x.format(formatter));
/* 478 */         sb.append("'");
/* 479 */         setValue(parameterIndex, sb.toString(), targetMysqlType);
/*     */         break;
/*     */       case DATETIME:
/*     */       case TIMESTAMP:
/* 483 */         sb = new StringBuilder("'");
/* 484 */         sb.append(DEFAULT_DATE);
/* 485 */         sb.append(" ");
/* 486 */         sb.append(x.format(formatter));
/* 487 */         sb.append("'");
/* 488 */         setValue(parameterIndex, sb.toString(), targetMysqlType);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDuration(int parameterIndex, Duration x, MysqlType targetMysqlType) {
/*     */     StringBuilder sb;
/* 497 */     if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/* 498 */       if (x.getNano() > 0) {
/* 499 */         x = x.isNegative() ? x.plusSeconds(1L).withNanos(0) : x.withNanos(0);
/*     */       }
/*     */     } else {
/* 502 */       int fractLen = 6;
/* 503 */       if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0)
/*     */       {
/* 505 */         fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*     */       }
/*     */       
/* 508 */       x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
/*     */     } 
/*     */     
/* 511 */     switch (targetMysqlType) {
/*     */       case TIME:
/* 513 */         sb = new StringBuilder("'");
/* 514 */         sb.append(TimeUtil.getDurationString(x));
/* 515 */         sb.append("'");
/* 516 */         setValue(parameterIndex, sb.toString(), targetMysqlType);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocalDateTime(int parameterIndex, LocalDateTime x, MysqlType targetMysqlType) {
/* 525 */     if (targetMysqlType == MysqlType.DATE) {
/* 526 */       setValue(parameterIndex, "'" + x.toLocalDate() + "'", MysqlType.DATE);
/*     */     } else {
/*     */       int fractLen; DateTimeFormatter formatter;
/*     */       StringBuilder sb;
/* 530 */       switch (targetMysqlType) {
/*     */         case CHAR:
/*     */         case VARCHAR:
/*     */         case TINYTEXT:
/*     */         case TEXT:
/*     */         case MEDIUMTEXT:
/*     */         case LONGTEXT:
/* 537 */           fractLen = 9;
/*     */           break;
/*     */         default:
/* 540 */           fractLen = 6;
/*     */           break;
/*     */       } 
/*     */       
/* 544 */       if (this.columnDefinition != null && parameterIndex <= (this.columnDefinition.getFields()).length && parameterIndex >= 0)
/*     */       {
/* 546 */         fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
/*     */       }
/*     */       
/* 549 */       if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !((Boolean)this.sendFractionalSeconds.getValue()).booleanValue()) {
/* 550 */         if (x.getNano() > 0) {
/* 551 */           x = x.withNano(0);
/*     */         }
/*     */       } else {
/* 554 */         x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
/*     */       } 
/*     */       
/* 557 */       switch (targetMysqlType) {
/*     */         
/*     */         case TIME:
/* 560 */           formatter = (new DateTimeFormatterBuilder()).appendPattern("HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true).toFormatter();
/* 561 */           sb = new StringBuilder("'");
/* 562 */           sb.append(x.toLocalTime().format(formatter));
/* 563 */           sb.append("'");
/* 564 */           setValue(parameterIndex, sb.toString(), targetMysqlType);
/*     */           break;
/*     */         
/*     */         case DATETIME:
/*     */         case TIMESTAMP:
/* 569 */           formatter = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true).toFormatter();
/* 570 */           sb = new StringBuilder("'");
/* 571 */           sb.append(x.format(formatter));
/* 572 */           sb.append("'");
/* 573 */           setValue(parameterIndex, sb.toString(), targetMysqlType);
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLong(int parameterIndex, long x) {
/* 583 */     setValue(parameterIndex, String.valueOf(x), MysqlType.BIGINT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value) {
/* 588 */     setNCharacterStream(parameterIndex, value, -1L);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader reader, long length) {
/* 593 */     if (reader == null) {
/* 594 */       setNull(parameterIndex);
/*     */     } else {
/*     */       try {
/* 597 */         char[] c = null;
/* 598 */         int len = 0;
/*     */         
/* 600 */         boolean useLength = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue();
/*     */ 
/*     */ 
/*     */         
/* 604 */         if (useLength && length != -1L) {
/* 605 */           c = new char[(int)length];
/*     */           
/* 607 */           int numCharsRead = Util.readFully(reader, c, (int)length);
/* 608 */           setNString(parameterIndex, new String(c, 0, numCharsRead));
/*     */         } else {
/*     */           
/* 611 */           c = new char[4096];
/*     */           
/* 613 */           StringBuilder buf = new StringBuilder();
/*     */           
/* 615 */           while ((len = reader.read(c)) != -1) {
/* 616 */             buf.append(c, 0, len);
/*     */           }
/*     */           
/* 619 */           setNString(parameterIndex, buf.toString());
/*     */         } 
/*     */         
/* 622 */         this.bindValues[parameterIndex].setMysqlType(MysqlType.TEXT);
/* 623 */       } catch (Throwable t) {
/* 624 */         throw ExceptionFactory.createException(t.getMessage(), t, this.session.getExceptionInterceptor());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader) {
/* 631 */     setNCharacterStream(parameterIndex, reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader, long length) {
/* 636 */     if (reader == null) {
/* 637 */       setNull(parameterIndex);
/*     */     } else {
/* 639 */       setNCharacterStream(parameterIndex, reader, length);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNClob(int parameterIndex, NClob value) {
/* 645 */     if (value == null) {
/* 646 */       setNull(parameterIndex);
/*     */     } else {
/*     */       try {
/* 649 */         setNCharacterStream(parameterIndex, value.getCharacterStream(), value.length());
/* 650 */       } catch (Throwable t) {
/* 651 */         throw ExceptionFactory.createException(t.getMessage(), t, this.session.getExceptionInterceptor());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNString(int parameterIndex, String x) {
/* 658 */     if (x == null) {
/* 659 */       setNull(parameterIndex);
/*     */     } else {
/* 661 */       if (this.charEncoding.equalsIgnoreCase("UTF-8") || this.charEncoding.equalsIgnoreCase("utf8")) {
/* 662 */         setString(parameterIndex, x);
/*     */         
/*     */         return;
/*     */       } 
/* 666 */       int stringLength = x.length();
/*     */ 
/*     */ 
/*     */       
/* 670 */       StringBuilder buf = new StringBuilder((int)(x.length() * 1.1D + 4.0D));
/* 671 */       buf.append("_utf8");
/* 672 */       buf.append('\'');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 678 */       for (int i = 0; i < stringLength; i++) {
/* 679 */         char c = x.charAt(i);
/*     */         
/* 681 */         switch (c) {
/*     */           case '\000':
/* 683 */             buf.append('\\');
/* 684 */             buf.append('0');
/*     */             break;
/*     */           case '\n':
/* 687 */             buf.append('\\');
/* 688 */             buf.append('n');
/*     */             break;
/*     */           case '\r':
/* 691 */             buf.append('\\');
/* 692 */             buf.append('r');
/*     */             break;
/*     */           case '\\':
/* 695 */             buf.append('\\');
/* 696 */             buf.append('\\');
/*     */             break;
/*     */           case '\'':
/* 699 */             buf.append('\\');
/* 700 */             buf.append('\'');
/*     */             break;
/*     */           case '"':
/* 703 */             if (this.session.getServerSession().useAnsiQuotedIdentifiers()) {
/* 704 */               buf.append('\\');
/*     */             }
/* 706 */             buf.append('"');
/*     */             break;
/*     */           case '\032':
/* 709 */             buf.append('\\');
/* 710 */             buf.append('Z');
/*     */             break;
/*     */           default:
/* 713 */             buf.append(c);
/*     */             break;
/*     */         } 
/*     */       } 
/* 717 */       buf.append('\'');
/*     */       
/* 719 */       byte[] parameterAsBytes = this.isLoadDataQuery ? StringUtils.getBytes(buf.toString()) : StringUtils.getBytes(buf.toString(), "UTF-8");
/*     */       
/* 721 */       setValue(parameterIndex, parameterAsBytes, MysqlType.VARCHAR);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setNull(int parameterIndex) {
/* 727 */     setValue(parameterIndex, "null", MysqlType.NULL);
/* 728 */     this.bindValues[parameterIndex].setNull(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setShort(int parameterIndex, short x) {
/* 733 */     setValue(parameterIndex, String.valueOf(x), MysqlType.SMALLINT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setString(int parameterIndex, String x) {
/* 738 */     if (x == null) {
/* 739 */       setNull(parameterIndex);
/*     */     } else {
/* 741 */       int stringLength = x.length();
/*     */       
/* 743 */       if (this.session.getServerSession().isNoBackslashEscapesSet()) {
/*     */ 
/*     */         
/* 746 */         boolean needsHexEscape = isEscapeNeededForString(x, stringLength);
/*     */         
/* 748 */         if (!needsHexEscape) {
/* 749 */           StringBuilder quotedString = new StringBuilder(x.length() + 2);
/* 750 */           quotedString.append('\'');
/* 751 */           quotedString.append(x);
/* 752 */           quotedString.append('\'');
/*     */ 
/*     */           
/* 755 */           byte[] arrayOfByte = this.isLoadDataQuery ? StringUtils.getBytes(quotedString.toString()) : StringUtils.getBytes(quotedString.toString(), this.charEncoding);
/* 756 */           setValue(parameterIndex, arrayOfByte, MysqlType.VARCHAR);
/*     */         } else {
/*     */           
/* 759 */           byte[] arrayOfByte = this.isLoadDataQuery ? StringUtils.getBytes(x) : StringUtils.getBytes(x, this.charEncoding);
/* 760 */           setBytes(parameterIndex, arrayOfByte);
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 766 */       String parameterAsString = x;
/* 767 */       boolean needsQuoted = true;
/*     */       
/* 769 */       if (this.isLoadDataQuery || isEscapeNeededForString(x, stringLength)) {
/* 770 */         needsQuoted = false;
/*     */         
/* 772 */         StringBuilder buf = new StringBuilder((int)(x.length() * 1.1D));
/*     */         
/* 774 */         buf.append('\'');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 780 */         for (int i = 0; i < stringLength; i++) {
/* 781 */           char c = x.charAt(i);
/*     */           
/* 783 */           switch (c) {
/*     */             case '\000':
/* 785 */               buf.append('\\');
/* 786 */               buf.append('0');
/*     */               break;
/*     */             case '\n':
/* 789 */               buf.append('\\');
/* 790 */               buf.append('n');
/*     */               break;
/*     */             case '\r':
/* 793 */               buf.append('\\');
/* 794 */               buf.append('r');
/*     */               break;
/*     */             case '\\':
/* 797 */               buf.append('\\');
/* 798 */               buf.append('\\');
/*     */               break;
/*     */             case '\'':
/* 801 */               buf.append('\'');
/* 802 */               buf.append('\'');
/*     */               break;
/*     */             case '"':
/* 805 */               if (this.session.getServerSession().useAnsiQuotedIdentifiers()) {
/* 806 */                 buf.append('\\');
/*     */               }
/* 808 */               buf.append('"');
/*     */               break;
/*     */             case '\032':
/* 811 */               buf.append('\\');
/* 812 */               buf.append('Z');
/*     */               break;
/*     */             
/*     */             case '¥':
/*     */             case '₩':
/* 817 */               if (this.charsetEncoder != null) {
/* 818 */                 CharBuffer cbuf = CharBuffer.allocate(1);
/* 819 */                 ByteBuffer bbuf = ByteBuffer.allocate(1);
/* 820 */                 cbuf.put(c);
/* 821 */                 cbuf.position(0);
/* 822 */                 this.charsetEncoder.encode(cbuf, bbuf, true);
/* 823 */                 if (bbuf.get(0) == 92) {
/* 824 */                   buf.append('\\');
/*     */                 }
/*     */               } 
/* 827 */               buf.append(c);
/*     */               break;
/*     */             
/*     */             default:
/* 831 */               buf.append(c);
/*     */               break;
/*     */           } 
/*     */         } 
/* 835 */         buf.append('\'');
/*     */         
/* 837 */         parameterAsString = buf.toString();
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 842 */       byte[] parameterAsBytes = this.isLoadDataQuery ? StringUtils.getBytes(parameterAsString) : (needsQuoted ? StringUtils.getBytesWrapped(parameterAsString, '\'', '\'', this.charEncoding) : StringUtils.getBytes(parameterAsString, this.charEncoding));
/*     */       
/* 844 */       setValue(parameterIndex, parameterAsBytes, MysqlType.VARCHAR);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isEscapeNeededForString(String x, int stringLength) {
/* 849 */     boolean needsHexEscape = false;
/*     */     
/* 851 */     for (int i = 0; i < stringLength; i++) {
/* 852 */       char c = x.charAt(i);
/*     */       
/* 854 */       switch (c) {
/*     */         case '\000':
/*     */         case '\n':
/*     */         case '\r':
/*     */         case '\032':
/*     */         case '"':
/*     */         case '\'':
/*     */         case '\\':
/* 862 */           needsHexEscape = true;
/*     */           break;
/*     */       } 
/*     */       
/* 866 */       if (needsHexEscape) {
/*     */         break;
/*     */       }
/*     */     } 
/* 870 */     return needsHexEscape;
/*     */   }
/*     */   
/*     */   public void setTime(int parameterIndex, Time x, Calendar cal) {
/* 874 */     if (x == null) {
/* 875 */       setNull(parameterIndex);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 880 */     String formatStr = (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && ((Boolean)this.sendFractionalSeconds.getValue()).booleanValue() && ((Boolean)this.sendFractionalSecondsForTime.getValue()).booleanValue() && TimeUtil.hasFractionalSeconds(x).booleanValue()) ? "''HH:mm:ss.SSS''" : "''HH:mm:ss''";
/*     */     
/* 882 */     if (cal != null) {
/* 883 */       setValue(parameterIndex, TimeUtil.getSimpleDateFormat(formatStr, cal).format(x), MysqlType.TIME);
/*     */     } else {
/* 885 */       this.tdf = TimeUtil.getSimpleDateFormat(this.tdf, formatStr, this.session.getServerSession().getDefaultTimeZone());
/* 886 */       setValue(parameterIndex, this.tdf.format(x), MysqlType.TIME);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setTime(int parameterIndex, Time x) {
/* 891 */     setTime(parameterIndex, x, (Calendar)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void bindTimestamp(int parameterIndex, Timestamp x, Calendar targetCalendar, int fractionalLength, MysqlType targetMysqlType) {
/* 896 */     if (fractionalLength < 0)
/*     */     {
/* 898 */       fractionalLength = 6;
/*     */     }
/*     */     
/* 901 */     x = TimeUtil.adjustNanosPrecision(x, fractionalLength, !this.session.getServerSession().isServerTruncatesFracSecs());
/*     */     
/* 903 */     StringBuffer buf = new StringBuffer();
/*     */     
/* 905 */     if (targetCalendar != null) {
/* 906 */       buf.append(TimeUtil.getSimpleDateFormat("''yyyy-MM-dd HH:mm:ss", targetCalendar).format(x));
/*     */     } else {
/* 908 */       this.tsdf = TimeUtil.getSimpleDateFormat(this.tsdf, "''yyyy-MM-dd HH:mm:ss", (targetMysqlType == MysqlType.TIMESTAMP && ((Boolean)this.preserveInstants
/* 909 */           .getValue()).booleanValue()) ? this.session.getServerSession().getSessionTimeZone() : this.session
/* 910 */           .getServerSession().getDefaultTimeZone());
/* 911 */       buf.append(this.tsdf.format(x));
/*     */     } 
/*     */     
/* 914 */     if (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && x.getNanos() > 0) {
/* 915 */       buf.append('.');
/* 916 */       buf.append(TimeUtil.formatNanos(x.getNanos(), 6));
/*     */     } 
/* 918 */     buf.append('\'');
/*     */     
/* 920 */     setValue(parameterIndex, buf.toString(), targetMysqlType);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ClientPreparedQueryBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */