/*     */ package org.apache.commons.compress.archivers;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.Closeable;
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
/*     */ import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZFile;
/*     */ import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
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
/*     */ public class ArchiveStreamFactory
/*     */   implements ArchiveStreamProvider
/*     */ {
/*     */   private static final int TAR_HEADER_SIZE = 512;
/*     */   private static final int DUMP_SIGNATURE_SIZE = 32;
/*     */   private static final int SIGNATURE_SIZE = 12;
/* 101 */   public static final ArchiveStreamFactory DEFAULT = new ArchiveStreamFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String AR = "ar";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ARJ = "arj";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String CPIO = "cpio";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DUMP = "dump";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String JAR = "jar";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String TAR = "tar";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ZIP = "zip";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SEVEN_Z = "7z";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String encoding;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile String entryEncoding;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SortedMap<String, ArchiveStreamProvider> archiveInputStreamProviders;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private SortedMap<String, ArchiveStreamProvider> archiveOutputStreamProviders;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ArrayList<ArchiveStreamProvider> findArchiveStreamProviders() {
/* 168 */     return Lists.newArrayList(serviceLoaderIterator());
/*     */   }
/*     */ 
/*     */   
/*     */   static void putAll(Set<String> names, ArchiveStreamProvider provider, TreeMap<String, ArchiveStreamProvider> map) {
/* 173 */     for (String name : names) {
/* 174 */       map.put(toKey(name), provider);
/*     */     }
/*     */   }
/*     */   
/*     */   private static Iterator<ArchiveStreamProvider> serviceLoaderIterator() {
/* 179 */     return (Iterator<ArchiveStreamProvider>)new ServiceLoaderIterator(ArchiveStreamProvider.class);
/*     */   }
/*     */   
/*     */   private static String toKey(String name) {
/* 183 */     return name.toUpperCase(Locale.ROOT);
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
/*     */   public static SortedMap<String, ArchiveStreamProvider> findAvailableArchiveInputStreamProviders() {
/* 214 */     return AccessController.<SortedMap<String, ArchiveStreamProvider>>doPrivileged(() -> {
/*     */           TreeMap<String, ArchiveStreamProvider> map = new TreeMap<>();
/*     */           putAll(DEFAULT.getInputStreamArchiveNames(), DEFAULT, map);
/*     */           for (ArchiveStreamProvider provider : findArchiveStreamProviders()) {
/*     */             putAll(provider.getInputStreamArchiveNames(), provider, map);
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
/*     */   public static SortedMap<String, ArchiveStreamProvider> findAvailableArchiveOutputStreamProviders() {
/* 252 */     return AccessController.<SortedMap<String, ArchiveStreamProvider>>doPrivileged(() -> {
/*     */           TreeMap<String, ArchiveStreamProvider> map = new TreeMap<>();
/*     */           putAll(DEFAULT.getOutputStreamArchiveNames(), DEFAULT, map);
/*     */           for (ArchiveStreamProvider provider : findArchiveStreamProviders()) {
/*     */             putAll(provider.getOutputStreamArchiveNames(), provider, map);
/*     */           }
/*     */           return map;
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveStreamFactory() {
/* 266 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveStreamFactory(String encoding) {
/* 277 */     this.encoding = encoding;
/*     */     
/* 279 */     this.entryEncoding = encoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getEntryEncoding() {
/* 290 */     return this.entryEncoding;
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
/*     */   @Deprecated
/*     */   public void setEntryEncoding(String entryEncoding) {
/* 305 */     if (this.encoding != null) {
/* 306 */       throw new IllegalStateException("Cannot overide encoding set by the constructor");
/*     */     }
/* 308 */     this.entryEncoding = entryEncoding;
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
/*     */   public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in) throws ArchiveException {
/* 325 */     return createArchiveInputStream(archiverName, in, this.entryEncoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in, String actualEncoding) throws ArchiveException {
/* 332 */     if (archiverName == null) {
/* 333 */       throw new IllegalArgumentException("Archivername must not be null.");
/*     */     }
/*     */     
/* 336 */     if (in == null) {
/* 337 */       throw new IllegalArgumentException("InputStream must not be null.");
/*     */     }
/*     */     
/* 340 */     if ("ar".equalsIgnoreCase(archiverName)) {
/* 341 */       return (ArchiveInputStream)new ArArchiveInputStream(in);
/*     */     }
/* 343 */     if ("arj".equalsIgnoreCase(archiverName)) {
/* 344 */       if (actualEncoding != null) {
/* 345 */         return (ArchiveInputStream)new ArjArchiveInputStream(in, actualEncoding);
/*     */       }
/* 347 */       return (ArchiveInputStream)new ArjArchiveInputStream(in);
/*     */     } 
/* 349 */     if ("zip".equalsIgnoreCase(archiverName)) {
/* 350 */       if (actualEncoding != null) {
/* 351 */         return (ArchiveInputStream)new ZipArchiveInputStream(in, actualEncoding);
/*     */       }
/* 353 */       return (ArchiveInputStream)new ZipArchiveInputStream(in);
/*     */     } 
/* 355 */     if ("tar".equalsIgnoreCase(archiverName)) {
/* 356 */       if (actualEncoding != null) {
/* 357 */         return (ArchiveInputStream)new TarArchiveInputStream(in, actualEncoding);
/*     */       }
/* 359 */       return (ArchiveInputStream)new TarArchiveInputStream(in);
/*     */     } 
/* 361 */     if ("jar".equalsIgnoreCase(archiverName)) {
/* 362 */       if (actualEncoding != null) {
/* 363 */         return (ArchiveInputStream)new JarArchiveInputStream(in, actualEncoding);
/*     */       }
/* 365 */       return (ArchiveInputStream)new JarArchiveInputStream(in);
/*     */     } 
/* 367 */     if ("cpio".equalsIgnoreCase(archiverName)) {
/* 368 */       if (actualEncoding != null) {
/* 369 */         return (ArchiveInputStream)new CpioArchiveInputStream(in, actualEncoding);
/*     */       }
/* 371 */       return (ArchiveInputStream)new CpioArchiveInputStream(in);
/*     */     } 
/* 373 */     if ("dump".equalsIgnoreCase(archiverName)) {
/* 374 */       if (actualEncoding != null) {
/* 375 */         return (ArchiveInputStream)new DumpArchiveInputStream(in, actualEncoding);
/*     */       }
/* 377 */       return (ArchiveInputStream)new DumpArchiveInputStream(in);
/*     */     } 
/* 379 */     if ("7z".equalsIgnoreCase(archiverName)) {
/* 380 */       throw new StreamingNotSupportedException("7z");
/*     */     }
/*     */     
/* 383 */     ArchiveStreamProvider archiveStreamProvider = getArchiveInputStreamProviders().get(toKey(archiverName));
/* 384 */     if (archiveStreamProvider != null) {
/* 385 */       return archiveStreamProvider.createArchiveInputStream(archiverName, in, actualEncoding);
/*     */     }
/*     */     
/* 388 */     throw new ArchiveException("Archiver: " + archiverName + " not found.");
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
/*     */   public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out) throws ArchiveException {
/* 405 */     return createArchiveOutputStream(archiverName, out, this.entryEncoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out, String actualEncoding) throws ArchiveException {
/* 412 */     if (archiverName == null) {
/* 413 */       throw new IllegalArgumentException("Archivername must not be null.");
/*     */     }
/* 415 */     if (out == null) {
/* 416 */       throw new IllegalArgumentException("OutputStream must not be null.");
/*     */     }
/*     */     
/* 419 */     if ("ar".equalsIgnoreCase(archiverName)) {
/* 420 */       return (ArchiveOutputStream)new ArArchiveOutputStream(out);
/*     */     }
/* 422 */     if ("zip".equalsIgnoreCase(archiverName)) {
/* 423 */       ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out);
/* 424 */       if (actualEncoding != null) {
/* 425 */         zip.setEncoding(actualEncoding);
/*     */       }
/* 427 */       return (ArchiveOutputStream)zip;
/*     */     } 
/* 429 */     if ("tar".equalsIgnoreCase(archiverName)) {
/* 430 */       if (actualEncoding != null) {
/* 431 */         return (ArchiveOutputStream)new TarArchiveOutputStream(out, actualEncoding);
/*     */       }
/* 433 */       return (ArchiveOutputStream)new TarArchiveOutputStream(out);
/*     */     } 
/* 435 */     if ("jar".equalsIgnoreCase(archiverName)) {
/* 436 */       if (actualEncoding != null) {
/* 437 */         return (ArchiveOutputStream)new JarArchiveOutputStream(out, actualEncoding);
/*     */       }
/* 439 */       return (ArchiveOutputStream)new JarArchiveOutputStream(out);
/*     */     } 
/* 441 */     if ("cpio".equalsIgnoreCase(archiverName)) {
/* 442 */       if (actualEncoding != null) {
/* 443 */         return (ArchiveOutputStream)new CpioArchiveOutputStream(out, actualEncoding);
/*     */       }
/* 445 */       return (ArchiveOutputStream)new CpioArchiveOutputStream(out);
/*     */     } 
/* 447 */     if ("7z".equalsIgnoreCase(archiverName)) {
/* 448 */       throw new StreamingNotSupportedException("7z");
/*     */     }
/*     */     
/* 451 */     ArchiveStreamProvider archiveStreamProvider = getArchiveOutputStreamProviders().get(toKey(archiverName));
/* 452 */     if (archiveStreamProvider != null) {
/* 453 */       return archiveStreamProvider.createArchiveOutputStream(archiverName, out, actualEncoding);
/*     */     }
/*     */     
/* 456 */     throw new ArchiveException("Archiver: " + archiverName + " not found.");
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
/*     */   public ArchiveInputStream createArchiveInputStream(InputStream in) throws ArchiveException {
/* 473 */     return createArchiveInputStream(detect(in), in);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String detect(InputStream in) throws ArchiveException {
/* 484 */     if (in == null) {
/* 485 */       throw new IllegalArgumentException("Stream must not be null.");
/*     */     }
/*     */     
/* 488 */     if (!in.markSupported()) {
/* 489 */       throw new IllegalArgumentException("Mark is not supported.");
/*     */     }
/*     */     
/* 492 */     byte[] signature = new byte[12];
/* 493 */     in.mark(signature.length);
/* 494 */     int signatureLength = -1;
/*     */     try {
/* 496 */       signatureLength = IOUtils.readFully(in, signature);
/* 497 */       in.reset();
/* 498 */     } catch (IOException e) {
/* 499 */       throw new ArchiveException("IOException while reading signature.", e);
/*     */     } 
/*     */     
/* 502 */     if (ZipArchiveInputStream.matches(signature, signatureLength)) {
/* 503 */       return "zip";
/*     */     }
/* 505 */     if (JarArchiveInputStream.matches(signature, signatureLength)) {
/* 506 */       return "jar";
/*     */     }
/* 508 */     if (ArArchiveInputStream.matches(signature, signatureLength)) {
/* 509 */       return "ar";
/*     */     }
/* 511 */     if (CpioArchiveInputStream.matches(signature, signatureLength)) {
/* 512 */       return "cpio";
/*     */     }
/* 514 */     if (ArjArchiveInputStream.matches(signature, signatureLength)) {
/* 515 */       return "arj";
/*     */     }
/* 517 */     if (SevenZFile.matches(signature, signatureLength)) {
/* 518 */       return "7z";
/*     */     }
/*     */ 
/*     */     
/* 522 */     byte[] dumpsig = new byte[32];
/* 523 */     in.mark(dumpsig.length);
/*     */     try {
/* 525 */       signatureLength = IOUtils.readFully(in, dumpsig);
/* 526 */       in.reset();
/* 527 */     } catch (IOException e) {
/* 528 */       throw new ArchiveException("IOException while reading dump signature", e);
/*     */     } 
/* 530 */     if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
/* 531 */       return "dump";
/*     */     }
/*     */ 
/*     */     
/* 535 */     byte[] tarHeader = new byte[512];
/* 536 */     in.mark(tarHeader.length);
/*     */     try {
/* 538 */       signatureLength = IOUtils.readFully(in, tarHeader);
/* 539 */       in.reset();
/* 540 */     } catch (IOException e) {
/* 541 */       throw new ArchiveException("IOException while reading tar signature", e);
/*     */     } 
/* 543 */     if (TarArchiveInputStream.matches(tarHeader, signatureLength)) {
/* 544 */       return "tar";
/*     */     }
/*     */ 
/*     */     
/* 548 */     if (signatureLength >= 512) {
/* 549 */       TarArchiveInputStream tais = null;
/*     */       try {
/* 551 */         tais = new TarArchiveInputStream(new ByteArrayInputStream(tarHeader));
/*     */         
/* 553 */         if (tais.getNextTarEntry().isCheckSumOK()) {
/* 554 */           return "tar";
/*     */         }
/* 556 */       } catch (Exception exception) {
/*     */ 
/*     */       
/*     */       }
/*     */       finally {
/*     */         
/* 562 */         IOUtils.closeQuietly((Closeable)tais);
/*     */       } 
/*     */     } 
/* 565 */     throw new ArchiveException("No Archiver found for the stream signature");
/*     */   }
/*     */   
/*     */   public SortedMap<String, ArchiveStreamProvider> getArchiveInputStreamProviders() {
/* 569 */     if (this.archiveInputStreamProviders == null) {
/* 570 */       this
/* 571 */         .archiveInputStreamProviders = Collections.unmodifiableSortedMap(findAvailableArchiveInputStreamProviders());
/*     */     }
/* 573 */     return this.archiveInputStreamProviders;
/*     */   }
/*     */   
/*     */   public SortedMap<String, ArchiveStreamProvider> getArchiveOutputStreamProviders() {
/* 577 */     if (this.archiveOutputStreamProviders == null) {
/* 578 */       this
/* 579 */         .archiveOutputStreamProviders = Collections.unmodifiableSortedMap(findAvailableArchiveOutputStreamProviders());
/*     */     }
/* 581 */     return this.archiveOutputStreamProviders;
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getInputStreamArchiveNames() {
/* 586 */     return Sets.newHashSet((Object[])new String[] { "ar", "arj", "zip", "tar", "jar", "cpio", "dump", "7z" });
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getOutputStreamArchiveNames() {
/* 591 */     return Sets.newHashSet((Object[])new String[] { "ar", "zip", "tar", "jar", "cpio", "7z" });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\ArchiveStreamFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */