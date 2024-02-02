package cn.hutool.db.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.db.Entity;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HandleHelper {
   public static <T> T handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs, T bean) throws SQLException {
      return handleRow(columnCount, meta, rs).toBeanIgnoreCase(bean);
   }

   public static <T> T handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs, Class<T> beanClass) throws SQLException {
      Assert.notNull(beanClass, "Bean Class must be not null !");
      if (!beanClass.isArray()) {
         Object[] objRow;
         if (Iterable.class.isAssignableFrom(beanClass)) {
            objRow = (Object[])handleRow(columnCount, meta, rs, Object[].class);
            return Convert.convert((Class)beanClass, objRow);
         } else if (beanClass.isAssignableFrom(Entity.class)) {
            return handleRow(columnCount, meta, rs);
         } else if (String.class == beanClass) {
            objRow = (Object[])handleRow(columnCount, meta, rs, Object[].class);
            return StrUtil.join(", ", objRow);
         } else {
            T bean = ReflectUtil.newInstanceIfPossible(beanClass);
            Map<String, PropDesc> propMap = BeanUtil.getBeanDesc(beanClass).getPropMap(true);

            for(int i = 1; i <= columnCount; ++i) {
               String columnLabel = meta.getColumnLabel(i);
               PropDesc pd = (PropDesc)propMap.get(columnLabel);
               if (null == pd) {
                  pd = (PropDesc)propMap.get(StrUtil.toCamelCase(columnLabel));
               }

               Method setter = null == pd ? null : pd.getSetter();
               if (null != setter) {
                  Object value = getColumnValue(rs, i, meta.getColumnType(i), TypeUtil.getFirstParamType(setter));
                  ReflectUtil.invokeWithCheck(bean, setter, value);
               }
            }

            return bean;
         }
      } else {
         Class<?> componentType = beanClass.getComponentType();
         Object[] result = ArrayUtil.newArray(componentType, columnCount);
         int i = 0;

         for(int j = 1; i < columnCount; ++j) {
            result[i] = getColumnValue(rs, j, meta.getColumnType(j), componentType);
            ++i;
         }

         return result;
      }
   }

   public static Entity handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs) throws SQLException {
      return handleRow(columnCount, meta, rs, false);
   }

   public static Entity handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs, boolean caseInsensitive) throws SQLException {
      return handleRow(new Entity((String)null, caseInsensitive), columnCount, meta, rs, true);
   }

   public static <T extends Entity> T handleRow(T row, int columnCount, ResultSetMetaData meta, ResultSet rs, boolean withMetaInfo) throws SQLException {
      for(int i = 1; i <= columnCount; ++i) {
         int type = meta.getColumnType(i);
         row.put(meta.getColumnLabel(i), getColumnValue(rs, i, type, (Type)null));
      }

      if (withMetaInfo) {
         try {
            row.setTableName(meta.getTableName(1));
         } catch (SQLException var7) {
         }

         row.setFieldNames((Collection)row.keySet());
      }

      return row;
   }

   public static Entity handleRow(ResultSet rs) throws SQLException {
      ResultSetMetaData meta = rs.getMetaData();
      int columnCount = meta.getColumnCount();
      return handleRow(columnCount, meta, rs);
   }

   public static List<Object> handleRowToList(ResultSet rs) throws SQLException {
      ResultSetMetaData meta = rs.getMetaData();
      int columnCount = meta.getColumnCount();
      List<Object> row = new ArrayList(columnCount);

      for(int i = 1; i <= columnCount; ++i) {
         row.add(getColumnValue(rs, i, meta.getColumnType(i), (Type)null));
      }

      return row;
   }

   public static <T extends Collection<Entity>> T handleRs(ResultSet rs, T collection) throws SQLException {
      return handleRs(rs, collection, false);
   }

   public static <T extends Collection<Entity>> T handleRs(ResultSet rs, T collection, boolean caseInsensitive) throws SQLException {
      ResultSetMetaData meta = rs.getMetaData();
      int columnCount = meta.getColumnCount();

      while(rs.next()) {
         collection.add(handleRow(columnCount, meta, rs, caseInsensitive));
      }

      return collection;
   }

   public static <E, T extends Collection<E>> T handleRsToBeanList(ResultSet rs, T collection, Class<E> elementBeanType) throws SQLException {
      ResultSetMetaData meta = rs.getMetaData();
      int columnCount = meta.getColumnCount();

      while(rs.next()) {
         collection.add(handleRow(columnCount, meta, rs, elementBeanType));
      }

      return collection;
   }

   private static Object getColumnValue(ResultSet rs, int columnIndex, int type, Type targetColumnType) throws SQLException {
      Object rawValue = null;
      switch (type) {
         case 92:
            rawValue = rs.getTime(columnIndex);
            break;
         case 93:
            try {
               rawValue = rs.getTimestamp(columnIndex);
            } catch (SQLException var6) {
            }
            break;
         default:
            rawValue = rs.getObject(columnIndex);
      }

      return null != targetColumnType && Object.class != targetColumnType ? Convert.convert(targetColumnType, rawValue) : rawValue;
   }
}
