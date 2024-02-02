package ch.qos.logback.core.spi;

import java.io.Serializable;

public interface PreSerializationTransformer<E> {
  Serializable transform(E paramE);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\PreSerializationTransformer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */