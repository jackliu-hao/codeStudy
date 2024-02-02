package org.antlr.v4.runtime.misc;

public interface EqualityComparator<T> {
   int hashCode(T var1);

   boolean equals(T var1, T var2);
}
