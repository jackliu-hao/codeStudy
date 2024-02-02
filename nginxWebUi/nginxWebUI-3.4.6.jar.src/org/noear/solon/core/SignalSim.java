/*    */ package org.noear.solon.core;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SignalSim
/*    */   implements Signal
/*    */ {
/*    */   private int port;
/*    */   private String protocol;
/*    */   private SignalType type;
/*    */   private String name;
/*    */   
/*    */   public String name() {
/* 15 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public int port() {
/* 20 */     return this.port;
/*    */   }
/*    */ 
/*    */   
/*    */   public String protocol() {
/* 25 */     return this.protocol;
/*    */   }
/*    */ 
/*    */   
/*    */   public SignalType type() {
/* 30 */     return this.type;
/*    */   }
/*    */   
/*    */   public SignalSim(String name, int port, String protocol, SignalType type) {
/* 34 */     this.name = name;
/* 35 */     this.port = port;
/* 36 */     this.protocol = protocol.toLowerCase();
/* 37 */     this.type = type;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\SignalSim.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */