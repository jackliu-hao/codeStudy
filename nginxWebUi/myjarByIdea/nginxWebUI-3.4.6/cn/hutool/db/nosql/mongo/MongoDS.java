package cn.hutool.db.nosql.mongo;

import cn.hutool.core.exceptions.NotInitedException;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.log.Log;
import cn.hutool.setting.Setting;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.SocketSettings;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bson.Document;

public class MongoDS implements Closeable {
   private static final Log log = Log.get();
   public static final String MONGO_CONFIG_PATH = "config/mongo.setting";
   private Setting setting;
   private String[] groups;
   private ServerAddress serverAddress;
   private MongoClient mongo;

   public MongoDS(String host, int port) {
      this.serverAddress = this.createServerAddress(host, port);
      this.initSingle();
   }

   public MongoDS(Setting mongoSetting, String host, int port) {
      this.setting = mongoSetting;
      this.serverAddress = this.createServerAddress(host, port);
      this.initSingle();
   }

   public MongoDS(String... groups) {
      this.groups = groups;
      this.init();
   }

   public MongoDS(Setting mongoSetting, String... groups) {
      if (mongoSetting == null) {
         throw new DbRuntimeException("Mongo setting is null!");
      } else {
         this.setting = mongoSetting;
         this.groups = groups;
         this.init();
      }
   }

   public void init() {
      if (this.groups != null && this.groups.length > 1) {
         this.initCloud();
      } else {
         this.initSingle();
      }

   }

   public synchronized void initSingle() {
      if (this.setting == null) {
         try {
            this.setting = new Setting("config/mongo.setting", true);
         } catch (Exception var4) {
         }
      }

      String group = "";
      if (null == this.serverAddress) {
         if (this.groups != null && this.groups.length == 1) {
            group = this.groups[0];
         }

         this.serverAddress = this.createServerAddress(group);
      }

      MongoCredential credentail = this.createCredentail(group);

      try {
         MongoClientSettings.Builder clusterSettingsBuilder = MongoClientSettings.builder().applyToClusterSettings((b) -> {
            b.hosts(Collections.singletonList(this.serverAddress));
         });
         this.buildMongoClientSettings(clusterSettingsBuilder, group);
         if (null != credentail) {
            clusterSettingsBuilder.credential(credentail);
         }

         this.mongo = MongoClients.create(clusterSettingsBuilder.build());
      } catch (Exception var5) {
         throw new DbRuntimeException(StrUtil.format("Init MongoDB pool with connection to [{}] error!", new Object[]{this.serverAddress}), var5);
      }

      log.info("Init MongoDB pool with connection to [{}]", new Object[]{this.serverAddress});
   }

   public synchronized void initCloud() {
      if (this.groups != null && this.groups.length != 0) {
         if (this.setting == null) {
            this.setting = new Setting("config/mongo.setting", true);
         }

         List<ServerAddress> addrList = new ArrayList();
         String[] var2 = this.groups;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String group = var2[var4];
            addrList.add(this.createServerAddress(group));
         }

         MongoCredential credentail = this.createCredentail("");

         try {
            MongoClientSettings.Builder clusterSettingsBuilder = MongoClientSettings.builder().applyToClusterSettings((b) -> {
               b.hosts(addrList);
            });
            this.buildMongoClientSettings(clusterSettingsBuilder, "");
            if (null != credentail) {
               clusterSettingsBuilder.credential(credentail);
            }

            this.mongo = MongoClients.create(clusterSettingsBuilder.build());
         } catch (Exception var6) {
            log.error(var6, "Init MongoDB connection error!", new Object[0]);
            return;
         }

         log.info("Init MongoDB cloud Set pool with connection to {}", new Object[]{addrList});
      } else {
         throw new DbRuntimeException("Please give replication set groups!");
      }
   }

   public void setSetting(Setting setting) {
      this.setting = setting;
   }

   public MongoClient getMongo() {
      return this.mongo;
   }

   public MongoDatabase getDb(String dbName) {
      return this.mongo.getDatabase(dbName);
   }

   public MongoCollection<Document> getCollection(String dbName, String collectionName) {
      return this.getDb(dbName).getCollection(collectionName);
   }

   public void close() {
      this.mongo.close();
   }

   private ServerAddress createServerAddress(String group) {
      Setting setting = this.checkSetting();
      if (group == null) {
         group = "";
      }

      String tmpHost = setting.getByGroup("host", group);
      if (StrUtil.isBlank(tmpHost)) {
         throw new NotInitedException("Host name is empy of group: {}", new Object[]{group});
      } else {
         int defaultPort = setting.getInt("port", group, 27017);
         return new ServerAddress(NetUtil.buildInetSocketAddress(tmpHost, defaultPort));
      }
   }

   private ServerAddress createServerAddress(String host, int port) {
      return new ServerAddress(host, port);
   }

   private MongoCredential createCredentail(String group) {
      Setting setting = this.setting;
      if (null == setting) {
         return null;
      } else {
         String user = setting.getStr("user", group, setting.getStr("user"));
         String pass = setting.getStr("pass", group, setting.getStr("pass"));
         String database = setting.getStr("database", group, setting.getStr("database"));
         return this.createCredentail(user, database, pass);
      }
   }

   private MongoCredential createCredentail(String userName, String database, String password) {
      return StrUtil.hasEmpty(new CharSequence[]{userName, database, database}) ? null : MongoCredential.createCredential(userName, database, password.toCharArray());
   }

   private MongoClientSettings.Builder buildMongoClientSettings(MongoClientSettings.Builder builder, String group) {
      if (this.setting == null) {
         return builder;
      } else {
         if (StrUtil.isEmpty(group)) {
            group = "";
         } else {
            group = group + ".";
         }

         Integer connectionsPerHost = this.setting.getInt(group + "connectionsPerHost");
         if (!StrUtil.isBlank(group) && connectionsPerHost == null) {
            connectionsPerHost = this.setting.getInt("connectionsPerHost");
         }

         ConnectionPoolSettings.Builder connectionPoolSettingsBuilder = ConnectionPoolSettings.builder();
         if (connectionsPerHost != null) {
            connectionPoolSettingsBuilder.maxSize(connectionsPerHost);
            log.debug("MongoDB connectionsPerHost: {}", new Object[]{connectionsPerHost});
         }

         Integer connectTimeout = this.setting.getInt(group + "connectTimeout");
         if (!StrUtil.isBlank(group) && connectTimeout == null) {
            this.setting.getInt("connectTimeout");
         }

         if (connectTimeout != null) {
            connectionPoolSettingsBuilder.maxWaitTime((long)connectTimeout, TimeUnit.MILLISECONDS);
            log.debug("MongoDB connectTimeout: {}", new Object[]{connectTimeout});
         }

         builder.applyToConnectionPoolSettings((b) -> {
            b.applySettings(connectionPoolSettingsBuilder.build());
         });
         Integer socketTimeout = this.setting.getInt(group + "socketTimeout");
         if (!StrUtil.isBlank(group) && socketTimeout == null) {
            this.setting.getInt("socketTimeout");
         }

         if (socketTimeout != null) {
            SocketSettings socketSettings = SocketSettings.builder().connectTimeout(socketTimeout, TimeUnit.MILLISECONDS).build();
            builder.applyToSocketSettings((b) -> {
               b.applySettings(socketSettings);
            });
            log.debug("MongoDB socketTimeout: {}", new Object[]{socketTimeout});
         }

         return builder;
      }
   }

   private Setting checkSetting() {
      if (null == this.setting) {
         throw new DbRuntimeException("Please indicate setting file or create default [{}]", new Object[]{"config/mongo.setting"});
      } else {
         return this.setting;
      }
   }
}
