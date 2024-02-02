/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.BindException;
/*     */ import java.net.ConnectException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.Properties;
/*     */ import org.h2.Driver;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.message.TraceSystem;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.NetUtils;
/*     */ import org.h2.util.SortedProperties;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.Transfer;
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
/*     */ public class FileLock
/*     */   implements Runnable
/*     */ {
/*     */   private static final String MAGIC = "FileLock";
/*     */   private static final String FILE = "file";
/*     */   private static final String SOCKET = "socket";
/*     */   private static final int RANDOM_BYTES = 16;
/*     */   private static final int SLEEP_GAP = 25;
/*     */   private static final int TIME_GRANULARITY = 2000;
/*     */   private volatile String fileName;
/*     */   private volatile ServerSocket serverSocket;
/*     */   private volatile boolean locked;
/*     */   private final int sleep;
/*     */   private final Trace trace;
/*     */   private long lastWrite;
/*     */   private String method;
/*     */   private Properties properties;
/*     */   private String uniqueId;
/*     */   private Thread watchdog;
/*     */   
/*     */   public FileLock(TraceSystem paramTraceSystem, String paramString, int paramInt) {
/*  91 */     this
/*  92 */       .trace = (paramTraceSystem == null) ? null : paramTraceSystem.getTrace(4);
/*  93 */     this.fileName = paramString;
/*  94 */     this.sleep = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void lock(FileLockMethod paramFileLockMethod) {
/* 104 */     checkServer();
/* 105 */     if (this.locked) {
/* 106 */       throw DbException.getInternalError("already locked");
/*     */     }
/* 108 */     switch (paramFileLockMethod) {
/*     */       case FILE:
/* 110 */         lockFile();
/*     */         break;
/*     */       case SOCKET:
/* 113 */         lockSocket();
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 119 */     this.locked = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void unlock() {
/* 127 */     if (!this.locked) {
/*     */       return;
/*     */     }
/* 130 */     this.locked = false;
/*     */     try {
/* 132 */       if (this.watchdog != null) {
/* 133 */         this.watchdog.interrupt();
/*     */       }
/* 135 */     } catch (Exception exception) {
/* 136 */       this.trace.debug(exception, "unlock");
/*     */     } 
/*     */     try {
/* 139 */       if (this.fileName != null && 
/* 140 */         load().equals(this.properties)) {
/* 141 */         FileUtils.delete(this.fileName);
/*     */       }
/*     */       
/* 144 */       if (this.serverSocket != null) {
/* 145 */         this.serverSocket.close();
/*     */       }
/* 147 */     } catch (Exception exception) {
/* 148 */       this.trace.debug(exception, "unlock");
/*     */     } finally {
/* 150 */       this.fileName = null;
/* 151 */       this.serverSocket = null;
/*     */     } 
/*     */     try {
/* 154 */       if (this.watchdog != null) {
/* 155 */         this.watchdog.join();
/*     */       }
/* 157 */     } catch (Exception exception) {
/* 158 */       this.trace.debug(exception, "unlock");
/*     */     } finally {
/* 160 */       this.watchdog = null;
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
/*     */   public void setProperty(String paramString1, String paramString2) {
/* 172 */     if (paramString2 == null) {
/* 173 */       this.properties.remove(paramString1);
/*     */     } else {
/* 175 */       this.properties.put(paramString1, paramString2);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties save() {
/*     */     try {
/* 186 */       try (OutputStream null = FileUtils.newOutputStream(this.fileName, false)) {
/* 187 */         this.properties.store(outputStream, "FileLock");
/*     */       } 
/* 189 */       this.lastWrite = aggressiveLastModified(this.fileName);
/* 190 */       if (this.trace.isDebugEnabled()) {
/* 191 */         this.trace.debug("save " + this.properties);
/*     */       }
/* 193 */       return this.properties;
/* 194 */     } catch (IOException iOException) {
/* 195 */       throw getExceptionFatal("Could not save properties " + this.fileName, iOException);
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
/*     */   private static long aggressiveLastModified(String paramString) {
/* 213 */     try (FileChannel null = FileChannel.open(Paths.get(paramString, new String[0]), FileUtils.RWS, (FileAttribute<?>[])FileUtils.NO_ATTRIBUTES)) {
/* 214 */       ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[1]);
/* 215 */       fileChannel.read(byteBuffer);
/*     */     }
/* 217 */     catch (IOException iOException) {}
/* 218 */     return FileUtils.lastModified(paramString);
/*     */   }
/*     */   
/*     */   private void checkServer() {
/* 222 */     Properties properties = load();
/* 223 */     String str1 = properties.getProperty("server");
/* 224 */     if (str1 == null) {
/*     */       return;
/*     */     }
/* 227 */     boolean bool = false;
/* 228 */     String str2 = properties.getProperty("id");
/*     */     try {
/* 230 */       Socket socket = NetUtils.createSocket(str1, 9092, false);
/*     */       
/* 232 */       Transfer transfer = new Transfer(null, socket);
/* 233 */       transfer.init();
/* 234 */       transfer.writeInt(17);
/* 235 */       transfer.writeInt(20);
/* 236 */       transfer.writeString(null);
/* 237 */       transfer.writeString(null);
/* 238 */       transfer.writeString(str2);
/* 239 */       transfer.writeInt(14);
/* 240 */       transfer.flush();
/* 241 */       int i = transfer.readInt();
/* 242 */       if (i == 1) {
/* 243 */         bool = true;
/*     */       }
/* 245 */       transfer.close();
/* 246 */       socket.close();
/* 247 */     } catch (IOException iOException) {
/*     */       return;
/*     */     } 
/* 250 */     if (bool) {
/* 251 */       DbException dbException = DbException.get(90020, "Server is running");
/*     */       
/* 253 */       throw dbException.addSQL(str1 + "/" + str2);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties load() {
/* 263 */     IOException iOException = null;
/* 264 */     for (byte b = 0; b < 5; b++) {
/*     */       try {
/* 266 */         SortedProperties sortedProperties = SortedProperties.loadProperties(this.fileName);
/* 267 */         if (this.trace.isDebugEnabled()) {
/* 268 */           this.trace.debug("load " + sortedProperties);
/*     */         }
/* 270 */         return (Properties)sortedProperties;
/* 271 */       } catch (IOException iOException1) {
/* 272 */         iOException = iOException1;
/*     */       } 
/*     */     } 
/* 275 */     throw getExceptionFatal("Could not load properties " + this.fileName, iOException);
/*     */   }
/*     */ 
/*     */   
/*     */   private void waitUntilOld() {
/* 280 */     for (byte b = 0; b < ' '; b++) {
/* 281 */       long l1 = aggressiveLastModified(this.fileName);
/* 282 */       long l2 = System.currentTimeMillis() - l1;
/* 283 */       if (l2 < -2000L) {
/*     */ 
/*     */         
/*     */         try {
/* 287 */           Thread.sleep(2L * this.sleep);
/* 288 */         } catch (Exception exception) {
/* 289 */           this.trace.debug(exception, "sleep");
/*     */         }  return;
/*     */       } 
/* 292 */       if (l2 > 2000L) {
/*     */         return;
/*     */       }
/*     */       try {
/* 296 */         Thread.sleep(25L);
/* 297 */       } catch (Exception exception) {
/* 298 */         this.trace.debug(exception, "sleep");
/*     */       } 
/*     */     } 
/* 301 */     throw getExceptionFatal("Lock file recently modified", null);
/*     */   }
/*     */   
/*     */   private void setUniqueId() {
/* 305 */     byte[] arrayOfByte = MathUtils.secureRandomBytes(16);
/* 306 */     String str = StringUtils.convertBytesToHex(arrayOfByte);
/* 307 */     this.uniqueId = Long.toHexString(System.currentTimeMillis()) + str;
/* 308 */     this.properties.setProperty("id", this.uniqueId);
/*     */   }
/*     */   
/*     */   private void lockFile() {
/* 312 */     this.method = "file";
/* 313 */     this.properties = (Properties)new SortedProperties();
/* 314 */     this.properties.setProperty("method", String.valueOf(this.method));
/* 315 */     setUniqueId();
/* 316 */     FileUtils.createDirectories(FileUtils.getParent(this.fileName));
/* 317 */     if (!FileUtils.createFile(this.fileName)) {
/* 318 */       waitUntilOld();
/* 319 */       String str = load().getProperty("method", "file");
/* 320 */       if (!str.equals("file")) {
/* 321 */         throw getExceptionFatal("Unsupported lock method " + str, null);
/*     */       }
/* 323 */       save();
/* 324 */       sleep(2 * this.sleep);
/* 325 */       if (!load().equals(this.properties)) {
/* 326 */         throw getExceptionAlreadyInUse("Locked by another process: " + this.fileName);
/*     */       }
/* 328 */       FileUtils.delete(this.fileName);
/* 329 */       if (!FileUtils.createFile(this.fileName)) {
/* 330 */         throw getExceptionFatal("Another process was faster", null);
/*     */       }
/*     */     } 
/* 333 */     save();
/* 334 */     sleep(25);
/* 335 */     if (!load().equals(this.properties)) {
/* 336 */       this.fileName = null;
/* 337 */       throw getExceptionFatal("Concurrent update", null);
/*     */     } 
/* 339 */     this.locked = true;
/* 340 */     this.watchdog = new Thread(this, "H2 File Lock Watchdog " + this.fileName);
/* 341 */     Driver.setThreadContextClassLoader(this.watchdog);
/* 342 */     this.watchdog.setDaemon(true);
/* 343 */     this.watchdog.setPriority(9);
/* 344 */     this.watchdog.start();
/*     */   }
/*     */   
/*     */   private void lockSocket() {
/* 348 */     this.method = "socket";
/* 349 */     this.properties = (Properties)new SortedProperties();
/* 350 */     this.properties.setProperty("method", String.valueOf(this.method));
/* 351 */     setUniqueId();
/*     */ 
/*     */     
/* 354 */     String str = NetUtils.getLocalAddress();
/* 355 */     FileUtils.createDirectories(FileUtils.getParent(this.fileName));
/* 356 */     if (!FileUtils.createFile(this.fileName)) {
/* 357 */       InetAddress inetAddress; waitUntilOld();
/* 358 */       long l = aggressiveLastModified(this.fileName);
/* 359 */       Properties properties = load();
/* 360 */       String str1 = properties.getProperty("method", "socket");
/* 361 */       if (str1.equals("file")) {
/* 362 */         lockFile(); return;
/*     */       } 
/* 364 */       if (!str1.equals("socket")) {
/* 365 */         throw getExceptionFatal("Unsupported lock method " + str1, null);
/*     */       }
/* 367 */       String str2 = properties.getProperty("ipAddress", str);
/* 368 */       if (!str.equals(str2)) {
/* 369 */         throw getExceptionAlreadyInUse("Locked by another computer: " + str2);
/*     */       }
/* 371 */       String str3 = properties.getProperty("port", "0");
/* 372 */       int i = Integer.parseInt(str3);
/*     */       
/*     */       try {
/* 375 */         inetAddress = InetAddress.getByName(str2);
/* 376 */       } catch (UnknownHostException unknownHostException) {
/* 377 */         throw getExceptionFatal("Unknown host " + str2, unknownHostException);
/*     */       } 
/* 379 */       for (byte b = 0; b < 3; b++) {
/*     */         try {
/* 381 */           Socket socket = new Socket(inetAddress, i);
/* 382 */           socket.close();
/* 383 */           throw getExceptionAlreadyInUse("Locked by another process");
/* 384 */         } catch (BindException bindException) {
/* 385 */           throw getExceptionFatal("Bind Exception", null);
/* 386 */         } catch (ConnectException connectException) {
/* 387 */           this.trace.debug(connectException, "socket not connected to port " + str3);
/* 388 */         } catch (IOException iOException) {
/* 389 */           throw getExceptionFatal("IOException", null);
/*     */         } 
/*     */       } 
/* 392 */       if (l != aggressiveLastModified(this.fileName)) {
/* 393 */         throw getExceptionFatal("Concurrent update", null);
/*     */       }
/* 395 */       FileUtils.delete(this.fileName);
/* 396 */       if (!FileUtils.createFile(this.fileName)) {
/* 397 */         throw getExceptionFatal("Another process was faster", null);
/*     */       }
/*     */     } 
/*     */     
/*     */     try {
/* 402 */       this.serverSocket = NetUtils.createServerSocket(0, false);
/* 403 */       int i = this.serverSocket.getLocalPort();
/* 404 */       this.properties.setProperty("ipAddress", str);
/* 405 */       this.properties.setProperty("port", Integer.toString(i));
/* 406 */     } catch (Exception exception) {
/* 407 */       this.trace.debug(exception, "lock");
/* 408 */       this.serverSocket = null;
/* 409 */       lockFile();
/*     */       return;
/*     */     } 
/* 412 */     save();
/* 413 */     this.locked = true;
/* 414 */     this.watchdog = new Thread(this, "H2 File Lock Watchdog (Socket) " + this.fileName);
/*     */     
/* 416 */     this.watchdog.setDaemon(true);
/* 417 */     this.watchdog.start();
/*     */   }
/*     */   
/*     */   private static void sleep(int paramInt) {
/*     */     try {
/* 422 */       Thread.sleep(paramInt);
/* 423 */     } catch (InterruptedException interruptedException) {
/* 424 */       throw getExceptionFatal("Sleep interrupted", interruptedException);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static DbException getExceptionFatal(String paramString, Throwable paramThrowable) {
/* 429 */     return DbException.get(8000, paramThrowable, new String[] { paramString });
/*     */   }
/*     */ 
/*     */   
/*     */   private DbException getExceptionAlreadyInUse(String paramString) {
/* 434 */     DbException dbException = DbException.get(90020, paramString);
/*     */     
/* 436 */     if (this.fileName != null) {
/*     */       try {
/* 438 */         Properties properties = load();
/* 439 */         String str = properties.getProperty("server");
/* 440 */         if (str != null) {
/* 441 */           String str1 = str + "/" + properties.getProperty("id");
/* 442 */           dbException = dbException.addSQL(str1);
/*     */         } 
/* 444 */       } catch (DbException dbException1) {}
/*     */     }
/*     */ 
/*     */     
/* 448 */     return dbException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileLockMethod getFileLockMethod(String paramString) {
/* 459 */     if (paramString == null || paramString.equalsIgnoreCase("FILE"))
/* 460 */       return FileLockMethod.FILE; 
/* 461 */     if (paramString.equalsIgnoreCase("NO"))
/* 462 */       return FileLockMethod.NO; 
/* 463 */     if (paramString.equalsIgnoreCase("SOCKET"))
/* 464 */       return FileLockMethod.SOCKET; 
/* 465 */     if (paramString.equalsIgnoreCase("FS")) {
/* 466 */       return FileLockMethod.FS;
/*     */     }
/* 468 */     throw DbException.get(90060, paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUniqueId() {
/* 473 */     return this.uniqueId;
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/* 479 */       while (this.locked && this.fileName != null) {
/*     */         
/*     */         try {
/* 482 */           if (!FileUtils.exists(this.fileName) || 
/* 483 */             aggressiveLastModified(this.fileName) != this.lastWrite) {
/* 484 */             save();
/*     */           }
/* 486 */           Thread.sleep(this.sleep);
/* 487 */         } catch (OutOfMemoryError|NullPointerException|InterruptedException outOfMemoryError) {
/*     */         
/* 489 */         } catch (Exception exception) {
/* 490 */           this.trace.debug(exception, "watchdog");
/*     */         } 
/*     */       } 
/*     */       
/*     */       while (true) {
/* 495 */         ServerSocket serverSocket = this.serverSocket;
/* 496 */         if (serverSocket == null) {
/*     */           break;
/*     */         }
/*     */         try {
/* 500 */           this.trace.debug("watchdog accept");
/* 501 */           Socket socket = serverSocket.accept();
/* 502 */           socket.close();
/* 503 */         } catch (Exception exception) {
/* 504 */           this.trace.debug(exception, "watchdog");
/*     */         } 
/*     */       } 
/* 507 */     } catch (Exception exception) {
/* 508 */       this.trace.debug(exception, "watchdog");
/*     */     } 
/* 510 */     this.trace.debug("watchdog end");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\FileLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */