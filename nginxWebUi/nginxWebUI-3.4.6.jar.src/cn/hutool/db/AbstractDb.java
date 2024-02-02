/*      */ package cn.hutool.db;
/*      */ 
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.lang.func.Func1;
/*      */ import cn.hutool.db.dialect.Dialect;
/*      */ import cn.hutool.db.handler.BeanListHandler;
/*      */ import cn.hutool.db.handler.EntityHandler;
/*      */ import cn.hutool.db.handler.EntityListHandler;
/*      */ import cn.hutool.db.handler.NumberHandler;
/*      */ import cn.hutool.db.handler.RsHandler;
/*      */ import cn.hutool.db.handler.StringHandler;
/*      */ import cn.hutool.db.sql.Condition;
/*      */ import cn.hutool.db.sql.Query;
/*      */ import cn.hutool.db.sql.SqlBuilder;
/*      */ import cn.hutool.db.sql.SqlExecutor;
/*      */ import cn.hutool.db.sql.SqlUtil;
/*      */ import cn.hutool.db.sql.Wrapper;
/*      */ import java.io.Serializable;
/*      */ import java.sql.Connection;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Collection;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.sql.DataSource;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class AbstractDb
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 3858951941916349062L;
/*      */   protected final DataSource ds;
/*   44 */   protected Boolean isSupportTransaction = null;
/*      */ 
/*      */ 
/*      */   
/*   48 */   protected boolean caseInsensitive = GlobalDbConfig.caseInsensitive;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SqlConnRunner runner;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AbstractDb(DataSource ds, Dialect dialect) {
/*   60 */     this.ds = ds;
/*   61 */     this.runner = new SqlConnRunner(dialect);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract Connection getConnection() throws SQLException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public abstract void closeConnection(Connection paramConnection);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> query(String sql, Map<String, Object> params) throws SQLException {
/*   91 */     return query(sql, (RsHandler<List<Entity>>)new EntityListHandler(this.caseInsensitive), params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> query(String sql, Object... params) throws SQLException {
/*  104 */     return query(sql, (RsHandler<List<Entity>>)new EntityListHandler(this.caseInsensitive), params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> List<T> query(String sql, Class<T> beanClass, Object... params) throws SQLException {
/*  119 */     return query(sql, (RsHandler<List<T>>)new BeanListHandler(beanClass), params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Entity queryOne(String sql, Object... params) throws SQLException {
/*  131 */     return query(sql, (RsHandler<Entity>)new EntityHandler(this.caseInsensitive), params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Number queryNumber(String sql, Object... params) throws SQLException {
/*  143 */     return query(sql, (RsHandler<Number>)new NumberHandler(), params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String queryString(String sql, Object... params) throws SQLException {
/*  155 */     return query(sql, (RsHandler<String>)new StringHandler(), params);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T query(String sql, RsHandler<T> rsh, Object... params) throws SQLException {
/*  169 */     Connection conn = null;
/*      */     try {
/*  171 */       conn = getConnection();
/*  172 */       return (T)SqlExecutor.query(conn, sql, rsh, params);
/*      */     } finally {
/*  174 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T query(String sql, RsHandler<T> rsh, Map<String, Object> paramMap) throws SQLException {
/*  190 */     Connection conn = null;
/*      */     try {
/*  192 */       conn = getConnection();
/*  193 */       return (T)SqlExecutor.query(conn, sql, rsh, paramMap);
/*      */     } finally {
/*  195 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T query(Func1<Connection, PreparedStatement> statementFunc, RsHandler<T> rsh) throws SQLException {
/*  211 */     Connection conn = null;
/*      */     try {
/*  213 */       conn = getConnection();
/*  214 */       return (T)SqlExecutor.query(conn, statementFunc, rsh);
/*      */     } finally {
/*  216 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int execute(String sql, Object... params) throws SQLException {
/*  230 */     Connection conn = null;
/*      */     try {
/*  232 */       conn = getConnection();
/*  233 */       return SqlExecutor.execute(conn, sql, params);
/*      */     } finally {
/*  235 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Long executeForGeneratedKey(String sql, Object... params) throws SQLException {
/*  249 */     Connection conn = null;
/*      */     try {
/*  251 */       conn = getConnection();
/*  252 */       return SqlExecutor.executeForGeneratedKey(conn, sql, params);
/*      */     } finally {
/*  254 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public int[] executeBatch(String sql, Object[]... paramsBatch) throws SQLException {
/*  269 */     Connection conn = null;
/*      */     try {
/*  271 */       conn = getConnection();
/*  272 */       return SqlExecutor.executeBatch(conn, sql, paramsBatch);
/*      */     } finally {
/*  274 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] executeBatch(String sql, Iterable<Object[]> paramsBatch) throws SQLException {
/*  288 */     Connection conn = null;
/*      */     try {
/*  290 */       conn = getConnection();
/*  291 */       return SqlExecutor.executeBatch(conn, sql, paramsBatch);
/*      */     } finally {
/*  293 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] executeBatch(String... sqls) throws SQLException {
/*  306 */     Connection conn = null;
/*      */     try {
/*  308 */       conn = getConnection();
/*  309 */       return SqlExecutor.executeBatch(conn, sqls);
/*      */     } finally {
/*  311 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] executeBatch(Iterable<String> sqls) throws SQLException {
/*  324 */     Connection conn = null;
/*      */     try {
/*  326 */       conn = getConnection();
/*  327 */       return SqlExecutor.executeBatch(conn, sqls);
/*      */     } finally {
/*  329 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int insert(Entity record) throws SQLException {
/*  343 */     Connection conn = null;
/*      */     try {
/*  345 */       conn = getConnection();
/*  346 */       return this.runner.insert(conn, record);
/*      */     } finally {
/*  348 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int insertOrUpdate(Entity record, String... keys) throws SQLException {
/*  363 */     Connection conn = null;
/*      */     try {
/*  365 */       conn = getConnection();
/*  366 */       return this.runner.insertOrUpdate(conn, record, keys);
/*      */     } finally {
/*  368 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int upsert(Entity record, String... keys) throws SQLException {
/*  383 */     Connection conn = null;
/*      */     try {
/*  385 */       conn = getConnection();
/*  386 */       return this.runner.upsert(conn, record, keys);
/*      */     } finally {
/*  388 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int[] insert(Collection<Entity> records) throws SQLException {
/*  402 */     Connection conn = null;
/*      */     try {
/*  404 */       conn = getConnection();
/*  405 */       return this.runner.insert(conn, records);
/*      */     } finally {
/*  407 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Object> insertForGeneratedKeys(Entity record) throws SQLException {
/*  419 */     Connection conn = null;
/*      */     try {
/*  421 */       conn = getConnection();
/*  422 */       return this.runner.insertForGeneratedKeys(conn, record);
/*      */     } finally {
/*  424 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Long insertForGeneratedKey(Entity record) throws SQLException {
/*  436 */     Connection conn = null;
/*      */     try {
/*  438 */       conn = getConnection();
/*  439 */       return this.runner.insertForGeneratedKey(conn, record);
/*      */     } finally {
/*  441 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int del(String tableName, String field, Object value) throws SQLException {
/*  455 */     return del(Entity.create(tableName).set(field, value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int del(Entity where) throws SQLException {
/*  466 */     Connection conn = null;
/*      */     try {
/*  468 */       conn = getConnection();
/*  469 */       return this.runner.del(conn, where);
/*      */     } finally {
/*  471 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int update(Entity record, Entity where) throws SQLException {
/*  485 */     Connection conn = null;
/*      */     try {
/*  487 */       conn = getConnection();
/*  488 */       return this.runner.update(conn, record, where);
/*      */     } finally {
/*  490 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> Entity get(String tableName, String field, T value) throws SQLException {
/*  508 */     return get(Entity.create(tableName).set(field, value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Entity get(Entity where) throws SQLException {
/*  519 */     return find(where.getFieldNames(), where, (RsHandler<Entity>)new EntityHandler(this.caseInsensitive));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T find(Collection<String> fields, Entity where, RsHandler<T> rsh) throws SQLException {
/*  536 */     Connection conn = null;
/*      */     try {
/*  538 */       conn = getConnection();
/*  539 */       return (T)this.runner.find(conn, fields, where, (RsHandler)rsh);
/*      */     } finally {
/*  541 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> find(Collection<String> fields, Entity where) throws SQLException {
/*  556 */     return find(fields, where, (RsHandler<List<Entity>>)new EntityListHandler(this.caseInsensitive));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T find(Query query, RsHandler<T> rsh) throws SQLException {
/*  571 */     Connection conn = null;
/*      */     try {
/*  573 */       conn = getConnection();
/*  574 */       return (T)this.runner.find(conn, query, (RsHandler)rsh);
/*      */     } finally {
/*  576 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T find(Entity where, RsHandler<T> rsh, String... fields) throws SQLException {
/*  592 */     return find(CollUtil.newArrayList((Object[])fields), where, rsh);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> find(Entity where) throws SQLException {
/*  605 */     return find(where.getFieldNames(), where, (RsHandler<List<Entity>>)new EntityListHandler(this.caseInsensitive));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> List<T> find(Entity where, Class<T> beanClass) throws SQLException {
/*  620 */     return find(where.getFieldNames(), where, (RsHandler<List<T>>)BeanListHandler.create(beanClass));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> findAll(Entity where) throws SQLException {
/*  632 */     return find(where, (RsHandler<List<Entity>>)EntityListHandler.create(), new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> List<T> findAll(Entity where, Class<T> beanClass) throws SQLException {
/*  647 */     return find(where, (RsHandler<List<T>>)BeanListHandler.create(beanClass), new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> findAll(String tableName) throws SQLException {
/*  658 */     return findAll(Entity.create(tableName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> findBy(String tableName, String field, Object value) throws SQLException {
/*  671 */     return findAll(Entity.create(tableName).set(field, value));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> findBy(String tableName, Condition... wheres) throws SQLException {
/*  684 */     Query query = new Query(wheres, new String[] { tableName });
/*  685 */     return find(query, (RsHandler<List<Entity>>)new EntityListHandler(this.caseInsensitive));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> findLike(String tableName, String field, String value, Condition.LikeType likeType) throws SQLException {
/*  699 */     return findAll(Entity.create(tableName).set(field, SqlUtil.buildLikeValue(value, likeType, true)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long count(Entity where) throws SQLException {
/*  710 */     Connection conn = null;
/*      */     try {
/*  712 */       conn = getConnection();
/*  713 */       return this.runner.count(conn, where);
/*      */     } finally {
/*  715 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long count(SqlBuilder sql) throws SQLException {
/*  727 */     Connection conn = null;
/*      */     try {
/*  729 */       conn = getConnection();
/*  730 */       return this.runner.count(conn, sql);
/*      */     } finally {
/*  732 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long count(CharSequence selectSql, Object... params) throws SQLException {
/*  746 */     Connection conn = null;
/*      */     try {
/*  748 */       conn = getConnection();
/*  749 */       return this.runner.count(conn, selectSql, params);
/*      */     } finally {
/*  751 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T page(Collection<String> fields, Entity where, int page, int numPerPage, RsHandler<T> rsh) throws SQLException {
/*  769 */     Connection conn = null;
/*      */     try {
/*  771 */       conn = getConnection();
/*  772 */       return (T)this.runner.page(conn, fields, where, page, numPerPage, (RsHandler)rsh);
/*      */     } finally {
/*  774 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T page(Entity where, int page, int numPerPage, RsHandler<T> rsh) throws SQLException {
/*  792 */     return page(where, new Page(page, numPerPage), rsh);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> pageForEntityList(Entity where, int page, int numPerPage) throws SQLException {
/*  807 */     return pageForEntityList(where, new Page(page, numPerPage));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<Entity> pageForEntityList(Entity where, Page page) throws SQLException {
/*  821 */     return page(where, page, (RsHandler<List<Entity>>)new EntityListHandler(this.caseInsensitive));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T page(Entity where, Page page, RsHandler<T> rsh) throws SQLException {
/*  837 */     return page(where.getFieldNames(), where, page, rsh);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T page(Collection<String> fields, Entity where, Page page, RsHandler<T> rsh) throws SQLException {
/*  853 */     Connection conn = null;
/*      */     try {
/*  855 */       conn = getConnection();
/*  856 */       return (T)this.runner.page(conn, fields, where, page, (RsHandler)rsh);
/*      */     } finally {
/*  858 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T page(CharSequence sql, Page page, RsHandler<T> rsh, Object... params) throws SQLException {
/*  875 */     Connection conn = null;
/*      */     try {
/*  877 */       conn = getConnection();
/*  878 */       return (T)this.runner.page(conn, SqlBuilder.of(sql).addParams(params), page, (RsHandler)rsh);
/*      */     } finally {
/*  880 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T page(SqlBuilder sql, Page page, RsHandler<T> rsh) throws SQLException {
/*  895 */     Connection conn = null;
/*      */     try {
/*  897 */       conn = getConnection();
/*  898 */       return (T)this.runner.page(conn, sql, page, (RsHandler)rsh);
/*      */     } finally {
/*  900 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PageResult<Entity> page(CharSequence sql, Page page, Object... params) throws SQLException {
/*  915 */     Connection conn = null;
/*      */     try {
/*  917 */       conn = getConnection();
/*  918 */       return this.runner.page(conn, SqlBuilder.of(sql).addParams(params), page);
/*      */     } finally {
/*  920 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PageResult<Entity> page(Collection<String> fields, Entity where, int pageNumber, int pageSize) throws SQLException {
/*  936 */     return page(fields, where, new Page(pageNumber, pageSize));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PageResult<Entity> page(Collection<String> fields, Entity where, Page page) throws SQLException {
/*  950 */     Connection conn = null;
/*      */     try {
/*  952 */       conn = getConnection();
/*  953 */       return this.runner.page(conn, fields, where, page);
/*      */     } finally {
/*  955 */       closeConnection(conn);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PageResult<Entity> page(Entity where, int page, int numPerPage) throws SQLException {
/*  971 */     return page(where, new Page(page, numPerPage));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PageResult<Entity> page(Entity where, Page page) throws SQLException {
/*  984 */     return page(where.getFieldNames(), where, page);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCaseInsensitive(boolean caseInsensitive) {
/*  998 */     this.caseInsensitive = caseInsensitive;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SqlConnRunner getRunner() {
/* 1007 */     return this.runner;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRunner(SqlConnRunner runner) {
/* 1016 */     this.runner = runner;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AbstractDb setWrapper(Character wrapperChar) {
/* 1027 */     return setWrapper(new Wrapper(wrapperChar));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AbstractDb setWrapper(Wrapper wrapper) {
/* 1038 */     this.runner.setWrapper(wrapper);
/* 1039 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AbstractDb disableWrapper() {
/* 1050 */     return setWrapper((Wrapper)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void checkTransactionSupported(Connection conn) throws SQLException, DbRuntimeException {
/* 1064 */     if (null == this.isSupportTransaction) {
/* 1065 */       this.isSupportTransaction = Boolean.valueOf(conn.getMetaData().supportsTransactions());
/*      */     }
/* 1067 */     if (false == this.isSupportTransaction.booleanValue())
/* 1068 */       throw new DbRuntimeException("Transaction not supported for current database!"); 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\AbstractDb.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */