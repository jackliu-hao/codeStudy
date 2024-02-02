package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.FormatInfo;

public class FormattingNode extends Node {
   FormatInfo formatInfo;

   FormattingNode(int type) {
      super(type);
   }

   FormattingNode(int type, Object value) {
      super(type, value);
   }

   public FormatInfo getFormatInfo() {
      return this.formatInfo;
   }

   public void setFormatInfo(FormatInfo formatInfo) {
      this.formatInfo = formatInfo;
   }

   public boolean equals(Object o) {
      if (!super.equals(o)) {
         return false;
      } else if (!(o instanceof FormattingNode)) {
         return false;
      } else {
         FormattingNode r = (FormattingNode)o;
         return this.formatInfo != null ? this.formatInfo.equals(r.formatInfo) : r.formatInfo == null;
      }
   }

   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (this.formatInfo != null ? this.formatInfo.hashCode() : 0);
      return result;
   }
}
