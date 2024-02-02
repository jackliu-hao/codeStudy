package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

public class ConversionRuleAction extends Action {
   boolean inError = false;

   public void begin(InterpretationContext ec, String localName, Attributes attributes) {
      this.inError = false;
      String conversionWord = attributes.getValue("conversionWord");
      String converterClass = attributes.getValue("converterClass");
      String errorMsg;
      if (OptionHelper.isEmpty(conversionWord)) {
         this.inError = true;
         errorMsg = "No 'conversionWord' attribute in <conversionRule>";
         this.addError(errorMsg);
      } else if (OptionHelper.isEmpty(converterClass)) {
         this.inError = true;
         errorMsg = "No 'converterClass' attribute in <conversionRule>";
         ec.addError(errorMsg);
      } else {
         try {
            Map<String, String> ruleRegistry = (Map)this.context.getObject("PATTERN_RULE_REGISTRY");
            if (ruleRegistry == null) {
               ruleRegistry = new HashMap();
               this.context.putObject("PATTERN_RULE_REGISTRY", ruleRegistry);
            }

            this.addInfo("registering conversion word " + conversionWord + " with class [" + converterClass + "]");
            ((Map)ruleRegistry).put(conversionWord, converterClass);
         } catch (Exception var8) {
            this.inError = true;
            errorMsg = "Could not add conversion rule to PatternLayout.";
            this.addError(errorMsg);
         }

      }
   }

   public void end(InterpretationContext ec, String n) {
   }

   public void finish(InterpretationContext ec) {
   }
}
