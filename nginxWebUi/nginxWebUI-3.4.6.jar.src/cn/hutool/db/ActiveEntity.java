/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.lang.Dict;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
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
/*     */ public class ActiveEntity
/*     */   extends Entity
/*     */ {
/*     */   private static final long serialVersionUID = 6112321379601134750L;
/*     */   private final Db db;
/*     */   
/*     */   public static ActiveEntity create() {
/*  28 */     return new ActiveEntity();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ActiveEntity create(String tableName) {
/*  38 */     return new ActiveEntity(tableName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ActiveEntity parse(T bean) {
/*  49 */     return create((String)null).parseBean(bean);
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
/*     */   public static <T> ActiveEntity parse(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
/*  62 */     return create((String)null).parseBean(bean, isToUnderlineCase, ignoreNullValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ActiveEntity parseWithUnderlineCase(T bean) {
/*  73 */     return create((String)null).parseBean(bean, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity() {
/*  82 */     this(Db.use(), (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity(String tableName) {
/*  91 */     this(Db.use(), tableName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity(Entity entity) {
/* 100 */     this(Db.use(), entity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity(Db db, String tableName) {
/* 110 */     super(tableName);
/* 111 */     this.db = db;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity(Db db, Entity entity) {
/* 121 */     super(entity.getTableName());
/* 122 */     putAll((Map)entity);
/* 123 */     this.db = db;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity setTableName(String tableName) {
/* 129 */     return (ActiveEntity)super.setTableName(tableName);
/*     */   }
/*     */ 
/*     */   
/*     */   public ActiveEntity setFieldNames(Collection<String> fieldNames) {
/* 134 */     return (ActiveEntity)super.setFieldNames(fieldNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public ActiveEntity setFieldNames(String... fieldNames) {
/* 139 */     return (ActiveEntity)super.setFieldNames(fieldNames);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity setFields(Func0<?>... fields) {
/* 149 */     return (ActiveEntity)super.setFields(fields);
/*     */   }
/*     */ 
/*     */   
/*     */   public ActiveEntity addFieldNames(String... fieldNames) {
/* 154 */     return (ActiveEntity)super.addFieldNames(fieldNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> ActiveEntity parseBean(T bean) {
/* 159 */     return (ActiveEntity)super.<T>parseBean(bean);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> ActiveEntity parseBean(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
/* 164 */     return (ActiveEntity)super.<T>parseBean(bean, isToUnderlineCase, ignoreNullValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public ActiveEntity set(String field, Object value) {
/* 169 */     return (ActiveEntity)super.set(field, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ActiveEntity setIgnoreNull(String field, Object value) {
/* 174 */     return (ActiveEntity)super.setIgnoreNull(field, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ActiveEntity clone() {
/* 179 */     return (ActiveEntity)super.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity add() {
/*     */     try {
/* 190 */       this.db.insert(this);
/* 191 */     } catch (SQLException e) {
/* 192 */       throw new DbRuntimeException(e);
/*     */     } 
/* 194 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity load() {
/*     */     try {
/* 204 */       Entity result = this.db.get(this);
/* 205 */       if (MapUtil.isNotEmpty((Map)result)) {
/* 206 */         putAll((Map)result);
/*     */       }
/* 208 */     } catch (SQLException e) {
/* 209 */       throw new DbRuntimeException(e);
/*     */     } 
/* 211 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity del() {
/*     */     try {
/* 221 */       this.db.del(this);
/* 222 */     } catch (SQLException e) {
/* 223 */       throw new DbRuntimeException(e);
/*     */     } 
/* 225 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActiveEntity update(String primaryKey) {
/*     */     try {
/* 236 */       this.db.update(this, Entity.create().set(primaryKey, get(primaryKey)));
/* 237 */     } catch (SQLException e) {
/* 238 */       throw new DbRuntimeException(e);
/*     */     } 
/* 240 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ActiveEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */