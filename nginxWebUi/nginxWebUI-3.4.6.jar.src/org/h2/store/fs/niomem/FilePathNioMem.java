/*     */ package org.h2.store.fs.niomem;
/*     */ 
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.TreeMap;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FilePath;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilePathNioMem
/*     */   extends FilePath
/*     */ {
/*  22 */   private static final TreeMap<String, FileNioMemData> MEMORY_FILES = new TreeMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  28 */   float compressLaterCachePercent = 1.0F;
/*     */ 
/*     */   
/*     */   public FilePathNioMem getPath(String paramString) {
/*  32 */     FilePathNioMem filePathNioMem = new FilePathNioMem();
/*  33 */     filePathNioMem.name = getCanonicalPath(paramString);
/*  34 */     return filePathNioMem;
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/*  39 */     return getMemoryFile().length();
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveTo(FilePath paramFilePath, boolean paramBoolean) {
/*  44 */     synchronized (MEMORY_FILES) {
/*  45 */       if (!paramBoolean && !this.name.equals(paramFilePath.name) && MEMORY_FILES
/*  46 */         .containsKey(paramFilePath.name)) {
/*  47 */         throw DbException.get(90024, new String[] { this.name, paramFilePath + " (exists)" });
/*     */       }
/*  49 */       FileNioMemData fileNioMemData = getMemoryFile();
/*  50 */       fileNioMemData.setName(paramFilePath.name);
/*  51 */       MEMORY_FILES.remove(this.name);
/*  52 */       MEMORY_FILES.put(paramFilePath.name, fileNioMemData);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean createFile() {
/*  58 */     synchronized (MEMORY_FILES) {
/*  59 */       if (exists()) {
/*  60 */         return false;
/*     */       }
/*  62 */       getMemoryFile();
/*     */     } 
/*  64 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean exists() {
/*  69 */     if (isRoot()) {
/*  70 */       return true;
/*     */     }
/*  72 */     synchronized (MEMORY_FILES) {
/*  73 */       return (MEMORY_FILES.get(this.name) != null);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/*  79 */     if (isRoot()) {
/*     */       return;
/*     */     }
/*  82 */     synchronized (MEMORY_FILES) {
/*  83 */       MEMORY_FILES.remove(this.name);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public List<FilePath> newDirectoryStream() {
/*  89 */     ArrayList<FilePathNioMem> arrayList = new ArrayList();
/*  90 */     synchronized (MEMORY_FILES) {
/*  91 */       for (String str : MEMORY_FILES.tailMap(this.name).keySet()) {
/*  92 */         if (str.startsWith(this.name)) {
/*  93 */           arrayList.add(getPath(str));
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*  98 */       return (List)arrayList;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setReadOnly() {
/* 104 */     return getMemoryFile().setReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite() {
/* 109 */     return getMemoryFile().canWrite();
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePathNioMem getParent() {
/* 114 */     int i = this.name.lastIndexOf('/');
/* 115 */     return (i < 0) ? null : getPath(this.name.substring(0, i));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 120 */     if (isRoot()) {
/* 121 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 125 */     synchronized (MEMORY_FILES) {
/* 126 */       return (MEMORY_FILES.get(this.name) == null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAbsolute() {
/* 133 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePathNioMem toRealPath() {
/* 138 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public long lastModified() {
/* 143 */     return getMemoryFile().getLastModified();
/*     */   }
/*     */ 
/*     */   
/*     */   public void createDirectory() {
/* 148 */     if (exists() && isDirectory()) {
/* 149 */       throw DbException.get(90062, this.name + " (a file with this name already exists)");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileChannel open(String paramString) {
/* 157 */     FileNioMemData fileNioMemData = getMemoryFile();
/* 158 */     return (FileChannel)new FileNioMem(fileNioMemData, "r".equals(paramString));
/*     */   }
/*     */   
/*     */   private FileNioMemData getMemoryFile() {
/* 162 */     synchronized (MEMORY_FILES) {
/* 163 */       FileNioMemData fileNioMemData = MEMORY_FILES.get(this.name);
/* 164 */       if (fileNioMemData == null) {
/* 165 */         fileNioMemData = new FileNioMemData(this.name, compressed(), this.compressLaterCachePercent);
/* 166 */         MEMORY_FILES.put(this.name, fileNioMemData);
/*     */       } 
/* 168 */       return fileNioMemData;
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean isRoot() {
/* 173 */     return this.name.equals(getScheme() + ":");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getCanonicalPath(String paramString) {
/* 184 */     paramString = paramString.replace('\\', '/');
/* 185 */     int i = paramString.lastIndexOf(':') + 1;
/* 186 */     if (paramString.length() > i && paramString.charAt(i) != '/') {
/* 187 */       paramString = paramString.substring(0, i) + "/" + paramString.substring(i);
/*     */     }
/* 189 */     return paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 194 */     return "nioMemFS";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean compressed() {
/* 203 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\niomem\FilePathNioMem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */