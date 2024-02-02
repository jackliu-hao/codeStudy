package cn.hutool.db.ds.jndi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.DbUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import javax.sql.DataSource;

public class JndiDSFactory extends AbstractDSFactory {
   private static final long serialVersionUID = 1573625812927370432L;
   public static final String DS_NAME = "JNDI DataSource";

   public JndiDSFactory() {
      this((Setting)null);
   }

   public JndiDSFactory(Setting setting) {
      super("JNDI DataSource", (Class)null, setting);
   }

   protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
      String jndiName = poolSetting.getStr("jndi");
      if (StrUtil.isEmpty(jndiName)) {
         throw new DbRuntimeException("No setting name [jndi] for this group.");
      } else {
         return DbUtil.getJndiDs(jndiName);
      }
   }
}
