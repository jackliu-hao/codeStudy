package org.apache.commons.compress.archivers.examples;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

public class Expander {
   public void expand(File archive, File targetDirectory) throws IOException, ArchiveException {
      String format = null;
      InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath()));
      Throwable var5 = null;

      try {
         format = ArchiveStreamFactory.detect(i);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (i != null) {
            if (var5 != null) {
               try {
                  i.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               i.close();
            }
         }

      }

      this.expand(format, archive, targetDirectory);
   }

   public void expand(String format, File archive, File targetDirectory) throws IOException, ArchiveException {
      Throwable var5;
      if (this.prefersSeekableByteChannel(format)) {
         SeekableByteChannel c = FileChannel.open(archive.toPath(), StandardOpenOption.READ);
         var5 = null;

         try {
            this.expand(format, (SeekableByteChannel)c, targetDirectory, CloseableConsumer.CLOSING_CONSUMER);
         } catch (Throwable var28) {
            var5 = var28;
            throw var28;
         } finally {
            if (c != null) {
               if (var5 != null) {
                  try {
                     c.close();
                  } catch (Throwable var27) {
                     var5.addSuppressed(var27);
                  }
               } else {
                  c.close();
               }
            }

         }

      } else {
         InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath()));
         var5 = null;

         try {
            this.expand(format, (InputStream)i, targetDirectory, CloseableConsumer.CLOSING_CONSUMER);
         } catch (Throwable var29) {
            var5 = var29;
            throw var29;
         } finally {
            if (i != null) {
               if (var5 != null) {
                  try {
                     i.close();
                  } catch (Throwable var26) {
                     var5.addSuppressed(var26);
                  }
               } else {
                  i.close();
               }
            }

         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void expand(InputStream archive, File targetDirectory) throws IOException, ArchiveException {
      this.expand(archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
   }

   public void expand(InputStream archive, File targetDirectory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
      CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer);
      Throwable var5 = null;

      try {
         this.expand((ArchiveInputStream)c.track(ArchiveStreamFactory.DEFAULT.createArchiveInputStream(archive)), targetDirectory);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (c != null) {
            if (var5 != null) {
               try {
                  c.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               c.close();
            }
         }

      }

   }

   /** @deprecated */
   @Deprecated
   public void expand(String format, InputStream archive, File targetDirectory) throws IOException, ArchiveException {
      this.expand(format, archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
   }

   public void expand(String format, InputStream archive, File targetDirectory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
      CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer);
      Throwable var6 = null;

      try {
         this.expand((ArchiveInputStream)c.track(ArchiveStreamFactory.DEFAULT.createArchiveInputStream(format, archive)), targetDirectory);
      } catch (Throwable var15) {
         var6 = var15;
         throw var15;
      } finally {
         if (c != null) {
            if (var6 != null) {
               try {
                  c.close();
               } catch (Throwable var14) {
                  var6.addSuppressed(var14);
               }
            } else {
               c.close();
            }
         }

      }

   }

   /** @deprecated */
   @Deprecated
   public void expand(String format, SeekableByteChannel archive, File targetDirectory) throws IOException, ArchiveException {
      this.expand(format, archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
   }

   public void expand(String format, SeekableByteChannel archive, File targetDirectory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
      CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer);
      Throwable var6 = null;

      try {
         if (!this.prefersSeekableByteChannel(format)) {
            this.expand(format, (InputStream)c.track(Channels.newInputStream(archive)), targetDirectory);
         } else if ("tar".equalsIgnoreCase(format)) {
            this.expand((TarFile)c.track(new TarFile(archive)), targetDirectory);
         } else if ("zip".equalsIgnoreCase(format)) {
            this.expand((ZipFile)c.track(new ZipFile(archive)), targetDirectory);
         } else {
            if (!"7z".equalsIgnoreCase(format)) {
               throw new ArchiveException("Don't know how to handle format " + format);
            }

            this.expand((SevenZFile)c.track(new SevenZFile(archive)), targetDirectory);
         }
      } catch (Throwable var15) {
         var6 = var15;
         throw var15;
      } finally {
         if (c != null) {
            if (var6 != null) {
               try {
                  c.close();
               } catch (Throwable var14) {
                  var6.addSuppressed(var14);
               }
            } else {
               c.close();
            }
         }

      }

   }

   public void expand(ArchiveInputStream archive, File targetDirectory) throws IOException, ArchiveException {
      this.expand(() -> {
         ArchiveEntry next;
         for(next = archive.getNextEntry(); next != null && !archive.canReadEntryData(next); next = archive.getNextEntry()) {
         }

         return next;
      }, (entry, out) -> {
         IOUtils.copy((InputStream)archive, out);
      }, targetDirectory);
   }

   public void expand(TarFile archive, File targetDirectory) throws IOException, ArchiveException {
      Iterator<TarArchiveEntry> entryIterator = archive.getEntries().iterator();
      this.expand(() -> {
         return entryIterator.hasNext() ? (ArchiveEntry)entryIterator.next() : null;
      }, (entry, out) -> {
         InputStream in = archive.getInputStream((TarArchiveEntry)entry);
         Throwable var4 = null;

         try {
            IOUtils.copy(in, out);
         } catch (Throwable var13) {
            var4 = var13;
            throw var13;
         } finally {
            if (in != null) {
               if (var4 != null) {
                  try {
                     in.close();
                  } catch (Throwable var12) {
                     var4.addSuppressed(var12);
                  }
               } else {
                  in.close();
               }
            }

         }

      }, targetDirectory);
   }

   public void expand(ZipFile archive, File targetDirectory) throws IOException, ArchiveException {
      Enumeration<ZipArchiveEntry> entries = archive.getEntries();
      this.expand(() -> {
         ZipArchiveEntry next;
         for(next = entries.hasMoreElements() ? (ZipArchiveEntry)entries.nextElement() : null; next != null && !archive.canReadEntryData(next); next = entries.hasMoreElements() ? (ZipArchiveEntry)entries.nextElement() : null) {
         }

         return next;
      }, (entry, out) -> {
         InputStream in = archive.getInputStream((ZipArchiveEntry)entry);
         Throwable var4 = null;

         try {
            IOUtils.copy(in, out);
         } catch (Throwable var13) {
            var4 = var13;
            throw var13;
         } finally {
            if (in != null) {
               if (var4 != null) {
                  try {
                     in.close();
                  } catch (Throwable var12) {
                     var4.addSuppressed(var12);
                  }
               } else {
                  in.close();
               }
            }

         }

      }, targetDirectory);
   }

   public void expand(SevenZFile archive, File targetDirectory) throws IOException, ArchiveException {
      this.expand(archive::getNextEntry, (entry, out) -> {
         byte[] buffer = new byte[8192];

         int n;
         while(-1 != (n = archive.read(buffer))) {
            out.write(buffer, 0, n);
         }

      }, targetDirectory);
   }

   private boolean prefersSeekableByteChannel(String format) {
      return "tar".equalsIgnoreCase(format) || "zip".equalsIgnoreCase(format) || "7z".equalsIgnoreCase(format);
   }

   private void expand(ArchiveEntrySupplier supplier, EntryWriter writer, File targetDirectory) throws IOException {
      String targetDirPath = targetDirectory.getCanonicalPath();
      if (!targetDirPath.endsWith(File.separator)) {
         targetDirPath = targetDirPath + File.separator;
      }

      for(ArchiveEntry nextEntry = supplier.getNextReadableEntry(); nextEntry != null; nextEntry = supplier.getNextReadableEntry()) {
         File f = new File(targetDirectory, nextEntry.getName());
         if (!f.getCanonicalPath().startsWith(targetDirPath)) {
            throw new IOException("Expanding " + nextEntry.getName() + " would create file outside of " + targetDirectory);
         }

         if (nextEntry.isDirectory()) {
            if (!f.isDirectory() && !f.mkdirs()) {
               throw new IOException("Failed to create directory " + f);
            }
         } else {
            File parent = f.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
               throw new IOException("Failed to create directory " + parent);
            }

            OutputStream o = Files.newOutputStream(f.toPath());
            Throwable var9 = null;

            try {
               writer.writeEntryDataTo(nextEntry, o);
            } catch (Throwable var18) {
               var9 = var18;
               throw var18;
            } finally {
               if (o != null) {
                  if (var9 != null) {
                     try {
                        o.close();
                     } catch (Throwable var17) {
                        var9.addSuppressed(var17);
                     }
                  } else {
                     o.close();
                  }
               }

            }
         }
      }

   }

   private interface EntryWriter {
      void writeEntryDataTo(ArchiveEntry var1, OutputStream var2) throws IOException;
   }

   private interface ArchiveEntrySupplier {
      ArchiveEntry getNextReadableEntry() throws IOException;
   }
}
