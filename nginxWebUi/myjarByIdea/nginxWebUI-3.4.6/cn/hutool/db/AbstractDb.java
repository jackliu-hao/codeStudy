package cn.hutool.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.db.dialect.Dialect;
import cn.hutool.db.handler.BeanListHandler;
import cn.hutool.db.handler.EntityHandler;
import cn.hutool.db.handler.EntityListHandler;
import cn.hutool.db.handler.NumberHandler;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.handler.StringHandler;
import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.Query;
import cn.hutool.db.sql.SqlBuilder;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.db.sql.SqlUtil;
import cn.hutool.db.sql.Wrapper;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public abstract class AbstractDb implements Serializable {
   private static final long serialVersionUID = 3858951941916349062L;
   protected final DataSource ds;
   protected Boolean isSupportTransaction = null;
   protected boolean caseInsensitive;
   protected SqlConnRunner runner;

   public AbstractDb(DataSource ds, Dialect dialect) {
      this.caseInsensitive = GlobalDbConfig.caseInsensitive;
      this.ds = ds;
      this.runner = new SqlConnRunner(dialect);
   }

   public abstract Connection getConnection() throws SQLException;

   public abstract void closeConnection(Connection var1);

   public List<Entity> query(String sql, Map<String, Object> params) throws SQLException {
      return (List)this.query(sql, (RsHandler)(new EntityListHandler(this.caseInsensitive)), (Map)params);
   }

   public List<Entity> query(String sql, Object... params) throws SQLException {
      return (List)this.query(sql, (RsHandler)(new EntityListHandler(this.caseInsensitive)), (Object[])params);
   }

   public <T> List<T> query(String sql, Class<T> beanClass, Object... params) throws SQLException {
      return (List)this.query(sql, (RsHandler)(new BeanListHandler(beanClass)), (Object[])params);
   }

   public Entity queryOne(String sql, Object... params) throws SQLException {
      return (Entity)this.query(sql, (RsHandler)(new EntityHandler(this.caseInsensitive)), (Object[])params);
   }

   public Number queryNumber(String sql, Object... params) throws SQLException {
      return (Number)this.query(sql, (RsHandler)(new NumberHandler()), (Object[])params);
   }

   public String queryString(String sql, Object... params) throws SQLException {
      return (String)this.query(sql, (RsHandler)(new StringHandler()), (Object[])params);
   }

   public <T> T query(String sql, RsHandler<T> rsh, Object... params) throws SQLException {
      Connection conn = null;

      Object var5;
      try {
         conn = this.getConnection();
         var5 = SqlExecutor.query(conn, sql, rsh, params);
      } finally {
         this.closeConnection(conn);
      }

      return var5;
   }

   public <T> T query(String sql, RsHandler<T> rsh, Map<String, Object> paramMap) throws SQLException {
      Connection conn = null;

      Object var5;
      try {
         conn = this.getConnection();
         var5 = SqlExecutor.query(conn, sql, rsh, paramMap);
      } finally {
         this.closeConnection(conn);
      }

      return var5;
   }

   public <T> T query(Func1<Connection, PreparedStatement> statementFunc, RsHandler<T> rsh) throws SQLException {
      Connection conn = null;

      Object var4;
      try {
         conn = this.getConnection();
         var4 = SqlExecutor.query(conn, statementFunc, rsh);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public int execute(String sql, Object... params) throws SQLException {
      Connection conn = null;

      int var4;
      try {
         conn = this.getConnection();
         var4 = SqlExecutor.execute(conn, sql, params);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public Long executeForGeneratedKey(String sql, Object... params) throws SQLException {
      Connection conn = null;

      Long var4;
      try {
         conn = this.getConnection();
         var4 = SqlExecutor.executeForGeneratedKey(conn, sql, params);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   /** @deprecated */
   @Deprecated
   public int[] executeBatch(String sql, Object[]... paramsBatch) throws SQLException {
      Connection conn = null;

      int[] var4;
      try {
         conn = this.getConnection();
         var4 = SqlExecutor.executeBatch(conn, sql, paramsBatch);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public int[] executeBatch(String sql, Iterable<Object[]> paramsBatch) throws SQLException {
      Connection conn = null;

      int[] var4;
      try {
         conn = this.getConnection();
         var4 = SqlExecutor.executeBatch(conn, sql, paramsBatch);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public int[] executeBatch(String... sqls) throws SQLException {
      Connection conn = null;

      int[] var3;
      try {
         conn = this.getConnection();
         var3 = SqlExecutor.executeBatch(conn, sqls);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public int[] executeBatch(Iterable<String> sqls) throws SQLException {
      Connection conn = null;

      int[] var3;
      try {
         conn = this.getConnection();
         var3 = SqlExecutor.executeBatch(conn, sqls);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public int insert(Entity record) throws SQLException {
      Connection conn = null;

      int var3;
      try {
         conn = this.getConnection();
         var3 = this.runner.insert(conn, record);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public int insertOrUpdate(Entity record, String... keys) throws SQLException {
      Connection conn = null;

      int var4;
      try {
         conn = this.getConnection();
         var4 = this.runner.insertOrUpdate(conn, record, keys);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public int upsert(Entity record, String... keys) throws SQLException {
      Connection conn = null;

      int var4;
      try {
         conn = this.getConnection();
         var4 = this.runner.upsert(conn, record, keys);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public int[] insert(Collection<Entity> records) throws SQLException {
      Connection conn = null;

      int[] var3;
      try {
         conn = this.getConnection();
         var3 = this.runner.insert(conn, records);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public List<Object> insertForGeneratedKeys(Entity record) throws SQLException {
      Connection conn = null;

      List var3;
      try {
         conn = this.getConnection();
         var3 = this.runner.insertForGeneratedKeys(conn, record);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public Long insertForGeneratedKey(Entity record) throws SQLException {
      Connection conn = null;

      Long var3;
      try {
         conn = this.getConnection();
         var3 = this.runner.insertForGeneratedKey(conn, record);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public int del(String tableName, String field, Object value) throws SQLException {
      return this.del(Entity.create(tableName).set(field, value));
   }

   public int del(Entity where) throws SQLException {
      Connection conn = null;

      int var3;
      try {
         conn = this.getConnection();
         var3 = this.runner.del(conn, where);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public int update(Entity record, Entity where) throws SQLException {
      Connection conn = null;

      int var4;
      try {
         conn = this.getConnection();
         var4 = this.runner.update(conn, record, where);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public <T> Entity get(String tableName, String field, T value) throws SQLException {
      return this.get(Entity.create(tableName).set(field, value));
   }

   public Entity get(Entity where) throws SQLException {
      return (Entity)this.find((Collection)where.getFieldNames(), (Entity)where, (RsHandler)(new EntityHandler(this.caseInsensitive)));
   }

   public <T> T find(Collection<String> fields, Entity where, RsHandler<T> rsh) throws SQLException {
      Connection conn = null;

      Object var5;
      try {
         conn = this.getConnection();
         var5 = this.runner.find(conn, fields, where, rsh);
      } finally {
         this.closeConnection(conn);
      }

      return var5;
   }

   public List<Entity> find(Collection<String> fields, Entity where) throws SQLException {
      return (List)this.find((Collection)fields, (Entity)where, (RsHandler)(new EntityListHandler(this.caseInsensitive)));
   }

   public <T> T find(Query query, RsHandler<T> rsh) throws SQLException {
      Connection conn = null;

      Object var4;
      try {
         conn = this.getConnection();
         var4 = this.runner.find(conn, query, rsh);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public <T> T find(Entity where, RsHandler<T> rsh, String... fields) throws SQLException {
      return this.find((Collection)CollUtil.newArrayList((Object[])fields), (Entity)where, (RsHandler)rsh);
   }

   public List<Entity> find(Entity where) throws SQLException {
      return (List)this.find((Collection)where.getFieldNames(), (Entity)where, (RsHandler)(new EntityListHandler(this.caseInsensitive)));
   }

   public <T> List<T> find(Entity where, Class<T> beanClass) throws SQLException {
      return (List)this.find((Collection)where.getFieldNames(), (Entity)where, (RsHandler)BeanListHandler.create(beanClass));
   }

   public List<Entity> findAll(Entity where) throws SQLException {
      return (List)this.find((Entity)where, (RsHandler)EntityListHandler.create(), (String[])());
   }

   public <T> List<T> findAll(Entity where, Class<T> beanClass) throws SQLException {
      return (List)this.find((Entity)where, (RsHandler)BeanListHandler.create(beanClass), (String[])());
   }

   public List<Entity> findAll(String tableName) throws SQLException {
      return this.findAll(Entity.create(tableName));
   }

   public List<Entity> findBy(String tableName, String field, Object value) throws SQLException {
      return this.findAll(Entity.create(tableName).set(field, value));
   }

   public List<Entity> findBy(String tableName, Condition... wheres) throws SQLException {
      Query query = new Query(wheres, new String[]{tableName});
      return (List)this.find((Query)query, (RsHandler)(new EntityListHandler(this.caseInsensitive)));
   }

   public List<Entity> findLike(String tableName, String field, String value, Condition.LikeType likeType) throws SQLException {
      return this.findAll(Entity.create(tableName).set(field, SqlUtil.buildLikeValue(value, likeType, true)));
   }

   public long count(Entity where) throws SQLException {
      Connection conn = null;

      long var3;
      try {
         conn = this.getConnection();
         var3 = this.runner.count(conn, where);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public long count(SqlBuilder sql) throws SQLException {
      Connection conn = null;

      long var3;
      try {
         conn = this.getConnection();
         var3 = this.runner.count(conn, sql);
      } finally {
         this.closeConnection(conn);
      }

      return var3;
   }

   public long count(CharSequence selectSql, Object... params) throws SQLException {
      Connection conn = null;

      long var4;
      try {
         conn = this.getConnection();
         var4 = this.runner.count(conn, selectSql, params);
      } finally {
         this.closeConnection(conn);
      }

      return var4;
   }

   public <T> T page(Collection<String> fields, Entity where, int page, int numPerPage, RsHandler<T> rsh) throws SQLException {
      Connection conn = null;

      Object var7;
      try {
         conn = this.getConnection();
         var7 = this.runner.page(conn, fields, where, page, numPerPage, rsh);
      } finally {
         this.closeConnection(conn);
      }

      return var7;
   }

   public <T> T page(Entity where, int page, int numPerPage, RsHandler<T> rsh) throws SQLException {
      return this.page(where, new Page(page, numPerPage), rsh);
   }

   public List<Entity> pageForEntityList(Entity where, int page, int numPerPage) throws SQLException {
      return this.pageForEntityList(where, new Page(page, numPerPage));
   }

   public List<Entity> pageForEntityList(Entity where, Page page) throws SQLException {
      return (List)this.page((Entity)where, (Page)page, (RsHandler)(new EntityListHandler(this.caseInsensitive)));
   }

   public <T> T page(Entity where, Page page, RsHandler<T> rsh) throws SQLException {
      return this.page((Collection)where.getFieldNames(), (Entity)where, (Page)page, (RsHandler)rsh);
   }

   public <T> T page(Collection<String> fields, Entity where, Page page, RsHandler<T> rsh) throws SQLException {
      Connection conn = null;

      Object var6;
      try {
         conn = this.getConnection();
         var6 = this.runner.page(conn, fields, where, page, rsh);
      } finally {
         this.closeConnection(conn);
      }

      return var6;
   }

   public <T> T page(CharSequence sql, Page page, RsHandler<T> rsh, Object... params) throws SQLException {
      Connection conn = null;

      Object var6;
      try {
         conn = this.getConnection();
         var6 = this.runner.page(conn, SqlBuilder.of(sql).addParams(params), page, rsh);
      } finally {
         this.closeConnection(conn);
      }

      return var6;
   }

   public <T> T page(SqlBuilder sql, Page page, RsHandler<T> rsh) throws SQLException {
      Connection conn = null;

      Object var5;
      try {
         conn = this.getConnection();
         var5 = this.runner.page(conn, sql, page, rsh);
      } finally {
         this.closeConnection(conn);
      }

      return var5;
   }

   public PageResult<Entity> page(CharSequence sql, Page page, Object... params) throws SQLException {
      Connection conn = null;

      PageResult var5;
      try {
         conn = this.getConnection();
         var5 = this.runner.page(conn, SqlBuilder.of(sql).addParams(params), page);
      } finally {
         this.closeConnection(conn);
      }

      return var5;
   }

   public PageResult<Entity> page(Collection<String> fields, Entity where, int pageNumber, int pageSize) throws SQLException {
      return this.page(fields, where, new Page(pageNumber, pageSize));
   }

   public PageResult<Entity> page(Collection<String> fields, Entity where, Page page) throws SQLException {
      Connection conn = null;

      PageResult var5;
      try {
         conn = this.getConnection();
         var5 = this.runner.page(conn, fields, where, page);
      } finally {
         this.closeConnection(conn);
      }

      return var5;
   }

   public PageResult<Entity> page(Entity where, int page, int numPerPage) throws SQLException {
      return this.page(where, new Page(page, numPerPage));
   }

   public PageResult<Entity> page(Entity where, Page page) throws SQLException {
      return this.page((Collection)where.getFieldNames(), (Entity)where, (Page)page);
   }

   public void setCaseInsensitive(boolean caseInsensitive) {
      this.caseInsensitive = caseInsensitive;
   }

   public SqlConnRunner getRunner() {
      return this.runner;
   }

   public void setRunner(SqlConnRunner runner) {
      this.runner = runner;
   }

   public AbstractDb setWrapper(Character wrapperChar) {
      return this.setWrapper(new Wrapper(wrapperChar));
   }

   public AbstractDb setWrapper(Wrapper wrapper) {
      this.runner.setWrapper(wrapper);
      return this;
   }

   public AbstractDb disableWrapper() {
      return this.setWrapper((Wrapper)null);
   }

   protected void checkTransactionSupported(Connection conn) throws SQLException, DbRuntimeException {
      if (null == this.isSupportTransaction) {
         this.isSupportTransaction = conn.getMetaData().supportsTransactions();
      }

      if (!this.isSupportTransaction) {
         throw new DbRuntimeException("Transaction not supported for current database!");
      }
   }
}
