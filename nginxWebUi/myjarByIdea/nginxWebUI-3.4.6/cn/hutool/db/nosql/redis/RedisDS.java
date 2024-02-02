package cn.hutool.db.nosql.redis;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import java.io.Closeable;
import java.io.Serializable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisDS implements Closeable, Serializable {
   private static final long serialVersionUID = -5605411972456177456L;
   public static final String REDIS_CONFIG_PATH = "config/redis.setting";
   private Setting setting;
   private JedisPool pool;

   public static RedisDS create() {
      return new RedisDS();
   }

   public static RedisDS create(String group) {
      return new RedisDS(group);
   }

   public static RedisDS create(Setting setting, String group) {
      return new RedisDS(setting, group);
   }

   public RedisDS() {
      this((Setting)null, (String)null);
   }

   public RedisDS(String group) {
      this((Setting)null, group);
   }

   public RedisDS(Setting setting, String group) {
      this.setting = setting;
      this.init(group);
   }

   public RedisDS init(String group) {
      if (null == this.setting) {
         this.setting = new Setting("config/redis.setting", true);
      }

      JedisPoolConfig config = new JedisPoolConfig();
      this.setting.toBean(config);
      if (StrUtil.isNotBlank(group)) {
         this.setting.toBean(group, config);
      }

      Long maxWaitMillis = this.setting.getLong("maxWaitMillis");
      if (null != maxWaitMillis) {
         config.setMaxWaitMillis(maxWaitMillis);
      }

      this.pool = new JedisPool(config, this.setting.getStr("host", group, "127.0.0.1"), this.setting.getInt("port", group, 6379), this.setting.getInt("connectionTimeout", group, this.setting.getInt("timeout", group, 2000)), this.setting.getInt("soTimeout", group, this.setting.getInt("timeout", group, 2000)), this.setting.getStr("password", group, (String)null), this.setting.getInt("database", group, 0), this.setting.getStr("clientName", group, "Hutool"), this.setting.getBool("ssl", group, false), (SSLSocketFactory)null, (SSLParameters)null, (HostnameVerifier)null);
      return this;
   }

   public Jedis getJedis() {
      return this.pool.getResource();
   }

   public String getStr(String key) {
      Jedis jedis = this.getJedis();
      Throwable var3 = null;

      String var4;
      try {
         var4 = jedis.get(key);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (jedis != null) {
            if (var3 != null) {
               try {
                  jedis.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               jedis.close();
            }
         }

      }

      return var4;
   }

   public String setStr(String key, String value) {
      Jedis jedis = this.getJedis();
      Throwable var4 = null;

      String var5;
      try {
         var5 = jedis.set(key, value);
      } catch (Throwable var14) {
         var4 = var14;
         throw var14;
      } finally {
         if (jedis != null) {
            if (var4 != null) {
               try {
                  jedis.close();
               } catch (Throwable var13) {
                  var4.addSuppressed(var13);
               }
            } else {
               jedis.close();
            }
         }

      }

      return var5;
   }

   public Long del(String... keys) {
      Jedis jedis = this.getJedis();
      Throwable var3 = null;

      Long var4;
      try {
         var4 = jedis.del(keys);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (jedis != null) {
            if (var3 != null) {
               try {
                  jedis.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               jedis.close();
            }
         }

      }

      return var4;
   }

   public void close() {
      IoUtil.close(this.pool);
   }
}
