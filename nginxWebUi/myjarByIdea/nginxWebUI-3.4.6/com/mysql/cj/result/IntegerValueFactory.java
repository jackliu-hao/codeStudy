package com.mysql.cj.result;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.NumberOutOfRange;
import com.mysql.cj.util.DataTypeUtil;
import java.math.BigDecimal;
import java.math.BigInteger;

public class IntegerValueFactory extends AbstractNumericValueFactory<Integer> {
   public IntegerValueFactory(PropertySet pset) {
      super(pset);
   }

   public Integer createFromBigInteger(BigInteger i) {
      if (!this.jdbcCompliantTruncationForReads || i.compareTo(Constants.BIG_INTEGER_MIN_INTEGER_VALUE) >= 0 && i.compareTo(Constants.BIG_INTEGER_MAX_INTEGER_VALUE) <= 0) {
         return i.intValue();
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{i, this.getTargetTypeName()}));
      }
   }

   public Integer createFromLong(long l) {
      if (!this.jdbcCompliantTruncationForReads || l >= -2147483648L && l <= 2147483647L) {
         return (int)l;
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{Long.valueOf(l).toString(), this.getTargetTypeName()}));
      }
   }

   public Integer createFromBigDecimal(BigDecimal d) {
      if (!this.jdbcCompliantTruncationForReads || d.compareTo(Constants.BIG_DECIMAL_MIN_INTEGER_VALUE) >= 0 && d.compareTo(Constants.BIG_DECIMAL_MAX_INTEGER_VALUE) <= 0) {
         return (int)d.longValue();
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{d, this.getTargetTypeName()}));
      }
   }

   public Integer createFromDouble(double d) {
      if (!this.jdbcCompliantTruncationForReads || !(d < -2.147483648E9) && !(d > 2.147483647E9)) {
         return (int)d;
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{d, this.getTargetTypeName()}));
      }
   }

   public Integer createFromBit(byte[] bytes, int offset, int length) {
      long l = DataTypeUtil.bitToLong(bytes, offset, length);
      if (this.jdbcCompliantTruncationForReads && l >> 32 != 0L) {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{Long.valueOf(l).toString(), this.getTargetTypeName()}));
      } else {
         return (int)l;
      }
   }

   public String getTargetTypeName() {
      return Integer.class.getName();
   }
}
