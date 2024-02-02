package cn.hutool.db.nosql.mongo;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.setting.Setting;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoFactory {
   private static final String GROUP_SEPRATER = ",";
   private static final Map<String, MongoDS> DS_MAP = new ConcurrentHashMap();

   public static MongoDS getDS(String host, int port) {
      String key = host + ":" + port;
      MongoDS ds = (MongoDS)DS_MAP.get(key);
      if (null == ds) {
         ds = new MongoDS(host, port);
         DS_MAP.put(key, ds);
      }

      return ds;
   }

   public static MongoDS getDS(String... groups) {
      String key = ArrayUtil.join((Object[])groups, ",");
      MongoDS ds = (MongoDS)DS_MAP.get(key);
      if (null == ds) {
         ds = new MongoDS(groups);
         DS_MAP.put(key, ds);
      }

      return ds;
   }

   public static MongoDS getDS(Collection<String> groups) {
      return getDS((String[])groups.toArray(new String[0]));
   }

   public static MongoDS getDS(Setting setting, String... groups) {
      String key = setting.getSettingPath() + "," + ArrayUtil.join((Object[])groups, ",");
      MongoDS ds = (MongoDS)DS_MAP.get(key);
      if (null == ds) {
         ds = new MongoDS(setting, groups);
         DS_MAP.put(key, ds);
      }

      return ds;
   }

   public static MongoDS getDS(Setting setting, Collection<String> groups) {
      return getDS(setting, (String[])groups.toArray(new String[0]));
   }

   public static void closeAll() {
      if (MapUtil.isNotEmpty(DS_MAP)) {
         Iterator var0 = DS_MAP.values().iterator();

         while(var0.hasNext()) {
            MongoDS ds = (MongoDS)var0.next();
            ds.close();
         }

         DS_MAP.clear();
      }

   }

   static {
      RuntimeUtil.addShutdownHook(MongoFactory::closeAll);
   }
}
