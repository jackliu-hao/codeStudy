/*     */ package com.mysql.cj.conf;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
/*     */ import com.mysql.cj.exceptions.UnsupportedConnectionStringException;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.util.DnsSrv;
/*     */ import com.mysql.cj.util.LRUCache;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.TreeMap;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import java.util.stream.Collectors;
/*     */ import javax.naming.NamingException;
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
/*     */ public abstract class ConnectionUrl
/*     */   implements DatabaseUrlContainer
/*     */ {
/*     */   public static final String DEFAULT_HOST = "localhost";
/*     */   public static final int DEFAULT_PORT = 3306;
/*  71 */   private static final LRUCache<String, ConnectionUrl> connectionUrlCache = new LRUCache(100);
/*  72 */   private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
/*     */   protected Type type;
/*     */   protected String originalConnStr;
/*     */   protected String originalDatabase;
/*     */   
/*     */   public enum HostsCardinality {
/*  78 */     SINGLE
/*     */     {
/*     */       public boolean assertSize(int n) {
/*  81 */         return (n == 1);
/*     */       }
/*     */     },
/*  84 */     MULTIPLE
/*     */     {
/*     */       public boolean assertSize(int n) {
/*  87 */         return (n > 1);
/*     */       }
/*     */     },
/*  90 */     ONE_OR_MORE
/*     */     {
/*     */       public boolean assertSize(int n) {
/*  93 */         return (n >= 1);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */     
/*     */     public abstract boolean assertSize(int param1Int);
/*     */   }
/*     */ 
/*     */   
/*     */   public enum Type
/*     */   {
/* 105 */     FAILOVER_DNS_SRV_CONNECTION("jdbc:mysql+srv:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.FailoverDnsSrvConnectionUrl"),
/* 106 */     LOADBALANCE_DNS_SRV_CONNECTION("jdbc:mysql+srv:loadbalance:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.LoadBalanceDnsSrvConnectionUrl"),
/* 107 */     REPLICATION_DNS_SRV_CONNECTION("jdbc:mysql+srv:replication:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.ReplicationDnsSrvConnectionUrl"),
/* 108 */     XDEVAPI_DNS_SRV_SESSION("mysqlx+srv:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.XDevApiDnsSrvConnectionUrl"),
/*     */     
/* 110 */     SINGLE_CONNECTION("jdbc:mysql:", ConnectionUrl.HostsCardinality.SINGLE, "com.mysql.cj.conf.url.SingleConnectionUrl", (ConnectionUrl.HostsCardinality)PropertyKey.dnsSrv, (String)FAILOVER_DNS_SRV_CONNECTION),
/* 111 */     FAILOVER_CONNECTION("jdbc:mysql:", ConnectionUrl.HostsCardinality.MULTIPLE, "com.mysql.cj.conf.url.FailoverConnectionUrl", (ConnectionUrl.HostsCardinality)PropertyKey.dnsSrv, (String)FAILOVER_DNS_SRV_CONNECTION),
/*     */     
/* 113 */     LOADBALANCE_CONNECTION("jdbc:mysql:loadbalance:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.LoadBalanceConnectionUrl", (ConnectionUrl.HostsCardinality)PropertyKey.dnsSrv, (String)LOADBALANCE_DNS_SRV_CONNECTION),
/*     */     
/* 115 */     REPLICATION_CONNECTION("jdbc:mysql:replication:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.ReplicationConnectionUrl", (ConnectionUrl.HostsCardinality)PropertyKey.dnsSrv, (String)REPLICATION_DNS_SRV_CONNECTION),
/*     */     
/* 117 */     XDEVAPI_SESSION("mysqlx:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.XDevApiConnectionUrl", (ConnectionUrl.HostsCardinality)PropertyKey.xdevapiDnsSrv, (String)XDEVAPI_DNS_SRV_SESSION);
/*     */     
/*     */     private String scheme;
/*     */     
/*     */     private ConnectionUrl.HostsCardinality cardinality;
/*     */     
/*     */     private String implementingClass;
/*     */     
/*     */     private PropertyKey dnsSrvPropertyKey;
/*     */     
/*     */     private Type alternateDnsSrvType;
/*     */ 
/*     */     
/*     */     Type(String scheme, ConnectionUrl.HostsCardinality cardinality, String implementingClass, PropertyKey dnsSrvPropertyKey, Type alternateDnsSrvType) {
/* 131 */       this.scheme = scheme;
/* 132 */       this.cardinality = cardinality;
/* 133 */       this.implementingClass = implementingClass;
/* 134 */       this.dnsSrvPropertyKey = dnsSrvPropertyKey;
/* 135 */       this.alternateDnsSrvType = alternateDnsSrvType;
/*     */     }
/*     */     
/*     */     public String getScheme() {
/* 139 */       return this.scheme;
/*     */     }
/*     */     
/*     */     public ConnectionUrl.HostsCardinality getCardinality() {
/* 143 */       return this.cardinality;
/*     */     }
/*     */     
/*     */     public String getImplementingClass() {
/* 147 */       return this.implementingClass;
/*     */     }
/*     */     
/*     */     public PropertyKey getDnsSrvPropertyKey() {
/* 151 */       return this.dnsSrvPropertyKey;
/*     */     }
/*     */     
/*     */     public Type getAlternateDnsSrvType() {
/* 155 */       return this.alternateDnsSrvType;
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
/*     */     public static Type fromValue(String scheme, int n) {
/* 170 */       for (Type t : values()) {
/* 171 */         if (t.getScheme().equalsIgnoreCase(scheme) && (n < 0 || t.getCardinality().assertSize(n))) {
/* 172 */           return t;
/*     */         }
/*     */       } 
/* 175 */       if (n < 0) {
/* 176 */         throw (UnsupportedConnectionStringException)ExceptionFactory.createException(UnsupportedConnectionStringException.class, 
/* 177 */             Messages.getString("ConnectionString.5", new Object[] { scheme }));
/*     */       }
/* 179 */       throw (UnsupportedConnectionStringException)ExceptionFactory.createException(UnsupportedConnectionStringException.class, 
/* 180 */           Messages.getString("ConnectionString.6", new Object[] { scheme, Integer.valueOf(n) }));
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
/*     */     public static ConnectionUrl getConnectionUrlInstance(ConnectionUrlParser parser, Properties info) {
/* 194 */       int hostsCount = parser.getHosts().size();
/* 195 */       Type type = fromValue(parser.getScheme(), hostsCount);
/* 196 */       PropertyKey dnsSrvPropKey = type.getDnsSrvPropertyKey();
/*     */ 
/*     */ 
/*     */       
/* 200 */       if (dnsSrvPropKey != null && type.getAlternateDnsSrvType() != null) {
/* 201 */         if (info != null && info.containsKey(dnsSrvPropKey.getKeyName())) {
/* 202 */           if (((Boolean)PropertyDefinitions.getPropertyDefinition(dnsSrvPropKey).parseObject(info.getProperty(dnsSrvPropKey.getKeyName()), null)).booleanValue())
/* 203 */             type = fromValue(type.getAlternateDnsSrvType().getScheme(), hostsCount); 
/*     */         } else {
/* 205 */           Map<String, String> parsedProperties; if ((parsedProperties = parser.getProperties()).containsKey(dnsSrvPropKey.getKeyName()) && (
/* 206 */             (Boolean)PropertyDefinitions.getPropertyDefinition(dnsSrvPropKey).parseObject(parsedProperties.get(dnsSrvPropKey.getKeyName()), null)).booleanValue()) {
/* 207 */             type = fromValue(type.getAlternateDnsSrvType().getScheme(), hostsCount);
/*     */           }
/*     */         } 
/*     */       }
/* 211 */       return type.getImplementingInstance(parser, info);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static boolean isSupported(String scheme) {
/* 222 */       for (Type t : values()) {
/* 223 */         if (t.getScheme().equalsIgnoreCase(scheme)) {
/* 224 */           return true;
/*     */         }
/*     */       } 
/* 227 */       return false;
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
/*     */     private ConnectionUrl getImplementingInstance(ConnectionUrlParser parser, Properties info) {
/* 241 */       return (ConnectionUrl)Util.getInstance(getImplementingClass(), new Class[] { ConnectionUrlParser.class, Properties.class }, new Object[] { parser, info }, null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 249 */   protected List<HostInfo> hosts = new ArrayList<>();
/* 250 */   protected Map<String, String> properties = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ConnectionPropertiesTransform propertiesTransformer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ConnectionUrl getConnectionUrlInstance(String connString, Properties info) {
/* 264 */     if (connString == null) {
/* 265 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.0"));
/*     */     }
/* 267 */     String connStringCacheKey = buildConnectionStringCacheKey(connString, info);
/*     */ 
/*     */     
/* 270 */     rwLock.readLock().lock();
/* 271 */     ConnectionUrl connectionUrl = (ConnectionUrl)connectionUrlCache.get(connStringCacheKey);
/* 272 */     if (connectionUrl == null) {
/* 273 */       rwLock.readLock().unlock();
/* 274 */       rwLock.writeLock().lock();
/*     */       
/*     */       try {
/* 277 */         connectionUrl = (ConnectionUrl)connectionUrlCache.get(connStringCacheKey);
/* 278 */         if (connectionUrl == null) {
/* 279 */           ConnectionUrlParser connStrParser = ConnectionUrlParser.parseConnectionString(connString);
/* 280 */           connectionUrl = Type.getConnectionUrlInstance(connStrParser, info);
/* 281 */           connectionUrlCache.put(connStringCacheKey, connectionUrl);
/*     */         } 
/* 283 */         rwLock.readLock().lock();
/*     */       } finally {
/* 285 */         rwLock.writeLock().unlock();
/*     */       } 
/*     */     } 
/* 288 */     rwLock.readLock().unlock();
/* 289 */     return connectionUrl;
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
/*     */   private static String buildConnectionStringCacheKey(String connString, Properties info) {
/* 302 */     StringBuilder sbKey = new StringBuilder(connString);
/* 303 */     sbKey.append("§");
/* 304 */     sbKey.append((info == null) ? null : info
/* 305 */         .stringPropertyNames().stream().map(k -> k + "=" + info.getProperty(k)).collect(Collectors.joining(", ", "{", "}")));
/* 306 */     return sbKey.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean acceptsUrl(String connString) {
/* 317 */     return ConnectionUrlParser.isConnectionStringSupported(connString);
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
/*     */   public ConnectionUrl(String origUrl) {
/* 333 */     this.originalConnStr = origUrl;
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
/*     */   protected ConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/* 345 */     this.originalConnStr = connStrParser.getDatabaseUrl();
/* 346 */     this.originalDatabase = (connStrParser.getPath() == null) ? "" : connStrParser.getPath();
/* 347 */     collectProperties(connStrParser, info);
/* 348 */     collectHostsInfo(connStrParser);
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
/*     */   protected void collectProperties(ConnectionUrlParser connStrParser, Properties info) {
/* 362 */     connStrParser.getProperties().entrySet().stream().forEach(e -> (String)this.properties.put(PropertyKey.normalizeCase((String)e.getKey()), (String)e.getValue()));
/*     */ 
/*     */     
/* 365 */     if (info != null) {
/* 366 */       info.stringPropertyNames().stream().forEach(k -> (String)this.properties.put(PropertyKey.normalizeCase(k), info.getProperty(k)));
/*     */     }
/*     */ 
/*     */     
/* 370 */     setupPropertiesTransformer();
/* 371 */     expandPropertiesFromConfigFiles(this.properties);
/* 372 */     injectPerTypeProperties(this.properties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setupPropertiesTransformer() {
/* 379 */     String propertiesTransformClassName = this.properties.get(PropertyKey.propertiesTransform.getKeyName());
/* 380 */     if (!StringUtils.isNullOrEmpty(propertiesTransformClassName)) {
/*     */       try {
/* 382 */         this.propertiesTransformer = (ConnectionPropertiesTransform)Class.forName(propertiesTransformClassName).newInstance();
/* 383 */       } catch (InstantiationException|IllegalAccessException|ClassNotFoundException|com.mysql.cj.exceptions.CJException e) {
/* 384 */         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/* 385 */             Messages.getString("ConnectionString.9", new Object[] { propertiesTransformClassName, e.toString() }), e);
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
/*     */   protected void expandPropertiesFromConfigFiles(Map<String, String> props) {
/* 398 */     String configFiles = props.get(PropertyKey.useConfigs.getKeyName());
/* 399 */     if (!StringUtils.isNullOrEmpty(configFiles)) {
/* 400 */       Properties configProps = getPropertiesFromConfigFiles(configFiles);
/* 401 */       configProps.stringPropertyNames().stream().map(PropertyKey::normalizeCase).filter(k -> !props.containsKey(k))
/* 402 */         .forEach(k -> (String)props.put(k, configProps.getProperty(k)));
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
/*     */   public static Properties getPropertiesFromConfigFiles(String configFiles) {
/* 414 */     Properties configProps = new Properties();
/* 415 */     for (String configFile : configFiles.split(",")) {
/* 416 */       try (InputStream configAsStream = ConnectionUrl.class.getResourceAsStream("/com/mysql/cj/configurations/" + configFile + ".properties")) {
/* 417 */         if (configAsStream == null) {
/* 418 */           throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/* 419 */               Messages.getString("ConnectionString.10", new Object[] { configFile }));
/*     */         }
/* 421 */         configProps.load(configAsStream);
/* 422 */       } catch (IOException e) {
/* 423 */         throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, 
/* 424 */             Messages.getString("ConnectionString.11", new Object[] { configFile }), e);
/*     */       } 
/*     */     } 
/* 427 */     return configProps;
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
/*     */   protected void injectPerTypeProperties(Map<String, String> props) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void replaceLegacyPropertyValues(Map<String, String> props) {
/* 449 */     String zeroDateTimeBehavior = props.get(PropertyKey.zeroDateTimeBehavior.getKeyName());
/* 450 */     if (zeroDateTimeBehavior != null && zeroDateTimeBehavior.equalsIgnoreCase("convertToNull")) {
/* 451 */       props.put(PropertyKey.zeroDateTimeBehavior.getKeyName(), "CONVERT_TO_NULL");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void collectHostsInfo(ConnectionUrlParser connStrParser) {
/* 462 */     connStrParser.getHosts().stream().map(this::fixHostInfo).forEach(this.hosts::add);
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
/*     */   protected HostInfo fixHostInfo(HostInfo hi) {
/* 474 */     Map<String, String> hostProps = new HashMap<>();
/*     */ 
/*     */     
/* 477 */     hostProps.putAll(this.properties);
/*     */     
/* 479 */     hi.getHostProperties().entrySet().stream().forEach(e -> (String)hostProps.put(PropertyKey.normalizeCase((String)e.getKey()), (String)e.getValue()));
/*     */     
/* 481 */     if (!hostProps.containsKey(PropertyKey.DBNAME.getKeyName())) {
/* 482 */       hostProps.put(PropertyKey.DBNAME.getKeyName(), getDatabase());
/*     */     }
/*     */     
/* 485 */     preprocessPerTypeHostProperties(hostProps);
/*     */     
/* 487 */     String host = hostProps.remove(PropertyKey.HOST.getKeyName());
/* 488 */     if (!StringUtils.isNullOrEmpty(hi.getHost())) {
/* 489 */       host = hi.getHost();
/* 490 */     } else if (StringUtils.isNullOrEmpty(host)) {
/* 491 */       host = getDefaultHost();
/*     */     } 
/*     */     
/* 494 */     String portAsString = hostProps.remove(PropertyKey.PORT.getKeyName());
/* 495 */     int port = hi.getPort();
/* 496 */     if (port == -1 && !StringUtils.isNullOrEmpty(portAsString)) {
/*     */       try {
/* 498 */         port = Integer.valueOf(portAsString).intValue();
/* 499 */       } catch (NumberFormatException e) {
/* 500 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, 
/* 501 */             Messages.getString("ConnectionString.7", new Object[] { hostProps.get(PropertyKey.PORT.getKeyName()) }), e);
/*     */       } 
/*     */     }
/* 504 */     if (port == -1) {
/* 505 */       port = getDefaultPort();
/*     */     }
/*     */     
/* 508 */     String user = hostProps.remove(PropertyKey.USER.getKeyName());
/* 509 */     if (!StringUtils.isNullOrEmpty(hi.getUser())) {
/* 510 */       user = hi.getUser();
/* 511 */     } else if (StringUtils.isNullOrEmpty(user)) {
/* 512 */       user = getDefaultUser();
/*     */     } 
/*     */     
/* 515 */     String password = hostProps.remove(PropertyKey.PASSWORD.getKeyName());
/* 516 */     if (hi.getPassword() != null) {
/* 517 */       password = hi.getPassword();
/* 518 */     } else if (StringUtils.isNullOrEmpty(password)) {
/* 519 */       password = getDefaultPassword();
/*     */     } 
/*     */     
/* 522 */     expandPropertiesFromConfigFiles(hostProps);
/* 523 */     fixProtocolDependencies(hostProps);
/* 524 */     replaceLegacyPropertyValues(hostProps);
/*     */     
/* 526 */     return buildHostInfo(host, port, user, password, hostProps);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void preprocessPerTypeHostProperties(Map<String, String> hostProps) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultHost() {
/* 545 */     return "localhost";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDefaultPort() {
/* 554 */     return 3306;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultUser() {
/* 563 */     return this.properties.get(PropertyKey.USER.getKeyName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDefaultPassword() {
/* 573 */     return this.properties.get(PropertyKey.PASSWORD.getKeyName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void fixProtocolDependencies(Map<String, String> hostProps) {
/* 583 */     String protocol = hostProps.get(PropertyKey.PROTOCOL.getKeyName());
/* 584 */     if (!StringUtils.isNullOrEmpty(protocol) && protocol.equalsIgnoreCase("PIPE") && 
/* 585 */       !hostProps.containsKey(PropertyKey.socketFactory.getKeyName())) {
/* 586 */       hostProps.put(PropertyKey.socketFactory.getKeyName(), "com.mysql.cj.protocol.NamedPipeSocketFactory");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type getType() {
/* 597 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatabaseUrl() {
/* 607 */     return this.originalConnStr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDatabase() {
/* 616 */     return this.properties.containsKey(PropertyKey.DBNAME.getKeyName()) ? this.properties.get(PropertyKey.DBNAME.getKeyName()) : this.originalDatabase;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hostsCount() {
/* 625 */     return this.hosts.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HostInfo getMainHost() {
/* 634 */     return this.hosts.isEmpty() ? null : this.hosts.get(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<HostInfo> getHostsList() {
/* 643 */     return getHostsList(HostsListView.ALL);
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
/*     */   public List<HostInfo> getHostsList(HostsListView view) {
/* 658 */     return Collections.unmodifiableList(this.hosts);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HostInfo getHostOrSpawnIsolated(String hostPortPair) {
/* 669 */     return getHostOrSpawnIsolated(hostPortPair, this.hosts);
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
/*     */   public HostInfo getHostOrSpawnIsolated(String hostPortPair, List<HostInfo> hostsList) {
/* 682 */     for (HostInfo hi : hostsList) {
/* 683 */       if (hostPortPair.equals(hi.getHostPortPair())) {
/* 684 */         return hi;
/*     */       }
/*     */     } 
/*     */     
/* 688 */     ConnectionUrlParser.Pair<String, Integer> hostAndPort = ConnectionUrlParser.parseHostPortPair(hostPortPair);
/* 689 */     String host = (String)hostAndPort.left;
/* 690 */     Integer port = (Integer)hostAndPort.right;
/* 691 */     String user = getDefaultUser();
/* 692 */     String password = getDefaultPassword();
/*     */     
/* 694 */     return buildHostInfo(host, port.intValue(), user, password, this.properties);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HostInfo buildHostInfo(String host, int port, String user, String password, Map<String, String> hostProps) {
/* 715 */     if (this.propertiesTransformer != null) {
/* 716 */       Properties props = new Properties();
/* 717 */       props.putAll(hostProps);
/*     */       
/* 719 */       props.setProperty(PropertyKey.HOST.getKeyName(), host);
/* 720 */       props.setProperty(PropertyKey.PORT.getKeyName(), String.valueOf(port));
/* 721 */       if (user != null) {
/* 722 */         props.setProperty(PropertyKey.USER.getKeyName(), user);
/*     */       }
/* 724 */       if (password != null) {
/* 725 */         props.setProperty(PropertyKey.PASSWORD.getKeyName(), password);
/*     */       }
/*     */       
/* 728 */       Properties transformedProps = this.propertiesTransformer.transformProperties(props);
/*     */       
/* 730 */       host = transformedProps.getProperty(PropertyKey.HOST.getKeyName());
/*     */       try {
/* 732 */         port = Integer.parseInt(transformedProps.getProperty(PropertyKey.PORT.getKeyName()));
/* 733 */       } catch (NumberFormatException e) {
/* 734 */         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.8", new Object[] { PropertyKey.PORT
/* 735 */                 .getKeyName(), transformedProps.getProperty(PropertyKey.PORT.getKeyName()) }), e);
/*     */       } 
/* 737 */       user = transformedProps.getProperty(PropertyKey.USER.getKeyName());
/* 738 */       password = transformedProps.getProperty(PropertyKey.PASSWORD.getKeyName());
/*     */       
/* 740 */       Map<String, String> transformedHostProps = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
/* 741 */       transformedProps.stringPropertyNames().stream().forEach(k -> (String)transformedHostProps.put(k, transformedProps.getProperty(k)));
/*     */       
/* 743 */       transformedHostProps.remove(PropertyKey.HOST.getKeyName());
/* 744 */       transformedHostProps.remove(PropertyKey.PORT.getKeyName());
/* 745 */       transformedHostProps.remove(PropertyKey.USER.getKeyName());
/* 746 */       transformedHostProps.remove(PropertyKey.PASSWORD.getKeyName());
/*     */       
/* 748 */       hostProps = transformedHostProps;
/*     */     } 
/*     */     
/* 751 */     return new HostInfo(this, host, port, user, password, hostProps);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getOriginalProperties() {
/* 760 */     return Collections.unmodifiableMap(this.properties);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties getConnectionArgumentsAsProperties() {
/* 770 */     Properties props = new Properties();
/* 771 */     if (this.properties != null) {
/* 772 */       props.putAll(this.properties);
/*     */     }
/*     */     
/* 775 */     return (this.propertiesTransformer != null) ? this.propertiesTransformer.transformProperties(props) : props;
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
/*     */   public List<HostInfo> getHostsListFromDnsSrv(HostInfo srvHost) {
/* 787 */     String srvServiceName = srvHost.getHost();
/* 788 */     List<DnsSrv.SrvRecord> srvRecords = null;
/*     */     
/*     */     try {
/* 791 */       srvRecords = DnsSrv.lookupSrvRecords(srvServiceName);
/* 792 */     } catch (NamingException e) {
/* 793 */       throw ExceptionFactory.createException(Messages.getString("ConnectionString.26", new Object[] { srvServiceName }), e);
/*     */     } 
/* 795 */     if (srvRecords == null || srvRecords.size() == 0) {
/* 796 */       throw ExceptionFactory.createException(Messages.getString("ConnectionString.26", new Object[] { srvServiceName }));
/*     */     }
/*     */     
/* 799 */     return Collections.unmodifiableList(srvRecordsToHostsList(srvRecords, srvHost));
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
/*     */   private List<HostInfo> srvRecordsToHostsList(List<DnsSrv.SrvRecord> srvRecords, HostInfo baseHostInfo) {
/* 813 */     return (List<HostInfo>)srvRecords.stream()
/* 814 */       .map(s -> buildHostInfo(s.getTarget(), s.getPort(), baseHostInfo.getUser(), baseHostInfo.getPassword(), baseHostInfo.getHostProperties()))
/* 815 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 825 */     StringBuilder asStr = new StringBuilder(super.toString());
/* 826 */     asStr.append(String.format(" :: {type: \"%s\", hosts: %s, database: \"%s\", properties: %s, propertiesTransformer: %s}", new Object[] { this.type, this.hosts, this.originalDatabase, this.properties, this.propertiesTransformer }));
/*     */     
/* 828 */     return asStr.toString();
/*     */   }
/*     */   
/*     */   protected ConnectionUrl() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\ConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */