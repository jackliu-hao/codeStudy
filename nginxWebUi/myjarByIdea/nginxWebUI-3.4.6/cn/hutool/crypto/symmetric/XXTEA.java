package cn.hutool.crypto.symmetric;

import cn.hutool.core.io.IoUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class XXTEA implements SymmetricEncryptor, SymmetricDecryptor, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int DELTA = -1640531527;
   private final byte[] key;

   public XXTEA(byte[] key) {
      this.key = key;
   }

   public byte[] encrypt(byte[] data) {
      return data.length == 0 ? data : toByteArray(encrypt(toIntArray(data, true), toIntArray(fixKey(this.key), false)), false);
   }

   public void encrypt(InputStream data, OutputStream out, boolean isClose) {
      IoUtil.write(out, isClose, this.encrypt(IoUtil.readBytes(data)));
   }

   public byte[] decrypt(byte[] data) {
      return data.length == 0 ? data : toByteArray(decrypt(toIntArray(data, false), toIntArray(fixKey(this.key), false)), true);
   }

   public void decrypt(InputStream data, OutputStream out, boolean isClose) {
      IoUtil.write(out, isClose, this.decrypt(IoUtil.readBytes(data)));
   }

   private static int[] encrypt(int[] v, int[] k) {
      int n = v.length - 1;
      if (n < 1) {
         return v;
      } else {
         int q = 6 + 52 / (n + 1);
         int z = v[n];

         int p;
         int y;
         int e;
         for(int sum = 0; q-- > 0; z = v[n] += mx(sum, y, z, p, e, k)) {
            sum += -1640531527;
            e = sum >>> 2 & 3;

            for(p = 0; p < n; ++p) {
               y = v[p + 1];
               z = v[p] += mx(sum, y, z, p, e, k);
            }

            y = v[0];
         }

         return v;
      }
   }

   private static int[] decrypt(int[] v, int[] k) {
      int n = v.length - 1;
      if (n < 1) {
         return v;
      } else {
         int q = 6 + 52 / (n + 1);
         int y = v[0];

         for(int sum = q * -1640531527; sum != 0; sum -= -1640531527) {
            int e = sum >>> 2 & 3;

            int p;
            int z;
            for(p = n; p > 0; --p) {
               z = v[p - 1];
               y = v[p] -= mx(sum, y, z, p, e, k);
            }

            z = v[n];
            y = v[0] -= mx(sum, y, z, p, e, k);
         }

         return v;
      }
   }

   private static int mx(int sum, int y, int z, int p, int e, int[] k) {
      return (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
   }

   private static byte[] fixKey(byte[] key) {
      if (key.length == 16) {
         return key;
      } else {
         byte[] fixedkey = new byte[16];
         System.arraycopy(key, 0, fixedkey, 0, Math.min(key.length, 16));
         return fixedkey;
      }
   }

   private static int[] toIntArray(byte[] data, boolean includeLength) {
      int n = (data.length & 3) == 0 ? data.length >>> 2 : (data.length >>> 2) + 1;
      int[] result;
      if (includeLength) {
         result = new int[n + 1];
         result[n] = data.length;
      } else {
         result = new int[n];
      }

      n = data.length;

      for(int i = 0; i < n; ++i) {
         result[i >>> 2] |= (255 & data[i]) << ((i & 3) << 3);
      }

      return result;
   }

   private static byte[] toByteArray(int[] data, boolean includeLength) {
      int n = data.length << 2;
      if (includeLength) {
         int m = data[data.length - 1];
         n -= 4;
         if (m < n - 3 || m > n) {
            return null;
         }

         n = m;
      }

      byte[] result = new byte[n];

      for(int i = 0; i < n; ++i) {
         result[i] = (byte)(data[i >>> 2] >>> ((i & 3) << 3));
      }

      return result;
   }
}
