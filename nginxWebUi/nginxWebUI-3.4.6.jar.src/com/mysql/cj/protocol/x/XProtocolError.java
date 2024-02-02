/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.mysql.cj.exceptions.CJException;
/*    */ import com.mysql.cj.x.protobuf.Mysqlx;
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
/*    */ public class XProtocolError
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 6991120628391138584L;
/*    */   private Mysqlx.Error msg;
/*    */   
/*    */   public XProtocolError(String message) {
/* 47 */     super(message);
/*    */   }
/*    */   
/*    */   public XProtocolError(Mysqlx.Error msg) {
/* 51 */     super(getFullErrorDescription(msg));
/* 52 */     this.msg = msg;
/*    */   }
/*    */   
/*    */   public XProtocolError(XProtocolError fromOtherThread) {
/* 56 */     super(getFullErrorDescription(fromOtherThread.msg), (Throwable)fromOtherThread);
/* 57 */     this.msg = fromOtherThread.msg;
/*    */   }
/*    */   
/*    */   public XProtocolError(String message, Throwable t) {
/* 61 */     super(message, t);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String getFullErrorDescription(Mysqlx.Error msg) {
/* 72 */     StringBuilder stringMessage = new StringBuilder("ERROR ");
/* 73 */     stringMessage.append(msg.getCode());
/* 74 */     stringMessage.append(" (");
/* 75 */     stringMessage.append(msg.getSqlState());
/* 76 */     stringMessage.append(") ");
/* 77 */     stringMessage.append(msg.getMsg());
/* 78 */     return stringMessage.toString();
/*    */   }
/*    */   
/*    */   public int getErrorCode() {
/* 82 */     return (this.msg == null) ? getVendorCode() : this.msg.getCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSQLState() {
/* 87 */     return (this.msg == null) ? super.getSQLState() : this.msg.getSqlState();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XProtocolError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */