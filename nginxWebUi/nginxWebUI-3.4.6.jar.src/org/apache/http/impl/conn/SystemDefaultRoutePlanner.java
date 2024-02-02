/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.ProxySelector;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.List;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.SchemePortResolver;
/*     */ import org.apache.http.protocol.HttpContext;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class SystemDefaultRoutePlanner
/*     */   extends DefaultRoutePlanner
/*     */ {
/*     */   private final ProxySelector proxySelector;
/*     */   
/*     */   public SystemDefaultRoutePlanner(SchemePortResolver schemePortResolver, ProxySelector proxySelector) {
/*  64 */     super(schemePortResolver);
/*  65 */     this.proxySelector = proxySelector;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SystemDefaultRoutePlanner(ProxySelector proxySelector) {
/*  72 */     this(null, proxySelector);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
/*     */     URI targetURI;
/*     */     try {
/*  82 */       targetURI = new URI(target.toURI());
/*  83 */     } catch (URISyntaxException ex) {
/*  84 */       throw new HttpException("Cannot convert host to URI: " + target, ex);
/*     */     } 
/*  86 */     ProxySelector proxySelectorInstance = this.proxySelector;
/*  87 */     if (proxySelectorInstance == null) {
/*  88 */       proxySelectorInstance = ProxySelector.getDefault();
/*     */     }
/*  90 */     if (proxySelectorInstance == null)
/*     */     {
/*  92 */       return null;
/*     */     }
/*  94 */     List<Proxy> proxies = proxySelectorInstance.select(targetURI);
/*  95 */     Proxy p = chooseProxy(proxies);
/*  96 */     HttpHost result = null;
/*  97 */     if (p.type() == Proxy.Type.HTTP) {
/*     */       
/*  99 */       if (!(p.address() instanceof InetSocketAddress)) {
/* 100 */         throw new HttpException("Unable to handle non-Inet proxy address: " + p.address());
/*     */       }
/* 102 */       InetSocketAddress isa = (InetSocketAddress)p.address();
/*     */       
/* 104 */       result = new HttpHost(getHost(isa), isa.getPort());
/*     */     } 
/*     */     
/* 107 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getHost(InetSocketAddress isa) {
/* 115 */     return isa.isUnresolved() ? isa.getHostName() : isa.getAddress().getHostAddress();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Proxy chooseProxy(List<Proxy> proxies) {
/* 121 */     Proxy result = null;
/*     */     
/* 123 */     for (int i = 0; result == null && i < proxies.size(); i++) {
/* 124 */       Proxy p = proxies.get(i);
/* 125 */       switch (p.type()) {
/*     */         
/*     */         case DIRECT:
/*     */         case HTTP:
/* 129 */           result = p;
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 138 */     if (result == null)
/*     */     {
/*     */ 
/*     */       
/* 142 */       result = Proxy.NO_PROXY;
/*     */     }
/* 144 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\SystemDefaultRoutePlanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */