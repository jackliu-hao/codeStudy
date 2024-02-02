/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.Entity;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Date;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Date;
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
/*     */ public class SqlUtil
/*     */ {
/*     */   public static String buildEqualsWhere(Entity entity, List<Object> paramValues) {
/*  39 */     if (null == entity || entity.isEmpty()) {
/*  40 */       return "";
/*     */     }
/*     */     
/*  43 */     StringBuilder sb = new StringBuilder(" WHERE ");
/*  44 */     boolean isNotFirst = false;
/*  45 */     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)entity.entrySet()) {
/*  46 */       if (isNotFirst) {
/*  47 */         sb.append(" and ");
/*     */       } else {
/*  49 */         isNotFirst = true;
/*     */       } 
/*  51 */       sb.append("`").append(entry.getKey()).append("`").append(" = ?");
/*  52 */       paramValues.add(entry.getValue());
/*     */     } 
/*     */     
/*  55 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Condition[] buildConditions(Entity entity) {
/*  65 */     if (null == entity || entity.isEmpty()) {
/*  66 */       return null;
/*     */     }
/*     */     
/*  69 */     Condition[] conditions = new Condition[entity.size()];
/*  70 */     int i = 0;
/*     */     
/*  72 */     for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)entity.entrySet()) {
/*  73 */       Object value = entry.getValue();
/*  74 */       if (value instanceof Condition) {
/*  75 */         conditions[i++] = (Condition)value; continue;
/*     */       } 
/*  77 */       conditions[i++] = new Condition(entry.getKey(), value);
/*     */     } 
/*     */ 
/*     */     
/*  81 */     return conditions;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String buildLikeValue(String value, Condition.LikeType likeType, boolean withLikeKeyword) {
/* 107 */     if (null == value) {
/* 108 */       return null;
/*     */     }
/*     */     
/* 111 */     StringBuilder likeValue = StrUtil.builder(new CharSequence[] { withLikeKeyword ? "LIKE " : "" });
/* 112 */     switch (likeType) {
/*     */       case StartWith:
/* 114 */         likeValue.append(value).append('%');
/*     */         break;
/*     */       case EndWith:
/* 117 */         likeValue.append('%').append(value);
/*     */         break;
/*     */       case Contains:
/* 120 */         likeValue.append('%').append(value).append('%');
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 126 */     return likeValue.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String formatSql(String sql) {
/* 136 */     return SqlFormatter.format(sql);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String rowIdToString(RowId rowId) {
/* 146 */     return StrUtil.str(rowId.getBytes(), CharsetUtil.CHARSET_ISO_8859_1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String clobToStr(Clob clob) {
/* 157 */     Reader reader = null;
/*     */     try {
/* 159 */       reader = clob.getCharacterStream();
/* 160 */       return IoUtil.read(reader);
/* 161 */     } catch (SQLException e) {
/* 162 */       throw new DbRuntimeException(e);
/*     */     } finally {
/* 164 */       IoUtil.close(reader);
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
/*     */   public static String blobToStr(Blob blob, Charset charset) {
/* 177 */     InputStream in = null;
/*     */     try {
/* 179 */       in = blob.getBinaryStream();
/* 180 */       return IoUtil.read(in, charset);
/* 181 */     } catch (SQLException e) {
/* 182 */       throw new DbRuntimeException(e);
/*     */     } finally {
/* 184 */       IoUtil.close(in);
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
/*     */   public static Blob createBlob(Connection conn, InputStream dataStream, boolean closeAfterUse) {
/*     */     Blob blob;
/* 199 */     OutputStream out = null;
/*     */     try {
/* 201 */       blob = conn.createBlob();
/* 202 */       out = blob.setBinaryStream(1L);
/* 203 */       IoUtil.copy(dataStream, out);
/* 204 */     } catch (SQLException e) {
/* 205 */       throw new DbRuntimeException(e);
/*     */     } finally {
/* 207 */       IoUtil.close(out);
/* 208 */       if (closeAfterUse) {
/* 209 */         IoUtil.close(dataStream);
/*     */       }
/*     */     } 
/* 212 */     return blob;
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
/*     */   public static Blob createBlob(Connection conn, byte[] data) {
/*     */     Blob blob;
/*     */     try {
/* 226 */       blob = conn.createBlob();
/* 227 */       blob.setBytes(0L, data);
/* 228 */     } catch (SQLException e) {
/* 229 */       throw new DbRuntimeException(e);
/*     */     } 
/* 231 */     return blob;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Date toSqlDate(Date date) {
/* 242 */     return new Date(date.getTime());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Timestamp toSqlTimestamp(Date date) {
/* 253 */     return new Timestamp(date.getTime());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\SqlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */