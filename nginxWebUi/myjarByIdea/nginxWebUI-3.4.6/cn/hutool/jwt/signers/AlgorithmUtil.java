package cn.hutool.jwt.signers;

import cn.hutool.core.map.BiMap;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.crypto.digest.HmacAlgorithm;
import java.util.HashMap;

public class AlgorithmUtil {
   private static final BiMap<String, String> map = new BiMap(new HashMap());

   public static String getAlgorithm(String idOrAlgorithm) {
      return (String)ObjectUtil.defaultIfNull(getAlgorithmById(idOrAlgorithm), (Object)idOrAlgorithm);
   }

   public static String getId(String idOrAlgorithm) {
      return (String)ObjectUtil.defaultIfNull(getIdByAlgorithm(idOrAlgorithm), (Object)idOrAlgorithm);
   }

   private static String getAlgorithmById(String id) {
      return (String)map.get(id.toUpperCase());
   }

   private static String getIdByAlgorithm(String algorithm) {
      return (String)map.getKey(algorithm);
   }

   static {
      map.put("HS256", HmacAlgorithm.HmacSHA256.getValue());
      map.put("HS384", HmacAlgorithm.HmacSHA384.getValue());
      map.put("HS512", HmacAlgorithm.HmacSHA512.getValue());
      map.put("RS256", SignAlgorithm.SHA256withRSA.getValue());
      map.put("RS384", SignAlgorithm.SHA384withRSA.getValue());
      map.put("RS512", SignAlgorithm.SHA512withRSA.getValue());
      map.put("ES256", SignAlgorithm.SHA256withECDSA.getValue());
      map.put("ES384", SignAlgorithm.SHA384withECDSA.getValue());
      map.put("ES512", SignAlgorithm.SHA512withECDSA.getValue());
      map.put("PS256", SignAlgorithm.SHA256withRSA_PSS.getValue());
      map.put("PS384", SignAlgorithm.SHA384withRSA_PSS.getValue());
      map.put("PS512", SignAlgorithm.SHA512withRSA_PSS.getValue());
   }
}
