package cn.hutool.db.ds.tomcat;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class TomcatDSFactory extends AbstractDSFactory {
   private static final long serialVersionUID = 4925514193275150156L;
   public static final String DS_NAME = "Tomcat-Jdbc-Pool";

   public TomcatDSFactory() {
      this((Setting)null);
   }

   public TomcatDSFactory(Setting setting) {
      super("Tomcat-Jdbc-Pool", DataSource.class, setting);
   }

   protected javax.sql.DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
      PoolProperties poolProps = new PoolProperties();
      poolProps.setUrl(jdbcUrl);
      poolProps.setDriverClassName(driver);
      poolProps.setUsername(user);
      poolProps.setPassword(pass);
      Props connProps = new Props();
      String[] var9 = KEY_CONN_PROPS;
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         String key = var9[var11];
         String connValue = poolSetting.getAndRemoveStr(key);
         if (StrUtil.isNotBlank(connValue)) {
            connProps.setProperty(key, connValue);
         }
      }

      poolProps.setDbProperties(connProps);
      poolSetting.toBean(poolProps);
      return new DataSource(poolProps);
   }
}
