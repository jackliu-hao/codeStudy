/*     */ package cn.hutool.db.handler;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.bean.PropDesc;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.TypeUtil;
/*     */ import cn.hutool.db.Entity;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class HandleHelper
/*     */ {
/*     */   public static <T> T handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs, T bean) throws SQLException {
/*  45 */     return (T)handleRow(columnCount, meta, rs).toBeanIgnoreCase(bean);
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
/*     */   public static <T> T handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs, Class<T> beanClass) throws SQLException {
/*  62 */     Assert.notNull(beanClass, "Bean Class must be not null !", new Object[0]);
/*     */     
/*  64 */     if (beanClass.isArray()) {
/*     */       
/*  66 */       Class<?> componentType = beanClass.getComponentType();
/*  67 */       Object[] result = ArrayUtil.newArray(componentType, columnCount);
/*  68 */       for (int k = 0, j = 1; k < columnCount; k++, j++) {
/*  69 */         result[k] = getColumnValue(rs, j, meta.getColumnType(j), componentType);
/*     */       }
/*  71 */       return (T)result;
/*  72 */     }  if (Iterable.class.isAssignableFrom(beanClass)) {
/*     */       
/*  74 */       Object[] objRow = handleRow(columnCount, meta, rs, Object[].class);
/*  75 */       return (T)Convert.convert(beanClass, objRow);
/*  76 */     }  if (beanClass.isAssignableFrom(Entity.class))
/*     */     {
/*  78 */       return (T)handleRow(columnCount, meta, rs); } 
/*  79 */     if (String.class == beanClass) {
/*     */       
/*  81 */       Object[] objRow = handleRow(columnCount, meta, rs, Object[].class);
/*  82 */       return (T)StrUtil.join(", ", objRow);
/*     */     } 
/*     */ 
/*     */     
/*  86 */     T bean = (T)ReflectUtil.newInstanceIfPossible(beanClass);
/*     */     
/*  88 */     Map<String, PropDesc> propMap = BeanUtil.getBeanDesc(beanClass).getPropMap(true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     for (int i = 1; i <= columnCount; i++) {
/*  94 */       String columnLabel = meta.getColumnLabel(i);
/*  95 */       PropDesc pd = propMap.get(columnLabel);
/*  96 */       if (null == pd)
/*     */       {
/*  98 */         pd = propMap.get(StrUtil.toCamelCase(columnLabel));
/*     */       }
/* 100 */       Method setter = (null == pd) ? null : pd.getSetter();
/* 101 */       if (null != setter) {
/* 102 */         Object value = getColumnValue(rs, i, meta.getColumnType(i), TypeUtil.getFirstParamType(setter));
/* 103 */         ReflectUtil.invokeWithCheck(bean, setter, new Object[] { value });
/*     */       } 
/*     */     } 
/* 106 */     return bean;
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
/*     */   public static Entity handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs) throws SQLException {
/* 119 */     return handleRow(columnCount, meta, rs, false);
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
/*     */   public static Entity handleRow(int columnCount, ResultSetMetaData meta, ResultSet rs, boolean caseInsensitive) throws SQLException {
/* 134 */     return handleRow(new Entity(null, caseInsensitive), columnCount, meta, rs, true);
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
/*     */   
/*     */   public static <T extends Entity> T handleRow(T row, int columnCount, ResultSetMetaData meta, ResultSet rs, boolean withMetaInfo) throws SQLException {
/* 152 */     for (int i = 1; i <= columnCount; i++) {
/* 153 */       int type = meta.getColumnType(i);
/* 154 */       row.put(meta.getColumnLabel(i), getColumnValue(rs, i, type, null));
/*     */     } 
/* 156 */     if (withMetaInfo) {
/*     */       try {
/* 158 */         row.setTableName(meta.getTableName(1));
/* 159 */       } catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */       
/* 163 */       row.setFieldNames(row.keySet());
/*     */     } 
/* 165 */     return row;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Entity handleRow(ResultSet rs) throws SQLException {
/* 176 */     ResultSetMetaData meta = rs.getMetaData();
/* 177 */     int columnCount = meta.getColumnCount();
/* 178 */     return handleRow(columnCount, meta, rs);
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
/*     */   public static List<Object> handleRowToList(ResultSet rs) throws SQLException {
/* 190 */     ResultSetMetaData meta = rs.getMetaData();
/* 191 */     int columnCount = meta.getColumnCount();
/* 192 */     List<Object> row = new ArrayList(columnCount);
/* 193 */     for (int i = 1; i <= columnCount; i++) {
/* 194 */       row.add(getColumnValue(rs, i, meta.getColumnType(i), null));
/*     */     }
/*     */     
/* 197 */     return row;
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
/*     */   public static <T extends java.util.Collection<Entity>> T handleRs(ResultSet rs, T collection) throws SQLException {
/* 210 */     return handleRs(rs, collection, false);
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
/*     */   public static <T extends java.util.Collection<Entity>> T handleRs(ResultSet rs, T collection, boolean caseInsensitive) throws SQLException {
/* 225 */     ResultSetMetaData meta = rs.getMetaData();
/* 226 */     int columnCount = meta.getColumnCount();
/*     */     
/* 228 */     while (rs.next()) {
/* 229 */       collection.add(handleRow(columnCount, meta, rs, caseInsensitive));
/*     */     }
/*     */     
/* 232 */     return collection;
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
/*     */   public static <E, T extends java.util.Collection<E>> T handleRsToBeanList(ResultSet rs, T collection, Class<E> elementBeanType) throws SQLException {
/* 248 */     ResultSetMetaData meta = rs.getMetaData();
/* 249 */     int columnCount = meta.getColumnCount();
/*     */     
/* 251 */     while (rs.next()) {
/* 252 */       collection.add(handleRow(columnCount, meta, rs, elementBeanType));
/*     */     }
/*     */     
/* 255 */     return collection;
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
/*     */   private static Object getColumnValue(ResultSet rs, int columnIndex, int type, Type targetColumnType) throws SQLException {
/* 271 */     Object rawValue = null;
/* 272 */     switch (type) {
/*     */       case 93:
/*     */         try {
/* 275 */           rawValue = rs.getTimestamp(columnIndex);
/* 276 */         } catch (SQLException sQLException) {}
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 92:
/* 282 */         rawValue = rs.getTime(columnIndex);
/*     */         break;
/*     */       default:
/* 285 */         rawValue = rs.getObject(columnIndex); break;
/*     */     } 
/* 287 */     if (null == targetColumnType || Object.class == targetColumnType)
/*     */     {
/* 289 */       return rawValue;
/*     */     }
/*     */     
/* 292 */     return Convert.convert(targetColumnType, rawValue);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\HandleHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */