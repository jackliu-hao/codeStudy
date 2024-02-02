/*     */ package cn.hutool.db.nosql.mongo;
/*     */ 
/*     */ import cn.hutool.core.exceptions.NotInitedException;
/*     */ import cn.hutool.core.net.NetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.log.Log;
/*     */ import cn.hutool.setting.Setting;
/*     */ import com.mongodb.MongoClientSettings;
/*     */ import com.mongodb.MongoCredential;
/*     */ import com.mongodb.ServerAddress;
/*     */ import com.mongodb.client.MongoClient;
/*     */ import com.mongodb.client.MongoClients;
/*     */ import com.mongodb.client.MongoCollection;
/*     */ import com.mongodb.client.MongoDatabase;
/*     */ import com.mongodb.connection.ClusterSettings;
/*     */ import com.mongodb.connection.ConnectionPoolSettings;
/*     */ import com.mongodb.connection.SocketSettings;
/*     */ import java.io.Closeable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.bson.Document;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MongoDS
/*     */   implements Closeable
/*     */ {
/*  33 */   private static final Log log = Log.get();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String MONGO_CONFIG_PATH = "config/mongo.setting";
/*     */ 
/*     */ 
/*     */   
/*     */   private Setting setting;
/*     */ 
/*     */ 
/*     */   
/*     */   private String[] groups;
/*     */ 
/*     */ 
/*     */   
/*     */   private ServerAddress serverAddress;
/*     */ 
/*     */ 
/*     */   
/*     */   private MongoClient mongo;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MongoDS(String host, int port) {
/*  59 */     this.serverAddress = createServerAddress(host, port);
/*  60 */     initSingle();
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
/*     */   public MongoDS(Setting mongoSetting, String host, int port) {
/*  72 */     this.setting = mongoSetting;
/*  73 */     this.serverAddress = createServerAddress(host, port);
/*  74 */     initSingle();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MongoDS(String... groups) {
/*  85 */     this.groups = groups;
/*  86 */     init();
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
/*     */   public MongoDS(Setting mongoSetting, String... groups) {
/*  99 */     if (mongoSetting == null) {
/* 100 */       throw new DbRuntimeException("Mongo setting is null!");
/*     */     }
/* 102 */     this.setting = mongoSetting;
/* 103 */     this.groups = groups;
/* 104 */     init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init() {
/* 112 */     if (this.groups != null && this.groups.length > 1) {
/* 113 */       initCloud();
/*     */     } else {
/* 115 */       initSingle();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void initSingle() {
/* 137 */     if (this.setting == null) {
/*     */       try {
/* 139 */         this.setting = new Setting("config/mongo.setting", true);
/* 140 */       } catch (Exception exception) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 145 */     String group = "";
/* 146 */     if (null == this.serverAddress) {
/*     */       
/* 148 */       if (this.groups != null && this.groups.length == 1) {
/* 149 */         group = this.groups[0];
/*     */       }
/* 151 */       this.serverAddress = createServerAddress(group);
/*     */     } 
/*     */     
/* 154 */     MongoCredential credentail = createCredentail(group);
/*     */     
/*     */     try {
/* 157 */       MongoClientSettings.Builder clusterSettingsBuilder = MongoClientSettings.builder().applyToClusterSettings(b -> b.hosts(Collections.singletonList(this.serverAddress)));
/* 158 */       buildMongoClientSettings(clusterSettingsBuilder, group);
/* 159 */       if (null != credentail) {
/* 160 */         clusterSettingsBuilder.credential(credentail);
/*     */       }
/* 162 */       this.mongo = MongoClients.create(clusterSettingsBuilder.build());
/* 163 */     } catch (Exception e) {
/* 164 */       throw new DbRuntimeException(StrUtil.format("Init MongoDB pool with connection to [{}] error!", new Object[] { this.serverAddress }), e);
/*     */     } 
/*     */     
/* 167 */     log.info("Init MongoDB pool with connection to [{}]", new Object[] { this.serverAddress });
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
/*     */   public synchronized void initCloud() {
/* 188 */     if (this.groups == null || this.groups.length == 0) {
/* 189 */       throw new DbRuntimeException("Please give replication set groups!");
/*     */     }
/*     */     
/* 192 */     if (this.setting == null)
/*     */     {
/* 194 */       this.setting = new Setting("config/mongo.setting", true);
/*     */     }
/*     */     
/* 197 */     List<ServerAddress> addrList = new ArrayList<>();
/* 198 */     for (String group : this.groups) {
/* 199 */       addrList.add(createServerAddress(group));
/*     */     }
/*     */     
/* 202 */     MongoCredential credentail = createCredentail("");
/*     */     
/*     */     try {
/* 205 */       MongoClientSettings.Builder clusterSettingsBuilder = MongoClientSettings.builder().applyToClusterSettings(b -> b.hosts(addrList));
/* 206 */       buildMongoClientSettings(clusterSettingsBuilder, "");
/* 207 */       if (null != credentail) {
/* 208 */         clusterSettingsBuilder.credential(credentail);
/*     */       }
/* 210 */       this.mongo = MongoClients.create(clusterSettingsBuilder.build());
/* 211 */     } catch (Exception e) {
/* 212 */       log.error(e, "Init MongoDB connection error!", new Object[0]);
/*     */       
/*     */       return;
/*     */     } 
/* 216 */     log.info("Init MongoDB cloud Set pool with connection to {}", new Object[] { addrList });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSetting(Setting setting) {
/* 225 */     this.setting = setting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MongoClient getMongo() {
/* 232 */     return this.mongo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MongoDatabase getDb(String dbName) {
/* 242 */     return this.mongo.getDatabase(dbName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MongoCollection<Document> getCollection(String dbName, String collectionName) {
/* 253 */     return getDb(dbName).getCollection(collectionName);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 258 */     this.mongo.close();
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
/*     */   private ServerAddress createServerAddress(String group) {
/* 270 */     Setting setting = checkSetting();
/*     */     
/* 272 */     if (group == null) {
/* 273 */       group = "";
/*     */     }
/*     */     
/* 276 */     String tmpHost = setting.getByGroup("host", group);
/* 277 */     if (StrUtil.isBlank(tmpHost)) {
/* 278 */       throw new NotInitedException("Host name is empy of group: {}", new Object[] { group });
/*     */     }
/*     */     
/* 281 */     int defaultPort = setting.getInt("port", group, Integer.valueOf(27017)).intValue();
/* 282 */     return new ServerAddress(NetUtil.buildInetSocketAddress(tmpHost, defaultPort));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ServerAddress createServerAddress(String host, int port) {
/* 293 */     return new ServerAddress(host, port);
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
/*     */   private MongoCredential createCredentail(String group) {
/* 305 */     Setting setting = this.setting;
/* 306 */     if (null == setting) {
/* 307 */       return null;
/*     */     }
/* 309 */     String user = setting.getStr("user", group, setting.getStr("user"));
/* 310 */     String pass = setting.getStr("pass", group, setting.getStr("pass"));
/* 311 */     String database = setting.getStr("database", group, setting.getStr("database"));
/* 312 */     return createCredentail(user, database, pass);
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
/*     */   private MongoCredential createCredentail(String userName, String database, String password) {
/* 325 */     if (StrUtil.hasEmpty(new CharSequence[] { userName, database, database })) {
/* 326 */       return null;
/*     */     }
/* 328 */     return MongoCredential.createCredential(userName, database, password.toCharArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private MongoClientSettings.Builder buildMongoClientSettings(MongoClientSettings.Builder builder, String group) {
/* 338 */     if (this.setting == null) {
/* 339 */       return builder;
/*     */     }
/*     */     
/* 342 */     if (StrUtil.isEmpty(group)) {
/* 343 */       group = "";
/*     */     } else {
/* 345 */       group = group + ".";
/*     */     } 
/*     */ 
/*     */     
/* 349 */     Integer connectionsPerHost = this.setting.getInt(group + "connectionsPerHost");
/* 350 */     if (!StrUtil.isBlank(group) && connectionsPerHost == null) {
/* 351 */       connectionsPerHost = this.setting.getInt("connectionsPerHost");
/*     */     }
/* 353 */     ConnectionPoolSettings.Builder connectionPoolSettingsBuilder = ConnectionPoolSettings.builder();
/* 354 */     if (connectionsPerHost != null) {
/* 355 */       connectionPoolSettingsBuilder.maxSize(connectionsPerHost.intValue());
/* 356 */       log.debug("MongoDB connectionsPerHost: {}", new Object[] { connectionsPerHost });
/*     */     } 
/*     */ 
/*     */     
/* 360 */     Integer connectTimeout = this.setting.getInt(group + "connectTimeout");
/* 361 */     if (!StrUtil.isBlank(group) && connectTimeout == null) {
/* 362 */       this.setting.getInt("connectTimeout");
/*     */     }
/* 364 */     if (connectTimeout != null) {
/* 365 */       connectionPoolSettingsBuilder.maxWaitTime(connectTimeout.intValue(), TimeUnit.MILLISECONDS);
/* 366 */       log.debug("MongoDB connectTimeout: {}", new Object[] { connectTimeout });
/*     */     } 
/* 368 */     builder.applyToConnectionPoolSettings(b -> b.applySettings(connectionPoolSettingsBuilder.build()));
/*     */ 
/*     */     
/* 371 */     Integer socketTimeout = this.setting.getInt(group + "socketTimeout");
/* 372 */     if (!StrUtil.isBlank(group) && socketTimeout == null) {
/* 373 */       this.setting.getInt("socketTimeout");
/*     */     }
/* 375 */     if (socketTimeout != null) {
/* 376 */       SocketSettings socketSettings = SocketSettings.builder().connectTimeout(socketTimeout.intValue(), TimeUnit.MILLISECONDS).build();
/* 377 */       builder.applyToSocketSettings(b -> b.applySettings(socketSettings));
/* 378 */       log.debug("MongoDB socketTimeout: {}", new Object[] { socketTimeout });
/*     */     } 
/*     */     
/* 381 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Setting checkSetting() {
/* 390 */     if (null == this.setting) {
/* 391 */       throw new DbRuntimeException("Please indicate setting file or create default [{}]", new Object[] { "config/mongo.setting" });
/*     */     }
/* 393 */     return this.setting;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\nosql\mongo\MongoDS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */