package com.mysql.cj.sasl;

import com.mysql.cj.exceptions.ExceptionFactory;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.sasl.SaslException;

public class ScramSha1SaslClient extends ScramShaSaslClient {
   public static final String IANA_MECHANISM_NAME = "SCRAM-SHA-1";
   public static final String MECHANISM_NAME = "MYSQLCJ-SCRAM-SHA-1";
   private static final String SHA1_ALGORITHM = "SHA-1";
   private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
   private static final String PBKCF2_HMAC_SHA1_ALGORITHM = "PBKDF2WithHmacSHA1";
   private static final int SHA1_HASH_LENGTH = 20;

   public ScramSha1SaslClient(String authorizationId, String authenticationId, String password) throws SaslException {
      super(authorizationId, authenticationId, password);
   }

   String getIanaMechanismName() {
      return "SCRAM-SHA-1";
   }

   public String getMechanismName() {
      return "MYSQLCJ-SCRAM-SHA-1";
   }

   byte[] h(byte[] str) {
      try {
         MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
         return sha1.digest(str);
      } catch (NoSuchAlgorithmException var3) {
         throw ExceptionFactory.createException((String)"Failed computing authentication hashes", (Throwable)var3);
      }
   }

   byte[] hmac(byte[] key, byte[] str) {
      try {
         Mac hmacSha1 = Mac.getInstance("HmacSHA1");
         hmacSha1.init(new SecretKeySpec(key, "HmacSHA1"));
         return hmacSha1.doFinal(str);
      } catch (InvalidKeyException | NoSuchAlgorithmException var5) {
         throw ExceptionFactory.createException((String)"Failed computing authentication hashes", (Throwable)var5);
      }
   }

   byte[] hi(String str, byte[] salt, int iterations) {
      KeySpec spec = new PBEKeySpec(str.toCharArray(), salt, iterations, 160);

      try {
         SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
         return factory.generateSecret(spec).getEncoded();
      } catch (InvalidKeySpecException | NoSuchAlgorithmException var6) {
         throw ExceptionFactory.createException(var6.getMessage());
      }
   }
}
