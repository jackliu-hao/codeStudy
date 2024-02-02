/*     */ package javax.mail;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Authenticator
/*     */ {
/*     */   private InetAddress requestingSite;
/*     */   private int requestingPort;
/*     */   private String requestingProtocol;
/*     */   private String requestingPrompt;
/*     */   private String requestingUserName;
/*     */   
/*     */   private void reset() {
/*  84 */     this.requestingSite = null;
/*  85 */     this.requestingPort = -1;
/*  86 */     this.requestingProtocol = null;
/*  87 */     this.requestingPrompt = null;
/*  88 */     this.requestingUserName = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final PasswordAuthentication requestPasswordAuthentication(InetAddress addr, int port, String protocol, String prompt, String defaultUserName) {
/* 108 */     reset();
/* 109 */     this.requestingSite = addr;
/* 110 */     this.requestingPort = port;
/* 111 */     this.requestingProtocol = protocol;
/* 112 */     this.requestingPrompt = prompt;
/* 113 */     this.requestingUserName = defaultUserName;
/* 114 */     return getPasswordAuthentication();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final InetAddress getRequestingSite() {
/* 122 */     return this.requestingSite;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int getRequestingPort() {
/* 129 */     return this.requestingPort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String getRequestingProtocol() {
/* 141 */     return this.requestingProtocol;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String getRequestingPrompt() {
/* 148 */     return this.requestingPrompt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String getDefaultUserName() {
/* 155 */     return this.requestingUserName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected PasswordAuthentication getPasswordAuthentication() {
/* 170 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\Authenticator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */