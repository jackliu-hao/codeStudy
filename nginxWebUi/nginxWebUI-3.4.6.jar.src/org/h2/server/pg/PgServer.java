/*     */ package org.h2.server.pg;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.server.Service;
/*     */ import org.h2.util.NetUtils;
/*     */ import org.h2.util.Tool;
/*     */ import org.h2.util.Utils10;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
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
/*     */ public class PgServer
/*     */   implements Service
/*     */ {
/*     */   public static final int DEFAULT_PORT = 5435;
/*     */   public static final int PG_TYPE_VARCHAR = 1043;
/*     */   public static final int PG_TYPE_BOOL = 16;
/*     */   public static final int PG_TYPE_BYTEA = 17;
/*     */   public static final int PG_TYPE_BPCHAR = 1042;
/*     */   public static final int PG_TYPE_INT8 = 20;
/*     */   public static final int PG_TYPE_INT2 = 21;
/*     */   public static final int PG_TYPE_INT4 = 23;
/*     */   public static final int PG_TYPE_TEXT = 25;
/*     */   public static final int PG_TYPE_FLOAT4 = 700;
/*     */   public static final int PG_TYPE_FLOAT8 = 701;
/*     */   public static final int PG_TYPE_UNKNOWN = 705;
/*     */   public static final int PG_TYPE_INT2_ARRAY = 1005;
/*     */   public static final int PG_TYPE_INT4_ARRAY = 1007;
/*     */   public static final int PG_TYPE_VARCHAR_ARRAY = 1015;
/*     */   public static final int PG_TYPE_DATE = 1082;
/*     */   public static final int PG_TYPE_TIME = 1083;
/*     */   public static final int PG_TYPE_TIMETZ = 1266;
/*     */   public static final int PG_TYPE_TIMESTAMP = 1114;
/*     */   public static final int PG_TYPE_TIMESTAMPTZ = 1184;
/*     */   public static final int PG_TYPE_NUMERIC = 1700;
/*  68 */   private final HashSet<Integer> typeSet = new HashSet<>();
/*     */   
/*  70 */   private int port = 5435;
/*     */   
/*     */   private boolean portIsSet;
/*     */   private boolean stop;
/*     */   private boolean trace;
/*     */   private ServerSocket serverSocket;
/*  76 */   private final Set<PgServerThread> running = Collections.synchronizedSet(new HashSet<>());
/*  77 */   private final AtomicInteger pid = new AtomicInteger();
/*     */   private String baseDir;
/*     */   private boolean allowOthers;
/*     */   private boolean isDaemon;
/*     */   private boolean ifExists = true;
/*     */   private String key;
/*     */   private String keyDatabase;
/*     */   
/*     */   public void init(String... paramVarArgs) {
/*  86 */     this.port = 5435;
/*  87 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  88 */       String str = paramVarArgs[b];
/*  89 */       if (Tool.isOption(str, "-trace")) {
/*  90 */         this.trace = true;
/*  91 */       } else if (Tool.isOption(str, "-pgPort")) {
/*  92 */         this.port = Integer.decode(paramVarArgs[++b]).intValue();
/*  93 */         this.portIsSet = true;
/*  94 */       } else if (Tool.isOption(str, "-baseDir")) {
/*  95 */         this.baseDir = paramVarArgs[++b];
/*  96 */       } else if (Tool.isOption(str, "-pgAllowOthers")) {
/*  97 */         this.allowOthers = true;
/*  98 */       } else if (Tool.isOption(str, "-pgDaemon")) {
/*  99 */         this.isDaemon = true;
/* 100 */       } else if (Tool.isOption(str, "-ifExists")) {
/* 101 */         this.ifExists = true;
/* 102 */       } else if (Tool.isOption(str, "-ifNotExists")) {
/* 103 */         this.ifExists = false;
/* 104 */       } else if (Tool.isOption(str, "-key")) {
/* 105 */         this.key = paramVarArgs[++b];
/* 106 */         this.keyDatabase = paramVarArgs[++b];
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean getTrace() {
/* 114 */     return this.trace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void trace(String paramString) {
/* 123 */     if (this.trace) {
/* 124 */       System.out.println(paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void remove(PgServerThread paramPgServerThread) {
/* 134 */     this.running.remove(paramPgServerThread);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void traceError(Exception paramException) {
/* 143 */     if (this.trace) {
/* 144 */       paramException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getURL() {
/* 150 */     return "pg://" + NetUtils.getLocalAddress() + ":" + this.port;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 155 */     return this.port;
/*     */   }
/*     */   
/*     */   private boolean allow(Socket paramSocket) {
/* 159 */     if (this.allowOthers) {
/* 160 */       return true;
/*     */     }
/*     */     try {
/* 163 */       return NetUtils.isLocalAddress(paramSocket);
/* 164 */     } catch (UnknownHostException unknownHostException) {
/* 165 */       traceError(unknownHostException);
/* 166 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void start() {
/* 172 */     this.stop = false;
/*     */     try {
/* 174 */       this.serverSocket = NetUtils.createServerSocket(this.port, false);
/* 175 */     } catch (DbException dbException) {
/* 176 */       if (!this.portIsSet) {
/* 177 */         this.serverSocket = NetUtils.createServerSocket(0, false);
/*     */       } else {
/* 179 */         throw dbException;
/*     */       } 
/*     */     } 
/* 182 */     this.port = this.serverSocket.getLocalPort();
/*     */   }
/*     */ 
/*     */   
/*     */   public void listen() {
/* 187 */     String str = Thread.currentThread().getName();
/*     */     try {
/* 189 */       while (!this.stop) {
/* 190 */         Socket socket = this.serverSocket.accept();
/* 191 */         if (!allow(socket)) {
/* 192 */           trace("Connection not allowed");
/* 193 */           socket.close(); continue;
/*     */         } 
/* 195 */         Utils10.setTcpQuickack(socket, true);
/* 196 */         PgServerThread pgServerThread = new PgServerThread(socket, this);
/* 197 */         this.running.add(pgServerThread);
/* 198 */         int i = this.pid.incrementAndGet();
/* 199 */         pgServerThread.setProcessId(i);
/* 200 */         Thread thread = new Thread(pgServerThread, str + " thread-" + i);
/* 201 */         thread.setDaemon(this.isDaemon);
/* 202 */         pgServerThread.setThread(thread);
/* 203 */         thread.start();
/*     */       }
/*     */     
/* 206 */     } catch (Exception exception) {
/* 207 */       if (!this.stop) {
/* 208 */         exception.printStackTrace();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 216 */     if (!this.stop) {
/* 217 */       this.stop = true;
/* 218 */       if (this.serverSocket != null) {
/*     */         try {
/* 220 */           this.serverSocket.close();
/* 221 */         } catch (IOException iOException) {
/*     */           
/* 223 */           iOException.printStackTrace();
/*     */         } 
/* 225 */         this.serverSocket = null;
/*     */       } 
/*     */     } 
/*     */     
/* 229 */     for (PgServerThread pgServerThread : new ArrayList(this.running)) {
/* 230 */       pgServerThread.close();
/*     */       try {
/* 232 */         Thread thread = pgServerThread.getThread();
/* 233 */         if (thread != null) {
/* 234 */           thread.join(100L);
/*     */         }
/* 236 */       } catch (Exception exception) {
/*     */         
/* 238 */         exception.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRunning(boolean paramBoolean) {
/* 245 */     if (this.serverSocket == null) {
/* 246 */       return false;
/*     */     }
/*     */     try {
/* 249 */       Socket socket = NetUtils.createLoopbackSocket(this.serverSocket.getLocalPort(), false);
/* 250 */       socket.close();
/* 251 */       return true;
/* 252 */     } catch (Exception exception) {
/* 253 */       if (paramBoolean) {
/* 254 */         traceError(exception);
/*     */       }
/* 256 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   PgServerThread getThread(int paramInt) {
/* 267 */     for (PgServerThread pgServerThread : new ArrayList(this.running)) {
/* 268 */       if (pgServerThread.getProcessId() == paramInt) {
/* 269 */         return pgServerThread;
/*     */       }
/*     */     } 
/* 272 */     return null;
/*     */   }
/*     */   
/*     */   String getBaseDir() {
/* 276 */     return this.baseDir;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getAllowOthers() {
/* 281 */     return this.allowOthers;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getType() {
/* 286 */     return "PG";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 291 */     return "H2 PG Server";
/*     */   }
/*     */   
/*     */   boolean getIfExists() {
/* 295 */     return this.ifExists;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatType(int paramInt) {
/*     */     byte b;
/* 306 */     switch (paramInt) {
/*     */       case 0:
/* 308 */         return "-";
/*     */       case 16:
/* 310 */         b = 8;
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
/* 376 */         return Value.getTypeName(b);case 17: b = 6; return Value.getTypeName(b);case 18: return "char";case 19: return "name";case 20: b = 12; return Value.getTypeName(b);case 21: b = 10; return Value.getTypeName(b);case 22: return "int2vector";case 23: b = 11; return Value.getTypeName(b);case 24: return "regproc";case 25: b = 3; return Value.getTypeName(b);case 700: b = 14; return Value.getTypeName(b);case 701: b = 15; return Value.getTypeName(b);case 1005: return "smallint[]";case 1007: return "integer[]";case 1015: return "character varying[]";case 1042: b = 1; return Value.getTypeName(b);case 1043: b = 2; return Value.getTypeName(b);case 1082: b = 17; return Value.getTypeName(b);case 1083: b = 18; return Value.getTypeName(b);case 1266: b = 19; return Value.getTypeName(b);case 1114: b = 20; return Value.getTypeName(b);case 1184: b = 21; return Value.getTypeName(b);case 1700: b = 13; return Value.getTypeName(b);
/*     */       case 2205:
/*     */         return "regclass";
/*     */     } 
/*     */     return "???";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int convertType(TypeInfo paramTypeInfo) {
/* 386 */     switch (paramTypeInfo.getValueType()) {
/*     */       case 8:
/* 388 */         return 16;
/*     */       case 2:
/* 390 */         return 1043;
/*     */       case 0:
/*     */       case 3:
/* 393 */         return 25;
/*     */       case 1:
/* 395 */         return 1042;
/*     */       case 10:
/* 397 */         return 21;
/*     */       case 11:
/* 399 */         return 23;
/*     */       case 12:
/* 401 */         return 20;
/*     */       case 13:
/*     */       case 16:
/* 404 */         return 1700;
/*     */       case 14:
/* 406 */         return 700;
/*     */       case 15:
/* 408 */         return 701;
/*     */       case 18:
/* 410 */         return 1083;
/*     */       case 19:
/* 412 */         return 1266;
/*     */       case 17:
/* 414 */         return 1082;
/*     */       case 20:
/* 416 */         return 1114;
/*     */       case 21:
/* 418 */         return 1184;
/*     */       case 5:
/*     */       case 6:
/* 421 */         return 17;
/*     */       case 40:
/* 423 */         paramTypeInfo = (TypeInfo)paramTypeInfo.getExtTypeInfo();
/* 424 */         switch (paramTypeInfo.getValueType()) {
/*     */           case 10:
/* 426 */             return 1005;
/*     */           case 11:
/* 428 */             return 1007;
/*     */           case 2:
/* 430 */             return 1015;
/*     */         } 
/* 432 */         return 1015;
/*     */     } 
/*     */ 
/*     */     
/* 436 */     return 705;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   HashSet<Integer> getTypeSet() {
/* 446 */     return this.typeSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void checkType(int paramInt) {
/* 456 */     if (!this.typeSet.contains(Integer.valueOf(paramInt))) {
/* 457 */       trace("Unsupported type: " + paramInt);
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
/* 471 */     if (this.key == null) {
/* 472 */       return paramString;
/*     */     }
/* 474 */     if (this.key.equals(paramString)) {
/* 475 */       return this.keyDatabase;
/*     */     }
/* 477 */     throw DbException.get(28000);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDaemon() {
/* 482 */     return this.isDaemon;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\pg\PgServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */