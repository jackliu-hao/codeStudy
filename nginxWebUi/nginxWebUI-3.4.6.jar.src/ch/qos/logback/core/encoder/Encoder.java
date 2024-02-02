package ch.qos.logback.core.encoder;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface Encoder<E> extends ContextAware, LifeCycle {
  byte[] headerBytes();
  
  byte[] encode(E paramE);
  
  byte[] footerBytes();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\encoder\Encoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */