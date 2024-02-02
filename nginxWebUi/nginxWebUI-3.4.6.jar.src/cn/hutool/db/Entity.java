/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.lang.Dict;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.sql.SqlUtil;
/*     */ import java.nio.charset.Charset;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.RowId;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.Set;
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
/*     */ public class Entity
/*     */   extends Dict
/*     */ {
/*     */   private static final long serialVersionUID = -1951012511464327448L;
/*     */   private String tableName;
/*     */   private Set<String> fieldNames;
/*     */   
/*     */   public static Entity create() {
/*  42 */     return new Entity();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Entity create(String tableName) {
/*  52 */     return new Entity(tableName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Entity parse(T bean) {
/*  63 */     return create((String)null).parseBean(bean);
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
/*     */   public static <T> Entity parse(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
/*  76 */     return create((String)null).parseBean(bean, isToUnderlineCase, ignoreNullValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Entity parseWithUnderlineCase(T bean) {
/*  87 */     return create((String)null).parseBean(bean, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity(String tableName) {
/* 107 */     this.tableName = tableName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity(String tableName, boolean caseInsensitive) {
/* 118 */     super(caseInsensitive);
/* 119 */     this.tableName = tableName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTableName() {
/* 129 */     return this.tableName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity setTableName(String tableName) {
/* 139 */     this.tableName = tableName;
/* 140 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getFieldNames() {
/* 147 */     return this.fieldNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity setFieldNames(Collection<String> fieldNames) {
/* 157 */     if (CollectionUtil.isNotEmpty(fieldNames)) {
/* 158 */       this.fieldNames = CollectionUtil.newHashSet(true, fieldNames);
/*     */     }
/* 160 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity setFieldNames(String... fieldNames) {
/* 170 */     if (ArrayUtil.isNotEmpty((Object[])fieldNames)) {
/* 171 */       this.fieldNames = CollectionUtil.newLinkedHashSet((Object[])fieldNames);
/*     */     }
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity setFields(Func0<?>... fields) {
/* 183 */     return (Entity)super.setFields((Func0[])fields);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity addFieldNames(String... fieldNames) {
/* 193 */     if (ArrayUtil.isNotEmpty((Object[])fieldNames)) {
/* 194 */       if (null == this.fieldNames) {
/* 195 */         return setFieldNames(fieldNames);
/*     */       }
/* 197 */       Collections.addAll(this.fieldNames, fieldNames);
/*     */     } 
/*     */     
/* 200 */     return this;
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
/*     */   public <T> Entity parseBean(T bean) {
/* 215 */     if (StrUtil.isBlank(this.tableName)) {
/* 216 */       setTableName(StrUtil.lowerFirst(bean.getClass().getSimpleName()));
/*     */     }
/* 218 */     return (Entity)super.parseBean(bean);
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
/*     */   public <T> Entity parseBean(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
/* 233 */     if (StrUtil.isBlank(this.tableName)) {
/* 234 */       String simpleName = bean.getClass().getSimpleName();
/* 235 */       setTableName(isToUnderlineCase ? StrUtil.toUnderlineCase(simpleName) : StrUtil.lowerFirst(simpleName));
/*     */     } 
/* 237 */     return (Entity)super.parseBean(bean, isToUnderlineCase, ignoreNullValue);
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
/*     */   public Entity filter(String... keys) {
/* 249 */     Entity result = new Entity(this.tableName);
/* 250 */     result.setFieldNames(this.fieldNames);
/*     */     
/* 252 */     for (String key : keys) {
/* 253 */       if (containsKey(key)) {
/* 254 */         result.put(key, get(key));
/*     */       }
/*     */     } 
/* 257 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity set(String field, Object value) {
/* 263 */     return (Entity)super.set(field, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public Entity setIgnoreNull(String field, Object value) {
/* 268 */     return (Entity)super.setIgnoreNull(field, value);
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
/*     */   public Clob getClob(String field) {
/* 281 */     return (Clob)get(field, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Blob getBlob(String field) {
/* 292 */     return (Blob)get(field, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Time getTime(String field) {
/* 297 */     Object obj = get(field);
/* 298 */     Time result = null;
/* 299 */     if (null != obj) {
/*     */       try {
/* 301 */         result = (Time)obj;
/* 302 */       } catch (Exception e) {
/*     */         
/* 304 */         result = (Time)ReflectUtil.invoke(obj, "timeValue", new Object[0]);
/*     */       } 
/*     */     }
/* 307 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getDate(String field) {
/* 312 */     Object obj = get(field);
/* 313 */     Date result = null;
/* 314 */     if (null != obj) {
/*     */       try {
/* 316 */         result = (Date)obj;
/* 317 */       } catch (Exception e) {
/*     */         
/* 319 */         result = (Date)ReflectUtil.invoke(obj, "dateValue", new Object[0]);
/*     */       } 
/*     */     }
/* 322 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public Timestamp getTimestamp(String field) {
/* 327 */     Object obj = get(field);
/* 328 */     Timestamp result = null;
/* 329 */     if (null != obj) {
/*     */       try {
/* 331 */         result = (Timestamp)obj;
/* 332 */       } catch (Exception e) {
/*     */         
/* 334 */         result = (Timestamp)ReflectUtil.invoke(obj, "timestampValue", new Object[0]);
/*     */       } 
/*     */     }
/* 337 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStr(String field) {
/* 342 */     return getStr(field, CharsetUtil.CHARSET_UTF_8);
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
/*     */   public String getStr(String field, Charset charset) {
/* 355 */     Object obj = get(field);
/* 356 */     if (obj instanceof Clob)
/* 357 */       return SqlUtil.clobToStr((Clob)obj); 
/* 358 */     if (obj instanceof Blob)
/* 359 */       return SqlUtil.blobToStr((Blob)obj, charset); 
/* 360 */     if (obj instanceof RowId) {
/* 361 */       RowId rowId = (RowId)obj;
/* 362 */       return StrUtil.str(rowId.getBytes(), charset);
/*     */     } 
/* 364 */     return super.getStr(field);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowId getRowId() {
/* 373 */     return getRowId("ROWID");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RowId getRowId(String field) {
/* 383 */     Object obj = get(field);
/* 384 */     if (null == obj) {
/* 385 */       return null;
/*     */     }
/* 387 */     if (obj instanceof RowId) {
/* 388 */       return (RowId)obj;
/*     */     }
/* 390 */     throw new DbRuntimeException("Value of field [{}] is not a rowid!", new Object[] { field });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Entity clone() {
/* 398 */     return (Entity)super.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 404 */     return "Entity {tableName=" + this.tableName + ", fieldNames=" + this.fieldNames + ", fields=" + super.toString() + "}";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\Entity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */