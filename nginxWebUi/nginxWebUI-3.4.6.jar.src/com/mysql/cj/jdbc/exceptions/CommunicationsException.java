/*    */ package com.mysql.cj.jdbc.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.StreamingNotifiable;
/*    */ import com.mysql.cj.jdbc.JdbcConnection;
/*    */ import com.mysql.cj.protocol.PacketReceivedTimeHolder;
/*    */ import com.mysql.cj.protocol.PacketSentTimeHolder;
/*    */ import java.sql.SQLRecoverableException;
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
/*    */ public class CommunicationsException
/*    */   extends SQLRecoverableException
/*    */   implements StreamingNotifiable
/*    */ {
/*    */   private static final long serialVersionUID = 4317904269000988676L;
/*    */   private String exceptionMessage;
/*    */   
/*    */   public CommunicationsException(JdbcConnection conn, PacketSentTimeHolder packetSentTimeHolder, PacketReceivedTimeHolder packetReceivedTimeHolder, Exception underlyingException) {
/* 56 */     this(ExceptionFactory.createLinkFailureMessageBasedOnHeuristics((PropertySet)conn.getPropertySet(), conn.getSession().getServerSession(), packetSentTimeHolder, packetReceivedTimeHolder, underlyingException), underlyingException);
/*    */   }
/*    */ 
/*    */   
/*    */   public CommunicationsException(String message, Throwable underlyingException) {
/* 61 */     this.exceptionMessage = message;
/*    */     
/* 63 */     if (underlyingException != null) {
/* 64 */       initCause(underlyingException);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 70 */     return this.exceptionMessage;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSQLState() {
/* 75 */     return "08S01";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void setWasStreamingResults() {
/* 81 */     this.exceptionMessage = Messages.getString("CommunicationsException.ClientWasStreaming");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\CommunicationsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */