package com.mysql.cj.result;

import com.mysql.cj.conf.PropertySet;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class BigDecimalValueFactory extends AbstractNumericValueFactory<BigDecimal> {
   int scale;
   boolean hasScale;

   public BigDecimalValueFactory(PropertySet pset) {
      super(pset);
   }

   public BigDecimalValueFactory(PropertySet pset, int scale) {
      super(pset);
      this.scale = scale;
      this.hasScale = true;
   }

   private BigDecimal adjustResult(BigDecimal d) {
      if (this.hasScale) {
         try {
            return d.setScale(this.scale);
         } catch (ArithmeticException var3) {
            return d.setScale(this.scale, 4);
         }
      } else {
         return d;
      }
   }

   public BigDecimal createFromBigInteger(BigInteger i) {
      return this.adjustResult(new BigDecimal(i));
   }

   public BigDecimal createFromLong(long l) {
      return this.adjustResult(BigDecimal.valueOf(l));
   }

   public BigDecimal createFromBigDecimal(BigDecimal d) {
      return this.adjustResult(d);
   }

   public BigDecimal createFromDouble(double d) {
      return this.adjustResult(BigDecimal.valueOf(d));
   }

   public BigDecimal createFromBit(byte[] bytes, int offset, int length) {
      return new BigDecimal(new BigInteger(ByteBuffer.allocate(length + 1).put((byte)0).put(bytes, offset, length).array()));
   }

   public String getTargetTypeName() {
      return BigDecimal.class.getName();
   }
}
