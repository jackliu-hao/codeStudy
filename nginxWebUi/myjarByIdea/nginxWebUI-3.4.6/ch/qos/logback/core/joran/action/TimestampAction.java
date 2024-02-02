package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.CachingDateFormatter;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public class TimestampAction extends Action {
   static String DATE_PATTERN_ATTRIBUTE = "datePattern";
   static String TIME_REFERENCE_ATTRIBUTE = "timeReference";
   static String CONTEXT_BIRTH = "contextBirth";
   boolean inError = false;

   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
      String keyStr = attributes.getValue("key");
      if (OptionHelper.isEmpty(keyStr)) {
         this.addError("Attribute named [key] cannot be empty");
         this.inError = true;
      }

      String datePatternStr = attributes.getValue(DATE_PATTERN_ATTRIBUTE);
      if (OptionHelper.isEmpty(datePatternStr)) {
         this.addError("Attribute named [" + DATE_PATTERN_ATTRIBUTE + "] cannot be empty");
         this.inError = true;
      }

      String timeReferenceStr = attributes.getValue(TIME_REFERENCE_ATTRIBUTE);
      long timeReference;
      if (CONTEXT_BIRTH.equalsIgnoreCase(timeReferenceStr)) {
         this.addInfo("Using context birth as time reference.");
         timeReference = this.context.getBirthTime();
      } else {
         timeReference = System.currentTimeMillis();
         this.addInfo("Using current interpretation time, i.e. now, as time reference.");
      }

      if (!this.inError) {
         String scopeStr = attributes.getValue("scope");
         ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
         CachingDateFormatter sdf = new CachingDateFormatter(datePatternStr);
         String val = sdf.format(timeReference);
         this.addInfo("Adding property to the context with key=\"" + keyStr + "\" and value=\"" + val + "\" to the " + scope + " scope");
         ActionUtil.setProperty(ec, keyStr, val, scope);
      }
   }

   public void end(InterpretationContext ec, String name) throws ActionException {
   }
}
