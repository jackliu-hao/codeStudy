package cn.hutool.cron.pattern.matcher;

import cn.hutool.core.lang.Matcher;

public interface PartMatcher extends Matcher<Integer> {
   int nextAfter(int var1);
}
