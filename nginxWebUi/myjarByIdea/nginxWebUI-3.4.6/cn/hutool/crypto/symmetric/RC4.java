package cn.hutool.crypto.symmetric;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.SecureUtil;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RC4 implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final int SBOX_LENGTH = 256;
   private static final int KEY_MIN_LENGTH = 5;
   private int[] sbox;
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

   public RC4(String key) throws CryptoException {
      this.setKey(key);
   }

   public byte[] encrypt(String message, Charset charset) throws CryptoException {
      return this.crypt(StrUtil.bytes(message, charset));
   }

   public byte[] encrypt(String message) throws CryptoException {
      return this.encrypt(message, CharsetUtil.CHARSET_UTF_8);
   }

   public String encryptHex(byte[] data) {
      return HexUtil.encodeHexStr(this.crypt(data));
   }

   public String encryptBase64(byte[] data) {
      return Base64.encode(this.crypt(data));
   }

   public String encryptHex(String data, Charset charset) {
      return HexUtil.encodeHexStr(this.encrypt(data, charset));
   }

   public String encryptHex(String data) {
      return HexUtil.encodeHexStr(this.encrypt(data));
   }

   public String encryptBase64(String data, Charset charset) {
      return Base64.encode(this.encrypt(data, charset));
   }

   public String encryptBase64(String data) {
      return Base64.encode(this.encrypt(data));
   }

   public String decrypt(byte[] message, Charset charset) throws CryptoException {
      return StrUtil.str(this.crypt(message), charset);
   }

   public String decrypt(byte[] message) throws CryptoException {
      return this.decrypt(message, CharsetUtil.CHARSET_UTF_8);
   }

   public String decrypt(String message) {
      return this.decrypt(SecureUtil.decode(message));
   }

   public String decrypt(String message, Charset charset) {
      return StrUtil.str((Object)this.decrypt(message), (Charset)charset);
   }

   public byte[] crypt(byte[] msg) {
      ReentrantReadWriteLock.ReadLock readLock = this.lock.readLock();
      readLock.lock();

      byte[] code;
      try {
         int[] sbox = (int[])this.sbox.clone();
         code = new byte[msg.length];
         int i = 0;
         int j = 0;

         for(int n = 0; n < msg.length; ++n) {
            i = (i + 1) % 256;
            j = (j + sbox[i]) % 256;
            this.swap(i, j, sbox);
            int rand = sbox[(sbox[i] + sbox[j]) % 256];
            code[n] = (byte)(rand ^ msg[n]);
         }
      } finally {
         readLock.unlock();
      }

      return code;
   }

   public void setKey(String key) throws CryptoException {
      int length = key.length();
      if (length >= 5 && length < 256) {
         ReentrantReadWriteLock.WriteLock writeLock = this.lock.writeLock();
         writeLock.lock();

         try {
            this.sbox = this.initSBox(StrUtil.utf8Bytes(key));
         } finally {
            writeLock.unlock();
         }

      } else {
         throw new CryptoException("Key length has to be between {} and {}", new Object[]{5, 255});
      }
   }

   private int[] initSBox(byte[] key) {
      int[] sbox = new int[256];
      int j = 0;

      int i;
      for(i = 0; i < 256; sbox[i] = i++) {
      }

      for(i = 0; i < 256; ++i) {
         j = (j + sbox[i] + key[i % key.length] & 255) % 256;
         this.swap(i, j, sbox);
      }

      return sbox;
   }

   private void swap(int i, int j, int[] sbox) {
      int temp = sbox[i];
      sbox[i] = sbox[j];
      sbox[j] = temp;
   }
}
