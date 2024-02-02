package cn.hutool.db.ds.dbcp;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

public class DbcpDSFactory extends AbstractDSFactory {
   private static final long serialVersionUID = -9133501414334104548L;
   public static final String DS_NAME = "commons-dbcp2";

   public DbcpDSFactory() {
      this((Setting)null);
   }

   public DbcpDSFactory(Setting setting) {
      super("commons-dbcp2", BasicDataSource.class, setting);
   }

   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
      BasicDataSource ds = new BasicDataSource();
      ds.setUrl(jdbcUrl);
      ds.setDriverClassName(driver);
      ds.setUsername(user);
      ds.setPassword(pass);
      String[] var8 = KEY_CONN_PROPS;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String key = var8[var10];
         String connValue = poolSetting.getAndRemoveStr(key);
         if (StrUtil.isNotBlank(connValue)) {
            ds.addConnectionProperty(key, connValue);
         }
      }

      poolSetting.toBean(ds);
      return ds;
   }
}
