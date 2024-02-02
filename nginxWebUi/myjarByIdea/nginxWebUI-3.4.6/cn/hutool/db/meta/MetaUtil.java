package cn.hutool.db.meta;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public class MetaUtil {
   public static List<String> getTables(DataSource ds) {
      return getTables(ds, TableType.TABLE);
   }

   public static List<String> getTables(DataSource ds, TableType... types) {
      return getTables(ds, (String)null, (String)null, types);
   }

   public static List<String> getTables(DataSource ds, String schema, TableType... types) {
      return getTables(ds, schema, (String)null, types);
   }

   public static List<String> getTables(DataSource ds, String schema, String tableName, TableType... types) {
      List<String> tables = new ArrayList();
      Connection conn = null;

      try {
         conn = ds.getConnection();
         String catalog = getCatalog(conn);
         if (null == schema) {
            schema = getSchema(conn);
         }

         DatabaseMetaData metaData = conn.getMetaData();
         ResultSet rs = metaData.getTables(catalog, schema, tableName, Convert.toStrArray(types));
         Throwable var9 = null;

         try {
            if (null != rs) {
               while(rs.next()) {
                  String table = rs.getString("TABLE_NAME");
                  if (StrUtil.isNotBlank(table)) {
                     tables.add(table);
                  }
               }
            }
         } catch (Throwable var27) {
            var9 = var27;
            throw var27;
         } finally {
            if (rs != null) {
               if (var9 != null) {
                  try {
                     rs.close();
                  } catch (Throwable var26) {
                     var9.addSuppressed(var26);
                  }
               } else {
                  rs.close();
               }
            }

         }
      } catch (Exception var29) {
         throw new DbRuntimeException("Get tables error!", var29);
      } finally {
         DbUtil.close(conn);
      }

      return tables;
   }

   public static String[] getColumnNames(ResultSet rs) throws DbRuntimeException {
      try {
         ResultSetMetaData rsmd = rs.getMetaData();
         int columnCount = rsmd.getColumnCount();
         String[] labelNames = new String[columnCount];

         for(int i = 0; i < labelNames.length; ++i) {
            labelNames[i] = rsmd.getColumnLabel(i + 1);
         }

         return labelNames;
      } catch (Exception var5) {
         throw new DbRuntimeException("Get colunms error!", var5);
      }
   }

   public static String[] getColumnNames(DataSource ds, String tableName) {
      List<String> columnNames = new ArrayList();
      Connection conn = null;

      String[] var30;
      try {
         conn = ds.getConnection();
         String catalog = getCatalog(conn);
         String schema = getSchema(conn);
         DatabaseMetaData metaData = conn.getMetaData();
         ResultSet rs = metaData.getColumns(catalog, schema, tableName, (String)null);
         Throwable var8 = null;

         try {
            if (null != rs) {
               while(rs.next()) {
                  columnNames.add(rs.getString("COLUMN_NAME"));
               }
            }
         } catch (Throwable var26) {
            var8 = var26;
            throw var26;
         } finally {
            if (rs != null) {
               if (var8 != null) {
                  try {
                     rs.close();
                  } catch (Throwable var25) {
                     var8.addSuppressed(var25);
                  }
               } else {
                  rs.close();
               }
            }

         }

         var30 = (String[])columnNames.toArray(new String[0]);
      } catch (Exception var28) {
         throw new DbRuntimeException("Get columns error!", var28);
      } finally {
         DbUtil.close(conn);
      }

      return var30;
   }

   public static Entity createLimitedEntity(DataSource ds, String tableName) {
      String[] columnNames = getColumnNames(ds, tableName);
      return Entity.create(tableName).setFieldNames(columnNames);
   }

   public static Table getTableMeta(DataSource ds, String tableName) {
      return getTableMeta(ds, (String)null, (String)null, tableName);
   }

   public static Table getTableMeta(DataSource ds, String catalog, String schema, String tableName) {
      Table table = Table.create(tableName);
      Connection conn = null;

      try {
         conn = ds.getConnection();
         if (null == catalog) {
            catalog = getCatalog(conn);
         }

         table.setCatalog(catalog);
         if (null == schema) {
            schema = getSchema(conn);
         }

         table.setSchema(schema);
         DatabaseMetaData metaData = conn.getMetaData();
         ResultSet rs = metaData.getTables(catalog, schema, tableName, new String[]{TableType.TABLE.value()});
         Throwable var8 = null;

         try {
            if (null != rs && rs.next()) {
               table.setComment(rs.getString("REMARKS"));
            }
         } catch (Throwable var107) {
            var8 = var107;
            throw var107;
         } finally {
            if (rs != null) {
               if (var8 != null) {
                  try {
                     rs.close();
                  } catch (Throwable var106) {
                     var8.addSuppressed(var106);
                  }
               } else {
                  rs.close();
               }
            }

         }

         rs = metaData.getPrimaryKeys(catalog, schema, tableName);
         var8 = null;

         try {
            if (null != rs) {
               while(rs.next()) {
                  table.addPk(rs.getString("COLUMN_NAME"));
               }
            }
         } catch (Throwable var109) {
            var8 = var109;
            throw var109;
         } finally {
            if (rs != null) {
               if (var8 != null) {
                  try {
                     rs.close();
                  } catch (Throwable var105) {
                     var8.addSuppressed(var105);
                  }
               } else {
                  rs.close();
               }
            }

         }

         rs = metaData.getColumns(catalog, schema, tableName, (String)null);
         var8 = null;

         try {
            if (null != rs) {
               while(rs.next()) {
                  table.setColumn(Column.create(table, rs));
               }
            }
         } catch (Throwable var111) {
            var8 = var111;
            throw var111;
         } finally {
            if (rs != null) {
               if (var8 != null) {
                  try {
                     rs.close();
                  } catch (Throwable var104) {
                     var8.addSuppressed(var104);
                  }
               } else {
                  rs.close();
               }
            }

         }

         rs = metaData.getIndexInfo(catalog, schema, tableName, false, false);
         var8 = null;

         try {
            Map<String, IndexInfo> indexInfoMap = new LinkedHashMap();
            if (null != rs) {
               while(rs.next()) {
                  if (0 != rs.getShort("TYPE")) {
                     String indexName = rs.getString("INDEX_NAME");
                     String key = StrUtil.join("&", new Object[]{tableName, indexName});
                     IndexInfo indexInfo = (IndexInfo)indexInfoMap.get(key);
                     if (null == indexInfo) {
                        indexInfo = new IndexInfo(rs.getBoolean("NON_UNIQUE"), indexName, tableName, schema, catalog);
                        indexInfoMap.put(key, indexInfo);
                     }

                     indexInfo.getColumnIndexInfoList().add(ColumnIndexInfo.create(rs));
                  }
               }
            }

            table.setIndexInfoList(ListUtil.toList(indexInfoMap.values()));
         } catch (Throwable var113) {
            var8 = var113;
            throw var113;
         } finally {
            if (rs != null) {
               if (var8 != null) {
                  try {
                     rs.close();
                  } catch (Throwable var103) {
                     var8.addSuppressed(var103);
                  }
               } else {
                  rs.close();
               }
            }

         }
      } catch (SQLException var115) {
         throw new DbRuntimeException("Get columns error!", var115);
      } finally {
         DbUtil.close(conn);
      }

      return table;
   }

   /** @deprecated */
   @Deprecated
   public static String getCataLog(Connection conn) {
      return getCatalog(conn);
   }

   public static String getCatalog(Connection conn) {
      if (null == conn) {
         return null;
      } else {
         try {
            return conn.getCatalog();
         } catch (SQLException var2) {
            return null;
         }
      }
   }

   public static String getSchema(Connection conn) {
      if (null == conn) {
         return null;
      } else {
         try {
            return conn.getSchema();
         } catch (SQLException var2) {
            return null;
         }
      }
   }
}
