/*     */ package io.undertow.servlet.api;
/*     */ 
/*     */ import java.util.Set;
/*     */ import javax.servlet.SessionTrackingMode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServletSessionConfig
/*     */ {
/*     */   public static final String DEFAULT_SESSION_ID = "JSESSIONID";
/*     */   private Set<SessionTrackingMode> sessionTrackingModes;
/*  37 */   private String name = "JSESSIONID";
/*     */   private String path;
/*     */   private String domain;
/*     */   private boolean secure;
/*     */   private boolean httpOnly;
/*  42 */   private int maxAge = -1;
/*     */   private String comment;
/*     */   
/*     */   public String getName() {
/*  46 */     return this.name;
/*     */   }
/*     */   
/*     */   public ServletSessionConfig setName(String name) {
/*  50 */     this.name = name;
/*  51 */     return this;
/*     */   }
/*     */   
/*     */   public String getDomain() {
/*  55 */     return this.domain;
/*     */   }
/*     */   
/*     */   public ServletSessionConfig setDomain(String domain) {
/*  59 */     this.domain = domain;
/*  60 */     return this;
/*     */   }
/*     */   
/*     */   public String getPath() {
/*  64 */     return this.path;
/*     */   }
/*     */   
/*     */   public ServletSessionConfig setPath(String path) {
/*  68 */     this.path = path;
/*  69 */     return this;
/*     */   }
/*     */   
/*     */   public String getComment() {
/*  73 */     return this.comment;
/*     */   }
/*     */   
/*     */   public ServletSessionConfig setComment(String comment) {
/*  77 */     this.comment = comment;
/*  78 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isHttpOnly() {
/*  82 */     return this.httpOnly;
/*     */   }
/*     */   
/*     */   public ServletSessionConfig setHttpOnly(boolean httpOnly) {
/*  86 */     this.httpOnly = httpOnly;
/*  87 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isSecure() {
/*  91 */     return this.secure;
/*     */   }
/*     */   
/*     */   public ServletSessionConfig setSecure(boolean secure) {
/*  95 */     this.secure = secure;
/*  96 */     return this;
/*     */   }
/*     */   
/*     */   public int getMaxAge() {
/* 100 */     return this.maxAge;
/*     */   }
/*     */   
/*     */   public ServletSessionConfig setMaxAge(int maxAge) {
/* 104 */     this.maxAge = maxAge;
/* 105 */     return this;
/*     */   }
/*     */   
/*     */   public Set<SessionTrackingMode> getSessionTrackingModes() {
/* 109 */     return this.sessionTrackingModes;
/*     */   }
/*     */   
/*     */   public ServletSessionConfig setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {
/* 113 */     this.sessionTrackingModes = sessionTrackingModes;
/* 114 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ServletSessionConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */