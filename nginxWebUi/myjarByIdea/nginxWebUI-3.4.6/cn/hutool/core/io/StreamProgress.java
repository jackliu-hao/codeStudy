package cn.hutool.core.io;

public interface StreamProgress {
   void start();

   void progress(long var1, long var3);

   void finish();
}
