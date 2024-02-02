/*     */ package com.mysql.cj.protocol;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.UnableToConnectException;
/*     */ import com.mysql.jdbc.SocketFactoryWrapper;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ public abstract class AbstractSocketConnection
/*     */   implements SocketConnection
/*     */ {
/*  46 */   protected String host = null;
/*  47 */   protected int port = 3306;
/*  48 */   protected SocketFactory socketFactory = null;
/*  49 */   protected Socket mysqlSocket = null;
/*  50 */   protected FullReadInputStream mysqlInput = null;
/*  51 */   protected BufferedOutputStream mysqlOutput = null;
/*     */   
/*     */   protected ExceptionInterceptor exceptionInterceptor;
/*     */   protected PropertySet propertySet;
/*     */   
/*     */   public String getHost() {
/*  57 */     return this.host;
/*     */   }
/*     */   
/*     */   public int getPort() {
/*  61 */     return this.port;
/*     */   }
/*     */   
/*     */   public Socket getMysqlSocket() {
/*  65 */     return this.mysqlSocket;
/*     */   }
/*     */   
/*     */   public FullReadInputStream getMysqlInput() throws IOException {
/*  69 */     if (this.mysqlInput != null) {
/*  70 */       return this.mysqlInput;
/*     */     }
/*  72 */     throw new IOException(Messages.getString("SocketConnection.2"));
/*     */   }
/*     */   
/*     */   public void setMysqlInput(FullReadInputStream mysqlInput) {
/*  76 */     this.mysqlInput = mysqlInput;
/*     */   }
/*     */   
/*     */   public BufferedOutputStream getMysqlOutput() throws IOException {
/*  80 */     if (this.mysqlOutput != null) {
/*  81 */       return this.mysqlOutput;
/*     */     }
/*  83 */     throw new IOException(Messages.getString("SocketConnection.2"));
/*     */   }
/*     */   
/*     */   public boolean isSSLEstablished() {
/*  87 */     return (ExportControlled.enabled() && ExportControlled.isSSLEstablished(getMysqlSocket()));
/*     */   }
/*     */   
/*     */   public SocketFactory getSocketFactory() {
/*  91 */     return this.socketFactory;
/*     */   }
/*     */   
/*     */   public void setSocketFactory(SocketFactory socketFactory) {
/*  95 */     this.socketFactory = socketFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forceClose() {
/*     */     try {
/* 103 */       getNetworkResources().forceClose();
/*     */     } finally {
/* 105 */       this.mysqlSocket = null;
/* 106 */       this.mysqlInput = null;
/* 107 */       this.mysqlOutput = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NetworkResources getNetworkResources() {
/* 114 */     return new NetworkResources(this.mysqlSocket, this.mysqlInput, this.mysqlOutput);
/*     */   }
/*     */   
/*     */   public ExceptionInterceptor getExceptionInterceptor() {
/* 118 */     return this.exceptionInterceptor;
/*     */   }
/*     */   
/*     */   public PropertySet getPropertySet() {
/* 122 */     return this.propertySet;
/*     */   }
/*     */   
/*     */   protected SocketFactory createSocketFactory(String socketFactoryClassName) {
/*     */     try {
/* 127 */       if (socketFactoryClassName == null) {
/* 128 */         throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, Messages.getString("SocketConnection.0"), getExceptionInterceptor());
/*     */       }
/*     */       
/* 131 */       Object sf = Class.forName(socketFactoryClassName).newInstance();
/* 132 */       if (sf instanceof SocketFactory) {
/* 133 */         return (SocketFactory)Class.forName(socketFactoryClassName).newInstance();
/*     */       }
/*     */ 
/*     */       
/* 137 */       return (SocketFactory)new SocketFactoryWrapper(sf);
/* 138 */     } catch (InstantiationException|IllegalAccessException|ClassNotFoundException|com.mysql.cj.exceptions.CJException ex) {
/* 139 */       throw (UnableToConnectException)ExceptionFactory.createException(UnableToConnectException.class, 
/* 140 */           Messages.getString("SocketConnection.1", new String[] { socketFactoryClassName }), getExceptionInterceptor());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\AbstractSocketConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */