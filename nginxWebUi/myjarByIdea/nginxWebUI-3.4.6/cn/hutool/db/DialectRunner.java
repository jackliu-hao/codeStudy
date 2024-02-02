package cn.hutool.db;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectFactory;
import cn.hutool.db.handler.NumberHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.db.sql.SqlUtil;
import cn.hutool.db.sql.Wrapper;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DialectRunner implements Serializable {
   private static final long serialVersionUID = 1L;
   private Dialect dialect;
   protected boolean caseInsensitive;

   public DialectRunner(Dialect dialect) {
      this.caseInsensitive = GlobalDbConfig.caseInsensitive;
      this.dialect = dialect;
   }

   public DialectRunner(String driverClassName) {
      this(DialectFactory.newDialect(driverClassName));
   }

   public int[] insert(Connection conn, Entity... records) throws SQLException {
      this.checkConn(conn);
      if (ArrayUtil.isEmpty((Object[])records)) {
         return new int[]{0};
      } else {
         PreparedStatement ps = null;

         int[] var4;
         try {
            if (1 != records.length) {
               ps = this.dialect.psForInsertBatch(conn, records);
               var4 = ps.executeBatch();
               return var4;
            }

            ps = this.dialect.psForInsert(conn, records[0]);
            var4 = new int[]{ps.executeUpdate()};
         } finally {
            DbUtil.close(ps);
         }

         return var4;
      }
   }

   public int upsert(Connection conn, Entity record, String... keys) throws SQLException {
      PreparedStatement ps = null;

      try {
         ps = this.getDialect().psForUpsert(conn, record, keys);
      } catch (SQLException var10) {
      }

      if (null != ps) {
         int var5;
         try {
            var5 = ps.executeUpdate();
         } finally {
            DbUtil.close(ps);
         }

         return var5;
      } else {
         return this.insertOrUpdate(conn, record, keys);
      }
   }

   public int insertOrUpdate(Connection conn, Entity record, String... keys) throws SQLException {
      Entity where = record.filter(keys);
      return MapUtil.isNotEmpty(where) && this.count(conn, where) > 0L ? this.update(conn, record, where) : this.insert(conn, record)[0];
   }

   public <T> T insert(Connection conn, Entity record, RsHandler<T> generatedKeysHandler) throws SQLException {
      this.checkConn(conn);
      if (MapUtil.isEmpty(record)) {
         throw new SQLException("Empty entity provided!");
      } else {
         PreparedStatement ps = null;

         Object var5;
         try {
            ps = this.dialect.psForInsert(conn, record);
            ps.executeUpdate();
            if (null != generatedKeysHandler) {
               var5 = StatementUtil.getGeneratedKeys(ps, generatedKeysHandler);
               return var5;
            }

            var5 = null;
         } finally {
            DbUtil.close(ps);
         }

         return var5;
      }
   }

   public int del(Connection conn, Entity where) throws SQLException {
      this.checkConn(conn);
      if (MapUtil.isEmpty(where)) {
         throw new SQLException("Empty entity provided!");
      } else {
         PreparedStatement ps = null;

         int var4;
         try {
            ps = this.dialect.psForDelete(conn, Query.of(where));
            var4 = ps.executeUpdate();
         } finally {
            DbUtil.close(ps);
         }

         return var4;
      }
   }

   public int update(Connection conn, Entity record, Entity where) throws SQLException {
      this.checkConn(conn);
      if (MapUtil.isEmpty(record)) {
         throw new SQLException("Empty entity provided!");
      } else if (MapUtil.isEmpty(where)) {
         throw new SQLException("Empty where provided!");
      } else {
         String tableName = record.getTableName();
         if (StrUtil.isBlank(tableName)) {
            tableName = where.getTableName();
            record.setTableName(tableName);
         }

         Query query = new Query(SqlUtil.buildConditions(where), new String[]{tableName});
         PreparedStatement ps = null;

         int var7;
         try {
            ps = this.dialect.psForUpdate(conn, record, query);
            var7 = ps.executeUpdate();
         } finally {
            DbUtil.close(ps);
         }

         return var7;
      }
   }

   public <T> T find(Connection conn, Query query, RsHandler<T> rsh) throws SQLException {
      this.checkConn(conn);
      Assert.notNull(query, "[query] is null !");
      return SqlExecutor.queryAndClosePs(this.dialect.psForFind(conn, query), rsh);
   }

   public long count(Connection conn, Entity where) throws SQLException {
      this.checkConn(conn);
      return ((Number)SqlExecutor.queryAndClosePs(this.dialect.psForCount(conn, Query.of(where)), new NumberHandler())).longValue();
   }

   public long count(Connection conn, SqlBuilder sqlBuilder) throws SQLException {
      this.checkConn(conn);
      String selectSql = sqlBuilder.build();
      int orderByIndex = StrUtil.indexOfIgnoreCase(selectSql, " order by");
      if (orderByIndex > 0) {
         selectSql = StrUtil.subPre(selectSql, orderByIndex);
      }

      return ((Number)SqlExecutor.queryAndClosePs(this.dialect.psForCount(conn, SqlBuilder.of(selectSql).addParams(sqlBuilder.getParamValueArray())), new NumberHandler())).longValue();
   }

   public <T> T page(Connection conn, Query query, RsHandler<T> rsh) throws SQLException {
      this.checkConn(conn);
      return null == query.getPage() ? this.find(conn, query, rsh) : SqlExecutor.queryAndClosePs(this.dialect.psForPage(conn, query), rsh);
   }

   public <T> T page(Connection conn, SqlBuilder sqlBuilder, Page page, RsHandler<T> rsh) throws SQLException {
      this.checkConn(conn);
      return null == page ? SqlExecutor.query(conn, sqlBuilder, rsh) : SqlExecutor.queryAndClosePs(this.dialect.psForPage(conn, sqlBuilder, page), rsh);
   }

   public void setCaseInsensitive(boolean caseInsensitive) {
      this.caseInsensitive = caseInsensitive;
   }

   public Dialect getDialect() {
      return this.dialect;
   }

   public void setDialect(Dialect dialect) {
      this.dialect = dialect;
   }

   public void setWrapper(Character wrapperChar) {
      this.setWrapper(new Wrapper(wrapperChar));
   }

   public void setWrapper(Wrapper wrapper) {
      this.dialect.setWrapper(wrapper);
   }

   private void checkConn(Connection conn) {
      Assert.notNull(conn, "Connection object must be not null!");
   }
}
