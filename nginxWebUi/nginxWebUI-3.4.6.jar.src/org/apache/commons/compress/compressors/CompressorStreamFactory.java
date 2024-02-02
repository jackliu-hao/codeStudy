/*     */ package org.apache.commons.compress.compressors;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.brotli.BrotliUtils;
/*     */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.lzma.LZMAUtils;
/*     */ import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.xz.XZUtils;
/*     */ import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
/*     */ import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.zstandard.ZstdUtils;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ import org.apache.commons.compress.utils.Lists;
/*     */ import org.apache.commons.compress.utils.ServiceLoaderIterator;
/*     */ import org.apache.commons.compress.utils.Sets;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompressorStreamFactory
/*     */   implements CompressorStreamProvider
/*     */ {
/* 100 */   private static final CompressorStreamFactory SINGLETON = new CompressorStreamFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String BROTLI = "br";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String BZIP2 = "bzip2";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String GZIP = "gz";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String PACK200 = "pack200";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String XZ = "xz";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LZMA = "lzma";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SNAPPY_FRAMED = "snappy-framed";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SNAPPY_RAW = "snappy-raw";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String Z = "z";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEFLATE = "deflate";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DEFLATE64 = "deflate64";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LZ4_BLOCK = "lz4-block";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String LZ4_FRAMED = "lz4-framed";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ZSTANDARD = "zstd";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 212 */   private static final String YOU_NEED_BROTLI_DEC = youNeed("Google Brotli Dec", "https://github.com/google/brotli/");
/* 213 */   private static final String YOU_NEED_XZ_JAVA = youNeed("XZ for Java", "https://tukaani.org/xz/java.html");
/* 214 */   private static final String YOU_NEED_ZSTD_JNI = youNeed("Zstd JNI", "https://github.com/luben/zstd-jni");
/*     */   
/*     */   private static String youNeed(String name, String url) {
/* 217 */     return " In addition to Apache Commons Compress you need the " + name + " library - see " + url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Boolean decompressUntilEOF;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SortedMap<String, CompressorStreamProvider> compressorInputStreamProviders;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SortedMap<String, CompressorStreamProvider> compressorOutputStreamProviders;
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean decompressConcatenated;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int memoryLimitInKb;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SortedMap<String, CompressorStreamProvider> findAvailableCompressorInputStreamProviders() {
/* 248 */     return AccessController.<SortedMap<String, CompressorStreamProvider>>doPrivileged(() -> {
/*     */           TreeMap<String, CompressorStreamProvider> map = new TreeMap<>();
/*     */           putAll(SINGLETON.getInputStreamCompressorNames(), SINGLETON, map);
/*     */           for (CompressorStreamProvider provider : findCompressorStreamProviders()) {
/*     */             putAll(provider.getInputStreamCompressorNames(), provider, map);
/*     */           }
/*     */           return map;
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SortedMap<String, CompressorStreamProvider> findAvailableCompressorOutputStreamProviders() {
/* 286 */     return AccessController.<SortedMap<String, CompressorStreamProvider>>doPrivileged(() -> {
/*     */           TreeMap<String, CompressorStreamProvider> map = new TreeMap<>();
/*     */           putAll(SINGLETON.getOutputStreamCompressorNames(), SINGLETON, map);
/*     */           for (CompressorStreamProvider provider : findCompressorStreamProviders())
/*     */             putAll(provider.getOutputStreamCompressorNames(), provider, map); 
/*     */           return map;
/*     */         });
/*     */   }
/*     */   
/*     */   private static ArrayList<CompressorStreamProvider> findCompressorStreamProviders() {
/* 296 */     return Lists.newArrayList(serviceLoaderIterator());
/*     */   }
/*     */   
/*     */   public static String getBrotli() {
/* 300 */     return "br";
/*     */   }
/*     */   
/*     */   public static String getBzip2() {
/* 304 */     return "bzip2";
/*     */   }
/*     */   
/*     */   public static String getDeflate() {
/* 308 */     return "deflate";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDeflate64() {
/* 316 */     return "deflate64";
/*     */   }
/*     */   
/*     */   public static String getGzip() {
/* 320 */     return "gz";
/*     */   }
/*     */   
/*     */   public static String getLzma() {
/* 324 */     return "lzma";
/*     */   }
/*     */   
/*     */   public static String getPack200() {
/* 328 */     return "pack200";
/*     */   }
/*     */   
/*     */   public static CompressorStreamFactory getSingleton() {
/* 332 */     return SINGLETON;
/*     */   }
/*     */   
/*     */   public static String getSnappyFramed() {
/* 336 */     return "snappy-framed";
/*     */   }
/*     */   
/*     */   public static String getSnappyRaw() {
/* 340 */     return "snappy-raw";
/*     */   }
/*     */   
/*     */   public static String getXz() {
/* 344 */     return "xz";
/*     */   }
/*     */   
/*     */   public static String getZ() {
/* 348 */     return "z";
/*     */   }
/*     */   
/*     */   public static String getLZ4Framed() {
/* 352 */     return "lz4-framed";
/*     */   }
/*     */   
/*     */   public static String getLZ4Block() {
/* 356 */     return "lz4-block";
/*     */   }
/*     */   
/*     */   public static String getZstandard() {
/* 360 */     return "zstd";
/*     */   }
/*     */ 
/*     */   
/*     */   static void putAll(Set<String> names, CompressorStreamProvider provider, TreeMap<String, CompressorStreamProvider> map) {
/* 365 */     for (String name : names) {
/* 366 */       map.put(toKey(name), provider);
/*     */     }
/*     */   }
/*     */   
/*     */   private static Iterator<CompressorStreamProvider> serviceLoaderIterator() {
/* 371 */     return (Iterator<CompressorStreamProvider>)new ServiceLoaderIterator(CompressorStreamProvider.class);
/*     */   }
/*     */   
/*     */   private static String toKey(String name) {
/* 375 */     return name.toUpperCase(Locale.ROOT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressorStreamFactory() {
/* 406 */     this.decompressUntilEOF = null;
/* 407 */     this.memoryLimitInKb = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressorStreamFactory(boolean decompressUntilEOF, int memoryLimitInKb) {
/* 427 */     this.decompressUntilEOF = Boolean.valueOf(decompressUntilEOF);
/*     */ 
/*     */     
/* 430 */     this.decompressConcatenated = decompressUntilEOF;
/* 431 */     this.memoryLimitInKb = memoryLimitInKb;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressorStreamFactory(boolean decompressUntilEOF) {
/* 445 */     this(decompressUntilEOF, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String detect(InputStream inputStream) throws CompressorException {
/* 460 */     if (inputStream == null) {
/* 461 */       throw new IllegalArgumentException("Stream must not be null.");
/*     */     }
/*     */     
/* 464 */     if (!inputStream.markSupported()) {
/* 465 */       throw new IllegalArgumentException("Mark is not supported.");
/*     */     }
/*     */     
/* 468 */     byte[] signature = new byte[12];
/* 469 */     inputStream.mark(signature.length);
/* 470 */     int signatureLength = -1;
/*     */     try {
/* 472 */       signatureLength = IOUtils.readFully(inputStream, signature);
/* 473 */       inputStream.reset();
/* 474 */     } catch (IOException e) {
/* 475 */       throw new CompressorException("IOException while reading signature.", e);
/*     */     } 
/*     */     
/* 478 */     if (BZip2CompressorInputStream.matches(signature, signatureLength)) {
/* 479 */       return "bzip2";
/*     */     }
/*     */     
/* 482 */     if (GzipCompressorInputStream.matches(signature, signatureLength)) {
/* 483 */       return "gz";
/*     */     }
/*     */     
/* 486 */     if (Pack200CompressorInputStream.matches(signature, signatureLength)) {
/* 487 */       return "pack200";
/*     */     }
/*     */     
/* 490 */     if (FramedSnappyCompressorInputStream.matches(signature, signatureLength)) {
/* 491 */       return "snappy-framed";
/*     */     }
/*     */     
/* 494 */     if (ZCompressorInputStream.matches(signature, signatureLength)) {
/* 495 */       return "z";
/*     */     }
/*     */     
/* 498 */     if (DeflateCompressorInputStream.matches(signature, signatureLength)) {
/* 499 */       return "deflate";
/*     */     }
/*     */     
/* 502 */     if (XZUtils.matches(signature, signatureLength)) {
/* 503 */       return "xz";
/*     */     }
/*     */     
/* 506 */     if (LZMAUtils.matches(signature, signatureLength)) {
/* 507 */       return "lzma";
/*     */     }
/*     */     
/* 510 */     if (FramedLZ4CompressorInputStream.matches(signature, signatureLength)) {
/* 511 */       return "lz4-framed";
/*     */     }
/*     */     
/* 514 */     if (ZstdUtils.matches(signature, signatureLength)) {
/* 515 */       return "zstd";
/*     */     }
/*     */     
/* 518 */     throw new CompressorException("No Compressor found for the stream signature.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressorInputStream createCompressorInputStream(InputStream in) throws CompressorException {
/* 535 */     return createCompressorInputStream(detect(in), in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressorInputStream createCompressorInputStream(String name, InputStream in) throws CompressorException {
/* 561 */     return createCompressorInputStream(name, in, this.decompressConcatenated);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressorInputStream createCompressorInputStream(String name, InputStream in, boolean actualDecompressConcatenated) throws CompressorException {
/* 567 */     if (name == null || in == null) {
/* 568 */       throw new IllegalArgumentException("Compressor name and stream must not be null.");
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 573 */       if ("gz".equalsIgnoreCase(name)) {
/* 574 */         return (CompressorInputStream)new GzipCompressorInputStream(in, actualDecompressConcatenated);
/*     */       }
/*     */       
/* 577 */       if ("bzip2".equalsIgnoreCase(name)) {
/* 578 */         return (CompressorInputStream)new BZip2CompressorInputStream(in, actualDecompressConcatenated);
/*     */       }
/*     */       
/* 581 */       if ("br".equalsIgnoreCase(name)) {
/* 582 */         if (!BrotliUtils.isBrotliCompressionAvailable()) {
/* 583 */           throw new CompressorException("Brotli compression is not available." + YOU_NEED_BROTLI_DEC);
/*     */         }
/* 585 */         return (CompressorInputStream)new BrotliCompressorInputStream(in);
/*     */       } 
/*     */       
/* 588 */       if ("xz".equalsIgnoreCase(name)) {
/* 589 */         if (!XZUtils.isXZCompressionAvailable()) {
/* 590 */           throw new CompressorException("XZ compression is not available." + YOU_NEED_XZ_JAVA);
/*     */         }
/* 592 */         return (CompressorInputStream)new XZCompressorInputStream(in, actualDecompressConcatenated, this.memoryLimitInKb);
/*     */       } 
/*     */       
/* 595 */       if ("zstd".equalsIgnoreCase(name)) {
/* 596 */         if (!ZstdUtils.isZstdCompressionAvailable()) {
/* 597 */           throw new CompressorException("Zstandard compression is not available." + YOU_NEED_ZSTD_JNI);
/*     */         }
/* 599 */         return (CompressorInputStream)new ZstdCompressorInputStream(in);
/*     */       } 
/*     */       
/* 602 */       if ("lzma".equalsIgnoreCase(name)) {
/* 603 */         if (!LZMAUtils.isLZMACompressionAvailable()) {
/* 604 */           throw new CompressorException("LZMA compression is not available" + YOU_NEED_XZ_JAVA);
/*     */         }
/* 606 */         return (CompressorInputStream)new LZMACompressorInputStream(in, this.memoryLimitInKb);
/*     */       } 
/*     */       
/* 609 */       if ("pack200".equalsIgnoreCase(name)) {
/* 610 */         return (CompressorInputStream)new Pack200CompressorInputStream(in);
/*     */       }
/*     */       
/* 613 */       if ("snappy-raw".equalsIgnoreCase(name)) {
/* 614 */         return (CompressorInputStream)new SnappyCompressorInputStream(in);
/*     */       }
/*     */       
/* 617 */       if ("snappy-framed".equalsIgnoreCase(name)) {
/* 618 */         return (CompressorInputStream)new FramedSnappyCompressorInputStream(in);
/*     */       }
/*     */       
/* 621 */       if ("z".equalsIgnoreCase(name)) {
/* 622 */         return (CompressorInputStream)new ZCompressorInputStream(in, this.memoryLimitInKb);
/*     */       }
/*     */       
/* 625 */       if ("deflate".equalsIgnoreCase(name)) {
/* 626 */         return (CompressorInputStream)new DeflateCompressorInputStream(in);
/*     */       }
/*     */       
/* 629 */       if ("deflate64".equalsIgnoreCase(name)) {
/* 630 */         return (CompressorInputStream)new Deflate64CompressorInputStream(in);
/*     */       }
/*     */       
/* 633 */       if ("lz4-block".equalsIgnoreCase(name)) {
/* 634 */         return (CompressorInputStream)new BlockLZ4CompressorInputStream(in);
/*     */       }
/*     */       
/* 637 */       if ("lz4-framed".equalsIgnoreCase(name)) {
/* 638 */         return (CompressorInputStream)new FramedLZ4CompressorInputStream(in, actualDecompressConcatenated);
/*     */       }
/*     */     }
/* 641 */     catch (IOException e) {
/* 642 */       throw new CompressorException("Could not create CompressorInputStream.", e);
/*     */     } 
/* 644 */     CompressorStreamProvider compressorStreamProvider = getCompressorInputStreamProviders().get(toKey(name));
/* 645 */     if (compressorStreamProvider != null) {
/* 646 */       return compressorStreamProvider.createCompressorInputStream(name, in, actualDecompressConcatenated);
/*     */     }
/*     */     
/* 649 */     throw new CompressorException("Compressor: " + name + " not found.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompressorOutputStream createCompressorOutputStream(String name, OutputStream out) throws CompressorException {
/* 672 */     if (name == null || out == null) {
/* 673 */       throw new IllegalArgumentException("Compressor name and stream must not be null.");
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 678 */       if ("gz".equalsIgnoreCase(name)) {
/* 679 */         return (CompressorOutputStream)new GzipCompressorOutputStream(out);
/*     */       }
/*     */       
/* 682 */       if ("bzip2".equalsIgnoreCase(name)) {
/* 683 */         return (CompressorOutputStream)new BZip2CompressorOutputStream(out);
/*     */       }
/*     */       
/* 686 */       if ("xz".equalsIgnoreCase(name)) {
/* 687 */         return (CompressorOutputStream)new XZCompressorOutputStream(out);
/*     */       }
/*     */       
/* 690 */       if ("pack200".equalsIgnoreCase(name)) {
/* 691 */         return (CompressorOutputStream)new Pack200CompressorOutputStream(out);
/*     */       }
/*     */       
/* 694 */       if ("lzma".equalsIgnoreCase(name)) {
/* 695 */         return (CompressorOutputStream)new LZMACompressorOutputStream(out);
/*     */       }
/*     */       
/* 698 */       if ("deflate".equalsIgnoreCase(name)) {
/* 699 */         return (CompressorOutputStream)new DeflateCompressorOutputStream(out);
/*     */       }
/*     */       
/* 702 */       if ("snappy-framed".equalsIgnoreCase(name)) {
/* 703 */         return (CompressorOutputStream)new FramedSnappyCompressorOutputStream(out);
/*     */       }
/*     */       
/* 706 */       if ("lz4-block".equalsIgnoreCase(name)) {
/* 707 */         return (CompressorOutputStream)new BlockLZ4CompressorOutputStream(out);
/*     */       }
/*     */       
/* 710 */       if ("lz4-framed".equalsIgnoreCase(name)) {
/* 711 */         return (CompressorOutputStream)new FramedLZ4CompressorOutputStream(out);
/*     */       }
/*     */       
/* 714 */       if ("zstd".equalsIgnoreCase(name)) {
/* 715 */         return (CompressorOutputStream)new ZstdCompressorOutputStream(out);
/*     */       }
/* 717 */     } catch (IOException e) {
/* 718 */       throw new CompressorException("Could not create CompressorOutputStream", e);
/*     */     } 
/* 720 */     CompressorStreamProvider compressorStreamProvider = getCompressorOutputStreamProviders().get(toKey(name));
/* 721 */     if (compressorStreamProvider != null) {
/* 722 */       return compressorStreamProvider.createCompressorOutputStream(name, out);
/*     */     }
/* 724 */     throw new CompressorException("Compressor: " + name + " not found.");
/*     */   }
/*     */   
/*     */   public SortedMap<String, CompressorStreamProvider> getCompressorInputStreamProviders() {
/* 728 */     if (this.compressorInputStreamProviders == null) {
/* 729 */       this
/* 730 */         .compressorInputStreamProviders = Collections.unmodifiableSortedMap(findAvailableCompressorInputStreamProviders());
/*     */     }
/* 732 */     return this.compressorInputStreamProviders;
/*     */   }
/*     */   
/*     */   public SortedMap<String, CompressorStreamProvider> getCompressorOutputStreamProviders() {
/* 736 */     if (this.compressorOutputStreamProviders == null) {
/* 737 */       this
/* 738 */         .compressorOutputStreamProviders = Collections.unmodifiableSortedMap(findAvailableCompressorOutputStreamProviders());
/*     */     }
/* 740 */     return this.compressorOutputStreamProviders;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean getDecompressConcatenated() {
/* 745 */     return this.decompressConcatenated;
/*     */   }
/*     */   
/*     */   public Boolean getDecompressUntilEOF() {
/* 749 */     return this.decompressUntilEOF;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getInputStreamCompressorNames() {
/* 754 */     return Sets.newHashSet((Object[])new String[] { "gz", "br", "bzip2", "xz", "lzma", "pack200", "deflate", "snappy-raw", "snappy-framed", "z", "lz4-block", "lz4-framed", "zstd", "deflate64" });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getOutputStreamCompressorNames() {
/* 760 */     return Sets.newHashSet((Object[])new String[] { "gz", "bzip2", "xz", "lzma", "pack200", "deflate", "snappy-framed", "lz4-block", "lz4-framed", "zstd" });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setDecompressConcatenated(boolean decompressConcatenated) {
/* 784 */     if (this.decompressUntilEOF != null) {
/* 785 */       throw new IllegalStateException("Cannot override the setting defined by the constructor");
/*     */     }
/* 787 */     this.decompressConcatenated = decompressConcatenated;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\CompressorStreamFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */