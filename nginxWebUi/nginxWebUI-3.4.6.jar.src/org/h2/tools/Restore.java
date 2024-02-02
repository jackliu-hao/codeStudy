/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.SQLException;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.Tool;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Restore
/*     */   extends Tool
/*     */ {
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/*  46 */     (new Restore()).runTool(paramVarArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/*  51 */     String str1 = "backup.zip";
/*  52 */     String str2 = ".";
/*  53 */     String str3 = null;
/*  54 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  55 */       String str = paramVarArgs[b];
/*  56 */       if (str.equals("-dir")) {
/*  57 */         str2 = paramVarArgs[++b];
/*  58 */       } else if (str.equals("-file")) {
/*  59 */         str1 = paramVarArgs[++b];
/*  60 */       } else if (str.equals("-db")) {
/*  61 */         str3 = paramVarArgs[++b];
/*  62 */       } else if (!str.equals("-quiet")) {
/*     */         
/*  64 */         if (str.equals("-help") || str.equals("-?")) {
/*  65 */           showUsage();
/*     */           return;
/*     */         } 
/*  68 */         showUsageAndThrowUnsupportedOption(str);
/*     */       } 
/*     */     } 
/*  71 */     execute(str1, str2, str3);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String getOriginalDbName(String paramString1, String paramString2) throws IOException {
/*  77 */     try (InputStream null = FileUtils.newInputStream(paramString1)) {
/*  78 */       ZipInputStream zipInputStream = new ZipInputStream(inputStream);
/*  79 */       String str = null;
/*  80 */       boolean bool = false;
/*     */       while (true) {
/*  82 */         ZipEntry zipEntry = zipInputStream.getNextEntry();
/*  83 */         if (zipEntry == null) {
/*     */           break;
/*     */         }
/*  86 */         String str1 = zipEntry.getName();
/*  87 */         zipInputStream.closeEntry();
/*  88 */         String str2 = getDatabaseNameFromFileName(str1);
/*  89 */         if (str2 != null) {
/*  90 */           if (paramString2.equals(str2)) {
/*  91 */             str = str2;
/*     */             break;
/*     */           } 
/*  94 */           if (str == null) {
/*  95 */             str = str2;
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 100 */           bool = true;
/*     */         } 
/*     */       } 
/*     */       
/* 104 */       zipInputStream.close();
/* 105 */       if (bool && !paramString2.equals(str)) {
/* 106 */         throw new IOException("Multiple databases found, but not " + paramString2);
/*     */       }
/* 108 */       return str;
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
/*     */   private static String getDatabaseNameFromFileName(String paramString) {
/* 120 */     if (paramString.endsWith(".mv.db")) {
/* 121 */       return paramString.substring(0, paramString
/* 122 */           .length() - ".mv.db".length());
/*     */     }
/* 124 */     return null;
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
/*     */   public static void execute(String paramString1, String paramString2, String paramString3) {
/* 136 */     InputStream inputStream = null;
/*     */     try {
/* 138 */       if (!FileUtils.exists(paramString1)) {
/* 139 */         throw new IOException("File not found: " + paramString1);
/*     */       }
/* 141 */       String str = null;
/* 142 */       int i = 0;
/* 143 */       if (paramString3 != null) {
/* 144 */         str = getOriginalDbName(paramString1, paramString3);
/* 145 */         if (str == null) {
/* 146 */           throw new IOException("No database named " + paramString3 + " found");
/*     */         }
/* 148 */         if (str.startsWith(File.separator)) {
/* 149 */           str = str.substring(1);
/*     */         }
/* 151 */         i = str.length();
/*     */       } 
/* 153 */       inputStream = FileUtils.newInputStream(paramString1);
/* 154 */       try (ZipInputStream null = new ZipInputStream(inputStream)) {
/*     */         while (true) {
/* 156 */           ZipEntry zipEntry = zipInputStream.getNextEntry();
/* 157 */           if (zipEntry == null) {
/*     */             break;
/*     */           }
/* 160 */           String str1 = zipEntry.getName();
/*     */           
/* 162 */           str1 = IOUtils.nameSeparatorsToNative(str1);
/* 163 */           if (str1.startsWith(File.separator)) {
/* 164 */             str1 = str1.substring(1);
/*     */           }
/* 166 */           boolean bool = false;
/* 167 */           if (paramString3 == null) {
/* 168 */             bool = true;
/* 169 */           } else if (str1.startsWith(str + ".")) {
/* 170 */             str1 = paramString3 + str1.substring(i);
/* 171 */             bool = true;
/*     */           } 
/* 173 */           if (bool) {
/* 174 */             OutputStream outputStream = null;
/*     */             try {
/* 176 */               outputStream = FileUtils.newOutputStream(paramString2 + File.separatorChar + str1, false);
/* 177 */               IOUtils.copy(zipInputStream, outputStream);
/* 178 */               outputStream.close();
/*     */             } finally {
/* 180 */               IOUtils.closeSilently(outputStream);
/*     */             } 
/*     */           } 
/* 183 */           zipInputStream.closeEntry();
/*     */         } 
/* 185 */         zipInputStream.closeEntry();
/*     */       } 
/* 187 */     } catch (IOException iOException) {
/* 188 */       throw DbException.convertIOException(iOException, paramString1);
/*     */     } finally {
/* 190 */       IOUtils.closeSilently(inputStream);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Restore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */