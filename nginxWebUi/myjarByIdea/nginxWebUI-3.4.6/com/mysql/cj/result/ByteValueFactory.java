package com.mysql.cj.result;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.NumberOutOfRange;
import com.mysql.cj.util.DataTypeUtil;
import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ByteValueFactory extends DefaultValueFactory<Byte> {
   public ByteValueFactory(PropertySet pset) {
      super(pset);
   }

   public Byte createFromBigInteger(BigInteger i) {
      if (!this.jdbcCompliantTruncationForReads || i.compareTo(Constants.BIG_INTEGER_MIN_BYTE_VALUE) >= 0 && i.compareTo(Constants.BIG_INTEGER_MAX_BYTE_VALUE) <= 0) {
         return (byte)i.intValue();
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{i, this.getTargetTypeName()}));
      }
   }

   public Byte createFromLong(long l) {
      if (!this.jdbcCompliantTruncationForReads || l >= -128L && l <= 127L) {
         return (byte)((int)l);
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{Long.valueOf(l).toString(), this.getTargetTypeName()}));
      }
   }

   public Byte createFromBigDecimal(BigDecimal d) {
      if (!this.jdbcCompliantTruncationForReads || d.compareTo(Constants.BIG_DECIMAL_MIN_BYTE_VALUE) >= 0 && d.compareTo(Constants.BIG_DECIMAL_MAX_BYTE_VALUE) <= 0) {
         return (byte)((int)d.longValue());
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{d, this.getTargetTypeName()}));
      }
   }

   public Byte createFromDouble(double d) {
      if (!this.jdbcCompliantTruncationForReads || !(d < -128.0) && !(d > 127.0)) {
         return (byte)((int)d);
      } else {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{d, this.getTargetTypeName()}));
      }
   }

   public Byte createFromBit(byte[] bytes, int offset, int length) {
      long l = DataTypeUtil.bitToLong(bytes, offset, length);
      if (this.jdbcCompliantTruncationForReads && l >> 8 != 0L) {
         throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{Long.valueOf(l).toString(), this.getTargetTypeName()}));
      } else {
         return (byte)((int)l);
      }
   }

   public Byte createFromYear(long l) {
      return this.createFromLong(l);
   }

   public String getTargetTypeName() {
      return Byte.class.getName();
   }

   public Byte createFromBytes(byte[] bytes, int offset, int length, Field f) {
      if (length == 0 && (Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()) {
         return 0;
      } else {
         String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
         byte[] newBytes = s.getBytes();
         if (this.jdbcCompliantTruncationForReads && newBytes.length != 1) {
            throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[]{s, this.getTargetTypeName()}));
         } else {
            return newBytes[0];
         }
      }
   }
}
