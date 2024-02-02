/*     */ package io.undertow.server.handlers.accesslog;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.ConcurrentLinkedDeque;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
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
/*     */ public class DefaultAccessLogReceiver
/*     */   implements AccessLogReceiver, Runnable, Closeable
/*     */ {
/*     */   private static final String DEFAULT_LOG_SUFFIX = "log";
/*     */   private final Executor logWriteExecutor;
/*     */   private final Deque<String> pendingMessages;
/*  63 */   private volatile int state = 0;
/*     */ 
/*     */   
/*  66 */   private static final AtomicIntegerFieldUpdater<DefaultAccessLogReceiver> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultAccessLogReceiver.class, "state");
/*     */   
/*     */   private long changeOverPoint;
/*     */   
/*     */   private String currentDateString;
/*     */   
/*     */   private boolean forceLogRotation;
/*     */   
/*     */   private final Path outputDirectory;
/*     */   private final Path defaultLogFile;
/*     */   private final String logBaseName;
/*     */   private final String logNameSuffix;
/*  78 */   private BufferedWriter writer = null;
/*     */   
/*     */   private volatile boolean closed = false;
/*     */   private boolean initialRun = true;
/*     */   private final boolean rotate;
/*     */   private final LogFileHeaderGenerator fileHeaderGenerator;
/*     */   
/*     */   public DefaultAccessLogReceiver(Executor logWriteExecutor, File outputDirectory, String logBaseName) {
/*  86 */     this(logWriteExecutor, outputDirectory.toPath(), logBaseName, (String)null);
/*     */   }
/*     */   
/*     */   public DefaultAccessLogReceiver(Executor logWriteExecutor, File outputDirectory, String logBaseName, String logNameSuffix) {
/*  90 */     this(logWriteExecutor, outputDirectory.toPath(), logBaseName, logNameSuffix, true);
/*     */   }
/*     */   
/*     */   public DefaultAccessLogReceiver(Executor logWriteExecutor, File outputDirectory, String logBaseName, String logNameSuffix, boolean rotate) {
/*  94 */     this(logWriteExecutor, outputDirectory.toPath(), logBaseName, logNameSuffix, rotate);
/*     */   }
/*     */   
/*     */   public DefaultAccessLogReceiver(Executor logWriteExecutor, Path outputDirectory, String logBaseName) {
/*  98 */     this(logWriteExecutor, outputDirectory, logBaseName, (String)null);
/*     */   }
/*     */   
/*     */   public DefaultAccessLogReceiver(Executor logWriteExecutor, Path outputDirectory, String logBaseName, String logNameSuffix) {
/* 102 */     this(logWriteExecutor, outputDirectory, logBaseName, logNameSuffix, true);
/*     */   }
/*     */   
/*     */   public DefaultAccessLogReceiver(Executor logWriteExecutor, Path outputDirectory, String logBaseName, String logNameSuffix, boolean rotate) {
/* 106 */     this(logWriteExecutor, outputDirectory, logBaseName, logNameSuffix, rotate, null);
/*     */   }
/*     */   
/*     */   private DefaultAccessLogReceiver(Executor logWriteExecutor, Path outputDirectory, String logBaseName, String logNameSuffix, boolean rotate, LogFileHeaderGenerator fileHeader) {
/* 110 */     this.logWriteExecutor = logWriteExecutor;
/* 111 */     this.outputDirectory = outputDirectory;
/* 112 */     this.logBaseName = logBaseName;
/* 113 */     this.rotate = rotate;
/* 114 */     this.fileHeaderGenerator = fileHeader;
/* 115 */     this.logNameSuffix = (logNameSuffix != null) ? logNameSuffix : "log";
/* 116 */     this.pendingMessages = new ConcurrentLinkedDeque<>();
/* 117 */     this.defaultLogFile = outputDirectory.resolve(logBaseName + this.logNameSuffix);
/* 118 */     calculateChangeOverPoint();
/*     */   }
/*     */   
/*     */   private void calculateChangeOverPoint() {
/* 122 */     Calendar calendar = Calendar.getInstance();
/* 123 */     calendar.set(13, 0);
/* 124 */     calendar.set(12, 0);
/* 125 */     calendar.set(11, 0);
/* 126 */     calendar.add(5, 1);
/* 127 */     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
/* 128 */     this.currentDateString = df.format(new Date());
/*     */     
/* 130 */     if (Files.exists(this.defaultLogFile, new java.nio.file.LinkOption[0])) {
/*     */       try {
/* 132 */         this.currentDateString = df.format(new Date(Files.getLastModifiedTime(this.defaultLogFile, new java.nio.file.LinkOption[0]).toMillis()));
/* 133 */       } catch (IOException iOException) {}
/*     */     }
/*     */ 
/*     */     
/* 137 */     this.changeOverPoint = calendar.getTimeInMillis();
/*     */   }
/*     */ 
/*     */   
/*     */   public void logMessage(String message) {
/* 142 */     this.pendingMessages.add(message);
/* 143 */     int state = stateUpdater.get(this);
/* 144 */     if (state == 0 && 
/* 145 */       stateUpdater.compareAndSet(this, 0, 1)) {
/* 146 */       this.logWriteExecutor.execute(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 156 */     if (!stateUpdater.compareAndSet(this, 1, 2)) {
/*     */       return;
/*     */     }
/* 159 */     if (this.forceLogRotation) {
/* 160 */       doRotate();
/* 161 */     } else if (this.initialRun && Files.exists(this.defaultLogFile, new java.nio.file.LinkOption[0])) {
/*     */       
/* 163 */       long lm = 0L;
/*     */       try {
/* 165 */         lm = Files.getLastModifiedTime(this.defaultLogFile, new java.nio.file.LinkOption[0]).toMillis();
/* 166 */       } catch (IOException e) {
/* 167 */         UndertowLogger.ROOT_LOGGER.errorRotatingAccessLog(e);
/*     */       } 
/* 169 */       Calendar c = Calendar.getInstance();
/* 170 */       c.setTimeInMillis(this.changeOverPoint);
/* 171 */       c.add(5, -1);
/* 172 */       if (lm <= c.getTimeInMillis()) {
/* 173 */         doRotate();
/*     */       }
/*     */     } 
/* 176 */     this.initialRun = false;
/* 177 */     List<String> messages = new ArrayList<>();
/*     */ 
/*     */     
/* 180 */     for (int i = 0; i < 1000; i++) {
/* 181 */       String msg = this.pendingMessages.poll();
/* 182 */       if (msg == null) {
/*     */         break;
/*     */       }
/* 185 */       messages.add(msg);
/*     */     } 
/*     */     try {
/* 188 */       if (!messages.isEmpty()) {
/* 189 */         writeMessage(messages);
/*     */       }
/*     */     } finally {
/*     */       
/* 193 */       stateUpdater.set(this, 3);
/*     */ 
/*     */       
/* 196 */       if ((!this.pendingMessages.isEmpty() || this.forceLogRotation) && 
/* 197 */         stateUpdater.compareAndSet(this, 3, 1)) {
/* 198 */         this.logWriteExecutor.execute(this);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 207 */       if (stateUpdater.compareAndSet(this, 3, 0) && this.closed)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 213 */         if (stateUpdater.compareAndSet(this, 0, 3)) {
/*     */           try {
/* 215 */             if (this.writer != null) {
/* 216 */               this.writer.flush();
/* 217 */               this.writer.close();
/* 218 */               this.writer = null;
/*     */             } 
/* 220 */           } catch (IOException e) {
/* 221 */             UndertowLogger.ROOT_LOGGER.errorWritingAccessLog(e);
/*     */           } finally {
/*     */             
/* 224 */             stateUpdater.set(this, 0);
/*     */           } 
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
/*     */   void awaitWrittenForTest() throws InterruptedException {
/* 238 */     while (!this.pendingMessages.isEmpty() || this.forceLogRotation) {
/* 239 */       Thread.sleep(10L);
/*     */     }
/* 241 */     while (this.state != 0) {
/* 242 */       Thread.sleep(10L);
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeMessage(List<String> messages) {
/* 247 */     if (System.currentTimeMillis() > this.changeOverPoint) {
/* 248 */       doRotate();
/*     */     }
/*     */     try {
/* 251 */       if (this.writer == null) {
/* 252 */         this.writer = Files.newBufferedWriter(this.defaultLogFile, StandardCharsets.UTF_8, new OpenOption[] { StandardOpenOption.APPEND, StandardOpenOption.CREATE });
/* 253 */         if (Files.size(this.defaultLogFile) == 0L && this.fileHeaderGenerator != null) {
/* 254 */           String header = this.fileHeaderGenerator.generateHeader();
/* 255 */           if (header != null) {
/* 256 */             this.writer.write(header);
/* 257 */             this.writer.newLine();
/* 258 */             this.writer.flush();
/*     */           } 
/*     */         } 
/*     */       } 
/* 262 */       for (String message : messages) {
/* 263 */         this.writer.write(message);
/* 264 */         this.writer.newLine();
/*     */       } 
/* 266 */       this.writer.flush();
/* 267 */     } catch (IOException e) {
/* 268 */       UndertowLogger.ROOT_LOGGER.errorWritingAccessLog(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doRotate() {
/* 273 */     this.forceLogRotation = false;
/* 274 */     if (!this.rotate) {
/*     */       return;
/*     */     }
/*     */     try {
/* 278 */       if (this.writer != null) {
/* 279 */         this.writer.flush();
/* 280 */         this.writer.close();
/* 281 */         this.writer = null;
/*     */       } 
/* 283 */       if (!Files.exists(this.defaultLogFile, new java.nio.file.LinkOption[0])) {
/*     */         return;
/*     */       }
/* 286 */       Path newFile = this.outputDirectory.resolve(this.logBaseName + this.currentDateString + "." + this.logNameSuffix);
/* 287 */       int count = 0;
/* 288 */       while (Files.exists(newFile, new java.nio.file.LinkOption[0])) {
/* 289 */         count++;
/* 290 */         newFile = this.outputDirectory.resolve(this.logBaseName + this.currentDateString + "-" + count + "." + this.logNameSuffix);
/*     */       } 
/* 292 */       Files.move(this.defaultLogFile, newFile, new java.nio.file.CopyOption[0]);
/* 293 */     } catch (IOException e) {
/* 294 */       UndertowLogger.ROOT_LOGGER.errorRotatingAccessLog(e);
/*     */     } finally {
/* 296 */       calculateChangeOverPoint();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rotate() {
/* 305 */     this.forceLogRotation = true;
/* 306 */     if (stateUpdater.compareAndSet(this, 0, 1)) {
/* 307 */       this.logWriteExecutor.execute(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 313 */     this.closed = true;
/* 314 */     if (stateUpdater.compareAndSet(this, 0, 1)) {
/* 315 */       this.logWriteExecutor.execute(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/* 320 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static class Builder {
/*     */     private Executor logWriteExecutor;
/*     */     private Path outputDirectory;
/*     */     private String logBaseName;
/*     */     private String logNameSuffix;
/*     */     private boolean rotate;
/*     */     private LogFileHeaderGenerator logFileHeaderGenerator;
/*     */     
/*     */     public Executor getLogWriteExecutor() {
/* 332 */       return this.logWriteExecutor;
/*     */     }
/*     */     
/*     */     public Builder setLogWriteExecutor(Executor logWriteExecutor) {
/* 336 */       this.logWriteExecutor = logWriteExecutor;
/* 337 */       return this;
/*     */     }
/*     */     
/*     */     public Path getOutputDirectory() {
/* 341 */       return this.outputDirectory;
/*     */     }
/*     */     
/*     */     public Builder setOutputDirectory(Path outputDirectory) {
/* 345 */       this.outputDirectory = outputDirectory;
/* 346 */       return this;
/*     */     }
/*     */     
/*     */     public String getLogBaseName() {
/* 350 */       return this.logBaseName;
/*     */     }
/*     */     
/*     */     public Builder setLogBaseName(String logBaseName) {
/* 354 */       this.logBaseName = logBaseName;
/* 355 */       return this;
/*     */     }
/*     */     
/*     */     public String getLogNameSuffix() {
/* 359 */       return this.logNameSuffix;
/*     */     }
/*     */     
/*     */     public Builder setLogNameSuffix(String logNameSuffix) {
/* 363 */       this.logNameSuffix = logNameSuffix;
/* 364 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isRotate() {
/* 368 */       return this.rotate;
/*     */     }
/*     */     
/*     */     public Builder setRotate(boolean rotate) {
/* 372 */       this.rotate = rotate;
/* 373 */       return this;
/*     */     }
/*     */     
/*     */     public LogFileHeaderGenerator getLogFileHeaderGenerator() {
/* 377 */       return this.logFileHeaderGenerator;
/*     */     }
/*     */     
/*     */     public Builder setLogFileHeaderGenerator(LogFileHeaderGenerator logFileHeaderGenerator) {
/* 381 */       this.logFileHeaderGenerator = logFileHeaderGenerator;
/* 382 */       return this;
/*     */     }
/*     */     
/*     */     public DefaultAccessLogReceiver build() {
/* 386 */       return new DefaultAccessLogReceiver(this.logWriteExecutor, this.outputDirectory, this.logBaseName, this.logNameSuffix, this.rotate, this.logFileHeaderGenerator);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\accesslog\DefaultAccessLogReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */