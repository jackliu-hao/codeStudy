/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashSet;
/*     */ import org.h2.engine.ConnectionInfo;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.store.fs.Recorder;
/*     */ import org.h2.store.fs.rec.FilePathRec;
/*     */ import org.h2.tools.Recover;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
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
/*     */ public class RecoverTester
/*     */   implements Recorder
/*     */ {
/*  34 */   private static final RecoverTester instance = new RecoverTester();
/*     */   
/*  36 */   private String testDatabase = "memFS:reopen";
/*  37 */   private int writeCount = Utils.getProperty("h2.recoverTestOffset", 0);
/*  38 */   private int testEvery = Utils.getProperty("h2.recoverTest", 64);
/*  39 */   private final long maxFileSize = Utils.getProperty("h2.recoverTestMaxFileSize", 2147483647) * 1024L * 1024L;
/*     */   
/*     */   private int verifyCount;
/*  42 */   private final HashSet<String> knownErrors = new HashSet<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean testing;
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void init(String paramString) {
/*  51 */     if (StringUtils.isNumber(paramString)) {
/*  52 */       instance.setTestEvery(Integer.parseInt(paramString));
/*     */     }
/*  54 */     FilePathRec.setRecorder(instance);
/*     */   }
/*     */ 
/*     */   
/*     */   public void log(int paramInt, String paramString, byte[] paramArrayOfbyte, long paramLong) {
/*  59 */     if (paramInt != 8 && paramInt != 7) {
/*     */       return;
/*     */     }
/*  62 */     if (!paramString.endsWith(".mv.db")) {
/*     */       return;
/*     */     }
/*  65 */     this.writeCount++;
/*  66 */     if (this.writeCount % this.testEvery != 0) {
/*     */       return;
/*     */     }
/*  69 */     if (FileUtils.size(paramString) > this.maxFileSize) {
/*     */       return;
/*     */     }
/*     */     
/*  73 */     if (this.testing) {
/*     */       return;
/*     */     }
/*     */     
/*  77 */     this.testing = true;
/*  78 */     PrintWriter printWriter = null;
/*     */ 
/*     */     
/*     */     try {
/*  82 */       printWriter = new PrintWriter(new OutputStreamWriter(FileUtils.newOutputStream(paramString + ".log", true)));
/*  83 */       testDatabase(paramString, printWriter);
/*  84 */     } catch (IOException iOException) {
/*  85 */       throw DbException.convertIOException(iOException, null);
/*     */     } finally {
/*  87 */       IOUtils.closeSilently(printWriter);
/*  88 */       this.testing = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private synchronized void testDatabase(String paramString, PrintWriter paramPrintWriter) {
/*  93 */     paramPrintWriter.println("+ write #" + this.writeCount + " verify #" + this.verifyCount);
/*     */     try {
/*  95 */       IOUtils.copyFiles(paramString, this.testDatabase + ".mv.db");
/*  96 */       this.verifyCount++;
/*     */       
/*  98 */       ConnectionInfo connectionInfo = new ConnectionInfo("jdbc:h2:" + this.testDatabase + ";FILE_LOCK=NO;TRACE_LEVEL_FILE=0", null, "", "");
/*     */       
/* 100 */       Database database = new Database(connectionInfo, null);
/*     */       
/* 102 */       SessionLocal sessionLocal = database.getSystemSession();
/* 103 */       sessionLocal.prepare("script to '" + this.testDatabase + ".sql'").query(0L);
/* 104 */       sessionLocal.prepare("shutdown immediately").update();
/* 105 */       database.removeSession(null);
/*     */       
/*     */       return;
/* 108 */     } catch (DbException dbException) {
/* 109 */       SQLException sQLException = DbException.toSQLException((Throwable)dbException);
/* 110 */       int i = sQLException.getErrorCode();
/* 111 */       if (i == 28000)
/*     */         return; 
/* 113 */       if (i == 90049) {
/*     */         return;
/*     */       }
/* 116 */       dbException.printStackTrace(System.out);
/* 117 */     } catch (Exception exception) {
/*     */       
/* 119 */       int i = 0;
/* 120 */       if (exception instanceof SQLException) {
/* 121 */         i = ((SQLException)exception).getErrorCode();
/*     */       }
/* 123 */       if (i == 28000)
/*     */         return; 
/* 125 */       if (i == 90049) {
/*     */         return;
/*     */       }
/* 128 */       exception.printStackTrace(System.out);
/*     */     } 
/* 130 */     paramPrintWriter.println("begin ------------------------------ " + this.writeCount);
/*     */     try {
/* 132 */       Recover.execute(paramString.substring(0, paramString.lastIndexOf('/')), null);
/* 133 */     } catch (SQLException sQLException) {}
/*     */ 
/*     */     
/* 136 */     this.testDatabase += "X";
/*     */     try {
/* 138 */       IOUtils.copyFiles(paramString, this.testDatabase + ".mv.db");
/*     */       
/* 140 */       ConnectionInfo connectionInfo = new ConnectionInfo("jdbc:h2:" + this.testDatabase + ";FILE_LOCK=NO", null, null, null);
/*     */       
/* 142 */       Database database = new Database(connectionInfo, null);
/*     */       
/* 144 */       database.removeSession(null);
/* 145 */     } catch (Exception exception) {
/* 146 */       int i = 0;
/* 147 */       if (exception instanceof DbException) {
/* 148 */         exception = ((DbException)exception).getSQLException();
/* 149 */         i = ((SQLException)exception).getErrorCode();
/*     */       } 
/* 151 */       if (i == 28000)
/*     */         return; 
/* 153 */       if (i == 90049) {
/*     */         return;
/*     */       }
/* 156 */       StringBuilder stringBuilder = new StringBuilder();
/* 157 */       StackTraceElement[] arrayOfStackTraceElement = exception.getStackTrace();
/* 158 */       for (byte b = 0; b < 10 && b < arrayOfStackTraceElement.length; b++) {
/* 159 */         stringBuilder.append(arrayOfStackTraceElement[b].toString()).append('\n');
/*     */       }
/* 161 */       String str = stringBuilder.toString();
/* 162 */       if (!this.knownErrors.contains(str)) {
/* 163 */         paramPrintWriter.println(this.writeCount + " code: " + i + " " + exception.toString());
/* 164 */         exception.printStackTrace(System.out);
/* 165 */         this.knownErrors.add(str);
/*     */       } else {
/* 167 */         paramPrintWriter.println(this.writeCount + " code: " + i);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setTestEvery(int paramInt) {
/* 173 */     this.testEvery = paramInt;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\RecoverTester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */