package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
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
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.ServiceLoaderIterator;
import org.apache.commons.compress.utils.Sets;

public class ArchiveStreamFactory implements ArchiveStreamProvider {
   private static final int TAR_HEADER_SIZE = 512;
   private static final int DUMP_SIGNATURE_SIZE = 32;
   private static final int SIGNATURE_SIZE = 12;
   public static final ArchiveStreamFactory DEFAULT = new ArchiveStreamFactory();
   public static final String AR = "ar";
   public static final String ARJ = "arj";
   public static final String CPIO = "cpio";
   public static final String DUMP = "dump";
   public static final String JAR = "jar";
   public static final String TAR = "tar";
   public static final String ZIP = "zip";
   public static final String SEVEN_Z = "7z";
   private final String encoding;
   private volatile String entryEncoding;
   private SortedMap<String, ArchiveStreamProvider> archiveInputStreamProviders;
   private SortedMap<String, ArchiveStreamProvider> archiveOutputStreamProviders;

   private static ArrayList<ArchiveStreamProvider> findArchiveStreamProviders() {
      return Lists.newArrayList(serviceLoaderIterator());
   }

   static void putAll(Set<String> names, ArchiveStreamProvider provider, TreeMap<String, ArchiveStreamProvider> map) {
      Iterator var3 = names.iterator();

      while(var3.hasNext()) {
         String name = (String)var3.next();
         map.put(toKey(name), provider);
      }

   }

   private static Iterator<ArchiveStreamProvider> serviceLoaderIterator() {
      return new ServiceLoaderIterator(ArchiveStreamProvider.class);
   }

   private static String toKey(String name) {
      return name.toUpperCase(Locale.ROOT);
   }

   public static SortedMap<String, ArchiveStreamProvider> findAvailableArchiveInputStreamProviders() {
      return (SortedMap)AccessController.doPrivileged(() -> {
         TreeMap<String, ArchiveStreamProvider> map = new TreeMap();
         putAll(DEFAULT.getInputStreamArchiveNames(), DEFAULT, map);
         Iterator var1 = findArchiveStreamProviders().iterator();

         while(var1.hasNext()) {
            ArchiveStreamProvider provider = (ArchiveStreamProvider)var1.next();
            putAll(provider.getInputStreamArchiveNames(), provider, map);
         }

         return map;
      });
   }

   public static SortedMap<String, ArchiveStreamProvider> findAvailableArchiveOutputStreamProviders() {
      return (SortedMap)AccessController.doPrivileged(() -> {
         TreeMap<String, ArchiveStreamProvider> map = new TreeMap();
         putAll(DEFAULT.getOutputStreamArchiveNames(), DEFAULT, map);
         Iterator var1 = findArchiveStreamProviders().iterator();

         while(var1.hasNext()) {
            ArchiveStreamProvider provider = (ArchiveStreamProvider)var1.next();
            putAll(provider.getOutputStreamArchiveNames(), provider, map);
         }

         return map;
      });
   }

   public ArchiveStreamFactory() {
      this((String)null);
   }

   public ArchiveStreamFactory(String encoding) {
      this.encoding = encoding;
      this.entryEncoding = encoding;
   }

   public String getEntryEncoding() {
      return this.entryEncoding;
   }

   /** @deprecated */
   @Deprecated
   public void setEntryEncoding(String entryEncoding) {
      if (this.encoding != null) {
         throw new IllegalStateException("Cannot overide encoding set by the constructor");
      } else {
         this.entryEncoding = entryEncoding;
      }
   }

   public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in) throws ArchiveException {
      return this.createArchiveInputStream(archiverName, in, this.entryEncoding);
   }

   public ArchiveInputStream createArchiveInputStream(String archiverName, InputStream in, String actualEncoding) throws ArchiveException {
      if (archiverName == null) {
         throw new IllegalArgumentException("Archivername must not be null.");
      } else if (in == null) {
         throw new IllegalArgumentException("InputStream must not be null.");
      } else if ("ar".equalsIgnoreCase(archiverName)) {
         return new ArArchiveInputStream(in);
      } else if ("arj".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new ArjArchiveInputStream(in, actualEncoding) : new ArjArchiveInputStream(in);
      } else if ("zip".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new ZipArchiveInputStream(in, actualEncoding) : new ZipArchiveInputStream(in);
      } else if ("tar".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new TarArchiveInputStream(in, actualEncoding) : new TarArchiveInputStream(in);
      } else if ("jar".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new JarArchiveInputStream(in, actualEncoding) : new JarArchiveInputStream(in);
      } else if ("cpio".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new CpioArchiveInputStream(in, actualEncoding) : new CpioArchiveInputStream(in);
      } else if ("dump".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new DumpArchiveInputStream(in, actualEncoding) : new DumpArchiveInputStream(in);
      } else if ("7z".equalsIgnoreCase(archiverName)) {
         throw new StreamingNotSupportedException("7z");
      } else {
         ArchiveStreamProvider archiveStreamProvider = (ArchiveStreamProvider)this.getArchiveInputStreamProviders().get(toKey(archiverName));
         if (archiveStreamProvider != null) {
            return archiveStreamProvider.createArchiveInputStream(archiverName, in, actualEncoding);
         } else {
            throw new ArchiveException("Archiver: " + archiverName + " not found.");
         }
      }
   }

   public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out) throws ArchiveException {
      return this.createArchiveOutputStream(archiverName, out, this.entryEncoding);
   }

   public ArchiveOutputStream createArchiveOutputStream(String archiverName, OutputStream out, String actualEncoding) throws ArchiveException {
      if (archiverName == null) {
         throw new IllegalArgumentException("Archivername must not be null.");
      } else if (out == null) {
         throw new IllegalArgumentException("OutputStream must not be null.");
      } else if ("ar".equalsIgnoreCase(archiverName)) {
         return new ArArchiveOutputStream(out);
      } else if ("zip".equalsIgnoreCase(archiverName)) {
         ZipArchiveOutputStream zip = new ZipArchiveOutputStream(out);
         if (actualEncoding != null) {
            zip.setEncoding(actualEncoding);
         }

         return zip;
      } else if ("tar".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new TarArchiveOutputStream(out, actualEncoding) : new TarArchiveOutputStream(out);
      } else if ("jar".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new JarArchiveOutputStream(out, actualEncoding) : new JarArchiveOutputStream(out);
      } else if ("cpio".equalsIgnoreCase(archiverName)) {
         return actualEncoding != null ? new CpioArchiveOutputStream(out, actualEncoding) : new CpioArchiveOutputStream(out);
      } else if ("7z".equalsIgnoreCase(archiverName)) {
         throw new StreamingNotSupportedException("7z");
      } else {
         ArchiveStreamProvider archiveStreamProvider = (ArchiveStreamProvider)this.getArchiveOutputStreamProviders().get(toKey(archiverName));
         if (archiveStreamProvider != null) {
            return archiveStreamProvider.createArchiveOutputStream(archiverName, out, actualEncoding);
         } else {
            throw new ArchiveException("Archiver: " + archiverName + " not found.");
         }
      }
   }

   public ArchiveInputStream createArchiveInputStream(InputStream in) throws ArchiveException {
      return this.createArchiveInputStream(detect(in), in);
   }

   public static String detect(InputStream in) throws ArchiveException {
      if (in == null) {
         throw new IllegalArgumentException("Stream must not be null.");
      } else if (!in.markSupported()) {
         throw new IllegalArgumentException("Mark is not supported.");
      } else {
         byte[] signature = new byte[12];
         in.mark(signature.length);
         int signatureLength = true;

         int signatureLength;
         try {
            signatureLength = IOUtils.readFully(in, signature);
            in.reset();
         } catch (IOException var15) {
            throw new ArchiveException("IOException while reading signature.", var15);
         }

         if (ZipArchiveInputStream.matches(signature, signatureLength)) {
            return "zip";
         } else if (JarArchiveInputStream.matches(signature, signatureLength)) {
            return "jar";
         } else if (ArArchiveInputStream.matches(signature, signatureLength)) {
            return "ar";
         } else if (CpioArchiveInputStream.matches(signature, signatureLength)) {
            return "cpio";
         } else if (ArjArchiveInputStream.matches(signature, signatureLength)) {
            return "arj";
         } else if (SevenZFile.matches(signature, signatureLength)) {
            return "7z";
         } else {
            byte[] dumpsig = new byte[32];
            in.mark(dumpsig.length);

            try {
               signatureLength = IOUtils.readFully(in, dumpsig);
               in.reset();
            } catch (IOException var14) {
               throw new ArchiveException("IOException while reading dump signature", var14);
            }

            if (DumpArchiveInputStream.matches(dumpsig, signatureLength)) {
               return "dump";
            } else {
               byte[] tarHeader = new byte[512];
               in.mark(tarHeader.length);

               try {
                  signatureLength = IOUtils.readFully(in, tarHeader);
                  in.reset();
               } catch (IOException var13) {
                  throw new ArchiveException("IOException while reading tar signature", var13);
               }

               if (TarArchiveInputStream.matches(tarHeader, signatureLength)) {
                  return "tar";
               } else {
                  if (signatureLength >= 512) {
                     TarArchiveInputStream tais = null;

                     try {
                        tais = new TarArchiveInputStream(new ByteArrayInputStream(tarHeader));
                        if (tais.getNextTarEntry().isCheckSumOK()) {
                           String var6 = "tar";
                           return var6;
                        }
                     } catch (Exception var16) {
                     } finally {
                        IOUtils.closeQuietly(tais);
                     }
                  }

                  throw new ArchiveException("No Archiver found for the stream signature");
               }
            }
         }
      }
   }

   public SortedMap<String, ArchiveStreamProvider> getArchiveInputStreamProviders() {
      if (this.archiveInputStreamProviders == null) {
         this.archiveInputStreamProviders = Collections.unmodifiableSortedMap(findAvailableArchiveInputStreamProviders());
      }

      return this.archiveInputStreamProviders;
   }

   public SortedMap<String, ArchiveStreamProvider> getArchiveOutputStreamProviders() {
      if (this.archiveOutputStreamProviders == null) {
         this.archiveOutputStreamProviders = Collections.unmodifiableSortedMap(findAvailableArchiveOutputStreamProviders());
      }

      return this.archiveOutputStreamProviders;
   }

   public Set<String> getInputStreamArchiveNames() {
      return Sets.newHashSet("ar", "arj", "zip", "tar", "jar", "cpio", "dump", "7z");
   }

   public Set<String> getOutputStreamArchiveNames() {
      return Sets.newHashSet("ar", "zip", "tar", "jar", "cpio", "7z");
   }
}
