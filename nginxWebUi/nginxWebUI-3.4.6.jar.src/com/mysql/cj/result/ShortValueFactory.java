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
/*    */ public class ShortValueFactory
/*    */   extends AbstractNumericValueFactory<Short>
/*    */ {
/*    */   public ShortValueFactory(PropertySet pset) {
/* 47 */     super(pset);
/*    */   }
/*    */ 
/*    */   
/*    */   public Short createFromBigInteger(BigInteger i) {
/* 52 */     if (this.jdbcCompliantTruncationForReads && (i
/* 53 */       .compareTo(Constants.BIG_INTEGER_MIN_SHORT_VALUE) < 0 || i.compareTo(Constants.BIG_INTEGER_MAX_SHORT_VALUE) > 0)) {
/* 54 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() }));
/*    */     }
/* 56 */     return Short.valueOf((short)i.intValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Short createFromLong(long l) {
/* 61 */     if (this.jdbcCompliantTruncationForReads && (l < -32768L || l > 32767L)) {
/* 62 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() }));
/*    */     }
/* 64 */     return Short.valueOf((short)(int)l);
/*    */   }
/*    */ 
/*    */   
/*    */   public Short createFromBigDecimal(BigDecimal d) {
/* 69 */     if (this.jdbcCompliantTruncationForReads && (d
/* 70 */       .compareTo(Constants.BIG_DECIMAL_MIN_SHORT_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_SHORT_VALUE) > 0)) {
/* 71 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() }));
/*    */     }
/* 73 */     return Short.valueOf((short)(int)d.longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Short createFromDouble(double d) {
/* 78 */     if (this.jdbcCompliantTruncationForReads && (d < -32768.0D || d > 32767.0D)) {
/* 79 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() }));
/*    */     }
/* 81 */     return Short.valueOf((short)(int)d);
/*    */   }
/*    */ 
/*    */   
/*    */   public Short createFromBit(byte[] bytes, int offset, int length) {
/* 86 */     long l = DataTypeUtil.bitToLong(bytes, offset, length);
/* 87 */     if (this.jdbcCompliantTruncationForReads && l >> 16L != 0L) {
/* 88 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() }));
/*    */     }
/* 90 */     return Short.valueOf((short)(int)l);
/*    */   }
/*    */   
/*    */   public String getTargetTypeName() {
/* 94 */     return Short.class.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\ShortValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */