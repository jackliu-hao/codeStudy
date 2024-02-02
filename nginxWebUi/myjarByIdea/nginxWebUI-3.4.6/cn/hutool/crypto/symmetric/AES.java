package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AES extends SymmetricCrypto {
   private static final long serialVersionUID = 1L;

   public AES() {
      super(SymmetricAlgorithm.AES);
   }

   public AES(byte[] key) {
      super(SymmetricAlgorithm.AES, key);
   }

   public AES(SecretKey key) {
      super(SymmetricAlgorithm.AES, key);
   }

   public AES(Mode mode, Padding padding) {
      this(mode.name(), padding.name());
   }

   public AES(Mode mode, Padding padding, byte[] key) {
      this((Mode)mode, (Padding)padding, (byte[])key, (byte[])null);
   }

   public AES(Mode mode, Padding padding, byte[] key, byte[] iv) {
      this(mode.name(), padding.name(), key, iv);
   }

   public AES(Mode mode, Padding padding, SecretKey key) {
      this((Mode)mode, (Padding)padding, (SecretKey)key, (AlgorithmParameterSpec)((IvParameterSpec)null));
   }

   public AES(Mode mode, Padding padding, SecretKey key, byte[] iv) {
      this((Mode)mode, (Padding)padding, (SecretKey)key, (AlgorithmParameterSpec)(ArrayUtil.isEmpty((byte[])iv) ? null : new IvParameterSpec(iv)));
   }

   public AES(Mode mode, Padding padding, SecretKey key, AlgorithmParameterSpec paramsSpec) {
      this(mode.name(), padding.name(), key, paramsSpec);
   }

   public AES(String mode, String padding) {
      this(mode, padding, (byte[])null);
   }

   public AES(String mode, String padding, byte[] key) {
      this((String)mode, (String)padding, (byte[])key, (byte[])null);
   }

   public AES(String mode, String padding, byte[] key, byte[] iv) {
      this((String)mode, (String)padding, (SecretKey)KeyUtil.generateKey(SymmetricAlgorithm.AES.getValue(), key), (AlgorithmParameterSpec)(ArrayUtil.isEmpty((byte[])iv) ? null : new IvParameterSpec(iv)));
   }

   public AES(String mode, String padding, SecretKey key) {
      this((String)mode, (String)padding, (SecretKey)key, (AlgorithmParameterSpec)null);
   }

   public AES(String mode, String padding, SecretKey key, AlgorithmParameterSpec paramsSpec) {
      super(StrUtil.format("AES/{}/{}", new Object[]{mode, padding}), key, paramsSpec);
   }
}
