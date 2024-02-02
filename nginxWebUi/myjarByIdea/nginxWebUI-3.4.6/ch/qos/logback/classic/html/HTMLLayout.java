package ch.qos.logback.classic.html;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.HTMLLayoutBase;
import ch.qos.logback.core.html.IThrowableRenderer;
import ch.qos.logback.core.pattern.Converter;
import java.util.Map;

public class HTMLLayout extends HTMLLayoutBase<ILoggingEvent> {
   static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%mdc%msg";
   IThrowableRenderer<ILoggingEvent> throwableRenderer;

   public HTMLLayout() {
      this.pattern = "%date%thread%level%logger%mdc%msg";
      this.throwableRenderer = new DefaultThrowableRenderer();
      this.cssBuilder = new DefaultCssBuilder();
   }

   public void start() {
      int errorCount = 0;
      if (this.throwableRenderer == null) {
         this.addError("ThrowableRender cannot be null.");
         ++errorCount;
      }

      if (errorCount == 0) {
         super.start();
      }

   }

   protected Map<String, String> getDefaultConverterMap() {
      return PatternLayout.DEFAULT_CONVERTER_MAP;
   }

   public String doLayout(ILoggingEvent event) {
      StringBuilder buf = new StringBuilder();
      this.startNewTableIfLimitReached(buf);
      boolean odd = true;
      if ((this.counter++ & 1L) == 0L) {
         odd = false;
      }

      String level = event.getLevel().toString().toLowerCase();
      buf.append(CoreConstants.LINE_SEPARATOR);
      buf.append("<tr class=\"");
      buf.append(level);
      if (odd) {
         buf.append(" odd\">");
      } else {
         buf.append(" even\">");
      }

      buf.append(CoreConstants.LINE_SEPARATOR);

      for(Converter<ILoggingEvent> c = this.head; c != null; c = c.getNext()) {
         this.appendEventToBuffer(buf, c, event);
      }

      buf.append("</tr>");
      buf.append(CoreConstants.LINE_SEPARATOR);
      if (event.getThrowableProxy() != null) {
         this.throwableRenderer.render(buf, event);
      }

      return buf.toString();
   }

   private void appendEventToBuffer(StringBuilder buf, Converter<ILoggingEvent> c, ILoggingEvent event) {
      buf.append("<td class=\"");
      buf.append(this.computeConverterName(c));
      buf.append("\">");
      buf.append(Transform.escapeTags(c.convert(event)));
      buf.append("</td>");
      buf.append(CoreConstants.LINE_SEPARATOR);
   }

   public IThrowableRenderer getThrowableRenderer() {
      return this.throwableRenderer;
   }

   public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
      this.throwableRenderer = throwableRenderer;
   }

   protected String computeConverterName(Converter c) {
      if (c instanceof MDCConverter) {
         MDCConverter mc = (MDCConverter)c;
         String key = mc.getFirstOption();
         return key != null ? key : "MDC";
      } else {
         return super.computeConverterName(c);
      }
   }
}
