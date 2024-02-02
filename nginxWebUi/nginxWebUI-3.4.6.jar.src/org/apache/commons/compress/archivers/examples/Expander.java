/*     */ package org.apache.commons.compress.archivers.examples;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveException;
/*     */ import org.apache.commons.compress.archivers.ArchiveInputStream;
/*     */ import org.apache.commons.compress.archivers.ArchiveStreamFactory;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZFile;
/*     */ import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.tar.TarFile;
/*     */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.zip.ZipFile;
/*     */ import org.apache.commons.compress.utils.IOUtils;
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
/*     */ public class Expander
/*     */ {
/*     */   public void expand(File archive, File targetDirectory) throws IOException, ArchiveException {
/*  70 */     String format = null;
/*  71 */     try (InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath(), new OpenOption[0]))) {
/*  72 */       format = ArchiveStreamFactory.detect(i);
/*     */     } 
/*  74 */     expand(format, archive, targetDirectory);
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
/*     */   public void expand(String format, File archive, File targetDirectory) throws IOException, ArchiveException {
/*  88 */     if (prefersSeekableByteChannel(format)) {
/*  89 */       try (SeekableByteChannel c = FileChannel.open(archive.toPath(), new OpenOption[] { StandardOpenOption.READ })) {
/*  90 */         expand(format, c, targetDirectory, CloseableConsumer.CLOSING_CONSUMER);
/*     */       } 
/*     */       return;
/*     */     } 
/*  94 */     try (InputStream i = new BufferedInputStream(Files.newInputStream(archive.toPath(), new OpenOption[0]))) {
/*  95 */       expand(format, i, targetDirectory, CloseableConsumer.CLOSING_CONSUMER);
/*     */     } 
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
/*     */   @Deprecated
/*     */   public void expand(InputStream archive, File targetDirectory) throws IOException, ArchiveException {
/* 117 */     expand(archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
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
/*     */   public void expand(InputStream archive, File targetDirectory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
/* 141 */     try (CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
/* 142 */       expand(c.<ArchiveInputStream>track(ArchiveStreamFactory.DEFAULT.createArchiveInputStream(archive)), targetDirectory);
/*     */     } 
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
/*     */   @Deprecated
/*     */   public void expand(String format, InputStream archive, File targetDirectory) throws IOException, ArchiveException {
/* 166 */     expand(format, archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
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
/*     */   public void expand(String format, InputStream archive, File targetDirectory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
/* 190 */     try (CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
/* 191 */       expand(c.<ArchiveInputStream>track(ArchiveStreamFactory.DEFAULT.createArchiveInputStream(format, archive)), targetDirectory);
/*     */     } 
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
/*     */   @Deprecated
/*     */   public void expand(String format, SeekableByteChannel archive, File targetDirectory) throws IOException, ArchiveException {
/* 215 */     expand(format, archive, targetDirectory, CloseableConsumer.NULL_CONSUMER);
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
/*     */   public void expand(String format, SeekableByteChannel archive, File targetDirectory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
/* 240 */     try (CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
/* 241 */       if (!prefersSeekableByteChannel(format)) {
/* 242 */         expand(format, c.<InputStream>track(Channels.newInputStream(archive)), targetDirectory);
/* 243 */       } else if ("tar".equalsIgnoreCase(format)) {
/* 244 */         expand(c.<TarFile>track(new TarFile(archive)), targetDirectory);
/* 245 */       } else if ("zip".equalsIgnoreCase(format)) {
/* 246 */         expand(c.<ZipFile>track(new ZipFile(archive)), targetDirectory);
/* 247 */       } else if ("7z".equalsIgnoreCase(format)) {
/* 248 */         expand(c.<SevenZFile>track(new SevenZFile(archive)), targetDirectory);
/*     */       } else {
/*     */         
/* 251 */         throw new ArchiveException("Don't know how to handle format " + format);
/*     */       } 
/*     */     } 
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
/*     */   public void expand(ArchiveInputStream archive, File targetDirectory) throws IOException, ArchiveException {
/* 266 */     expand(() -> { ArchiveEntry next = archive.getNextEntry(); while (next != null && !archive.canReadEntryData(next)) next = archive.getNextEntry();  return next; }(entry, out) -> IOUtils.copy((InputStream)archive, out), targetDirectory);
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
/*     */   public void expand(TarFile archive, File targetDirectory) throws IOException, ArchiveException {
/* 286 */     Iterator<TarArchiveEntry> entryIterator = archive.getEntries().iterator();
/* 287 */     expand(() -> entryIterator.hasNext() ? entryIterator.next() : null, (entry, out) -> { try (InputStream in = archive.getInputStream((TarArchiveEntry)entry)) { IOUtils.copy(in, out); }  }targetDirectory);
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
/*     */   public void expand(ZipFile archive, File targetDirectory) throws IOException, ArchiveException {
/* 305 */     Enumeration<ZipArchiveEntry> entries = archive.getEntries();
/* 306 */     expand(() -> { ZipArchiveEntry next = entries.hasMoreElements() ? entries.nextElement() : null; while (next != null && !archive.canReadEntryData(next)) next = entries.hasMoreElements() ? entries.nextElement() : null;  return (ArchiveEntry)next; }(entry, out) -> { try (InputStream in = archive.getInputStream((ZipArchiveEntry)entry)) { IOUtils.copy(in, out); }  }targetDirectory);
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
/*     */   public void expand(SevenZFile archive, File targetDirectory) throws IOException, ArchiveException {
/* 329 */     expand(archive::getNextEntry, (entry, out) -> { byte[] buffer = new byte[8192]; int n; while (-1 != (n = archive.read(buffer))) out.write(buffer, 0, n);  }targetDirectory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean prefersSeekableByteChannel(String format) {
/* 339 */     return ("tar".equalsIgnoreCase(format) || "zip"
/* 340 */       .equalsIgnoreCase(format) || "7z"
/* 341 */       .equalsIgnoreCase(format));
/*     */   }
/*     */ 
/*     */   
/*     */   private void expand(ArchiveEntrySupplier supplier, EntryWriter writer, File targetDirectory) throws IOException {
/* 346 */     String targetDirPath = targetDirectory.getCanonicalPath();
/* 347 */     if (!targetDirPath.endsWith(File.separator)) {
/* 348 */       targetDirPath = targetDirPath + File.separator;
/*     */     }
/* 350 */     ArchiveEntry nextEntry = supplier.getNextReadableEntry();
/* 351 */     while (nextEntry != null) {
/* 352 */       File f = new File(targetDirectory, nextEntry.getName());
/* 353 */       if (!f.getCanonicalPath().startsWith(targetDirPath)) {
/* 354 */         throw new IOException("Expanding " + nextEntry.getName() + " would create file outside of " + targetDirectory);
/*     */       }
/*     */       
/* 357 */       if (nextEntry.isDirectory()) {
/* 358 */         if (!f.isDirectory() && !f.mkdirs()) {
/* 359 */           throw new IOException("Failed to create directory " + f);
/*     */         }
/*     */       } else {
/* 362 */         File parent = f.getParentFile();
/* 363 */         if (!parent.isDirectory() && !parent.mkdirs()) {
/* 364 */           throw new IOException("Failed to create directory " + parent);
/*     */         }
/* 366 */         try (OutputStream o = Files.newOutputStream(f.toPath(), new OpenOption[0])) {
/* 367 */           writer.writeEntryDataTo(nextEntry, o);
/*     */         } 
/*     */       } 
/* 370 */       nextEntry = supplier.getNextReadableEntry();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static interface EntryWriter {
/*     */     void writeEntryDataTo(ArchiveEntry param1ArchiveEntry, OutputStream param1OutputStream) throws IOException;
/*     */   }
/*     */   
/*     */   private static interface ArchiveEntrySupplier {
/*     */     ArchiveEntry getNextReadableEntry() throws IOException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\examples\Expander.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */