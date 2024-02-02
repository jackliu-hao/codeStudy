/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.client.AuthCache;
/*     */ import org.apache.http.conn.SchemePortResolver;
/*     */ import org.apache.http.conn.UnsupportedSchemeException;
/*     */ import org.apache.http.impl.conn.DefaultSchemePortResolver;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class BasicAuthCache
/*     */   implements AuthCache
/*     */ {
/*  63 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   private final Map<HttpHost, byte[]> map;
/*     */ 
/*     */   
/*     */   private final SchemePortResolver schemePortResolver;
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicAuthCache(SchemePortResolver schemePortResolver) {
/*  75 */     this.map = (Map)new ConcurrentHashMap<HttpHost, byte>();
/*  76 */     this.schemePortResolver = (schemePortResolver != null) ? schemePortResolver : (SchemePortResolver)DefaultSchemePortResolver.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public BasicAuthCache() {
/*  81 */     this(null);
/*     */   }
/*     */   
/*     */   protected HttpHost getKey(HttpHost host) {
/*  85 */     if (host.getPort() <= 0) {
/*     */       int port;
/*     */       try {
/*  88 */         port = this.schemePortResolver.resolve(host);
/*  89 */       } catch (UnsupportedSchemeException ignore) {
/*  90 */         return host;
/*     */       } 
/*  92 */       return new HttpHost(host.getHostName(), port, host.getSchemeName());
/*     */     } 
/*  94 */     return host;
/*     */   }
/*     */ 
/*     */   
/*     */   public void put(HttpHost host, AuthScheme authScheme) {
/*  99 */     Args.notNull(host, "HTTP host");
/* 100 */     if (authScheme == null) {
/*     */       return;
/*     */     }
/* 103 */     if (authScheme instanceof java.io.Serializable) {
/*     */       try {
/* 105 */         ByteArrayOutputStream buf = new ByteArrayOutputStream();
/* 106 */         ObjectOutputStream out = new ObjectOutputStream(buf);
/* 107 */         out.writeObject(authScheme);
/* 108 */         out.close();
/* 109 */         this.map.put(getKey(host), buf.toByteArray());
/* 110 */       } catch (IOException ex) {
/* 111 */         if (this.log.isWarnEnabled()) {
/* 112 */           this.log.warn("Unexpected I/O error while serializing auth scheme", ex);
/*     */         }
/*     */       }
/*     */     
/* 116 */     } else if (this.log.isDebugEnabled()) {
/* 117 */       this.log.debug("Auth scheme " + authScheme.getClass() + " is not serializable");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthScheme get(HttpHost host) {
/* 124 */     Args.notNull(host, "HTTP host");
/* 125 */     byte[] bytes = this.map.get(getKey(host));
/* 126 */     if (bytes != null) {
/*     */       try {
/* 128 */         ByteArrayInputStream buf = new ByteArrayInputStream(bytes);
/* 129 */         ObjectInputStream in = new ObjectInputStream(buf);
/* 130 */         AuthScheme authScheme = (AuthScheme)in.readObject();
/* 131 */         in.close();
/* 132 */         return authScheme;
/* 133 */       } catch (IOException ex) {
/* 134 */         if (this.log.isWarnEnabled()) {
/* 135 */           this.log.warn("Unexpected I/O error while de-serializing auth scheme", ex);
/*     */         }
/* 137 */         return null;
/* 138 */       } catch (ClassNotFoundException ex) {
/* 139 */         if (this.log.isWarnEnabled()) {
/* 140 */           this.log.warn("Unexpected error while de-serializing auth scheme", ex);
/*     */         }
/* 142 */         return null;
/*     */       } 
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(HttpHost host) {
/* 150 */     Args.notNull(host, "HTTP host");
/* 151 */     this.map.remove(getKey(host));
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 156 */     this.map.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 161 */     return this.map.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\BasicAuthCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */