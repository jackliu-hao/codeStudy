package cn.hutool.db.ds.pooled;

import cn.hutool.core.map.MapUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.setting.dialect.Props;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PooledConnection extends ConnectionWraper {
   private final PooledDataSource ds;
   private boolean isClosed;

   public PooledConnection(PooledDataSource ds) throws SQLException {
      this.ds = ds;
      DbConfig config = ds.getConfig();
      Props info = new Props();
      String user = config.getUser();
      if (user != null) {
         info.setProperty("user", user);
      }

      String password = config.getPass();
      if (password != null) {
         info.setProperty("password", password);
      }

      Properties connProps = config.getConnProps();
      if (MapUtil.isNotEmpty(connProps)) {
         info.putAll(connProps);
      }

      this.raw = DriverManager.getConnection(config.getUrl(), info);
   }

   public PooledConnection(PooledDataSource ds, Connection conn) {
      this.ds = ds;
      this.raw = conn;
   }

   public void close() {
      this.ds.free(this);
      this.isClosed = true;
   }

   public boolean isClosed() throws SQLException {
      return this.isClosed || this.raw.isClosed();
   }

   protected PooledConnection open() {
      this.isClosed = false;
      return this;
   }

   protected PooledConnection release() {
      DbUtil.close(this.raw);
      return this;
   }
}
