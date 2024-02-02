package cn.hutool.crypto.digest.mac;

import cn.hutool.crypto.CryptoException;
import java.io.IOException;
import java.io.InputStream;

public interface MacEngine {
   default void update(byte[] in) {
      this.update(in, 0, in.length);
   }

   void update(byte[] var1, int var2, int var3);

   byte[] doFinal();

   void reset();

   default byte[] digest(InputStream data, int bufferLength) {
      if (bufferLength < 1) {
         bufferLength = 8192;
      }

      byte[] buffer = new byte[bufferLength];

      try {
         for(int read = data.read(buffer, 0, bufferLength); read > -1; read = data.read(buffer, 0, bufferLength)) {
            this.update(buffer, 0, read);
         }

         byte[] result = this.doFinal();
         return result;
      } catch (IOException var9) {
         throw new CryptoException(var9);
      } finally {
         this.reset();
      }
   }

   int getMacLength();

   String getAlgorithm();
}
