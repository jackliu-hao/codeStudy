/*     */ package org.apache.http.config;
/*     */ 
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class SocketConfig
/*     */   implements Cloneable
/*     */ {
/*  42 */   public static final SocketConfig DEFAULT = (new Builder()).build();
/*     */ 
/*     */   
/*     */   private final int soTimeout;
/*     */   
/*     */   private final boolean soReuseAddress;
/*     */   
/*     */   private final int soLinger;
/*     */   
/*     */   private final boolean soKeepAlive;
/*     */   
/*     */   private final boolean tcpNoDelay;
/*     */   
/*     */   private final int sndBufSize;
/*     */   
/*     */   private final int rcvBufSize;
/*     */   
/*     */   private final int backlogSize;
/*     */ 
/*     */   
/*     */   SocketConfig(int soTimeout, boolean soReuseAddress, int soLinger, boolean soKeepAlive, boolean tcpNoDelay, int sndBufSize, int rcvBufSize, int backlogSize) {
/*  63 */     this.soTimeout = soTimeout;
/*  64 */     this.soReuseAddress = soReuseAddress;
/*  65 */     this.soLinger = soLinger;
/*  66 */     this.soKeepAlive = soKeepAlive;
/*  67 */     this.tcpNoDelay = tcpNoDelay;
/*  68 */     this.sndBufSize = sndBufSize;
/*  69 */     this.rcvBufSize = rcvBufSize;
/*  70 */     this.backlogSize = backlogSize;
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
/*     */   public int getSoTimeout() {
/*  83 */     return this.soTimeout;
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
/*     */   public boolean isSoReuseAddress() {
/*  97 */     return this.soReuseAddress;
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
/*     */   public int getSoLinger() {
/* 111 */     return this.soLinger;
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
/*     */   public boolean isSoKeepAlive() {
/* 125 */     return this.soKeepAlive;
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
/*     */   public boolean isTcpNoDelay() {
/* 139 */     return this.tcpNoDelay;
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
/*     */   public int getSndBufSize() {
/* 154 */     return this.sndBufSize;
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
/*     */   public int getRcvBufSize() {
/* 169 */     return this.rcvBufSize;
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
/*     */   public int getBacklogSize() {
/* 182 */     return this.backlogSize;
/*     */   }
/*     */ 
/*     */   
/*     */   protected SocketConfig clone() throws CloneNotSupportedException {
/* 187 */     return (SocketConfig)super.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 192 */     StringBuilder builder = new StringBuilder();
/* 193 */     builder.append("[soTimeout=").append(this.soTimeout).append(", soReuseAddress=").append(this.soReuseAddress).append(", soLinger=").append(this.soLinger).append(", soKeepAlive=").append(this.soKeepAlive).append(", tcpNoDelay=").append(this.tcpNoDelay).append(", sndBufSize=").append(this.sndBufSize).append(", rcvBufSize=").append(this.rcvBufSize).append(", backlogSize=").append(this.backlogSize).append("]");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 202 */     return builder.toString();
/*     */   }
/*     */   
/*     */   public static Builder custom() {
/* 206 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static Builder copy(SocketConfig config) {
/* 210 */     Args.notNull(config, "Socket config");
/* 211 */     return (new Builder()).setSoTimeout(config.getSoTimeout()).setSoReuseAddress(config.isSoReuseAddress()).setSoLinger(config.getSoLinger()).setSoKeepAlive(config.isSoKeepAlive()).setTcpNoDelay(config.isTcpNoDelay()).setSndBufSize(config.getSndBufSize()).setRcvBufSize(config.getRcvBufSize()).setBacklogSize(config.getBacklogSize());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     private int soTimeout;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean soReuseAddress;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 234 */     private int soLinger = -1;
/*     */     private boolean soKeepAlive;
/*     */     private boolean tcpNoDelay = true;
/*     */     
/*     */     public Builder setSoTimeout(int soTimeout) {
/* 239 */       this.soTimeout = soTimeout;
/* 240 */       return this;
/*     */     }
/*     */     private int sndBufSize; private int rcvBufSize; private int backlogSize;
/*     */     public Builder setSoReuseAddress(boolean soReuseAddress) {
/* 244 */       this.soReuseAddress = soReuseAddress;
/* 245 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setSoLinger(int soLinger) {
/* 249 */       this.soLinger = soLinger;
/* 250 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setSoKeepAlive(boolean soKeepAlive) {
/* 254 */       this.soKeepAlive = soKeepAlive;
/* 255 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setTcpNoDelay(boolean tcpNoDelay) {
/* 259 */       this.tcpNoDelay = tcpNoDelay;
/* 260 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setSndBufSize(int sndBufSize) {
/* 267 */       this.sndBufSize = sndBufSize;
/* 268 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRcvBufSize(int rcvBufSize) {
/* 275 */       this.rcvBufSize = rcvBufSize;
/* 276 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setBacklogSize(int backlogSize) {
/* 283 */       this.backlogSize = backlogSize;
/* 284 */       return this;
/*     */     }
/*     */     
/*     */     public SocketConfig build() {
/* 288 */       return new SocketConfig(this.soTimeout, this.soReuseAddress, this.soLinger, this.soKeepAlive, this.tcpNoDelay, this.sndBufSize, this.rcvBufSize, this.backlogSize);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\config\SocketConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */