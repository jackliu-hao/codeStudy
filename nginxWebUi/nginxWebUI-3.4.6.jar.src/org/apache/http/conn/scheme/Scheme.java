/*     */ package org.apache.http.conn.scheme;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.LangUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public final class Scheme
/*     */ {
/*     */   private final String name;
/*     */   private final SchemeSocketFactory socketFactory;
/*     */   private final int defaultPort;
/*     */   private final boolean layered;
/*     */   private String stringRep;
/*     */   
/*     */   public Scheme(String name, int port, SchemeSocketFactory factory) {
/*  92 */     Args.notNull(name, "Scheme name");
/*  93 */     Args.check((port > 0 && port <= 65535), "Port is invalid");
/*  94 */     Args.notNull(factory, "Socket factory");
/*  95 */     this.name = name.toLowerCase(Locale.ENGLISH);
/*  96 */     this.defaultPort = port;
/*  97 */     if (factory instanceof SchemeLayeredSocketFactory) {
/*  98 */       this.layered = true;
/*  99 */       this.socketFactory = factory;
/* 100 */     } else if (factory instanceof LayeredSchemeSocketFactory) {
/* 101 */       this.layered = true;
/* 102 */       this.socketFactory = new SchemeLayeredSocketFactoryAdaptor2((LayeredSchemeSocketFactory)factory);
/*     */     } else {
/* 104 */       this.layered = false;
/* 105 */       this.socketFactory = factory;
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
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Scheme(String name, SocketFactory factory, int port) {
/* 127 */     Args.notNull(name, "Scheme name");
/* 128 */     Args.notNull(factory, "Socket factory");
/* 129 */     Args.check((port > 0 && port <= 65535), "Port is invalid");
/*     */     
/* 131 */     this.name = name.toLowerCase(Locale.ENGLISH);
/* 132 */     if (factory instanceof LayeredSocketFactory) {
/* 133 */       this.socketFactory = new SchemeLayeredSocketFactoryAdaptor((LayeredSocketFactory)factory);
/*     */       
/* 135 */       this.layered = true;
/*     */     } else {
/* 137 */       this.socketFactory = new SchemeSocketFactoryAdaptor(factory);
/* 138 */       this.layered = false;
/*     */     } 
/* 140 */     this.defaultPort = port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getDefaultPort() {
/* 149 */     return this.defaultPort;
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
/*     */   @Deprecated
/*     */   public final SocketFactory getSocketFactory() {
/* 164 */     if (this.socketFactory instanceof SchemeSocketFactoryAdaptor) {
/* 165 */       return ((SchemeSocketFactoryAdaptor)this.socketFactory).getFactory();
/*     */     }
/* 167 */     return this.layered ? new LayeredSocketFactoryAdaptor((LayeredSchemeSocketFactory)this.socketFactory) : new SocketFactoryAdaptor(this.socketFactory);
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
/*     */   public final SchemeSocketFactory getSchemeSocketFactory() {
/* 183 */     return this.socketFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getName() {
/* 192 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isLayered() {
/* 202 */     return this.layered;
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
/*     */   public final int resolvePort(int port) {
/* 215 */     return (port <= 0) ? this.defaultPort : port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 225 */     if (this.stringRep == null) {
/* 226 */       StringBuilder buffer = new StringBuilder();
/* 227 */       buffer.append(this.name);
/* 228 */       buffer.append(':');
/* 229 */       buffer.append(Integer.toString(this.defaultPort));
/* 230 */       this.stringRep = buffer.toString();
/*     */     } 
/* 232 */     return this.stringRep;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object obj) {
/* 237 */     if (this == obj) {
/* 238 */       return true;
/*     */     }
/* 240 */     if (obj instanceof Scheme) {
/* 241 */       Scheme that = (Scheme)obj;
/* 242 */       return (this.name.equals(that.name) && this.defaultPort == that.defaultPort && this.layered == that.layered);
/*     */     } 
/*     */ 
/*     */     
/* 246 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 251 */     int hash = 17;
/* 252 */     hash = LangUtils.hashCode(hash, this.defaultPort);
/* 253 */     hash = LangUtils.hashCode(hash, this.name);
/* 254 */     hash = LangUtils.hashCode(hash, this.layered);
/* 255 */     return hash;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\Scheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */