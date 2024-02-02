package freemarker.cache;

import java.io.IOException;

public class OrMatcher extends TemplateSourceMatcher {
   private final TemplateSourceMatcher[] matchers;

   public OrMatcher(TemplateSourceMatcher... matchers) {
      if (matchers.length == 0) {
         throw new IllegalArgumentException("Need at least 1 matcher, had 0.");
      } else {
         this.matchers = matchers;
      }
   }

   public boolean matches(String sourceName, Object templateSource) throws IOException {
      TemplateSourceMatcher[] var3 = this.matchers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         TemplateSourceMatcher matcher = var3[var5];
         if (matcher.matches(sourceName, templateSource)) {
            return true;
         }
      }

      return false;
   }
}
