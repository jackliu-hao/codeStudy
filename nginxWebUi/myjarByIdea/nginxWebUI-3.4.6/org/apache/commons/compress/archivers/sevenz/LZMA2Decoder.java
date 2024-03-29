package org.apache.commons.compress.archivers.sevenz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.compress.MemoryLimitException;
import org.tukaani.xz.FinishableOutputStream;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.LZMA2InputStream;
import org.tukaani.xz.LZMA2Options;

class LZMA2Decoder extends CoderBase {
   LZMA2Decoder() {
      super(LZMA2Options.class, Number.class);
   }

   InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
      try {
         int dictionarySize = this.getDictionarySize(coder);
         int memoryUsageInKb = LZMA2InputStream.getMemoryUsage(dictionarySize);
         if (memoryUsageInKb > maxMemoryLimitInKb) {
            throw new MemoryLimitException((long)memoryUsageInKb, maxMemoryLimitInKb);
         } else {
            return new LZMA2InputStream(in, dictionarySize);
         }
      } catch (IllegalArgumentException var10) {
         throw new IOException(var10.getMessage());
      }
   }

   OutputStream encode(OutputStream out, Object opts) throws IOException {
      LZMA2Options options = this.getOptions(opts);
      FinishableOutputStream wrapped = new FinishableWrapperOutputStream(out);
      return options.getOutputStream(wrapped);
   }

   byte[] getOptionsAsProperties(Object opts) {
      int dictSize = this.getDictSize(opts);
      int lead = Integer.numberOfLeadingZeros(dictSize);
      int secondBit = (dictSize >>> 30 - lead) - 2;
      return new byte[]{(byte)((19 - lead) * 2 + secondBit)};
   }

   Object getOptionsFromCoder(Coder coder, InputStream in) throws IOException {
      return this.getDictionarySize(coder);
   }

   private int getDictSize(Object opts) {
      return opts instanceof LZMA2Options ? ((LZMA2Options)opts).getDictSize() : this.numberOptionOrDefault(opts);
   }

   private int getDictionarySize(Coder coder) throws IOException {
      if (coder.properties == null) {
         throw new IOException("Missing LZMA2 properties");
      } else if (coder.properties.length < 1) {
         throw new IOException("LZMA2 properties too short");
      } else {
         int dictionarySizeBits = 255 & coder.properties[0];
         if ((dictionarySizeBits & -64) != 0) {
            throw new IOException("Unsupported LZMA2 property bits");
         } else if (dictionarySizeBits > 40) {
            throw new IOException("Dictionary larger than 4GiB maximum size");
         } else {
            return dictionarySizeBits == 40 ? -1 : (2 | dictionarySizeBits & 1) << dictionarySizeBits / 2 + 11;
         }
      }
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
