package com.mysql.cj.conf;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.exceptions.InvalidConnectionAttributeException;
import com.mysql.cj.exceptions.UnsupportedConnectionStringException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.util.DnsSrv;
import com.mysql.cj.util.LRUCache;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.naming.NamingException;

public abstract class ConnectionUrl implements DatabaseUrlContainer {
   public static final String DEFAULT_HOST = "localhost";
   public static final int DEFAULT_PORT = 3306;
   private static final LRUCache<String, ConnectionUrl> connectionUrlCache = new LRUCache(100);
   private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
   protected Type type;
   protected String originalConnStr;
   protected String originalDatabase;
   protected List<HostInfo> hosts = new ArrayList();
   protected Map<String, String> properties = new HashMap();
   ConnectionPropertiesTransform propertiesTransformer;

   public static ConnectionUrl getConnectionUrlInstance(String connString, Properties info) {
      if (connString == null) {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.0"));
      } else {
         String connStringCacheKey = buildConnectionStringCacheKey(connString, info);
         rwLock.readLock().lock();
         ConnectionUrl connectionUrl = (ConnectionUrl)connectionUrlCache.get(connStringCacheKey);
         if (connectionUrl == null) {
            rwLock.readLock().unlock();
            rwLock.writeLock().lock();

            try {
               connectionUrl = (ConnectionUrl)connectionUrlCache.get(connStringCacheKey);
               if (connectionUrl == null) {
                  ConnectionUrlParser connStrParser = ConnectionUrlParser.parseConnectionString(connString);
                  connectionUrl = ConnectionUrl.Type.getConnectionUrlInstance(connStrParser, info);
                  connectionUrlCache.put(connStringCacheKey, connectionUrl);
               }

               rwLock.readLock().lock();
            } finally {
               rwLock.writeLock().unlock();
            }
         }

         rwLock.readLock().unlock();
         return connectionUrl;
      }
   }

   private static String buildConnectionStringCacheKey(String connString, Properties info) {
      StringBuilder sbKey = new StringBuilder(connString);
      sbKey.append("§");
      sbKey.append(info == null ? null : (String)info.stringPropertyNames().stream().map((k) -> {
         return k + "=" + info.getProperty(k);
      }).collect(Collectors.joining(", ", "{", "}")));
      return sbKey.toString();
   }

   public static boolean acceptsUrl(String connString) {
      return ConnectionUrlParser.isConnectionStringSupported(connString);
   }

   protected ConnectionUrl() {
   }

   public ConnectionUrl(String origUrl) {
      this.originalConnStr = origUrl;
   }

   protected ConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
      this.originalConnStr = connStrParser.getDatabaseUrl();
      this.originalDatabase = connStrParser.getPath() == null ? "" : connStrParser.getPath();
      this.collectProperties(connStrParser, info);
      this.collectHostsInfo(connStrParser);
   }

   protected void collectProperties(ConnectionUrlParser connStrParser, Properties info) {
      connStrParser.getProperties().entrySet().stream().forEach((e) -> {
         String var10000 = (String)this.properties.put(PropertyKey.normalizeCase((String)e.getKey()), e.getValue());
      });
      if (info != null) {
         info.stringPropertyNames().stream().forEach((k) -> {
            String var10000 = (String)this.properties.put(PropertyKey.normalizeCase(k), info.getProperty(k));
         });
      }

      this.setupPropertiesTransformer();
      this.expandPropertiesFromConfigFiles(this.properties);
      this.injectPerTypeProperties(this.properties);
   }

   protected void setupPropertiesTransformer() {
      String propertiesTransformClassName = (String)this.properties.get(PropertyKey.propertiesTransform.getKeyName());
      if (!StringUtils.isNullOrEmpty(propertiesTransformClassName)) {
         try {
            this.propertiesTransformer = (ConnectionPropertiesTransform)Class.forName(propertiesTransformClassName).newInstance();
         } catch (IllegalAccessException | ClassNotFoundException | CJException | InstantiationException var3) {
            throw (InvalidConnectionAttributeException)ExceptionFactory.createException((Class)InvalidConnectionAttributeException.class, (String)Messages.getString("ConnectionString.9", new Object[]{propertiesTransformClassName, var3.toString()}), (Throwable)var3);
         }
      }

   }

   protected void expandPropertiesFromConfigFiles(Map<String, String> props) {
      String configFiles = (String)props.get(PropertyKey.useConfigs.getKeyName());
      if (!StringUtils.isNullOrEmpty(configFiles)) {
         Properties configProps = getPropertiesFromConfigFiles(configFiles);
         configProps.stringPropertyNames().stream().map(PropertyKey::normalizeCase).filter((k) -> {
            return !props.containsKey(k);
         }).forEach((k) -> {
            String var10000 = (String)props.put(k, configProps.getProperty(k));
         });
      }

   }

   public static Properties getPropertiesFromConfigFiles(String configFiles) {
      Properties configProps = new Properties();
      String[] var2 = configFiles.split(",");
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String configFile = var2[var4];

         try {
            InputStream configAsStream = ConnectionUrl.class.getResourceAsStream("/com/mysql/cj/configurations/" + configFile + ".properties");
            Throwable var7 = null;

            try {
               if (configAsStream == null) {
                  throw (InvalidConnectionAttributeException)ExceptionFactory.createException(InvalidConnectionAttributeException.class, Messages.getString("ConnectionString.10", new Object[]{configFile}));
               }

               configProps.load(configAsStream);
            } catch (Throwable var17) {
               var7 = var17;
               throw var17;
            } finally {
               if (configAsStream != null) {
                  if (var7 != null) {
                     try {
                        configAsStream.close();
                     } catch (Throwable var16) {
                        var7.addSuppressed(var16);
                     }
                  } else {
                     configAsStream.close();
                  }
               }

            }
         } catch (IOException var19) {
            throw (InvalidConnectionAttributeException)ExceptionFactory.createException((Class)InvalidConnectionAttributeException.class, (String)Messages.getString("ConnectionString.11", new Object[]{configFile}), (Throwable)var19);
         }
      }

      return configProps;
   }

   protected void injectPerTypeProperties(Map<String, String> props) {
   }

   protected void replaceLegacyPropertyValues(Map<String, String> props) {
      String zeroDateTimeBehavior = (String)props.get(PropertyKey.zeroDateTimeBehavior.getKeyName());
      if (zeroDateTimeBehavior != null && zeroDateTimeBehavior.equalsIgnoreCase("convertToNull")) {
         props.put(PropertyKey.zeroDateTimeBehavior.getKeyName(), "CONVERT_TO_NULL");
      }

   }

   protected void collectHostsInfo(ConnectionUrlParser connStrParser) {
      Stream var10000 = connStrParser.getHosts().stream().map(this::fixHostInfo);
      List var10001 = this.hosts;
      var10000.forEach(var10001::add);
   }

   protected HostInfo fixHostInfo(HostInfo hi) {
      Map<String, String> hostProps = new HashMap();
      hostProps.putAll(this.properties);
      hi.getHostProperties().entrySet().stream().forEach((e) -> {
         String var10000 = (String)hostProps.put(PropertyKey.normalizeCase((String)e.getKey()), e.getValue());
      });
      if (!hostProps.containsKey(PropertyKey.DBNAME.getKeyName())) {
         hostProps.put(PropertyKey.DBNAME.getKeyName(), this.getDatabase());
      }

      this.preprocessPerTypeHostProperties(hostProps);
      String host = (String)hostProps.remove(PropertyKey.HOST.getKeyName());
      if (!StringUtils.isNullOrEmpty(hi.getHost())) {
         host = hi.getHost();
      } else if (StringUtils.isNullOrEmpty(host)) {
         host = this.getDefaultHost();
      }

      String portAsString = (String)hostProps.remove(PropertyKey.PORT.getKeyName());
      int port = hi.getPort();
      if (port == -1 && !StringUtils.isNullOrEmpty(portAsString)) {
         try {
            port = Integer.valueOf(portAsString);
         } catch (NumberFormatException var8) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("ConnectionString.7", new Object[]{hostProps.get(PropertyKey.PORT.getKeyName())}), (Throwable)var8);
         }
      }

      if (port == -1) {
         port = this.getDefaultPort();
      }

      String user = (String)hostProps.remove(PropertyKey.USER.getKeyName());
      if (!StringUtils.isNullOrEmpty(hi.getUser())) {
         user = hi.getUser();
      } else if (StringUtils.isNullOrEmpty(user)) {
         user = this.getDefaultUser();
      }

      String password = (String)hostProps.remove(PropertyKey.PASSWORD.getKeyName());
      if (hi.getPassword() != null) {
         password = hi.getPassword();
      } else if (StringUtils.isNullOrEmpty(password)) {
         password = this.getDefaultPassword();
      }

      this.expandPropertiesFromConfigFiles(hostProps);
      this.fixProtocolDependencies(hostProps);
      this.replaceLegacyPropertyValues(hostProps);
      return this.buildHostInfo(host, port, user, password, hostProps);
   }

   protected void preprocessPerTypeHostProperties(Map<String, String> hostProps) {
   }

   public String getDefaultHost() {
      return "localhost";
   }

   public int getDefaultPort() {
      return 3306;
   }

   public String getDefaultUser() {
      return (String)this.properties.get(PropertyKey.USER.getKeyName());
   }

   public String getDefaultPassword() {
      return (String)this.properties.get(PropertyKey.PASSWORD.getKeyName());
   }

   protected void fixProtocolDependencies(Map<String, String> hostProps) {
      String protocol = (String)hostProps.get(PropertyKey.PROTOCOL.getKeyName());
      if (!StringUtils.isNullOrEmpty(protocol) && protocol.equalsIgnoreCase("PIPE") && !hostProps.containsKey(PropertyKey.socketFactory.getKeyName())) {
         hostProps.put(PropertyKey.socketFactory.getKeyName(), "com.mysql.cj.protocol.NamedPipeSocketFactory");
      }

   }

   public Type getType() {
      return this.type;
   }

   public String getDatabaseUrl() {
      return this.originalConnStr;
   }

   public String getDatabase() {
      return this.properties.containsKey(PropertyKey.DBNAME.getKeyName()) ? (String)this.properties.get(PropertyKey.DBNAME.getKeyName()) : this.originalDatabase;
   }

   public int hostsCount() {
      return this.hosts.size();
   }

   public HostInfo getMainHost() {
      return this.hosts.isEmpty() ? null : (HostInfo)this.hosts.get(0);
   }

   public List<HostInfo> getHostsList() {
      return this.getHostsList(HostsListView.ALL);
   }

   public List<HostInfo> getHostsList(HostsListView view) {
      return Collections.unmodifiableList(this.hosts);
   }

   public HostInfo getHostOrSpawnIsolated(String hostPortPair) {
      return this.getHostOrSpawnIsolated(hostPortPair, this.hosts);
   }

   public HostInfo getHostOrSpawnIsolated(String hostPortPair, List<HostInfo> hostsList) {
      Iterator var3 = hostsList.iterator();

      HostInfo hi;
      do {
         if (!var3.hasNext()) {
            ConnectionUrlParser.Pair<String, Integer> hostAndPort = ConnectionUrlParser.parseHostPortPair(hostPortPair);
            String host = (String)hostAndPort.left;
            Integer port = (Integer)hostAndPort.right;
            String user = this.getDefaultUser();
            String password = this.getDefaultPassword();
            return this.buildHostInfo(host, port, user, password, this.properties);
         }

         hi = (HostInfo)var3.next();
      } while(!hostPortPair.equals(hi.getHostPortPair()));

      return hi;
   }

   protected HostInfo buildHostInfo(String host, int port, String user, String password, Map<String, String> hostProps) {
      if (this.propertiesTransformer != null) {
         Properties props = new Properties();
         props.putAll((Map)hostProps);
         props.setProperty(PropertyKey.HOST.getKeyName(), host);
         props.setProperty(PropertyKey.PORT.getKeyName(), String.valueOf(port));
         if (user != null) {
            props.setProperty(PropertyKey.USER.getKeyName(), user);
         }

         if (password != null) {
            props.setProperty(PropertyKey.PASSWORD.getKeyName(), password);
         }

         Properties transformedProps = this.propertiesTransformer.transformProperties(props);
         host = transformedProps.getProperty(PropertyKey.HOST.getKeyName());

         try {
            port = Integer.parseInt(transformedProps.getProperty(PropertyKey.PORT.getKeyName()));
         } catch (NumberFormatException var9) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("ConnectionString.8", new Object[]{PropertyKey.PORT.getKeyName(), transformedProps.getProperty(PropertyKey.PORT.getKeyName())}), (Throwable)var9);
         }

         user = transformedProps.getProperty(PropertyKey.USER.getKeyName());
         password = transformedProps.getProperty(PropertyKey.PASSWORD.getKeyName());
         Map<String, String> transformedHostProps = new TreeMap(String.CASE_INSENSITIVE_ORDER);
         transformedProps.stringPropertyNames().stream().forEach((k) -> {
            String var10000 = (String)transformedHostProps.put(k, transformedProps.getProperty(k));
         });
         transformedHostProps.remove(PropertyKey.HOST.getKeyName());
         transformedHostProps.remove(PropertyKey.PORT.getKeyName());
         transformedHostProps.remove(PropertyKey.USER.getKeyName());
         transformedHostProps.remove(PropertyKey.PASSWORD.getKeyName());
         hostProps = transformedHostProps;
      }

      return new HostInfo(this, host, port, user, password, (Map)hostProps);
   }

   public Map<String, String> getOriginalProperties() {
      return Collections.unmodifiableMap(this.properties);
   }

   public Properties getConnectionArgumentsAsProperties() {
      Properties props = new Properties();
      if (this.properties != null) {
         props.putAll(this.properties);
      }

      return this.propertiesTransformer != null ? this.propertiesTransformer.transformProperties(props) : props;
   }

   public List<HostInfo> getHostsListFromDnsSrv(HostInfo srvHost) {
      String srvServiceName = srvHost.getHost();
      List<DnsSrv.SrvRecord> srvRecords = null;

      try {
         srvRecords = DnsSrv.lookupSrvRecords(srvServiceName);
      } catch (NamingException var5) {
         throw ExceptionFactory.createException((String)Messages.getString("ConnectionString.26", new Object[]{srvServiceName}), (Throwable)var5);
      }

      if (srvRecords != null && srvRecords.size() != 0) {
         return Collections.unmodifiableList(this.srvRecordsToHostsList(srvRecords, srvHost));
      } else {
         throw ExceptionFactory.createException(Messages.getString("ConnectionString.26", new Object[]{srvServiceName}));
      }
   }

   private List<HostInfo> srvRecordsToHostsList(List<DnsSrv.SrvRecord> srvRecords, HostInfo baseHostInfo) {
      return (List)srvRecords.stream().map((s) -> {
         return this.buildHostInfo(s.getTarget(), s.getPort(), baseHostInfo.getUser(), baseHostInfo.getPassword(), baseHostInfo.getHostProperties());
      }).collect(Collectors.toList());
   }

   public String toString() {
      StringBuilder asStr = new StringBuilder(super.toString());
      asStr.append(String.format(" :: {type: \"%s\", hosts: %s, database: \"%s\", properties: %s, propertiesTransformer: %s}", this.type, this.hosts, this.originalDatabase, this.properties, this.propertiesTransformer));
      return asStr.toString();
   }

   public static enum Type {
      FAILOVER_DNS_SRV_CONNECTION("jdbc:mysql+srv:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.FailoverDnsSrvConnectionUrl"),
      LOADBALANCE_DNS_SRV_CONNECTION("jdbc:mysql+srv:loadbalance:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.LoadBalanceDnsSrvConnectionUrl"),
      REPLICATION_DNS_SRV_CONNECTION("jdbc:mysql+srv:replication:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.ReplicationDnsSrvConnectionUrl"),
      XDEVAPI_DNS_SRV_SESSION("mysqlx+srv:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.XDevApiDnsSrvConnectionUrl"),
      SINGLE_CONNECTION("jdbc:mysql:", ConnectionUrl.HostsCardinality.SINGLE, "com.mysql.cj.conf.url.SingleConnectionUrl", PropertyKey.dnsSrv, FAILOVER_DNS_SRV_CONNECTION),
      FAILOVER_CONNECTION("jdbc:mysql:", ConnectionUrl.HostsCardinality.MULTIPLE, "com.mysql.cj.conf.url.FailoverConnectionUrl", PropertyKey.dnsSrv, FAILOVER_DNS_SRV_CONNECTION),
      LOADBALANCE_CONNECTION("jdbc:mysql:loadbalance:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.LoadBalanceConnectionUrl", PropertyKey.dnsSrv, LOADBALANCE_DNS_SRV_CONNECTION),
      REPLICATION_CONNECTION("jdbc:mysql:replication:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.ReplicationConnectionUrl", PropertyKey.dnsSrv, REPLICATION_DNS_SRV_CONNECTION),
      XDEVAPI_SESSION("mysqlx:", ConnectionUrl.HostsCardinality.ONE_OR_MORE, "com.mysql.cj.conf.url.XDevApiConnectionUrl", PropertyKey.xdevapiDnsSrv, XDEVAPI_DNS_SRV_SESSION);

      private String scheme;
      private HostsCardinality cardinality;
      private String implementingClass;
      private PropertyKey dnsSrvPropertyKey;
      private Type alternateDnsSrvType;

      private Type(String scheme, HostsCardinality cardinality, String implementingClass) {
         this(scheme, cardinality, implementingClass, (PropertyKey)null, (Type)null);
      }

      private Type(String scheme, HostsCardinality cardinality, String implementingClass, PropertyKey dnsSrvPropertyKey, Type alternateDnsSrvType) {
         this.scheme = scheme;
         this.cardinality = cardinality;
         this.implementingClass = implementingClass;
         this.dnsSrvPropertyKey = dnsSrvPropertyKey;
         this.alternateDnsSrvType = alternateDnsSrvType;
      }

      public String getScheme() {
         return this.scheme;
      }

      public HostsCardinality getCardinality() {
         return this.cardinality;
      }

      public String getImplementingClass() {
         return this.implementingClass;
      }

      public PropertyKey getDnsSrvPropertyKey() {
         return this.dnsSrvPropertyKey;
      }

      public Type getAlternateDnsSrvType() {
         return this.alternateDnsSrvType;
      }

      public static Type fromValue(String scheme, int n) {
         Type[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Type t = var2[var4];
            if (t.getScheme().equalsIgnoreCase(scheme) && (n < 0 || t.getCardinality().assertSize(n))) {
               return t;
            }
         }

         if (n < 0) {
            throw (UnsupportedConnectionStringException)ExceptionFactory.createException(UnsupportedConnectionStringException.class, Messages.getString("ConnectionString.5", new Object[]{scheme}));
         } else {
            throw (UnsupportedConnectionStringException)ExceptionFactory.createException(UnsupportedConnectionStringException.class, Messages.getString("ConnectionString.6", new Object[]{scheme, n}));
         }
      }

      public static ConnectionUrl getConnectionUrlInstance(ConnectionUrlParser parser, Properties info) {
         int hostsCount = parser.getHosts().size();
         Type type = fromValue(parser.getScheme(), hostsCount);
         PropertyKey dnsSrvPropKey = type.getDnsSrvPropertyKey();
         if (dnsSrvPropKey != null && type.getAlternateDnsSrvType() != null) {
            if (info != null && info.containsKey(dnsSrvPropKey.getKeyName())) {
               if ((Boolean)PropertyDefinitions.getPropertyDefinition(dnsSrvPropKey).parseObject(info.getProperty(dnsSrvPropKey.getKeyName()), (ExceptionInterceptor)null)) {
                  type = fromValue(type.getAlternateDnsSrvType().getScheme(), hostsCount);
               }
            } else {
               Map parsedProperties;
               if ((parsedProperties = parser.getProperties()).containsKey(dnsSrvPropKey.getKeyName()) && (Boolean)PropertyDefinitions.getPropertyDefinition(dnsSrvPropKey).parseObject((String)parsedProperties.get(dnsSrvPropKey.getKeyName()), (ExceptionInterceptor)null)) {
                  type = fromValue(type.getAlternateDnsSrvType().getScheme(), hostsCount);
               }
            }
         }

         return type.getImplementingInstance(parser, info);
      }

      public static boolean isSupported(String scheme) {
         Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Type t = var1[var3];
            if (t.getScheme().equalsIgnoreCase(scheme)) {
               return true;
            }
         }

         return false;
      }

      private ConnectionUrl getImplementingInstance(ConnectionUrlParser parser, Properties info) {
         return (ConnectionUrl)Util.getInstance(this.getImplementingClass(), new Class[]{ConnectionUrlParser.class, Properties.class}, new Object[]{parser, info}, (ExceptionInterceptor)null);
      }
   }

   public static enum HostsCardinality {
      SINGLE {
         public boolean assertSize(int n) {
            return n == 1;
         }
      },
      MULTIPLE {
         public boolean assertSize(int n) {
            return n > 1;
         }
      },
      ONE_OR_MORE {
         public boolean assertSize(int n) {
            return n >= 1;
         }
      };

      private HostsCardinality() {
      }

      public abstract boolean assertSize(int var1);

      // $FF: synthetic method
      HostsCardinality(Object x2) {
         this();
      }
   }
}
