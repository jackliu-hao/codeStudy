package ch.qos.logback.core.pattern.parser;

import java.util.List;

public class SimpleKeywordNode extends FormattingNode {
   List<String> optionList;

   SimpleKeywordNode(Object value) {
      super(1, value);
   }

   protected SimpleKeywordNode(int type, Object value) {
      super(type, value);
   }

   public List<String> getOptions() {
      return this.optionList;
   }

   public void setOptions(List<String> optionList) {
      this.optionList = optionList;
   }

   public boolean equals(Object o) {
      if (!super.equals(o)) {
         return false;
      } else if (!(o instanceof SimpleKeywordNode)) {
         return false;
      } else {
         SimpleKeywordNode r = (SimpleKeywordNode)o;
         return this.optionList != null ? this.optionList.equals(r.optionList) : r.optionList == null;
      }
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      if (this.optionList == null) {
         buf.append("KeyWord(" + this.value + "," + this.formatInfo + ")");
      } else {
         buf.append("KeyWord(" + this.value + ", " + this.formatInfo + "," + this.optionList + ")");
      }

      buf.append(this.printNext());
      return buf.toString();
   }
}
