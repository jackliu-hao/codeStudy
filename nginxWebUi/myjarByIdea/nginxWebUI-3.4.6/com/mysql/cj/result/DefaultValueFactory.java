package com.mysql.cj.result;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataConversionException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class DefaultValueFactory<T> implements ValueFactory<T> {
   protected boolean jdbcCompliantTruncationForReads = true;
   protected PropertySet pset = null;

   public DefaultValueFactory(PropertySet pset) {
      this.pset = pset;
      this.jdbcCompliantTruncationForReads = (Boolean)this.pset.getBooleanProperty(PropertyKey.jdbcCompliantTruncation).getInitialValue();
   }

   public void setPropertySet(PropertySet pset) {
      this.pset = pset;
   }

   protected T unsupported(String sourceType) {
      throw new DataConversionException(Messages.getString("ResultSet.UnsupportedConversion", new Object[]{sourceType, this.getTargetTypeName()}));
   }

   public T createFromDate(InternalDate idate) {
      return this.unsupported("DATE");
   }

   public T createFromTime(InternalTime it) {
      return this.unsupported("TIME");
   }

   public T createFromTimestamp(InternalTimestamp its) {
      return this.unsupported("TIMESTAMP");
   }

   public T createFromDatetime(InternalTimestamp its) {
      return this.unsupported("DATETIME");
   }

   public T createFromLong(long l) {
      return this.unsupported("LONG");
   }

   public T createFromBigInteger(BigInteger i) {
      return this.unsupported("BIGINT");
   }

   public T createFromDouble(double d) {
      return this.unsupported("DOUBLE");
   }

   public T createFromBigDecimal(BigDecimal d) {
      return this.unsupported("DECIMAL");
   }

   public T createFromBit(byte[] bytes, int offset, int length) {
      return this.unsupported("BIT");
   }

   public T createFromYear(long l) {
      return this.unsupported("YEAR");
   }

   public T createFromNull() {
      return null;
   }
}
