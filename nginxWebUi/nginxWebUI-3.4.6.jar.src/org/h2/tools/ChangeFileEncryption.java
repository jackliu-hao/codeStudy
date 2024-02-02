/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.store.FileLister;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.store.fs.encrypt.FileEncrypt;
/*     */ import org.h2.store.fs.encrypt.FilePathEncrypt;
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
/*     */ public class ChangeFileEncryption
/*     */   extends Tool
/*     */ {
/*     */   private String directory;
/*     */   private String cipherType;
/*     */   private byte[] decryptKey;
/*     */   private byte[] encryptKey;
/*     */   
/*     */   public static void main(String... paramVarArgs) {
/*     */     try {
/*  63 */       (new ChangeFileEncryption()).runTool(paramVarArgs);
/*  64 */     } catch (SQLException sQLException) {
/*  65 */       sQLException.printStackTrace(System.err);
/*  66 */       System.exit(1);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/*  72 */     String str1 = ".";
/*  73 */     String str2 = null;
/*  74 */     char[] arrayOfChar1 = null;
/*  75 */     char[] arrayOfChar2 = null;
/*  76 */     String str3 = null;
/*  77 */     boolean bool = false;
/*  78 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  79 */       String str = paramVarArgs[b];
/*  80 */       if (str.equals("-dir"))
/*  81 */       { str1 = paramVarArgs[++b]; }
/*  82 */       else if (str.equals("-cipher"))
/*  83 */       { str2 = paramVarArgs[++b]; }
/*  84 */       else if (str.equals("-db"))
/*  85 */       { str3 = paramVarArgs[++b]; }
/*  86 */       else if (str.equals("-decrypt"))
/*  87 */       { arrayOfChar1 = paramVarArgs[++b].toCharArray(); }
/*  88 */       else if (str.equals("-encrypt"))
/*  89 */       { arrayOfChar2 = paramVarArgs[++b].toCharArray(); }
/*  90 */       else if (str.equals("-quiet"))
/*  91 */       { bool = true; }
/*  92 */       else { if (str.equals("-help") || str.equals("-?")) {
/*  93 */           showUsage();
/*     */           return;
/*     */         } 
/*  96 */         showUsageAndThrowUnsupportedOption(str); }
/*     */     
/*     */     } 
/*  99 */     if ((arrayOfChar2 == null && arrayOfChar1 == null) || str2 == null) {
/* 100 */       showUsage();
/* 101 */       throw new SQLException("Encryption or decryption password not set, or cipher not set");
/*     */     } 
/*     */     
/*     */     try {
/* 105 */       process(str1, str3, str2, arrayOfChar1, arrayOfChar2, bool);
/* 106 */     } catch (Exception exception) {
/* 107 */       throw DbException.toSQLException(exception);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void execute(String paramString1, String paramString2, String paramString3, char[] paramArrayOfchar1, char[] paramArrayOfchar2, boolean paramBoolean) throws SQLException {
/*     */     try {
/* 128 */       (new ChangeFileEncryption()).process(paramString1, paramString2, paramString3, paramArrayOfchar1, paramArrayOfchar2, paramBoolean);
/*     */     }
/* 130 */     catch (Exception exception) {
/* 131 */       throw DbException.toSQLException(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void process(String paramString1, String paramString2, String paramString3, char[] paramArrayOfchar1, char[] paramArrayOfchar2, boolean paramBoolean) throws SQLException {
/* 138 */     paramString1 = FileLister.getDir(paramString1);
/* 139 */     ChangeFileEncryption changeFileEncryption = new ChangeFileEncryption();
/* 140 */     if (paramArrayOfchar2 != null) {
/* 141 */       for (char c : paramArrayOfchar2) {
/* 142 */         if (c == ' ') {
/* 143 */           throw new SQLException("The file password may not contain spaces");
/*     */         }
/*     */       } 
/* 146 */       changeFileEncryption.encryptKey = FilePathEncrypt.getPasswordBytes(paramArrayOfchar2);
/*     */     } 
/* 148 */     if (paramArrayOfchar1 != null) {
/* 149 */       changeFileEncryption.decryptKey = FilePathEncrypt.getPasswordBytes(paramArrayOfchar1);
/*     */     }
/* 151 */     changeFileEncryption.out = this.out;
/* 152 */     changeFileEncryption.directory = paramString1;
/* 153 */     changeFileEncryption.cipherType = paramString3;
/*     */     
/* 155 */     ArrayList arrayList = FileLister.getDatabaseFiles(paramString1, paramString2, true);
/* 156 */     FileLister.tryUnlockDatabase(arrayList, "encryption");
/* 157 */     arrayList = FileLister.getDatabaseFiles(paramString1, paramString2, false);
/* 158 */     if (arrayList.isEmpty() && !paramBoolean) {
/* 159 */       printNoDatabaseFilesFound(paramString1, paramString2);
/*     */     }
/*     */ 
/*     */     
/* 163 */     for (String str1 : arrayList) {
/* 164 */       String str2 = paramString1 + "/temp.db";
/* 165 */       FileUtils.delete(str2);
/* 166 */       FileUtils.move(str1, str2);
/* 167 */       FileUtils.move(str2, str1);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 172 */     for (String str : arrayList) {
/*     */       
/* 174 */       if (!FileUtils.isDirectory(str)) {
/* 175 */         changeFileEncryption.process(str, paramBoolean, paramArrayOfchar1);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void process(String paramString, boolean paramBoolean, char[] paramArrayOfchar) throws SQLException {
/* 181 */     if (paramString.endsWith(".mv.db")) {
/*     */       try {
/* 183 */         copyMvStore(paramString, paramBoolean, paramArrayOfchar);
/* 184 */       } catch (IOException iOException) {
/* 185 */         throw DbException.convertIOException(iOException, "Error encrypting / decrypting file " + paramString);
/*     */       } 
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void copyMvStore(String paramString, boolean paramBoolean, char[] paramArrayOfchar) throws IOException, SQLException {
/* 193 */     if (FileUtils.isDirectory(paramString)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 202 */       MVStore mVStore = (new MVStore.Builder()).fileName(paramString).readOnly().encryptionKey(paramArrayOfchar).open();
/* 203 */       mVStore.close();
/* 204 */     } catch (IllegalStateException illegalStateException) {
/* 205 */       throw new SQLException("error decrypting file " + paramString, illegalStateException);
/*     */     } 
/*     */     
/* 208 */     String str = this.directory + "/temp.db";
/* 209 */     try(FileChannel null = getFileChannel(paramString, "r", this.decryptKey); 
/* 210 */         InputStream null = Channels.newInputStream(fileChannel)) {
/* 211 */       FileUtils.delete(str);
/* 212 */       try (OutputStream null = Channels.newOutputStream(getFileChannel(str, "rw", this.encryptKey))) {
/* 213 */         byte[] arrayOfByte = new byte[4096];
/* 214 */         long l1 = fileChannel.size();
/* 215 */         long l2 = l1;
/* 216 */         long l3 = System.nanoTime();
/* 217 */         while (l1 > 0L) {
/* 218 */           if (!paramBoolean && System.nanoTime() - l3 > TimeUnit.SECONDS.toNanos(1L)) {
/* 219 */             this.out.println(paramString + ": " + (100L - 100L * l1 / l2) + "%");
/* 220 */             l3 = System.nanoTime();
/*     */           } 
/* 222 */           int i = (int)Math.min(arrayOfByte.length, l1);
/* 223 */           i = inputStream.read(arrayOfByte, 0, i);
/* 224 */           outputStream.write(arrayOfByte, 0, i);
/* 225 */           l1 -= i;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 230 */     FileUtils.delete(paramString);
/* 231 */     FileUtils.move(str, paramString);
/*     */   }
/*     */   
/*     */   private static FileChannel getFileChannel(String paramString1, String paramString2, byte[] paramArrayOfbyte) throws IOException {
/*     */     FileEncrypt fileEncrypt;
/* 236 */     FileChannel fileChannel = FilePath.get(paramString1).open(paramString2);
/* 237 */     if (paramArrayOfbyte != null) {
/* 238 */       fileEncrypt = new FileEncrypt(paramString1, paramArrayOfbyte, fileChannel);
/*     */     }
/*     */     
/* 241 */     return (FileChannel)fileEncrypt;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\ChangeFileEncryption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */