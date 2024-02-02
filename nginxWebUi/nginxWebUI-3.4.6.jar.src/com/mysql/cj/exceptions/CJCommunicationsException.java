/*    */ package com.mysql.cj.exceptions;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.protocol.PacketReceivedTimeHolder;
/*    */ import com.mysql.cj.protocol.PacketSentTimeHolder;
/*    */ import com.mysql.cj.protocol.ServerSession;
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
/*    */ public class CJCommunicationsException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 344035358493554245L;
/*    */   
/*    */   public CJCommunicationsException() {}
/*    */   
/*    */   public CJCommunicationsException(String message) {
/* 46 */     super(message);
/*    */   }
/*    */   
/*    */   public CJCommunicationsException(String message, Throwable cause) {
/* 50 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public CJCommunicationsException(Throwable cause) {
/* 54 */     super(cause);
/*    */   }
/*    */   
/*    */   protected CJCommunicationsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
/* 58 */     super(message, cause, enableSuppression, writableStackTrace);
/*    */   }
/*    */ 
/*    */   
/*    */   public void init(PropertySet propertySet, ServerSession serverSession, PacketSentTimeHolder packetSentTimeHolder, PacketReceivedTimeHolder packetReceivedTimeHolder) {
/* 63 */     this.exceptionMessage = ExceptionFactory.createLinkFailureMessageBasedOnHeuristics(propertySet, serverSession, packetSentTimeHolder, packetReceivedTimeHolder, 
/* 64 */         getCause());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\CJCommunicationsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */