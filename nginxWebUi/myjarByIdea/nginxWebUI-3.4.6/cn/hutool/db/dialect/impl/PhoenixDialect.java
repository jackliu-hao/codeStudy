package cn.hutool.db.dialect.impl;

import cn.hutool.db.Entity;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.Query;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PhoenixDialect extends AnsiSqlDialect {
   private static final long serialVersionUID = 1L;

   public PreparedStatement psForUpdate(Connection conn, Entity entity, Query query) throws SQLException {
      return super.psForInsert(conn, entity);
   }

   public String dialectName() {
      return DialectName.PHOENIX.name();
   }

   public PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
      return this.psForInsert(conn, entity);
   }
}
