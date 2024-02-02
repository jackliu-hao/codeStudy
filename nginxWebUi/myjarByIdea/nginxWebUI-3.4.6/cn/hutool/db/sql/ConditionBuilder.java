package cn.hutool.db.sql;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;

public class ConditionBuilder implements Builder<String> {
   private static final long serialVersionUID = 1L;
   private final Condition[] conditions;
   private List<Object> paramValues;

   public static ConditionBuilder of(Condition... conditions) {
      return new ConditionBuilder(conditions);
   }

   public ConditionBuilder(Condition... conditions) {
      this.conditions = conditions;
   }

   public List<Object> getParamValues() {
      return ListUtil.unmodifiable(this.paramValues);
   }

   public String build() {
      if (null == this.paramValues) {
         this.paramValues = new ArrayList();
      } else {
         this.paramValues.clear();
      }

      return this.build(this.paramValues);
   }

   public String build(List<Object> paramValues) {
      if (ArrayUtil.isEmpty((Object[])this.conditions)) {
         return "";
      } else {
         StringBuilder conditionStrBuilder = new StringBuilder();
         boolean isFirst = true;
         Condition[] var4 = this.conditions;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Condition condition = var4[var6];
            if (isFirst) {
               isFirst = false;
            } else {
               conditionStrBuilder.append(' ').append(condition.getLinkOperator()).append(' ');
            }

            conditionStrBuilder.append(condition.toString(paramValues));
         }

         return conditionStrBuilder.toString();
      }
   }

   public String toString() {
      return this.build();
   }
}
