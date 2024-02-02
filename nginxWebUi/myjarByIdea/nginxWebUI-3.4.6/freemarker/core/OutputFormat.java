package freemarker.core;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;

public abstract class OutputFormat {
   public abstract String getName();

   public abstract String getMimeType();

   public abstract boolean isOutputFormatMixingAllowed();

   public final String toString() {
      String extras = this.toStringExtraProperties();
      return this.getName() + "(mimeType=" + StringUtil.jQuote(this.getMimeType()) + ", class=" + ClassUtil.getShortClassNameOfObject(this, true) + (extras.length() != 0 ? ", " : "") + extras + ")";
   }

   protected String toStringExtraProperties() {
      return "";
   }
}
