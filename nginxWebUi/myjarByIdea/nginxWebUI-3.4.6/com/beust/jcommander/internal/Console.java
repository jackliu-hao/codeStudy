package com.beust.jcommander.internal;

public interface Console {
   void print(String var1);

   void println(String var1);

   char[] readPassword(boolean var1);
}
