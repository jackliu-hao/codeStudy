package com.mysql.cj;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.a.NativeConstants;
import com.mysql.cj.protocol.a.NativePacketPayload;
import com.mysql.cj.protocol.a.ValueEncoder;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractPreparedQuery<T extends QueryBindings<?>> extends AbstractQuery implements PreparedQuery<T> {
   protected ParseInfo parseInfo;
   protected T queryBindings = null;
   protected String originalSql = null;
   protected int parameterCount;
   protected RuntimeProperty<Boolean> autoClosePStmtStreams;
   protected int batchCommandIndex = -1;
   protected RuntimeProperty<Boolean> useStreamLengthsInPrepStmts;
   private byte[] streamConvertBuf = null;

   public AbstractPreparedQuery(NativeSession sess) {
      super(sess);
      this.autoClosePStmtStreams = this.session.getPropertySet().getBooleanProperty(PropertyKey.autoClosePStmtStreams);
      this.useStreamLengthsInPrepStmts = this.session.getPropertySet().getBooleanProperty(PropertyKey.useStreamLengthsInPrepStmts);
   }

   public void closeQuery() {
      this.streamConvertBuf = null;
      super.closeQuery();
   }

   public ParseInfo getParseInfo() {
      return this.parseInfo;
   }

   public void setParseInfo(ParseInfo parseInfo) {
      this.parseInfo = parseInfo;
   }

   public String getOriginalSql() {
      return this.originalSql;
   }

   public void setOriginalSql(String originalSql) {
      this.originalSql = originalSql;
   }

   public int getParameterCount() {
      return this.parameterCount;
   }

   public void setParameterCount(int parameterCount) {
      this.parameterCount = parameterCount;
   }

   public T getQueryBindings() {
      return this.queryBindings;
   }

   public void setQueryBindings(T queryBindings) {
      this.queryBindings = queryBindings;
   }

   public int getBatchCommandIndex() {
      return this.batchCommandIndex;
   }

   public void setBatchCommandIndex(int batchCommandIndex) {
      this.batchCommandIndex = batchCommandIndex;
   }

   public int computeBatchSize(int numBatchedArgs) {
      long[] combinedValues = this.computeMaxParameterSetSizeAndBatchSize(numBatchedArgs);
      long maxSizeOfParameterSet = combinedValues[0];
      long sizeOfEntireBatch = combinedValues[1];
      return sizeOfEntireBatch < (long)((Integer)this.maxAllowedPacket.getValue() - this.originalSql.length()) ? numBatchedArgs : (int)Math.max(1L, (long)((Integer)this.maxAllowedPacket.getValue() - this.originalSql.length()) / maxSizeOfParameterSet);
   }

   public void checkNullOrEmptyQuery(String sql) {
      if (sql == null) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedQuery.0"), this.session.getExceptionInterceptor());
      } else if (sql.length() == 0) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedQuery.1"), this.session.getExceptionInterceptor());
      }
   }

   public String asSql() {
      return this.asSql(false);
   }

   public String asSql(boolean quoteStreamsAndUnknowns) {
      StringBuilder buf = new StringBuilder();
      Object batchArg = null;
      if (this.batchCommandIndex != -1) {
         batchArg = this.batchedArgs.get(this.batchCommandIndex);
      }

      byte[][] staticSqlStrings = this.parseInfo.getStaticSql();

      for(int i = 0; i < this.parameterCount; ++i) {
         buf.append(this.charEncoding != null ? StringUtils.toString(staticSqlStrings[i], this.charEncoding) : StringUtils.toString(staticSqlStrings[i]));
         byte[] val = null;
         if (batchArg != null && batchArg instanceof String) {
            buf.append((String)batchArg);
         } else {
            byte[] val = this.batchCommandIndex == -1 ? (this.queryBindings == null ? null : this.queryBindings.getBindValues()[i].getByteValue()) : ((QueryBindings)batchArg).getBindValues()[i].getByteValue();
            boolean isStreamParam = this.batchCommandIndex == -1 ? (this.queryBindings == null ? false : this.queryBindings.getBindValues()[i].isStream()) : ((QueryBindings)batchArg).getBindValues()[i].isStream();
            if (val == null && !isStreamParam) {
               buf.append(quoteStreamsAndUnknowns ? "'** NOT SPECIFIED **'" : "** NOT SPECIFIED **");
            } else if (isStreamParam) {
               buf.append(quoteStreamsAndUnknowns ? "'** STREAM DATA **'" : "** STREAM DATA **");
            } else {
               buf.append(StringUtils.toString(val, this.charEncoding));
            }
         }
      }

      buf.append(this.charEncoding != null ? StringUtils.toString(staticSqlStrings[this.parameterCount], this.charEncoding) : StringUtils.toAsciiString(staticSqlStrings[this.parameterCount]));
      return buf.toString();
   }

   protected abstract long[] computeMaxParameterSetSizeAndBatchSize(int var1);

   public <M extends Message> M fillSendPacket() {
      synchronized(this) {
         return this.fillSendPacket(this.queryBindings);
      }
   }

   public <M extends Message> M fillSendPacket(QueryBindings<?> bindings) {
      synchronized(this) {
         BindValue[] bindValues = bindings.getBindValues();
         NativePacketPayload sendPacket = this.session.getSharedSendPacket();
         sendPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 3L);
         int ensurePacketSize;
         if (this.getSession().getServerSession().supportsQueryAttributes()) {
            if (this.queryAttributesBindings.getCount() > 0) {
               sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, (long)this.queryAttributesBindings.getCount());
               sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
               byte[] nullBitsBuffer = new byte[(this.queryAttributesBindings.getCount() + 7) / 8];

               for(ensurePacketSize = 0; ensurePacketSize < this.queryAttributesBindings.getCount(); ++ensurePacketSize) {
                  if (this.queryAttributesBindings.getAttributeValue(ensurePacketSize).isNull()) {
                     nullBitsBuffer[ensurePacketSize >>> 3] = (byte)(nullBitsBuffer[ensurePacketSize >>> 3] | 1 << (ensurePacketSize & 7));
                  }
               }

               sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_VAR, nullBitsBuffer);
               sendPacket.writeInteger(NativeConstants.IntegerDataType.INT1, 1L);
               this.queryAttributesBindings.runThroughAll((a) -> {
                  sendPacket.writeInteger(NativeConstants.IntegerDataType.INT2, (long)a.getType());
                  sendPacket.writeBytes(NativeConstants.StringSelfDataType.STRING_LENENC, a.getName().getBytes());
               });
               ValueEncoder valueEncoder = new ValueEncoder(sendPacket, this.charEncoding, this.session.getServerSession().getDefaultTimeZone());
               this.queryAttributesBindings.runThroughAll((a) -> {
                  valueEncoder.encodeValue(a.getValue(), a.getType());
               });
            } else {
               sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 0L);
               sendPacket.writeInteger(NativeConstants.IntegerDataType.INT_LENENC, 1L);
            }
         } else if (this.queryAttributesBindings.getCount() > 0) {
            this.session.getLog().logWarn(Messages.getString("QueryAttributes.SetButNotSupported"));
         }

         sendPacket.setTag("QUERY");
         boolean useStreamLengths = (Boolean)this.useStreamLengthsInPrepStmts.getValue();
         ensurePacketSize = 0;
         String statementComment = this.session.getProtocol().getQueryComment();
         byte[] commentAsBytes = null;
         if (statementComment != null) {
            commentAsBytes = StringUtils.getBytes(statementComment, this.charEncoding);
            ensurePacketSize += commentAsBytes.length;
            ensurePacketSize += 6;
         }

         for(int i = 0; i < bindValues.length; ++i) {
            if (bindValues[i].isStream() && useStreamLengths) {
               ensurePacketSize = (int)((long)ensurePacketSize + bindValues[i].getStreamLength());
            }
         }

         if (ensurePacketSize != 0) {
            sendPacket.ensureCapacity(ensurePacketSize);
         }

         if (commentAsBytes != null) {
            sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, Constants.SLASH_STAR_SPACE_AS_BYTES);
            sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, commentAsBytes);
            sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, Constants.SPACE_STAR_SLASH_SPACE_AS_BYTES);
         }

         byte[][] staticSqlStrings = this.parseInfo.getStaticSql();

         for(int i = 0; i < bindValues.length; ++i) {
            bindings.checkParameterSet(i);
            sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, staticSqlStrings[i]);
            if (bindValues[i].isStream()) {
               this.streamToBytes(sendPacket, bindValues[i].getStreamValue(), true, bindValues[i].getStreamLength(), useStreamLengths);
            } else {
               sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, bindValues[i].getByteValue());
            }
         }

         sendPacket.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, staticSqlStrings[bindValues.length]);
         return sendPacket;
      }
   }

   private final void streamToBytes(NativePacketPayload packet, InputStream in, boolean escape, long streamLength, boolean useLength) {
      try {
         if (this.streamConvertBuf == null) {
            this.streamConvertBuf = new byte[4096];
         }

         boolean hexEscape = this.session.getServerSession().isNoBackslashEscapesSet();
         if (streamLength == -1L) {
            useLength = false;
         }

         int bc = useLength ? Util.readBlock(in, this.streamConvertBuf, (int)streamLength, this.session.getExceptionInterceptor()) : Util.readBlock(in, this.streamConvertBuf, this.session.getExceptionInterceptor());
         int lengthLeftToRead = (int)streamLength - bc;
         packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, StringUtils.getBytes(hexEscape ? "x" : "_binary"));
         if (escape) {
            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 39L);
         }

         while(bc > 0) {
            if (hexEscape) {
               ((AbstractQueryBindings)this.queryBindings).hexEscapeBlock(this.streamConvertBuf, packet, bc);
            } else if (escape) {
               this.escapeblockFast(this.streamConvertBuf, packet, bc);
            } else {
               packet.writeBytes((NativeConstants.StringLengthDataType)NativeConstants.StringLengthDataType.STRING_FIXED, this.streamConvertBuf, 0, bc);
            }

            if (useLength) {
               bc = Util.readBlock(in, this.streamConvertBuf, lengthLeftToRead, this.session.getExceptionInterceptor());
               if (bc > 0) {
                  lengthLeftToRead -= bc;
               }
            } else {
               bc = Util.readBlock(in, this.streamConvertBuf, this.session.getExceptionInterceptor());
            }
         }

         if (escape) {
            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 39L);
         }
      } finally {
         if ((Boolean)this.autoClosePStmtStreams.getValue()) {
            try {
               in.close();
            } catch (IOException var15) {
            }

            in = null;
         }

      }

   }

   private final void escapeblockFast(byte[] buf, NativePacketPayload packet, int size) {
      int lastwritten = 0;

      for(int i = 0; i < size; ++i) {
         byte b = buf[i];
         if (b == 0) {
            if (i > lastwritten) {
               packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, i - lastwritten);
            }

            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 92L);
            packet.writeInteger(NativeConstants.IntegerDataType.INT1, 48L);
            lastwritten = i + 1;
         } else if (b == 92 || b == 39) {
            if (i > lastwritten) {
               packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, i - lastwritten);
            }

            packet.writeInteger(NativeConstants.IntegerDataType.INT1, (long)b);
            lastwritten = i;
         }
      }

      if (lastwritten < size) {
         packet.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, buf, lastwritten, size - lastwritten);
      }

   }
}
