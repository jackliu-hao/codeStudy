package com.mysql.cj;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.TimeUtil;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerPreparedQueryBindings extends AbstractQueryBindings<ServerPreparedQueryBindValue> {
   private AtomicBoolean sendTypesToServer = new AtomicBoolean(false);
   private boolean longParameterSwitchDetected = false;

   public ServerPreparedQueryBindings(int parameterCount, Session sess) {
      super(parameterCount, sess);
   }

   protected void initBindValues(int parameterCount) {
      this.bindValues = new ServerPreparedQueryBindValue[parameterCount];

      for(int i = 0; i < parameterCount; ++i) {
         ((ServerPreparedQueryBindValue[])this.bindValues)[i] = new ServerPreparedQueryBindValue(this.session.getServerSession().getDefaultTimeZone(), this.session.getServerSession().getSessionTimeZone(), this.session.getPropertySet());
      }

   }

   public ServerPreparedQueryBindings clone() {
      ServerPreparedQueryBindings newBindings = new ServerPreparedQueryBindings(((ServerPreparedQueryBindValue[])this.bindValues).length, this.session);
      ServerPreparedQueryBindValue[] bvs = new ServerPreparedQueryBindValue[((ServerPreparedQueryBindValue[])this.bindValues).length];

      for(int i = 0; i < ((ServerPreparedQueryBindValue[])this.bindValues).length; ++i) {
         bvs[i] = ((ServerPreparedQueryBindValue[])this.bindValues)[i].clone();
      }

      newBindings.bindValues = bvs;
      newBindings.sendTypesToServer = this.sendTypesToServer;
      newBindings.longParameterSwitchDetected = this.longParameterSwitchDetected;
      newBindings.isLoadDataQuery = this.isLoadDataQuery;
      return newBindings;
   }

   public ServerPreparedQueryBindValue getBinding(int parameterIndex, boolean forLongData) {
      if (((ServerPreparedQueryBindValue[])this.bindValues)[parameterIndex] != null && ((ServerPreparedQueryBindValue[])this.bindValues)[parameterIndex].isStream && !forLongData) {
         this.longParameterSwitchDetected = true;
      }

      return ((ServerPreparedQueryBindValue[])this.bindValues)[parameterIndex];
   }

   public void checkParameterSet(int columnIndex) {
      if (!((ServerPreparedQueryBindValue[])this.bindValues)[columnIndex].isSet()) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ServerPreparedStatement.13") + (columnIndex + 1) + Messages.getString("ServerPreparedStatement.14"));
      }
   }

   public AtomicBoolean getSendTypesToServer() {
      return this.sendTypesToServer;
   }

   public boolean isLongParameterSwitchDetected() {
      return this.longParameterSwitchDetected;
   }

   public void setLongParameterSwitchDetected(boolean longParameterSwitchDetected) {
      this.longParameterSwitchDetected = longParameterSwitchDetected;
   }

   public void setAsciiStream(int parameterIndex, InputStream x) {
      this.setAsciiStream(parameterIndex, x, -1);
   }

   public void setAsciiStream(int parameterIndex, InputStream x, int length) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, true);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, (long)this.numberOfExecutions));
         binding.value = x;
         binding.isStream = true;
         binding.streamLength = (Boolean)this.useStreamLengthsInPrepStmts.getValue() ? (long)length : -1L;
      }

   }

   public void setAsciiStream(int parameterIndex, InputStream x, long length) {
      this.setAsciiStream(parameterIndex, x, (int)length);
      ((ServerPreparedQueryBindValue[])this.bindValues)[parameterIndex].setMysqlType(MysqlType.TEXT);
   }

   public void setBigDecimal(int parameterIndex, BigDecimal x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(246, (long)this.numberOfExecutions));
         binding.value = StringUtils.fixDecimalExponent(x.toPlainString());
         binding.parameterType = MysqlType.DECIMAL;
      }

   }

   public void setBigInteger(int parameterIndex, BigInteger x) {
      this.setValue(parameterIndex, x.toString(), MysqlType.BIGINT_UNSIGNED);
   }

   public void setBinaryStream(int parameterIndex, InputStream x) {
      this.setBinaryStream(parameterIndex, x, -1);
   }

   public void setBinaryStream(int parameterIndex, InputStream x, int length) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, true);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, (long)this.numberOfExecutions));
         binding.value = x;
         binding.isStream = true;
         binding.streamLength = (Boolean)this.useStreamLengthsInPrepStmts.getValue() ? (long)length : -1L;
         binding.parameterType = MysqlType.BLOB;
      }

   }

   public void setBinaryStream(int parameterIndex, InputStream x, long length) {
      this.setBinaryStream(parameterIndex, x, (int)length);
   }

   public void setBlob(int parameterIndex, InputStream inputStream) {
      this.setBinaryStream(parameterIndex, inputStream);
   }

   public void setBlob(int parameterIndex, InputStream inputStream, long length) {
      this.setBinaryStream(parameterIndex, inputStream, (int)length);
   }

   public void setBlob(int parameterIndex, Blob x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         try {
            ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, true);
            this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, (long)this.numberOfExecutions));
            binding.value = x;
            binding.isStream = true;
            binding.streamLength = (Boolean)this.useStreamLengthsInPrepStmts.getValue() ? x.length() : -1L;
            binding.parameterType = MysqlType.BLOB;
         } catch (Throwable var4) {
            throw ExceptionFactory.createException(var4.getMessage(), var4);
         }
      }

   }

   public void setBoolean(int parameterIndex, boolean x) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(1, (long)this.numberOfExecutions));
      binding.value = x ? 1L : 0L;
      binding.parameterType = MysqlType.BOOLEAN;
   }

   public void setByte(int parameterIndex, byte x) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(1, (long)this.numberOfExecutions));
      binding.value = (long)x;
      binding.parameterType = MysqlType.TINYINT;
   }

   public void setBytes(int parameterIndex, byte[] x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(253, (long)this.numberOfExecutions));
         binding.value = x;
         binding.parameterType = MysqlType.BINARY;
      }

   }

   public void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars) {
      this.setBytes(parameterIndex, x);
   }

   public void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes) {
      this.setBytes(parameterIndex, parameterAsBytes);
   }

   public void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) {
      this.setBytes(parameterIndex, parameterAsBytes);
   }

   public void setCharacterStream(int parameterIndex, Reader reader) {
      this.setCharacterStream(parameterIndex, reader, -1);
   }

   public void setCharacterStream(int parameterIndex, Reader reader, int length) {
      if (reader == null) {
         this.setNull(parameterIndex);
      } else {
         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, true);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, (long)this.numberOfExecutions));
         binding.value = reader;
         binding.isStream = true;
         binding.streamLength = (Boolean)this.useStreamLengthsInPrepStmts.getValue() ? (long)length : -1L;
         binding.parameterType = MysqlType.TEXT;
      }

   }

   public void setCharacterStream(int parameterIndex, Reader reader, long length) {
      this.setCharacterStream(parameterIndex, reader, (int)length);
   }

   public void setClob(int parameterIndex, Reader reader) {
      this.setCharacterStream(parameterIndex, reader);
   }

   public void setClob(int parameterIndex, Reader reader, long length) {
      this.setCharacterStream(parameterIndex, reader, length);
   }

   public void setClob(int parameterIndex, Clob x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         try {
            ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, true);
            this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, (long)this.numberOfExecutions));
            binding.value = x.getCharacterStream();
            binding.isStream = true;
            binding.streamLength = (Boolean)this.useStreamLengthsInPrepStmts.getValue() ? x.length() : -1L;
            binding.parameterType = MysqlType.TEXT;
         } catch (Throwable var4) {
            throw ExceptionFactory.createException(var4.getMessage(), var4);
         }
      }

   }

   public void setDate(int parameterIndex, Date x) {
      this.setDate(parameterIndex, x, (Calendar)null);
   }

   public void setDate(int parameterIndex, Date x, Calendar cal) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(10, (long)this.numberOfExecutions));
         binding.value = x;
         binding.calendar = cal == null ? null : (Calendar)cal.clone();
         binding.parameterType = MysqlType.DATE;
      }

   }

   public void setDouble(int parameterIndex, double x) {
      if ((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.allowNanAndInf).getValue() || x != Double.POSITIVE_INFINITY && x != Double.NEGATIVE_INFINITY && !Double.isNaN(x)) {
         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(5, (long)this.numberOfExecutions));
         binding.value = x;
         binding.parameterType = MysqlType.DOUBLE;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("PreparedStatement.64", new Object[]{x}), this.session.getExceptionInterceptor());
      }
   }

   public void setFloat(int parameterIndex, float x) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(4, (long)this.numberOfExecutions));
      binding.value = x;
      binding.parameterType = MysqlType.FLOAT;
   }

   public void setInt(int parameterIndex, int x) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(3, (long)this.numberOfExecutions));
      binding.value = (long)x;
      binding.parameterType = MysqlType.INT;
   }

   public void setLocalDate(int parameterIndex, LocalDate x, MysqlType targetMysqlType) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(10, (long)this.numberOfExecutions));
      binding.parameterType = targetMysqlType;
      binding.value = x;
   }

   public void setLocalTime(int parameterIndex, LocalTime x, MysqlType targetMysqlType) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      if (targetMysqlType == MysqlType.DATE) {
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(10, (long)this.numberOfExecutions));
         binding.parameterType = targetMysqlType;
         binding.value = DEFAULT_DATE;
      } else {
         if (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && (Boolean)this.sendFractionalSeconds.getValue()) {
            int fractLen = 6;
            if (this.columnDefinition != null && parameterIndex <= this.columnDefinition.getFields().length && parameterIndex >= 0) {
               fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
            }

            x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
         } else if (x.getNano() > 0) {
            x = x.withNano(0);
         }

         if (targetMysqlType == MysqlType.TIME) {
            this.sendTypesToServer.compareAndSet(false, binding.resetToType(11, (long)this.numberOfExecutions));
         } else {
            this.sendTypesToServer.compareAndSet(false, binding.resetToType(12, (long)this.numberOfExecutions));
         }

         binding.parameterType = targetMysqlType;
         binding.value = x;
      }
   }

   public void setLocalDateTime(int parameterIndex, LocalDateTime x, MysqlType targetMysqlType) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      if (targetMysqlType == MysqlType.DATE) {
         x = LocalDateTime.of(x.toLocalDate(), DEFAULT_TIME);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(10, (long)this.numberOfExecutions));
      } else {
         int fractLen = 6;
         if (this.columnDefinition != null && parameterIndex <= this.columnDefinition.getFields().length && parameterIndex >= 0) {
            fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
         }

         if (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && (Boolean)this.sendFractionalSeconds.getValue()) {
            x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
         } else if (x.getNano() > 0) {
            x = x.withNano(0);
         }

         if (targetMysqlType == MysqlType.TIME) {
            this.sendTypesToServer.compareAndSet(false, binding.resetToType(11, (long)this.numberOfExecutions));
         } else {
            this.sendTypesToServer.compareAndSet(false, binding.resetToType(12, (long)this.numberOfExecutions));
         }
      }

      binding.parameterType = targetMysqlType;
      binding.value = x;
   }

   public void setLong(int parameterIndex, long x) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(8, (long)this.numberOfExecutions));
      binding.value = x;
      binding.parameterType = MysqlType.BIGINT;
   }

   public void setNCharacterStream(int parameterIndex, Reader value) {
      this.setNCharacterStream(parameterIndex, value, -1L);
   }

   public void setNCharacterStream(int parameterIndex, Reader reader, long length) {
      if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8")) {
         throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.28"), this.session.getExceptionInterceptor());
      } else {
         if (reader == null) {
            this.setNull(parameterIndex);
         } else {
            ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, true);
            this.sendTypesToServer.compareAndSet(false, binding.resetToType(252, (long)this.numberOfExecutions));
            binding.value = reader;
            binding.isStream = true;
            binding.streamLength = (Boolean)this.useStreamLengthsInPrepStmts.getValue() ? length : -1L;
            binding.parameterType = MysqlType.TEXT;
         }

      }
   }

   public void setNClob(int parameterIndex, Reader reader) {
      this.setNCharacterStream(parameterIndex, reader);
   }

   public void setNClob(int parameterIndex, Reader reader, long length) {
      if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8")) {
         throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.29"), this.session.getExceptionInterceptor());
      } else {
         this.setNCharacterStream(parameterIndex, reader, length);
      }
   }

   public void setNClob(int parameterIndex, NClob value) {
      try {
         this.setNClob(parameterIndex, value.getCharacterStream(), (Boolean)this.useStreamLengthsInPrepStmts.getValue() ? value.length() : -1L);
      } catch (Throwable var4) {
         throw ExceptionFactory.createException(var4.getMessage(), var4, this.session.getExceptionInterceptor());
      }
   }

   public void setNString(int parameterIndex, String x) {
      if (!this.charEncoding.equalsIgnoreCase("UTF-8") && !this.charEncoding.equalsIgnoreCase("utf8")) {
         throw ExceptionFactory.createException(Messages.getString("ServerPreparedStatement.30"), this.session.getExceptionInterceptor());
      } else {
         this.setString(parameterIndex, x);
      }
   }

   public void setNull(int parameterIndex) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(6, (long)this.numberOfExecutions));
      binding.setNull(true);
   }

   public void setShort(int parameterIndex, short x) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(2, (long)this.numberOfExecutions));
      binding.value = (long)x;
      binding.parameterType = MysqlType.SMALLINT;
   }

   public void setString(int parameterIndex, String x) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(253, (long)this.numberOfExecutions));
         binding.value = x;
         binding.charEncoding = this.charEncoding;
         binding.parameterType = MysqlType.VARCHAR;
      }

   }

   public void setTime(int parameterIndex, Time x, Calendar cal) {
      if (x == null) {
         this.setNull(parameterIndex);
      } else {
         if (!this.session.getServerSession().getCapabilities().serverSupportsFracSecs() || !(Boolean)this.sendFractionalSeconds.getValue() || !(Boolean)this.sendFractionalSecondsForTime.getValue()) {
            x = TimeUtil.truncateFractionalSeconds(x);
         }

         ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(11, (long)this.numberOfExecutions));
         binding.value = x;
         binding.calendar = cal == null ? null : (Calendar)cal.clone();
         binding.parameterType = MysqlType.TIME;
      }

   }

   public void setTime(int parameterIndex, Time x) {
      this.setTime(parameterIndex, x, (Calendar)null);
   }

   public void bindTimestamp(int parameterIndex, Timestamp x, Calendar targetCalendar, int fractionalLength, MysqlType targetMysqlType) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      this.sendTypesToServer.compareAndSet(false, binding.resetToType(targetMysqlType == MysqlType.TIMESTAMP ? 7 : 12, (long)this.numberOfExecutions));
      if (fractionalLength < 0) {
         fractionalLength = 6;
      }

      x = TimeUtil.adjustNanosPrecision(x, fractionalLength, !this.session.getServerSession().isServerTruncatesFracSecs());
      binding.value = x;
      binding.calendar = targetCalendar == null ? null : (Calendar)targetCalendar.clone();
      binding.parameterType = targetMysqlType;
   }

   public void setDuration(int parameterIndex, Duration x, MysqlType targetMysqlType) {
      ServerPreparedQueryBindValue binding = this.getBinding(parameterIndex, false);
      if (this.session.getServerSession().getCapabilities().serverSupportsFracSecs() && (Boolean)this.sendFractionalSeconds.getValue()) {
         int fractLen = 6;
         if (this.columnDefinition != null && parameterIndex <= this.columnDefinition.getFields().length && parameterIndex >= 0) {
            fractLen = this.columnDefinition.getFields()[parameterIndex].getDecimals();
         }

         x = TimeUtil.adjustNanosPrecision(x, fractLen, !this.session.getServerSession().isServerTruncatesFracSecs());
      } else if (x.getNano() > 0) {
         x = x.isNegative() ? x.plusSeconds(1L).withNanos(0) : x.withNanos(0);
      }

      if (targetMysqlType == MysqlType.TIME) {
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(11, (long)this.numberOfExecutions));
      } else {
         this.sendTypesToServer.compareAndSet(false, binding.resetToType(12, (long)this.numberOfExecutions));
      }

      binding.parameterType = targetMysqlType;
      binding.value = x;
   }
}
