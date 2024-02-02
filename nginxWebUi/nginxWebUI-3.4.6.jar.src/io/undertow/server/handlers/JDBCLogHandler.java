/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.Headers;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentLinkedDeque;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import javax.naming.InitialContext;
/*     */ import javax.naming.NamingException;
/*     */ import javax.sql.DataSource;
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
/*     */ public class JDBCLogHandler
/*     */   implements HttpHandler, Runnable
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final String formatString;
/*  53 */   private final ExchangeCompletionListener exchangeCompletionListener = new JDBCLogCompletionListener();
/*     */ 
/*     */ 
/*     */   
/*     */   private final Deque<JDBCLogAttribute> pendingMessages;
/*     */ 
/*     */   
/*  60 */   private volatile int state = 0;
/*     */ 
/*     */   
/*     */   private volatile Executor executor;
/*     */   
/*  65 */   private static final AtomicIntegerFieldUpdater<JDBCLogHandler> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(JDBCLogHandler.class, "state");
/*     */   
/*     */   protected boolean useLongContentLength = false;
/*     */   
/*     */   private final DataSource dataSource;
/*     */   
/*     */   private String tableName;
/*     */   private String remoteHostField;
/*     */   private String userField;
/*     */   private String timestampField;
/*     */   private String virtualHostField;
/*     */   private String methodField;
/*     */   private String queryField;
/*     */   private String statusField;
/*     */   private String bytesField;
/*     */   private String refererField;
/*     */   private String userAgentField;
/*     */   
/*     */   @Deprecated
/*     */   public JDBCLogHandler(HttpHandler next, Executor logWriteExecutor, String formatString, DataSource dataSource) {
/*  85 */     this(next, formatString, dataSource);
/*     */   }
/*     */   
/*     */   public JDBCLogHandler(HttpHandler next, String formatString, DataSource dataSource) {
/*  89 */     this.next = next;
/*  90 */     this.formatString = formatString;
/*  91 */     this.dataSource = dataSource;
/*     */     
/*  93 */     this.tableName = "access";
/*  94 */     this.remoteHostField = "remoteHost";
/*  95 */     this.userField = "userName";
/*  96 */     this.timestampField = "timestamp";
/*  97 */     this.virtualHostField = "virtualHost";
/*  98 */     this.methodField = "method";
/*  99 */     this.queryField = "query";
/* 100 */     this.statusField = "status";
/* 101 */     this.bytesField = "bytes";
/* 102 */     this.refererField = "referer";
/* 103 */     this.userAgentField = "userAgent";
/* 104 */     this.pendingMessages = new ConcurrentLinkedDeque<>();
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 109 */     exchange.addExchangeCompleteListener(this.exchangeCompletionListener);
/* 110 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private class JDBCLogCompletionListener implements ExchangeCompletionListener {
/*     */     private JDBCLogCompletionListener() {}
/*     */     
/*     */     public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*     */       try {
/* 118 */         JDBCLogHandler.this.logMessage(JDBCLogHandler.this.formatString, exchange);
/*     */       } finally {
/* 120 */         nextListener.proceed();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void logMessage(String pattern, HttpServerExchange exchange) {
/* 126 */     JDBCLogAttribute jdbcLogAttribute = new JDBCLogAttribute();
/*     */     
/* 128 */     if (pattern.equals("combined")) {
/* 129 */       jdbcLogAttribute.pattern = pattern;
/*     */     }
/* 131 */     jdbcLogAttribute.remoteHost = ((InetSocketAddress)exchange.getConnection().getPeerAddress()).getAddress().getHostAddress();
/* 132 */     SecurityContext sc = exchange.getSecurityContext();
/* 133 */     if (sc == null || !sc.isAuthenticated()) {
/* 134 */       jdbcLogAttribute.user = null;
/*     */     } else {
/* 136 */       jdbcLogAttribute.user = sc.getAuthenticatedAccount().getPrincipal().getName();
/*     */     } 
/* 138 */     jdbcLogAttribute.query = exchange.getQueryString();
/*     */     
/* 140 */     jdbcLogAttribute.bytes = exchange.getResponseContentLength();
/* 141 */     if (jdbcLogAttribute.bytes < 0L) {
/* 142 */       jdbcLogAttribute.bytes = 0L;
/*     */     }
/*     */     
/* 145 */     jdbcLogAttribute.status = exchange.getStatusCode();
/*     */     
/* 147 */     if (jdbcLogAttribute.pattern.equals("combined")) {
/* 148 */       jdbcLogAttribute.virtualHost = exchange.getRequestHeaders().getFirst(Headers.HOST);
/* 149 */       jdbcLogAttribute.method = exchange.getRequestMethod().toString();
/* 150 */       jdbcLogAttribute.referer = exchange.getRequestHeaders().getFirst(Headers.REFERER);
/* 151 */       jdbcLogAttribute.userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
/*     */     } 
/*     */     
/* 154 */     this.pendingMessages.add(jdbcLogAttribute);
/* 155 */     int state = stateUpdater.get(this);
/* 156 */     if (state == 0 && 
/* 157 */       stateUpdater.compareAndSet(this, 0, 1)) {
/* 158 */       this.executor = (Executor)exchange.getConnection().getWorker();
/* 159 */       this.executor.execute(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 169 */     if (!stateUpdater.compareAndSet(this, 1, 2)) {
/*     */       return;
/*     */     }
/*     */     
/* 173 */     List<JDBCLogAttribute> messages = new ArrayList<>();
/* 174 */     JDBCLogAttribute msg = null;
/*     */ 
/*     */     
/* 177 */     for (int i = 0; i < 1000; i++) {
/* 178 */       msg = this.pendingMessages.poll();
/* 179 */       if (msg == null) {
/*     */         break;
/*     */       }
/* 182 */       messages.add(msg);
/*     */     } 
/*     */     try {
/* 185 */       if (!messages.isEmpty()) {
/* 186 */         writeMessage(messages);
/*     */       }
/*     */     } finally {
/* 189 */       Executor executor = this.executor;
/* 190 */       stateUpdater.set(this, 0);
/*     */ 
/*     */       
/* 193 */       if (!this.pendingMessages.isEmpty() && 
/* 194 */         stateUpdater.compareAndSet(this, 0, 1)) {
/* 195 */         executor.execute(this);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeMessage(List<JDBCLogAttribute> messages) {
/* 202 */     PreparedStatement ps = null;
/* 203 */     Connection conn = null;
/*     */     try {
/* 205 */       conn = this.dataSource.getConnection();
/* 206 */       conn.setAutoCommit(true);
/* 207 */       ps = prepareStatement(conn);
/* 208 */       for (JDBCLogAttribute jdbcLogAttribute : messages) {
/* 209 */         int numberOfTries = 2;
/* 210 */         while (numberOfTries > 0) {
/*     */           try {
/* 212 */             ps.clearParameters();
/* 213 */             ps.setString(1, jdbcLogAttribute.remoteHost);
/* 214 */             ps.setString(2, jdbcLogAttribute.user);
/* 215 */             ps.setTimestamp(3, jdbcLogAttribute.timestamp);
/* 216 */             ps.setString(4, jdbcLogAttribute.query);
/* 217 */             ps.setInt(5, jdbcLogAttribute.status);
/* 218 */             if (this.useLongContentLength) {
/* 219 */               ps.setLong(6, jdbcLogAttribute.bytes);
/*     */             } else {
/* 221 */               if (jdbcLogAttribute.bytes > 2147483647L) {
/* 222 */                 jdbcLogAttribute.bytes = -1L;
/*     */               }
/* 224 */               ps.setInt(6, (int)jdbcLogAttribute.bytes);
/*     */             } 
/* 226 */             ps.setString(7, jdbcLogAttribute.virtualHost);
/* 227 */             ps.setString(8, jdbcLogAttribute.method);
/* 228 */             ps.setString(9, jdbcLogAttribute.referer);
/* 229 */             ps.setString(10, jdbcLogAttribute.userAgent);
/*     */             
/* 231 */             ps.executeUpdate();
/* 232 */             numberOfTries = 0;
/* 233 */           } catch (SQLException e) {
/* 234 */             UndertowLogger.ROOT_LOGGER.failedToWriteJdbcAccessLog(e);
/*     */           } 
/* 236 */           numberOfTries--;
/*     */         } 
/*     */       } 
/* 239 */       ps.close();
/* 240 */     } catch (SQLException e) {
/* 241 */       UndertowLogger.ROOT_LOGGER.errorWritingJDBCLog(e);
/*     */     } finally {
/* 243 */       if (ps != null) {
/*     */         try {
/* 245 */           ps.close();
/* 246 */         } catch (SQLException e) {
/* 247 */           UndertowLogger.ROOT_LOGGER.debug("Exception closing prepared statement", e);
/*     */         } 
/*     */       }
/* 250 */       if (conn != null) {
/*     */         try {
/* 252 */           conn.close();
/* 253 */         } catch (SQLException e) {
/* 254 */           UndertowLogger.ROOT_LOGGER.debug("Exception closing connection", e);
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
/*     */   void awaitWrittenForTest() throws InterruptedException {
/* 266 */     while (!this.pendingMessages.isEmpty()) {
/* 267 */       Thread.sleep(10L);
/*     */     }
/* 269 */     while (this.state != 0) {
/* 270 */       Thread.sleep(10L);
/*     */     }
/*     */   }
/*     */   
/*     */   private PreparedStatement prepareStatement(Connection conn) throws SQLException {
/* 275 */     return conn.prepareStatement("INSERT INTO " + this.tableName + " (" + this.remoteHostField + ", " + this.userField + ", " + this.timestampField + ", " + this.queryField + ", " + this.statusField + ", " + this.bytesField + ", " + this.virtualHostField + ", " + this.methodField + ", " + this.refererField + ", " + this.userAgentField + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class JDBCLogAttribute
/*     */   {
/*     */     private JDBCLogAttribute() {}
/*     */ 
/*     */ 
/*     */     
/* 286 */     protected String remoteHost = "";
/* 287 */     protected String user = "";
/* 288 */     protected String query = "";
/* 289 */     protected long bytes = 0L;
/* 290 */     protected int status = 0;
/* 291 */     protected String virtualHost = "";
/* 292 */     protected String method = "";
/* 293 */     protected String referer = "";
/* 294 */     protected String userAgent = "";
/* 295 */     protected String pattern = "common";
/* 296 */     protected Timestamp timestamp = new Timestamp(System.currentTimeMillis());
/*     */   }
/*     */   
/*     */   public boolean isUseLongContentLength() {
/* 300 */     return this.useLongContentLength;
/*     */   }
/*     */   
/*     */   public void setUseLongContentLength(boolean useLongContentLength) {
/* 304 */     this.useLongContentLength = useLongContentLength;
/*     */   }
/*     */   
/*     */   public String getTableName() {
/* 308 */     return this.tableName;
/*     */   }
/*     */   
/*     */   public void setTableName(String tableName) {
/* 312 */     this.tableName = tableName;
/*     */   }
/*     */   
/*     */   public String getRemoteHostField() {
/* 316 */     return this.remoteHostField;
/*     */   }
/*     */   
/*     */   public void setRemoteHostField(String remoteHostField) {
/* 320 */     this.remoteHostField = remoteHostField;
/*     */   }
/*     */   
/*     */   public String getUserField() {
/* 324 */     return this.userField;
/*     */   }
/*     */   
/*     */   public void setUserField(String userField) {
/* 328 */     this.userField = userField;
/*     */   }
/*     */   
/*     */   public String getTimestampField() {
/* 332 */     return this.timestampField;
/*     */   }
/*     */   
/*     */   public void setTimestampField(String timestampField) {
/* 336 */     this.timestampField = timestampField;
/*     */   }
/*     */   
/*     */   public String getVirtualHostField() {
/* 340 */     return this.virtualHostField;
/*     */   }
/*     */   
/*     */   public void setVirtualHostField(String virtualHostField) {
/* 344 */     this.virtualHostField = virtualHostField;
/*     */   }
/*     */   
/*     */   public String getMethodField() {
/* 348 */     return this.methodField;
/*     */   }
/*     */   
/*     */   public void setMethodField(String methodField) {
/* 352 */     this.methodField = methodField;
/*     */   }
/*     */   
/*     */   public String getQueryField() {
/* 356 */     return this.queryField;
/*     */   }
/*     */   
/*     */   public void setQueryField(String queryField) {
/* 360 */     this.queryField = queryField;
/*     */   }
/*     */   
/*     */   public String getStatusField() {
/* 364 */     return this.statusField;
/*     */   }
/*     */   
/*     */   public void setStatusField(String statusField) {
/* 368 */     this.statusField = statusField;
/*     */   }
/*     */   
/*     */   public String getBytesField() {
/* 372 */     return this.bytesField;
/*     */   }
/*     */   
/*     */   public void setBytesField(String bytesField) {
/* 376 */     this.bytesField = bytesField;
/*     */   }
/*     */   
/*     */   public String getRefererField() {
/* 380 */     return this.refererField;
/*     */   }
/*     */   
/*     */   public void setRefererField(String refererField) {
/* 384 */     this.refererField = refererField;
/*     */   }
/*     */   
/*     */   public String getUserAgentField() {
/* 388 */     return this.userAgentField;
/*     */   }
/*     */   
/*     */   public void setUserAgentField(String userAgentField) {
/* 392 */     this.userAgentField = userAgentField;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 397 */     return "JDBCLogHandler{formatString='" + this.formatString + '\'' + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 406 */       return "jdbc-access-log";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 411 */       Map<String, Class<?>> params = new HashMap<>();
/* 412 */       params.put("format", String.class);
/* 413 */       params.put("datasource", String.class);
/* 414 */       params.put("tableName", String.class);
/* 415 */       params.put("remoteHostField", String.class);
/* 416 */       params.put("userField", String.class);
/* 417 */       params.put("timestampField", String.class);
/* 418 */       params.put("virtualHostField", String.class);
/* 419 */       params.put("methodField", String.class);
/* 420 */       params.put("queryField", String.class);
/* 421 */       params.put("statusField", String.class);
/* 422 */       params.put("bytesField", String.class);
/* 423 */       params.put("refererField", String.class);
/* 424 */       params.put("userAgentField", String.class);
/* 425 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 430 */       return Collections.singleton("datasource");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 435 */       return "datasource";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 440 */       String datasourceName = (String)config.get("datasource");
/*     */       try {
/* 442 */         DataSource ds = (DataSource)(new InitialContext()).lookup((String)config.get("datasource"));
/* 443 */         String format = (String)config.get("format");
/* 444 */         return new JDBCLogHandler.Wrapper(format, ds, (String)config.get("tableName"), (String)config.get("remoteHostField"), (String)config.get("userField"), (String)config.get("timestampField"), (String)config.get("virtualHostField"), (String)config.get("methodField"), (String)config.get("queryField"), (String)config.get("statusField"), (String)config.get("bytesField"), (String)config.get("refererField"), (String)config.get("userAgentField"));
/* 445 */       } catch (NamingException ex) {
/* 446 */         throw UndertowMessages.MESSAGES.datasourceNotFound(datasourceName);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final DataSource datasource;
/*     */     private final String format;
/*     */     private final String tableName;
/*     */     private final String remoteHostField;
/*     */     private final String userField;
/*     */     private final String timestampField;
/*     */     private final String virtualHostField;
/*     */     private final String methodField;
/*     */     private final String queryField;
/*     */     private final String statusField;
/*     */     private final String bytesField;
/*     */     private final String refererField;
/*     */     private final String userAgentField;
/*     */     
/*     */     private Wrapper(String format, DataSource datasource, String tableName, String remoteHostField, String userField, String timestampField, String virtualHostField, String methodField, String queryField, String statusField, String bytesField, String refererField, String userAgentField) {
/* 470 */       this.datasource = datasource;
/* 471 */       this.tableName = tableName;
/* 472 */       this.remoteHostField = remoteHostField;
/* 473 */       this.userField = userField;
/* 474 */       this.timestampField = timestampField;
/* 475 */       this.virtualHostField = virtualHostField;
/* 476 */       this.methodField = methodField;
/* 477 */       this.queryField = queryField;
/* 478 */       this.statusField = statusField;
/* 479 */       this.bytesField = bytesField;
/* 480 */       this.refererField = refererField;
/* 481 */       this.userAgentField = userAgentField;
/* 482 */       this.format = "combined".equals(format) ? "combined" : "common";
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 487 */       JDBCLogHandler jdbc = new JDBCLogHandler(handler, this.format, this.datasource);
/* 488 */       if (this.tableName != null) {
/* 489 */         jdbc.setTableName(this.tableName);
/*     */       }
/* 491 */       if (this.remoteHostField != null) {
/* 492 */         jdbc.setRemoteHostField(this.remoteHostField);
/*     */       }
/* 494 */       if (this.userField != null) {
/* 495 */         jdbc.setUserField(this.userField);
/*     */       }
/* 497 */       if (this.timestampField != null) {
/* 498 */         jdbc.setTimestampField(this.timestampField);
/*     */       }
/* 500 */       if (this.virtualHostField != null) {
/* 501 */         jdbc.setVirtualHostField(this.virtualHostField);
/*     */       }
/* 503 */       if (this.methodField != null) {
/* 504 */         jdbc.setMethodField(this.methodField);
/*     */       }
/* 506 */       if (this.queryField != null) {
/* 507 */         jdbc.setQueryField(this.queryField);
/*     */       }
/* 509 */       if (this.statusField != null) {
/* 510 */         jdbc.setStatusField(this.statusField);
/*     */       }
/* 512 */       if (this.bytesField != null) {
/* 513 */         jdbc.setBytesField(this.bytesField);
/*     */       }
/* 515 */       if (this.refererField != null) {
/* 516 */         jdbc.setRefererField(this.refererField);
/*     */       }
/* 518 */       if (this.userAgentField != null) {
/* 519 */         jdbc.setUserAgentField(this.userAgentField);
/*     */       }
/*     */       
/* 522 */       return jdbc;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\JDBCLogHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */