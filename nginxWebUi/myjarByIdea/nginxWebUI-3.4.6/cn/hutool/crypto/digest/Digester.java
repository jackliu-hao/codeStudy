package cn.hutool.crypto.digest;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.SecureUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

public class Digester implements Serializable {
   private static final long serialVersionUID = 1L;
   private MessageDigest digest;
   protected byte[] salt;
   protected int saltPosition;
   protected int digestCount;

   public Digester(DigestAlgorithm algorithm) {
      this(algorithm.getValue());
   }

   public Digester(String algorithm) {
      this((String)algorithm, (Provider)null);
   }

   public Digester(DigestAlgorithm algorithm, Provider provider) {
      this.init(algorithm.getValue(), provider);
   }

   public Digester(String algorithm, Provider provider) {
      this.init(algorithm, provider);
   }

   public Digester init(String algorithm, Provider provider) {
      if (null == provider) {
         this.digest = SecureUtil.createMessageDigest(algorithm);
      } else {
         try {
            this.digest = MessageDigest.getInstance(algorithm, provider);
         } catch (NoSuchAlgorithmException var4) {
            throw new CryptoException(var4);
         }
      }

      return this;
   }

   public Digester setSalt(byte[] salt) {
      this.salt = salt;
      return this;
   }

   public Digester setSaltPosition(int saltPosition) {
      this.saltPosition = saltPosition;
      return this;
   }

   public Digester setDigestCount(int digestCount) {
      this.digestCount = digestCount;
      return this;
   }

   public Digester reset() {
      this.digest.reset();
      return this;
   }

   public byte[] digest(String data, String charsetName) {
      return this.digest(data, CharsetUtil.charset(charsetName));
   }

   public byte[] digest(String data, Charset charset) {
      return this.digest(StrUtil.bytes(data, charset));
   }

   public byte[] digest(String data) {
      return this.digest(data, CharsetUtil.CHARSET_UTF_8);
   }

   public String digestHex(String data, String charsetName) {
      return this.digestHex(data, CharsetUtil.charset(charsetName));
   }

   public String digestHex(String data, Charset charset) {
      return HexUtil.encodeHexStr(this.digest(data, charset));
   }

   public String digestHex(String data) {
      return this.digestHex(data, "UTF-8");
   }

   public byte[] digest(File file) throws CryptoException {
      InputStream in = null;

      byte[] var3;
      try {
         in = FileUtil.getInputStream(file);
         var3 = this.digest((InputStream)in);
      } finally {
         IoUtil.close(in);
      }

      return var3;
   }

   public String digestHex(File file) {
      return HexUtil.encodeHexStr(this.digest(file));
   }

   public byte[] digest(byte[] data) {
      byte[] result;
      if (this.saltPosition <= 0) {
         result = this.doDigest(this.salt, data);
      } else if (this.saltPosition >= data.length) {
         result = this.doDigest(data, this.salt);
      } else if (ArrayUtil.isNotEmpty((byte[])this.salt)) {
         this.digest.update(data, 0, this.saltPosition);
         this.digest.update(this.salt);
         this.digest.update(data, this.saltPosition, data.length - this.saltPosition);
         result = this.digest.digest();
      } else {
         result = this.doDigest(data);
      }

      return this.resetAndRepeatDigest(result);
   }

   public String digestHex(byte[] data) {
      return HexUtil.encodeHexStr(this.digest(data));
   }

   public byte[] digest(InputStream data) {
      return this.digest(data, 8192);
   }

   public String digestHex(InputStream data) {
      return HexUtil.encodeHexStr(this.digest(data));
   }

   public byte[] digest(InputStream data, int bufferLength) throws IORuntimeException {
      if (bufferLength < 1) {
         bufferLength = 8192;
      }

      byte[] result;
      try {
         if (ArrayUtil.isEmpty((byte[])this.salt)) {
            result = this.digestWithoutSalt(data, bufferLength);
         } else {
            result = this.digestWithSalt(data, bufferLength);
         }
      } catch (IOException var5) {
         throw new IORuntimeException(var5);
      }

      return this.resetAndRepeatDigest(result);
   }

   public String digestHex(InputStream data, int bufferLength) {
      return HexUtil.encodeHexStr(this.digest(data, bufferLength));
   }

   public MessageDigest getDigest() {
      return this.digest;
   }

   public int getDigestLength() {
      return this.digest.getDigestLength();
   }

   private byte[] digestWithoutSalt(InputStream data, int bufferLength) throws IOException {
      byte[] buffer = new byte[bufferLength];

      int read;
      while((read = data.read(buffer, 0, bufferLength)) > -1) {
         this.digest.update(buffer, 0, read);
      }

      return this.digest.digest();
   }

   private byte[] digestWithSalt(InputStream data, int bufferLength) throws IOException {
      if (this.saltPosition <= 0) {
         this.digest.update(this.salt);
      }

      byte[] buffer = new byte[bufferLength];
      int total = 0;

      while(true) {
         int read;
         while((read = data.read(buffer, 0, bufferLength)) > -1) {
            total += read;
            if (this.saltPosition > 0 && total >= this.saltPosition) {
               if (total != this.saltPosition) {
                  this.digest.update(buffer, 0, total - this.saltPosition);
               }

               this.digest.update(this.salt);
               this.digest.update(buffer, total - this.saltPosition, read);
            } else {
               this.digest.update(buffer, 0, read);
            }
         }

         if (total < this.saltPosition) {
            this.digest.update(this.salt);
         }

         return this.digest.digest();
      }
   }

   private byte[] doDigest(byte[]... datas) {
      byte[][] var2 = datas;
      int var3 = datas.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte[] data = var2[var4];
         if (null != data) {
            this.digest.update(data);
         }
      }

      return this.digest.digest();
   }

   private byte[] resetAndRepeatDigest(byte[] digestData) {
      int digestCount = Math.max(1, this.digestCount);
      this.reset();

      for(int i = 0; i < digestCount - 1; ++i) {
         digestData = this.doDigest(digestData);
         this.reset();
      }

      return digestData;
   }
}
