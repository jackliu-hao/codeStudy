package cn.hutool.db.ds;

import cn.hutool.db.ds.bee.BeeDSFactory;
import cn.hutool.db.ds.c3p0.C3p0DSFactory;
import cn.hutool.db.ds.dbcp.DbcpDSFactory;
import cn.hutool.db.ds.druid.DruidDSFactory;
import cn.hutool.db.ds.hikari.HikariDSFactory;
import cn.hutool.db.ds.pooled.PooledDSFactory;
import cn.hutool.db.ds.tomcat.TomcatDSFactory;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import java.io.Closeable;
import java.io.Serializable;
import javax.sql.DataSource;

public abstract class DSFactory implements Closeable, Serializable {
   private static final long serialVersionUID = -8789780234095234765L;
   private static final Log log = LogFactory.get();
   public static final String[] KEY_CONN_PROPS = new String[]{"remarks", "useInformationSchema"};
   public static final String[] KEY_ALIAS_URL = new String[]{"url", "jdbcUrl"};
   public static final String[] KEY_ALIAS_DRIVER = new String[]{"driver", "driverClassName"};
   public static final String[] KEY_ALIAS_USER = new String[]{"user", "username"};
   public static final String[] KEY_ALIAS_PASSWORD = new String[]{"pass", "password"};
   protected final String dataSourceName;

   public DSFactory(String dataSourceName) {
      this.dataSourceName = dataSourceName;
   }

   public DataSource getDataSource() {
      return this.getDataSource("");
   }

   public abstract DataSource getDataSource(String var1);

   public void close() {
      this.close("");
   }

   public abstract void close(String var1);

   public abstract void destroy();

   public static DataSource get() {
      return get((String)null);
   }

   public static DataSource get(String group) {
      return GlobalDSFactory.get().getDataSource(group);
   }

   public static DSFactory setCurrentDSFactory(DSFactory dsFactory) {
      return GlobalDSFactory.set(dsFactory);
   }

   public static DSFactory create(Setting setting) {
      DSFactory dsFactory = doCreate(setting);
      log.debug("Use [{}] DataSource As Default", new Object[]{dsFactory.dataSourceName});
      return dsFactory;
   }

   private static DSFactory doCreate(Setting setting) {
      try {
         return new HikariDSFactory(setting);
      } catch (NoClassDefFoundError var7) {
         try {
            return new DruidDSFactory(setting);
         } catch (NoClassDefFoundError var6) {
            try {
               return new TomcatDSFactory(setting);
            } catch (NoClassDefFoundError var5) {
               try {
                  return new BeeDSFactory(setting);
               } catch (NoClassDefFoundError var4) {
                  try {
                     return new DbcpDSFactory(setting);
                  } catch (NoClassDefFoundError var3) {
                     try {
                        return new C3p0DSFactory(setting);
                     } catch (NoClassDefFoundError var2) {
                        return new PooledDSFactory(setting);
                     }
                  }
               }
            }
         }
      }
   }
}
