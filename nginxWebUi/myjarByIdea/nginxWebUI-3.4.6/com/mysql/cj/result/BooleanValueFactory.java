package com.mysql.cj.result;

import com.mysql.cj.Constants;
import com.mysql.cj.Messages;
import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.DataConversionException;
import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
import com.mysql.cj.util.DataTypeUtil;
import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BooleanValueFactory extends DefaultValueFactory<Boolean> {
   public BooleanValueFactory(PropertySet pset) {
      super(pset);
   }

   public Boolean createFromLong(long l) {
      return l == -1L || l > 0L;
   }

   public Boolean createFromBigInteger(BigInteger i) {
      return i.compareTo(Constants.BIG_INTEGER_ZERO) > 0 || i.compareTo(Constants.BIG_INTEGER_NEGATIVE_ONE) == 0;
   }

   public Boolean createFromDouble(double d) {
      return d > 0.0 || d == -1.0;
   }

   public Boolean createFromBigDecimal(BigDecimal d) {
      return d.compareTo(Constants.BIG_DECIMAL_ZERO) > 0 || d.compareTo(Constants.BIG_DECIMAL_NEGATIVE_ONE) == 0;
   }

   public Boolean createFromBit(byte[] bytes, int offset, int length) {
      return this.createFromLong(DataTypeUtil.bitToLong(bytes, offset, length));
   }

   public Boolean createFromYear(long l) {
      return this.createFromLong(l);
   }

   public String getTargetTypeName() {
      return Boolean.class.getName();
   }

   public Boolean createFromBytes(byte[] bytes, int offset, int length, Field f) {
      if (length == 0 && (Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()) {
         return this.createFromLong(0L);
      } else {
         String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
         byte[] newBytes = s.getBytes();
         if (!s.equalsIgnoreCase("Y") && !s.equalsIgnoreCase("yes") && !s.equalsIgnoreCase("T") && !s.equalsIgnoreCase("true")) {
            if (!s.equalsIgnoreCase("N") && !s.equalsIgnoreCase("no") && !s.equalsIgnoreCase("F") && !s.equalsIgnoreCase("false")) {
               if (!s.contains("e") && !s.contains("E") && !s.matches("-?\\d*\\.\\d*")) {
                  if (!s.matches("-?\\d+")) {
                     throw new DataConversionException(Messages.getString("ResultSet.UnableToInterpretString", new Object[]{s}));
                  } else {
                     return s.charAt(0) != '-' && (length > 19 || newBytes[0] < 48 || newBytes[0] > 56) ? this.createFromBigInteger(MysqlTextValueDecoder.getBigInteger(newBytes, 0, newBytes.length)) : this.createFromLong(MysqlTextValueDecoder.getLong(newBytes, 0, newBytes.length));
                  }
               } else {
                  return this.createFromDouble(MysqlTextValueDecoder.getDouble(newBytes, 0, newBytes.length));
               }
            } else {
               return this.createFromLong(0L);
            }
         } else {
            return this.createFromLong(1L);
         }
      }
   }
}
