/*     */ package org.h2.server;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.net.Socket;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Objects;
/*     */ import org.h2.command.Command;
/*     */ import org.h2.engine.ConnectionInfo;
/*     */ import org.h2.engine.Engine;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.expression.ParameterRemote;
/*     */ import org.h2.jdbc.JdbcException;
/*     */ import org.h2.jdbc.meta.DatabaseMetaServer;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultColumn;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultWithGeneratedKeys;
/*     */ import org.h2.store.LobStorageInterface;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.NetUtils;
/*     */ import org.h2.util.NetworkConnectionInfo;
/*     */ import org.h2.util.SmallLRUCache;
/*     */ import org.h2.util.SmallMap;
/*     */ import org.h2.util.TimeZoneProvider;
/*     */ import org.h2.value.Transfer;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueLob;
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
/*     */ public class TcpServerThread
/*     */   implements Runnable
/*     */ {
/*     */   protected final Transfer transfer;
/*     */   private final TcpServer server;
/*     */   private SessionLocal session;
/*     */   private boolean stop;
/*     */   private Thread thread;
/*     */   private Command commit;
/*  60 */   private final SmallMap cache = new SmallMap(SysProperties.SERVER_CACHED_OBJECTS);
/*     */ 
/*     */   
/*  63 */   private final SmallLRUCache<Long, CachedInputStream> lobs = SmallLRUCache.newInstance(Math.max(SysProperties.SERVER_CACHED_OBJECTS, SysProperties.SERVER_RESULT_SET_FETCH_SIZE * 5));
/*     */   
/*     */   private final int threadId;
/*     */   
/*     */   private int clientVersion;
/*     */   private String sessionId;
/*     */   private long lastRemoteSettingsId;
/*     */   
/*     */   TcpServerThread(Socket paramSocket, TcpServer paramTcpServer, int paramInt) {
/*  72 */     this.server = paramTcpServer;
/*  73 */     this.threadId = paramInt;
/*  74 */     this.transfer = new Transfer(null, paramSocket);
/*     */   }
/*     */   
/*     */   private void trace(String paramString) {
/*  78 */     this.server.trace(this + " " + paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/*  84 */       this.transfer.init();
/*  85 */       trace("Connect");
/*     */ 
/*     */       
/*     */       try {
/*  89 */         Socket socket = this.transfer.getSocket();
/*  90 */         if (socket == null) {
/*     */           return;
/*     */         }
/*     */         
/*  94 */         if (!this.server.allow(this.transfer.getSocket())) {
/*  95 */           throw DbException.get(90117);
/*     */         }
/*  97 */         int i = this.transfer.readInt();
/*  98 */         if (i < 6)
/*  99 */           throw DbException.get(90047, new String[] {
/* 100 */                 Integer.toString(i), "17"
/*     */               }); 
/* 102 */         int j = this.transfer.readInt();
/* 103 */         if (j < 17)
/* 104 */           throw DbException.get(90047, new String[] {
/* 105 */                 Integer.toString(j), "17" }); 
/* 106 */         if (i > 20)
/* 107 */           throw DbException.get(90047, new String[] {
/* 108 */                 Integer.toString(i), "20"
/*     */               }); 
/* 110 */         if (j >= 20) {
/* 111 */           this.clientVersion = 20;
/*     */         } else {
/* 113 */           this.clientVersion = j;
/*     */         } 
/* 115 */         this.transfer.setVersion(this.clientVersion);
/* 116 */         String str1 = this.transfer.readString();
/* 117 */         String str2 = this.transfer.readString();
/* 118 */         if (str1 == null && str2 == null) {
/* 119 */           String str = this.transfer.readString();
/* 120 */           int m = this.transfer.readInt();
/* 121 */           this.stop = true;
/* 122 */           if (m == 13) {
/*     */             
/* 124 */             int n = this.transfer.readInt();
/* 125 */             this.server.cancelStatement(str, n);
/* 126 */           } else if (m == 14) {
/*     */             
/* 128 */             str1 = this.server.checkKeyAndGetDatabaseName(str);
/* 129 */             if (!str.equals(str1)) {
/* 130 */               this.transfer.writeInt(1);
/*     */             } else {
/* 132 */               this.transfer.writeInt(0);
/*     */             } 
/*     */           } 
/*     */         } 
/* 136 */         String str3 = this.server.getBaseDir();
/* 137 */         if (str3 == null) {
/* 138 */           str3 = SysProperties.getBaseDir();
/*     */         }
/* 140 */         str1 = this.server.checkKeyAndGetDatabaseName(str1);
/* 141 */         ConnectionInfo connectionInfo = new ConnectionInfo(str1);
/* 142 */         connectionInfo.setOriginalURL(str2);
/* 143 */         connectionInfo.setUserName(this.transfer.readString());
/* 144 */         connectionInfo.setUserPasswordHash(this.transfer.readBytes());
/* 145 */         connectionInfo.setFilePasswordHash(this.transfer.readBytes());
/* 146 */         int k = this.transfer.readInt();
/* 147 */         for (byte b = 0; b < k; b++) {
/* 148 */           connectionInfo.setProperty(this.transfer.readString(), this.transfer.readString());
/*     */         }
/*     */         
/* 151 */         if (str3 != null) {
/* 152 */           connectionInfo.setBaseDir(str3);
/*     */         }
/* 154 */         if (this.server.getIfExists()) {
/* 155 */           connectionInfo.setProperty("FORBID_CREATION", "TRUE");
/*     */         }
/* 157 */         this.transfer.writeInt(1);
/* 158 */         this.transfer.writeInt(this.clientVersion);
/* 159 */         this.transfer.flush();
/* 160 */         if (connectionInfo.getFilePasswordHash() != null) {
/* 161 */           connectionInfo.setFileEncryptionKey(this.transfer.readBytes());
/*     */         }
/* 163 */         connectionInfo.setNetworkConnectionInfo(new NetworkConnectionInfo(
/* 164 */               NetUtils.ipToShortForm(new StringBuilder(this.server.getSSL() ? "ssl://" : "tcp://"), socket
/* 165 */                 .getLocalAddress().getAddress(), true)
/* 166 */               .append(':').append(socket.getLocalPort()).toString(), socket
/* 167 */               .getInetAddress().getAddress(), socket.getPort(), 'P' + 
/* 168 */               this.clientVersion));
/* 169 */         if (this.clientVersion < 20) {
/*     */           
/* 171 */           connectionInfo.setProperty("OLD_INFORMATION_SCHEMA", "TRUE");
/*     */           
/* 173 */           connectionInfo.setProperty("NON_KEYWORDS", "VALUE");
/*     */         } 
/* 175 */         this.session = Engine.createSession(connectionInfo);
/* 176 */         this.transfer.setSession((Session)this.session);
/* 177 */         this.server.addConnection(this.threadId, str2, connectionInfo.getUserName());
/* 178 */         trace("Connected");
/* 179 */         this.lastRemoteSettingsId = this.session.getDatabase().getRemoteSettingsId();
/* 180 */       } catch (OutOfMemoryError outOfMemoryError) {
/*     */         
/* 182 */         this.server.traceError(outOfMemoryError);
/* 183 */         sendError(outOfMemoryError, true);
/* 184 */         this.stop = true;
/* 185 */       } catch (Throwable throwable) {
/* 186 */         sendError(throwable, true);
/* 187 */         this.stop = true;
/*     */       } 
/* 189 */       while (!this.stop) {
/*     */         try {
/* 191 */           process();
/* 192 */         } catch (Throwable throwable) {
/* 193 */           sendError(throwable, true);
/*     */         } 
/*     */       } 
/* 196 */       trace("Disconnect");
/* 197 */     } catch (Throwable throwable) {
/* 198 */       this.server.traceError(throwable);
/*     */     } finally {
/* 200 */       close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void closeSession() {
/* 205 */     if (this.session != null) {
/* 206 */       RuntimeException runtimeException = null;
/*     */       try {
/* 208 */         this.session.close();
/* 209 */         this.server.removeConnection(this.threadId);
/* 210 */       } catch (RuntimeException runtimeException1) {
/* 211 */         runtimeException = runtimeException1;
/* 212 */         this.server.traceError(runtimeException1);
/* 213 */       } catch (Exception exception) {
/* 214 */         this.server.traceError(exception);
/*     */       } finally {
/* 216 */         this.session = null;
/*     */       } 
/* 218 */       if (runtimeException != null) {
/* 219 */         throw runtimeException;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void close() {
/*     */     try {
/* 229 */       this.stop = true;
/* 230 */       closeSession();
/* 231 */     } catch (Exception exception) {
/* 232 */       this.server.traceError(exception);
/*     */     } finally {
/* 234 */       this.transfer.close();
/* 235 */       trace("Close");
/* 236 */       this.server.remove(this);
/*     */     } 
/*     */   }
/*     */   private void sendError(Throwable paramThrowable, boolean paramBoolean) {
/*     */     try {
/*     */       String str2, str3;
/* 242 */       SQLException sQLException = DbException.convert(paramThrowable).getSQLException();
/* 243 */       StringWriter stringWriter = new StringWriter();
/* 244 */       sQLException.printStackTrace(new PrintWriter(stringWriter));
/* 245 */       String str1 = stringWriter.toString();
/*     */ 
/*     */       
/* 248 */       if (sQLException instanceof JdbcException) {
/* 249 */         JdbcException jdbcException = (JdbcException)sQLException;
/* 250 */         str2 = jdbcException.getOriginalMessage();
/* 251 */         str3 = jdbcException.getSQL();
/*     */       } else {
/* 253 */         str2 = sQLException.getMessage();
/* 254 */         str3 = null;
/*     */       } 
/* 256 */       if (paramBoolean) {
/* 257 */         this.transfer.writeInt(0);
/*     */       }
/* 259 */       this.transfer
/* 260 */         .writeString(sQLException.getSQLState()).writeString(str2)
/* 261 */         .writeString(str3).writeInt(sQLException.getErrorCode()).writeString(str1).flush();
/* 262 */     } catch (Exception exception) {
/* 263 */       if (!this.transfer.isClosed()) {
/* 264 */         this.server.traceError(exception);
/*     */       }
/*     */       
/* 267 */       this.stop = true;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setParameters(Command paramCommand) throws IOException {
/* 272 */     int i = this.transfer.readInt();
/* 273 */     ArrayList<Parameter> arrayList = paramCommand.getParameters();
/* 274 */     for (byte b = 0; b < i; b++) {
/* 275 */       Parameter parameter = arrayList.get(b);
/* 276 */       parameter.setValue(this.transfer.readValue(null));
/*     */     }  } private void process() throws IOException { int k; boolean bool1; long l1; int j; String str; int i1; Command command1; int n; ResultInterface resultInterface1; int m, i2; Command command2; long l2; boolean bool; ResultInterface resultInterface2; Object object; Command command3; ResultInterface resultInterface3; Boolean bool2; int[] arrayOfInt1; String[] arrayOfString1; long l3; int i3; boolean bool3; int i4; ResultInterface resultInterface4; boolean bool4; Command command4; int i5; ArrayList arrayList; int i7, arrayOfInt2[]; String[] arrayOfString2; ResultWithGeneratedKeys resultWithGeneratedKeys; CachedInputStream cachedInputStream; int i6; ResultInterface resultInterface5; int i8; byte[] arrayOfByte; long l4;
/*     */     int i9, i10;
/*     */     long l5;
/*     */     byte b;
/* 281 */     int i = this.transfer.readInt();
/* 282 */     switch (i) {
/*     */       case 0:
/*     */       case 18:
/* 285 */         k = this.transfer.readInt();
/* 286 */         str = this.transfer.readString();
/* 287 */         i2 = this.session.getModificationId();
/* 288 */         command3 = this.session.prepareLocal(str);
/* 289 */         bool3 = command3.isReadOnly();
/* 290 */         this.cache.addObject(k, command3);
/* 291 */         bool4 = command3.isQuery();
/*     */         
/* 293 */         this.transfer.writeInt(getState(i2)).writeBoolean(bool4)
/* 294 */           .writeBoolean(bool3);
/*     */         
/* 296 */         if (i != 0) {
/* 297 */           this.transfer.writeInt(command3.getCommandType());
/*     */         }
/*     */         
/* 300 */         arrayList = command3.getParameters();
/*     */         
/* 302 */         this.transfer.writeInt(arrayList.size());
/*     */         
/* 304 */         if (i != 0) {
/* 305 */           for (ParameterInterface parameterInterface : arrayList) {
/* 306 */             ParameterRemote.writeMetaData(this.transfer, parameterInterface);
/*     */           }
/*     */         }
/* 309 */         this.transfer.flush();
/*     */         return;
/*     */       
/*     */       case 1:
/* 313 */         this.stop = true;
/* 314 */         closeSession();
/* 315 */         this.transfer.writeInt(1).flush();
/* 316 */         close();
/*     */         return;
/*     */       
/*     */       case 8:
/* 320 */         if (this.commit == null) {
/* 321 */           this.commit = this.session.prepareLocal("COMMIT");
/*     */         }
/* 323 */         k = this.session.getModificationId();
/* 324 */         this.commit.executeUpdate(null);
/* 325 */         this.transfer.writeInt(getState(k)).flush();
/*     */         return;
/*     */       
/*     */       case 10:
/* 329 */         k = this.transfer.readInt();
/* 330 */         i1 = this.transfer.readInt();
/* 331 */         command2 = (Command)this.cache.getObject(k, false);
/* 332 */         resultInterface3 = command2.getMetaData();
/* 333 */         this.cache.addObject(i1, resultInterface3);
/* 334 */         i4 = resultInterface3.getVisibleColumnCount();
/* 335 */         this.transfer.writeInt(1)
/* 336 */           .writeInt(i4).writeRowCount(0L);
/* 337 */         for (bool4 = false; bool4 < i4; bool4++) {
/* 338 */           ResultColumn.writeColumn(this.transfer, resultInterface3, bool4);
/*     */         }
/* 340 */         this.transfer.flush();
/*     */         return;
/*     */       
/*     */       case 2:
/* 344 */         k = this.transfer.readInt();
/* 345 */         i1 = this.transfer.readInt();
/* 346 */         l2 = this.transfer.readRowCount();
/* 347 */         i4 = this.transfer.readInt();
/* 348 */         command4 = (Command)this.cache.getObject(k, false);
/* 349 */         setParameters(command4);
/* 350 */         i7 = this.session.getModificationId();
/*     */         
/* 352 */         synchronized (this.session) {
/* 353 */           resultInterface5 = command4.executeQuery(l2, false);
/*     */         } 
/* 355 */         this.cache.addObject(i1, resultInterface5);
/* 356 */         i9 = resultInterface5.getVisibleColumnCount();
/* 357 */         i10 = getState(i7);
/* 358 */         this.transfer.writeInt(i10).writeInt(i9);
/* 359 */         l5 = resultInterface5.isLazy() ? -1L : resultInterface5.getRowCount();
/* 360 */         this.transfer.writeRowCount(l5);
/* 361 */         for (b = 0; b < i9; b++) {
/* 362 */           ResultColumn.writeColumn(this.transfer, resultInterface5, b);
/*     */         }
/* 364 */         sendRows(resultInterface5, (l5 >= 0L) ? Math.min(l5, i4) : i4);
/* 365 */         this.transfer.flush();
/*     */         return;
/*     */       
/*     */       case 3:
/* 369 */         k = this.transfer.readInt();
/* 370 */         command1 = (Command)this.cache.getObject(k, false);
/* 371 */         setParameters(command1);
/* 372 */         bool = true;
/*     */         
/* 374 */         i4 = this.transfer.readInt();
/* 375 */         switch (i4) {
/*     */           case 0:
/* 377 */             bool2 = Boolean.valueOf(false);
/* 378 */             bool = false;
/*     */             break;
/*     */           case 1:
/* 381 */             bool2 = Boolean.valueOf(true);
/*     */             break;
/*     */           case 2:
/* 384 */             i5 = this.transfer.readInt();
/* 385 */             arrayOfInt2 = new int[i5];
/* 386 */             for (i8 = 0; i8 < i5; i8++) {
/* 387 */               arrayOfInt2[i8] = this.transfer.readInt();
/*     */             }
/* 389 */             arrayOfInt1 = arrayOfInt2;
/*     */             break;
/*     */           
/*     */           case 3:
/* 393 */             i5 = this.transfer.readInt();
/* 394 */             arrayOfString2 = new String[i5];
/* 395 */             for (i8 = 0; i8 < i5; i8++) {
/* 396 */               arrayOfString2[i8] = this.transfer.readString();
/*     */             }
/* 398 */             arrayOfString1 = arrayOfString2;
/*     */             break;
/*     */           
/*     */           default:
/* 402 */             throw DbException.get(90067, "Unsupported generated keys' mode " + i4);
/*     */         } 
/*     */         
/* 405 */         i5 = this.session.getModificationId();
/*     */         
/* 407 */         synchronized (this.session) {
/* 408 */           resultWithGeneratedKeys = command1.executeUpdate(arrayOfString1);
/*     */         } 
/*     */         
/* 411 */         if (this.session.isClosed()) {
/* 412 */           i8 = 2;
/* 413 */           this.stop = true;
/*     */         } else {
/* 415 */           i8 = getState(i5);
/*     */         } 
/* 417 */         this.transfer.writeInt(i8);
/* 418 */         this.transfer.writeRowCount(resultWithGeneratedKeys.getUpdateCount());
/* 419 */         this.transfer.writeBoolean(this.session.getAutoCommit());
/* 420 */         if (bool) {
/* 421 */           ResultInterface resultInterface = resultWithGeneratedKeys.getGeneratedKeys();
/* 422 */           i10 = resultInterface.getVisibleColumnCount();
/* 423 */           this.transfer.writeInt(i10);
/* 424 */           l5 = resultInterface.getRowCount();
/* 425 */           this.transfer.writeRowCount(l5);
/* 426 */           for (b = 0; b < i10; b++) {
/* 427 */             ResultColumn.writeColumn(this.transfer, resultInterface, b);
/*     */           }
/* 429 */           sendRows(resultInterface, l5);
/* 430 */           resultInterface.close();
/*     */         } 
/* 432 */         this.transfer.flush();
/*     */         return;
/*     */       
/*     */       case 4:
/* 436 */         k = this.transfer.readInt();
/* 437 */         command1 = (Command)this.cache.getObject(k, true);
/* 438 */         if (command1 != null) {
/* 439 */           command1.close();
/* 440 */           this.cache.freeObject(k);
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 5:
/* 445 */         k = this.transfer.readInt();
/* 446 */         n = this.transfer.readInt();
/* 447 */         resultInterface2 = (ResultInterface)this.cache.getObject(k, false);
/* 448 */         this.transfer.writeInt(1);
/* 449 */         sendRows(resultInterface2, n);
/* 450 */         this.transfer.flush();
/*     */         return;
/*     */       
/*     */       case 6:
/* 454 */         k = this.transfer.readInt();
/* 455 */         resultInterface1 = (ResultInterface)this.cache.getObject(k, false);
/* 456 */         resultInterface1.reset();
/*     */         return;
/*     */       
/*     */       case 7:
/* 460 */         k = this.transfer.readInt();
/* 461 */         resultInterface1 = (ResultInterface)this.cache.getObject(k, true);
/* 462 */         if (resultInterface1 != null) {
/* 463 */           resultInterface1.close();
/* 464 */           this.cache.freeObject(k);
/*     */         } 
/*     */         return;
/*     */       
/*     */       case 9:
/* 469 */         k = this.transfer.readInt();
/* 470 */         m = this.transfer.readInt();
/* 471 */         object = this.cache.getObject(k, false);
/* 472 */         this.cache.freeObject(k);
/* 473 */         this.cache.addObject(m, object);
/*     */         return;
/*     */       
/*     */       case 12:
/* 477 */         this.sessionId = this.transfer.readString();
/* 478 */         if (this.clientVersion >= 20) {
/* 479 */           this.session.setTimeZone(TimeZoneProvider.ofId(this.transfer.readString()));
/*     */         }
/* 481 */         this.transfer.writeInt(1)
/* 482 */           .writeBoolean(this.session.getAutoCommit())
/* 483 */           .flush();
/*     */         return;
/*     */       
/*     */       case 15:
/* 487 */         bool1 = this.transfer.readBoolean();
/* 488 */         this.session.setAutoCommit(bool1);
/* 489 */         this.transfer.writeInt(1).flush();
/*     */         return;
/*     */       
/*     */       case 16:
/* 493 */         this.transfer.writeInt(1)
/* 494 */           .writeInt(this.session.hasPendingTransaction() ? 1 : 0).flush();
/*     */         return;
/*     */       
/*     */       case 17:
/* 498 */         l1 = this.transfer.readLong();
/* 499 */         object = this.transfer.readBytes();
/* 500 */         l3 = this.transfer.readLong();
/* 501 */         i5 = this.transfer.readInt();
/* 502 */         this.transfer.verifyLobMac((byte[])object, l1);
/* 503 */         cachedInputStream = (CachedInputStream)this.lobs.get(Long.valueOf(l1));
/* 504 */         if (cachedInputStream == null || cachedInputStream.getPos() != l3) {
/* 505 */           LobStorageInterface lobStorageInterface = this.session.getDataHandler().getLobStorage();
/*     */           
/* 507 */           InputStream inputStream = lobStorageInterface.getInputStream(l1, -1L);
/* 508 */           cachedInputStream = new CachedInputStream(inputStream);
/* 509 */           this.lobs.put(Long.valueOf(l1), cachedInputStream);
/* 510 */           inputStream.skip(l3);
/*     */         } 
/*     */         
/* 513 */         i5 = Math.min(65536, i5);
/* 514 */         arrayOfByte = new byte[i5];
/* 515 */         i5 = IOUtils.readFully(cachedInputStream, arrayOfByte, i5);
/* 516 */         this.transfer.writeInt(1);
/* 517 */         this.transfer.writeInt(i5);
/* 518 */         this.transfer.writeBytes(arrayOfByte, 0, i5);
/* 519 */         this.transfer.flush();
/*     */         return;
/*     */       
/*     */       case 19:
/* 523 */         j = this.transfer.readInt();
/* 524 */         m = this.transfer.readInt();
/* 525 */         object = new Value[m];
/* 526 */         for (i3 = 0; i3 < m; i3++) {
/* 527 */           object[i3] = this.transfer.readValue(null);
/*     */         }
/* 529 */         i3 = this.session.getModificationId();
/*     */         
/* 531 */         synchronized (this.session) {
/* 532 */           resultInterface4 = DatabaseMetaServer.process(this.session, j, (Value[])object);
/*     */         } 
/* 534 */         i5 = resultInterface4.getVisibleColumnCount();
/* 535 */         i6 = getState(i3);
/* 536 */         this.transfer.writeInt(i6).writeInt(i5);
/* 537 */         l4 = resultInterface4.getRowCount();
/* 538 */         this.transfer.writeRowCount(l4);
/* 539 */         for (i10 = 0; i10 < i5; i10++) {
/* 540 */           ResultColumn.writeColumn(this.transfer, resultInterface4, i10);
/*     */         }
/* 542 */         sendRows(resultInterface4, l4);
/* 543 */         this.transfer.flush();
/*     */         return;
/*     */     } 
/*     */     
/* 547 */     trace("Unknown operation: " + i);
/* 548 */     close(); }
/*     */ 
/*     */ 
/*     */   
/*     */   private int getState(int paramInt) {
/* 553 */     if (this.session == null) {
/* 554 */       return 2;
/*     */     }
/* 556 */     if (this.session.getModificationId() == paramInt) {
/* 557 */       long l = this.session.getDatabase().getRemoteSettingsId();
/* 558 */       if (this.lastRemoteSettingsId == l) {
/* 559 */         return 1;
/*     */       }
/* 561 */       this.lastRemoteSettingsId = l;
/*     */     } 
/* 563 */     return 3;
/*     */   }
/*     */   
/*     */   private void sendRows(ResultInterface paramResultInterface, long paramLong) throws IOException {
/* 567 */     int i = paramResultInterface.getVisibleColumnCount();
/* 568 */     boolean bool = paramResultInterface.isLazy();
/* 569 */     Session session = bool ? this.session.setThreadLocalSession() : null;
/*     */     try {
/* 571 */       while (paramLong-- > 0L) {
/*     */         boolean bool1;
/*     */         try {
/* 574 */           bool1 = paramResultInterface.next();
/* 575 */         } catch (Exception exception) {
/* 576 */           this.transfer.writeByte((byte)-1);
/* 577 */           sendError(exception, false);
/*     */           break;
/*     */         } 
/* 580 */         if (bool1) {
/* 581 */           this.transfer.writeByte((byte)1);
/* 582 */           Value[] arrayOfValue = paramResultInterface.currentRow();
/* 583 */           for (byte b = 0; b < i; b++) {
/* 584 */             ValueLob valueLob; Value value = arrayOfValue[b];
/* 585 */             if (bool && value instanceof ValueLob) {
/* 586 */               ValueLob valueLob1 = ((ValueLob)value).copyToResult();
/* 587 */               if (valueLob1 != value) {
/* 588 */                 valueLob = this.session.addTemporaryLob(valueLob1);
/*     */               }
/*     */             } 
/* 591 */             this.transfer.writeValue((Value)valueLob);
/*     */           }  continue;
/*     */         } 
/* 594 */         this.transfer.writeByte((byte)0);
/*     */       }
/*     */     
/*     */     } finally {
/*     */       
/* 599 */       if (bool) {
/* 600 */         this.session.resetThreadLocalSession(session);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   void setThread(Thread paramThread) {
/* 606 */     this.thread = paramThread;
/*     */   }
/*     */   
/*     */   Thread getThread() {
/* 610 */     return this.thread;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void cancelStatement(String paramString, int paramInt) {
/* 620 */     if (Objects.equals(paramString, this.sessionId)) {
/* 621 */       Command command = (Command)this.cache.getObject(paramInt, false);
/* 622 */       command.cancel();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class CachedInputStream
/*     */     extends FilterInputStream
/*     */   {
/* 631 */     private static final ByteArrayInputStream DUMMY = new ByteArrayInputStream(new byte[0]);
/*     */     
/*     */     private long pos;
/*     */     
/*     */     CachedInputStream(InputStream param1InputStream) {
/* 636 */       super((param1InputStream == null) ? DUMMY : param1InputStream);
/* 637 */       if (param1InputStream == null) {
/* 638 */         this.pos = -1L;
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public int read(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
/* 644 */       param1Int2 = super.read(param1ArrayOfbyte, param1Int1, param1Int2);
/* 645 */       if (param1Int2 > 0) {
/* 646 */         this.pos += param1Int2;
/*     */       }
/* 648 */       return param1Int2;
/*     */     }
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/* 653 */       int i = this.in.read();
/* 654 */       if (i >= 0) {
/* 655 */         this.pos++;
/*     */       }
/* 657 */       return i;
/*     */     }
/*     */ 
/*     */     
/*     */     public long skip(long param1Long) throws IOException {
/* 662 */       param1Long = super.skip(param1Long);
/* 663 */       if (param1Long > 0L) {
/* 664 */         this.pos += param1Long;
/*     */       }
/* 666 */       return param1Long;
/*     */     }
/*     */     
/*     */     public long getPos() {
/* 670 */       return this.pos;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\TcpServerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */