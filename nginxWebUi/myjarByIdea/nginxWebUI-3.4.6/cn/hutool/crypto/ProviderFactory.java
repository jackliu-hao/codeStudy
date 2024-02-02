package cn.hutool.crypto;

import java.security.Provider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderFactory {
   public static Provider createBouncyCastleProvider() {
      return new BouncyCastleProvider();
   }
}
