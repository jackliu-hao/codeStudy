/*    */ package com.mysql.cj.protocol;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import com.mysql.cj.exceptions.FeatureNotAvailableException;
/*    */ import com.mysql.cj.exceptions.SSLParamsException;
/*    */ import com.mysql.cj.log.Log;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.net.Socket;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface SocketConnection
/*    */ {
/*    */   void connect(String paramString, int paramInt1, PropertySet paramPropertySet, ExceptionInterceptor paramExceptionInterceptor, Log paramLog, int paramInt2);
/*    */   
/*    */   void performTlsHandshake(ServerSession paramServerSession) throws SSLParamsException, FeatureNotAvailableException, IOException;
/*    */   
/*    */   default void performTlsHandshake(ServerSession serverSession, Log log) throws SSLParamsException, FeatureNotAvailableException, IOException {
/* 79 */     performTlsHandshake(serverSession);
/*    */   }
/*    */   
/*    */   void forceClose();
/*    */   
/*    */   NetworkResources getNetworkResources();
/*    */   
/*    */   String getHost();
/*    */   
/*    */   int getPort();
/*    */   
/*    */   Socket getMysqlSocket() throws IOException;
/*    */   
/*    */   FullReadInputStream getMysqlInput() throws IOException;
/*    */   
/*    */   void setMysqlInput(FullReadInputStream paramFullReadInputStream);
/*    */   
/*    */   BufferedOutputStream getMysqlOutput() throws IOException;
/*    */   
/*    */   boolean isSSLEstablished();
/*    */   
/*    */   SocketFactory getSocketFactory();
/*    */   
/*    */   void setSocketFactory(SocketFactory paramSocketFactory);
/*    */   
/*    */   ExceptionInterceptor getExceptionInterceptor();
/*    */   
/*    */   PropertySet getPropertySet();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\SocketConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */