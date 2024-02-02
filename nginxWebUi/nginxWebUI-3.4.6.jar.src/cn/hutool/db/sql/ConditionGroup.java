/*    */ package cn.hutool.db.sql;
/*    */ 
/*    */ import cn.hutool.core.util.ArrayUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConditionGroup
/*    */   extends Condition
/*    */ {
/*    */   private Condition[] conditions;
/*    */   
/*    */   public void addConditions(Condition... conditions) {
/* 27 */     if (null == this.conditions) {
/* 28 */       this.conditions = conditions;
/*    */     } else {
/* 30 */       this.conditions = (Condition[])ArrayUtil.addAll((Object[][])new Condition[][] { this.conditions, conditions });
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString(List<Object> paramValues) {
/* 42 */     if (ArrayUtil.isEmpty((Object[])this.conditions)) {
/* 43 */       return "";
/*    */     }
/*    */     
/* 46 */     StringBuilder conditionStrBuilder = StrUtil.builder();
/* 47 */     conditionStrBuilder.append("(");
/*    */     
/* 49 */     conditionStrBuilder.append(ConditionBuilder.of(this.conditions).build(paramValues));
/* 50 */     conditionStrBuilder.append(")");
/*    */     
/* 52 */     return conditionStrBuilder.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\ConditionGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */