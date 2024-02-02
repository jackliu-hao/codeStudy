/*    */ package com.mysql.cj.jdbc.exceptions;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import java.sql.SQLException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PacketTooBigException
/*    */   extends SQLException
/*    */ {
/*    */   static final long serialVersionUID = 7248633977685452174L;
/*    */   
/*    */   public PacketTooBigException(long packetSize, long maximumPacketSize) {
/* 53 */     super(Messages.getString("PacketTooBigException.0", new Object[] { Long.valueOf(packetSize), Long.valueOf(maximumPacketSize) }), "S1000");
/*    */   }
/*    */   
/*    */   public PacketTooBigException(String message) {
/* 57 */     super(message, "S1000");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\PacketTooBigException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */