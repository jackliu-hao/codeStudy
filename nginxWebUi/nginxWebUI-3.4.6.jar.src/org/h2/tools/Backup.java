/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ import org.h2.command.dml.BackupCommand;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.FileLister;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Backup
/*     */   extends Tool
/*     */ {
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/*  56 */     (new Backup()).runTool(paramVarArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/*  61 */     String str1 = "backup.zip";
/*  62 */     String str2 = ".";
/*  63 */     String str3 = null;
/*  64 */     boolean bool = false;
/*  65 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  66 */       String str = paramVarArgs[b];
/*  67 */       if (str.equals("-dir"))
/*  68 */       { str2 = paramVarArgs[++b]; }
/*  69 */       else if (str.equals("-db"))
/*  70 */       { str3 = paramVarArgs[++b]; }
/*  71 */       else if (str.equals("-quiet"))
/*  72 */       { bool = true; }
/*  73 */       else if (str.equals("-file"))
/*  74 */       { str1 = paramVarArgs[++b]; }
/*  75 */       else { if (str.equals("-help") || str.equals("-?")) {
/*  76 */           showUsage();
/*     */           return;
/*     */         } 
/*  79 */         showUsageAndThrowUnsupportedOption(str); }
/*     */     
/*     */     } 
/*     */     try {
/*  83 */       process(str1, str2, str3, bool);
/*  84 */     } catch (Exception exception) {
/*  85 */       throw DbException.toSQLException(exception);
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
/*     */   public static void execute(String paramString1, String paramString2, String paramString3, boolean paramBoolean) throws SQLException {
/*     */     try {
/* 102 */       (new Backup()).process(paramString1, paramString2, paramString3, paramBoolean);
/* 103 */     } catch (Exception exception) {
/* 104 */       throw DbException.toSQLException(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void process(String paramString1, String paramString2, String paramString3, boolean paramBoolean) throws SQLException {
/*     */     List list;
/* 111 */     boolean bool = (paramString3 != null && paramString3.isEmpty()) ? true : false;
/* 112 */     if (bool) {
/* 113 */       list = FileUtils.newDirectoryStream(paramString2);
/*     */     } else {
/* 115 */       list = FileLister.getDatabaseFiles(paramString2, paramString3, true);
/*     */     } 
/* 117 */     if (list.isEmpty()) {
/* 118 */       if (!paramBoolean) {
/* 119 */         printNoDatabaseFilesFound(paramString2, paramString3);
/*     */       }
/*     */       return;
/*     */     } 
/* 123 */     if (!paramBoolean) {
/* 124 */       FileLister.tryUnlockDatabase(list, "backup");
/*     */     }
/* 126 */     paramString1 = FileUtils.toRealPath(paramString1);
/* 127 */     FileUtils.delete(paramString1);
/* 128 */     OutputStream outputStream = null;
/*     */     try {
/* 130 */       outputStream = FileUtils.newOutputStream(paramString1, false);
/* 131 */       try (ZipOutputStream null = new ZipOutputStream(outputStream)) {
/* 132 */         String str = "";
/* 133 */         for (String str1 : list) {
/* 134 */           if (bool || str1
/* 135 */             .endsWith(".mv.db")) {
/* 136 */             str = FileUtils.getParent(str1);
/*     */             break;
/*     */           } 
/*     */         } 
/* 140 */         for (String str1 : list) {
/* 141 */           String str2 = FileUtils.toRealPath(str1);
/* 142 */           if (!str2.startsWith(str)) {
/* 143 */             throw DbException.getInternalError(str2 + " does not start with " + str);
/*     */           }
/* 145 */           if (str2.endsWith(paramString1)) {
/*     */             continue;
/*     */           }
/* 148 */           if (FileUtils.isDirectory(str1)) {
/*     */             continue;
/*     */           }
/* 151 */           str2 = str2.substring(str.length());
/* 152 */           str2 = BackupCommand.correctFileName(str2);
/* 153 */           ZipEntry zipEntry = new ZipEntry(str2);
/* 154 */           zipOutputStream.putNextEntry(zipEntry);
/* 155 */           InputStream inputStream = null;
/*     */           try {
/* 157 */             inputStream = FileUtils.newInputStream(str1);
/* 158 */             IOUtils.copyAndCloseInput(inputStream, zipOutputStream);
/* 159 */           } catch (FileNotFoundException fileNotFoundException) {
/*     */ 
/*     */           
/*     */           } finally {
/* 163 */             IOUtils.closeSilently(inputStream);
/*     */           } 
/* 165 */           zipOutputStream.closeEntry();
/* 166 */           if (!paramBoolean) {
/* 167 */             this.out.println("Processed: " + str1);
/*     */           }
/*     */         } 
/*     */       } 
/* 171 */     } catch (IOException iOException) {
/* 172 */       throw DbException.convertIOException(iOException, paramString1);
/*     */     } finally {
/* 174 */       IOUtils.closeSilently(outputStream);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Backup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */