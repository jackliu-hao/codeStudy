package com.mysql.cj.result;

import com.mysql.cj.conf.PropertyKey;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.util.DataTypeUtil;
import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;

public class StringValueFactory implements ValueFactory<String> {
   protected PropertySet pset = null;

   public StringValueFactory(PropertySet pset) {
      this.pset = pset;
   }

   public void setPropertySet(PropertySet pset) {
      this.pset = pset;
   }

   public String createFromDate(InternalDate idate) {
      return String.format("%04d-%02d-%02d", idate.getYear(), idate.getMonth(), idate.getDay());
   }

   public String createFromTime(InternalTime it) {
      return it.toString();
   }

   public String createFromTimestamp(InternalTimestamp its) {
      return String.format("%s %s", this.createFromDate(its), this.createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale())));
   }

   public String createFromDatetime(InternalTimestamp its) {
      return String.format("%s %s", this.createFromDate(its), this.createFromTime(new InternalTime(its.getHours(), its.getMinutes(), its.getSeconds(), its.getNanos(), its.getScale())));
   }

   public String createFromLong(long l) {
      return String.valueOf(l);
   }

   public String createFromBigInteger(BigInteger i) {
      return i.toString();
   }

   public String createFromDouble(double d) {
      return String.valueOf(d);
   }

   public String createFromBigDecimal(BigDecimal d) {
      return d.toString();
   }

   public String createFromBytes(byte[] bytes, int offset, int length, Field f) {
      return StringUtils.toString(bytes, offset, length, f.getCollationIndex() == 63 ? (String)this.pset.getStringProperty(PropertyKey.characterEncoding).getValue() : f.getEncoding());
   }

   public String createFromBit(byte[] bytes, int offset, int length) {
      return this.createFromLong(DataTypeUtil.bitToLong(bytes, offset, length));
   }

   public String createFromYear(long l) {
      if ((Boolean)this.pset.getBooleanProperty(PropertyKey.yearIsDateType).getValue()) {
         if (l < 100L) {
            if (l <= 69L) {
               l += 100L;
            }

            l += 1900L;
         }

         return this.createFromDate(new InternalDate((int)l, 1, 1));
      } else {
         return this.createFromLong(l);
      }
   }

   public String createFromNull() {
      return null;
   }

   public String getTargetTypeName() {
      return String.class.getName();
   }
}
