/*     */ package com.cym.sqlhelper.utils;
/*     */ 
/*     */ import com.cym.sqlhelper.reflection.ReflectionUtil;
/*     */ import com.cym.sqlhelper.reflection.SerializableFunction;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
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
/*     */ public class ConditionAndWrapper
/*     */   extends ConditionWrapper
/*     */ {
/*     */   public ConditionAndWrapper and(ConditionWrapper conditionWrapper) {
/*  21 */     this.list.add(conditionWrapper);
/*  22 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper eq(String column, Object params) {
/*  33 */     super.eq(column, params);
/*  34 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper eq(SerializableFunction<T, R> column, Object params) {
/*  45 */     super.eq(ReflectionUtil.getFieldName(column), params);
/*  46 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper ne(String column, Object params) {
/*  57 */     super.ne(column, params);
/*  58 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper ne(SerializableFunction<T, R> column, Object params) {
/*  69 */     super.ne(ReflectionUtil.getFieldName(column), params);
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper lt(String column, Object params) {
/*  81 */     super.lt(column, params);
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper lt(SerializableFunction<T, R> column, Object params) {
/*  93 */     super.lt(ReflectionUtil.getFieldName(column), params);
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper lte(String column, Object params) {
/* 105 */     super.lte(column, params);
/* 106 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper lte(SerializableFunction<T, R> column, Object params) {
/* 117 */     super.lte(ReflectionUtil.getFieldName(column), params);
/* 118 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper gt(String column, Object params) {
/* 129 */     super.gt(column, params);
/* 130 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper gt(SerializableFunction<T, R> column, Object params) {
/* 141 */     super.gt(ReflectionUtil.getFieldName(column), params);
/* 142 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper gte(String column, Object params) {
/* 153 */     super.gte(column, params);
/* 154 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper gte(SerializableFunction<T, R> column, Object params) {
/* 165 */     super.gte(ReflectionUtil.getFieldName(column), params);
/* 166 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper like(String column, String params) {
/* 177 */     super.like(column, params);
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper like(SerializableFunction<T, R> column, String params) {
/* 189 */     super.like(ReflectionUtil.getFieldName(column), params);
/* 190 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper in(String column, Collection<?> params) {
/* 201 */     super.in(column, params);
/* 202 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper in(SerializableFunction<T, R> column, Collection<?> params) {
/* 213 */     super.in(ReflectionUtil.getFieldName(column), params);
/* 214 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper in(String column, Object[] params) {
/* 225 */     super.in(column, Arrays.asList(params));
/* 226 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper in(SerializableFunction<T, R> column, Object[] params) {
/* 237 */     super.in(ReflectionUtil.getFieldName(column), Arrays.asList(params));
/* 238 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper nin(String column, Collection<?> params) {
/* 249 */     super.nin(column, params);
/* 250 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper nin(SerializableFunction<T, R> column, Collection<?> params) {
/* 261 */     super.nin(ReflectionUtil.getFieldName(column), params);
/* 262 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionAndWrapper nin(String column, Object[] params) {
/* 273 */     super.nin(column, Arrays.asList(params));
/* 274 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionAndWrapper nin(SerializableFunction<T, R> column, Object[] params) {
/* 285 */     super.nin(ReflectionUtil.getFieldName(column), Arrays.asList(params));
/* 286 */     return this;
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
/*     */   public ConditionAndWrapper isNull(String column) {
/* 298 */     super.isNull(column);
/* 299 */     return this;
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
/*     */   public <T, R> ConditionAndWrapper isNull(SerializableFunction<T, R> column) {
/* 311 */     super.isNull(ReflectionUtil.getFieldName(column));
/* 312 */     return this;
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
/*     */   public ConditionAndWrapper isNotNull(String column) {
/* 324 */     super.isNotNull(column);
/* 325 */     return this;
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
/*     */   public <T, R> ConditionAndWrapper isNotNull(SerializableFunction<T, R> column) {
/* 337 */     super.isNotNull(ReflectionUtil.getFieldName(column));
/* 338 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\ConditionAndWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */