package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.KeyUtil;
import javax.crypto.spec.IvParameterSpec;

public class ZUC extends SymmetricCrypto {
   private static final long serialVersionUID = 1L;

   public static byte[] generateKey(ZUCAlgorithm algorithm) {
      return KeyUtil.generateKey(algorithm.value).getEncoded();
   }

   public ZUC(ZUCAlgorithm algorithm, byte[] key, byte[] iv) {
      super(algorithm.value, KeyUtil.generateKey(algorithm.value, key), generateIvParam(algorithm, iv));
   }

   private static IvParameterSpec generateIvParam(ZUCAlgorithm algorithm, byte[] iv) {
      if (null == iv) {
         switch (algorithm) {
            case ZUC_128:
               iv = RandomUtil.randomBytes(16);
               break;
            case ZUC_256:
               iv = RandomUtil.randomBytes(25);
         }
      }

      return new IvParameterSpec(iv);
   }

   public static enum ZUCAlgorithm {
      ZUC_128("ZUC-128"),
      ZUC_256("ZUC-256");

      private final String value;

      private ZUCAlgorithm(String value) {
         this.value = value;
      }

      public String getValue() {
         return this.value;
      }
   }
}
