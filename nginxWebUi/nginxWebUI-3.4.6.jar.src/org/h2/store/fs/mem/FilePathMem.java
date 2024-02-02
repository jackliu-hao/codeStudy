/*     */ package org.h2.store.fs.mem;
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
/*     */ public class FilePathMem
/*     */   extends FilePath
/*     */ {
/*  22 */   private static final TreeMap<String, FileMemData> MEMORY_FILES = new TreeMap<>();
/*     */   
/*  24 */   private static final FileMemData DIRECTORY = new FileMemData("", false);
/*     */ 
/*     */   
/*     */   public FilePathMem getPath(String paramString) {
/*  28 */     FilePathMem filePathMem = new FilePathMem();
/*  29 */     filePathMem.name = getCanonicalPath(paramString);
/*  30 */     return filePathMem;
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/*  35 */     return getMemoryFile().length();
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveTo(FilePath paramFilePath, boolean paramBoolean) {
/*  40 */     synchronized (MEMORY_FILES) {
/*  41 */       if (!paramBoolean && !paramFilePath.name.equals(this.name) && MEMORY_FILES
/*  42 */         .containsKey(paramFilePath.name)) {
/*  43 */         throw DbException.get(90024, new String[] { this.name, paramFilePath + " (exists)" });
/*     */       }
/*  45 */       FileMemData fileMemData = getMemoryFile();
/*  46 */       fileMemData.setName(paramFilePath.name);
/*  47 */       MEMORY_FILES.remove(this.name);
/*  48 */       MEMORY_FILES.put(paramFilePath.name, fileMemData);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean createFile() {
/*  54 */     synchronized (MEMORY_FILES) {
/*  55 */       if (exists()) {
/*  56 */         return false;
/*     */       }
/*  58 */       getMemoryFile();
/*     */     } 
/*  60 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean exists() {
/*  65 */     if (isRoot()) {
/*  66 */       return true;
/*     */     }
/*  68 */     synchronized (MEMORY_FILES) {
/*  69 */       return (MEMORY_FILES.get(this.name) != null);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/*  75 */     if (isRoot()) {
/*     */       return;
/*     */     }
/*  78 */     synchronized (MEMORY_FILES) {
/*  79 */       FileMemData fileMemData = MEMORY_FILES.remove(this.name);
/*  80 */       if (fileMemData != null) {
/*  81 */         fileMemData.truncate(0L);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public List<FilePath> newDirectoryStream() {
/*  88 */     ArrayList<FilePathMem> arrayList = new ArrayList();
/*  89 */     synchronized (MEMORY_FILES) {
/*  90 */       for (String str : MEMORY_FILES.tailMap(this.name).keySet()) {
/*  91 */         if (str.startsWith(this.name)) {
/*  92 */           if (!str.equals(this.name) && str.indexOf('/', this.name.length() + 1) < 0) {
/*  93 */             arrayList.add(getPath(str));
/*     */           }
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*  99 */       return (List)arrayList;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setReadOnly() {
/* 105 */     return getMemoryFile().setReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite() {
/* 110 */     return getMemoryFile().canWrite();
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePathMem getParent() {
/* 115 */     int i = this.name.lastIndexOf('/');
/* 116 */     return (i < 0) ? null : getPath(this.name.substring(0, i));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 121 */     if (isRoot()) {
/* 122 */       return true;
/*     */     }
/* 124 */     synchronized (MEMORY_FILES) {
/* 125 */       FileMemData fileMemData = MEMORY_FILES.get(this.name);
/* 126 */       return (fileMemData == DIRECTORY);
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
/*     */   public FilePathMem toRealPath() {
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
/* 148 */     if (exists()) {
/* 149 */       throw DbException.get(90062, this.name + " (a file with this name already exists)");
/*     */     }
/*     */     
/* 152 */     synchronized (MEMORY_FILES) {
/* 153 */       MEMORY_FILES.put(this.name, DIRECTORY);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel open(String paramString) {
/* 159 */     FileMemData fileMemData = getMemoryFile();
/* 160 */     return (FileChannel)new FileMem(fileMemData, "r".equals(paramString));
/*     */   }
/*     */   
/*     */   private FileMemData getMemoryFile() {
/* 164 */     synchronized (MEMORY_FILES) {
/* 165 */       FileMemData fileMemData = MEMORY_FILES.get(this.name);
/* 166 */       if (fileMemData == DIRECTORY) {
/* 167 */         throw DbException.get(90062, this.name + " (a directory with this name already exists)");
/*     */       }
/*     */       
/* 170 */       if (fileMemData == null) {
/* 171 */         fileMemData = new FileMemData(this.name, compressed());
/* 172 */         MEMORY_FILES.put(this.name, fileMemData);
/*     */       } 
/* 174 */       return fileMemData;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isRoot() {
/* 179 */     return this.name.equals(getScheme() + ":");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String getCanonicalPath(String paramString) {
/* 189 */     paramString = paramString.replace('\\', '/');
/* 190 */     int i = paramString.indexOf(':') + 1;
/* 191 */     if (paramString.length() > i && paramString.charAt(i) != '/') {
/* 192 */       paramString = paramString.substring(0, i) + "/" + paramString.substring(i);
/*     */     }
/* 194 */     return paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 199 */     return "memFS";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean compressed() {
/* 208 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\mem\FilePathMem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */