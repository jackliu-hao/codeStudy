/*     */ package org.apache.commons.compress.archivers.examples;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.FileVisitOption;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Objects;
/*     */ import org.apache.commons.compress.archivers.ArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.ArchiveException;
/*     */ import org.apache.commons.compress.archivers.ArchiveOutputStream;
/*     */ import org.apache.commons.compress.archivers.ArchiveStreamFactory;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
/*     */ import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
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
/*     */ public class Archiver
/*     */ {
/*     */   private static class ArchiverFileVisitor
/*     */     extends SimpleFileVisitor<Path>
/*     */   {
/*     */     private final ArchiveOutputStream target;
/*     */     private final Path directory;
/*     */     private final LinkOption[] linkOptions;
/*     */     
/*     */     private ArchiverFileVisitor(ArchiveOutputStream target, Path directory, LinkOption... linkOptions) {
/*  62 */       this.target = target;
/*  63 */       this.directory = directory;
/*  64 */       this.linkOptions = (linkOptions == null) ? IOUtils.EMPTY_LINK_OPTIONS : (LinkOption[])linkOptions.clone();
/*     */     }
/*     */ 
/*     */     
/*     */     public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
/*  69 */       return visit(dir, attrs, false);
/*     */     }
/*     */ 
/*     */     
/*     */     protected FileVisitResult visit(Path path, BasicFileAttributes attrs, boolean isFile) throws IOException {
/*  74 */       Objects.requireNonNull(path);
/*  75 */       Objects.requireNonNull(attrs);
/*  76 */       String name = this.directory.relativize(path).toString().replace('\\', '/');
/*  77 */       if (!name.isEmpty()) {
/*  78 */         ArchiveEntry archiveEntry = this.target.createArchiveEntry(path, (isFile || name
/*  79 */             .endsWith("/")) ? name : (name + "/"), this.linkOptions);
/*  80 */         this.target.putArchiveEntry(archiveEntry);
/*  81 */         if (isFile)
/*     */         {
/*  83 */           Files.copy(path, (OutputStream)this.target);
/*     */         }
/*  85 */         this.target.closeArchiveEntry();
/*     */       } 
/*  87 */       return FileVisitResult.CONTINUE;
/*     */     }
/*     */ 
/*     */     
/*     */     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/*  92 */       return visit(file, attrs, true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  99 */   public static final EnumSet<FileVisitOption> EMPTY_FileVisitOption = EnumSet.noneOf(FileVisitOption.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void create(ArchiveOutputStream target, File directory) throws IOException, ArchiveException {
/* 110 */     create(target, directory.toPath(), EMPTY_FileVisitOption, new LinkOption[0]);
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
/*     */   public void create(ArchiveOutputStream target, Path directory, EnumSet<FileVisitOption> fileVisitOptions, LinkOption... linkOptions) throws IOException {
/* 125 */     Files.walkFileTree(directory, fileVisitOptions, 2147483647, new ArchiverFileVisitor(target, directory, linkOptions));
/*     */     
/* 127 */     target.finish();
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
/*     */   public void create(ArchiveOutputStream target, Path directory) throws IOException {
/* 139 */     create(target, directory, EMPTY_FileVisitOption, new LinkOption[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void create(SevenZOutputFile target, File directory) throws IOException {
/* 150 */     create(target, directory.toPath());
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
/*     */   public void create(final SevenZOutputFile target, final Path directory) throws IOException {
/* 163 */     Files.walkFileTree(directory, new ArchiverFileVisitor(null, directory, new LinkOption[0])
/*     */         {
/*     */           
/*     */           protected FileVisitResult visit(Path path, BasicFileAttributes attrs, boolean isFile) throws IOException
/*     */           {
/* 168 */             Objects.requireNonNull(path);
/* 169 */             Objects.requireNonNull(attrs);
/* 170 */             String name = directory.relativize(path).toString().replace('\\', '/');
/* 171 */             if (!name.isEmpty()) {
/* 172 */               SevenZArchiveEntry sevenZArchiveEntry = target.createArchiveEntry(path, (isFile || name
/* 173 */                   .endsWith("/")) ? name : (name + "/"), new LinkOption[0]);
/* 174 */               target.putArchiveEntry((ArchiveEntry)sevenZArchiveEntry);
/* 175 */               if (isFile)
/*     */               {
/* 177 */                 target.write(path, new OpenOption[0]);
/*     */               }
/* 179 */               target.closeArchiveEntry();
/*     */             } 
/* 181 */             return FileVisitResult.CONTINUE;
/*     */           }
/*     */         });
/*     */     
/* 185 */     target.finish();
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
/*     */   public void create(String format, File target, File directory) throws IOException, ArchiveException {
/* 200 */     create(format, target.toPath(), directory.toPath());
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
/*     */   @Deprecated
/*     */   public void create(String format, OutputStream target, File directory) throws IOException, ArchiveException {
/* 222 */     create(format, target, directory, CloseableConsumer.NULL_CONSUMER);
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
/*     */   public void create(String format, OutputStream target, File directory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
/* 246 */     try (CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
/* 247 */       create(c.<ArchiveOutputStream>track(ArchiveStreamFactory.DEFAULT.createArchiveOutputStream(format, target)), directory);
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
/*     */   public void create(String format, Path target, Path directory) throws IOException, ArchiveException {
/* 264 */     if (prefersSeekableByteChannel(format)) {
/* 265 */       try (SeekableByteChannel channel = FileChannel.open(target, new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING })) {
/*     */         
/* 267 */         create(format, channel, directory);
/*     */         
/*     */         return;
/*     */       } 
/*     */     }
/* 272 */     try (ArchiveOutputStream outputStream = ArchiveStreamFactory.DEFAULT.createArchiveOutputStream(format, 
/* 273 */           Files.newOutputStream(target, new OpenOption[0]))) {
/* 274 */       create(outputStream, directory, EMPTY_FileVisitOption, new LinkOption[0]);
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
/*     */   @Deprecated
/*     */   public void create(String format, SeekableByteChannel target, File directory) throws IOException, ArchiveException {
/* 297 */     create(format, target, directory, CloseableConsumer.NULL_CONSUMER);
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
/*     */   public void create(String format, SeekableByteChannel target, File directory, CloseableConsumer closeableConsumer) throws IOException, ArchiveException {
/* 321 */     try (CloseableConsumerAdapter c = new CloseableConsumerAdapter(closeableConsumer)) {
/* 322 */       if (!prefersSeekableByteChannel(format)) {
/* 323 */         create(format, c.<OutputStream>track(Channels.newOutputStream(target)), directory);
/* 324 */       } else if ("zip".equalsIgnoreCase(format)) {
/* 325 */         create((ArchiveOutputStream)c.track(new ZipArchiveOutputStream(target)), directory);
/* 326 */       } else if ("7z".equalsIgnoreCase(format)) {
/* 327 */         create(c.<SevenZOutputFile>track(new SevenZOutputFile(target)), directory);
/*     */       } else {
/*     */         
/* 330 */         throw new ArchiveException("Don't know how to handle format " + format);
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
/*     */   
/*     */   public void create(String format, SeekableByteChannel target, Path directory) throws IOException {
/* 346 */     if ("7z".equalsIgnoreCase(format)) {
/* 347 */       try (SevenZOutputFile sevenZFile = new SevenZOutputFile(target)) {
/* 348 */         create(sevenZFile, directory);
/*     */       } 
/* 350 */     } else if ("zip".equalsIgnoreCase(format)) {
/* 351 */       try (ZipArchiveOutputStream null = new ZipArchiveOutputStream(target)) {
/* 352 */         create((ArchiveOutputStream)zipArchiveOutputStream, directory, EMPTY_FileVisitOption, new LinkOption[0]);
/*     */       } 
/*     */     } else {
/* 355 */       throw new IllegalStateException(format);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean prefersSeekableByteChannel(String format) {
/* 360 */     return ("zip".equalsIgnoreCase(format) || "7z"
/* 361 */       .equalsIgnoreCase(format));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\examples\Archiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */