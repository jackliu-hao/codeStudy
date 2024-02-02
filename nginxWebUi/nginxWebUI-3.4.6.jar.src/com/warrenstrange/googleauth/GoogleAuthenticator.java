/*     */ package com.warrenstrange.googleauth;
/*     */ 
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ServiceLoader;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.crypto.Mac;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.apache.commons.codec.binary.Base32;
/*     */ import org.apache.commons.codec.binary.Base64;
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
/*     */ public final class GoogleAuthenticator
/*     */   implements IGoogleAuthenticator
/*     */ {
/*     */   public static final String RNG_ALGORITHM = "com.warrenstrange.googleauth.rng.algorithm";
/*     */   public static final String RNG_ALGORITHM_PROVIDER = "com.warrenstrange.googleauth.rng.algorithmProvider";
/*  96 */   private static final Logger LOGGER = Logger.getLogger(GoogleAuthenticator.class.getName());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int SCRATCH_CODE_LENGTH = 8;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 106 */   public static final int SCRATCH_CODE_MODULUS = (int)Math.pow(10.0D, 8.0D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int SCRATCH_CODE_INVALID = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int BYTES_PER_SCRATCH_CODE = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String DEFAULT_RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String DEFAULT_RANDOM_NUMBER_ALGORITHM_PROVIDER = "SUN";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final GoogleAuthenticatorConfig config;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 149 */   private ReseedingSecureRandom secureRandom = new ReseedingSecureRandom(
/* 150 */       getRandomNumberAlgorithm(), 
/* 151 */       getRandomNumberAlgorithmProvider());
/*     */   
/*     */   private ICredentialRepository credentialRepository;
/*     */   
/*     */   private boolean credentialRepositorySearched;
/*     */   
/*     */   public GoogleAuthenticator() {
/* 158 */     this.config = new GoogleAuthenticatorConfig();
/*     */   }
/*     */ 
/*     */   
/*     */   public GoogleAuthenticator(GoogleAuthenticatorConfig config) {
/* 163 */     if (config == null)
/*     */     {
/* 165 */       throw new IllegalArgumentException("Configuration cannot be null.");
/*     */     }
/*     */     
/* 168 */     this.config = config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getRandomNumberAlgorithm() {
/* 177 */     return System.getProperty("com.warrenstrange.googleauth.rng.algorithm", "SHA1PRNG");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String getRandomNumberAlgorithmProvider() {
/* 188 */     return System.getProperty("com.warrenstrange.googleauth.rng.algorithmProvider", "SUN");
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
/*     */   int calculateCode(byte[] key, long tm) {
/* 206 */     byte[] data = new byte[8];
/* 207 */     long value = tm;
/*     */ 
/*     */ 
/*     */     
/* 211 */     for (int i = 8; i-- > 0; value >>>= 8L)
/*     */     {
/* 213 */       data[i] = (byte)(int)value;
/*     */     }
/*     */ 
/*     */     
/* 217 */     SecretKeySpec signKey = new SecretKeySpec(key, this.config.getHmacHashFunction().toString());
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 222 */       Mac mac = Mac.getInstance(this.config.getHmacHashFunction().toString());
/*     */ 
/*     */       
/* 225 */       mac.init(signKey);
/*     */ 
/*     */       
/* 228 */       byte[] hash = mac.doFinal(data);
/*     */ 
/*     */ 
/*     */       
/* 232 */       int offset = hash[hash.length - 1] & 0xF;
/*     */ 
/*     */ 
/*     */       
/* 236 */       long truncatedHash = 0L;
/*     */       
/* 238 */       for (int j = 0; j < 4; j++) {
/*     */         
/* 240 */         truncatedHash <<= 8L;
/*     */ 
/*     */ 
/*     */         
/* 244 */         truncatedHash |= (hash[offset + j] & 0xFF);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 249 */       truncatedHash &= 0x7FFFFFFFL;
/* 250 */       truncatedHash %= this.config.getKeyModulus();
/*     */ 
/*     */       
/* 253 */       return (int)truncatedHash;
/*     */     }
/* 255 */     catch (NoSuchAlgorithmException|java.security.InvalidKeyException ex) {
/*     */ 
/*     */       
/* 258 */       LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
/*     */ 
/*     */       
/* 261 */       throw new GoogleAuthenticatorException("The operation cannot be performed now.");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private long getTimeWindowFromTime(long time) {
/* 267 */     return time / this.config.getTimeStepSizeInMillis();
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
/*     */   
/*     */   private boolean checkCode(String secret, long code, long timestamp, int window) {
/* 288 */     byte[] decodedKey = decodeSecret(secret);
/*     */ 
/*     */ 
/*     */     
/* 292 */     long timeWindow = getTimeWindowFromTime(timestamp);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 297 */     for (int i = -((window - 1) / 2); i <= window / 2; i++) {
/*     */ 
/*     */       
/* 300 */       long hash = calculateCode(decodedKey, timeWindow + i);
/*     */ 
/*     */       
/* 303 */       if (hash == code)
/*     */       {
/*     */         
/* 306 */         return true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 311 */     return false;
/*     */   }
/*     */   
/*     */   private byte[] decodeSecret(String secret) {
/*     */     Base32 codec32;
/*     */     Base64 codec64;
/* 317 */     switch (this.config.getKeyRepresentation()) {
/*     */       
/*     */       case BASE32:
/* 320 */         codec32 = new Base32();
/*     */ 
/*     */         
/* 323 */         return codec32.decode(secret.toUpperCase());
/*     */       case BASE64:
/* 325 */         codec64 = new Base64();
/* 326 */         return codec64.decode(secret);
/*     */     } 
/* 328 */     throw new IllegalArgumentException("Unknown key representation type.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GoogleAuthenticatorKey createCredentials() {
/* 337 */     int bufferSize = this.config.getSecretBits() / 8;
/* 338 */     byte[] buffer = new byte[bufferSize];
/*     */     
/* 340 */     this.secureRandom.nextBytes(buffer);
/*     */ 
/*     */     
/* 343 */     byte[] secretKey = Arrays.copyOf(buffer, bufferSize);
/* 344 */     String generatedKey = calculateSecretKey(secretKey);
/*     */ 
/*     */     
/* 347 */     int validationCode = calculateValidationCode(secretKey);
/*     */ 
/*     */     
/* 350 */     List<Integer> scratchCodes = calculateScratchCodes();
/*     */     
/* 352 */     return (new GoogleAuthenticatorKey.Builder(generatedKey))
/*     */ 
/*     */       
/* 355 */       .setConfig(this.config)
/* 356 */       .setVerificationCode(validationCode)
/* 357 */       .setScratchCodes(scratchCodes)
/* 358 */       .build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GoogleAuthenticatorKey createCredentials(String userName) {
/* 365 */     if (userName == null)
/*     */     {
/* 367 */       throw new IllegalArgumentException("User name cannot be null.");
/*     */     }
/*     */     
/* 370 */     GoogleAuthenticatorKey key = createCredentials();
/*     */     
/* 372 */     ICredentialRepository repository = getValidCredentialRepository();
/* 373 */     repository.saveUserCredentials(userName, key
/*     */         
/* 375 */         .getKey(), key
/* 376 */         .getVerificationCode(), key
/* 377 */         .getScratchCodes());
/*     */     
/* 379 */     return key;
/*     */   }
/*     */ 
/*     */   
/*     */   private List<Integer> calculateScratchCodes() {
/* 384 */     List<Integer> scratchCodes = new ArrayList<>();
/*     */     
/* 386 */     for (int i = 0; i < this.config.getNumberOfScratchCodes(); i++)
/*     */     {
/* 388 */       scratchCodes.add(Integer.valueOf(generateScratchCode()));
/*     */     }
/*     */     
/* 391 */     return scratchCodes;
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
/*     */   private int calculateScratchCode(byte[] scratchCodeBuffer) {
/* 404 */     if (scratchCodeBuffer.length < 4)
/*     */     {
/* 406 */       throw new IllegalArgumentException(
/* 407 */           String.format("The provided random byte buffer is too small: %d.", new Object[] {
/*     */               
/* 409 */               Integer.valueOf(scratchCodeBuffer.length)
/*     */             }));
/*     */     }
/* 412 */     int scratchCode = 0;
/*     */     
/* 414 */     for (int i = 0; i < 4; i++)
/*     */     {
/* 416 */       scratchCode = (scratchCode << 8) + (scratchCodeBuffer[i] & 0xFF);
/*     */     }
/*     */     
/* 419 */     scratchCode = (scratchCode & Integer.MAX_VALUE) % SCRATCH_CODE_MODULUS;
/*     */ 
/*     */ 
/*     */     
/* 423 */     if (validateScratchCode(scratchCode))
/*     */     {
/* 425 */       return scratchCode;
/*     */     }
/*     */ 
/*     */     
/* 429 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean validateScratchCode(int scratchCode) {
/* 435 */     return (scratchCode >= SCRATCH_CODE_MODULUS / 10);
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
/*     */   private int generateScratchCode() {
/*     */     int scratchCode;
/*     */     do {
/* 450 */       byte[] scratchCodeBuffer = new byte[4];
/* 451 */       this.secureRandom.nextBytes(scratchCodeBuffer);
/*     */       
/* 453 */       scratchCode = calculateScratchCode(scratchCodeBuffer);
/*     */     }
/* 455 */     while (scratchCode == -1);
/*     */     
/* 457 */     return scratchCode;
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
/*     */   private int calculateValidationCode(byte[] secretKey) {
/* 470 */     return calculateCode(secretKey, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTotpPassword(String secret) {
/* 476 */     return getTotpPassword(secret, (new Date()).getTime());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTotpPassword(String secret, long time) {
/* 481 */     return calculateCode(decodeSecret(secret), getTimeWindowFromTime(time));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTotpPasswordOfUser(String userName) {
/* 486 */     return getTotpPasswordOfUser(userName, (new Date()).getTime());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTotpPasswordOfUser(String userName, long time) {
/* 491 */     ICredentialRepository repository = getValidCredentialRepository();
/*     */     
/* 493 */     return calculateCode(
/* 494 */         decodeSecret(repository.getSecretKey(userName)), 
/* 495 */         getTimeWindowFromTime(time));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String calculateSecretKey(byte[] secretKey) {
/* 506 */     switch (this.config.getKeyRepresentation()) {
/*     */       
/*     */       case BASE32:
/* 509 */         return (new Base32()).encodeToString(secretKey);
/*     */       case BASE64:
/* 511 */         return (new Base64()).encodeToString(secretKey);
/*     */     } 
/* 513 */     throw new IllegalArgumentException("Unknown key representation type.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean authorize(String secret, int verificationCode) {
/* 520 */     return authorize(secret, verificationCode, (new Date()).getTime());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean authorize(String secret, int verificationCode, long time) {
/* 527 */     if (secret == null)
/*     */     {
/* 529 */       throw new IllegalArgumentException("Secret cannot be null.");
/*     */     }
/*     */ 
/*     */     
/* 533 */     if (verificationCode <= 0 || verificationCode >= this.config.getKeyModulus())
/*     */     {
/* 535 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 539 */     return checkCode(secret, verificationCode, time, this.config
/*     */ 
/*     */ 
/*     */         
/* 543 */         .getWindowSize());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean authorizeUser(String userName, int verificationCode) {
/* 549 */     return authorizeUser(userName, verificationCode, (new Date()).getTime());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean authorizeUser(String userName, int verificationCode, long time) {
/* 555 */     ICredentialRepository repository = getValidCredentialRepository();
/*     */     
/* 557 */     return authorize(repository.getSecretKey(userName), verificationCode, time);
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
/*     */   private ICredentialRepository getValidCredentialRepository() {
/* 570 */     ICredentialRepository repository = getCredentialRepository();
/*     */     
/* 572 */     if (repository == null)
/*     */     {
/* 574 */       throw new UnsupportedOperationException(
/* 575 */           String.format("An instance of the %s service must be configured in order to use this feature.", new Object[] {
/*     */               
/* 577 */               ICredentialRepository.class.getName()
/*     */             }));
/*     */     }
/*     */ 
/*     */     
/* 582 */     return repository;
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
/*     */   public ICredentialRepository getCredentialRepository() {
/* 594 */     if (this.credentialRepositorySearched) return this.credentialRepository;
/*     */     
/* 596 */     this.credentialRepositorySearched = true;
/*     */ 
/*     */     
/* 599 */     ServiceLoader<ICredentialRepository> loader = ServiceLoader.load(ICredentialRepository.class);
/*     */ 
/*     */     
/* 602 */     Iterator<ICredentialRepository> iterator = loader.iterator(); if (iterator.hasNext()) { ICredentialRepository repository = iterator.next();
/*     */       
/* 604 */       this.credentialRepository = repository; }
/*     */ 
/*     */ 
/*     */     
/* 608 */     return this.credentialRepository;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCredentialRepository(ICredentialRepository repository) {
/* 614 */     this.credentialRepository = repository;
/* 615 */     this.credentialRepositorySearched = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\warrenstrange\googleauth\GoogleAuthenticator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */