/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.server.handlers.CookieSameSiteMode;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import javax.servlet.http.Cookie;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletCookieAdaptor
/*     */   implements Cookie
/*     */ {
/*     */   private final Cookie cookie;
/*     */   private boolean sameSite;
/*     */   private String sameSiteMode;
/*     */   
/*     */   public ServletCookieAdaptor(Cookie cookie) {
/*  44 */     this.cookie = cookie;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  49 */     return this.cookie.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValue() {
/*  54 */     return this.cookie.getValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setValue(String value) {
/*  59 */     this.cookie.setValue(value);
/*  60 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/*  65 */     return this.cookie.getPath();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setPath(String path) {
/*  70 */     this.cookie.setPath(path);
/*  71 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDomain() {
/*  76 */     return this.cookie.getDomain();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setDomain(String domain) {
/*  81 */     this.cookie.setDomain(domain);
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getMaxAge() {
/*  87 */     return Integer.valueOf(this.cookie.getMaxAge());
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setMaxAge(Integer maxAge) {
/*  92 */     this.cookie.setMaxAge(maxAge.intValue());
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDiscard() {
/*  98 */     return (this.cookie.getMaxAge() < 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setDiscard(boolean discard) {
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 108 */     return this.cookie.getSecure();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setSecure(boolean secure) {
/* 113 */     this.cookie.setSecure(secure);
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVersion() {
/* 119 */     return this.cookie.getVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setVersion(int version) {
/* 124 */     this.cookie.setVersion(version);
/* 125 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isHttpOnly() {
/* 130 */     return this.cookie.isHttpOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setHttpOnly(boolean httpOnly) {
/* 135 */     this.cookie.setHttpOnly(httpOnly);
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getExpires() {
/* 141 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setExpires(Date expires) {
/* 146 */     throw UndertowServletMessages.MESSAGES.notImplemented();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getComment() {
/* 151 */     return this.cookie.getComment();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setComment(String comment) {
/* 156 */     this.cookie.setComment(comment);
/* 157 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSameSite() {
/* 162 */     return this.sameSite;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setSameSite(boolean sameSite) {
/* 167 */     this.sameSite = sameSite;
/* 168 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSameSiteMode() {
/* 173 */     return this.sameSiteMode;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cookie setSameSiteMode(String mode) {
/* 178 */     String m = CookieSameSiteMode.lookupModeString(mode);
/* 179 */     if (m != null) {
/* 180 */       UndertowServletLogger.REQUEST_LOGGER.tracef("Setting SameSite mode to [%s] for cookie [%s]", m, getName());
/* 181 */       this.sameSiteMode = m;
/* 182 */       setSameSite(true);
/*     */     } else {
/* 184 */       UndertowServletLogger.REQUEST_LOGGER.warnf(UndertowMessages.MESSAGES.invalidSameSiteMode(mode, Arrays.toString((Object[])CookieSameSiteMode.values())), "Ignoring specified SameSite mode [%s] for cookie [%s]", mode, getName());
/*     */     } 
/* 186 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 191 */     int result = 17;
/* 192 */     result = 37 * result + ((getName() == null) ? 0 : getName().hashCode());
/* 193 */     result = 37 * result + ((getPath() == null) ? 0 : getPath().hashCode());
/* 194 */     result = 37 * result + ((getDomain() == null) ? 0 : getDomain().hashCode());
/* 195 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean equals(Object other) {
/* 200 */     if (other == this) return true; 
/* 201 */     if (!(other instanceof Cookie)) return false; 
/* 202 */     Cookie o = (Cookie)other;
/*     */     
/* 204 */     if (getName() == null && o.getName() != null) return false; 
/* 205 */     if (getName() != null && !getName().equals(o.getName())) return false;
/*     */     
/* 207 */     if (getPath() == null && o.getPath() != null) return false; 
/* 208 */     if (getPath() != null && !getPath().equals(o.getPath())) return false;
/*     */     
/* 210 */     if (getDomain() == null && o.getDomain() != null) return false; 
/* 211 */     if (getDomain() != null && !getDomain().equals(o.getDomain())) return false;
/*     */     
/* 213 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int compareTo(Object other) {
/* 218 */     return super.compareTo(other);
/*     */   }
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 223 */     return "{ServletCookieAdaptor@" + System.identityHashCode(this) + " name=" + getName() + " path=" + getPath() + " domain=" + getDomain() + "}";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ServletCookieAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */