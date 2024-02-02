/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.api.DatabaseEventListener;
/*     */ import org.h2.api.JavaObjectSerializer;
/*     */ import org.h2.command.CommandInterface;
/*     */ import org.h2.command.CommandRemote;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.jdbc.JdbcException;
/*     */ import org.h2.jdbc.meta.DatabaseMeta;
/*     */ import org.h2.jdbc.meta.DatabaseMetaLegacy;
/*     */ import org.h2.jdbc.meta.DatabaseMetaRemote;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.message.TraceSystem;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.store.FileStore;
/*     */ import org.h2.store.LobStorageFrontend;
/*     */ import org.h2.store.LobStorageInterface;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.NetUtils;
/*     */ import org.h2.util.NetworkConnectionInfo;
/*     */ import org.h2.util.SmallLRUCache;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.TempFileDeleter;
/*     */ import org.h2.util.TimeZoneProvider;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.Transfer;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueLob;
/*     */ import org.h2.value.ValueTimestampTimeZone;
/*     */ import org.h2.value.ValueVarchar;
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
/*     */ public final class SessionRemote
/*     */   extends Session
/*     */   implements DataHandler
/*     */ {
/*     */   public static final int SESSION_PREPARE = 0;
/*     */   public static final int SESSION_CLOSE = 1;
/*     */   public static final int COMMAND_EXECUTE_QUERY = 2;
/*     */   public static final int COMMAND_EXECUTE_UPDATE = 3;
/*     */   public static final int COMMAND_CLOSE = 4;
/*     */   public static final int RESULT_FETCH_ROWS = 5;
/*     */   public static final int RESULT_RESET = 6;
/*     */   public static final int RESULT_CLOSE = 7;
/*     */   public static final int COMMAND_COMMIT = 8;
/*     */   public static final int CHANGE_ID = 9;
/*     */   public static final int COMMAND_GET_META_DATA = 10;
/*     */   public static final int SESSION_SET_ID = 12;
/*     */   public static final int SESSION_CANCEL_STATEMENT = 13;
/*     */   public static final int SESSION_CHECK_KEY = 14;
/*     */   public static final int SESSION_SET_AUTOCOMMIT = 15;
/*     */   public static final int SESSION_HAS_PENDING_TRANSACTION = 16;
/*     */   public static final int LOB_READ = 17;
/*     */   public static final int SESSION_PREPARE_READ_PARAMS2 = 18;
/*     */   public static final int GET_JDBC_META = 19;
/*     */   public static final int STATUS_ERROR = 0;
/*     */   public static final int STATUS_OK = 1;
/*     */   public static final int STATUS_CLOSED = 2;
/*     */   public static final int STATUS_OK_STATE_CHANGED = 3;
/*     */   private TraceSystem traceSystem;
/*     */   private Trace trace;
/*  84 */   private ArrayList<Transfer> transferList = Utils.newSmallArrayList();
/*     */   private int nextId;
/*     */   private boolean autoCommit = true;
/*     */   private ConnectionInfo connectionInfo;
/*     */   private String databaseName;
/*     */   private String cipher;
/*     */   private byte[] fileEncryptionKey;
/*  91 */   private final Object lobSyncObject = new Object();
/*     */   
/*     */   private String sessionId;
/*     */   
/*     */   private int clientVersion;
/*     */   private boolean autoReconnect;
/*     */   private int lastReconnect;
/*     */   private Session embedded;
/*     */   private DatabaseEventListener eventListener;
/*     */   private LobStorageFrontend lobStorage;
/*     */   private boolean cluster;
/*     */   private TempFileDeleter tempFileDeleter;
/*     */   private JavaObjectSerializer javaObjectSerializer;
/* 104 */   private final CompareMode compareMode = CompareMode.getInstance(null, 0);
/*     */   
/*     */   private final boolean oldInformationSchema;
/*     */   
/*     */   private String currentSchemaName;
/*     */   
/*     */   private volatile Session.DynamicSettings dynamicSettings;
/*     */   
/*     */   public SessionRemote(ConnectionInfo paramConnectionInfo) {
/* 113 */     this.connectionInfo = paramConnectionInfo;
/* 114 */     this.oldInformationSchema = paramConnectionInfo.getProperty("OLD_INFORMATION_SCHEMA", false);
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<String> getClusterServers() {
/* 119 */     ArrayList<String> arrayList = new ArrayList();
/* 120 */     for (Transfer transfer : this.transferList) {
/* 121 */       arrayList.add(transfer.getSocket().getInetAddress()
/* 122 */           .getHostAddress() + ":" + transfer
/* 123 */           .getSocket().getPort());
/*     */     }
/* 125 */     return arrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   private Transfer initTransfer(ConnectionInfo paramConnectionInfo, String paramString1, String paramString2) throws IOException {
/* 130 */     Socket socket = NetUtils.createSocket(paramString2, 9092, paramConnectionInfo.isSSL(), paramConnectionInfo
/* 131 */         .getProperty("NETWORK_TIMEOUT", 0));
/* 132 */     Transfer transfer = new Transfer(this, socket);
/* 133 */     transfer.setSSL(paramConnectionInfo.isSSL());
/* 134 */     transfer.init();
/* 135 */     transfer.writeInt(17);
/* 136 */     transfer.writeInt(20);
/* 137 */     transfer.writeString(paramString1);
/* 138 */     transfer.writeString(paramConnectionInfo.getOriginalURL());
/* 139 */     transfer.writeString(paramConnectionInfo.getUserName());
/* 140 */     transfer.writeBytes(paramConnectionInfo.getUserPasswordHash());
/* 141 */     transfer.writeBytes(paramConnectionInfo.getFilePasswordHash());
/* 142 */     String[] arrayOfString = paramConnectionInfo.getKeys();
/* 143 */     transfer.writeInt(arrayOfString.length);
/* 144 */     for (String str : arrayOfString) {
/* 145 */       transfer.writeString(str).writeString(paramConnectionInfo.getProperty(str));
/*     */     }
/*     */     try {
/* 148 */       done(transfer);
/* 149 */       this.clientVersion = transfer.readInt();
/* 150 */       transfer.setVersion(this.clientVersion);
/* 151 */       if (paramConnectionInfo.getFileEncryptionKey() != null) {
/* 152 */         transfer.writeBytes(paramConnectionInfo.getFileEncryptionKey());
/*     */       }
/* 154 */       transfer.writeInt(12);
/* 155 */       transfer.writeString(this.sessionId);
/* 156 */       if (this.clientVersion >= 20) {
/* 157 */         TimeZoneProvider timeZoneProvider = paramConnectionInfo.getTimeZone();
/* 158 */         if (timeZoneProvider == null) {
/* 159 */           timeZoneProvider = DateTimeUtils.getTimeZone();
/*     */         }
/* 161 */         transfer.writeString(timeZoneProvider.getId());
/*     */       } 
/* 163 */       done(transfer);
/* 164 */       this.autoCommit = transfer.readBoolean();
/* 165 */       return transfer;
/* 166 */     } catch (DbException dbException) {
/* 167 */       transfer.close();
/* 168 */       throw dbException;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasPendingTransaction() {
/* 174 */     for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/* 175 */       Transfer transfer = this.transferList.get(b1);
/*     */       try {
/* 177 */         traceOperation("SESSION_HAS_PENDING_TRANSACTION", 0);
/* 178 */         transfer.writeInt(16);
/*     */         
/* 180 */         done(transfer);
/* 181 */         return (transfer.readInt() != 0);
/* 182 */       } catch (IOException iOException) {
/* 183 */         removeServer(iOException, b1--, ++b2);
/*     */       } 
/*     */     } 
/* 186 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancel() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancelStatement(int paramInt) {
/* 202 */     for (Transfer transfer : this.transferList) {
/*     */       try {
/* 204 */         Transfer transfer1 = transfer.openNewConnection();
/* 205 */         transfer1.init();
/* 206 */         transfer1.writeInt(this.clientVersion);
/* 207 */         transfer1.writeInt(this.clientVersion);
/* 208 */         transfer1.writeString(null);
/* 209 */         transfer1.writeString(null);
/* 210 */         transfer1.writeString(this.sessionId);
/* 211 */         transfer1.writeInt(13);
/* 212 */         transfer1.writeInt(paramInt);
/* 213 */         transfer1.close();
/* 214 */       } catch (IOException iOException) {
/* 215 */         this.trace.debug(iOException, "could not cancel statement");
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkClusterDisableAutoCommit(String paramString) {
/* 221 */     if (this.autoCommit && this.transferList.size() > 1) {
/* 222 */       setAutoCommitSend(false);
/* 223 */       CommandInterface commandInterface = prepareCommand("SET CLUSTER " + paramString, 2147483647);
/*     */ 
/*     */       
/* 226 */       commandInterface.executeUpdate(null);
/*     */       
/* 228 */       this.autoCommit = true;
/* 229 */       this.cluster = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getClientVersion() {
/* 239 */     return this.clientVersion;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getAutoCommit() {
/* 244 */     return this.autoCommit;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAutoCommit(boolean paramBoolean) {
/* 249 */     if (!this.cluster) {
/* 250 */       setAutoCommitSend(paramBoolean);
/*     */     }
/* 252 */     this.autoCommit = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setAutoCommitFromServer(boolean paramBoolean) {
/* 256 */     if (this.cluster) {
/* 257 */       if (paramBoolean) {
/*     */         
/* 259 */         setAutoCommitSend(false);
/* 260 */         this.autoCommit = true;
/*     */       } 
/*     */     } else {
/* 263 */       this.autoCommit = paramBoolean;
/*     */     } 
/*     */   }
/*     */   
/*     */   private synchronized void setAutoCommitSend(boolean paramBoolean) {
/* 268 */     for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/* 269 */       Transfer transfer = this.transferList.get(b1);
/*     */       try {
/* 271 */         traceOperation("SESSION_SET_AUTOCOMMIT", paramBoolean ? 1 : 0);
/* 272 */         transfer.writeInt(15)
/* 273 */           .writeBoolean(paramBoolean);
/* 274 */         done(transfer);
/* 275 */       } catch (IOException iOException) {
/* 276 */         removeServer(iOException, b1--, ++b2);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void autoCommitIfCluster() {
/* 285 */     if (this.autoCommit && this.cluster)
/*     */     {
/*     */ 
/*     */       
/* 289 */       for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/* 290 */         Transfer transfer = this.transferList.get(b1);
/*     */         try {
/* 292 */           traceOperation("COMMAND_COMMIT", 0);
/* 293 */           transfer.writeInt(8);
/* 294 */           done(transfer);
/* 295 */         } catch (IOException iOException) {
/* 296 */           removeServer(iOException, b1--, ++b2);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private String getFilePrefix(String paramString) {
/* 303 */     StringBuilder stringBuilder = new StringBuilder(paramString);
/* 304 */     stringBuilder.append('/');
/* 305 */     for (byte b = 0; b < this.databaseName.length(); b++) {
/* 306 */       char c = this.databaseName.charAt(b);
/* 307 */       if (Character.isLetterOrDigit(c)) {
/* 308 */         stringBuilder.append(c);
/*     */       } else {
/* 310 */         stringBuilder.append('_');
/*     */       } 
/*     */     } 
/* 313 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session connectEmbeddedOrServer(boolean paramBoolean) {
/* 323 */     ConnectionInfo connectionInfo1 = this.connectionInfo;
/* 324 */     if (connectionInfo1.isRemote()) {
/* 325 */       connectServer(connectionInfo1);
/* 326 */       return this;
/*     */     } 
/* 328 */     boolean bool = connectionInfo1.getProperty("AUTO_SERVER", false);
/* 329 */     ConnectionInfo connectionInfo2 = null;
/*     */     try {
/* 331 */       if (bool) {
/* 332 */         connectionInfo2 = connectionInfo1.clone();
/* 333 */         this.connectionInfo = connectionInfo1.clone();
/*     */       } 
/* 335 */       if (paramBoolean) {
/* 336 */         connectionInfo1.setProperty("OPEN_NEW", "true");
/*     */       }
/* 338 */       return Engine.createSession(connectionInfo1);
/* 339 */     } catch (Exception exception) {
/* 340 */       DbException dbException = DbException.convert(exception);
/* 341 */       if (dbException.getErrorCode() == 90020 && 
/* 342 */         bool) {
/* 343 */         String str = ((JdbcException)dbException.getSQLException()).getSQL();
/* 344 */         if (str != null) {
/* 345 */           connectionInfo2.setServerKey(str);
/*     */ 
/*     */ 
/*     */           
/* 349 */           connectionInfo2.removeProperty("OPEN_NEW", (String)null);
/* 350 */           connectServer(connectionInfo2);
/* 351 */           return this;
/*     */         } 
/*     */       } 
/*     */       
/* 355 */       throw dbException;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void connectServer(ConnectionInfo paramConnectionInfo) {
/* 360 */     String str1 = paramConnectionInfo.getName();
/* 361 */     if (str1.startsWith("//")) {
/* 362 */       str1 = str1.substring("//".length());
/*     */     }
/* 364 */     int i = str1.indexOf('/');
/* 365 */     if (i < 0) {
/* 366 */       throw paramConnectionInfo.getFormatException();
/*     */     }
/* 368 */     this.databaseName = str1.substring(i + 1);
/* 369 */     String str2 = str1.substring(0, i);
/* 370 */     this.traceSystem = new TraceSystem(null);
/* 371 */     String str3 = paramConnectionInfo.getProperty(9, (String)null);
/*     */     
/* 373 */     if (str3 != null) {
/* 374 */       int k = Integer.parseInt(str3);
/* 375 */       String str = getFilePrefix(SysProperties.CLIENT_TRACE_DIRECTORY);
/*     */       
/*     */       try {
/* 378 */         this.traceSystem.setLevelFile(k);
/* 379 */         if (k > 0 && k < 4) {
/* 380 */           String str6 = FileUtils.createTempFile(str, ".trace.db", false);
/*     */           
/* 382 */           this.traceSystem.setFileName(str6);
/*     */         } 
/* 384 */       } catch (IOException iOException) {
/* 385 */         throw DbException.convertIOException(iOException, str);
/*     */       } 
/*     */     } 
/* 388 */     String str4 = paramConnectionInfo.getProperty(8, (String)null);
/*     */     
/* 390 */     if (str4 != null) {
/* 391 */       int k = Integer.parseInt(str4);
/* 392 */       this.traceSystem.setLevelSystemOut(k);
/*     */     } 
/* 394 */     this.trace = this.traceSystem.getTrace(6);
/* 395 */     String str5 = null;
/* 396 */     if (str2.indexOf(',') >= 0) {
/* 397 */       str5 = StringUtils.quoteStringSQL(str2);
/* 398 */       paramConnectionInfo.setProperty("CLUSTER", "TRUE");
/*     */     } 
/* 400 */     this.autoReconnect = paramConnectionInfo.getProperty("AUTO_RECONNECT", false);
/*     */     
/* 402 */     boolean bool = paramConnectionInfo.getProperty("AUTO_SERVER", false);
/* 403 */     if (bool && str5 != null)
/*     */     {
/* 405 */       throw DbException.getUnsupportedException("autoServer && serverList != null");
/*     */     }
/* 407 */     this.autoReconnect |= bool;
/* 408 */     if (this.autoReconnect) {
/* 409 */       String str = paramConnectionInfo.getProperty("DATABASE_EVENT_LISTENER");
/* 410 */       if (str != null) {
/* 411 */         str = StringUtils.trim(str, true, true, "'");
/*     */         try {
/* 413 */           this
/* 414 */             .eventListener = JdbcUtils.loadUserClass(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 415 */         } catch (Throwable throwable) {
/* 416 */           throw DbException.convert(throwable);
/*     */         } 
/*     */       } 
/*     */     } 
/* 420 */     this.cipher = paramConnectionInfo.getProperty("CIPHER");
/* 421 */     if (this.cipher != null) {
/* 422 */       this.fileEncryptionKey = MathUtils.secureRandomBytes(32);
/*     */     }
/* 424 */     String[] arrayOfString = StringUtils.arraySplit(str2, ',', true);
/* 425 */     int j = arrayOfString.length;
/* 426 */     this.transferList.clear();
/* 427 */     this.sessionId = StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
/*     */     
/* 429 */     boolean bool1 = false;
/*     */     try {
/* 431 */       for (String str : arrayOfString) {
/*     */         try {
/* 433 */           Transfer transfer = initTransfer(paramConnectionInfo, this.databaseName, str);
/* 434 */           this.transferList.add(transfer);
/* 435 */         } catch (IOException iOException) {
/* 436 */           if (j == 1) {
/* 437 */             throw DbException.get(90067, iOException, new String[] { iOException + ": " + str });
/*     */           }
/* 439 */           bool1 = true;
/*     */         } 
/*     */       } 
/* 442 */       checkClosed();
/* 443 */       if (bool1) {
/* 444 */         switchOffCluster();
/*     */       }
/* 446 */       checkClusterDisableAutoCommit(str5);
/* 447 */     } catch (DbException dbException) {
/* 448 */       this.traceSystem.close();
/* 449 */       throw dbException;
/*     */     } 
/* 451 */     getDynamicSettings();
/*     */   }
/*     */   
/*     */   private void switchOffCluster() {
/* 455 */     CommandInterface commandInterface = prepareCommand("SET CLUSTER ''", 2147483647);
/* 456 */     commandInterface.executeUpdate(null);
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
/*     */   public void removeServer(IOException paramIOException, int paramInt1, int paramInt2) {
/* 468 */     this.trace.debug(paramIOException, "removing server because of exception");
/* 469 */     this.transferList.remove(paramInt1);
/* 470 */     if (this.transferList.isEmpty() && autoReconnect(paramInt2)) {
/*     */       return;
/*     */     }
/* 473 */     checkClosed();
/* 474 */     switchOffCluster();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized CommandInterface prepareCommand(String paramString, int paramInt) {
/* 479 */     checkClosed();
/* 480 */     return (CommandInterface)new CommandRemote(this, this.transferList, paramString, paramInt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean autoReconnect(int paramInt) {
/* 490 */     if (!isClosed()) {
/* 491 */       return false;
/*     */     }
/* 493 */     if (!this.autoReconnect) {
/* 494 */       return false;
/*     */     }
/* 496 */     if (!this.cluster && !this.autoCommit) {
/* 497 */       return false;
/*     */     }
/* 499 */     if (paramInt > SysProperties.MAX_RECONNECT) {
/* 500 */       return false;
/*     */     }
/* 502 */     this.lastReconnect++;
/*     */     while (true) {
/*     */       try {
/* 505 */         this.embedded = connectEmbeddedOrServer(false);
/*     */         break;
/* 507 */       } catch (DbException dbException) {
/* 508 */         if (dbException.getErrorCode() != 90135) {
/* 509 */           throw dbException;
/*     */         }
/*     */         
/*     */         try {
/* 513 */           Thread.sleep(500L);
/* 514 */         } catch (Exception exception) {}
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 519 */     if (this.embedded == this) {
/*     */       
/* 521 */       this.embedded = null;
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 526 */       connectEmbeddedOrServer(true);
/*     */     } 
/* 528 */     recreateSessionState();
/* 529 */     if (this.eventListener != null) {
/* 530 */       this.eventListener.setProgress(4, this.databaseName, paramInt, SysProperties.MAX_RECONNECT);
/*     */     }
/*     */     
/* 533 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkClosed() {
/* 542 */     if (isClosed()) {
/* 543 */       throw DbException.get(90067, "session closed");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 549 */     RuntimeException runtimeException = null;
/* 550 */     if (this.transferList != null) {
/* 551 */       synchronized (this) {
/* 552 */         for (Transfer transfer : this.transferList) {
/*     */           try {
/* 554 */             traceOperation("SESSION_CLOSE", 0);
/* 555 */             transfer.writeInt(1);
/* 556 */             done(transfer);
/* 557 */             transfer.close();
/* 558 */           } catch (RuntimeException runtimeException1) {
/* 559 */             this.trace.error(runtimeException1, "close");
/* 560 */             runtimeException = runtimeException1;
/* 561 */           } catch (Exception exception) {
/* 562 */             this.trace.error(exception, "close");
/*     */           } 
/*     */         } 
/*     */       } 
/* 566 */       this.transferList = null;
/*     */     } 
/* 568 */     this.traceSystem.close();
/* 569 */     if (this.embedded != null) {
/* 570 */       this.embedded.close();
/* 571 */       this.embedded = null;
/*     */     } 
/* 573 */     if (runtimeException != null) {
/* 574 */       throw runtimeException;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Trace getTrace() {
/* 580 */     return this.traceSystem.getTrace(6);
/*     */   }
/*     */   
/*     */   public int getNextId() {
/* 584 */     return this.nextId++;
/*     */   }
/*     */   
/*     */   public int getCurrentId() {
/* 588 */     return this.nextId;
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
/*     */   public void done(Transfer paramTransfer) throws IOException {
/* 602 */     paramTransfer.flush();
/* 603 */     int i = paramTransfer.readInt();
/* 604 */     switch (i) {
/*     */       case 0:
/* 606 */         throw readException(paramTransfer);
/*     */       case 1:
/*     */         return;
/*     */       case 2:
/* 610 */         this.transferList = null;
/*     */       
/*     */       case 3:
/* 613 */         this.sessionStateChanged = true;
/* 614 */         this.currentSchemaName = null;
/* 615 */         this.dynamicSettings = null;
/*     */     } 
/*     */     
/* 618 */     throw DbException.get(90067, "unexpected status " + i);
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
/*     */   public static DbException readException(Transfer paramTransfer) throws IOException {
/* 632 */     String str1 = paramTransfer.readString();
/* 633 */     String str2 = paramTransfer.readString();
/* 634 */     String str3 = paramTransfer.readString();
/* 635 */     int i = paramTransfer.readInt();
/* 636 */     String str4 = paramTransfer.readString();
/* 637 */     SQLException sQLException = DbException.getJdbcSQLException(str2, str3, str1, i, null, str4);
/* 638 */     if (i == 90067)
/*     */     {
/* 640 */       throw new IOException(sQLException.toString(), sQLException);
/*     */     }
/* 642 */     return DbException.convert(sQLException);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClustered() {
/* 651 */     return this.cluster;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 656 */     return (this.transferList == null || this.transferList.isEmpty());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void traceOperation(String paramString, int paramInt) {
/* 666 */     if (this.trace.isDebugEnabled()) {
/* 667 */       this.trace.debug("{0} {1}", new Object[] { paramString, Integer.valueOf(paramInt) });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkPowerOff() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkWritingAllowed() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatabasePath() {
/* 683 */     return "";
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxLengthInplaceLob() {
/* 688 */     return SysProperties.LOB_CLIENT_MAX_SIZE_MEMORY;
/*     */   }
/*     */   
/*     */   public FileStore openFile(String paramString1, String paramString2, boolean paramBoolean) {
/*     */     FileStore fileStore;
/* 693 */     if (paramBoolean && !FileUtils.exists(paramString1)) {
/* 694 */       throw DbException.get(90124, paramString1);
/*     */     }
/*     */     
/* 697 */     if (this.cipher == null) {
/* 698 */       fileStore = FileStore.open(this, paramString1, paramString2);
/*     */     } else {
/* 700 */       fileStore = FileStore.open(this, paramString1, paramString2, this.cipher, this.fileEncryptionKey, 0);
/*     */     } 
/* 702 */     fileStore.setCheckedWriting(false);
/*     */     try {
/* 704 */       fileStore.init();
/* 705 */     } catch (DbException dbException) {
/* 706 */       fileStore.closeSilently();
/* 707 */       throw dbException;
/*     */     } 
/* 709 */     return fileStore;
/*     */   }
/*     */ 
/*     */   
/*     */   public DataHandler getDataHandler() {
/* 714 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getLobSyncObject() {
/* 719 */     return this.lobSyncObject;
/*     */   }
/*     */ 
/*     */   
/*     */   public SmallLRUCache<String, String[]> getLobFileListCache() {
/* 724 */     return null;
/*     */   }
/*     */   
/*     */   public int getLastReconnect() {
/* 728 */     return this.lastReconnect;
/*     */   }
/*     */ 
/*     */   
/*     */   public TempFileDeleter getTempFileDeleter() {
/* 733 */     if (this.tempFileDeleter == null) {
/* 734 */       this.tempFileDeleter = TempFileDeleter.getInstance();
/*     */     }
/* 736 */     return this.tempFileDeleter;
/*     */   }
/*     */ 
/*     */   
/*     */   public LobStorageFrontend getLobStorage() {
/* 741 */     if (this.lobStorage == null) {
/* 742 */       this.lobStorage = new LobStorageFrontend(this);
/*     */     }
/* 744 */     return this.lobStorage;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized int readLob(long paramLong1, byte[] paramArrayOfbyte1, long paramLong2, byte[] paramArrayOfbyte2, int paramInt1, int paramInt2) {
/* 750 */     checkClosed();
/* 751 */     for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/* 752 */       Transfer transfer = this.transferList.get(b1);
/*     */       try {
/* 754 */         traceOperation("LOB_READ", (int)paramLong1);
/* 755 */         transfer.writeInt(17);
/* 756 */         transfer.writeLong(paramLong1);
/* 757 */         transfer.writeBytes(paramArrayOfbyte1);
/* 758 */         transfer.writeLong(paramLong2);
/* 759 */         transfer.writeInt(paramInt2);
/* 760 */         done(transfer);
/* 761 */         paramInt2 = transfer.readInt();
/* 762 */         if (paramInt2 <= 0) {
/* 763 */           return paramInt2;
/*     */         }
/* 765 */         transfer.readBytes(paramArrayOfbyte2, paramInt1, paramInt2);
/* 766 */         return paramInt2;
/* 767 */       } catch (IOException iOException) {
/* 768 */         removeServer(iOException, b1--, ++b2);
/*     */       } 
/*     */     } 
/* 771 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public JavaObjectSerializer getJavaObjectSerializer() {
/* 776 */     if (this.dynamicSettings == null) {
/* 777 */       getDynamicSettings();
/*     */     }
/* 779 */     return this.javaObjectSerializer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueLob addTemporaryLob(ValueLob paramValueLob) {
/* 785 */     return paramValueLob;
/*     */   }
/*     */ 
/*     */   
/*     */   public CompareMode getCompareMode() {
/* 790 */     return this.compareMode;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRemote() {
/* 795 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCurrentSchemaName() {
/* 800 */     String str = this.currentSchemaName;
/* 801 */     if (str == null) {
/* 802 */       synchronized (this) {
/* 803 */         try(CommandInterface null = prepareCommand("CALL SCHEMA()", 1); 
/* 804 */             ResultInterface null = commandInterface.executeQuery(1L, false)) {
/* 805 */           resultInterface.next();
/* 806 */           this.currentSchemaName = str = resultInterface.currentRow()[0].getString();
/*     */         } 
/*     */       } 
/*     */     }
/* 810 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void setCurrentSchemaName(String paramString) {
/* 815 */     this.currentSchemaName = null;
/* 816 */     try (CommandInterface null = prepareCommand(
/* 817 */           StringUtils.quoteIdentifier(new StringBuilder("SET SCHEMA "), paramString).toString(), 0)) {
/* 818 */       commandInterface.executeUpdate(null);
/* 819 */       this.currentSchemaName = paramString;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNetworkConnectionInfo(NetworkConnectionInfo paramNetworkConnectionInfo) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public IsolationLevel getIsolationLevel() {
/* 830 */     if (this.clientVersion >= 19) {
/* 831 */       try(CommandInterface null = prepareCommand(!isOldInformationSchema() ? "SELECT ISOLATION_LEVEL FROM INFORMATION_SCHEMA.SESSIONS WHERE SESSION_ID = SESSION_ID()" : "SELECT ISOLATION_LEVEL FROM INFORMATION_SCHEMA.SESSIONS WHERE ID = SESSION_ID()", 1); 
/*     */ 
/*     */           
/* 834 */           ResultInterface null = commandInterface1.executeQuery(1L, false)) {
/* 835 */         resultInterface.next();
/* 836 */         return IsolationLevel.fromSql(resultInterface.currentRow()[0].getString());
/*     */       } 
/*     */     }
/* 839 */     try(CommandInterface null = prepareCommand("CALL LOCK_MODE()", 1); 
/* 840 */         ResultInterface null = commandInterface.executeQuery(1L, false)) {
/* 841 */       resultInterface.next();
/* 842 */       return IsolationLevel.fromLockMode(resultInterface.currentRow()[0].getInt());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIsolationLevel(IsolationLevel paramIsolationLevel) {
/* 849 */     if (this.clientVersion >= 19) {
/* 850 */       try (CommandInterface null = prepareCommand("SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL " + paramIsolationLevel
/* 851 */             .getSQL(), 0)) {
/* 852 */         commandInterface.executeUpdate(null);
/*     */       } 
/*     */     } else {
/* 855 */       try (CommandInterface null = prepareCommand("SET LOCK_MODE ?", 0)) {
/* 856 */         ((ParameterInterface)commandInterface.getParameters().get(0)).setValue((Value)ValueInteger.get(paramIsolationLevel.getLockMode()), false);
/* 857 */         commandInterface.executeUpdate(null);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Session.StaticSettings getStaticSettings() {
/* 864 */     Session.StaticSettings staticSettings = this.staticSettings;
/* 865 */     if (staticSettings == null) {
/* 866 */       boolean bool1 = true, bool2 = false, bool3 = false;
/* 867 */       try (CommandInterface null = getSettingsCommand(" IN (?, ?, ?)")) {
/* 868 */         ArrayList<ParameterInterface> arrayList = commandInterface.getParameters();
/* 869 */         ((ParameterInterface)arrayList.get(0)).setValue(ValueVarchar.get("DATABASE_TO_UPPER"), false);
/* 870 */         ((ParameterInterface)arrayList.get(1)).setValue(ValueVarchar.get("DATABASE_TO_LOWER"), false);
/* 871 */         ((ParameterInterface)arrayList.get(2)).setValue(ValueVarchar.get("CASE_INSENSITIVE_IDENTIFIERS"), false);
/* 872 */         try (ResultInterface null = commandInterface.executeQuery(0L, false)) {
/* 873 */           while (resultInterface.next()) {
/* 874 */             Value[] arrayOfValue = resultInterface.currentRow();
/* 875 */             String str = arrayOfValue[1].getString();
/* 876 */             switch (arrayOfValue[0].getString()) {
/*     */               case "DATABASE_TO_UPPER":
/* 878 */                 bool1 = Boolean.valueOf(str).booleanValue();
/*     */               
/*     */               case "DATABASE_TO_LOWER":
/* 881 */                 bool2 = Boolean.valueOf(str).booleanValue();
/*     */               
/*     */               case "CASE_INSENSITIVE_IDENTIFIERS":
/* 884 */                 bool3 = Boolean.valueOf(str).booleanValue();
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 889 */       if (this.clientVersion < 18) {
/* 890 */         bool3 = !bool1;
/*     */       }
/* 892 */       this.staticSettings = staticSettings = new Session.StaticSettings(bool1, bool2, bool3);
/*     */     } 
/*     */     
/* 895 */     return staticSettings;
/*     */   }
/*     */ 
/*     */   
/*     */   public Session.DynamicSettings getDynamicSettings() {
/* 900 */     Session.DynamicSettings dynamicSettings = this.dynamicSettings;
/* 901 */     if (dynamicSettings == null) {
/* 902 */       String str1 = Mode.ModeEnum.REGULAR.name();
/* 903 */       TimeZoneProvider timeZoneProvider = DateTimeUtils.getTimeZone();
/* 904 */       String str2 = null;
/* 905 */       try (CommandInterface null = getSettingsCommand(" IN (?, ?, ?)")) {
/* 906 */         ArrayList<ParameterInterface> arrayList = commandInterface.getParameters();
/* 907 */         ((ParameterInterface)arrayList.get(0)).setValue(ValueVarchar.get("MODE"), false);
/* 908 */         ((ParameterInterface)arrayList.get(1)).setValue(ValueVarchar.get("TIME ZONE"), false);
/* 909 */         ((ParameterInterface)arrayList.get(2)).setValue(ValueVarchar.get("JAVA_OBJECT_SERIALIZER"), false);
/* 910 */         try (ResultInterface null = commandInterface.executeQuery(0L, false)) {
/* 911 */           while (resultInterface.next()) {
/* 912 */             Value[] arrayOfValue = resultInterface.currentRow();
/* 913 */             String str = arrayOfValue[1].getString();
/* 914 */             switch (arrayOfValue[0].getString()) {
/*     */               case "MODE":
/* 916 */                 str1 = str;
/*     */               
/*     */               case "TIME ZONE":
/* 919 */                 timeZoneProvider = TimeZoneProvider.ofId(str);
/*     */               
/*     */               case "JAVA_OBJECT_SERIALIZER":
/* 922 */                 str2 = str;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 927 */       Mode mode = Mode.getInstance(str1);
/* 928 */       if (mode == null) {
/* 929 */         mode = Mode.getRegular();
/*     */       }
/* 931 */       this.dynamicSettings = dynamicSettings = new Session.DynamicSettings(mode, timeZoneProvider);
/* 932 */       if (str2 != null && 
/* 933 */         !(str2 = str2.trim()).isEmpty() && 
/* 934 */         !str2.equals("null")) {
/*     */         try {
/* 936 */           this
/* 937 */             .javaObjectSerializer = JdbcUtils.loadUserClass(str2).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 938 */         } catch (Exception exception) {
/* 939 */           throw DbException.convert(exception);
/*     */         } 
/*     */       } else {
/* 942 */         this.javaObjectSerializer = null;
/*     */       } 
/*     */     } 
/* 945 */     return dynamicSettings;
/*     */   }
/*     */   
/*     */   private CommandInterface getSettingsCommand(String paramString) {
/* 949 */     return prepareCommand((
/* 950 */         !isOldInformationSchema() ? "SELECT SETTING_NAME, SETTING_VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE SETTING_NAME" : "SELECT NAME, `VALUE` FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME") + paramString, 2147483647);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ValueTimestampTimeZone currentTimestamp() {
/* 958 */     return DateTimeUtils.currentTimestamp((getDynamicSettings()).timeZone);
/*     */   }
/*     */ 
/*     */   
/*     */   public TimeZoneProvider currentTimeZone() {
/* 963 */     return (getDynamicSettings()).timeZone;
/*     */   }
/*     */ 
/*     */   
/*     */   public Mode getMode() {
/* 968 */     return (getDynamicSettings()).mode;
/*     */   }
/*     */ 
/*     */   
/*     */   public DatabaseMeta getDatabaseMeta() {
/* 973 */     return (this.clientVersion >= 20) ? (DatabaseMeta)new DatabaseMetaRemote(this, this.transferList) : (DatabaseMeta)new DatabaseMetaLegacy(this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOldInformationSchema() {
/* 979 */     return (this.oldInformationSchema || this.clientVersion < 20);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean zeroBasedEnums() {
/* 984 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\SessionRemote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */