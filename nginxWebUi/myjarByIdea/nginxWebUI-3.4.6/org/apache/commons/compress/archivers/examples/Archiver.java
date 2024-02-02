package org.apache.commons.compress.archivers.examples;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Objects;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class Archiver {
   public static final EnumSet<FileVisitOption> EMPTY_FileVisitOption = EnumSet.noneOf(FileVisitOption.class);

   public void create(ArchiveOutputStream target, File directory) throws IOException, ArchiveException {
      this.create(target, directory.toPath(), EMPTY_FileVisitOption);
   }

   public void create(ArchiveOutputStream target, Path directory, EnumSet<FileVisitOption> fileVisitOptions, LinkOption... linkOptions) throws IOException {
      Files.walkFileTree(directory, fileVisitOptions, Integer.MAX_VALUE, new ArchiverFileVisitor(target, directory, linkOptions));
      target.finish();
   }

   public void create(ArchiveOutputStream target, Path directory) throws IOException {
      this.create(target, directory, EMPTY_FileVisitOption);
   }

   public void create(SevenZOutputFile target, File directory) throws IOException {
      this.create(target, directory.toPath());
   }

   public void create(final SevenZOutputFile target, final Path directory) throws IOException {
      Files.walkFileTree(directory, new ArchiverFileVisitor((ArchiveOutputStream)null, directory, new LinkOption[0]) {
         protected FileVisitResult visit(Path path, BasicFileAttributes attrs, boolean isFile) throws IOException {
            Objects.requireNonNull(path);
            Objects.requireNonNull(attrs);
            String name = directory.relativize(path).toString().replace('\\', '/');
            if (!name.isEmpty()) {
               ArchiveEntry archiveEntry = target.createArchiveEntry(path, !isFile && !name.endsWith("/") ? name + "/" : name);
               target.putArchiveEntry(archiveEntry);
               if (isFile) {
                  target.write(path);
               }

               target.closeArchiveEntry();
            }

            return FileVisitResult.CONTINUE;
         }
      });
      target.finish();
   }

   public void create(String format, File target, File directory) throws IOException, ArchiveException {
      this.create(format, target.toPath(), directory.toPath());
   }

   /** @deprecated */
   @Deprecated
   public void create(String format, OutputStream target, File directory) throws IOException, ArchiveException {
      this.create(format, target, directory, CloseableConsumer.NULL_CONSUMER);
   }

   public void create(String format, OutputStream target, File directory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
      CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer);
      Throwable var6 = null;

      try {
         this.create((ArchiveOutputStream)c.track(ArchiveStreamFactory.DEFAULT.createArchiveOutputStream(format, target)), directory);
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

   public void create(String format, Path target, Path directory) throws IOException, ArchiveException {
      Throwable var5;
      if (this.prefersSeekableByteChannel(format)) {
         SeekableByteChannel channel = FileChannel.open(target, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         var5 = null;

         try {
            this.create(format, (SeekableByteChannel)channel, (Path)directory);
         } catch (Throwable var28) {
            var5 = var28;
            throw var28;
         } finally {
            if (channel != null) {
               if (var5 != null) {
                  try {
                     channel.close();
                  } catch (Throwable var26) {
                     var5.addSuppressed(var26);
                  }
               } else {
                  channel.close();
               }
            }

         }

      } else {
         ArchiveOutputStream outputStream = ArchiveStreamFactory.DEFAULT.createArchiveOutputStream(format, Files.newOutputStream(target));
         var5 = null;

         try {
            this.create(outputStream, directory, EMPTY_FileVisitOption);
         } catch (Throwable var29) {
            var5 = var29;
            throw var29;
         } finally {
            if (outputStream != null) {
               if (var5 != null) {
                  try {
                     outputStream.close();
                  } catch (Throwable var27) {
                     var5.addSuppressed(var27);
                  }
               } else {
                  outputStream.close();
               }
            }

         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void create(String format, SeekableByteChannel target, File directory) throws IOException, ArchiveException {
      this.create(format, target, directory, CloseableConsumer.NULL_CONSUMER);
   }

   public void create(String format, SeekableByteChannel target, File directory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
      CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer);
      Throwable var6 = null;

      try {
         if (!this.prefersSeekableByteChannel(format)) {
            this.create(format, (OutputStream)c.track(Channels.newOutputStream(target)), directory);
         } else if ("zip".equalsIgnoreCase(format)) {
            this.create((ArchiveOutputStream)c.track(new ZipArchiveOutputStream(target)), directory);
         } else {
            if (!"7z".equalsIgnoreCase(format)) {
               throw new ArchiveException("Don't know how to handle format " + format);
            }

            this.create((SevenZOutputFile)c.track(new SevenZOutputFile(target)), directory);
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

   public void create(String format, SeekableByteChannel target, Path directory) throws IOException {
      Throwable var5;
      if ("7z".equalsIgnoreCase(format)) {
         SevenZOutputFile sevenZFile = new SevenZOutputFile(target);
         var5 = null;

         try {
            this.create(sevenZFile, directory);
         } catch (Throwable var29) {
            var5 = var29;
            throw var29;
         } finally {
            if (sevenZFile != null) {
               if (var5 != null) {
                  try {
                     sevenZFile.close();
                  } catch (Throwable var27) {
                     var5.addSuppressed(var27);
                  }
               } else {
                  sevenZFile.close();
               }
            }

         }
      } else {
         if (!"zip".equalsIgnoreCase(format)) {
            throw new IllegalStateException(format);
         }

         ArchiveOutputStream archiveOutputStream = new ZipArchiveOutputStream(target);
         var5 = null;

         try {
            this.create((ArchiveOutputStream)archiveOutputStream, (Path)directory, (EnumSet)EMPTY_FileVisitOption, (LinkOption[])());
         } catch (Throwable var28) {
            var5 = var28;
            throw var28;
         } finally {
            if (archiveOutputStream != null) {
               if (var5 != null) {
                  try {
                     archiveOutputStream.close();
                  } catch (Throwable var26) {
                     var5.addSuppressed(var26);
                  }
               } else {
                  archiveOutputStream.close();
               }
            }

         }
      }

   }

   private boolean prefersSeekableByteChannel(String format) {
      return "zip".equalsIgnoreCase(format) || "7z".equalsIgnoreCase(format);
   }

   private static class ArchiverFileVisitor extends SimpleFileVisitor<Path> {
      private final ArchiveOutputStream target;
      private final Path directory;
      private final LinkOption[] linkOptions;

      private ArchiverFileVisitor(ArchiveOutputStream target, Path directory, LinkOption... linkOptions) {
         this.target = target;
         this.directory = directory;
         this.linkOptions = linkOptions == null ? IOUtils.EMPTY_LINK_OPTIONS : (LinkOption[])linkOptions.clone();
      }

      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
         return this.visit(dir, attrs, false);
      }

      protected FileVisitResult visit(Path path, BasicFileAttributes attrs, boolean isFile) throws IOException {
         Objects.requireNonNull(path);
         Objects.requireNonNull(attrs);
         String name = this.directory.relativize(path).toString().replace('\\', '/');
         if (!name.isEmpty()) {
            ArchiveEntry archiveEntry = this.target.createArchiveEntry(path, !isFile && !name.endsWith("/") ? name + "/" : name, this.linkOptions);
            this.target.putArchiveEntry(archiveEntry);
            if (isFile) {
               Files.copy(path, this.target);
            }

            this.target.closeArchiveEntry();
         }

         return FileVisitResult.CONTINUE;
      }

      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
         return this.visit(file, attrs, true);
      }

      // $FF: synthetic method
      ArchiverFileVisitor(ArchiveOutputStream x0, Path x1, LinkOption[] x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
