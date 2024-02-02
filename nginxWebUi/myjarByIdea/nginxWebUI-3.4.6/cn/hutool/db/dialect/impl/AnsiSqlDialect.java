package cn.hutool.db.dialect.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.StatementUtil;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectName;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.Wrapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class AnsiSqlDialect implements Dialect {
   private static final long serialVersionUID = 2088101129774974580L;
   protected Wrapper wrapper = new Wrapper();

   public Wrapper getWrapper() {
      return this.wrapper;
   }

   public void setWrapper(Wrapper wrapper) {
      this.wrapper = wrapper;
   }

   public PreparedStatement psForInsert(Connection conn, Entity entity) throws SQLException {
      SqlBuilder insert = SqlBuilder.create(this.wrapper).insert(entity, this.dialectName());
      return StatementUtil.prepareStatement(conn, insert);
   }

   public PreparedStatement psForInsertBatch(Connection conn, Entity... entities) throws SQLException {
      if (ArrayUtil.isEmpty((Object[])entities)) {
         throw new DbRuntimeException("Entities for batch insert is empty !");
      } else {
         SqlBuilder insert = SqlBuilder.create(this.wrapper).insert(entities[0], this.dialectName());
         Set<String> fields = (Set)CollUtil.filter(entities[0].keySet(), CharSequenceUtil::isNotBlank);
         return StatementUtil.prepareStatementForBatch(conn, insert.build(), fields, entities);
      }
   }

   public PreparedStatement psForDelete(Connection conn, Query query) throws SQLException {
      Assert.notNull(query, "query must be not null !");
      Condition[] where = query.getWhere();
      if (ArrayUtil.isEmpty((Object[])where)) {
         throw new SQLException("No 'WHERE' condition, we can't prepared statement for delete everything.");
      } else {
         SqlBuilder delete = SqlBuilder.create(this.wrapper).delete(query.getFirstTableName()).where(where);
         return StatementUtil.prepareStatement(conn, delete);
      }
   }

   public PreparedStatement psForUpdate(Connection conn, Entity entity, Query query) throws SQLException {
      Assert.notNull(query, "query must be not null !");
      Condition[] where = query.getWhere();
      if (ArrayUtil.isEmpty((Object[])where)) {
         throw new SQLException("No 'WHERE' condition, we can't prepare statement for update everything.");
      } else {
         SqlBuilder update = SqlBuilder.create(this.wrapper).update(entity).where(where);
         return StatementUtil.prepareStatement(conn, update);
      }
   }

   public PreparedStatement psForFind(Connection conn, Query query) throws SQLException {
      return this.psForPage(conn, query);
   }

   public PreparedStatement psForPage(Connection conn, Query query) throws SQLException {
      Assert.notNull(query, "query must be not null !");
      if (StrUtil.hasBlank(query.getTableNames())) {
         throw new DbRuntimeException("Table name must be not empty !");
      } else {
         SqlBuilder find = SqlBuilder.create(this.wrapper).query(query);
         return this.psForPage(conn, find, query.getPage());
      }
   }

   public PreparedStatement psForPage(Connection conn, SqlBuilder sqlBuilder, Page page) throws SQLException {
      if (null != page) {
         sqlBuilder = this.wrapPageSql(sqlBuilder.orderBy(page.getOrders()), page);
      }

      return StatementUtil.prepareStatement(conn, sqlBuilder);
   }

   protected SqlBuilder wrapPageSql(SqlBuilder find, Page page) {
      return find.append(" limit ").append(page.getPageSize()).append(" offset ").append(page.getStartPosition());
   }

   public String dialectName() {
      return DialectName.ANSI.name();
   }
}
