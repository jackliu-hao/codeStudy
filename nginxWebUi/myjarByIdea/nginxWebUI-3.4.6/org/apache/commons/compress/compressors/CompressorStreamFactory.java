package org.apache.commons.compress.compressors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import org.apache.commons.compress.compressors.brotli.BrotliUtils;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMAUtils;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZUtils;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
import org.apache.commons.compress.compressors.zstandard.ZstdUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.ServiceLoaderIterator;
import org.apache.commons.compress.utils.Sets;

public class CompressorStreamFactory implements CompressorStreamProvider {
   private static final CompressorStreamFactory SINGLETON = new CompressorStreamFactory();
   public static final String BROTLI = "br";
   public static final String BZIP2 = "bzip2";
   public static final String GZIP = "gz";
   public static final String PACK200 = "pack200";
   public static final String XZ = "xz";
   public static final String LZMA = "lzma";
   public static final String SNAPPY_FRAMED = "snappy-framed";
   public static final String SNAPPY_RAW = "snappy-raw";
   public static final String Z = "z";
   public static final String DEFLATE = "deflate";
   public static final String DEFLATE64 = "deflate64";
   public static final String LZ4_BLOCK = "lz4-block";
   public static final String LZ4_FRAMED = "lz4-framed";
   public static final String ZSTANDARD = "zstd";
   private static final String YOU_NEED_BROTLI_DEC = youNeed("Google Brotli Dec", "https://github.com/google/brotli/");
   private static final String YOU_NEED_XZ_JAVA = youNeed("XZ for Java", "https://tukaani.org/xz/java.html");
   private static final String YOU_NEED_ZSTD_JNI = youNeed("Zstd JNI", "https://github.com/luben/zstd-jni");
   private final Boolean decompressUntilEOF;
   private SortedMap<String, CompressorStreamProvider> compressorInputStreamProviders;
   private SortedMap<String, CompressorStreamProvider> compressorOutputStreamProviders;
   private volatile boolean decompressConcatenated;
   private final int memoryLimitInKb;

   private static String youNeed(String name, String url) {
      return " In addition to Apache Commons Compress you need the " + name + " library - see " + url;
   }

   public static SortedMap<String, CompressorStreamProvider> findAvailableCompressorInputStreamProviders() {
      return (SortedMap)AccessController.doPrivileged(() -> {
         TreeMap<String, CompressorStreamProvider> map = new TreeMap();
         putAll(SINGLETON.getInputStreamCompressorNames(), SINGLETON, map);
         Iterator var1 = findCompressorStreamProviders().iterator();

         while(var1.hasNext()) {
            CompressorStreamProvider provider = (CompressorStreamProvider)var1.next();
            putAll(provider.getInputStreamCompressorNames(), provider, map);
         }

         return map;
      });
   }

   public static SortedMap<String, CompressorStreamProvider> findAvailableCompressorOutputStreamProviders() {
      return (SortedMap)AccessController.doPrivileged(() -> {
         TreeMap<String, CompressorStreamProvider> map = new TreeMap();
         putAll(SINGLETON.getOutputStreamCompressorNames(), SINGLETON, map);
         Iterator var1 = findCompressorStreamProviders().iterator();

         while(var1.hasNext()) {
            CompressorStreamProvider provider = (CompressorStreamProvider)var1.next();
            putAll(provider.getOutputStreamCompressorNames(), provider, map);
         }

         return map;
      });
   }

   private static ArrayList<CompressorStreamProvider> findCompressorStreamProviders() {
      return Lists.newArrayList(serviceLoaderIterator());
   }

   public static String getBrotli() {
      return "br";
   }

   public static String getBzip2() {
      return "bzip2";
   }

   public static String getDeflate() {
      return "deflate";
   }

   public static String getDeflate64() {
      return "deflate64";
   }

   public static String getGzip() {
      return "gz";
   }

   public static String getLzma() {
      return "lzma";
   }

   public static String getPack200() {
      return "pack200";
   }

   public static CompressorStreamFactory getSingleton() {
      return SINGLETON;
   }

   public static String getSnappyFramed() {
      return "snappy-framed";
   }

   public static String getSnappyRaw() {
      return "snappy-raw";
   }

   public static String getXz() {
      return "xz";
   }

   public static String getZ() {
      return "z";
   }

   public static String getLZ4Framed() {
      return "lz4-framed";
   }

   public static String getLZ4Block() {
      return "lz4-block";
   }

   public static String getZstandard() {
      return "zstd";
   }

   static void putAll(Set<String> names, CompressorStreamProvider provider, TreeMap<String, CompressorStreamProvider> map) {
      Iterator var3 = names.iterator();

      while(var3.hasNext()) {
         String name = (String)var3.next();
         map.put(toKey(name), provider);
      }

   }

   private static Iterator<CompressorStreamProvider> serviceLoaderIterator() {
      return new ServiceLoaderIterator(CompressorStreamProvider.class);
   }

   private static String toKey(String name) {
      return name.toUpperCase(Locale.ROOT);
   }

   public CompressorStreamFactory() {
      this.decompressUntilEOF = null;
      this.memoryLimitInKb = -1;
   }

   public CompressorStreamFactory(boolean decompressUntilEOF, int memoryLimitInKb) {
      this.decompressUntilEOF = decompressUntilEOF;
      this.decompressConcatenated = decompressUntilEOF;
      this.memoryLimitInKb = memoryLimitInKb;
   }

   public CompressorStreamFactory(boolean decompressUntilEOF) {
      this(decompressUntilEOF, -1);
   }

   public static String detect(InputStream inputStream) throws CompressorException {
      if (inputStream == null) {
         throw new IllegalArgumentException("Stream must not be null.");
      } else if (!inputStream.markSupported()) {
         throw new IllegalArgumentException("Mark is not supported.");
      } else {
         byte[] signature = new byte[12];
         inputStream.mark(signature.length);
         int signatureLength = true;

         int signatureLength;
         try {
            signatureLength = IOUtils.readFully(inputStream, signature);
            inputStream.reset();
         } catch (IOException var4) {
            throw new CompressorException("IOException while reading signature.", var4);
         }

         if (BZip2CompressorInputStream.matches(signature, signatureLength)) {
            return "bzip2";
         } else if (GzipCompressorInputStream.matches(signature, signatureLength)) {
            return "gz";
         } else if (Pack200CompressorInputStream.matches(signature, signatureLength)) {
            return "pack200";
         } else if (FramedSnappyCompressorInputStream.matches(signature, signatureLength)) {
            return "snappy-framed";
         } else if (ZCompressorInputStream.matches(signature, signatureLength)) {
            return "z";
         } else if (DeflateCompressorInputStream.matches(signature, signatureLength)) {
            return "deflate";
         } else if (XZUtils.matches(signature, signatureLength)) {
            return "xz";
         } else if (LZMAUtils.matches(signature, signatureLength)) {
            return "lzma";
         } else if (FramedLZ4CompressorInputStream.matches(signature, signatureLength)) {
            return "lz4-framed";
         } else if (ZstdUtils.matches(signature, signatureLength)) {
            return "zstd";
         } else {
            throw new CompressorException("No Compressor found for the stream signature.");
         }
      }
   }

   public CompressorInputStream createCompressorInputStream(InputStream in) throws CompressorException {
      return this.createCompressorInputStream(detect(in), in);
   }

   public CompressorInputStream createCompressorInputStream(String name, InputStream in) throws CompressorException {
      return this.createCompressorInputStream(name, in, this.decompressConcatenated);
   }

   public CompressorInputStream createCompressorInputStream(String name, InputStream in, boolean actualDecompressConcatenated) throws CompressorException {
      if (name != null && in != null) {
         try {
            if ("gz".equalsIgnoreCase(name)) {
               return new GzipCompressorInputStream(in, actualDecompressConcatenated);
            }

            if ("bzip2".equalsIgnoreCase(name)) {
               return new BZip2CompressorInputStream(in, actualDecompressConcatenated);
            }

            if ("br".equalsIgnoreCase(name)) {
               if (!BrotliUtils.isBrotliCompressionAvailable()) {
                  throw new CompressorException("Brotli compression is not available." + YOU_NEED_BROTLI_DEC);
               }

               return new BrotliCompressorInputStream(in);
            }

            if ("xz".equalsIgnoreCase(name)) {
               if (!XZUtils.isXZCompressionAvailable()) {
                  throw new CompressorException("XZ compression is not available." + YOU_NEED_XZ_JAVA);
               }

               return new XZCompressorInputStream(in, actualDecompressConcatenated, this.memoryLimitInKb);
            }

            if ("zstd".equalsIgnoreCase(name)) {
               if (!ZstdUtils.isZstdCompressionAvailable()) {
                  throw new CompressorException("Zstandard compression is not available." + YOU_NEED_ZSTD_JNI);
               }

               return new ZstdCompressorInputStream(in);
            }

            if ("lzma".equalsIgnoreCase(name)) {
               if (!LZMAUtils.isLZMACompressionAvailable()) {
                  throw new CompressorException("LZMA compression is not available" + YOU_NEED_XZ_JAVA);
               }

               return new LZMACompressorInputStream(in, this.memoryLimitInKb);
            }

            if ("pack200".equalsIgnoreCase(name)) {
               return new Pack200CompressorInputStream(in);
            }

            if ("snappy-raw".equalsIgnoreCase(name)) {
               return new SnappyCompressorInputStream(in);
            }

            if ("snappy-framed".equalsIgnoreCase(name)) {
               return new FramedSnappyCompressorInputStream(in);
            }

            if ("z".equalsIgnoreCase(name)) {
               return new ZCompressorInputStream(in, this.memoryLimitInKb);
            }

            if ("deflate".equalsIgnoreCase(name)) {
               return new DeflateCompressorInputStream(in);
            }

            if ("deflate64".equalsIgnoreCase(name)) {
               return new Deflate64CompressorInputStream(in);
            }

            if ("lz4-block".equalsIgnoreCase(name)) {
               return new BlockLZ4CompressorInputStream(in);
            }

            if ("lz4-framed".equalsIgnoreCase(name)) {
               return new FramedLZ4CompressorInputStream(in, actualDecompressConcatenated);
            }
         } catch (IOException var5) {
            throw new CompressorException("Could not create CompressorInputStream.", var5);
         }

         CompressorStreamProvider compressorStreamProvider = (CompressorStreamProvider)this.getCompressorInputStreamProviders().get(toKey(name));
         if (compressorStreamProvider != null) {
            return compressorStreamProvider.createCompressorInputStream(name, in, actualDecompressConcatenated);
         } else {
            throw new CompressorException("Compressor: " + name + " not found.");
         }
      } else {
         throw new IllegalArgumentException("Compressor name and stream must not be null.");
      }
   }

   public CompressorOutputStream createCompressorOutputStream(String name, OutputStream out) throws CompressorException {
      if (name != null && out != null) {
         try {
            if ("gz".equalsIgnoreCase(name)) {
               return new GzipCompressorOutputStream(out);
            }

            if ("bzip2".equalsIgnoreCase(name)) {
               return new BZip2CompressorOutputStream(out);
            }

            if ("xz".equalsIgnoreCase(name)) {
               return new XZCompressorOutputStream(out);
            }

            if ("pack200".equalsIgnoreCase(name)) {
               return new Pack200CompressorOutputStream(out);
            }

            if ("lzma".equalsIgnoreCase(name)) {
               return new LZMACompressorOutputStream(out);
            }

            if ("deflate".equalsIgnoreCase(name)) {
               return new DeflateCompressorOutputStream(out);
            }

            if ("snappy-framed".equalsIgnoreCase(name)) {
               return new FramedSnappyCompressorOutputStream(out);
            }

            if ("lz4-block".equalsIgnoreCase(name)) {
               return new BlockLZ4CompressorOutputStream(out);
            }

            if ("lz4-framed".equalsIgnoreCase(name)) {
               return new FramedLZ4CompressorOutputStream(out);
            }

            if ("zstd".equalsIgnoreCase(name)) {
               return new ZstdCompressorOutputStream(out);
            }
         } catch (IOException var4) {
            throw new CompressorException("Could not create CompressorOutputStream", var4);
         }

         CompressorStreamProvider compressorStreamProvider = (CompressorStreamProvider)this.getCompressorOutputStreamProviders().get(toKey(name));
         if (compressorStreamProvider != null) {
            return compressorStreamProvider.createCompressorOutputStream(name, out);
         } else {
            throw new CompressorException("Compressor: " + name + " not found.");
         }
      } else {
         throw new IllegalArgumentException("Compressor name and stream must not be null.");
      }
   }

   public SortedMap<String, CompressorStreamProvider> getCompressorInputStreamProviders() {
      if (this.compressorInputStreamProviders == null) {
         this.compressorInputStreamProviders = Collections.unmodifiableSortedMap(findAvailableCompressorInputStreamProviders());
      }

      return this.compressorInputStreamProviders;
   }

   public SortedMap<String, CompressorStreamProvider> getCompressorOutputStreamProviders() {
      if (this.compressorOutputStreamProviders == null) {
         this.compressorOutputStreamProviders = Collections.unmodifiableSortedMap(findAvailableCompressorOutputStreamProviders());
      }

      return this.compressorOutputStreamProviders;
   }

   boolean getDecompressConcatenated() {
      return this.decompressConcatenated;
   }

   public Boolean getDecompressUntilEOF() {
      return this.decompressUntilEOF;
   }

   public Set<String> getInputStreamCompressorNames() {
      return Sets.newHashSet("gz", "br", "bzip2", "xz", "lzma", "pack200", "deflate", "snappy-raw", "snappy-framed", "z", "lz4-block", "lz4-framed", "zstd", "deflate64");
   }

   public Set<String> getOutputStreamCompressorNames() {
      return Sets.newHashSet("gz", "bzip2", "xz", "lzma", "pack200", "deflate", "snappy-framed", "lz4-block", "lz4-framed", "zstd");
   }

   /** @deprecated */
   @Deprecated
   public void setDecompressConcatenated(boolean decompressConcatenated) {
      if (this.decompressUntilEOF != null) {
         throw new IllegalStateException("Cannot override the setting defined by the constructor");
      } else {
         this.decompressConcatenated = decompressConcatenated;
      }
   }
}
