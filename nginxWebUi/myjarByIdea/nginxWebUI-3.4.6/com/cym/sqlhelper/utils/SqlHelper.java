package com.cym.sqlhelper.utils;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.bean.Sort;
import com.cym.sqlhelper.bean.Update;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.reflection.ReflectionUtil;
import com.cym.sqlhelper.reflection.SerializableFunction;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SqlHelper extends SqlUtils {
   @Inject("${project.beanPackage}")
   String packageName;
   @Inject
   JdbcTemplate jdbcTemplate;
   @Inject
   TableUtils tableUtils;
   static Logger logger = LoggerFactory.getLogger(SqlHelper.class);
   SnowFlake snowFlake = new SnowFlake(1L, 1L);

   @Init
   public void init() throws SQLException {
      Set<Class<?>> set = ClassUtil.scanPackage(this.packageName);
      Iterator var2 = set.iterator();

      while(var2.hasNext()) {
         Class<?> clazz = (Class)var2.next();
         this.tableUtils.initTable(clazz);
      }

   }

   public String insertOrUpdate(Object object) {
      Long time = System.currentTimeMillis();
      String id = (String)ReflectUtil.getFieldValue(object, "id");
      Object objectOrg = StrUtil.isNotEmpty(id) ? this.findById(id, object.getClass()) : null;

      try {
         ArrayList fieldsPart;
         ArrayList placeHolder;
         if (objectOrg == null) {
            if (ReflectUtil.getField(object.getClass(), "createTime") != null) {
               ReflectUtil.setFieldValue(object, (String)"createTime", time);
            }

            if (ReflectUtil.getField(object.getClass(), "updateTime") != null) {
               ReflectUtil.setFieldValue(object, (String)"updateTime", time);
            }

            this.setDefaultVaule(object);
            ReflectUtil.setFieldValue(object, (String)"id", this.snowFlake.nextId());
            String sql = "";
            fieldsPart = new ArrayList();
            placeHolder = new ArrayList();
            List<Object> paramValues = new ArrayList();
            Field[] fields = ReflectUtil.getFields(object.getClass());
            Field[] var10 = fields;
            int var11 = fields.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               Field field = var10[var12];
               fieldsPart.add("`" + StrUtil.toUnderlineCase(field.getName()) + "`");
               placeHolder.add("?");
               paramValues.add(ReflectUtil.getFieldValue(object, field));
            }

            sql = "INSERT INTO `" + StrUtil.toUnderlineCase(object.getClass().getSimpleName()) + "` (" + StrUtil.join(",", fieldsPart) + ") VALUES (" + StrUtil.join(",", placeHolder) + ")";
            this.logQuery(this.formatSql(sql), paramValues.toArray());
            this.jdbcTemplate.execute(this.formatSql(sql), paramValues.toArray());
         } else {
            Field[] fields = ReflectUtil.getFields(object.getClass());
            if (ReflectUtil.getField(object.getClass(), "updateTime") != null) {
               ReflectUtil.setFieldValue(object, (String)"updateTime", time);
            }

            fieldsPart = new ArrayList();
            placeHolder = new ArrayList();
            Field[] var16 = fields;
            int var18 = fields.length;

            for(int var19 = 0; var19 < var18; ++var19) {
               Field field = var16[var19];
               if (!field.getName().equals("id") && ReflectUtil.getFieldValue(object, field) != null) {
                  fieldsPart.add("`" + StrUtil.toUnderlineCase(field.getName()) + "`=?");
                  placeHolder.add(ReflectUtil.getFieldValue(object, field));
               }
            }

            placeHolder.add(id);
            String sql = "UPDATE `" + StrUtil.toUnderlineCase(object.getClass().getSimpleName()) + "` SET " + StrUtil.join(",", fieldsPart) + " WHERE id = ?";
            this.logQuery(this.formatSql(sql), placeHolder.toArray());
            this.jdbcTemplate.execute(this.formatSql(sql), placeHolder.toArray());
         }
      } catch (Exception var14) {
         var14.printStackTrace();
         throw new RuntimeException(var14);
      }

      return (String)ReflectUtil.getFieldValue(object, "id");
   }

   public String insert(Object object) {
      String id = (String)ReflectUtil.getFieldValue(object, "id");
      Object objectOrg = StrUtil.isNotEmpty(id) ? this.findById(id, object.getClass()) : null;
      if (objectOrg != null) {
         ReflectUtil.setFieldValue(object, (String)"id", this.snowFlake.nextId());
      }

      if (ReflectUtil.getFieldValue(object, "id") == null) {
         ReflectUtil.setFieldValue(object, (String)"id", this.snowFlake.nextId());
      }

      this.insertOrUpdate(object);
      return (String)ReflectUtil.getFieldValue(object, "id");
   }

   public <T> void insertAll(List<T> list) {
      Long time = System.currentTimeMillis();
      Map<String, Object> idMap = new HashMap();
      Iterator var4 = list.iterator();

      Object object;
      while(var4.hasNext()) {
         object = var4.next();
         if (ReflectUtil.getFieldValue(object, "id") != null) {
            String id = (String)ReflectUtil.getFieldValue(object, "id");
            Object objectOrg = StrUtil.isNotEmpty(id) ? this.findById(id, object.getClass()) : null;
            idMap.put((String)ReflectUtil.getFieldValue(object, "id"), objectOrg);
         }
      }

      for(var4 = list.iterator(); var4.hasNext(); this.setDefaultVaule(object)) {
         object = var4.next();
         if (ReflectUtil.getFieldValue(object, "id") != null && idMap.get((String)ReflectUtil.getFieldValue(object, "id")) != null) {
            ReflectUtil.setFieldValue(object, (String)"id", this.snowFlake.nextId());
         }

         if (ReflectUtil.getFieldValue(object, "id") == null) {
            ReflectUtil.setFieldValue(object, (String)"id", this.snowFlake.nextId());
         }

         if (ReflectUtil.getField(object.getClass(), "createTime") != null) {
            ReflectUtil.setFieldValue(object, (String)"createTime", time);
         }

         if (ReflectUtil.getField(object.getClass(), "updateTime") != null) {
            ReflectUtil.setFieldValue(object, (String)"updateTime", time);
         }
      }

      String sqls = null;

      ArrayList params;
      for(Iterator var16 = list.iterator(); var16.hasNext(); this.jdbcTemplate.execute(this.formatSql(sqls), params.toArray())) {
         Object object = var16.next();
         Field[] fields = ReflectUtil.getFields(object.getClass());
         List<String> fieldsPart = new ArrayList();
         List<String> placeHolder = new ArrayList();
         params = new ArrayList();
         Field[] var11 = fields;
         int var12 = fields.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            Field field = var11[var13];
            fieldsPart.add("`" + StrUtil.toUnderlineCase(field.getName()) + "`");
            placeHolder.add("?");
            params.add(ReflectUtil.getFieldValue(object, field));
         }

         if (sqls == null) {
            sqls = "INSERT INTO `" + StrUtil.toUnderlineCase(object.getClass().getSimpleName()) + "` (" + StrUtil.join(",", fieldsPart) + ") VALUES (" + StrUtil.join(",", placeHolder) + ")";
         }
      }

   }

   public void updateById(Object object) {
      if (!StrUtil.isEmpty((String)ReflectUtil.getFieldValue(object, "id"))) {
         this.insertOrUpdate(object);
      }
   }

   public void updateMulti(ConditionWrapper conditionWrapper, Update update, Class<?> clazz) {
      if (update != null && update.getSets().size() != 0) {
         List<String> fieldsPart = new ArrayList();
         List<String> paramValues = new ArrayList();
         Iterator var6 = update.getSets().entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry)var6.next();
            if (entry.getKey() != null && entry.getValue() != null) {
               fieldsPart.add("`" + StrUtil.toUnderlineCase((CharSequence)entry.getKey()) + "`=?");
               paramValues.add(entry.getValue().toString());
            }
         }

         String sql = "UPDATE `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` SET " + StrUtil.join(",", fieldsPart);
         if (conditionWrapper != null && conditionWrapper.notEmpty()) {
            sql = sql + " WHERE " + conditionWrapper.build(paramValues);
         }

         this.logQuery(this.formatSql(sql), paramValues.toArray());
         this.jdbcTemplate.execute(this.formatSql(sql), paramValues.toArray());
      }
   }

   public void addCountById(String id, String property, Long count, Class<?> clazz) {
      String sql = "UPDATE `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` SET `" + property + "` = CAST(`" + property + "` AS DECIMAL(30,10)) + ? WHERE `id` =  ?";
      Object[] params = new Object[]{count, id};
      this.logQuery(this.formatSql(sql), params);
      this.jdbcTemplate.execute(this.formatSql(sql), params);
   }

   public <T, R> void addCountById(String id, SerializableFunction<T, R> property, Long count, Class<?> clazz) {
      this.addCountById(id, ReflectionUtil.getFieldName(property), count, clazz);
   }

   public void updateAllColumnById(Object object) {
      if (!StrUtil.isEmpty((String)ReflectUtil.getFieldValue(object, "id"))) {
         Field[] fields = ReflectUtil.getFields(object.getClass());
         List<String> fieldsPart = new ArrayList();
         List<Object> paramValues = new ArrayList();
         Field[] var5 = fields;
         int var6 = fields.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Field field = var5[var7];
            if (!field.getName().equals("id")) {
               fieldsPart.add("`" + StrUtil.toUnderlineCase(field.getName()) + "`=?");
               paramValues.add(ReflectUtil.getFieldValue(object, field));
            }
         }

         paramValues.add((String)ReflectUtil.getFieldValue(object, "id"));
         String sql = "UPDATE `" + StrUtil.toUnderlineCase(object.getClass().getSimpleName()) + "` SET " + StrUtil.join(",", fieldsPart) + " WHERE id = ?";
         this.logQuery(this.formatSql(sql), paramValues.toArray());
         this.jdbcTemplate.execute(this.formatSql(sql), paramValues.toArray());
      }
   }

   public void deleteById(String id, Class<?> clazz) {
      if (!StrUtil.isEmpty(id)) {
         this.deleteByQuery((new ConditionAndWrapper()).eq((String)"id", id), clazz);
      }
   }

   public void deleteByIds(Collection<String> ids, Class<?> clazz) {
      if (ids != null && ids.size() != 0) {
         this.deleteByQuery((new ConditionAndWrapper()).in("id", ids), clazz);
      }
   }

   public void deleteByIds(String[] ids, Class<?> clazz) {
      this.deleteByIds((Collection)Arrays.asList(ids), clazz);
   }

   public void deleteByQuery(ConditionWrapper conditionWrapper, Class<?> clazz) {
      List<String> values = new ArrayList();
      String sql = "DELETE FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
      if (conditionWrapper != null && conditionWrapper.notEmpty()) {
         sql = sql + " WHERE " + conditionWrapper.build(values);
      }

      this.logQuery(this.formatSql(sql), values.toArray());
      this.jdbcTemplate.execute(this.formatSql(sql), values.toArray());
   }

   private void setDefaultVaule(Object object) {
      Field[] fields = ReflectUtil.getFields(object.getClass());
      Field[] var3 = fields;
      int var4 = fields.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Field field = var3[var5];
         if (field.isAnnotationPresent(InitValue.class)) {
            InitValue defaultValue = (InitValue)field.getAnnotation(InitValue.class);
            String value = defaultValue.value();
            if (ReflectUtil.getFieldValue(object, field) == null) {
               Class<?> type = field.getType();
               if (type.equals(String.class)) {
                  ReflectUtil.setFieldValue(object, (Field)field, value);
               }

               if (type.equals(Short.class)) {
                  ReflectUtil.setFieldValue(object, (Field)field, Short.parseShort(value));
               }

               if (type.equals(Integer.class)) {
                  ReflectUtil.setFieldValue(object, (Field)field, Integer.parseInt(value));
               }

               if (type.equals(Long.class)) {
                  ReflectUtil.setFieldValue(object, (Field)field, Long.parseLong(value));
               }

               if (type.equals(Float.class)) {
                  ReflectUtil.setFieldValue(object, (Field)field, Float.parseFloat(value));
               }

               if (type.equals(Double.class)) {
                  ReflectUtil.setFieldValue(object, (Field)field, Double.parseDouble(value));
               }

               if (type.equals(Boolean.class)) {
                  ReflectUtil.setFieldValue(object, (Field)field, Boolean.parseBoolean(value));
               }
            }
         }
      }

   }

   public Page findPage(ConditionWrapper conditionWrapper, Sort sort, Page page, Class<?> clazz) {
      List<String> values = new ArrayList();
      Long count = this.findCountByQuery(conditionWrapper, clazz);
      String sql = "SELECT * FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
      if (conditionWrapper != null && conditionWrapper.notEmpty()) {
         sql = sql + " WHERE " + conditionWrapper.build(values);
      }

      if (sort != null) {
         sql = sql + " " + sort.toString();
      } else {
         sql = sql + " ORDER BY id DESC";
      }

      sql = sql + " LIMIT " + (page.getCurr() - 1) * page.getLimit() + "," + page.getLimit();
      page.setCount(count);
      this.logQuery(this.formatSql(sql), values.toArray());
      page.setRecords(this.buildObjects(this.jdbcTemplate.queryForList(this.formatSql(sql), values.toArray()), clazz));
      return page;
   }

   public Page findPage(Sort sort, Page page, Class<?> clazz) {
      return this.findPage((ConditionWrapper)null, sort, page, clazz);
   }

   public Page findPage(ConditionWrapper conditionWrapper, Page page, Class<?> clazz) {
      return this.findPage(conditionWrapper, (Sort)null, page, clazz);
   }

   public Page findPage(Page page, Class<?> clazz) {
      return this.findPage((ConditionWrapper)null, (Sort)null, page, clazz);
   }

   public <T> T findById(String id, Class<T> clazz) {
      return StrUtil.isEmpty(id) ? null : this.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"id", id), clazz);
   }

   public <T> T findOneByQuery(ConditionWrapper conditionWrapper, Sort sort, Class<T> clazz) {
      List<String> values = new ArrayList();
      new ArrayList();
      String sql = "SELECT * FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
      if (conditionWrapper != null && conditionWrapper.notEmpty()) {
         sql = sql + " WHERE " + conditionWrapper.build(values);
      }

      if (sort != null) {
         sql = sql + " " + sort.toString();
      } else {
         sql = sql + " ORDER BY id DESC";
      }

      sql = sql + " limit 1";
      this.logQuery(this.formatSql(sql), values.toArray());
      List<T> list = this.buildObjects(this.jdbcTemplate.queryForList(this.formatSql(sql), values.toArray()), clazz);
      return list.size() > 0 ? list.get(0) : null;
   }

   public <T> T findOneByQuery(Sort sort, Class<T> clazz) {
      return this.findOneByQuery((ConditionWrapper)null, sort, clazz);
   }

   public <T> T findOneByQuery(ConditionWrapper conditionWrapper, Class<T> clazz) {
      return this.findOneByQuery(conditionWrapper, (Sort)null, clazz);
   }

   public <T> List<T> findListByQuery(ConditionWrapper conditionWrapper, Sort sort, Class<T> clazz) {
      List<String> values = new ArrayList();
      String sql = "SELECT * FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
      if (conditionWrapper != null && conditionWrapper.notEmpty()) {
         sql = sql + " WHERE " + conditionWrapper.build(values);
      }

      if (sort != null) {
         sql = sql + " " + sort.toString();
      } else {
         sql = sql + " ORDER BY id DESC";
      }

      this.logQuery(this.formatSql(sql), values.toArray());
      return this.buildObjects(this.jdbcTemplate.queryForList(this.formatSql(sql), values.toArray()), clazz);
   }

   public <T> List<T> findListByQuery(ConditionWrapper conditionWrapper, Class<T> clazz) {
      return this.findListByQuery(conditionWrapper, (Sort)null, clazz);
   }

   public <T> List<T> findListByQuery(Sort sort, Class<T> clazz) {
      return this.findListByQuery((ConditionWrapper)null, sort, clazz);
   }

   public <T> List<T> findPropertiesByQuery(ConditionWrapper conditionWrapper, Class<?> documentClass, String property, Class<T> propertyClass) {
      List<?> list = this.findListByQuery(conditionWrapper, documentClass);
      List<T> propertyList = this.extractProperty(list, property, propertyClass);
      return propertyList;
   }

   public <T, R> List<T> findPropertiesByQuery(ConditionWrapper conditionWrapper, Class<?> documentClass, SerializableFunction<T, R> property, Class<T> propertyClass) {
      return this.findPropertiesByQuery(conditionWrapper, documentClass, ReflectionUtil.getFieldName(property), propertyClass);
   }

   public List<String> findPropertiesByQuery(ConditionWrapper conditionWrapper, Class<?> documentClass, String property) {
      return this.findPropertiesByQuery(conditionWrapper, documentClass, property, String.class);
   }

   public <T, R> List<String> findPropertiesByQuery(ConditionWrapper conditionWrapper, Class<?> documentClass, SerializableFunction<T, R> property) {
      return this.findPropertiesByQuery(conditionWrapper, documentClass, ReflectionUtil.getFieldName(property), String.class);
   }

   public List<String> findPropertiesByIds(Collection<String> ids, Class<?> documentClass, String property) {
      if (ids != null && ids.size() != 0) {
         ConditionAndWrapper ConditionAndWrapper = new ConditionAndWrapper();
         ConditionAndWrapper.in("id", ids);
         return this.findPropertiesByQuery(ConditionAndWrapper, documentClass, (String)property, String.class);
      } else {
         return new ArrayList();
      }
   }

   public <T, R> List<String> findPropertiesByIds(Collection<String> ids, Class<?> documentClass, SerializableFunction<T, R> property) {
      return this.findPropertiesByIds(ids, documentClass, ReflectionUtil.getFieldName(property));
   }

   public List<String> findPropertiesByIds(String[] ids, Class<?> documentClass, String property) {
      return this.findPropertiesByIds((Collection)Arrays.asList(ids), documentClass, (String)property);
   }

   public <T, R> List<String> findPropertiesByIds(String[] ids, Class<?> documentClass, SerializableFunction<T, R> property) {
      return this.findPropertiesByIds((Collection)Arrays.asList(ids), documentClass, (String)ReflectionUtil.getFieldName(property));
   }

   public List<String> findIdsByQuery(ConditionWrapper conditionWrapper, Class<?> clazz) {
      return this.findPropertiesByQuery(conditionWrapper, clazz, "id");
   }

   public <T> List<T> findListByIds(Collection<String> ids, Class<T> clazz) {
      return this.findListByIds((Collection)ids, (Sort)null, clazz);
   }

   public <T> List<T> findListByIds(String[] ids, Class<T> clazz) {
      return this.findListByIds((Collection)Arrays.asList(ids), (Sort)null, clazz);
   }

   public <T> List<T> findListByIds(Collection<String> ids, Sort sort, Class<T> clazz) {
      if (ids != null && ids.size() != 0) {
         ConditionAndWrapper ConditionAndWrapper = new ConditionAndWrapper();
         ConditionAndWrapper.in("id", ids);
         return this.findListByQuery(ConditionAndWrapper, sort, clazz);
      } else {
         return new ArrayList();
      }
   }

   public <T> List<T> findListByIds(String[] ids, Sort sort, Class<T> clazz) {
      return this.findListByIds((Collection)Arrays.asList(ids), sort, clazz);
   }

   public <T> List<T> findAll(Class<T> clazz) {
      return this.findAll((Sort)null, clazz);
   }

   public <T> List<T> findAll(Sort sort, Class<T> clazz) {
      return this.findListByQuery((ConditionWrapper)null, sort, clazz);
   }

   public List<String> findAllIds(Class<?> clazz) {
      return this.findIdsByQuery((ConditionWrapper)null, clazz);
   }

   public Long findCountByQuery(ConditionWrapper conditionWrapper, Class<?> clazz) {
      List<String> values = new ArrayList();
      String sql = "SELECT COUNT(*) FROM `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`";
      if (conditionWrapper != null && conditionWrapper.notEmpty()) {
         sql = sql + " WHERE " + conditionWrapper.build(values);
      }

      this.logQuery(this.formatSql(sql), values.toArray());
      return this.jdbcTemplate.queryForCount(this.formatSql(sql), values.toArray());
   }

   public Long findAllCount(Class<?> clazz) {
      return this.findCountByQuery((ConditionWrapper)null, clazz);
   }

   private <T> List<T> extractProperty(List<?> list, String property, Class<T> clazz) {
      Set<T> rs = new HashSet();
      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         Object object = var5.next();
         Object value = ReflectUtil.getFieldValue(object, property);
         if (value != null && value.getClass().equals(clazz)) {
            rs.add(value);
         }
      }

      return new ArrayList(rs);
   }

   private <T> List<T> buildObjects(List<Map<String, Object>> queryForList, Class<T> clazz) {
      List<T> list = new ArrayList();

      try {
         Field[] fields = ReflectUtil.getFields(clazz);

         Object obj;
         label39:
         for(Iterator var5 = queryForList.iterator(); var5.hasNext(); list.add(obj)) {
            Map<String, Object> map = (Map)var5.next();
            obj = clazz.getDeclaredConstructor().newInstance();
            Iterator var8 = map.entrySet().iterator();

            while(true) {
               while(true) {
                  if (!var8.hasNext()) {
                     continue label39;
                  }

                  Map.Entry<String, Object> entry = (Map.Entry)var8.next();
                  String mapKey = (String)entry.getKey();
                  Object mapValue = entry.getValue();
                  Field[] var12 = fields;
                  int var13 = fields.length;

                  for(int var14 = 0; var14 < var13; ++var14) {
                     Field field = var12[var14];
                     if (StrUtil.toUnderlineCase(field.getName()).equals(mapKey)) {
                        ReflectUtil.setFieldValue(obj, field.getName(), mapValue);
                        break;
                     }
                  }
               }
            }
         }
      } catch (Exception var16) {
         var16.printStackTrace();
      }

      return list;
   }
}
