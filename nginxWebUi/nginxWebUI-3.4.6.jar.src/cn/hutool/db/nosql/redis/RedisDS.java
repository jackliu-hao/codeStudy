/*     */ package cn.hutool.db.nosql.redis;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.setting.Setting;
/*     */ import java.io.Closeable;
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
/*     */ import redis.clients.jedis.Jedis;
/*     */ import redis.clients.jedis.JedisPool;
/*     */ import redis.clients.jedis.JedisPoolConfig;
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
/*     */ public class RedisDS
/*     */   implements Closeable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -5605411972456177456L;
/*     */   public static final String REDIS_CONFIG_PATH = "config/redis.setting";
/*     */   private Setting setting;
/*     */   private JedisPool pool;
/*     */   
/*     */   public static RedisDS create() {
/*  37 */     return new RedisDS();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RedisDS create(String group) {
/*  47 */     return new RedisDS(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RedisDS create(Setting setting, String group) {
/*  58 */     return new RedisDS(setting, group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RedisDS() {
/*  66 */     this(null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RedisDS(String group) {
/*  75 */     this(null, group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RedisDS(Setting setting, String group) {
/*  85 */     this.setting = setting;
/*  86 */     init(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RedisDS init(String group) {
/*  96 */     if (null == this.setting) {
/*  97 */       this.setting = new Setting("config/redis.setting", true);
/*     */     }
/*     */     
/* 100 */     JedisPoolConfig config = new JedisPoolConfig();
/*     */     
/* 102 */     this.setting.toBean(config);
/* 103 */     if (StrUtil.isNotBlank(group))
/*     */     {
/* 105 */       this.setting.toBean(group, config);
/*     */     }
/*     */ 
/*     */     
/* 109 */     Long maxWaitMillis = this.setting.getLong("maxWaitMillis");
/* 110 */     if (null != maxWaitMillis)
/*     */     {
/* 112 */       config.setMaxWaitMillis(maxWaitMillis.longValue());
/*     */     }
/*     */     
/* 115 */     this
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
/* 131 */       .pool = new JedisPool((GenericObjectPoolConfig)config, this.setting.getStr("host", group, "127.0.0.1"), this.setting.getInt("port", group, Integer.valueOf(6379)).intValue(), this.setting.getInt("connectionTimeout", group, this.setting.getInt("timeout", group, Integer.valueOf(2000))).intValue(), this.setting.getInt("soTimeout", group, this.setting.getInt("timeout", group, Integer.valueOf(2000))).intValue(), this.setting.getStr("password", group, null), this.setting.getInt("database", group, Integer.valueOf(0)).intValue(), this.setting.getStr("clientName", group, "Hutool"), this.setting.getBool("ssl", group, Boolean.valueOf(false)).booleanValue(), null, null, null);
/*     */ 
/*     */ 
/*     */     
/* 135 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Jedis getJedis() {
/* 144 */     return this.pool.getResource();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStr(String key) {
/* 154 */     try (Jedis jedis = getJedis()) {
/* 155 */       return jedis.get(key);
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
/*     */   public String setStr(String key, String value) {
/* 167 */     try (Jedis jedis = getJedis()) {
/* 168 */       return jedis.set(key, value);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long del(String... keys) {
/* 179 */     try (Jedis jedis = getJedis()) {
/* 180 */       return Long.valueOf(jedis.del(keys));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 186 */     IoUtil.close((Closeable)this.pool);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\nosql\redis\RedisDS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */