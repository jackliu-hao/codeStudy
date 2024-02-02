/*     */ package org.apache.commons.compress.archivers;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.file.Files;
/*     */ import java.util.Enumeration;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.sevenz.SevenZFile;
/*     */ import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.tar.TarFile;
/*     */ import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
/*     */ import org.apache.commons.compress.archivers.zip.ZipFile;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Lister
/*     */ {
/*  43 */   private static final ArchiveStreamFactory FACTORY = ArchiveStreamFactory.DEFAULT;
/*     */   
/*     */   public static void main(String[] args) throws Exception {
/*  46 */     if (args.length == 0) {
/*  47 */       usage();
/*     */       return;
/*     */     } 
/*  50 */     System.out.println("Analysing " + args[0]);
/*  51 */     File f = new File(args[0]);
/*  52 */     if (!f.isFile()) {
/*  53 */       System.err.println(f + " doesn't exist or is a directory");
/*     */     }
/*  55 */     String format = (args.length > 1) ? args[1] : detectFormat(f);
/*  56 */     if ("7z".equalsIgnoreCase(format)) {
/*  57 */       list7z(f);
/*  58 */     } else if ("zipfile".equals(format)) {
/*  59 */       listZipUsingZipFile(f);
/*  60 */     } else if ("tarfile".equals(format)) {
/*  61 */       listZipUsingTarFile(f);
/*     */     } else {
/*  63 */       listStream(f, args);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void listStream(File f, String[] args) throws ArchiveException, IOException {
/*  68 */     try(InputStream fis = new BufferedInputStream(Files.newInputStream(f.toPath(), new java.nio.file.OpenOption[0])); 
/*  69 */         ArchiveInputStream ais = createArchiveInputStream(args, fis)) {
/*  70 */       System.out.println("Created " + ais.toString());
/*     */       ArchiveEntry ae;
/*  72 */       while ((ae = ais.getNextEntry()) != null) {
/*  73 */         System.out.println(ae.getName());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static ArchiveInputStream createArchiveInputStream(String[] args, InputStream fis) throws ArchiveException {
/*  80 */     if (args.length > 1) {
/*  81 */       return FACTORY.createArchiveInputStream(args[1], fis);
/*     */     }
/*  83 */     return FACTORY.createArchiveInputStream(fis);
/*     */   }
/*     */   
/*     */   private static String detectFormat(File f) throws ArchiveException, IOException {
/*  87 */     try (InputStream fis = new BufferedInputStream(Files.newInputStream(f.toPath(), new java.nio.file.OpenOption[0]))) {
/*  88 */       return ArchiveStreamFactory.detect(fis);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void list7z(File f) throws ArchiveException, IOException {
/*  93 */     try (SevenZFile z = new SevenZFile(f)) {
/*  94 */       System.out.println("Created " + z.toString());
/*     */       SevenZArchiveEntry sevenZArchiveEntry;
/*  96 */       while ((sevenZArchiveEntry = z.getNextEntry()) != null) {
/*     */         
/*  98 */         String name = (sevenZArchiveEntry.getName() == null) ? (z.getDefaultName() + " (entry name was null)") : sevenZArchiveEntry.getName();
/*  99 */         System.out.println(name);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void listZipUsingZipFile(File f) throws ArchiveException, IOException {
/* 105 */     try (ZipFile z = new ZipFile(f)) {
/* 106 */       System.out.println("Created " + z.toString());
/* 107 */       for (Enumeration<ZipArchiveEntry> en = z.getEntries(); en.hasMoreElements();) {
/* 108 */         System.out.println(((ZipArchiveEntry)en.nextElement()).getName());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void listZipUsingTarFile(File f) throws ArchiveException, IOException {
/* 114 */     try (TarFile t = new TarFile(f)) {
/* 115 */       System.out.println("Created " + t.toString());
/* 116 */       for (TarArchiveEntry en : t.getEntries()) {
/* 117 */         System.out.println(en.getName());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void usage() {
/* 123 */     System.out.println("Parameters: archive-name [archive-type]\n");
/* 124 */     System.out.println("the magic archive-type 'zipfile' prefers ZipFile over ZipArchiveInputStream");
/* 125 */     System.out.println("the magic archive-type 'tarfile' prefers TarFile over TarArchiveInputStream");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\Lister.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */