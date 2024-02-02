package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.KeyUtil;
import javax.crypto.spec.IvParameterSpec;

public class ChaCha20 extends SymmetricCrypto {
   private static final long serialVersionUID = 1L;
   public static final String ALGORITHM_NAME = "ChaCha20";

   public ChaCha20(byte[] key, byte[] iv) {
      super("ChaCha20", KeyUtil.generateKey("ChaCha20", key), generateIvParam(iv));
   }

   private static IvParameterSpec generateIvParam(byte[] iv) {
      if (null == iv) {
         iv = RandomUtil.randomBytes(12);
      }

      return new IvParameterSpec(iv);
   }
}
