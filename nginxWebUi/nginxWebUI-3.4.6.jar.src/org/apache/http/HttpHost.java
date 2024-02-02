/*     */ package org.apache.http;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.net.InetAddress;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public final class HttpHost
/*     */   implements Cloneable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -7529410654042457626L;
/*     */   public static final String DEFAULT_SCHEME_NAME = "http";
/*     */   protected final String hostname;
/*     */   protected final String lcHostname;
/*     */   protected final int port;
/*     */   protected final String schemeName;
/*     */   protected final InetAddress address;
/*     */   
/*     */   public HttpHost(String hostname, int port, String scheme) {
/*  80 */     this.hostname = (String)Args.containsNoBlanks(hostname, "Host name");
/*  81 */     this.lcHostname = hostname.toLowerCase(Locale.ROOT);
/*  82 */     if (scheme != null) {
/*  83 */       this.schemeName = scheme.toLowerCase(Locale.ROOT);
/*     */     } else {
/*  85 */       this.schemeName = "http";
/*     */     } 
/*  87 */     this.port = port;
/*  88 */     this.address = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHost(String hostname, int port) {
/*  99 */     this(hostname, port, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpHost create(String s) {
/* 108 */     Args.containsNoBlanks(s, "HTTP Host");
/* 109 */     String text = s;
/* 110 */     String scheme = null;
/* 111 */     int schemeIdx = text.indexOf("://");
/* 112 */     if (schemeIdx > 0) {
/* 113 */       scheme = text.substring(0, schemeIdx);
/* 114 */       text = text.substring(schemeIdx + 3);
/*     */     } 
/* 116 */     int port = -1;
/* 117 */     int portIdx = text.lastIndexOf(":");
/* 118 */     if (portIdx > 0) {
/*     */       try {
/* 120 */         port = Integer.parseInt(text.substring(portIdx + 1));
/* 121 */       } catch (NumberFormatException ex) {
/* 122 */         throw new IllegalArgumentException("Invalid HTTP host: " + text);
/*     */       } 
/* 124 */       text = text.substring(0, portIdx);
/*     */     } 
/* 126 */     return new HttpHost(text, port, scheme);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHost(String hostname) {
/* 135 */     this(hostname, -1, (String)null);
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
/*     */   public HttpHost(InetAddress address, int port, String scheme) {
/* 151 */     this((InetAddress)Args.notNull(address, "Inet address"), address.getHostName(), port, scheme);
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
/*     */   public HttpHost(InetAddress address, String hostname, int port, String scheme) {
/* 169 */     this.address = (InetAddress)Args.notNull(address, "Inet address");
/* 170 */     this.hostname = (String)Args.notNull(hostname, "Hostname");
/* 171 */     this.lcHostname = this.hostname.toLowerCase(Locale.ROOT);
/* 172 */     if (scheme != null) {
/* 173 */       this.schemeName = scheme.toLowerCase(Locale.ROOT);
/*     */     } else {
/* 175 */       this.schemeName = "http";
/*     */     } 
/* 177 */     this.port = port;
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
/*     */   public HttpHost(InetAddress address, int port) {
/* 191 */     this(address, port, (String)null);
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
/*     */   public HttpHost(InetAddress address) {
/* 203 */     this(address, -1, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHost(HttpHost httphost) {
/* 213 */     Args.notNull(httphost, "HTTP host");
/* 214 */     this.hostname = httphost.hostname;
/* 215 */     this.lcHostname = httphost.lcHostname;
/* 216 */     this.schemeName = httphost.schemeName;
/* 217 */     this.port = httphost.port;
/* 218 */     this.address = httphost.address;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHostName() {
/* 227 */     return this.hostname;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 236 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSchemeName() {
/* 245 */     return this.schemeName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InetAddress getAddress() {
/* 256 */     return this.address;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toURI() {
/* 265 */     StringBuilder buffer = new StringBuilder();
/* 266 */     buffer.append(this.schemeName);
/* 267 */     buffer.append("://");
/* 268 */     buffer.append(this.hostname);
/* 269 */     if (this.port != -1) {
/* 270 */       buffer.append(':');
/* 271 */       buffer.append(Integer.toString(this.port));
/*     */     } 
/* 273 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toHostString() {
/* 283 */     if (this.port != -1) {
/*     */       
/* 285 */       StringBuilder buffer = new StringBuilder(this.hostname.length() + 6);
/* 286 */       buffer.append(this.hostname);
/* 287 */       buffer.append(":");
/* 288 */       buffer.append(Integer.toString(this.port));
/* 289 */       return buffer.toString();
/*     */     } 
/* 291 */     return this.hostname;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 297 */     return toURI();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 303 */     if (this == obj) {
/* 304 */       return true;
/*     */     }
/* 306 */     if (obj instanceof HttpHost) {
/* 307 */       HttpHost that = (HttpHost)obj;
/* 308 */       return (this.lcHostname.equals(that.lcHostname) && this.port == that.port && this.schemeName.equals(that.schemeName) && ((this.address == null) ? (that.address == null) : this.address.equals(that.address)));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 313 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 321 */     int hash = 17;
/* 322 */     hash = LangUtils.hashCode(hash, this.lcHostname);
/* 323 */     hash = LangUtils.hashCode(hash, this.port);
/* 324 */     hash = LangUtils.hashCode(hash, this.schemeName);
/* 325 */     if (this.address != null) {
/* 326 */       hash = LangUtils.hashCode(hash, this.address);
/*     */     }
/* 328 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 333 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\HttpHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */