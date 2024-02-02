package cn.hutool.db.dialect;

import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Dialect extends Serializable {
   Wrapper getWrapper();

   void setWrapper(Wrapper var1);

   PreparedStatement psForInsert(Connection var1, Entity var2) throws SQLException;

   PreparedStatement psForInsertBatch(Connection var1, Entity... var2) throws SQLException;

   PreparedStatement psForDelete(Connection var1, Query var2) throws SQLException;

   PreparedStatement psForUpdate(Connection var1, Entity var2, Query var3) throws SQLException;

   PreparedStatement psForFind(Connection var1, Query var2) throws SQLException;

   PreparedStatement psForPage(Connection var1, Query var2) throws SQLException;

   PreparedStatement psForPage(Connection var1, SqlBuilder var2, Page var3) throws SQLException;

   default PreparedStatement psForCount(Connection conn, Query query) throws SQLException {
      return this.psForCount(conn, SqlBuilder.create().query(query));
   }

   default PreparedStatement psForCount(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
      sqlBuilder = sqlBuilder.insertPreFragment("SELECT count(1) from(").append(") hutool_alias_count_");
      return this.psForPage(conn, sqlBuilder, (Page)null);
   }

   default PreparedStatement psForUpsert(Connection conn, Entity entity, String... keys) throws SQLException {
      throw new SQLException("Unsupported upsert operation of " + this.dialectName());
   }

   String dialectName();
}
