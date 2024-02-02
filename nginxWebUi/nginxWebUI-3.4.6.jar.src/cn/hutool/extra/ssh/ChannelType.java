/*    */ package cn.hutool.extra.ssh;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum ChannelType
/*    */ {
/* 11 */   SESSION("session"),
/*    */   
/* 13 */   SHELL("shell"),
/*    */   
/* 15 */   EXEC("exec"),
/*    */   
/* 17 */   X11("x11"),
/*    */   
/* 19 */   AGENT_FORWARDING("auth-agent@openssh.com"),
/*    */   
/* 21 */   DIRECT_TCPIP("direct-tcpip"),
/*    */   
/* 23 */   FORWARDED_TCPIP("forwarded-tcpip"),
/*    */   
/* 25 */   SFTP("sftp"),
/*    */   
/* 27 */   SUBSYSTEM("subsystem");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final String value;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   ChannelType(String value) {
/* 38 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 47 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ssh\ChannelType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */