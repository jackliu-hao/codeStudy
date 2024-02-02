package ch.qos.logback.classic.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

public class PatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
   public void start() {
      PatternLayout patternLayout = new PatternLayout();
      patternLayout.setContext(this.context);
      patternLayout.setPattern(this.getPattern());
      patternLayout.setOutputPatternAsHeader(this.outputPatternAsHeader);
      patternLayout.start();
      this.layout = patternLayout;
      super.start();
   }
}
