/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.a.NativeConstants;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.ValueEncoder;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractPreparedQuery<T extends QueryBindings<?>>
/*     */   extends AbstractQuery
/*     */   implements PreparedQuery<T>
/*     */ {
/*     */   protected ParseInfo parseInfo;
/*  54 */   protected T queryBindings = null;
/*     */ 
/*     */   
/*  57 */   protected String originalSql = null;
/*     */ 
/*     */   
/*     */   protected int parameterCount;
/*     */ 
/*     */   
/*     */   protected RuntimeProperty<Boolean> autoClosePStmtStreams;
/*     */   
/*  65 */   protected int batchCommandIndex = -1;
/*     */   
/*     */   protected RuntimeProperty<Boolean> useStreamLengthsInPrepStmts;
/*     */   
/*  69 */   private byte[] streamConvertBuf = null;
/*     */   
/*     */   public AbstractPreparedQuery(NativeSession sess) {
/*  72 */     super(sess);
/*     */     
/*  74 */     this.autoClosePStmtStreams = this.session.getPropertySet().getBooleanProperty(PropertyKey.autoClosePStmtStreams);
/*  75 */     this.useStreamLengthsInPrepStmts = this.session.getPropertySet().getBooleanProperty(PropertyKey.useStreamLengthsInPrepStmts);
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeQuery() {
/*  80 */     this.streamConvertBuf = null;
/*  81 */     super.closeQuery();
/*     */   }
/*     */   
/*     */   public ParseInfo getParseInfo() {
/*  85 */     return this.parseInfo;
/*     */   }
/*     */   
/*     */   public void setParseInfo(ParseInfo parseInfo) {
/*  89 */     this.parseInfo = parseInfo;
/*     */   }
/*     */   
/*     */   public String getOriginalSql() {
/*  93 */     return this.originalSql;
/*     */   }
/*     */   
/*     */   public void setOriginalSql(String originalSql) {
/*  97 */     this.originalSql = originalSql;
/*     */   }
/*     */   
/*     */   public int getParameterCount() {
/* 101 */     return this.parameterCount;
/*     */   }
/*     */   
/*     */   public void setParameterCount(int parameterCount) {
/* 105 */     this.parameterCount = parameterCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public T getQueryBindings() {
/* 110 */     return this.queryBindings;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setQueryBindings(T queryBindings) {
/* 115 */     this.queryBindings = queryBindings;
/*     */   }
/*     */   
/*     */   public int getBatchCommandIndex() {
/* 119 */     return this.batchCommandIndex;
/*     */   }
/*     */   
/*     */   public void setBatchCommandIndex(int batchCommandIndex) {
/* 123 */     this.batchCommandIndex = batchCommandIndex;
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
/*     */   public int computeBatchSize(int numBatchedArgs) {
/* 135 */     long[] combinedValues = computeMaxParameterSetSizeAndBatchSize(numBatchedArgs);
/*     */     
/* 137 */     long maxSizeOfParameterSet = combinedValues[0];
/* 138 */     long sizeOfEntireBatch = combinedValues[1];
/*     */     
/* 140 */     if (sizeOfEntireBatch < (((Integer)this.maxAllowedPacket.getValue()).intValue() - this.originalSql.length())) {
/* 141 */       return numBatchedArgs;
/*     */     }
/*     */     
/* 144 */     return (int)Math.max(1L, (((Integer)this.maxAllowedPacket.getValue()).intValue() - this.originalSql.length()) / maxSizeOfParameterSet);
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
/*     */   public void checkNullOrEmptyQuery(String sql) {
/* 157 */     if (sql == null) {
/* 158 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedQuery.0"), this.session.getExceptionInterceptor());
/*     */     }
/*     */     
/* 161 */     if (sql.length() == 0) {
/* 162 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedQuery.1"), this.session.getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */   public String asSql() {
/* 167 */     return asSql(false);
/*     */   }
/*     */   
/*     */   public String asSql(boolean quoteStreamsAndUnknowns) {
/* 171 */     StringBuilder buf = new StringBuilder();
/*     */     
/* 173 */     Object batchArg = null;
/* 174 */     if (this.batchCommandIndex != -1) {
/* 175 */       batchArg = this.batchedArgs.get(this.batchCommandIndex);
/*     */     }
/*     */     
/* 178 */     byte[][] staticSqlStrings = this.parseInfo.getStaticSql();
/* 179 */     for (int i = 0; i < this.parameterCount; i++) {
/* 180 */       buf.append((this.charEncoding != null) ? StringUtils.toString(staticSqlStrings[i], this.charEncoding) : StringUtils.toString(staticSqlStrings[i]));
/*     */       
/* 182 */       byte[] val = null;
/* 183 */       if (batchArg != null && batchArg instanceof String) {
/* 184 */         buf.append((String)batchArg);
/*     */       }
/*     */       else {
/*     */         
/* 188 */         val = (this.batchCommandIndex == -1) ? ((this.queryBindings == null) ? null : this.queryBindings.getBindValues()[i].getByteValue()) : ((QueryBindings)batchArg).getBindValues()[i].getByteValue();
/*     */ 
/*     */         
/* 191 */         boolean isStreamParam = (this.batchCommandIndex == -1) ? ((this.queryBindings == null) ? false : this.queryBindings.getBindValues()[i].isStream()) : ((QueryBindings)batchArg).getBindValues()[i].isStream();
/*     */         
/* 193 */         if (val == null && !isStreamParam) {
/* 194 */           buf.append(quoteStreamsAndUnknowns ? "'** NOT SPECIFIED **'" : "** NOT SPECIFIED **");
/* 195 */         } else if (isStreamParam) {
/* 196 */           buf.append(quoteStreamsAndUnknowns ? "'** STREAM DATA **'" : "** STREAM DATA **");
/*     */         } else {
/* 198 */           buf.append(StringUtils.toString(val, this.charEncoding));
/*     */         } 
/*     */       } 
/*     */     } 
/* 202 */     buf.append((this.charEncoding != null) ? StringUtils.toString(staticSqlStrings[this.parameterCount], this.charEncoding) : 
/* 203 */         StringUtils.toAsciiString(staticSqlStrings[this.parameterCount]));
/*     */     
/* 205 */     return buf.toString();
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
/*     */   public <M extends com.mysql.cj.protocol.Message> M fillSendPacket() {
/* 218 */     synchronized (this) {
/* 219 */       return fillSendPacket((QueryBindings<?>)this.queryBindings);
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
/*     */   public <M extends com.mysql.cj.protocol.Message> M fillSendPacket(QueryBindings<?> bindings) {
/* 236 */     synchronized (this) {
/* 237 */       BindValue[] bindValues = (BindValue[])bindings.getBindValues();
/*     */       
/* 239 */       NativePacketPayload sendPacket = this.session.getSharedSendPacket();
/*     */       
/* 241 */       sendPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 3L);
/*     */       
/* 243 */       if (getSession().getServerSession().supportsQueryAttributes()) {
/* 244 */         if (this.queryAttributesBindings.getCount() > 0) {
/* 245 */           sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, this.queryAttributesBindings.getCount());
/* 246 */           sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
/* 247 */           byte[] nullBitsBuffer = new byte[(this.queryAttributesBindings.getCount() + 7) / 8];
/* 248 */           for (int k = 0; k < this.queryAttributesBindings.getCount(); k++) {
/* 249 */             if (this.queryAttributesBindings.getAttributeValue(k).isNull()) {
/* 250 */               nullBitsBuffer[k >>> 3] = (byte)(nullBitsBuffer[k >>> 3] | 1 << (k & 0x7));
/*     */             }
/*     */           } 
/* 253 */           sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_VAR, nullBitsBuffer);
/* 254 */           sendPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 1L);
/* 255 */           this.queryAttributesBindings.runThroughAll(a -> {
/*     */                 sendPacket.writeInteger(NativeConstants.IntegerDataType.INT2, a.getType());
/*     */                 sendPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, a.getName().getBytes());
/*     */               });
/* 259 */           ValueEncoder valueEncoder = new ValueEncoder(sendPacket, this.charEncoding, this.session.getServerSession().getDefaultTimeZone());
/* 260 */           this.queryAttributesBindings.runThroughAll(a -> valueEncoder.encodeValue(a.getValue(), a.getType()));
/*     */         } else {
/* 262 */           sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 0L);
/* 263 */           sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
/*     */         } 
/* 265 */       } else if (this.queryAttributesBindings.getCount() > 0) {
/* 266 */         this.session.getLog().logWarn(Messages.getString("QueryAttributes.SetButNotSupported"));
/*     */       } 
/*     */       
/* 269 */       sendPacket.setTag("QUERY");
/*     */       
/* 271 */       boolean useStreamLengths = ((Boolean)this.useStreamLengthsInPrepStmts.getValue()).booleanValue();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 276 */       int ensurePacketSize = 0;
/*     */       
/* 278 */       String statementComment = this.session.getProtocol().getQueryComment();
/*     */       
/* 280 */       byte[] commentAsBytes = null;
/*     */       
/* 282 */       if (statementComment != null) {
/* 283 */         commentAsBytes = StringUtils.getBytes(statementComment, this.charEncoding);
/*     */         
/* 285 */         ensurePacketSize += commentAsBytes.length;
/* 286 */         ensurePacketSize += 6;
/*     */       } 
/*     */       
/* 289 */       for (int i = 0; i < bindValues.length; i++) {
/* 290 */         if (bindValues[i].isStream() && useStreamLengths) {
/* 291 */           ensurePacketSize = (int)(ensurePacketSize + bindValues[i].getStreamLength());
/*     */         }
/*     */       } 
/*     */       
/* 295 */       if (ensurePacketSize != 0) {
/* 296 */         sendPacket.ensureCapacity(ensurePacketSize);
/*     */       }
/*     */       
/* 299 */       if (commentAsBytes != null) {
/* 300 */         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, Constants.SLASH_STAR_SPACE_AS_BYTES);
/* 301 */         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, commentAsBytes);
/* 302 */         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
/*     */       } 
/*     */       
/* 305 */       byte[][] staticSqlStrings = this.parseInfo.getStaticSql();
/* 306 */       for (int j = 0; j < bindValues.length; j++) {
/* 307 */         bindings.checkParameterSet(j);
/*     */         
/* 309 */         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, staticSqlStrings[j]);
/*     */         
/* 311 */         if (bindValues[j].isStream()) {
/* 312 */           streamToBytes(sendPacket, bindValues[j].getStreamValue(), true, bindValues[j].getStreamLength(), useStreamLengths);
/*     */         } else {
/* 314 */           sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, bindValues[j].getByteValue());
/*     */         } 
/*     */       } 
/*     */       
/* 318 */       sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, staticSqlStrings[bindValues.length]);
/*     */       
/* 320 */       return (M)sendPacket;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private final void streamToBytes(NativePacketPayload packet, InputStream in, boolean escape, long streamLength, boolean useLength) {
/*     */     try {
/* 327 */       if (this.streamConvertBuf == null) {
/* 328 */         this.streamConvertBuf = new byte[4096];
/*     */       }
/*     */       
/* 331 */       boolean hexEscape = this.session.getServerSession().isNoBackslashEscapesSet();
/*     */       
/* 333 */       if (streamLength == -1L) {
/* 334 */         useLength = false;
/*     */       }
/*     */ 
/*     */       
/* 338 */       int bc = useLength ? Util.readBlock(in, this.streamConvertBuf, (int)streamLength, this.session.getExceptionInterceptor()) : Util.readBlock(in, this.streamConvertBuf, this.session.getExceptionInterceptor());
/*     */       
/* 340 */       int lengthLeftToRead = (int)streamLength - bc;
/*     */       
/* 342 */       packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, StringUtils.getBytes(hexEscape ? "x" : "_binary"));
/*     */       
/* 344 */       if (escape) {
/* 345 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 39L);
/*     */       }
/*     */       
/* 348 */       while (bc > 0) {
/* 349 */         if (hexEscape) {
/* 350 */           ((AbstractQueryBindings)this.queryBindings).hexEscapeBlock(this.streamConvertBuf, packet, bc);
/* 351 */         } else if (escape) {
/* 352 */           escapeblockFast(this.streamConvertBuf, packet, bc);
/*     */         } else {
/* 354 */           packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, this.streamConvertBuf, 0, bc);
/*     */         } 
/*     */         
/* 357 */         if (useLength) {
/* 358 */           bc = Util.readBlock(in, this.streamConvertBuf, lengthLeftToRead, this.session.getExceptionInterceptor());
/*     */           
/* 360 */           if (bc > 0)
/* 361 */             lengthLeftToRead -= bc; 
/*     */           continue;
/*     */         } 
/* 364 */         bc = Util.readBlock(in, this.streamConvertBuf, this.session.getExceptionInterceptor());
/*     */       } 
/*     */ 
/*     */       
/* 368 */       if (escape) {
/* 369 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 39L);
/*     */       }
/*     */     } finally {
/* 372 */       if (((Boolean)this.autoClosePStmtStreams.getValue()).booleanValue()) {
/*     */         try {
/* 374 */           in.close();
/* 375 */         } catch (IOException iOException) {}
/*     */ 
/*     */         
/* 378 */         in = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private final void escapeblockFast(byte[] buf, NativePacketPayload packet, int size) {
/* 384 */     int lastwritten = 0;
/*     */     
/* 386 */     for (int i = 0; i < size; i++) {
/* 387 */       byte b = buf[i];
/*     */       
/* 389 */       if (b == 0) {
/*     */         
/* 391 */         if (i > lastwritten) {
/* 392 */           packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, i - lastwritten);
/*     */         }
/*     */ 
/*     */         
/* 396 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 92L);
/* 397 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, 48L);
/* 398 */         lastwritten = i + 1;
/*     */       }
/* 400 */       else if (b == 92 || b == 39) {
/*     */         
/* 402 */         if (i > lastwritten) {
/* 403 */           packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, i - lastwritten);
/*     */         }
/*     */ 
/*     */         
/* 407 */         packet.writeInteger(NativeConstants.IntegerDataType.INT1, b);
/* 408 */         lastwritten = i;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 414 */     if (lastwritten < size)
/* 415 */       packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, size - lastwritten); 
/*     */   }
/*     */   
/*     */   protected abstract long[] computeMaxParameterSetSizeAndBatchSize(int paramInt);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\AbstractPreparedQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */