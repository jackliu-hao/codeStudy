package cn.hutool.db;

import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.map.MapUtil;
import java.sql.SQLException;
import java.util.Collection;

public class ActiveEntity extends Entity {
   private static final long serialVersionUID = 6112321379601134750L;
   private final Db db;

   public static ActiveEntity create() {
      return new ActiveEntity();
   }

   public static ActiveEntity create(String tableName) {
      return new ActiveEntity(tableName);
   }

   public static <T> ActiveEntity parse(T bean) {
      return create((String)null).parseBean(bean);
   }

   public static <T> ActiveEntity parse(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
      return create((String)null).parseBean(bean, isToUnderlineCase, ignoreNullValue);
   }

   public static <T> ActiveEntity parseWithUnderlineCase(T bean) {
      return create((String)null).parseBean(bean, true, true);
   }

   public ActiveEntity() {
      this(Db.use(), (String)null);
   }

   public ActiveEntity(String tableName) {
      this(Db.use(), tableName);
   }

   public ActiveEntity(Entity entity) {
      this(Db.use(), entity);
   }

   public ActiveEntity(Db db, String tableName) {
      super(tableName);
      this.db = db;
   }

   public ActiveEntity(Db db, Entity entity) {
      super(entity.getTableName());
      this.putAll(entity);
      this.db = db;
   }

   public ActiveEntity setTableName(String tableName) {
      return (ActiveEntity)super.setTableName(tableName);
   }

   public ActiveEntity setFieldNames(Collection<String> fieldNames) {
      return (ActiveEntity)super.setFieldNames(fieldNames);
   }

   public ActiveEntity setFieldNames(String... fieldNames) {
      return (ActiveEntity)super.setFieldNames(fieldNames);
   }

   public ActiveEntity setFields(Func0<?>... fields) {
      return (ActiveEntity)super.setFields(fields);
   }

   public ActiveEntity addFieldNames(String... fieldNames) {
      return (ActiveEntity)super.addFieldNames(fieldNames);
   }

   public <T> ActiveEntity parseBean(T bean) {
      return (ActiveEntity)super.parseBean(bean);
   }

   public <T> ActiveEntity parseBean(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
      return (ActiveEntity)super.parseBean(bean, isToUnderlineCase, ignoreNullValue);
   }

   public ActiveEntity set(String field, Object value) {
      return (ActiveEntity)super.set(field, value);
   }

   public ActiveEntity setIgnoreNull(String field, Object value) {
      return (ActiveEntity)super.setIgnoreNull(field, value);
   }

   public ActiveEntity clone() {
      return (ActiveEntity)super.clone();
   }

   public ActiveEntity add() {
      try {
         this.db.insert(this);
         return this;
      } catch (SQLException var2) {
         throw new DbRuntimeException(var2);
      }
   }

   public ActiveEntity load() {
      try {
         Entity result = this.db.get(this);
         if (MapUtil.isNotEmpty(result)) {
            this.putAll(result);
         }

         return this;
      } catch (SQLException var2) {
         throw new DbRuntimeException(var2);
      }
   }

   public ActiveEntity del() {
      try {
         this.db.del(this);
         return this;
      } catch (SQLException var2) {
         throw new DbRuntimeException(var2);
      }
   }

   public ActiveEntity update(String primaryKey) {
      try {
         this.db.update(this, Entity.create().set(primaryKey, this.get(primaryKey)));
         return this;
      } catch (SQLException var3) {
         throw new DbRuntimeException(var3);
      }
   }
}
