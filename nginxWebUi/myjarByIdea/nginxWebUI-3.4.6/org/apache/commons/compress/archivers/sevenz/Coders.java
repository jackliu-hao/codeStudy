package org.apache.commons.compress.archivers.sevenz;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;
import org.tukaani.xz.ARMOptions;
import org.tukaani.xz.ARMThumbOptions;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.FinishableWrapperOutputStream;
import org.tukaani.xz.IA64Options;
import org.tukaani.xz.PowerPCOptions;
import org.tukaani.xz.SPARCOptions;
import org.tukaani.xz.X86Options;

class Coders {
   private static final Map<SevenZMethod, CoderBase> CODER_MAP = new HashMap<SevenZMethod, CoderBase>() {
      private static final long serialVersionUID = 1664829131806520867L;

      {
         this.put(SevenZMethod.COPY, new CopyDecoder());
         this.put(SevenZMethod.LZMA, new LZMADecoder());
         this.put(SevenZMethod.LZMA2, new LZMA2Decoder());
         this.put(SevenZMethod.DEFLATE, new DeflateDecoder());
         this.put(SevenZMethod.DEFLATE64, new Deflate64Decoder());
         this.put(SevenZMethod.BZIP2, new BZIP2Decoder());
         this.put(SevenZMethod.AES256SHA256, new AES256SHA256Decoder());
         this.put(SevenZMethod.BCJ_X86_FILTER, new BCJDecoder(new X86Options()));
         this.put(SevenZMethod.BCJ_PPC_FILTER, new BCJDecoder(new PowerPCOptions()));
         this.put(SevenZMethod.BCJ_IA64_FILTER, new BCJDecoder(new IA64Options()));
         this.put(SevenZMethod.BCJ_ARM_FILTER, new BCJDecoder(new ARMOptions()));
         this.put(SevenZMethod.BCJ_ARM_THUMB_FILTER, new BCJDecoder(new ARMThumbOptions()));
         this.put(SevenZMethod.BCJ_SPARC_FILTER, new BCJDecoder(new SPARCOptions()));
         this.put(SevenZMethod.DELTA_FILTER, new DeltaDecoder());
      }
   };

   static CoderBase findByMethod(SevenZMethod method) {
      return (CoderBase)CODER_MAP.get(method);
   }

   static InputStream addDecoder(String archiveName, InputStream is, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
      CoderBase cb = findByMethod(SevenZMethod.byId(coder.decompressionMethodId));
      if (cb == null) {
         throw new IOException("Unsupported compression method " + Arrays.toString(coder.decompressionMethodId) + " used in " + archiveName);
      } else {
         return cb.decode(archiveName, is, uncompressedLength, coder, password, maxMemoryLimitInKb);
      }
   }

   static OutputStream addEncoder(OutputStream out, SevenZMethod method, Object options) throws IOException {
      CoderBase cb = findByMethod(method);
      if (cb == null) {
         throw new IOException("Unsupported compression method " + method);
      } else {
         return cb.encode(out, options);
      }
   }

   static class BZIP2Decoder extends CoderBase {
      BZIP2Decoder() {
         super(Number.class);
      }

      InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
         return new BZip2CompressorInputStream(in);
      }

      OutputStream encode(OutputStream out, Object options) throws IOException {
         int blockSize = numberOptionOrDefault(options, 9);
         return new BZip2CompressorOutputStream(out, blockSize);
      }
   }

   static class Deflate64Decoder extends CoderBase {
      Deflate64Decoder() {
         super(Number.class);
      }

      InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
         return new Deflate64CompressorInputStream(in);
      }
   }

   static class DeflateDecoder extends CoderBase {
      private static final byte[] ONE_ZERO_BYTE = new byte[1];

      DeflateDecoder() {
         super(Number.class);
      }

      InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
         Inflater inflater = new Inflater(true);
         InflaterInputStream inflaterInputStream = new InflaterInputStream(new SequenceInputStream(in, new ByteArrayInputStream(ONE_ZERO_BYTE)), inflater);
         return new DeflateDecoderInputStream(inflaterInputStream, inflater);
      }

      OutputStream encode(OutputStream out, Object options) {
         int level = numberOptionOrDefault(options, 9);
         Deflater deflater = new Deflater(level, true);
         DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(out, deflater);
         return new DeflateDecoderOutputStream(deflaterOutputStream, deflater);
      }

      static class DeflateDecoderOutputStream extends OutputStream {
         final DeflaterOutputStream deflaterOutputStream;
         Deflater deflater;

         public DeflateDecoderOutputStream(DeflaterOutputStream deflaterOutputStream, Deflater deflater) {
            this.deflaterOutputStream = deflaterOutputStream;
            this.deflater = deflater;
         }

         public void write(int b) throws IOException {
            this.deflaterOutputStream.write(b);
         }

         public void write(byte[] b) throws IOException {
            this.deflaterOutputStream.write(b);
         }

         public void write(byte[] b, int off, int len) throws IOException {
            this.deflaterOutputStream.write(b, off, len);
         }

         public void close() throws IOException {
            try {
               this.deflaterOutputStream.close();
            } finally {
               this.deflater.end();
            }

         }
      }

      static class DeflateDecoderInputStream extends InputStream {
         final InflaterInputStream inflaterInputStream;
         Inflater inflater;

         public DeflateDecoderInputStream(InflaterInputStream inflaterInputStream, Inflater inflater) {
            this.inflaterInputStream = inflaterInputStream;
            this.inflater = inflater;
         }

         public int read() throws IOException {
            return this.inflaterInputStream.read();
         }

         public int read(byte[] b, int off, int len) throws IOException {
            return this.inflaterInputStream.read(b, off, len);
         }

         public int read(byte[] b) throws IOException {
            return this.inflaterInputStream.read(b);
         }

         public void close() throws IOException {
            try {
               this.inflaterInputStream.close();
            } finally {
               this.inflater.end();
            }

         }
      }
   }

   static class BCJDecoder extends CoderBase {
      private final FilterOptions opts;

      BCJDecoder(FilterOptions opts) {
         super();
         this.opts = opts;
      }

      InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
         try {
            return this.opts.getInputStream(in);
         } catch (AssertionError var9) {
            throw new IOException("BCJ filter used in " + archiveName + " needs XZ for Java > 1.4 - see https://commons.apache.org/proper/commons-compress/limitations.html#7Z", var9);
         }
      }

      OutputStream encode(OutputStream out, Object options) {
         return new FlushShieldFilterOutputStream(this.opts.getOutputStream(new FinishableWrapperOutputStream(out)));
      }
   }

   static class CopyDecoder extends CoderBase {
      CopyDecoder() {
         super();
      }

      InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
         return in;
      }

      OutputStream encode(OutputStream out, Object options) {
         return out;
      }
   }
}
