package cn.hutool.db.ds.druid;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;

public class DruidDSFactory extends AbstractDSFactory {
   private static final long serialVersionUID = 4680621702534433222L;
   public static final String DS_NAME = "Druid";

   public DruidDSFactory() {
      this((Setting)null);
   }

   public DruidDSFactory(Setting setting) {
      super("Druid", DruidDataSource.class, setting);
   }

   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
      DruidDataSource ds = new DruidDataSource();
      ds.setUrl(jdbcUrl);
      ds.setDriverClassName(driver);
      ds.setUsername(user);
      ds.setPassword(pass);
      String[] var8 = KEY_CONN_PROPS;
      int var9 = var8.length;

      String timeBetweenConnectErrorMillisKey;
      for(int var10 = 0; var10 < var9; ++var10) {
         timeBetweenConnectErrorMillisKey = var8[var10];
         String connValue = poolSetting.getAndRemoveStr(timeBetweenConnectErrorMillisKey);
         if (StrUtil.isNotBlank(connValue)) {
            ds.addConnectionProperty(timeBetweenConnectErrorMillisKey, connValue);
         }
      }

      Props druidProps = new Props();
      poolSetting.forEach((key, value) -> {
         druidProps.put(StrUtil.addPrefixIfNot(key, "druid."), value);
      });
      ds.configFromPropety(druidProps);
      String[] var10000 = new String[]{"druid.connectionErrorRetryAttempts", "druid.breakAfterAcquireFailure"};
      String connectionErrorRetryAttemptsKey = "druid.connectionErrorRetryAttempts";
      if (druidProps.containsKey("druid.connectionErrorRetryAttempts")) {
         ds.setConnectionErrorRetryAttempts(druidProps.getInt("druid.connectionErrorRetryAttempts"));
      }

      timeBetweenConnectErrorMillisKey = "druid.timeBetweenConnectErrorMillis";
      if (druidProps.containsKey("druid.timeBetweenConnectErrorMillis")) {
         ds.setTimeBetweenConnectErrorMillis((long)druidProps.getInt("druid.timeBetweenConnectErrorMillis"));
      }

      String breakAfterAcquireFailureKey = "druid.breakAfterAcquireFailure";
      if (druidProps.containsKey("druid.breakAfterAcquireFailure")) {
         ds.setBreakAfterAcquireFailure(druidProps.getBool("druid.breakAfterAcquireFailure"));
      }

      if (null == ds.getValidationQuery()) {
         ds.setTestOnBorrow(false);
         ds.setTestOnReturn(false);
         ds.setTestWhileIdle(false);
      }

      return ds;
   }
}
