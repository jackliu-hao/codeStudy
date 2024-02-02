/*     */ package org.h2.message;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ import org.h2.api.ErrorCode;
/*     */ import org.h2.jdbc.JdbcException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TraceSystem
/*     */   implements TraceWriter
/*     */ {
/*     */   public static final int PARENT = -1;
/*     */   public static final int OFF = 0;
/*     */   public static final int ERROR = 1;
/*     */   public static final int INFO = 2;
/*     */   public static final int DEBUG = 3;
/*     */   public static final int ADAPTER = 4;
/*     */   public static final int DEFAULT_TRACE_LEVEL_SYSTEM_OUT = 0;
/*     */   public static final int DEFAULT_TRACE_LEVEL_FILE = 1;
/*     */   private static final int DEFAULT_MAX_FILE_SIZE = 67108864;
/*     */   private static final int CHECK_SIZE_EACH_WRITES = 4096;
/*  79 */   private int levelSystemOut = 0;
/*  80 */   private int levelFile = 1;
/*     */   private int levelMax;
/*  82 */   private int maxFileSize = 67108864;
/*     */   private String fileName;
/*  84 */   private final AtomicReferenceArray<Trace> traces = new AtomicReferenceArray<>(Trace.MODULE_NAMES.length);
/*     */ 
/*     */   
/*     */   private SimpleDateFormat dateFormat;
/*     */ 
/*     */   
/*     */   private Writer fileWriter;
/*     */   
/*     */   private PrintWriter printWriter;
/*     */   
/*  94 */   private int checkSize = -1;
/*     */   private boolean closed;
/*     */   private boolean writingErrorLogged;
/*  97 */   private TraceWriter writer = this;
/*  98 */   private PrintStream sysOut = System.out;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TraceSystem(String paramString) {
/* 106 */     this.fileName = paramString;
/* 107 */     updateLevel();
/*     */   }
/*     */   
/*     */   private void updateLevel() {
/* 111 */     this.levelMax = Math.max(this.levelSystemOut, this.levelFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSysOut(PrintStream paramPrintStream) {
/* 120 */     this.sysOut = paramPrintStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Trace getTrace(int paramInt) {
/* 131 */     Trace trace = this.traces.get(paramInt);
/* 132 */     if (trace == null) {
/* 133 */       trace = new Trace(this.writer, paramInt);
/* 134 */       if (!this.traces.compareAndSet(paramInt, null, trace)) {
/* 135 */         trace = this.traces.get(paramInt);
/*     */       }
/*     */     } 
/* 138 */     return trace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Trace getTrace(String paramString) {
/* 149 */     return new Trace(this.writer, paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabled(int paramInt) {
/* 154 */     if (this.levelMax == 4) {
/* 155 */       return this.writer.isEnabled(paramInt);
/*     */     }
/* 157 */     return (paramInt <= this.levelMax);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFileName(String paramString) {
/* 166 */     this.fileName = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxFileSize(int paramInt) {
/* 175 */     this.maxFileSize = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevelSystemOut(int paramInt) {
/* 184 */     this.levelSystemOut = paramInt;
/* 185 */     updateLevel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevelFile(int paramInt) {
/* 194 */     if (paramInt == 4) {
/* 195 */       String str1 = "org.h2.message.TraceWriterAdapter";
/*     */       try {
/* 197 */         this.writer = Class.forName(str1).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 198 */       } catch (Throwable throwable) {
/* 199 */         throwable = DbException.get(90086, throwable, new String[] { str1 });
/* 200 */         write(1, 2, str1, throwable);
/*     */         return;
/*     */       } 
/* 203 */       String str2 = this.fileName;
/* 204 */       if (str2 != null) {
/* 205 */         if (str2.endsWith(".trace.db")) {
/* 206 */           str2 = str2.substring(0, str2.length() - ".trace.db".length());
/*     */         }
/* 208 */         int i = Math.max(str2.lastIndexOf('/'), str2.lastIndexOf('\\'));
/* 209 */         if (i >= 0) {
/* 210 */           str2 = str2.substring(i + 1);
/*     */         }
/* 212 */         this.writer.setName(str2);
/*     */       } 
/*     */     } 
/* 215 */     this.levelFile = paramInt;
/* 216 */     updateLevel();
/*     */   }
/*     */   
/*     */   public int getLevelFile() {
/* 220 */     return this.levelFile;
/*     */   }
/*     */   
/*     */   private synchronized String format(String paramString1, String paramString2) {
/* 224 */     if (this.dateFormat == null) {
/* 225 */       this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
/*     */     }
/* 227 */     return this.dateFormat.format(Long.valueOf(System.currentTimeMillis())) + paramString1 + ": " + paramString2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int paramInt1, int paramInt2, String paramString, Throwable paramThrowable) {
/* 232 */     write(paramInt1, Trace.MODULE_NAMES[paramInt2], paramString, paramThrowable);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int paramInt, String paramString1, String paramString2, Throwable paramThrowable) {
/* 237 */     if (paramInt <= this.levelSystemOut || paramInt > this.levelMax) {
/*     */ 
/*     */       
/* 240 */       this.sysOut.println(format(paramString1, paramString2));
/* 241 */       if (paramThrowable != null && this.levelSystemOut == 3) {
/* 242 */         paramThrowable.printStackTrace(this.sysOut);
/*     */       }
/*     */     } 
/* 245 */     if (this.fileName != null && 
/* 246 */       paramInt <= this.levelFile) {
/* 247 */       writeFile(format(paramString1, paramString2), paramThrowable);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private synchronized void writeFile(String paramString, Throwable paramThrowable) {
/*     */     try {
/* 254 */       this.checkSize = (this.checkSize + 1) % 4096;
/* 255 */       if (this.checkSize == 0) {
/* 256 */         closeWriter();
/* 257 */         if (this.maxFileSize > 0 && FileUtils.size(this.fileName) > this.maxFileSize) {
/* 258 */           String str = this.fileName + ".old";
/* 259 */           FileUtils.delete(str);
/* 260 */           FileUtils.move(this.fileName, str);
/*     */         } 
/*     */       } 
/* 263 */       if (!openWriter()) {
/*     */         return;
/*     */       }
/* 266 */       this.printWriter.println(paramString);
/* 267 */       if (paramThrowable != null) {
/* 268 */         if (this.levelFile == 1 && paramThrowable instanceof JdbcException) {
/* 269 */           JdbcException jdbcException = (JdbcException)paramThrowable;
/* 270 */           int i = jdbcException.getErrorCode();
/* 271 */           if (ErrorCode.isCommon(i)) {
/* 272 */             this.printWriter.println(paramThrowable);
/*     */           } else {
/* 274 */             paramThrowable.printStackTrace(this.printWriter);
/*     */           } 
/*     */         } else {
/* 277 */           paramThrowable.printStackTrace(this.printWriter);
/*     */         } 
/*     */       }
/* 280 */       this.printWriter.flush();
/* 281 */       if (this.closed) {
/* 282 */         closeWriter();
/*     */       }
/* 284 */     } catch (Exception exception) {
/* 285 */       logWritingError(exception);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void logWritingError(Exception paramException) {
/* 290 */     if (this.writingErrorLogged) {
/*     */       return;
/*     */     }
/* 293 */     this.writingErrorLogged = true;
/* 294 */     DbException dbException = DbException.get(90034, paramException, new String[] { this.fileName, paramException
/* 295 */           .toString() });
/*     */     
/* 297 */     this.fileName = null;
/* 298 */     this.sysOut.println(dbException);
/* 299 */     dbException.printStackTrace();
/*     */   }
/*     */   
/*     */   private boolean openWriter() {
/* 303 */     if (this.printWriter == null) {
/*     */       try {
/* 305 */         FileUtils.createDirectories(FileUtils.getParent(this.fileName));
/* 306 */         if (FileUtils.exists(this.fileName) && !FileUtils.canWrite(this.fileName))
/*     */         {
/*     */           
/* 309 */           return false;
/*     */         }
/* 311 */         this.fileWriter = IOUtils.getBufferedWriter(
/* 312 */             FileUtils.newOutputStream(this.fileName, true));
/* 313 */         this.printWriter = new PrintWriter(this.fileWriter, true);
/* 314 */       } catch (Exception exception) {
/* 315 */         logWritingError(exception);
/* 316 */         return false;
/*     */       } 
/*     */     }
/* 319 */     return true;
/*     */   }
/*     */   
/*     */   private synchronized void closeWriter() {
/* 323 */     if (this.printWriter != null) {
/* 324 */       this.printWriter.flush();
/* 325 */       this.printWriter.close();
/* 326 */       this.printWriter = null;
/*     */     } 
/* 328 */     if (this.fileWriter != null) {
/*     */       try {
/* 330 */         this.fileWriter.close();
/* 331 */       } catch (IOException iOException) {}
/*     */ 
/*     */       
/* 334 */       this.fileWriter = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 344 */     closeWriter();
/* 345 */     this.closed = true;
/*     */   }
/*     */   
/*     */   public void setName(String paramString) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\message\TraceSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */