package org.h2.message;

interface TraceWriter {
   void setName(String var1);

   void write(int var1, String var2, String var3, Throwable var4);

   void write(int var1, int var2, String var3, Throwable var4);

   boolean isEnabled(int var1);
}
