package cn.hutool.crypto.digest;

import cn.hutool.crypto.digest.mac.Mac;
import cn.hutool.crypto.digest.mac.MacEngine;
import cn.hutool.crypto.digest.mac.MacEngineFactory;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class HMac extends Mac {
   private static final long serialVersionUID = 1L;

   public HMac(HmacAlgorithm algorithm) {
      this(algorithm, (Key)null);
   }

   public HMac(HmacAlgorithm algorithm, byte[] key) {
      this(algorithm.getValue(), key);
   }

   public HMac(HmacAlgorithm algorithm, Key key) {
      this(algorithm.getValue(), key);
   }

   public HMac(String algorithm, byte[] key) {
      this((String)algorithm, (Key)(new SecretKeySpec(key, algorithm)));
   }

   public HMac(String algorithm, Key key) {
      this(algorithm, key, (AlgorithmParameterSpec)null);
   }

   public HMac(String algorithm, Key key, AlgorithmParameterSpec spec) {
      this(MacEngineFactory.createEngine(algorithm, key, spec));
   }

   public HMac(MacEngine engine) {
      super(engine);
   }
}
