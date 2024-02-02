package cn.hutool.core.text.finder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;

public class StrFinder extends TextFinder {
   private static final long serialVersionUID = 1L;
   private final CharSequence strToFind;
   private final boolean caseInsensitive;

   public StrFinder(CharSequence strToFind, boolean caseInsensitive) {
      Assert.notEmpty(strToFind);
      this.strToFind = strToFind;
      this.caseInsensitive = caseInsensitive;
   }

   public int start(int from) {
      Assert.notNull(this.text, "Text to find must be not null!");
      int subLen = this.strToFind.length();
      if (from < 0) {
         from = 0;
      }

      int endLimit = this.getValidEndIndex();
      int i;
      if (this.negative) {
         for(i = from; i > endLimit; --i) {
            if (CharSequenceUtil.isSubEquals(this.text, i, this.strToFind, 0, subLen, this.caseInsensitive)) {
               return i;
            }
         }
      } else {
         endLimit = endLimit - subLen + 1;

         for(i = from; i < endLimit; ++i) {
            if (CharSequenceUtil.isSubEquals(this.text, i, this.strToFind, 0, subLen, this.caseInsensitive)) {
               return i;
            }
         }
      }

      return -1;
   }

   public int end(int start) {
      return start < 0 ? -1 : start + this.strToFind.length();
   }
}
