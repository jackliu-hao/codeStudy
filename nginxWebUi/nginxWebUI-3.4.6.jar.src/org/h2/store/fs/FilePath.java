/*     */ package org.h2.store.fs;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.h2.store.fs.disk.FilePathDisk;
/*     */ import org.h2.util.MathUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class FilePath
/*     */ {
/*     */   private static final FilePath defaultProvider;
/*     */   private static final ConcurrentHashMap<String, FilePath> providers;
/*     */   private static String tempRandom;
/*     */   private static long tempSequence;
/*     */   public String name;
/*     */   
/*     */   static {
/*  43 */     FilePath filePath = null;
/*  44 */     ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
/*  45 */     for (String str : new String[] { "org.h2.store.fs.disk.FilePathDisk", "org.h2.store.fs.mem.FilePathMem", "org.h2.store.fs.mem.FilePathMemLZF", "org.h2.store.fs.niomem.FilePathNioMem", "org.h2.store.fs.niomem.FilePathNioMemLZF", "org.h2.store.fs.split.FilePathSplit", "org.h2.store.fs.niomapped.FilePathNioMapped", "org.h2.store.fs.async.FilePathAsync", "org.h2.store.fs.zip.FilePathZip", "org.h2.store.fs.retry.FilePathRetryOnInterrupt" }) {
/*     */ 
/*     */       
/*     */       try {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  58 */         FilePath filePath1 = Class.forName(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*  59 */         concurrentHashMap.put(filePath1.getScheme(), filePath1);
/*  60 */         if (filePath1.getClass() == FilePathDisk.class) {
/*  61 */           concurrentHashMap.put("nio", filePath1);
/*     */         }
/*  63 */         if (filePath == null) {
/*  64 */           filePath = filePath1;
/*     */         }
/*  66 */       } catch (Exception exception) {}
/*     */     } 
/*     */ 
/*     */     
/*  70 */     defaultProvider = filePath;
/*  71 */     providers = (ConcurrentHashMap)concurrentHashMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FilePath get(String paramString) {
/*  82 */     paramString = paramString.replace('\\', '/');
/*  83 */     int i = paramString.indexOf(':');
/*  84 */     if (i < 2)
/*     */     {
/*     */       
/*  87 */       return defaultProvider.getPath(paramString);
/*     */     }
/*  89 */     String str = paramString.substring(0, i);
/*  90 */     FilePath filePath = providers.get(str);
/*  91 */     if (filePath == null)
/*     */     {
/*  93 */       filePath = defaultProvider;
/*     */     }
/*  95 */     return filePath.getPath(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void register(FilePath paramFilePath) {
/* 104 */     providers.put(paramFilePath.getScheme(), paramFilePath);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void unregister(FilePath paramFilePath) {
/* 113 */     providers.remove(paramFilePath.getScheme());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 212 */     int i = Math.max(this.name.indexOf(':'), this.name.lastIndexOf('/'));
/* 213 */     return (i < 0) ? this.name : this.name.substring(i + 1);
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
/*     */   public OutputStream newOutputStream(boolean paramBoolean) throws IOException {
/* 225 */     return newFileChannelOutputStream(open("rw"), paramBoolean);
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
/*     */   public static final OutputStream newFileChannelOutputStream(FileChannel paramFileChannel, boolean paramBoolean) throws IOException {
/* 238 */     if (paramBoolean) {
/* 239 */       paramFileChannel.position(paramFileChannel.size());
/*     */     } else {
/* 241 */       paramFileChannel.position(0L);
/* 242 */       paramFileChannel.truncate(0L);
/*     */     } 
/* 244 */     return Channels.newOutputStream(paramFileChannel);
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
/*     */   public InputStream newInputStream() throws IOException {
/* 263 */     return Channels.newInputStream(open("r"));
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
/*     */   public FilePath createTempFile(String paramString, boolean paramBoolean) throws IOException {
/*     */     FilePath filePath;
/*     */     while (true) {
/* 284 */       filePath = getPath(this.name + getNextTempFileNamePart(false) + paramString);
/* 285 */       if (filePath.exists() || !filePath.createFile()) {
/*     */         
/* 287 */         getNextTempFileNamePart(true); continue;
/*     */       }  break;
/*     */     } 
/* 290 */     filePath.open("rw").close();
/* 291 */     return filePath;
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
/*     */   private static synchronized String getNextTempFileNamePart(boolean paramBoolean) {
/* 303 */     if (paramBoolean || tempRandom == null) {
/* 304 */       tempRandom = MathUtils.randomInt(2147483647) + ".";
/*     */     }
/* 306 */     return tempRandom + tempSequence++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 317 */     return this.name;
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
/*     */   public FilePath unwrap() {
/* 347 */     return this;
/*     */   }
/*     */   
/*     */   public abstract long size();
/*     */   
/*     */   public abstract void moveTo(FilePath paramFilePath, boolean paramBoolean);
/*     */   
/*     */   public abstract boolean createFile();
/*     */   
/*     */   public abstract boolean exists();
/*     */   
/*     */   public abstract void delete();
/*     */   
/*     */   public abstract List<FilePath> newDirectoryStream();
/*     */   
/*     */   public abstract FilePath toRealPath();
/*     */   
/*     */   public abstract FilePath getParent();
/*     */   
/*     */   public abstract boolean isDirectory();
/*     */   
/*     */   public abstract boolean isAbsolute();
/*     */   
/*     */   public abstract long lastModified();
/*     */   
/*     */   public abstract boolean canWrite();
/*     */   
/*     */   public abstract void createDirectory();
/*     */   
/*     */   public abstract FileChannel open(String paramString) throws IOException;
/*     */   
/*     */   public abstract boolean setReadOnly();
/*     */   
/*     */   public abstract String getScheme();
/*     */   
/*     */   public abstract FilePath getPath(String paramString);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\FilePath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */