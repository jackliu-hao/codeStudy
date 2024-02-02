package cn.hutool.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.dialect.DialectFactory;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.handler.HandleHelper;
import cn.hutool.db.handler.PageResultHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlUtil;
import java.lang.invoke.SerializedLambda;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

public class SqlConnRunner extends DialectRunner {
   private static final long serialVersionUID = 1L;

   public static SqlConnRunner create(Dialect dialect) {
      return new SqlConnRunner(dialect);
   }

   public static SqlConnRunner create(DataSource ds) {
      return new SqlConnRunner(DialectFactory.getDialect(ds));
   }

   public static SqlConnRunner create(String driverClassName) {
      return new SqlConnRunner(driverClassName);
   }

   public SqlConnRunner(Dialect dialect) {
      super(dialect);
   }

   public SqlConnRunner(String driverClassName) {
      super(driverClassName);
   }

   public int[] insert(Connection conn, Collection<Entity> records) throws SQLException {
      return this.insert(conn, (Entity[])((Entity[])records.toArray(new Entity[0])));
   }

   public int insert(Connection conn, Entity record) throws SQLException {
      return this.insert(conn, (Entity[])(new Entity[]{record}))[0];
   }

   public List<Object> insertForGeneratedKeys(Connection conn, Entity record) throws SQLException {
      return (List)this.insert(conn, record, HandleHelper::handleRowToList);
   }

   public Long insertForGeneratedKey(Connection conn, Entity record) throws SQLException {
      return (Long)this.insert(conn, record, (rs) -> {
         Long generatedKey = null;
         if (rs != null && rs.next()) {
            try {
               generatedKey = rs.getLong(1);
            } catch (SQLException var3) {
            }
         }

         return generatedKey;
      });
   }

   public <T> T find(Connection conn, Collection<String> fields, Entity where, RsHandler<T> rsh) throws SQLException {
      return this.find(conn, Query.of(where).setFields(fields), rsh);
   }

   public <T> T find(Connection conn, Entity where, RsHandler<T> rsh, String... fields) throws SQLException {
      return this.find(conn, (Collection)CollUtil.newArrayList((Object[])fields), (Entity)where, (RsHandler)rsh);
   }

   public List<Entity> find(Connection conn, Entity where) throws SQLException {
      return (List)this.find(conn, (Collection)where.getFieldNames(), (Entity)where, (RsHandler)(new EntityListHandler(this.caseInsensitive)));
   }

   public List<Entity> findAll(Connection conn, Entity where) throws SQLException {
      return (List)this.find(conn, (Entity)where, (RsHandler)(new EntityListHandler(this.caseInsensitive)), (String[])());
   }

   public List<Entity> findAll(Connection conn, String tableName) throws SQLException {
      return this.findAll(conn, Entity.create(tableName));
   }

   public List<Entity> findBy(Connection conn, String tableName, String field, Object value) throws SQLException {
      return this.findAll(conn, Entity.create(tableName).set(field, value));
   }

   public List<Entity> findLike(Connection conn, String tableName, String field, String value, Condition.LikeType likeType) throws SQLException {
      return this.findAll(conn, Entity.create(tableName).set(field, SqlUtil.buildLikeValue(value, likeType, true)));
   }

   public List<Entity> findIn(Connection conn, String tableName, String field, Object... values) throws SQLException {
      return this.findAll(conn, Entity.create(tableName).set(field, values));
   }

   public long count(Connection conn, CharSequence selectSql, Object... params) throws SQLException {
      return this.count(conn, SqlBuilder.of(selectSql).addParams(params));
   }

   public <T> T page(Connection conn, Collection<String> fields, Entity where, int pageNumber, int numPerPage, RsHandler<T> rsh) throws SQLException {
      return this.page(conn, (Query)Query.of(where).setFields(fields).setPage(new Page(pageNumber, numPerPage)), (RsHandler)rsh);
   }

   public PageResult<Entity> page(Connection conn, SqlBuilder sqlBuilder, Page page) throws SQLException {
      PageResultHandler pageResultHandler = new PageResultHandler(new PageResult(page.getPageNumber(), page.getPageSize(), (int)this.count(conn, sqlBuilder)), this.caseInsensitive);
      return (PageResult)this.page(conn, sqlBuilder, page, pageResultHandler);
   }

   public PageResult<Entity> page(Connection conn, Collection<String> fields, Entity where, int page, int numPerPage) throws SQLException {
      return this.page(conn, fields, where, new Page(page, numPerPage));
   }

   public PageResult<Entity> page(Connection conn, Entity where, Page page) throws SQLException {
      return this.page(conn, (Collection)null, where, page);
   }

   public PageResult<Entity> page(Connection conn, Collection<String> fields, Entity where, Page page) throws SQLException {
      PageResultHandler pageResultHandler = new PageResultHandler(new PageResult(page.getPageNumber(), page.getPageSize(), (int)this.count(conn, where)), this.caseInsensitive);
      return (PageResult)this.page(conn, fields, where, page, pageResultHandler);
   }

   public <T> T page(Connection conn, Collection<String> fields, Entity where, Page page, RsHandler<T> handler) throws SQLException {
      return this.page(conn, (Query)Query.of(where).setFields(fields).setPage(page), (RsHandler)handler);
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "handleRowToList":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/db/handler/RsHandler") && lambda.getFunctionalInterfaceMethodName().equals("handle") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/db/handler/HandleHelper") && lambda.getImplMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/util/List;")) {
               return HandleHelper::handleRowToList;
            }
            break;
         case "lambda$insertForGeneratedKey$2dfcceed$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/db/handler/RsHandler") && lambda.getFunctionalInterfaceMethodName().equals("handle") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/db/SqlConnRunner") && lambda.getImplMethodSignature().equals("(Ljava/sql/ResultSet;)Ljava/lang/Long;")) {
               return (rs) -> {
                  Long generatedKey = null;
                  if (rs != null && rs.next()) {
                     try {
                        generatedKey = rs.getLong(1);
                     } catch (SQLException var3) {
                     }
                  }

                  return generatedKey;
               };
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
