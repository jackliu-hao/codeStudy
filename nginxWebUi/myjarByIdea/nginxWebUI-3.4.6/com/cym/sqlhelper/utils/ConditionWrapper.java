package com.cym.sqlhelper.utils;

import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class ConditionWrapper {
   boolean andLink;
   List<Object> list = new ArrayList();

   public String build(List<String> values) {
      String sql = "";
      if (this.list.size() > 0) {
         List<String> blocks = new ArrayList();
         Iterator var4 = this.list.iterator();

         while(var4.hasNext()) {
            Object object = var4.next();
            String block;
            if (object instanceof Condition) {
               Condition condition = (Condition)object;
               block = null;
               if (condition.getValue() == null) {
                  if (!condition.getOperation().equals("IS NULL") && !condition.getOperation().equals("IS NOT NULL")) {
                     block = this.buildColumn(condition.getColumn(), String.class) + " " + condition.getOperation() + " null";
                  } else {
                     block = this.buildColumn(condition.getColumn(), String.class) + " " + condition.getOperation();
                  }
               } else if (condition.getValue() instanceof List) {
                  block = this.buildColumn(condition.getColumn(), condition.getValue().getClass()) + " " + condition.getOperation() + " " + this.buildIn(condition.getValue());
                  Iterator var8 = ((List)condition.getValue()).iterator();

                  while(var8.hasNext()) {
                     Object val = var8.next();
                     values.add(val.toString());
                  }
               } else {
                  block = this.buildColumn(condition.getColumn(), condition.getValue().getClass()) + " " + condition.getOperation() + " ?";
                  if (!condition.getOperation().equals("LIKE")) {
                     values.add(condition.getValue().toString());
                  } else {
                     values.add("%" + condition.getValue().toString().replace("%", "\\%") + "%");
                  }
               }

               blocks.add(block);
            }

            if (object instanceof ConditionWrapper) {
               ConditionWrapper conditionWrapper = (ConditionWrapper)object;
               block = " (" + conditionWrapper.build(values) + ") ";
               blocks.add(block);
            }
         }

         if (this.andLink) {
            sql = StrUtil.join(" AND ", blocks);
         } else {
            sql = StrUtil.join(" OR ", blocks);
         }
      }

      return sql;
   }

   public String buildColumn(String column, Class<?> clazz) {
      return "`" + StrUtil.toUnderlineCase(column) + "`";
   }

   public String buildIn(Object value) {
      List<String> ask = new ArrayList();
      Iterator var3 = ((Collection)value).iterator();

      while(var3.hasNext()) {
         Object obj = var3.next();
         ask.add("?");
      }

      return ask.size() > 0 ? " (" + StrUtil.join(",", ask) + ") " : " (null) ";
   }

   public ConditionWrapper eq(String column, Object params) {
      this.list.add(new Condition(column, "=", params));
      return this;
   }

   public ConditionWrapper ne(String column, Object params) {
      this.list.add(new Condition(column, "<>", params));
      return this;
   }

   public ConditionWrapper lt(String column, Object params) {
      this.list.add(new Condition(column, "<", params));
      return this;
   }

   public ConditionWrapper lte(String column, Object params) {
      this.list.add(new Condition(column, "<=", params));
      return this;
   }

   public ConditionWrapper gt(String column, Object params) {
      this.list.add(new Condition(column, ">", params));
      return this;
   }

   public ConditionWrapper gte(String column, Object params) {
      this.list.add(new Condition(column, ">=", params));
      return this;
   }

   public ConditionWrapper like(String column, String params) {
      this.list.add(new Condition(column, "LIKE", params));
      return this;
   }

   public ConditionWrapper in(String column, Collection<?> params) {
      this.list.add(new Condition(column, "IN", params));
      return this;
   }

   public ConditionWrapper nin(String column, Collection<?> params) {
      this.list.add(new Condition(column, "NOT IN", params));
      return this;
   }

   public ConditionWrapper isNull(String column) {
      this.list.add(new Condition(column, "IS NULL", (Object)null));
      return this;
   }

   public ConditionWrapper isNotNull(String column) {
      this.list.add(new Condition(column, "IS NOT NULL", (Object)null));
      return this;
   }

   public boolean notEmpty() {
      return this.list.size() > 0;
   }
}
