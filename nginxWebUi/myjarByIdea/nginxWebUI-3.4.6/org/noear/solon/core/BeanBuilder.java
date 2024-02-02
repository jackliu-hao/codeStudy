package org.noear.solon.core;

import java.lang.annotation.Annotation;

@FunctionalInterface
public interface BeanBuilder<T extends Annotation> {
   void doBuild(Class<?> clz, BeanWrap bw, T anno) throws Throwable;
}
