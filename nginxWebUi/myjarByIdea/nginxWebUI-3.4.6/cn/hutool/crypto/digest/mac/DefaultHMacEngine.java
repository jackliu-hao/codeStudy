package cn.hutool.crypto.digest.mac;

import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.SecureUtil;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DefaultHMacEngine implements MacEngine {
   private javax.crypto.Mac mac;

   public DefaultHMacEngine(String algorithm, byte[] key) {
      this(algorithm, (Key)(null == key ? null : new SecretKeySpec(key, algorithm)));
   }

   public DefaultHMacEngine(String algorithm, Key key) {
      this(algorithm, key, (AlgorithmParameterSpec)null);
   }

   public DefaultHMacEngine(String algorithm, Key key, AlgorithmParameterSpec spec) {
      this.init(algorithm, key, spec);
   }

   public DefaultHMacEngine init(String algorithm, byte[] key) {
      return this.init(algorithm, (Key)(null == key ? null : new SecretKeySpec(key, algorithm)));
   }

   public DefaultHMacEngine init(String algorithm, Key key) {
      return this.init(algorithm, key, (AlgorithmParameterSpec)null);
   }

   public DefaultHMacEngine init(String algorithm, Key key, AlgorithmParameterSpec spec) {
      try {
         this.mac = SecureUtil.createMac(algorithm);
         if (null == key) {
            key = SecureUtil.generateKey(algorithm);
         }

         if (null != spec) {
            this.mac.init((Key)key, spec);
         } else {
            this.mac.init((Key)key);
         }

         return this;
      } catch (Exception var5) {
         throw new CryptoException(var5);
      }
   }

   public javax.crypto.Mac getMac() {
      return this.mac;
   }

   public void update(byte[] in) {
      this.mac.update(in);
   }

   public void update(byte[] in, int inOff, int len) {
      this.mac.update(in, inOff, len);
   }

   public byte[] doFinal() {
      return this.mac.doFinal();
   }

   public void reset() {
      this.mac.reset();
   }

   public int getMacLength() {
      return this.mac.getMacLength();
   }

   public String getAlgorithm() {
      return this.mac.getAlgorithm();
   }
}
