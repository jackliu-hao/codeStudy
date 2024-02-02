/*     */ package io.undertow.server.handlers;
/*     */ 
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
/*     */ 
/*     */ public interface Cookie
/*     */   extends Comparable
/*     */ {
/*     */   String getName();
/*     */   
/*     */   String getValue();
/*     */   
/*     */   Cookie setValue(String paramString);
/*     */   
/*     */   String getPath();
/*     */   
/*     */   Cookie setPath(String paramString);
/*     */   
/*     */   String getDomain();
/*     */   
/*     */   Cookie setDomain(String paramString);
/*     */   
/*     */   Integer getMaxAge();
/*     */   
/*     */   Cookie setMaxAge(Integer paramInteger);
/*     */   
/*     */   boolean isDiscard();
/*     */   
/*     */   Cookie setDiscard(boolean paramBoolean);
/*     */   
/*     */   boolean isSecure();
/*     */   
/*     */   Cookie setSecure(boolean paramBoolean);
/*     */   
/*     */   int getVersion();
/*     */   
/*     */   Cookie setVersion(int paramInt);
/*     */   
/*     */   boolean isHttpOnly();
/*     */   
/*     */   Cookie setHttpOnly(boolean paramBoolean);
/*     */   
/*     */   Date getExpires();
/*     */   
/*     */   Cookie setExpires(Date paramDate);
/*     */   
/*     */   String getComment();
/*     */   
/*     */   Cookie setComment(String paramString);
/*     */   
/*     */   default boolean isSameSite() {
/*  74 */     return false;
/*     */   }
/*     */   
/*     */   default Cookie setSameSite(boolean sameSite) {
/*  78 */     throw new UnsupportedOperationException("Not implemented");
/*     */   }
/*     */   
/*     */   default String getSameSiteMode() {
/*  82 */     return null;
/*     */   }
/*     */   
/*     */   default Cookie setSameSiteMode(String mode) {
/*  86 */     throw new UnsupportedOperationException("Not implemented");
/*     */   }
/*     */ 
/*     */   
/*     */   default int compareTo(Object other) {
/*  91 */     Cookie o = (Cookie)other;
/*  92 */     int retVal = 0;
/*     */ 
/*     */     
/*  95 */     if (getName() == null && o.getName() != null) return -1; 
/*  96 */     if (getName() != null && o.getName() == null) return 1; 
/*  97 */     retVal = (getName() == null && o.getName() == null) ? 0 : getName().compareTo(o.getName());
/*  98 */     if (retVal != 0) return retVal;
/*     */ 
/*     */     
/* 101 */     if (getPath() == null && o.getPath() != null) return -1; 
/* 102 */     if (getPath() != null && o.getPath() == null) return 1; 
/* 103 */     retVal = (getPath() == null && o.getPath() == null) ? 0 : getPath().compareTo(o.getPath());
/* 104 */     if (retVal != 0) return retVal;
/*     */ 
/*     */     
/* 107 */     if (getDomain() == null && o.getDomain() != null) return -1; 
/* 108 */     if (getDomain() != null && o.getDomain() == null) return 1; 
/* 109 */     retVal = (getDomain() == null && o.getDomain() == null) ? 0 : getDomain().compareTo(o.getDomain());
/* 110 */     if (retVal != 0) return retVal;
/*     */     
/* 112 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\Cookie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */