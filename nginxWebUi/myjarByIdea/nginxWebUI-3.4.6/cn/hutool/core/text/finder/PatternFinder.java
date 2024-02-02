package cn.hutool.core.text.finder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternFinder extends TextFinder {
   private static final long serialVersionUID = 1L;
   private final Pattern pattern;
   private Matcher matcher;

   public PatternFinder(String regex, boolean caseInsensitive) {
      this(Pattern.compile(regex, caseInsensitive ? 2 : 0));
   }

   public PatternFinder(Pattern pattern) {
      this.pattern = pattern;
   }

   public TextFinder setText(CharSequence text) {
      this.matcher = this.pattern.matcher(text);
      return super.setText(text);
   }

   public TextFinder setNegative(boolean negative) {
      throw new UnsupportedOperationException("Negative is invalid for Pattern!");
   }

   public int start(int from) {
      return this.matcher.find(from) && this.matcher.end() <= this.getValidEndIndex() ? this.matcher.start() : -1;
   }

   public int end(int start) {
      int end = this.matcher.end();
      int limit;
      if (this.endIndex < 0) {
         limit = this.text.length();
      } else {
         limit = Math.min(this.endIndex, this.text.length());
      }

      return end <= limit ? end : -1;
   }

   public PatternFinder reset() {
      this.matcher.reset();
      return this;
   }
}
