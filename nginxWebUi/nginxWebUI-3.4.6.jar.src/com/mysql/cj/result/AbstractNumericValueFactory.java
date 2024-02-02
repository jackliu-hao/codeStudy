/*    */ package com.mysql.cj.result;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.conf.PropertyKey;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.DataConversionException;
/*    */ import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
/*    */ import com.mysql.cj.util.StringUtils;
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
/*    */ public abstract class AbstractNumericValueFactory<T>
/*    */   extends DefaultValueFactory<T>
/*    */ {
/*    */   public AbstractNumericValueFactory(PropertySet pset) {
/* 42 */     super(pset);
/*    */   }
/*    */ 
/*    */   
/*    */   public T createFromBytes(byte[] bytes, int offset, int length, Field f) {
/* 47 */     if (length == 0 && ((Boolean)this.pset.getBooleanProperty(PropertyKey.emptyStringsConvertToZero).getValue()).booleanValue()) {
/* 48 */       return createFromLong(0L);
/*    */     }
/*    */     
/* 51 */     String s = StringUtils.toString(bytes, offset, length, f.getEncoding());
/* 52 */     byte[] newBytes = s.getBytes();
/*    */     
/* 54 */     if (s.contains("e") || s.contains("E") || s.matches("-?\\d*\\.\\d*"))
/*    */     {
/* 56 */       return createFromDouble(MysqlTextValueDecoder.getDouble(newBytes, 0, newBytes.length).doubleValue()); } 
/* 57 */     if (s.matches("-?\\d+")) {
/*    */       
/* 59 */       if (s.charAt(0) == '-' || (length <= 19 && newBytes[0] >= 48 && newBytes[0] <= 56))
/*    */       {
/* 61 */         return createFromLong(MysqlTextValueDecoder.getLong(newBytes, 0, newBytes.length));
/*    */       }
/* 63 */       return createFromBigInteger(MysqlTextValueDecoder.getBigInteger(newBytes, 0, newBytes.length));
/*    */     } 
/* 65 */     throw new DataConversionException(Messages.getString("ResultSet.UnableToInterpretString", new Object[] { s }));
/*    */   }
/*    */ 
/*    */   
/*    */   public T createFromYear(long l) {
/* 70 */     return createFromLong(l);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\AbstractNumericValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */