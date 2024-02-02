/*     */ package org.h2.store;
/*     */ 
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.TraceSystem;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.FileUtils;
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
/*     */ public class FileLister
/*     */ {
/*     */   public static void tryUnlockDatabase(List<String> paramList, String paramString) throws SQLException {
/*  38 */     for (String str : paramList) {
/*  39 */       if (str.endsWith(".lock.db")) {
/*  40 */         FileLock fileLock = new FileLock(new TraceSystem(null), str, 1000);
/*     */         
/*     */         try {
/*  43 */           fileLock.lock(FileLockMethod.FILE);
/*  44 */           fileLock.unlock();
/*  45 */         } catch (DbException dbException) {
/*  46 */           throw DbException.getJdbcSQLException(90133, paramString);
/*     */         } 
/*     */         continue;
/*     */       } 
/*  50 */       if (str.endsWith(".mv.db")) {
/*  51 */         try (FileChannel null = FilePath.get(str).open("r")) {
/*  52 */           FileLock fileLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
/*  53 */           fileLock.release();
/*  54 */         } catch (Exception exception) {
/*  55 */           throw DbException.getJdbcSQLException(90133, exception, new String[] { paramString });
/*     */         } 
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
/*     */ 
/*     */   
/*     */   public static String getDir(String paramString) {
/*  70 */     if (paramString == null || paramString.equals("")) {
/*  71 */       return ".";
/*     */     }
/*  73 */     return FileUtils.toRealPath(paramString);
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
/*     */   public static ArrayList<String> getDatabaseFiles(String paramString1, String paramString2, boolean paramBoolean) {
/*  88 */     ArrayList<String> arrayList = new ArrayList();
/*     */     
/*  90 */     String str = (paramString2 == null) ? null : (FileUtils.toRealPath(paramString1 + "/" + paramString2) + ".");
/*  91 */     for (String str1 : FileUtils.newDirectoryStream(paramString1)) {
/*  92 */       boolean bool = false;
/*  93 */       if (str1.endsWith(".mv.db")) {
/*  94 */         bool = true;
/*  95 */       } else if (paramBoolean) {
/*  96 */         if (str1.endsWith(".lock.db")) {
/*  97 */           bool = true;
/*  98 */         } else if (str1.endsWith(".temp.db")) {
/*  99 */           bool = true;
/* 100 */         } else if (str1.endsWith(".trace.db")) {
/* 101 */           bool = true;
/*     */         } 
/*     */       } 
/* 104 */       if (bool && (
/* 105 */         paramString2 == null || str1.startsWith(str))) {
/* 106 */         arrayList.add(str1);
/*     */       }
/*     */     } 
/*     */     
/* 110 */     return arrayList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\FileLister.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */