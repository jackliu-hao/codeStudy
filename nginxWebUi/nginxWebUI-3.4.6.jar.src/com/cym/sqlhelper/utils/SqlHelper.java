/*     */ package com.cym.sqlhelper.utils;
/*     */ 
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.sqlhelper.bean.Sort;
/*     */ import com.cym.sqlhelper.bean.Update;
/*     */ import com.cym.sqlhelper.config.InitValue;
/*     */ import com.cym.sqlhelper.reflection.ReflectionUtil;
/*     */ import com.cym.sqlhelper.reflection.SerializableFunction;
/*     */ import java.lang.reflect.Field;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.noear.solon.annotation.Component;
/*     */ import org.noear.solon.annotation.Init;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class SqlHelper
/*     */   extends SqlUtils
/*     */ {
/*     */   @Inject("${project.beanPackage}")
/*     */   String packageName;
/*     */   @Inject
/*     */   JdbcTemplate jdbcTemplate;
/*     */   @Inject
/*     */   TableUtils tableUtils;
/*  46 */   static Logger logger = LoggerFactory.getLogger(SqlHelper.class);
/*  47 */   SnowFlake snowFlake = new SnowFlake(1L, 1L);
/*     */   
/*     */   @Init
/*     */   public void init() throws SQLException {
/*  51 */     Set<Class<?>> set = ClassUtil.scanPackage(this.packageName);
/*  52 */     for (Class<?> clazz : set) {
/*  53 */       this.tableUtils.initTable(clazz);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String insertOrUpdate(Object object) {
/*  64 */     Long time = Long.valueOf(System.currentTimeMillis());
/*  65 */     String id = (String)ReflectUtil.getFieldValue(object, "id");
/*  66 */     Object objectOrg = StrUtil.isNotEmpty(id) ? findById(id, object.getClass()) : null;
/*     */     try {
/*  68 */       if (objectOrg == null) {
/*     */ 
/*     */         
/*  71 */         if (ReflectUtil.getField(object.getClass(), "createTime") != null) {
/*  72 */           ReflectUtil.setFieldValue(object, "createTime", time);
/*     */         }
/*  74 */         if (ReflectUtil.getField(object.getClass(), "updateTime") != null) {
/*  75 */           ReflectUtil.setFieldValue(object, "updateTime", time);
/*     */         }
/*     */         
/*  78 */         setDefaultVaule(object);
/*     */         
/*  80 */         ReflectUtil.setFieldValue(object, "id", this.snowFlake.nextId());
/*     */         
/*  82 */         String sql = "";
/*  83 */         List<String> fieldsPart = new ArrayList<>();
/*  84 */         List<String> placeHolder = new ArrayList<>();
/*  85 */         List<Object> paramValues = new ArrayList();
/*     */         
/*  87 */         Field[] fields = ReflectUtil.getFields(object.getClass());
/*  88 */         for (Field field : fields) {
/*  89 */           fieldsPart.add("`" + StrUtil.toUnderlineCase(field.getName()) + "`");
/*  90 */           placeHolder.add("?");
/*  91 */           paramValues.add(ReflectUtil.getFieldValue(object, field));
/*     */         } 
/*     */         
/*  94 */         sql = "INSERT INTO `" + StrUtil.toUnderlineCase(object.getClass().getSimpleName()) + "` (" + StrUtil.join(",", fieldsPart) + ") VALUES (" + StrUtil.join(",", placeHolder) + ")";
/*     */         
/*  96 */         logQuery(formatSql(sql), paramValues.toArray());
/*  97 */         this.jdbcTemplate.execute(formatSql(sql), paramValues.toArray());
/*     */       }
/*     */       else {
/*     */         
/* 101 */         Field[] fields = ReflectUtil.getFields(object.getClass());
/*     */ 
/*     */         
/* 104 */         if (ReflectUtil.getField(object.getClass(), "updateTime") != null) {
/* 105 */           ReflectUtil.setFieldValue(object, "updateTime", time);
/*     */         }
/*     */         
/* 108 */         List<String> fieldsPart = new ArrayList<>();
/* 109 */         List<Object> paramValues = new ArrayList();
/*     */         
/* 111 */         for (Field field : fields) {
/* 112 */           if (!field.getName().equals("id") && ReflectUtil.getFieldValue(object, field) != null) {
/* 113 */             fieldsPart.add("`" + StrUtil.toUnderlineCase(field.getName()) + "`=?");
/* 114 */             paramValues.add(ReflectUtil.getFieldValue(object, field));
/*     */           } 
/*     */         } 
/* 117 */         paramValues.add(id);
/*     */         
/* 119 */         String sql = "UPDATE `" + StrUtil.toUnderlineCase(object.getClass().getSimpleName()) + "` SET " + StrUtil.join(",", fieldsPart) + " WHERE id = ?";
/*     */         
/* 121 */         logQuery(formatSql(sql), paramValues.toArray());
/* 122 */         this.jdbcTemplate.execute(formatSql(sql), paramValues.toArray());
/*     */       } 
/* 124 */     } catch (Exception e) {
/* 125 */       e.printStackTrace();
/* 126 */       throw new RuntimeException(e);
/*     */     } 
/*     */     
/* 129 */     return (String)ReflectUtil.getFieldValue(object, "id");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String insert(Object object) {
/* 138 */     String id = (String)ReflectUtil.getFieldValue(object, "id");
/* 139 */     Object objectOrg = StrUtil.isNotEmpty(id) ? findById(id, object.getClass()) : null;
/* 140 */     if (objectOrg != null)
/*     */     {
/* 142 */       ReflectUtil.setFieldValue(object, "id", this.snowFlake.nextId());
/*     */     }
/*     */ 
/*     */     
/* 146 */     if (ReflectUtil.getFieldValue(object, "id") == null) {
/* 147 */       ReflectUtil.setFieldValue(object, "id", this.snowFlake.nextId());
/*     */     }
/*     */     
/* 150 */     insertOrUpdate(object);
/*     */     
/* 152 */     return (String)ReflectUtil.getFieldValue(object, "id");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> void insertAll(List<T> list) {
/* 163 */     Long time = Long.valueOf(System.currentTimeMillis());
/*     */     
/* 165 */     Map<String, Object> idMap = new HashMap<>();
/* 166 */     for (T object : list) {
/* 167 */       if (ReflectUtil.getFieldValue(object, "id") != null) {
/* 168 */         String id = (String)ReflectUtil.getFieldValue(object, "id");
/* 169 */         Object objectOrg = StrUtil.isNotEmpty(id) ? findById(id, object.getClass()) : null;
/* 170 */         idMap.put((String)ReflectUtil.getFieldValue(object, "id"), objectOrg);
/*     */       } 
/*     */     } 
/*     */     
/* 174 */     for (T object : list) {
/* 175 */       if (ReflectUtil.getFieldValue(object, "id") != null && idMap.get(ReflectUtil.getFieldValue(object, "id")) != null)
/*     */       {
/* 177 */         ReflectUtil.setFieldValue(object, "id", this.snowFlake.nextId());
/*     */       }
/*     */ 
/*     */       
/* 181 */       if (ReflectUtil.getFieldValue(object, "id") == null) {
/* 182 */         ReflectUtil.setFieldValue(object, "id", this.snowFlake.nextId());
/*     */       }
/*     */ 
/*     */       
/* 186 */       if (ReflectUtil.getField(object.getClass(), "createTime") != null) {
/* 187 */         ReflectUtil.setFieldValue(object, "createTime", time);
/*     */       }
/* 189 */       if (ReflectUtil.getField(object.getClass(), "updateTime") != null) {
/* 190 */         ReflectUtil.setFieldValue(object, "updateTime", time);
/*     */       }
/*     */       
/* 193 */       setDefaultVaule(object);
/*     */     } 
/*     */     
/* 196 */     String sqls = null;
/* 197 */     for (T object : list) {
/* 198 */       Field[] fields = ReflectUtil.getFields(object.getClass());
/*     */       
/* 200 */       List<String> fieldsPart = new ArrayList<>();
/* 201 */       List<String> placeHolder = new ArrayList<>();
/*     */       
/* 203 */       List<Object> params = new ArrayList();
/* 204 */       for (Field field : fields) {
/* 205 */         fieldsPart.add("`" + StrUtil.toUnderlineCase(field.getName()) + "`");
/* 206 */         placeHolder.add("?");
/* 207 */         params.add(ReflectUtil.getFieldValue(object, field));
/*     */       } 
/*     */       
/* 210 */       if (sqls == null) {
/* 211 */         sqls = "INSERT INTO `" + StrUtil.toUnderlineCase(object.getClass().getSimpleName()) + "` (" + StrUtil.join(",", fieldsPart) + ") VALUES (" + StrUtil.join(",", placeHolder) + ")";
/*     */       }
/*     */       
/* 214 */       this.jdbcTemplate.execute(formatSql(sqls), params.toArray());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateById(Object object) {
/* 225 */     if (StrUtil.isEmpty((String)ReflectUtil.getFieldValue(object, "id"))) {
/*     */       return;
/*     */     }
/* 228 */     insertOrUpdate(object);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateMulti(ConditionWrapper conditionWrapper, Update update, Class<?> clazz) {
/* 239 */     if (update == null || update.getSets().size() == 0) {
/*     */       return;
/*     */     }
/* 242 */     List<String> fieldsPart = new ArrayList<>();
/* 243 */     List<String> paramValues = new ArrayList<>();
/* 244 */     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)update.getSets().entrySet()) {
/* 245 */       if (entry.getKey() != null && entry.getValue() != null) {
/* 246 */         fieldsPart.add("`" + StrUtil.toUnderlineCase(entry.getKey()) + "`=?");
/* 247 */         paramValues.add(entry.getValue().toString());
/*     */       } 
/*     */     } 
/*     */     
/* 251 */     String sql = "UPDATE `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` SET " + StrUtil.join(",", fieldsPart);
/* 252 */     if (conditionWrapper != null && conditionWrapper.notEmpty()) {
/* 253 */       sql = sql + " WHERE " + conditionWrapper.build(paramValues);
/*     */     }
/*     */     
/* 256 */     logQuery(formatSql(sql), paramValues.toArray());
/* 257 */     this.jdbcTemplate.execute(formatSql(sql), paramValues.toArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addCountById(String id, String property, Long count, Class<?> clazz) {
/* 266 */     String sql = "UPDATE `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` SET `" + property + "` = CAST(`" + property + "` AS DECIMAL(30,10)) + ? WHERE `id` =  ?";
/* 267 */     Object[] params = { count, id };
/* 268 */     logQuery(formatSql(sql), params);
/* 269 */     this.jdbcTemplate.execute(formatSql(sql), params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> void addCountById(String id, SerializableFunction<T, R> property, Long count, Class<?> clazz) {
/* 278 */     addCountById(id, ReflectionUtil.getFieldName(property), count, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateAllColumnById(Object object) {
/* 287 */     if (StrUtil.isEmpty((String)ReflectUtil.getFieldValue(object, "id"))) {
/*     */       return;
/*     */     }
/*     */     
/* 291 */     Field[] fields = ReflectUtil.getFields(object.getClass());
/*     */     
/* 293 */     List<String> fieldsPart = new ArrayList<>();
/* 294 */     List<Object> paramValues = new ArrayList();
/*     */     
/* 296 */     for (Field field : fields) {
/* 297 */       if (!field.getName().equals("id")) {
/* 298 */         fieldsPart.add("`" + StrUtil.toUnderlineCase(field.getName()) + "`=?");
/* 299 */         paramValues.add(ReflectUtil.getFieldValue(object, field));
/*     */       } 
/*     */     } 
/* 302 */     paramValues.add(ReflectUtil.getFieldValue(object, "id"));
/*     */     
/* 304 */     String sql = "UPDATE `" + StrUtil.toUnderlineCase(object.getClass().getSimpleName()) + "` SET " + StrUtil.join(",", fieldsPart) + " WHERE id = ?";
/*     */     
/* 306 */     logQuery(formatSql(sql), paramValues.toArray());
/* 307 */     this.jdbcTemplate.execute(formatSql(sql), paramValues.toArray());
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
/*     */   public void deleteById(String id, Class<?> clazz) {
/* 319 */     if (StrUtil.isEmpty(id)) {
/*     */       return;
/*     */     }
/* 322 */     deleteByQuery((new ConditionAndWrapper()).eq("id", id), clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteByIds(Collection<String> ids, Class<?> clazz) {
/* 332 */     if (ids == null || ids.size() == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 336 */     deleteByQuery((new ConditionAndWrapper()).in("id", ids), clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteByIds(String[] ids, Class<?> clazz) {
/* 346 */     deleteByIds(Arrays.asList(ids), clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleteByQuery(ConditionWrapper conditionWrapper, Class<?> clazz) {
/* 356 */     List<String> values = new ArrayList<>();
/* 357 */     String sql = "DELETE FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
/* 358 */     if (conditionWrapper != null && conditionWrapper.notEmpty()) {
/* 359 */       sql = sql + " WHERE " + conditionWrapper.build(values);
/*     */     }
/* 361 */     logQuery(formatSql(sql), values.toArray());
/* 362 */     this.jdbcTemplate.execute(formatSql(sql), values.toArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setDefaultVaule(Object object) {
/* 371 */     Field[] fields = ReflectUtil.getFields(object.getClass());
/* 372 */     for (Field field : fields) {
/*     */       
/* 374 */       if (field.isAnnotationPresent((Class)InitValue.class)) {
/* 375 */         InitValue defaultValue = field.<InitValue>getAnnotation(InitValue.class);
/*     */         
/* 377 */         String value = defaultValue.value();
/*     */         
/* 379 */         if (ReflectUtil.getFieldValue(object, field) == null) {
/*     */           
/* 381 */           Class<?> type = field.getType();
/* 382 */           if (type.equals(String.class)) {
/* 383 */             ReflectUtil.setFieldValue(object, field, value);
/*     */           }
/* 385 */           if (type.equals(Short.class)) {
/* 386 */             ReflectUtil.setFieldValue(object, field, Short.valueOf(Short.parseShort(value)));
/*     */           }
/* 388 */           if (type.equals(Integer.class)) {
/* 389 */             ReflectUtil.setFieldValue(object, field, Integer.valueOf(Integer.parseInt(value)));
/*     */           }
/* 391 */           if (type.equals(Long.class)) {
/* 392 */             ReflectUtil.setFieldValue(object, field, Long.valueOf(Long.parseLong(value)));
/*     */           }
/* 394 */           if (type.equals(Float.class)) {
/* 395 */             ReflectUtil.setFieldValue(object, field, Float.valueOf(Float.parseFloat(value)));
/*     */           }
/* 397 */           if (type.equals(Double.class)) {
/* 398 */             ReflectUtil.setFieldValue(object, field, Double.valueOf(Double.parseDouble(value)));
/*     */           }
/* 400 */           if (type.equals(Boolean.class)) {
/* 401 */             ReflectUtil.setFieldValue(object, field, Boolean.valueOf(Boolean.parseBoolean(value)));
/*     */           }
/*     */         } 
/*     */       } 
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
/*     */   public Page findPage(ConditionWrapper conditionWrapper, Sort sort, Page page, Class<?> clazz) {
/* 417 */     List<String> values = new ArrayList<>();
/*     */     
/* 419 */     Long count = findCountByQuery(conditionWrapper, clazz);
/*     */     
/* 421 */     String sql = "SELECT * FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
/* 422 */     if (conditionWrapper != null && conditionWrapper.notEmpty()) {
/* 423 */       sql = sql + " WHERE " + conditionWrapper.build(values);
/*     */     }
/* 425 */     if (sort != null) {
/* 426 */       sql = sql + " " + sort.toString();
/*     */     } else {
/* 428 */       sql = sql + " ORDER BY id DESC";
/*     */     } 
/* 430 */     sql = sql + " LIMIT " + ((page.getCurr().intValue() - 1) * page.getLimit().intValue()) + "," + page.getLimit();
/*     */     
/* 432 */     page.setCount(count);
/*     */     
/* 434 */     logQuery(formatSql(sql), values.toArray());
/* 435 */     page.setRecords(buildObjects(this.jdbcTemplate.queryForList(formatSql(sql), values.toArray()), clazz));
/*     */     
/* 437 */     return page;
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
/*     */   public Page findPage(Sort sort, Page page, Class<?> clazz) {
/* 449 */     return findPage((ConditionWrapper)null, sort, page, clazz);
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
/*     */   public Page findPage(ConditionWrapper conditionWrapper, Page page, Class<?> clazz) {
/* 461 */     return findPage(conditionWrapper, (Sort)null, page, clazz);
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
/*     */   public Page findPage(Page page, Class<?> clazz) {
/* 473 */     return findPage((ConditionWrapper)null, (Sort)null, page, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T findById(String id, Class<T> clazz) {
/* 484 */     if (StrUtil.isEmpty(id)) {
/* 485 */       return null;
/*     */     }
/*     */     
/* 488 */     return findOneByQuery((new ConditionAndWrapper()).eq("id", id), clazz);
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
/*     */   public <T> T findOneByQuery(ConditionWrapper conditionWrapper, Sort sort, Class<T> clazz) {
/* 500 */     List<String> values = new ArrayList<>();
/* 501 */     List<T> list = new ArrayList<>();
/* 502 */     String sql = "SELECT * FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
/* 503 */     if (conditionWrapper != null && conditionWrapper.notEmpty()) {
/* 504 */       sql = sql + " WHERE " + conditionWrapper.build(values);
/*     */     }
/* 506 */     if (sort != null) {
/* 507 */       sql = sql + " " + sort.toString();
/*     */     } else {
/* 509 */       sql = sql + " ORDER BY id DESC";
/*     */     } 
/* 511 */     sql = sql + " limit 1";
/*     */     
/* 513 */     logQuery(formatSql(sql), values.toArray());
/* 514 */     list = buildObjects(this.jdbcTemplate.queryForList(formatSql(sql), values.toArray()), clazz);
/* 515 */     return (list.size() > 0) ? list.get(0) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T findOneByQuery(Sort sort, Class<T> clazz) {
/* 526 */     return findOneByQuery((ConditionWrapper)null, sort, clazz);
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
/*     */   public <T> T findOneByQuery(ConditionWrapper conditionWrapper, Class<T> clazz) {
/* 538 */     return findOneByQuery(conditionWrapper, (Sort)null, clazz);
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
/*     */   public <T> List<T> findListByQuery(ConditionWrapper conditionWrapper, Sort sort, Class<T> clazz) {
/* 551 */     List<String> values = new ArrayList<>();
/*     */     
/* 553 */     String sql = "SELECT * FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
/* 554 */     if (conditionWrapper != null && conditionWrapper.notEmpty()) {
/* 555 */       sql = sql + " WHERE " + conditionWrapper.build(values);
/*     */     }
/* 557 */     if (sort != null) {
/* 558 */       sql = sql + " " + sort.toString();
/*     */     } else {
/* 560 */       sql = sql + " ORDER BY id DESC";
/*     */     } 
/*     */     
/* 563 */     logQuery(formatSql(sql), values.toArray());
/* 564 */     return buildObjects(this.jdbcTemplate.queryForList(formatSql(sql), values.toArray()), clazz);
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
/*     */   public <T> List<T> findListByQuery(ConditionWrapper conditionWrapper, Class<T> clazz) {
/* 576 */     return findListByQuery(conditionWrapper, (Sort)null, clazz);
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
/*     */   public <T> List<T> findListByQuery(Sort sort, Class<T> clazz) {
/* 588 */     return findListByQuery((ConditionWrapper)null, sort, clazz);
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
/*     */   public <T> List<T> findPropertiesByQuery(ConditionWrapper conditionWrapper, Class<?> documentClass, String property, Class<T> propertyClass) {
/* 602 */     List<?> list = findListByQuery(conditionWrapper, documentClass);
/* 603 */     List<T> propertyList = extractProperty(list, property, propertyClass);
/*     */     
/* 605 */     return propertyList;
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
/*     */   public <T, R> List<T> findPropertiesByQuery(ConditionWrapper conditionWrapper, Class<?> documentClass, SerializableFunction<T, R> property, Class<T> propertyClass) {
/* 619 */     return findPropertiesByQuery(conditionWrapper, documentClass, ReflectionUtil.getFieldName(property), propertyClass);
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
/*     */   public List<String> findPropertiesByQuery(ConditionWrapper conditionWrapper, Class<?> documentClass, String property) {
/* 632 */     return findPropertiesByQuery(conditionWrapper, documentClass, property, String.class);
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
/*     */   public <T, R> List<String> findPropertiesByQuery(ConditionWrapper conditionWrapper, Class<?> documentClass, SerializableFunction<T, R> property) {
/* 645 */     return findPropertiesByQuery(conditionWrapper, documentClass, ReflectionUtil.getFieldName(property), String.class);
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
/*     */   public List<String> findPropertiesByIds(Collection<String> ids, Class<?> documentClass, String property) {
/* 658 */     if (ids == null || ids.size() == 0) {
/* 659 */       return new ArrayList<>();
/*     */     }
/*     */     
/* 662 */     ConditionAndWrapper ConditionAndWrapper = new ConditionAndWrapper();
/* 663 */     ConditionAndWrapper.in("id", ids);
/*     */     
/* 665 */     return findPropertiesByQuery(ConditionAndWrapper, documentClass, property, String.class);
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
/*     */   public <T, R> List<String> findPropertiesByIds(Collection<String> ids, Class<?> documentClass, SerializableFunction<T, R> property) {
/* 678 */     return findPropertiesByIds(ids, documentClass, ReflectionUtil.getFieldName(property));
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
/*     */   public List<String> findPropertiesByIds(String[] ids, Class<?> documentClass, String property) {
/* 691 */     return findPropertiesByIds(Arrays.asList(ids), documentClass, property);
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
/*     */   public <T, R> List<String> findPropertiesByIds(String[] ids, Class<?> documentClass, SerializableFunction<T, R> property) {
/* 704 */     return findPropertiesByIds(Arrays.asList(ids), documentClass, ReflectionUtil.getFieldName(property));
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
/*     */   public List<String> findIdsByQuery(ConditionWrapper conditionWrapper, Class<?> clazz) {
/* 716 */     return findPropertiesByQuery(conditionWrapper, clazz, "id");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> findListByIds(Collection<String> ids, Class<T> clazz) {
/* 727 */     return findListByIds(ids, (Sort)null, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> findListByIds(String[] ids, Class<T> clazz) {
/* 738 */     return findListByIds(Arrays.asList(ids), (Sort)null, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> findListByIds(Collection<String> ids, Sort sort, Class<T> clazz) {
/* 749 */     if (ids == null || ids.size() == 0) {
/* 750 */       return new ArrayList<>();
/*     */     }
/*     */     
/* 753 */     ConditionAndWrapper ConditionAndWrapper = new ConditionAndWrapper();
/* 754 */     ConditionAndWrapper.in("id", ids);
/*     */     
/* 756 */     return findListByQuery(ConditionAndWrapper, sort, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> findListByIds(String[] ids, Sort sort, Class<T> clazz) {
/* 767 */     return findListByIds(Arrays.asList(ids), sort, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> findAll(Class<T> clazz) {
/* 778 */     return findAll((Sort)null, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> findAll(Sort sort, Class<T> clazz) {
/* 789 */     return findListByQuery((ConditionWrapper)null, sort, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> findAllIds(Class<?> clazz) {
/* 799 */     return findIdsByQuery((ConditionWrapper)null, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long findCountByQuery(ConditionWrapper conditionWrapper, Class<?> clazz) {
/* 810 */     List<String> values = new ArrayList<>();
/* 811 */     String sql = "SELECT COUNT(*) FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
/* 812 */     if (conditionWrapper != null && conditionWrapper.notEmpty()) {
/* 813 */       sql = sql + " WHERE " + conditionWrapper.build(values);
/*     */     }
/*     */     
/* 816 */     logQuery(formatSql(sql), values.toArray());
/* 817 */     return this.jdbcTemplate.queryForCount(formatSql(sql), values.toArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long findAllCount(Class<?> clazz) {
/* 827 */     return findCountByQuery((ConditionWrapper)null, clazz);
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
/*     */   private <T> List<T> extractProperty(List<?> list, String property, Class<T> clazz) {
/* 839 */     Set<T> rs = new HashSet<>();
/* 840 */     for (Object object : list) {
/* 841 */       Object value = ReflectUtil.getFieldValue(object, property);
/* 842 */       if (value != null && value.getClass().equals(clazz)) {
/* 843 */         rs.add((T)value);
/*     */       }
/*     */     } 
/*     */     
/* 847 */     return new ArrayList<>(rs);
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
/*     */   private <T> List<T> buildObjects(List<Map<String, Object>> queryForList, Class<T> clazz) {
/* 859 */     List<T> list = new ArrayList<>();
/*     */     
/*     */     try {
/* 862 */       Field[] fields = ReflectUtil.getFields(clazz);
/*     */       
/* 864 */       for (Map<String, Object> map : queryForList) {
/* 865 */         Object obj = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*     */         
/* 867 */         for (Map.Entry<String, Object> entry : map.entrySet()) {
/* 868 */           String mapKey = entry.getKey();
/* 869 */           Object mapValue = entry.getValue();
/*     */           
/* 871 */           for (Field field : fields) {
/* 872 */             if (StrUtil.toUnderlineCase(field.getName()).equals(mapKey)) {
/* 873 */               ReflectUtil.setFieldValue(obj, field.getName(), mapValue);
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 880 */         list.add((T)obj);
/*     */       }
/*     */     
/* 883 */     } catch (Exception e) {
/* 884 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 887 */     return list;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\SqlHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */