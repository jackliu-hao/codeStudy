package cn.hutool.core.text.finder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Matcher;

public class CharMatcherFinder extends TextFinder {
   private static final long serialVersionUID = 1L;
   private final Matcher<Character> matcher;

   public CharMatcherFinder(Matcher<Character> matcher) {
      this.matcher = matcher;
   }

   public int start(int from) {
      Assert.notNull(this.text, "Text to find must be not null!");
      int limit = this.getValidEndIndex();
      int i;
      if (this.negative) {
         for(i = from; i > limit; --i) {
            if (this.matcher.match(this.text.charAt(i))) {
               return i;
            }
         }
      } else {
         for(i = from; i < limit; ++i) {
            if (this.matcher.match(this.text.charAt(i))) {
               return i;
            }
         }
      }

      return -1;
   }

   public int end(int start) {
      return start < 0 ? -1 : start + 1;
   }
}
