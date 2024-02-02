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
/*     */ public class ConditionOrWrapper
/*     */   extends ConditionWrapper
/*     */ {
/*     */   public ConditionOrWrapper or(ConditionWrapper conditionWrapper) {
/*  20 */     this.list.add(conditionWrapper);
/*  21 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper eq(String column, Object params) {
/*  32 */     super.eq(column, params);
/*  33 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper eq(SerializableFunction<T, R> column, Object params) {
/*  43 */     super.eq(ReflectionUtil.getFieldName(column), params);
/*  44 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper ne(String column, Object params) {
/*  54 */     super.ne(column, params);
/*  55 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper ne(SerializableFunction<T, R> column, Object params) {
/*  66 */     super.ne(ReflectionUtil.getFieldName(column), params);
/*  67 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper lt(String column, Object params) {
/*  78 */     super.lt(column, params);
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper lt(SerializableFunction<T, R> column, Object params) {
/*  89 */     super.lt(ReflectionUtil.getFieldName(column), params);
/*  90 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper lte(String column, Object params) {
/* 100 */     super.lte(column, params);
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper lte(SerializableFunction<T, R> column, Object params) {
/* 111 */     super.lte(ReflectionUtil.getFieldName(column), params);
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper gt(String column, Object params) {
/* 122 */     super.gt(column, params);
/* 123 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper gt(SerializableFunction<T, R> column, Object params) {
/* 133 */     super.gt(ReflectionUtil.getFieldName(column), params);
/* 134 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper gte(String column, Object params) {
/* 144 */     super.gte(column, params);
/* 145 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper gte(SerializableFunction<T, R> column, Object params) {
/* 155 */     super.gte(ReflectionUtil.getFieldName(column), params);
/* 156 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper like(String column, String params) {
/* 166 */     super.like(column, params);
/* 167 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper like(SerializableFunction<T, R> column, String params) {
/* 177 */     super.like(ReflectionUtil.getFieldName(column), params);
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper in(String column, Collection<?> params) {
/* 188 */     super.in(column, params);
/* 189 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper in(SerializableFunction<T, R> column, Collection<?> params) {
/* 199 */     super.in(ReflectionUtil.getFieldName(column), params);
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
/*     */   public ConditionOrWrapper in(String column, Object[] params) {
/* 211 */     super.in(column, Arrays.asList(params));
/* 212 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper in(SerializableFunction<T, R> column, Object[] params) {
/* 223 */     super.in(ReflectionUtil.getFieldName(column), Arrays.asList(params));
/* 224 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper nin(String column, Collection<?> params) {
/* 235 */     super.nin(column, params);
/* 236 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper nin(SerializableFunction<T, R> column, Collection<?> params) {
/* 246 */     super.nin(ReflectionUtil.getFieldName(column), params);
/* 247 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper nin(String column, Object[] params) {
/* 258 */     super.nin(column, Arrays.asList(params));
/* 259 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper nin(SerializableFunction<T, R> column, Object[] params) {
/* 269 */     super.nin(ReflectionUtil.getFieldName(column), Arrays.asList(params));
/* 270 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConditionOrWrapper isNull(String column) {
/* 281 */     super.isNull(column);
/* 282 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper isNull(SerializableFunction<T, R> column) {
/* 293 */     super.isNull(ReflectionUtil.getFieldName(column));
/* 294 */     return this;
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
/*     */   public ConditionOrWrapper isNotNull(String column) {
/* 306 */     super.isNotNull(column);
/* 307 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R> ConditionOrWrapper isNotNull(SerializableFunction<T, R> column) {
/* 318 */     super.isNotNull(ReflectionUtil.getFieldName(column));
/* 319 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\ConditionOrWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */