/*     */ package org.h2.util;
/*     */ 
/*     */ import java.sql.Driver;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ import javax.sql.DataSource;
/*     */ import javax.sql.XADataSource;
/*     */ import org.h2.Driver;
/*     */ import org.h2.engine.Constants;
/*     */ import org.h2.jdbcx.JdbcDataSource;
/*     */ import org.osgi.framework.BundleContext;
/*     */ import org.osgi.service.jdbc.DataSourceFactory;
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
/*     */ public class OsgiDataSourceFactory
/*     */   implements DataSourceFactory
/*     */ {
/*     */   private final Driver driver;
/*     */   
/*     */   public OsgiDataSourceFactory(Driver paramDriver) {
/*  43 */     this.driver = paramDriver;
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
/*     */   public DataSource createDataSource(Properties paramProperties) throws SQLException {
/*  58 */     Properties properties = new Properties();
/*  59 */     if (paramProperties != null) {
/*  60 */       properties.putAll(paramProperties);
/*     */     }
/*     */ 
/*     */     
/*  64 */     rejectUnsupportedOptions(properties);
/*     */ 
/*     */     
/*  67 */     rejectPoolingOptions(properties);
/*     */     
/*  69 */     JdbcDataSource jdbcDataSource = new JdbcDataSource();
/*     */     
/*  71 */     setupH2DataSource(jdbcDataSource, properties);
/*     */     
/*  73 */     return (DataSource)jdbcDataSource;
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
/*     */   public ConnectionPoolDataSource createConnectionPoolDataSource(Properties paramProperties) throws SQLException {
/*  88 */     Properties properties = new Properties();
/*  89 */     if (paramProperties != null) {
/*  90 */       properties.putAll(paramProperties);
/*     */     }
/*     */ 
/*     */     
/*  94 */     rejectUnsupportedOptions(properties);
/*     */ 
/*     */     
/*  97 */     rejectPoolingOptions(properties);
/*     */     
/*  99 */     JdbcDataSource jdbcDataSource = new JdbcDataSource();
/*     */     
/* 101 */     setupH2DataSource(jdbcDataSource, properties);
/*     */     
/* 103 */     return (ConnectionPoolDataSource)jdbcDataSource;
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
/*     */   public XADataSource createXADataSource(Properties paramProperties) throws SQLException {
/* 118 */     Properties properties = new Properties();
/* 119 */     if (paramProperties != null) {
/* 120 */       properties.putAll(paramProperties);
/*     */     }
/*     */ 
/*     */     
/* 124 */     rejectUnsupportedOptions(properties);
/*     */ 
/*     */     
/* 127 */     rejectPoolingOptions(properties);
/*     */     
/* 129 */     JdbcDataSource jdbcDataSource = new JdbcDataSource();
/*     */     
/* 131 */     setupH2DataSource(jdbcDataSource, properties);
/*     */     
/* 133 */     return (XADataSource)jdbcDataSource;
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
/*     */   public Driver createDriver(Properties paramProperties) throws SQLException {
/* 146 */     if (paramProperties != null && !paramProperties.isEmpty())
/*     */     {
/* 148 */       throw new SQLException();
/*     */     }
/* 150 */     return (Driver)this.driver;
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
/*     */   private static void rejectUnsupportedOptions(Properties paramProperties) throws SQLFeatureNotSupportedException {
/* 164 */     if (paramProperties.containsKey("roleName")) {
/* 165 */       throw new SQLFeatureNotSupportedException("The roleName property is not supported by H2");
/*     */     }
/*     */ 
/*     */     
/* 169 */     if (paramProperties.containsKey("dataSourceName")) {
/* 170 */       throw new SQLFeatureNotSupportedException("The dataSourceName property is not supported by H2");
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
/*     */   private static void setupH2DataSource(JdbcDataSource paramJdbcDataSource, Properties paramProperties) {
/* 186 */     if (paramProperties.containsKey("user")) {
/* 187 */       paramJdbcDataSource.setUser((String)paramProperties.remove("user"));
/*     */     }
/* 189 */     if (paramProperties.containsKey("password")) {
/* 190 */       paramJdbcDataSource.setPassword((String)paramProperties
/* 191 */           .remove("password"));
/*     */     }
/*     */ 
/*     */     
/* 195 */     if (paramProperties.containsKey("description")) {
/* 196 */       paramJdbcDataSource.setDescription((String)paramProperties
/* 197 */           .remove("description"));
/*     */     }
/*     */ 
/*     */     
/* 201 */     StringBuilder stringBuilder = new StringBuilder();
/* 202 */     if (paramProperties.containsKey("url")) {
/*     */       
/* 204 */       stringBuilder.append(paramProperties.remove("url"));
/*     */       
/* 206 */       paramProperties.remove("networkProtocol");
/* 207 */       paramProperties.remove("serverName");
/* 208 */       paramProperties.remove("portNumber");
/* 209 */       paramProperties.remove("databaseName");
/*     */     } else {
/*     */       
/* 212 */       stringBuilder.append("jdbc:h2:");
/*     */ 
/*     */       
/* 215 */       String str = "";
/* 216 */       if (paramProperties.containsKey("networkProtocol")) {
/* 217 */         str = (String)paramProperties.remove("networkProtocol");
/* 218 */         stringBuilder.append(str).append(":");
/*     */       } 
/*     */ 
/*     */       
/* 222 */       if (paramProperties.containsKey("serverName")) {
/* 223 */         stringBuilder.append("//").append(paramProperties
/* 224 */             .remove("serverName"));
/*     */         
/* 226 */         if (paramProperties.containsKey("portNumber")) {
/* 227 */           stringBuilder.append(":").append(paramProperties
/* 228 */               .remove("portNumber"));
/*     */         }
/*     */         
/* 231 */         stringBuilder.append("/");
/* 232 */       } else if (paramProperties.containsKey("portNumber")) {
/*     */ 
/*     */         
/* 235 */         stringBuilder
/* 236 */           .append("//localhost:")
/* 237 */           .append(paramProperties.remove("portNumber"))
/* 238 */           .append("/");
/* 239 */       } else if (str.equals("tcp") || str.equals("ssl")) {
/*     */ 
/*     */         
/* 242 */         stringBuilder.append("//localhost/");
/*     */       } 
/*     */ 
/*     */       
/* 246 */       if (paramProperties.containsKey("databaseName")) {
/* 247 */         stringBuilder.append(paramProperties
/* 248 */             .remove("databaseName"));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 253 */     for (Object object : paramProperties.keySet()) {
/* 254 */       stringBuilder.append(";").append(object).append("=")
/* 255 */         .append(paramProperties.get(object));
/*     */     }
/*     */     
/* 258 */     if (stringBuilder.length() > "jdbc:h2:".length()) {
/* 259 */       paramJdbcDataSource.setURL(stringBuilder.toString());
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
/*     */   private static void rejectPoolingOptions(Properties paramProperties) throws SQLFeatureNotSupportedException {
/* 273 */     if (paramProperties.containsKey("initialPoolSize") || paramProperties
/* 274 */       .containsKey("maxIdleTime") || paramProperties
/* 275 */       .containsKey("maxPoolSize") || paramProperties
/* 276 */       .containsKey("maxStatements") || paramProperties
/* 277 */       .containsKey("minPoolSize") || paramProperties
/* 278 */       .containsKey("propertyCycle")) {
/* 279 */       throw new SQLFeatureNotSupportedException("Pooling properties are not supported by H2");
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
/*     */   static void registerService(BundleContext paramBundleContext, Driver paramDriver) {
/* 292 */     Hashtable<Object, Object> hashtable = new Hashtable<>();
/* 293 */     hashtable.put("osgi.jdbc.driver.class", Driver.class
/*     */         
/* 295 */         .getName());
/* 296 */     hashtable.put("osgi.jdbc.driver.name", "H2 JDBC Driver");
/*     */ 
/*     */     
/* 299 */     hashtable.put("osgi.jdbc.driver.version", Constants.FULL_VERSION);
/*     */ 
/*     */     
/* 302 */     paramBundleContext.registerService(DataSourceFactory.class
/* 303 */         .getName(), new OsgiDataSourceFactory(paramDriver), hashtable);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\OsgiDataSourceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */