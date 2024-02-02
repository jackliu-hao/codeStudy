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
/*    */ public class IntegerValueFactory
/*    */   extends AbstractNumericValueFactory<Integer>
/*    */ {
/*    */   public IntegerValueFactory(PropertySet pset) {
/* 47 */     super(pset);
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer createFromBigInteger(BigInteger i) {
/* 52 */     if (this.jdbcCompliantTruncationForReads && (i
/* 53 */       .compareTo(Constants.BIG_INTEGER_MIN_INTEGER_VALUE) < 0 || i.compareTo(Constants.BIG_INTEGER_MAX_INTEGER_VALUE) > 0)) {
/* 54 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() }));
/*    */     }
/* 56 */     return Integer.valueOf(i.intValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer createFromLong(long l) {
/* 61 */     if (this.jdbcCompliantTruncationForReads && (l < -2147483648L || l > 2147483647L)) {
/* 62 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() }));
/*    */     }
/* 64 */     return Integer.valueOf((int)l);
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer createFromBigDecimal(BigDecimal d) {
/* 69 */     if (this.jdbcCompliantTruncationForReads && (d
/* 70 */       .compareTo(Constants.BIG_DECIMAL_MIN_INTEGER_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_INTEGER_VALUE) > 0)) {
/* 71 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() }));
/*    */     }
/* 73 */     return Integer.valueOf((int)d.longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer createFromDouble(double d) {
/* 78 */     if (this.jdbcCompliantTruncationForReads && (d < -2.147483648E9D || d > 2.147483647E9D)) {
/* 79 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() }));
/*    */     }
/* 81 */     return Integer.valueOf((int)d);
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer createFromBit(byte[] bytes, int offset, int length) {
/* 86 */     long l = DataTypeUtil.bitToLong(bytes, offset, length);
/* 87 */     if (this.jdbcCompliantTruncationForReads && l >> 32L != 0L) {
/* 88 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() }));
/*    */     }
/* 90 */     return Integer.valueOf((int)l);
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 94 */     return Integer.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\IntegerValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */