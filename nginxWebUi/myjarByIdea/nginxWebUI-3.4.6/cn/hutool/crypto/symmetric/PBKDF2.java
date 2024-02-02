package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.KeyUtil;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;

public class PBKDF2 {
   private String algorithm = "PBKDF2WithHmacSHA1";
   private int keyLength = 512;
   private int iterationCount = 1000;

   public PBKDF2() {
   }

   public PBKDF2(String algorithm, int keyLength, int iterationCount) {
      this.algorithm = algorithm;
      this.keyLength = keyLength;
      this.iterationCount = iterationCount;
   }

   public byte[] encrypt(char[] password, byte[] salt) {
      PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, this.iterationCount, this.keyLength);
      SecretKey secretKey = KeyUtil.generateKey(this.algorithm, (KeySpec)pbeKeySpec);
      return secretKey.getEncoded();
   }

   public String encryptHex(char[] password, byte[] salt) {
      return HexUtil.encodeHexStr(this.encrypt(password, salt));
   }
}
