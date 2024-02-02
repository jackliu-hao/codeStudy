/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.a.ColumnDefinitionFactory;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativeMessageBuilder;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.ValueEncoder;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.sql.Blob;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerPreparedQuery
/*     */   extends AbstractPreparedQuery<ServerPreparedQueryBindings>
/*     */ {
/*     */   public static final int BLOB_STREAM_READ_BUF_SIZE = 8192;
/*     */   public static final byte OPEN_CURSOR_FLAG = 1;
/*     */   public static final byte PARAMETER_COUNT_AVAILABLE = 8;
/*     */   private long serverStatementId;
/*     */   private Field[] parameterFields;
/*     */   private ColumnDefinition resultFields;
/*     */   protected boolean profileSQL = false;
/*     */   protected boolean gatherPerfMetrics;
/*     */   protected boolean logSlowQueries = false;
/*     */   private boolean useAutoSlowLog;
/*     */   protected RuntimeProperty<Integer> slowQueryThresholdMillis;
/*     */   protected RuntimeProperty<Boolean> explainSlowQueries;
/*     */   protected boolean useCursorFetch = false;
/*     */   protected boolean queryWasSlow = false;
/*  94 */   protected NativeMessageBuilder commandBuilder = null;
/*     */   
/*     */   public static ServerPreparedQuery getInstance(NativeSession sess) {
/*  97 */     if (((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.autoGenerateTestcaseScript).getValue()).booleanValue()) {
/*  98 */       return new ServerPreparedQueryTestcaseGenerator(sess);
/*     */     }
/* 100 */     return new ServerPreparedQuery(sess);
/*     */   }
/*     */   
/*     */   protected ServerPreparedQuery(NativeSession sess) {
/* 104 */     super(sess);
/* 105 */     this.profileSQL = ((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.profileSQL).getValue()).booleanValue();
/* 106 */     this.gatherPerfMetrics = ((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.gatherPerfMetrics).getValue()).booleanValue();
/* 107 */     this.logSlowQueries = ((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.logSlowQueries).getValue()).booleanValue();
/* 108 */     this.useAutoSlowLog = ((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.autoSlowLog).getValue()).booleanValue();
/* 109 */     this.slowQueryThresholdMillis = sess.getPropertySet().getIntegerProperty(PropertyKey.slowQueryThresholdMillis);
/* 110 */     this.explainSlowQueries = sess.getPropertySet().getBooleanProperty(PropertyKey.explainSlowQueries);
/* 111 */     this.useCursorFetch = ((Boolean)sess.getPropertySet().getBooleanProperty(PropertyKey.useCursorFetch).getValue()).booleanValue();
/* 112 */     this.commandBuilder = new NativeMessageBuilder(this.session.getServerSession().supportsQueryAttributes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void serverPrepare(String sql) throws IOException {
/* 123 */     this.session.checkClosed();
/*     */     
/* 125 */     synchronized (this.session) {
/* 126 */       long begin = this.profileSQL ? System.currentTimeMillis() : 0L;
/*     */       
/* 128 */       boolean loadDataQuery = StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA");
/*     */       
/* 130 */       String characterEncoding = null;
/* 131 */       String connectionEncoding = (String)this.session.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
/*     */       
/* 133 */       if (!loadDataQuery && connectionEncoding != null) {
/* 134 */         characterEncoding = connectionEncoding;
/*     */       }
/*     */ 
/*     */       
/* 138 */       NativePacketPayload prepareResultPacket = this.session.sendCommand(this.commandBuilder.buildComStmtPrepare(this.session.getSharedSendPacket(), sql, characterEncoding), false, 0);
/*     */ 
/*     */       
/* 141 */       prepareResultPacket.setPosition(1);
/*     */       
/* 143 */       this.serverStatementId = prepareResultPacket.readInteger(NativeConstants.IntegerDataType.INT4);
/* 144 */       int fieldCount = (int)prepareResultPacket.readInteger(NativeConstants.IntegerDataType.INT2);
/* 145 */       setParameterCount((int)prepareResultPacket.readInteger(NativeConstants.IntegerDataType.INT2));
/*     */       
/* 147 */       this.queryBindings = new ServerPreparedQueryBindings(this.parameterCount, this.session);
/* 148 */       this.queryBindings.setLoadDataQuery(loadDataQuery);
/*     */       
/* 150 */       if (this.gatherPerfMetrics) {
/* 151 */         this.session.getProtocol().getMetricsHolder().incrementNumberOfPrepares();
/*     */       }
/*     */       
/* 154 */       if (this.profileSQL) {
/* 155 */         this.session.getProfilerEventHandler().processEvent((byte)2, this.session, this, null, this.session
/* 156 */             .getCurrentTimeNanosOrMillis() - begin, new Throwable(), truncateQueryToLog(sql));
/*     */       }
/*     */       
/* 159 */       boolean checkEOF = !this.session.getServerSession().isEOFDeprecated();
/*     */       
/* 161 */       if (this.parameterCount > 0) {
/* 162 */         if (checkEOF) {
/* 163 */           this.session.getProtocol().skipPacket();
/*     */         }
/*     */         
/* 166 */         this
/* 167 */           .parameterFields = ((ColumnDefinition)this.session.getProtocol().read(ColumnDefinition.class, (ProtocolEntityFactory)new ColumnDefinitionFactory(this.parameterCount, null))).getFields();
/*     */       } 
/*     */ 
/*     */       
/* 171 */       if (fieldCount > 0) {
/* 172 */         this.resultFields = (ColumnDefinition)this.session.getProtocol().read(ColumnDefinition.class, (ProtocolEntityFactory)new ColumnDefinitionFactory(fieldCount, null));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void statementBegins() {
/* 179 */     super.statementBegins();
/* 180 */     this.queryWasSlow = false;
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
/*     */   public <T extends Resultset> T serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet, ColumnDefinition metadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) {
/* 198 */     if (this.session.shouldIntercept()) {
/* 199 */       T interceptedResults = this.session.invokeQueryInterceptorsPre(() -> getOriginalSql(), this, true);
/*     */ 
/*     */ 
/*     */       
/* 203 */       if (interceptedResults != null) {
/* 204 */         return interceptedResults;
/*     */       }
/*     */     } 
/* 207 */     String queryAsString = (this.profileSQL || this.logSlowQueries || this.gatherPerfMetrics) ? asSql(true) : "";
/*     */     
/* 209 */     NativePacketPayload packet = prepareExecutePacket();
/* 210 */     NativePacketPayload resPacket = sendExecutePacket(packet, queryAsString);
/* 211 */     T rs = readExecuteResult(resPacket, maxRowsToRetrieve, createStreamingResultSet, metadata, resultSetFactory, queryAsString);
/*     */     
/* 213 */     return rs;
/*     */   }
/*     */   
/*     */   public NativePacketPayload prepareExecutePacket() {
/* 217 */     ServerPreparedQueryBindValue[] parameterBindings = this.queryBindings.getBindValues();
/*     */     
/* 219 */     if (this.queryBindings.isLongParameterSwitchDetected()) {
/*     */       
/* 221 */       boolean firstFound = false;
/* 222 */       long boundTimeToCheck = 0L;
/*     */       
/* 224 */       for (int j = 0; j < this.parameterCount - 1; j++) {
/* 225 */         if (parameterBindings[j].isStream()) {
/* 226 */           if (firstFound && boundTimeToCheck != (parameterBindings[j]).boundBeforeExecutionNum) {
/* 227 */             throw ExceptionFactory.createException(
/* 228 */                 Messages.getString("ServerPreparedStatement.11") + Messages.getString("ServerPreparedStatement.12"), "S1C00", 0, true, null, this.session
/* 229 */                 .getExceptionInterceptor());
/*     */           }
/* 231 */           firstFound = true;
/* 232 */           boundTimeToCheck = (parameterBindings[j]).boundBeforeExecutionNum;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 237 */       serverResetStatement();
/*     */     } 
/*     */     
/* 240 */     this.queryBindings.checkAllParametersSet();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 245 */     for (int i = 0; i < this.parameterCount; i++) {
/* 246 */       if (parameterBindings[i].isStream()) {
/* 247 */         serverLongData(i, parameterBindings[i]);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 254 */     NativePacketPayload packet = this.session.getSharedSendPacket();
/*     */     
/* 256 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, 23L);
/* 257 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, this.serverStatementId);
/*     */     
/* 259 */     boolean supportsQueryAttributes = this.session.getServerSession().supportsQueryAttributes();
/* 260 */     boolean sendQueryAttributes = false;
/* 261 */     if (supportsQueryAttributes) {
/*     */       
/* 263 */       sendQueryAttributes = this.session.getServerSession().getServerVersion().meetsMinimum(new ServerVersion(8, 0, 26));
/* 264 */     } else if (this.queryAttributesBindings.getCount() > 0) {
/* 265 */       this.session.getLog().logWarn(Messages.getString("QueryAttributes.SetButNotSupported"));
/*     */     } 
/*     */     
/* 268 */     byte flags = 0;
/* 269 */     if (this.resultFields != null && this.resultFields.getFields() != null && this.useCursorFetch && this.resultSetType == Resultset.Type.FORWARD_ONLY && this.fetchSize > 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 276 */       flags = (byte)(flags | 0x1);
/*     */     }
/* 278 */     if (sendQueryAttributes) {
/* 279 */       flags = (byte)(flags | 0x8);
/*     */     }
/* 281 */     packet.writeInteger(NativeConstants.IntegerDataType.INT1, flags);
/*     */     
/* 283 */     packet.writeInteger(NativeConstants.IntegerDataType.INT4, 1L);
/*     */     
/* 285 */     int parametersAndAttributesCount = this.parameterCount;
/* 286 */     if (supportsQueryAttributes) {
/* 287 */       if (sendQueryAttributes) {
/* 288 */         parametersAndAttributesCount += this.queryAttributesBindings.getCount();
/*     */       }
/* 290 */       if (sendQueryAttributes || parametersAndAttributesCount > 0)
/*     */       {
/* 292 */         packet.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, parametersAndAttributesCount);
/*     */       }
/*     */     } 
/*     */     
/* 296 */     if (parametersAndAttributesCount > 0) {
/*     */       
/* 298 */       int nullCount = (parametersAndAttributesCount + 7) / 8;
/*     */       
/* 300 */       int nullBitsPosition = packet.getPosition();
/*     */       
/* 302 */       for (int j = 0; j < nullCount; j++) {
/* 303 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/*     */       }
/*     */       
/* 306 */       byte[] nullBitsBuffer = new byte[nullCount];
/*     */ 
/*     */       
/* 309 */       if (this.queryBindings.getSendTypesToServer().get() || (sendQueryAttributes && this.queryAttributesBindings.getCount() > 0)) {
/* 310 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 1L);
/*     */ 
/*     */         
/* 313 */         for (int m = 0; m < this.parameterCount; m++) {
/* 314 */           packet.writeInteger(NativeConstants.IntegerDataType.INT2, (parameterBindings[m]).bufferType);
/* 315 */           if (supportsQueryAttributes) {
/* 316 */             packet.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, "".getBytes());
/*     */           }
/*     */         } 
/*     */         
/* 320 */         if (sendQueryAttributes) {
/* 321 */           this.queryAttributesBindings.runThroughAll(a -> {
/*     */                 packet.writeInteger(NativeConstants.IntegerDataType.INT2, a.getType());
/*     */                 packet.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, a.getName().getBytes());
/*     */               });
/*     */         }
/*     */       } else {
/* 327 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 0L);
/*     */       } 
/*     */       
/*     */       int k;
/* 331 */       for (k = 0; k < this.parameterCount; k++) {
/* 332 */         if (!parameterBindings[k].isStream()) {
/* 333 */           if (!parameterBindings[k].isNull()) {
/* 334 */             parameterBindings[k].storeBinding(packet, this.queryBindings.isLoadDataQuery(), this.charEncoding, this.session
/* 335 */                 .getExceptionInterceptor());
/*     */           } else {
/* 337 */             nullBitsBuffer[k >>> 3] = (byte)(nullBitsBuffer[k >>> 3] | 1 << (k & 0x7));
/*     */           } 
/*     */         }
/*     */       } 
/*     */       
/* 342 */       if (sendQueryAttributes) {
/* 343 */         for (k = 0; k < this.queryAttributesBindings.getCount(); k++) {
/* 344 */           if (this.queryAttributesBindings.getAttributeValue(k).isNull()) {
/* 345 */             int b = k + this.parameterCount;
/* 346 */             nullBitsBuffer[b >>> 3] = (byte)(nullBitsBuffer[b >>> 3] | 1 << (b & 0x7));
/*     */           } 
/*     */         } 
/* 349 */         ValueEncoder valueEncoder = new ValueEncoder(packet, this.charEncoding, this.session.getServerSession().getDefaultTimeZone());
/* 350 */         this.queryAttributesBindings.runThroughAll(a -> valueEncoder.encodeValue(a.getValue(), a.getType()));
/*     */       } 
/*     */ 
/*     */       
/* 354 */       int endPosition = packet.getPosition();
/* 355 */       packet.setPosition(nullBitsPosition);
/* 356 */       packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, nullBitsBuffer);
/* 357 */       packet.setPosition(endPosition);
/*     */     } 
/*     */     
/* 360 */     return packet;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload sendExecutePacket(NativePacketPayload packet, String queryAsString) {
/* 365 */     long begin = this.session.getCurrentTimeNanosOrMillis();
/*     */     
/* 367 */     resetCancelledState();
/*     */     
/* 369 */     CancelQueryTask timeoutTask = null;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 374 */       timeoutTask = startQueryTimer(this, this.timeoutInMillis);
/*     */       
/* 376 */       statementBegins();
/*     */       
/* 378 */       NativePacketPayload resultPacket = this.session.sendCommand(packet, false, 0);
/*     */       
/* 380 */       long queryEndTime = this.session.getCurrentTimeNanosOrMillis();
/*     */       
/* 382 */       if (timeoutTask != null) {
/* 383 */         stopQueryTimer(timeoutTask, true, true);
/* 384 */         timeoutTask = null;
/*     */       } 
/*     */       
/* 387 */       long executeTime = queryEndTime - begin;
/* 388 */       setExecuteTime(executeTime);
/*     */       
/* 390 */       if (this.logSlowQueries) {
/* 391 */         this
/*     */           
/* 393 */           .queryWasSlow = this.useAutoSlowLog ? this.session.getProtocol().getMetricsHolder().checkAbonormallyLongQuery(executeTime) : ((executeTime > ((Integer)this.slowQueryThresholdMillis.getValue()).intValue()));
/*     */         
/* 395 */         if (this.queryWasSlow) {
/* 396 */           this.session.getProfilerEventHandler().processEvent((byte)6, this.session, this, null, executeTime, new Throwable(), 
/* 397 */               Messages.getString("ServerPreparedStatement.15", (Object[])new String[] { String.valueOf(this.session.getSlowQueryThreshold()), 
/* 398 */                   String.valueOf(executeTime), this.originalSql, queryAsString }));
/*     */         }
/*     */       } 
/*     */       
/* 402 */       if (this.gatherPerfMetrics) {
/* 403 */         this.session.getProtocol().getMetricsHolder().registerQueryExecutionTime(executeTime);
/* 404 */         this.session.getProtocol().getMetricsHolder().incrementNumberOfPreparedExecutes();
/*     */       } 
/*     */       
/* 407 */       if (this.profileSQL) {
/* 408 */         this.session.getProfilerEventHandler().processEvent((byte)4, this.session, this, null, executeTime, new Throwable(), 
/* 409 */             truncateQueryToLog(queryAsString));
/*     */       }
/*     */       
/* 412 */       return resultPacket;
/*     */     }
/* 414 */     catch (CJException sqlEx) {
/* 415 */       if (this.session.shouldIntercept()) {
/* 416 */         this.session.invokeQueryInterceptorsPost(() -> getOriginalSql(), this, (Resultset)null, true);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 421 */       throw sqlEx;
/*     */     } finally {
/* 423 */       this.statementExecuting.set(false);
/* 424 */       stopQueryTimer(timeoutTask, false, false);
/*     */     } 
/*     */   }
/*     */   
/*     */   public <T extends Resultset> T readExecuteResult(NativePacketPayload resultPacket, int maxRowsToRetrieve, boolean createStreamingResultSet, ColumnDefinition metadata, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory, String queryAsString) {
/*     */     try {
/*     */       T t;
/* 431 */       long fetchStartTime = this.profileSQL ? this.session.getCurrentTimeNanosOrMillis() : 0L;
/*     */       
/* 433 */       Resultset resultset = this.session.getProtocol().readAllResults(maxRowsToRetrieve, createStreamingResultSet, resultPacket, true, (metadata != null) ? metadata : this.resultFields, resultSetFactory);
/*     */ 
/*     */       
/* 436 */       if (this.session.shouldIntercept()) {
/* 437 */         T interceptedResults = this.session.invokeQueryInterceptorsPost(() -> getOriginalSql(), this, (T)resultset, true);
/*     */ 
/*     */ 
/*     */         
/* 441 */         if (interceptedResults != null) {
/* 442 */           t = interceptedResults;
/*     */         }
/*     */       } 
/*     */       
/* 446 */       if (this.profileSQL) {
/* 447 */         this.session.getProfilerEventHandler().processEvent((byte)5, this.session, this, (Resultset)t, this.session
/* 448 */             .getCurrentTimeNanosOrMillis() - fetchStartTime, new Throwable(), null);
/*     */       }
/*     */       
/* 451 */       if (this.queryWasSlow && ((Boolean)this.explainSlowQueries.getValue()).booleanValue()) {
/* 452 */         this.session.getProtocol().explainSlowQuery(queryAsString, queryAsString);
/*     */       }
/*     */       
/* 455 */       this.queryBindings.getSendTypesToServer().set(false);
/*     */       
/* 457 */       if (this.session.hadWarnings()) {
/* 458 */         this.session.getProtocol().scanForAndThrowDataTruncation();
/*     */       }
/*     */       
/* 461 */       return t;
/* 462 */     } catch (IOException ioEx) {
/* 463 */       throw ExceptionFactory.createCommunicationsException(this.session.getPropertySet(), this.session.getServerSession(), this.session
/* 464 */           .getProtocol().getPacketSentTimeHolder(), this.session.getProtocol().getPacketReceivedTimeHolder(), ioEx, this.session
/* 465 */           .getExceptionInterceptor());
/* 466 */     } catch (CJException sqlEx) {
/* 467 */       if (this.session.shouldIntercept()) {
/* 468 */         this.session.invokeQueryInterceptorsPost(() -> getOriginalSql(), this, (Resultset)null, true);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 473 */       throw sqlEx;
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void serverLongData(int parameterIndex, ServerPreparedQueryBindValue longData) {
/* 502 */     synchronized (this) {
/* 503 */       NativePacketPayload packet = this.session.getSharedSendPacket();
/*     */       
/* 505 */       Object value = longData.value;
/*     */       
/* 507 */       if (value instanceof byte[]) {
/* 508 */         this.session.sendCommand(this.commandBuilder.buildComStmtSendLongData(packet, this.serverStatementId, parameterIndex, (byte[])value), true, 0);
/* 509 */       } else if (value instanceof InputStream) {
/* 510 */         storeStream(parameterIndex, packet, (InputStream)value);
/* 511 */       } else if (value instanceof Blob) {
/*     */         try {
/* 513 */           storeStream(parameterIndex, packet, ((Blob)value).getBinaryStream());
/* 514 */         } catch (Throwable t) {
/* 515 */           throw ExceptionFactory.createException(t.getMessage(), this.session.getExceptionInterceptor());
/*     */         } 
/* 517 */       } else if (value instanceof Reader) {
/* 518 */         storeReader(parameterIndex, packet, (Reader)value);
/*     */       } else {
/* 520 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 521 */             Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", this.session.getExceptionInterceptor());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeQuery() {
/* 528 */     this.queryBindings = null;
/* 529 */     this.parameterFields = null;
/* 530 */     this.resultFields = null;
/* 531 */     super.closeQuery();
/*     */   }
/*     */   
/*     */   public long getServerStatementId() {
/* 535 */     return this.serverStatementId;
/*     */   }
/*     */   
/*     */   public void setServerStatementId(long serverStatementId) {
/* 539 */     this.serverStatementId = serverStatementId;
/*     */   }
/*     */   
/*     */   public Field[] getParameterFields() {
/* 543 */     return this.parameterFields;
/*     */   }
/*     */   
/*     */   public void setParameterFields(Field[] parameterFields) {
/* 547 */     this.parameterFields = parameterFields;
/*     */   }
/*     */   
/*     */   public ColumnDefinition getResultFields() {
/* 551 */     return this.resultFields;
/*     */   }
/*     */   
/*     */   public void setResultFields(ColumnDefinition resultFields) {
/* 555 */     this.resultFields = resultFields;
/*     */   }
/*     */   
/*     */   public void storeStream(int parameterIndex, NativePacketPayload packet, InputStream inStream) {
/* 559 */     this.session.checkClosed();
/* 560 */     synchronized (this.session) {
/* 561 */       byte[] buf = new byte[8192];
/*     */       
/* 563 */       int numRead = 0;
/*     */       
/*     */       try {
/* 566 */         int bytesInPacket = 0;
/* 567 */         int totalBytesRead = 0;
/* 568 */         int bytesReadAtLastSend = 0;
/* 569 */         int packetIsFullAt = ((Integer)this.session.getPropertySet().getMemorySizeProperty(PropertyKey.blobSendChunkSize).getValue()).intValue();
/*     */         
/* 571 */         packet.setPosition(0);
/* 572 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 24L);
/* 573 */         packet.writeInteger(NativeConstants.IntegerDataType.INT4, this.serverStatementId);
/* 574 */         packet.writeInteger(NativeConstants.IntegerDataType.INT2, parameterIndex);
/*     */         
/* 576 */         boolean readAny = false;
/*     */         
/* 578 */         while ((numRead = inStream.read(buf)) != -1) {
/*     */           
/* 580 */           readAny = true;
/*     */           
/* 582 */           packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, 0, numRead);
/* 583 */           bytesInPacket += numRead;
/* 584 */           totalBytesRead += numRead;
/*     */           
/* 586 */           if (bytesInPacket >= packetIsFullAt) {
/* 587 */             bytesReadAtLastSend = totalBytesRead;
/*     */             
/* 589 */             this.session.sendCommand(packet, true, 0);
/*     */             
/* 591 */             bytesInPacket = 0;
/* 592 */             packet.setPosition(0);
/* 593 */             packet.writeInteger(NativeConstants.IntegerDataType.INT1, 24L);
/* 594 */             packet.writeInteger(NativeConstants.IntegerDataType.INT4, this.serverStatementId);
/* 595 */             packet.writeInteger(NativeConstants.IntegerDataType.INT2, parameterIndex);
/*     */           } 
/*     */         } 
/*     */         
/* 599 */         if (totalBytesRead != bytesReadAtLastSend) {
/* 600 */           this.session.sendCommand(packet, true, 0);
/*     */         }
/*     */         
/* 603 */         if (!readAny) {
/* 604 */           this.session.sendCommand(packet, true, 0);
/*     */         }
/* 606 */       } catch (IOException ioEx) {
/* 607 */         throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.25") + ioEx.toString(), ioEx, this.session
/* 608 */             .getExceptionInterceptor());
/*     */       } finally {
/* 610 */         if (((Boolean)this.autoClosePStmtStreams.getValue()).booleanValue() && 
/* 611 */           inStream != null) {
/*     */           try {
/* 613 */             inStream.close();
/* 614 */           } catch (IOException iOException) {}
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void storeReader(int parameterIndex, NativePacketPayload packet, Reader inStream) {
/* 625 */     this.session.checkClosed();
/* 626 */     synchronized (this.session) {
/* 627 */       String forcedEncoding = this.session.getPropertySet().getStringProperty(PropertyKey.clobCharacterEncoding).getStringValue();
/*     */       
/* 629 */       String clobEncoding = (forcedEncoding == null) ? (String)this.session.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue() : forcedEncoding;
/*     */ 
/*     */       
/* 632 */       int maxBytesChar = 2;
/*     */       
/* 634 */       if (clobEncoding != null) {
/* 635 */         if (!clobEncoding.equals("UTF-16")) {
/* 636 */           maxBytesChar = this.session.getServerSession().getCharsetSettings().getMaxBytesPerChar(clobEncoding);
/*     */           
/* 638 */           if (maxBytesChar == 1) {
/* 639 */             maxBytesChar = 2;
/*     */           }
/*     */         } else {
/* 642 */           maxBytesChar = 4;
/*     */         } 
/*     */       }
/*     */       
/* 646 */       char[] buf = new char[8192 / maxBytesChar];
/*     */       
/* 648 */       int numRead = 0;
/*     */       
/* 650 */       int bytesInPacket = 0;
/* 651 */       int totalBytesRead = 0;
/* 652 */       int bytesReadAtLastSend = 0;
/* 653 */       int packetIsFullAt = ((Integer)this.session.getPropertySet().getMemorySizeProperty(PropertyKey.blobSendChunkSize).getValue()).intValue();
/*     */       
/*     */       try {
/* 656 */         packet.setPosition(0);
/* 657 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 24L);
/* 658 */         packet.writeInteger(NativeConstants.IntegerDataType.INT4, this.serverStatementId);
/* 659 */         packet.writeInteger(NativeConstants.IntegerDataType.INT2, parameterIndex);
/*     */         
/* 661 */         boolean readAny = false;
/*     */         
/* 663 */         while ((numRead = inStream.read(buf)) != -1) {
/* 664 */           readAny = true;
/*     */           
/* 666 */           byte[] valueAsBytes = StringUtils.getBytes(buf, 0, numRead, clobEncoding);
/*     */           
/* 668 */           packet.writeBytes(NativeConstants.StringSelfDataType.STRING_EOF, valueAsBytes);
/*     */           
/* 670 */           bytesInPacket += valueAsBytes.length;
/* 671 */           totalBytesRead += valueAsBytes.length;
/*     */           
/* 673 */           if (bytesInPacket >= packetIsFullAt) {
/* 674 */             bytesReadAtLastSend = totalBytesRead;
/*     */             
/* 676 */             this.session.sendCommand(packet, true, 0);
/*     */             
/* 678 */             bytesInPacket = 0;
/* 679 */             packet.setPosition(0);
/* 680 */             packet.writeInteger(NativeConstants.IntegerDataType.INT1, 24L);
/* 681 */             packet.writeInteger(NativeConstants.IntegerDataType.INT4, this.serverStatementId);
/* 682 */             packet.writeInteger(NativeConstants.IntegerDataType.INT2, parameterIndex);
/*     */           } 
/*     */         } 
/*     */         
/* 686 */         if (totalBytesRead != bytesReadAtLastSend) {
/* 687 */           this.session.sendCommand(packet, true, 0);
/*     */         }
/*     */         
/* 690 */         if (!readAny) {
/* 691 */           this.session.sendCommand(packet, true, 0);
/*     */         }
/* 693 */       } catch (IOException ioEx) {
/* 694 */         throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.24") + ioEx.toString(), ioEx, this.session
/* 695 */             .getExceptionInterceptor());
/*     */       } finally {
/* 697 */         if (((Boolean)this.autoClosePStmtStreams.getValue()).booleanValue() && 
/* 698 */           inStream != null) {
/*     */           try {
/* 700 */             inStream.close();
/* 701 */           } catch (IOException iOException) {}
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearParameters(boolean clearServerParameters) {
/* 715 */     boolean hadLongData = false;
/*     */     
/* 717 */     if (this.queryBindings != null) {
/* 718 */       hadLongData = this.queryBindings.clearBindValues();
/* 719 */       this.queryBindings.setLongParameterSwitchDetected(!(clearServerParameters && hadLongData));
/*     */     } 
/*     */     
/* 722 */     if (clearServerParameters && hadLongData) {
/* 723 */       serverResetStatement();
/*     */     }
/*     */   }
/*     */   
/*     */   public void serverResetStatement() {
/* 728 */     this.session.checkClosed();
/* 729 */     synchronized (this.session) {
/*     */       try {
/* 731 */         this.session.sendCommand(this.commandBuilder.buildComStmtReset(this.session.getSharedSendPacket(), this.serverStatementId), false, 0);
/*     */       } finally {
/* 733 */         this.session.clearInputStream();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) {
/* 744 */     boolean supportsQueryAttributes = this.session.getServerSession().supportsQueryAttributes();
/*     */     
/* 746 */     long maxSizeOfParameterSet = 0L;
/* 747 */     long sizeOfEntireBatch = 11L;
/* 748 */     if (supportsQueryAttributes) {
/* 749 */       sizeOfEntireBatch += 9L;
/* 750 */       sizeOfEntireBatch += ((this.queryAttributesBindings.getCount() + 7) / 8);
/* 751 */       for (int j = 0; j < this.queryAttributesBindings.getCount(); j++) {
/* 752 */         QueryAttributesBindValue queryAttribute = this.queryAttributesBindings.getAttributeValue(j);
/* 753 */         sizeOfEntireBatch += (2 + queryAttribute.getName().length()) + queryAttribute.getBoundLength();
/*     */       } 
/*     */     } 
/*     */     
/* 757 */     for (int i = 0; i < numBatchedArgs; i++) {
/* 758 */       ServerPreparedQueryBindValue[] paramArg = ((ServerPreparedQueryBindings)this.batchedArgs.get(i)).getBindValues();
/*     */       
/* 760 */       long sizeOfParameterSet = ((this.parameterCount + 7) / 8 + this.parameterCount * 2);
/*     */       
/* 762 */       if (supportsQueryAttributes) {
/* 763 */         sizeOfParameterSet += this.parameterCount;
/*     */       }
/*     */       
/* 766 */       ServerPreparedQueryBindValue[] parameterBindings = this.queryBindings.getBindValues();
/* 767 */       for (int j = 0; j < parameterBindings.length; j++) {
/* 768 */         if (!paramArg[j].isNull()) {
/* 769 */           long size = paramArg[j].getBoundLength();
/* 770 */           if (paramArg[j].isStream()) {
/* 771 */             if (size != -1L) {
/* 772 */               sizeOfParameterSet += size;
/*     */             }
/*     */           } else {
/* 775 */             sizeOfParameterSet += size;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 780 */       sizeOfEntireBatch += sizeOfParameterSet;
/*     */       
/* 782 */       if (sizeOfParameterSet > maxSizeOfParameterSet) {
/* 783 */         maxSizeOfParameterSet = sizeOfParameterSet;
/*     */       }
/*     */     } 
/*     */     
/* 787 */     return new long[] { maxSizeOfParameterSet, sizeOfEntireBatch };
/*     */   }
/*     */   
/*     */   private String truncateQueryToLog(String sql) {
/* 791 */     String queryStr = null;
/*     */     
/* 793 */     int maxQuerySizeToLog = ((Integer)this.session.getPropertySet().getIntegerProperty(PropertyKey.maxQuerySizeToLog).getValue()).intValue();
/* 794 */     if (sql.length() > maxQuerySizeToLog) {
/* 795 */       StringBuilder queryBuf = new StringBuilder(maxQuerySizeToLog + 12);
/* 796 */       queryBuf.append(sql.substring(0, maxQuerySizeToLog));
/* 797 */       queryBuf.append(Messages.getString("MysqlIO.25"));
/*     */       
/* 799 */       queryStr = queryBuf.toString();
/*     */     } else {
/* 801 */       queryStr = sql;
/*     */     } 
/*     */     
/* 804 */     return queryStr;
/*     */   }
/*     */ 
/*     */   
/*     */   public <M extends com.mysql.cj.protocol.Message> M fillSendPacket() {
/* 809 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <M extends com.mysql.cj.protocol.Message> M fillSendPacket(QueryBindings<?> bindings) {
/* 814 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\ServerPreparedQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */