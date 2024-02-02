/*    */ package com.mysql.cj.jdbc.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.jdbc.JdbcConnection;
/*    */ import com.mysql.cj.protocol.PacketSentTimeHolder;
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
/*    */ public class ConnectionFeatureNotAvailableException
/*    */   extends CommunicationsException
/*    */ {
/*    */   private static final long serialVersionUID = 8315412078945570018L;
/*    */   
/*    */   public ConnectionFeatureNotAvailableException(JdbcConnection conn, PacketSentTimeHolder packetSentTimeHolder, Exception underlyingException) {
/* 46 */     super(conn, packetSentTimeHolder, null, underlyingException);
/*    */   }
/*    */   
/*    */   public ConnectionFeatureNotAvailableException(String message, Throwable underlyingException) {
/* 50 */     super(message, underlyingException);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 55 */     return Messages.getString("ConnectionFeatureNotAvailableException.0");
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSQLState() {
/* 60 */     return "01S00";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\ConnectionFeatureNotAvailableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */