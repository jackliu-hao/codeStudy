/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.mvstore.db.Store;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.store.FileLister;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
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
/*     */ public class BackupCommand
/*     */   extends Prepared
/*     */ {
/*     */   private Expression fileNameExpr;
/*     */   
/*     */   public BackupCommand(SessionLocal paramSessionLocal) {
/*  38 */     super(paramSessionLocal);
/*     */   }
/*     */   
/*     */   public void setFileName(Expression paramExpression) {
/*  42 */     this.fileNameExpr = paramExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public long update() {
/*  47 */     String str = this.fileNameExpr.getValue(this.session).getString();
/*  48 */     this.session.getUser().checkAdmin();
/*  49 */     backupTo(str);
/*  50 */     return 0L;
/*     */   }
/*     */   
/*     */   private void backupTo(String paramString) {
/*  54 */     Database database = this.session.getDatabase();
/*  55 */     if (!database.isPersistent()) {
/*  56 */       throw DbException.get(90126);
/*     */     }
/*     */     try {
/*  59 */       Store store = database.getStore();
/*  60 */       store.flush();
/*  61 */       String str = database.getName();
/*  62 */       str = FileUtils.getName(str);
/*  63 */       try (OutputStream null = FileUtils.newOutputStream(paramString, false)) {
/*  64 */         ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
/*  65 */         database.flush();
/*     */ 
/*     */         
/*  68 */         String str1 = FileUtils.getParent(database.getName());
/*  69 */         synchronized (database.getLobSyncObject()) {
/*  70 */           String str2 = database.getDatabasePath();
/*  71 */           String str3 = FileUtils.getParent(str2);
/*  72 */           str3 = FileLister.getDir(str3);
/*  73 */           ArrayList arrayList = FileLister.getDatabaseFiles(str3, str, true);
/*  74 */           for (String str4 : arrayList) {
/*  75 */             if (str4.endsWith(".mv.db")) {
/*  76 */               MVStore mVStore = store.getMvStore();
/*  77 */               boolean bool = mVStore.getReuseSpace();
/*  78 */               mVStore.setReuseSpace(false);
/*     */               try {
/*  80 */                 InputStream inputStream = store.getInputStream();
/*  81 */                 backupFile(zipOutputStream, str1, str4, inputStream);
/*     */               } finally {
/*  83 */                 mVStore.setReuseSpace(bool);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*  88 */         zipOutputStream.close();
/*     */       } 
/*  90 */     } catch (IOException iOException) {
/*  91 */       throw DbException.convertIOException(iOException, paramString);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void backupFile(ZipOutputStream paramZipOutputStream, String paramString1, String paramString2, InputStream paramInputStream) throws IOException {
/*  97 */     String str = FileUtils.toRealPath(paramString2);
/*  98 */     paramString1 = FileUtils.toRealPath(paramString1);
/*  99 */     if (!str.startsWith(paramString1)) {
/* 100 */       throw DbException.getInternalError(str + " does not start with " + paramString1);
/*     */     }
/* 102 */     str = str.substring(paramString1.length());
/* 103 */     str = correctFileName(str);
/* 104 */     paramZipOutputStream.putNextEntry(new ZipEntry(str));
/* 105 */     IOUtils.copyAndCloseInput(paramInputStream, paramZipOutputStream);
/* 106 */     paramZipOutputStream.closeEntry();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/* 111 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String correctFileName(String paramString) {
/* 121 */     paramString = paramString.replace('\\', '/');
/* 122 */     if (paramString.startsWith("/")) {
/* 123 */       paramString = paramString.substring(1);
/*     */     }
/* 125 */     return paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRecompile() {
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/* 135 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 140 */     return 56;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\BackupCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */