package cn.hutool.cron.pattern.matcher;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BoolArrayMatcher implements PartMatcher {
   private final int minValue;
   private final boolean[] bValues;

   public BoolArrayMatcher(List<Integer> intValueList) {
      Assert.isTrue(CollUtil.isNotEmpty((Collection)intValueList), "Values must be not empty!");
      this.bValues = new boolean[(Integer)Collections.max(intValueList) + 1];
      int min = Integer.MAX_VALUE;

      Integer value;
      for(Iterator var3 = intValueList.iterator(); var3.hasNext(); this.bValues[value] = true) {
         value = (Integer)var3.next();
         min = Math.min(min, value);
      }

      this.minValue = min;
   }

   public boolean match(Integer value) {
      return null != value && value < this.bValues.length ? this.bValues[value] : false;
   }

   public int nextAfter(int value) {
      if (value > this.minValue) {
         while(value < this.bValues.length) {
            if (this.bValues[value]) {
               return value;
            }

            ++value;
         }
      }

      return this.minValue;
   }

   public int getMinValue() {
      return this.minValue;
   }

   public String toString() {
      return StrUtil.format("Matcher:{}", new Object[]{this.bValues});
   }
}
