/*     */ package org.h2.store.fs.zip;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.disk.FilePathDisk;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilePathZip
/*     */   extends FilePath
/*     */ {
/*     */   public FilePathZip getPath(String paramString) {
/*  28 */     FilePathZip filePathZip = new FilePathZip();
/*  29 */     filePathZip.name = paramString;
/*  30 */     return filePathZip;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void createDirectory() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean createFile() {
/*  40 */     throw DbException.getUnsupportedException("write");
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/*  45 */     throw DbException.getUnsupportedException("write");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean exists() {
/*     */     try {
/*  51 */       String str = getEntryName();
/*  52 */       if (str.isEmpty()) {
/*  53 */         return true;
/*     */       }
/*  55 */       try (ZipFile null = openZipFile()) {
/*  56 */         return (zipFile.getEntry(str) != null);
/*     */       } 
/*  58 */     } catch (IOException iOException) {
/*  59 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long lastModified() {
/*  65 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath getParent() {
/*  70 */     int i = this.name.lastIndexOf('/');
/*  71 */     return (i < 0) ? null : getPath(this.name.substring(0, i));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAbsolute() {
/*  76 */     String str = translateFileName(this.name);
/*  77 */     return FilePath.get(str).isAbsolute();
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath unwrap() {
/*  82 */     return FilePath.get(this.name.substring(getScheme().length() + 1));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/*     */     try {
/*  88 */       String str = getEntryName();
/*  89 */       if (str.isEmpty()) {
/*  90 */         return true;
/*     */       }
/*  92 */       try (ZipFile null = openZipFile()) {
/*  93 */         Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
/*  94 */         while (enumeration.hasMoreElements()) {
/*  95 */           ZipEntry zipEntry = enumeration.nextElement();
/*  96 */           String str1 = zipEntry.getName();
/*  97 */           if (str1.equals(str))
/*  98 */             return zipEntry.isDirectory(); 
/*  99 */           if (str1.startsWith(str) && 
/* 100 */             str1.length() == str.length() + 1 && 
/* 101 */             str1.equals(str + "/")) {
/* 102 */             return true;
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 108 */       return false;
/* 109 */     } catch (IOException iOException) {
/* 110 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canWrite() {
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setReadOnly() {
/* 121 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long size() {
/* 127 */     try (ZipFile null = openZipFile()) {
/* 128 */       ZipEntry zipEntry = zipFile.getEntry(getEntryName());
/* 129 */       return (zipEntry == null) ? 0L : zipEntry.getSize();
/*     */     }
/* 131 */     catch (IOException iOException) {
/* 132 */       return 0L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<FilePath> newDirectoryStream() {
/* 138 */     String str = this.name;
/* 139 */     ArrayList<FilePathZip> arrayList = new ArrayList();
/*     */     try {
/* 141 */       if (str.indexOf('!') < 0) {
/* 142 */         str = str + "!";
/*     */       }
/* 144 */       if (!str.endsWith("/")) {
/* 145 */         str = str + "/";
/*     */       }
/* 147 */       try (ZipFile null = openZipFile()) {
/* 148 */         String str1 = getEntryName();
/* 149 */         String str2 = str.substring(0, str.length() - str1.length());
/* 150 */         Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
/* 151 */         while (enumeration.hasMoreElements()) {
/* 152 */           ZipEntry zipEntry = enumeration.nextElement();
/* 153 */           String str3 = zipEntry.getName();
/* 154 */           if (!str3.startsWith(str1)) {
/*     */             continue;
/*     */           }
/* 157 */           if (str3.length() <= str1.length()) {
/*     */             continue;
/*     */           }
/* 160 */           int i = str3.indexOf('/', str1.length());
/* 161 */           if (i < 0 || i >= str3.length() - 1) {
/* 162 */             arrayList.add(getPath(str2 + str3));
/*     */           }
/*     */         } 
/*     */       } 
/* 166 */       return (ArrayList)arrayList;
/* 167 */     } catch (IOException iOException) {
/* 168 */       throw DbException.convertIOException(iOException, "listFiles " + str);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel open(String paramString) throws IOException {
/* 174 */     ZipFile zipFile = openZipFile();
/* 175 */     ZipEntry zipEntry = zipFile.getEntry(getEntryName());
/* 176 */     if (zipEntry == null) {
/* 177 */       zipFile.close();
/* 178 */       throw new FileNotFoundException(this.name);
/*     */     } 
/* 180 */     return (FileChannel)new FileZip(zipFile, zipEntry);
/*     */   }
/*     */ 
/*     */   
/*     */   public OutputStream newOutputStream(boolean paramBoolean) throws IOException {
/* 185 */     throw new IOException("write");
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveTo(FilePath paramFilePath, boolean paramBoolean) {
/* 190 */     throw DbException.getUnsupportedException("write");
/*     */   }
/*     */   
/*     */   private static String translateFileName(String paramString) {
/* 194 */     if (paramString.startsWith("zip:")) {
/* 195 */       paramString = paramString.substring("zip:".length());
/*     */     }
/* 197 */     int i = paramString.indexOf('!');
/* 198 */     if (i >= 0) {
/* 199 */       paramString = paramString.substring(0, i);
/*     */     }
/* 201 */     return FilePathDisk.expandUserHomeDirectory(paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath toRealPath() {
/* 206 */     return this;
/*     */   }
/*     */   
/*     */   private String getEntryName() {
/* 210 */     int i = this.name.indexOf('!');
/*     */     
/* 212 */     if (i <= 0) {
/* 213 */       str = "";
/*     */     } else {
/* 215 */       str = this.name.substring(i + 1);
/*     */     } 
/* 217 */     String str = str.replace('\\', '/');
/* 218 */     if (str.startsWith("/")) {
/* 219 */       str = str.substring(1);
/*     */     }
/* 221 */     return str;
/*     */   }
/*     */   
/*     */   private ZipFile openZipFile() throws IOException {
/* 225 */     String str = translateFileName(this.name);
/* 226 */     return new ZipFile(str);
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath createTempFile(String paramString, boolean paramBoolean) throws IOException {
/* 231 */     if (!paramBoolean) {
/* 232 */       throw new IOException("File system is read-only");
/*     */     }
/* 234 */     return (new FilePathDisk()).getPath(this.name).createTempFile(paramString, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 239 */     return "zip";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\zip\FilePathZip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */