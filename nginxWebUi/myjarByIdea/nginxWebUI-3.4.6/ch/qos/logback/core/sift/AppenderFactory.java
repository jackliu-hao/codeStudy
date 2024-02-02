package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;

public interface AppenderFactory<E> {
   Appender<E> buildAppender(Context var1, String var2) throws JoranException;
}
