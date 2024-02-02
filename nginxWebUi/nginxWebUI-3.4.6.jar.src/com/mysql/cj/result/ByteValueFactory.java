/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Constants;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.NumberOutOfRange;
/*     */ import com.mysql.cj.util.DataTypeUtil;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteValueFactory
/*     */   extends DefaultValueFactory<Byte>
/*     */ {
/*     */   public ByteValueFactory(PropertySet pset) {
/*  49 */     super(pset);
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte createFromBigInteger(BigInteger i) {
/*  54 */     if (this.jdbcCompliantTruncationForReads && (i
/*  55 */       .compareTo(Constants.BIG_INTEGER_MIN_BYTE_VALUE) < 0 || i.compareTo(Constants.BIG_INTEGER_MAX_BYTE_VALUE) > 0)) {
/*  56 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { i, getTargetTypeName() }));
/*     */     }
/*  58 */     return Byte.valueOf((byte)i.intValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte createFromLong(long l) {
/*  63 */     if (this.jdbcCompliantTruncationForReads && (l < -128L || l > 127L)) {
/*  64 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() }));
/*     */     }
/*  66 */     return Byte.valueOf((byte)(int)l);
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte createFromBigDecimal(BigDecimal d) {
/*  71 */     if (this.jdbcCompliantTruncationForReads && (d
/*  72 */       .compareTo(Constants.BIG_DECIMAL_MIN_BYTE_VALUE) < 0 || d.compareTo(Constants.BIG_DECIMAL_MAX_BYTE_VALUE) > 0)) {
/*  73 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { d, getTargetTypeName() }));
/*     */     }
/*  75 */     return Byte.valueOf((byte)(int)d.longValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte createFromDouble(double d) {
/*  80 */     if (this.jdbcCompliantTruncationForReads && (d < -128.0D || d > 127.0D)) {
/*  81 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Double.valueOf(d), getTargetTypeName() }));
/*     */     }
/*  83 */     return Byte.valueOf((byte)(int)d);
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte createFromBit(byte[] bytes, int offset, int length) {
/*  88 */     long l = DataTypeUtil.bitToLong(bytes, offset, length);
/*  89 */     if (this.jdbcCompliantTruncationForReads && l >> 8L != 0L) {
/*  90 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { Long.valueOf(l).toString(), getTargetTypeName() }));
/*     */     }
/*  92 */     return Byte.valueOf((byte)(int)l);
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte createFromYear(long l) {
/*  97 */     return createFromLong(l);
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/* 101 */     return Byte.class.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte createFromBytes(byte[] bytes, int offset, int length, Field f) {
/* 106 */     if (length == 0 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()).booleanValue()) {
/* 107 */       return Byte.valueOf((byte)0);
/*     */     }
/* 109 */     String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
/* 110 */     byte[] newBytes = s.getBytes();
/*     */     
/* 112 */     if (this.jdbcCompliantTruncationForReads && newBytes.length != 1) {
/* 113 */       throw new NumberOutOfRange(Messages.getString("ResultSet.NumberOutOfRange", new Object[] { s, getTargetTypeName() }));
/*     */     }
/* 115 */     return Byte.valueOf(newBytes[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\ByteValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */