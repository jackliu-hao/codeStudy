package cn.hutool.crypto;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import java.util.Map;

public class SignUtil {
   public static Sign sign(SignAlgorithm algorithm) {
      return new Sign(algorithm);
   }

   public static Sign sign(SignAlgorithm algorithm, String privateKeyBase64, String publicKeyBase64) {
      return new Sign(algorithm, privateKeyBase64, publicKeyBase64);
   }

   public static Sign sign(SignAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
      return new Sign(algorithm, privateKey, publicKey);
   }

   public static String signParams(SymmetricCrypto crypto, Map<?, ?> params, String... otherParams) {
      return signParams(crypto, params, "", "", true, otherParams);
   }

   public static String signParams(SymmetricCrypto crypto, Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
      return crypto.encryptHex(MapUtil.sortJoin(params, separator, keyValueSeparator, isIgnoreNull, otherParams));
   }

   public static String signParamsMd5(Map<?, ?> params, String... otherParams) {
      return signParams(DigestAlgorithm.MD5, params, otherParams);
   }

   public static String signParamsSha1(Map<?, ?> params, String... otherParams) {
      return signParams(DigestAlgorithm.SHA1, params, otherParams);
   }

   public static String signParamsSha256(Map<?, ?> params, String... otherParams) {
      return signParams(DigestAlgorithm.SHA256, params, otherParams);
   }

   public static String signParams(DigestAlgorithm digestAlgorithm, Map<?, ?> params, String... otherParams) {
      return signParams(digestAlgorithm, params, "", "", true, otherParams);
   }

   public static String signParams(DigestAlgorithm digestAlgorithm, Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
      return (new Digester(digestAlgorithm)).digestHex(MapUtil.sortJoin(params, separator, keyValueSeparator, isIgnoreNull, otherParams));
   }
}
