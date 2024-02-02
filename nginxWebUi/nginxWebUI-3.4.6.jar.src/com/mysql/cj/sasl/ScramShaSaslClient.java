/*     */ package com.mysql.cj.sasl;
/*     */ 
/*     */ import com.mysql.cj.util.SaslPrep;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Base64;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import javax.security.sasl.SaslClient;
/*     */ import javax.security.sasl.SaslException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ScramShaSaslClient
/*     */   implements SaslClient
/*     */ {
/*     */   protected static final int MINIMUM_ITERATIONS = 4096;
/*     */   protected static final String GS2_CBIND_FLAG = "n";
/*     */   
/*     */   protected enum ScramExchangeStage
/*     */   {
/*  52 */     TERMINATED(null), SERVER_FINAL((String)TERMINATED), SERVER_FIRST_CLIENT_FINAL((String)SERVER_FINAL), CLIENT_FIRST((String)SERVER_FIRST_CLIENT_FINAL);
/*     */     
/*     */     private ScramExchangeStage next;
/*     */     
/*     */     ScramExchangeStage(ScramExchangeStage next) {
/*  57 */       this.next = next;
/*     */     }
/*     */     
/*     */     public ScramExchangeStage getNext() {
/*  61 */       return (this.next == null) ? this : this.next;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  67 */   protected static final byte[] CLIENT_KEY = "Client Key".getBytes();
/*  68 */   protected static final byte[] SERVER_KEY = "Server Key".getBytes();
/*     */   
/*     */   protected String authorizationId;
/*     */   
/*     */   protected String authenticationId;
/*     */   protected String password;
/*  74 */   protected ScramExchangeStage scramStage = ScramExchangeStage.CLIENT_FIRST;
/*     */   protected String cNonce;
/*     */   protected String gs2Header;
/*     */   protected String clientFirstMessageBare;
/*     */   protected byte[] serverSignature;
/*     */   
/*     */   public ScramShaSaslClient(String authorizationId, String authenticationId, String password) throws SaslException {
/*  81 */     this.authorizationId = StringUtils.isNullOrEmpty(authorizationId) ? "" : authorizationId;
/*  82 */     this.authenticationId = StringUtils.isNullOrEmpty(authenticationId) ? this.authorizationId : authenticationId;
/*  83 */     if (StringUtils.isNullOrEmpty(this.authenticationId)) {
/*  84 */       throw new SaslException("The authenticationId cannot be null or empty.");
/*     */     }
/*  86 */     this.password = StringUtils.isNullOrEmpty(password) ? "" : password;
/*  87 */     this.scramStage = ScramExchangeStage.CLIENT_FIRST;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract String getIanaMechanismName();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasInitialResponse() {
/* 101 */     return true; } public byte[] evaluateChallenge(byte[] challenge) throws SaslException { try {
/*     */       byte[] arrayOfByte1; String serverFirstMessage; Map<String, String> serverFirstAttributes; String sNonce; byte[] salt; int iterations; String clientFinalMessageWithoutProof; byte[] saltedPassword, clientKey, storedKey; String authMessage; byte[] clientSignature, clientProof; String clientFinalMessage;
/*     */       byte[] serverKey, arrayOfByte2;
/*     */       String serverFinalMessage;
/*     */       Map<String, String> serverFinalAttributes;
/*     */       byte[] verifier;
/* 107 */       switch (this.scramStage) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case CLIENT_FIRST:
/* 123 */           this.gs2Header = "n," + (StringUtils.isNullOrEmpty(this.authorizationId) ? "" : ("a=" + prepUserName(this.authorizationId))) + ",";
/*     */           
/* 125 */           this.cNonce = generateRandomPrintableAsciiString(32);
/* 126 */           this.clientFirstMessageBare = "n=" + prepUserName(this.authenticationId) + ",r=" + this.cNonce;
/* 127 */           clientFirstMessage = this.gs2Header + this.clientFirstMessageBare;
/*     */           
/* 129 */           return StringUtils.getBytes(clientFirstMessage, "UTF-8");
/*     */ 
/*     */         
/*     */         case SERVER_FIRST_CLIENT_FINAL:
/* 133 */           serverFirstMessage = StringUtils.toString(challenge, "UTF-8");
/* 134 */           serverFirstAttributes = parseChallenge(serverFirstMessage);
/*     */           
/* 136 */           if (!serverFirstAttributes.containsKey("r") || !serverFirstAttributes.containsKey("s") || !serverFirstAttributes.containsKey("i")) {
/* 137 */             throw new SaslException("Missing required SCRAM attribute from server first message.");
/*     */           }
/*     */           
/* 140 */           sNonce = serverFirstAttributes.get("r");
/* 141 */           if (!sNonce.startsWith(this.cNonce)) {
/* 142 */             throw new SaslException("Invalid server nonce for " + getIanaMechanismName() + " authentication.");
/*     */           }
/* 144 */           salt = Base64.getDecoder().decode(serverFirstAttributes.get("s"));
/* 145 */           iterations = Integer.parseInt(serverFirstAttributes.get("i"));
/* 146 */           if (iterations < 4096) {
/* 147 */             throw new SaslException("Announced " + getIanaMechanismName() + " iteration count is too low.");
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 156 */           clientFinalMessageWithoutProof = "c=" + Base64.getEncoder().encodeToString(StringUtils.getBytes(this.gs2Header, "UTF-8")) + ",r=" + sNonce;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 166 */           saltedPassword = hi(SaslPrep.prepare(this.password, SaslPrep.StringType.STORED), salt, iterations);
/* 167 */           clientKey = hmac(saltedPassword, CLIENT_KEY);
/* 168 */           storedKey = h(clientKey);
/* 169 */           authMessage = this.clientFirstMessageBare + "," + serverFirstMessage + "," + clientFinalMessageWithoutProof;
/* 170 */           clientSignature = hmac(storedKey, StringUtils.getBytes(authMessage, "UTF-8"));
/* 171 */           clientProof = (byte[])clientKey.clone();
/* 172 */           xorInPlace(clientProof, clientSignature);
/*     */ 
/*     */ 
/*     */           
/* 176 */           clientFinalMessage = clientFinalMessageWithoutProof + ",p=" + Base64.getEncoder().encodeToString(clientProof);
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 181 */           serverKey = hmac(saltedPassword, SERVER_KEY);
/* 182 */           this.serverSignature = hmac(serverKey, StringUtils.getBytes(authMessage, "UTF-8"));
/*     */           
/* 184 */           return StringUtils.getBytes(clientFinalMessage, "UTF-8");
/*     */         
/*     */         case SERVER_FINAL:
/* 187 */           serverFinalMessage = StringUtils.toString(challenge, "UTF-8");
/* 188 */           serverFinalAttributes = parseChallenge(serverFinalMessage);
/*     */           
/* 190 */           if (serverFinalAttributes.containsKey("e")) {
/* 191 */             throw new SaslException("Authentication failed due to server error '" + (String)serverFinalAttributes.get("e") + "'.");
/*     */           }
/*     */           
/* 194 */           if (!serverFinalAttributes.containsKey("v")) {
/* 195 */             throw new SaslException("Missing required SCRAM attribute from server final message.");
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 200 */           verifier = Base64.getDecoder().decode(serverFinalAttributes.get("v"));
/*     */           
/* 202 */           if (!MessageDigest.isEqual(this.serverSignature, verifier)) {
/* 203 */             throw new SaslException(getIanaMechanismName() + " server signature could not be verified.");
/*     */           }
/*     */           break;
/*     */         
/*     */         default:
/* 208 */           throw new SaslException("Unexpected SCRAM authentication message.");
/*     */       } 
/*     */       
/* 211 */       String clientFirstMessage = null; return (byte[])clientFirstMessage;
/* 212 */     } catch (Throwable e) {
/* 213 */       this.scramStage = ScramExchangeStage.TERMINATED;
/* 214 */       throw e;
/*     */     } finally {
/* 216 */       this.scramStage = this.scramStage.getNext();
/*     */     }  }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isComplete() {
/* 222 */     return (this.scramStage == ScramExchangeStage.TERMINATED);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
/* 227 */     throw new IllegalStateException("Integrity and/or privacy has not been negotiated.");
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
/* 232 */     throw new IllegalStateException("Integrity and/or privacy has not been negotiated.");
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getNegotiatedProperty(String propName) {
/* 237 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispose() throws SaslException {}
/*     */ 
/*     */   
/*     */   private String prepUserName(String userName) {
/* 245 */     return SaslPrep.prepare(userName, SaslPrep.StringType.QUERY).replace("=", "=2D").replace(",", "=2C");
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
/*     */   private Map<String, String> parseChallenge(String challenge) {
/* 257 */     Map<String, String> attributesMap = new HashMap<>();
/* 258 */     for (String attribute : challenge.split(",")) {
/* 259 */       String[] keyValue = attribute.split("=", 2);
/* 260 */       attributesMap.put(keyValue[0], keyValue[1]);
/*     */     } 
/* 262 */     return attributesMap;
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
/*     */   private String generateRandomPrintableAsciiString(int length) {
/* 274 */     int first = 33;
/* 275 */     int last = 126;
/* 276 */     int excl = 44;
/* 277 */     int bound = 93;
/* 278 */     Random random = new SecureRandom();
/* 279 */     char[] result = new char[length];
/*     */     
/* 281 */     for (int i = 0; i < length; ) {
/* 282 */       int randomValue = random.nextInt(93) + 33;
/* 283 */       if (randomValue != 44) {
/* 284 */         result[i++] = (char)randomValue;
/*     */       }
/*     */     } 
/* 287 */     return new String(result);
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
/*     */   abstract byte[] h(byte[] paramArrayOfbyte);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract byte[] hmac(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract byte[] hi(String paramString, byte[] paramArrayOfbyte, int paramInt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] xorInPlace(byte[] inOut, byte[] other) {
/* 338 */     for (int i = 0; i < inOut.length; i++) {
/* 339 */       inOut[i] = (byte)(inOut[i] ^ other[i]);
/*     */     }
/* 341 */     return inOut;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\sasl\ScramShaSaslClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */