/*     */ package org.h2.util;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
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
/*     */ public final class NetworkConnectionInfo
/*     */ {
/*     */   private final String server;
/*     */   private final byte[] clientAddr;
/*     */   private final int clientPort;
/*     */   private final String clientInfo;
/*     */   
/*     */   public NetworkConnectionInfo(String paramString1, String paramString2, int paramInt) throws UnknownHostException {
/*  37 */     this(paramString1, InetAddress.getByName(paramString2).getAddress(), paramInt, null);
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
/*     */   public NetworkConnectionInfo(String paramString1, byte[] paramArrayOfbyte, int paramInt, String paramString2) {
/*  53 */     this.server = paramString1;
/*  54 */     this.clientAddr = paramArrayOfbyte;
/*  55 */     this.clientPort = paramInt;
/*  56 */     this.clientInfo = paramString2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getServer() {
/*  65 */     return this.server;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getClientAddr() {
/*  74 */     return this.clientAddr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getClientPort() {
/*  83 */     return this.clientPort;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getClientInfo() {
/*  92 */     return this.clientInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getClient() {
/* 101 */     return NetUtils.ipToShortForm(new StringBuilder(), this.clientAddr, true).append(':').append(this.clientPort).toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\NetworkConnectionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */