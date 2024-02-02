package cn.hutool.crypto.asymmetric;

import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class AbstractAsymmetricCrypto<T extends AbstractAsymmetricCrypto<T>> extends BaseAsymmetric<T> implements AsymmetricEncryptor, AsymmetricDecryptor {
   private static final long serialVersionUID = 1L;

   public AbstractAsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
      super(algorithm, privateKey, publicKey);
   }
}
