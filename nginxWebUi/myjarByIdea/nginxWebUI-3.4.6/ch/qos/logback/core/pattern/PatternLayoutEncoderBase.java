package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

public class PatternLayoutEncoderBase<E> extends LayoutWrappingEncoder<E> {
   String pattern;
   protected boolean outputPatternAsHeader = false;

   public String getPattern() {
      return this.pattern;
   }

   public void setPattern(String pattern) {
      this.pattern = pattern;
   }

   public boolean isOutputPatternAsHeader() {
      return this.outputPatternAsHeader;
   }

   public void setOutputPatternAsHeader(boolean outputPatternAsHeader) {
      this.outputPatternAsHeader = outputPatternAsHeader;
   }

   public boolean isOutputPatternAsPresentationHeader() {
      return this.outputPatternAsHeader;
   }

   /** @deprecated */
   public void setOutputPatternAsPresentationHeader(boolean outputPatternAsHeader) {
      this.addWarn("[outputPatternAsPresentationHeader] property is deprecated. Please use [outputPatternAsHeader] option instead.");
      this.outputPatternAsHeader = outputPatternAsHeader;
   }

   public void setLayout(Layout<E> layout) {
      throw new UnsupportedOperationException("one cannot set the layout of " + this.getClass().getName());
   }
}
