/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.ConnectException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.NoRouteToHostException;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.config.Lookup;
/*     */ import org.apache.http.config.SocketConfig;
/*     */ import org.apache.http.conn.ConnectTimeoutException;
/*     */ import org.apache.http.conn.DnsResolver;
/*     */ import org.apache.http.conn.HttpClientConnectionOperator;
/*     */ import org.apache.http.conn.HttpHostConnectException;
/*     */ import org.apache.http.conn.ManagedHttpClientConnection;
/*     */ import org.apache.http.conn.SchemePortResolver;
/*     */ import org.apache.http.conn.UnsupportedSchemeException;
/*     */ import org.apache.http.conn.socket.ConnectionSocketFactory;
/*     */ import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class DefaultHttpClientConnectionOperator
/*     */   implements HttpClientConnectionOperator
/*     */ {
/*     */   static final String SOCKET_FACTORY_REGISTRY = "http.socket-factory-registry";
/*  69 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private final Lookup<ConnectionSocketFactory> socketFactoryRegistry;
/*     */   
/*     */   private final SchemePortResolver schemePortResolver;
/*     */   
/*     */   private final DnsResolver dnsResolver;
/*     */ 
/*     */   
/*     */   public DefaultHttpClientConnectionOperator(Lookup<ConnectionSocketFactory> socketFactoryRegistry, SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
/*  80 */     Args.notNull(socketFactoryRegistry, "Socket factory registry");
/*  81 */     this.socketFactoryRegistry = socketFactoryRegistry;
/*  82 */     this.schemePortResolver = (schemePortResolver != null) ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
/*     */     
/*  84 */     this.dnsResolver = (dnsResolver != null) ? dnsResolver : SystemDefaultDnsResolver.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Lookup<ConnectionSocketFactory> getSocketFactoryRegistry(HttpContext context) {
/*  90 */     Lookup<ConnectionSocketFactory> reg = (Lookup<ConnectionSocketFactory>)context.getAttribute("http.socket-factory-registry");
/*     */     
/*  92 */     if (reg == null) {
/*  93 */       reg = this.socketFactoryRegistry;
/*     */     }
/*  95 */     return reg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ManagedHttpClientConnection conn, HttpHost host, InetSocketAddress localAddress, int connectTimeout, SocketConfig socketConfig, HttpContext context) throws IOException {
/* 106 */     Lookup<ConnectionSocketFactory> registry = getSocketFactoryRegistry(context);
/* 107 */     ConnectionSocketFactory sf = (ConnectionSocketFactory)registry.lookup(host.getSchemeName());
/* 108 */     if (sf == null) {
/* 109 */       throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
/*     */     }
/*     */     
/* 112 */     (new InetAddress[1])[0] = host.getAddress(); InetAddress[] addresses = (host.getAddress() != null) ? new InetAddress[1] : this.dnsResolver.resolve(host.getHostName());
/*     */     
/* 114 */     int port = this.schemePortResolver.resolve(host);
/* 115 */     for (int i = 0; i < addresses.length; i++) {
/* 116 */       InetAddress address = addresses[i];
/* 117 */       boolean last = (i == addresses.length - 1);
/*     */       
/* 119 */       Socket sock = sf.createSocket(context);
/* 120 */       sock.setSoTimeout(socketConfig.getSoTimeout());
/* 121 */       sock.setReuseAddress(socketConfig.isSoReuseAddress());
/* 122 */       sock.setTcpNoDelay(socketConfig.isTcpNoDelay());
/* 123 */       sock.setKeepAlive(socketConfig.isSoKeepAlive());
/* 124 */       if (socketConfig.getRcvBufSize() > 0) {
/* 125 */         sock.setReceiveBufferSize(socketConfig.getRcvBufSize());
/*     */       }
/* 127 */       if (socketConfig.getSndBufSize() > 0) {
/* 128 */         sock.setSendBufferSize(socketConfig.getSndBufSize());
/*     */       }
/*     */       
/* 131 */       int linger = socketConfig.getSoLinger();
/* 132 */       if (linger >= 0) {
/* 133 */         sock.setSoLinger(true, linger);
/*     */       }
/* 135 */       conn.bind(sock);
/*     */       
/* 137 */       InetSocketAddress remoteAddress = new InetSocketAddress(address, port);
/* 138 */       if (this.log.isDebugEnabled()) {
/* 139 */         this.log.debug("Connecting to " + remoteAddress);
/*     */       }
/*     */       try {
/* 142 */         sock = sf.connectSocket(connectTimeout, sock, host, remoteAddress, localAddress, context);
/*     */         
/* 144 */         conn.bind(sock);
/* 145 */         if (this.log.isDebugEnabled()) {
/* 146 */           this.log.debug("Connection established " + conn);
/*     */         }
/*     */         return;
/* 149 */       } catch (SocketTimeoutException ex) {
/* 150 */         if (last) {
/* 151 */           throw new ConnectTimeoutException(ex, host, addresses);
/*     */         }
/* 153 */       } catch (ConnectException ex) {
/* 154 */         if (last) {
/* 155 */           String msg = ex.getMessage();
/* 156 */           throw "Connection timed out".equals(msg) ? new ConnectTimeoutException(ex, host, addresses) : new HttpHostConnectException(ex, host, addresses);
/*     */         }
/*     */       
/*     */       }
/* 160 */       catch (NoRouteToHostException ex) {
/* 161 */         if (last) {
/* 162 */           throw ex;
/*     */         }
/*     */       } 
/* 165 */       if (this.log.isDebugEnabled()) {
/* 166 */         this.log.debug("Connect to " + remoteAddress + " timed out. " + "Connection will be retried using another IP address");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void upgrade(ManagedHttpClientConnection conn, HttpHost host, HttpContext context) throws IOException {
/* 177 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/* 178 */     Lookup<ConnectionSocketFactory> registry = getSocketFactoryRegistry((HttpContext)clientContext);
/* 179 */     ConnectionSocketFactory sf = (ConnectionSocketFactory)registry.lookup(host.getSchemeName());
/* 180 */     if (sf == null) {
/* 181 */       throw new UnsupportedSchemeException(host.getSchemeName() + " protocol is not supported");
/*     */     }
/*     */     
/* 184 */     if (!(sf instanceof LayeredConnectionSocketFactory)) {
/* 185 */       throw new UnsupportedSchemeException(host.getSchemeName() + " protocol does not support connection upgrade");
/*     */     }
/*     */     
/* 188 */     LayeredConnectionSocketFactory lsf = (LayeredConnectionSocketFactory)sf;
/* 189 */     Socket sock = conn.getSocket();
/* 190 */     int port = this.schemePortResolver.resolve(host);
/* 191 */     sock = lsf.createLayeredSocket(sock, host.getHostName(), port, context);
/* 192 */     conn.bind(sock);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultHttpClientConnectionOperator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */