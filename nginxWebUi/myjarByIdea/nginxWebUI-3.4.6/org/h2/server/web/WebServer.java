package org.h2.server.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.server.Service;
import org.h2.server.ShutdownHandler;
import org.h2.store.fs.FileUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;

public class WebServer implements Service {
   static final String[][] LANGUAGES = new String[][]{{"cs", "Čeština"}, {"de", "Deutsch"}, {"en", "English"}, {"es", "Español"}, {"fr", "Français"}, {"hi", "Hindi हिंदी"}, {"hu", "Magyar"}, {"ko", "한국어"}, {"in", "Indonesia"}, {"it", "Italiano"}, {"ja", "日本語"}, {"nl", "Nederlands"}, {"pl", "Polski"}, {"pt_BR", "Português (Brasil)"}, {"pt_PT", "Português (Europeu)"}, {"ru", "русский"}, {"sk", "Slovensky"}, {"tr", "Türkçe"}, {"uk", "Українська"}, {"zh_CN", "中文 (简体)"}, {"zh_TW", "中文 (繁體)"}};
   private static final String COMMAND_HISTORY = "commandHistory";
   private static final String DEFAULT_LANGUAGE = "en";
   private static final String[] GENERIC = new String[]{"Generic JNDI Data Source|javax.naming.InitialContext|java:comp/env/jdbc/Test|sa", "Generic Teradata|com.teradata.jdbc.TeraDriver|jdbc:teradata://whomooz/|", "Generic Snowflake|com.snowflake.client.jdbc.SnowflakeDriver|jdbc:snowflake://accountName.snowflakecomputing.com|", "Generic Redshift|com.amazon.redshift.jdbc42.Driver|jdbc:redshift://endpoint:5439/database|", "Generic Impala|org.cloudera.impala.jdbc41.Driver|jdbc:impala://clustername:21050/default|", "Generic Hive 2|org.apache.hive.jdbc.HiveDriver|jdbc:hive2://clustername:10000/default|", "Generic Hive|org.apache.hadoop.hive.jdbc.HiveDriver|jdbc:hive://clustername:10000/default|", "Generic Azure SQL|com.microsoft.sqlserver.jdbc.SQLServerDriver|jdbc:sqlserver://name.database.windows.net:1433|", "Generic Firebird Server|org.firebirdsql.jdbc.FBDriver|jdbc:firebirdsql:localhost:c:/temp/firebird/test|sysdba", "Generic SQLite|org.sqlite.JDBC|jdbc:sqlite:test|sa", "Generic DB2|com.ibm.db2.jcc.DB2Driver|jdbc:db2://localhost/test|", "Generic Oracle|oracle.jdbc.driver.OracleDriver|jdbc:oracle:thin:@localhost:1521:XE|sa", "Generic MS SQL Server 2000|com.microsoft.jdbc.sqlserver.SQLServerDriver|jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=sqlexpress|sa", "Generic MS SQL Server 2005|com.microsoft.sqlserver.jdbc.SQLServerDriver|jdbc:sqlserver://localhost;DatabaseName=test|sa", "Generic PostgreSQL|org.postgresql.Driver|jdbc:postgresql:test|", "Generic MySQL|com.mysql.cj.jdbc.Driver|jdbc:mysql://localhost:3306/test|", "Generic MariaDB|org.mariadb.jdbc.Driver|jdbc:mariadb://localhost:3306/test|", "Generic HSQLDB|org.hsqldb.jdbcDriver|jdbc:hsqldb:test;hsqldb.default_table_type=cached|sa", "Generic Derby (Server)|org.apache.derby.client.ClientAutoloadedDriver|jdbc:derby://localhost:1527/test;create=true|sa", "Generic Derby (Embedded)|org.apache.derby.iapi.jdbc.AutoloadedDriver|jdbc:derby:test;create=true|sa", "Generic H2 (Server)|org.h2.Driver|jdbc:h2:tcp://localhost/~/test|sa", "Generic H2 (Embedded)|org.h2.Driver|jdbc:h2:~/test|sa"};
   private static int ticker;
   private static final long SESSION_TIMEOUT;
   private int port;
   private boolean allowOthers;
   private String externalNames;
   private boolean isDaemon;
   private final Set<WebThread> running = Collections.synchronizedSet(new HashSet());
   private boolean ssl;
   private byte[] adminPassword;
   private final HashMap<String, ConnectionInfo> connInfoMap = new HashMap();
   private long lastTimeoutCheck;
   private final HashMap<String, WebSession> sessions = new HashMap();
   private final HashSet<String> languages = new HashSet();
   private String startDateTime;
   private ServerSocket serverSocket;
   private String host;
   private String url;
   private ShutdownHandler shutdownHandler;
   private Thread listenerThread;
   private boolean ifExists = true;
   private String key;
   private boolean allowSecureCreation;
   private boolean trace;
   private TranslateThread translateThread;
   private boolean allowChunked = true;
   private String serverPropertiesDir = "~";
   private String commandHistoryString;

   byte[] getFile(String var1) throws IOException {
      this.trace("getFile <" + var1 + ">");
      byte[] var2 = Utils.getResource("/org/h2/server/web/res/" + var1);
      if (var2 == null) {
         this.trace(" null");
      } else {
         this.trace(" size=" + var2.length);
      }

      return var2;
   }

   synchronized void remove(WebThread var1) {
      this.running.remove(var1);
   }

   private static String generateSessionId() {
      byte[] var0 = MathUtils.secureRandomBytes(16);
      return StringUtils.convertBytesToHex(var0);
   }

   WebSession getSession(String var1) {
      long var2 = System.currentTimeMillis();
      if (this.lastTimeoutCheck + SESSION_TIMEOUT < var2) {
         Iterator var4 = (new ArrayList(this.sessions.keySet())).iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            WebSession var6 = (WebSession)this.sessions.get(var5);
            if (var6.lastAccess + SESSION_TIMEOUT < var2) {
               this.trace("timeout for " + var5);
               this.sessions.remove(var5);
            }
         }

         this.lastTimeoutCheck = var2;
      }

      WebSession var7 = (WebSession)this.sessions.get(var1);
      if (var7 != null) {
         var7.lastAccess = System.currentTimeMillis();
      }

      return var7;
   }

   WebSession createNewSession(String var1) {
      String var2;
      do {
         var2 = generateSessionId();
      } while(this.sessions.get(var2) != null);

      WebSession var3 = new WebSession(this);
      var3.lastAccess = System.currentTimeMillis();
      var3.put("sessionId", var2);
      var3.put("ip", var1);
      var3.put("language", "en");
      var3.put("frame-border", "0");
      var3.put("frameset-border", "4");
      this.sessions.put(var2, var3);
      this.readTranslations(var3, "en");
      return this.getSession(var2);
   }

   String getStartDateTime() {
      if (this.startDateTime == null) {
         this.startDateTime = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH).format(ZonedDateTime.now(ZoneId.of("UTC")));
      }

      return this.startDateTime;
   }

   String getKey() {
      return this.key;
   }

   public void setKey(String var1) {
      if (!this.allowOthers) {
         this.key = var1;
      }

   }

   public void setAllowSecureCreation(boolean var1) {
      if (!this.allowOthers) {
         this.allowSecureCreation = var1;
      }

   }

   public void init(String... var1) {
      for(int var2 = 0; var1 != null && var2 < var1.length; ++var2) {
         if ("-properties".equals(var1[var2])) {
            ++var2;
            this.serverPropertiesDir = var1[var2];
         }
      }

      Properties var7 = this.loadProperties();
      this.port = SortedProperties.getIntProperty(var7, "webPort", 8082);
      this.ssl = SortedProperties.getBooleanProperty(var7, "webSSL", false);
      this.allowOthers = SortedProperties.getBooleanProperty(var7, "webAllowOthers", false);
      this.setExternalNames(SortedProperties.getStringProperty(var7, "webExternalNames", (String)null));
      this.setAdminPassword(SortedProperties.getStringProperty(var7, "webAdminPassword", (String)null));
      this.commandHistoryString = var7.getProperty("commandHistory");

      for(int var3 = 0; var1 != null && var3 < var1.length; ++var3) {
         String var4 = var1[var3];
         if (Tool.isOption(var4, "-webPort")) {
            ++var3;
            this.port = Integer.decode(var1[var3]);
         } else if (Tool.isOption(var4, "-webSSL")) {
            this.ssl = true;
         } else if (Tool.isOption(var4, "-webAllowOthers")) {
            this.allowOthers = true;
         } else if (Tool.isOption(var4, "-webExternalNames")) {
            ++var3;
            this.setExternalNames(var1[var3]);
         } else if (Tool.isOption(var4, "-webDaemon")) {
            this.isDaemon = true;
         } else if (Tool.isOption(var4, "-baseDir")) {
            ++var3;
            String var5 = var1[var3];
            SysProperties.setBaseDir(var5);
         } else if (Tool.isOption(var4, "-ifExists")) {
            this.ifExists = true;
         } else if (Tool.isOption(var4, "-ifNotExists")) {
            this.ifExists = false;
         } else if (Tool.isOption(var4, "-webAdminPassword")) {
            ++var3;
            this.setAdminPassword(var1[var3]);
         } else if (Tool.isOption(var4, "-properties")) {
            ++var3;
         } else if (Tool.isOption(var4, "-trace")) {
            this.trace = true;
         }
      }

      String[][] var10 = LANGUAGES;
      int var8 = var10.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         String[] var6 = var10[var9];
         this.languages.add(var6[0]);
      }

      if (this.allowOthers) {
         this.key = null;
      }

      this.updateURL();
   }

   public String getURL() {
      this.updateURL();
      return this.url;
   }

   public String getHost() {
      if (this.host == null) {
         this.updateURL();
      }

      return this.host;
   }

   private void updateURL() {
      try {
         this.host = StringUtils.toLowerEnglish(NetUtils.getLocalAddress());
         StringBuilder var1 = (new StringBuilder(this.ssl ? "https" : "http")).append("://").append(this.host).append(':').append(this.port);
         if (this.key != null && this.serverSocket != null) {
            var1.append("?key=").append(this.key);
         }

         this.url = var1.toString();
      } catch (NoClassDefFoundError var2) {
      }

   }

   public void start() {
      this.serverSocket = NetUtils.createServerSocket(this.port, this.ssl);
      this.port = this.serverSocket.getLocalPort();
      this.updateURL();
   }

   public void listen() {
      this.listenerThread = Thread.currentThread();

      try {
         while(this.serverSocket != null) {
            Socket var1 = this.serverSocket.accept();
            WebThread var2 = new WebThread(var1, this);
            this.running.add(var2);
            var2.start();
         }
      } catch (Exception var3) {
         this.trace(var3.toString());
      }

   }

   public boolean isRunning(boolean var1) {
      if (this.serverSocket == null) {
         return false;
      } else {
         try {
            Socket var2 = NetUtils.createLoopbackSocket(this.port, this.ssl);
            var2.close();
            return true;
         } catch (Exception var3) {
            if (var1) {
               this.traceError(var3);
            }

            return false;
         }
      }
   }

   public boolean isStopped() {
      return this.serverSocket == null;
   }

   public void stop() {
      if (this.serverSocket != null) {
         try {
            this.serverSocket.close();
         } catch (IOException var6) {
            this.traceError(var6);
         }

         this.serverSocket = null;
      }

      if (this.listenerThread != null) {
         try {
            this.listenerThread.join(1000L);
         } catch (InterruptedException var5) {
            DbException.traceThrowable(var5);
         }
      }

      Iterator var1 = (new ArrayList(this.sessions.values())).iterator();

      while(var1.hasNext()) {
         WebSession var2 = (WebSession)var1.next();
         var2.close();
      }

      var1 = (new ArrayList(this.running)).iterator();

      while(var1.hasNext()) {
         WebThread var7 = (WebThread)var1.next();

         try {
            var7.stopNow();
            var7.join(100);
         } catch (Exception var4) {
            this.traceError(var4);
         }
      }

   }

   void trace(String var1) {
      if (this.trace) {
         System.out.println(var1);
      }

   }

   void traceError(Throwable var1) {
      if (this.trace) {
         var1.printStackTrace();
      }

   }

   boolean supportsLanguage(String var1) {
      return this.languages.contains(var1);
   }

   void readTranslations(WebSession var1, String var2) {
      Object var3 = new Properties();

      try {
         this.trace("translation: " + var2);
         byte[] var4 = this.getFile("_text_" + var2 + ".prop");
         this.trace("  " + new String(var4));
         var3 = SortedProperties.fromLines(new String(var4, StandardCharsets.UTF_8));
         Iterator var5 = ((Properties)var3).entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry var6 = (Map.Entry)var5.next();
            String var7 = (String)var6.getValue();
            if (var7.startsWith("#")) {
               var6.setValue(var7.substring(1));
            }
         }
      } catch (IOException var8) {
         DbException.traceThrowable(var8);
      }

      var1.put("text", new HashMap((Map)var3));
   }

   ArrayList<HashMap<String, Object>> getSessions() {
      ArrayList var1 = new ArrayList(this.sessions.size());
      Iterator var2 = this.sessions.values().iterator();

      while(var2.hasNext()) {
         WebSession var3 = (WebSession)var2.next();
         var1.add(var3.getInfo());
      }

      return var1;
   }

   public String getType() {
      return "Web Console";
   }

   public String getName() {
      return "H2 Console Server";
   }

   void setAllowOthers(boolean var1) {
      if (var1) {
         this.key = null;
      }

      this.allowOthers = var1;
   }

   public boolean getAllowOthers() {
      return this.allowOthers;
   }

   void setExternalNames(String var1) {
      this.externalNames = var1 != null ? StringUtils.toLowerEnglish(var1) : null;
   }

   String getExternalNames() {
      return this.externalNames;
   }

   void setSSL(boolean var1) {
      this.ssl = var1;
   }

   void setPort(int var1) {
      this.port = var1;
   }

   boolean getSSL() {
      return this.ssl;
   }

   public int getPort() {
      return this.port;
   }

   public boolean isCommandHistoryAllowed() {
      return this.commandHistoryString != null;
   }

   public void setCommandHistoryAllowed(boolean var1) {
      if (var1) {
         if (this.commandHistoryString == null) {
            this.commandHistoryString = "";
         }
      } else {
         this.commandHistoryString = null;
      }

   }

   public ArrayList<String> getCommandHistoryList() {
      ArrayList var1 = new ArrayList();
      if (this.commandHistoryString == null) {
         return var1;
      } else {
         StringBuilder var2 = new StringBuilder();
         int var3 = 0;

         while(true) {
            if (var3 != this.commandHistoryString.length() && this.commandHistoryString.charAt(var3) != ';') {
               if (this.commandHistoryString.charAt(var3) == '\\' && var3 < this.commandHistoryString.length() - 1) {
                  ++var3;
                  var2.append(this.commandHistoryString.charAt(var3));
               } else {
                  var2.append(this.commandHistoryString.charAt(var3));
               }
            } else {
               if (var2.length() > 0) {
                  var1.add(var2.toString());
                  var2.delete(0, var2.length());
               }

               if (var3 == this.commandHistoryString.length()) {
                  return var1;
               }
            }

            ++var3;
         }
      }
   }

   public void saveCommandHistoryList(ArrayList<String> var1) {
      StringBuilder var2 = new StringBuilder();

      String var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2.append(var4.replace("\\", "\\\\").replace(";", "\\;"))) {
         var4 = (String)var3.next();
         if (var2.length() > 0) {
            var2.append(';');
         }
      }

      this.commandHistoryString = var2.toString();
      this.saveProperties((Properties)null);
   }

   ConnectionInfo getSetting(String var1) {
      return (ConnectionInfo)this.connInfoMap.get(var1);
   }

   void updateSetting(ConnectionInfo var1) {
      this.connInfoMap.put(var1.name, var1);
      var1.lastAccess = ticker++;
   }

   void removeSetting(String var1) {
      this.connInfoMap.remove(var1);
   }

   private Properties loadProperties() {
      try {
         return (Properties)("null".equals(this.serverPropertiesDir) ? new Properties() : SortedProperties.loadProperties(this.serverPropertiesDir + "/" + ".h2.server.properties"));
      } catch (Exception var2) {
         DbException.traceThrowable(var2);
         return new Properties();
      }
   }

   String[] getSettingNames() {
      ArrayList var1 = this.getSettings();
      String[] var2 = new String[var1.size()];

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         var2[var3] = ((ConnectionInfo)var1.get(var3)).name;
      }

      return var2;
   }

   synchronized ArrayList<ConnectionInfo> getSettings() {
      ArrayList var1 = new ArrayList();
      if (this.connInfoMap.size() == 0) {
         Properties var2 = this.loadProperties();
         if (var2.size() == 0) {
            String[] var3 = GENERIC;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String var6 = var3[var5];
               ConnectionInfo var7 = new ConnectionInfo(var6);
               var1.add(var7);
               this.updateSetting(var7);
            }
         } else {
            int var8 = 0;

            while(true) {
               String var9 = var2.getProperty(Integer.toString(var8));
               if (var9 == null) {
                  break;
               }

               ConnectionInfo var10 = new ConnectionInfo(var9);
               var1.add(var10);
               this.updateSetting(var10);
               ++var8;
            }
         }
      } else {
         var1.addAll(this.connInfoMap.values());
      }

      Collections.sort(var1);
      return var1;
   }

   synchronized void saveProperties(Properties var1) {
      try {
         if (var1 == null) {
            Properties var2 = this.loadProperties();
            var1 = new SortedProperties();
            ((Properties)var1).setProperty("webPort", Integer.toString(SortedProperties.getIntProperty(var2, "webPort", this.port)));
            ((Properties)var1).setProperty("webAllowOthers", Boolean.toString(SortedProperties.getBooleanProperty(var2, "webAllowOthers", this.allowOthers)));
            if (this.externalNames != null) {
               ((Properties)var1).setProperty("webExternalNames", this.externalNames);
            }

            ((Properties)var1).setProperty("webSSL", Boolean.toString(SortedProperties.getBooleanProperty(var2, "webSSL", this.ssl)));
            if (this.adminPassword != null) {
               ((Properties)var1).setProperty("webAdminPassword", StringUtils.convertBytesToHex(this.adminPassword));
            }

            if (this.commandHistoryString != null) {
               ((Properties)var1).setProperty("commandHistory", this.commandHistoryString);
            }
         }

         ArrayList var7 = this.getSettings();
         int var3 = var7.size();

         for(int var4 = 0; var4 < var3; ++var4) {
            ConnectionInfo var5 = (ConnectionInfo)var7.get(var4);
            if (var5 != null) {
               ((Properties)var1).setProperty(Integer.toString(var3 - var4 - 1), var5.getString());
            }
         }

         if (!"null".equals(this.serverPropertiesDir)) {
            OutputStream var8 = FileUtils.newOutputStream(this.serverPropertiesDir + "/" + ".h2.server.properties", false);
            ((Properties)var1).store(var8, "H2 Server Properties");
            var8.close();
         }
      } catch (Exception var6) {
         DbException.traceThrowable(var6);
      }

   }

   Connection getConnection(String var1, String var2, String var3, String var4, String var5, NetworkConnectionInfo var6) throws SQLException {
      var1 = var1.trim();
      var2 = var2.trim();
      return JdbcUtils.getConnection(var1, var2, var3.trim(), var4, var6, this.ifExists && (!this.allowSecureCreation || this.key == null || !this.key.equals(var5)));
   }

   void shutdown() {
      if (this.shutdownHandler != null) {
         this.shutdownHandler.shutdown();
      }

   }

   public void setShutdownHandler(ShutdownHandler var1) {
      this.shutdownHandler = var1;
   }

   public String addSession(Connection var1) throws SQLException {
      WebSession var2 = this.createNewSession("local");
      var2.setShutdownServerOnDisconnect();
      var2.setConnection(var1);
      var2.put("url", var1.getMetaData().getURL());
      String var3 = (String)var2.get("sessionId");
      return this.url + "/frame.jsp?jsessionid=" + var3;
   }

   String startTranslate(Map<Object, Object> var1) {
      if (this.translateThread != null) {
         this.translateThread.stopNow();
      }

      this.translateThread = new TranslateThread(var1);
      this.translateThread.setDaemon(true);
      this.translateThread.start();
      return this.translateThread.getFileName();
   }

   public boolean isDaemon() {
      return this.isDaemon;
   }

   void setAllowChunked(boolean var1) {
      this.allowChunked = var1;
   }

   boolean getAllowChunked() {
      return this.allowChunked;
   }

   byte[] getAdminPassword() {
      return this.adminPassword;
   }

   void setAdminPassword(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         if (var1.length() == 128) {
            try {
               this.adminPassword = StringUtils.convertHexToBytes(var1);
               return;
            } catch (Exception var5) {
            }
         }

         byte[] var2 = MathUtils.secureRandomBytes(32);
         byte[] var3 = SHA256.getHashWithSalt(var1.getBytes(StandardCharsets.UTF_8), var2);
         byte[] var4 = Arrays.copyOf(var2, 64);
         System.arraycopy(var3, 0, var4, 32, 32);
         this.adminPassword = var4;
      } else {
         this.adminPassword = null;
      }
   }

   boolean checkAdminPassword(String var1) {
      if (this.adminPassword == null) {
         return false;
      } else {
         byte[] var2 = Arrays.copyOf(this.adminPassword, 32);
         byte[] var3 = new byte[32];
         System.arraycopy(this.adminPassword, 32, var3, 0, 32);
         return Utils.compareSecure(var3, SHA256.getHashWithSalt(var1.getBytes(StandardCharsets.UTF_8), var2));
      }
   }

   static {
      SESSION_TIMEOUT = (long)SysProperties.CONSOLE_TIMEOUT;
   }

   private class TranslateThread extends Thread {
      private final Path file = Paths.get("translation.properties");
      private final Map<Object, Object> translation;
      private volatile boolean stopNow;

      TranslateThread(Map<Object, Object> var2) {
         this.translation = var2;
      }

      public String getFileName() {
         return this.file.toAbsolutePath().toString();
      }

      public void stopNow() {
         this.stopNow = true;

         try {
            this.join();
         } catch (InterruptedException var2) {
         }

      }

      public void run() {
         while(!this.stopNow) {
            try {
               SortedProperties var1 = new SortedProperties();
               if (Files.exists(this.file, new LinkOption[0])) {
                  InputStream var2 = Files.newInputStream(this.file);
                  var1.load(var2);
                  this.translation.putAll(var1);
               } else {
                  OutputStream var4 = Files.newOutputStream(this.file);
                  var1.putAll(this.translation);
                  var1.store(var4, "Translation");
               }

               Thread.sleep(1000L);
            } catch (Exception var3) {
               WebServer.this.traceError(var3);
            }
         }

      }
   }
}
