package cn.hutool.crypto.digest;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MD5 extends Digester {
   private static final long serialVersionUID = 1L;

   public static MD5 create() {
      return new MD5();
   }

   public MD5() {
      super(DigestAlgorithm.MD5);
   }

   public MD5(byte[] salt) {
      this(salt, 0, 1);
   }

   public MD5(byte[] salt, int digestCount) {
      this(salt, 0, digestCount);
   }

   public MD5(byte[] salt, int saltPosition, int digestCount) {
      this();
      this.salt = salt;
      this.saltPosition = saltPosition;
      this.digestCount = digestCount;
   }

   public String digestHex16(String data, Charset charset) {
      return DigestUtil.md5HexTo16(this.digestHex(data, charset));
   }

   public String digestHex16(String data) {
      return DigestUtil.md5HexTo16(this.digestHex(data));
   }

   public String digestHex16(InputStream data) {
      return DigestUtil.md5HexTo16(this.digestHex(data));
   }

   public String digestHex16(File data) {
      return DigestUtil.md5HexTo16(this.digestHex(data));
   }

   public String digestHex16(byte[] data) {
      return DigestUtil.md5HexTo16(this.digestHex(data));
   }
}
