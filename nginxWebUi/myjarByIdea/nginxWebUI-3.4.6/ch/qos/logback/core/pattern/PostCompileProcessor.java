package ch.qos.logback.core.pattern;

import ch.qos.logback.core.Context;

public interface PostCompileProcessor<E> {
   void process(Context var1, Converter<E> var2);
}
