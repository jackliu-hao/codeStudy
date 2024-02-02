/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.Entity;
/*     */ import cn.hutool.db.dialect.DialectName;
/*     */ import cn.hutool.db.dialect.impl.OracleDialect;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public class SqlBuilder
/*     */   implements Builder<String>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public static SqlBuilder create() {
/*  36 */     return new SqlBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SqlBuilder create(Wrapper wrapper) {
/*  46 */     return new SqlBuilder(wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SqlBuilder of(CharSequence sql) {
/*  57 */     return create().append(sql);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void validateEntity(Entity entity) throws DbRuntimeException {
/*  67 */     if (null == entity) {
/*  68 */       throw new DbRuntimeException("Entity is null !");
/*     */     }
/*  70 */     if (StrUtil.isBlank(entity.getTableName())) {
/*  71 */       throw new DbRuntimeException("Entity`s table name is null !");
/*     */     }
/*  73 */     if (entity.isEmpty()) {
/*  74 */       throw new DbRuntimeException("No filed and value in this entity !");
/*     */     }
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
/*     */   public enum Join
/*     */   {
/*  91 */     INNER,
/*     */ 
/*     */ 
/*     */     
/*  95 */     LEFT,
/*     */ 
/*     */ 
/*     */     
/*  99 */     RIGHT,
/*     */ 
/*     */ 
/*     */     
/* 103 */     FULL;
/*     */   }
/*     */ 
/*     */   
/* 107 */   private final StringBuilder sql = new StringBuilder();
/*     */ 
/*     */ 
/*     */   
/* 111 */   private final List<Object> paramValues = new ArrayList();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Wrapper wrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder(Wrapper wrapper) {
/* 122 */     this.wrapper = wrapper;
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
/*     */   public SqlBuilder insert(Entity entity) {
/* 135 */     return insert(entity, DialectName.ANSI);
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
/*     */   public SqlBuilder insert(Entity entity, DialectName dialectName) {
/* 147 */     return insert(entity, dialectName.name());
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
/*     */   public SqlBuilder insert(Entity entity, String dialectName) {
/* 161 */     validateEntity(entity);
/*     */     
/* 163 */     boolean isOracle = DialectName.ORACLE.match(dialectName);
/* 164 */     StringBuilder fieldsPart = new StringBuilder();
/* 165 */     StringBuilder placeHolder = new StringBuilder();
/*     */     
/* 167 */     entity.forEach((field, value) -> {
/*     */           if (StrUtil.isNotBlank(field)) {
/*     */             if (fieldsPart.length() > 0) {
/*     */               fieldsPart.append(", ");
/*     */ 
/*     */               
/*     */               placeHolder.append(", ");
/*     */             } 
/*     */             
/*     */             fieldsPart.append((null != this.wrapper) ? this.wrapper.wrap(field) : field);
/*     */             
/*     */             if (isOracle && OracleDialect.isNextVal(value)) {
/*     */               placeHolder.append(value);
/*     */             } else {
/*     */               placeHolder.append("?");
/*     */               
/*     */               this.paramValues.add(value);
/*     */             } 
/*     */           } 
/*     */         });
/*     */     
/* 188 */     if (DialectName.PHOENIX.match(dialectName)) {
/* 189 */       this.sql.append("UPSERT INTO ");
/*     */     } else {
/* 191 */       this.sql.append("INSERT INTO ");
/*     */     } 
/*     */     
/* 194 */     String tableName = entity.getTableName();
/* 195 */     if (null != this.wrapper)
/*     */     {
/* 197 */       tableName = this.wrapper.wrap(tableName);
/*     */     }
/* 199 */     this.sql.append(tableName)
/* 200 */       .append(" (").append(fieldsPart).append(") VALUES (")
/* 201 */       .append(placeHolder).append(")");
/*     */     
/* 203 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder delete(String tableName) {
/* 213 */     if (StrUtil.isBlank(tableName)) {
/* 214 */       throw new DbRuntimeException("Table name is blank !");
/*     */     }
/*     */     
/* 217 */     if (null != this.wrapper)
/*     */     {
/* 219 */       tableName = this.wrapper.wrap(tableName);
/*     */     }
/*     */     
/* 222 */     this.sql.append("DELETE FROM ").append(tableName);
/*     */     
/* 224 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder update(Entity entity) {
/* 235 */     validateEntity(entity);
/*     */     
/* 237 */     String tableName = entity.getTableName();
/* 238 */     if (null != this.wrapper)
/*     */     {
/* 240 */       tableName = this.wrapper.wrap(tableName);
/*     */     }
/*     */     
/* 243 */     this.sql.append("UPDATE ").append(tableName).append(" SET ");
/* 244 */     entity.forEach((field, value) -> {
/*     */           if (StrUtil.isNotBlank(field)) {
/*     */             if (this.paramValues.size() > 0) {
/*     */               this.sql.append(", ");
/*     */             }
/*     */             this.sql.append((null != this.wrapper) ? this.wrapper.wrap(field) : field).append(" = ? ");
/*     */             this.paramValues.add(value);
/*     */           } 
/*     */         });
/* 253 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder select(boolean isDistinct, String... fields) {
/* 264 */     return select(isDistinct, Arrays.asList(fields));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder select(boolean isDistinct, Collection<String> fields) {
/* 275 */     this.sql.append("SELECT ");
/* 276 */     if (isDistinct) {
/* 277 */       this.sql.append("DISTINCT ");
/*     */     }
/*     */     
/* 280 */     if (CollectionUtil.isEmpty(fields)) {
/* 281 */       this.sql.append("*");
/*     */     } else {
/* 283 */       if (null != this.wrapper)
/*     */       {
/* 285 */         fields = this.wrapper.wrap(fields);
/*     */       }
/* 287 */       this.sql.append(CollectionUtil.join(fields, ","));
/*     */     } 
/*     */     
/* 290 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder select(String... fields) {
/* 300 */     return select(false, fields);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder select(Collection<String> fields) {
/* 310 */     return select(false, fields);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder from(String... tableNames) {
/* 320 */     if (ArrayUtil.isEmpty((Object[])tableNames) || StrUtil.hasBlank((CharSequence[])tableNames)) {
/* 321 */       throw new DbRuntimeException("Table name is blank in table names !");
/*     */     }
/*     */     
/* 324 */     if (null != this.wrapper)
/*     */     {
/* 326 */       tableNames = this.wrapper.wrap(tableNames);
/*     */     }
/*     */     
/* 329 */     this.sql.append(" FROM ").append(ArrayUtil.join((Object[])tableNames, ","));
/*     */     
/* 331 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder where(Condition... conditions) {
/* 342 */     if (ArrayUtil.isNotEmpty((Object[])conditions)) {
/* 343 */       where(buildCondition(conditions));
/*     */     }
/*     */     
/* 346 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder where(String where) {
/* 356 */     if (StrUtil.isNotBlank(where)) {
/* 357 */       this.sql.append(" WHERE ").append(where);
/*     */     }
/* 359 */     return this;
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
/*     */   public <T> SqlBuilder in(String field, T... values) {
/* 372 */     this.sql.append(this.wrapper.wrap(field)).append(" IN ").append("(").append(ArrayUtil.join((Object[])values, ",")).append(")");
/* 373 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder groupBy(String... fields) {
/* 383 */     if (ArrayUtil.isNotEmpty((Object[])fields)) {
/* 384 */       if (null != this.wrapper)
/*     */       {
/* 386 */         fields = this.wrapper.wrap(fields);
/*     */       }
/*     */       
/* 389 */       this.sql.append(" GROUP BY ").append(ArrayUtil.join((Object[])fields, ","));
/*     */     } 
/*     */     
/* 392 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder having(Condition... conditions) {
/* 403 */     if (ArrayUtil.isNotEmpty((Object[])conditions)) {
/* 404 */       having(buildCondition(conditions));
/*     */     }
/*     */     
/* 407 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder having(String having) {
/* 417 */     if (StrUtil.isNotBlank(having)) {
/* 418 */       this.sql.append(" HAVING ").append(having);
/*     */     }
/* 420 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder orderBy(Order... orders) {
/* 430 */     if (ArrayUtil.isEmpty((Object[])orders)) {
/* 431 */       return this;
/*     */     }
/*     */     
/* 434 */     this.sql.append(" ORDER BY ");
/*     */     
/* 436 */     boolean isFirst = true;
/* 437 */     for (Order order : orders) {
/* 438 */       String field = order.getField();
/* 439 */       if (null != this.wrapper)
/*     */       {
/* 441 */         field = this.wrapper.wrap(field);
/*     */       }
/* 443 */       if (!StrUtil.isBlank(field)) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 448 */         if (isFirst) {
/* 449 */           isFirst = false;
/*     */         } else {
/* 451 */           this.sql.append(",");
/*     */         } 
/* 453 */         this.sql.append(field);
/* 454 */         Direction direction = order.getDirection();
/* 455 */         if (null != direction)
/* 456 */           this.sql.append(" ").append(direction); 
/*     */       } 
/*     */     } 
/* 459 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder join(String tableName, Join join) {
/* 470 */     if (StrUtil.isBlank(tableName)) {
/* 471 */       throw new DbRuntimeException("Table name is blank !");
/*     */     }
/*     */     
/* 474 */     if (null != join) {
/* 475 */       this.sql.append(" ").append(join).append(" JOIN ");
/* 476 */       if (null != this.wrapper)
/*     */       {
/* 478 */         tableName = this.wrapper.wrap(tableName);
/*     */       }
/* 480 */       this.sql.append(tableName);
/*     */     } 
/* 482 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder on(Condition... conditions) {
/* 493 */     if (ArrayUtil.isNotEmpty((Object[])conditions)) {
/* 494 */       on(buildCondition(conditions));
/*     */     }
/*     */     
/* 497 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder on(String on) {
/* 508 */     if (StrUtil.isNotBlank(on)) {
/* 509 */       this.sql.append(" ON ").append(on);
/*     */     }
/* 511 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder insertPreFragment(Object sqlFragment) {
/* 522 */     if (null != sqlFragment) {
/* 523 */       this.sql.insert(0, sqlFragment);
/*     */     }
/* 525 */     return this;
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
/*     */   
/*     */   public SqlBuilder append(Object sqlFragment) {
/* 542 */     if (null != sqlFragment) {
/* 543 */       this.sql.append(sqlFragment);
/*     */     }
/* 545 */     return this;
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
/*     */   
/*     */   public SqlBuilder addParams(Object... params) {
/* 562 */     if (ArrayUtil.isNotEmpty(params)) {
/* 563 */       Collections.addAll(this.paramValues, params);
/*     */     }
/* 565 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SqlBuilder query(Query query) {
/* 575 */     return select(query.getFields()).from(query.getTableNames()).where(query.getWhere());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Object> getParamValues() {
/* 585 */     return this.paramValues;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] getParamValueArray() {
/* 594 */     return this.paramValues.toArray(new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String build() {
/* 604 */     return this.sql.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 609 */     return build();
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
/*     */   private String buildCondition(Condition... conditions) {
/* 622 */     if (ArrayUtil.isEmpty((Object[])conditions)) {
/* 623 */       return "";
/*     */     }
/*     */     
/* 626 */     if (null != this.wrapper)
/*     */     {
/* 628 */       conditions = this.wrapper.wrap(conditions);
/*     */     }
/*     */     
/* 631 */     return ConditionBuilder.of(conditions).build(this.paramValues);
/*     */   }
/*     */   
/*     */   public SqlBuilder() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\SqlBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */