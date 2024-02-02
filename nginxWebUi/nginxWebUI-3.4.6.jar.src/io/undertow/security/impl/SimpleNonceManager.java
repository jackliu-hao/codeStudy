/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.api.SessionNonceManager;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.FlexBase64;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimpleNonceManager
/*     */   implements SessionNonceManager
/*     */ {
/*     */   private static final String DEFAULT_HASH_ALG = "MD5";
/*  73 */   private final Set<String> invalidNonces = Collections.synchronizedSet(new HashSet<>());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   private final Map<String, Nonce> knownNonces = Collections.synchronizedMap(new HashMap<>());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  92 */   private final Map<NonceHolder, String> forwardMapping = Collections.synchronizedMap(new WeakHashMap<>());
/*     */ 
/*     */ 
/*     */   
/*     */   private final String secret;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String hashAlg;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int hashLength;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long firstUseTimeOut = 300000L;
/*     */ 
/*     */   
/*     */   private static final long overallTimeOut = 900000L;
/*     */ 
/*     */   
/*     */   private static final long cacheTimePostExpiry = 300000L;
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleNonceManager() {
/* 119 */     this("MD5");
/*     */   }
/*     */ 
/*     */   
/*     */   public SimpleNonceManager(String hashAlg) {
/* 124 */     MessageDigest digest = getDigest(hashAlg);
/*     */     
/* 126 */     this.hashAlg = hashAlg;
/* 127 */     this.hashLength = digest.getDigestLength();
/*     */ 
/*     */     
/* 130 */     Random rand = new SecureRandom();
/* 131 */     byte[] secretBytes = new byte[32];
/* 132 */     rand.nextBytes(secretBytes);
/* 133 */     this.secret = FlexBase64.encodeString(digest.digest(secretBytes), false);
/*     */   }
/*     */   
/*     */   private MessageDigest getDigest(String hashAlg) {
/*     */     try {
/* 138 */       return MessageDigest.getInstance(hashAlg);
/* 139 */     } catch (NoSuchAlgorithmException e) {
/* 140 */       throw UndertowMessages.MESSAGES.hashAlgorithmNotFound(hashAlg);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String nextNonce(String lastNonce, HttpServerExchange exchange) {
/* 149 */     if (lastNonce == null) {
/* 150 */       return createNewNonceString();
/*     */     }
/*     */     
/* 153 */     if (this.invalidNonces.contains(lastNonce))
/*     */     {
/* 155 */       return createNewNonceString();
/*     */     }
/*     */     
/* 158 */     String nonce = lastNonce;
/*     */     
/* 160 */     synchronized (this.forwardMapping) {
/* 161 */       NonceHolder holder = new NonceHolder(lastNonce);
/* 162 */       while (this.forwardMapping.containsKey(holder)) {
/* 163 */         nonce = this.forwardMapping.get(holder);
/*     */         
/* 165 */         holder = new NonceHolder(nonce);
/*     */       } 
/*     */       
/* 168 */       synchronized (this.knownNonces) {
/* 169 */         Nonce value = this.knownNonces.get(nonce);
/* 170 */         if (value == null) {
/*     */ 
/*     */           
/* 173 */           nonce = createNewNonceString();
/*     */         } else {
/* 175 */           long now = System.currentTimeMillis();
/*     */ 
/*     */           
/* 178 */           long earliestAccepted = now - 300000L;
/* 179 */           if (value.timeStamp < earliestAccepted || value.timeStamp > now) {
/* 180 */             Nonce replacement = createNewNonce(holder);
/* 181 */             if (value.executorKey != null)
/*     */             {
/* 183 */               value.executorKey.remove();
/*     */             }
/*     */             
/* 186 */             nonce = replacement.nonce;
/*     */ 
/*     */             
/* 189 */             this.forwardMapping.put(holder, nonce);
/*     */             
/* 191 */             replacement.setSessionKey(value.getSessionKey());
/*     */ 
/*     */             
/* 194 */             this.knownNonces.remove(holder.nonce);
/*     */ 
/*     */ 
/*     */             
/* 198 */             this.knownNonces.put(nonce, replacement);
/* 199 */             earliestAccepted = now - 1200000L;
/* 200 */             long timeTillExpiry = replacement.timeStamp - earliestAccepted;
/* 201 */             replacement.executorKey = WorkerUtils.executeAfter(exchange.getIoThread(), new KnownNonceCleaner(nonce), timeTillExpiry, TimeUnit.MILLISECONDS);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 209 */     return nonce;
/*     */   }
/*     */   
/*     */   private String createNewNonceString() {
/* 213 */     return (createNewNonce(null)).nonce;
/*     */   }
/*     */   
/*     */   private Nonce createNewNonce(NonceHolder previousNonce) {
/* 217 */     byte[] prefix = new byte[8];
/*     */ 
/*     */     
/* 220 */     ThreadLocalRandom.current().nextBytes(prefix);
/* 221 */     long timeStamp = System.currentTimeMillis();
/* 222 */     byte[] now = Long.toString(timeStamp).getBytes(StandardCharsets.UTF_8);
/*     */     
/* 224 */     String nonce = createNonce(prefix, now);
/*     */     
/* 226 */     return new Nonce(nonce, timeStamp, previousNonce);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean validateNonce(String nonce, int nonceCount, HttpServerExchange exchange) {
/* 235 */     if (nonceCount < 0) {
/* 236 */       if (this.invalidNonces.contains(nonce))
/*     */       {
/* 238 */         return false;
/*     */       }
/*     */     } else {
/* 241 */       if (this.knownNonces.containsKey(nonce))
/*     */       {
/*     */ 
/*     */         
/* 245 */         return validateNonceWithCount(new Nonce(nonce), nonceCount, exchange.getIoThread());
/*     */       }
/* 247 */       if (this.forwardMapping.containsKey(new NonceHolder(nonce)))
/*     */       {
/*     */         
/* 250 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 254 */     Nonce value = verifyUnknownNonce(nonce, nonceCount);
/* 255 */     if (value == null) {
/* 256 */       return false;
/*     */     }
/*     */     
/* 259 */     long now = System.currentTimeMillis();
/*     */     
/* 261 */     long earliestAccepted = now - 300000L;
/* 262 */     if (value.timeStamp < earliestAccepted || value.timeStamp > now)
/*     */     {
/* 264 */       return false;
/*     */     }
/*     */     
/* 267 */     if (nonceCount < 0)
/*     */     {
/* 269 */       return addInvalidNonce(value, (XnioExecutor)exchange.getIoThread());
/*     */     }
/* 271 */     return validateNonceWithCount(value, nonceCount, exchange.getIoThread());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean validateNonceWithCount(Nonce nonce, int nonceCount, XnioIoThread executor) {
/* 279 */     synchronized (this.knownNonces) {
/* 280 */       Nonce value = this.knownNonces.get(nonce.nonce);
/* 281 */       long now = System.currentTimeMillis();
/*     */ 
/*     */       
/* 284 */       long earliestAccepted = now - 1200000L;
/* 285 */       if (value == null) {
/* 286 */         if (nonce.timeStamp < 0L)
/*     */         {
/* 288 */           return false;
/*     */         }
/*     */         
/* 291 */         if (nonce.timeStamp > earliestAccepted && nonce.timeStamp <= now) {
/* 292 */           this.knownNonces.put(nonce.nonce, nonce);
/* 293 */           long timeTillExpiry = nonce.timeStamp - earliestAccepted;
/* 294 */           nonce.executorKey = WorkerUtils.executeAfter(executor, new KnownNonceCleaner(nonce.nonce), timeTillExpiry, TimeUnit.MILLISECONDS);
/*     */           
/* 296 */           return true;
/*     */         } 
/*     */         
/* 299 */         return false;
/*     */       } 
/*     */       
/* 302 */       if (value.timeStamp < earliestAccepted || value.timeStamp > now)
/*     */       {
/* 304 */         return false;
/*     */       }
/*     */       
/* 307 */       if (value.getMaxNonceCount() < nonceCount) {
/* 308 */         value.setMaxNonceCount(nonceCount);
/* 309 */         return true;
/*     */       } 
/*     */       
/* 312 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean addInvalidNonce(Nonce nonce, XnioExecutor executor) {
/* 320 */     long now = System.currentTimeMillis();
/* 321 */     long invalidBefore = now - 300000L;
/*     */     
/* 323 */     long timeTillInvalid = nonce.timeStamp - invalidBefore;
/* 324 */     if (timeTillInvalid > 0L) {
/* 325 */       if (this.invalidNonces.add(nonce.nonce)) {
/* 326 */         executor.executeAfter(new InvalidNonceCleaner(nonce.nonce), timeTillInvalid, TimeUnit.MILLISECONDS);
/* 327 */         return true;
/*     */       } 
/* 329 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 334 */     return false;
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
/*     */   private Nonce verifyUnknownNonce(String nonce, int nonceCount) {
/*     */     byte[] complete;
/*     */     int offset, length;
/*     */     try {
/* 358 */       ByteBuffer decode = FlexBase64.decode(nonce);
/* 359 */       complete = decode.array();
/* 360 */       offset = decode.arrayOffset();
/* 361 */       length = decode.limit() - offset;
/* 362 */     } catch (IOException e) {
/* 363 */       throw UndertowMessages.MESSAGES.invalidBase64Token(e);
/*     */     } 
/*     */     
/* 366 */     int timeStampLength = complete[offset + 8];
/*     */     
/* 368 */     if (this.hashLength > 0) {
/* 369 */       int expectedLength = 9 + timeStampLength + this.hashLength;
/* 370 */       if (length != expectedLength)
/* 371 */         throw UndertowMessages.MESSAGES.invalidNonceReceived(); 
/* 372 */       if (timeStampLength + 1 >= length) {
/* 373 */         throw UndertowMessages.MESSAGES.invalidNonceReceived();
/*     */       }
/*     */     } 
/* 376 */     byte[] prefix = new byte[8];
/* 377 */     System.arraycopy(complete, offset, prefix, 0, 8);
/* 378 */     byte[] timeStampBytes = new byte[timeStampLength];
/* 379 */     System.arraycopy(complete, offset + 9, timeStampBytes, 0, timeStampBytes.length);
/*     */     
/* 381 */     String expectedNonce = createNonce(prefix, timeStampBytes);
/*     */     
/* 383 */     if (expectedNonce.equals(nonce)) {
/*     */       try {
/* 385 */         long timeStamp = Long.parseLong(new String(timeStampBytes, StandardCharsets.UTF_8));
/*     */         
/* 387 */         return new Nonce(expectedNonce, timeStamp, nonceCount);
/* 388 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */     
/* 392 */     return null;
/*     */   }
/*     */   
/*     */   private String createNonce(byte[] prefix, byte[] timeStamp) {
/* 396 */     byte[] hashedPart = generateHash(prefix, timeStamp);
/* 397 */     byte[] complete = new byte[9 + timeStamp.length + hashedPart.length];
/* 398 */     System.arraycopy(prefix, 0, complete, 0, 8);
/* 399 */     complete[8] = (byte)timeStamp.length;
/* 400 */     System.arraycopy(timeStamp, 0, complete, 9, timeStamp.length);
/* 401 */     System.arraycopy(hashedPart, 0, complete, 9 + timeStamp.length, hashedPart.length);
/*     */     
/* 403 */     return FlexBase64.encodeString(complete, false);
/*     */   }
/*     */   
/*     */   private byte[] generateHash(byte[] prefix, byte[] timeStamp) {
/* 407 */     MessageDigest digest = getDigest(this.hashAlg);
/*     */     
/* 409 */     digest.update(prefix);
/* 410 */     digest.update(timeStamp);
/*     */     
/* 412 */     return digest.digest(this.secret.getBytes(StandardCharsets.UTF_8));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void associateHash(String nonce, byte[] hash) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] lookupHash(String nonce) {
/* 422 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class NonceHolder
/*     */   {
/*     */     private final String nonce;
/*     */ 
/*     */     
/*     */     private NonceHolder(String nonce) {
/* 432 */       if (nonce == null) {
/* 433 */         throw new NullPointerException("nonce must not be null.");
/*     */       }
/* 435 */       this.nonce = nonce;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 440 */       return this.nonce.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 445 */       return (obj instanceof NonceHolder) ? this.nonce.equals(((NonceHolder)obj).nonce) : false;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Nonce
/*     */   {
/*     */     private final String nonce;
/*     */ 
/*     */     
/*     */     private final long timeStamp;
/*     */ 
/*     */     
/*     */     private int maxNonceCount;
/*     */     
/*     */     private final SimpleNonceManager.NonceHolder previousNonce;
/*     */     
/*     */     private byte[] sessionKey;
/*     */     
/*     */     private XnioExecutor.Key executorKey;
/*     */ 
/*     */     
/*     */     private Nonce(String nonce) {
/* 469 */       this(nonce, -1L, -1);
/*     */     }
/*     */     
/*     */     private Nonce(String nonce, long timeStamp, int initialNC) {
/* 473 */       this(nonce, timeStamp, initialNC, (SimpleNonceManager.NonceHolder)null);
/*     */     }
/*     */     
/*     */     private Nonce(String nonce, long timeStamp, SimpleNonceManager.NonceHolder previousNonce) {
/* 477 */       this(nonce, timeStamp, -1, previousNonce);
/*     */     }
/*     */     
/*     */     private Nonce(String nonce, long timeStamp, int initialNC, SimpleNonceManager.NonceHolder previousNonce) {
/* 481 */       this.nonce = nonce;
/* 482 */       this.timeStamp = timeStamp;
/* 483 */       this.maxNonceCount = initialNC;
/* 484 */       this.previousNonce = previousNonce;
/*     */     }
/*     */     
/*     */     byte[] getSessionKey() {
/* 488 */       return this.sessionKey;
/*     */     }
/*     */     
/*     */     void setSessionKey(byte[] sessionKey) {
/* 492 */       this.sessionKey = sessionKey;
/*     */     }
/*     */     
/*     */     int getMaxNonceCount() {
/* 496 */       return this.maxNonceCount;
/*     */     }
/*     */     
/*     */     void setMaxNonceCount(int maxNonceCount) {
/* 500 */       this.maxNonceCount = maxNonceCount;
/*     */     }
/*     */   }
/*     */   
/*     */   private class InvalidNonceCleaner
/*     */     implements Runnable
/*     */   {
/*     */     private final String nonce;
/*     */     
/*     */     private InvalidNonceCleaner(String nonce) {
/* 510 */       if (nonce == null) {
/* 511 */         throw new NullPointerException("nonce must not be null.");
/*     */       }
/* 513 */       this.nonce = nonce;
/*     */     }
/*     */     
/*     */     public void run() {
/* 517 */       SimpleNonceManager.this.invalidNonces.remove(this.nonce);
/*     */     }
/*     */   }
/*     */   
/*     */   private class KnownNonceCleaner
/*     */     implements Runnable {
/*     */     private final String nonce;
/*     */     
/*     */     private KnownNonceCleaner(String nonce) {
/* 526 */       if (nonce == null) {
/* 527 */         throw new NullPointerException("nonce must not be null.");
/*     */       }
/* 529 */       this.nonce = nonce;
/*     */     }
/*     */     
/*     */     public void run() {
/* 533 */       SimpleNonceManager.this.knownNonces.remove(this.nonce);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\SimpleNonceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */