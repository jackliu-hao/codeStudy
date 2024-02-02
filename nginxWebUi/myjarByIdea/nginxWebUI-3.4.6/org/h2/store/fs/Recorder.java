package org.h2.store.fs;

public interface Recorder {
   int CREATE_NEW_FILE = 2;
   int CREATE_TEMP_FILE = 3;
   int DELETE = 4;
   int OPEN_OUTPUT_STREAM = 5;
   int RENAME = 6;
   int TRUNCATE = 7;
   int WRITE = 8;

   void log(int var1, String var2, byte[] var3, long var4);
}
