/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.Constants;
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.NumberOutOfRange;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FloatValueFactory
/*    */   extends AbstractNumericValueFactory<Float>
/*    */ {
/*    */   public FloatValueFactory(PropertySet pset) {
/* 47 */     super(pset);
/*    */   }
/*    */ 
/*    */   
/*    */   public Float createFromBigInteger(BigInteger i) {
/* 52 */     if (this.jdbcCompliantTruncationForReads && ((new BigDecimal(i)).compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_FLOAT_VALUE) < 0 || (new BigDecimal(i))
/* 53 */       .compareTo(Constants.BIG_DECIMAL_MAX_FLOAT_VALUE) > 0)) {
/* 54 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() }));
/*    */     }
/* 56 */     return Float.valueOf((float)i.doubleValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Float createFromLong(long l) {
/* 61 */     if (this.jdbcCompliantTruncationForReads && ((float)l < -3.4028235E38F || (float)l > Float.MAX_VALUE)) {
/* 62 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l), getTargetTypeName() }));
/*    */     }
/* 64 */     return Float.valueOf((float)l);
/*    */   }
/*    */ 
/*    */   
/*    */   public Float createFromBigDecimal(BigDecimal d) {
/* 69 */     if (this.jdbcCompliantTruncationForReads && (d
/* 70 */       .compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_FLOAT_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_FLOAT_VALUE) > 0)) {
/* 71 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() }));
/*    */     }
/* 73 */     return Float.valueOf((float)d.doubleValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Float createFromDouble(double d) {
/* 78 */     if (this.jdbcCompliantTruncationForReads && (d < -3.4028234663852886E38D || d > 3.4028234663852886E38D)) {
/* 79 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() }));
/*    */     }
/* 81 */     return Float.valueOf((float)d);
/*    */   }
/*    */ 
/*    */   
/*    */   public Float createFromBit(byte[] bytes, int offset, int length) {
/* 86 */     return Float.valueOf((new BigInteger(ByteBuffer.allocate(length + 1).put((byte)0).put(bytes, offset, length).array())).floatValue());
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 90 */     return Float.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\FloatValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */