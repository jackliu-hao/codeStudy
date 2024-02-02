/*     */ package com.mysql.cj.protocol.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ResultsetRow;
/*     */ import com.mysql.cj.protocol.ValueDecoder;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.ValueFactory;
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
/*     */ public abstract class AbstractResultsetRow
/*     */   implements ResultsetRow
/*     */ {
/*     */   protected ExceptionInterceptor exceptionInterceptor;
/*     */   protected ColumnDefinition metadata;
/*     */   protected ValueDecoder valueDecoder;
/*     */   protected boolean wasNull;
/*     */   
/*     */   protected AbstractResultsetRow(ExceptionInterceptor exceptionInterceptor) {
/*  47 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
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
/*     */   private <T> T decodeAndCreateReturnValue(int columnIndex, byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*  78 */     Field f = this.metadata.getFields()[columnIndex];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     switch (f.getMysqlTypeId()) {
/*     */       case 12:
/*  86 */         return (T)this.valueDecoder.decodeDatetime(bytes, offset, length, f.getDecimals(), vf);
/*     */       
/*     */       case 7:
/*  89 */         return (T)this.valueDecoder.decodeTimestamp(bytes, offset, length, f.getDecimals(), vf);
/*     */       
/*     */       case 10:
/*  92 */         return (T)this.valueDecoder.decodeDate(bytes, offset, length, vf);
/*     */       
/*     */       case 11:
/*  95 */         return (T)this.valueDecoder.decodeTime(bytes, offset, length, f.getDecimals(), vf);
/*     */       
/*     */       case 1:
/*  98 */         return f.isUnsigned() ? (T)this.valueDecoder.decodeUInt1(bytes, offset, length, vf) : (T)this.valueDecoder.decodeInt1(bytes, offset, length, vf);
/*     */       
/*     */       case 13:
/* 101 */         return (T)this.valueDecoder.decodeYear(bytes, offset, length, vf);
/*     */       
/*     */       case 2:
/* 104 */         return f.isUnsigned() ? (T)this.valueDecoder.decodeUInt2(bytes, offset, length, vf) : (T)this.valueDecoder.decodeInt2(bytes, offset, length, vf);
/*     */       
/*     */       case 3:
/* 107 */         return f.isUnsigned() ? (T)this.valueDecoder.decodeUInt4(bytes, offset, length, vf) : (T)this.valueDecoder.decodeInt4(bytes, offset, length, vf);
/*     */       
/*     */       case 9:
/* 110 */         return (T)this.valueDecoder.decodeInt4(bytes, offset, length, vf);
/*     */       
/*     */       case 8:
/* 113 */         return f.isUnsigned() ? (T)this.valueDecoder.decodeUInt8(bytes, offset, length, vf) : (T)this.valueDecoder.decodeInt8(bytes, offset, length, vf);
/*     */       
/*     */       case 4:
/* 116 */         return (T)this.valueDecoder.decodeFloat(bytes, offset, length, vf);
/*     */       
/*     */       case 5:
/* 119 */         return (T)this.valueDecoder.decodeDouble(bytes, offset, length, vf);
/*     */       
/*     */       case 0:
/*     */       case 246:
/* 123 */         return (T)this.valueDecoder.decodeDecimal(bytes, offset, length, vf);
/*     */       
/*     */       case 15:
/*     */       case 245:
/*     */       case 247:
/*     */       case 249:
/*     */       case 250:
/*     */       case 251:
/*     */       case 252:
/*     */       case 253:
/*     */       case 254:
/*     */       case 255:
/* 135 */         return (T)this.valueDecoder.decodeByteArray(bytes, offset, length, f, vf);
/*     */       
/*     */       case 248:
/* 138 */         return (T)this.valueDecoder.decodeSet(bytes, offset, length, f, vf);
/*     */       
/*     */       case 16:
/* 141 */         return (T)this.valueDecoder.decodeBit(bytes, offset, length, vf);
/*     */       
/*     */       case 6:
/* 144 */         return (T)vf.createFromNull();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 149 */     switch (f.getMysqlType()) {
/*     */       case TINYINT:
/* 151 */         return (T)this.valueDecoder.decodeInt1(bytes, offset, length, vf);
/*     */       case TINYINT_UNSIGNED:
/* 153 */         return (T)this.valueDecoder.decodeUInt1(bytes, offset, length, vf);
/*     */       case SMALLINT:
/* 155 */         return (T)this.valueDecoder.decodeInt2(bytes, offset, length, vf);
/*     */       case YEAR:
/* 157 */         return (T)this.valueDecoder.decodeYear(bytes, offset, length, vf);
/*     */       case SMALLINT_UNSIGNED:
/* 159 */         return (T)this.valueDecoder.decodeUInt2(bytes, offset, length, vf);
/*     */       case INT:
/*     */       case MEDIUMINT:
/* 162 */         return (T)this.valueDecoder.decodeInt4(bytes, offset, length, vf);
/*     */       case INT_UNSIGNED:
/*     */       case MEDIUMINT_UNSIGNED:
/* 165 */         return (T)this.valueDecoder.decodeUInt4(bytes, offset, length, vf);
/*     */       case BIGINT:
/* 167 */         return (T)this.valueDecoder.decodeInt8(bytes, offset, length, vf);
/*     */       case BIGINT_UNSIGNED:
/* 169 */         return (T)this.valueDecoder.decodeUInt8(bytes, offset, length, vf);
/*     */       case FLOAT:
/*     */       case FLOAT_UNSIGNED:
/* 172 */         return (T)this.valueDecoder.decodeFloat(bytes, offset, length, vf);
/*     */       case DOUBLE:
/*     */       case DOUBLE_UNSIGNED:
/* 175 */         return (T)this.valueDecoder.decodeDouble(bytes, offset, length, vf);
/*     */       case DECIMAL:
/*     */       case DECIMAL_UNSIGNED:
/* 178 */         return (T)this.valueDecoder.decodeDecimal(bytes, offset, length, vf);
/*     */       
/*     */       case BOOLEAN:
/*     */       case VARBINARY:
/*     */       case VARCHAR:
/*     */       case BINARY:
/*     */       case CHAR:
/*     */       case TINYBLOB:
/*     */       case BLOB:
/*     */       case MEDIUMBLOB:
/*     */       case LONGBLOB:
/*     */       case TINYTEXT:
/*     */       case TEXT:
/*     */       case MEDIUMTEXT:
/*     */       case LONGTEXT:
/*     */       case JSON:
/*     */       case ENUM:
/*     */       case SET:
/*     */       case GEOMETRY:
/*     */       case UNKNOWN:
/* 198 */         return (T)this.valueDecoder.decodeByteArray(bytes, offset, length, f, vf);
/*     */       
/*     */       case BIT:
/* 201 */         return (T)this.valueDecoder.decodeBit(bytes, offset, length, vf);
/*     */       
/*     */       case DATETIME:
/*     */       case TIMESTAMP:
/* 205 */         return (T)this.valueDecoder.decodeTimestamp(bytes, offset, length, f.getDecimals(), vf);
/*     */       case DATE:
/* 207 */         return (T)this.valueDecoder.decodeDate(bytes, offset, length, vf);
/*     */       case TIME:
/* 209 */         return (T)this.valueDecoder.decodeTime(bytes, offset, length, f.getDecimals(), vf);
/*     */       
/*     */       case NULL:
/* 212 */         return (T)vf.createFromNull();
/*     */     } 
/*     */ 
/*     */     
/* 216 */     throw new DataReadException(Messages.getString("ResultSet.UnknownSourceType"));
/*     */   }
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
/*     */   protected <T> T getValueFromBytes(int columnIndex, byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 238 */     if (getNull(columnIndex)) {
/* 239 */       return (T)vf.createFromNull();
/*     */     }
/*     */ 
/*     */     
/* 243 */     T retVal = decodeAndCreateReturnValue(columnIndex, bytes, offset, length, vf);
/* 244 */     this.wasNull = (retVal == null);
/* 245 */     return retVal;
/*     */   }
/*     */ 
/*     */   
/*     */   public Row setMetadata(ColumnDefinition f) {
/* 250 */     this.metadata = f;
/*     */     
/* 252 */     return (Row)this;
/*     */   }
/*     */   
/*     */   public boolean wasNull() {
/* 256 */     return this.wasNull;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\result\AbstractResultsetRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */