package freemarker.cache;

import java.io.IOException;

public class NotMatcher extends TemplateSourceMatcher {
   private final TemplateSourceMatcher matcher;

   public NotMatcher(TemplateSourceMatcher matcher) {
      this.matcher = matcher;
   }

   public boolean matches(String sourceName, Object templateSource) throws IOException {
      return !this.matcher.matches(sourceName, templateSource);
   }
}
