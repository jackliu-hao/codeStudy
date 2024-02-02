package cn.hutool.core.lang.hash;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.StrUtil;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KetamaHash implements Hash64<String>, Hash32<String> {
   public long hash64(String key) {
      byte[] bKey = md5(key);
      return (long)(bKey[3] & 255) << 24 | (long)(bKey[2] & 255) << 16 | (long)(bKey[1] & 255) << 8 | (long)(bKey[0] & 255);
   }

   public int hash32(String key) {
      return (int)(this.hash64(key) & 4294967295L);
   }

   public Number hash(String key) {
      return this.hash64(key);
   }

   private static byte[] md5(String key) {
      MessageDigest md5;
      try {
         md5 = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException var3) {
         throw new UtilException("MD5 algorithm not suooport!", var3);
      }

      return md5.digest(StrUtil.utf8Bytes(key));
   }
}
