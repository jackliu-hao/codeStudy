/*     */ package cn.hutool.crypto.digest.otp;
/*     */ 
/*     */ import cn.hutool.core.codec.Base32;
/*     */ import cn.hutool.core.util.RandomUtil;
/*     */ import cn.hutool.crypto.digest.HMac;
/*     */ import cn.hutool.crypto.digest.HmacAlgorithm;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HOTP
/*     */ {
/*  23 */   private static final int[] MOD_DIVISORS = new int[] { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_PASSWORD_LENGTH = 6;
/*     */ 
/*     */ 
/*     */   
/*  32 */   public static final HmacAlgorithm HOTP_HMAC_ALGORITHM = HmacAlgorithm.HmacSHA1;
/*     */ 
/*     */   
/*     */   private final HMac mac;
/*     */ 
/*     */   
/*     */   private final int passwordLength;
/*     */   
/*     */   private final int modDivisor;
/*     */   
/*     */   private final byte[] buffer;
/*     */ 
/*     */   
/*     */   public HOTP(byte[] key) {
/*  46 */     this(6, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HOTP(int passwordLength, byte[] key) {
/*  56 */     this(passwordLength, HOTP_HMAC_ALGORITHM, key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HOTP(int passwordLength, HmacAlgorithm algorithm, byte[] key) {
/*  67 */     if (passwordLength >= MOD_DIVISORS.length) {
/*  68 */       throw new IllegalArgumentException("Password length must be < " + MOD_DIVISORS.length);
/*     */     }
/*  70 */     this.mac = new HMac(algorithm, key);
/*  71 */     this.modDivisor = MOD_DIVISORS[passwordLength];
/*  72 */     this.passwordLength = passwordLength;
/*  73 */     this.buffer = new byte[8];
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
/*     */   public synchronized int generate(long counter) {
/*  86 */     this.buffer[0] = (byte)(int)((counter & 0xFF00000000000000L) >>> 56L);
/*  87 */     this.buffer[1] = (byte)(int)((counter & 0xFF000000000000L) >>> 48L);
/*  88 */     this.buffer[2] = (byte)(int)((counter & 0xFF0000000000L) >>> 40L);
/*  89 */     this.buffer[3] = (byte)(int)((counter & 0xFF00000000L) >>> 32L);
/*  90 */     this.buffer[4] = (byte)(int)((counter & 0xFF000000L) >>> 24L);
/*  91 */     this.buffer[5] = (byte)(int)((counter & 0xFF0000L) >>> 16L);
/*  92 */     this.buffer[6] = (byte)(int)((counter & 0xFF00L) >>> 8L);
/*  93 */     this.buffer[7] = (byte)(int)(counter & 0xFFL);
/*     */     
/*  95 */     byte[] digest = this.mac.digest(this.buffer);
/*     */     
/*  97 */     return truncate(digest);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String generateSecretKey(int numBytes) {
/* 108 */     return Base32.encode(RandomUtil.getSHA1PRNGRandom(RandomUtil.randomBytes(256)).generateSeed(numBytes));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPasswordLength() {
/* 117 */     return this.passwordLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAlgorithm() {
/* 126 */     return this.mac.getAlgorithm();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int truncate(byte[] digest) {
/* 136 */     int offset = digest[digest.length - 1] & 0xF;
/* 137 */     return ((digest[offset] & Byte.MAX_VALUE) << 24 | (digest[offset + 1] & 0xFF) << 16 | (digest[offset + 2] & 0xFF) << 8 | digest[offset + 3] & 0xFF) % this.modDivisor;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\otp\HOTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */