package cn.hutool.db.ds.hikari;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class HikariDSFactory extends AbstractDSFactory {
   private static final long serialVersionUID = -8834744983614749401L;
   public static final String DS_NAME = "HikariCP";

   public HikariDSFactory() {
      this((Setting)null);
   }

   public HikariDSFactory(Setting setting) {
      super("HikariCP", HikariDataSource.class, setting);
   }

   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
      Props connProps = new Props();
      String[] var8 = KEY_CONN_PROPS;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String key = var8[var10];
         String connValue = poolSetting.getAndRemoveStr(key);
         if (StrUtil.isNotBlank(connValue)) {
            connProps.setProperty(key, connValue);
         }
      }

      Props config = new Props();
      config.putAll(poolSetting);
      config.put("jdbcUrl", jdbcUrl);
      if (null != driver) {
         config.put("driverClassName", driver);
      }

      if (null != user) {
         config.put("username", user);
      }

      if (null != pass) {
         config.put("password", pass);
      }

      HikariConfig hikariConfig = new HikariConfig(config);
      hikariConfig.setDataSourceProperties(connProps);
      return new HikariDataSource(hikariConfig);
   }
}
