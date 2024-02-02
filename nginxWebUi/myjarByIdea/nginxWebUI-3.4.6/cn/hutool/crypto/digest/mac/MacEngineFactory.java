package cn.hutool.crypto.digest.mac;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

public class MacEngineFactory {
   public static MacEngine createEngine(String algorithm, Key key) {
      return createEngine(algorithm, key, (AlgorithmParameterSpec)null);
   }

   public static MacEngine createEngine(String algorithm, Key key, AlgorithmParameterSpec spec) {
      return (MacEngine)(algorithm.equalsIgnoreCase(HmacAlgorithm.HmacSM3.getValue()) ? SmUtil.createHmacSm3Engine(key.getEncoded()) : new DefaultHMacEngine(algorithm, key, spec));
   }
}
