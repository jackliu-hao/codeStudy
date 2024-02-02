package cn.hutool.db.ds.pooled;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import javax.sql.DataSource;

public class PooledDSFactory extends AbstractDSFactory {
   private static final long serialVersionUID = 8093886210895248277L;
   public static final String DS_NAME = "Hutool-Pooled-DataSource";

   public PooledDSFactory() {
      this((Setting)null);
   }

   public PooledDSFactory(Setting setting) {
      super("Hutool-Pooled-DataSource", PooledDataSource.class, setting);
   }

   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
      DbConfig dbConfig = new DbConfig();
      dbConfig.setUrl(jdbcUrl);
      dbConfig.setDriver(driver);
      dbConfig.setUser(user);
      dbConfig.setPass(pass);
      dbConfig.setInitialSize(poolSetting.getInt("initialSize", 0));
      dbConfig.setMinIdle(poolSetting.getInt("minIdle", 0));
      dbConfig.setMaxActive(poolSetting.getInt("maxActive", 8));
      dbConfig.setMaxWait(poolSetting.getLong("maxWait", 6000L));
      String[] var8 = KEY_CONN_PROPS;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String key = var8[var10];
         String connValue = poolSetting.get(key);
         if (StrUtil.isNotBlank(connValue)) {
            dbConfig.addConnProps(key, connValue);
         }
      }

      return new PooledDataSource(dbConfig);
   }
}
