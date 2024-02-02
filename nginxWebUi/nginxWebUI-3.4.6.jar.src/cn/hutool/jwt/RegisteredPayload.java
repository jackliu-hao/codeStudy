/*     */ package cn.hutool.jwt;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface RegisteredPayload<T extends RegisteredPayload<T>>
/*     */ {
/*     */   public static final String ISSUER = "iss";
/*     */   public static final String SUBJECT = "sub";
/*     */   public static final String AUDIENCE = "aud";
/*     */   public static final String EXPIRES_AT = "exp";
/*     */   public static final String NOT_BEFORE = "nbf";
/*     */   public static final String ISSUED_AT = "iat";
/*     */   public static final String JWT_ID = "jti";
/*     */   
/*     */   default T setIssuer(String issuer) {
/*  50 */     return setPayload("iss", issuer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default T setSubject(String subject) {
/*  60 */     return setPayload("sub", subject);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   T setAudience(String... audience) {
/*  70 */     return setPayload("aud", audience);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default T setExpiresAt(Date expiresAt) {
/*  81 */     return setPayload("exp", expiresAt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default T setNotBefore(Date notBefore) {
/*  91 */     return setPayload("nbf", notBefore);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default T setIssuedAt(Date issuedAt) {
/* 101 */     return setPayload("iat", issuedAt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default T setJWTId(String jwtId) {
/* 111 */     return setPayload("jti", jwtId);
/*     */   }
/*     */   
/*     */   T setPayload(String paramString, Object paramObject);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\RegisteredPayload.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */