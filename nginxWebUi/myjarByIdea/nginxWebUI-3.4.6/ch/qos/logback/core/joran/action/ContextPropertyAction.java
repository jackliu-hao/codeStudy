package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public class ContextPropertyAction extends Action {
   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
      this.addError("The [contextProperty] element has been removed. Please use [substitutionProperty] element instead");
   }

   public void end(InterpretationContext ec, String name) throws ActionException {
   }
}
