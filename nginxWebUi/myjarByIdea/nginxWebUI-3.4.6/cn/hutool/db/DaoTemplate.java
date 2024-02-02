package cn.hutool.db;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.DSFactory;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

public class DaoTemplate {
   protected String tableName;
   protected String primaryKeyField;
   protected Db db;

   public DaoTemplate(String tableName) {
      this(tableName, (String)null);
   }

   public DaoTemplate(String tableName, String primaryKeyField) {
      this(tableName, primaryKeyField, DSFactory.get());
   }

   public DaoTemplate(String tableName, DataSource ds) {
      this(tableName, (String)null, (DataSource)ds);
   }

   public DaoTemplate(String tableName, String primaryKeyField, DataSource ds) {
      this(tableName, primaryKeyField, Db.use(ds));
   }

   public DaoTemplate(String tableName, String primaryKeyField, Db db) {
      this.primaryKeyField = "id";
      this.tableName = tableName;
      if (StrUtil.isNotBlank(primaryKeyField)) {
         this.primaryKeyField = primaryKeyField;
      }

      this.db = db;
   }

   public int add(Entity entity) throws SQLException {
      return this.db.insert(this.fixEntity(entity));
   }

   public List<Object> addForGeneratedKeys(Entity entity) throws SQLException {
      return this.db.insertForGeneratedKeys(this.fixEntity(entity));
   }

   public Long addForGeneratedKey(Entity entity) throws SQLException {
      return this.db.insertForGeneratedKey(this.fixEntity(entity));
   }

   public <T> int del(T pk) throws SQLException {
      return pk == null ? 0 : this.del(Entity.create(this.tableName).set(this.primaryKeyField, pk));
   }

   public <T> int del(String field, T value) throws SQLException {
      return StrUtil.isBlank(field) ? 0 : this.del(Entity.create(this.tableName).set(field, value));
   }

   public <T> int del(Entity where) throws SQLException {
      return MapUtil.isEmpty(where) ? 0 : this.db.del(this.fixEntity(where));
   }

   public int update(Entity record, Entity where) throws SQLException {
      return MapUtil.isEmpty(record) ? 0 : this.db.update(this.fixEntity(record), where);
   }

   public int update(Entity entity) throws SQLException {
      if (MapUtil.isEmpty(entity)) {
         return 0;
      } else {
         entity = this.fixEntity(entity);
         Object pk = entity.get(this.primaryKeyField);
         if (null == pk) {
            throw new SQLException(StrUtil.format("Please determine `{}` for update", new Object[]{this.primaryKeyField}));
         } else {
            Entity where = Entity.create(this.tableName).set(this.primaryKeyField, pk);
            Entity record = entity.clone();
            record.remove(this.primaryKeyField);
            return this.db.update(record, where);
         }
      }
   }

   public int addOrUpdate(Entity entity) throws SQLException {
      return null == entity.get(this.primaryKeyField) ? this.add(entity) : this.update(entity);
   }

   public <T> Entity get(T pk) throws SQLException {
      return this.get(this.primaryKeyField, pk);
   }

   public <T> Entity get(String field, T value) throws SQLException {
      return this.get(Entity.create(this.tableName).set(field, value));
   }

   public Entity get(Entity where) throws SQLException {
      return this.db.get(this.fixEntity(where));
   }

   public <T> List<Entity> find(String field, T value) throws SQLException {
      return this.find(Entity.create(this.tableName).set(field, value));
   }

   public List<Entity> findAll() throws SQLException {
      return this.find(Entity.create(this.tableName));
   }

   public List<Entity> find(Entity where) throws SQLException {
      return this.db.find((Collection)null, this.fixEntity(where));
   }

   public List<Entity> findBySql(String sql, Object... params) throws SQLException {
      String selectKeyword = StrUtil.subPre(sql.trim(), 6).toLowerCase();
      if (!"select".equals(selectKeyword)) {
         sql = "SELECT * FROM " + this.tableName + " " + sql;
      }

      return this.db.query(sql, params);
   }

   public PageResult<Entity> page(Entity where, Page page, String... selectFields) throws SQLException {
      return this.db.page(Arrays.asList(selectFields), this.fixEntity(where), page);
   }

   public PageResult<Entity> page(Entity where, Page page) throws SQLException {
      return this.db.page(this.fixEntity(where), page);
   }

   public long count(Entity where) throws SQLException {
      return this.db.count(this.fixEntity(where));
   }

   public boolean exist(Entity where) throws SQLException {
      return this.count(where) > 0L;
   }

   private Entity fixEntity(Entity entity) {
      if (null == entity) {
         entity = Entity.create(this.tableName);
      } else if (StrUtil.isBlank(entity.getTableName())) {
         entity.setTableName(this.tableName);
      }

      return entity;
   }
}
