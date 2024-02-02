/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CookieImpl
/*     */   implements Cookie
/*     */ {
/*     */   private final String name;
/*     */   private String value;
/*     */   private String path;
/*     */   private String domain;
/*     */   private Integer maxAge;
/*     */   private Date expires;
/*     */   private boolean discard;
/*     */   private boolean secure;
/*     */   private boolean httpOnly;
/*  42 */   private int version = 0;
/*     */   private String comment;
/*     */   private boolean sameSite;
/*     */   private String sameSiteMode;
/*     */   
/*     */   public CookieImpl(String name, String value) {
/*  48 */     this.name = name;
/*  49 */     this.value = value;
/*     */   }
/*     */   
/*     */   public CookieImpl(String name) {
/*  53 */     this.name = name;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  57 */     return this.name;
/*     */   }
/*     */   
/*     */   public String getValue() {
/*  61 */     return this.value;
/*     */   }
/*     */   
/*     */   public CookieImpl setValue(String value) {
/*  65 */     this.value = value;
/*  66 */     return this;
/*     */   }
/*     */   
/*     */   public String getPath() {
/*  70 */     return this.path;
/*     */   }
/*     */   
/*     */   public CookieImpl setPath(String path) {
/*  74 */     this.path = path;
/*  75 */     return this;
/*     */   }
/*     */   
/*     */   public String getDomain() {
/*  79 */     return this.domain;
/*     */   }
/*     */   
/*     */   public CookieImpl setDomain(String domain) {
/*  83 */     this.domain = domain;
/*  84 */     return this;
/*     */   }
/*     */   
/*     */   public Integer getMaxAge() {
/*  88 */     return this.maxAge;
/*     */   }
/*     */   
/*     */   public CookieImpl setMaxAge(Integer maxAge) {
/*  92 */     this.maxAge = maxAge;
/*  93 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isDiscard() {
/*  97 */     return this.discard;
/*     */   }
/*     */   
/*     */   public CookieImpl setDiscard(boolean discard) {
/* 101 */     this.discard = discard;
/* 102 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isSecure() {
/* 106 */     return this.secure;
/*     */   }
/*     */   
/*     */   public CookieImpl setSecure(boolean secure) {
/* 110 */     this.secure = secure;
/* 111 */     return this;
/*     */   }
/*     */   
/*     */   public int getVersion() {
/* 115 */     return this.version;
/*     */   }
/*     */   
/*     */   public CookieImpl setVersion(int version) {
/* 119 */     this.version = version;
/* 120 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isHttpOnly() {
/* 124 */     return this.httpOnly;
/*     */   }
/*     */   
/*     */   public CookieImpl setHttpOnly(boolean httpOnly) {
/* 128 */     this.httpOnly = httpOnly;
/* 129 */     return this;
/*     */   }
/*     */   
/*     */   public Date getExpires() {
/* 133 */     return this.expires;
/*     */   }
/*     */   
/*     */   public CookieImpl setExpires(Date expires) {
/* 137 */     this.expires = expires;
/* 138 */     return this;
/*     */   }
/*     */   
/*     */   public String getComment() {
/* 142 */     return this.comment;
/*     */   }
/*     */   
/*     */   public Cookie setComment(String comment) {
/* 146 */     this.comment = comment;
/* 147 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSameSite() {
/* 152 */     return this.sameSite;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setSameSite(boolean sameSite) {
/* 157 */     this.sameSite = sameSite;
/* 158 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSameSiteMode() {
/* 163 */     return this.sameSiteMode;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setSameSiteMode(String mode) {
/* 168 */     String m = CookieSameSiteMode.lookupModeString(mode);
/* 169 */     if (m != null) {
/* 170 */       UndertowLogger.REQUEST_LOGGER.tracef("Setting SameSite mode to [%s] for cookie [%s]", m, this.name);
/* 171 */       this.sameSiteMode = m;
/* 172 */       setSameSite(true);
/*     */     } else {
/* 174 */       UndertowLogger.REQUEST_LOGGER.warnf(UndertowMessages.MESSAGES.invalidSameSiteMode(mode, Arrays.toString((Object[])CookieSameSiteMode.values())), "Ignoring specified SameSite mode [%s] for cookie [%s]", mode, this.name);
/*     */     } 
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 181 */     int result = 17;
/* 182 */     result = 37 * result + ((getName() == null) ? 0 : getName().hashCode());
/* 183 */     result = 37 * result + ((getPath() == null) ? 0 : getPath().hashCode());
/* 184 */     result = 37 * result + ((getDomain() == null) ? 0 : getDomain().hashCode());
/* 185 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object other) {
/* 190 */     if (other == this) return true; 
/* 191 */     if (!(other instanceof Cookie)) return false; 
/* 192 */     Cookie o = (Cookie)other;
/*     */     
/* 194 */     if (getName() == null && o.getName() != null) return false; 
/* 195 */     if (getName() != null && !getName().equals(o.getName())) return false;
/*     */     
/* 197 */     if (getPath() == null && o.getPath() != null) return false; 
/* 198 */     if (getPath() != null && !getPath().equals(o.getPath())) return false;
/*     */     
/* 200 */     if (getDomain() == null && o.getDomain() != null) return false; 
/* 201 */     if (getDomain() != null && !getDomain().equals(o.getDomain())) return false;
/*     */     
/* 203 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int compareTo(Object other) {
/* 208 */     return super.compareTo(other);
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 213 */     return "{CookieImpl@" + System.identityHashCode(this) + " name=" + getName() + " path=" + getPath() + " domain=" + getDomain() + "}";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\CookieImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */