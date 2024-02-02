/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.log.Log;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
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
/*     */ public class StandardSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*  50 */   protected String host = null;
/*     */ 
/*     */   
/*  53 */   protected int port = 3306;
/*     */ 
/*     */   
/*  56 */   protected Socket rawSocket = null;
/*     */ 
/*     */   
/*  59 */   protected Socket sslSocket = null;
/*     */ 
/*     */   
/*  62 */   protected int loginTimeoutCountdown = 0;
/*     */ 
/*     */   
/*  65 */   protected long loginTimeoutCheckTimestamp = System.currentTimeMillis();
/*     */ 
/*     */   
/*  68 */   protected int socketTimeoutBackup = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Socket createSocket(PropertySet props) {
/*  78 */     return new Socket();
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
/*     */   private void configureSocket(Socket sock, PropertySet pset) throws SocketException, IOException {
/*  95 */     sock.setTcpNoDelay(((Boolean)pset.getBooleanProperty(PropertyKey.tcpNoDelay).getValue()).booleanValue());
/*  96 */     sock.setKeepAlive(((Boolean)pset.getBooleanProperty(PropertyKey.tcpKeepAlive).getValue()).booleanValue());
/*     */     
/*  98 */     int receiveBufferSize = ((Integer)pset.getIntegerProperty(PropertyKey.tcpRcvBuf).getValue()).intValue();
/*  99 */     if (receiveBufferSize > 0) {
/* 100 */       sock.setReceiveBufferSize(receiveBufferSize);
/*     */     }
/*     */     
/* 103 */     int sendBufferSize = ((Integer)pset.getIntegerProperty(PropertyKey.tcpSndBuf).getValue()).intValue();
/* 104 */     if (sendBufferSize > 0) {
/* 105 */       sock.setSendBufferSize(sendBufferSize);
/*     */     }
/*     */     
/* 108 */     int trafficClass = ((Integer)pset.getIntegerProperty(PropertyKey.tcpTrafficClass).getValue()).intValue();
/* 109 */     if (trafficClass > 0) {
/* 110 */       sock.setTrafficClass(trafficClass);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.io.Closeable> T connect(String hostname, int portNumber, PropertySet pset, int loginTimeout) throws IOException {
/* 117 */     this.loginTimeoutCountdown = loginTimeout;
/*     */     
/* 119 */     if (pset != null) {
/* 120 */       this.host = hostname;
/*     */       
/* 122 */       this.port = portNumber;
/*     */       
/* 124 */       String localSocketHostname = (String)pset.getStringProperty(PropertyKey.localSocketAddress).getValue();
/* 125 */       InetSocketAddress localSockAddr = null;
/* 126 */       if (localSocketHostname != null && localSocketHostname.length() > 0) {
/* 127 */         localSockAddr = new InetSocketAddress(InetAddress.getByName(localSocketHostname), 0);
/*     */       }
/*     */       
/* 130 */       int connectTimeout = ((Integer)pset.getIntegerProperty(PropertyKey.connectTimeout).getValue()).intValue();
/*     */       
/* 132 */       if (this.host != null) {
/* 133 */         InetAddress[] possibleAddresses = InetAddress.getAllByName(this.host);
/*     */         
/* 135 */         if (possibleAddresses.length == 0) {
/* 136 */           throw new SocketException("No addresses for host");
/*     */         }
/*     */ 
/*     */         
/* 140 */         SocketException lastException = null;
/*     */ 
/*     */ 
/*     */         
/* 144 */         for (int i = 0; i < possibleAddresses.length; i++) {
/*     */           try {
/* 146 */             this.rawSocket = createSocket(pset);
/*     */             
/* 148 */             configureSocket(this.rawSocket, pset);
/*     */             
/* 150 */             InetSocketAddress sockAddr = new InetSocketAddress(possibleAddresses[i], this.port);
/*     */             
/* 152 */             if (localSockAddr != null) {
/* 153 */               this.rawSocket.bind(localSockAddr);
/*     */             }
/*     */             
/* 156 */             this.rawSocket.connect(sockAddr, getRealTimeout(connectTimeout));
/*     */             
/*     */             break;
/* 159 */           } catch (SocketException ex) {
/* 160 */             lastException = ex;
/* 161 */             resetLoginTimeCountdown();
/* 162 */             this.rawSocket = null;
/*     */           } 
/*     */         } 
/*     */         
/* 166 */         if (this.rawSocket == null && lastException != null) {
/* 167 */           throw lastException;
/*     */         }
/*     */         
/* 170 */         resetLoginTimeCountdown();
/*     */         
/* 172 */         this.sslSocket = this.rawSocket;
/* 173 */         return (T)this.rawSocket;
/*     */       } 
/*     */     } 
/*     */     
/* 177 */     throw new SocketException("Unable to create socket");
/*     */   }
/*     */   
/*     */   public void beforeHandshake() throws IOException {
/* 181 */     resetLoginTimeCountdown();
/* 182 */     this.socketTimeoutBackup = this.rawSocket.getSoTimeout();
/* 183 */     this.rawSocket.setSoTimeout(getRealTimeout(this.socketTimeoutBackup));
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession) throws IOException {
/* 188 */     return performTlsHandshake(socketConnection, serverSession, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession, Log log) throws IOException {
/* 194 */     this.sslSocket = ExportControlled.performTlsHandshake(this.rawSocket, socketConnection, (serverSession == null) ? null : serverSession.getServerVersion(), log);
/*     */     
/* 196 */     return (T)this.sslSocket;
/*     */   }
/*     */   
/*     */   public void afterHandshake() throws IOException {
/* 200 */     resetLoginTimeCountdown();
/* 201 */     this.rawSocket.setSoTimeout(this.socketTimeoutBackup);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void resetLoginTimeCountdown() throws SocketException {
/* 211 */     if (this.loginTimeoutCountdown > 0) {
/* 212 */       long now = System.currentTimeMillis();
/* 213 */       this.loginTimeoutCountdown = (int)(this.loginTimeoutCountdown - now - this.loginTimeoutCheckTimestamp);
/* 214 */       if (this.loginTimeoutCountdown <= 0) {
/* 215 */         throw new SocketException(Messages.getString("Connection.LoginTimeout"));
/*     */       }
/* 217 */       this.loginTimeoutCheckTimestamp = now;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getRealTimeout(int expectedTimeout) {
/* 229 */     if (this.loginTimeoutCountdown > 0 && (expectedTimeout == 0 || expectedTimeout > this.loginTimeoutCountdown)) {
/* 230 */       return this.loginTimeoutCountdown;
/*     */     }
/* 232 */     return expectedTimeout;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\StandardSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */