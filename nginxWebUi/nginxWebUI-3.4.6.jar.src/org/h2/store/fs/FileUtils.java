/*     */ package org.h2.store.fs;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileUtils
/*     */ {
/*  38 */   public static final Set<? extends OpenOption> R = Collections.singleton(StandardOpenOption.READ);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   public static final Set<? extends OpenOption> RW = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   public static final Set<? extends OpenOption> RWS = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.SYNC));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   public static final Set<? extends OpenOption> RWD = Collections.unmodifiableSet(EnumSet.of(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.DSYNC));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   public static final FileAttribute<?>[] NO_ATTRIBUTES = (FileAttribute<?>[])new FileAttribute[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean exists(String paramString) {
/*  74 */     return FilePath.get(paramString).exists();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void createDirectory(String paramString) {
/*  85 */     FilePath.get(paramString).createDirectory();
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
/*     */   public static boolean createFile(String paramString) {
/*  97 */     return FilePath.get(paramString).createFile();
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
/*     */   public static void delete(String paramString) {
/* 109 */     FilePath.get(paramString).delete();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toRealPath(String paramString) {
/* 120 */     return FilePath.get(paramString).toRealPath().toString();
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
/*     */   public static String getParent(String paramString) {
/* 132 */     FilePath filePath = FilePath.get(paramString).getParent();
/* 133 */     return (filePath == null) ? null : filePath.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAbsolute(String paramString) {
/* 144 */     return (FilePath.get(paramString).isAbsolute() || paramString
/*     */ 
/*     */       
/* 147 */       .startsWith(File.separator) || paramString
/*     */       
/* 149 */       .startsWith("/"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void move(String paramString1, String paramString2) {
/* 160 */     FilePath.get(paramString1).moveTo(FilePath.get(paramString2), false);
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
/*     */   public static void moveAtomicReplace(String paramString1, String paramString2) {
/* 172 */     FilePath.get(paramString1).moveTo(FilePath.get(paramString2), true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getName(String paramString) {
/* 183 */     return FilePath.get(paramString).getName();
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
/*     */   public static List<String> newDirectoryStream(String paramString) {
/* 195 */     List<FilePath> list = FilePath.get(paramString).newDirectoryStream();
/* 196 */     int i = list.size();
/* 197 */     ArrayList<String> arrayList = new ArrayList(i);
/* 198 */     for (FilePath filePath : list) {
/* 199 */       arrayList.add(filePath.toString());
/*     */     }
/* 201 */     return arrayList;
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
/*     */   public static long lastModified(String paramString) {
/* 214 */     return FilePath.get(paramString).lastModified();
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
/*     */   public static long size(String paramString) {
/* 227 */     return FilePath.get(paramString).size();
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
/*     */   public static boolean isDirectory(String paramString) {
/* 239 */     return FilePath.get(paramString).isDirectory();
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
/*     */   public static FileChannel open(String paramString1, String paramString2) throws IOException {
/* 254 */     return FilePath.get(paramString1).open(paramString2);
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
/*     */   public static InputStream newInputStream(String paramString) throws IOException {
/* 267 */     return FilePath.get(paramString).newInputStream();
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
/*     */   public static BufferedReader newBufferedReader(String paramString, Charset paramCharset) throws IOException {
/* 281 */     return new BufferedReader(new InputStreamReader(newInputStream(paramString), paramCharset), 4096);
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
/*     */   public static OutputStream newOutputStream(String paramString, boolean paramBoolean) throws IOException {
/* 296 */     return FilePath.get(paramString).newOutputStream(paramBoolean);
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
/*     */   public static boolean canWrite(String paramString) {
/* 308 */     return FilePath.get(paramString).canWrite();
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
/*     */   public static boolean setReadOnly(String paramString) {
/* 320 */     return FilePath.get(paramString).setReadOnly();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String unwrap(String paramString) {
/* 331 */     return FilePath.get(paramString).unwrap().toString();
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
/*     */   public static void deleteRecursive(String paramString, boolean paramBoolean) {
/* 343 */     if (exists(paramString)) {
/* 344 */       if (isDirectory(paramString)) {
/* 345 */         for (String str : newDirectoryStream(paramString)) {
/* 346 */           deleteRecursive(str, paramBoolean);
/*     */         }
/*     */       }
/* 349 */       if (paramBoolean) {
/* 350 */         tryDelete(paramString);
/*     */       } else {
/* 352 */         delete(paramString);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void createDirectories(String paramString) {
/* 363 */     if (paramString != null) {
/* 364 */       if (exists(paramString)) {
/* 365 */         if (!isDirectory(paramString))
/*     */         {
/* 367 */           createDirectory(paramString);
/*     */         }
/*     */       } else {
/* 370 */         String str = getParent(paramString);
/* 371 */         createDirectories(str);
/* 372 */         createDirectory(paramString);
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
/*     */   public static boolean tryDelete(String paramString) {
/*     */     try {
/* 385 */       FilePath.get(paramString).delete();
/* 386 */       return true;
/* 387 */     } catch (Exception exception) {
/* 388 */       return false;
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
/*     */   public static String createTempFile(String paramString1, String paramString2, boolean paramBoolean) throws IOException {
/* 404 */     return FilePath.get(paramString1).createTempFile(paramString2, paramBoolean).toString();
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
/*     */   public static void readFully(FileChannel paramFileChannel, ByteBuffer paramByteBuffer) throws IOException {
/*     */     do {
/* 418 */       int i = paramFileChannel.read(paramByteBuffer);
/* 419 */       if (i < 0) {
/* 420 */         throw new EOFException();
/*     */       }
/* 422 */     } while (paramByteBuffer.remaining() > 0);
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
/*     */   public static void writeFully(FileChannel paramFileChannel, ByteBuffer paramByteBuffer) throws IOException {
/*     */     do {
/* 435 */       paramFileChannel.write(paramByteBuffer);
/* 436 */     } while (paramByteBuffer.remaining() > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set<? extends OpenOption> modeToOptions(String paramString) {
/*     */     Set<? extends OpenOption> set;
/* 447 */     switch (paramString) {
/*     */       case "r":
/* 449 */         set = R;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 463 */         return set;case "rw": set = RW; return set;case "rws": set = RWS; return set;case "rwd": set = RWD; return set;
/*     */     } 
/*     */     throw new IllegalArgumentException(paramString);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\FileUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */