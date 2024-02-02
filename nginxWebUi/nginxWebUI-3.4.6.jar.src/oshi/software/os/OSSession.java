/*    */ package oshi.software.os;
/*    */ 
/*    */ import java.time.Instant;
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.ZoneId;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import oshi.annotation.concurrent.Immutable;
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
/*    */ @Immutable
/*    */ public class OSSession
/*    */ {
/* 39 */   private static final DateTimeFormatter LOGIN_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
/*    */   
/*    */   private final String userName;
/*    */   private final String terminalDevice;
/*    */   private final long loginTime;
/*    */   private final String host;
/*    */   
/*    */   public OSSession(String userName, String terminalDevice, long loginTime, String host) {
/* 47 */     this.userName = userName;
/* 48 */     this.terminalDevice = terminalDevice;
/* 49 */     this.loginTime = loginTime;
/* 50 */     this.host = host;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUserName() {
/* 59 */     return this.userName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getTerminalDevice() {
/* 68 */     return this.terminalDevice;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getLoginTime() {
/* 77 */     return this.loginTime;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getHost() {
/* 87 */     return this.host;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 93 */     String loginStr = (this.loginTime == 0L) ? "No login" : LocalDateTime.ofInstant(Instant.ofEpochMilli(this.loginTime), ZoneId.systemDefault()).format(LOGIN_FORMAT);
/* 94 */     String hostStr = "";
/* 95 */     if (!this.host.isEmpty() && !this.host.equals("::") && !this.host.equals("0.0.0.0")) {
/* 96 */       hostStr = ", (" + this.host + ")";
/*    */     }
/* 98 */     return String.format("%s, %s, %s%s", new Object[] { this.userName, this.terminalDevice, loginStr, hostStr });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\OSSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */