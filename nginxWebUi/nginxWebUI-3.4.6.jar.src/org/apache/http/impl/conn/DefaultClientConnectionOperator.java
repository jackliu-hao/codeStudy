/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.ConnectException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.ConnectTimeoutException;
/*     */ import org.apache.http.conn.DnsResolver;
/*     */ import org.apache.http.conn.HttpInetSocketAddress;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.conn.scheme.Scheme;
/*     */ import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.conn.scheme.SchemeSocketFactory;
/*     */ import org.apache.http.params.HttpConnectionParams;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public class DefaultClientConnectionOperator
/*     */   implements ClientConnectionOperator
/*     */ {
/*  93 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SchemeRegistry schemeRegistry;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final DnsResolver dnsResolver;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultClientConnectionOperator(SchemeRegistry schemes) {
/* 109 */     Args.notNull(schemes, "Scheme registry");
/* 110 */     this.schemeRegistry = schemes;
/* 111 */     this.dnsResolver = new SystemDefaultDnsResolver();
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
/*     */   public DefaultClientConnectionOperator(SchemeRegistry schemes, DnsResolver dnsResolver) {
/* 124 */     Args.notNull(schemes, "Scheme registry");
/*     */     
/* 126 */     Args.notNull(dnsResolver, "DNS resolver");
/*     */     
/* 128 */     this.schemeRegistry = schemes;
/* 129 */     this.dnsResolver = dnsResolver;
/*     */   }
/*     */ 
/*     */   
/*     */   public OperatedClientConnection createConnection() {
/* 134 */     return new DefaultClientConnection();
/*     */   }
/*     */   
/*     */   private SchemeRegistry getSchemeRegistry(HttpContext context) {
/* 138 */     SchemeRegistry reg = (SchemeRegistry)context.getAttribute("http.scheme-registry");
/*     */     
/* 140 */     if (reg == null) {
/* 141 */       reg = this.schemeRegistry;
/*     */     }
/* 143 */     return reg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void openConnection(OperatedClientConnection conn, HttpHost target, InetAddress local, HttpContext context, HttpParams params) throws IOException {
/* 153 */     Args.notNull(conn, "Connection");
/* 154 */     Args.notNull(target, "Target host");
/* 155 */     Args.notNull(params, "HTTP parameters");
/* 156 */     Asserts.check(!conn.isOpen(), "Connection must not be open");
/*     */     
/* 158 */     SchemeRegistry registry = getSchemeRegistry(context);
/* 159 */     Scheme schm = registry.getScheme(target.getSchemeName());
/* 160 */     SchemeSocketFactory sf = schm.getSchemeSocketFactory();
/*     */     
/* 162 */     InetAddress[] addresses = resolveHostname(target.getHostName());
/* 163 */     int port = schm.resolvePort(target.getPort());
/* 164 */     for (int i = 0; i < addresses.length; i++) {
/* 165 */       InetAddress address = addresses[i];
/* 166 */       boolean last = (i == addresses.length - 1);
/*     */       
/* 168 */       Socket sock = sf.createSocket(params);
/* 169 */       conn.opening(sock, target);
/*     */       
/* 171 */       HttpInetSocketAddress httpInetSocketAddress = new HttpInetSocketAddress(target, address, port);
/* 172 */       InetSocketAddress localAddress = null;
/* 173 */       if (local != null) {
/* 174 */         localAddress = new InetSocketAddress(local, 0);
/*     */       }
/* 176 */       if (this.log.isDebugEnabled()) {
/* 177 */         this.log.debug("Connecting to " + httpInetSocketAddress);
/*     */       }
/*     */       try {
/* 180 */         Socket connsock = sf.connectSocket(sock, (InetSocketAddress)httpInetSocketAddress, localAddress, params);
/* 181 */         if (sock != connsock) {
/* 182 */           sock = connsock;
/* 183 */           conn.opening(sock, target);
/*     */         } 
/* 185 */         prepareSocket(sock, context, params);
/* 186 */         conn.openCompleted(sf.isSecure(sock), params);
/*     */         return;
/* 188 */       } catch (ConnectException ex) {
/* 189 */         if (last) {
/* 190 */           throw ex;
/*     */         }
/* 192 */       } catch (ConnectTimeoutException ex) {
/* 193 */         if (last) {
/* 194 */           throw ex;
/*     */         }
/*     */       } 
/* 197 */       if (this.log.isDebugEnabled()) {
/* 198 */         this.log.debug("Connect to " + httpInetSocketAddress + " timed out. " + "Connection will be retried using another IP address");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateSecureConnection(OperatedClientConnection conn, HttpHost target, HttpContext context, HttpParams params) throws IOException {
/* 210 */     Args.notNull(conn, "Connection");
/* 211 */     Args.notNull(target, "Target host");
/* 212 */     Args.notNull(params, "Parameters");
/* 213 */     Asserts.check(conn.isOpen(), "Connection must be open");
/*     */     
/* 215 */     SchemeRegistry registry = getSchemeRegistry(context);
/* 216 */     Scheme schm = registry.getScheme(target.getSchemeName());
/* 217 */     Asserts.check(schm.getSchemeSocketFactory() instanceof SchemeLayeredSocketFactory, "Socket factory must implement SchemeLayeredSocketFactory");
/*     */     
/* 219 */     SchemeLayeredSocketFactory lsf = (SchemeLayeredSocketFactory)schm.getSchemeSocketFactory();
/* 220 */     Socket sock = lsf.createLayeredSocket(conn.getSocket(), target.getHostName(), schm.resolvePort(target.getPort()), params);
/*     */     
/* 222 */     prepareSocket(sock, context, params);
/* 223 */     conn.update(sock, target, lsf.isSecure(sock), params);
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
/*     */   protected void prepareSocket(Socket sock, HttpContext context, HttpParams params) throws IOException {
/* 239 */     sock.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
/* 240 */     sock.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
/*     */     
/* 242 */     int linger = HttpConnectionParams.getLinger(params);
/* 243 */     if (linger >= 0) {
/* 244 */       sock.setSoLinger((linger > 0), linger);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InetAddress[] resolveHostname(String host) throws UnknownHostException {
/* 263 */     return this.dnsResolver.resolve(host);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultClientConnectionOperator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */