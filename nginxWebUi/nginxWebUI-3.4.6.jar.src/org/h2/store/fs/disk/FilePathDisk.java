/*     */ package org.h2.store.fs.disk;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.AtomicMoveNotSupportedException;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.DirectoryNotEmptyException;
/*     */ import java.nio.file.FileAlreadyExistsException;
/*     */ import java.nio.file.FileStore;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.NoSuchFileException;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.DosFileAttributeView;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.nio.file.attribute.PosixFileAttributeView;
/*     */ import java.nio.file.attribute.PosixFilePermission;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Stream;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilePathDisk
/*     */   extends FilePath
/*     */ {
/*     */   private static final String CLASSPATH_PREFIX = "classpath:";
/*     */   
/*     */   public FilePathDisk getPath(String paramString) {
/*  52 */     FilePathDisk filePathDisk = new FilePathDisk();
/*  53 */     filePathDisk.name = translateFileName(paramString);
/*  54 */     return filePathDisk;
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/*  59 */     if (this.name.startsWith("classpath:")) {
/*     */       try {
/*  61 */         String str = this.name.substring("classpath:".length());
/*     */         
/*  63 */         if (!str.startsWith("/")) {
/*  64 */           str = "/" + str;
/*     */         }
/*  66 */         URL uRL = getClass().getResource(str);
/*  67 */         if (uRL != null) {
/*  68 */           return Files.size(Paths.get(uRL.toURI()));
/*     */         }
/*  70 */         return 0L;
/*     */       }
/*  72 */       catch (Exception exception) {
/*  73 */         return 0L;
/*     */       } 
/*     */     }
/*     */     try {
/*  77 */       return Files.size(Paths.get(this.name, new String[0]));
/*  78 */     } catch (IOException iOException) {
/*  79 */       return 0L;
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
/*     */   protected static String translateFileName(String paramString) {
/*  91 */     paramString = paramString.replace('\\', '/');
/*  92 */     if (paramString.startsWith("file:")) {
/*  93 */       paramString = paramString.substring(5);
/*  94 */     } else if (paramString.startsWith("nio:")) {
/*  95 */       paramString = paramString.substring(4);
/*     */     } 
/*  97 */     return expandUserHomeDirectory(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String expandUserHomeDirectory(String paramString) {
/* 108 */     if (paramString.startsWith("~") && (paramString.length() == 1 || paramString
/* 109 */       .startsWith("~/"))) {
/* 110 */       String str = SysProperties.USER_HOME;
/* 111 */       paramString = str + paramString.substring(1);
/*     */     } 
/* 113 */     return paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveTo(FilePath paramFilePath, boolean paramBoolean) {
/* 118 */     Path path1 = Paths.get(this.name, new String[0]);
/* 119 */     Path path2 = Paths.get(paramFilePath.name, new String[0]);
/* 120 */     if (!Files.exists(path1, new java.nio.file.LinkOption[0])) {
/* 121 */       throw DbException.get(90024, new String[] { this.name + " (not found)", paramFilePath.name });
/*     */     }
/* 123 */     if (paramBoolean) {
/*     */       try {
/* 125 */         Files.move(path1, path2, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE });
/*     */         return;
/* 127 */       } catch (AtomicMoveNotSupportedException atomicMoveNotSupportedException) {
/*     */       
/* 129 */       } catch (IOException iOException) {
/* 130 */         throw DbException.get(90024, iOException, new String[] { this.name, paramFilePath.name });
/*     */       } 
/*     */     }
/* 133 */     (new CopyOption[1])[0] = StandardCopyOption.REPLACE_EXISTING; CopyOption[] arrayOfCopyOption = paramBoolean ? new CopyOption[1] : new CopyOption[0];
/*     */ 
/*     */     
/*     */     try {
/* 137 */       Files.move(path1, path2, arrayOfCopyOption);
/* 138 */     } catch (FileAlreadyExistsException fileAlreadyExistsException) {
/* 139 */       throw DbException.get(90024, new String[] { this.name, paramFilePath + " (exists)" });
/* 140 */     } catch (IOException iOException2) {
/* 141 */       IOException iOException1 = iOException2;
/* 142 */       for (byte b = 0; b < SysProperties.MAX_FILE_RETRY; b++) {
/* 143 */         IOUtils.trace("rename", this.name + " >" + paramFilePath, null);
/*     */         try {
/* 145 */           Files.move(path1, path2, arrayOfCopyOption);
/*     */           return;
/* 147 */         } catch (FileAlreadyExistsException fileAlreadyExistsException) {
/* 148 */           throw DbException.get(90024, new String[] { this.name, paramFilePath + " (exists)" });
/* 149 */         } catch (IOException iOException) {
/* 150 */           iOException1 = iOException2;
/*     */           
/* 152 */           wait(b);
/*     */         } 
/* 154 */       }  throw DbException.get(90024, iOException1, new String[] { this.name, paramFilePath.name });
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void wait(int paramInt) {
/* 159 */     if (paramInt == 8) {
/* 160 */       System.gc();
/*     */     }
/*     */     
/*     */     try {
/* 164 */       long l = Math.min(256, paramInt * paramInt);
/* 165 */       Thread.sleep(l);
/* 166 */     } catch (InterruptedException interruptedException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean createFile() {
/* 173 */     Path path = Paths.get(this.name, new String[0]);
/* 174 */     for (byte b = 0; b < SysProperties.MAX_FILE_RETRY; b++) {
/*     */       try {
/* 176 */         Files.createFile(path, (FileAttribute<?>[])new FileAttribute[0]);
/* 177 */         return true;
/* 178 */       } catch (FileAlreadyExistsException fileAlreadyExistsException) {
/* 179 */         return false;
/* 180 */       } catch (IOException iOException) {
/*     */         
/* 182 */         wait(b);
/*     */       } 
/*     */     } 
/* 185 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean exists() {
/* 190 */     return Files.exists(Paths.get(this.name, new String[0]), new java.nio.file.LinkOption[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/* 195 */     Path path = Paths.get(this.name, new String[0]);
/* 196 */     IOException iOException = null;
/* 197 */     for (byte b = 0; b < SysProperties.MAX_FILE_RETRY; b++) {
/* 198 */       IOUtils.trace("delete", this.name, null);
/*     */       try {
/* 200 */         Files.deleteIfExists(path);
/*     */         return;
/* 202 */       } catch (DirectoryNotEmptyException directoryNotEmptyException) {
/* 203 */         throw DbException.get(90025, directoryNotEmptyException, new String[] { this.name });
/* 204 */       } catch (IOException iOException1) {
/* 205 */         iOException = iOException1;
/*     */         
/* 207 */         wait(b);
/*     */       } 
/* 209 */     }  throw DbException.get(90025, iOException, new String[] { this.name });
/*     */   }
/*     */ 
/*     */   
/*     */   public List<FilePath> newDirectoryStream() {
/* 214 */     try (Stream<Path> null = Files.list(Paths.get(this.name, new String[0]).toRealPath(new java.nio.file.LinkOption[0]))) {
/* 215 */       return stream.<List>collect(ArrayList::new, (paramArrayList, paramPath) -> paramArrayList.add(getPath(paramPath.toString())), ArrayList::addAll);
/* 216 */     } catch (NoSuchFileException noSuchFileException) {
/* 217 */       return Collections.emptyList();
/* 218 */     } catch (IOException iOException) {
/* 219 */       throw DbException.convertIOException(iOException, this.name);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite() {
/*     */     try {
/* 226 */       return Files.isWritable(Paths.get(this.name, new String[0]));
/* 227 */     } catch (Exception exception) {
/*     */       
/* 229 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setReadOnly() {
/* 235 */     Path path = Paths.get(this.name, new String[0]);
/*     */     try {
/* 237 */       FileStore fileStore = Files.getFileStore(path);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 244 */       if (fileStore.supportsFileAttributeView((Class)PosixFileAttributeView.class)) {
/* 245 */         HashSet<PosixFilePermission> hashSet = new HashSet();
/* 246 */         for (PosixFilePermission posixFilePermission : Files.getPosixFilePermissions(path, new java.nio.file.LinkOption[0])) {
/* 247 */           switch (posixFilePermission) {
/*     */             case OWNER_WRITE:
/*     */             case GROUP_WRITE:
/*     */             case OTHERS_WRITE:
/*     */               continue;
/*     */           } 
/* 253 */           hashSet.add(posixFilePermission);
/*     */         } 
/*     */         
/* 256 */         Files.setPosixFilePermissions(path, hashSet);
/* 257 */       } else if (fileStore.supportsFileAttributeView((Class)DosFileAttributeView.class)) {
/* 258 */         Files.setAttribute(path, "dos:readonly", Boolean.valueOf(true), new java.nio.file.LinkOption[0]);
/*     */       } else {
/* 260 */         return false;
/*     */       } 
/* 262 */       return true;
/* 263 */     } catch (IOException iOException) {
/* 264 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePathDisk toRealPath() {
/* 270 */     Path path = Paths.get(this.name, new String[0]);
/*     */     try {
/* 272 */       return getPath(path.toRealPath(new java.nio.file.LinkOption[0]).toString());
/* 273 */     } catch (IOException iOException) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 278 */       return getPath(toRealPath(path.toAbsolutePath().normalize()).toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Path toRealPath(Path paramPath) {
/* 283 */     Path path = paramPath.getParent();
/* 284 */     if (path == null) {
/* 285 */       return paramPath;
/*     */     }
/*     */     try {
/* 288 */       path = path.toRealPath(new java.nio.file.LinkOption[0]);
/* 289 */     } catch (IOException iOException) {
/* 290 */       path = toRealPath(path);
/*     */     } 
/* 292 */     return path.resolve(paramPath.getFileName());
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath getParent() {
/* 297 */     Path path = Paths.get(this.name, new String[0]).getParent();
/* 298 */     return (path == null) ? null : getPath(path.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 303 */     return Files.isDirectory(Paths.get(this.name, new String[0]), new java.nio.file.LinkOption[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAbsolute() {
/* 308 */     return Paths.get(this.name, new String[0]).isAbsolute();
/*     */   }
/*     */ 
/*     */   
/*     */   public long lastModified() {
/*     */     try {
/* 314 */       return Files.getLastModifiedTime(Paths.get(this.name, new String[0]), new java.nio.file.LinkOption[0]).toMillis();
/* 315 */     } catch (IOException iOException) {
/* 316 */       return 0L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void createDirectory() {
/* 322 */     Path path = Paths.get(this.name, new String[0]);
/*     */     try {
/* 324 */       Files.createDirectory(path, (FileAttribute<?>[])new FileAttribute[0]);
/* 325 */     } catch (FileAlreadyExistsException fileAlreadyExistsException) {
/* 326 */       throw DbException.get(90062, this.name + " (a file with this name already exists)");
/* 327 */     } catch (IOException iOException1) {
/* 328 */       IOException iOException2 = iOException1;
/* 329 */       for (byte b = 0; b < SysProperties.MAX_FILE_RETRY; b++) {
/* 330 */         if (Files.isDirectory(path, new java.nio.file.LinkOption[0])) {
/*     */           return;
/*     */         }
/*     */         try {
/* 334 */           Files.createDirectory(path, (FileAttribute<?>[])new FileAttribute[0]);
/* 335 */         } catch (FileAlreadyExistsException fileAlreadyExistsException) {
/* 336 */           throw DbException.get(90062, this.name + " (a file with this name already exists)");
/*     */         }
/* 338 */         catch (IOException iOException) {
/* 339 */           iOException2 = iOException;
/*     */         } 
/* 341 */         wait(b);
/*     */       } 
/* 343 */       throw DbException.get(90062, iOException2, new String[] { this.name });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public OutputStream newOutputStream(boolean paramBoolean) throws IOException {
/* 349 */     Path path = Paths.get(this.name, new String[0]);
/* 350 */     (new OpenOption[2])[0] = StandardOpenOption.CREATE; (new OpenOption[2])[1] = StandardOpenOption.APPEND; OpenOption[] arrayOfOpenOption = paramBoolean ? new OpenOption[2] : new OpenOption[0];
/*     */ 
/*     */     
/*     */     try {
/* 354 */       Path path1 = path.getParent();
/* 355 */       if (path1 != null) {
/* 356 */         Files.createDirectories(path1, (FileAttribute<?>[])new FileAttribute[0]);
/*     */       }
/* 358 */       OutputStream outputStream = Files.newOutputStream(path, arrayOfOpenOption);
/* 359 */       IOUtils.trace("openFileOutputStream", this.name, outputStream);
/* 360 */       return outputStream;
/* 361 */     } catch (IOException iOException) {
/* 362 */       freeMemoryAndFinalize();
/* 363 */       return Files.newOutputStream(path, arrayOfOpenOption);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream newInputStream() throws IOException {
/* 369 */     if (this.name.matches("[a-zA-Z]{2,19}:.*")) {
/*     */ 
/*     */ 
/*     */       
/* 373 */       if (this.name.startsWith("classpath:")) {
/* 374 */         String str = this.name.substring("classpath:".length());
/*     */         
/* 376 */         if (!str.startsWith("/")) {
/* 377 */           str = "/" + str;
/*     */         }
/* 379 */         InputStream inputStream1 = getClass().getResourceAsStream(str);
/* 380 */         if (inputStream1 == null)
/*     */         {
/*     */           
/* 383 */           inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(str.substring(1));
/*     */         }
/* 385 */         if (inputStream1 == null) {
/* 386 */           throw new FileNotFoundException("resource " + str);
/*     */         }
/* 388 */         return inputStream1;
/*     */       } 
/*     */       
/* 391 */       URL uRL = new URL(this.name);
/* 392 */       return uRL.openStream();
/*     */     } 
/* 394 */     InputStream inputStream = Files.newInputStream(Paths.get(this.name, new String[0]), new OpenOption[0]);
/* 395 */     IOUtils.trace("openFileInputStream", this.name, inputStream);
/* 396 */     return inputStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void freeMemoryAndFinalize() {
/* 404 */     IOUtils.trace("freeMemoryAndFinalize", null, null);
/* 405 */     Runtime runtime = Runtime.getRuntime();
/* 406 */     long l = runtime.freeMemory();
/* 407 */     for (byte b = 0; b < 16; b++) {
/* 408 */       runtime.gc();
/* 409 */       long l1 = runtime.freeMemory();
/* 410 */       runtime.runFinalization();
/* 411 */       if (l1 == l) {
/*     */         break;
/*     */       }
/* 414 */       l = l1;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel open(String paramString) throws IOException {
/* 420 */     FileChannel fileChannel = FileChannel.open(Paths.get(this.name, new String[0]), FileUtils.modeToOptions(paramString), (FileAttribute<?>[])FileUtils.NO_ATTRIBUTES);
/* 421 */     IOUtils.trace("open", this.name, fileChannel);
/* 422 */     return fileChannel;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 427 */     return "file";
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath createTempFile(String paramString, boolean paramBoolean) throws IOException {
/* 432 */     Path path = Paths.get(this.name + '.', new String[0]).toAbsolutePath();
/* 433 */     String str = path.getFileName().toString();
/* 434 */     if (paramBoolean) {
/* 435 */       Files.createDirectories(Paths.get(System.getProperty("java.io.tmpdir", "."), new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
/* 436 */       path = Files.createTempFile(str, paramString, (FileAttribute<?>[])new FileAttribute[0]);
/*     */     } else {
/* 438 */       Path path1 = path.getParent();
/* 439 */       Files.createDirectories(path1, (FileAttribute<?>[])new FileAttribute[0]);
/* 440 */       path = Files.createTempFile(path1, str, paramString, (FileAttribute<?>[])new FileAttribute[0]);
/*     */     } 
/* 442 */     return get(path.toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\disk\FilePathDisk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */