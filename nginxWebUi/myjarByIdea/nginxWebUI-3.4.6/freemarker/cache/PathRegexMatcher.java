package freemarker.cache;

import java.io.IOException;
import java.util.regex.Pattern;

public class PathRegexMatcher extends TemplateSourceMatcher {
   private final Pattern pattern;

   public PathRegexMatcher(String regex) {
      if (regex.startsWith("/")) {
         throw new IllegalArgumentException("Absolute template paths need no inital \"/\"; remove it from: " + regex);
      } else {
         this.pattern = Pattern.compile(regex);
      }
   }

   public boolean matches(String sourceName, Object templateSource) throws IOException {
      return this.pattern.matcher(sourceName).matches();
   }
}
