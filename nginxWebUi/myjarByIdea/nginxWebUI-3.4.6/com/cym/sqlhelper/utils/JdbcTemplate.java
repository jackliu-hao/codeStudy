package com.cym.sqlhelper.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.cym.sqlhelper.config.DataSourceEmbed;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.h2.jdbc.JdbcClob;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JdbcTemplate {
   @Inject
   DataSourceEmbed dataSourceEmbed;
   SnowFlake snowFlake = new SnowFlake(1L, 1L);
   static Logger logger = LoggerFactory.getLogger(JdbcTemplate.class);

   public List<Map<String, Object>> queryForList(String formatSql, Object... array) {
      try {
         List<Entity> list = Db.use(this.dataSourceEmbed.getDataSource()).query(formatSql, array);
         List<Map<String, Object>> mapList = new ArrayList();
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            Entity entity = (Entity)var5.next();
            Map<String, Object> map = new HashMap();
            Iterator var8 = entity.entrySet().iterator();

            while(var8.hasNext()) {
               Map.Entry entry = (Map.Entry)var8.next();
               if (entry.getValue() instanceof JdbcClob) {
                  map.put(entry.getKey().toString(), this.clobToStr((JdbcClob)entry.getValue()));
               } else {
                  map.put(entry.getKey().toString(), entry.getValue());
               }
            }

            mapList.add(map);
         }

         return mapList;
      } catch (Exception var10) {
         logger.error((String)var10.getMessage(), (Throwable)var10);
         throw new RuntimeException(var10);
      }
   }

   public String clobToStr(JdbcClob jdbcClob) {
      try {
         StringBuilder builder = new StringBuilder();
         Reader rd = jdbcClob.getCharacterStream();
         char[] str = new char[1];

         while(rd.read(str) != -1) {
            builder.append(new String(str));
         }

         return builder.toString();
      } catch (Exception var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public Set<String> queryForColumn(Class clazz) throws SQLException {
      Set<String> set = new HashSet();
      String uuid = this.snowFlake.nextId();
      Entity entity = new Entity();
      entity.setTableName("`" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "`");
      entity.set("id", uuid);
      Db.use(this.dataSourceEmbed.getDataSource()).insert(entity);
      List<Entity> list = Db.use(this.dataSourceEmbed.getDataSource()).query("select * from `" + StrUtil.toUnderlineCase(clazz.getSimpleName()) + "` where id='" + uuid + "'", new Object[0]);

      Entity entityOne;
      for(Iterator var6 = list.iterator(); var6.hasNext(); set = entityOne.getFieldNames()) {
         entityOne = (Entity)var6.next();
      }

      Db.use(this.dataSourceEmbed.getDataSource()).del(entity);
      return (Set)set;
   }

   public Long queryForCount(String formatSql, Object... array) {
      List<Map<String, Object>> list = this.queryForList(formatSql, array);
      if (list != null && list.size() != 0) {
         Map<String, Object> map = (Map)list.get(0);
         Iterator var5 = map.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<String, Object> entity = (Map.Entry)var5.next();
            if (entity.getValue() instanceof Long) {
               return (Long)entity.getValue();
            }

            if (entity.getValue() instanceof Integer) {
               return ((Integer)entity.getValue()).longValue();
            }

            if (entity.getValue() instanceof Short) {
               return ((Short)entity.getValue()).longValue();
            }
         }
      }

      return 0L;
   }

   public void execute(String formatSql, Object... array) {
      try {
         Db.use(this.dataSourceEmbed.getDataSource()).execute(formatSql, array);
      } catch (SQLException var4) {
         logger.error((String)var4.getMessage(), (Throwable)var4);
      }

   }
}
