/*     */ package org.h2.server;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.NetUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Tool;
/*     */ import org.h2.util.Utils10;
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
/*     */ public class TcpServer
/*     */   implements Service
/*     */ {
/*     */   private static final int SHUTDOWN_NORMAL = 0;
/*     */   private static final int SHUTDOWN_FORCE = 1;
/*     */   private static final String MANAGEMENT_DB_PREFIX = "management_db_";
/*  50 */   private static final ConcurrentHashMap<Integer, TcpServer> SERVERS = new ConcurrentHashMap<>();
/*     */   
/*     */   private int port;
/*     */   
/*     */   private boolean portIsSet;
/*     */   private boolean trace;
/*     */   private boolean ssl;
/*     */   private boolean stop;
/*     */   private ShutdownHandler shutdownHandler;
/*     */   private ServerSocket serverSocket;
/*  60 */   private final Set<TcpServerThread> running = Collections.synchronizedSet(new HashSet<>());
/*     */   private String baseDir;
/*     */   private boolean allowOthers;
/*     */   private boolean isDaemon;
/*     */   private boolean ifExists = true;
/*     */   private JdbcConnection managementDb;
/*     */   private PreparedStatement managementDbAdd;
/*     */   private PreparedStatement managementDbRemove;
/*  68 */   private String managementPassword = "";
/*     */ 
/*     */   
/*     */   private Thread listenerThread;
/*     */   
/*     */   private int nextThreadId;
/*     */   
/*     */   private String key;
/*     */   
/*     */   private String keyDatabase;
/*     */ 
/*     */   
/*     */   public static String getManagementDbName(int paramInt) {
/*  81 */     return "mem:management_db_" + paramInt;
/*     */   }
/*     */   
/*     */   private void initManagementDb() throws SQLException {
/*  85 */     if (this.managementPassword.isEmpty()) {
/*  86 */       this.managementPassword = StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(32));
/*     */     }
/*     */     
/*  89 */     JdbcConnection jdbcConnection = new JdbcConnection("jdbc:h2:" + getManagementDbName(this.port), null, "", this.managementPassword, false);
/*     */     
/*  91 */     this.managementDb = jdbcConnection;
/*     */     
/*  93 */     try (Statement null = jdbcConnection.createStatement()) {
/*  94 */       statement.execute("CREATE ALIAS IF NOT EXISTS STOP_SERVER FOR '" + TcpServer.class.getName() + ".stopServer'");
/*  95 */       statement.execute("CREATE TABLE IF NOT EXISTS SESSIONS(ID INT PRIMARY KEY, URL VARCHAR, `USER` VARCHAR, CONNECTED TIMESTAMP(9) WITH TIME ZONE)");
/*     */ 
/*     */       
/*  98 */       this.managementDbAdd = jdbcConnection.prepareStatement("INSERT INTO SESSIONS VALUES(?, ?, ?, CURRENT_TIMESTAMP(9))");
/*     */       
/* 100 */       this.managementDbRemove = jdbcConnection.prepareStatement("DELETE FROM SESSIONS WHERE ID=?");
/*     */     } 
/*     */     
/* 103 */     SERVERS.put(Integer.valueOf(this.port), this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void shutdown() {
/* 110 */     if (this.shutdownHandler != null) {
/* 111 */       this.shutdownHandler.shutdown();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setShutdownHandler(ShutdownHandler paramShutdownHandler) {
/* 116 */     this.shutdownHandler = paramShutdownHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void addConnection(int paramInt, String paramString1, String paramString2) {
/*     */     try {
/* 128 */       this.managementDbAdd.setInt(1, paramInt);
/* 129 */       this.managementDbAdd.setString(2, paramString1);
/* 130 */       this.managementDbAdd.setString(3, paramString2);
/* 131 */       this.managementDbAdd.execute();
/* 132 */     } catch (SQLException sQLException) {
/* 133 */       DbException.traceThrowable(sQLException);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void removeConnection(int paramInt) {
/*     */     try {
/* 144 */       this.managementDbRemove.setInt(1, paramInt);
/* 145 */       this.managementDbRemove.execute();
/* 146 */     } catch (SQLException sQLException) {
/* 147 */       DbException.traceThrowable(sQLException);
/*     */     } 
/*     */   }
/*     */   
/*     */   private synchronized void stopManagementDb() {
/* 152 */     if (this.managementDb != null) {
/*     */       try {
/* 154 */         this.managementDb.close();
/* 155 */       } catch (SQLException sQLException) {
/* 156 */         DbException.traceThrowable(sQLException);
/*     */       } 
/* 158 */       this.managementDb = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void init(String... paramVarArgs) {
/* 164 */     this.port = 9092;
/* 165 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/* 166 */       String str = paramVarArgs[b];
/* 167 */       if (Tool.isOption(str, "-trace")) {
/* 168 */         this.trace = true;
/* 169 */       } else if (Tool.isOption(str, "-tcpSSL")) {
/* 170 */         this.ssl = true;
/* 171 */       } else if (Tool.isOption(str, "-tcpPort")) {
/* 172 */         this.port = Integer.decode(paramVarArgs[++b]).intValue();
/* 173 */         this.portIsSet = true;
/* 174 */       } else if (Tool.isOption(str, "-tcpPassword")) {
/* 175 */         this.managementPassword = paramVarArgs[++b];
/* 176 */       } else if (Tool.isOption(str, "-baseDir")) {
/* 177 */         this.baseDir = paramVarArgs[++b];
/* 178 */       } else if (Tool.isOption(str, "-key")) {
/* 179 */         this.key = paramVarArgs[++b];
/* 180 */         this.keyDatabase = paramVarArgs[++b];
/* 181 */       } else if (Tool.isOption(str, "-tcpAllowOthers")) {
/* 182 */         this.allowOthers = true;
/* 183 */       } else if (Tool.isOption(str, "-tcpDaemon")) {
/* 184 */         this.isDaemon = true;
/* 185 */       } else if (Tool.isOption(str, "-ifExists")) {
/* 186 */         this.ifExists = true;
/* 187 */       } else if (Tool.isOption(str, "-ifNotExists")) {
/* 188 */         this.ifExists = false;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getURL() {
/* 195 */     return (this.ssl ? "ssl" : "tcp") + "://" + NetUtils.getLocalAddress() + ":" + this.port;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 200 */     return this.port;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getSSL() {
/* 210 */     return this.ssl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean allow(Socket paramSocket) {
/* 221 */     if (this.allowOthers) {
/* 222 */       return true;
/*     */     }
/*     */     try {
/* 225 */       return NetUtils.isLocalAddress(paramSocket);
/* 226 */     } catch (UnknownHostException unknownHostException) {
/* 227 */       traceError(unknownHostException);
/* 228 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void start() throws SQLException {
/* 234 */     this.stop = false;
/*     */     try {
/* 236 */       this.serverSocket = NetUtils.createServerSocket(this.port, this.ssl);
/* 237 */     } catch (DbException dbException) {
/* 238 */       if (!this.portIsSet) {
/* 239 */         this.serverSocket = NetUtils.createServerSocket(0, this.ssl);
/*     */       } else {
/* 241 */         throw dbException;
/*     */       } 
/*     */     } 
/* 244 */     this.port = this.serverSocket.getLocalPort();
/* 245 */     initManagementDb();
/*     */   }
/*     */ 
/*     */   
/*     */   public void listen() {
/* 250 */     this.listenerThread = Thread.currentThread();
/* 251 */     String str = this.listenerThread.getName();
/*     */     try {
/* 253 */       while (!this.stop) {
/* 254 */         Socket socket = this.serverSocket.accept();
/* 255 */         Utils10.setTcpQuickack(socket, true);
/* 256 */         int i = this.nextThreadId++;
/* 257 */         TcpServerThread tcpServerThread = new TcpServerThread(socket, this, i);
/* 258 */         this.running.add(tcpServerThread);
/* 259 */         Thread thread = new Thread(tcpServerThread, str + " thread-" + i);
/* 260 */         thread.setDaemon(this.isDaemon);
/* 261 */         tcpServerThread.setThread(thread);
/* 262 */         thread.start();
/*     */       } 
/* 264 */       this.serverSocket = NetUtils.closeSilently(this.serverSocket);
/* 265 */     } catch (Exception exception) {
/* 266 */       if (!this.stop) {
/* 267 */         DbException.traceThrowable(exception);
/*     */       }
/*     */     } 
/* 270 */     stopManagementDb();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean isRunning(boolean paramBoolean) {
/* 275 */     if (this.serverSocket == null) {
/* 276 */       return false;
/*     */     }
/*     */     try {
/* 279 */       Socket socket = NetUtils.createLoopbackSocket(this.port, this.ssl);
/* 280 */       socket.close();
/* 281 */       return true;
/* 282 */     } catch (Exception exception) {
/* 283 */       if (paramBoolean) {
/* 284 */         traceError(exception);
/*     */       }
/* 286 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 295 */     SERVERS.remove(Integer.valueOf(this.port));
/* 296 */     if (!this.stop) {
/* 297 */       stopManagementDb();
/* 298 */       this.stop = true;
/* 299 */       if (this.serverSocket != null) {
/*     */         try {
/* 301 */           this.serverSocket.close();
/* 302 */         } catch (IOException iOException) {
/* 303 */           DbException.traceThrowable(iOException);
/* 304 */         } catch (NullPointerException nullPointerException) {}
/*     */ 
/*     */         
/* 307 */         this.serverSocket = null;
/*     */       } 
/* 309 */       if (this.listenerThread != null) {
/*     */         try {
/* 311 */           this.listenerThread.join(1000L);
/* 312 */         } catch (InterruptedException interruptedException) {
/* 313 */           DbException.traceThrowable(interruptedException);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 318 */     for (TcpServerThread tcpServerThread : new ArrayList(this.running)) {
/* 319 */       if (tcpServerThread != null) {
/* 320 */         tcpServerThread.close();
/*     */         try {
/* 322 */           tcpServerThread.getThread().join(100L);
/* 323 */         } catch (Exception exception) {
/* 324 */           DbException.traceThrowable(exception);
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
/*     */ 
/*     */   
/*     */   public static void stopServer(int paramInt1, String paramString, int paramInt2) {
/* 339 */     if (paramInt1 == 0) {
/* 340 */       Integer[] arrayOfInteger; int i; byte b; for (arrayOfInteger = (Integer[])SERVERS.keySet().toArray((Object[])new Integer[0]), i = arrayOfInteger.length, b = 0; b < i; ) { int j = arrayOfInteger[b].intValue();
/* 341 */         if (j != 0)
/* 342 */           stopServer(j, paramString, paramInt2); 
/*     */         b++; }
/*     */       
/*     */       return;
/*     */     } 
/* 347 */     TcpServer tcpServer = SERVERS.get(Integer.valueOf(paramInt1));
/* 348 */     if (tcpServer == null) {
/*     */       return;
/*     */     }
/* 351 */     if (!tcpServer.managementPassword.equals(paramString)) {
/*     */       return;
/*     */     }
/* 354 */     if (paramInt2 == 0) {
/* 355 */       tcpServer.stopManagementDb();
/* 356 */       tcpServer.stop = true;
/*     */       try {
/* 358 */         Socket socket = NetUtils.createLoopbackSocket(paramInt1, false);
/* 359 */         socket.close();
/* 360 */       } catch (Exception exception) {}
/*     */     
/*     */     }
/* 363 */     else if (paramInt2 == 1) {
/* 364 */       tcpServer.stop();
/*     */     } 
/* 366 */     tcpServer.shutdown();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void remove(TcpServerThread paramTcpServerThread) {
/* 375 */     this.running.remove(paramTcpServerThread);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getBaseDir() {
/* 384 */     return this.baseDir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void trace(String paramString) {
/* 393 */     if (this.trace) {
/* 394 */       System.out.println(paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void traceError(Throwable paramThrowable) {
/* 403 */     if (this.trace) {
/* 404 */       paramThrowable.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getAllowOthers() {
/* 410 */     return this.allowOthers;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getType() {
/* 415 */     return "TCP";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 420 */     return "H2 TCP Server";
/*     */   }
/*     */   
/*     */   boolean getIfExists() {
/* 424 */     return this.ifExists;
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
/*     */   public static synchronized void shutdown(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) throws SQLException {
/*     */     try {
/* 440 */       int i = 9092;
/* 441 */       int j = paramString1.lastIndexOf(':');
/* 442 */       if (j >= 0) {
/* 443 */         String str1 = paramString1.substring(j + 1);
/* 444 */         if (StringUtils.isNumber(str1)) {
/* 445 */           i = Integer.decode(str1).intValue();
/*     */         }
/*     */       } 
/* 448 */       String str = getManagementDbName(i);
/* 449 */       for (byte b = 0; b < 2; b++) { try {
/* 450 */           try (JdbcConnection null = new JdbcConnection("jdbc:h2:" + paramString1 + '/' + str, null, "", paramString2, true)) {
/* 451 */             PreparedStatement preparedStatement = jdbcConnection.prepareStatement("CALL STOP_SERVER(?, ?, ?)");
/* 452 */             preparedStatement.setInt(1, paramBoolean2 ? 0 : i);
/* 453 */             preparedStatement.setString(2, paramString2);
/* 454 */             preparedStatement.setInt(3, paramBoolean1 ? 1 : 0);
/*     */             try {
/* 456 */               preparedStatement.execute();
/* 457 */             } catch (SQLException sQLException) {
/* 458 */               if (!paramBoolean1)
/*     */               {
/*     */                 
/* 461 */                 if (sQLException.getErrorCode() != 90067)
/* 462 */                   throw sQLException; 
/*     */               }
/*     */             } 
/*     */           } 
/*     */           break;
/* 467 */         } catch (SQLException sQLException) {
/* 468 */           if (b == 1) {
/* 469 */             throw sQLException;
/*     */           }
/*     */         }  }
/*     */     
/* 473 */     } catch (Exception exception) {
/* 474 */       throw DbException.toSQLException(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void cancelStatement(String paramString, int paramInt) {
/* 485 */     for (TcpServerThread tcpServerThread : new ArrayList(this.running)) {
/* 486 */       if (tcpServerThread != null) {
/* 487 */         tcpServerThread.cancelStatement(paramString, paramInt);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public String checkKeyAndGetDatabaseName(String paramString) {
/* 502 */     if (this.key == null) {
/* 503 */       return paramString;
/*     */     }
/* 505 */     if (this.key.equals(paramString)) {
/* 506 */       return this.keyDatabase;
/*     */     }
/* 508 */     throw DbException.get(28000);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDaemon() {
/* 513 */     return this.isDaemon;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\TcpServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */