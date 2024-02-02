package cn.hutool.cron.pattern.matcher;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class YearValueMatcher implements PartMatcher {
   private final LinkedHashSet<Integer> valueList;

   public YearValueMatcher(Collection<Integer> intValueList) {
      this.valueList = new LinkedHashSet(intValueList);
   }

   public boolean match(Integer t) {
      return this.valueList.contains(t);
   }

   public int nextAfter(int value) {
      Iterator var2 = this.valueList.iterator();

      Integer year;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         year = (Integer)var2.next();
      } while(year < value);

      return year;
   }
}
