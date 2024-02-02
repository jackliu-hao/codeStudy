package freemarker.cache;

import freemarker.template.utility.StringUtil;
import java.io.IOException;
import java.util.regex.Pattern;

public class PathGlobMatcher extends TemplateSourceMatcher {
   private final String glob;
   private Pattern pattern;
   private boolean caseInsensitive;

   public PathGlobMatcher(String glob) {
      if (glob.startsWith("/")) {
         throw new IllegalArgumentException("Absolute template paths need no inital \"/\"; remove it from: " + glob);
      } else {
         this.glob = glob;
         this.buildPattern();
      }
   }

   private void buildPattern() {
      this.pattern = StringUtil.globToRegularExpression(this.glob, this.caseInsensitive);
   }

   public boolean matches(String sourceName, Object templateSource) throws IOException {
      return this.pattern.matcher(sourceName).matches();
   }

   public boolean isCaseInsensitive() {
      return this.caseInsensitive;
   }

   public void setCaseInsensitive(boolean caseInsensitive) {
      boolean lastCaseInsensitive = this.caseInsensitive;
      this.caseInsensitive = caseInsensitive;
      if (lastCaseInsensitive != caseInsensitive) {
         this.buildPattern();
      }

   }

   public PathGlobMatcher caseInsensitive(boolean caseInsensitive) {
      this.setCaseInsensitive(caseInsensitive);
      return this;
   }
}
