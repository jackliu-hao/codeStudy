/*    */ package com.mysql.cj.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
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
/*    */ public class CJPacketTooBigException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 7186090399276725363L;
/*    */   
/*    */   public CJPacketTooBigException() {}
/*    */   
/*    */   public CJPacketTooBigException(String message) {
/* 46 */     super(message);
/*    */   }
/*    */   
/*    */   public CJPacketTooBigException(Throwable cause) {
/* 50 */     super(cause);
/*    */   }
/*    */   
/*    */   public CJPacketTooBigException(String message, Throwable cause) {
/* 54 */     super(message, cause);
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
/*    */   public CJPacketTooBigException(long packetSize, long maximumPacketSize) {
/* 66 */     super(Messages.getString("PacketTooBigException.0", new Object[] { Long.valueOf(packetSize), Long.valueOf(maximumPacketSize) }));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\CJPacketTooBigException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */