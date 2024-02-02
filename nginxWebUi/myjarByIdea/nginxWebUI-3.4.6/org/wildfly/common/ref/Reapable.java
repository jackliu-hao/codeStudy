package org.wildfly.common.ref;

interface Reapable<T, A> {
   Reaper<T, A> getReaper();
}
