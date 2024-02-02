package cn.hutool.crypto.asymmetric;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.KeyUtil;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BaseAsymmetric<T extends BaseAsymmetric<T>> implements Serializable {
   private static final long serialVersionUID = 1L;
   protected String algorithm;
   protected PublicKey publicKey;
   protected PrivateKey privateKey;
   protected final Lock lock = new ReentrantLock();

   public BaseAsymmetric(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
      this.init(algorithm, privateKey, publicKey);
   }

   protected T init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
      this.algorithm = algorithm;
      if (null == privateKey && null == publicKey) {
         this.initKeys();
      } else {
         if (null != privateKey) {
            this.privateKey = privateKey;
         }

         if (null != publicKey) {
            this.publicKey = publicKey;
         }
      }

      return this;
   }

   public T initKeys() {
      KeyPair keyPair = KeyUtil.generateKeyPair(this.algorithm);
      this.publicKey = keyPair.getPublic();
      this.privateKey = keyPair.getPrivate();
      return this;
   }

   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   public String getPublicKeyBase64() {
      PublicKey publicKey = this.getPublicKey();
      return null == publicKey ? null : Base64.encode(publicKey.getEncoded());
   }

   public T setPublicKey(PublicKey publicKey) {
      this.publicKey = publicKey;
      return this;
   }

   public PrivateKey getPrivateKey() {
      return this.privateKey;
   }

   public String getPrivateKeyBase64() {
      PrivateKey privateKey = this.getPrivateKey();
      return null == privateKey ? null : Base64.encode(privateKey.getEncoded());
   }

   public T setPrivateKey(PrivateKey privateKey) {
      this.privateKey = privateKey;
      return this;
   }

   public T setKey(Key key) {
      Assert.notNull(key, "key must be not null !");
      if (key instanceof PublicKey) {
         return this.setPublicKey((PublicKey)key);
      } else if (key instanceof PrivateKey) {
         return this.setPrivateKey((PrivateKey)key);
      } else {
         throw new CryptoException("Unsupported key type: {}", new Object[]{key.getClass()});
      }
   }

   protected Key getKeyByType(KeyType type) {
      switch (type) {
         case PrivateKey:
            if (null == this.privateKey) {
               throw new NullPointerException("Private key must not null when use it !");
            }

            return this.privateKey;
         case PublicKey:
            if (null == this.publicKey) {
               throw new NullPointerException("Public key must not null when use it !");
            }

            return this.publicKey;
         default:
            throw new CryptoException("Unsupported key type: " + type);
      }
   }
}
