/*     */ package cn.hutool.extra.ssh;
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
/*     */ public class Connector
/*     */ {
/*     */   private String host;
/*     */   private int port;
/*     */   private String user;
/*     */   private String password;
/*     */   private String group;
/*     */   
/*     */   public Connector() {}
/*     */   
/*     */   public Connector(String user, String password, String group) {
/*  28 */     this.user = user;
/*  29 */     this.password = password;
/*  30 */     this.group = group;
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
/*     */   public Connector(String host, int port, String user, String password) {
/*  42 */     this.host = host;
/*  43 */     this.port = port;
/*  44 */     this.user = user;
/*  45 */     this.password = password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/*  55 */     return this.host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHost(String host) {
/*  64 */     this.host = host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/*  73 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPort(int port) {
/*  82 */     this.port = port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUser() {
/*  91 */     return this.user;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUser(String name) {
/* 100 */     this.user = name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPassword() {
/* 109 */     return this.password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPassword(String password) {
/* 118 */     this.password = password;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getGroup() {
/* 127 */     return this.group;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGroup(String group) {
/* 136 */     this.group = group;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 144 */     return "Connector [host=" + this.host + ", port=" + this.port + ", user=" + this.user + ", password=" + this.password + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ssh\Connector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */