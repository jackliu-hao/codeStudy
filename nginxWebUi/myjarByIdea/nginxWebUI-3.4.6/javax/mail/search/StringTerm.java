package javax.mail.search;

public abstract class StringTerm extends SearchTerm {
   protected String pattern;
   protected boolean ignoreCase;
   private static final long serialVersionUID = 1274042129007696269L;

   protected StringTerm(String pattern) {
      this.pattern = pattern;
      this.ignoreCase = true;
   }

   protected StringTerm(String pattern, boolean ignoreCase) {
      this.pattern = pattern;
      this.ignoreCase = ignoreCase;
   }

   public String getPattern() {
      return this.pattern;
   }

   public boolean getIgnoreCase() {
      return this.ignoreCase;
   }

   protected boolean match(String s) {
      int len = s.length() - this.pattern.length();

      for(int i = 0; i <= len; ++i) {
         if (s.regionMatches(this.ignoreCase, i, this.pattern, 0, this.pattern.length())) {
            return true;
         }
      }

      return false;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof StringTerm)) {
         return false;
      } else {
         StringTerm st = (StringTerm)obj;
         if (this.ignoreCase) {
            return st.pattern.equalsIgnoreCase(this.pattern) && st.ignoreCase == this.ignoreCase;
         } else {
            return st.pattern.equals(this.pattern) && st.ignoreCase == this.ignoreCase;
         }
      }
   }

   public int hashCode() {
      return this.ignoreCase ? this.pattern.hashCode() : ~this.pattern.hashCode();
   }
}
