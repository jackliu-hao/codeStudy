package cn.hutool.cron.pattern.matcher;

import cn.hutool.core.lang.Matcher;

public interface PartMatcher extends Matcher<Integer> {
  int nextAfter(int paramInt);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\matcher\PartMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */