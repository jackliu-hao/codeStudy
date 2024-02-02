/*     */ package org.h2.server.web;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.time.ZoneId;
/*     */ import java.time.ZonedDateTime;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.security.SHA256;
/*     */ import org.h2.server.Service;
/*     */ import org.h2.server.ShutdownHandler;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.util.NetUtils;
/*     */ import org.h2.util.NetworkConnectionInfo;
/*     */ import org.h2.util.SortedProperties;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Tool;
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
/*     */ public class WebServer
/*     */   implements Service
/*     */ {
/*  55 */   static final String[][] LANGUAGES = new String[][] { { "cs", "Čeština" }, { "de", "Deutsch" }, { "en", "English" }, { "es", "Español" }, { "fr", "Français" }, { "hi", "Hindi हिंदी" }, { "hu", "Magyar" }, { "ko", "한국어" }, { "in", "Indonesia" }, { "it", "Italiano" }, { "ja", "日本語" }, { "nl", "Nederlands" }, { "pl", "Polski" }, { "pt_BR", "Português (Brasil)" }, { "pt_PT", "Português (Europeu)" }, { "ru", "русский" }, { "sk", "Slovensky" }, { "tr", "Türkçe" }, { "uk", "Українська" }, { "zh_CN", "中文 (简体)" }, { "zh_TW", "中文 (繁體)" } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String COMMAND_HISTORY = "commandHistory";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String DEFAULT_LANGUAGE = "en";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   private static final String[] GENERIC = new String[] { "Generic JNDI Data Source|javax.naming.InitialContext|java:comp/env/jdbc/Test|sa", "Generic Teradata|com.teradata.jdbc.TeraDriver|jdbc:teradata://whomooz/|", "Generic Snowflake|com.snowflake.client.jdbc.SnowflakeDriver|jdbc:snowflake://accountName.snowflakecomputing.com|", "Generic Redshift|com.amazon.redshift.jdbc42.Driver|jdbc:redshift://endpoint:5439/database|", "Generic Impala|org.cloudera.impala.jdbc41.Driver|jdbc:impala://clustername:21050/default|", "Generic Hive 2|org.apache.hive.jdbc.HiveDriver|jdbc:hive2://clustername:10000/default|", "Generic Hive|org.apache.hadoop.hive.jdbc.HiveDriver|jdbc:hive://clustername:10000/default|", "Generic Azure SQL|com.microsoft.sqlserver.jdbc.SQLServerDriver|jdbc:sqlserver://name.database.windows.net:1433|", "Generic Firebird Server|org.firebirdsql.jdbc.FBDriver|jdbc:firebirdsql:localhost:c:/temp/firebird/test|sysdba", "Generic SQLite|org.sqlite.JDBC|jdbc:sqlite:test|sa", "Generic DB2|com.ibm.db2.jcc.DB2Driver|jdbc:db2://localhost/test|", "Generic Oracle|oracle.jdbc.driver.OracleDriver|jdbc:oracle:thin:@localhost:1521:XE|sa", "Generic MS SQL Server 2000|com.microsoft.jdbc.sqlserver.SQLServerDriver|jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=sqlexpress|sa", "Generic MS SQL Server 2005|com.microsoft.sqlserver.jdbc.SQLServerDriver|jdbc:sqlserver://localhost;DatabaseName=test|sa", "Generic PostgreSQL|org.postgresql.Driver|jdbc:postgresql:test|", "Generic MySQL|com.mysql.cj.jdbc.Driver|jdbc:mysql://localhost:3306/test|", "Generic MariaDB|org.mariadb.jdbc.Driver|jdbc:mariadb://localhost:3306/test|", "Generic HSQLDB|org.hsqldb.jdbcDriver|jdbc:hsqldb:test;hsqldb.default_table_type=cached|sa", "Generic Derby (Server)|org.apache.derby.client.ClientAutoloadedDriver|jdbc:derby://localhost:1527/test;create=true|sa", "Generic Derby (Embedded)|org.apache.derby.iapi.jdbc.AutoloadedDriver|jdbc:derby:test;create=true|sa", "Generic H2 (Server)|org.h2.Driver|jdbc:h2:tcp://localhost/~/test|sa", "Generic H2 (Embedded)|org.h2.Driver|jdbc:h2:~/test|sa" };
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
/*     */   private static int ticker;
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
/* 136 */   private static final long SESSION_TIMEOUT = SysProperties.CONSOLE_TIMEOUT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int port;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean allowOthers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String externalNames;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isDaemon;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 165 */   private final Set<WebThread> running = Collections.synchronizedSet(new HashSet<>());
/*     */   private boolean ssl;
/*     */   private byte[] adminPassword;
/* 168 */   private final HashMap<String, ConnectionInfo> connInfoMap = new HashMap<>();
/*     */   
/*     */   private long lastTimeoutCheck;
/* 171 */   private final HashMap<String, WebSession> sessions = new HashMap<>();
/* 172 */   private final HashSet<String> languages = new HashSet<>();
/*     */   private String startDateTime;
/*     */   private ServerSocket serverSocket;
/*     */   private String host;
/*     */   private String url;
/*     */   private ShutdownHandler shutdownHandler;
/*     */   private Thread listenerThread;
/*     */   private boolean ifExists = true;
/*     */   private String key;
/*     */   private boolean allowSecureCreation;
/*     */   private boolean trace;
/*     */   private TranslateThread translateThread;
/*     */   private boolean allowChunked = true;
/* 185 */   private String serverPropertiesDir = "~";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String commandHistoryString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] getFile(String paramString) throws IOException {
/* 197 */     trace("getFile <" + paramString + ">");
/* 198 */     byte[] arrayOfByte = Utils.getResource("/org/h2/server/web/res/" + paramString);
/* 199 */     if (arrayOfByte == null) {
/* 200 */       trace(" null");
/*     */     } else {
/* 202 */       trace(" size=" + arrayOfByte.length);
/*     */     } 
/* 204 */     return arrayOfByte;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void remove(WebThread paramWebThread) {
/* 213 */     this.running.remove(paramWebThread);
/*     */   }
/*     */   
/*     */   private static String generateSessionId() {
/* 217 */     byte[] arrayOfByte = MathUtils.secureRandomBytes(16);
/* 218 */     return StringUtils.convertBytesToHex(arrayOfByte);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   WebSession getSession(String paramString) {
/* 228 */     long l = System.currentTimeMillis();
/* 229 */     if (this.lastTimeoutCheck + SESSION_TIMEOUT < l) {
/* 230 */       for (String str : new ArrayList(this.sessions.keySet())) {
/* 231 */         WebSession webSession1 = this.sessions.get(str);
/* 232 */         if (webSession1.lastAccess + SESSION_TIMEOUT < l) {
/* 233 */           trace("timeout for " + str);
/* 234 */           this.sessions.remove(str);
/*     */         } 
/*     */       } 
/* 237 */       this.lastTimeoutCheck = l;
/*     */     } 
/* 239 */     WebSession webSession = this.sessions.get(paramString);
/* 240 */     if (webSession != null) {
/* 241 */       webSession.lastAccess = System.currentTimeMillis();
/*     */     }
/* 243 */     return webSession;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   WebSession createNewSession(String paramString) {
/*     */     while (true) {
/* 255 */       String str = generateSessionId();
/* 256 */       if (this.sessions.get(str) == null) {
/* 257 */         WebSession webSession = new WebSession(this);
/* 258 */         webSession.lastAccess = System.currentTimeMillis();
/* 259 */         webSession.put("sessionId", str);
/* 260 */         webSession.put("ip", paramString);
/* 261 */         webSession.put("language", "en");
/* 262 */         webSession.put("frame-border", "0");
/* 263 */         webSession.put("frameset-border", "4");
/* 264 */         this.sessions.put(str, webSession);
/*     */ 
/*     */         
/* 267 */         readTranslations(webSession, "en");
/* 268 */         return getSession(str);
/*     */       } 
/*     */     } 
/*     */   } String getStartDateTime() {
/* 272 */     if (this.startDateTime == null) {
/* 273 */       this
/* 274 */         .startDateTime = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH).format(ZonedDateTime.now(ZoneId.of("UTC")));
/*     */     }
/* 276 */     return this.startDateTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getKey() {
/* 285 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setKey(String paramString) {
/* 294 */     if (!this.allowOthers) {
/* 295 */       this.key = paramString;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAllowSecureCreation(boolean paramBoolean) {
/* 304 */     if (!this.allowOthers) {
/* 305 */       this.allowSecureCreation = paramBoolean;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(String... paramVarArgs) {
/* 312 */     for (byte b1 = 0; paramVarArgs != null && b1 < paramVarArgs.length; b1++) {
/* 313 */       if ("-properties".equals(paramVarArgs[b1])) {
/* 314 */         this.serverPropertiesDir = paramVarArgs[++b1];
/*     */       }
/*     */     } 
/* 317 */     Properties properties = loadProperties();
/* 318 */     this.port = SortedProperties.getIntProperty(properties, "webPort", 8082);
/*     */     
/* 320 */     this.ssl = SortedProperties.getBooleanProperty(properties, "webSSL", false);
/*     */     
/* 322 */     this.allowOthers = SortedProperties.getBooleanProperty(properties, "webAllowOthers", false);
/*     */     
/* 324 */     setExternalNames(SortedProperties.getStringProperty(properties, "webExternalNames", null));
/* 325 */     setAdminPassword(SortedProperties.getStringProperty(properties, "webAdminPassword", null));
/* 326 */     this.commandHistoryString = properties.getProperty("commandHistory");
/* 327 */     for (byte b2 = 0; paramVarArgs != null && b2 < paramVarArgs.length; b2++) {
/* 328 */       String str = paramVarArgs[b2];
/* 329 */       if (Tool.isOption(str, "-webPort")) {
/* 330 */         this.port = Integer.decode(paramVarArgs[++b2]).intValue();
/* 331 */       } else if (Tool.isOption(str, "-webSSL")) {
/* 332 */         this.ssl = true;
/* 333 */       } else if (Tool.isOption(str, "-webAllowOthers")) {
/* 334 */         this.allowOthers = true;
/* 335 */       } else if (Tool.isOption(str, "-webExternalNames")) {
/* 336 */         setExternalNames(paramVarArgs[++b2]);
/* 337 */       } else if (Tool.isOption(str, "-webDaemon")) {
/* 338 */         this.isDaemon = true;
/* 339 */       } else if (Tool.isOption(str, "-baseDir")) {
/* 340 */         String str1 = paramVarArgs[++b2];
/* 341 */         SysProperties.setBaseDir(str1);
/* 342 */       } else if (Tool.isOption(str, "-ifExists")) {
/* 343 */         this.ifExists = true;
/* 344 */       } else if (Tool.isOption(str, "-ifNotExists")) {
/* 345 */         this.ifExists = false;
/* 346 */       } else if (Tool.isOption(str, "-webAdminPassword")) {
/* 347 */         setAdminPassword(paramVarArgs[++b2]);
/* 348 */       } else if (Tool.isOption(str, "-properties")) {
/*     */         
/* 350 */         b2++;
/* 351 */       } else if (Tool.isOption(str, "-trace")) {
/* 352 */         this.trace = true;
/*     */       } 
/*     */     } 
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
/* 368 */     for (String[] arrayOfString : LANGUAGES) {
/* 369 */       this.languages.add(arrayOfString[0]);
/*     */     }
/* 371 */     if (this.allowOthers) {
/* 372 */       this.key = null;
/*     */     }
/* 374 */     updateURL();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getURL() {
/* 379 */     updateURL();
/* 380 */     return this.url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHost() {
/* 387 */     if (this.host == null) {
/* 388 */       updateURL();
/*     */     }
/* 390 */     return this.host;
/*     */   }
/*     */   
/*     */   private void updateURL() {
/*     */     try {
/* 395 */       this.host = StringUtils.toLowerEnglish(NetUtils.getLocalAddress());
/*     */       
/* 397 */       StringBuilder stringBuilder = (new StringBuilder(this.ssl ? "https" : "http")).append("://").append(this.host).append(':').append(this.port);
/* 398 */       if (this.key != null && this.serverSocket != null) {
/* 399 */         stringBuilder.append("?key=").append(this.key);
/*     */       }
/* 401 */       this.url = stringBuilder.toString();
/* 402 */     } catch (NoClassDefFoundError noClassDefFoundError) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/* 409 */     this.serverSocket = NetUtils.createServerSocket(this.port, this.ssl);
/* 410 */     this.port = this.serverSocket.getLocalPort();
/* 411 */     updateURL();
/*     */   }
/*     */ 
/*     */   
/*     */   public void listen() {
/* 416 */     this.listenerThread = Thread.currentThread();
/*     */     try {
/* 418 */       while (this.serverSocket != null) {
/* 419 */         Socket socket = this.serverSocket.accept();
/* 420 */         WebThread webThread = new WebThread(socket, this);
/* 421 */         this.running.add(webThread);
/* 422 */         webThread.start();
/*     */       } 
/* 424 */     } catch (Exception exception) {
/* 425 */       trace(exception.toString());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRunning(boolean paramBoolean) {
/* 431 */     if (this.serverSocket == null) {
/* 432 */       return false;
/*     */     }
/*     */     try {
/* 435 */       Socket socket = NetUtils.createLoopbackSocket(this.port, this.ssl);
/* 436 */       socket.close();
/* 437 */       return true;
/* 438 */     } catch (Exception exception) {
/* 439 */       if (paramBoolean) {
/* 440 */         traceError(exception);
/*     */       }
/* 442 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isStopped() {
/* 447 */     return (this.serverSocket == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void stop() {
/* 452 */     if (this.serverSocket != null) {
/*     */       try {
/* 454 */         this.serverSocket.close();
/* 455 */       } catch (IOException iOException) {
/* 456 */         traceError(iOException);
/*     */       } 
/* 458 */       this.serverSocket = null;
/*     */     } 
/* 460 */     if (this.listenerThread != null) {
/*     */       try {
/* 462 */         this.listenerThread.join(1000L);
/* 463 */       } catch (InterruptedException interruptedException) {
/* 464 */         DbException.traceThrowable(interruptedException);
/*     */       } 
/*     */     }
/*     */     
/* 468 */     for (WebSession webSession : new ArrayList(this.sessions.values())) {
/* 469 */       webSession.close();
/*     */     }
/* 471 */     for (WebThread webThread : new ArrayList(this.running)) {
/*     */       try {
/* 473 */         webThread.stopNow();
/* 474 */         webThread.join(100);
/* 475 */       } catch (Exception exception) {
/* 476 */         traceError(exception);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void trace(String paramString) {
/* 487 */     if (this.trace) {
/* 488 */       System.out.println(paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void traceError(Throwable paramThrowable) {
/* 498 */     if (this.trace) {
/* 499 */       paramThrowable.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean supportsLanguage(String paramString) {
/* 510 */     return this.languages.contains(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void readTranslations(WebSession paramWebSession, String paramString) {
/*     */     SortedProperties sortedProperties;
/* 521 */     Properties properties = new Properties();
/*     */     try {
/* 523 */       trace("translation: " + paramString);
/* 524 */       byte[] arrayOfByte = getFile("_text_" + paramString + ".prop");
/* 525 */       trace("  " + new String(arrayOfByte));
/* 526 */       sortedProperties = SortedProperties.fromLines(new String(arrayOfByte, StandardCharsets.UTF_8));
/*     */       
/* 528 */       for (Map.Entry<Object, Object> entry : sortedProperties.entrySet()) {
/* 529 */         String str = (String)entry.getValue();
/* 530 */         if (str.startsWith("#")) {
/* 531 */           entry.setValue(str.substring(1));
/*     */         }
/*     */       } 
/* 534 */     } catch (IOException iOException) {
/* 535 */       DbException.traceThrowable(iOException);
/*     */     } 
/* 537 */     paramWebSession.put("text", new HashMap<>((Map<?, ?>)sortedProperties));
/*     */   }
/*     */   
/*     */   ArrayList<HashMap<String, Object>> getSessions() {
/* 541 */     ArrayList<HashMap<String, Object>> arrayList = new ArrayList(this.sessions.size());
/* 542 */     for (WebSession webSession : this.sessions.values()) {
/* 543 */       arrayList.add(webSession.getInfo());
/*     */     }
/* 545 */     return arrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getType() {
/* 550 */     return "Web Console";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 555 */     return "H2 Console Server";
/*     */   }
/*     */   
/*     */   void setAllowOthers(boolean paramBoolean) {
/* 559 */     if (paramBoolean) {
/* 560 */       this.key = null;
/*     */     }
/* 562 */     this.allowOthers = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getAllowOthers() {
/* 567 */     return this.allowOthers;
/*     */   }
/*     */   
/*     */   void setExternalNames(String paramString) {
/* 571 */     this.externalNames = (paramString != null) ? StringUtils.toLowerEnglish(paramString) : null;
/*     */   }
/*     */   
/*     */   String getExternalNames() {
/* 575 */     return this.externalNames;
/*     */   }
/*     */   
/*     */   void setSSL(boolean paramBoolean) {
/* 579 */     this.ssl = paramBoolean;
/*     */   }
/*     */   
/*     */   void setPort(int paramInt) {
/* 583 */     this.port = paramInt;
/*     */   }
/*     */   
/*     */   boolean getSSL() {
/* 587 */     return this.ssl;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPort() {
/* 592 */     return this.port;
/*     */   }
/*     */   
/*     */   public boolean isCommandHistoryAllowed() {
/* 596 */     return (this.commandHistoryString != null);
/*     */   }
/*     */   
/*     */   public void setCommandHistoryAllowed(boolean paramBoolean) {
/* 600 */     if (paramBoolean) {
/* 601 */       if (this.commandHistoryString == null) {
/* 602 */         this.commandHistoryString = "";
/*     */       }
/*     */     } else {
/* 605 */       this.commandHistoryString = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public ArrayList<String> getCommandHistoryList() {
/* 610 */     ArrayList<String> arrayList = new ArrayList();
/* 611 */     if (this.commandHistoryString == null) {
/* 612 */       return arrayList;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 617 */     StringBuilder stringBuilder = new StringBuilder();
/* 618 */     for (byte b = 0;; b++) {
/* 619 */       if (b == this.commandHistoryString.length() || this.commandHistoryString
/* 620 */         .charAt(b) == ';') {
/* 621 */         if (stringBuilder.length() > 0) {
/* 622 */           arrayList.add(stringBuilder.toString());
/* 623 */           stringBuilder.delete(0, stringBuilder.length());
/*     */         } 
/* 625 */         if (b == this.commandHistoryString.length()) {
/*     */           break;
/*     */         }
/* 628 */       } else if (this.commandHistoryString.charAt(b) == '\\' && b < this.commandHistoryString
/* 629 */         .length() - 1) {
/* 630 */         stringBuilder.append(this.commandHistoryString.charAt(++b));
/*     */       } else {
/* 632 */         stringBuilder.append(this.commandHistoryString.charAt(b));
/*     */       } 
/*     */     } 
/* 635 */     return arrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void saveCommandHistoryList(ArrayList<String> paramArrayList) {
/* 644 */     StringBuilder stringBuilder = new StringBuilder();
/* 645 */     for (String str : paramArrayList) {
/* 646 */       if (stringBuilder.length() > 0) {
/* 647 */         stringBuilder.append(';');
/*     */       }
/* 649 */       stringBuilder.append(str.replace("\\", "\\\\").replace(";", "\\;"));
/*     */     } 
/* 651 */     this.commandHistoryString = stringBuilder.toString();
/* 652 */     saveProperties(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ConnectionInfo getSetting(String paramString) {
/* 662 */     return this.connInfoMap.get(paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void updateSetting(ConnectionInfo paramConnectionInfo) {
/* 671 */     this.connInfoMap.put(paramConnectionInfo.name, paramConnectionInfo);
/* 672 */     paramConnectionInfo.lastAccess = ticker++;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void removeSetting(String paramString) {
/* 681 */     this.connInfoMap.remove(paramString);
/*     */   }
/*     */   
/*     */   private Properties loadProperties() {
/*     */     try {
/* 686 */       if ("null".equals(this.serverPropertiesDir)) {
/* 687 */         return new Properties();
/*     */       }
/* 689 */       return (Properties)SortedProperties.loadProperties(this.serverPropertiesDir + "/" + ".h2.server.properties");
/*     */     }
/* 691 */     catch (Exception exception) {
/* 692 */       DbException.traceThrowable(exception);
/* 693 */       return new Properties();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String[] getSettingNames() {
/* 703 */     ArrayList<ConnectionInfo> arrayList = getSettings();
/* 704 */     String[] arrayOfString = new String[arrayList.size()];
/* 705 */     for (byte b = 0; b < arrayList.size(); b++) {
/* 706 */       arrayOfString[b] = ((ConnectionInfo)arrayList.get(b)).name;
/*     */     }
/* 708 */     return arrayOfString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized ArrayList<ConnectionInfo> getSettings() {
/* 717 */     ArrayList<ConnectionInfo> arrayList = new ArrayList();
/* 718 */     if (this.connInfoMap.size() == 0) {
/* 719 */       Properties properties = loadProperties();
/* 720 */       if (properties.size() == 0) {
/* 721 */         for (String str : GENERIC) {
/* 722 */           ConnectionInfo connectionInfo = new ConnectionInfo(str);
/* 723 */           arrayList.add(connectionInfo);
/* 724 */           updateSetting(connectionInfo);
/*     */         } 
/*     */       } else {
/* 727 */         for (byte b = 0;; b++) {
/* 728 */           String str = properties.getProperty(Integer.toString(b));
/* 729 */           if (str == null) {
/*     */             break;
/*     */           }
/* 732 */           ConnectionInfo connectionInfo = new ConnectionInfo(str);
/* 733 */           arrayList.add(connectionInfo);
/* 734 */           updateSetting(connectionInfo);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 738 */       arrayList.addAll(this.connInfoMap.values());
/*     */     } 
/* 740 */     Collections.sort(arrayList);
/* 741 */     return arrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   synchronized void saveProperties(Properties paramProperties) {
/*     */     try {
/*     */       SortedProperties sortedProperties;
/* 751 */       if (paramProperties == null) {
/* 752 */         Properties properties = loadProperties();
/* 753 */         sortedProperties = new SortedProperties();
/* 754 */         sortedProperties.setProperty("webPort", 
/* 755 */             Integer.toString(SortedProperties.getIntProperty(properties, "webPort", this.port)));
/* 756 */         sortedProperties.setProperty("webAllowOthers", 
/* 757 */             Boolean.toString(SortedProperties.getBooleanProperty(properties, "webAllowOthers", this.allowOthers)));
/* 758 */         if (this.externalNames != null) {
/* 759 */           sortedProperties.setProperty("webExternalNames", this.externalNames);
/*     */         }
/* 761 */         sortedProperties.setProperty("webSSL", 
/* 762 */             Boolean.toString(SortedProperties.getBooleanProperty(properties, "webSSL", this.ssl)));
/* 763 */         if (this.adminPassword != null) {
/* 764 */           sortedProperties.setProperty("webAdminPassword", StringUtils.convertBytesToHex(this.adminPassword));
/*     */         }
/* 766 */         if (this.commandHistoryString != null) {
/* 767 */           sortedProperties.setProperty("commandHistory", this.commandHistoryString);
/*     */         }
/*     */       } 
/* 770 */       ArrayList<ConnectionInfo> arrayList = getSettings();
/* 771 */       int i = arrayList.size();
/* 772 */       for (byte b = 0; b < i; b++) {
/* 773 */         ConnectionInfo connectionInfo = arrayList.get(b);
/* 774 */         if (connectionInfo != null) {
/* 775 */           sortedProperties.setProperty(Integer.toString(i - b - 1), connectionInfo.getString());
/*     */         }
/*     */       } 
/* 778 */       if (!"null".equals(this.serverPropertiesDir)) {
/* 779 */         OutputStream outputStream = FileUtils.newOutputStream(this.serverPropertiesDir + "/" + ".h2.server.properties", false);
/*     */         
/* 781 */         sortedProperties.store(outputStream, "H2 Server Properties");
/* 782 */         outputStream.close();
/*     */       } 
/* 784 */     } catch (Exception exception) {
/* 785 */       DbException.traceThrowable(exception);
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
/*     */   Connection getConnection(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, NetworkConnectionInfo paramNetworkConnectionInfo) throws SQLException {
/* 803 */     paramString1 = paramString1.trim();
/* 804 */     paramString2 = paramString2.trim();
/*     */ 
/*     */     
/* 807 */     return JdbcUtils.getConnection(paramString1, paramString2, paramString3.trim(), paramString4, paramNetworkConnectionInfo, (this.ifExists && (!this.allowSecureCreation || this.key == null || 
/* 808 */         !this.key.equals(paramString5))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void shutdown() {
/* 815 */     if (this.shutdownHandler != null) {
/* 816 */       this.shutdownHandler.shutdown();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setShutdownHandler(ShutdownHandler paramShutdownHandler) {
/* 821 */     this.shutdownHandler = paramShutdownHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String addSession(Connection paramConnection) throws SQLException {
/* 832 */     WebSession webSession = createNewSession("local");
/* 833 */     webSession.setShutdownServerOnDisconnect();
/* 834 */     webSession.setConnection(paramConnection);
/* 835 */     webSession.put("url", paramConnection.getMetaData().getURL());
/* 836 */     String str = (String)webSession.get("sessionId");
/* 837 */     return this.url + "/frame.jsp?jsessionid=" + str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class TranslateThread
/*     */     extends Thread
/*     */   {
/* 846 */     private final Path file = Paths.get("translation.properties", new String[0]);
/*     */     private final Map<Object, Object> translation;
/*     */     private volatile boolean stopNow;
/*     */     
/*     */     TranslateThread(Map<Object, Object> param1Map) {
/* 851 */       this.translation = param1Map;
/*     */     }
/*     */     
/*     */     public String getFileName() {
/* 855 */       return this.file.toAbsolutePath().toString();
/*     */     }
/*     */     
/*     */     public void stopNow() {
/* 859 */       this.stopNow = true;
/*     */       try {
/* 861 */         join();
/* 862 */       } catch (InterruptedException interruptedException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 869 */       while (!this.stopNow) {
/*     */         try {
/* 871 */           SortedProperties sortedProperties = new SortedProperties();
/* 872 */           if (Files.exists(this.file, new java.nio.file.LinkOption[0])) {
/* 873 */             InputStream inputStream = Files.newInputStream(this.file, new java.nio.file.OpenOption[0]);
/* 874 */             sortedProperties.load(inputStream);
/* 875 */             this.translation.putAll((Map<?, ?>)sortedProperties);
/*     */           } else {
/* 877 */             OutputStream outputStream = Files.newOutputStream(this.file, new java.nio.file.OpenOption[0]);
/* 878 */             sortedProperties.putAll(this.translation);
/* 879 */             sortedProperties.store(outputStream, "Translation");
/*     */           } 
/* 881 */           Thread.sleep(1000L);
/* 882 */         } catch (Exception exception) {
/* 883 */           WebServer.this.traceError(exception);
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
/*     */   String startTranslate(Map<Object, Object> paramMap) {
/* 897 */     if (this.translateThread != null) {
/* 898 */       this.translateThread.stopNow();
/*     */     }
/* 900 */     this.translateThread = new TranslateThread(paramMap);
/* 901 */     this.translateThread.setDaemon(true);
/* 902 */     this.translateThread.start();
/* 903 */     return this.translateThread.getFileName();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDaemon() {
/* 908 */     return this.isDaemon;
/*     */   }
/*     */   
/*     */   void setAllowChunked(boolean paramBoolean) {
/* 912 */     this.allowChunked = paramBoolean;
/*     */   }
/*     */   
/*     */   boolean getAllowChunked() {
/* 916 */     return this.allowChunked;
/*     */   }
/*     */   
/*     */   byte[] getAdminPassword() {
/* 920 */     return this.adminPassword;
/*     */   }
/*     */   
/*     */   void setAdminPassword(String paramString) {
/* 924 */     if (paramString == null || paramString.isEmpty()) {
/* 925 */       this.adminPassword = null;
/*     */       return;
/*     */     } 
/* 928 */     if (paramString.length() == 128) {
/*     */       try {
/* 930 */         this.adminPassword = StringUtils.convertHexToBytes(paramString);
/*     */         return;
/* 932 */       } catch (Exception exception) {}
/*     */     }
/* 934 */     byte[] arrayOfByte1 = MathUtils.secureRandomBytes(32);
/* 935 */     byte[] arrayOfByte2 = SHA256.getHashWithSalt(paramString.getBytes(StandardCharsets.UTF_8), arrayOfByte1);
/* 936 */     byte[] arrayOfByte3 = Arrays.copyOf(arrayOfByte1, 64);
/* 937 */     System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 32, 32);
/* 938 */     this.adminPassword = arrayOfByte3;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean checkAdminPassword(String paramString) {
/* 948 */     if (this.adminPassword == null) {
/* 949 */       return false;
/*     */     }
/* 951 */     byte[] arrayOfByte1 = Arrays.copyOf(this.adminPassword, 32);
/* 952 */     byte[] arrayOfByte2 = new byte[32];
/* 953 */     System.arraycopy(this.adminPassword, 32, arrayOfByte2, 0, 32);
/* 954 */     return Utils.compareSecure(arrayOfByte2, SHA256.getHashWithSalt(paramString.getBytes(StandardCharsets.UTF_8), arrayOfByte1));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\web\WebServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */