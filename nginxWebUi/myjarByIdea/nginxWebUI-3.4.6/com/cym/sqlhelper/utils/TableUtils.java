package com.cym.sqlhelper.utils;

import cn.hutool.core.util.ReflectUtil;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.config.Table;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Set;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TableUtils {
   static Logger logger = LoggerFactory.getLogger(TableUtils.class);
   @Inject
   JdbcTemplate jdbcTemplate;
   @Inject
   SqlUtils sqlUtils;

   public void initTable(Class<?> clazz) throws SQLException {
      Table table = (Table)clazz.getAnnotation(Table.class);
      if (table != null) {
         this.sqlUtils.checkOrCreateTable(clazz);
         Set<String> columns = this.jdbcTemplate.queryForColumn(clazz);
         Field[] fields = ReflectUtil.getFields(clazz);
         Field[] var5 = fields;
         int var6 = fields.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Field field = var5[var7];
            if (!field.getName().equals("id")) {
               this.sqlUtils.checkOrCreateColumn(clazz, field.getName(), columns);
            }

            if (field.isAnnotationPresent(InitValue.class)) {
               InitValue defaultValue = (InitValue)field.getAnnotation(InitValue.class);
               if (defaultValue.value() != null) {
                  this.sqlUtils.updateDefaultValue(clazz, field.getName(), defaultValue.value());
               }
            }
         }
      }

   }
}
