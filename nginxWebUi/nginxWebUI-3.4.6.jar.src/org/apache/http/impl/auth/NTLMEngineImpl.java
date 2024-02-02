/*      */ package org.apache.http.impl.auth;
/*      */ 
/*      */ import java.nio.charset.Charset;
/*      */ import java.security.Key;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.security.SecureRandom;
/*      */ import java.security.cert.Certificate;
/*      */ import java.security.cert.CertificateEncodingException;
/*      */ import java.util.Arrays;
/*      */ import java.util.Locale;
/*      */ import java.util.Random;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import org.apache.commons.codec.binary.Base64;
/*      */ import org.apache.http.Consts;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class NTLMEngineImpl
/*      */   implements NTLMEngine
/*      */ {
/*   54 */   private static final Charset UNICODE_LITTLE_UNMARKED = Charset.forName("UnicodeLittleUnmarked");
/*      */   
/*   56 */   private static final Charset DEFAULT_CHARSET = Consts.ASCII;
/*      */   
/*      */   static final int FLAG_REQUEST_UNICODE_ENCODING = 1;
/*      */   
/*      */   static final int FLAG_REQUEST_OEM_ENCODING = 2;
/*      */   
/*      */   static final int FLAG_REQUEST_TARGET = 4;
/*      */   
/*      */   static final int FLAG_REQUEST_SIGN = 16;
/*      */   
/*      */   static final int FLAG_REQUEST_SEAL = 32;
/*      */   
/*      */   static final int FLAG_REQUEST_LAN_MANAGER_KEY = 128;
/*      */   
/*      */   static final int FLAG_REQUEST_NTLMv1 = 512;
/*      */   
/*      */   static final int FLAG_DOMAIN_PRESENT = 4096;
/*      */   
/*      */   static final int FLAG_WORKSTATION_PRESENT = 8192;
/*      */   
/*      */   static final int FLAG_REQUEST_ALWAYS_SIGN = 32768;
/*      */   
/*      */   static final int FLAG_REQUEST_NTLM2_SESSION = 524288;
/*      */   static final int FLAG_REQUEST_VERSION = 33554432;
/*      */   static final int FLAG_TARGETINFO_PRESENT = 8388608;
/*      */   static final int FLAG_REQUEST_128BIT_KEY_EXCH = 536870912;
/*      */   static final int FLAG_REQUEST_EXPLICIT_KEY_EXCH = 1073741824;
/*      */   static final int FLAG_REQUEST_56BIT_ENCRYPTION = -2147483648;
/*      */   static final int MSV_AV_EOL = 0;
/*      */   static final int MSV_AV_NB_COMPUTER_NAME = 1;
/*      */   static final int MSV_AV_NB_DOMAIN_NAME = 2;
/*      */   static final int MSV_AV_DNS_COMPUTER_NAME = 3;
/*      */   static final int MSV_AV_DNS_DOMAIN_NAME = 4;
/*      */   static final int MSV_AV_DNS_TREE_NAME = 5;
/*      */   static final int MSV_AV_FLAGS = 6;
/*      */   static final int MSV_AV_TIMESTAMP = 7;
/*      */   static final int MSV_AV_SINGLE_HOST = 8;
/*      */   static final int MSV_AV_TARGET_NAME = 9;
/*      */   static final int MSV_AV_CHANNEL_BINDINGS = 10;
/*      */   static final int MSV_AV_FLAGS_ACCOUNT_AUTH_CONSTAINED = 1;
/*      */   static final int MSV_AV_FLAGS_MIC = 2;
/*      */   static final int MSV_AV_FLAGS_UNTRUSTED_TARGET_SPN = 4;
/*      */   private static final SecureRandom RND_GEN;
/*      */   
/*      */   static {
/*  101 */     SecureRandom rnd = null;
/*      */     try {
/*  103 */       rnd = SecureRandom.getInstance("SHA1PRNG");
/*  104 */     } catch (Exception ignore) {}
/*      */     
/*  106 */     RND_GEN = rnd;
/*      */   }
/*      */ 
/*      */   
/*  110 */   private static final byte[] SIGNATURE = getNullTerminatedAsciiString("NTLMSSP");
/*      */ 
/*      */ 
/*      */   
/*  114 */   private static final byte[] SIGN_MAGIC_SERVER = getNullTerminatedAsciiString("session key to server-to-client signing key magic constant");
/*      */   
/*  116 */   private static final byte[] SIGN_MAGIC_CLIENT = getNullTerminatedAsciiString("session key to client-to-server signing key magic constant");
/*      */   
/*  118 */   private static final byte[] SEAL_MAGIC_SERVER = getNullTerminatedAsciiString("session key to server-to-client sealing key magic constant");
/*      */   
/*  120 */   private static final byte[] SEAL_MAGIC_CLIENT = getNullTerminatedAsciiString("session key to client-to-server sealing key magic constant");
/*      */ 
/*      */ 
/*      */   
/*  124 */   private static final byte[] MAGIC_TLS_SERVER_ENDPOINT = "tls-server-end-point:".getBytes(Consts.ASCII);
/*      */ 
/*      */   
/*      */   private static byte[] getNullTerminatedAsciiString(String source) {
/*  128 */     byte[] bytesWithoutNull = source.getBytes(Consts.ASCII);
/*  129 */     byte[] target = new byte[bytesWithoutNull.length + 1];
/*  130 */     System.arraycopy(bytesWithoutNull, 0, target, 0, bytesWithoutNull.length);
/*  131 */     target[bytesWithoutNull.length] = 0;
/*  132 */     return target;
/*      */   }
/*      */   
/*  135 */   private static final String TYPE_1_MESSAGE = (new Type1Message()).getResponse();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String getType1Message(String host, String domain) {
/*  154 */     return TYPE_1_MESSAGE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String getType3Message(String user, String password, String host, String domain, byte[] nonce, int type2Flags, String target, byte[] targetInformation) throws NTLMEngineException {
/*  180 */     return (new Type3Message(domain, host, user, password, nonce, type2Flags, target, targetInformation)).getResponse();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static String getType3Message(String user, String password, String host, String domain, byte[] nonce, int type2Flags, String target, byte[] targetInformation, Certificate peerServerCertificate, byte[] type1Message, byte[] type2Message) throws NTLMEngineException {
/*  208 */     return (new Type3Message(domain, host, user, password, nonce, type2Flags, target, targetInformation, peerServerCertificate, type1Message, type2Message)).getResponse();
/*      */   }
/*      */ 
/*      */   
/*      */   private static int readULong(byte[] src, int index) {
/*  213 */     if (src.length < index + 4) {
/*  214 */       return 0;
/*      */     }
/*  216 */     return src[index] & 0xFF | (src[index + 1] & 0xFF) << 8 | (src[index + 2] & 0xFF) << 16 | (src[index + 3] & 0xFF) << 24;
/*      */   }
/*      */ 
/*      */   
/*      */   private static int readUShort(byte[] src, int index) {
/*  221 */     if (src.length < index + 2) {
/*  222 */       return 0;
/*      */     }
/*  224 */     return src[index] & 0xFF | (src[index + 1] & 0xFF) << 8;
/*      */   }
/*      */   
/*      */   private static byte[] readSecurityBuffer(byte[] src, int index) {
/*  228 */     int length = readUShort(src, index);
/*  229 */     int offset = readULong(src, index + 4);
/*  230 */     if (src.length < offset + length) {
/*  231 */       return new byte[length];
/*      */     }
/*  233 */     byte[] buffer = new byte[length];
/*  234 */     System.arraycopy(src, offset, buffer, 0, length);
/*  235 */     return buffer;
/*      */   }
/*      */ 
/*      */   
/*      */   private static byte[] makeRandomChallenge(Random random) {
/*  240 */     byte[] rval = new byte[8];
/*  241 */     synchronized (random) {
/*  242 */       random.nextBytes(rval);
/*      */     } 
/*  244 */     return rval;
/*      */   }
/*      */ 
/*      */   
/*      */   private static byte[] makeSecondaryKey(Random random) {
/*  249 */     byte[] rval = new byte[16];
/*  250 */     synchronized (random) {
/*  251 */       random.nextBytes(rval);
/*      */     } 
/*  253 */     return rval;
/*      */   }
/*      */ 
/*      */   
/*      */   protected static class CipherGen
/*      */   {
/*      */     protected final Random random;
/*      */     
/*      */     protected final long currentTime;
/*      */     
/*      */     protected final String domain;
/*      */     
/*      */     protected final String user;
/*      */     
/*      */     protected final String password;
/*      */     protected final byte[] challenge;
/*      */     protected final String target;
/*      */     protected final byte[] targetInformation;
/*      */     protected byte[] clientChallenge;
/*      */     protected byte[] clientChallenge2;
/*      */     protected byte[] secondaryKey;
/*      */     protected byte[] timestamp;
/*  275 */     protected byte[] lmHash = null;
/*  276 */     protected byte[] lmResponse = null;
/*  277 */     protected byte[] ntlmHash = null;
/*  278 */     protected byte[] ntlmResponse = null;
/*  279 */     protected byte[] ntlmv2Hash = null;
/*  280 */     protected byte[] lmv2Hash = null;
/*  281 */     protected byte[] lmv2Response = null;
/*  282 */     protected byte[] ntlmv2Blob = null;
/*  283 */     protected byte[] ntlmv2Response = null;
/*  284 */     protected byte[] ntlm2SessionResponse = null;
/*  285 */     protected byte[] lm2SessionResponse = null;
/*  286 */     protected byte[] lmUserSessionKey = null;
/*  287 */     protected byte[] ntlmUserSessionKey = null;
/*  288 */     protected byte[] ntlmv2UserSessionKey = null;
/*  289 */     protected byte[] ntlm2SessionResponseUserSessionKey = null;
/*  290 */     protected byte[] lanManagerSessionKey = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public CipherGen(String domain, String user, String password, byte[] challenge, String target, byte[] targetInformation, byte[] clientChallenge, byte[] clientChallenge2, byte[] secondaryKey, byte[] timestamp) {
/*  300 */       this(NTLMEngineImpl.RND_GEN, System.currentTimeMillis(), domain, user, password, challenge, target, targetInformation, clientChallenge, clientChallenge2, secondaryKey, timestamp);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public CipherGen(Random random, long currentTime, String domain, String user, String password, byte[] challenge, String target, byte[] targetInformation, byte[] clientChallenge, byte[] clientChallenge2, byte[] secondaryKey, byte[] timestamp) {
/*  311 */       this.random = random;
/*  312 */       this.currentTime = currentTime;
/*      */       
/*  314 */       this.domain = domain;
/*  315 */       this.target = target;
/*  316 */       this.user = user;
/*  317 */       this.password = password;
/*  318 */       this.challenge = challenge;
/*  319 */       this.targetInformation = targetInformation;
/*  320 */       this.clientChallenge = clientChallenge;
/*  321 */       this.clientChallenge2 = clientChallenge2;
/*  322 */       this.secondaryKey = secondaryKey;
/*  323 */       this.timestamp = timestamp;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public CipherGen(String domain, String user, String password, byte[] challenge, String target, byte[] targetInformation) {
/*  336 */       this(NTLMEngineImpl.RND_GEN, System.currentTimeMillis(), domain, user, password, challenge, target, targetInformation);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public CipherGen(Random random, long currentTime, String domain, String user, String password, byte[] challenge, String target, byte[] targetInformation) {
/*  346 */       this(random, currentTime, domain, user, password, challenge, target, targetInformation, null, null, null, null);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getClientChallenge() throws NTLMEngineException {
/*  352 */       if (this.clientChallenge == null) {
/*  353 */         this.clientChallenge = NTLMEngineImpl.makeRandomChallenge(this.random);
/*      */       }
/*  355 */       return this.clientChallenge;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getClientChallenge2() throws NTLMEngineException {
/*  361 */       if (this.clientChallenge2 == null) {
/*  362 */         this.clientChallenge2 = NTLMEngineImpl.makeRandomChallenge(this.random);
/*      */       }
/*  364 */       return this.clientChallenge2;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getSecondaryKey() throws NTLMEngineException {
/*  370 */       if (this.secondaryKey == null) {
/*  371 */         this.secondaryKey = NTLMEngineImpl.makeSecondaryKey(this.random);
/*      */       }
/*  373 */       return this.secondaryKey;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getLMHash() throws NTLMEngineException {
/*  379 */       if (this.lmHash == null) {
/*  380 */         this.lmHash = NTLMEngineImpl.lmHash(this.password);
/*      */       }
/*  382 */       return this.lmHash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getLMResponse() throws NTLMEngineException {
/*  388 */       if (this.lmResponse == null) {
/*  389 */         this.lmResponse = NTLMEngineImpl.lmResponse(getLMHash(), this.challenge);
/*      */       }
/*  391 */       return this.lmResponse;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLMHash() throws NTLMEngineException {
/*  397 */       if (this.ntlmHash == null) {
/*  398 */         this.ntlmHash = NTLMEngineImpl.ntlmHash(this.password);
/*      */       }
/*  400 */       return this.ntlmHash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLMResponse() throws NTLMEngineException {
/*  406 */       if (this.ntlmResponse == null) {
/*  407 */         this.ntlmResponse = NTLMEngineImpl.lmResponse(getNTLMHash(), this.challenge);
/*      */       }
/*  409 */       return this.ntlmResponse;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getLMv2Hash() throws NTLMEngineException {
/*  415 */       if (this.lmv2Hash == null) {
/*  416 */         this.lmv2Hash = NTLMEngineImpl.lmv2Hash(this.domain, this.user, getNTLMHash());
/*      */       }
/*  418 */       return this.lmv2Hash;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLMv2Hash() throws NTLMEngineException {
/*  424 */       if (this.ntlmv2Hash == null) {
/*  425 */         this.ntlmv2Hash = NTLMEngineImpl.ntlmv2Hash(this.domain, this.user, getNTLMHash());
/*      */       }
/*  427 */       return this.ntlmv2Hash;
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] getTimestamp() {
/*  432 */       if (this.timestamp == null) {
/*  433 */         long time = this.currentTime;
/*  434 */         time += 11644473600000L;
/*  435 */         time *= 10000L;
/*      */         
/*  437 */         this.timestamp = new byte[8];
/*  438 */         for (int i = 0; i < 8; i++) {
/*  439 */           this.timestamp[i] = (byte)(int)time;
/*  440 */           time >>>= 8L;
/*      */         } 
/*      */       } 
/*  443 */       return this.timestamp;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLMv2Blob() throws NTLMEngineException {
/*  449 */       if (this.ntlmv2Blob == null) {
/*  450 */         this.ntlmv2Blob = NTLMEngineImpl.createBlob(getClientChallenge2(), this.targetInformation, getTimestamp());
/*      */       }
/*  452 */       return this.ntlmv2Blob;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLMv2Response() throws NTLMEngineException {
/*  458 */       if (this.ntlmv2Response == null) {
/*  459 */         this.ntlmv2Response = NTLMEngineImpl.lmv2Response(getNTLMv2Hash(), this.challenge, getNTLMv2Blob());
/*      */       }
/*  461 */       return this.ntlmv2Response;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getLMv2Response() throws NTLMEngineException {
/*  467 */       if (this.lmv2Response == null) {
/*  468 */         this.lmv2Response = NTLMEngineImpl.lmv2Response(getLMv2Hash(), this.challenge, getClientChallenge());
/*      */       }
/*  470 */       return this.lmv2Response;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLM2SessionResponse() throws NTLMEngineException {
/*  476 */       if (this.ntlm2SessionResponse == null) {
/*  477 */         this.ntlm2SessionResponse = NTLMEngineImpl.ntlm2SessionResponse(getNTLMHash(), this.challenge, getClientChallenge());
/*      */       }
/*  479 */       return this.ntlm2SessionResponse;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getLM2SessionResponse() throws NTLMEngineException {
/*  485 */       if (this.lm2SessionResponse == null) {
/*  486 */         byte[] clntChallenge = getClientChallenge();
/*  487 */         this.lm2SessionResponse = new byte[24];
/*  488 */         System.arraycopy(clntChallenge, 0, this.lm2SessionResponse, 0, clntChallenge.length);
/*  489 */         Arrays.fill(this.lm2SessionResponse, clntChallenge.length, this.lm2SessionResponse.length, (byte)0);
/*      */       } 
/*  491 */       return this.lm2SessionResponse;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getLMUserSessionKey() throws NTLMEngineException {
/*  497 */       if (this.lmUserSessionKey == null) {
/*  498 */         this.lmUserSessionKey = new byte[16];
/*  499 */         System.arraycopy(getLMHash(), 0, this.lmUserSessionKey, 0, 8);
/*  500 */         Arrays.fill(this.lmUserSessionKey, 8, 16, (byte)0);
/*      */       } 
/*  502 */       return this.lmUserSessionKey;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLMUserSessionKey() throws NTLMEngineException {
/*  508 */       if (this.ntlmUserSessionKey == null) {
/*  509 */         NTLMEngineImpl.MD4 md4 = new NTLMEngineImpl.MD4();
/*  510 */         md4.update(getNTLMHash());
/*  511 */         this.ntlmUserSessionKey = md4.getOutput();
/*      */       } 
/*  513 */       return this.ntlmUserSessionKey;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLMv2UserSessionKey() throws NTLMEngineException {
/*  519 */       if (this.ntlmv2UserSessionKey == null) {
/*  520 */         byte[] ntlmv2hash = getNTLMv2Hash();
/*  521 */         byte[] truncatedResponse = new byte[16];
/*  522 */         System.arraycopy(getNTLMv2Response(), 0, truncatedResponse, 0, 16);
/*  523 */         this.ntlmv2UserSessionKey = NTLMEngineImpl.hmacMD5(truncatedResponse, ntlmv2hash);
/*      */       } 
/*  525 */       return this.ntlmv2UserSessionKey;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getNTLM2SessionResponseUserSessionKey() throws NTLMEngineException {
/*  531 */       if (this.ntlm2SessionResponseUserSessionKey == null) {
/*  532 */         byte[] ntlm2SessionResponseNonce = getLM2SessionResponse();
/*  533 */         byte[] sessionNonce = new byte[this.challenge.length + ntlm2SessionResponseNonce.length];
/*  534 */         System.arraycopy(this.challenge, 0, sessionNonce, 0, this.challenge.length);
/*  535 */         System.arraycopy(ntlm2SessionResponseNonce, 0, sessionNonce, this.challenge.length, ntlm2SessionResponseNonce.length);
/*  536 */         this.ntlm2SessionResponseUserSessionKey = NTLMEngineImpl.hmacMD5(sessionNonce, getNTLMUserSessionKey());
/*      */       } 
/*  538 */       return this.ntlm2SessionResponseUserSessionKey;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getLanManagerSessionKey() throws NTLMEngineException {
/*  544 */       if (this.lanManagerSessionKey == null) {
/*      */         try {
/*  546 */           byte[] keyBytes = new byte[14];
/*  547 */           System.arraycopy(getLMHash(), 0, keyBytes, 0, 8);
/*  548 */           Arrays.fill(keyBytes, 8, keyBytes.length, (byte)-67);
/*  549 */           Key lowKey = NTLMEngineImpl.createDESKey(keyBytes, 0);
/*  550 */           Key highKey = NTLMEngineImpl.createDESKey(keyBytes, 7);
/*  551 */           byte[] truncatedResponse = new byte[8];
/*  552 */           System.arraycopy(getLMResponse(), 0, truncatedResponse, 0, truncatedResponse.length);
/*  553 */           Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
/*  554 */           des.init(1, lowKey);
/*  555 */           byte[] lowPart = des.doFinal(truncatedResponse);
/*  556 */           des = Cipher.getInstance("DES/ECB/NoPadding");
/*  557 */           des.init(1, highKey);
/*  558 */           byte[] highPart = des.doFinal(truncatedResponse);
/*  559 */           this.lanManagerSessionKey = new byte[16];
/*  560 */           System.arraycopy(lowPart, 0, this.lanManagerSessionKey, 0, lowPart.length);
/*  561 */           System.arraycopy(highPart, 0, this.lanManagerSessionKey, lowPart.length, highPart.length);
/*  562 */         } catch (Exception e) {
/*  563 */           throw new NTLMEngineException(e.getMessage(), e);
/*      */         } 
/*      */       }
/*  566 */       return this.lanManagerSessionKey;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static byte[] hmacMD5(byte[] value, byte[] key) throws NTLMEngineException {
/*  573 */     HMACMD5 hmacMD5 = new HMACMD5(key);
/*  574 */     hmacMD5.update(value);
/*  575 */     return hmacMD5.getOutput();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static byte[] RC4(byte[] value, byte[] key) throws NTLMEngineException {
/*      */     try {
/*  582 */       Cipher rc4 = Cipher.getInstance("RC4");
/*  583 */       rc4.init(1, new SecretKeySpec(key, "RC4"));
/*  584 */       return rc4.doFinal(value);
/*  585 */     } catch (Exception e) {
/*  586 */       throw new NTLMEngineException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static byte[] ntlm2SessionResponse(byte[] ntlmHash, byte[] challenge, byte[] clientChallenge) throws NTLMEngineException {
/*      */     try {
/*  601 */       MessageDigest md5 = getMD5();
/*  602 */       md5.update(challenge);
/*  603 */       md5.update(clientChallenge);
/*  604 */       byte[] digest = md5.digest();
/*      */       
/*  606 */       byte[] sessionHash = new byte[8];
/*  607 */       System.arraycopy(digest, 0, sessionHash, 0, 8);
/*  608 */       return lmResponse(ntlmHash, sessionHash);
/*  609 */     } catch (Exception e) {
/*  610 */       if (e instanceof NTLMEngineException) {
/*  611 */         throw (NTLMEngineException)e;
/*      */       }
/*  613 */       throw new NTLMEngineException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] lmHash(String password) throws NTLMEngineException {
/*      */     try {
/*  628 */       byte[] oemPassword = password.toUpperCase(Locale.ROOT).getBytes(Consts.ASCII);
/*  629 */       int length = Math.min(oemPassword.length, 14);
/*  630 */       byte[] keyBytes = new byte[14];
/*  631 */       System.arraycopy(oemPassword, 0, keyBytes, 0, length);
/*  632 */       Key lowKey = createDESKey(keyBytes, 0);
/*  633 */       Key highKey = createDESKey(keyBytes, 7);
/*  634 */       byte[] magicConstant = "KGS!@#$%".getBytes(Consts.ASCII);
/*  635 */       Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
/*  636 */       des.init(1, lowKey);
/*  637 */       byte[] lowHash = des.doFinal(magicConstant);
/*  638 */       des.init(1, highKey);
/*  639 */       byte[] highHash = des.doFinal(magicConstant);
/*  640 */       byte[] lmHash = new byte[16];
/*  641 */       System.arraycopy(lowHash, 0, lmHash, 0, 8);
/*  642 */       System.arraycopy(highHash, 0, lmHash, 8, 8);
/*  643 */       return lmHash;
/*  644 */     } catch (Exception e) {
/*  645 */       throw new NTLMEngineException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] ntlmHash(String password) throws NTLMEngineException {
/*  659 */     if (UNICODE_LITTLE_UNMARKED == null) {
/*  660 */       throw new NTLMEngineException("Unicode not supported");
/*      */     }
/*  662 */     byte[] unicodePassword = password.getBytes(UNICODE_LITTLE_UNMARKED);
/*  663 */     MD4 md4 = new MD4();
/*  664 */     md4.update(unicodePassword);
/*  665 */     return md4.getOutput();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] lmv2Hash(String domain, String user, byte[] ntlmHash) throws NTLMEngineException {
/*  676 */     if (UNICODE_LITTLE_UNMARKED == null) {
/*  677 */       throw new NTLMEngineException("Unicode not supported");
/*      */     }
/*  679 */     HMACMD5 hmacMD5 = new HMACMD5(ntlmHash);
/*      */     
/*  681 */     hmacMD5.update(user.toUpperCase(Locale.ROOT).getBytes(UNICODE_LITTLE_UNMARKED));
/*  682 */     if (domain != null) {
/*  683 */       hmacMD5.update(domain.toUpperCase(Locale.ROOT).getBytes(UNICODE_LITTLE_UNMARKED));
/*      */     }
/*  685 */     return hmacMD5.getOutput();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] ntlmv2Hash(String domain, String user, byte[] ntlmHash) throws NTLMEngineException {
/*  696 */     if (UNICODE_LITTLE_UNMARKED == null) {
/*  697 */       throw new NTLMEngineException("Unicode not supported");
/*      */     }
/*  699 */     HMACMD5 hmacMD5 = new HMACMD5(ntlmHash);
/*      */     
/*  701 */     hmacMD5.update(user.toUpperCase(Locale.ROOT).getBytes(UNICODE_LITTLE_UNMARKED));
/*  702 */     if (domain != null) {
/*  703 */       hmacMD5.update(domain.getBytes(UNICODE_LITTLE_UNMARKED));
/*      */     }
/*  705 */     return hmacMD5.getOutput();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] lmResponse(byte[] hash, byte[] challenge) throws NTLMEngineException {
/*      */     try {
/*  720 */       byte[] keyBytes = new byte[21];
/*  721 */       System.arraycopy(hash, 0, keyBytes, 0, 16);
/*  722 */       Key lowKey = createDESKey(keyBytes, 0);
/*  723 */       Key middleKey = createDESKey(keyBytes, 7);
/*  724 */       Key highKey = createDESKey(keyBytes, 14);
/*  725 */       Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
/*  726 */       des.init(1, lowKey);
/*  727 */       byte[] lowResponse = des.doFinal(challenge);
/*  728 */       des.init(1, middleKey);
/*  729 */       byte[] middleResponse = des.doFinal(challenge);
/*  730 */       des.init(1, highKey);
/*  731 */       byte[] highResponse = des.doFinal(challenge);
/*  732 */       byte[] lmResponse = new byte[24];
/*  733 */       System.arraycopy(lowResponse, 0, lmResponse, 0, 8);
/*  734 */       System.arraycopy(middleResponse, 0, lmResponse, 8, 8);
/*  735 */       System.arraycopy(highResponse, 0, lmResponse, 16, 8);
/*  736 */       return lmResponse;
/*  737 */     } catch (Exception e) {
/*  738 */       throw new NTLMEngineException(e.getMessage(), e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] lmv2Response(byte[] hash, byte[] challenge, byte[] clientData) {
/*  757 */     HMACMD5 hmacMD5 = new HMACMD5(hash);
/*  758 */     hmacMD5.update(challenge);
/*  759 */     hmacMD5.update(clientData);
/*  760 */     byte[] mac = hmacMD5.getOutput();
/*  761 */     byte[] lmv2Response = new byte[mac.length + clientData.length];
/*  762 */     System.arraycopy(mac, 0, lmv2Response, 0, mac.length);
/*  763 */     System.arraycopy(clientData, 0, lmv2Response, mac.length, clientData.length);
/*  764 */     return lmv2Response;
/*      */   }
/*      */   
/*      */   enum Mode
/*      */   {
/*  769 */     CLIENT, SERVER;
/*      */   }
/*      */   
/*      */   static class Handle
/*      */   {
/*      */     private final byte[] exportedSessionKey;
/*      */     private byte[] signingKey;
/*      */     private byte[] sealingKey;
/*      */     private final Cipher rc4;
/*      */     final NTLMEngineImpl.Mode mode;
/*      */     private final boolean isConnection;
/*  780 */     int sequenceNumber = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Handle(byte[] exportedSessionKey, NTLMEngineImpl.Mode mode, boolean isConnection) throws NTLMEngineException {
/*  786 */       this.exportedSessionKey = exportedSessionKey;
/*  787 */       this.isConnection = isConnection;
/*  788 */       this.mode = mode;
/*      */       
/*      */       try {
/*  791 */         MessageDigest signMd5 = NTLMEngineImpl.getMD5();
/*  792 */         MessageDigest sealMd5 = NTLMEngineImpl.getMD5();
/*  793 */         signMd5.update(exportedSessionKey);
/*  794 */         sealMd5.update(exportedSessionKey);
/*  795 */         if (mode == NTLMEngineImpl.Mode.CLIENT) {
/*      */           
/*  797 */           signMd5.update(NTLMEngineImpl.SIGN_MAGIC_CLIENT);
/*  798 */           sealMd5.update(NTLMEngineImpl.SEAL_MAGIC_CLIENT);
/*      */         }
/*      */         else {
/*      */           
/*  802 */           signMd5.update(NTLMEngineImpl.SIGN_MAGIC_SERVER);
/*  803 */           sealMd5.update(NTLMEngineImpl.SEAL_MAGIC_SERVER);
/*      */         } 
/*  805 */         this.signingKey = signMd5.digest();
/*  806 */         this.sealingKey = sealMd5.digest();
/*      */       }
/*  808 */       catch (Exception e) {
/*      */         
/*  810 */         throw new NTLMEngineException(e.getMessage(), e);
/*      */       } 
/*  812 */       this.rc4 = initCipher();
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] getSigningKey() {
/*  817 */       return this.signingKey;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getSealingKey() {
/*  823 */       return this.sealingKey;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private Cipher initCipher() throws NTLMEngineException {
/*      */       Cipher cipher;
/*      */       try {
/*  831 */         cipher = Cipher.getInstance("RC4");
/*  832 */         if (this.mode == NTLMEngineImpl.Mode.CLIENT)
/*      */         {
/*  834 */           cipher.init(1, new SecretKeySpec(this.sealingKey, "RC4"));
/*      */         }
/*      */         else
/*      */         {
/*  838 */           cipher.init(2, new SecretKeySpec(this.sealingKey, "RC4"));
/*      */         }
/*      */       
/*  841 */       } catch (Exception e) {
/*      */         
/*  843 */         throw new NTLMEngineException(e.getMessage(), e);
/*      */       } 
/*  845 */       return cipher;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void advanceMessageSequence() throws NTLMEngineException {
/*  851 */       if (!this.isConnection) {
/*      */         
/*  853 */         MessageDigest sealMd5 = NTLMEngineImpl.getMD5();
/*  854 */         sealMd5.update(this.sealingKey);
/*  855 */         byte[] seqNumBytes = new byte[4];
/*  856 */         NTLMEngineImpl.writeULong(seqNumBytes, this.sequenceNumber, 0);
/*  857 */         sealMd5.update(seqNumBytes);
/*  858 */         this.sealingKey = sealMd5.digest();
/*  859 */         initCipher();
/*      */       } 
/*  861 */       this.sequenceNumber++;
/*      */     }
/*      */ 
/*      */     
/*      */     private byte[] encrypt(byte[] data) {
/*  866 */       return this.rc4.update(data);
/*      */     }
/*      */ 
/*      */     
/*      */     private byte[] decrypt(byte[] data) {
/*  871 */       return this.rc4.update(data);
/*      */     }
/*      */ 
/*      */     
/*      */     private byte[] computeSignature(byte[] message) {
/*  876 */       byte[] sig = new byte[16];
/*      */ 
/*      */       
/*  879 */       sig[0] = 1;
/*  880 */       sig[1] = 0;
/*  881 */       sig[2] = 0;
/*  882 */       sig[3] = 0;
/*      */ 
/*      */       
/*  885 */       NTLMEngineImpl.HMACMD5 hmacMD5 = new NTLMEngineImpl.HMACMD5(this.signingKey);
/*  886 */       hmacMD5.update(NTLMEngineImpl.encodeLong(this.sequenceNumber));
/*  887 */       hmacMD5.update(message);
/*  888 */       byte[] hmac = hmacMD5.getOutput();
/*  889 */       byte[] trimmedHmac = new byte[8];
/*  890 */       System.arraycopy(hmac, 0, trimmedHmac, 0, 8);
/*  891 */       byte[] encryptedHmac = encrypt(trimmedHmac);
/*  892 */       System.arraycopy(encryptedHmac, 0, sig, 4, 8);
/*      */ 
/*      */       
/*  895 */       NTLMEngineImpl.encodeLong(sig, 12, this.sequenceNumber);
/*      */       
/*  897 */       return sig;
/*      */     }
/*      */ 
/*      */     
/*      */     private boolean validateSignature(byte[] signature, byte[] message) {
/*  902 */       byte[] computedSignature = computeSignature(message);
/*      */ 
/*      */ 
/*      */       
/*  906 */       return Arrays.equals(signature, computedSignature);
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] signAndEncryptMessage(byte[] cleartextMessage) throws NTLMEngineException {
/*  911 */       byte[] encryptedMessage = encrypt(cleartextMessage);
/*  912 */       byte[] signature = computeSignature(cleartextMessage);
/*  913 */       byte[] outMessage = new byte[signature.length + encryptedMessage.length];
/*  914 */       System.arraycopy(signature, 0, outMessage, 0, signature.length);
/*  915 */       System.arraycopy(encryptedMessage, 0, outMessage, signature.length, encryptedMessage.length);
/*  916 */       advanceMessageSequence();
/*  917 */       return outMessage;
/*      */     }
/*      */ 
/*      */     
/*      */     public byte[] decryptAndVerifySignedMessage(byte[] inMessage) throws NTLMEngineException {
/*  922 */       byte[] signature = new byte[16];
/*  923 */       System.arraycopy(inMessage, 0, signature, 0, signature.length);
/*  924 */       byte[] encryptedMessage = new byte[inMessage.length - 16];
/*  925 */       System.arraycopy(inMessage, 16, encryptedMessage, 0, encryptedMessage.length);
/*  926 */       byte[] cleartextMessage = decrypt(encryptedMessage);
/*  927 */       if (!validateSignature(signature, cleartextMessage))
/*      */       {
/*  929 */         throw new NTLMEngineException("Wrong signature");
/*      */       }
/*  931 */       advanceMessageSequence();
/*  932 */       return cleartextMessage;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] encodeLong(int value) {
/*  939 */     byte[] enc = new byte[4];
/*  940 */     encodeLong(enc, 0, value);
/*  941 */     return enc;
/*      */   }
/*      */ 
/*      */   
/*      */   private static void encodeLong(byte[] buf, int offset, int value) {
/*  946 */     buf[offset + 0] = (byte)(value & 0xFF);
/*  947 */     buf[offset + 1] = (byte)(value >> 8 & 0xFF);
/*  948 */     buf[offset + 2] = (byte)(value >> 16 & 0xFF);
/*  949 */     buf[offset + 3] = (byte)(value >> 24 & 0xFF);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static byte[] createBlob(byte[] clientChallenge, byte[] targetInformation, byte[] timestamp) {
/*  964 */     byte[] blobSignature = { 1, 1, 0, 0 };
/*  965 */     byte[] reserved = { 0, 0, 0, 0 };
/*  966 */     byte[] unknown1 = { 0, 0, 0, 0 };
/*  967 */     byte[] unknown2 = { 0, 0, 0, 0 };
/*  968 */     byte[] blob = new byte[blobSignature.length + reserved.length + timestamp.length + 8 + unknown1.length + targetInformation.length + unknown2.length];
/*      */     
/*  970 */     int offset = 0;
/*  971 */     System.arraycopy(blobSignature, 0, blob, offset, blobSignature.length);
/*  972 */     offset += blobSignature.length;
/*  973 */     System.arraycopy(reserved, 0, blob, offset, reserved.length);
/*  974 */     offset += reserved.length;
/*  975 */     System.arraycopy(timestamp, 0, blob, offset, timestamp.length);
/*  976 */     offset += timestamp.length;
/*  977 */     System.arraycopy(clientChallenge, 0, blob, offset, 8);
/*  978 */     offset += 8;
/*  979 */     System.arraycopy(unknown1, 0, blob, offset, unknown1.length);
/*  980 */     offset += unknown1.length;
/*  981 */     System.arraycopy(targetInformation, 0, blob, offset, targetInformation.length);
/*  982 */     offset += targetInformation.length;
/*  983 */     System.arraycopy(unknown2, 0, blob, offset, unknown2.length);
/*  984 */     offset += unknown2.length;
/*  985 */     return blob;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Key createDESKey(byte[] bytes, int offset) {
/* 1001 */     byte[] keyBytes = new byte[7];
/* 1002 */     System.arraycopy(bytes, offset, keyBytes, 0, 7);
/* 1003 */     byte[] material = new byte[8];
/* 1004 */     material[0] = keyBytes[0];
/* 1005 */     material[1] = (byte)(keyBytes[0] << 7 | (keyBytes[1] & 0xFF) >>> 1);
/* 1006 */     material[2] = (byte)(keyBytes[1] << 6 | (keyBytes[2] & 0xFF) >>> 2);
/* 1007 */     material[3] = (byte)(keyBytes[2] << 5 | (keyBytes[3] & 0xFF) >>> 3);
/* 1008 */     material[4] = (byte)(keyBytes[3] << 4 | (keyBytes[4] & 0xFF) >>> 4);
/* 1009 */     material[5] = (byte)(keyBytes[4] << 3 | (keyBytes[5] & 0xFF) >>> 5);
/* 1010 */     material[6] = (byte)(keyBytes[5] << 2 | (keyBytes[6] & 0xFF) >>> 6);
/* 1011 */     material[7] = (byte)(keyBytes[6] << 1);
/* 1012 */     oddParity(material);
/* 1013 */     return new SecretKeySpec(material, "DES");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void oddParity(byte[] bytes) {
/* 1023 */     for (int i = 0; i < bytes.length; i++) {
/* 1024 */       byte b = bytes[i];
/* 1025 */       boolean needsParity = (((b >>> 7 ^ b >>> 6 ^ b >>> 5 ^ b >>> 4 ^ b >>> 3 ^ b >>> 2 ^ b >>> 1) & 0x1) == 0);
/*      */       
/* 1027 */       if (needsParity) {
/* 1028 */         bytes[i] = (byte)(bytes[i] | 0x1);
/*      */       } else {
/* 1030 */         bytes[i] = (byte)(bytes[i] & 0xFFFFFFFE);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Charset getCharset(int flags) throws NTLMEngineException {
/* 1042 */     if ((flags & 0x1) == 0) {
/* 1043 */       return DEFAULT_CHARSET;
/*      */     }
/* 1045 */     if (UNICODE_LITTLE_UNMARKED == null) {
/* 1046 */       throw new NTLMEngineException("Unicode not supported");
/*      */     }
/* 1048 */     return UNICODE_LITTLE_UNMARKED;
/*      */   }
/*      */ 
/*      */   
/*      */   private static String stripDotSuffix(String value) {
/* 1053 */     if (value == null) {
/* 1054 */       return null;
/*      */     }
/* 1056 */     int index = value.indexOf('.');
/* 1057 */     if (index != -1) {
/* 1058 */       return value.substring(0, index);
/*      */     }
/* 1060 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   private static String convertHost(String host) {
/* 1065 */     return stripDotSuffix(host);
/*      */   }
/*      */ 
/*      */   
/*      */   private static String convertDomain(String domain) {
/* 1070 */     return stripDotSuffix(domain);
/*      */   }
/*      */ 
/*      */   
/*      */   static class NTLMMessage
/*      */   {
/* 1076 */     protected byte[] messageContents = null;
/*      */ 
/*      */     
/* 1079 */     protected int currentOutputPosition = 0;
/*      */ 
/*      */ 
/*      */     
/*      */     NTLMMessage() {}
/*      */ 
/*      */     
/*      */     NTLMMessage(String messageBody, int expectedType) throws NTLMEngineException {
/* 1087 */       this(Base64.decodeBase64(messageBody.getBytes(NTLMEngineImpl.DEFAULT_CHARSET)), expectedType);
/*      */     }
/*      */ 
/*      */     
/*      */     NTLMMessage(byte[] message, int expectedType) throws NTLMEngineException {
/* 1092 */       this.messageContents = message;
/*      */       
/* 1094 */       if (this.messageContents.length < NTLMEngineImpl.SIGNATURE.length) {
/* 1095 */         throw new NTLMEngineException("NTLM message decoding error - packet too short");
/*      */       }
/* 1097 */       int i = 0;
/* 1098 */       while (i < NTLMEngineImpl.SIGNATURE.length) {
/* 1099 */         if (this.messageContents[i] != NTLMEngineImpl.SIGNATURE[i]) {
/* 1100 */           throw new NTLMEngineException("NTLM message expected - instead got unrecognized bytes");
/*      */         }
/*      */         
/* 1103 */         i++;
/*      */       } 
/*      */ 
/*      */       
/* 1107 */       int type = readULong(NTLMEngineImpl.SIGNATURE.length);
/* 1108 */       if (type != expectedType) {
/* 1109 */         throw new NTLMEngineException("NTLM type " + Integer.toString(expectedType) + " message expected - instead got type " + Integer.toString(type));
/*      */       }
/*      */ 
/*      */       
/* 1113 */       this.currentOutputPosition = this.messageContents.length;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected int getPreambleLength() {
/* 1121 */       return NTLMEngineImpl.SIGNATURE.length + 4;
/*      */     }
/*      */ 
/*      */     
/*      */     protected int getMessageLength() {
/* 1126 */       return this.currentOutputPosition;
/*      */     }
/*      */ 
/*      */     
/*      */     protected byte readByte(int position) throws NTLMEngineException {
/* 1131 */       if (this.messageContents.length < position + 1) {
/* 1132 */         throw new NTLMEngineException("NTLM: Message too short");
/*      */       }
/* 1134 */       return this.messageContents[position];
/*      */     }
/*      */ 
/*      */     
/*      */     protected void readBytes(byte[] buffer, int position) throws NTLMEngineException {
/* 1139 */       if (this.messageContents.length < position + buffer.length) {
/* 1140 */         throw new NTLMEngineException("NTLM: Message too short");
/*      */       }
/* 1142 */       System.arraycopy(this.messageContents, position, buffer, 0, buffer.length);
/*      */     }
/*      */ 
/*      */     
/*      */     protected int readUShort(int position) throws NTLMEngineException {
/* 1147 */       return NTLMEngineImpl.readUShort(this.messageContents, position);
/*      */     }
/*      */ 
/*      */     
/*      */     protected int readULong(int position) throws NTLMEngineException {
/* 1152 */       return NTLMEngineImpl.readULong(this.messageContents, position);
/*      */     }
/*      */ 
/*      */     
/*      */     protected byte[] readSecurityBuffer(int position) throws NTLMEngineException {
/* 1157 */       return NTLMEngineImpl.readSecurityBuffer(this.messageContents, position);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void prepareResponse(int maxlength, int messageType) {
/* 1169 */       this.messageContents = new byte[maxlength];
/* 1170 */       this.currentOutputPosition = 0;
/* 1171 */       addBytes(NTLMEngineImpl.SIGNATURE);
/* 1172 */       addULong(messageType);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void addByte(byte b) {
/* 1182 */       this.messageContents[this.currentOutputPosition] = b;
/* 1183 */       this.currentOutputPosition++;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void addBytes(byte[] bytes) {
/* 1193 */       if (bytes == null) {
/*      */         return;
/*      */       }
/* 1196 */       for (byte b : bytes) {
/* 1197 */         this.messageContents[this.currentOutputPosition] = b;
/* 1198 */         this.currentOutputPosition++;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     protected void addUShort(int value) {
/* 1204 */       addByte((byte)(value & 0xFF));
/* 1205 */       addByte((byte)(value >> 8 & 0xFF));
/*      */     }
/*      */ 
/*      */     
/*      */     protected void addULong(int value) {
/* 1210 */       addByte((byte)(value & 0xFF));
/* 1211 */       addByte((byte)(value >> 8 & 0xFF));
/* 1212 */       addByte((byte)(value >> 16 & 0xFF));
/* 1213 */       addByte((byte)(value >> 24 & 0xFF));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getResponse() {
/* 1223 */       return new String(Base64.encodeBase64(getBytes()), Consts.ASCII);
/*      */     }
/*      */     
/*      */     public byte[] getBytes() {
/* 1227 */       if (this.messageContents == null) {
/* 1228 */         buildMessage();
/*      */       }
/*      */       
/* 1231 */       if (this.messageContents.length > this.currentOutputPosition) {
/* 1232 */         byte[] tmp = new byte[this.currentOutputPosition];
/* 1233 */         System.arraycopy(this.messageContents, 0, tmp, 0, this.currentOutputPosition);
/* 1234 */         this.messageContents = tmp;
/*      */       } 
/* 1236 */       return this.messageContents;
/*      */     }
/*      */     
/*      */     protected void buildMessage() {
/* 1240 */       throw new RuntimeException("Message builder not implemented for " + getClass().getName());
/*      */     }
/*      */   }
/*      */   
/*      */   static class Type1Message
/*      */     extends NTLMMessage
/*      */   {
/*      */     private final byte[] hostBytes;
/*      */     private final byte[] domainBytes;
/*      */     private final int flags;
/*      */     
/*      */     Type1Message(String domain, String host) throws NTLMEngineException {
/* 1252 */       this(domain, host, (Integer)null);
/*      */     }
/*      */ 
/*      */     
/*      */     Type1Message(String domain, String host, Integer flags) throws NTLMEngineException {
/* 1257 */       this.flags = (flags == null) ? getDefaultFlags() : flags.intValue();
/*      */ 
/*      */       
/* 1260 */       String unqualifiedHost = NTLMEngineImpl.convertHost(host);
/*      */       
/* 1262 */       String unqualifiedDomain = NTLMEngineImpl.convertDomain(domain);
/*      */       
/* 1264 */       this.hostBytes = (unqualifiedHost != null) ? unqualifiedHost.getBytes(NTLMEngineImpl.UNICODE_LITTLE_UNMARKED) : null;
/*      */       
/* 1266 */       this.domainBytes = (unqualifiedDomain != null) ? unqualifiedDomain.toUpperCase(Locale.ROOT).getBytes(NTLMEngineImpl.UNICODE_LITTLE_UNMARKED) : null;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     Type1Message() {
/* 1272 */       this.hostBytes = null;
/* 1273 */       this.domainBytes = null;
/* 1274 */       this.flags = getDefaultFlags();
/*      */     }
/*      */     
/*      */     private int getDefaultFlags() {
/* 1278 */       return -1576500735;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     protected void buildMessage() {
/* 1310 */       int domainBytesLength = 0;
/* 1311 */       if (this.domainBytes != null) {
/* 1312 */         domainBytesLength = this.domainBytes.length;
/*      */       }
/* 1314 */       int hostBytesLength = 0;
/* 1315 */       if (this.hostBytes != null) {
/* 1316 */         hostBytesLength = this.hostBytes.length;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1321 */       int finalLength = 40 + hostBytesLength + domainBytesLength;
/*      */ 
/*      */ 
/*      */       
/* 1325 */       prepareResponse(finalLength, 1);
/*      */ 
/*      */       
/* 1328 */       addULong(this.flags);
/*      */ 
/*      */       
/* 1331 */       addUShort(domainBytesLength);
/* 1332 */       addUShort(domainBytesLength);
/*      */ 
/*      */       
/* 1335 */       addULong(hostBytesLength + 32 + 8);
/*      */ 
/*      */       
/* 1338 */       addUShort(hostBytesLength);
/* 1339 */       addUShort(hostBytesLength);
/*      */ 
/*      */       
/* 1342 */       addULong(40);
/*      */ 
/*      */       
/* 1345 */       addUShort(261);
/*      */       
/* 1347 */       addULong(2600);
/*      */       
/* 1349 */       addUShort(3840);
/*      */ 
/*      */       
/* 1352 */       if (this.hostBytes != null) {
/* 1353 */         addBytes(this.hostBytes);
/*      */       }
/*      */       
/* 1356 */       if (this.domainBytes != null) {
/* 1357 */         addBytes(this.domainBytes);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   static class Type2Message
/*      */     extends NTLMMessage
/*      */   {
/*      */     protected final byte[] challenge;
/*      */     protected String target;
/*      */     protected byte[] targetInfo;
/*      */     protected final int flags;
/*      */     
/*      */     Type2Message(String messageBody) throws NTLMEngineException {
/* 1371 */       this(Base64.decodeBase64(messageBody.getBytes(NTLMEngineImpl.DEFAULT_CHARSET)));
/*      */     }
/*      */     
/*      */     Type2Message(byte[] message) throws NTLMEngineException {
/* 1375 */       super(message, 2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1392 */       this.challenge = new byte[8];
/* 1393 */       readBytes(this.challenge, 24);
/*      */       
/* 1395 */       this.flags = readULong(20);
/*      */ 
/*      */       
/* 1398 */       this.target = null;
/*      */ 
/*      */ 
/*      */       
/* 1402 */       if (getMessageLength() >= 20) {
/* 1403 */         byte[] bytes = readSecurityBuffer(12);
/* 1404 */         if (bytes.length != 0) {
/* 1405 */           this.target = new String(bytes, NTLMEngineImpl.getCharset(this.flags));
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1410 */       this.targetInfo = null;
/*      */       
/* 1412 */       if (getMessageLength() >= 48) {
/* 1413 */         byte[] bytes = readSecurityBuffer(40);
/* 1414 */         if (bytes.length != 0) {
/* 1415 */           this.targetInfo = bytes;
/*      */         }
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     byte[] getChallenge() {
/* 1422 */       return this.challenge;
/*      */     }
/*      */ 
/*      */     
/*      */     String getTarget() {
/* 1427 */       return this.target;
/*      */     }
/*      */ 
/*      */     
/*      */     byte[] getTargetInfo() {
/* 1432 */       return this.targetInfo;
/*      */     }
/*      */ 
/*      */     
/*      */     int getFlags() {
/* 1437 */       return this.flags;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class Type3Message
/*      */     extends NTLMMessage
/*      */   {
/*      */     protected final byte[] type1Message;
/*      */ 
/*      */     
/*      */     protected final byte[] type2Message;
/*      */ 
/*      */     
/*      */     protected final int type2Flags;
/*      */     
/*      */     protected final byte[] domainBytes;
/*      */     
/*      */     protected final byte[] hostBytes;
/*      */     
/*      */     protected final byte[] userBytes;
/*      */     
/*      */     protected byte[] lmResp;
/*      */     
/*      */     protected byte[] ntResp;
/*      */     
/*      */     protected final byte[] sessionKey;
/*      */     
/*      */     protected final byte[] exportedSessionKey;
/*      */     
/*      */     protected final boolean computeMic;
/*      */ 
/*      */     
/*      */     Type3Message(String domain, String host, String user, String password, byte[] nonce, int type2Flags, String target, byte[] targetInformation) throws NTLMEngineException {
/* 1472 */       this(domain, host, user, password, nonce, type2Flags, target, targetInformation, (Certificate)null, (byte[])null, (byte[])null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Type3Message(Random random, long currentTime, String domain, String host, String user, String password, byte[] nonce, int type2Flags, String target, byte[] targetInformation) throws NTLMEngineException {
/* 1487 */       this(random, currentTime, domain, host, user, password, nonce, type2Flags, target, targetInformation, (Certificate)null, (byte[])null, (byte[])null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Type3Message(String domain, String host, String user, String password, byte[] nonce, int type2Flags, String target, byte[] targetInformation, Certificate peerServerCertificate, byte[] type1Message, byte[] type2Message) throws NTLMEngineException {
/* 1503 */       this(NTLMEngineImpl.RND_GEN, System.currentTimeMillis(), domain, host, user, password, nonce, type2Flags, target, targetInformation, peerServerCertificate, type1Message, type2Message);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     Type3Message(Random random, long currentTime, String domain, String host, String user, String password, byte[] nonce, int type2Flags, String target, byte[] targetInformation, Certificate peerServerCertificate, byte[] type1Message, byte[] type2Message) throws NTLMEngineException {
/*      */       byte[] userSessionKey;
/* 1521 */       if (random == null) {
/* 1522 */         throw new NTLMEngineException("Random generator not available");
/*      */       }
/*      */ 
/*      */       
/* 1526 */       this.type2Flags = type2Flags;
/* 1527 */       this.type1Message = type1Message;
/* 1528 */       this.type2Message = type2Message;
/*      */ 
/*      */       
/* 1531 */       String unqualifiedHost = NTLMEngineImpl.convertHost(host);
/*      */       
/* 1533 */       String unqualifiedDomain = NTLMEngineImpl.convertDomain(domain);
/*      */       
/* 1535 */       byte[] responseTargetInformation = targetInformation;
/* 1536 */       if (peerServerCertificate != null) {
/* 1537 */         responseTargetInformation = addGssMicAvsToTargetInfo(targetInformation, peerServerCertificate);
/* 1538 */         this.computeMic = true;
/*      */       } else {
/* 1540 */         this.computeMic = false;
/*      */       } 
/*      */ 
/*      */       
/* 1544 */       NTLMEngineImpl.CipherGen gen = new NTLMEngineImpl.CipherGen(random, currentTime, unqualifiedDomain, user, password, nonce, target, responseTargetInformation);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1558 */         if ((type2Flags & 0x800000) != 0 && targetInformation != null && target != null) {
/*      */ 
/*      */           
/* 1561 */           this.ntResp = gen.getNTLMv2Response();
/* 1562 */           this.lmResp = gen.getLMv2Response();
/* 1563 */           if ((type2Flags & 0x80) != 0) {
/* 1564 */             userSessionKey = gen.getLanManagerSessionKey();
/*      */           } else {
/* 1566 */             userSessionKey = gen.getNTLMv2UserSessionKey();
/*      */           }
/*      */         
/*      */         }
/* 1570 */         else if ((type2Flags & 0x80000) != 0) {
/*      */           
/* 1572 */           this.ntResp = gen.getNTLM2SessionResponse();
/* 1573 */           this.lmResp = gen.getLM2SessionResponse();
/* 1574 */           if ((type2Flags & 0x80) != 0) {
/* 1575 */             userSessionKey = gen.getLanManagerSessionKey();
/*      */           } else {
/* 1577 */             userSessionKey = gen.getNTLM2SessionResponseUserSessionKey();
/*      */           } 
/*      */         } else {
/* 1580 */           this.ntResp = gen.getNTLMResponse();
/* 1581 */           this.lmResp = gen.getLMResponse();
/* 1582 */           if ((type2Flags & 0x80) != 0) {
/* 1583 */             userSessionKey = gen.getLanManagerSessionKey();
/*      */           } else {
/* 1585 */             userSessionKey = gen.getNTLMUserSessionKey();
/*      */           }
/*      */         
/*      */         } 
/* 1589 */       } catch (NTLMEngineException e) {
/*      */ 
/*      */         
/* 1592 */         this.ntResp = new byte[0];
/* 1593 */         this.lmResp = gen.getLMResponse();
/* 1594 */         if ((type2Flags & 0x80) != 0) {
/* 1595 */           userSessionKey = gen.getLanManagerSessionKey();
/*      */         } else {
/* 1597 */           userSessionKey = gen.getLMUserSessionKey();
/*      */         } 
/*      */       } 
/*      */       
/* 1601 */       if ((type2Flags & 0x10) != 0) {
/* 1602 */         if ((type2Flags & 0x40000000) != 0) {
/* 1603 */           this.exportedSessionKey = gen.getSecondaryKey();
/* 1604 */           this.sessionKey = NTLMEngineImpl.RC4(this.exportedSessionKey, userSessionKey);
/*      */         } else {
/* 1606 */           this.sessionKey = userSessionKey;
/* 1607 */           this.exportedSessionKey = this.sessionKey;
/*      */         } 
/*      */       } else {
/* 1610 */         if (this.computeMic) {
/* 1611 */           throw new NTLMEngineException("Cannot sign/seal: no exported session key");
/*      */         }
/* 1613 */         this.sessionKey = null;
/* 1614 */         this.exportedSessionKey = null;
/*      */       } 
/* 1616 */       Charset charset = NTLMEngineImpl.getCharset(type2Flags);
/* 1617 */       this.hostBytes = (unqualifiedHost != null) ? unqualifiedHost.getBytes(charset) : null;
/* 1618 */       this.domainBytes = (unqualifiedDomain != null) ? unqualifiedDomain.toUpperCase(Locale.ROOT).getBytes(charset) : null;
/*      */       
/* 1620 */       this.userBytes = user.getBytes(charset);
/*      */     }
/*      */     
/*      */     public byte[] getEncryptedRandomSessionKey() {
/* 1624 */       return this.sessionKey;
/*      */     }
/*      */     
/*      */     public byte[] getExportedSessionKey() {
/* 1628 */       return this.exportedSessionKey;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     protected void buildMessage() {
/* 1634 */       int sessionKeyLen, ntRespLen = this.ntResp.length;
/* 1635 */       int lmRespLen = this.lmResp.length;
/*      */       
/* 1637 */       int domainLen = (this.domainBytes != null) ? this.domainBytes.length : 0;
/* 1638 */       int hostLen = (this.hostBytes != null) ? this.hostBytes.length : 0;
/* 1639 */       int userLen = this.userBytes.length;
/*      */       
/* 1641 */       if (this.sessionKey != null) {
/* 1642 */         sessionKeyLen = this.sessionKey.length;
/*      */       } else {
/* 1644 */         sessionKeyLen = 0;
/*      */       } 
/*      */ 
/*      */       
/* 1648 */       int lmRespOffset = 72 + (this.computeMic ? 16 : 0);
/*      */       
/* 1650 */       int ntRespOffset = lmRespOffset + lmRespLen;
/* 1651 */       int domainOffset = ntRespOffset + ntRespLen;
/* 1652 */       int userOffset = domainOffset + domainLen;
/* 1653 */       int hostOffset = userOffset + userLen;
/* 1654 */       int sessionKeyOffset = hostOffset + hostLen;
/* 1655 */       int finalLength = sessionKeyOffset + sessionKeyLen;
/*      */ 
/*      */       
/* 1658 */       prepareResponse(finalLength, 3);
/*      */ 
/*      */       
/* 1661 */       addUShort(lmRespLen);
/* 1662 */       addUShort(lmRespLen);
/*      */ 
/*      */       
/* 1665 */       addULong(lmRespOffset);
/*      */ 
/*      */       
/* 1668 */       addUShort(ntRespLen);
/* 1669 */       addUShort(ntRespLen);
/*      */ 
/*      */       
/* 1672 */       addULong(ntRespOffset);
/*      */ 
/*      */       
/* 1675 */       addUShort(domainLen);
/* 1676 */       addUShort(domainLen);
/*      */ 
/*      */       
/* 1679 */       addULong(domainOffset);
/*      */ 
/*      */       
/* 1682 */       addUShort(userLen);
/* 1683 */       addUShort(userLen);
/*      */ 
/*      */       
/* 1686 */       addULong(userOffset);
/*      */ 
/*      */       
/* 1689 */       addUShort(hostLen);
/* 1690 */       addUShort(hostLen);
/*      */ 
/*      */       
/* 1693 */       addULong(hostOffset);
/*      */ 
/*      */       
/* 1696 */       addUShort(sessionKeyLen);
/* 1697 */       addUShort(sessionKeyLen);
/*      */ 
/*      */       
/* 1700 */       addULong(sessionKeyOffset);
/*      */ 
/*      */       
/* 1703 */       addULong(this.type2Flags);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1734 */       addUShort(261);
/*      */       
/* 1736 */       addULong(2600);
/*      */       
/* 1738 */       addUShort(3840);
/*      */       
/* 1740 */       int micPosition = -1;
/* 1741 */       if (this.computeMic) {
/* 1742 */         micPosition = this.currentOutputPosition;
/* 1743 */         this.currentOutputPosition += 16;
/*      */       } 
/*      */ 
/*      */       
/* 1747 */       addBytes(this.lmResp);
/* 1748 */       addBytes(this.ntResp);
/* 1749 */       addBytes(this.domainBytes);
/* 1750 */       addBytes(this.userBytes);
/* 1751 */       addBytes(this.hostBytes);
/* 1752 */       if (this.sessionKey != null) {
/* 1753 */         addBytes(this.sessionKey);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1758 */       if (this.computeMic) {
/*      */         
/* 1760 */         NTLMEngineImpl.HMACMD5 hmacMD5 = new NTLMEngineImpl.HMACMD5(this.exportedSessionKey);
/* 1761 */         hmacMD5.update(this.type1Message);
/* 1762 */         hmacMD5.update(this.type2Message);
/* 1763 */         hmacMD5.update(this.messageContents);
/* 1764 */         byte[] mic = hmacMD5.getOutput();
/* 1765 */         System.arraycopy(mic, 0, this.messageContents, micPosition, mic.length);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private byte[] addGssMicAvsToTargetInfo(byte[] originalTargetInfo, Certificate peerServerCertificate) throws NTLMEngineException {
/* 1776 */       byte[] channelBindingsHash, newTargetInfo = new byte[originalTargetInfo.length + 8 + 20];
/* 1777 */       int appendLength = originalTargetInfo.length - 4;
/* 1778 */       System.arraycopy(originalTargetInfo, 0, newTargetInfo, 0, appendLength);
/* 1779 */       NTLMEngineImpl.writeUShort(newTargetInfo, 6, appendLength);
/* 1780 */       NTLMEngineImpl.writeUShort(newTargetInfo, 4, appendLength + 2);
/* 1781 */       NTLMEngineImpl.writeULong(newTargetInfo, 2, appendLength + 4);
/* 1782 */       NTLMEngineImpl.writeUShort(newTargetInfo, 10, appendLength + 8);
/* 1783 */       NTLMEngineImpl.writeUShort(newTargetInfo, 16, appendLength + 10);
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1788 */         byte[] certBytes = peerServerCertificate.getEncoded();
/* 1789 */         MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
/* 1790 */         byte[] certHashBytes = sha256.digest(certBytes);
/* 1791 */         byte[] channelBindingStruct = new byte[20 + NTLMEngineImpl.MAGIC_TLS_SERVER_ENDPOINT.length + certHashBytes.length];
/*      */         
/* 1793 */         NTLMEngineImpl.writeULong(channelBindingStruct, 53, 16);
/* 1794 */         System.arraycopy(NTLMEngineImpl.MAGIC_TLS_SERVER_ENDPOINT, 0, channelBindingStruct, 20, NTLMEngineImpl.MAGIC_TLS_SERVER_ENDPOINT.length);
/*      */         
/* 1796 */         System.arraycopy(certHashBytes, 0, channelBindingStruct, 20 + NTLMEngineImpl.MAGIC_TLS_SERVER_ENDPOINT.length, certHashBytes.length);
/*      */         
/* 1798 */         MessageDigest md5 = NTLMEngineImpl.getMD5();
/* 1799 */         channelBindingsHash = md5.digest(channelBindingStruct);
/*      */       }
/* 1801 */       catch (CertificateEncodingException e) {
/*      */         
/* 1803 */         throw new NTLMEngineException(e.getMessage(), e);
/*      */       }
/* 1805 */       catch (NoSuchAlgorithmException e) {
/*      */         
/* 1807 */         throw new NTLMEngineException(e.getMessage(), e);
/*      */       } 
/*      */       
/* 1810 */       System.arraycopy(channelBindingsHash, 0, newTargetInfo, appendLength + 12, 16);
/* 1811 */       return newTargetInfo;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static void writeUShort(byte[] buffer, int value, int offset) {
/* 1817 */     buffer[offset] = (byte)(value & 0xFF);
/* 1818 */     buffer[offset + 1] = (byte)(value >> 8 & 0xFF);
/*      */   }
/*      */   
/*      */   static void writeULong(byte[] buffer, int value, int offset) {
/* 1822 */     buffer[offset] = (byte)(value & 0xFF);
/* 1823 */     buffer[offset + 1] = (byte)(value >> 8 & 0xFF);
/* 1824 */     buffer[offset + 2] = (byte)(value >> 16 & 0xFF);
/* 1825 */     buffer[offset + 3] = (byte)(value >> 24 & 0xFF);
/*      */   }
/*      */   
/*      */   static int F(int x, int y, int z) {
/* 1829 */     return x & y | (x ^ 0xFFFFFFFF) & z;
/*      */   }
/*      */   
/*      */   static int G(int x, int y, int z) {
/* 1833 */     return x & y | x & z | y & z;
/*      */   }
/*      */   
/*      */   static int H(int x, int y, int z) {
/* 1837 */     return x ^ y ^ z;
/*      */   }
/*      */   
/*      */   static int rotintlft(int val, int numbits) {
/* 1841 */     return val << numbits | val >>> 32 - numbits;
/*      */   }
/*      */   
/*      */   static MessageDigest getMD5() {
/*      */     try {
/* 1846 */       return MessageDigest.getInstance("MD5");
/* 1847 */     } catch (NoSuchAlgorithmException ex) {
/* 1848 */       throw new RuntimeException("MD5 message digest doesn't seem to exist - fatal error: " + ex.getMessage(), ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class MD4
/*      */   {
/* 1860 */     protected int A = 1732584193;
/* 1861 */     protected int B = -271733879;
/* 1862 */     protected int C = -1732584194;
/* 1863 */     protected int D = 271733878;
/* 1864 */     protected long count = 0L;
/* 1865 */     protected final byte[] dataBuffer = new byte[64];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void update(byte[] input) {
/* 1874 */       int curBufferPos = (int)(this.count & 0x3FL);
/* 1875 */       int inputIndex = 0;
/* 1876 */       while (input.length - inputIndex + curBufferPos >= this.dataBuffer.length) {
/*      */ 
/*      */ 
/*      */         
/* 1880 */         int transferAmt = this.dataBuffer.length - curBufferPos;
/* 1881 */         System.arraycopy(input, inputIndex, this.dataBuffer, curBufferPos, transferAmt);
/* 1882 */         this.count += transferAmt;
/* 1883 */         curBufferPos = 0;
/* 1884 */         inputIndex += transferAmt;
/* 1885 */         processBuffer();
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1890 */       if (inputIndex < input.length) {
/* 1891 */         int transferAmt = input.length - inputIndex;
/* 1892 */         System.arraycopy(input, inputIndex, this.dataBuffer, curBufferPos, transferAmt);
/* 1893 */         this.count += transferAmt;
/* 1894 */         curBufferPos += transferAmt;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     byte[] getOutput() {
/* 1901 */       int bufferIndex = (int)(this.count & 0x3FL);
/* 1902 */       int padLen = (bufferIndex < 56) ? (56 - bufferIndex) : (120 - bufferIndex);
/* 1903 */       byte[] postBytes = new byte[padLen + 8];
/*      */ 
/*      */       
/* 1906 */       postBytes[0] = Byte.MIN_VALUE;
/*      */       
/* 1908 */       for (int i = 0; i < 8; i++) {
/* 1909 */         postBytes[padLen + i] = (byte)(int)(this.count * 8L >>> 8 * i);
/*      */       }
/*      */ 
/*      */       
/* 1913 */       update(postBytes);
/*      */ 
/*      */       
/* 1916 */       byte[] result = new byte[16];
/* 1917 */       NTLMEngineImpl.writeULong(result, this.A, 0);
/* 1918 */       NTLMEngineImpl.writeULong(result, this.B, 4);
/* 1919 */       NTLMEngineImpl.writeULong(result, this.C, 8);
/* 1920 */       NTLMEngineImpl.writeULong(result, this.D, 12);
/* 1921 */       return result;
/*      */     }
/*      */ 
/*      */     
/*      */     protected void processBuffer() {
/* 1926 */       int[] d = new int[16];
/*      */       
/* 1928 */       for (int i = 0; i < 16; i++) {
/* 1929 */         d[i] = (this.dataBuffer[i * 4] & 0xFF) + ((this.dataBuffer[i * 4 + 1] & 0xFF) << 8) + ((this.dataBuffer[i * 4 + 2] & 0xFF) << 16) + ((this.dataBuffer[i * 4 + 3] & 0xFF) << 24);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1935 */       int AA = this.A;
/* 1936 */       int BB = this.B;
/* 1937 */       int CC = this.C;
/* 1938 */       int DD = this.D;
/* 1939 */       round1(d);
/* 1940 */       round2(d);
/* 1941 */       round3(d);
/* 1942 */       this.A += AA;
/* 1943 */       this.B += BB;
/* 1944 */       this.C += CC;
/* 1945 */       this.D += DD;
/*      */     }
/*      */ 
/*      */     
/*      */     protected void round1(int[] d) {
/* 1950 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[0], 3);
/* 1951 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[1], 7);
/* 1952 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[2], 11);
/* 1953 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[3], 19);
/*      */       
/* 1955 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[4], 3);
/* 1956 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[5], 7);
/* 1957 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[6], 11);
/* 1958 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[7], 19);
/*      */       
/* 1960 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[8], 3);
/* 1961 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[9], 7);
/* 1962 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[10], 11);
/* 1963 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[11], 19);
/*      */       
/* 1965 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[12], 3);
/* 1966 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[13], 7);
/* 1967 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[14], 11);
/* 1968 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[15], 19);
/*      */     }
/*      */     
/*      */     protected void round2(int[] d) {
/* 1972 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[0] + 1518500249, 3);
/* 1973 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[4] + 1518500249, 5);
/* 1974 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[8] + 1518500249, 9);
/* 1975 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[12] + 1518500249, 13);
/*      */       
/* 1977 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[1] + 1518500249, 3);
/* 1978 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[5] + 1518500249, 5);
/* 1979 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[9] + 1518500249, 9);
/* 1980 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[13] + 1518500249, 13);
/*      */       
/* 1982 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[2] + 1518500249, 3);
/* 1983 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[6] + 1518500249, 5);
/* 1984 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[10] + 1518500249, 9);
/* 1985 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[14] + 1518500249, 13);
/*      */       
/* 1987 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[3] + 1518500249, 3);
/* 1988 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[7] + 1518500249, 5);
/* 1989 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[11] + 1518500249, 9);
/* 1990 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[15] + 1518500249, 13);
/*      */     }
/*      */ 
/*      */     
/*      */     protected void round3(int[] d) {
/* 1995 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[0] + 1859775393, 3);
/* 1996 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[8] + 1859775393, 9);
/* 1997 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[4] + 1859775393, 11);
/* 1998 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[12] + 1859775393, 15);
/*      */       
/* 2000 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[2] + 1859775393, 3);
/* 2001 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[10] + 1859775393, 9);
/* 2002 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[6] + 1859775393, 11);
/* 2003 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[14] + 1859775393, 15);
/*      */       
/* 2005 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[1] + 1859775393, 3);
/* 2006 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[9] + 1859775393, 9);
/* 2007 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[5] + 1859775393, 11);
/* 2008 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[13] + 1859775393, 15);
/*      */       
/* 2010 */       this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[3] + 1859775393, 3);
/* 2011 */       this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[11] + 1859775393, 9);
/* 2012 */       this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[7] + 1859775393, 11);
/* 2013 */       this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[15] + 1859775393, 15);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class HMACMD5
/*      */   {
/*      */     protected final byte[] ipad;
/*      */     
/*      */     protected final byte[] opad;
/*      */     
/*      */     protected final MessageDigest md5;
/*      */ 
/*      */     
/*      */     HMACMD5(byte[] input) {
/* 2029 */       byte[] key = input;
/* 2030 */       this.md5 = NTLMEngineImpl.getMD5();
/*      */ 
/*      */       
/* 2033 */       this.ipad = new byte[64];
/* 2034 */       this.opad = new byte[64];
/*      */       
/* 2036 */       int keyLength = key.length;
/* 2037 */       if (keyLength > 64) {
/*      */         
/* 2039 */         this.md5.update(key);
/* 2040 */         key = this.md5.digest();
/* 2041 */         keyLength = key.length;
/*      */       } 
/* 2043 */       int i = 0;
/* 2044 */       while (i < keyLength) {
/* 2045 */         this.ipad[i] = (byte)(key[i] ^ 0x36);
/* 2046 */         this.opad[i] = (byte)(key[i] ^ 0x5C);
/* 2047 */         i++;
/*      */       } 
/* 2049 */       while (i < 64) {
/* 2050 */         this.ipad[i] = 54;
/* 2051 */         this.opad[i] = 92;
/* 2052 */         i++;
/*      */       } 
/*      */ 
/*      */       
/* 2056 */       this.md5.reset();
/* 2057 */       this.md5.update(this.ipad);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     byte[] getOutput() {
/* 2063 */       byte[] digest = this.md5.digest();
/* 2064 */       this.md5.update(this.opad);
/* 2065 */       return this.md5.digest(digest);
/*      */     }
/*      */ 
/*      */     
/*      */     void update(byte[] input) {
/* 2070 */       this.md5.update(input);
/*      */     }
/*      */ 
/*      */     
/*      */     void update(byte[] input, int offset, int length) {
/* 2075 */       this.md5.update(input, offset, length);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String generateType1Msg(String domain, String workstation) throws NTLMEngineException {
/* 2084 */     return getType1Message(workstation, domain);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String generateType3Msg(String username, String password, String domain, String workstation, String challenge) throws NTLMEngineException {
/* 2094 */     Type2Message t2m = new Type2Message(challenge);
/* 2095 */     return getType3Message(username, password, workstation, domain, t2m.getChallenge(), t2m.getFlags(), t2m.getTarget(), t2m.getTargetInfo());
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\NTLMEngineImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */