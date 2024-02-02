/*    */ package com.mysql.cj.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.conf.PropertySet;
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
/*    */ 
/*    */ public class CJConnectionFeatureNotAvailableException
/*    */   extends CJCommunicationsException
/*    */ {
/*    */   private static final long serialVersionUID = -4129847384681995107L;
/*    */   
/*    */   public CJConnectionFeatureNotAvailableException() {}
/*    */   
/*    */   public CJConnectionFeatureNotAvailableException(PropertySet propertySet, ServerSession serverSession, PacketSentTimeHolder packetSentTimeHolder, Exception underlyingException) {
/* 47 */     super(underlyingException);
/* 48 */     init(propertySet, serverSession, packetSentTimeHolder, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 53 */     return Messages.getString("ConnectionFeatureNotAvailableException.0");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\CJConnectionFeatureNotAvailableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */