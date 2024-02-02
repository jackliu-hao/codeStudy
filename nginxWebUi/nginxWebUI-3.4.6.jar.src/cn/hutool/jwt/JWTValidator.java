/*     */ package cn.hutool.jwt;
/*     */ 
/*     */ import cn.hutool.core.date.DateTime;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.exceptions.ValidateException;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.jwt.signers.JWTSigner;
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
/*     */ public class JWTValidator
/*     */ {
/*     */   private final JWT jwt;
/*     */   
/*     */   public static JWTValidator of(String token) {
/*  33 */     return new JWTValidator(JWT.of(token));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JWTValidator of(JWT jwt) {
/*  43 */     return new JWTValidator(jwt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWTValidator(JWT jwt) {
/*  52 */     this.jwt = jwt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWTValidator validateAlgorithm() throws ValidateException {
/*  62 */     return validateAlgorithm(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JWTValidator validateAlgorithm(JWTSigner signer) throws ValidateException {
/*  73 */     validateAlgorithm(this.jwt, signer);
/*  74 */     return this;
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
/*     */   public JWTValidator validateDate() throws ValidateException {
/*  93 */     return validateDate((Date)DateUtil.beginOfSecond((Date)DateUtil.date()));
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
/*     */   public JWTValidator validateDate(Date dateToCheck) throws ValidateException {
/* 112 */     validateDate(this.jwt.getPayload(), dateToCheck, 0L);
/* 113 */     return this;
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
/*     */   public JWTValidator validateDate(Date dateToCheck, long leeway) throws ValidateException {
/* 133 */     validateDate(this.jwt.getPayload(), dateToCheck, leeway);
/* 134 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void validateAlgorithm(JWT jwt, JWTSigner signer) throws ValidateException {
/* 145 */     String algorithmId = jwt.getAlgorithm();
/* 146 */     if (null == signer) {
/* 147 */       signer = jwt.getSigner();
/*     */     }
/*     */     
/* 150 */     if (StrUtil.isEmpty(algorithmId)) {
/*     */       
/* 152 */       if (null == signer || signer instanceof cn.hutool.jwt.signers.NoneJWTSigner) {
/*     */         return;
/*     */       }
/* 155 */       throw new ValidateException("No algorithm defined in header!");
/*     */     } 
/*     */     
/* 158 */     if (null == signer) {
/* 159 */       throw new IllegalArgumentException("No Signer for validate algorithm!");
/*     */     }
/*     */     
/* 162 */     String algorithmIdInSigner = signer.getAlgorithmId();
/* 163 */     if (false == StrUtil.equals(algorithmId, algorithmIdInSigner)) {
/* 164 */       throw new ValidateException("Algorithm [{}] defined in header doesn't match to [{}]!", new Object[] { algorithmId, algorithmIdInSigner });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 169 */     if (false == jwt.verify(signer)) {
/* 170 */       throw new ValidateException("Signature verification failed!");
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
/*     */   private static void validateDate(JWTPayload payload, Date now, long leeway) throws ValidateException {
/*     */     DateTime dateTime;
/* 191 */     if (null == now) {
/*     */       
/* 193 */       dateTime = DateUtil.date();
/*     */       
/* 195 */       dateTime.setTime(dateTime.getTime() / 1000L * 1000L);
/*     */     } 
/*     */ 
/*     */     
/* 199 */     Date notBefore = payload.getClaimsJson().getDate("nbf");
/* 200 */     validateNotAfter("nbf", notBefore, (Date)dateTime, leeway);
/*     */ 
/*     */     
/* 203 */     Date expiresAt = payload.getClaimsJson().getDate("exp");
/* 204 */     validateNotBefore("exp", expiresAt, (Date)dateTime, leeway);
/*     */ 
/*     */     
/* 207 */     Date issueAt = payload.getClaimsJson().getDate("iat");
/* 208 */     validateNotAfter("iat", issueAt, (Date)dateTime, leeway);
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
/*     */   private static void validateNotAfter(String fieldName, Date dateToCheck, Date now, long leeway) throws ValidateException {
/*     */     DateTime dateTime;
/* 222 */     if (null == dateToCheck) {
/*     */       return;
/*     */     }
/* 225 */     if (leeway > 0L) {
/* 226 */       dateTime = DateUtil.date(now.getTime() + leeway * 1000L);
/*     */     }
/* 228 */     if (dateToCheck.after((Date)dateTime)) {
/* 229 */       throw new ValidateException("'{}':[{}] is after now:[{}]", new Object[] { fieldName, 
/* 230 */             DateUtil.date(dateToCheck), DateUtil.date(dateTime) });
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
/*     */   private static void validateNotBefore(String fieldName, Date dateToCheck, Date now, long leeway) throws ValidateException {
/*     */     DateTime dateTime;
/* 246 */     if (null == dateToCheck) {
/*     */       return;
/*     */     }
/* 249 */     if (leeway > 0L) {
/* 250 */       dateTime = DateUtil.date(now.getTime() - leeway * 1000L);
/*     */     }
/* 252 */     if (dateToCheck.before((Date)dateTime))
/* 253 */       throw new ValidateException("'{}':[{}] is before now:[{}]", new Object[] { fieldName, 
/* 254 */             DateUtil.date(dateToCheck), DateUtil.date(dateTime) }); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\JWTValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */