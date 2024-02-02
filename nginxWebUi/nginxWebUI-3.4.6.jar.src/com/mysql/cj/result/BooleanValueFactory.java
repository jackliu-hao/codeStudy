/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.Constants;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataConversionException;
/*     */ import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
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
/*     */ public class BooleanValueFactory
/*     */   extends DefaultValueFactory<Boolean>
/*     */ {
/*     */   public BooleanValueFactory(PropertySet pset) {
/*  50 */     super(pset);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean createFromLong(long l) {
/*  56 */     return Boolean.valueOf((l == -1L || l > 0L));
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean createFromBigInteger(BigInteger i) {
/*  61 */     return Boolean.valueOf((i.compareTo(Constants.BIG_INTEGER_ZERO) > 0 || i.compareTo(Constants.BIG_INTEGER_NEGATIVE_ONE) == 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean createFromDouble(double d) {
/*  68 */     return Boolean.valueOf((d > 0.0D || d == -1.0D));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean createFromBigDecimal(BigDecimal d) {
/*  74 */     return Boolean.valueOf((d.compareTo(Constants.BIG_DECIMAL_ZERO) > 0 || d.compareTo(Constants.BIG_DECIMAL_NEGATIVE_ONE) == 0));
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean createFromBit(byte[] bytes, int offset, int length) {
/*  79 */     return createFromLong(DataTypeUtil.bitToLong(bytes, offset, length));
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean createFromYear(long l) {
/*  84 */     return createFromLong(l);
/*     */   }
/*     */   
/*     */   public String getTargetTypeName() {
/*  88 */     return Boolean.class.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Boolean createFromBytes(byte[] bytes, int offset, int length, Field f) {
/*  93 */     if (length == 0 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()).booleanValue()) {
/*  94 */       return createFromLong(0L);
/*     */     }
/*     */     
/*  97 */     String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
/*  98 */     byte[] newBytes = s.getBytes();
/*     */     
/* 100 */     if (s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("T") || s.equalsIgnoreCase("true"))
/* 101 */       return createFromLong(1L); 
/* 102 */     if (s.equalsIgnoreCase("N") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("F") || s.equalsIgnoreCase("false"))
/* 103 */       return createFromLong(0L); 
/* 104 */     if (s.contains("e") || s.contains("E") || s.matches("-?\\d*\\.\\d*"))
/*     */     {
/* 106 */       return createFromDouble(MysqlTextValueDecoder.getDouble(newBytes, 0, newBytes.length).doubleValue()); } 
/* 107 */     if (s.matches("-?\\d+")) {
/*     */       
/* 109 */       if (s.charAt(0) == '-' || (length <= 19 && newBytes[0] >= 48 && newBytes[0] <= 56))
/*     */       {
/* 111 */         return createFromLong(MysqlTextValueDecoder.getLong(newBytes, 0, newBytes.length));
/*     */       }
/* 113 */       return createFromBigInteger(MysqlTextValueDecoder.getBigInteger(newBytes, 0, newBytes.length));
/*     */     } 
/* 115 */     throw new DataConversionException(Messages.getString("ResultSet.UnableToInterpretString", new Object[] { s }));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\BooleanValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */