package cn.hutool.core.text.finder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;

public class CharFinder extends TextFinder {
   private static final long serialVersionUID = 1L;
   private final char c;
   private final boolean caseInsensitive;

   public CharFinder(char c) {
      this(c, false);
   }

   public CharFinder(char c, boolean caseInsensitive) {
      this.c = c;
      this.caseInsensitive = caseInsensitive;
   }

   public int start(int from) {
      Assert.notNull(this.text, "Text to find must be not null!");
      int limit = this.getValidEndIndex();
      int i;
      if (this.negative) {
         for(i = from; i > limit; --i) {
            if (NumberUtil.equals(this.c, this.text.charAt(i), this.caseInsensitive)) {
               return i;
            }
         }
      } else {
         for(i = from; i < limit; ++i) {
            if (NumberUtil.equals(this.c, this.text.charAt(i), this.caseInsensitive)) {
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
