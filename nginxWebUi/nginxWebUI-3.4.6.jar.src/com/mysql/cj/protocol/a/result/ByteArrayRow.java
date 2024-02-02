/*    */ package com.mysql.cj.protocol.a.result;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import com.mysql.cj.protocol.ValueDecoder;
/*    */ import com.mysql.cj.protocol.a.MysqlTextValueDecoder;
/*    */ import com.mysql.cj.protocol.result.AbstractResultsetRow;
/*    */ import com.mysql.cj.result.ValueFactory;
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
/*    */ public class ByteArrayRow
/*    */   extends AbstractResultsetRow
/*    */ {
/*    */   byte[][] internalRowData;
/*    */   
/*    */   public ByteArrayRow(byte[][] internalRowData, ExceptionInterceptor exceptionInterceptor, ValueDecoder valueDecoder) {
/* 47 */     super(exceptionInterceptor);
/*    */     
/* 49 */     this.internalRowData = internalRowData;
/* 50 */     this.valueDecoder = valueDecoder;
/*    */   }
/*    */   
/*    */   public ByteArrayRow(byte[][] internalRowData, ExceptionInterceptor exceptionInterceptor) {
/* 54 */     super(exceptionInterceptor);
/*    */     
/* 56 */     this.internalRowData = internalRowData;
/* 57 */     this.valueDecoder = (ValueDecoder)new MysqlTextValueDecoder();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isBinaryEncoded() {
/* 62 */     return this.valueDecoder instanceof com.mysql.cj.protocol.a.MysqlBinaryValueDecoder;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getBytes(int index) {
/* 67 */     if (getNull(index)) {
/* 68 */       return null;
/*    */     }
/* 70 */     return this.internalRowData[index];
/*    */   }
/*    */ 
/*    */   
/*    */   public void setBytes(int index, byte[] value) {
/* 75 */     this.internalRowData[index] = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean getNull(int columnIndex) {
/* 80 */     this.wasNull = (this.internalRowData[columnIndex] == null);
/* 81 */     return this.wasNull;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public <T> T getValue(int columnIndex, ValueFactory<T> vf) {
/* 89 */     byte[] columnData = this.internalRowData[columnIndex];
/* 90 */     int length = (columnData == null) ? 0 : columnData.length;
/* 91 */     return (T)getValueFromBytes(columnIndex, columnData, 0, length, vf);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\ByteArrayRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */