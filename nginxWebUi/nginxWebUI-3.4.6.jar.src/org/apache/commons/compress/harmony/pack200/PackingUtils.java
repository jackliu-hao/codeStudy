/*     */ package org.apache.commons.compress.harmony.pack200;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import java.util.jar.Manifest;
/*     */ import java.util.logging.FileHandler;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.LogRecord;
/*     */ import java.util.logging.Logger;
/*     */ import java.util.logging.SimpleFormatter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PackingUtils
/*     */ {
/*  48 */   private static PackingLogger packingLogger = new PackingLogger("org.harmony.apache.pack200", null); static {
/*  49 */     LogManager.getLogManager().addLogger(packingLogger);
/*     */   }
/*     */   
/*     */   private static class PackingLogger
/*     */     extends Logger {
/*     */     private boolean verbose = false;
/*     */     
/*     */     protected PackingLogger(String name, String resourceBundleName) {
/*  57 */       super(name, resourceBundleName);
/*     */     }
/*     */ 
/*     */     
/*     */     public void log(LogRecord logRecord) {
/*  62 */       if (this.verbose) {
/*  63 */         super.log(logRecord);
/*     */       }
/*     */     }
/*     */     
/*     */     public void setVerbose(boolean isVerbose) {
/*  68 */       this.verbose = isVerbose;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void config(PackingOptions options) throws IOException {
/*  73 */     String logFileName = options.getLogFile();
/*  74 */     if (logFileName != null) {
/*  75 */       FileHandler fileHandler = new FileHandler(logFileName, false);
/*  76 */       fileHandler.setFormatter(new SimpleFormatter());
/*  77 */       packingLogger.addHandler(fileHandler);
/*  78 */       packingLogger.setUseParentHandlers(false);
/*     */     } 
/*     */     
/*  81 */     packingLogger.setVerbose(options.isVerbose());
/*     */   }
/*     */   
/*     */   public static void log(String message) {
/*  85 */     packingLogger.log(Level.INFO, message);
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
/*     */   public static void copyThroughJar(JarInputStream jarInputStream, OutputStream outputStream) throws IOException {
/*  97 */     Manifest manifest = jarInputStream.getManifest();
/*  98 */     JarOutputStream jarOutputStream = new JarOutputStream(outputStream, manifest);
/*  99 */     jarOutputStream.setComment("PACK200");
/* 100 */     log("Packed META-INF/MANIFEST.MF");
/*     */     
/* 102 */     byte[] bytes = new byte[16384];
/*     */     
/*     */     JarEntry jarEntry;
/* 105 */     while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
/* 106 */       jarOutputStream.putNextEntry(jarEntry); int bytesRead;
/* 107 */       while ((bytesRead = jarInputStream.read(bytes)) != -1) {
/* 108 */         jarOutputStream.write(bytes, 0, bytesRead);
/*     */       }
/* 110 */       log("Packed " + jarEntry.getName());
/*     */     } 
/* 112 */     jarInputStream.close();
/* 113 */     jarOutputStream.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copyThroughJar(JarFile jarFile, OutputStream outputStream) throws IOException {
/* 124 */     JarOutputStream jarOutputStream = new JarOutputStream(outputStream);
/* 125 */     jarOutputStream.setComment("PACK200");
/* 126 */     byte[] bytes = new byte[16384];
/* 127 */     Enumeration<JarEntry> entries = jarFile.entries();
/*     */ 
/*     */ 
/*     */     
/* 131 */     while (entries.hasMoreElements()) {
/* 132 */       JarEntry jarEntry = entries.nextElement();
/* 133 */       jarOutputStream.putNextEntry(jarEntry);
/* 134 */       InputStream inputStream = jarFile.getInputStream(jarEntry); int bytesRead;
/* 135 */       while ((bytesRead = inputStream.read(bytes)) != -1) {
/* 136 */         jarOutputStream.write(bytes, 0, bytesRead);
/*     */       }
/* 138 */       jarOutputStream.closeEntry();
/* 139 */       log("Packed " + jarEntry.getName());
/*     */     } 
/* 141 */     jarFile.close();
/* 142 */     jarOutputStream.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public static List getPackingFileListFromJar(JarInputStream jarInputStream, boolean keepFileOrder) throws IOException {
/* 147 */     List<Archive.PackingFile> packingFileList = new ArrayList();
/*     */ 
/*     */     
/* 150 */     Manifest manifest = jarInputStream.getManifest();
/* 151 */     if (manifest != null) {
/* 152 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 153 */       manifest.write(baos);
/* 154 */       packingFileList.add(new Archive.PackingFile("META-INF/MANIFEST.MF", baos.toByteArray(), 0L));
/*     */     } 
/*     */ 
/*     */     
/*     */     JarEntry jarEntry;
/*     */     
/* 160 */     while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
/* 161 */       byte[] bytes = readJarEntry(jarEntry, new BufferedInputStream(jarInputStream));
/* 162 */       packingFileList.add(new Archive.PackingFile(bytes, jarEntry));
/*     */     } 
/*     */ 
/*     */     
/* 166 */     if (!keepFileOrder) {
/* 167 */       reorderPackingFiles(packingFileList);
/*     */     }
/* 169 */     return packingFileList;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List getPackingFileListFromJar(JarFile jarFile, boolean keepFileOrder) throws IOException {
/* 174 */     List<Archive.PackingFile> packingFileList = new ArrayList();
/* 175 */     Enumeration<JarEntry> jarEntries = jarFile.entries();
/*     */ 
/*     */     
/* 178 */     while (jarEntries.hasMoreElements()) {
/* 179 */       JarEntry jarEntry = jarEntries.nextElement();
/* 180 */       byte[] bytes = readJarEntry(jarEntry, new BufferedInputStream(jarFile.getInputStream(jarEntry)));
/* 181 */       packingFileList.add(new Archive.PackingFile(bytes, jarEntry));
/*     */     } 
/*     */ 
/*     */     
/* 185 */     if (!keepFileOrder) {
/* 186 */       reorderPackingFiles(packingFileList);
/*     */     }
/* 188 */     return packingFileList;
/*     */   }
/*     */   
/*     */   private static byte[] readJarEntry(JarEntry jarEntry, InputStream inputStream) throws IOException {
/* 192 */     long size = jarEntry.getSize();
/* 193 */     if (size > 2147483647L)
/*     */     {
/* 195 */       throw new RuntimeException("Large Class!");
/*     */     }
/* 197 */     if (size < 0L) {
/* 198 */       size = 0L;
/*     */     }
/* 200 */     byte[] bytes = new byte[(int)size];
/* 201 */     if (inputStream.read(bytes) != size) {
/* 202 */       throw new RuntimeException("Error reading from stream");
/*     */     }
/* 204 */     return bytes;
/*     */   }
/*     */   
/*     */   private static void reorderPackingFiles(List<?> packingFileList) {
/* 208 */     Iterator<Archive.PackingFile> iterator = packingFileList.iterator();
/*     */     
/* 210 */     while (iterator.hasNext()) {
/* 211 */       Archive.PackingFile packingFile = iterator.next();
/* 212 */       if (packingFile.isDirectory())
/*     */       {
/* 214 */         iterator.remove();
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 220 */     Collections.sort(packingFileList, (arg0, arg1) -> {
/*     */           if (arg0 instanceof Archive.PackingFile && arg1 instanceof Archive.PackingFile) {
/*     */             String fileName0 = ((Archive.PackingFile)arg0).getName();
/*     */             String fileName1 = ((Archive.PackingFile)arg1).getName();
/*     */             return fileName0.equals(fileName1) ? 0 : ("META-INF/MANIFEST.MF".equals(fileName0) ? -1 : ("META-INF/MANIFEST.MF".equals(fileName1) ? 1 : fileName0.compareTo(fileName1)));
/*     */           } 
/*     */           throw new IllegalArgumentException();
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\PackingUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */