package com.mysql.cj.sasl;

import com.mysql.cj.util.SaslPrep;
import com.mysql.cj.util.StringUtils;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public abstract class ScramShaSaslClient implements SaslClient {
   protected static final int MINIMUM_ITERATIONS = 4096;
   protected static final String GS2_CBIND_FLAG = "n";
   protected static final byte[] CLIENT_KEY = "Client Key".getBytes();
   protected static final byte[] SERVER_KEY = "Server Key".getBytes();
   protected String authorizationId;
   protected String authenticationId;
   protected String password;
   protected ScramExchangeStage scramStage;
   protected String cNonce;
   protected String gs2Header;
   protected String clientFirstMessageBare;
   protected byte[] serverSignature;

   public ScramShaSaslClient(String authorizationId, String authenticationId, String password) throws SaslException {
      this.scramStage = ScramShaSaslClient.ScramExchangeStage.CLIENT_FIRST;
      this.authorizationId = StringUtils.isNullOrEmpty(authorizationId) ? "" : authorizationId;
      this.authenticationId = StringUtils.isNullOrEmpty(authenticationId) ? this.authorizationId : authenticationId;
      if (StringUtils.isNullOrEmpty(this.authenticationId)) {
         throw new SaslException("The authenticationId cannot be null or empty.");
      } else {
         this.password = StringUtils.isNullOrEmpty(password) ? "" : password;
         this.scramStage = ScramShaSaslClient.ScramExchangeStage.CLIENT_FIRST;
      }
   }

   abstract String getIanaMechanismName();

   public boolean hasInitialResponse() {
      return true;
   }

   public byte[] evaluateChallenge(byte[] challenge) throws SaslException {
      try {
         String clientFirstMessage;
         switch (this.scramStage) {
            case CLIENT_FIRST:
               this.gs2Header = "n," + (StringUtils.isNullOrEmpty(this.authorizationId) ? "" : "a=" + this.prepUserName(this.authorizationId)) + ",";
               this.cNonce = this.generateRandomPrintableAsciiString(32);
               this.clientFirstMessageBare = "n=" + this.prepUserName(this.authenticationId) + ",r=" + this.cNonce;
               clientFirstMessage = this.gs2Header + this.clientFirstMessageBare;
               byte[] var25 = StringUtils.getBytes(clientFirstMessage, "UTF-8");
               return var25;
            case SERVER_FIRST_CLIENT_FINAL:
               String serverFirstMessage = StringUtils.toString(challenge, "UTF-8");
               Map<String, String> serverFirstAttributes = this.parseChallenge(serverFirstMessage);
               if (serverFirstAttributes.containsKey("r") && serverFirstAttributes.containsKey("s") && serverFirstAttributes.containsKey("i")) {
                  String sNonce = (String)serverFirstAttributes.get("r");
                  if (!sNonce.startsWith(this.cNonce)) {
                     throw new SaslException("Invalid server nonce for " + this.getIanaMechanismName() + " authentication.");
                  }

                  byte[] salt = Base64.getDecoder().decode((String)serverFirstAttributes.get("s"));
                  int iterations = Integer.parseInt((String)serverFirstAttributes.get("i"));
                  if (iterations < 4096) {
                     throw new SaslException("Announced " + this.getIanaMechanismName() + " iteration count is too low.");
                  }

                  String clientFinalMessageWithoutProof = "c=" + Base64.getEncoder().encodeToString(StringUtils.getBytes(this.gs2Header, "UTF-8")) + ",r=" + sNonce;
                  byte[] saltedPassword = this.hi(SaslPrep.prepare(this.password, SaslPrep.StringType.STORED), salt, iterations);
                  byte[] clientKey = this.hmac(saltedPassword, CLIENT_KEY);
                  byte[] storedKey = this.h(clientKey);
                  String authMessage = this.clientFirstMessageBare + "," + serverFirstMessage + "," + clientFinalMessageWithoutProof;
                  byte[] clientSignature = this.hmac(storedKey, StringUtils.getBytes(authMessage, "UTF-8"));
                  byte[] clientProof = (byte[])clientKey.clone();
                  this.xorInPlace(clientProof, clientSignature);
                  String clientFinalMessage = clientFinalMessageWithoutProof + ",p=" + Base64.getEncoder().encodeToString(clientProof);
                  byte[] serverKey = this.hmac(saltedPassword, SERVER_KEY);
                  this.serverSignature = this.hmac(serverKey, StringUtils.getBytes(authMessage, "UTF-8"));
                  byte[] var26 = StringUtils.getBytes(clientFinalMessage, "UTF-8");
                  return var26;
               }

               throw new SaslException("Missing required SCRAM attribute from server first message.");
            case SERVER_FINAL:
               String serverFinalMessage = StringUtils.toString(challenge, "UTF-8");
               Map<String, String> serverFinalAttributes = this.parseChallenge(serverFinalMessage);
               if (serverFinalAttributes.containsKey("e")) {
                  throw new SaslException("Authentication failed due to server error '" + (String)serverFinalAttributes.get("e") + "'.");
               } else if (!serverFinalAttributes.containsKey("v")) {
                  throw new SaslException("Missing required SCRAM attribute from server final message.");
               } else {
                  byte[] verifier = Base64.getDecoder().decode((String)serverFinalAttributes.get("v"));
                  if (!MessageDigest.isEqual(this.serverSignature, verifier)) {
                     throw new SaslException(this.getIanaMechanismName() + " server signature could not be verified.");
                  }

                  clientFirstMessage = null;
                  return (byte[])clientFirstMessage;
               }
            default:
               throw new SaslException("Unexpected SCRAM authentication message.");
         }
      } catch (Throwable var23) {
         this.scramStage = ScramShaSaslClient.ScramExchangeStage.TERMINATED;
         throw var23;
      } finally {
         this.scramStage = this.scramStage.getNext();
      }
   }

   public boolean isComplete() {
      return this.scramStage == ScramShaSaslClient.ScramExchangeStage.TERMINATED;
   }

   public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
      throw new IllegalStateException("Integrity and/or privacy has not been negotiated.");
   }

   public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
      throw new IllegalStateException("Integrity and/or privacy has not been negotiated.");
   }

   public Object getNegotiatedProperty(String propName) {
      return null;
   }

   public void dispose() throws SaslException {
   }

   private String prepUserName(String userName) {
      return SaslPrep.prepare(userName, SaslPrep.StringType.QUERY).replace("=", "=2D").replace(",", "=2C");
   }

   private Map<String, String> parseChallenge(String challenge) {
      Map<String, String> attributesMap = new HashMap();
      String[] var3 = challenge.split(",");
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String attribute = var3[var5];
         String[] keyValue = attribute.split("=", 2);
         attributesMap.put(keyValue[0], keyValue[1]);
      }

      return attributesMap;
   }

   private String generateRandomPrintableAsciiString(int length) {
      int first = true;
      int last = true;
      int excl = true;
      int bound = true;
      Random random = new SecureRandom();
      char[] result = new char[length];
      int i = 0;

      while(i < length) {
         int randomValue = random.nextInt(93) + 33;
         if (randomValue != 44) {
            result[i++] = (char)randomValue;
         }
      }

      return new String(result);
   }

   abstract byte[] h(byte[] var1);

   abstract byte[] hmac(byte[] var1, byte[] var2);

   abstract byte[] hi(String var1, byte[] var2, int var3);

   byte[] xorInPlace(byte[] inOut, byte[] other) {
      for(int i = 0; i < inOut.length; ++i) {
         inOut[i] ^= other[i];
      }

      return inOut;
   }

   protected static enum ScramExchangeStage {
      TERMINATED((ScramExchangeStage)null),
      SERVER_FINAL(TERMINATED),
      SERVER_FIRST_CLIENT_FINAL(SERVER_FINAL),
      CLIENT_FIRST(SERVER_FIRST_CLIENT_FINAL);

      private ScramExchangeStage next;

      private ScramExchangeStage(ScramExchangeStage next) {
         this.next = next;
      }

      public ScramExchangeStage getNext() {
         return this.next == null ? this : this.next;
      }
   }
}
