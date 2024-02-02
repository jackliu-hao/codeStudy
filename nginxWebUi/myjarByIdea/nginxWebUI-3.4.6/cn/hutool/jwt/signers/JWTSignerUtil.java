package cn.hutool.jwt.signers;

import cn.hutool.core.lang.Assert;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class JWTSignerUtil {
   public static JWTSigner none() {
      return NoneJWTSigner.NONE;
   }

   public static JWTSigner hs256(byte[] key) {
      return createSigner("HS256", key);
   }

   public static JWTSigner hs384(byte[] key) {
      return createSigner("HS384", key);
   }

   public static JWTSigner hs512(byte[] key) {
      return createSigner("HS512", key);
   }

   public static JWTSigner rs256(Key key) {
      return createSigner("RS256", key);
   }

   public static JWTSigner rs384(Key key) {
      return createSigner("RS384", key);
   }

   public static JWTSigner rs512(Key key) {
      return createSigner("RS512", key);
   }

   public static JWTSigner es256(Key key) {
      return createSigner("ES256", key);
   }

   public static JWTSigner es384(Key key) {
      return createSigner("ES384", key);
   }

   public static JWTSigner es512(Key key) {
      return createSigner("ES512", key);
   }

   public static JWTSigner createSigner(String algorithmId, byte[] key) {
      Assert.notNull(key, "Signer key must be not null!");
      return (JWTSigner)(null != algorithmId && !"none".equals(algorithmId) ? new HMacJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key) : none());
   }

   public static JWTSigner createSigner(String algorithmId, KeyPair keyPair) {
      Assert.notNull(keyPair, "Signer key pair must be not null!");
      return (JWTSigner)(null != algorithmId && !"none".equals(algorithmId) ? new AsymmetricJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), keyPair) : none());
   }

   public static JWTSigner createSigner(String algorithmId, Key key) {
      Assert.notNull(key, "Signer key must be not null!");
      if (null != algorithmId && !"none".equals(algorithmId)) {
         return (JWTSigner)(!(key instanceof PrivateKey) && !(key instanceof PublicKey) ? new HMacJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key) : new AsymmetricJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key));
      } else {
         return NoneJWTSigner.NONE;
      }
   }
}
