/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.Constants;
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.NumberOutOfRange;
/*    */ import com.mysql.cj.util.DataTypeUtil;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
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
/*    */ public class LongValueFactory
/*    */   extends AbstractNumericValueFactory<Long>
/*    */ {
/*    */   public LongValueFactory(PropertySet pset) {
/* 47 */     super(pset);
/*    */   }
/*    */ 
/*    */   
/*    */   public Long createFromBigInteger(BigInteger i) {
/* 52 */     if (this.jdbcCompliantTruncationForReads && (i
/* 53 */       .compareTo(Constants.BIG_INTEGER_MIN_LONG_VALUE) < 0 || i.compareTo(Constants.BIG_INTEGER_MAX_LONG_VALUE) > 0)) {
/* 54 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() }));
/*    */     }
/* 56 */     return Long.valueOf(i.longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Long createFromLong(long l) {
/* 61 */     if (this.jdbcCompliantTruncationForReads && (l < Long.MIN_VALUE || l > Long.MAX_VALUE)) {
/* 62 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() }));
/*    */     }
/* 64 */     return Long.valueOf(l);
/*    */   }
/*    */ 
/*    */   
/*    */   public Long createFromBigDecimal(BigDecimal d) {
/* 69 */     if (this.jdbcCompliantTruncationForReads && (d
/* 70 */       .compareTo(Constants.BIG_DECIMAL_MIN_LONG_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_LONG_VALUE) > 0)) {
/* 71 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() }));
/*    */     }
/* 73 */     return Long.valueOf(d.longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Long createFromDouble(double d) {
/* 78 */     if (this.jdbcCompliantTruncationForReads && (d < -9.223372036854776E18D || d > 9.223372036854776E18D)) {
/* 79 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() }));
/*    */     }
/* 81 */     return Long.valueOf((long)d);
/*    */   }
/*    */ 
/*    */   
/*    */   public Long createFromBit(byte[] bytes, int offset, int length) {
/* 86 */     return Long.valueOf(DataTypeUtil.bitToLong(bytes, offset, length));
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 90 */     return Long.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\LongValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */