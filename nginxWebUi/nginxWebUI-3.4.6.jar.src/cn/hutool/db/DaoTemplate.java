/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.ds.DSFactory;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DaoTemplate
/*     */ {
/*     */   protected String tableName;
/*  29 */   protected String primaryKeyField = "id";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Db db;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DaoTemplate(String tableName) {
/*  43 */     this(tableName, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DaoTemplate(String tableName, String primaryKeyField) {
/*  53 */     this(tableName, primaryKeyField, DSFactory.get());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DaoTemplate(String tableName, DataSource ds) {
/*  63 */     this(tableName, (String)null, ds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DaoTemplate(String tableName, String primaryKeyField, DataSource ds) {
/*  74 */     this(tableName, primaryKeyField, Db.use(ds));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DaoTemplate(String tableName, String primaryKeyField, Db db) {
/*  85 */     this.tableName = tableName;
/*  86 */     if (StrUtil.isNotBlank(primaryKeyField)) {
/*  87 */       this.primaryKeyField = primaryKeyField;
/*     */     }
/*  89 */     this.db = db;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int add(Entity entity) throws SQLException {
/* 103 */     return this.db.insert(fixEntity(entity));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Object> addForGeneratedKeys(Entity entity) throws SQLException {
/* 114 */     return this.db.insertForGeneratedKeys(fixEntity(entity));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long addForGeneratedKey(Entity entity) throws SQLException {
/* 125 */     return this.db.insertForGeneratedKey(fixEntity(entity));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> int del(T pk) throws SQLException {
/* 140 */     if (pk == null) {
/* 141 */       return 0;
/*     */     }
/* 143 */     return del(Entity.create(this.tableName).set(this.primaryKeyField, pk));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> int del(String field, T value) throws SQLException {
/* 156 */     if (StrUtil.isBlank(field)) {
/* 157 */       return 0;
/*     */     }
/*     */     
/* 160 */     return del(Entity.create(this.tableName).set(field, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> int del(Entity where) throws SQLException {
/* 172 */     if (MapUtil.isEmpty((Map)where)) {
/* 173 */       return 0;
/*     */     }
/* 175 */     return this.db.del(fixEntity(where));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int update(Entity record, Entity where) throws SQLException {
/* 190 */     if (MapUtil.isEmpty((Map)record)) {
/* 191 */       return 0;
/*     */     }
/* 193 */     return this.db.update(fixEntity(record), where);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int update(Entity entity) throws SQLException {
/* 204 */     if (MapUtil.isEmpty((Map)entity)) {
/* 205 */       return 0;
/*     */     }
/* 207 */     entity = fixEntity(entity);
/* 208 */     Object pk = entity.get(this.primaryKeyField);
/* 209 */     if (null == pk) {
/* 210 */       throw new SQLException(StrUtil.format("Please determine `{}` for update", new Object[] { this.primaryKeyField }));
/*     */     }
/*     */     
/* 213 */     Entity where = Entity.create(this.tableName).set(this.primaryKeyField, pk);
/* 214 */     Entity record = entity.clone();
/* 215 */     record.remove(this.primaryKeyField);
/*     */     
/* 217 */     return this.db.update(record, where);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int addOrUpdate(Entity entity) throws SQLException {
/* 228 */     return (null == entity.get(this.primaryKeyField)) ? add(entity) : update(entity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Entity get(T pk) throws SQLException {
/* 243 */     return get(this.primaryKeyField, pk);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> Entity get(String field, T value) throws SQLException {
/* 257 */     return get(Entity.create(this.tableName).set(field, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity get(Entity where) throws SQLException {
/* 268 */     return this.db.get(fixEntity(where));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<Entity> find(String field, T value) throws SQLException {
/* 284 */     return find(Entity.create(this.tableName).set(field, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Entity> findAll() throws SQLException {
/* 294 */     return find(Entity.create(this.tableName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Entity> find(Entity where) throws SQLException {
/* 305 */     return this.db.find((Collection<String>)null, fixEntity(where));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Entity> findBySql(String sql, Object... params) throws SQLException {
/* 319 */     String selectKeyword = StrUtil.subPre(sql.trim(), 6).toLowerCase();
/* 320 */     if (false == "select".equals(selectKeyword)) {
/* 321 */       sql = "SELECT * FROM " + this.tableName + " " + sql;
/*     */     }
/* 323 */     return this.db.query(sql, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PageResult<Entity> page(Entity where, Page page, String... selectFields) throws SQLException {
/* 336 */     return this.db.page(Arrays.asList(selectFields), fixEntity(where), page);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PageResult<Entity> page(Entity where, Page page) throws SQLException {
/* 348 */     return this.db.page(fixEntity(where), page);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long count(Entity where) throws SQLException {
/* 359 */     return this.db.count(fixEntity(where));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean exist(Entity where) throws SQLException {
/* 370 */     return (count(where) > 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Entity fixEntity(Entity entity) {
/* 381 */     if (null == entity) {
/* 382 */       entity = Entity.create(this.tableName);
/* 383 */     } else if (StrUtil.isBlank(entity.getTableName())) {
/* 384 */       entity.setTableName(this.tableName);
/*     */     } 
/* 386 */     return entity;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\DaoTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */