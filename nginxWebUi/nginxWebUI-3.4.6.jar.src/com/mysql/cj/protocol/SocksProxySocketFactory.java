/*    */ package com.mysql.cj.protocol;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertyKey;
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Proxy;
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
/*    */ public class SocksProxySocketFactory
/*    */   extends StandardSocketFactory
/*    */ {
/*    */   protected Socket createSocket(PropertySet props) {
/* 46 */     String socksProxyHost = (String)props.getStringProperty(PropertyKey.socksProxyHost).getValue();
/* 47 */     int socksProxyPort = ((Integer)props.getIntegerProperty(PropertyKey.socksProxyPort).getValue()).intValue();
/* 48 */     return new Socket(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(socksProxyHost, socksProxyPort)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\SocksProxySocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */