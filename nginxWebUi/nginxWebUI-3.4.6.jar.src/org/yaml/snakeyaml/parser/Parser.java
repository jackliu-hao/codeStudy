package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.events.Event;

public interface Parser {
  boolean checkEvent(Event.ID paramID);
  
  Event peekEvent();
  
  Event getEvent();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\parser\Parser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */