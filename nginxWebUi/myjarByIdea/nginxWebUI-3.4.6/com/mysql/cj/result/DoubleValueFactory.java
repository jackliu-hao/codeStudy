package com.mysql.cj.result;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.NumberOutOfRange;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class DoubleValueFactory extends AbstractNumericValueFactory<Double> {
   public DoubleValueFactory(PropertySet pset) {
      super(pset);
   }

   public Double createFromBigInteger(BigInteger i) {
      if (!this.jdbcCompliantTruncationForReads || (new BigDecimal(i)).compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE) >= 0 && (new BigDecimal(i)).compareTo(Constants.BIG_DECIMAL_MAX_DOUBLE_VALUE) <= 0) {
         return i.doubleValue();
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{i, this.getTargetTypeName()}));
      }
   }

   public Double createFromLong(long l) {
      if (!this.jdbcCompliantTruncationForReads || !((double)l < -1.7976931348623157E308) && !((double)l > Double.MAX_VALUE)) {
         return (double)l;
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{l, this.getTargetTypeName()}));
      }
   }

   public Double createFromBigDecimal(BigDecimal d) {
      if (!this.jdbcCompliantTruncationForReads || d.compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE) >= 0 && d.compareTo(Constants.BIG_DECIMAL_MAX_DOUBLE_VALUE) <= 0) {
         return d.doubleValue();
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{d, this.getTargetTypeName()}));
      }
   }

   public Double createFromDouble(double d) {
      if (!this.jdbcCompliantTruncationForReads || !(d < -1.7976931348623157E308) && !(d > Double.MAX_VALUE)) {
         return d;
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{d, this.getTargetTypeName()}));
      }
   }

   public Double createFromBit(byte[] bytes, int offset, int length) {
      return (new BigInteger(ByteBuffer.allocate(length + 1).put((byte)0).put(bytes, offset, length).array())).doubleValue();
   }

   public String getTargetTypeName() {
      return Double.class.getName();
   }
}
