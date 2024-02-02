/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import org.apache.http.cookie.ClientCookie;
/*     */ import org.apache.http.cookie.SetCookie;
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
/*     */ public class BasicClientCookie
/*     */   implements SetCookie, ClientCookie, Cloneable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -3869795591041535538L;
/*     */   private final String name;
/*     */   private Map<String, String> attribs;
/*     */   private String value;
/*     */   private String cookieComment;
/*     */   private String cookieDomain;
/*     */   private Date cookieExpiryDate;
/*     */   private String cookiePath;
/*     */   private boolean isSecure;
/*     */   private int cookieVersion;
/*     */   private Date creationDate;
/*     */   
/*     */   public BasicClientCookie(String name, String value) {
/*  57 */     Args.notNull(name, "Name");
/*  58 */     this.name = name;
/*  59 */     this.attribs = new HashMap<String, String>();
/*  60 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  70 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getValue() {
/*  80 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValue(String value) {
/*  90 */     this.value = value;
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
/*     */   public String getComment() {
/* 103 */     return this.cookieComment;
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
/*     */   public void setComment(String comment) {
/* 116 */     this.cookieComment = comment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCommentURL() {
/* 125 */     return null;
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
/*     */   public Date getExpiryDate() {
/* 142 */     return this.cookieExpiryDate;
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
/*     */   public void setExpiryDate(Date expiryDate) {
/* 158 */     this.cookieExpiryDate = expiryDate;
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
/*     */   public boolean isPersistent() {
/* 171 */     return (null != this.cookieExpiryDate);
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
/*     */   public String getDomain() {
/* 184 */     return this.cookieDomain;
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
/*     */   public void setDomain(String domain) {
/* 196 */     if (domain != null) {
/* 197 */       this.cookieDomain = domain.toLowerCase(Locale.ROOT);
/*     */     } else {
/* 199 */       this.cookieDomain = null;
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
/*     */   public String getPath() {
/* 213 */     return this.cookiePath;
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
/*     */   public void setPath(String path) {
/* 226 */     this.cookiePath = path;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 235 */     return this.isSecure;
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
/*     */   public void setSecure(boolean secure) {
/* 252 */     this.isSecure = secure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getPorts() {
/* 261 */     return null;
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
/*     */   public int getVersion() {
/* 276 */     return this.cookieVersion;
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
/*     */   public void setVersion(int version) {
/* 289 */     this.cookieVersion = version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExpired(Date date) {
/* 300 */     Args.notNull(date, "Date");
/* 301 */     return (this.cookieExpiryDate != null && this.cookieExpiryDate.getTime() <= date.getTime());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getCreationDate() {
/* 309 */     return this.creationDate;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCreationDate(Date creationDate) {
/* 316 */     this.creationDate = creationDate;
/*     */   }
/*     */   
/*     */   public void setAttribute(String name, String value) {
/* 320 */     this.attribs.put(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAttribute(String name) {
/* 325 */     return this.attribs.get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsAttribute(String name) {
/* 330 */     return this.attribs.containsKey(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeAttribute(String name) {
/* 337 */     return (this.attribs.remove(name) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 342 */     BasicClientCookie clone = (BasicClientCookie)super.clone();
/* 343 */     clone.attribs = new HashMap<String, String>(this.attribs);
/* 344 */     return clone;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 349 */     StringBuilder buffer = new StringBuilder();
/* 350 */     buffer.append("[version: ");
/* 351 */     buffer.append(Integer.toString(this.cookieVersion));
/* 352 */     buffer.append("]");
/* 353 */     buffer.append("[name: ");
/* 354 */     buffer.append(this.name);
/* 355 */     buffer.append("]");
/* 356 */     buffer.append("[value: ");
/* 357 */     buffer.append(this.value);
/* 358 */     buffer.append("]");
/* 359 */     buffer.append("[domain: ");
/* 360 */     buffer.append(this.cookieDomain);
/* 361 */     buffer.append("]");
/* 362 */     buffer.append("[path: ");
/* 363 */     buffer.append(this.cookiePath);
/* 364 */     buffer.append("]");
/* 365 */     buffer.append("[expiry: ");
/* 366 */     buffer.append(this.cookieExpiryDate);
/* 367 */     buffer.append("]");
/* 368 */     return buffer.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\BasicClientCookie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */