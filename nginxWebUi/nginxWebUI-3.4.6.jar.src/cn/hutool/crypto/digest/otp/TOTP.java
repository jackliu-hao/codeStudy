/*     */ package cn.hutool.crypto.digest.otp;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.crypto.digest.HmacAlgorithm;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TOTP
/*     */   extends HOTP
/*     */ {
/*  25 */   public static final Duration DEFAULT_TIME_STEP = Duration.ofSeconds(30L);
/*     */ 
/*     */ 
/*     */   
/*     */   private final Duration timeStep;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TOTP(byte[] key) {
/*  35 */     this(DEFAULT_TIME_STEP, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TOTP(Duration timeStep, byte[] key) {
/*  45 */     this(timeStep, 6, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TOTP(Duration timeStep, int passwordLength, byte[] key) {
/*  56 */     this(timeStep, passwordLength, HOTP_HMAC_ALGORITHM, key);
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
/*     */   public TOTP(Duration timeStep, int passwordLength, HmacAlgorithm algorithm, byte[] key) {
/*  68 */     super(passwordLength, algorithm, key);
/*  69 */     this.timeStep = timeStep;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int generate(Instant timestamp) {
/*  79 */     return generate(timestamp.toEpochMilli() / this.timeStep.toMillis());
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
/*     */   public boolean validate(Instant timestamp, int offsetSize, int code) {
/*  92 */     if (offsetSize == 0) {
/*  93 */       return (generate(timestamp) == code);
/*     */     }
/*  95 */     for (int i = -offsetSize; i <= offsetSize; i++) {
/*  96 */       if (generate(timestamp.plus(getTimeStep().multipliedBy(i))) == code) {
/*  97 */         return true;
/*     */       }
/*     */     } 
/* 100 */     return false;
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
/*     */   public static String generateGoogleSecretKey(String account, int numBytes) {
/* 113 */     return StrUtil.format("otpauth://totp/{}?secret={}", new Object[] { account, generateSecretKey(numBytes) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Duration getTimeStep() {
/* 122 */     return this.timeStep;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\otp\TOTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */