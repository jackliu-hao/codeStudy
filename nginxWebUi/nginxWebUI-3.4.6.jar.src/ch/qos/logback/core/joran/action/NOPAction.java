package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.spi.InterpretationContext;
import org.xml.sax.Attributes;

public class NOPAction extends Action {
  public void begin(InterpretationContext ec, String name, Attributes attributes) {}
  
  public void end(InterpretationContext ec, String name) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\action\NOPAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */