/*     */ package org.h2.jdbcx;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.logging.Logger;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.Referenceable;
/*     */ import javax.naming.StringRefAddr;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ import javax.sql.DataSource;
/*     */ import javax.sql.PooledConnection;
/*     */ import javax.sql.XAConnection;
/*     */ import javax.sql.XADataSource;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.TraceObject;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public final class JdbcDataSource
/*     */   extends TraceObject
/*     */   implements XADataSource, DataSource, ConnectionPoolDataSource, Serializable, Referenceable, JdbcDataSourceBackwardsCompat
/*     */ {
/*     */   private static final long serialVersionUID = 1288136338451857771L;
/*     */   private transient JdbcDataSourceFactory factory;
/*     */   private transient PrintWriter logWriter;
/*     */   private int loginTimeout;
/*  69 */   private String userName = "";
/*  70 */   private char[] passwordChars = new char[0];
/*  71 */   private String url = "";
/*     */ 
/*     */   
/*     */   private String description;
/*     */ 
/*     */   
/*     */   public JdbcDataSource() {
/*  78 */     initFactory();
/*  79 */     int i = getNextId(12);
/*  80 */     setTrace(this.factory.getTrace(), 12, i);
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
/*     */   private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
/*  92 */     initFactory();
/*  93 */     paramObjectInputStream.defaultReadObject();
/*     */   }
/*     */   
/*     */   private void initFactory() {
/*  97 */     this.factory = new JdbcDataSourceFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLoginTimeout() {
/* 107 */     debugCodeCall("getLoginTimeout");
/* 108 */     return this.loginTimeout;
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
/*     */   public void setLoginTimeout(int paramInt) {
/* 120 */     debugCodeCall("setLoginTimeout", paramInt);
/* 121 */     this.loginTimeout = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrintWriter getLogWriter() {
/* 131 */     debugCodeCall("getLogWriter");
/* 132 */     return this.logWriter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLogWriter(PrintWriter paramPrintWriter) {
/* 143 */     debugCodeCall("setLogWriter(out)");
/* 144 */     this.logWriter = paramPrintWriter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 154 */     debugCodeCall("getConnection");
/* 155 */     return (Connection)new JdbcConnection(this.url, null, this.userName, StringUtils.cloneCharArray(this.passwordChars), false);
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
/*     */   public Connection getConnection(String paramString1, String paramString2) throws SQLException {
/* 169 */     if (isDebugEnabled()) {
/* 170 */       debugCode("getConnection(" + quote(paramString1) + ", \"\")");
/*     */     }
/* 172 */     return (Connection)new JdbcConnection(this.url, null, paramString1, paramString2, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getURL() {
/* 181 */     debugCodeCall("getURL");
/* 182 */     return this.url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setURL(String paramString) {
/* 191 */     debugCodeCall("setURL", paramString);
/* 192 */     this.url = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUrl() {
/* 203 */     debugCodeCall("getUrl");
/* 204 */     return this.url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUrl(String paramString) {
/* 215 */     debugCodeCall("setUrl", paramString);
/* 216 */     this.url = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPassword(String paramString) {
/* 225 */     debugCodeCall("setPassword", "");
/* 226 */     this.passwordChars = (paramString == null) ? null : paramString.toCharArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPasswordChars(char[] paramArrayOfchar) {
/* 235 */     if (isDebugEnabled()) {
/* 236 */       debugCode("setPasswordChars(new char[0])");
/*     */     }
/* 238 */     this.passwordChars = paramArrayOfchar;
/*     */   }
/*     */   
/*     */   private static String convertToString(char[] paramArrayOfchar) {
/* 242 */     return (paramArrayOfchar == null) ? null : new String(paramArrayOfchar);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPassword() {
/* 251 */     debugCodeCall("getPassword");
/* 252 */     return convertToString(this.passwordChars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUser() {
/* 261 */     debugCodeCall("getUser");
/* 262 */     return this.userName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUser(String paramString) {
/* 271 */     debugCodeCall("setUser", paramString);
/* 272 */     this.userName = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDescription() {
/* 281 */     debugCodeCall("getDescription");
/* 282 */     return this.description;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDescription(String paramString) {
/* 291 */     debugCodeCall("getDescription", paramString);
/* 292 */     this.description = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reference getReference() {
/* 302 */     debugCodeCall("getReference");
/* 303 */     String str = JdbcDataSourceFactory.class.getName();
/* 304 */     Reference reference = new Reference(getClass().getName(), str, null);
/* 305 */     reference.add(new StringRefAddr("url", this.url));
/* 306 */     reference.add(new StringRefAddr("user", this.userName));
/* 307 */     reference.add(new StringRefAddr("password", convertToString(this.passwordChars)));
/* 308 */     reference.add(new StringRefAddr("loginTimeout", Integer.toString(this.loginTimeout)));
/* 309 */     reference.add(new StringRefAddr("description", this.description));
/* 310 */     return reference;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XAConnection getXAConnection() throws SQLException {
/* 320 */     debugCodeCall("getXAConnection");
/* 321 */     return new JdbcXAConnection(this.factory, getNextId(13), new JdbcConnection(this.url, null, this.userName, 
/* 322 */           StringUtils.cloneCharArray(this.passwordChars), false));
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
/*     */   public XAConnection getXAConnection(String paramString1, String paramString2) throws SQLException {
/* 336 */     if (isDebugEnabled()) {
/* 337 */       debugCode("getXAConnection(" + quote(paramString1) + ", \"\")");
/*     */     }
/* 339 */     return new JdbcXAConnection(this.factory, getNextId(13), new JdbcConnection(this.url, null, paramString1, paramString2, false));
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
/*     */   public PooledConnection getPooledConnection() throws SQLException {
/* 351 */     debugCodeCall("getPooledConnection");
/* 352 */     return getXAConnection();
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
/*     */   public PooledConnection getPooledConnection(String paramString1, String paramString2) throws SQLException {
/* 366 */     if (isDebugEnabled()) {
/* 367 */       debugCode("getPooledConnection(" + quote(paramString1) + ", \"\")");
/*     */     }
/* 369 */     return getXAConnection(paramString1, paramString2);
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
/*     */   public <T> T unwrap(Class<T> paramClass) throws SQLException {
/*     */     try {
/* 382 */       if (isWrapperFor(paramClass)) {
/* 383 */         return (T)this;
/*     */       }
/* 385 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 386 */     } catch (Exception exception) {
/* 387 */       throw logAndConvert(exception);
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
/*     */   public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
/* 399 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() {
/* 407 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 415 */     return getTraceObjectName() + ": url=" + this.url + " user=" + this.userName;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbcx\JdbcDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */