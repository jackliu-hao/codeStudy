package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import java.util.HashMap;
import java.util.Map;

public abstract class PatternLayoutBase<E> extends LayoutBase<E> {
   static final int INTIAL_STRING_BUILDER_SIZE = 256;
   Converter<E> head;
   String pattern;
   protected PostCompileProcessor<E> postCompileProcessor;
   Map<String, String> instanceConverterMap = new HashMap();
   protected boolean outputPatternAsHeader = false;

   public abstract Map<String, String> getDefaultConverterMap();

   public Map<String, String> getEffectiveConverterMap() {
      Map<String, String> effectiveMap = new HashMap();
      Map<String, String> defaultMap = this.getDefaultConverterMap();
      if (defaultMap != null) {
         effectiveMap.putAll(defaultMap);
      }

      Context context = this.getContext();
      if (context != null) {
         Map<String, String> contextMap = (Map)context.getObject("PATTERN_RULE_REGISTRY");
         if (contextMap != null) {
            effectiveMap.putAll(contextMap);
         }
      }

      effectiveMap.putAll(this.instanceConverterMap);
      return effectiveMap;
   }

   public void start() {
      if (this.pattern != null && this.pattern.length() != 0) {
         try {
            Parser<E> p = new Parser(this.pattern);
            if (this.getContext() != null) {
               p.setContext(this.getContext());
            }

            Node t = p.parse();
            this.head = p.compile(t, this.getEffectiveConverterMap());
            if (this.postCompileProcessor != null) {
               this.postCompileProcessor.process(this.context, this.head);
            }

            ConverterUtil.setContextForConverters(this.getContext(), this.head);
            ConverterUtil.startConverters(this.head);
            super.start();
         } catch (ScanException var3) {
            StatusManager sm = this.getContext().getStatusManager();
            sm.add((Status)(new ErrorStatus("Failed to parse pattern \"" + this.getPattern() + "\".", this, var3)));
         }

      } else {
         this.addError("Empty or null pattern.");
      }
   }

   public void setPostCompileProcessor(PostCompileProcessor<E> postCompileProcessor) {
      this.postCompileProcessor = postCompileProcessor;
   }

   /** @deprecated */
   protected void setContextForConverters(Converter<E> head) {
      ConverterUtil.setContextForConverters(this.getContext(), head);
   }

   protected String writeLoopOnConverters(E event) {
      StringBuilder strBuilder = new StringBuilder(256);

      for(Converter<E> c = this.head; c != null; c = c.getNext()) {
         c.write(strBuilder, event);
      }

      return strBuilder.toString();
   }

   public String getPattern() {
      return this.pattern;
   }

   public void setPattern(String pattern) {
      this.pattern = pattern;
   }

   public String toString() {
      return this.getClass().getName() + "(\"" + this.getPattern() + "\")";
   }

   public Map<String, String> getInstanceConverterMap() {
      return this.instanceConverterMap;
   }

   protected String getPresentationHeaderPrefix() {
      return "";
   }

   public boolean isOutputPatternAsHeader() {
      return this.outputPatternAsHeader;
   }

   public void setOutputPatternAsHeader(boolean outputPatternAsHeader) {
      this.outputPatternAsHeader = outputPatternAsHeader;
   }

   public String getPresentationHeader() {
      return this.outputPatternAsHeader ? this.getPresentationHeaderPrefix() + this.pattern : super.getPresentationHeader();
   }
}
