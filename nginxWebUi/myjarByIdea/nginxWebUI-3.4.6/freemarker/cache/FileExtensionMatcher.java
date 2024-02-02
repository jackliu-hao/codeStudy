package freemarker.cache;

import java.io.IOException;

public class FileExtensionMatcher extends TemplateSourceMatcher {
   private final String extension;
   private boolean caseInsensitive = true;

   public FileExtensionMatcher(String extension) {
      if (extension.indexOf(47) != -1) {
         throw new IllegalArgumentException("A file extension can't contain \"/\": " + extension);
      } else if (extension.indexOf(42) != -1) {
         throw new IllegalArgumentException("A file extension can't contain \"*\": " + extension);
      } else if (extension.indexOf(63) != -1) {
         throw new IllegalArgumentException("A file extension can't contain \"*\": " + extension);
      } else if (extension.startsWith(".")) {
         throw new IllegalArgumentException("A file extension can't start with \".\": " + extension);
      } else {
         this.extension = extension;
      }
   }

   public boolean matches(String sourceName, Object templateSource) throws IOException {
      int ln = sourceName.length();
      int extLn = this.extension.length();
      return ln >= extLn + 1 && sourceName.charAt(ln - extLn - 1) == '.' ? sourceName.regionMatches(this.caseInsensitive, ln - extLn, this.extension, 0, extLn) : false;
   }

   public boolean isCaseInsensitive() {
      return this.caseInsensitive;
   }

   public void setCaseInsensitive(boolean caseInsensitive) {
      this.caseInsensitive = caseInsensitive;
   }

   public FileExtensionMatcher caseInsensitive(boolean caseInsensitive) {
      this.setCaseInsensitive(caseInsensitive);
      return this;
   }
}
