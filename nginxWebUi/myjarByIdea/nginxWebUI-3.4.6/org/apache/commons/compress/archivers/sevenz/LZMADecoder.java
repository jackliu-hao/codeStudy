package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.MemoryLimitException;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.LZMAInputStream;
import org.tukaani.xz.LZMAOutputStream;

class LZMADecoder extends CoderBase {
   LZMADecoder() {
      super(LZMA2Options.class, Number.class);
   }

   InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
      if (coder.properties == null) {
         throw new IOException("Missing LZMA properties");
      } else if (coder.properties.length < 1) {
         throw new IOException("LZMA properties too short");
      } else {
         byte propsByte = coder.properties[0];
         int dictSize = this.getDictionarySize(coder);
         if (dictSize > 2147483632) {
            throw new IOException("Dictionary larger than 4GiB maximum size used in " + archiveName);
         } else {
            int memoryUsageInKb = LZMAInputStream.getMemoryUsage(dictSize, propsByte);
            if (memoryUsageInKb > maxMemoryLimitInKb) {
               throw new MemoryLimitException((long)memoryUsageInKb, maxMemoryLimitInKb);
            } else {
               return new LZMAInputStream(in, uncompressedLength, propsByte, dictSize);
            }
         }
      }
   }

   OutputStream encode(OutputStream out, Object opts) throws IOException {
      return new FlushShieldFilterOutputStream(new LZMAOutputStream(out, this.getOptions(opts), false));
   }

   byte[] getOptionsAsProperties(Object opts) throws IOException {
      LZMA2Options options = this.getOptions(opts);
      byte props = (byte)((options.getPb() * 5 + options.getLp()) * 9 + options.getLc());
      int dictSize = options.getDictSize();
      byte[] o = new byte[5];
      o[0] = props;
      ByteUtils.toLittleEndian(o, (long)dictSize, 1, 4);
      return o;
   }

   Object getOptionsFromCoder(Coder coder, InputStream in) throws IOException {
      if (coder.properties == null) {
         throw new IOException("Missing LZMA properties");
      } else if (coder.properties.length < 1) {
         throw new IOException("LZMA properties too short");
      } else {
         byte propsByte = coder.properties[0];
         int props = propsByte & 255;
         int pb = props / 45;
         props -= pb * 9 * 5;
         int lp = props / 9;
         int lc = props - lp * 9;
         LZMA2Options opts = new LZMA2Options();
         opts.setPb(pb);
         opts.setLcLp(lc, lp);
         opts.setDictSize(this.getDictionarySize(coder));
         return opts;
      }
   }

   private int getDictionarySize(Coder coder) throws IllegalArgumentException {
      return (int)ByteUtils.fromLittleEndian(coder.properties, 1, 4);
   }

   private LZMA2Options getOptions(Object opts) throws IOException {
      if (opts instanceof LZMA2Options) {
         return (LZMA2Options)opts;
      } else {
         LZMA2Options options = new LZMA2Options();
         options.setDictSize(this.numberOptionOrDefault(opts));
         return options;
      }
   }

   private int numberOptionOrDefault(Object opts) {
      return numberOptionOrDefault(opts, 8388608);
   }
}
