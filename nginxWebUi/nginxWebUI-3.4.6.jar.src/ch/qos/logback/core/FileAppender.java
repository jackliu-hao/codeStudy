/*     */ package ch.qos.logback.core;
/*     */ 
/*     */ import ch.qos.logback.core.recovery.ResilientFileOutputStream;
/*     */ import ch.qos.logback.core.util.ContextUtil;
/*     */ import ch.qos.logback.core.util.FileSize;
/*     */ import ch.qos.logback.core.util.FileUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.util.Map;
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
/*     */ public class FileAppender<E>
/*     */   extends OutputStreamAppender<E>
/*     */ {
/*     */   public static final long DEFAULT_BUFFER_SIZE = 8192L;
/*  43 */   protected static String COLLISION_WITH_EARLIER_APPENDER_URL = "http://logback.qos.ch/codes.html#earlier_fa_collision";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean append = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   protected String fileName = null;
/*     */   
/*     */   private boolean prudent = false;
/*     */   
/*  59 */   private FileSize bufferSize = new FileSize(8192L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFile(String file) {
/*  66 */     if (file == null) {
/*  67 */       this.fileName = file;
/*     */     }
/*     */     else {
/*     */       
/*  71 */       this.fileName = file.trim();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAppend() {
/*  79 */     return this.append;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String rawFileProperty() {
/*  89 */     return this.fileName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFile() {
/* 100 */     return this.fileName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 109 */     int errors = 0;
/* 110 */     if (getFile() != null) {
/* 111 */       addInfo("File property is set to [" + this.fileName + "]");
/*     */       
/* 113 */       if (this.prudent && 
/* 114 */         !isAppend()) {
/* 115 */         setAppend(true);
/* 116 */         addWarn("Setting \"Append\" property to true on account of \"Prudent\" mode");
/*     */       } 
/*     */ 
/*     */       
/* 120 */       if (checkForFileCollisionInPreviousFileAppenders()) {
/* 121 */         addError("Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.");
/* 122 */         addError("For more information, please visit " + COLLISION_WITH_EARLIER_APPENDER_URL);
/* 123 */         errors++;
/*     */       } else {
/*     */         
/*     */         try {
/* 127 */           openFile(getFile());
/* 128 */         } catch (IOException e) {
/* 129 */           errors++;
/* 130 */           addError("openFile(" + this.fileName + "," + this.append + ") call failed.", e);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 134 */       errors++;
/* 135 */       addError("\"File\" property not set for appender named [" + this.name + "].");
/*     */     } 
/* 137 */     if (errors == 0) {
/* 138 */       super.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 144 */     super.stop();
/*     */     
/* 146 */     Map<String, String> map = ContextUtil.getFilenameCollisionMap(this.context);
/* 147 */     if (map == null || getName() == null) {
/*     */       return;
/*     */     }
/* 150 */     map.remove(getName());
/*     */   }
/*     */   
/*     */   protected boolean checkForFileCollisionInPreviousFileAppenders() {
/* 154 */     boolean collisionsDetected = false;
/* 155 */     if (this.fileName == null) {
/* 156 */       return false;
/*     */     }
/*     */     
/* 159 */     Map<String, String> map = (Map<String, String>)this.context.getObject("FA_FILENAME_COLLISION_MAP");
/* 160 */     if (map == null) {
/* 161 */       return collisionsDetected;
/*     */     }
/* 163 */     for (Map.Entry<String, String> entry : map.entrySet()) {
/* 164 */       if (this.fileName.equals(entry.getValue())) {
/* 165 */         addErrorForCollision("File", entry.getValue(), entry.getKey());
/* 166 */         collisionsDetected = true;
/*     */       } 
/*     */     } 
/* 169 */     if (this.name != null) {
/* 170 */       map.put(getName(), this.fileName);
/*     */     }
/* 172 */     return collisionsDetected;
/*     */   }
/*     */   
/*     */   protected void addErrorForCollision(String optionName, String optionValue, String appenderName) {
/* 176 */     addError("'" + optionName + "' option has the same value \"" + optionValue + "\" as that given for appender [" + appenderName + "] defined earlier.");
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
/*     */   
/*     */   public void openFile(String file_name) throws IOException {
/* 196 */     this.lock.lock();
/*     */     try {
/* 198 */       File file = new File(file_name);
/* 199 */       boolean result = FileUtil.createMissingParentDirectories(file);
/* 200 */       if (!result) {
/* 201 */         addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
/*     */       }
/*     */       
/* 204 */       ResilientFileOutputStream resilientFos = new ResilientFileOutputStream(file, this.append, this.bufferSize.getSize());
/* 205 */       resilientFos.setContext(this.context);
/* 206 */       setOutputStream((OutputStream)resilientFos);
/*     */     } finally {
/* 208 */       this.lock.unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPrudent() {
/* 218 */     return this.prudent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPrudent(boolean prudent) {
/* 228 */     this.prudent = prudent;
/*     */   }
/*     */   
/*     */   public void setAppend(boolean append) {
/* 232 */     this.append = append;
/*     */   }
/*     */   
/*     */   public void setBufferSize(FileSize bufferSize) {
/* 236 */     addInfo("Setting bufferSize to [" + bufferSize.toString() + "]");
/* 237 */     this.bufferSize = bufferSize;
/*     */   }
/*     */   
/*     */   private void safeWrite(E event) throws IOException {
/* 241 */     ResilientFileOutputStream resilientFOS = (ResilientFileOutputStream)getOutputStream();
/* 242 */     FileChannel fileChannel = resilientFOS.getChannel();
/* 243 */     if (fileChannel == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 248 */     boolean interrupted = Thread.interrupted();
/*     */     
/* 250 */     FileLock fileLock = null;
/*     */     try {
/* 252 */       fileLock = fileChannel.lock();
/* 253 */       long position = fileChannel.position();
/* 254 */       long size = fileChannel.size();
/* 255 */       if (size != position) {
/* 256 */         fileChannel.position(size);
/*     */       }
/* 258 */       super.writeOut(event);
/* 259 */     } catch (IOException e) {
/*     */       
/* 261 */       resilientFOS.postIOFailure(e);
/*     */     } finally {
/* 263 */       if (fileLock != null && fileLock.isValid()) {
/* 264 */         fileLock.release();
/*     */       }
/*     */ 
/*     */       
/* 268 */       if (interrupted) {
/* 269 */         Thread.currentThread().interrupt();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeOut(E event) throws IOException {
/* 276 */     if (this.prudent) {
/* 277 */       safeWrite(event);
/*     */     } else {
/* 279 */       super.writeOut(event);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\FileAppender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */