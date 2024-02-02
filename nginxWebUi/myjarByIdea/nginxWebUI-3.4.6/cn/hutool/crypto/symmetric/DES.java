package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class DES extends SymmetricCrypto {
   private static final long serialVersionUID = 1L;

   public DES() {
      super(SymmetricAlgorithm.DES);
   }

   public DES(byte[] key) {
      super(SymmetricAlgorithm.DES, key);
   }

   public DES(Mode mode, Padding padding) {
      this(mode.name(), padding.name());
   }

   public DES(Mode mode, Padding padding, byte[] key) {
      this((Mode)mode, (Padding)padding, (byte[])key, (byte[])null);
   }

   public DES(Mode mode, Padding padding, byte[] key, byte[] iv) {
      this(mode.name(), padding.name(), key, iv);
   }

   public DES(Mode mode, Padding padding, SecretKey key) {
      this((Mode)mode, (Padding)padding, (SecretKey)key, (IvParameterSpec)null);
   }

   public DES(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
      this(mode.name(), padding.name(), key, iv);
   }

   public DES(String mode, String padding) {
      this(mode, padding, (byte[])null);
   }

   public DES(String mode, String padding, byte[] key) {
      this((String)mode, (String)padding, (SecretKey)SecureUtil.generateKey("DES", key), (IvParameterSpec)null);
   }

   public DES(String mode, String padding, byte[] key, byte[] iv) {
      this(mode, padding, SecureUtil.generateKey("DES", key), null == iv ? null : new IvParameterSpec(iv));
   }

   public DES(String mode, String padding, SecretKey key) {
      this((String)mode, (String)padding, (SecretKey)key, (IvParameterSpec)null);
   }

   public DES(String mode, String padding, SecretKey key, IvParameterSpec iv) {
      super(StrUtil.format("DES/{}/{}", new Object[]{mode, padding}), key, iv);
   }
}
