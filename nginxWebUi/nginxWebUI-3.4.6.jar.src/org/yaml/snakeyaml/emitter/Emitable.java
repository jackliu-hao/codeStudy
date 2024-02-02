package org.yaml.snakeyaml.emitter;

import java.io.IOException;
import org.yaml.snakeyaml.events.Event;

public interface Emitable {
  void emit(Event paramEvent) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\emitter\Emitable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */