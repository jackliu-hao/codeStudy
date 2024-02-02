/*     */ package cn.hutool.db.nosql.mongo;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.RuntimeUtil;
/*     */ import cn.hutool.setting.Setting;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class MongoFactory
/*     */ {
/*     */   private static final String GROUP_SEPRATER = ",";
/*  27 */   private static final Map<String, MongoDS> DS_MAP = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*     */   static {
/*  31 */     RuntimeUtil.addShutdownHook(MongoFactory::closeAll);
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
/*     */   public static MongoDS getDS(String host, int port) {
/*  44 */     String key = host + ":" + port;
/*  45 */     MongoDS ds = DS_MAP.get(key);
/*  46 */     if (null == ds) {
/*     */       
/*  48 */       ds = new MongoDS(host, port);
/*  49 */       DS_MAP.put(key, ds);
/*     */     } 
/*     */     
/*  52 */     return ds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MongoDS getDS(String... groups) {
/*  63 */     String key = ArrayUtil.join((Object[])groups, ",");
/*  64 */     MongoDS ds = DS_MAP.get(key);
/*  65 */     if (null == ds) {
/*     */       
/*  67 */       ds = new MongoDS(groups);
/*  68 */       DS_MAP.put(key, ds);
/*     */     } 
/*     */     
/*  71 */     return ds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MongoDS getDS(Collection<String> groups) {
/*  81 */     return getDS(groups.<String>toArray(new String[0]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MongoDS getDS(Setting setting, String... groups) {
/*  92 */     String key = setting.getSettingPath() + "," + ArrayUtil.join((Object[])groups, ",");
/*  93 */     MongoDS ds = DS_MAP.get(key);
/*  94 */     if (null == ds) {
/*     */       
/*  96 */       ds = new MongoDS(setting, groups);
/*  97 */       DS_MAP.put(key, ds);
/*     */     } 
/*     */     
/* 100 */     return ds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MongoDS getDS(Setting setting, Collection<String> groups) {
/* 111 */     return getDS(setting, groups.<String>toArray(new String[0]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeAll() {
/* 119 */     if (MapUtil.isNotEmpty(DS_MAP)) {
/* 120 */       for (MongoDS ds : DS_MAP.values()) {
/* 121 */         ds.close();
/*     */       }
/* 123 */       DS_MAP.clear();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\nosql\mongo\MongoFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */