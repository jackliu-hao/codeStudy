/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*     */ import com.mysql.cj.exceptions.SSLParamsException;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.protocol.AbstractSocketConnection;
/*     */ import com.mysql.cj.protocol.FullReadInputStream;
/*     */ import com.mysql.cj.protocol.PacketSentTimeHolder;
/*     */ import com.mysql.cj.protocol.ReadAheadInputStream;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.protocol.SocketConnection;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.Socket;
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
/*     */ public class NativeSocketConnection
/*     */   extends AbstractSocketConnection
/*     */   implements SocketConnection
/*     */ {
/*     */   public void connect(String hostName, int portNumber, PropertySet propSet, ExceptionInterceptor excInterceptor, Log log, int loginTimeout) {
/*     */     try {
/*     */       InputStream rawInputStream;
/*  57 */       this.port = portNumber;
/*  58 */       this.host = hostName;
/*  59 */       this.propertySet = propSet;
/*  60 */       this.exceptionInterceptor = excInterceptor;
/*     */       
/*  62 */       this.socketFactory = createSocketFactory(propSet.getStringProperty(PropertyKey.socketFactory).getStringValue());
/*  63 */       this.mysqlSocket = (Socket)this.socketFactory.connect(this.host, this.port, propSet, loginTimeout);
/*     */       
/*  65 */       int socketTimeout = ((Integer)propSet.getIntegerProperty(PropertyKey.socketTimeout).getValue()).intValue();
/*  66 */       if (socketTimeout != 0) {
/*     */         try {
/*  68 */           this.mysqlSocket.setSoTimeout(socketTimeout);
/*  69 */         } catch (Exception exception) {}
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  74 */       this.socketFactory.beforeHandshake();
/*     */ 
/*     */       
/*  77 */       if (((Boolean)propSet.getBooleanProperty(PropertyKey.useReadAheadInput).getValue()).booleanValue()) {
/*     */         
/*  79 */         ReadAheadInputStream readAheadInputStream = new ReadAheadInputStream(this.mysqlSocket.getInputStream(), 16384, ((Boolean)propSet.getBooleanProperty(PropertyKey.traceProtocol).getValue()).booleanValue(), log);
/*  80 */       } else if (((Boolean)propSet.getBooleanProperty(PropertyKey.useUnbufferedInput).getValue()).booleanValue()) {
/*  81 */         rawInputStream = this.mysqlSocket.getInputStream();
/*     */       } else {
/*  83 */         rawInputStream = new BufferedInputStream(this.mysqlSocket.getInputStream(), 16384);
/*     */       } 
/*     */       
/*  86 */       this.mysqlInput = new FullReadInputStream(rawInputStream);
/*  87 */       this.mysqlOutput = new BufferedOutputStream(this.mysqlSocket.getOutputStream(), 16384);
/*  88 */     } catch (IOException ioEx) {
/*  89 */       throw ExceptionFactory.createCommunicationsException(propSet, null, new PacketSentTimeHolder() {  }, null, ioEx, 
/*  90 */           getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void performTlsHandshake(ServerSession serverSession) throws SSLParamsException, FeatureNotAvailableException, IOException {
/*  96 */     performTlsHandshake(serverSession, (Log)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void performTlsHandshake(ServerSession serverSession, Log log) throws SSLParamsException, FeatureNotAvailableException, IOException {
/* 101 */     this.mysqlSocket = (Socket)this.socketFactory.performTlsHandshake(this, serverSession, log);
/*     */     
/* 103 */     this
/*     */       
/* 105 */       .mysqlInput = new FullReadInputStream(((Boolean)this.propertySet.getBooleanProperty(PropertyKey.useUnbufferedInput).getValue()).booleanValue() ? getMysqlSocket().getInputStream() : new BufferedInputStream(getMysqlSocket().getInputStream(), 16384));
/*     */     
/* 107 */     this.mysqlOutput = new BufferedOutputStream(getMysqlSocket().getOutputStream(), 16384);
/* 108 */     this.mysqlOutput.flush();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativeSocketConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */