package cn.hutool.core.text.finder;

import cn.hutool.core.lang.Assert;
import java.io.Serializable;

public abstract class TextFinder implements Finder, Serializable {
   private static final long serialVersionUID = 1L;
   protected CharSequence text;
   protected int endIndex = -1;
   protected boolean negative;

   public TextFinder setText(CharSequence text) {
      this.text = (CharSequence)Assert.notNull(text, "Text must be not null!");
      return this;
   }

   public TextFinder setEndIndex(int endIndex) {
      this.endIndex = endIndex;
      return this;
   }

   public TextFinder setNegative(boolean negative) {
      this.negative = negative;
      return this;
   }

   protected int getValidEndIndex() {
      if (this.negative && -1 == this.endIndex) {
         return -1;
      } else {
         int limit;
         if (this.endIndex < 0) {
            limit = this.endIndex + this.text.length() + 1;
         } else {
            limit = Math.min(this.endIndex, this.text.length());
         }

         return limit;
      }
   }
}
