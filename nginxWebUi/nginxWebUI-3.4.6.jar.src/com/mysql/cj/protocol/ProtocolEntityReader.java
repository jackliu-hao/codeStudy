/*    */ package com.mysql.cj.protocol;
/*    */ 
/*    */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface ProtocolEntityReader<T extends ProtocolEntity, M extends Message>
/*    */ {
/*    */   default T read(ProtocolEntityFactory<T, M> sf) throws IOException {
/* 48 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
/*    */   }
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
/*    */   default T read(int maxRows, boolean streamResults, M resultPacket, ColumnDefinition metadata, ProtocolEntityFactory<T, M> protocolEntityFactory) throws IOException {
/* 74 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not allowed");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ProtocolEntityReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */