/*     */ package org.apache.http.auth;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.http.HttpHost;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class AuthScope
/*     */ {
/*  53 */   public static final String ANY_HOST = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int ANY_PORT = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   public static final String ANY_REALM = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   public static final String ANY_SCHEME = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   public static final AuthScope ANY = new AuthScope(ANY_HOST, -1, ANY_REALM, ANY_SCHEME);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String scheme;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String realm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String host;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int port;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final HttpHost origin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthScope(String host, int port, String realm, String schemeName) {
/* 110 */     this.host = (host == null) ? ANY_HOST : host.toLowerCase(Locale.ROOT);
/* 111 */     this.port = (port < 0) ? -1 : port;
/* 112 */     this.realm = (realm == null) ? ANY_REALM : realm;
/* 113 */     this.scheme = (schemeName == null) ? ANY_SCHEME : schemeName.toUpperCase(Locale.ROOT);
/* 114 */     this.origin = null;
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
/*     */   public AuthScope(HttpHost origin, String realm, String schemeName) {
/* 132 */     Args.notNull(origin, "Host");
/* 133 */     this.host = origin.getHostName().toLowerCase(Locale.ROOT);
/* 134 */     this.port = (origin.getPort() < 0) ? -1 : origin.getPort();
/* 135 */     this.realm = (realm == null) ? ANY_REALM : realm;
/* 136 */     this.scheme = (schemeName == null) ? ANY_SCHEME : schemeName.toUpperCase(Locale.ROOT);
/* 137 */     this.origin = origin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthScope(HttpHost origin) {
/* 148 */     this(origin, ANY_REALM, ANY_SCHEME);
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
/*     */   public AuthScope(String host, int port, String realm) {
/* 162 */     this(host, port, realm, ANY_SCHEME);
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
/*     */   public AuthScope(String host, int port) {
/* 174 */     this(host, port, ANY_REALM, ANY_SCHEME);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthScope(AuthScope authscope) {
/* 182 */     Args.notNull(authscope, "Scope");
/* 183 */     this.host = authscope.getHost();
/* 184 */     this.port = authscope.getPort();
/* 185 */     this.realm = authscope.getRealm();
/* 186 */     this.scheme = authscope.getScheme();
/* 187 */     this.origin = authscope.getOrigin();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHost getOrigin() {
/* 196 */     return this.origin;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 203 */     return this.host;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 210 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRealm() {
/* 217 */     return this.realm;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 224 */     return this.scheme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int match(AuthScope that) {
/* 235 */     int factor = 0;
/* 236 */     if (LangUtils.equals(this.scheme, that.scheme)) {
/* 237 */       factor++;
/*     */     }
/* 239 */     else if (this.scheme != ANY_SCHEME && that.scheme != ANY_SCHEME) {
/* 240 */       return -1;
/*     */     } 
/*     */     
/* 243 */     if (LangUtils.equals(this.realm, that.realm)) {
/* 244 */       factor += 2;
/*     */     }
/* 246 */     else if (this.realm != ANY_REALM && that.realm != ANY_REALM) {
/* 247 */       return -1;
/*     */     } 
/*     */     
/* 250 */     if (this.port == that.port) {
/* 251 */       factor += 4;
/*     */     }
/* 253 */     else if (this.port != -1 && that.port != -1) {
/* 254 */       return -1;
/*     */     } 
/*     */     
/* 257 */     if (LangUtils.equals(this.host, that.host)) {
/* 258 */       factor += 8;
/*     */     }
/* 260 */     else if (this.host != ANY_HOST && that.host != ANY_HOST) {
/* 261 */       return -1;
/*     */     } 
/*     */     
/* 264 */     return factor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 272 */     if (o == null) {
/* 273 */       return false;
/*     */     }
/* 275 */     if (o == this) {
/* 276 */       return true;
/*     */     }
/* 278 */     if (!(o instanceof AuthScope)) {
/* 279 */       return super.equals(o);
/*     */     }
/* 281 */     AuthScope that = (AuthScope)o;
/* 282 */     return (LangUtils.equals(this.host, that.host) && this.port == that.port && LangUtils.equals(this.realm, that.realm) && LangUtils.equals(this.scheme, that.scheme));
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
/*     */   public String toString() {
/* 294 */     StringBuilder buffer = new StringBuilder();
/* 295 */     if (this.scheme != null) {
/* 296 */       buffer.append(this.scheme.toUpperCase(Locale.ROOT));
/* 297 */       buffer.append(' ');
/*     */     } 
/* 299 */     if (this.realm != null) {
/* 300 */       buffer.append('\'');
/* 301 */       buffer.append(this.realm);
/* 302 */       buffer.append('\'');
/*     */     } else {
/* 304 */       buffer.append("<any realm>");
/*     */     } 
/* 306 */     if (this.host != null) {
/* 307 */       buffer.append('@');
/* 308 */       buffer.append(this.host);
/* 309 */       if (this.port >= 0) {
/* 310 */         buffer.append(':');
/* 311 */         buffer.append(this.port);
/*     */       } 
/*     */     } 
/* 314 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 322 */     int hash = 17;
/* 323 */     hash = LangUtils.hashCode(hash, this.host);
/* 324 */     hash = LangUtils.hashCode(hash, this.port);
/* 325 */     hash = LangUtils.hashCode(hash, this.realm);
/* 326 */     hash = LangUtils.hashCode(hash, this.scheme);
/* 327 */     return hash;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\AuthScope.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */