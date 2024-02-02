/*     */ package org.h2.store.fs.rec;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.FileChannel;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.FilePathWrapper;
/*     */ import org.h2.store.fs.Recorder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilePathRec
/*     */   extends FilePathWrapper
/*     */ {
/*  20 */   private static final FilePathRec INSTANCE = new FilePathRec();
/*     */ 
/*     */   
/*     */   private static Recorder recorder;
/*     */ 
/*     */   
/*     */   private boolean trace;
/*     */ 
/*     */   
/*     */   public static void register() {
/*  30 */     FilePath.register((FilePath)INSTANCE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setRecorder(Recorder paramRecorder) {
/*  39 */     recorder = paramRecorder;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean createFile() {
/*  44 */     log(2, this.name);
/*  45 */     return super.createFile();
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath createTempFile(String paramString, boolean paramBoolean) throws IOException {
/*  50 */     log(3, unwrap(this.name) + ":" + paramString + ":" + paramBoolean);
/*  51 */     return super.createTempFile(paramString, paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/*  56 */     log(4, this.name);
/*  57 */     super.delete();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel open(String paramString) throws IOException {
/*  62 */     return (FileChannel)new FileRec(this, super.open(paramString), this.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public OutputStream newOutputStream(boolean paramBoolean) throws IOException {
/*  67 */     log(5, this.name);
/*  68 */     return super.newOutputStream(paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveTo(FilePath paramFilePath, boolean paramBoolean) {
/*  73 */     log(6, unwrap(this.name) + ":" + unwrap(paramFilePath.name));
/*  74 */     super.moveTo(paramFilePath, paramBoolean);
/*     */   }
/*     */   
/*     */   public boolean isTrace() {
/*  78 */     return this.trace;
/*     */   }
/*     */   
/*     */   public void setTrace(boolean paramBoolean) {
/*  82 */     this.trace = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void log(int paramInt, String paramString) {
/*  92 */     log(paramInt, paramString, (byte[])null, 0L);
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
/*     */   void log(int paramInt, String paramString, byte[] paramArrayOfbyte, long paramLong) {
/* 104 */     if (recorder != null) {
/* 105 */       recorder.log(paramInt, paramString, paramArrayOfbyte, paramLong);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 116 */     return "rec";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\rec\FilePathRec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */