package org.noear.solon.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@FunctionalInterface
public interface BeanExtractor<T extends Annotation> {
   void doExtract(BeanWrap bw, Method method, T anno) throws Throwable;
}
