/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.builder.Builder;
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class ConditionBuilder
/*     */   implements Builder<String>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Condition[] conditions;
/*     */   private List<Object> paramValues;
/*     */   
/*     */   public static ConditionBuilder of(Condition... conditions) {
/*  30 */     return new ConditionBuilder(conditions);
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
/*     */   public ConditionBuilder(Condition... conditions) {
/*  48 */     this.conditions = conditions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Object> getParamValues() {
/*  58 */     return ListUtil.unmodifiable(this.paramValues);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String build() {
/*  69 */     if (null == this.paramValues) {
/*  70 */       this.paramValues = new ArrayList();
/*     */     } else {
/*  72 */       this.paramValues.clear();
/*     */     } 
/*  74 */     return build(this.paramValues);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String build(List<Object> paramValues) {
/*  85 */     if (ArrayUtil.isEmpty((Object[])this.conditions)) {
/*  86 */       return "";
/*     */     }
/*     */     
/*  89 */     StringBuilder conditionStrBuilder = new StringBuilder();
/*  90 */     boolean isFirst = true;
/*  91 */     for (Condition condition : this.conditions) {
/*     */       
/*  93 */       if (isFirst) {
/*  94 */         isFirst = false;
/*     */       } else {
/*     */         
/*  97 */         conditionStrBuilder.append(' ').append(condition.getLinkOperator()).append(' ');
/*     */       } 
/*     */ 
/*     */       
/* 101 */       conditionStrBuilder.append(condition.toString(paramValues));
/*     */     } 
/* 103 */     return conditionStrBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 114 */     return build();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\ConditionBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */