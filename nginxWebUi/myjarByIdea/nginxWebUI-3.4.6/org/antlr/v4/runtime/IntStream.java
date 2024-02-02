package org.antlr.v4.runtime;

public interface IntStream {
   int EOF = -1;
   String UNKNOWN_SOURCE_NAME = "<unknown>";

   void consume();

   int LA(int var1);

   int mark();

   void release(int var1);

   int index();

   void seek(int var1);

   int size();

   String getSourceName();
}
