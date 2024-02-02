package freemarker.debug;

import java.io.Serializable;

public class Breakpoint implements Serializable, Comparable {
   private static final long serialVersionUID = 1L;
   private final String templateName;
   private final int line;

   public Breakpoint(String templateName, int line) {
      this.templateName = templateName;
      this.line = line;
   }

   public int getLine() {
      return this.line;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public int hashCode() {
      return this.templateName.hashCode() + 31 * this.line;
   }

   public boolean equals(Object o) {
      if (!(o instanceof Breakpoint)) {
         return false;
      } else {
         Breakpoint b = (Breakpoint)o;
         return b.templateName.equals(this.templateName) && b.line == this.line;
      }
   }

   public int compareTo(Object o) {
      Breakpoint b = (Breakpoint)o;
      int r = this.templateName.compareTo(b.templateName);
      return r == 0 ? this.line - b.line : r;
   }

   public String getLocationString() {
      return this.templateName + ":" + this.line;
   }
}
