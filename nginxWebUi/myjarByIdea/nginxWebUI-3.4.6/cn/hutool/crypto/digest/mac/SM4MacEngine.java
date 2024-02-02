package cn.hutool.crypto.digest.mac;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM4Engine;

public class SM4MacEngine extends CBCBlockCipherMacEngine {
   private static final int MAC_SIZE = 128;

   public SM4MacEngine(CipherParameters params) {
      super(new SM4Engine(), 128, (CipherParameters)params);
   }
}
