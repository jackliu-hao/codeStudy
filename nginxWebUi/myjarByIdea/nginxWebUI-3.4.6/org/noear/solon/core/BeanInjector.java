package org.noear.solon.core;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface BeanInjector<T extends Annotation> {
   void doInject(VarHolder varH, T anno);
}
