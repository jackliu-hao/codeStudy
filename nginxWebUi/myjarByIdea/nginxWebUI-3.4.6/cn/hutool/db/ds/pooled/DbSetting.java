package cn.hutool.db.ds.pooled;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.dialect.DriverUtil;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;

public class DbSetting {
   public static final String DEFAULT_DB_CONFIG_PATH = "config/db.setting";
   private final Setting setting;

   public DbSetting() {
      this((Setting)null);
   }

   public DbSetting(Setting setting) {
      if (null == setting) {
         this.setting = new Setting("config/db.setting");
      } else {
         this.setting = setting;
      }

   }

   public DbConfig getDbConfig(String group) {
      Setting config = this.setting.getSetting(group);
      if (MapUtil.isEmpty(config)) {
         throw new DbRuntimeException("No Hutool pool config for group: [{}]", new Object[]{group});
      } else {
         DbConfig dbConfig = new DbConfig();
         String url = config.getAndRemoveStr(DSFactory.KEY_ALIAS_URL);
         if (StrUtil.isBlank(url)) {
            throw new DbRuntimeException("No JDBC URL for group: [{}]", new Object[]{group});
         } else {
            dbConfig.setUrl(url);
            String driver = config.getAndRemoveStr(DSFactory.KEY_ALIAS_DRIVER);
            dbConfig.setDriver(StrUtil.isNotBlank(driver) ? driver : DriverUtil.identifyDriver(url));
            dbConfig.setUser(config.getAndRemoveStr(DSFactory.KEY_ALIAS_USER));
            dbConfig.setPass(config.getAndRemoveStr(DSFactory.KEY_ALIAS_PASSWORD));
            dbConfig.setInitialSize(this.setting.getInt("initialSize", group, 0));
            dbConfig.setMinIdle(this.setting.getInt("minIdle", group, 0));
            dbConfig.setMaxActive(this.setting.getInt("maxActive", group, 8));
            dbConfig.setMaxWait(this.setting.getLong("maxWait", group, 6000L));
            String[] var7 = DSFactory.KEY_CONN_PROPS;
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               String key = var7[var9];
               String connValue = config.get(key);
               if (StrUtil.isNotBlank(connValue)) {
                  dbConfig.addConnProps(key, connValue);
               }
            }

            return dbConfig;
         }
      }
   }
}
