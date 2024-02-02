package cn.hutool.cron.pattern.matcher;

import cn.hutool.core.util.StrUtil;

public class AlwaysTrueMatcher implements PartMatcher {
   public static AlwaysTrueMatcher INSTANCE = new AlwaysTrueMatcher();

   public boolean match(Integer t) {
      return true;
   }

   public int nextAfter(int value) {
      return value;
   }

   public String toString() {
      return StrUtil.format("[Matcher]: always true.", new Object[0]);
   }
}
