package cn.hutool.db.ds.bee;

import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import javax.sql.DataSource;

public class BeeDSFactory extends AbstractDSFactory {
   private static final long serialVersionUID = 1L;
   public static final String DS_NAME = "BeeCP";

   public BeeDSFactory() {
      this((Setting)null);
   }

   public BeeDSFactory(Setting setting) {
      super("BeeCP", BeeDataSource.class, setting);
   }

   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
      BeeDataSourceConfig beeConfig = new BeeDataSourceConfig(driver, jdbcUrl, user, pass);
      poolSetting.toBean(beeConfig);
      String[] var8 = KEY_CONN_PROPS;
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String key = var8[var10];
         String connValue = poolSetting.getAndRemoveStr(key);
         if (StrUtil.isNotBlank(connValue)) {
            beeConfig.addConnectProperty(key, connValue);
         }
      }

      return new BeeDataSource(beeConfig);
   }
}
