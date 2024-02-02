package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.Headers;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JDBCLogHandler implements HttpHandler, Runnable {
   private final HttpHandler next;
   private final String formatString;
   private final ExchangeCompletionListener exchangeCompletionListener;
   private final Deque<JDBCLogAttribute> pendingMessages;
   private volatile int state;
   private volatile Executor executor;
   private static final AtomicIntegerFieldUpdater<JDBCLogHandler> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(JDBCLogHandler.class, "state");
   protected boolean useLongContentLength;
   private final DataSource dataSource;
   private String tableName;
   private String remoteHostField;
   private String userField;
   private String timestampField;
   private String virtualHostField;
   private String methodField;
   private String queryField;
   private String statusField;
   private String bytesField;
   private String refererField;
   private String userAgentField;

   /** @deprecated */
   @Deprecated
   public JDBCLogHandler(HttpHandler next, Executor logWriteExecutor, String formatString, DataSource dataSource) {
      this(next, formatString, dataSource);
   }

   public JDBCLogHandler(HttpHandler next, String formatString, DataSource dataSource) {
      this.exchangeCompletionListener = new JDBCLogCompletionListener();
      this.state = 0;
      this.useLongContentLength = false;
      this.next = next;
      this.formatString = formatString;
      this.dataSource = dataSource;
      this.tableName = "access";
      this.remoteHostField = "remoteHost";
      this.userField = "userName";
      this.timestampField = "timestamp";
      this.virtualHostField = "virtualHost";
      this.methodField = "method";
      this.queryField = "query";
      this.statusField = "status";
      this.bytesField = "bytes";
      this.refererField = "referer";
      this.userAgentField = "userAgent";
      this.pendingMessages = new ConcurrentLinkedDeque();
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.addExchangeCompleteListener(this.exchangeCompletionListener);
      this.next.handleRequest(exchange);
   }

   public void logMessage(String pattern, HttpServerExchange exchange) {
      JDBCLogAttribute jdbcLogAttribute = new JDBCLogAttribute();
      if (pattern.equals("combined")) {
         jdbcLogAttribute.pattern = pattern;
      }

      jdbcLogAttribute.remoteHost = ((InetSocketAddress)exchange.getConnection().getPeerAddress()).getAddress().getHostAddress();
      SecurityContext sc = exchange.getSecurityContext();
      if (sc != null && sc.isAuthenticated()) {
         jdbcLogAttribute.user = sc.getAuthenticatedAccount().getPrincipal().getName();
      } else {
         jdbcLogAttribute.user = null;
      }

      jdbcLogAttribute.query = exchange.getQueryString();
      jdbcLogAttribute.bytes = exchange.getResponseContentLength();
      if (jdbcLogAttribute.bytes < 0L) {
         jdbcLogAttribute.bytes = 0L;
      }

      jdbcLogAttribute.status = exchange.getStatusCode();
      if (jdbcLogAttribute.pattern.equals("combined")) {
         jdbcLogAttribute.virtualHost = exchange.getRequestHeaders().getFirst(Headers.HOST);
         jdbcLogAttribute.method = exchange.getRequestMethod().toString();
         jdbcLogAttribute.referer = exchange.getRequestHeaders().getFirst(Headers.REFERER);
         jdbcLogAttribute.userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
      }

      this.pendingMessages.add(jdbcLogAttribute);
      int state = stateUpdater.get(this);
      if (state == 0 && stateUpdater.compareAndSet(this, 0, 1)) {
         this.executor = exchange.getConnection().getWorker();
         this.executor.execute(this);
      }

   }

   public void run() {
      if (stateUpdater.compareAndSet(this, 1, 2)) {
         List<JDBCLogAttribute> messages = new ArrayList();
         JDBCLogAttribute msg = null;

         for(int i = 0; i < 1000; ++i) {
            msg = (JDBCLogAttribute)this.pendingMessages.poll();
            if (msg == null) {
               break;
            }

            messages.add(msg);
         }

         boolean var7 = false;

         try {
            var7 = true;
            if (!messages.isEmpty()) {
               this.writeMessage(messages);
               var7 = false;
            } else {
               var7 = false;
            }
         } finally {
            if (var7) {
               Executor executor = this.executor;
               stateUpdater.set(this, 0);
               if (!this.pendingMessages.isEmpty() && stateUpdater.compareAndSet(this, 0, 1)) {
                  executor.execute(this);
               }

            }
         }

         Executor executor = this.executor;
         stateUpdater.set(this, 0);
         if (!this.pendingMessages.isEmpty() && stateUpdater.compareAndSet(this, 0, 1)) {
            executor.execute(this);
         }

      }
   }

   private void writeMessage(List<JDBCLogAttribute> messages) {
      PreparedStatement ps = null;
      Connection conn = null;

      try {
         conn = this.dataSource.getConnection();
         conn.setAutoCommit(true);
         ps = this.prepareStatement(conn);
         Iterator var4 = messages.iterator();

         while(var4.hasNext()) {
            JDBCLogAttribute jdbcLogAttribute = (JDBCLogAttribute)var4.next();

            for(int numberOfTries = 2; numberOfTries > 0; --numberOfTries) {
               try {
                  ps.clearParameters();
                  ps.setString(1, jdbcLogAttribute.remoteHost);
                  ps.setString(2, jdbcLogAttribute.user);
                  ps.setTimestamp(3, jdbcLogAttribute.timestamp);
                  ps.setString(4, jdbcLogAttribute.query);
                  ps.setInt(5, jdbcLogAttribute.status);
                  if (this.useLongContentLength) {
                     ps.setLong(6, jdbcLogAttribute.bytes);
                  } else {
                     if (jdbcLogAttribute.bytes > 2147483647L) {
                        jdbcLogAttribute.bytes = -1L;
                     }

                     ps.setInt(6, (int)jdbcLogAttribute.bytes);
                  }

                  ps.setString(7, jdbcLogAttribute.virtualHost);
                  ps.setString(8, jdbcLogAttribute.method);
                  ps.setString(9, jdbcLogAttribute.referer);
                  ps.setString(10, jdbcLogAttribute.userAgent);
                  ps.executeUpdate();
                  numberOfTries = 0;
               } catch (SQLException var21) {
                  UndertowLogger.ROOT_LOGGER.failedToWriteJdbcAccessLog(var21);
               }
            }
         }

         ps.close();
      } catch (SQLException var22) {
         UndertowLogger.ROOT_LOGGER.errorWritingJDBCLog(var22);
      } finally {
         if (ps != null) {
            try {
               ps.close();
            } catch (SQLException var20) {
               UndertowLogger.ROOT_LOGGER.debug("Exception closing prepared statement", var20);
            }
         }

         if (conn != null) {
            try {
               conn.close();
            } catch (SQLException var19) {
               UndertowLogger.ROOT_LOGGER.debug("Exception closing connection", var19);
            }
         }

      }

   }

   void awaitWrittenForTest() throws InterruptedException {
      while(!this.pendingMessages.isEmpty()) {
         Thread.sleep(10L);
      }

      while(this.state != 0) {
         Thread.sleep(10L);
      }

   }

   private PreparedStatement prepareStatement(Connection conn) throws SQLException {
      return conn.prepareStatement("INSERT INTO " + this.tableName + " (" + this.remoteHostField + ", " + this.userField + ", " + this.timestampField + ", " + this.queryField + ", " + this.statusField + ", " + this.bytesField + ", " + this.virtualHostField + ", " + this.methodField + ", " + this.refererField + ", " + this.userAgentField + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
   }

   public boolean isUseLongContentLength() {
      return this.useLongContentLength;
   }

   public void setUseLongContentLength(boolean useLongContentLength) {
      this.useLongContentLength = useLongContentLength;
   }

   public String getTableName() {
      return this.tableName;
   }

   public void setTableName(String tableName) {
      this.tableName = tableName;
   }

   public String getRemoteHostField() {
      return this.remoteHostField;
   }

   public void setRemoteHostField(String remoteHostField) {
      this.remoteHostField = remoteHostField;
   }

   public String getUserField() {
      return this.userField;
   }

   public void setUserField(String userField) {
      this.userField = userField;
   }

   public String getTimestampField() {
      return this.timestampField;
   }

   public void setTimestampField(String timestampField) {
      this.timestampField = timestampField;
   }

   public String getVirtualHostField() {
      return this.virtualHostField;
   }

   public void setVirtualHostField(String virtualHostField) {
      this.virtualHostField = virtualHostField;
   }

   public String getMethodField() {
      return this.methodField;
   }

   public void setMethodField(String methodField) {
      this.methodField = methodField;
   }

   public String getQueryField() {
      return this.queryField;
   }

   public void setQueryField(String queryField) {
      this.queryField = queryField;
   }

   public String getStatusField() {
      return this.statusField;
   }

   public void setStatusField(String statusField) {
      this.statusField = statusField;
   }

   public String getBytesField() {
      return this.bytesField;
   }

   public void setBytesField(String bytesField) {
      this.bytesField = bytesField;
   }

   public String getRefererField() {
      return this.refererField;
   }

   public void setRefererField(String refererField) {
      this.refererField = refererField;
   }

   public String getUserAgentField() {
      return this.userAgentField;
   }

   public void setUserAgentField(String userAgentField) {
      this.userAgentField = userAgentField;
   }

   public String toString() {
      return "JDBCLogHandler{formatString='" + this.formatString + '\'' + '}';
   }

   private static class Wrapper implements HandlerWrapper {
      private final DataSource datasource;
      private final String format;
      private final String tableName;
      private final String remoteHostField;
      private final String userField;
      private final String timestampField;
      private final String virtualHostField;
      private final String methodField;
      private final String queryField;
      private final String statusField;
      private final String bytesField;
      private final String refererField;
      private final String userAgentField;

      private Wrapper(String format, DataSource datasource, String tableName, String remoteHostField, String userField, String timestampField, String virtualHostField, String methodField, String queryField, String statusField, String bytesField, String refererField, String userAgentField) {
         this.datasource = datasource;
         this.tableName = tableName;
         this.remoteHostField = remoteHostField;
         this.userField = userField;
         this.timestampField = timestampField;
         this.virtualHostField = virtualHostField;
         this.methodField = methodField;
         this.queryField = queryField;
         this.statusField = statusField;
         this.bytesField = bytesField;
         this.refererField = refererField;
         this.userAgentField = userAgentField;
         this.format = "combined".equals(format) ? "combined" : "common";
      }

      public HttpHandler wrap(HttpHandler handler) {
         JDBCLogHandler jdbc = new JDBCLogHandler(handler, this.format, this.datasource);
         if (this.tableName != null) {
            jdbc.setTableName(this.tableName);
         }

         if (this.remoteHostField != null) {
            jdbc.setRemoteHostField(this.remoteHostField);
         }

         if (this.userField != null) {
            jdbc.setUserField(this.userField);
         }

         if (this.timestampField != null) {
            jdbc.setTimestampField(this.timestampField);
         }

         if (this.virtualHostField != null) {
            jdbc.setVirtualHostField(this.virtualHostField);
         }

         if (this.methodField != null) {
            jdbc.setMethodField(this.methodField);
         }

         if (this.queryField != null) {
            jdbc.setQueryField(this.queryField);
         }

         if (this.statusField != null) {
            jdbc.setStatusField(this.statusField);
         }

         if (this.bytesField != null) {
            jdbc.setBytesField(this.bytesField);
         }

         if (this.refererField != null) {
            jdbc.setRefererField(this.refererField);
         }

         if (this.userAgentField != null) {
            jdbc.setUserAgentField(this.userAgentField);
         }

         return jdbc;
      }

      // $FF: synthetic method
      Wrapper(String x0, DataSource x1, String x2, String x3, String x4, String x5, String x6, String x7, String x8, String x9, String x10, String x11, String x12, Object x13) {
         this(x0, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "jdbc-access-log";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("format", String.class);
         params.put("datasource", String.class);
         params.put("tableName", String.class);
         params.put("remoteHostField", String.class);
         params.put("userField", String.class);
         params.put("timestampField", String.class);
         params.put("virtualHostField", String.class);
         params.put("methodField", String.class);
         params.put("queryField", String.class);
         params.put("statusField", String.class);
         params.put("bytesField", String.class);
         params.put("refererField", String.class);
         params.put("userAgentField", String.class);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("datasource");
      }

      public String defaultParameter() {
         return "datasource";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         String datasourceName = (String)config.get("datasource");

         try {
            DataSource ds = (DataSource)(new InitialContext()).lookup((String)config.get("datasource"));
            String format = (String)config.get("format");
            return new Wrapper(format, ds, (String)config.get("tableName"), (String)config.get("remoteHostField"), (String)config.get("userField"), (String)config.get("timestampField"), (String)config.get("virtualHostField"), (String)config.get("methodField"), (String)config.get("queryField"), (String)config.get("statusField"), (String)config.get("bytesField"), (String)config.get("refererField"), (String)config.get("userAgentField"));
         } catch (NamingException var5) {
            throw UndertowMessages.MESSAGES.datasourceNotFound(datasourceName);
         }
      }
   }

   private static class JDBCLogAttribute {
      protected String remoteHost;
      protected String user;
      protected String query;
      protected long bytes;
      protected int status;
      protected String virtualHost;
      protected String method;
      protected String referer;
      protected String userAgent;
      protected String pattern;
      protected Timestamp timestamp;

      private JDBCLogAttribute() {
         this.remoteHost = "";
         this.user = "";
         this.query = "";
         this.bytes = 0L;
         this.status = 0;
         this.virtualHost = "";
         this.method = "";
         this.referer = "";
         this.userAgent = "";
         this.pattern = "common";
         this.timestamp = new Timestamp(System.currentTimeMillis());
      }

      // $FF: synthetic method
      JDBCLogAttribute(Object x0) {
         this();
      }
   }

   private class JDBCLogCompletionListener implements ExchangeCompletionListener {
      private JDBCLogCompletionListener() {
      }

      public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
         try {
            JDBCLogHandler.this.logMessage(JDBCLogHandler.this.formatString, exchange);
         } finally {
            nextListener.proceed();
         }

      }

      // $FF: synthetic method
      JDBCLogCompletionListener(Object x1) {
         this();
      }
   }
}
