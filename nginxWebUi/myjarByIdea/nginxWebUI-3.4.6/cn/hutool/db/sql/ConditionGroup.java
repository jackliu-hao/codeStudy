package cn.hutool.db.sql;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.util.List;

public class ConditionGroup extends Condition {
   private Condition[] conditions;

   public void addConditions(Condition... conditions) {
      if (null == this.conditions) {
         this.conditions = conditions;
      } else {
         this.conditions = (Condition[])ArrayUtil.addAll(this.conditions, conditions);
      }

   }

   public String toString(List<Object> paramValues) {
      if (ArrayUtil.isEmpty((Object[])this.conditions)) {
         return "";
      } else {
         StringBuilder conditionStrBuilder = StrUtil.builder();
         conditionStrBuilder.append("(");
         conditionStrBuilder.append(ConditionBuilder.of(this.conditions).build(paramValues));
         conditionStrBuilder.append(")");
         return conditionStrBuilder.toString();
      }
   }
}
