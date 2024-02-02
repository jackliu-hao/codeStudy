/*     */ package com.cym.sqlhelper.utils;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ConditionWrapper
/*     */ {
/*     */   boolean andLink;
/*  18 */   List<Object> list = new ArrayList();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String build(List<String> values) {
/*  28 */     String sql = "";
/*  29 */     if (this.list.size() > 0) {
/*  30 */       List<String> blocks = new ArrayList<>();
/*  31 */       for (Object object : this.list) {
/*  32 */         if (object instanceof Condition) {
/*  33 */           Condition condition = (Condition)object;
/*  34 */           String block = null;
/*     */           
/*  36 */           if (condition.getValue() == null) {
/*  37 */             if (condition.getOperation().equals("IS NULL") || condition.getOperation().equals("IS NOT NULL")) {
/*  38 */               block = buildColumn(condition.getColumn(), String.class) + " " + condition.getOperation();
/*     */             } else {
/*  40 */               block = buildColumn(condition.getColumn(), String.class) + " " + condition.getOperation() + " null";
/*     */             }
/*     */           
/*  43 */           } else if (condition.getValue() instanceof List) {
/*  44 */             block = buildColumn(condition.getColumn(), condition.getValue().getClass()) + " " + condition.getOperation() + " " + buildIn(condition.getValue());
/*  45 */             for (Object val : condition.getValue()) {
/*  46 */               values.add(val.toString());
/*     */             }
/*     */           } else {
/*  49 */             block = buildColumn(condition.getColumn(), condition.getValue().getClass()) + " " + condition.getOperation() + " ?";
/*  50 */             if (!condition.getOperation().equals("LIKE")) {
/*  51 */               values.add(condition.getValue().toString());
/*     */             } else {
/*  53 */               values.add("%" + condition.getValue().toString().replace("%", "\\%") + "%");
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/*  58 */           blocks.add(block);
/*     */         } 
/*     */         
/*  61 */         if (object instanceof ConditionWrapper) {
/*  62 */           ConditionWrapper conditionWrapper = (ConditionWrapper)object;
/*  63 */           String block = " (" + conditionWrapper.build(values) + ") ";
/*  64 */           blocks.add(block);
/*     */         } 
/*     */       } 
/*     */       
/*  68 */       if (this.andLink) {
/*  69 */         sql = StrUtil.join(" AND ", blocks);
/*     */       } else {
/*  71 */         sql = StrUtil.join(" OR ", blocks);
/*     */       } 
/*     */     } 
/*     */     
/*  75 */     return sql;
/*     */   }
/*     */ 
/*     */   
/*     */   public String buildColumn(String column, Class<?> clazz) {
/*  80 */     return "`" + StrUtil.toUnderlineCase(column) + "`";
/*     */   }
/*     */   
/*     */   public String buildIn(Object value) {
/*  84 */     List<String> ask = new ArrayList<>();
/*  85 */     for (Object obj : value) {
/*  86 */       ask.add("?");
/*     */     }
/*     */     
/*  89 */     if (ask.size() > 0) {
/*  90 */       return " (" + StrUtil.join(",", ask) + ") ";
/*     */     }
/*  92 */     return " (null) ";
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
/*     */   public ConditionWrapper eq(String column, Object params) {
/* 105 */     this.list.add(new Condition(column, "=", params));
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
/*     */   public ConditionWrapper ne(String column, Object params) {
/* 117 */     this.list.add(new Condition(column, "<>", params));
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
/*     */   public ConditionWrapper lt(String column, Object params) {
/* 129 */     this.list.add(new Condition(column, "<", params));
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
/*     */   public ConditionWrapper lte(String column, Object params) {
/* 141 */     this.list.add(new Condition(column, "<=", params));
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
/*     */   public ConditionWrapper gt(String column, Object params) {
/* 153 */     this.list.add(new Condition(column, ">", params));
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
/*     */   public ConditionWrapper gte(String column, Object params) {
/* 165 */     this.list.add(new Condition(column, ">=", params));
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
/*     */   public ConditionWrapper like(String column, String params) {
/* 177 */     this.list.add(new Condition(column, "LIKE", params));
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
/*     */   public ConditionWrapper in(String column, Collection<?> params) {
/* 189 */     this.list.add(new Condition(column, "IN", params));
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
/*     */   public ConditionWrapper nin(String column, Collection<?> params) {
/* 201 */     this.list.add(new Condition(column, "NOT IN", params));
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
/*     */   
/*     */   public ConditionWrapper isNull(String column) {
/* 214 */     this.list.add(new Condition(column, "IS NULL", null));
/* 215 */     return this;
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
/*     */   public ConditionWrapper isNotNull(String column) {
/* 227 */     this.list.add(new Condition(column, "IS NOT NULL", null));
/* 228 */     return this;
/*     */   }
/*     */   
/*     */   public boolean notEmpty() {
/* 232 */     return (this.list.size() > 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\ConditionWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */