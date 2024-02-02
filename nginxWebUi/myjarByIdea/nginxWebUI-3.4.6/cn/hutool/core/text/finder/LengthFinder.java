package cn.hutool.core.text.finder;

import cn.hutool.core.lang.Assert;

public class LengthFinder extends TextFinder {
   private static final long serialVersionUID = 1L;
   private final int length;

   public LengthFinder(int length) {
      this.length = length;
   }

   public int start(int from) {
      Assert.notNull(this.text, "Text to find must be not null!");
      int limit = this.getValidEndIndex();
      int result;
      if (this.negative) {
         result = from - this.length;
         if (result > limit) {
            return result;
         }
      } else {
         result = from + this.length;
         if (result < limit) {
            return result;
         }
      }

      return -1;
   }

   public int end(int start) {
      return start;
   }
}
