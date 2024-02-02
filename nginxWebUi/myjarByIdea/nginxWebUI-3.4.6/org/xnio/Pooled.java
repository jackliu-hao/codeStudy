package org.xnio;

/** @deprecated */
public interface Pooled<T> extends AutoCloseable {
   void discard();

   void free();

   T getResource() throws IllegalStateException;

   void close();
}
