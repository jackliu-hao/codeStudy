/*     */ package cn.hutool.db.meta;
/*     */ 
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.DbUtil;
/*     */ import cn.hutool.db.Entity;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.sql.DataSource;
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
/*     */ public class MetaUtil
/*     */ {
/*     */   public static List<String> getTables(DataSource ds) {
/*  41 */     return getTables(ds, new TableType[] { TableType.TABLE });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> getTables(DataSource ds, TableType... types) {
/*  52 */     return getTables(ds, null, null, types);
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
/*     */   public static List<String> getTables(DataSource ds, String schema, TableType... types) {
/*  65 */     return getTables(ds, schema, null, types);
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
/*     */   public static List<String> getTables(DataSource ds, String schema, String tableName, TableType... types) {
/*  79 */     List<String> tables = new ArrayList<>();
/*  80 */     Connection conn = null;
/*     */     try {
/*  82 */       conn = ds.getConnection();
/*     */ 
/*     */       
/*  85 */       String catalog = getCatalog(conn);
/*  86 */       if (null == schema) {
/*  87 */         schema = getSchema(conn);
/*     */       }
/*     */       
/*  90 */       DatabaseMetaData metaData = conn.getMetaData();
/*  91 */       try (ResultSet rs = metaData.getTables(catalog, schema, tableName, Convert.toStrArray(types))) {
/*  92 */         if (null != rs)
/*     */         {
/*  94 */           while (rs.next()) {
/*  95 */             String table = rs.getString("TABLE_NAME");
/*  96 */             if (StrUtil.isNotBlank(table)) {
/*  97 */               tables.add(table);
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/* 102 */     } catch (Exception e) {
/* 103 */       throw new DbRuntimeException("Get tables error!", e);
/*     */     } finally {
/* 105 */       DbUtil.close(new Object[] { conn });
/*     */     } 
/* 107 */     return tables;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] getColumnNames(ResultSet rs) throws DbRuntimeException {
/*     */     try {
/* 119 */       ResultSetMetaData rsmd = rs.getMetaData();
/* 120 */       int columnCount = rsmd.getColumnCount();
/* 121 */       String[] labelNames = new String[columnCount];
/* 122 */       for (int i = 0; i < labelNames.length; i++) {
/* 123 */         labelNames[i] = rsmd.getColumnLabel(i + 1);
/*     */       }
/* 125 */       return labelNames;
/* 126 */     } catch (Exception e) {
/* 127 */       throw new DbRuntimeException("Get colunms error!", e);
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
/*     */   public static String[] getColumnNames(DataSource ds, String tableName) {
/* 140 */     List<String> columnNames = new ArrayList<>();
/* 141 */     Connection conn = null;
/*     */     try {
/* 143 */       conn = ds.getConnection();
/*     */ 
/*     */       
/* 146 */       String catalog = getCatalog(conn);
/* 147 */       String schema = getSchema(conn);
/*     */       
/* 149 */       DatabaseMetaData metaData = conn.getMetaData();
/* 150 */       try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, null)) {
/* 151 */         if (null != rs) {
/* 152 */           while (rs.next()) {
/* 153 */             columnNames.add(rs.getString("COLUMN_NAME"));
/*     */           }
/*     */         }
/*     */       } 
/* 157 */       return columnNames.<String>toArray(new String[0]);
/* 158 */     } catch (Exception e) {
/* 159 */       throw new DbRuntimeException("Get columns error!", e);
/*     */     } finally {
/* 161 */       DbUtil.close(new Object[] { conn });
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
/*     */   public static Entity createLimitedEntity(DataSource ds, String tableName) {
/* 174 */     String[] columnNames = getColumnNames(ds, tableName);
/* 175 */     return Entity.create(tableName).setFieldNames(columnNames);
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
/*     */   public static Table getTableMeta(DataSource ds, String tableName) {
/* 191 */     return getTableMeta(ds, null, null, tableName);
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
/*     */   
/*     */   public static Table getTableMeta(DataSource ds, String catalog, String schema, String tableName) {
/* 210 */     Table table = Table.create(tableName);
/* 211 */     Connection conn = null;
/*     */     try {
/* 213 */       conn = ds.getConnection();
/*     */ 
/*     */       
/* 216 */       if (null == catalog) {
/* 217 */         catalog = getCatalog(conn);
/*     */       }
/* 219 */       table.setCatalog(catalog);
/* 220 */       if (null == schema) {
/* 221 */         schema = getSchema(conn);
/*     */       }
/* 223 */       table.setSchema(schema);
/*     */       
/* 225 */       DatabaseMetaData metaData = conn.getMetaData();
/*     */ 
/*     */       
/* 228 */       try (ResultSet rs = metaData.getTables(catalog, schema, tableName, new String[] { TableType.TABLE.value() })) {
/* 229 */         if (null != rs && 
/* 230 */           rs.next()) {
/* 231 */           table.setComment(rs.getString("REMARKS"));
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 237 */       try (ResultSet rs = metaData.getPrimaryKeys(catalog, schema, tableName)) {
/* 238 */         if (null != rs) {
/* 239 */           while (rs.next()) {
/* 240 */             table.addPk(rs.getString("COLUMN_NAME"));
/*     */           }
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 246 */       try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, null)) {
/* 247 */         if (null != rs) {
/* 248 */           while (rs.next()) {
/* 249 */             table.setColumn(Column.create(table, rs));
/*     */           }
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 255 */       try (ResultSet rs = metaData.getIndexInfo(catalog, schema, tableName, false, false)) {
/* 256 */         Map<String, IndexInfo> indexInfoMap = new LinkedHashMap<>();
/* 257 */         if (null != rs) {
/* 258 */           while (rs.next()) {
/*     */             
/* 260 */             if (0 == rs.getShort("TYPE")) {
/*     */               continue;
/*     */             }
/*     */             
/* 264 */             String indexName = rs.getString("INDEX_NAME");
/* 265 */             String key = StrUtil.join("&", new Object[] { tableName, indexName });
/*     */             
/* 267 */             IndexInfo indexInfo = indexInfoMap.get(key);
/* 268 */             if (null == indexInfo) {
/* 269 */               indexInfo = new IndexInfo(rs.getBoolean("NON_UNIQUE"), indexName, tableName, schema, catalog);
/* 270 */               indexInfoMap.put(key, indexInfo);
/*     */             } 
/* 272 */             indexInfo.getColumnIndexInfoList().add(ColumnIndexInfo.create(rs));
/*     */           } 
/*     */         }
/* 275 */         table.setIndexInfoList(ListUtil.toList(indexInfoMap.values()));
/*     */       } 
/* 277 */     } catch (SQLException e) {
/* 278 */       throw new DbRuntimeException("Get columns error!", e);
/*     */     } finally {
/* 280 */       DbUtil.close(new Object[] { conn });
/*     */     } 
/*     */     
/* 283 */     return table;
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
/*     */   @Deprecated
/*     */   public static String getCataLog(Connection conn) {
/* 296 */     return getCatalog(conn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getCatalog(Connection conn) {
/* 307 */     if (null == conn) {
/* 308 */       return null;
/*     */     }
/*     */     try {
/* 311 */       return conn.getCatalog();
/* 312 */     } catch (SQLException sQLException) {
/*     */ 
/*     */ 
/*     */       
/* 316 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getSchema(Connection conn) {
/* 327 */     if (null == conn) {
/* 328 */       return null;
/*     */     }
/*     */     try {
/* 331 */       return conn.getSchema();
/* 332 */     } catch (SQLException sQLException) {
/*     */ 
/*     */ 
/*     */       
/* 336 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\meta\MetaUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */