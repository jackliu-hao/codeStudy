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
/*    */ public class DoubleValueFactory
/*    */   extends AbstractNumericValueFactory<Double>
/*    */ {
/*    */   public DoubleValueFactory(PropertySet pset) {
/* 47 */     super(pset);
/*    */   }
/*    */ 
/*    */   
/*    */   public Double createFromBigInteger(BigInteger i) {
/* 52 */     if (this.jdbcCompliantTruncationForReads && ((new BigDecimal(i)).compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE) < 0 || (new BigDecimal(i))
/* 53 */       .compareTo(Constants.BIG_DECIMAL_MAX_DOUBLE_VALUE) > 0)) {
/* 54 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() }));
/*    */     }
/* 56 */     return Double.valueOf(i.doubleValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Double createFromLong(long l) {
/* 61 */     if (this.jdbcCompliantTruncationForReads && (l < -1.7976931348623157E308D || l > Double.MAX_VALUE)) {
/* 62 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l), getTargetTypeName() }));
/*    */     }
/* 64 */     return Double.valueOf(l);
/*    */   }
/*    */ 
/*    */   
/*    */   public Double createFromBigDecimal(BigDecimal d) {
/* 69 */     if (this.jdbcCompliantTruncationForReads && (d
/* 70 */       .compareTo(Constants.BIG_DECIMAL_MAX_NEGATIVE_DOUBLE_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_DOUBLE_VALUE) > 0)) {
/* 71 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() }));
/*    */     }
/* 73 */     return Double.valueOf(d.doubleValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Double createFromDouble(double d) {
/* 78 */     if (this.jdbcCompliantTruncationForReads && (d < -1.7976931348623157E308D || d > Double.MAX_VALUE)) {
/* 79 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() }));
/*    */     }
/* 81 */     return Double.valueOf(d);
/*    */   }
/*    */ 
/*    */   
/*    */   public Double createFromBit(byte[] bytes, int offset, int length) {
/* 86 */     return Double.valueOf((new BigInteger(ByteBuffer.allocate(length + 1).put((byte)0).put(bytes, offset, length).array())).doubleValue());
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 90 */     return Double.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\DoubleValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */