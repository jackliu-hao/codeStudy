package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.PropertySetter;
import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
import org.xml.sax.Attributes;

public class ParamAction extends Action {
   static String NO_NAME = "No name attribute in <param> element";
   static String NO_VALUE = "No value attribute in <param> element";
   boolean inError = false;
   private final BeanDescriptionCache beanDescriptionCache;

   public ParamAction(BeanDescriptionCache beanDescriptionCache) {
      this.beanDescriptionCache = beanDescriptionCache;
   }

   public void begin(InterpretationContext ec, String localName, Attributes attributes) {
      String name = attributes.getValue("name");
      String value = attributes.getValue("value");
      if (name == null) {
         this.inError = true;
         this.addError(NO_NAME);
      } else if (value == null) {
         this.inError = true;
         this.addError(NO_VALUE);
      } else {
         value = value.trim();
         Object o = ec.peekObject();
         PropertySetter propSetter = new PropertySetter(this.beanDescriptionCache, o);
         propSetter.setContext(this.context);
         value = ec.subst(value);
         name = ec.subst(name);
         propSetter.setProperty(name, value);
      }
   }

   public void end(InterpretationContext ec, String localName) {
   }

   public void finish(InterpretationContext ec) {
   }
}
