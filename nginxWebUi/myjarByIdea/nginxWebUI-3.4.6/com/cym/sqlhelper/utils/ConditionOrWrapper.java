package com.cym.sqlhelper.utils;

import com.cym.sqlhelper.reflection.ReflectionUtil;
import com.cym.sqlhelper.reflection.SerializableFunction;
import java.util.Arrays;
import java.util.Collection;

public class ConditionOrWrapper extends ConditionWrapper {
   public ConditionOrWrapper() {
      this.andLink = false;
   }

   public ConditionOrWrapper or(ConditionWrapper conditionWrapper) {
      this.list.add(conditionWrapper);
      return this;
   }

   public ConditionOrWrapper eq(String column, Object params) {
      super.eq(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper eq(SerializableFunction<T, R> column, Object params) {
      super.eq(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper ne(String column, Object params) {
      super.ne(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper ne(SerializableFunction<T, R> column, Object params) {
      super.ne(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper lt(String column, Object params) {
      super.lt(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper lt(SerializableFunction<T, R> column, Object params) {
      super.lt(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper lte(String column, Object params) {
      super.lte(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper lte(SerializableFunction<T, R> column, Object params) {
      super.lte(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper gt(String column, Object params) {
      super.gt(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper gt(SerializableFunction<T, R> column, Object params) {
      super.gt(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper gte(String column, Object params) {
      super.gte(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper gte(SerializableFunction<T, R> column, Object params) {
      super.gte(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper like(String column, String params) {
      super.like(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper like(SerializableFunction<T, R> column, String params) {
      super.like(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper in(String column, Collection<?> params) {
      super.in(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper in(SerializableFunction<T, R> column, Collection<?> params) {
      super.in(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper in(String column, Object[] params) {
      super.in(column, Arrays.asList(params));
      return this;
   }

   public <T, R> ConditionOrWrapper in(SerializableFunction<T, R> column, Object[] params) {
      super.in(ReflectionUtil.getFieldName(column), Arrays.asList(params));
      return this;
   }

   public ConditionOrWrapper nin(String column, Collection<?> params) {
      super.nin(column, params);
      return this;
   }

   public <T, R> ConditionOrWrapper nin(SerializableFunction<T, R> column, Collection<?> params) {
      super.nin(ReflectionUtil.getFieldName(column), params);
      return this;
   }

   public ConditionOrWrapper nin(String column, Object[] params) {
      super.nin(column, Arrays.asList(params));
      return this;
   }

   public <T, R> ConditionOrWrapper nin(SerializableFunction<T, R> column, Object[] params) {
      super.nin(ReflectionUtil.getFieldName(column), Arrays.asList(params));
      return this;
   }

   public ConditionOrWrapper isNull(String column) {
      super.isNull(column);
      return this;
   }

   public <T, R> ConditionOrWrapper isNull(SerializableFunction<T, R> column) {
      super.isNull(ReflectionUtil.getFieldName(column));
      return this;
   }

   public ConditionOrWrapper isNotNull(String column) {
      super.isNotNull(column);
      return this;
   }

   public <T, R> ConditionOrWrapper isNotNull(SerializableFunction<T, R> column) {
      super.isNotNull(ReflectionUtil.getFieldName(column));
      return this;
   }
}
