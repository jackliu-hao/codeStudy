package org.wildfly.common.ref;

public interface Reaper<T, A> {
   void reap(Reference<T, A> var1);
}
