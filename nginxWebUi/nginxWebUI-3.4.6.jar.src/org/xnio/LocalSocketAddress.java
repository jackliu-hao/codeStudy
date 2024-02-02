/*    */ package org.xnio;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ import org.xnio._private.Messages;
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
/*    */ public final class LocalSocketAddress
/*    */   extends SocketAddress
/*    */ {
/*    */   private static final long serialVersionUID = -596342428809783686L;
/*    */   private final String name;
/*    */   
/*    */   public LocalSocketAddress(String name) {
/* 43 */     if (name == null) {
/* 44 */       throw Messages.msg.nullParameter("name");
/*    */     }
/* 46 */     this.name = name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 55 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\LocalSocketAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */