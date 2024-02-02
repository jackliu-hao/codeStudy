/*     */ package cn.hutool.core.io.file;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.file.visitor.CopyVisitor;
/*     */ import cn.hutool.core.io.file.visitor.DelVisitor;
/*     */ import cn.hutool.core.io.file.visitor.MoveVisitor;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.AccessDeniedException;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.FileVisitOption;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.FileVisitor;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathUtil
/*     */ {
/*     */   public static boolean isDirEmpty(Path dirPath) {
/*  52 */     try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dirPath)) {
/*  53 */       return (false == dirStream.iterator().hasNext());
/*  54 */     } catch (IOException e) {
/*  55 */       throw new IORuntimeException(e);
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
/*     */   public static List<File> loopFiles(Path path, FileFilter fileFilter) {
/*  69 */     return loopFiles(path, -1, fileFilter);
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
/*     */   public static List<File> loopFiles(Path path, int maxDepth, final FileFilter fileFilter) {
/*  83 */     final List<File> fileList = new ArrayList<>();
/*     */     
/*  85 */     if (null == path || false == Files.exists(path, new LinkOption[0]))
/*  86 */       return fileList; 
/*  87 */     if (false == isDirectory(path)) {
/*  88 */       File file = path.toFile();
/*  89 */       if (null == fileFilter || fileFilter.accept(file)) {
/*  90 */         fileList.add(file);
/*     */       }
/*  92 */       return fileList;
/*     */     } 
/*     */     
/*  95 */     walkFiles(path, maxDepth, new SimpleFileVisitor<Path>()
/*     */         {
/*     */           public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
/*     */           {
/*  99 */             File file = path.toFile();
/* 100 */             if (null == fileFilter || fileFilter.accept(file)) {
/* 101 */               fileList.add(file);
/*     */             }
/* 103 */             return FileVisitResult.CONTINUE;
/*     */           }
/*     */         });
/*     */     
/* 107 */     return fileList;
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
/*     */   public static void walkFiles(Path start, FileVisitor<? super Path> visitor) {
/* 119 */     walkFiles(start, -1, visitor);
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
/*     */   public static void walkFiles(Path start, int maxDepth, FileVisitor<? super Path> visitor) {
/* 132 */     if (maxDepth < 0)
/*     */     {
/* 134 */       maxDepth = Integer.MAX_VALUE;
/*     */     }
/*     */     
/*     */     try {
/* 138 */       Files.walkFileTree(start, EnumSet.noneOf(FileVisitOption.class), maxDepth, visitor);
/* 139 */     } catch (IOException e) {
/* 140 */       throw new IORuntimeException(e);
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
/*     */   public static boolean del(Path path) throws IORuntimeException {
/* 155 */     if (Files.notExists(path, new LinkOption[0])) {
/* 156 */       return true;
/*     */     }
/*     */     
/*     */     try {
/* 160 */       if (isDirectory(path)) {
/* 161 */         Files.walkFileTree(path, (FileVisitor<? super Path>)DelVisitor.INSTANCE);
/*     */       } else {
/* 163 */         delFile(path);
/*     */       } 
/* 165 */     } catch (IOException e) {
/* 166 */       throw new IORuntimeException(e);
/*     */     } 
/* 168 */     return true;
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
/*     */   public static Path copyFile(Path src, Path dest, StandardCopyOption... options) throws IORuntimeException {
/* 182 */     return copyFile(src, dest, (CopyOption[])options);
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
/*     */   public static Path copyFile(Path src, Path target, CopyOption... options) throws IORuntimeException {
/* 197 */     Assert.notNull(src, "Source File is null !", new Object[0]);
/* 198 */     Assert.notNull(target, "Destination File or directory is null !", new Object[0]);
/*     */     
/* 200 */     Path targetPath = isDirectory(target) ? target.resolve(src.getFileName()) : target;
/*     */     
/* 202 */     mkParentDirs(targetPath);
/*     */     try {
/* 204 */       return Files.copy(src, targetPath, options);
/* 205 */     } catch (IOException e) {
/* 206 */       throw new IORuntimeException(e);
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
/*     */   public static Path copy(Path src, Path target, CopyOption... options) throws IORuntimeException {
/* 227 */     Assert.notNull(src, "Src path must be not null !", new Object[0]);
/* 228 */     Assert.notNull(target, "Target path must be not null !", new Object[0]);
/*     */     
/* 230 */     if (isDirectory(src)) {
/* 231 */       return copyContent(src, target.resolve(src.getFileName()), options);
/*     */     }
/* 233 */     return copyFile(src, target, options);
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
/*     */   public static Path copyContent(Path src, Path target, CopyOption... options) throws IORuntimeException {
/* 251 */     Assert.notNull(src, "Src path must be not null !", new Object[0]);
/* 252 */     Assert.notNull(target, "Target path must be not null !", new Object[0]);
/*     */     
/*     */     try {
/* 255 */       Files.walkFileTree(src, (FileVisitor<? super Path>)new CopyVisitor(src, target, options));
/* 256 */     } catch (IOException e) {
/* 257 */       throw new IORuntimeException(e);
/*     */     } 
/* 259 */     return target;
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
/*     */   public static boolean isDirectory(Path path) {
/* 271 */     return isDirectory(path, false);
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
/*     */   public static boolean isDirectory(Path path, boolean isFollowLinks) {
/* 283 */     if (null == path) {
/* 284 */       return false;
/*     */     }
/* 286 */     (new LinkOption[1])[0] = LinkOption.NOFOLLOW_LINKS; LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[1];
/* 287 */     return Files.isDirectory(path, options);
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
/*     */   public static Path getPathEle(Path path, int index) {
/* 299 */     return subPath(path, index, (index == -1) ? path.getNameCount() : (index + 1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Path getLastPathEle(Path path) {
/* 310 */     return getPathEle(path, path.getNameCount() - 1);
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
/*     */   public static Path subPath(Path path, int fromIndex, int toIndex) {
/* 323 */     if (null == path) {
/* 324 */       return null;
/*     */     }
/* 326 */     int len = path.getNameCount();
/*     */     
/* 328 */     if (fromIndex < 0) {
/* 329 */       fromIndex = len + fromIndex;
/* 330 */       if (fromIndex < 0) {
/* 331 */         fromIndex = 0;
/*     */       }
/* 333 */     } else if (fromIndex > len) {
/* 334 */       fromIndex = len;
/*     */     } 
/*     */     
/* 337 */     if (toIndex < 0) {
/* 338 */       toIndex = len + toIndex;
/* 339 */       if (toIndex < 0) {
/* 340 */         toIndex = len;
/*     */       }
/* 342 */     } else if (toIndex > len) {
/* 343 */       toIndex = len;
/*     */     } 
/*     */     
/* 346 */     if (toIndex < fromIndex) {
/* 347 */       int tmp = fromIndex;
/* 348 */       fromIndex = toIndex;
/* 349 */       toIndex = tmp;
/*     */     } 
/*     */     
/* 352 */     if (fromIndex == toIndex) {
/* 353 */       return null;
/*     */     }
/* 355 */     return path.subpath(fromIndex, toIndex);
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
/*     */   public static BasicFileAttributes getAttributes(Path path, boolean isFollowLinks) throws IORuntimeException {
/* 367 */     if (null == path) {
/* 368 */       return null;
/*     */     }
/*     */     
/* 371 */     (new LinkOption[1])[0] = LinkOption.NOFOLLOW_LINKS; LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[1];
/*     */     try {
/* 373 */       return Files.readAttributes(path, BasicFileAttributes.class, options);
/* 374 */     } catch (IOException e) {
/* 375 */       throw new IORuntimeException(e);
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
/*     */   public static BufferedInputStream getInputStream(Path path) throws IORuntimeException {
/*     */     InputStream in;
/*     */     try {
/* 390 */       in = Files.newInputStream(path, new java.nio.file.OpenOption[0]);
/* 391 */     } catch (IOException e) {
/* 392 */       throw new IORuntimeException(e);
/*     */     } 
/* 394 */     return IoUtil.toBuffered(in);
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
/*     */   public static BufferedReader getUtf8Reader(Path path) throws IORuntimeException {
/* 406 */     return getReader(path, CharsetUtil.CHARSET_UTF_8);
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
/*     */   public static BufferedReader getReader(Path path, Charset charset) throws IORuntimeException {
/* 419 */     return IoUtil.getReader(getInputStream(path), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] readBytes(Path path) {
/*     */     try {
/* 431 */       return Files.readAllBytes(path);
/* 432 */     } catch (IOException e) {
/* 433 */       throw new IORuntimeException(e);
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
/*     */   public static BufferedOutputStream getOutputStream(Path path) throws IORuntimeException {
/*     */     OutputStream in;
/*     */     try {
/* 448 */       in = Files.newOutputStream(path, new java.nio.file.OpenOption[0]);
/* 449 */     } catch (IOException e) {
/* 450 */       throw new IORuntimeException(e);
/*     */     } 
/* 452 */     return IoUtil.toBuffered(in);
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
/*     */   public static Path rename(Path path, String newName, boolean isOverride) {
/* 469 */     return move(path, path.resolveSibling(newName), isOverride);
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
/*     */   public static Path move(Path src, Path target, boolean isOverride) {
/* 488 */     Assert.notNull(src, "Src path must be not null !", new Object[0]);
/* 489 */     Assert.notNull(target, "Target path must be not null !", new Object[0]);
/*     */     
/* 491 */     if (isDirectory(target)) {
/* 492 */       target = target.resolve(src.getFileName());
/*     */     }
/* 494 */     return moveContent(src, target, isOverride);
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
/*     */   public static Path moveContent(Path src, Path target, boolean isOverride) {
/* 511 */     Assert.notNull(src, "Src path must be not null !", new Object[0]);
/* 512 */     Assert.notNull(target, "Target path must be not null !", new Object[0]);
/* 513 */     (new CopyOption[1])[0] = StandardCopyOption.REPLACE_EXISTING; CopyOption[] options = isOverride ? new CopyOption[1] : new CopyOption[0];
/*     */ 
/*     */     
/* 516 */     mkParentDirs(target);
/*     */     try {
/* 518 */       return Files.move(src, target, options);
/* 519 */     } catch (IOException e) {
/* 520 */       if (e instanceof java.nio.file.FileAlreadyExistsException)
/*     */       {
/*     */         
/* 523 */         throw new IORuntimeException(e);
/*     */       }
/*     */       
/*     */       try {
/* 527 */         Files.walkFileTree(src, (FileVisitor<? super Path>)new MoveVisitor(src, target, options));
/*     */         
/* 529 */         del(src);
/* 530 */       } catch (IOException e2) {
/* 531 */         throw new IORuntimeException(e2);
/*     */       } 
/* 533 */       return target;
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
/*     */   public static boolean equals(Path file1, Path file2) throws IORuntimeException {
/*     */     try {
/* 550 */       return Files.isSameFile(file1, file2);
/* 551 */     } catch (IOException e) {
/* 552 */       throw new IORuntimeException(e);
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
/*     */   public static boolean isFile(Path path, boolean isFollowLinks) {
/* 565 */     if (null == path) {
/* 566 */       return false;
/*     */     }
/* 568 */     (new LinkOption[1])[0] = LinkOption.NOFOLLOW_LINKS; LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[1];
/* 569 */     return Files.isRegularFile(path, options);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSymlink(Path path) {
/* 580 */     return Files.isSymbolicLink(path);
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
/*     */   public static boolean exists(Path path, boolean isFollowLinks) {
/* 592 */     (new LinkOption[1])[0] = LinkOption.NOFOLLOW_LINKS; LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[1];
/* 593 */     return Files.exists(path, options);
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
/*     */   public static boolean isSub(Path parent, Path sub) {
/* 605 */     return toAbsNormal(sub).startsWith(toAbsNormal(parent));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Path toAbsNormal(Path path) {
/* 616 */     Assert.notNull(path);
/* 617 */     return path.toAbsolutePath().normalize();
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
/*     */   public static String getMimeType(Path file) {
/*     */     try {
/* 630 */       return Files.probeContentType(file);
/* 631 */     } catch (IOException e) {
/* 632 */       throw new IORuntimeException(e);
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
/*     */   public static Path mkdir(Path dir) {
/* 644 */     if (null != dir && false == exists(dir, false)) {
/*     */       try {
/* 646 */         Files.createDirectories(dir, (FileAttribute<?>[])new FileAttribute[0]);
/* 647 */       } catch (IOException e) {
/* 648 */         throw new IORuntimeException(e);
/*     */       } 
/*     */     }
/* 651 */     return dir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Path mkParentDirs(Path path) {
/* 662 */     return mkdir(path.getParent());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getName(Path path) {
/* 673 */     if (null == path) {
/* 674 */       return null;
/*     */     }
/* 676 */     return path.getFileName().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void delFile(Path path) throws IOException {
/*     */     try {
/* 688 */       Files.delete(path);
/* 689 */     } catch (AccessDeniedException e) {
/*     */       
/* 691 */       if (false == path.toFile().delete())
/* 692 */         throw e; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\PathUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */